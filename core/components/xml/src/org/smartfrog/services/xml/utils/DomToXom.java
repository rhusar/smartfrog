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

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

/**
 * created 25-Nov-2005 16:50:16
 */

public class DomToXom {

    /**
     * builder class
     */
    private Builder builder;

    /**
     * How to look for a dom3 parser
     * {@value}
     */
    private static final String DOM3 = "XML 3.0";
    private static final String UTF8 = "UTF-8";

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
            String rawdoc = out.toString(UTF8);
            return out.toByteArray();
        } catch (IOException e) {
            //too unlikely for an internal thing
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a Dom3 impl
     * @return the implementation
     * @throws RuntimeException if things go wrong
     */
    public static DOMImplementation getDom3Implementation() {
        try {
            // get an instance of the DOMImplementation registry
            DOMImplementationRegistry registry =
                    DOMImplementationRegistry.newInstance();
            // get a DOM implementation the Level 3 XML module
            DOMImplementation domImpl =
                    registry.getDOMImplementation(DOM3);
            return domImpl;
        } catch (Exception e) {
            RuntimeException rte;
            if(!(e instanceof RuntimeException)) {
                rte=new RuntimeException(e);
            } else {
                rte=(RuntimeException) e;
            }
            throw rte;
        }
    }

    /**
     * Convert a Dom doc to a Xom doc
     * @param dom
     * @return
     * @throws ParsingException
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

    /**
     * Convert from a Xom document to a W3C Dom Document
     * @param xom
     * @return the Dom equivalent
     * @throws RuntimeException for dom instantiation problems
     */
    public static org.w3c.dom.Document fromXom(Document xom) {
        DOMImplementation domImpl=getDom3Implementation();
        return DOMConverter.convert(xom,domImpl);
    }
}
