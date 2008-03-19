/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.utils;

import org.smartfrog.sfcore.prim.ChildMinder;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.deployer.SFDeployer;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.List;

/**
 * Contains all the child-minding logic for anything that wants to host children
 */
public class ParentHelper implements ChildMinder {
    private Prim owner;
    private ComponentHelper helper;

    /**
     * Maintains children on which life of parent depends (and vice versa).
     */
    private Vector<Prim> sfChildren = new Vector<Prim>(5, 2);

    /**
     * construct a parent helper and bind to a prim class
     * @param owner he owner to which this helper should be bound
     */
    public ParentHelper(Prim owner) {
        this.owner = owner;
        helper=new ComponentHelper(owner);
    }

    /**
     * Add a child.
     * if synchronized it locks processCompound when it registers back!
     *
     * @param child child to add, which must be a Prim
     *
     * @throws RemoteException In case of Remote/network error
     */
    public void sfAddChild(Liveness child) throws RemoteException {
        Prim prim = ((Prim) child);
        sfChildren.addElement(prim);
        prim.sfParentageChanged();
    }

    /**
     * Remove a child.
     *
     * @param child child to add
     *
     * @return Status of child removal
     * @throws SmartFrogRuntimeException if failed ro remove 
     * @throws RemoteException In case of Remote/network error
     */
    public boolean sfRemoveChild(Liveness child)
            throws SmartFrogRuntimeException, RemoteException {
        boolean res = sfChildren.removeElement(child);
        try {
            owner.sfRemoveAttribute(owner.sfAttributeKeyFor(child));
        } catch (SmartFrogRuntimeException ex) {
            //Ignore: it happens when attribute does not exist
        }
        return res;
    }

    /**
     * Request whether implementor contains a given child.
     *
     * @param child child to check for
     *
     * @return true is child is present else false
     *
     * @throws RemoteException In case of Remote/network error
     */
    public boolean sfContainsChild(Liveness child) throws RemoteException {
        return sfChildren.contains(child);
    }

    /**
     * Gets an enumeration over the children of the implementor.
     *
     * @return enumeration over children
     *
     * @throws RemoteException In case of Remote/network error
     */
    public Enumeration<Liveness> sfChildren() throws RemoteException {
       return((Vector) sfChildren.clone()).elements();
    }

    /**
     * Return a list of all the children. This vector is a shallow clone of the internal list; changes to the list do
     * not affect the internal data structures, though actions on the children will do so. It relies on all children
     * implementing Prim.
     *
     * @return a cloned list of all deployed children
     * @since SmartFrog 3.13.003
     */
    public List<Prim> sfChildList() {
        return (List<Prim>) sfChildren.clone();
    }


    /**
     * An low-level SmartFrog method. It deploys a compiled component and makes
     * it an attribute of the parent compound. Also start heartbeating the
     * deployed component if the component registers. Note that the remaining
     * lifecycle methods must still be invoked on the created component - namely
     * sfDeploy() and sfStart(). This is primarily an internal method - the
     * prefered method for end users is #sfCreateNewChild.
     * <p/>
     * Note that the remaining lifecycle methods must still be invoked on the
     * created component - namely sfDeploy() and sfStart().
     *
     * @param name   name to name deployed component under in context
     * @param parent of deployer component
     * @param cmp    compiled component to deploy
     * @param parms  parameters for description; can be null
     *
     * @return newly deployed component
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogDeploymentException
     *          failed to deploy compiled component
     */
    public Prim deployComponentDescription(Object name,
                                           Prim parent,
                                           ComponentDescription cmp,
                                           Context parms)
            throws SmartFrogDeploymentException {
        Log log = helper.getLogger();
        if (parms == null) {
            parms = new ContextImpl();
        }
        // check for attribute already named like given name
        try {
            Object res = ((parent == null) || (name == null)) ? null : owner.sfResolveHere(
                    name,
                    false);

            if ((res != null) && !(res instanceof ComponentDescription)) {
                throw new SmartFrogDeploymentException(null,
                		ComponentHelper.completeNameSafe(parent),
                        name,
                        cmp,
                        parms,
                        MessageUtil.
                                formatMessage(MessageKeys.MSG_NON_REP_ATTRIB,
                                name),
                        null,
                        null);
            }

            if (log.isTraceEnabled()) {
                StringBuilder message = new StringBuilder();
                try {
                    message.append(helper.completeNameSafe());
                    message.append(" is deploying: ");
                    if (name != null) {
                        message.append(name);
                    } else {
                        message.append("no-name");
                    }
                    if (parent != null) {
                        message.append(", Parent: ");
                        message.append(parent.sfCompleteName());
                    }
                    message.append(", Component description: ");
                    message.append(cmp.toString());
                    if (parms != null) {
                        message.append(", Params: ");
                        message.append(parms.toString());
                    }
                } catch (Throwable thr) {
                    log.trace("", thr);
                }
                log.trace(message.toString());
            }

            // try to deploy
            Prim result = SFDeployer.deploy(cmp, null, parent, parms);

            /**
             *
             * @TODO don't like this, we need to make the attribute over-write atomic with child registration (Patrick).
             *
             */
            if (parent != null) {
                if (name != null) {
                    parent.sfReplaceAttribute(name, result);
                    result.sfParentageChanged(); // yuk.... see todo above!
                } else {
                    //@TODO - Review after refactoring ProcessCompound
                    //This should throw an exception when a
                    //component is registered without a name
                    //in a processcompound, but compound should not know anything
                    //about processcompound
                }
            }
            return result;
        } catch (SmartFrogDeploymentException dex) {
            // It will build source recursively
            Reference newRef = new Reference();
            if (name == null) {
                //@todo review methods for compDesc
                if (cmp.sfContext()
                        .containsKey(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME))
                    name = cmp.sfContext()
                            .get(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME);
                if(parent!=null) {
	                try {
	                    newRef = parent.sfCompleteName();
	                } catch (Exception ex) {
	                    // LOG ex
	                    LogFactory.sfGetProcessLog()
	                            .ignore("could not get complete name", ex);
	                }
                }
            }
            if ((dex.get(SmartFrogDeploymentException.OBJECT_NAME)) != null) {
                newRef.addElement(ReferencePart.here(name));
            } else {
                dex.add(SmartFrogDeploymentException.OBJECT_NAME, name);
            }
            if (dex.get(SmartFrogDeploymentException.SOURCE) != null) {
                newRef.addElements((Reference) dex.get(
                        SmartFrogDeploymentException.SOURCE));
            }

            if (newRef.size() != 0) {
                dex.put(SmartFrogDeploymentException.SOURCE, newRef);
            }
            throw dex;
        } catch (Throwable thr) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(
                    thr);
        }
    }
}
