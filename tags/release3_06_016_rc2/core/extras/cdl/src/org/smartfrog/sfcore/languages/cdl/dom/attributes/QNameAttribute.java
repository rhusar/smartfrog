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
import nu.xom.Element;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

/**
 * created 26-May-2005 14:23:58
 */

public class QNameAttribute extends GenericAttribute {
    private QName qname;
    public static final String ERROR_UNKNOWN_PREFIX = "Unknown prefix ";
    public static final String ERROR_NO_LOCALNAME = "No localname in ";

    public QNameAttribute() {
    }

    public QNameAttribute(Attribute attribute) throws CdlXmlParsingException {
        super(attribute);
    }


    /**
     * crack up a prefix:localname string into a (prefix,localname) tuple and
     * then map the prefix to a URI by way of the parent node.
     *
     * @param value qname to parse
     * @return a QName. the namespace may be {@link XMLConstants.DEFAULT_NS_PREFIX},
     *         which means "default namespace".
     * @throws CdlXmlParsingException for parse failure. Error text may inclue
     *                                {@link #ERROR_NO_LOCALNAME} and {@link
     *                                #ERROR_UNKNOWN_PREFIX}
     */
    protected QName parseQname(String value) throws CdlXmlParsingException {
        String prefix;
        String localname;
        String namespace;
        Element parent = getParentElement();
        int prefixIndex = value.indexOf(':');
        if (prefixIndex < 0) {
            //could mean this is the default NS
            prefix = XMLConstants.DEFAULT_NS_PREFIX;
            namespace = parent.getNamespaceURI();
            localname = value;
        } else {
            prefix = value.substring(0, prefixIndex);
            localname = value.substring(prefixIndex + 1);
            namespace = parent.getNamespaceURI(prefix);
            if (namespace == null) {
                //unknown prefix
                throw new CdlXmlParsingException(
                        ERROR_UNKNOWN_PREFIX + prefix + " in " + value);
            }

        }
        if (localname.length() == 0) {
            throw new CdlXmlParsingException(ERROR_NO_LOCALNAME + value);
        }
        QName qName = new QName(namespace, localname, prefix);
        return qName;
    }

    public QName getQname() {
        return qname;
    }

    /**
     * bind the attribute; extract the qname This cannot be applied to an
     * attribute without a parent.
     *
     * @param attr attribute source
     * @throws CdlXmlParsingException
     */
    public void bind(Attribute attr) throws CdlXmlParsingException {
        super.bind(attr);
        String value = getValue();
        QName qName = parseQname(value);
        qname = qName;
    }
}
