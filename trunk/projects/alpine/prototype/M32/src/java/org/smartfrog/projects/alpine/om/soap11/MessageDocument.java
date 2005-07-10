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
import org.smartfrog.projects.alpine.xmlutils.NodeIterator;
import org.smartfrog.projects.alpine.interfaces.ValidateXml;
import org.smartfrog.projects.alpine.faults.InvalidXmlException;

import java.util.HashMap;
import java.util.Enumeration;


/**
 * a message
 */
public class MessageDocument extends Document implements ValidateXml {
    public static final String ERROR_EMPTY_DOCUMENT = "Empty";

    private HashMap<String,String> mimeHeaders=new HashMap<String, String>();
    
    public MessageDocument(Element element) {
        super(element);
    }

    public MessageDocument(Document document) {
        super(document);
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
     * @return
     */ 
    public Body getBody() {
        return getEnvelope().getBody();
    }
    
    /**
     * Are we a fault. 
     * precondition: body!=null;
     * @return
     */ 
    public boolean isFault() {
        Body body= getBody();
        return body.isFault();
    }

    
    
    /**
     * Validate the Xml. Throw {@link InvalidXmlException} if invalid.
     */
    public void validateXml() {
        if(getRootElement()==null) {
            throw new InvalidXmlException(ERROR_EMPTY_DOCUMENT);
        }
        if(!(getRootElement() instanceof Envelope)) {
            throw new InvalidXmlException(Soap11Constants.FAULTCODE_VERSION_MISMATCH);
        }
        Envelope env=(Envelope) getRootElement();
        env.validateXml();
    }
    
    public static MessageDocument create() {
        return new MessageDocument(new Element("root",
                "http://www.xom.nu/fakeRoot"));
    }
    
    public void putMimeHeader(String name,String value) {
        mimeHeaders.put(name,value);
    }
    
    
    public String getMimeHeader(String name) {
        return mimeHeaders.get(name);
    }

    public HashMap<String, String> getMimeHeaders() {
        return mimeHeaders;
    }
}
