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
package org.smartfrog.services.xml.impl;

import nu.xom.Document;

import java.io.IOException;
import java.io.Serializable;

import org.smartfrog.services.xml.interfaces.XmlWireCodec;
import org.smartfrog.services.xml.impl.codecs.XmlTextCodec;

/**
 * switchable codec
 */

public class SerializedXomDocument implements Serializable {

    XmlWireCodec codec=new XmlTextCodec();

    public SerializedXomDocument() {
    }

    /**
     * create a node with a document
     *
     * @param document
     */
    public SerializedXomDocument(Document document) {
        this.document = document;
    }

    /**
     * this is our document
     */
    private transient Document document;

    /**
     * write out an object
     *
     * @param out
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws
            IOException {
        codec.writeObject(document,out);
    }

    /**
     * read in an object
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws
            IOException, ClassNotFoundException {
        document = codec.readObject(in);
    }

    /**
     * get the underlying document
     *
     * @return
     */
    public Document getDocument() {
        return document;
    }

    /**
     * set the underlying document
     *
     * @param document
     */
    public void setDocument(Document document) {
        this.document = document;
    }

}
