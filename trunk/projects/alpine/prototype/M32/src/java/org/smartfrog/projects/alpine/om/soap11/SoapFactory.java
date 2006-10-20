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

package org.smartfrog.projects.alpine.om.soap11;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.NodeFactory;
import org.smartfrog.projects.alpine.om.ExtendedNodeFactory;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;

/**
 * this iteration doesnt have the envisaged chain of handlers, all we do is create soap nodes,
 * though we can handoff to another factory.
 */
public class SoapFactory extends ExtendedNodeFactory {

    private String soapns;

    private NodeFactory handoff;

    public SoapFactory(String namespace, NodeFactory handoff) {
        this.soapns = namespace;

        this.handoff = handoff;
    }

    /**
     * Default ctor is bound to {@link SoapConstants#URI_SOAPAPI}.
     */
    public SoapFactory() {
        this(SoapConstants.URI_SOAPAPI, null);
    }

    /**
     * Make a new element
     *
     * @param fullname  this comes in with a prefix: on it, which we will need to strip off
     * @param namespace
     * @return
     */
    public Element startMakingElement(String fullname, String namespace) {
        String name = XsdUtils.extractLocalname(fullname);

        Element element = null;
        if (soapns.equals(namespace)) {
            if (SoapConstants.ELEMENT_ENVELOPE.equals(name)) {
                element = new Envelope(name, namespace);
            } else if (SoapConstants.ELEMENT_HEADER.equals(name)) {
                element = new Header(name, namespace);
            } else if (SoapConstants.ELEMENT_BODY.equals(name)) {
                element = new Body(name, namespace);
            } else if (SoapConstants.ELEMENT_FAULT.equals(name)) {
                element = new Fault(name, namespace);
            } else {
                //something else in our namespace. wierd.
                element = new SoapElement(name, namespace);
            }
        } else {
            if (handoff != null) {
                return handoff.startMakingElement(name, namespace);
            } else {
                return new SoapElement(name, namespace);
            }
        }

        return element;
    }

    /**
     * <p/>
     * Creates a new element in the specified namespace with the specified name. The builder calls this method to make
     * the root element of the document. </p>
     * <p/>
     * <p/>
     * Subclasses may change the name, namespace, content, or other characteristics of the element returned. The default
     * implementation merely calls <code>startMakingElement</code>. However, when subclassing, it is often useful to be
     * able to easily distinguish between the root element and a non-root element because the root element cannot be
     * detached. Therefore, subclasses must not return null from this method. Doing so will cause a
     * <code>NullPointerException</code>. </p>
     *
     * @param name      the qualified name of the element
     * @param namespace the namespace URI of the element
     * @return the new root element
     */
    public Element makeRootElement(String name, String namespace) {
        return startMakingElement(name, namespace);
    }

    /**
     * <p/>
     * Creates a new <code>Document</code> object. The root element of this document is initially set to <code>&lt;root
     * xmlns=http://www.xom.nu/fakeRoot""/></code>. This is only temporary. As soon as the real root element's start-tag
     * is read, this element is replaced by the real root. This fake root should never be exposed. </p>
     * <p/>
     * <p/>
     * The builder calls this method at the beginning of each document, before it calls any other method in this class.
     * Thus this is a useful place to perform per-document initialization tasks. </p>
     * <p/>
     * <p/>
     * Subclasses may change the root element, content, or other characteristics of the document returned. However, this
     * method must not return null or the builder will throw a <code>ParsingException</code>. </p>
     *
     * @return the newly created <code>Document</code>
     */
    public Document startMakingDocument() {
        return MessageDocument.create();
    }
}
