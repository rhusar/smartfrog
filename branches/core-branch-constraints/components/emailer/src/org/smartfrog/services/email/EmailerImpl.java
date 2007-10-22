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
import javax.mail.Multipart;
import javax.mail.MessagingException;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import java.util.Date;
import java.util.Vector;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;

/**
 * Email component for smartfrog.
 * 
 * Emailer component can be used in two modes.
 * 1. As a standard workflow component ( Sends email only once when sfStart
 *    lifecycle method is executed. In this mode other components in the work-
 *    flow may not be aware of Emailer component. Can be useful in applications
 *    installations using SF, where sequence workflow installs the
 *    application and emailer sends the log file of the installation through
 *    email. It terminates itself after sending the mail. Please see 
 *    exampleUsageAsWFComp.sf for sample usage.
 * 2. As a Utility component, where sendEmail API can be invoked multiple times 
 *    by other components deployed under the same parent. In this mode the 
 *    component has to provide email attributes at run-time. 
 *    In this mode it does not terminate itself but stays deployed so that
 *    other component could use email utility. 
 *    Please see example.sf for sample usage.     
 *
 * @author Ashish Awasthi
 */ 
public class EmailerImpl extends PrimImpl implements Emailer {
    private String toList; // comma separated list of email ids     
    private String ccList =""; // comma separated list, default is blank
    private String from;
    private String host;     // SMTP host
    private String subject;
    private Vector attachmentList = null; // attachments file name
    private boolean runAsWorkFlowComponent = true; // by default
    private Session session = null;
    private String message = "SmartFrog Message";
    private boolean sendOnStartup=false;
    private boolean sendOnShutdown = false;

    private static final String PROP_HOST_NAME = "mail.smtp.host";

    /**
     * our log
     */
    private Log log;

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
        log = LogFactory.getOwnerLog(this, this);
        //read SmartFrog Attributes
        readSFAttributes();
        Properties props = new Properties();
        props.put(PROP_HOST_NAME, host);
        session = Session.getInstance(props, null);
        //then parent
        super.sfDeploy();
    }
    
    /**
     * Sends email using attributes specified in the SmartFrog description
     * of the component.
     *
     * @throws SmartFrogException in case of error in sending email
     * @throws RemoteException in case of network/emi error
     */ 
    public synchronized void sfStart() throws SmartFrogException, 
                                                          RemoteException {
        //start parent
        super.sfStart();
        // send mail only when it is part of workflow and user has set
        // boolean attribute "runAsWorkFlowComponent"                   
        if(runAsWorkFlowComponent || sendOnStartup ) {
            sendConfiguredMessage();
        }
        if(runAsWorkFlowComponent  ) {
            TerminationRecord termR = new TerminationRecord("normal",
                "Emailer finished: ",sfCompleteName());
            TerminatorThread terminator = new TerminatorThread(this,termR);
            terminator.start();
        }
    }

    /**
     * send the message we are configured to send.
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void sendConfiguredMessage() throws SmartFrogException, RemoteException {
        if(attachmentList == null)
            sendEmail(toList ,ccList, from, subject, message);
        else {
            sendEmailWithAttachments(toList ,ccList, from, subject,
                                     message, attachmentList);
        }
    }

    /**
     * Life cycle method for terminating the SmartFrog component.
     * @param tr Termination record
     */ 
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        if(sendOnShutdown) {
            //send a shutdown message
            try {
                sendConfiguredMessage();
            } catch (SmartFrogException e) {
                log.error(e);
            } catch (RemoteException e) {
                log.error(e);
            }
        }
        super.sfTerminateWith(tr);
    }
    
    /**
     * Reads SmartFrog attributes.
     * @throws SmartFrogResolutionException if failed to read any 
     * attribute or a mandatory attribute is not defined.
     * @throws RemoteException in case of network/rmi error
     */
    private void readSFAttributes() throws SmartFrogResolutionException,
                                                             RemoteException{
        // mandatory attributes
        toList = sfResolve(TO, toList, true); 
        runAsWorkFlowComponent = sfResolve(RUNASWFCOMPONENT, 
                                        runAsWorkFlowComponent, true);
        host = sfResolve(SMTP_HOST, host, true); 
        
        // optional attributes
        ccList = sfResolve(CC, ccList, false); 
        from = sfResolve(FROM, from, false); 
        subject =  sfResolve(SUBJECT, subject, false);
        message =  sfResolve(MESSAGE, message, false);
        attachmentList = sfResolve(ATTACHMENTS, attachmentList,false);
        sendOnStartup= sfResolve(SEND_ON_STARTUP, sendOnStartup,false);
        sendOnShutdown = sfResolve(SEND_ON_SHUTDOWN, sendOnShutdown, false);    }

    // Utility methods to send Emails used when Emailer is to be used multiple
    // times by other components.

    /**
     * Sends a single part message using to, from subject attributes defined in
     * the Emailer component
     * @param text Message body
     * @throws SmartFrogException if any error while sending the email
     * @throws RemoteException if any rmi or network error
     */
    public void sendEmail(String text)
                            throws SmartFrogException, RemoteException {
        //use default to, from and subject
        sendEmail(toList, ccList, from, subject, text);
    }
    
    /**
     * Sends a single part message using to, from attributes defined in
     * the Emailer component
     * @param messageSubject The subject text that overrides the default value
     * @param text Message body
     * @throws SmartFrogException if any error while sending the email
     * @throws RemoteException if any rmi or network error
     */
    public void sendEmail(String messageSubject, String text)
                            throws SmartFrogException, RemoteException {
        //use default to and from email addresses
        sendEmail(toList, ccList, from, messageSubject, text);
    }

    /**
     * Sends a single part message.
     * @param to comma separated list of email ids
     * @param cc  comma separated list of email ids
     * @param messageFrom from address
     * @param messageSubject The subject text that overrides the default value
     * @param text Message body
     * @throws SmartFrogException if any error while sending the email
     * @throws RemoteException if any rmi or network error
     */
    public void sendEmail(String to ,String cc, 
                        String messageFrom, String messageSubject, String text)
            throws SmartFrogException, RemoteException {
        try {
            if ( log.isInfoEnabled() ) {
                log.info("Sending email to " + to + " cc: +" + cc + " from: " + messageFrom);
                log.info("Subject :" + messageSubject);
                log.info("Message:" + text);
            }
            Message emailMsg = constructSinglepartMessage(to, cc, messageFrom, messageSubject, text);
            sendMessage(emailMsg);
        } catch (MessagingException mex) {
            log.error("failed to send message", mex);
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
     * @param to
     * @param cc
     * @param messageFrom
     * @param messageSubject
     * @param text
     * @return the message to send
     * @throws MessagingException
     */
    private Message constructSinglepartMessage(String to, String cc, 
            String messageFrom, String messageSubject, String text)
                                                throws MessagingException{
        validateHeaders(to, messageFrom);
            
	    Message msg = new MimeMessage(session);
        
	    msg.setFrom(new InternetAddress(messageFrom));
        
	    InternetAddress[] toAddress = InternetAddress.parse(toList);
	    msg.setRecipients(Message.RecipientType.TO, toAddress);

        if (cc != null && cc.length() != 0) {
            InternetAddress[] ccAddress = InternetAddress.parse(cc);
            msg.setRecipients(Message.RecipientType.CC, ccAddress);
        }
	    msg.setSubject(messageSubject);
	    msg.setSentDate(new Date());
	    msg.setText(text);
        return msg;
    }

    private void validateHeaders(String to, String messageFrom) throws MessagingException {
        if (( to == null) || to.length()==0) {
            throw new MessagingException("To address list can not be empty");
        }
        if (( messageFrom == null) || messageFrom.length() == 0) {
            throw new MessagingException("From address can not be empty");
        }
    }

    /**
     * Sends Email with the attachment.
     * @param to to addresses
     * @param cc cc addresses
     * @param messageFrom from address
     * @param messageSubject the subject
     * @param text the message body
     * @param attachments vector of attachments 
     * @throws SmartFrogException if unable to send email
     */
    public void sendEmailWithAttachments(String to, String cc, String messageFrom,
                String messageSubject, String text, Vector attachments)
                    throws SmartFrogException {
        try {
            if ( log.isInfoEnabled() ) {
                log.info("Sending email to " + to + " cc: +" + cc + " from: " + messageFrom);
                log.info("Subject :" + messageSubject);
                log.info("Message:" + text);
            }

            Message emailMsg = constructMultipartMessage(to, cc, messageFrom, messageSubject,
                    text, attachments);
            sendMessage(emailMsg);
        }catch (MessagingException mex) {
            log.error("failed to send message", mex);
            throw new SmartFrogException(mex);
        }
    }
    
    /**
     * Constructs Multipart email message.
     * @param to
     * @param cc
     * @param messageFrom
     * @param messageSubject
     * @param text
     * @param attachments
     * @return the message
     * @throws MessagingException
     */
    private Message constructMultipartMessage(String to, String cc, 
            String messageFrom, String messageSubject, String text, Vector attachments)
                                                throws MessagingException{
        validateHeaders(to, messageFrom);
	    Message msg = new MimeMessage(session);
        
	    msg.setFrom(new InternetAddress(messageFrom));
        
	    InternetAddress[] toAddress = InternetAddress.parse(toList);
	    msg.setRecipients(Message.RecipientType.TO, toAddress);

        if (cc != null && cc.length()!=0) {
            InternetAddress[] ccAddress = InternetAddress.parse(cc);
            msg.setRecipients(Message.RecipientType.CC, ccAddress);
        }
	    msg.setSubject(messageSubject);
	    msg.setSentDate(new Date());

   	    // create the Multipart and add its parts to it
	    Multipart mp = new MimeMultipart();

   	    // create and fill the first message part
	    MimeBodyPart mbp1 = new MimeBodyPart();
        
	    mbp1.setText(text);
        mp.addBodyPart(mbp1);
        
        // create and add MIME body parts for all the attachments
        for (int i = 0; i < attachments.size() ; i ++ ) {
            String fileName = (String) attachments.get(i);
            
            MimeBodyPart mbp = new MimeBodyPart();

            // attach the file to the message
            FileDataSource fds = new FileDataSource(fileName);
            mbp.setDataHandler(new DataHandler(fds));
            mbp.setFileName(fds.getName());
            mp.addBodyPart(mbp);
        }
	    // add the Multipart to the message
	    msg.setContent(mp);
        return msg;
    }
}
