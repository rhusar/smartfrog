package org.smartfrog.sfcore.languages.cdl.resolving;

/**
 * (C) Copyright 2005 Hewlett-Packard Development Company, LP
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * <p/>
 * For more information: www.smartfrog.org
 */
public enum ResolveEnum {


    /*
    There is some deviousness here
    */
    ResolvedUnknown(0),
    ResolvedComplete(1),
    ResolvedIncomplete(2),
    ResolvedLazyLinksRemaining(3),
    ;
    private int value;

    ResolveEnum(int value) {
       this.value = value;
   }

    /**
     * Merge the parent state and the child state to produce an aggregate which is
     * the worst-case merging of the two.
     * @param child
     * @return whichever of the two is in the least resolved state.
     */
    public ResolveEnum merge(ResolveEnum child) {
        if (child.value > this.value) {
            return child;
        } else {
            return this;
        }
    }

    /**
     * Test for the state being completed for parse time.
     * There may be links, but they are lazy ones.
     *
     * @return true if this is the case
     */
    public boolean isParseTimeResolutionComplete() {
        return this == ResolvedComplete
            || this == ResolvedLazyLinksRemaining;
    }


}
