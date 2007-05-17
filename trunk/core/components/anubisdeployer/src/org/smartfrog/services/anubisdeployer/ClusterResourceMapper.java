/** (C) Copyright Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */

package org.smartfrog.services.anubisdeployer;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

/**
 * Static mapper of resources.
 * Static class holding all the intelligence about matching resources with
 * requirements, and so on... should be redone as "Resource" and  "ResourceSet" classes
 * for a more OO feel...
 * <p/>
 * <hr>
 * <p/>
 * <p/>
 * <p/>
 * <p/>
 * How resources are described, recorded, and so on...
 * <p/>
 * Resources in hte generic sense (i.e. resources, attributes and data) are represented in a number of ways within
 * component descriptions. These models are manipulated within this stati class which encapsulates
 * most of the semantics of resource allocation, matching, release, and so on.
 * <p/>
 * The three type of resource have different purposes, and have differenct semantics, when in use.
 * <bl>
 * <li> reources: these are numbers representing the amount of something (such as memory, disc space, and so on).
 * Resouces are used up by requirements (also numbers) which reduce the amount of resource available on reservation,
 * and increse it again on release of the resource.
 * <li> attributes: attributes are properties that simply need to be matched (such as cputype being x86). Each named attribute
 * may have a number of associated values (in a vector), and a requriement is also represented as a number of required values.
 * A match occurs when the required set of values are a subset of the offered set of values. These values are not used up by
 * allocation but remain constant.
 * <li> data: data a finite sets of values that may be allocated uniquely on reservation. They are consequently used up
 * on reservation, and replaced on release. The data is given to a requesting application by binding the values as attributes
 * within the application context.
 * </bl>
 * <p/>
 * <hr>
 * <p/>
 * A node advertises resources in the following way:
 * <p/>
 * extends {
 * resources extends LAZY {
 * r1 nnn;
 * r2 nnn;
 * )
 * attributes extends LAZY {
 * a1 "foo";
 * a2 [87, "baz"];
 * }
 * data extends LAZY {
 * d1 [43, 56, 78];
 * d2 ["s1", "s2", s3"];
 * }
 * <p/>
 * <hr>
 * <p/>
 * <p/>
 * <p/>
 * A component requests resources in the following way:
 * <p/>
 * extends {
 * resources extends LAZY { //less than
 * r1 nnn;
 * r2 nnn;
 * )
 * attributes extends LAZY { //subset
 * a1 ["foo"];
 * a2 87;
 * }
 * data extends LAZY { //can be allocated to attributes
 * d1 extends { attrName; };
 * d2 extends { attr1Name; attr2Name; };
 * }
 * }
 * <p/>
 * The attributes in the data get set during the process of allocating the data
 * and may be linked to in order to extract the values. These links must, of course,
 * be lazy.
 * <p/>
 * <p/>
 * <hr>
 * <p/>
 * <p/>
 * <p/>
 * Accumulated resources requirements are represented in the following way:
 * <p/>
 * extends {
 * resources extends LAZY { //sum
 * r1 nnn;
 * r2 nnn;
 * )
 * attributes extends LAZY { //concatentation
 * a1 ["foo", "bar"];
 * a2 [87];
 * }
 * data extends LAZY { //total number required
 * d1 1;
 * d2 5;
 * }
 * <p/>
 * <p/>
 * <hr>
 * <p/>
 * <p/>
 * <p/>
 * Declared resource reservations are represented in the following way:
 * <p/>
 * extends {
 * resources extends LAZY { //amount
 * r1 nnn;
 * r2 nnn;
 * )
 * attributes extends LAZY { //matched attributes
 * a1 ["foo"]
 * a2 [87, "baz"];
 * }
 * data extends LAZY { //allocated values
 * d1 [43, 56, 78];
 * d2 ["s1", "s2", s3"];
 * }
 * <p/>
 * <hr>
 * <p/>
 */

public class ClusterResourceMapper {

    /* ***************************************************************
   *
   *    Resource, Attribute and Data accumulation during
   *    initial deployment phase
   *
   *************************************************************** */


    public static void accumulateRequirements(ComponentDescription currentReqs, ComponentDescription additionalReqs) {
        // if a resource with that name exists - merge, if not add
        try {
            Object name = additionalReqs.sfResolveHere("name", true);
            Object existingReq = currentReqs.sfResolveHere(name, false);

            if (existingReq == null) {
                try {
                    currentReqs.sfAddAttribute(name, additionalReqs.copy());
                } catch (SmartFrogRuntimeException se) {
                    // shoudnt happen...
                    se.printStackTrace();
                }
            } else {
                ComponentDescription existing = (ComponentDescription) existingReq;
                ComponentDescription currentResources = (ComponentDescription) existing.sfResolveHere("resources", false);
                ComponentDescription currentAttributes = (ComponentDescription) existing.sfResolveHere("attributes", false);
                ComponentDescription currentData = (ComponentDescription) existing.sfResolveHere("data", false);

                ComponentDescription additionalResources = (ComponentDescription) additionalReqs.sfResolveHere("resources", false);
                ComponentDescription additionalAttributes = (ComponentDescription) additionalReqs.sfResolveHere("attributes", false);
                ComponentDescription additionalData = (ComponentDescription) additionalReqs.sfResolveHere("data", false);

                combineResources(currentResources, additionalResources);
                combineAttributes(currentAttributes, additionalAttributes);
                combineData(currentData, additionalData);
            }
        } catch (SmartFrogResolutionException re) {
            // could only happen for the name
            System.out.println("cannot find the name in the addtiional requriements");
            re.printStackTrace();
        }
    }


    private static void combineResources(ComponentDescription soFar, ComponentDescription also) {
        Context alsoC = also.sfContext();
        Context soFarC = soFar.sfContext();
        for (Enumeration e = alsoC.keys(); e.hasMoreElements();) {
            Object resource = e.nextElement();
            if (soFarC.containsKey(resource)) {
                soFarC.put(resource,
                        new Integer(((Integer) soFarC.get(resource)).intValue() +
                                ((Integer) alsoC.get(resource)).intValue()));
            } else {
                soFarC.put(resource, new Integer(((Integer) alsoC.get(resource)).intValue()));
            }
        }
    }

    private static void combineAttributes(ComponentDescription soFar, ComponentDescription also) {
        Context alsoC = also.sfContext();
        Context soFarC = soFar.sfContext();
        for (Enumeration e = alsoC.keys(); e.hasMoreElements();) {
            Object attribute = e.nextElement();
            if (soFarC.containsKey(attribute)) {
                soFarC.put(attribute, combineAttributeValues(soFarC.get(attribute), alsoC.get(attribute)));
            } else {
                soFarC.put(attribute, alsoC.get(attribute));
            }
        }
    }

    private static Object combineAttributeValues(Object attr1, Object attr2) {
        if (attr1 instanceof Vector) {
            Vector data = (Vector) (((Vector) attr1).clone());
            if (attr2 instanceof Vector) {
                for (Enumeration e = ((Vector) attr2).elements(); e.hasMoreElements();) {
                    data.add(e.nextElement());
                }
            } else {
                data.add(attr2);
            }
            return data;
        } else {
            if (attr2 instanceof Vector) {
                Vector data = (Vector) (((Vector) attr2).clone());
                data.add(attr1);
                return data;
            } else {
                if (attr1.equals(attr2))
                    return attr1;
                else {
                    Vector data = new Vector();
                    data.add(attr1);
                    data.add(attr2);
                    return data;
                }
            }
        }
    }


    // temporary ahck to make names of attributes unique - should do it a different way
    static int unique = 0;

    private static void combineData(ComponentDescription soFar, ComponentDescription also) {
        Context alsoC = also.sfContext();
        Context soFarC = soFar.sfContext();

        for (Enumeration e = alsoC.keys(); e.hasMoreElements();) {
            Object data = e.nextElement();
            if (soFarC.containsKey(data)) {
                for (Enumeration instances = ((ComponentDescription) (alsoC.get(data))).sfContext().keys(); instances.hasMoreElements();)
                {
                    try {
                        ((ComponentDescription) (soFarC.get(data))).sfAddAttribute("n" + unique + instances.nextElement(), SFNull.get());
                    } catch (SmartFrogRuntimeException se) {
                        // shoudn't happen...
                        se.printStackTrace();
                    }
                }
            } else {
                soFarC.put(data, ((ComponentDescription) (alsoC.get(data))).copy());
            }
        }
    }

    /* ***************************************************************
   *
   *    mathcing of nodes to Resource, Attribute and Data
   *    requirements during initial deployment phase
   *
   *************************************************************** */

    public static ComponentDescription mapNodes(ComponentDescription nodes, ComponentDescription reqs) {
        Context nodeKeys = new ContextImpl();
        HashSet nodeKeySet = new HashSet();

        Context reqKeys = new ContextImpl();
        LinkedList reqKeySet = new LinkedList();

        ComponentDescription allocations = new ComponentDescriptionImpl(null, new ContextImpl(), true);

        for (Enumeration e = nodes.sfContext().keys(); e.hasMoreElements();)
            nodeKeySet.add(e.nextElement());
        for (Enumeration e = reqs.sfContext().keys(); e.hasMoreElements();)
            reqKeySet.add(e.nextElement());

        if (!findMapping(nodeKeySet, reqKeySet, nodes, reqs, allocations)) {
            allocations = null;
        }

        return allocations;
    }


    private static boolean findMapping(HashSet nodeKeysLeft,
                                       LinkedList reqKeysLeft,
                                       ComponentDescription nodes,
                                       ComponentDescription reqs,
                                       ComponentDescription mapping) {
        if (reqKeysLeft.size() == 0) return true; //all done...
        Object reqKey = reqKeysLeft.get(0);
        reqKeysLeft.remove(0);

        for (Iterator e = ((HashSet) nodeKeysLeft.clone()).iterator(); e.hasNext();) {
            Object nodeName = e.next();

            try {
                if (checkMapping((ComponentDescription) nodes.sfResolveHere(nodeName, false),
                        (ComponentDescription) reqs.sfResolveHere(reqKey, false))) {
                    nodeKeysLeft.remove(nodeName);
                    boolean ok = findMapping(nodeKeysLeft, reqKeysLeft, nodes, reqs, mapping);
                    if (ok) {
                        try {
                            mapping.sfAddAttribute(reqKey, nodeName);
                        } catch (SmartFrogRuntimeException se) {
                            //shoudn't happen....
                            se.printStackTrace();
                        }
                        return true;
                    } else {
                        nodeKeysLeft.add(nodeName);
                    }
                }
            } catch (SmartFrogResolutionException re) {
                System.out.println("error in obtaining nodes or requirements");
                re.printStackTrace();
            }
        }
        reqKeysLeft.add(reqKey);
        return false;
    }


    private static boolean checkMapping(ComponentDescription has, ComponentDescription needs) {
        // compare resources: has >= needs for all resources in needs
        try {
            ComponentDescription hasResources = (ComponentDescription) has.sfResolveHere("resources", false);
            ComponentDescription needsResources = (ComponentDescription) needs.sfResolveHere("resources", false);
            for (Enumeration e = needsResources.sfContext().keys(); e.hasMoreElements();) {
                Object name = e.nextElement();
                if (!hasResources.sfContext().containsKey(name)) {
                    return false;
                }
                if (((Integer) hasResources.sfResolveHere(name, false)).intValue() <
                        ((Integer) needsResources.sfResolveHere(name, false)).intValue()
                        ) {
                    return false;
                }
            }

            // compare attributes has superset of needs for all needs
            ComponentDescription hasAttributes = (ComponentDescription) has.sfResolveHere("attributes", false);
            ComponentDescription needsAttributes = (ComponentDescription) needs.sfResolveHere("attributes", false);

            for (Enumeration e = needsAttributes.sfContext().keys(); e.hasMoreElements();) {
                Object name = e.nextElement();
                if (!hasAttributes.sfContext().containsKey(name)) return false;
                if (! matchAttributeValues(
                        hasAttributes.sfResolveHere(name, false),
                        needsAttributes.sfResolveHere(name, false)
                )
                        ) return false;
            }

            // compare data to ensure that requried data is available
            ComponentDescription hasData = (ComponentDescription) has.sfResolveHere("data", false);
            ComponentDescription needsData = (ComponentDescription) needs.sfResolveHere("data", false);
            for (Enumeration e = needsData.sfContext().keys(); e.hasMoreElements();) {
                Object name = e.nextElement();
                if (!hasData.sfContext().containsKey(name)) return false;

                if (((Vector) hasData.sfResolveHere(name, false)).size() <
                        ((ComponentDescription) needsData.sfResolveHere(name, false)).sfContext().size())
                    return false;
            }

        } catch (Throwable t) {
            t.printStackTrace();
            return false; // irrespective of reason...!
        }
        // haven't had an error, nor a mis-match...
        return true;
    }


    private static boolean matchAttributeValues(Object have, Object want) {
        if (have instanceof Vector) {
            if (want instanceof Vector) {
                boolean partial = true;
                for (Enumeration e = ((Vector) want).elements(); e.hasMoreElements();) {
                    partial = ((Vector) have).contains(e.nextElement()) && partial;
                }
                return partial;
            } else {
                return ((Vector) have).contains(want);
            }
        } else {
            if (want instanceof Vector) {
                boolean partial = true;
                for (Enumeration e = ((Vector) want).elements(); e.hasMoreElements();) {
                    partial = partial && (have.equals(e.nextElement()));
                }
                return partial;
            } else {
                return have.equals(want);
            }
        }
    }

    /* ***************************************************************
   *
   *    Resource and Data reservation and release
   *
   *************************************************************** */


    public static void reserveData(
            String id,
            ComponentDescription data,
            ComponentDescription availableData,
            Prim forManager,
            Prim forComponent)
            throws SmartFrogResolutionException, SmartFrogDeploymentException {
        // for each of the attribute values
        // get an item of data: remove from available, add to reservation
        // set attributes in the component...


        for (Enumeration d = data.sfContext().keys(); d.hasMoreElements();) {
            String dataName = (String) d.nextElement();
            Reference dataRef = new Reference(ReferencePart.here(dataName));

            try {
                Vector availableD = (Vector) availableData.sfResolve(dataRef);
                ComponentDescription needsD = (ComponentDescription) data.sfResolve(dataRef);

                for (Enumeration i = needsD.sfContext().keys(); i.hasMoreElements();) {
                    String instanceName = (String) i.nextElement();
                    needsD.sfReplaceAttribute(instanceName, availableD.remove(0));
                }
            } catch (Exception e) {
                String name = "";
                try {
                    name = forManager.sfCompleteName().toString();
                } catch (RemoteException r) {
                }
                throw new SmartFrogDeploymentException(
                        "insufficient data "
                                + data
                                + " in resource manager "
                                + name
                                + " for id "
                                + id, e);

            }
        }
    }

    public static void reserveResouces(
            String id,
            ComponentDescription resources,
            ComponentDescription availableResources,
            Prim forManager,
            Prim forComponent)
            throws SmartFrogResolutionException, SmartFrogDeploymentException {

        for (Enumeration e = resources.sfContext().keys();
             e.hasMoreElements();
                ) {
            String resource = (String) e.nextElement();
            Reference resourceRef = new Reference(ReferencePart.here(resource));

            // in the following check, allow a resolution exception to trigger failure - if
            // there is no available resource of the same name as the required resouce, this is a
            // failure of the check...

            int resourceInt =
                    ((Integer) (resources.sfResolve(resourceRef))).intValue();
            int availableInt;
            try {
                availableInt =
                        ((Integer) (availableResources.sfResolve(resourceRef)))
                                .intValue();
            } catch (Exception ex) {
                String name = "";
                try {
                    name = forManager.sfCompleteName().toString();
                } catch (RemoteException r) {
                }
                throw new SmartFrogDeploymentException(
                        "missing resource "
                                + resource
                                + " in resource manager "
                                + name
                                + " for id "
                                + id);
            }

            if (resourceInt > availableInt) {
                String name = "";
                try {
                    name = forManager.sfCompleteName().toString();
                } catch (RemoteException r) {
                }
                throw new SmartFrogDeploymentException(
                        "insufficient resource "
                                + resource
                                + " in resource manager "
                                + name
                                + " for id "
                                + id);
            } else {
                try {
                    availableResources.sfReplaceAttribute(
                            resource,
                            new Integer(availableInt - resourceInt));
                } catch (SmartFrogRuntimeException se) {
                    //shouldn't happen...
                    se.printStackTrace();
                }
            }
        }
    }


    public static void releaseData(
            ComponentDescription data,
            ComponentDescription availableData)
            throws SmartFrogResolutionException {

        for (Enumeration d = data.sfContext().keys(); d.hasMoreElements();) {
            String dataName = (String) d.nextElement();
            Reference dataRef = new Reference(ReferencePart.here(dataName));

            try {
                Vector availableD = (Vector) availableData.sfResolve(dataRef);
                ComponentDescription needsD = (ComponentDescription) data.sfResolve(dataRef);

                for (Enumeration i = needsD.sfContext().keys(); i.hasMoreElements();) {
                    String instanceName = (String) i.nextElement();
                    availableD.add(needsD.sfResolveHere(instanceName, false));
                }
            } catch (Exception e) {
            }
        }

    }


    public static void releaseResouces(
            ComponentDescription resources,
            ComponentDescription availableResources)
            throws SmartFrogResolutionException {

        for (Enumeration e = resources.sfContext().keys();
             e.hasMoreElements();
                ) {
            String resource = (String) e.nextElement();
            Reference resourceRef = new Reference(ReferencePart.here(resource));
            // in the following check, allow a resolution exception to trigger failure - if
            // there is no available resource of the same name as the required resouce, this is a
            // failure of the check...
            int resourceInt =
                    ((Integer) (resources.sfResolve(resourceRef))).intValue();
            try {
                int availableInt =
                        ((Integer) (availableResources.sfResolve(resourceRef)))
                                .intValue();
                try {
                    availableResources.sfReplaceAttribute(
                            resource,
                            new Integer(availableInt + resourceInt));
                } catch (SmartFrogRuntimeException se) {
                    //shouldn't happen...
                    se.printStackTrace();
                }
            } catch (SmartFrogResolutionException e1) {
            }
        }
    }

    /* ***************************************************************
   *
   *    Some testing code
   *
   *************************************************************** */


    public static void main(String args[]) {
        // tests for the above things...
        System.out.println("combining attributes");
        System.out.println("should be [4,10] ::" + combineAttributeValues(new Integer(4), new Integer(10)));
        System.out.println("should be [4,\"hello\"] ::" + combineAttributeValues(new Integer(4), "hello"));
        System.out.println("should be 4 " + combineAttributeValues(new Integer(4), new Integer(4)));
        System.out.println("shold be \"hello\" ::" + combineAttributeValues("hello", "hello"));

        Vector test1 = new Vector();
        test1.add(new Integer(4));
        test1.add(new Integer(5));
        Vector test2 = new Vector();
        test2.add("hello");
        test2.add("goodbye");

        System.out.println("should be [1 4 5] ::" + combineAttributeValues(new Integer(1), test1));
        System.out.println("should be [4 5 1] ::" + combineAttributeValues(test1, new Integer(1)));
        System.out.println("should be [4 5 \"hello\" \"goodbye\" ::" + combineAttributeValues(test1, test2));

        System.out.println("Matching attributes");
        System.out.println("should be true : " + matchAttributeValues(new Integer(4), new Integer(4)));
        System.out.println("should be false : " + matchAttributeValues(new Integer(4), new Integer(5)));
        System.out.println("should be false : " + matchAttributeValues(new Integer(4), "foo"));

        Vector test3 = new Vector();
        test3.add(new Integer(4));
        test3.add(new Integer(4));
        System.out.println("should be true : " + matchAttributeValues(new Integer(4), test3));
        System.out.println("should be true : " + matchAttributeValues(test2, "hello"));
        System.out.println("should be false : " + matchAttributeValues(test2, "foo"));

        System.out.println("should be false : " + matchAttributeValues(test2, test3));
        System.out.println("should be true : " + matchAttributeValues(test1, test3));
        test3.add(new Integer(15));
        System.out.println("should be false : " + matchAttributeValues(test1, test3));

        System.out.println("Combining Resources");
        Context c1 = new ContextImpl();
        c1.put("foo", new Integer(1));
        c1.put("bar", new Integer(2));
        Context c2 = new ContextImpl();
        c2.put("baz", new Integer(5));
        c2.put("foo", new Integer(3));
        c2.put("boo", new Integer(10));
        combineResources(new ComponentDescriptionImpl(null, c1, true), new ComponentDescriptionImpl(null, c2, true));
        System.out.println("should be foo->4, bar->2, baz->5, boo->10 ::\n" + c1);
        /*
      System.out.println("Combining Data");
          Context ct1 = new ContextImpl(); ct1.put("foo", new Integer(1)); ct1.put("bar", new Integer(2));

          Vector v1 = new Vector(); v1.add("a"); v1.add("b");
          Vector v2 = new Vector(); v2.add("c"); v2.add("d"); v2.add("e");
          Context ct2 = new ContextImpl(); ct2.put("bar", v1); ct2.put("foo", v2); ct2.put("boo", new Vector());

          combineData(new ComponentDescriptionImpl(null,ct1,true),new ComponentDescriptionImpl(null,ct2,true));
          System.out.println("should be foo->4, bar->4, boo->0 ::\n" + ct1 );
      */


    }
}
