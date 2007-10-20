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
package org.smartfrog.sfcore.languages.cdl.dom.attributes;

import nu.xom.Attribute;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Attribute for URIs created 13-Jun-2005 15:08:34
 */

public class URIAttribute extends GenericAttribute {

    private URI uri;


    /**
     * simple constructor
     */
    public URIAttribute() {
    }

    /**
     * bind at construct time. calls {@link #bind(Attribute)}
     *
     * @param attribute
     */
    public URIAttribute(Attribute attribute) throws CdlXmlParsingException {
        super(attribute);
    }

    /**
     * Get the URI
     *
     * @return
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Set the URI
     *
     * @param uri
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * bind to an attribute. Sets the attribute value subclasses can extend to
     * extract more information; always call the parent
     *
     * @param attr
     */
    public void bind(Attribute attr) throws CdlXmlParsingException {
        super.bind(attr);
        try {
            uri = new URI(getValue());
        } catch (URISyntaxException e) {
            throw new CdlXmlParsingException("Not a URI: " + getValue(), e);
        }

    }

}
