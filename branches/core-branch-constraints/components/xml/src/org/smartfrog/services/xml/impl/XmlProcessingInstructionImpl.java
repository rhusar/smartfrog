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
import nu.xom.ProcessingInstruction;
import nu.xom.XMLException;
import org.smartfrog.services.xml.interfaces.XmlProcessingInstruction;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * processing instr
 */
public class XmlProcessingInstructionImpl extends SimpleXmlNode
        implements XmlProcessingInstruction {

    /**
     * @throws RemoteException In case of network/rmi error
     */
    public XmlProcessingInstructionImpl() throws RemoteException {
    }

    /**
     * create a node of the appropriate type. This is called during deployment;
     *
     * @return a Node of type {@link nu.xom.ProcessingInstruction}
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException For smartfrog problems, and for caught
     *                            XMLExceptions     */
    public Node createNode() throws RemoteException, SmartFrogException {
        String target = sfResolve(ATTR_TARGET, "", true);
        String data = sfResolve(ATTR_DATA, "", true);
        try {
            return new ProcessingInstruction(target, data);
        } catch (XMLException e) {
            throw XmlNodeHelper.handleXmlException(e);
        }

    }
}
