/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.api;


import java.io.Serializable;

/**
 * A range for supporting positive numbers only. Pass in a negative value for max and there is no maximum
 */

public class Range implements Cloneable, Serializable {

    private int min;
    private int max;

    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public boolean isInRange(int value) {
        return value >= min && (max < 0 || value <= max);
    }

    @Override
    public String toString() {
        return "[" + min + ", " + max +"]";
    }

    /**
     * Calculate the max number that can be allocated. if the number <= 0: none
     *
     * @param requestMin the minimum that was asked for
     * @param requestMax the max that was asked for
     * @param current    the current number in use
     * @return the permitted number
     */
    public int calculateMaximumAllocatable(int requestMin, int requestMax, int current) {
        if (max < 0) {
            //as many as you want
            return requestMax;
        }
        int spaceLeft = max - current;
        if (spaceLeft <= 0) {
            //all in use, go away
            return 0;
        }
        if (spaceLeft < requestMin) {
            //no room for the min request
            return 0;
        }
        if (spaceLeft >= requestMax) {
            //room for all
            return requestMax;
        }
        //otherwise, here is all the space that is left, and it is in range
        return spaceLeft;


    }

    public static final Range NO_LIMITS = new Range(0, -1);


}
