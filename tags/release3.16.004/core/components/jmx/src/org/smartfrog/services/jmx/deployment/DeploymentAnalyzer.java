/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jmx.deployment;

import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.componentdescription.*;
import org.smartfrog.sfcore.processcompound.*;

import java.beans.*;
import java.lang.reflect.*;
import java.util.*;
import java.rmi.*;
import java.rmi.server.RemoteStub;
import java.io.*;

import javax.management.*;
import javax.management.modelmbean.*;
import org.smartfrog.services.jmx.common.*;
import org.smartfrog.services.jmx.notification.RemoteNotificationBroadcaster;
import org.smartfrog.services.jmx.notification.RemoteNotificationListener;
import org.smartfrog.services.jmx.notification.RemoteNotificationBroadcasterSupport;

/**
 *  Deployment analyzer compound
 * created        25 January 2002
 */
public class DeploymentAnalyzer extends CompoundImpl implements Compound, DeploymentAnalyzerMBean, RemoteNotificationBroadcaster {

    /**
     *  The root component of the tree from which this component should go
     *  through
     */
    protected Prim rootTarget = null;

    /**
     *  The MetadataParser
     */
    MetadataParser parser = null;

    /**
     */
    String rootPath = null;

    /**
     */
    ObjectName myName = null;

    /**
     */
    long sequence = 0;


    /**
     *  Constructor
     *
     *@exception  RemoteException  Description of the Exception
     */
    public DeploymentAnalyzer() throws RemoteException {
        super();
    }


    /**
     *  Obtains a full path String making use of the reference to the parent.
     *
     *@return                      The rootPath value
     *@exception  Exception        Description of the Exception
     *@exception  RemoteException  Description of the Exception
     */
    public String getRootPath() throws RemoteException, Exception {
        String path = null;
        // Locate the parent
        if (rootTarget != null) {
            path = rootTarget.sfCompleteName().toString();
            if (path.equals("")) {
                return "ROOT";
            } else {
                return ("ROOT:" + path);
            }
        } else {
            return "ROOT";
        }
    }


    /**
     *  INTERFACE RemoteNotificationBroadcaster ****
     */
    protected RemoteNotificationBroadcasterSupport broadcaster = new RemoteNotificationBroadcasterSupport();


    /**
     *  Adds a feature to the RemoteNotificationListener attribute of the
     *  DeploymentAnalyzer object
     *
     *@param  listener                      The feature to be added to the
     *      RemoteNotificationListener attribute
     *@param  filter                        The feature to be added to the
     *      RemoteNotificationListener attribute
     *@param  handback                      The feature to be added to the
     *      RemoteNotificationListener attribute
     *@exception  MBeanException            Description of the Exception
     *@exception  IllegalArgumentException  Description of the Exception
     *@exception  RemoteException           Description of the Exception
     */
    public void addRemoteNotificationListener
            (RemoteNotificationListener listener, NotificationFilter filter, Object handback)
             throws MBeanException, IllegalArgumentException, RemoteException {
        if (broadcaster == null) {
            broadcaster = new RemoteNotificationBroadcasterSupport();
        }
        broadcaster.addRemoteNotificationListener(listener, filter, handback);

    }


    /**
     *  Description of the Method
     *
     *@param  listener                       Description of the Parameter
     *@exception  ListenerNotFoundException  Description of the Exception
     *@exception  RemoteException            Description of the Exception
     */
    public void removeRemoteNotificationListener(RemoteNotificationListener listener)
             throws ListenerNotFoundException, RemoteException {
        if (broadcaster == null) {
            throw new ListenerNotFoundException("No notification listeners registered");
        } else {
            broadcaster.removeRemoteNotificationListener(listener);
        }
    }


    /**
     *  Gets the remoteNotificationInfo attribute of the
     *  RemoteNotificationBroadcaster object
     *
     *@return                      The remoteNotificationInfo value
     *@exception  RemoteException  Description of the Exception
     */
    public MBeanNotificationInfo[] getRemoteNotificationInfo() throws RemoteException {
        return new MBeanNotificationInfo[]
                {
                new MBeanNotificationInfo(new String[]{AttributeChangeNotification.ATTRIBUTE_CHANGE},
                AttributeChangeNotification.ATTRIBUTE_CHANGE,
                "A SmartFrog attribute has been modified"),
                new MBeanNotificationInfo(new String[]{"smartfrog.lifecycle.deploy"},
                "smartfrog.lifecycle.deploy",
                "The component has been deployed"),
                new MBeanNotificationInfo(new String[]{"smartfrog.lifecycle.start"},
                "smartfrog.lifecycle.start",
                "The component has been started"),
                new MBeanNotificationInfo(new String[]{"smartfrog.lifecycle.terminate"},
                "smartfrog.lifecycle.terminate",
                "The component has been terminated"),
                };
    }


    /**
     *  Description of the Method
     *
     *@param  type  Description of the Parameter
     */
    public void sendNotification(String type) {
        try {
            if (broadcaster == null) {
                return;
            }
            Notification notif = new Notification(type, sfCompleteName().toString(), sequence++, System.currentTimeMillis(), "");
            sendNotification(notif);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  Description of the Method
     *
     *@param  notification   Description of the Parameter
     *@exception  Exception  Description of the Exception
     */
    public void sendNotification(Notification notification) throws Exception {
        broadcaster.sendRemoteNotification(notification);
    }


    /**
     *  Lifecycle methods ****
     *
     *@exception  Exception  Description of the Exception
     */

    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        rootTarget = (Prim) sfResolve("rootTarget");
        Context metadata = ((ComponentDescription) sfResolveHere("sfManageable")).sfContext();
        try {
          parser = new MetadataParser(metadata);
        } catch (Exception ex) {
          throw SmartFrogException.forward(ex);
        }
        sendNotification("smartfrog.lifecycle.deploy");
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        sendNotification("smartfrog.lifecycle.start");
    }


    /**
     *  Description of the Method
     *
     *@param  tr  Description of the Parameter
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        sendNotification("smartfrog.lifecycle.terminate");
        super.sfTerminateWith(tr);
    }


    /**
     *  Interface Manageable ****
     *
     *@param  attrib_path          Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */

    /**
     *  Returs the value of the required attribute
     *
     *@param  attrib_path          The full path of the attribute in the
     *      SmartFrog deployment tree
     *@return                      The value of the attribute
     *@exception  RemoteException
     *@exception  Exception
     */
    public Object sfGetAttribute(String attrib_path) throws RemoteException, Exception {
        if (attrib_path == null || attrib_path == "") {
            throw new IllegalArgumentException("Attribute cannot be null or empty");
        }
        String attribute = attrib_path.substring(attrib_path.lastIndexOf(":") + 1);
        if (!parser.isManageableAttribute(attribute)) {
            throw new NonManageableAttributeException(attribute);
        }
        if (!parser.isReadable(attribute)) {
            throw new NonReadableAttributeException(attribute);
        }
        rootPath = getRootPath();
        Object value = sfResolve(rootPath + ":" + attrib_path);
        if (Utilities.isAttribute(value)) {
            value = Utilities.objectFromString(parser.getType(attribute), value);
            return value;
        } else {
            return null;
        }
    }


    /**
     *  Description of the Method
     *
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public SFAttribute sfGetRoot() throws RemoteException, Exception {
        try {
            String rootPath = getRootPath();
            String lastPart = null;
            if (rootPath.equals("ROOT")) {
                lastPart = "ROOT";
            } else {
                lastPart = rootPath.substring(rootPath.lastIndexOf(":") + 1);
            }
            return new SFAttribute(lastPart, null, SFAttribute.COMPOUND, false, "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     *  Description of the Method
     *
     *@param  comp_path            Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Context sfGetAttributes(String comp_path) throws RemoteException, Exception {
        rootPath = getRootPath();
        String fullPath = rootPath;
        if (comp_path == null || comp_path.equals("")) {
            fullPath = rootPath;
        } else {
            fullPath = fullPath + ":" + comp_path;
        }

        Object component = null;
        try {
            component = sfResolve(fullPath);
            // if it is a reference, we resolve it first.
            // if (component instanceof Reference) component = sfResolve((Reference)component);
        } catch (Exception ex) {
            return null;
        }

        Context context = null;
        boolean isInstanceOfPrim = false;
        if (component instanceof Prim) {
            context = ((Prim) component).sfContext();
            isInstanceOfPrim = true;
        } else if (component instanceof ComponentDescription) {
            context = ((ComponentDescription) component).sfContext();
        } else {
            return null;
        }

        Context c = new ContextImpl();
        // We use a context since it keeps the order of the elements
        for (Enumeration e = context.keys(); e.hasMoreElements(); ) {
            try {
                String name = e.nextElement().toString();
                Object value = context.get(name);
                int sfType = SFAttribute.UNKNOWN;
                boolean writable = false;
                String description = "";

                Reference reference = null;
                if (value instanceof Reference) {
                    reference = (Reference) value;
                    try {
                        // Resolve the reference
                        if (isInstanceOfPrim) {
                            value = ((Prim) component).sfResolve(reference);
                        } else {
                            value = ((ReferenceResolver) component).sfResolve(reference);
                        }
                        // It's ComponentDescription
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                // Check the type of the referencedObject
                if (value instanceof Compound) {
                    sfType = SFAttribute.COMPOUND;
                } else if (value instanceof Prim) {
                    sfType = SFAttribute.PRIM;
                } else if (value instanceof ComponentDescription) {
                    sfType = SFAttribute.COMP_DESCRIPTION;
                }
                // If it is ComponentDescription and the Reference is LAZY, we still have the LAZY Reference not resolved
                else if (value instanceof Reference) {
                    sfType = SFAttribute.REFERENCE;
                } else {
                    sfType = SFAttribute.BASIC;
                }

                if (sfType != SFAttribute.BASIC) {
                    if (reference != null) {
                        value = reference.toString();
                    }
                    // If it isn't BASIC, we left the reference as value
                    else {
                        value = null;
                    }
                } else {
                    // Case of a SF BASIC attribute, we get Attribute info from parser
                    if (!parser.isManageableAttribute(name)) {
                        continue;
                    }
                    if (!parser.isReadable(name)) {
                        value = null;
                    }
                    writable = parser.isWritable(name);
                    description = parser.getDescription(name);
                    value = Utilities.objectFromString(parser.getType(name), value);
                }
                // We build the SF Attribute
                SFAttribute sfAttr = new SFAttribute(name, value, sfType, writable, description);
                c.put(name, sfAttr);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return c;
    }


    /**
     *  This method replace an attribute resolving LAZY references recursively
     *  if necessary.
     *
     *@param  resolver       Description of the Parameter
     *@param  ref            Description of the Parameter
     *@param  value          Description of the Parameter
     *@exception  Exception  Description of the Exception
     */
    private void sfReplaceReferencedAttribute(RemoteReferenceResolver resolver, Reference ref, Object value) throws Exception {
        // Get the reference to parent that contains pointed attribute
        Reference reference = (Reference) ref.copy();
        ReferencePart attribRefPart = reference.lastElement();
        // ReferencePart that corresponds to the attribute
        reference.removeElement(attribRefPart);
        // Remove the attribute ReferencePart. Now it points to the parent

        // Resolve the reference and substitute the container by the parent that owns the pointed attribute
        Object container = resolver.sfResolve(reference);
        String attribute = attribRefPart.toString();
        Object referencedObject = null;
        if (container instanceof Prim) {
            referencedObject = ((Prim) container).sfResolveHere(attribute); //sfResolveId
        } else if (container instanceof ComponentDescription) {
            referencedObject = ((ComponentDescription) container).sfResolveHere(attribute);//sfResolveId
        }

        // If it is still a reference we call this method recursively, otherwise we replace the attribute
        if (referencedObject instanceof Reference) {
            if (container instanceof ComponentDescription) {
                // If the container is a ComponentDescription, we replace the reference
                // since it can not resolve a LAZY link
                ((ComponentDescription) container).sfContext().put(attribute, value);
            } else {
                sfReplaceReferencedAttribute((RemoteReferenceResolver) container,
                        (Reference) referencedObject,
                        value);
            }
        } else {
            Object formatedValue = formatValue(value, referencedObject);
            // Replace attribute
            if (container instanceof Prim) {
                ((Prim) container).sfReplaceAttribute(attribute, formatedValue);
            } else if (container instanceof ComponentDescription) {
                ((ComponentDescription) container).sfContext().put(attribute, formatedValue);
            }
        }
    }


    /**
     *  This method return the value formated with the same type of the other
     *  object given as a parameter @ Object value The object to be formated @
     *  Object format The object given as a reference from which the type is
     *  taken @ return Object Formated object @ exception If the given value can
     *  not be formated with type of the object given as a reference
     *
     *@param  value                         Description of the Parameter
     *@param  format                        Description of the Parameter
     *@return                               Description of the Return Value
     *@exception  IllegalArgumentException  Description of the Exception
     */
    private Object formatValue(Object value, Object format) throws IllegalArgumentException {
        String type = format.getClass().getName();
        Object formatedValue = Utilities.objectFromString(type, value);
        if (formatedValue == null) {
            throw new java.lang.IllegalArgumentException("Argument does not match type '" + type + "': " + value);
        }
        return formatedValue;
    }


    /**
     *  Description of the Method
     *
     *@param  attrib_path          Description of the Parameter
     *@param  newValue             Description of the Parameter
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sfSetAttribute(String attrib_path, Object newValue) throws RemoteException, Exception {
        // Check Illegal Arguments
        if (attrib_path == null || attrib_path.equals("")) {
            throw new IllegalArgumentException("Attribute cannot be null or empty");
        }

        // Get the last part of the reference, which is the name of the attribute
        String attribute = attrib_path.substring(attrib_path.lastIndexOf(":") + 1);

        // Check permisions in the parser
        if (!parser.isManageableAttribute(attribute)) {
            throw new NonManageableAttributeException(attribute);
        }
        if (!parser.isWritable(attribute)) {
            throw new NonWritableAttributeException(attribute);
        }

        // Update rootPath just in case of any modification
        rootPath = getRootPath();

        // Obtain the full path of the component that contains the attribute
        String fullPath = rootPath;
        String comp_path = null;
        int lastIndex = attrib_path.lastIndexOf(":");
        if (lastIndex > -1) {
            comp_path = attrib_path.substring(0, lastIndex);
        }
        if (!(comp_path == null || comp_path.equals(""))) {
            fullPath = fullPath + ":" + comp_path;
        }

        // Once we have the fullPath, we get the parent component
        Object component = sfResolve(fullPath);

        // We get the current value contain in the Context of the component
        Object value = null;
        boolean isInstanceOfPrim = false;
        // Flag indicating if the component is a Prim or a Component Description
        if (component instanceof Prim) {
            value = ((Prim) component).sfContext().get(attribute);
            isInstanceOfPrim = true;
        } else if (component instanceof ComponentDescription) {
            value = ((ComponentDescription) component).sfContext().get(attribute);
        } else {
            return;
        }

        // Check if the value is still a reference. If so, resolve it.
        if (value instanceof Reference) {
            if (component instanceof ComponentDescription) {
                // If the container is a ComponentDescription, we replace the reference without checking format,
                // since it can not resolve a LAZY link
                ((ComponentDescription) component).sfContext().put(attribute, newValue);
            } else {
                sfReplaceReferencedAttribute((Prim) component, (Reference) value, newValue);
            }
        } else {
            // Format the new value for the attribute with type of the current value
            Object formatedValue = formatValue(newValue, value);
            // Replace attribute
            if (component instanceof Prim) {
                ((Prim) component).sfReplaceAttribute(attribute, formatedValue);
            } else if (component instanceof ComponentDescription) {
                ((ComponentDescription) component).sfContext().put(attribute, formatedValue);
            }
        }
        //Send notification
        sendNotification(
                new AttributeChangeNotification(this,
                sequence++,
                System.currentTimeMillis(),
                "SmartFrog attribute modified",
                attrib_path,
                newValue.getClass().getName(),
                value,
                newValue));
    }


    /**
     *  Description of the Method
     *
     *@param  attrib_path          Description of the Parameter
     *@param  value                Description of the Parameter
     *@param  type                 Description of the Parameter
     *@param  description          Description of the Parameter
     *@param  readable             Description of the Parameter
     *@param  writable             Description of the Parameter
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sfAddAttribute(String attrib_path, String value, String type, String description, boolean readable, boolean writable) throws RemoteException, Exception {
        if (attrib_path == null || attrib_path.equals("")) {
            throw new IllegalArgumentException("Attribute cannot be null or empty");
        }
        rootPath = getRootPath();
        String fullPath = rootPath;
        String attribute = attrib_path.substring(attrib_path.lastIndexOf(":") + 1);
        String comp_path = null;
        int lastIndex = attrib_path.lastIndexOf(":");
        if (lastIndex > -1) {
            comp_path = attrib_path.substring(0, lastIndex);
        }
        if (!(comp_path == null || comp_path.equals(""))) {
            fullPath = fullPath + ":" + comp_path;
        }
        Object component = sfResolve(fullPath);
        Object val = Utilities.objectFromString(type, value);

        if (component instanceof Reference) {
            component = sfResolve((Reference) component);
        }

        if (component instanceof Prim) {
            ((Prim) component).sfAddAttribute(attribute, val);
        } else if (component instanceof ComponentDescription) {
            ((ComponentDescription) component).sfContext().put(attribute, val);
        } else {
            return;
        }
        parser.addAttributeInfoFor(attribute, val, type, description, readable, writable);
    }


    /**
     *  Description of the Method
     *
     *@param  attrib_path          Description of the Parameter
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sfRemoveAttribute(String attrib_path) throws RemoteException, Exception {
        if (attrib_path == null || attrib_path.equals("")) {
            throw new IllegalArgumentException("Attribute cannot be null or empty");
        }
        String attribute = attrib_path.substring(attrib_path.lastIndexOf(":") + 1);
        if (!parser.isManageableAttribute(attribute)) {
            throw new NonManageableAttributeException(attribute);
        }
        if (!parser.isWritable(attribute)) {
            throw new NonWritableAttributeException(attribute);
        }

        rootPath = getRootPath();
        String fullPath = rootPath;

        String comp_path = null;
        int lastIndex = attrib_path.lastIndexOf(":");
        if (lastIndex > -1) {
            comp_path = attrib_path.substring(0, lastIndex);
        }
        if (!(comp_path == null || comp_path.equals(""))) {
            fullPath = fullPath + ":" + comp_path;
        }
        Object component = sfResolve(fullPath);

        if (component instanceof Reference) {
            component = sfResolve((Reference) component);
        }

        if (component instanceof Prim) {
            ((Prim) component).sfRemoveAttribute(attribute);
        } else if (component instanceof ComponentDescription) {
            ((ComponentDescription) component).sfContext().remove(attribute);
        } else {
            return;
        }
    }


    /**
     *  Description of the Method
     *
     *@param  comp_path            Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public MBeanOperationInfo[] sfGetMethods(String comp_path) throws RemoteException, Exception {
        rootPath = getRootPath();
        String fullPath = rootPath;
        if (comp_path != null && !comp_path.equals("")) {
            fullPath = fullPath + ":" + comp_path;
        }
        Object component = sfResolve(fullPath);

        if (component instanceof Reference) {
            component = sfResolve((Reference) component);
        }

        if (!(component instanceof Prim)) {
            return null;
        }

        Class beanClass = component.getClass();
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        MethodDescriptor[] methodDescr = beanInfo.getMethodDescriptors();
        ArrayList methodList = new ArrayList();
        // We go through the Method Descriptors to create MBeanOperationInfo[]
        for (int i = 0; i < methodDescr.length; i++) {
            Method method = methodDescr[i].getMethod();
            MethodID methodID = new MethodID(method.getName(), method.getParameterTypes());
            if (parser.isManageableMethod(methodID)) {
                Context methodContext = parser.getMethodContext(methodID);
                MBeanParameterInfo[] parameterInfo = null;
                // Default information
                String name = method.getName();
                String type = method.getReturnType().getName();
                int impact = MBeanOperationInfo.UNKNOWN;
                String description = "Method exposed for management";

                if (methodContext != null) {
                    // If a SF context exists for this method, we extract information
                    if (((String) methodContext.get("impact")).equals("INFO")) {
                        impact = MBeanOperationInfo.INFO;
                    } else if (((String) methodContext.get("impact")).equals("ACTION_INFO")) {
                        impact = MBeanOperationInfo.ACTION_INFO;
                    } else if (((String) methodContext.get("impact")).equals("ACTION")) {
                        impact = MBeanOperationInfo.ACTION;
                    }
                    description = (String) methodContext.get("description");
                    parameterInfo = (MBeanParameterInfo[]) methodContext.get("parameters");
                } else {
                    // Else we only create parameter Information from the Method Descriptors
                    Class[] parTypes = methodDescr[i].getMethod().getParameterTypes();
                    parameterInfo = new MBeanParameterInfo[parTypes.length];
                    for (int j = 0; j < parTypes.length; j++) {
                        parameterInfo[j] = new MBeanParameterInfo("p" + j, parTypes[j].getName(), "Parameter " + j);
                    }
                }
                methodList.add(new MBeanOperationInfo(name, description, parameterInfo, type, impact));
            }
        }
        MBeanOperationInfo[] operationInfo = new MBeanOperationInfo[methodList.size()];
        methodList.toArray(operationInfo);
        return operationInfo;
    }


    /**
     *  Description of the Method
     *
     *@param  comp_path            Description of the Parameter
     *@param  method               Description of the Parameter
     *@param  params               Description of the Parameter
     *@param  signature            Description of the Parameter
     *@return                      Description of the Return Value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Object sfInvokeMethod(String comp_path, String method, String params, String signature) throws RemoteException, Exception {
        if (method == null) {
            throw new IllegalArgumentException("Neither method nor signature can be null");
        }
        rootPath = getRootPath();
        String fullPath = rootPath;
        if (comp_path != null && !comp_path.equals("")) {
            fullPath = fullPath + ":" + comp_path;
        }
        Object component = sfResolve(fullPath);

        if (component instanceof Reference) {
            component = sfResolve((Reference) component);
        }

        if (!(component instanceof Prim)) {
            return null;
        }
        if (signature == null) {
            signature = "";
        }
        if (params == null) {
            params = "";
        }
        StringTokenizer st1 = new StringTokenizer(signature, ",");
        StringTokenizer st2 = new StringTokenizer(params, ",");
        if (st1.countTokens() != st2.countTokens()) {
            throw new IllegalArgumentException("Mismatch between parameters and signature");
        }
        Class[] signClass = new Class[st1.countTokens()];
        Object[] parameters = new Object[st2.countTokens()];
        for (int i = 0; i < signClass.length; i++) {
            signClass[i] = Utilities.classFromString(st1.nextToken());
            parameters[i] = Utilities.objectFromString(signClass[i].getName(), st2.nextElement());
        }
        Class beanClass = component.getClass();
        Method m = beanClass.getMethod(method, signClass);
        MethodID methodID = new MethodID(m.getName(), m.getParameterTypes());
        if (!parser.isManageableMethod(methodID)) {
            throw new NonManageableMethodException(methodID.toString());
        }
        return m.invoke(component, parameters);
    }


    /**
     *  Description of the Method
     *
     *@param  attribute            Description of the Parameter
     *@param  readable             Description of the Parameter
     *@param  writable             Description of the Parameter
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sfChangeAccess(String attribute, boolean readable, boolean writable) throws RemoteException, Exception {
        if (parser.isManageableAttribute(attribute)) {
            parser.changeAccessFor(attribute, readable, writable);
        } else {
            throw new NonManageableAttributeException(attribute);
        }
    }

}
