/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.projects.alpine.wsa;

/**
 * created 23-Mar-2006 13:03:39
 */


public interface AddressingConstants {
    /**
     * {@value}
     */
    String XMLNS_WSA_2003= "http://schemas.xmlsoap.org/ws/2003/03/addressing";
    /**
     * {@value}
     */
    String XMLNS_WSA_2004 = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    /**
     * {@value}
     */
    String XMLNS_WSA_2005 = "http://www.w3.org/2005/08/addressing";
    String XMLNS_WSA_2005_FAULTS = "http://www.w3.org/2005/08/addressing/fault";
    /**
     * Some endpoints cannot be located with a meaningful IRI;
     * this URI is used to allow such endpoints to send and receive messages.
     */
    String WSA_ADDRESS_NONE = "http://www.w3.org/2005/08/addressing/none";
    /**
     * Messages sent to EPRs whose [address] is this value MUST be discarded (i.e. not sent).
     */
    String WSA_ADDRESS_ANON = "http://www.w3.org/2005/08/addressing/anonymous";

    String WSA_RELATIONSHIP_IS_REPLY = "http://www.w3.org/2005/08/addressing/reply";

    String WSA_TO = "To";
    String WSA_FROM = "From";
    String WSA_FAULTTO="FaultTo";
    String WSA_REPLYTO = "ReplyTo";

    String WSA_ACTION = "Action";
    String WSA_ADDRESS = "Address";
    String WSA_METADATA = "Metadata";
    String WSA_MESSAGEID = "MessageID";
    String WSA_REFERENCE_PARAMETERS = "ReferenceParameters";
    String WSA_ATTR_IS_REFERENCE_PARAMETER = "IsReferenceParameter";

    String WSA_RELATES_TO = "RelatesTo";
}
