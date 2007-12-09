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

import nu.xom.Node;
import nu.xom.Text;
import nu.xom.XMLException;
import org.smartfrog.services.xml.interfaces.XmlTextNode;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * Implement the text node
 */
public class XmlTextNodeImpl extends SimpleXmlNode implements XmlTextNode {

    /**
     *
     * @throws RemoteException In case of network/rmi error
     *  */
    public XmlTextNodeImpl() throws RemoteException {
    }

    /**
     * create a node of the appropriate type. This is called during deployment;
     * Requires {@link #ATTR_TEXT} to be set
     * @return a Node of type {@link Text}
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException For smartfrog problems, and for caught
     *                            XMLExceptions
     */
    public Node createNode() throws RemoteException, SmartFrogException {
        String text = sfResolve(ATTR_TEXT, "", true);
        try {
            return new Text(text);
        } catch (XMLException e) {
            throw XmlNodeHelper.handleXmlException(e);
        }
    }
}
