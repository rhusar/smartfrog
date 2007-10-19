/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl.generate;

import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * created 02-Feb-2006 14:40:52
 */

public abstract class BaseGenerator implements TypeGenerator {
    protected boolean trim=false;


    protected BaseGenerator(boolean trim) {
        this.trim = trim;
    }

    protected BaseGenerator() {
    }

    public abstract Object generateType(PropertyList node) throws SmartFrogException;

    /**
     * Override point: get the string value of a node.
     * @param node
     * @return
     * @throws SmartFrogException if there was no text
     */
    protected String extractStringValue(PropertyList node) throws SmartFrogException {
        String text = node.getTextValue();
        if(text==null) {
            throw new SmartFrogException("no text under "+node.getDescription());
        }
        if(trim) {
            text=text.trim();
        }
        return text;
    }

    /**
     * Test for the text being empty after any trimming
     * @param node to check
     * @return true if the text under the node is absent, or, after trimming, empty.
     */
    public boolean isEmptyText(PropertyList node) {
        String text = node.getTextValue();
        if(text==null) {
            return true;
        }
        if (trim) {
            text = text.trim();
        }
        return text.length()==0;
    }
}
