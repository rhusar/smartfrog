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

package org.smartfrog.projects.alpine.core;

import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.Body;
import org.smartfrog.projects.alpine.om.soap11.Envelope;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.SoapConstants;
import org.smartfrog.projects.alpine.om.soap11.SoapFactory;
import org.smartfrog.projects.alpine.om.soap11.SoapMessageParser;
import org.smartfrog.projects.alpine.om.soap12.Soap12Constants;
import org.smartfrog.projects.alpine.xmlutils.ResourceLoader;
import org.xml.sax.SAXException;

/**
 * This represents a message in the system.
 */
public class MessageContext extends Context {

    /**
     * Create witha  default role, and non-validating parser.
     */
    public MessageContext() {
    }

    /**
     * Create a parser in a known role
     *
     * @param role       role to use
     * @param validating whether to validate or not
     */
    public MessageContext(String role, boolean validating) {
        this.role = role;
        this.validating = validating;
    }

    private ResourceLoader loader = new ResourceLoader();

    /**
     * should messages be validated when parsing them.
     */
    private boolean validating = false;

    /**
     * actor. This is used in generating faults
     */

    private String role = Soap12Constants.ROLE_ULTIMATE_RECEIVER;

    /**
     * SOAP namespace. Defaults to SOAP12
     */
    private String soapNamespace = SoapConstants.URI_SOAPAPI;

    /**
     * incoming request
     */
    private MessageDocument request;

    /**
     * outgoing response
     */
    private MessageDocument response;

    /**
     * This is just a bit to say whether or not a message has been processed.
     * its there for the getCurrentMessage(), whifh
     */
    private boolean processed;

    public MessageDocument getRequest() {
        return request;
    }

    public void setRequest(MessageDocument request) {
        this.request = request;
        setSoapNamespace(request.getEnvelope().getNamespaceURI());
    }

    public MessageDocument getResponse() {
        return response;
    }

    public void setResponse(MessageDocument response) {
        this.response = response;
    }

    /**
     * Get the current message, be it request or response, depending upon the
     * processed flag.
     *
     * @return a message. May be null if the context state is not in a consistent position.
     */
    public MessageDocument getCurrentMessage() {
        return processed ? response : request;
    }

    public boolean isValidating() {
        return validating;
    }

    public void setValidating(boolean validating) {
        this.validating = validating;
    }


    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }


    public ResourceLoader getLoader() {
        return loader;
    }

    /**
     * Create a new request and return it, as well as saving
     * it in the <code>request</code> attribute.
     *
     * @return the newly created message
     */

    public MessageDocument createRequest() {
        SoapElement envelope = createMessage();
        request = new MessageDocument(envelope);
        return request;
    }

    /**
     * Create a new response and return it, as well as saving
     * it in the <code>response</code> attribute.
     *
     * @return the newly created message
     */
    public MessageDocument createResponse() {
        SoapElement envelope = createMessage();
        response = new MessageDocument(envelope);
        return response;
    }

    /**
     * Create a message with stub header and body elements
     *
     * @return a template message
     */
    protected Envelope createMessage() {
        Envelope envelope = new Envelope(SoapConstants.ELEMENT_ENVELOPE,
                getSoapNamespace());
        envelope.appendChild(new Body(SoapConstants.ELEMENT_BODY,
                getSoapNamespace()));
        envelope.getHeader();
        return envelope;
    }

    /**
     * Create a new parser
     *
     * @return a new parser
     * @throws SAXException
     */
    public SoapMessageParser createParser() throws SAXException {
        return new SoapMessageParser(loader, soapNamespace, validating, null);
    }

    /**
     * Get the fault actor
     *
     * @return what role this parser is operating in
     */
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSoapNamespace() {
        return soapNamespace;
    }

    public void setSoapNamespace(String soapNamespace) {
        this.soapNamespace = soapNamespace;
    }
}
