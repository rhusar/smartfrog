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
package org.smartfrog.services.xml.impl.codecs;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import org.smartfrog.services.xml.interfaces.XmlWireCodec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * created 27-Jul-2005 13:39:41
 */

public class XmlTextCodec implements XmlWireCodec {
    public static final String ENCODING = "utf-8";
    public static final String ERROR_NO_DESER = "failed to deserialize doc";


    /**
     * Read a document from the input stream
     *
     * @param in input stream
     * @return an XML document
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Document readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();

        byte[] data = new byte[in.readInt()];
        in.readFully(data, 0, data.length);
        Document document;
        Builder builder=new Builder();
        try {
            document = builder.build(in);
        } catch (ParsingException e) {
            //throw this as a runtime as IOE still doesnt support chaining
            return convert(e);
        }
        return document;
    }

    private Document convert(Throwable t) throws IOException {
        IOException ioException = new IOException(ERROR_NO_DESER);
        ioException.initCause(t);
        throw ioException;
    }

    /**
     * Write a document
     *
     * @param document document to write
     * @param out      output stream
     * @throws IOException for output problems
     */
    public void writeObject(Document document, ObjectOutputStream out)
            throws IOException {
        out.defaultWriteObject();
        Serializer serializer = new Serializer(out,ENCODING);
        serializer.write(document);
        serializer.flush();
    }
}
