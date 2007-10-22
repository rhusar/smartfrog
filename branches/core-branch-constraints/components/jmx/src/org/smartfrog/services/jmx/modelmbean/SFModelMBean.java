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

import java.lang.reflect.*;
import java.util.*;
import java.io.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.RemoteStub;
import javax.management.*;
import javax.management.modelmbean.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
//import com.sun.management.jmx.Trace;
import mx4j.*;
import org.smartfrog.services.jmx.notification.RemoteNotificationBroadcaster;
import org.smartfrog.services.jmx.notification.RemoteNotificationListenerWrapper;

/*
 *  *
 *  This class implements the SFModelMBean interface by extending the RequiredModelMBean provided
 *  in the JMX RI. It introduces some improvements in order to adapt the RequiredModelMBean to the
 *  needs of SmartFrog.
 *  <P>
 *  It also implements NotificationListener and NotificationFilter interfaces in order to receive
 *  notifications. But for the moment, this MBean is a listener to itself.
 *  The new interface RemoteNotificationBroadcaster has been introduced in order to have the functionality
 *  provided by JMX NotificationBroadCaster but through RMI protocol. By default, only the SmartFrog component
 *  that register its own MBean will be the listener to this Broadcaster. The implementation of this interfaces
 *  enables a remote object to be registered as a listener and receive notifications thrown remotely. The
 *  thrown notifications are those received by the MBean locally so that a remote listener be able
 *  to receive the same notifications as a local component.
 *
 *            sfJMX
 *     JMX-based Management Framework for SmartFrog Applications
 *         Hewlett Packard
 *
 *  @version        1.0
 */
public class SFModelMBean extends RequiredModelMBean implements ModelMBean, MBeanRegistration, NotificationListener {

    /**
     *  The type of the managed resource. By default, it is an RMI Reference
     */
    private String managedResourceType = "RMIReference";
    // ObjectReference | IOR | EJBHandle | MBeanName

    /**
     *  The managed object
     */
    private Object managedResource = null;

    /**
     *  The class name of this ModelMBean
     */
    private String currClass = "SFModelMBean";

    /**
     *  Number of sent notifications by this ModelMBean
     */
    private long notifNumber = 0;

    /**
     *  Description of the Field
     */
    public final static String NOTIFICATION_TYPE_LOAD = "jmx.modelmbean.persistence.load";

    /**
     *  Description of the Field
     */
    public final static String NOTIFICATION_TYPE_STORE = "jmx.modelmbean.persistence.store";

    /**
     *  Description of the Field
     */
    protected SFModelMBeanInfoBuilder infoBuilder = null;

    /**
     *  Description of the Field
     */
    protected ObjectName myName = null;

    /**
     *  Description of the Field
     */
    protected MBeanServer myServer = null;

    /**
     *
     */
    protected RemoteNotificationListenerWrapper listenerWrapper = null;

    /**
     *  Constructor for the SFModelMBean object
     *
     *@exception  MBeanException              Description of the Exception
     *@exception  RuntimeOperationsException  Description of the Exception
     */
    public SFModelMBean() throws MBeanException, RuntimeOperationsException {
        if (tracing()) {
            trace("SFModelMBean(MBeanInfo)", "Entry");
        }
        try {
            load();
        } catch (Exception e) {
            throw new MBeanException(e);
        }
        setModelMBeanInfo(new ModelMBeanInfoSupport((this.getClass().getName()), "SFModelMBean", null, null, null, null));
        if (tracing()) {
            trace("SFModelMBean(MBeanInfo)", "Exit");
        }
    }


    /**
     *  TODO
     *
     *@param  mmbi                            Description of the Parameter
     *@exception  MBeanException              Description of the Exception
     *@exception  RuntimeOperationsException  Description of the Exception
     *@throws  IOException
     */
    public SFModelMBean(ModelMBeanInfo mmbi) throws MBeanException, RuntimeOperationsException {
        if (tracing()) {
            trace("SFModelMBean(MBeanInfo)", "Entry");
        }
        try {
            load();
        } catch (Exception e) {
            throw new MBeanException(e);
        }
        setModelMBeanInfo(mmbi);
        if (tracing()) {
            trace("SFModelMBean(MBeanInfo)", "Exit");
        }
    }


    /**
     *  TODO
     *
     *@param  target
     *@param  mmbi                            Description of the Parameter
     *@exception  MBeanException              Description of the Exception
     *@exception  RuntimeOperationsException  Description of the Exception
     *@throws  IOException
     */
    public SFModelMBean(Object target, ModelMBeanInfo mmbi) throws MBeanException, RuntimeOperationsException {
        if (tracing()) {
            trace("SFModelMBean(MBeanInfo)", "Entry");
        }
        try {
            load();
            setModelMBeanInfo(mmbi);
            setManagedResource(target, "RMIReference");
        } catch (MBeanException e) {
            throw (e);
        } catch (Exception e) {
            throw new MBeanException(e);
        }
        if (tracing()) {
            trace("SFModelMBean(MBeanInfo)", "Exit");
        }
    }


    /**
     *  Sets the modelMBeanInfo attribute of the SFModelMBean object
     *
     *@param  mbi                             The new modelMBeanInfo value
     *@exception  MBeanException              Description of the Exception
     *@exception  RuntimeOperationsException  Description of the Exception
     */
    public void setModelMBeanInfo(ModelMBeanInfo mbi) throws MBeanException, RuntimeOperationsException {
        super.setModelMBeanInfo(mbi);
        try {
            removeAttributeChangeNotificationListener(this, null);
            removeNotificationListener(this);
        } catch (Exception e) {}
        addAttributeChangeNotificationListener(this, null, null);
        addNotificationListener(this, null, null);
    }


    /**
     *  Sets the instance handle of the object against which to execute all
     *  methods in this RequiredModelMBean management interface (ModelMBeanInfo
     *  and Descriptors). This setting can be overridden by setting the
     *  'targetObject' field of the ModelMBeanOperationInfo's descriptor. In the
     *  current implementation of JMX RI, this method only accept
     *  ObjectReference as type of managed resource. For this reason, it has
     *  been overwriten here to accept RMIReference.
     *
     *@param  mr                                    Object that is the managed
     *      resource
     *@param  mr_type                               The type of reference for
     *      the managed resource. Can be: ObjectReference, Handle, IOR,
     *      EJBHandle, RMIReference. If the MBeanServer cannot process the
     *      mr_type passed in, an exception will be thrown.
     *@exception  MBeanException                    The initializer of the
     *      object has thrown an exception.
     *@exception  RuntimeOperationsException        Wraps an
     *      IllegalArgumentException: The managed resource or managed resoure
     *      type passed in parameter is null or invalid.
     *@exception  InstanceNotFoundException         The managed resource object
     *      could not be found
     *@exception  InvalidTargetObjectTypeException  Description of the Exception
     */
    public void setManagedResource(Object mr, String mr_type)
             throws MBeanException, RuntimeOperationsException, InstanceNotFoundException, InvalidTargetObjectTypeException {
        if (tracing()) {
            trace("SFModelMBean.setManagedResource(Object,String)", "Entry");
        }
        if (mr_type != null && mr_type.equalsIgnoreCase("RMIReference")) {
            mr_type = "objectReference";
        }
        if (managedResource != null) {
            try {
                if (managedResource instanceof NotificationBroadcaster) {
                    ((NotificationBroadcaster)managedResource).removeNotificationListener(this);
                }
                else if (managedResource instanceof RemoteNotificationBroadcaster) {
                    ((RemoteNotificationBroadcaster)managedResource).removeRemoteNotificationListener(listenerWrapper);
                }
            } catch (Exception ex) { }
        }

        // Set the new managed resource
        managedResource = mr;
        if (managedResource instanceof NotificationBroadcaster) {
            ((NotificationBroadcaster)managedResource).addNotificationListener(this, null, "managedResource");
        }
        else if (managedResource instanceof RemoteNotificationBroadcaster) {
            try {
                listenerWrapper = new RemoteNotificationListenerWrapper(this);
                if (managedResource instanceof RemoteStub) {
                    UnicastRemoteObject.exportObject(listenerWrapper);
                }
                ((RemoteNotificationBroadcaster)managedResource).addRemoteNotificationListener(listenerWrapper, null, "managedResource");
            }
            catch (RemoteException re) { re.printStackTrace(); }
        }
        super.setManagedResource(mr, mr_type);
//        System.out.println("setManagedResource(): " + managedResource +" "+ mr_type);
        if (tracing()) {
            trace("SFModelMBean.setManagedResource(Object,String)", "Exit");
        }
    }


    /**
     *  The load method instantiates this RequiredModelMBean instance with the
     *  data found for the RequiredModelMBean in the persistent store. The data
     *  loaded will include a serialized ModelMBeanInfo. If there are object
     *  references in the descriptors which are not serializeable, then
     *  persistence will fail. <P>
     *
     *  For the reference implementation, it looks for a file with the same name
     *  as the RequiredModelMBean. It checks the RequiredModelMBean
     *  MBeanDescriptor for a 'PersistLocation' field which would contain the
     *  directory where the RequiredModelMBean's whould be stored. It also looks
     *  for a 'PersistName' field which contains the file name into which the
     *  ModelBMean should be persisted. If these fields are not provided, then
     *  persistence is not implemented. This method should be called during
     *  construction or inialization of this instance, and before the
     *  RequiredModelMBean is registered with the MBeanServer.
     *
     *@exception  MBeanException              Wraps another exception or
     *      persistence is not supported.
     *@exception  RuntimeOperationsException  Wraps exceptions from the
     *      persistence mechanism.
     *@exception  InstanceNotFoundException   Could not find or load this MBean
     *      from persistent storage.
     */
    public void load() throws MBeanException, RuntimeOperationsException, InstanceNotFoundException {
        if (tracing()) {
            trace("load()", "Entry");
        }

        Descriptor mmbDesc = ((ModelMBeanInfo) super.getMBeanInfo()).getMBeanDescriptor();

        // get directory
        String persistDir = (String) mmbDesc.getFieldValue("persistLocation");
        String persistName = (String) mmbDesc.getFieldValue("persistName");

        String currName = (String) mmbDesc.getFieldValue("name");
        if ((persistDir == null) || (persistName == null)) {
            if (tracing()) {
                trace("load()", "Persistence not supported for this MBean");
            }
            return;
        }

        // generate filename from mbean name
        String mbeanFileName = new String(persistDir + File.separator + persistName);
        try {
            // look for file of that name
            FileInputStream fis = new FileInputStream(mbeanFileName);
            createMBeanInfoFromFile(fis);
        } catch (FileNotFoundException fnfe) {
            if (tracing()) {
                trace("load()", "Persistent MBean file was not found");
            }
            throw new MBeanException(fnfe, "Persistent MBean file was not found");
        } catch (IOException ioe) {
            if (tracing()) {
                trace("load()", "IO Exception loading MBean from persistent store");
            }
            throw new MBeanException(ioe, "IO Exception loading MBean from persistent store");
        }
        sendNotification(new Notification(NOTIFICATION_TYPE_LOAD, this.myName, notifNumber++, ((new Date()).getTime()), "ModelMBean loaded successfully"));
        if (tracing()) {
            trace("load()", "Exit");
        }
    }


    /**
     *  Captures the current state of this RequiredModelMBean instance and
     *  writes it out to the persistent store. The state stored could include
     *  attribute and operation values. Persistance policy from the attribute
     *  descriptor is used to guide execution of this method. If no persistence
     *  policy is defined in the descriptor then the persistence policy (if
     *  there is one) defined in the MBeanDescriptor is used. <PRE>
     * Store the MBean if 'persistPolicy' field is:
     *   != "never"
     *   = "always"
     *   = "onTimer" and now > 'lastPersistTime' + 'persistPeriod'
     *   = "NoMoreOftenThan" and now > 'lastPersistTime' + 'persistPeriod'
     *
     * Do not store the MBean if 'persistPolicy' field is:
     *    = "never"
     *    = "onUpdate"
     *    = "onTimer" && now < 'lastPersistTime' + 'persistPeriod'
     *</PRE> <P>
     *
     *  The data stored will include a serialized ModelMBeanInfo. If there are
     *  object references in the descriptors which are not serializeable, then
     *  persistence will fail. <P>
     *
     *  For the reference implementation, it looks for a file with the same name
     *  as the RequiredModelMBean. It checks the RequiredModelMBean
     *  MBeanDescriptor for a 'PersistLocation' field which would contain the
     *  directory where the RequiredModelMBean's whould be stored. It also looks
     *  for a 'PersistName' field which contains the file name into which the
     *  RequiredModelBMean should be persisted. If these fields are not
     *  provided, then persistence is not implemented.
     *
     *@exception  MBeanException              Wraps another exception or
     *      persistence is not supported
     *@exception  RuntimeOperationsException  Wraps exceptions from the
     *      persistence mechanism
     *@exception  InstanceNotFoundException   Could not find/access the
     *      persistant store
     */
    public void store() throws MBeanException, RuntimeOperationsException, InstanceNotFoundException {
        if (tracing()) {
            trace("store()", "Entry");
        }

        /*
         *  check to see if should be persisted
         */
        boolean MBeanPersistItNow = false;
        ModelMBeanInfo modelMBeanInfo = (ModelMBeanInfo) super.getMBeanInfo();
        if (modelMBeanInfo == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("ModelMBeanInfo must not be null"),
                    ("Exception occured trying to set the store data for the RequiredModelMBean"));
        }
        Descriptor mmbDesc = modelMBeanInfo.getMBeanDescriptor();
        MBeanPersistItNow = persistItNow(mmbDesc);
        if (MBeanPersistItNow == true) {
            try {
                // get directory
                String persistDir = (String) mmbDesc.getFieldValue("persistLocation");
                String persistName = (String) mmbDesc.getFieldValue("persistName");
                String currName = (String) mmbDesc.getFieldValue("name");
                if ((persistDir == null) || (persistName == null)) {
                    if (tracing()) {
                        trace("store()", "Persistence not supported for this MBean");
                    }
                    return;
                }

                // generate filename from mbean name
                String persistFileName = new String(persistDir + File.separator + persistName);

                // Open file
                FileOutputStream fos = new FileOutputStream(persistFileName, false);
                if (tracing()) {
                    trace("store()", "Writing MBean to file " + persistFileName);
                }
                writeMBeanInfoToFile(fos);
            } catch (Exception e) {
                if (tracing()) {
                    trace("store()", "Exception storing MBean into file for RequiredModelMBean " + e.getClass() + ":" + e.getMessage());
                }
                throw new MBeanException(e, "Exception storing MBean into file for SFModelMBean " + e.getClass() + ":" + e.getMessage());
                //e.printStackTrace();
            }
        }
        sendNotification(new Notification(NOTIFICATION_TYPE_STORE, this.myName, notifNumber++, ((new Date()).getTime()), "ModelMBean stored successfully"));
        if (tracing()) {
            trace("store()", "Exit");
        }
    }


    /**
     *  Create the MBeanInfo from a serialized copy of this object.
     *
     *@param  fis                      Description of the Parameter
     *@exception  java.io.IOException  Description of the Exception
     *@exception  MBeanException       Description of the Exception
     */
    private void createMBeanInfoFromFile(FileInputStream fis) throws java.io.IOException, MBeanException {
        try {
            // Create the object input stream
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Read the serialized object
            ModelMBeanInfo mmbInfo = (ModelMBeanInfo) ois.readObject();

            // Close
            ois.close();

            // Run initialization with the new MBeanInfo object
            setModelMBeanInfo(mmbInfo);
        } catch (ClassNotFoundException cnfe) {
            // Throw MBeanException
            throw new MBeanException(cnfe, "Could not load serialized ModelMBeanInfo.");
        }
    }


    /*
     *  Write the ModelMBeanInfo object out to a file as a serialized object.
     */
    /**
     *  Description of the Method
     *
     *@param  fos                      Description of the Parameter
     *@exception  java.io.IOException  Description of the Exception
     *@exception  MBeanException       Description of the Exception
     */
    private void writeMBeanInfoToFile(FileOutputStream fos) throws java.io.IOException, MBeanException {
        try {
            // Create the object output stream
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Write the serialized object
            oos.writeObject((ModelMBeanInfo) super.getMBeanInfo());

            // Flush and close output stream
            oos.flush();
            oos.close();
        } catch (java.io.NotSerializableException nse) {
            // Throw MBeanException
            throw new MBeanException(nse, "Could not store ModelMBeanInfo because it contained an object that was not serializable: " + nse.toString() + ".");
        }
    }


    /**
     *  analyzes descriptor fields to see if the MBean or Attribute should be
     *  persisted now returns true if 'persistPolicy' field is: != "never" =
     *  "always" = "onTimer" and now > 'lastPersistTime' + 'persistPeriod' =
     *  "NoMoreOftenThan" and now > 'lastPersistTime' + 'persistPeriod'
     *  otherwise it returns false when 'persistPolicy' field is: = "never" =
     *  "onUpdate" = "onTimer" && now < 'lastPersistTime' + 'persistPeriod'
     *
     *@param  mmbDesc  Description of the Parameter
     *@return          Description of the Return Value
     */
    private boolean persistItNow(Descriptor mmbDesc) {
        boolean persistItNowResponse = false;
        String MBeanPersistPolicy = null;

        if (tracing()) {
            trace("persistItNow(Descriptor)", "Entry");
        }
        if (mmbDesc == null) {
            if (tracing()) {
                trace("persistItNow(Descriptor)", "Input Descriptor null, no persist policy");
            }
            return persistItNowResponse;
        }

        MBeanPersistPolicy = (String) mmbDesc.getFieldValue("persistPolicy");

        if ((MBeanPersistPolicy == null) || (MBeanPersistPolicy.equalsIgnoreCase("never"))) {
            /*
             *  don't persist
             */
            persistItNowResponse = false;
        } else if (MBeanPersistPolicy.equalsIgnoreCase("onUpdate") || MBeanPersistPolicy.equalsIgnoreCase("always")) {
            persistItNowResponse = true;
        } else if (MBeanPersistPolicy.equalsIgnoreCase("onTimer") || MBeanPersistPolicy.equalsIgnoreCase("noMoreOftenThan")) {
            String MBeanExpTime = (String) mmbDesc.getFieldValue("persistPeriod");
            if ((MBeanExpTime == null) || (MBeanExpTime.length() == 0)) {
                MBeanExpTime = "0";
            }
            int MBeanPersistPeriod = (new Integer(MBeanExpTime)).intValue();
            String MBeanTStamp = (String) mmbDesc.getFieldValue("lastPersistedTimeStamp");
            int MBeanLastPersisted = 0;
            if (MBeanTStamp != null) {
                MBeanLastPersisted = (new Integer(MBeanTStamp)).intValue();
            }
            if ((new Date()).getTime() >= (MBeanLastPersisted + MBeanPersistPeriod)) {
                if (MBeanPersistPolicy.equalsIgnoreCase("onTimer")) {
                    persistItNowResponse = true;
                    /*
                     *  store will update the lastPersistedTimeStamp
                     *  of all attributeDescriptors and will reset the timer
                     *  mbean if there is one.  if there is not timer mbean
                     *  functionality, then onTimer will only be checked when
                     *  something is updated.  The onTimer logic in store() may
                     *  chose not to persist it just yet
                     */
                } else {
                    persistItNowResponse = true;
                }
                /*
                 *  its noMoreOftenThan
                 */
            }
        }
        if (tracing()) {
            trace("persistItNow(Descriptor)", "Exit with " + persistItNowResponse);
        }
        return persistItNowResponse;
    }


    /*
     *  The resolveForCacheValue method checks the descriptor passed in to see if there is a valid
     *  cached value in the descriptor.
     *  The valid value will be in the 'value' field if there is one.
     *  If the 'currencyTimeLimit' field in the descriptor is:
     *  <0 Then the value is not cached and is never valid.  Null is returned.
     *  The 'value' and 'lastUpdatedTimeStamp' fields are cleared.
     *  =0 Then the value is always cached and always valid.  The 'value' field is returned.
     *  The 'lastUpdatedTimeStamp' field is not checked.
     *  >0 Represents the number of seconds that the 'value' field is valid.
     *  The 'value' field is no longer valid when 'lastUpdatedTimeStamp' + 'currencyTimeLimit' > Now.
     *  When 'value' is valid, 'valid' is returned.
     *  When 'value' is no longer valid then null is returned and ;value' and 'lastUpdatedTimeStamp'
     *  fields are cleared.
     *
     */
    /**
     *  Description of the Method
     *
     *@param  descr                           Description of the Parameter
     *@return                                 Description of the Return Value
     *@exception  MBeanException              Description of the Exception
     *@exception  RuntimeOperationsException  Description of the Exception
     */
    private Object resolveForCacheValue(Descriptor descr) throws MBeanException, RuntimeOperationsException {
        if (tracing()) {
            trace("resolveForCacheValue(Descriptor)", "Entry");
        }

        ModelMBeanInfo modelMBeanInfo = (ModelMBeanInfo) super.getMBeanInfo();
        Object response = null;
        boolean resetValue = false;
        boolean returnCachedValue = true;
        long currencyPeriod = 0;

        if (descr == null) {
            if (tracing()) {
                trace("resolveForCacheValue(Descriptor)", "Input Descriptor is null");
            }
            return response;
        }
        if (tracing()) {
            trace("resolveForCacheValue(Descriptor)", "descriptor is " + descr.toString());
        }

        Descriptor mmbDescr = modelMBeanInfo.getMBeanDescriptor();
        if (mmbDescr == null) {
            if (tracing()) {
                trace("resolveForCacheValue(Descriptor)", "MBean Descriptor is null");
            }
            //return response;
        }

        String expTime = (String) descr.getFieldValue("currencyTimeLimit");
        if ((expTime == null) && (mmbDescr != null)) {
            expTime = (String) mmbDescr.getFieldValue("currencyTimeLimit");
        }
        if (expTime != null) {
            if (tracing()) {
                trace("SFModelMBean.resolveForCacheValue", " currencyTimeLimit: " + expTime);
            }
            // convert seconds to milliseconds for time comparison
            currencyPeriod = ((new Long(expTime)).longValue()) * 1000;
            /*
             *  if currencyTimeLimit is 0 then value is never current or cached
             */
            if (currencyPeriod < 0) {
                returnCachedValue = true;
                resetValue = false;
                if (tracing()) {
                    trace("SFModelMBean.resolveForCacheValue", currencyPeriod + ": always valid Cache");
                }
                /*
                 *  if currencyTimeLimit is -1 then value is always current
                 */
            } else if (currencyPeriod == 0) {
                returnCachedValue = false;
                resetValue = false;
                if (tracing()) {
                    trace("SFModelMBean.resolveForCacheValue", " never valid Cache");
                }
            } else {
                String tStamp = (String) descr.getFieldValue("lastUpdatedTimeStamp");
                if (tracing()) {
                    trace("SFModelMBean.resolveForCacheValue", " lastUpdatedTimeStamp: " + tStamp);
                }
                if (tStamp == null) {
                    tStamp = "0";
                }
                long lastTime = (new Long(tStamp)).longValue();
                if (tracing()) {
                    trace("SFModelMBean.resolveForCacheValue", " currencyPeriod:" + currencyPeriod + " lastUpdatedTimeStamp:" + lastTime);
                }

                long now = (new Date()).getTime();
                if (now < (lastTime + currencyPeriod)) {
                    returnCachedValue = true;
                    resetValue = false;
                    if (tracing()) {
                        trace("SFModelMBean.resolveForCacheValue", " timed valid Cache for " + now + " < " + (lastTime + currencyPeriod));
                    }
                } else {
                    /*
                     *  value is expired
                     */
                    returnCachedValue = false;
                    resetValue = true;
                    if (tracing()) {
                        trace("SFModelMBean.resolveForCacheValue", " timed expired cache for " + now + " > " + (lastTime + currencyPeriod));
                    }
                }
            }
            if (tracing()) {
                trace("SFModelMBean.resolveForCacheValue", "returnCachedValue:" + returnCachedValue + " resetValue: " + resetValue);
            }
            if (returnCachedValue == true) {
                Object currValue = descr.getFieldValue("value");
                if (currValue != null) {
                    /*
                     *  error/validity check return value here
                     */
                    response = currValue;
                    /*
                     *  need to cast string cached value to type
                     */
                    if (tracing()) {
                        trace("SFModelMBean.resolveForCacheValue", "valid Cache value: " + currValue);
                    }
                } else {
                    response = null;
                    if (tracing()) {
                        trace("SFModelMBean.resolveForCacheValue", " no Cached value");
                    }
                }
            }

            if (resetValue == true) {
                /*
                 *  value is not current, so remove it
                 */
                descr.removeField("lastUpdatedTimeStamp");
                descr.removeField("value");
                response = null;
                modelMBeanInfo.setDescriptor(descr, null);
                super.setModelMBeanInfo(modelMBeanInfo);
                if (tracing()) {
                    trace("SFModelMBean.resolveForCacheValue", "reset cached value to null");
                }
            }
        }
        if (tracing()) {
            trace("resolveForCache(Descriptor)", "Exit");
        }
        return response;
    }


    /**
     *@param  opName                   Description of the Parameter
     *@param  opArgs                   Description of the Parameter
     *@param  sig                      Description of the Parameter
     *@return                          Description of the Return Value
     *@exception  MBeanException       Description of the Exception
     *@exception  ReflectionException  Description of the Exception
     */
    /**
     *  Interface DynamicMBean *****
     *
     *@param  opName                   Description of the Parameter
     *@param  opArgs                   Description of the Parameter
     *@param  sig                      Description of the Parameter
     *@return                          Description of the Return Value
     *@exception  MBeanException       Description of the Exception
     *@exception  ReflectionException  Description of the Exception
     */
    /**
     *@param  opName                   Description of the Parameter
     *@param  opArgs                   Description of the Parameter
     *@param  sig                      Description of the Parameter
     *@return                          Description of the Return Value
     *@exception  MBeanException       Description of the Exception
     *@exception  ReflectionException  Description of the Exception
     */

    /**
     *  Invokes a method on or through a RequiredModelMBean and returns the
     *  result of the method execution. <PRE>
     * The last value returned by an operation may be cached in the operation's descriptor which
     * is in the ModelMBeanOperationInfo's descriptor.
     * The valid value will be in the 'value' field if there is one.
     * If the 'currencyTimeLimit' field in the descriptor is:
     *  null
     *  =0 Then the value is not cached and is never valid.  Null is returned.
     *      The 'value' and 'lastUpdatedTimeStamp' fields are cleared.
     *  <0 Then the value is always cached and always valid.  The 'value' field is returned.
     *      The 'lastUpdatedTimeStamp' field is not checked.
     *  >0 Represents the number of seconds that the 'value' field is valid.
     *      The 'value' field is no longer valid when 'lastUpdatedTimeStamp' + 'currencyTimeLimit' > Now.
     *      When 'value' is valid, 'valid' is returned.
     *      When 'value' is no longer valid then null is returned and ;value' and 'lastUpdatedTimeStamp'
     *      fields are cleared.
     *
     * Note: For this implementation: if the cached value is not a String, then the object must have
     * a constructor which accepts a string in the same format as the objects toString() method creates.
     * </pre>
     *
     *@param  opName                   Description of the Parameter
     *@param  opArgs                   Description of the Parameter
     *@param  sig                      Description of the Parameter
     *@return                          The object returned by the method, which
     *      represents the result of invoking the method on the specified
     *      managed resource.
     *@exception  MBeanException       Wraps an exception thrown by the MBean's
     *      invoked method.
     *@exception  ReflectionException  Wraps an java.lang.Exception thrown while
     *      trying to invoke the method.
     */

    public Object invoke(String opName, Object[] opArgs, String[] sig) throws MBeanException, ReflectionException {
        if (tracing()) {
            trace("invoke(String, Object[], String[])", "Entry");
        }

        if (opName == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Method name must not be null"),
                    "An exception occured while trying to invoke a method on a SFModelMBean");
        }

        ModelMBeanInfo modelMBeanInfo = (ModelMBeanInfo) super.getMBeanInfo();
        ModelMBeanOperationInfo opInfo = null;
        Descriptor opDescr = null;

        Object response = null;
        Method mmbOpHandle = null;
        Class[] mmbSig = null;
        Object[] mmbArgs = opArgs;
        boolean[] mmbClassFlag = null;
        String opClassName = "";
        String opMethodName = opName;

        // Parse for class name and method
        int opSplitter = opName.lastIndexOf(".");
        if (opSplitter > 0) {
            opClassName = opName.substring(0, opSplitter);
            opMethodName = opName.substring(opSplitter + 1);
        }
        opSplitter = opMethodName.indexOf("(");
        if (opSplitter > 0) {
            opMethodName = opMethodName.substring(0, opSplitter);
        }
        if (tracing()) {
            trace("invoke(String, Object[], String[])", "Finding operation " + opName + " as " + opMethodName);
        }
        // Create signature for method for local object invoke
        boolean mmbLocalInvoke = true;

        if ((opArgs != null) && (opArgs.length != 0)) {
            // intercept ModelMBean ifc calls and delegate to local methods first
            mmbArgs = opArgs;
            mmbSig = new Class[opArgs.length];
            mmbClassFlag = new boolean[opArgs.length];
            for (int i = 0; i < opArgs.length; i++) {
                try {

                    if (tracing()) {
                        trace("invoke(String, Object[], String[])", "finding class for " + sig[i]);
                    }

                    // set primitives in signature
                    if ((sig[i]).equals(Boolean.TYPE.toString())) {
                        mmbSig[i] = Boolean.TYPE;
                    } else if ((sig[i]).equals(Integer.TYPE.toString())) {
                        mmbSig[i] = Integer.TYPE;
                    } else if ((sig[i]).equals(Character.TYPE.toString())) {
                        mmbSig[i] = Character.TYPE;
                    } else if ((sig[i]).equals(Float.TYPE.toString())) {
                        mmbSig[i] = Float.TYPE;
                    } else if ((sig[i]).equals(Long.TYPE.toString())) {
                        mmbSig[i] = Long.TYPE;
                    } else if ((sig[i]).equals(Double.TYPE.toString())) {
                        mmbSig[i] = Double.TYPE;
                    } else if ((sig[i]).equals(Byte.TYPE.toString())) {
                        mmbSig[i] = Byte.TYPE;
                    } else if ((sig[i]).equals(Short.TYPE.toString())) {
                        mmbSig[i] = Short.TYPE;
                    } else {
                        // use default class loader
                        mmbClassFlag[i] = true;
                        mmbSig[i] = Class.forName(sig[i]);
                    }

                    if (tracing()) {
                        String mmbArgOut;
                        if (mmbArgs[i] == null) {
                            mmbArgOut = "null";
                        } else {
                            mmbArgOut = mmbArgs[i].toString();
                        }
                        trace("invoke(String, Object[], String[])", "invoke method found a valid argument: " + mmbSig[i].toString() + " is " + mmbArgOut);
                    }
                } catch (ClassNotFoundException cnfl) {
                    if (tracing()) {
                        trace("invoke(String, Object[], String[])", opName + " method " + opMethodName + "is not in RequiredModelMBean class:" + cnfl.getMessage());
                        trace("invoke(String, Object[], String[])", "The parameter class could not be found in default class loader");
                        // we'll try this again with the class loader of the target object
                        mmbClassFlag[i] = true;
                        mmbSig[i] = null;
                        mmbLocalInvoke = false;
                    }
                    // end if
                }
                // end catch
            }
            // end for

        } else {
            // set empty args and signature
            mmbSig = new Class[0];
            mmbArgs = new Object[0];
        }

        // Get method handle
        if (mmbLocalInvoke) {
            try {
                mmbOpHandle = (this.getClass()).getMethod(opMethodName, mmbSig);
            } catch (NoSuchMethodException nsml) {
                mmbLocalInvoke = false;
            }
        }

        // invoke method within the RequiredModelMBean object
        if ((mmbOpHandle != null) || mmbLocalInvoke) {
            try {
                // do local invoke
                if (tracing()) {
                    trace("invoke(String, Object[], String[])", opName + " being invoked on ModelMBean");
                }
                response = mmbOpHandle.invoke(this, mmbArgs);
                if (tracing()) {
                    if (response == null) {
                        trace("invoke(String, Object[], String[])", "invoke returning null response from local invoke of " + opName);
                    } else {
                        trace("invoke(String, Object[], String[])", "invoke returning non-null response from local invoke of " + opName);
                    }
                }
                return response;
            } catch (RuntimeErrorException ree) {
                throw new RuntimeOperationsException(ree, "RuntimeException occured in SFModelMBean while trying to invoke operation " + opName);
            } catch (RuntimeException re) {
                throw new RuntimeOperationsException(re, "RuntimeException occured in SFModelMBean while trying to invoke operation " + opName);
            } catch (IllegalAccessException iae) {
                throw new ReflectionException(iae, "IllegalAccessException occured in SFModelMBean while trying to invoke operation " + opName);
            } catch (InvocationTargetException ite) {
                Throwable mmbTargEx = ite.getTargetException();
                if (mmbTargEx instanceof RuntimeException) {
                    throw new MBeanException((RuntimeException) mmbTargEx, "RuntimeException thrown in SFModelMBean while trying to invoke operation " + opName);
                } else if (mmbTargEx instanceof ReflectionException) {
                    throw (ReflectionException) mmbTargEx;
                } else {
                    throw new MBeanException((Exception) mmbTargEx, "Exception thrown in SFModelMBean while trying to invoke operation " + opName);
                }
            } catch (Error err) {
                throw new RuntimeErrorException((Error) err, "Error occured in SFModelMBean while trying to invoke operation " + opName);
            } catch (Exception e) {
                throw new ReflectionException(e, "Exception occured in SFModelMBean while trying to invoke operation " + opName);
            }
        }

        // do invoke on another object from descriptors and mbeaninfo data
        if (tracing()) {
            trace("invoke(String, Object[], String[])", opName + " is not on SFModelMBean, looking in descriptor for " + opMethodName);
        }
        opInfo = modelMBeanInfo.getOperation(opName);
        // or should it be opMethodName?
        if (opInfo == null) {
            throw new MBeanException(new ServiceNotFoundException("operation " + opName + " execution not supported from descriptor data"),
                    "An exception occured in SFModelMBean while trying to invoke an operation");
        }
        opDescr = opInfo.getDescriptor();
        if (opDescr == null) {
            throw new MBeanException(new ServiceNotFoundException("operation " + opName + " execution not supported from descriptor data"),
                    "An exception occured in SFModelMBean while trying to invoke an operation");
        } else {
            /*
             *  return current cached value
             */
            response = resolveForCacheValue(opDescr);

            if (response == null) {
                /*
                 *  no caching to run invoke
                 */
                if (tracing()) {
                    trace("invoke(String, Object[], String[])", "No cached value returned for operation");
                }

                // get target object
                Object currObj = (Object) opDescr.getFieldValue("targetObject");
                if (currObj != null) {
                    if (tracing()) {
                        trace("invoke(String, Object[], String[])", "Found target object in descriptor");
                    }
                    String currType = (String) opDescr.getFieldValue("targetObjectType");
                    if ((currType == null) ||
                            !(currType.equalsIgnoreCase("objectReference") || currType.equalsIgnoreCase("rmiReference"))) {
                        throw new MBeanException(new InvalidTargetObjectTypeException(currType),
                                "An exception occured while trying to invoke an operation on a descpriptor provided target");
                        //throw ModelMBean.targetObjectTypeNotSupported;
                    }
                    if (tracing()) {
                        trace("invoke(String, Object[], String[]", "target object is a valid type");
                    }
                } else {
                    if (managedResource != null) {
                        if (tracing()) {
                            trace("invoke(String, Object[], String[])", "managedResource for invoke found");
                        }
                        currObj = managedResource;
                    } else {
                        if (tracing()) {
                            trace("invoke(String, Object[], String[])", "err managedResource for invoke is null");
                        }
                    }
                }

                // recreate sig with class loader of target object

                /*
                 *  Have to use MethodDescriptor of mbeaninfo and strip off last token
                 */
                /*
                 *  note: this means that the opName is the fully qualified class.method name
                 */
                /*
                 *  or it has to have a "class" field in descriptor
                 */
                /*
                 *  or the class of the target object will be used
                 */
                Class opClass = null;
                try {
                    if (tracing()) {
                        trace("invoke(String, Object[], String[])", "getting class for operation");
                    }
                    // get target object class name
                    if ((opClassName == null) || opClassName.equals("")) {
                        opClassName = (String) opDescr.getFieldValue("class");
                        if ((opClassName == null) || (opClassName.equals(""))) {
                            opClass = currObj.getClass();
                        } else {
                            opClass = Class.forName(opClassName);
                        }
                    } else {
                        opClass = Class.forName(opClassName);
                    }
                    if (opClass == null) {
                        throw new MBeanException(new Exception("A valid class could not be found for " + opMethodName),
                                "Exception occured while trying to find class for method of invoke");
                    }
                } catch (ClassNotFoundException cnf) {
                    throw new ReflectionException(cnf, "The target object class " + opClassName + " could not be found");
                }

                ClassLoader mmbLoader = null;
                try {
                    if (tracing()) {
                        trace("invoke(String, Object[], String[])", "setting signature with correct class loaders for operation");
                    }
                    // get signature with correct objects class loader
                    mmbLoader = (currObj.getClass()).getClassLoader();

                    if (mmbLoader != null) {
                        for (int i = 0; i < mmbSig.length; i++) {
                            if (mmbClassFlag[i]) {
                                mmbSig[i] = mmbLoader.loadClass(sig[i]);
                            }
                        }
                    }
                } catch (ClassNotFoundException cnf) {
                    throw new ReflectionException(cnf, "The parameter class could not be found");
                }

                // get method handle
                if (tracing()) {
                    trace("invoke(String, Object[], String[])", "Looking for operations class " + opClassName + " method " + opMethodName);
                }

                Method opHandle;
                try {
                    opHandle = opClass.getMethod(opMethodName, mmbSig);
                    if (opHandle == null) {
                        throw new ReflectionException(new Exception("null method handle"), "Retrieved null method handle for method");
                    }
                    if (tracing()) {
                        trace("invoke(String, Object[], String[])", "Retrieved valid method handle for " + opClassName + "." + opMethodName);
                    }
                } catch (NoSuchMethodException nsm) {
                    throw new ReflectionException(nsm, "The method " + opClassName + "." + opMethodName + " could not be found");
                }

                // invoke method on target object
                try {
                    response = opHandle.invoke((Object) currObj, mmbArgs);
                    if (tracing()) {
                        trace("invoke(String, Object[], String[])", "invoke done for " + opName);
                    }

                    if ((response != null) && (opDescr != null)) {
                        //if (tracing()) trace("response:" + response + " opDescr:" + opDescr.toString());
                        /*
                         *  check for validity of response class
                         */
                        /*
                         *  update cached value
                         */
                        Descriptor mmbDesc = modelMBeanInfo.getMBeanDescriptor();

                        String ctl = (String) opDescr.getFieldValue("currencyTimeLimit");
                        if ((ctl == null) && (mmbDesc != null)) {
                            ctl = (String) mmbDesc.getFieldValue("currencyTimeLimit");
                        }
                        if ((ctl != null) && !(ctl.equals("0"))) {
                            opDescr.setField("value", response);
                            opDescr.setField("lastUpdatedTimeStamp", (new Long((new Date()).getTime())).toString());
                            opInfo.setDescriptor(opDescr);

                            modelMBeanInfo.setDescriptor(opDescr, "operation");
                            if (tracing()) {
                                trace("invoke(String,Object[],Object[])", "new descriptor is " + opDescr.toString());

                                trace("invoke()", "OperationInfo descriptor is " + opInfo.getDescriptor().toString());
                                trace("invoke()", "OperationInfo descriptor is " + modelMBeanInfo.getDescriptor(opName, "operation").toString());
                            }
                        }
                        if (tracing()) {
                            trace("invoke(String, Object[], String[])", "invoke retrieved " + response.toString());
                        }
                    }
                } catch (RuntimeErrorException ree) {
                    throw new RuntimeOperationsException(ree, "RuntimeException occured in managed resource while trying to invoke operation " + opName);
                } catch (RuntimeException re) {
                    throw new RuntimeOperationsException(re, "RuntimeException occured in managed resource while trying to invoke operation " + opName);
                } catch (IllegalAccessException iae) {
                    throw new ReflectionException(iae, "IllegalAccessException occured in managed resource while trying to invoke operation " + opName);
                } catch (Error err) {
                    throw new RuntimeErrorException((Error) err, "Error occured in managed resource while trying to invoke operation " + opName);
                } catch (InvocationTargetException ite) {
                    Throwable mmbTargEx = ite.getTargetException();
                    if (mmbTargEx instanceof RuntimeException) {
                        throw new RuntimeMBeanException((RuntimeException) mmbTargEx, "RuntimeException thrown in managed resource while trying to invoke operation " + opName);
                    } else if (mmbTargEx instanceof Error) {
                        throw new RuntimeErrorException((Error) mmbTargEx, "Error thrown in managed resource while trying to invoke operation " + opName);
                    } else if (mmbTargEx instanceof ReflectionException) {
                        throw (ReflectionException) mmbTargEx;
                    } else {
                        throw new MBeanException((Exception) mmbTargEx, "Exception thrown in managed resource while trying to invoke operation " + opName);
                    }
                } catch (Exception e) {
                    throw new ReflectionException(e, "Exception occured in managed resource while trying to invoke operation " + opName);
                }
            }
            // response ! cached
        }
        // opDescr

        if (tracing()) {
            trace("invoke(String, Object[], String[])", "Exit");
        }
        return response;
    }


    /**
     *  Returns the value of a specific attribute defined for this ModelMBean.
     *  <PRE>
     * The last value returned by an attribute may be cached in the attribute's descriptor.
     * The valid value will be in the 'value' field if there is one.
     * If the 'currencyTimeLimit' field in the descriptor is:
     *   null
     *   <0 Then the value is not cached and is never valid.  Null is returned.
     *       The 'value' and 'lastUpdatedTimeStamp' fields are cleared.
     *   =0 Then the value is always cached and always valid.  The 'value' field is returned.
     *       The 'lastUpdatedTimeStamp' field is not checked.
     *   >0 Represents the number of seconds that the 'value' field is valid.
     *       The 'value' field is no longer valid when 'lastUpdatedTimeStamp' + 'currencyTimeLimit' > Now.
     *       When 'value' is valid, 'valid' is returned.
     *       When 'value' is no longer valid then null is returned and ;value' and 'lastUpdatedTimeStamp'
     *       fields are cleared.
     *
     *
     * </PRE> If there is no valid cached value then the 'getMethod' field in
     *  the attributes descriptor is analyzed. If 'getMethod' contains the name
     *  of a valid operation descriptor, then the method described by the
     *  operation descriptor is executed. The response from the method is
     *  returned as the value of the attribute. If the operation fails or the
     *  response value is not of the same type as the attribute, an exception
     *  will be thrown. If currencyTimeLimit is > 0, then the value of the
     *  attribute is cached in the attribute descriptor's 'value' field and the
     *  'lastUpdatedTimeStamp' field is set to the current time stamp. A new
     *  Attribute Descriptor field called "isGetterParameter" and not considered
     *  by the RequiredModelMBean is checked. This field must contain a string
     *  with the values "True"/"False". If the value is "True" the name of the
     *  attribute is included as a parameter when invoking the getter/setter
     *  method. In this way, we can reuse just one method for all the attributes
     *  without having to define a getter/setter method for every attribute we
     *  want to manage, since we do not know a priori.
     *
     *@param  attrName                        Description of the Parameter
     *@return                                 The value of the retrieved
     *      attribute from the descriptor 'value' field or from the invokation
     *      of the operation in the 'getMethod' field of the descriptor.
     *@exception  AttributeNotFoundException  The specified attribute is not
     *      accessible in the MBean.
     *@exception  MBeanException              Wraps an exception thrown by the
     *      MBean's getter.
     *@exception  ReflectionException         Wraps an java.lang.Exception
     *      thrown while trying to invoke the setter.
     */

    public Object getAttribute(String attrName) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (attrName == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("attributeName must not be null"),
                    "Exception occured trying to get attribute of a RequiredModelMBean");
        }
        if (tracing()) {
            trace("getAttribute(String)", "Entry with" + attrName);
        }
        /*
         *  Check attributeDescriptor for getMethod
         */
        ModelMBeanInfo modelMBeanInfo = (ModelMBeanInfo) super.getMBeanInfo();
        ModelMBeanAttributeInfo attrInfo = null;
        Descriptor attrDescr = null;
        Object response = null;
        try {
            if (modelMBeanInfo == null) {
                throw new AttributeNotFoundException("getAttribute failed: ModelMBeanAttributeInfo not found for " + attrName);
            }
            attrInfo = modelMBeanInfo.getAttribute(attrName);
            Descriptor mmbDesc = modelMBeanInfo.getMBeanDescriptor();
            if (attrInfo == null) {
                throw new AttributeNotFoundException("getAttribute failed: ModelMBeanAttributeInfo not found for " + attrName);
            }
            attrDescr = attrInfo.getDescriptor();
            if (attrDescr != null) {
                response = resolveForCacheValue(attrDescr);
                /*
                 *  return current cached value
                 */
                if (tracing()) {
                    trace("getAttribute(String)", "*** cached value is " + response);
                }
                if (response == null) {
                    /*
                     *  no cached value, run getMethod
                     */
                    if (tracing()) {
                        trace("getAttribute(String)", "**** cached value is null - getting getMethod");
                    }
                    String attrGetMethod = (String) (attrDescr.getFieldValue("getMethod"));
                    if (attrGetMethod != null) {
                        /*
                         *  run method from operations descriptor
                         */
                        // We check if the attribute name must be included as a parameter
                        boolean isGetterParameter = new Boolean((String) attrDescr.getFieldValue("isGetterParameter")).booleanValue();
                        Object[] args = new Object[]{};
                        String[] sigs = new String[]{};
                        if (isGetterParameter) {
                            args = new Object[]{attrName};
                            sigs = new String[]{"java.lang.String"};
                        }
                        if (tracing()) {
                            trace("getAttribute(String)", "invoking a getMethod for " + attrName);
                        }
                        Object getResponse = invoke(attrGetMethod, args, sigs);
                        //System.out.println(attrGetMethod+"("+args[0]+") "+getResponse);
                        if (getResponse != null) {
                            /*
                             *  error/validity check return value here
                             */
                            if (tracing()) {
                                trace("getAttribute(String)", "got a non-null response from getMethod\n");
                            }
                            response = getResponse;
                            /*
                             *  change cached value in attribute descriptor
                             */
                            String ctl = (String) attrDescr.getFieldValue("currencyTimeLimit");
                            if ((ctl == null) && (mmbDesc != null)) {
                                ctl = (String) mmbDesc.getFieldValue("currencyTimeLimit");
                            }
                            if ((ctl != null) && !(ctl.equals("0"))) {
                                if (tracing()) {
                                    trace("getAttribute(String)", "setting cached value and lastUpdatedTime in descriptor");
                                }
                                attrDescr.setField("value", response);
                                attrDescr.setField("lastUpdatedTimeStamp", (new Long((new Date()).getTime())).toString());
                                attrInfo.setDescriptor(attrDescr);
                                modelMBeanInfo.setDescriptor(attrDescr, "attribute");
                                super.setModelMBeanInfo(modelMBeanInfo);
                                if (tracing()) {
                                    trace("getAttribute(String)", "new descriptor is " + attrDescr.toString());
                                    trace("setAttribute()", "local: AttributeInfo descriptor is " + attrInfo.getDescriptor().toString());
                                    trace("setAttribute()", "modelMBeanInfo: AttributeInfo descriptor is " + modelMBeanInfo.getDescriptor(attrName, "attribute").toString());
                                }
                                // end if
                            }
                            // end if
                        }
                        // end if
                        else {
                            /*
                             *  response was invalid or really returned null
                             */
                            if (tracing()) {
                                trace("getAttribute(String)", "got a null response from getMethod\n");
                            }
                            response = null;
                        }
                        // end else
                    }
                    // end if
                    else {
                        /*
                         *  not getMethod so return default value
                         */
                        if (tracing()) {
                            trace("getAttribute(String)", "could not find getMethod for " + attrName + ", returning default value");
                        }
                        response = attrDescr.getFieldValue("default");
                        // !! cast response to right class
                    }
                    // end else
                }
                /*
                 *  make sure response class matches type field
                 */
                String respType = attrInfo.getType();
                if (response != null) {
                    String responseClass = response.getClass().getName();
                    //if (!respType.equals(responseClass)) { // Original from Manuel
                    if (!(Class.forName(respType).isInstance(response))){
                        if (tracing()) {
                            trace("getAttribute(String)", "wrong response type");
                        }
                        System.out.println("  Debug[SFModelMBean.getAttribute(String)] Attrib type: "+respType);
                        System.out.println("  Debug[SFModelMBean.getAttribute(String)] Respon type: "+responseClass);
                        /*
                         *  throw exception, didn't get back right attribute type
                         */
                        throw new MBeanException(new InvalidAttributeValueException("Wrong value type recieved for get attribute"),
                                "An exception occured while trying to get an attribute value through a RequiredModelMBean");
                        //response = null;
                    }
                }
            } else {
                if (tracing()) {
                    trace("getAttribute(String)", "getMethod failed " + attrName + " not in attributeDescriptor\n");
                }
                throw new MBeanException(new InvalidAttributeValueException("Unable to resolve attribute value, no getMethod defined in descriptor for attribute"),
                        "An exception occured while trying to get an attribute value through a RequiredModelMBean");
            }
        } catch (MBeanException mbe) {
            throw mbe;
        } catch (Exception e) {
            e.printStackTrace();
            if (tracing()) {
                trace("getAttribute(String)", "getMethod failed with " + e.getMessage() + " exception type " + (e.getClass()).toString());
            }
            throw new MBeanException(e, "An exception occured while trying to get an attribute value: " + e.getMessage());
        }
        if (tracing()) {
            trace("getAttribute(String)", "Exit");
        }
        return response;
    }


    /**
     *  Returns the values of several attributes in the ModelMBean. Executes a
     *  getAttribute for each attribute name in the attrNames array passed in.
     *
     *@param  attrNames  Description of the Parameter
     *@return            The array of the retrieved attributes.
     */
    public AttributeList getAttributes(String[] attrNames) {
        if (tracing()) {
            trace("getAttributes(String[])", "Entry");
        }
        AttributeList responseList = null;
        if (attrNames == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames must not be null"),
                    "Exception occured trying to get attributes of a RequiredModelMBean");
        }
        responseList = new AttributeList();
        for (int i = 0; i < attrNames.length; i++) {
            try {
                Attribute attribute = new Attribute(attrNames[i], getAttribute(attrNames[i]));
                responseList.add(attribute);
            } catch (MBeanException e) {
                e.printStackTrace();
                responseList.add(new Attribute(attrNames[i], ""));
            } catch (Exception e) {
                e.printStackTrace();
                responseList.add(new Attribute(attrNames[i], ""));
                // eat exceptions because interface doesn't have an exception on it
                //throw new MBeanException(e,"exception for getting attribute " + attrNames[i]);
            }
        }
        if (tracing()) {
            trace("getAttributes(String[])", "Exit");
        }
        return responseList;
    }


    /**
     *  Sets the value of a specific attribute of a named ModelMBean. If the
     *  'setMethod' field of the attribute's descriptor contains the name of a
     *  valid operation descriptor, then the method described by the operation
     *  descriptor is executed. The response from the method is set as the value
     *  of the attribute in the descriptor. If the operation fails or the
     *  response value is not of the same type as the attribute, an exception
     *  will be thrown. <PRE>
     * If currencyTimeLimit is > 0, then the new value for the attribute is cached in the attribute descriptor's
     * 'value' field and the 'lastUpdatedTimeStamp' field is set to the current time stamp.
     *
     *
     * If the persist field of the attribute's descriptor is not null then
     * Persistance policy from the attribute descriptor is used to guide storing the attribute in a
     * persistenant store.
     * Store the MBean if 'persistPolicy' field is:
     *   != "never"
     *   = "always"
     *   = "onUpdate"
     *   = "onTimer" and now > 'lastPersistTime' + 'persistPeriod'
     *   = "NoMoreOftenThan" and now > 'lastPersistTime' + 'persistPeriod'
     *
     * Do not store the MBean if 'persistPolicy' field is:
     *   = "never"
     *   = "onTimer" && now < 'lastPersistTime' + 'persistPeriod'
     *   = "NoMoreOftenThan" and now < 'lastPersistTime' + 'persistPeriod'
     * </PRE> A new Attribute Descriptor field called "isSetterParameter" and
     *  not considered by the RequiredModelMBean is checked. This field must
     *  contains a string with the values "True"/"False". If the value is "True"
     *  the name of the attribute is included as a parameter when invoking the
     *  getter/setter method. In this way, we can reuse just one method for all
     *  the attributes without having to define a getter/setter method for every
     *  attribute we want to manage, since we do not know a priori.
     *
     *@param  attribute                           The Attribute instance
     *      containing the name of the attribute to be set and the value it is
     *      to be set to.
     *@exception  AttributeNotFoundException      The specified attribute is not
     *      accessible in the MBean.
     *@exception  InvalidAttributeValueException  The specified value for the
     *      attribute is not valid.
     *@exception  MBeanException                  Wraps an exception thrown by
     *      the MBean's setter.
     *@exception  ReflectionException             Wraps an java.lang.Exception
     *      thrown while trying to invoke the setter.
     */
    public void setAttribute(Attribute attribute)
             throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        if (tracing()) {
            trace("setAttribute()", "Entry");
        }
        if (attribute == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("attribute must not be null"),
                    "Exception occured trying to set an attribute of a RequiredModelMBean");
        }

        String attrName = attribute.getName();
        Object attrValue = attribute.getValue();

        try {
            ModelMBeanInfo modelMBeanInfo = (ModelMBeanInfo) super.getMBeanInfo();
            ModelMBeanAttributeInfo attrInfo = modelMBeanInfo.getAttribute(attrName);
            if (attrInfo == null) {
                throw new AttributeNotFoundException("setAttribute failed: " + attrName + " is not found ");
            }
            Descriptor mmbDesc = modelMBeanInfo.getMBeanDescriptor();
            Descriptor attrDescr = attrInfo.getDescriptor();

            if (attrDescr != null) {
                if (!attrInfo.isWritable()) {
                    throw new AttributeNotFoundException("setAttribute failed: " + attrName + " is not writable ");
                } else {
                    Object setResponse = null;
                    String attrSetMethod = (String) (attrDescr.getFieldValue("setMethod"));
                    String attrType = (String) (attrInfo.getType());

                    Object currValue = new Object();
                    if (attrInfo.isReadable()) {
                        currValue = this.getAttribute(attrName);
                    }
                    Attribute oldAttr = new Attribute(attrName, currValue);

                    if (tracing()) {
                        trace("setAttribute()", "changing attribute" + attrName + " from " + currValue.toString() + " to " + attrValue);
                    }
                    if (attrSetMethod != null) {
                        /*
                         *  run method from operations descriptor
                         */
                        // JMX-RI uses attribute type as the signature of the method, which can mean a bug for our SF purposes
                        // since we have a getter/setter method for all the attributes instead of one for each attribute
                        Object[] args = new Object[]{attrValue};
                        String[] sigs = new String[]{attrType};

                        // We check if the attribute name must be included as a parameter
                        boolean isSetterParameter = new Boolean((String) attrDescr.getFieldValue("isSetterParameter")).booleanValue();
                        if (isSetterParameter) {
                            // We check if the attribute value is an instance of the type required by the setter method
                            MBeanParameterInfo[] parInfo = modelMBeanInfo.getOperation(attrSetMethod).getSignature();
                            try {
                                Class valueSig = Class.forName(parInfo[1].getType());
                                // System.out.println("Name: "+attrName+"; Value: "+attrValue+"; Class: "+parInfo[1].getType()+";");
                                if (valueSig.isInstance(attrValue)) {
                                    args = new Object[]{attrName, attrValue};
                                    sigs = new String[]{"java.lang.String", valueSig.getName()};
                                } else {
                                    throw new Exception("Type of value does not match method signature");
                                }
                            } catch (Exception e) {
                                throw new javax.management.ReflectionException(e);
                            }
                        }
                        setResponse = invoke(attrSetMethod, args, sigs);
                    }
                    /*
                     *  change cached value
                     */
                    String ctl = (String) attrDescr.getFieldValue("currencyTimeLimit");
                    if ((ctl == null) && (mmbDesc != null)) {
                        ctl = (String) mmbDesc.getFieldValue("currencyTimeLimit");
                    }
                    if ((ctl != null) && !(ctl.equals("0"))) {
                        if (tracing()) {
                            trace("setAttribute()", "setting cached value of " + attrName + " to " + attrValue);
                        }
                        attrDescr.setField("value", attrValue);
                        String currtime = (new Long((new Date()).getTime())).toString();
                        attrDescr.setField("lastUpdatedTimeStamp", currtime);
                        attrInfo.setDescriptor(attrDescr);

                        modelMBeanInfo.setDescriptor(attrDescr, "attribute");
                        if (tracing()) {
                            trace("setAttribute()", "new descriptor is " + attrDescr.toString());
                            trace("setAttribute()", "AttributeInfo descriptor is " + attrInfo.getDescriptor().toString());
                            trace("setAttribute()", "AttributeInfo descriptor is " + modelMBeanInfo.getDescriptor(attrName, "attribute").toString());
                        }
                    }
                    if (tracing()) {
                        trace("setAttribute()", "sending sendAttributeNotification");
                    }
                    sendAttributeChangeNotification(new AttributeChangeNotification(this.myName,
                            notifNumber++,
                            ((new Date()).getTime()),
                            "AttributeChangeDetected",
                            oldAttr.getName(),
                            (((attribute.getValue()).getClass()).getName().toString()),
                            oldAttr.getValue(),
                            attribute.getValue())
                            );
            //sendAttributeChangeNotification(oldAttr,attribute);
            // We also send the notification to the Remote Listeners. It could be sent here. But we'd
            // rather do it in the handleNotification() method.
//          if (tracing()) trace("setAttribute()","sending sendRemoteAttributeNotification");
//          sendRemoteAttributeChangeNotification(oldAttr,attribute);

                    /*
                     *  check to see if should be persisted
                     */
                    boolean persistMBeanNow = false;
                    String persistPolicy = (String) attrDescr.getFieldValue("persistPolicy");
                    if (persistPolicy != null) {
                        // if attribute policy
                        boolean persistAttributeNow = persistItNow(attrDescr);
                        if (persistAttributeNow == true) {
                            store();
                        }
                    }
                    // if attribute policy
                    else {
                        // else check global mbean policy
                        if (mmbDesc != null) {
                            persistMBeanNow = persistItNow(mmbDesc);
                            if (persistMBeanNow == true) {
                                store();
                            }
                        }
                    }
                    // else check global mbean policy
                }
                // else writeable
            }
            // if descriptor
            else {
                // else no descriptor
                if (tracing()) {
                    trace("setAttribute(String)", "setMethod failed " + attrName + " not in attributeDescriptor\n");
                }
                throw new MBeanException(new InvalidAttributeValueException("Unable to resolve attribute value, nosetMethod defined in descriptor for attribute"),
                        "An exception occured while trying to set an attribute value through a RequiredModelMBean");
            }
            // else no descriptor
        }
        // try
//      catch (RemoteException re) {
//        if (tracing()) trace("setAttribute(String)","setMethod failed with " + re.getMessage() + "\n");
//        throw new MBeanException(re,"An exception occured while trying to set an attribute value: " + re.getMessage());
//      }
        catch (InstanceNotFoundException e) {
            if (tracing()) {
                trace("setAttribute(String)", "setMethod failed with " + e.getMessage() + "\n");
            }
            throw new MBeanException(e, "An exception occured while trying to set an attribute value: " + e.getMessage());
        }

        if (tracing()) {
            trace("setAttribute(Attribute)", "Exit");
        }
    }


    /**
     *  Sets the values of an array of attributes of this ModelMBean. Executes
     *  the setAttribute() method for each attribute in the list. It is
     *  overwriten here because of bug in the JMX RI.
     *
     *@param  attributes  A list of attributes: The identification of the
     *      attributes to be set and the values they are to be set to.
     *@return             The array of attributes that were set, with their new
     *      values in Attribute instances.
     */
    public AttributeList setAttributes(AttributeList attributes) {
        if (tracing()) {
            trace("setAttributes(AttributeList)", "Entry");
        }
        AttributeList responseList = null;
        String[] attrNames = null;
        if (attributes == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("attributes must not be null"),
                    "Exception occured trying to set attributes of a RequiredModelMBean");
        }
        attrNames = new String[attributes.size()];
        int i = 0;
        for (ListIterator iptr = attributes.listIterator(); iptr.hasNext(); i++) {
            try {
                Attribute a = (Attribute) iptr.next();
                attrNames[i] = a.getName();
                setAttribute(a);
            } catch (Exception e) {
                break;
                // eat exceptions because none allowed on this interface
                // really should rethrow as MBeanException
            }
        }
        responseList = getAttributes(attrNames);
        // set returning Attribute List
        if (tracing()) {
            trace("setdAttributes(AttributeList)", "Exit");
        }
        return attributes;
    }


    /**
     *  Registers an object which implements the NotificationListener interface
     *  as a listener for AttributeChangeNotifications. This object's
     *  'handleNotification()' method will be invoked when any
     *  attributeChangeNotification is issued through or by the MBean. This does
     *  not include other Notifications. They must be registered for
     *  independently. An AttributeChangeNotification will be generated for this
     *  attributeName.
     *
     *@param  inlistener                      The feature to be added to the
     *      AttributeChangeNotificationListener attribute
     *@param  inAttributeName                 The feature to be added to the
     *      AttributeChangeNotificationListener attribute
     *@param  inhandback                      The feature to be added to the
     *      AttributeChangeNotificationListener attribute
     *@exception  IllegalArgumentException    Listener is null or attributeName
     *      is null.
     *@exception  MBeanException              Description of the Exception
     *@exception  RuntimeOperationsException  Description of the Exception
     */
    public void addAttributeChangeNotificationListener(NotificationListener inlistener,
            String inAttributeName,
            Object inhandback)
             throws MBeanException, RuntimeOperationsException, IllegalArgumentException {
        MBeanAttributeInfo[] attributes;
            //System.out.println(inlistener.toString() +" "+inAttributeName+" "+inhandback);
        if (inAttributeName == null) {
            attributes = getMBeanInfo().getAttributes();
            for (int i = 0; i < attributes.length; i++) {
                super.addAttributeChangeNotificationListener(inlistener, attributes[i].getName(), inhandback);
            }
        } else {
            super.addAttributeChangeNotificationListener(inlistener, inAttributeName, inhandback);
        }
    }


    /**
     *  Removes a listener for attributeChangeNotifications from the MBean.
     *
     *@param  inlistener                      Description of the Parameter
     *@param  inAttributeName                 Description of the Parameter
     *@exception  ListenerNotFoundException   The couple (listener,handback) is
     *      not registered in the MBean. The exception message contains either
     *      "listener", "handback" or the object name depending on which object
     *      cannot be found.
     *@exception  MBeanException              Description of the Exception
     *@exception  RuntimeOperationsException  Description of the Exception
     */
    public void removeAttributeChangeNotificationListener(NotificationListener inlistener, String inAttributeName)
             throws MBeanException, RuntimeOperationsException, ListenerNotFoundException {

        MBeanAttributeInfo[] attributes;
        if (inAttributeName == null) {
            attributes = getMBeanInfo().getAttributes();
            for (int i = 0; i < attributes.length; i++) {
                super.removeAttributeChangeNotificationListener(inlistener, attributes[i].getName());
            }
        } else {
            super.removeAttributeChangeNotificationListener(inlistener, inAttributeName);
        }
    }


/***** INTERFACE NotificationListener *****/

    /**
     *  Forwards Notifications and AttributeChangeNotifications to remote
     *  listeners by using the sendRemoteAttributeChangeNotification listener
     *  since the AttributeChangeNotificationBroadcaster interface is not
     *  supported by the ConnectorServers.
     *
     *@param  notification  Description of the Parameter
     *@param  handback      Description of the Parameter
     */

    public void handleNotification(Notification notification, Object handback) {
        try {
            if ("managedResource".equals(handback)) { // It comes from managedResource
                sendNotification(notification);
            }
        } catch (Exception e) {
            if (tracing()) {
                trace("handleNotification(Notification, Object)", "handleNotification failed with " + e.getMessage() + "\n");
            }
            e.printStackTrace();
        }
    }

    // SUN Trace and debug functions
    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    private boolean tracing() {
     //   return Trace.isSelected(Trace.LEVEL_TRACE, Trace.INFO_MODELMBEAN);
     return false;
    }


    /**
     *  Description of the Method
     *
     *@param  inClass   Description of the Parameter
     *@param  inMethod  Description of the Parameter
     *@param  inText    Description of the Parameter
     */
    private void trace(String inClass, String inMethod, String inText) {
        System.out.println("TRACE: " + inClass + ":" + inMethod + ": " + inText);
      //  Trace.send(Trace.LEVEL_TRACE, Trace.INFO_MODELMBEAN, inClass, inMethod, Integer.toHexString(this.hashCode()) + " " + inText);
    }


    /**
     *  Description of the Method
     *
     *@param  inMethod  Description of the Parameter
     *@param  inText    Description of the Parameter
     */
    private void trace(String inMethod, String inText) {
        //System.out.println("TRACE: " + currClass + ":" + inMethod + ": " + inText);
        trace(currClass, inMethod, inText);
    }


    /**
     *  Allows the MBean to perform any operations it needs before being
     *  registered in the MBean server. If the name of the MBean is not
     *  specified, the MBean can provide a name for its registration. If any
     *  exception is raised, the MBean will not be registered in the MBean
     *  server.
     *
     *@param  server                   The MBean server in which the MBean will
     *      be registered.
     *@param  name                     The object name of the MBean.
     *@return                          The name of the MBean registered.
     *@exception  java.lang.Exception  This exception should be caught by the
     *      MBean server and re-thrown as an <CODE>MBeanRegistrationException</CODE>
     *      .
     */
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws java.lang.Exception {
        myName = name;
        myServer = server;
        ObjectName mBeanServerDelegateName = new ObjectName("JMImplementation:type=MBeanServerDelegate");
        //myServer.addNotificationListener(mBeanServerDelegateName, this, this, null);
        return name;
    }


    /**
     *  Allows the MBean to perform any operations needed after having been
     *  registered in the MBean server or after the registration has failed.
     *
     *@param  registrationDone  Indicates whether or not the MBean has been
     *      successfully registered in the MBean server. The value false means
     *      that the registration phase has failed.
     */
    public void postRegister(Boolean registrationDone) { }


    /**
     *  Allows the MBean to perform any operations it needs before being
     *  de-registered by the MBean server.
     *
     *@exception  java.lang.Exception  Description of the Exception
     */
    public void preDeregister() throws java.lang.Exception { }


    /**
     *  Allows the MBean to perform any operations needed after having been
     *  de-registered in the MBean server.
     */
    public void postDeregister() { }


}
