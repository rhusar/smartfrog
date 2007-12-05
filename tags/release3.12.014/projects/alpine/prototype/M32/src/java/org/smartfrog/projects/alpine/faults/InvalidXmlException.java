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

package org.smartfrog.projects.alpine.faults;

import nu.xom.Node;
import org.smartfrog.projects.alpine.om.soap11.Fault;

/**
 * This exception is used to indicate some invalid XML 
 */
public class InvalidXmlException extends AlpineRuntimeException {

    private Node invalidNode;
    
    public InvalidXmlException(String message) {
        super(message);
    }
    
    public InvalidXmlException(Node invalidNode, String message) {
        super(message);
        this.invalidNode= invalidNode;
    }

    public Node getInvalidNode() {
        return invalidNode;
    }

    /**
     * if {@link #invalidNode} is not null, clone that node and insert it in the message
     * @param fault
     */
    public void addExtraDetails(Fault fault) {
        super.addExtraDetails(fault);
        if(invalidNode!=null) {
            Node cloned=invalidNode.copy();
            fault.addFaultDetail(FaultConstants.QNAME_FAULTDETAIL_INVALID_XML, cloned);
        }
    }
}
