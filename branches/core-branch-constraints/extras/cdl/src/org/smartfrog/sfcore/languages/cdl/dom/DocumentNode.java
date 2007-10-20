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

import nu.xom.Document;
import nu.xom.Element;

/**
 */
public class DocumentNode extends Document {

    public DocumentNode(Element root) {
        super(root);
    }

    public DocumentNode(Document doc) {
        super(doc);
    }

    public static DocumentNode create() {
        return new DocumentNode(new Element("root",
                "http://www.xom.nu/fakeRoot"));
    }


    /**
     * owner document. may be null.
     */
    private CdlDocument owner;


    public CdlDocument getOwner() {
        return owner;
    }

    public void setOwner(CdlDocument owner) {
        this.owner = owner;
    }

}
