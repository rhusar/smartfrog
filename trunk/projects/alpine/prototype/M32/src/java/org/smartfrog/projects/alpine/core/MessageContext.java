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
import org.smartfrog.projects.alpine.om.base.ElementEx;
import nu.xom.Element;

/**
 * This represents a message in the system. 
 */
public class MessageContext extends Context {
    
    private MessageDocument request;
    
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

    public MessageDocument createRequest() {
        ElementEx envelope = createMessage();
        request = new MessageDocument(envelope);
        return request;
    }    
    public MessageDocument createResponse() {
        ElementEx envelope = createMessage();
        response = new MessageDocument(envelope);
        return response;
    }

    protected ElementEx createMessage() {
        ElementEx envelope=new Envelope();
        envelope.appendChild(new Body());
        return envelope;
    }


}
