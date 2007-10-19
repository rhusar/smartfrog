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
import org.smartfrog.projects.alpine.om.base.SoapElement;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.LinkedList;

/**
 * this class holds a static resource mapping, stored as a Xom Element.
 */
public class StaticProperty implements Property {

    private QName name;
    private List<Element> value;
    private int minOccurs = 0;
    private int maxOccurs = UNBOUNDED;
    /**
     * constant to indicate that the element is unbounded
     */
    public static final int UNBOUNDED = Integer.MAX_VALUE;


    public StaticProperty() {
    }


    public StaticProperty(QName name) {
        this.name = name;
        setValue((Element)null);
    }

    public StaticProperty(QName name, Element value) {
        this.name = name;
        setValue(value);
    }

    public StaticProperty(QName name, String value) {
        this.name = name;
        setValue(value);
    }

    public StaticProperty(QName name, List<Element> value) {
        this.name = name;
        this.value = value;
    }

    public int getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    public int getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public List<Element> getValue() {
        return value;
    }

    public void setValue(Element value) {
        setValue(WsrfUtils.listify(value));
    }

    public void setValue(String value) {
        Element elt = new SoapElement(name);
        elt.appendChild(value);
        setValue(elt);
    }

    public void setValue(List<Element> value) {
        int size = value.size();
        if (size < minOccurs || size > maxOccurs) {
            throw new IllegalArgumentException("Size of list (" + size
                    + ") "
                    + " is out of bounds."
                    + " minOccurs=" + minOccurs + " ."
                    + " maxOccurs=" + maxOccurs + ".");
        }
        this.value = value;
    }

}
