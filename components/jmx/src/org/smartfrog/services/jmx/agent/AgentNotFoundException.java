/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jmx.agent;

import java.io.Serializable;

import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 *  A new exception
 *@version        1.0
 */

public class AgentNotFoundException extends Exception implements Serializable {

    public Object remoteAgentAddress=null;
    /** The source that was trying to resolve the remoteAgent */
    public Reference source = null;

    public String extraInfo = null;


    /**
     *  Constructor for the AgentNotFoundException object
     */
    public AgentNotFoundException() { }



    /**
     * construct an exception
     * @param remoteAgentAddress remote address
     * @param reason text message
     */
    public AgentNotFoundException(Object remoteAgentAddress, String reason) {
        this(remoteAgentAddress, null, reason);
    }


    /**
     * construct an exception
     * @param remoteAgentAddress remote address
     * @param source optional source reference
     * @param reason text message
     */
    public AgentNotFoundException(Object remoteAgentAddress, Reference source,
                                  String reason) {
        this(remoteAgentAddress, null, reason, null);
    }

    /**
     * construct an exception
     * @param remoteAgentAddress remote address
     * @param source optional source reference
     * @param reason text message
     * @param extraInfo optional extra information
     */
    public AgentNotFoundException(Object remoteAgentAddress, Reference source,
                                  String reason, String extraInfo) {
        super(reason);
        this.remoteAgentAddress = remoteAgentAddress;
        this.source = source;
        this.extraInfo = extraInfo;
    }


    /** Returns a string representation of the Agent Not Found exception
     *
     * @return reason source and agentAdd of exception */
    public String toString() {
      return
        (getMessage() == null ? "\n nAgentNotFoundException: Unknown" : "\nAgentNotFoundException: " +getMessage())
        + (source == null || source.size() == 0 ? "" : "\n  source: " + source.toString())
        + (remoteAgentAddress == null ? "" : "\n  remoteAgentAddress: " + remoteAgentAddress.toString()+ ", class: "+ remoteAgentAddress.getClass())
        + (extraInfo == null ? "" : "\n  extraInfo: " + extraInfo);
      }
}
