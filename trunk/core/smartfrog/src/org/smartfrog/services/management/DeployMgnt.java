/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
    public static void addAttribute(Prim obj, Object attribName,
        Object attribValue) throws Exception {
        // Object oldValue = obj.sfContext().get(attribName);
        // ComponentDescription cmp = SFParser.getParser().parse(attribName.
    // toString() + " " + value.toString() + ";");
        // obj.sfReplaceAttribute(attribName, cmp.getContext().get(attribName));
        obj.sfReplaceAttribute(attribName, attribValue);
    }

    /**
     *  Removes an attribute
     *
     *@param  obj            SF Component
     *@param  attribName     Name of the attribute
     *@throws  Exception  If any error
     */
    public static void removeAttribute(Prim obj, Object attribName)
        throws Exception {
        obj.sfRemoveAttribute(attribName);
    }

    /**
     * Modifies any attribute with new value
     *
     *@param  obj            SF Component
     *@param  attribName     Name of the attribute
     *@param  attribValue    Value of the attribute
     *@throws  Exception  If any error
     */
    public static void modifyAttribute(Prim obj, Object attribName,
        Object attribValue) throws Exception {
        // Object oldValue = obj.sfContext().get(attribName);
        // ComponentDescription cmp = SFParser.getParser().parse(attribName.
    // toString() + " " + value.toString() + ";");
        // obj.sfReplaceAttribute(attribName, cmp.getContext().get(attribName));
        obj.sfReplaceAttribute(attribName, attribValue);
    }

    /**
     * Terminates the SF Component
     *
     *@param  obj  Reference to SF Component
     */
    public static void terminate(Prim obj, String type, String reason) {
        try {
            TerminationRecord tr = new TerminationRecord(type,
                    reason, null);
            obj.sfTerminate(tr);
        } catch (Exception ex) {
            //@TODO: log exception message.
            //ex.printStackTrace();
        }
    }

    /**
     * Detaches and terminates the SF Component
     *
     *@param  obj  Reference to SF Component
     */
    public static void dTerminate(Prim obj, String type, String reason) {
        try {
            TerminationRecord tr = new TerminationRecord(type,
                    reason, null);
            obj.sfDetachAndTerminate(tr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Detaches the SF Component
     *
     *@param  obj  Reference to SF Component
     */
    public static void detach(Prim obj) {
        try {
            obj.sfDetach();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
