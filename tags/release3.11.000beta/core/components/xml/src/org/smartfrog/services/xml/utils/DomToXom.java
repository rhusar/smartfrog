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
package org.smartfrog.services.xml.utils;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.converters.DOMConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Convert from Dom to Xom, using a builder of choice.
 * The built in Xom utility doesnt let you spec a builder, so you dont
 * get a custom class hierarchy.
 * created 25-Nov-2005 16:50:16
 */

public class DomToXom {

    /**
     * builder class
     */
    private Builder builder;

    public DomToXom(Builder builder) {
        this.builder = builder;
    }

    /**
     * convert a DOM to a byte array
     * @param dom dom to serialize
     * @return the byte array equivalent
     * @throws ParsingException
     */
    public byte[] convertToBytes(org.w3c.dom.Document dom) throws ParsingException {
        Document xom = DOMConverter.convert(dom);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Serializer ser = new Serializer(out);
        try {
            ser.write(xom);
            ser.flush();
            return out.toByteArray();
        } catch (IOException e) {
            //too unlikely for an internal thing
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert a Dom doc to a Xom doc, bu turning it into bytes and then back into Xom.
     * @param dom dom document to read
     * @return a parsed Xom document
     * @throws ParsingException if the doc doesnt parse
     * @throws RuntimeException for IO problems (very hard to do, given we are reading a string buffer)
     */
    public Document convert(org.w3c.dom.Document dom) throws ParsingException {
        try {
            byte bytes[]=convertToBytes(dom);
            ByteArrayInputStream r=new ByteArrayInputStream(bytes);
            Document result = builder.build(r);
            return result;
        } catch (IOException e) {
            //too unlikely for an internal thing
            throw new RuntimeException(e);
        }
    }

}
