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

import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.Envelope;
import org.smartfrog.projects.alpine.om.soap11.Body;
import org.smartfrog.projects.alpine.om.soap11.SoapMessageParser;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap12.Soap12Constants;
import org.smartfrog.projects.alpine.xmlutils.ResourceLoader;
import org.xml.sax.SAXException;

/**
 * This represents a message in the system. 
 */
public class MessageContext extends Context {

    private ResourceLoader loader=new ResourceLoader();

    /**
     * should responses be validated on the way out?
     */
    private boolean validateResponses;

    /**
     * actor. This is used in generating faults
     */

    private String role = Soap12Constants.ROLE_ULTIMATE_RECEIVER;

    /**
     * incoming request
     */
    private MessageDocument request;

    /**
     * outgoing response
     */
    private MessageDocument response;

    public MessageDocument getRequest() {
        return request;
    }

    public void setRequest(MessageDocument request) {
        this.request = request;
    }

    public MessageDocument getResponse() {
        return response;
    }

    public void setResponse(MessageDocument response) {
        this.response = response;
    }

    /**
     * Create a new request and return it, as well as saving
     * it in the <code>request</code> attribute.
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
     * @return the newly created message
     */
    public MessageDocument createResponse() {
        SoapElement envelope = createMessage();
        response = new MessageDocument(envelope);
        return response;
    }

    /**
     * Create a message with stub header and body elements
     * @return
     */
    protected Envelope createMessage() {
        Envelope envelope=new Envelope();
        envelope.appendChild(new Body());
        envelope.getHeader();
        return envelope;
    }

    /**
     * Create a new parser
     * @return
     * @throws SAXException
     */
    public SoapMessageParser createParser() throws SAXException {
        return new SoapMessageParser(loader, validateResponses);
    }

    /**
     * Get the fault actor
     * @return
     */
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
