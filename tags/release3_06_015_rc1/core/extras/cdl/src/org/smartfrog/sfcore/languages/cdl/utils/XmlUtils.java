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
package org.smartfrog.sfcore.languages.cdl.utils;

import org.ggf.cddlm.utils.QualifiedName;

import javax.xml.namespace.QName;

import nu.xom.Element;

/**
 * Minor XML utility stuff, unique to CDL created 13-Jun-2005 14:51:30
 */

public final class XmlUtils {

    private XmlUtils() {
    }

    /**
     * Convert from the GGF QualifiedName to a Javax QName type.
     *
     * @param src
     * @return a QName that matches the source QualifiedName.
     */
    public static QName makeQName(QualifiedName src) {
        return makeQName(src.getNamespaceURI(),
                src.getLocalPart(),
                src.getPrefix());
    }

    /**
     * Convert from a string triple. Prefix may be null.
     *
     * @param namespace namespace
     * @param local     local name
     * @param prefix    optional prefix
     * @return a QName that matches the source QualifiedName.
     */
    public static QName makeQName(String namespace,
            String local,
            String prefix) {
        QName dest;
        if (prefix != null) {
            dest = new QName(namespace,
                    local,
                    prefix);

        } else {
            dest = new QName(namespace, local);
        }
        return dest;
    }

    /**
     * map from, say tns:something to 'tns'
     *
     * @param string
     * @return null for no namespace
     */
    public static String extractNamespacePrefix(String string) {
        int offset = string.indexOf(':');
        if (offset >= 0) {
            return string.substring(0, offset);
        } else {
            return null;
        }
    }

    /**
     * map from, say tns:something to 'something'
     *
     * @param string
     * @return everything following the : or the whole string if one is not
     *         there
     */
    public static String extractLocalname(String string) {
        int offset = string.indexOf(':');
        if (offset >= 0) {
            return string.substring(offset + 1);
        } else {
            return string;
        }
    }

    /**
     * Get the qname of an element
     * @param element
     * @return
     */
    public static QName makeQName(Element element) {
        return new QName(element.getNamespaceURI(),element.getLocalName());
    }
}
