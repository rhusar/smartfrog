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
package org.smartfrog.sfcore.languages.cdl.importing;

import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;

import java.net.URL;


/**
 * Represents an imported document. created 08-Jun-2005 13:39:12
 */

public class ImportedDocument /*extends CdlDocument */ {

    private String namespace;

    private String location;

    private URL url;

    /**
     * the imported document itself
     */
    private CdlDocument document;

    /**
     * Get the namespace of a document (may be null)
     *
     * @return namespace or null
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * get the location of a document
     *
     * @return the documents location
     */
    public String getLocation() {
        return location;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public CdlDocument getDocument() {
        return document;
    }

    public void setDocument(CdlDocument document) {
        this.document = document;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * equality test is on namespace only
     *
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImportedDocument)) {
            return false;
        }

        final ImportedDocument importedDocument = (ImportedDocument) o;

        if (namespace != null ?
                !namespace.equals(importedDocument.namespace) :
                importedDocument.namespace != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (namespace != null ? namespace.hashCode() : 0);
    }
}
