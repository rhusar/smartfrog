/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.languages.sf.functions;

/**
 * Defines the Random function that returns a random number for each 
 * invocation.
 */ 
public class Random extends BaseFunction {
    /**
     * Holder of random number.
     */
    static java.util.Random gen = null;
    
    /**
     * Returns the random number.
     * @return Random number object.
     */
    protected Object doFunction() {
        long seed;
        double number;
        Object result;
        int min = 0;
        int max = 10;
        boolean isInt = false;

        if (gen == null) {
            if (context.containsKey("seed")) {
                try {
                    seed = ((Integer) context.get("seed")).intValue();
                } catch (ClassCastException e) {
                    seed = ((Long) context.get("seed")).longValue();
                }

                gen = new java.util.Random(seed);
            } else {
                gen = new java.util.Random();
            }
        }

        if (context.containsKey("integer")) {
            isInt = ((Boolean) context.get("integer")).booleanValue();
        }

        if (context.containsKey("min")) {
            min = ((Integer) context.get("min")).intValue();
        }

        if (context.containsKey("max")) {
            max = ((Integer) context.get("max")).intValue();
        }
        number = gen.nextFloat();

        if (isInt) {
            if (max < min) {
                result = new Integer(min);
            } else {
                int i = (int) (number * (max - min + 1));
                result = new Integer(i + min);
            }
        } else {
            result = new Float(number);
        }

        return result;
    }
}
