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
    //ResolvedNoWorkNeeded(1),
    ResolvedComplete(1),
    ResolvedIncomplete(2),
    ResolvedLazyLinksRemaining(3),
    ;
    private int value;

    ResolveEnum(int value) {
       this.value = value;
   }

    /**
     * Propagate resolution
     *
     * @param parent
     * @return
     */
    public static ResolveEnum propagate(ResolveEnum parent) {
        switch (parent) {
            case ResolvedComplete:
                return ResolvedComplete;
            case ResolvedIncomplete:
                return ResolvedIncomplete;
//            case ResolvedNoWorkNeeded:
//                return ResolvedComplete;
            case ResolvedUnknown:
                return ResolvedUnknown;
            case ResolvedLazyLinksRemaining:
            default:
                return ResolvedLazyLinksRemaining;
        }
    }

    /**
     * Merge the parent state and the child state to produce an aggregate which is
     * the worst-case merging of the two.
     * @param parent
     * @param child
     * @return whichever of the two is in the least resolved state.
     */
    public static ResolveEnum merge(ResolveEnum parent, ResolveEnum child) {
        if(child.value>parent.value) {
            return child;
        } else {
            return parent;
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
