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
package org.smartfrog.sfcore.languages.cdl;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.reference.Reference;

import java.util.Iterator;

/**
 * Our phases type; what we return from parsing.
 * created 18-Apr-2005 13:46:30
 */

public class CdlPhases extends SFComponentDescriptionImpl {

    public CdlPhases(Reference type,
            SFComponentDescription parent,
            Context cxt,
            boolean eager) {
        super(type, parent, cxt, eager);
    }

    public Iterator sfValues() {
        //TODO
        return super.sfValues();
    }

    /**
     * Returns the clone.
     *
     * @return an Object clone
     */
    public Object clone() {
        return super.clone();
    }
}


