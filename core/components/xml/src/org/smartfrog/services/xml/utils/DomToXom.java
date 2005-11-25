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

import nu.xom.NodeFactory;
import nu.xom.Document;
import nu.xom.Builder;
import nu.xom.Serializer;
import nu.xom.ValidityException;
import nu.xom.ParsingException;
import nu.xom.converters.DOMConverter;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.ByteArrayInputStream;

/**
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

    public byte[] convertToBytes(org.w3c.dom.Document dom) throws ParsingException {
        Document xom = DOMConverter.convert(dom);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Serializer ser = new Serializer(out);
        try {
            ser.write(xom);
            ser.flush();
            String rawdoc = out.toString("UTF-8");
            return out.toByteArray();
        } catch (IOException e) {
            //too unlikely for an internal thing
            throw new RuntimeException(e);
        }
    }

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
