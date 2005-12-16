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

import nu.xom.Element;
import nu.xom.Document;
import nu.xom.Serializer;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * Generic static Xom utils; all java1.4+
 * The class has a protected constructor to allow direct subclasses
 */

public class XomUtils {

    protected XomUtils() {
    }

    /**
     * turn a qname into an element of the same name
     *
     * @param qname
     * @return
     */
    public static Element element(QName qname) {
        Element element = new Element(qname.getLocalPart(),
                qname.getNamespaceURI());
        element.setNamespacePrefix(qname.getPrefix());
        return element;

    }

    /**
     * Save Xom to a buffer
     * @param document
     * @return
     * @throws java.io.IOException
     */
    public static byte[] xomToBuffer(Document document) throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        Serializer serializer=new Serializer(out);
        serializer.write(document);
        serializer.flush();
        out.close();
        return out.toByteArray();
    }

    /**
     * detatch the root element from the doc, so it
     * can be used elsewhere. The doc is left with a dummy element
     * to avoid it being malformed.
     * @param document
     * @return element that was the root
     */
    public static Element detachRootElement(Document document) {
        Element rootElement = document.getRootElement();
        document.setRootElement(new Element("dummy"));
        return rootElement;
    }
}
