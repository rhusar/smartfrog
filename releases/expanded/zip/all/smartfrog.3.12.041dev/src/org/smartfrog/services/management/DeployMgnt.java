/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.management;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.util.Set;


/**
 *  Class used for deploy management.
 *  Proxy for management operations in Prim SmartFrog components.
 *
 */
public class DeployMgnt {
    /**
     *  Constructs the DeployMgnt object
     */
    private DeployMgnt() {
    }

    /**
     *  Adds an attribute (name value pair)
     *
     *@param  obj            SF Component
     *@param  attribName     Name of the attribute
     *@param  attribValue    Value of the attribute
     *@throws  Exception  If any error
     */
    public static void addAttribute(Object obj, Object attribName, Object attribValue) throws Exception {
        if (obj instanceof Prim) {
            ((Prim)obj).sfReplaceAttribute(attribName, attribValue);
        } else if (obj instanceof ComponentDescription){
            ((ComponentDescription)obj).sfReplaceAttribute(attribName, attribValue);
        }

    }

    /**
     *  Removes an attribute
     *
     *@param  obj            SF Component
     *@param  attribName     Name of the attribute
     *@throws  Exception  If any error
     */
    public static void removeAttribute(Object obj, Object attribName) throws Exception {
        if (obj instanceof Prim) {
            ((Prim)obj).sfRemoveAttribute(attribName);
        } else if (obj instanceof ComponentDescription){
            ((ComponentDescription)obj).sfRemoveAttribute(attribName);
        }
    }

    /**
     * Modifies any attribute with new value
     *
     *@param  obj            SF Component
     *@param  attribName     Name of the attribute
     *@param  attribValue    Value of the attribute
     *@throws  Exception  If any error
     */
    public static void modifyAttribute(Object obj, Object attribName, Object attribValue, Object attribTags) throws Exception {
        if (obj instanceof Prim) {
            ((Prim)obj).sfReplaceAttribute(attribName, attribValue);
            if (attribTags != null){
               ((Prim)obj).sfSetTags(attribName,(Set)attribTags);
            }
        } else if (obj instanceof ComponentDescription){
            ((ComponentDescription)obj).sfReplaceAttribute(attribName, attribValue);
            if (attribTags != null){
               ((ComponentDescription)obj).sfSetTags(attribName,(Set)attribTags);
            }
        }
    }

    /**
     * Terminates the SF Component
     *
     * @param  obj  Reference to SF Component
     * @param type error type
     * @param reason cause
     * @throws Exception if termination fails
     */
    public static void terminate(Prim obj, String type, String reason) throws Exception {
            TerminationRecord tr = new TerminationRecord(type, reason, null);
            obj.sfTerminate(tr);
    }

    /**
     * Detaches and terminates the SF Component
     *
     * @param  obj  Reference to SF Component
     * @param type error type
     * @param reason cause
     * @throws Exception if termination fails
     */
    public static void dTerminate(Prim obj, String type, String reason) throws Exception {
        TerminationRecord tr = new TerminationRecord(type, reason, null);
        obj.sfDetachAndTerminate(tr);
    }

    /**
     * Detaches the SF Component
     *
     *@param  obj  Reference to SF Component
     */
    public static void detach(Prim obj) throws Exception {
        obj.sfDetach();
    }
}
