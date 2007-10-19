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
package org.smartfrog.sfcore.languages.cdl.resolving;

import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;

/**
 * POJO for results of an extension resolve
 * created 10-Jun-2005 16:47:23
 */

public class ResolveResult {

    public ResolveEnum state;

    public PropertyList resolvedPropertyList;

    public ResolveResult(ResolveEnum state, PropertyList resolvedPropertyList) {
        this.state = state;
        this.resolvedPropertyList = resolvedPropertyList;
    }

    public ResolveResult(PropertyList resolvedPropertyList) {
        this.resolvedPropertyList = resolvedPropertyList;
        this.state=resolvedPropertyList.getResolveState();
    }

    public ResolveEnum getState() {
        return state;
    }

    public PropertyList getResolvedPropertyList() {
        return resolvedPropertyList;
    }

}
