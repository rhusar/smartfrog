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
package org.smartfrog.sfcore.languages.cdl.dom;

import org.smartfrog.sfcore.languages.cdl.dom.attributes.RefRootAttribute;
import org.smartfrog.sfcore.languages.cdl.dom.attributes.RefAttribute;
import org.smartfrog.sfcore.languages.cdl.dom.attributes.LazyAttribute;
import org.smartfrog.sfcore.languages.cdl.CdlParsingException;

import javax.xml.namespace.QName;

import nu.xom.Element;

/**
 * Reference type
 * created 26-May-2005 11:14:38
 */

public class Ref extends DocNode {

    public Ref() {
    }

    public Ref(Element node) throws CdlParsingException {
        super(node);
    }


    /**
     * optional reference root
     */
    private RefRootAttribute refRoot;
    private RefAttribute refAttr;
    private boolean lazy;

    /**
     * bind to an element
     * @param element
     * @throws CdlParsingException
     */
    public void bind(Element element) throws CdlParsingException {
        super.bind(element);
        lazy=LazyAttribute.isLazy(element,false);
        refRoot = RefRootAttribute.extract(element, false);
        refAttr = RefAttribute.extract(element, true);
    }

    public RefRootAttribute getRefRoot() {
        return refRoot;
    }

    public void setRefRoot(RefRootAttribute refRoot) {
        this.refRoot = refRoot;
    }

    public RefAttribute getRefAttr() {
        return refAttr;
    }

    public void setRefAttr(RefAttribute refAttr) {
        this.refAttr = refAttr;
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }
}
