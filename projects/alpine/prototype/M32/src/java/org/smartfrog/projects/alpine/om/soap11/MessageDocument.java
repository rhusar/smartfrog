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

package org.smartfrog.projects.alpine.om.soap11;

import nu.xom.Document;
import nu.xom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.projects.alpine.faults.InvalidXmlException;
import org.smartfrog.projects.alpine.interfaces.ValidateXml;
import org.smartfrog.projects.alpine.om.base.Attachment;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.wsa.AddressDetails;
import org.smartfrog.projects.alpine.wsa.AddressingConstants;
import org.smartfrog.projects.alpine.xmlutils.NodeIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * a message
 */
public class MessageDocument extends Document implements ValidateXml {

    private static Log log = LogFactory.getLog(MessageDocument.class);

    public static final String ERROR_EMPTY_DOCUMENT = "Empty";

    private HashMap<String, String> mimeHeaders = new HashMap<String, String>();

    private AddressDetails addressDetails;

    private List<Attachment> attachments;

    public MessageDocument(Element element) {
        super(element);
    }

    public MessageDocument(MessageDocument document) {
        super(document);
    }

    /**
     * this extracts the address information from the document
     */
    public void bindAddressing() {
        addressDetails = new AddressDetails();
        if (!addressDetails.read(this, AddressingConstants.XMLNS_WSA_2005)
                && !addressDetails.read(this, AddressingConstants.XMLNS_WSA_2004)
                && !addressDetails.read(this, AddressingConstants.XMLNS_WSA_2003)) {
            log.info("No address details found");
        }
    }

    /**
     * Get the address details. This will demand create it if needed.
     *
     * @return the address details
     */
    public AddressDetails getAddressDetails() {
        if (addressDetails == null) {
            addressDetails = new AddressDetails();
        }
        return addressDetails;
    }

    public void setAddressDetails(AddressDetails addressDetails) {
        this.addressDetails = addressDetails;
    }


    /**
     * add a new attachment
     *
     * @param attachment the attachment
     */
    public void addAttachment(Attachment attachment) {
        getAttachments().add(attachment);
    }

    /**
     * Get the attachment list. This will demand create an empty list
     *
     * @return the attachment list
     */
    public List<Attachment> getAttachments() {
        if (attachments == null) {
            attachments = new ArrayList<Attachment>();
        }
        return attachments;
    }

    /**
     * Iterate just over elements
     *
     * @return an iterator
     */
    public NodeIterator nodes() {
        return new NodeIterator(this);
    }

    public Envelope getEnvelope() {
        return (Envelope) getRootElement();
    }

    /**
     * Get the body. Fails horribly if there is no envelope/body
     *
     * @return the envelope body
     */
    public Body getBody() {
        return getEnvelope().getBody();
    }

    /**
     * Get whatever is inside the body of a message.
     * You cannot make any assumptions about the type of these nodes, other than they
     * are Element or a derivative.
     *
     * @return the payload
     */
    public SoapElement getPayload() {
        return (SoapElement) getBody().getFirstChildElement();
    }

    /**
     * Are we a fault.
     * precondition: body!=null;
     *
     * @return true if there is a fault in the body
     */
    public boolean isFault() {
        Body body = getBody();
        return body.isFault();
    }

    /**
     * Get the fault of a message
     *
     * @return the fault or null if there is no such fault
     */
    public Fault getFault() {
        Body body = getBody();
        return (Fault) body.getFirstChildElement(SoapConstants.QNAME_FAULT);
    }


    /**
     * Validate the Xml. Throw {@link InvalidXmlException} if invalid.
     */
    public void validateXml() {
        Element root = getRootElement();
        if (root == null) {
            throw new InvalidXmlException(ERROR_EMPTY_DOCUMENT);
        }
        if (!(root instanceof Envelope)) {
            throw new InvalidXmlException(SoapConstants.FAULTCODE_VERSION_MISMATCH
                    +" : {"+ root.getNamespaceURI()+ "}#" + root.getQualifiedName());
        }
        Envelope env = (Envelope) root;
        env.validateXml();
    }

    public static MessageDocument create() {
        return new MessageDocument(new Element("root",
                "http://www.xom.nu/fakeRoot"));
    }

    public void putMimeHeader(String name, String value) {
        mimeHeaders.put(name, value);
    }


    public String getMimeHeader(String name) {
        return mimeHeaders.get(name);
    }

    public HashMap<String, String> getMimeHeaders() {
        return mimeHeaders;
    }

    /**
     * Returns a complete copy of this document. 
     *
     * @return a deep copy of this <code>Document</code> object
     */
    public MessageDocument copy() {
        return new MessageDocument(this);
    }

    protected Object clone() throws CloneNotSupportedException {
        Object clone = super.clone();

        return clone;
    }
}
