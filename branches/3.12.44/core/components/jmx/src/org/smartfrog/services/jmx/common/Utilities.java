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

package org.smartfrog.services.jmx.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.NoSuchObjectException;
import java.net.MalformedURLException;
import javax.management.ObjectName;
import javax.management.MBeanParameterInfo;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.services.jmx.mbeanbrowser.OperationListElement;
import org.smartfrog.services.jmx.communication.ConnectionFactory;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class Utilities {

    /**
     *  Description of the Class
     *
     *          sfJMX
     *   JMX-based Management Framework for SmartFrog Applications
     *       Hewlett Packard
 *
     *@version        1.0
     */
    public static class StringComparator implements Comparator {

        /**
         *  This method lexically compares two objects. Null references are
         *  treated as lexically less then valid references.
         *
         *@param  left   The object to lexically compare to the right object.
         *      May be null.
         *@param  right  The object to lexically compare to the left object. May
         *      be null.
         *@return        A value indicating the result of the comparison. A
         *      value of -1 indicates that the left is lexically less than the
         *      right. A value of 0 indicates that the left and right are
         *      lexically equal. A value of 1 indicates that the left is
         *      lexically larger then the right. Note: Null references for both
         *      left and right are treated as equal.
         *@see           java.util.Comparator
         */
        public int compare(Object left, Object right) {
            if (left == null && right == null) {
                return (0);
            } else if (left == null) {
                return (-1);
            } else if (right == null) {
                return (1);
            } else {
                String leftStr = left.toString();
                String rightStr = right.toString();
                return (leftStr.compareTo(rightStr));
            }
        }


        /**
         *  This method determines if the two objects are lexically equal. The
         *  compare() method is used.
         *
         *@param  left   The object to lexically compare to the right object.
         *      May be null.
         *@param  right  The object to lexically compare to the left object. May
         *      be null.
         *@return        True if the left and right objects are lexically equal
         *      and False otherwise. Null references for both left and right are
         *      treated as equal.
         */
        public boolean equals(Object left, Object right) {
            return (compare(left, right) == 0);
        }
    }


    /**
     *  Description of the Class
     *
     *          sfJMX
     *   JMX-based Management Framework for SmartFrog Applications
     *       Hewlett Packard
 *
     *@version        1.0
     */
    public static class OperationComparator implements Comparator {
        /**
         *  Description of the Method
         *
         *@param  left   Description of the Parameter
         *@param  right  Description of the Parameter
         *@return        Description of the Return Value
         */
        public int compare(Object left, Object right) {
            if (left == null && right == null) {
                return (0);
            } else if (left == null) {
                return (-1);
            } else if (right == null) {
                return (1);
            } else {
                String leftStr = left.toString();
                String rightStr = right.toString();
                int result = leftStr.compareTo(rightStr);
                if (result != 0) {
                    return result;
                }
                if (!(left instanceof OperationListElement) &&
                        !(right instanceof OperationListElement)) {
                    return (0);
                } else if (!(left instanceof OperationListElement)) {
                    return (-1);
                } else if (!(right instanceof OperationListElement)) {
                    return (1);
                } else {
                    leftStr = ((OperationListElement) left).turnParametersIntoString();
                    rightStr = ((OperationListElement) right).turnParametersIntoString();
                    return leftStr.compareTo(rightStr);
                }
            }
        }


        /**
         *  Description of the Method
         *
         *@param  left   Description of the Parameter
         *@param  right  Description of the Parameter
         *@return        Description of the Return Value
         */
        public boolean equals(Object left, Object right) {
            return (compare(left, right) == 0);
        }
    }


    /**
     *  Description of the Field
     */
    public final static int STRING_COMPARATOR = 1;
    /**
     *  Description of the Field
     */
    public final static int INTEGER_COMPARATOR = 2;
    /**
     *  Description of the Field
     */
    public final static int OPERATION_COMPARATOR = 3;


    /**
     *  Gets the comparator attribute of the Utilities class
     *
     *@param  type  Description of the Parameter
     *@return       The comparator value
     */
    public static Comparator getComparator(int type) {
        switch (type) {
            case (STRING_COMPARATOR):
                return new StringComparator();
            case (INTEGER_COMPARATOR):
                return null;
            case (OPERATION_COMPARATOR):
                return new OperationComparator();
            default:
                return null;
        }
    }


    /**
     *  TODO
     *
     *@param  type
     *@param  value
     *@return  Object object instance
     */
    public static Object objectFromString(String type, Object value) {
        if (value == null) {
            return (null);
        }
        if (type == null) {
            return value;
        }
        if (!(value instanceof String)) {
            return (value);
        }
        String strValue = (String) value;

        if (type.equals("java.lang.Number")) {
            try {
                return (new Integer(strValue));
            } catch (Exception i) {
                try {
                    return (new Long(strValue));
                } catch (Exception l) {
                    try {
                        return (new Float(strValue));
                    } catch (Exception f) {
                        try {
                            return (new Double(strValue));
                        } catch (Exception d) {
                            // return new Number();
                        }
                    }
                }
            }
        }
        if (type.equals("int") || type.equals("java.lang.Integer")) {
            try {
                return (new Integer(strValue));
            } catch (Exception e) {
                //   return( new Integer( 0 ) );
            }
        } else if (type.equals("long") || type.equals("java.lang.Long")) {
            try {
                return (new Long(strValue));
            } catch (Exception e) {
                return null;
                //return( new Long( 0 ) );
            }
        } else if (type.equals("float") || type.equals("java.lang.Float")) {
            try {
                return (new Float(strValue));
            } catch (Exception e) {
                //return( new Float( 0 ) );
            }
        } else if (type.equals("double") || type.equals("java.lang.Double")) {
            try {
                return (new Double(strValue));
            } catch (Exception e) {
                //return( new Double( 0 ) );
            }
        } else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
            return (new Boolean(strValue));
        } else if (type.equals("javax.management.ObjectName")) {
            try {
                return (new ObjectName(strValue));
            } catch (Exception e) {}
        } else if (type.equals("java.util.Date")) {
            try {
                return (new SimpleDateFormat()).parse(strValue);
            } catch (Exception e) {}
        } else if (type.equals("org.smartfrog.sfcore.prim.TerminationRecord")) {
            return (TerminationRecord.abnormal(strValue, null));
        } else if (type.equals("java.net.URL")) {
            try {
              return new java.net.URL(strValue);
            } catch (MalformedURLException e) {}
        } else {
            try {
              Class clazz = Class.forName(type);
              return clazz.getConstructor(new Class[]{String.class}).newInstance(new Object[]{strValue});
            } catch (Exception e) { return (value); }
        }
        return null;
    }


    /**
     *  Description of the Method
     *
     *@param  type   Description of the Parameter
     *@param  value  Description of the Parameter
     *@return        Description of the Return Value
     */
    public static Object objectFromVector(String type, Object value) {

        if (!(value instanceof Vector)) {
            return value;
        }
        Vector vecValue = (Vector) value;
        try {
            if (type.equals("org.smartfrog.services.jmx.communication.ServerAddress")) {
                String protocol = (String) vecValue.get(0);
                String host = (String) vecValue.get(1);
                int port = ((Integer) vecValue.get(2)).intValue();
                Object resource = vecValue.get(3);
                return ConnectionFactory.createServerAddress(protocol, host, port, resource);
            } else if (type.equals("com.sun.jdmk.comm.AuthInfo")) {
                Class authInfoClass = Class.forName(type);
                Constructor authInfoCons = authInfoClass.getConstructor(new Class[]{String.class, String.class});
                return authInfoCons.newInstance(new Object[]{(String) vecValue.get(0), (String) vecValue.get(1)});
            } else {
                return (value);
            }
        } catch (Exception e) {}
        return null;
    }


    /**
     *  Checks and returns the class identified by the string given as parameter.
     *  It even returns the class representing basic types (boolean, byte, char,
     *  short, ...)
     *
     *@param  typeString
     *@return the class whose name matches the string
     *@throws ClassNotFoundException
     */
    public static Class classFromString(String typeString) throws ClassNotFoundException {
        if (typeString == null) {
            return null;
        }
        if (typeString.equals("boolean")) {
            return boolean.class;
        }
        if (typeString.equals("byte")) {
            return byte.class;
        }
        if (typeString.equals("char")) {
            return char.class;
        }
        if (typeString.equals("short")) {
            return short.class;
        }
        if (typeString.equals("int")) {
            return int.class;
        }
        if (typeString.equals("long")) {
            return long.class;
        }
        if (typeString.equals("float")) {
            return float.class;
        }
        if (typeString.equals("double")) {
            return double.class;
        }
        return Class.forName(typeString);
    }

    /**
     *  Returns an array of MBeanParameterInfo from the Context describing a
     *  method.
     *
     *@param  method  The context of manageable method
     *@return         the array of MBeanParameterInfo of the described method
     */
    public static MBeanParameterInfo[] getParameterInfo(Context method) {
        Vector infoVector = new Vector();
        for (Enumeration m = method.keys(); m.hasMoreElements(); ) {
            String key = (String) m.nextElement();
            ComponentDescription paramCompDesc = null;
            Context paramContext = null;
            try {
                paramCompDesc = (ComponentDescription) method.get(key);
            } catch (ClassCastException cce) {
                continue;
            }
            try {
                paramContext = paramCompDesc.sfContext();
                if (!"parameter".equals(paramContext.get("descriptorType"))) {
                    continue;
                }
                String typeString = (String) paramContext.get("type");
                infoVector.addElement(new MBeanParameterInfo((String) paramContext.get("name"),
                        typeString,
                        (String) paramContext.get("description")));
            } catch (Exception e) {
                return null;
            }
        }
        MBeanParameterInfo[] parameterInfo = new MBeanParameterInfo[infoVector.size()];
        infoVector.copyInto(parameterInfo);
        return parameterInfo;
    }

    /**
     *  Returns a method identifier from the Context describing a method. The
     *  method is identified by its name and its parameters.
     *
     *@param  method  The Context containing the description of a manageable
     *@return         the method identifier
     */
    public static MethodID getMethodID(Context method) {
        Vector typeVector = new Vector();
        for (Enumeration m = method.keys(); m.hasMoreElements(); ) {
            String key = (String) m.nextElement();
            ComponentDescription paramCompDesc = null;
            Context paramContext = null;
            try {
                paramCompDesc = (ComponentDescription) method.get(key);
            } catch (ClassCastException cce) {
                continue;
            }
            try {
                paramContext = paramCompDesc.sfContext();
                String typeString = (String) paramContext.get("type");
                typeVector.addElement(classFromString(typeString));
            } catch (Exception e) {
                return null;
            }
        }
        Class[] types = new Class[typeVector.size()];
        typeVector.copyInto(types);
        return new MethodID((String) method.get("name"), types);
    }

    /**
     *  Checks if the object passed as argument is a basic attribute, that is,
     *  neither a deployed Prim component nor a ComponentDescription
     *
     *@param  obj  Description of the Parameter
     *@return      The attribute value
     */
    public static boolean isAttribute(Object obj) {
        if (!(obj instanceof Prim)
                 && !(obj instanceof ComponentDescription)
                 && !(obj instanceof Reference)) {
            return true;
        }
        return false;
    }


    /**
     *  Returns the number of basic attributes contained in the Context which
     *  are neither a deployed Prim component nor a ComponentDescription
     *
     *@param  c  The context to be analyzed
     *@return    number of basic attributes
     */
    public static int numberOfAttribute(Context c) {
        try {
            int counter = 0;
            String name = "";
            Object obj = null;
            for (Enumeration e = c.keys(); e.hasMoreElements(); ) {
                name = e.nextElement().toString();
                obj = c.get(name);
                if (isAttribute(obj)) {
                    counter++;
                }
                //&& !name.toString().endsWith("URL"))
            }
            return counter;
        } catch (Exception ex) {
            return 0;
        }
    }



   public static Method checkGetter(String getter, Object targetObject, String attribute, String type, boolean isGetterParameter) {
      Class   targetClass  = targetObject.getClass();
      Method  method = null;
      Class[] params = null;
      if (isGetterParameter) {
          params = new Class[] {String.class};
      } else {
          params = new Class[0];
      }

      if (getter != null) {
         try {
            method = targetClass.getMethod( getter, params );
         }
         catch (Exception e) { }
      }

      if (method == null) {
         getter = "get" + attribute;
         try {
            method = targetClass.getMethod(getter, params);
         }
         catch (Exception e) { }
      }

      // Check if it is a boolean, so its getter may be a "is" method
      if (method == null) {
         if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
            getter = "is" + attribute ;
            try {
                method = targetClass.getMethod( getter, params );
            }
            catch (Exception e) { }
         }
      }
      return method;
   }

   public static Method checkSetter(String setter, Object targetObject, String attribute, String type, boolean isSetterParameter) throws ClassNotFoundException {
      Class   targetClass = targetObject.getClass();
      Method  method = null;
      Class[] params = null;

      if (isSetterParameter) {
          params = new Class[] {String.class, classFromString(type)};
      } else {
          params = new Class[] {classFromString(type)};
      }


      if (setter != null) {
         try {
            method = targetClass.getMethod(setter, params);
         }
         catch (Exception e) { }
      }

      if (method == null) {
         setter = "set" + attribute ;
         try {
            method = targetClass.getMethod(setter, params);
         }
         catch (Exception e) { }
      }
      return method;
   }

   public static Method checkOperation(Object targetObject, String operation, String[] signature) throws ClassNotFoundException {
      Class   targetClass  = targetObject.getClass();
      Class[] params = new Class[signature.length];
      Method  method = null;

      for( int i=0; i<params.length; i++ ) {
          params[i] = classFromString(signature[i]);
      }

      try {
         method = targetClass.getMethod(operation, params);
      }
      catch (Exception e) { }

      return (method);
   }

    /**
     *  Finds a RemoteStub for the specified object
     *
     *@param  object               The object from which we want to get the stub
     *@return                      The RemoteStub
     *@exception  RemoteException  if it has not been possible to find the stub
     *@exception  Exception        Description of the Exception
     */
    public static RemoteStub getStub(Remote object) throws RemoteException, Exception {
        if (object instanceof RemoteStub) {
            return (RemoteStub) object;
        }
        if (object instanceof PrimImpl) {
            RemoteStub rs = (RemoteStub) ((PrimImpl) object).sfExportRef();
            if (rs != null) {
                return rs;
            }
        }
        try {
            return (RemoteStub) RemoteStub.toStub(object);
        } catch (NoSuchObjectException nsoe) {
            return UnicastRemoteObject.exportObject(object);
        }
    }

    /**
     *  Gets the defaultNameProperty attribute for an MBean representing a Prim
     *  object
     *
     *@param  resource    Description of the Parameter
     *@return             The defaultNameProperty value
     *@throws  Exception  Description of the Exception
     */
    public static String getDefaultNamePropertyFor(Object resource) {
        try {
            if (resource instanceof Prim) {
                return ((Prim) resource).sfCompleteName().toString().replace(':', '_');
            }
            else if (resource instanceof ComponentDescription) {
                return ((ComponentDescription)resource).sfCompleteName().toString().replace(':', '_');
            }
        } catch (Exception e) { }
        return resource.toString();
    }

}
