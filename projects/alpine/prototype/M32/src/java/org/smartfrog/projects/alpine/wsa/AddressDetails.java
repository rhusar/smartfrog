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

import nu.xom.Element;
import org.smartfrog.projects.alpine.interfaces.Validatable;
import org.smartfrog.projects.alpine.faults.ValidationException;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is all the extra stuff for WS-A addressing of SOAP
 * created 23-Mar-2006 13:39:59
 * <p/>
 * <wsa:To>xs:anyURI</wsa:To> ?
 * <wsa:From>wsa:EndpointReferenceType</wsa:From> ?
 * <wsa:ReplyTo>wsa:EndpointReferenceType</wsa:ReplyTo> ?
 * <wsa:FaultTo>wsa:EndpointReferenceType</wsa:FaultTo> ?
 * <wsa:Action>xs:anyURI</wsa:Action>
 * <wsa:MessageID>xs:anyURI</wsa:MessageID> ?
 * <wsa:RelatesTo RelationshipType="xs:anyURI"?>xs:anyURI</wsa:RelatesTo> *
 * <wsa:ReferenceParameters>xs:any*</wsa:ReferenceParameters> ?
 */

public class AddressDetails implements Validatable, AddressingConstants {

    private static Log log = LogFactory.getLog(MessageDocument.class);

    private AlpineEPR to;

    private String action;


    private AlpineEPR from;

    private AlpineEPR replyTo;

    private AlpineEPR faultTo;

    private String messageID;

    private String relatesTo;

    private Element referenceParameters;

    public AddressDetails() {
    }

    public AddressDetails(MessageDocument message, String namespace) {
        read(message, namespace);
    }

    /**
     * Deep copy constructor
     *
     * @param that
     */
    public AddressDetails(AddressDetails that) {

        action = that.action;
        messageID = that.messageID;
        relatesTo = that.relatesTo;


        if (that.to != null) {
            to = that.to.clone();
        }
        if (that.from != null) {
            from = that.from.clone();
        }
        if (that.replyTo != null) {
            replyTo = that.replyTo.clone();
        }
        if (that.faultTo != null) {
            faultTo = that.faultTo.clone();
        }
        if (referenceParameters != null) {
            referenceParameters = (Element) that.referenceParameters.copy();
        }

    }

    public AlpineEPR getTo() {
        return to;
    }

    public void setTo(AlpineEPR to) {
        this.to = to;
    }

    public AlpineEPR getFrom() {
        return from;
    }

    public void setFrom(AlpineEPR from) {
        this.from = from;
    }

    public AlpineEPR getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(AlpineEPR replyTo) {
        this.replyTo = replyTo;
    }

    public AlpineEPR getFaultTo() {
        return faultTo;
    }

    public void setFaultTo(AlpineEPR faultTo) {
        this.faultTo = faultTo;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getRelatesTo() {
        return relatesTo;
    }

    public void setRelatesTo(String relatesTo) {
        this.relatesTo = relatesTo;
    }

    public Element getReferenceParameters() {
        return referenceParameters;
    }

    public void setReferenceParameters(Element referenceParameters) {
        this.referenceParameters = referenceParameters;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * validate an instance.
     * Return if the object is valid, thrown an exception if not.
     * It is imperative that this call has <i>No side effects</i>.
     * <p/>
     * Why is this a boolean? For insertion into assert ; statements.
     *
     * @return true unless an exception is thrown
     * @throws org.smartfrog.projects.alpine.faults.ValidationException
     *          with text if not valid
     */
    public boolean validate() throws ValidationException {
        if (getAction() == null || getAction().length() == 0) {
            throw new ValidationException("Missing or empty " + WSA_ACTION + " attribute");
        }
        if(to!=null) {
            to.validate();
        }
        if(from!=null) {
            from.validate();
        }
        if(replyTo!=null) {
            replyTo.validate();
        }
        if(faultTo!=null) {
            faultTo.validate();
        }

        return true;
    }

    /**
     * read everything from the document. After this is done, the extracted elements are
     * copied; they are not live. Changes in the values are not reflected in the message contents
     *
     * @param message
     */
    public void read(MessageDocument message, String namespace) {
        for (Element header : message.getEnvelope().getHeaders()) {
            if (!namespace.equals(header.getNamespaceURI())) {
                continue;
            }
            String localname = header.getLocalName();
            String text = header.getValue();
            if (WSA_TO.equals(localname)) {
                to = new AlpineEPR(header, namespace);
                //extract To:
            } else if (WSA_MESSAGEID.equals(localname)) {
                messageID = text;
            } else if (WSA_ACTION.equals(localname)) {
                action = text;
            } else if (WSA_FROM.equals(localname)) {
                from = new AlpineEPR(header, namespace);
            } else if (WSA_REPLYTO.equals(localname)) {
                replyTo = new AlpineEPR(header, namespace);
            } else if (WSA_FAULTTO.equals(localname)) {
                faultTo = new AlpineEPR(header, namespace);
            } else if (WSA_REFERENCE_PARAMETERS.equals(localname)){
                referenceParameters=(Element) header.copy();
            } else if (WSA_RELATES_TO.equals(localname)) {
                //TODO: RelatesTo, as and when needed
                log.warn("Not yet implemented "+header);
            }
        }
        ;


    }

    /*
            Element action = new ElementEx(prefixColon +WSA_ACTION, namespace);
        Header.setMustUnderstand(action, mustUnderstand);
        action.appendChild(getAction());

        if(action!=null) {
            ElementEx actionElt = new ElementEx(prefixColon + WSA_ADDRESS, namespace, getAction());
            root.appendChild(actionElt);
        }

    */
}
