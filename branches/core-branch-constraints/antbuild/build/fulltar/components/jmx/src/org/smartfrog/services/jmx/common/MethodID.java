package org.smartfrog.services.jmx.common;

import java.io.Serializable;

/**
 *  This class is used as the identifier of a method, since a method is not only
 *  identified by its name, but also by its parameters since, remember, a method
 *  can be overloaded.
 *
 *
 */

public class MethodID implements Serializable {
    String method = null;
    Class[] parameters = null;


    /**
     *  Constructor for the MethodID object
     *
     *@param  m  Description of the Parameter
     *@param  p  Description of the Parameter
     */
    public MethodID(String m, Class[] p) {
        method = m == null ? "" : m;
        parameters = p == null ? new Class[0] : p;
    }


    /**
     *  Description of the Method
     *
     *@param  obj  Description of the Parameter
     *@return      Description of the Return Value
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((!(obj instanceof MethodID)) || (obj == null)) {
            return false;
        }
        String m = ((MethodID) obj).method;
        Class[] p = ((MethodID) obj).parameters;
        if (!method.equals(m)) {
            return false;
        }
        if (parameters.length != p.length) {
            return false;
        }
        for (int i = 0; i < parameters.length; i++) {
            if (!parameters[i].getName().equals(p[i].getName())) {
                return false;
            }
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public int hashCode() {
        return this.toString().hashCode();
    }


    /**
     *  Gets the method attribute of the MethodID object
     *
     *@return    The method value
     */
    public String getMethod() {
        return method;
    }


    /**
     *  Gets the parameters attribute of the MethodID object
     *
     *@return    The parameters value
     */
    public Class[] getParameters() {
        return parameters;
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String toString() {
        String parameterLine = "";
        for (int i = 0; i < parameters.length; i++) {
            parameterLine = parameterLine + parameters[i].getName()+ " p" + i;
            if (i < (parameters.length - 1)) {
                parameterLine = parameterLine + ", ";
            }
        }
        return method + "( " + parameterLine + " )";
    }
}
