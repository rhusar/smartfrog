/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.deployapi.transport.wsrf;

import org.ggf.cddlm.utils.QualifiedName;
import org.smartfrog.services.deployapi.system.Utils;
import org.apache.axis2.om.OMElement;

import javax.xml.namespace.QName;
import java.util.Hashtable;
import java.util.Map;

import nu.xom.Element;

/**

 */
public class PropertyMap {

    private Map<QName,Property> map=new Hashtable<QName, Property>();


    /**
     * add a resource
     * @param property
     */
    public synchronized void add(Property property) {
        map.put(property.getName(),property);
    }

    public synchronized void remove(Property property) {
        map.remove(property.getName());
    }

    /**
     * resolve a property or return null
     * @param name
     * @return
     */
    public Property getProperty(QName name) {
        return map.get(name);
    }


    /**
     * Look for a matchiing property and get its value
     * @param name
     * @return the value or null for no match
     */
    public OMElement getPropertyValue(QName name) {
        Property property = getProperty(name);
        if(property!=null) {
            return property.getValue();
        } else {
            return null;
        }
    }

    /**
     * Add a static property
     * @param name property name
     * @param value property value
     */
    public void addStaticProperty(QName name,OMElement value) {
        StaticProperty property=new StaticProperty();
        property.setName(name);
        property.setValue(value);
    }

    /**
     * Add a static property
     *
     * @param name  property name
     * @param value property value
     */
    public void addStaticProperty(QualifiedName name, Element value) {
        StaticProperty property = new StaticProperty();
        property.setName(name);
        property.setValue(value);
    }

}
