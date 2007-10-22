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
package org.smartfrog.services.xml.impl;

import nu.xom.Attribute;
import nu.xom.Node;
import nu.xom.XMLException;
import org.smartfrog.services.xml.interfaces.XmlAttribute;
import org.smartfrog.services.xml.interfaces.XmlQNameNode;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * Attributes
 */
public class XmlAttributeImpl extends SimpleXmlNode
        implements XmlAttribute {

    /**
     *
     * @throws RemoteException In case of network/rmi error
     */
    public XmlAttributeImpl() throws RemoteException {
    }


    /**
     * create a node of the appropriate type. This is called during deployment;
     * The node name is from {@link XmlQNameNode#ATTR_LOCALNAME}; optional
     * namespace from {@link XmlQNameNode#ATTR_NAMESPACE},
     * Mandatory Value from {@link XmlAttribute#ATTR_VALUE}
     * @return a new node of type {@link Attribute}
     * @throws nu.xom.XMLException if needed
     */
    public Node createNode() throws RemoteException, SmartFrogException {
        String localname = sfResolve(ATTR_LOCALNAME, (String) null, true);
        String namespace = sfResolve(ATTR_NAMESPACE, (String) null, false);
        String value = sfResolve(ATTR_VALUE, "", true);
        try {
            return new Attribute(localname, namespace, value);
        } catch (XMLException e) {
            throw XmlNodeHelper.handleXmlException(e);
        }
    }
}
