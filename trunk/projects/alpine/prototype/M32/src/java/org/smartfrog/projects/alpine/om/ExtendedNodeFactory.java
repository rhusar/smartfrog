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
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.Document;
import org.smartfrog.projects.alpine.interfaces.NamespaceNodeFactory;
import org.smartfrog.projects.alpine.xmlutils.XsdUtils;

/**
 * This is something that can be subclassed for more interesting class creation
 *
 */
public abstract class ExtendedNodeFactory extends NodeFactory {

    private final static Nodes EMPTY = new Nodes();


    /**
     * SOAP says these are forbidden. We just strip them, in an act of silent forgiveness.
     * 
     * @return the nodes to be added to the tree
     */
    public Nodes makeProcessingInstruction(String target, String data) {
        return EMPTY;
    }




}
