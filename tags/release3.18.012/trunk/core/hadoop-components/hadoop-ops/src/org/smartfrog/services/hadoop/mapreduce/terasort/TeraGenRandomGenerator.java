/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.hadoop.mapreduce.terasort;

@SuppressWarnings({"deprecation"})

public class TeraGenRandomGenerator {
  private long seed = 0;
  private static final long mask32 = (1l<<32) - 1;
  /**
   * The number of iterations separating the precomputed seeds.
   */
  private static final int seedSkip = 128 * 1024 * 1024;
  /**
   * The precomputed seed values after every seedSkip iterations.
   * There should be enough values so that a 2**32 iterations are 
   * covered.
   */
  private static final long[] seeds = new long[]{0L,
                                                 4160749568L,
                                                 4026531840L,
                                                 3892314112L,
                                                 3758096384L,
                                                 3623878656L,
                                                 3489660928L,
                                                 3355443200L,
                                                 3221225472L,
                                                 3087007744L,
                                                 2952790016L,
                                                 2818572288L,
                                                 2684354560L,
                                                 2550136832L,
                                                 2415919104L,
                                                 2281701376L,
                                                 2147483648L,
                                                 2013265920L,
                                                 1879048192L,
                                                 1744830464L,
                                                 1610612736L,
                                                 1476395008L,
                                                 1342177280L,
                                                 1207959552L,
                                                 1073741824L,
                                                 939524096L,
                                                 805306368L,
                                                 671088640L,
                                                 536870912L,
                                                 402653184L,
                                                 268435456L,
                                                 134217728L,
                                                };

  /**
   * Start the random number generator on the given iteration.
   * @param initalIteration the iteration number to start on
   */
  TeraGenRandomGenerator(long initalIteration) {
    int baseIndex = (int) ((initalIteration & mask32) / seedSkip);
    seed = seeds[baseIndex];
    for(int i=0; i < initalIteration % seedSkip; ++i) {
      next();
    }
  }

  TeraGenRandomGenerator() {
    this(0);
  }

  long next() {
    seed = (seed * 3141592621l + 663896637) & mask32;
    return seed;
  }
}
