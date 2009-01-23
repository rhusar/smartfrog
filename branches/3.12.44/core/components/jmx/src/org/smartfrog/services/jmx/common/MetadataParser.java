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

import java.util.*;
import java.lang.reflect.Method;
import java.beans.*;
import java.rmi.*;
import javax.management.MBeanParameterInfo;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.componentdescription.*;


/**
 * This class parse SF ComponentDescriptions matchin JMX metadata
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */

public class MetadataParser {

    /**
     *  Hashtable containing parsed metadata of manageable attributes
     */
    protected Hashtable attributes = new Hashtable();

    /**
     *  Hashtable containing parsed metadata of manageable methods
     */
    protected Hashtable methods = new Hashtable();


    /**
     *  Default constructor
     */
    public MetadataParser() { }


    /**
     *  Creates a MetadataParser from the Context object
     *
     *@param  metadata       Description of the Parameter
     *@exception  Exception  Description of the Exception
     */
    public MetadataParser(Context metadata) throws Exception {
        setMetadata(metadata);
    }


    /**
     *  Configure this SFModelMBeanInfoBuilder with a Prim object as the target
     *  to introspect and required information about the ModelMBean to be
     *  created.
     *
     *@param  metadata       The new metadata value
     *@exception  Exception  Description of the Exception
     */
    public void setMetadata(Context metadata) throws Exception {
        if (metadata == null) {
            throw new java.lang.IllegalArgumentException("mbeanContext argument cannot be null");
        }
        getMetadataFrom(metadata);
    }


    /**
     *  Retrieves and parses manageable metadata stored in the context. The
     *  context must contain metadata and descriptions of all those resources
     *  (attributes and methods) that are desired to have avalaible for
     *  management. Have a look at the file
     *  org/smartfrog/services/jmx/modelmbean/metadata.sf for describing manageable
     *  metadata.
     *
     *@param  metadata  The target containing manageable metadata. It can be
     *      null.
     */
    private void getMetadataFrom(Context metadata) {
        if (metadata == null) {
            return;
        }
        attributes = new Hashtable();
        methods = new Hashtable();
        for (Enumeration mk = metadata.keys(); mk.hasMoreElements(); ) {
            String mngKey = (String) mk.nextElement();
            ComponentDescription mngItemCompDesc = null;
            Context mngItemContext = null;
            Hashtable mngItemHash = null;
            try {
                mngItemCompDesc = (ComponentDescription) metadata.get(mngKey);
                mngItemContext = mngItemCompDesc.sfContext();
            } catch (ClassCastException cce) {
                continue;
            }
            if ("attribute".equals((String) mngItemContext.get("descriptorType"))) {
                //If attribute, we place only name in manageable attribute hashtable
                attributes.put(mngItemContext.get("name"), mngItemContext);
            } else if ("operation".equals(((String) mngItemContext.get("descriptorType")))) {
                MBeanParameterInfo[] parameterInfo = Utilities.getParameterInfo(mngItemContext);
                MethodID methodID = Utilities.getMethodID(mngItemContext);
                if (parameterInfo != null && methodID != null) {
                    mngItemContext.put("parameters", parameterInfo);
                    methods.put(methodID, mngItemContext);
                }
            }
        }
        // end for
    }

    /**
     * Returns the number of attributes contexts stored in this parser
     *
     * @return the number of attributes
     */
    public int getAttributeCount() {
      return attributes.size();
    }

    /**
     * Returns the number of methods contexts stored in this parser
     *
     * @return the number of methods
     */
    public int getMethodCount() {
      return methods.size();
    }

    /**
     *  Gets the metadata of manageable methods parsed into a Hashtable object
     *
     *@return    The methods value
     */
    public Enumeration getMethodIDs() {
        if (methods == null) {
            return null;
        }
        return methods.keys();
    }

    /**
     *
     * @return   Enumeration Method metadata
     */
    public Enumeration getMethodMetadata() {
        if (methods == null) {
          return null;
        }
        return methods.elements();
    }

    /**
     *  Gets the Context of the method containing its properties
     *
     *@param  methodID  Description of the Parameter
     *@return           The manageableOperationContext value
     */
    public Context getMethodContext(MethodID methodID) {
        if (methods == null) {
            return null;
        }
        return (Context) methods.get(methodID);
    }


    /**
     *  Gets the metadata of manageable attributes parsed into a Hashtable
     *  object
     *
     *@return    The manageable attribute metadata
     */
    public Enumeration getAttributes() {
        if (attributes == null) {
            return null;
        }
        return attributes.keys();
    }

    public Enumeration getAttributeMetadata() {
        if (attributes == null) {
            return null;
        }
        return attributes.elements();
    }

    /**
     *  Gets the context of the attribute passed as argument if it is manageable
     *
     *@param  attribute  Description of the Parameter
     *@return            The manageableAttributeContext value
     */
    public Context getAttributeContext(String attribute) {
        if (attributes == null) {
            return null;
        }
        return (Context) attributes.get(attribute);
    }

    /**
     *  Creates an entry for a new attribute with the properties given as
     *  arguments
     *
     *@param  name         The name of the attribute, used as the entry
     *@param  value        The intial value given to the attribute
     *@param  type         The type of the attribute
     *@param  description  A brief description of the attribute
     *@param  readable     Read access for the attribute
     *@param  writable     Write access for the attribute
     */
    public void addAttributeInfoFor(String name, Object value, String type, String description, boolean readable, boolean writable) {
        if (attributes == null) {
            attributes = new Hashtable();
        }
        Context c = new ContextImpl();
        c.put("name", name);
        c.put("type", type);
        c.put("description", description);
        c.put("readable", (new Boolean(readable)));
        c.put("writable", (new Boolean(writable)));
        c.put("scope", "public");
        attributes.put(name, c);
    }


    /**
     *  Remove the entry of the attribute
     *
     *@param  attribute  Description of the Parameter
     */
    public void removeAttribute(String attribute) {
        if (attributes != null) {
            attributes.remove(attribute);
        }
    }


    /**
     *  Change the access properties for the attribute
     *
     *@param  attribute  Description of the Parameter
     *@param  readable   Description of the Parameter
     *@param  writable   Description of the Parameter
     */
    public void changeAccessFor(String attribute, boolean readable, boolean writable) {
        ((Context) attributes.get(attribute)).remove("readable");
        ((Context) attributes.get(attribute)).put("readable", new Boolean(readable));
        ((Context) attributes.get(attribute)).remove("writable");
        ((Context) attributes.get(attribute)).put("writable", new Boolean(writable));
    }


    /**
     *  Checks if the attribute is readable
     *
     *@param  attribute  Description of the Parameter
     *@return            The readable value
     */
    public boolean isReadable(String attribute) {
        Context attrInfo = (Context) attributes.get(attribute);
        if (attrInfo != null) {
            return ((Boolean) attrInfo.get("readable")).booleanValue();
        } else {
            return false;
        }
    }


    /**
     *  Checks if the attribute is writable
     *
     *@param  attribute  Description of the Parameter
     *@return            The writable value
     */
    public boolean isWritable(String attribute) {
        Context attrInfo = (Context) attributes.get(attribute);
        if (attrInfo != null) {
            return ((Boolean) attrInfo.get("writable")).booleanValue();
        } else {
            return false;
        }
    }


    /**
     *  Gets the description of the attribute. If the attribute is not
     *  available, it returns an empty String
     *
     *@param  attribute  Description of the Parameter
     *@return            The description value
     */
    public String getDescription(String attribute) {
        Context attrInfo = (Context) attributes.get(attribute);
        if (attrInfo != null) {
            return (String) attrInfo.get("description");
        } else {
            return "";
        }
    }


    /**
     *  Gets the type of the attribute. If the attribute is not manageable,
     *  returns a empty String.
     *
     *@param  attribute  Description of the Parameter
     *@return            The type value
     */
    public String getType(String attribute) {
        Context attrInfo = (Context) attributes.get(attribute);
        if (attrInfo != null) {
            return (String) attrInfo.get("type");
        } else {
            return "";
        }
    }


    /**
     *  Checks if metadata of the attribute has been provided. Otherwise the
     *  attribute is not manageable.
     *
     *@param  attribute  Description of the Parameter
     *@return            The manageableAttribute value
     */
    public boolean isManageableAttribute(String attribute) {
        if (attributes != null) {
            return attributes.containsKey(attribute);
        }
        return false;
    }


    /**
     *  Checks if metadata of the method has been provided. Otherwise the method
     *  is not manageable.
     *
     *@param  method  Description of the Parameter
     *@return         The manageableMethod value
     */
    public boolean isManageableMethod(MethodID method) {
        if (methods != null) {
            return methods.containsKey(method);
        }
        return false;
    }

}
