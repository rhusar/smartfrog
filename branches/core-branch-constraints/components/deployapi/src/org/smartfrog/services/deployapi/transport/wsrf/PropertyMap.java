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

import nu.xom.Element;

import javax.xml.namespace.QName;
import java.util.Hashtable;
import java.util.Map;
import java.util.List;

/**
 * A property map represents a source of live or static WSRP properties.
 * This class acts as a source for WSRP resources.
 */
public class PropertyMap implements WSRPResourceSource {

    private Map<QName,Property> map=new Hashtable<QName, Property>();

    public PropertyMap() {
    }

    /**
     * add a resource
     * @param property
     */
    public synchronized void add(Property property) {
        map.put(property.getName(),property);
    }

    public synchronized void remove(Property property) {
        remove(property.getName());
    }

    public synchronized void remove(QName name) {
        map.remove(name);
    }



    /**
     * resolve a property or return null
     * @param name
     * @return
     */
    public Property lookupProperty(QName name) {
        return map.get(name);
    }


    /**
     * Look for a matchiing property and get its value
     * @param name
     * @return the value or null for no match
     */
    public List<Element> getProperty(QName name) {
        Property property = lookupProperty(name);
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
    public StaticProperty addStaticProperty(QName name,Element value) {
        StaticProperty property=new StaticProperty();
        property.setName(name);
        property.setValue(value);
        add(property);
        return property;
    }

    /**
     * Add a static property
     *
     * @param name  property name
     */
    public StaticProperty addStaticProperty(QName name) {
        StaticProperty property = new StaticProperty(name);
        add(property);
        return property;
    }

    /**
     * Add a static property
     *
     * @param name  property name
     * @param value property value
     */
    public StaticProperty addStaticProperty(QName name, String value) {
        StaticProperty property = new StaticProperty(name, value);
        add(property);
        return property;
    }


    /**
     * Add a static property
     *
     * @param name  property name
     * @param value property value
     */
    public StaticProperty addStaticProperty(QName name, List<Element> value) {
        StaticProperty property = new StaticProperty(name, value);
        add(property);
        return property;
    }

}
