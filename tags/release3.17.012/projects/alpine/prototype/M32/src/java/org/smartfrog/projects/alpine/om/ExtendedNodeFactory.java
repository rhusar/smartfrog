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

package org.smartfrog.projects.alpine.om;

import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.Document;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;

/**
 * This is something that can be subclassed for more interesting class creation
 *
 */
public abstract class ExtendedNodeFactory extends NodeFactory {

    private static final Nodes EMPTY = new Nodes();


    /**
     * SOAP says these are forbidden. We just strip them, in an act of silent forgiveness.
     * 
     * @return the nodes to be added to the tree
     */
    public Nodes makeProcessingInstruction(String target, String data) {
        return EMPTY;
    }


    /**
     * Test for an element being in scope before it is created
     *
     * @param element   element name
     * @param namespace element namespace, "" for local
     *
     * @return true iff the element is in scope for this factory
     */
    public abstract boolean inScope(String element, String namespace);


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
