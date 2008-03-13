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
package org.smartfrog.services.xml.interfaces;

import nu.xom.Document;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * created 27-Jul-2005 13:31:16
 */


public interface XmlWireCodec {



    /**
     * Read a document from the input stream
     * @param in input stream
     * @return an XML document
     * @throws IOException
     * @throws ClassNotFoundException
     */
    Document readObject(ObjectInputStream in) throws
            IOException, ClassNotFoundException;

    /**
     * Write a document
     * @param document document to write
     * @param out output stream
     * @throws IOException
     */
    void writeObject(Document document,ObjectOutputStream out) throws
            IOException;
}
