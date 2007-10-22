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

package org.smartfrog.services.jmx.communication;

import java.io.Serializable;
import java.util.Date;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class ObjectID implements Serializable {

    private String toString;

    private String mbeanServerId;

    private Long objectReference;

    private String objectClass;


    /**
     *  Constructor for the ObjectID object
     *
     *@param  serverId  Description of the Parameter
     *@param  object    Description of the Parameter
     */
    public ObjectID(String serverId, Object object) {
        mbeanServerId = serverId;
        toString = object.toString();
        objectReference = new Long((new Date()).getTime());
        objectClass = object.getClass().getName();
    }


    /**
     *  Gets the mBeanServerId attribute of the ObjectID object
     *
     *@return    The mBeanServerId value
     */
    public String getMBeanServerId() {
        return mbeanServerId;
    }


    /**
     *  Gets the objectReference attribute of the ObjectID object
     *
     *@return    The objectReference value
     */
    public long getObjectReference() {
        return objectReference.longValue();
    }


    /**
     *  Gets the objectClass attribute of the ObjectID object
     *
     *@return    The objectClass value
     */
    public String getObjectClass() {
        return objectClass;
    }


    /**
     *  Description of the Method
     *
     *@param  object  Description of the Parameter
     *@return         Description of the Return Value
     */
    public boolean equals(Object object) {
        if (object instanceof ObjectID) {
            ObjectID objectID = (ObjectID) object;
            return (getMBeanServerId().equals(objectID.getMBeanServerId()) &&
                    getObjectReference() == objectID.getObjectReference());
        }
        return false;
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public int hashCode() {
        return mbeanServerId.hashCode() + objectReference.hashCode();
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String toString() {
        return toString;
    }
}
