/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.net;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;


/**
 * SmartFrogTelnetException is used by SmartFrog Telnet component.
 * @author Ashish Awasthi
 */
public class SmartFrogTelnetException extends SmartFrogException {
    private String command = null;
    private String failMessage = null;
    
    /**
     * Constructs a SmartFrogTelnetException with message.
     *
     * @param message exception message
     */
    public SmartFrogTelnetException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogTelnetException with command and fail message.
     * 
     * @param command Failed Command
     * @param failMsg Failure message
     */
    public SmartFrogTelnetException(String command, String failMsg) {
        super("unable to execute command["+ command +"]");
        this.command = command;
        this.failMessage = failMsg;
      
    }
    /**
     * Gets error in string form.
     * @return Detailed exception message
     */
    public String toString() {
        StringBuffer msg = new StringBuffer();
        msg.append("Unable to execute command: ")
           .append(command)
           .append("failure message: ")
           .append(failMessage)
           .append(". See telnet log file for more details");
        return msg.toString();
    }
}
