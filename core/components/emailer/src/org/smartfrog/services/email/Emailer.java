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

package org.smartfrog.services.email;

import java.rmi.RemoteException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * Emailer Interface. 
 * @author Ashish Awasthi
 */ 
public interface Emailer extends Prim {
    
    //SmartFrog attributes for the emailer component
    public static String TO = "to";
    public static String CC = "cc";
    public static String FROM = "from";
    public static String SUBJECT = "subject";
    public static String MESSAGE = "message";
    public static String ATTACHMENTS = "attachments";
    public static String SMTP_HOST = "smtpHost";
    public static String CHARSET = "charset";
    public static String RUNASWFCOMPONENT = "runAsWorkFlowComponent";
    public static String SEND_ON_STARTUP = "sendOnStartup";
    public static String SEND_ON_SHUTDOWN = "sendOnShutdown";

     /**
     * Sends a single part message using to, from subject attributes defined in
     * the Emailer component
     * @param message Message body
     * @throws SmartFrogException if any error while sending the email
     * @throws RemoteException if any rmi or network error
     */
    public void sendEmail(String message) 
                                throws SmartFrogException, RemoteException;
    /**
     * Sends a single part message using to, from attributes defined in
     * the Emailer component
     * @param subject The subject text that overrides the default value
     * @param message Message body
     * @throws SmartFrogException if any error while sending the email
     * @throws RemoteException if any rmi or network error
     */
    public void sendEmail(String subject, String message) 
                                throws SmartFrogException, RemoteException;
    /**
     * Sends a single part message.
     * @param to comma separated list of email ids
     * @param cc comma separated list of email ids
     * @param from from address
     * @param subject The subject text that overrides the default value
     * @param message Message body
     * @throws SmartFrogException if any error while sending the email
     * @throws RemoteException if any rmi or network error
     */
    public void sendEmail(String to, String cc, String from, 
        String subject, String message)
                                throws SmartFrogException, RemoteException;

    /**
     * send the message we are configured to send.
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void sendConfiguredMessage() throws SmartFrogException, RemoteException;
}
