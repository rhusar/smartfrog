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



import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.MessagingException;
import javax.activation.*;
import javax.mail.internet.*;
import javax.mail.internet.InternetAddress;
import java.util.Properties;
import java.util.Date;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 * EmailerImpl is a SmartFrog component which provides utility methods to send 
 * singlepart and multipart email messages.
 * This component can be deployed as any SmartFrog component and then used by
 * other components to send single and multi part email messages.
 * @author Ashish Awasthi
 */ 
public class EmailerImpl extends PrimImpl implements Emailer {
    private String toList; // comma separated list of email ids     
    private String ccList =""; // comma separated list, default is blank
    private String defaultFrom;
    private String host;     // SMTP host
    private String defaultSubject;
    private Session session = null;
    private final String PROP_HOST_NAME = "mail.smtp.host";

    /**
     * Constructs Emailer object.
     *
     * @throws RemoteException in case of network/rmi error
     */
    public EmailerImpl() throws RemoteException {
    }
    
    /**
     * Reads default attribites, creates a session for the SMTP server and
     * deploys the emailer component.
     *
     * @throws SmartFrogException in case of error in deploying or reading the 
     * attributes
     * @throws RemoteException in case of network/emi error
     */ 
    public synchronized void sfDeploy() throws SmartFrogException, 
                                                            RemoteException {
        //read SmartFrog Attributes
        readSFAttributes();
        Properties props = new Properties();
        props.put(PROP_HOST_NAME, host);
        session = Session.getInstance(props, null);
        super.sfDeploy();
    }
    
    /**
     * Life cycle method for terminating the SmartFrog component.
     * @param tr Termination record
     */ 
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        super.sfTerminateWith(tr);
    }
    
    /**
     * Reads the SmartFrog attributes
     */
    private void readSFAttributes() throws SmartFrogResolutionException,
                                                             RemoteException{
        toList = sfResolve(TO, toList, true); 
        ccList = sfResolve(CC, ccList, false); 
        defaultFrom = sfResolve(FROM, defaultFrom, false); 
        host = sfResolve(SMTP_HOST, host, true); 
        defaultSubject =  sfResolve(SUBJECT, defaultSubject, false);
    }

    // Utility methods to send Emails

    /**
     * Sends a single part message using to, from subject attributes defined in
     * the Emailer component
     * @param message Message body
     * @throws SmartFrogException if any error while sending the email
     * @throws RemoteException if any rmi or network error
     */
    public void sendEmail(String message) 
                            throws SmartFrogException, RemoteException {
        //use default to, from and subject
        sendEmail(toList, ccList, defaultFrom, defaultSubject, message);
    }
    
    /**
     * Sends a single part message using to, from attributes defined in
     * the Emailer component
     * @param subject The subject text that overrides the default value
     * @param message Message body
     * @throws SmartFrogException if any error while sending the email
     * @throws RemoteException if any rmi or network error
     */
    public void sendEmail(String subject, String message) 
                            throws SmartFrogException, RemoteException {
        //use default to and from email addresses
        sendEmail(toList, ccList, defaultFrom, subject, message);
    }

    /**
     * Sends a single part message.
     * @param to comma separated list of email ids
     * @param cc  comma separated list of email ids
     * @param from from address
     * @param subject The subject text that overrides the default value
     * @param message Message body
     * @throws SmartFrogException if any error while sending the email
     * @throws RemoteException if any rmi or network error
     */
    public void sendEmail(String to ,String cc, 
                        String from, String subject, String message) 
                                throws SmartFrogException, RemoteException {
       try {
           Message msg = constructSinglePartMessage(to, cc,from, subject, 
                                                                    message);
           sendMessage(msg); 
       }catch (MessagingException mex) {
            throw new SmartFrogException(mex);
       }
    }

    
    
    /**
     * Sends an email message
     * @param mailMessage Email Message
     * @throws MessagingException if unable to send the email message
     */
    private void sendMessage(Message mailMessage) throws MessagingException{
        Transport.send(mailMessage);
    }
    
    /**
     * Constructs single part email message.
     * @return Mail message
     */
    private Message constructSinglePartMessage(String to, String cc, 
            String from, String subject, String msgText) 
                                                throws MessagingException{
        if (( to == null) || to.equals("")) {
            throw new MessagingException("To address list can not be null");
        }
        if (( from == null) || from.equals("")) {
            throw new MessagingException("From address can not be null");
        }
            
	    Message msg = new MimeMessage(session);
        
	    msg.setFrom(new InternetAddress(from));
        
	    InternetAddress[] toAddress = InternetAddress.parse(toList);
	    msg.setRecipients(Message.RecipientType.TO, toAddress);

        if ((cc != null) && (!cc.equals(""))) {
            InternetAddress[] ccAddress = InternetAddress.parse(cc);
            msg.setRecipients(Message.RecipientType.CC, ccAddress);
        }
	    msg.setSubject(subject);
	    msg.setSentDate(new Date());
	    msg.setText(msgText);
        return msg;
    }
}
