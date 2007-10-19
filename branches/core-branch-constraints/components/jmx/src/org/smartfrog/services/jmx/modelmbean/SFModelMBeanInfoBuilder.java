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

package org.smartfrog.services.jmx.modelmbean;

import java.util.*;
import java.lang.reflect.Method;
import java.beans.Introspector;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.MethodDescriptor;
import java.rmi.*;
import java.rmi.server.RemoteStub;

import javax.management.*;
import javax.management.modelmbean.*;

import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.componentdescription.*;
import org.smartfrog.services.jmx.common.*;
import org.smartfrog.services.jmx.deployer.MBeanDeployerMBean;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class SFModelMBeanInfoBuilder {

    /**
     *  Description of the Field
     */
    protected Context modelMBeanContext = null;

    /**
     *  Description of the Field
     */
    protected String className = null;
    /**
     *  Description of the Field
     */
    protected String description = null;

    /**
     *  Description of the Field
     */
    protected ModelMBeanInfoSupport mbeanInfo = null;

    // Hashtable with all the Model MBean Info
    //protected List constructors = new ArrayList();
    /**
     *  Description of the Field
     */
    protected Vector notifications = new Vector();
    /**
     *  Description of the Field
     */
    protected Hashtable attributes = new Hashtable();

    /**
     *  Description of the Field
     */
    protected Hashtable operations = new Hashtable();
    /**
     *  Description of the Field
     */
    protected Descriptor descriptor = null;

    /**
     *  Parser for attributes
     */
    MetadataParser atParser = null;

    /**
     *  Parser for operations
     */
    MetadataParser opParser = null;


    /**
     *  Default constructor. It creates a SFModelMBeanInfoBuilder without any
     *  target on which to make introspection.
     */
    public SFModelMBeanInfoBuilder() { }


    /**
     *  Creates a SFModelMBeanInfoBuilder with a Prim object as the target to
     *  introspect and required information about the ModelMBean to be created.
     *
     *@param  mr             Description of the Parameter
     *@param  mmbCtxt        Description of the Parameter
     *@exception  Exception  Description of the Exception
     */
    public SFModelMBeanInfoBuilder(Object mr, Context mmbCtxt) throws Exception {
        setModelMBeanContext(mmbCtxt);
    }


    /**
     *  Configure this SFModelMBeanInfoBuilder with a managed resource to be
     *  introspected and required information about the ModelMBean to be
     *  created.
     *
     *@param  mmbCtxt        The new modelMBeanContext value
     *@exception  Exception  Description of the Exception
     */
    public void setModelMBeanContext(Context mmbCtxt) throws Exception {
        if (mmbCtxt == null) {
            throw new java.lang.IllegalArgumentException("ModelMBean context argument cannot be null");
        }

        modelMBeanContext = mmbCtxt;
        className = (String) modelMBeanContext.get("class");
        description = (String) modelMBeanContext.get("description");

        // Extract fields and make sure that the following fields are available in the SF description
        Context fieldContext = ((ComponentDescription) modelMBeanContext.get("fields")).sfContext();

        String descriptorType = (String) fieldContext.get("descriptorType");
        if (descriptorType == null || !descriptorType.equalsIgnoreCase("mbean")) {
            throw new Exception("Unexpected descriptorType: " + descriptorType);
        }
        if (!fieldContext.containsKey("name")) {
            throw new Exception("Field name must be specified");
        }
        if (!fieldContext.containsKey("displayName")) {
            fieldContext.put("displayName", fieldContext.get("name"));
        }

        // Build the ModelMBean descriptor from the fields
        String[] fieldArray = new String[fieldContext.size()];
        int i = 0;
        for (Enumeration keys = fieldContext.keys(); keys.hasMoreElements(); i++) {
            String key = (String) keys.nextElement();
            fieldArray[i] = key + "=" + fieldContext.get(key).toString();
        }
        if (descriptor == null) {
            descriptor = new DescriptorSupport(fieldArray);
        }

        // Extract and parse attribute descriptions
        Context attributesContext = ((ComponentDescription) modelMBeanContext.get("attributes")).sfContext();
        atParser = new MetadataParser(attributesContext);

        // Extract and parse operation descriptions
        Context operationsContext = ((ComponentDescription) modelMBeanContext.get("operations")).sfContext();
        opParser = new MetadataParser(operationsContext);
    }


    /**
     *  Converts a Collection containing ModelMBeanAttributeInfo objects into an
     *  array of ModelMBeanAttributeInfo.
     *
     *@param  attribCollection  Description of the Parameter
     *@return                   array with the objects contained in the
     *      Collection
     */
    public static ModelMBeanAttributeInfo[] toAttributeArray(Collection attribCollection) {
        if (attribCollection == null || attribCollection.size() == 0) {
            return new ModelMBeanAttributeInfo[0];
        }
        ModelMBeanAttributeInfo[] attributesArray = new ModelMBeanAttributeInfo[attribCollection.size()];
        attribCollection.toArray(attributesArray);
        return attributesArray;
    }


    /**
     *  Description of the Method
     *
     *@param  opCollection  Description of the Parameter
     *@return               Description of the Return Value
     */
    public static ModelMBeanOperationInfo[] toOperationArray(Collection opCollection) {
        if (opCollection == null || opCollection.size() == 0) {
            return new ModelMBeanOperationInfo[0];
        }
        ModelMBeanOperationInfo[] operationsArray = new ModelMBeanOperationInfo[opCollection.size()];
        opCollection.toArray(operationsArray);
        return operationsArray;
    }


    /**
     *  Description of the Method
     *
     *@param  notifCollection  Description of the Parameter
     *@return                  Description of the Return Value
     */
    public static ModelMBeanNotificationInfo[] toNotificationArray(Collection notifCollection) {
        if (notifCollection == null || notifCollection.size() == 0) {
            return new ModelMBeanNotificationInfo[0];
        }
        ModelMBeanNotificationInfo[] notifArray = new ModelMBeanNotificationInfo[notifCollection.size()];
        notifCollection.toArray(notifArray);
        return notifArray;
    }


    /**
     *  Gets the modelMBeanInfo attribute of the SFModelMBeanInfoBuilder object
     *
     *@return    The modelMBeanInfo value
     */
    public ModelMBeanInfo getModelMBeanInfo() {
        return (new ModelMBeanInfoSupport(
                className,
                description,
                toAttributeArray(attributes.values()),
                null,
                toOperationArray(operations.values()),
                toNotificationArray(notifications),
                descriptor));
    }


    /**
     *  Gets the attributeInfo attribute of the SFModelMBeanInfoBuilder object
     *
     *@param  attribute  Description of the Parameter
     *@return            The attributeInfo value
     */
    public ModelMBeanAttributeInfo getAttributeInfo(String attribute) {
        return (ModelMBeanAttributeInfo) this.attributes.get(attribute);
    }


    /**
     *  Description of the Method
     *
     *@param  name  Description of the Parameter
     */
    public void removeAttributeInfoFor(String name) {
        if (attributes != null) {
            attributes.remove(name);
        }
    }


    /**
     *@param  methodID  Description of the Parameter
     */
    public void removeOperationInfoFor(MethodID methodID) {
        if (operations != null) {
            operations.remove(methodID);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  finder                                       Description of the
     *      Parameter
     *@param  objectTarget                                 Description of the
     *      Parameter
     *@return                                              Description of the
     *      Return Value
     *@exception  java.beans.IntrospectionException        Description of the
     *      Exception
     *@exception  javax.management.IntrospectionException  Description of the
     *      Exception
     *@exception  RemoteException                          Description of the
     *      Exception
     *@exception  Exception                                Description of the
     *      Exception
     */
    public ModelMBeanInfo buildInfoFromTarget(MBeanDeployerMBean finder, Object objectTarget)
             throws java.beans.IntrospectionException, javax.management.IntrospectionException, RemoteException, Exception {
        //if (!(target instanceof MngPrim)) return null;
        buildAttributeInfo(objectTarget);
        buildOperationInfo(finder, objectTarget);
        buildNotificationInfo();
        // We build the ModelMBeanInfo
        return this.getModelMBeanInfo();
    }


    /**
     *  Description of the Method
     *
     *@param  targetObject   Description of the Parameter
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    public ModelMBeanAttributeInfo[] buildAttributeInfo(Object targetObject) throws Exception {
        return buildAttributeInfo(targetObject, atParser);
    }


    /**
     *  Description of the Method
     *
     *@param  targetObject   Description of the Parameter
     *@param  parser         Description of the Parameter
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    public ModelMBeanAttributeInfo[] buildAttributeInfo(Object targetObject, MetadataParser parser) throws Exception {
        if (targetObject == null) {
            throw new java.lang.IllegalArgumentException("Managed resource argument cannot be null");
        }
        try {
            Enumeration attribMetadata = parser.getAttributeMetadata();
            if (parser == null || !attribMetadata.hasMoreElements()) {
                if (targetObject == null) {
                    return null;
                }
                return getAttributeInfoFromTarget(targetObject);
            } else {
                return getAttributeInfoFromMetadata(targetObject, attribMetadata);
            }
        } catch (Exception ex) {
            System.out.println("buildAttributeInfo(): " + ex.toString());
            ex.printStackTrace();
            return null;
        }
    }


    /**
     *  Gets an rray of ModelMBeanAttributeInfo by instropecting the
     *  targetObject.
     *
     *@param  targetObject
     *@return the attribute info
     *@exception  java.beans.IntrospectionException  Description of the
     *      Exception
     */
    public ModelMBeanAttributeInfo[] getAttributeInfoFromTarget(Object targetObject) throws java.beans.IntrospectionException {
        Class targetClass = targetObject.getClass();
        BeanInfo beanInfo = Introspector.getBeanInfo(targetClass);
        PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < properties.length; i++) {
            try {
                ModelMBeanAttributeInfo attributeInfo = getAttributeInfoFrom(properties[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toAttributeArray(attributes.values());
    }


    /**
     *  Gets the attributeInfoFrom attribute of the SFModelMBeanInfoBuilder
     *  object
     *
     *@param  property                                     Description of the
     *      Parameter
     *@return                                              The attributeInfoFrom
     *      value
     *@exception  javax.management.IntrospectionException  Description of the
     *      Exception
     */
    public ModelMBeanAttributeInfo getAttributeInfoFrom(PropertyDescriptor property) throws javax.management.IntrospectionException {
        if (property == null) {
            return null;
        }
        ModelMBeanAttributeInfo attribute = null;
        Descriptor attributeDesc = new DescriptorSupport();
        Method getMethod = null;
        Method setMethod = null;
        String name = null;

        getMethod = property.getReadMethod();
        setMethod = property.getWriteMethod();
        if (getMethod == null && setMethod == null) {
            return null;
        }

        name = property.getName();
        attributeDesc.setField("name", name);
        attributeDesc.setField("descriptorType", "attribute");
        attributeDesc.setField("displayName", property.getDisplayName());

        if (getMethod != null) {
            attributeDesc.setField("getMethod", getMethod.getName());
        }
        if (setMethod != null) {
            attributeDesc.setField("setMethod", setMethod.getName());
        }
        attribute = new ModelMBeanAttributeInfo(
                name,
                property.getShortDescription(),
                getMethod,
                setMethod,
                attributeDesc);
        attributes.put(name, attribute);
        if (getMethod != null) {
            getOperationInfoFrom(getMethod, "getter", "Returns the value of the attribute " + name);
        }
        if (setMethod != null) {
            getOperationInfoFrom(setMethod, "setter", "Sets a new value for the attribute " + name);
        }
        return attribute;
    }


    /**
     *  Gets an array of ModelMBeanAttributeInfo from the attribute metadata
     *  provided in the Enumeration making checks in the targetObject if
     *  neccesary.
     *
     *@param  targetObject target to work on
     *@param  atMetadata    an existing enumeration (can be null)
     *@return the metadata as an array. If the enumeration is null, array is of size 0
     */
    public ModelMBeanAttributeInfo[] getAttributeInfoFromMetadata(Object targetObject, Enumeration atMetadata) {
        if (atMetadata == null) {
            return new ModelMBeanAttributeInfo[0];
        }
        while (atMetadata.hasMoreElements()) {
            try {
                ModelMBeanAttributeInfo attributeInfo = getAttributeInfoFrom(targetObject, (Context) atMetadata.nextElement());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toAttributeArray(attributes.values());
    }


    /**
     *  Methods to build Attribute Info *
     *
     *@param  targetObject   Description of the Parameter
     *@param  attrCtxt       Description of the Parameter
     *@return                The attributeInfoFrom value
     *@exception  Exception  Description of the Exception
     */
    public ModelMBeanAttributeInfo getAttributeInfoFrom(Object targetObject, Context attrCtxt)
             throws Exception {
        if (attrCtxt == null) {
            return null;
        }
        Context attributeContext = (Context) attrCtxt.copy();

        Descriptor attributeDesc = new DescriptorSupport();
        Descriptor getterDescriptor = null;
        Descriptor setterDescriptor = null;
        String name = null;
        String type = null;
        boolean readable = true;
        boolean writable = true;
        String description = null;
        String getter = null;
        String setter = null;
        boolean isIsMethod = false;
        boolean isGetterParameter = false;
        boolean isSetterParameter = false;
        Method getMethod = null;
        Method setMethod = null;

        // If descriptor type is not attribute or the attribute does not have a name, this does not make sense
        if (!"attribute".equals(attributeContext.get("descriptorType"))) {
            throw new Exception("Unexpected attribute descriptor type: " + attributeContext.get("descriptorType"));
        }
        if ((name = (String) attributeContext.get("name")) == null) {
            throw new Exception("Attribute name not specified");
        }
        if ((type = (String) attributeContext.remove("type")) == null) {
            throw new Exception("Attribute type not specified");
        }
        readable = ((Boolean) attributeContext.remove("readable")).booleanValue();
        writable = ((Boolean) attributeContext.remove("writable")).booleanValue();
        description = (String) attributeContext.remove("description");
        getter = (String) attributeContext.remove("getMethod");
        setter = (String) attributeContext.remove("setMethod");
        isGetterParameter = (!attributeContext.containsKey("isGetterParameter")) ? false : ((Boolean) attributeContext.get("isGetterParameter")).booleanValue();
        isSetterParameter = (!attributeContext.containsKey("isSetterParameter")) ? false : ((Boolean) attributeContext.get("isSetterParameter")).booleanValue();

        // set the fields
        attributeDesc.setField("name", name);
//      attributeDesc.setField("descriptorType", "attribute"); // It may be specified in the Context, but make sure
//      attributeDesc.setField("displayName", name);
        for (Enumeration keys = attributeContext.keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            attributeDesc.setField(key.toString(), attributeContext.get(key).toString());
        }

        // Check getter/setter methods
        if (readable) {
            getMethod = Utilities.checkGetter(getter, targetObject, name, type, isGetterParameter);
        }
        if (writable) {
            setMethod = Utilities.checkSetter(setter, targetObject, name, type, isSetterParameter);
        }
        if (getMethod != null) {
            getter = getMethod.getName();
            attributeDesc.setField("getMethod", getter);
            String returnType = getMethod.getReturnType().getName();
            if ((returnType.equals("java.lang.Boolean") || returnType.equals("boolean")) &&
                    getter.startsWith("is")) {
                isIsMethod = true;
            }
        }
        if (setMethod != null) {
            setter = setMethod.getName();
            attributeDesc.setField("setMethod", setter);
        }

        if (description == null) {
            description = "Attribute exposed for management";
        }

        ModelMBeanAttributeInfo attrInfo = null;
        if (type == null) {
            attrInfo = new ModelMBeanAttributeInfo(
                    name,
                    description,
                    getMethod,
                    setMethod,
                    attributeDesc);
        } else {
            attrInfo = new ModelMBeanAttributeInfo(
                    name,
                    type,
                    description,
                    readable,
                    writable,
                    isIsMethod,
                    attributeDesc);
        }
        attributes.put(name, attrInfo);

        if (getMethod != null) {
            String getDescription = "Operation to get the value of the attribute " + name;
            getOperationInfoFrom(getMethod, "getter", getDescription);
        }

        if (setMethod != null) {
            String setDescription = "Operation to set a new value of the attribute " + name;
            getOperationInfoFrom(setMethod, "setter", setDescription);
        }

        return attrInfo;
    }


    /**
     *  Gets the operationInfoFrom attribute of the SFModelMBeanInfoBuilder
     *  object
     *
     *@param  method       Description of the Parameter
     *@param  role         Description of the Parameter
     *@param  description  Description of the Parameter
     *@return              The operationInfoFrom value
     */
    public ModelMBeanOperationInfo getOperationInfoFrom(Method method, String role, String description) {
        Descriptor descriptor = new DescriptorSupport();
        descriptor.setField("name", method.getName());
        descriptor.setField("descriptorType", "operation");
        descriptor.setField("displayName", method.getName());
        descriptor.setField("role", role);
        ModelMBeanOperationInfo operationInfo = new ModelMBeanOperationInfo(description, method, descriptor);
        MethodID methodID = new MethodID(method.getName(), method.getParameterTypes());
        operations.put(methodID, operationInfo);
        return operationInfo;
    }


    /**
     *  Methods to build the Operation Info ****
     *
     *@param  finder                                 Description of the
     *      Parameter
     *@param  targetObject                           Description of the
     *      Parameter
     *@return                                        Description of the Return
     *      Value
     *@exception  java.beans.IntrospectionException  Description of the
     *      Exception
     *@exception  Exception                          Description of the
     *      Exception
     */

    /**
     *  Description of the Method
     *
     *@param  targetObject                           Description of the
     *      Parameter
     *@param  finder                                 Description of the
     *      Parameter
     *@return                                        Description of the Return
     *      Value
     *@exception  java.beans.IntrospectionException  Description of the
     *      Exception
     *@exception  Exception                          Description of the
     *      Exception
     */
    public ModelMBeanOperationInfo[] buildOperationInfo(MBeanDeployerMBean finder, Object targetObject) throws java.beans.IntrospectionException, Exception {
        return buildOperationInfo(finder, targetObject, opParser);
    }


    /**
     *  Description of the Method
     *
     *@param  targetObject   Description of the Parameter
     *@param  parser         Description of the Parameter
     *@param  finder         Description of the Parameter
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    public ModelMBeanOperationInfo[] buildOperationInfo(MBeanDeployerMBean finder, Object targetObject, MetadataParser parser) throws Exception {
        if (targetObject == null) {
            throw new IllegalArgumentException("Target object cannot be null");
        }
        try {
            Enumeration opMetadata = parser.getMethodMetadata();
            if (parser == null || !opMetadata.hasMoreElements()) {
                return getOperationInfoFromTarget(targetObject);
            } else {
                return getOperationInfoFromMetadata(finder, targetObject, opMetadata);
            }
        } catch (Exception ex) {
            System.out.println("buildOperationInfo(): " + ex.toString());
            ex.printStackTrace();
            return null;
        }
    }


    /**
     *@param  targetObject                           Description of the
     *      Parameter
     *@return the operation information as an array
     *@exception  java.beans.IntrospectionException  Description of the
     *      Exception
     */
    public ModelMBeanOperationInfo[] getOperationInfoFromTarget(Object targetObject) throws java.beans.IntrospectionException {
        Class targetClass = targetObject.getClass();
        BeanInfo beanInfo = Introspector.getBeanInfo(targetClass);
        MethodDescriptor[] methods = beanInfo.getMethodDescriptors();
        for (int i = 0; i < methods.length; i++) {
            try {
                getOperationInfoFrom(methods[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toOperationArray(operations.values());
    }


    /**
     *  Gets the operationInfoFrom attribute of the SFModelMBeanInfoBuilder
     *  object
     *
     *@param  method  Description of the Parameter
     *@return         The operationInfoFrom value
     */
    public ModelMBeanOperationInfo getOperationInfoFrom(MethodDescriptor method) {
        if (method == null) {
            return null;
        }
        ModelMBeanOperationInfo operation = null;
        Descriptor operationDesc = new DescriptorSupport();
        String name = null;
        MethodID methodID = new MethodID(method.getMethod().getName(), method.getMethod().getParameterTypes());
        if (operations.contains(methodID)) {
            return (ModelMBeanOperationInfo) operations.get(methodID);
        }

        name = method.getName();
        operationDesc.setField("name", name);
        operationDesc.setField("descriptorType", "operation");
        operationDesc.setField("displayName", method.getDisplayName());

        operation = new ModelMBeanOperationInfo(
                method.getShortDescription(),
                method.getMethod(),
                operationDesc);
        operations.put(methodID, operation);
        return operation;
    }


    /**
     * extract metadata from the opMetadata parameter; prints stack traces to stdout on case of trouble.
     *@param  targetObject target object
     *@param  finder        Finder
     *@param  opMetadata    metadata (can be null)
     *@return an array of operation info. will be empty if opMetadata is null
     */
    public ModelMBeanOperationInfo[] getOperationInfoFromMetadata(MBeanDeployerMBean finder, Object targetObject, Enumeration opMetadata) {
        if (opMetadata == null) {
            return new ModelMBeanOperationInfo[0];
        }
        while (opMetadata.hasMoreElements()) {
            try {
                getOperationInfoFrom(finder, targetObject, (Context) opMetadata.nextElement());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toOperationArray(operations.values());
    }


    /**
     *@param  targetObject
     *@param  opCtxt
     *@param  finder         Description of the Parameter
     *@return information about the target object 
     *@exception  Exception  Description of the Exception
     */
    public ModelMBeanOperationInfo getOperationInfoFrom(MBeanDeployerMBean finder, Object targetObject, Context opCtxt) throws Exception {
        if (targetObject == null) {
            throw new Exception("Target object cannot be null");
        }
        if (opCtxt == null) {
            throw new Exception("Operation context cannot be null");
        }
        Context operationContext = (Context) opCtxt.copy();
        ModelMBeanOperationInfo operation = null;
        DescriptorSupport operationDesc = new DescriptorSupport();
        Method method = null;
        MBeanParameterInfo[] parameterInfo = null;
        Class[] params = null;
        String[] signature = null;
        MethodID methodID = null;

        String name = null;
        int impact = MBeanOperationInfo.UNKNOWN;
        int visibility = 1;
        String role = "operation";
        String description = "Method exposed for management";
        Object targetObjRef = null;

        // If there is no name, this does not make sense
        if (!"operation".equals(operationContext.get("descriptorType"))) {
            throw new Exception("Unexpected operation descriptor type: " + operationContext.get("descriptorType"));
        }
        if ((name = (String) operationContext.get("name")) == null) {
            throw new Exception("Invalid operation name: " + name);
        }
        String impactStr = (String) operationContext.remove("impact");
        if (impactStr.equals("INFO")) {
            impact = MBeanOperationInfo.INFO;
        } else if (impactStr.equals("ACTION_INFO")) {
            impact = MBeanOperationInfo.ACTION_INFO;
        } else if (impactStr.equals("ACTION")) {
            impact = MBeanOperationInfo.ACTION;
        }
        visibility = ((Integer) operationContext.remove("visibility")).intValue();
        role = (String) operationContext.remove("role");
        description = (String) operationContext.remove("description");
        parameterInfo = (MBeanParameterInfo[]) operationContext.remove("parameters");
        targetObjRef = operationContext.remove("targetObject");
        if (targetObjRef != null & targetObjRef instanceof Reference) {
            targetObject = finder.findManagedResource((Reference) targetObjRef);
            String resourceType = "ObjectReference";
            if (targetObject instanceof RemoteStub) {
                resourceType = "RMIReference";
            }
            operationDesc.setField("targetobject", targetObject);
            operationDesc.setField("targetType", resourceType);
            operationDesc.setField("targetObjectType", resourceType);
        }

        params = new Class[parameterInfo.length];
        signature = new String[parameterInfo.length];
        for (int i = 0; i < params.length; i++) {
            signature[i] = parameterInfo[i].getType();
            params[i] = Utilities.classFromString(signature[i]);
        }
        methodID = new MethodID(name, params);
        method = Utilities.checkOperation(targetObject, name, signature);
        if (method == null) {
            throw new java.lang.NoSuchMethodException("Not found method " + name + " in " + targetObject.getClass().toString());
        }

        // Set fields
        for (Enumeration keys = operationContext.keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            operationDesc.setField(key.toString(), operationContext.get(key).toString());
        }
        operationDesc.setField("role", role);
        operationDesc.setField("visibility", Integer.toString(visibility));

        operation = new ModelMBeanOperationInfo(name,
                description,
                parameterInfo,
                method.getReturnType().getName(),
                impact,
                operationDesc);
        operations.put(methodID, operation);
        return operation;
    }


    /**
     *  Methods to build Notification Info *
     *
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    public ModelMBeanNotificationInfo[] buildNotificationInfo() throws Exception {
        Context notificationsContext = ((ComponentDescription) modelMBeanContext.get("notifications")).sfContext();
        if (notificationsContext == null) {
            throw new Exception("Notifications description not found");
        }
        for (Enumeration n = notificationsContext.elements(); n.hasMoreElements(); ) {
            try {
                Context notifContext = ((ComponentDescription) n.nextElement()).sfContext();
                getNotificationInfo(notifContext);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        // end for
        return toNotificationArray(notifications);
    }


    /**
     *  Gets the notificationInfo attribute of the SFModelMBeanInfoBuilder
     *  object
     *
     *@param  notifCtxt      Description of the Parameter
     *@return                The notificationInfo value
     *@exception  Exception  Description of the Exception
     */
    public ModelMBeanNotificationInfo getNotificationInfo(Context notifCtxt) throws Exception {
        if (notifCtxt == null) {
            throw new Exception("Notification context cannot be null");
        }
        ModelMBeanNotificationInfo notification = null;
        DescriptorSupport notifDesc = null;
        String name = null;
        // We make sure that the SF descriptions contains the following attributes

        Context notifContext = (Context) notifCtxt.copy();
        if (!"notification".equals(notifContext.get("descriptorType"))) {
            throw new Exception("Unexpected descriptor type: " + notifContext.get("descriptorType"));
        }
        if ((name = (String) notifContext.get("name")) == null) {
            throw new Exception("Invalid notification name: " + name);
        }

        // Get the description and then remove it
        String description = (String) notifContext.remove("description");

        // Get the types for this Notification Info
        Vector typeVector = (Vector) notifContext.remove("type");
        String[] typeArray = new String[typeVector.size()];
        typeVector.toArray(typeArray);

        // Create descriptor and set fields with the remaining attributes
        notifDesc = new DescriptorSupport();
        String[] fieldArray = new String[notifContext.size()];
        int i = 0;
        for (Enumeration keys = notifContext.keys(); keys.hasMoreElements(); i++) {
            Object key = keys.nextElement();
            notifDesc.setField(key.toString(), notifContext.get(key).toString());
        }

        notification = new ModelMBeanNotificationInfo(
                typeArray,
                name,
                description,
                notifDesc);
        notifications.add(notification);
        return notification;
    }

}
