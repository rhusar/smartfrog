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

import org.apache.axis2.om.OMElement;
import org.smartfrog.services.deployapi.system.Utils;
import org.ggf.cddlm.utils.QualifiedName;

import javax.xml.namespace.QName;

import nu.xom.Element;

/**
 * this class represents something that can provide a resource
 */
public class StaticProperty implements Property {

    private QName name;
    private OMElement value;

    public StaticProperty() {
    }

    public StaticProperty(QName name, OMElement value) {
        this.name = name;
        this.value = value;
    }

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public void setName(QualifiedName name) {
        setName(Utils.convert(name));
    }

    public OMElement getValue() {
        return value;
    }

    public void setValue(OMElement value) {
        this.value = value;
    }

    public void setValue(Element element) {
        setValue(Utils.xomToAxiom(element));
    }


}
