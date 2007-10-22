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

import java.io.Serializable;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class SFAttribute implements Serializable {
    /**
     *  Description of the Field
     */
    public final static int UNKNOWN = -1;
    /**
     *  Description of the Field
     */
    public final static int BASIC = 0;
    /**
     *  Description of the Field
     */
    public final static int PRIM = 1;
    /**
     *  Description of the Field
     */
    public final static int COMPOUND = 2;
    /**
     *  Description of the Field
     */
    public final static int COMP_DESCRIPTION = 3;
    /**
     *  Description of the Field
     */
    public final static int REFERENCE = 4;

    /**
     *  Description of the Field
     */
    protected String name = null;
    /**
     *  Description of the Field
     */
    protected Object value = null;
    /**
     *  Description of the Field
     */
    protected String clazz = "";
    /**
     *  Description of the Field
     */
    protected int sfType = -1;
    /**
     *  Description of the Field
     */
    protected boolean writable = false;
    /**
     *  Description of the Field
     */
    protected String description = null;


    /**
     *  Constructor for the SFAttribute object
     *
     *@param  n     Description of the Parameter
     *@param  v     Description of the Parameter
     *@param  t     Description of the Parameter
     *@param  w     Description of the Parameter
     *@param  desc  Description of the Parameter
     */
    public SFAttribute(String n, Object v, int t, boolean w, String desc) {
        name = n;
        value = v;
        if (value != null) {
            clazz = value.getClass().getName();
        }
        sfType = t;
        writable = w;
        description = desc;
    }


    /**
     *  Gets the name attribute of the SFAttribute object
     *
     *@return    The name value
     */
    public String getName() {
        return name;
    }


    /**
     *  Gets the value attribute of the SFAttribute object
     *
     *@return    The value value
     */
    public Object getValue() {
        return value;
    }


    /**
     *  Gets the clazz attribute of the SFAttribute object
     *
     *@return    The clazz value
     */
    public String getClazz() {
        return clazz;
    }


    /**
     *  Gets the sFType attribute of the SFAttribute object
     *
     *@return    The sFType value
     */
    public int getSFType() {
        return sfType;
    }


    /**
     *  Gets the writable attribute of the SFAttribute object
     *
     *@return    The writable value
     */
    public boolean isWritable() {
        return writable;
    }


    /**
     *  Gets the description attribute of the SFAttribute object
     *
     *@return    The description value
     */
    public String getDescription() {
        return description;
    }


    /**
     *  Sets the name attribute of the SFAttribute object
     *
     *@param  n  The new name value
     */
    public void setName(String n) {
        name = n;
    }


    /**
     *  Sets the value attribute of the SFAttribute object
     *
     *@param  v  The new value value
     */
    public void setValue(Object v) {
        value = v;
    }


    /**
     *  Sets the sFType attribute of the SFAttribute object
     *
     *@param  t  The new sFType value
     */
    public void setSFType(int t) {
        sfType = t;
    }


    /**
     *  Sets the description attribute of the SFAttribute object
     *
     *@param  desc  The new description value
     */
    public void setDescription(String desc) {
        description = desc;
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String toString() {
        String type = null;
        switch (sfType) {
            case (0):
                type = "BASIC";
                break;
            case (1):
                type = "PRIM";
                break;
            case (2):
                type = "COMPOUND";
                break;
            case (3):
                type = "COMPONENT_DESCRIPTION";
                break;
            case (4):
                type = "REFERENCE";
                break;
            default:
                type = "UNKNOWN";
        }
        return "[name: " + name + ", value: " + value + ", type: " + type + "]\n";
    }

}
