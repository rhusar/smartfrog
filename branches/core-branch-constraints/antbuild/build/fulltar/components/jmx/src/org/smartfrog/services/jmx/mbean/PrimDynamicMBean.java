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

package org.smartfrog.services.jmx.mbean;

import java.util.Vector;
import java.util.Enumeration;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import javax.management.DynamicMBean;
import javax.management.AttributeList;
import javax.management.MBeanInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.Attribute;
import javax.management.NotificationListener;
import javax.management.NotificationFilter;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ListenerNotFoundException;
import javax.management.AttributeNotFoundException;
import javax.management.AttributeChangeNotification;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.RuntimeErrorException;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.services.jmx.common.Utilities;

/**
 * A dynamic mbean that is bound to a prim
 *
 * @version 1.0
 */

public class PrimDynamicMBean implements DynamicMBean, NotificationBroadcaster {

  public static String CLASS = "org.smartfrog.services.jmx.mbean.PrimDynamicMBean";

  protected String description = "Prim component exposed for management";

  protected MBeanConstructorInfo[] constructors = null;

  protected MBeanOperationInfo[] operations = null;

  protected MBeanNotificationInfo[] notifications = null;

  protected Prim target = null;

  protected NotificationBroadcasterSupport nbs = new NotificationBroadcasterSupport();

  protected long sequence = 0;

  public PrimDynamicMBean() throws Exception {
      // Build ConstructorInfo
      Constructor[] cons = getClass().getConstructors();
      constructors = new MBeanConstructorInfo[cons.length];
      for (int i = 0; i < cons.length; i++) {
          constructors[i] = new MBeanConstructorInfo( "Constructs a DyanamicMBean from a Prim component", cons[i]);
      };

      // Build NotificationInfo
      notifications = new MBeanNotificationInfo[] {
          new MBeanNotificationInfo(new String[]{AttributeChangeNotification.ATTRIBUTE_CHANGE},
                                    AttributeChangeNotification.ATTRIBUTE_CHANGE,
                                    "SmartFrog attribute has been modified")
      };
  }

  public PrimDynamicMBean(Prim prim) throws Exception {
      this();
      setPrim(prim);
  }

  public void setPrim(Prim prim) throws Exception {
      target = prim;
      // Build OperationInfo
      Class primClass = prim.getClass();
      Method[] meth = primClass.getMethods();
      operations = new MBeanOperationInfo[meth.length+1];
      int i;
      for (i = 0; i < meth.length; i++) {
          operations[i] = new MBeanOperationInfo( "Prim method exposed for management", meth[i]);
      }
      operations[i] = new MBeanOperationInfo( "Sets a new Prim component to be exposed by this DynamicMBean",
                                              getClass().getMethod("setPrim", new Class[]{Prim.class}));
  }

  public MBeanAttributeInfo[] getMBeanAttributeInfo() {
      if (target == null) return null;
      Vector attrInfo = new Vector();
      Context context = null;
      try {
          context = target.sfContext();
      }
      catch (Exception e) { return null; }
      String description = "SmartFrog attribute exposed for management";
      for (Enumeration e = context.keys(); e.hasMoreElements(); ) {
          String name = (String) e.nextElement();
          Object value = null;
          String type = null;
          try {
              value = getAttribute(name);
              if (!Utilities.isAttribute(value)) continue;
              if (value == null) type = Object.class.getName();
              else type = value.getClass().getName();
          }
          catch (Exception ex) {
              ex.printStackTrace();
              continue;
          }
          MBeanAttributeInfo attr = new MBeanAttributeInfo(name, type, description, true, true, false);
          attrInfo.addElement(attr);
      }
      MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[attrInfo.size()];
      attrInfo.toArray(attributes);
      return attributes;
  }

  public Object getAttribute(String attribute) throws javax.management.AttributeNotFoundException, javax.management.MBeanException, javax.management.ReflectionException {
      try {
          return target.sfResolve(attribute);
      }
      catch (Exception e) {
          throw new AttributeNotFoundException(e.getMessage());
      }
  }

  public AttributeList getAttributes(String[] params) {
      AttributeList attributes = new AttributeList();
      for (int i=0; i<params.length; i++) {
          try {
              Attribute attribute = new Attribute(params[i], getAttribute(params[i]));
          }
          catch (Exception e) { }
      }
      return attributes;
  }

  public MBeanInfo getMBeanInfo() {
      MBeanAttributeInfo[] attributes = getMBeanAttributeInfo();
      return new MBeanInfo(CLASS, description, attributes, constructors, operations, notifications);
  }

  public Object invoke(String opName, Object[] opArgs, String[] sig) throws javax.management.MBeanException, javax.management.ReflectionException {
        if (opName == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Method name cannot not be null"),
                    "An exception occured while trying to invoke a method on a PrimDynamicMBean");
        }

        Method opHandle = null;
        Class[] signature = null;
        Object[] arguments = opArgs;
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

        if ((opArgs != null) && (opArgs.length != 0)) {
            // intercept ModelMBean ifc calls and delegate to local methods first
            arguments = opArgs;
            signature = new Class[opArgs.length];
            for (int i = 0; i < opArgs.length; i++) {
                try {
                    signature[i] = Utilities.classFromString(sig[i]);
                } catch (ClassNotFoundException cnfe) {
                    throw new ReflectionException(cnfe, "The parameter class could not be found: "+ sig[i]);
                }
            }
        } else {
            // set empty args and signature
            signature = new Class[0];
            arguments = new Object[0];
        }

        Object targetObject = null;
        // Get method handle
        if (opMethodName.equals("setPrim")) {
            try {
                opHandle = this.getClass().getMethod(opMethodName, signature);
                targetObject = this;
            } catch (NoSuchMethodException nsml) {

            }
        }

        if (opHandle == null && targetObject == null) {
            try {
                opHandle = target.getClass().getMethod(opMethodName, signature);
                targetObject = target;
            } catch (NoSuchMethodException nsme) {
                throw new ReflectionException(nsme, "The method " + opClassName + "." + opMethodName + " could not be found");
            }
        }

        try {

            return opHandle.invoke(targetObject, arguments);

        } catch (RuntimeErrorException ree) {
            throw new RuntimeOperationsException(ree, "RuntimeException occured in PrimDyanmicMBean while trying to invoke operation " + opName);
        } catch (RuntimeException re) {
            throw new RuntimeOperationsException(re, "RuntimeException occured in PrimDyanmicMBean while trying to invoke operation " + opName);
        } catch (IllegalAccessException iae) {
            throw new ReflectionException(iae, "IllegalAccessException occured in PrimDyanmicMBean while trying to invoke operation " + opName);
        } catch (InvocationTargetException ite) {
            Throwable mmbTargEx = ite.getTargetException();
            if (mmbTargEx instanceof RuntimeException) {
                throw new MBeanException((RuntimeException) mmbTargEx, "RuntimeException thrown in PrimDyanmicMBean while trying to invoke operation " + opName);
            } else if (mmbTargEx instanceof ReflectionException) {
                throw (ReflectionException) mmbTargEx;
            } else {
                throw new MBeanException((Exception) mmbTargEx, "Exception thrown in PrimDyanmicMBean while trying to invoke operation " + opName);
            }
        } catch (Error err) {
            throw new RuntimeErrorException((Error) err, "Error occured in PrimDyanmicMBean while trying to invoke operation " + opName);
        } catch (Exception e) {
            throw new ReflectionException(e, "Exception occured in PrimDyanmicMBean while trying to invoke operation " + opName);
        }
  }

  public void setAttribute(Attribute attribute) throws javax.management.AttributeNotFoundException, javax.management.InvalidAttributeValueException, javax.management.MBeanException, javax.management.ReflectionException {
      try {
          String name = attribute.getName();
          Object oldValue = getAttribute(name);
          Object newValue = attribute.getValue();
          target.sfReplaceAttribute(name, newValue);
          // Send notification
          long timeStamp = System.currentTimeMillis();
          String message = "SmartFrog attribute modified";
          String type = newValue.getClass().getName();
          nbs.sendNotification(new AttributeChangeNotification(this, sequence++, timeStamp, message, name, type, oldValue, newValue));
      }
      catch (RemoteException re) {
          throw new MBeanException(re, re.getMessage());
      }
      catch (AttributeNotFoundException anfe) {
          throw anfe;
      }
      catch (Exception e) {
          throw new ReflectionException(e, e.getMessage());
      }
  }

  public AttributeList setAttributes(AttributeList attributes) {
      if (attributes == null) return null;
      AttributeList newValues = (AttributeList) attributes.clone();
      for (int i=0; i<attributes.size(); i++) {
          try {
              setAttribute((Attribute) attributes.get(i));
          }
          catch (Exception e) { newValues.remove(i); }
      }
      return newValues;
  }

  public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {
      if (listener == null) throw new IllegalArgumentException("listener cannot be null");
      nbs.addNotificationListener(listener, filter, handback);

  }

  public MBeanNotificationInfo[] getNotificationInfo() {
      return notifications;
  }

  public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
      nbs.removeNotificationListener(listener);
  }

}
