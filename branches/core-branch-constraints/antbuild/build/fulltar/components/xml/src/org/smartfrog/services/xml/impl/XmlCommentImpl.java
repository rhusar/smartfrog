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

import nu.xom.Comment;
import nu.xom.Node;
import nu.xom.XMLException;
import org.smartfrog.services.xml.interfaces.XmlComment;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * Comment class
 */
public class XmlCommentImpl extends SimpleXmlNode implements XmlComment {

    /**
     *
     * @throws RemoteException In case of network/rmi error
     */
    public XmlCommentImpl() throws RemoteException {
    }

    /**
     * create a node of the appropriate type. This is called during deployment;
     *
     * @return a Node of type {@link nu.xom.Comment}
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException For smartfrog problems, and for caught
     *                            XMLExceptions
     */
    public Node createNode() throws RemoteException, SmartFrogException {
        String text = sfResolve(ATTR_COMMENT, "", true);
        try {
            return new Comment(text);
        } catch (XMLException e) {
            throw XmlNodeHelper.handleXmlException(e);
        }
    }


}
