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

import nu.xom.Element;
import org.smartfrog.projects.alpine.om.base.SoapElement;

import javax.xml.namespace.QName;

/**
 * This is the base class of of all Soap11 elements
 */
public class Soap11Element extends SoapElement {

    public Soap11Element(QName name) {
        super(name);
    }
    
    public Soap11Element(String name) {
        super(name);
    }

    public Soap11Element(String name, String uri) {
        super(name, uri);
    }

    public Soap11Element(Element element) {
        super(element);
    }

    /**
     * Get the root document as a message document
     * @return the message document
     */ 
    public MessageDocument getMessageDocument() {
        return (MessageDocument) getDocument();
    }
    
    /**
     * Get the envelope
     * @return the envelope in the message
     */ 
    public Envelope getEnvelope() {
        return getMessageDocument().getEnvelope();
    }


}
