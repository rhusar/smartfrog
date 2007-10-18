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

package org.smartfrog.examples.arithnet;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Random;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Defines the generator component.
 */
public class Generator extends NetElemImpl implements Remote {
    /**
     * Seed value for the generator.
     */
    int seed;
    /**
     * Delay value for the generator.
     */
    int delay;

    /**
     * Difference value for the generator.
     */
    int diff;
    /**
     * Minimum value for the generator.
     */
    int min;
    /**
     * Generator thread.
     */
    Thread generator;

    /**
     * Constructs Generator object
     * @throws RemoteException if unable to construct Generator object
     * remotely.
     */
    public Generator() throws java.rmi.RemoteException {
        super();
    }
    /**
     * Starts the component.
     * Overrides NetElemImpl.sfStart.
     *
     * @throws SmartFrogException if unable to read the attributes or start the
     * component
     * @throws RemoteException if network or remote error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        try {
            super.sfStart();
            min = ((Integer) sfResolve("min")).intValue();
            diff = ((Integer) sfResolve("max")).intValue() - min + 1;
            seed = ((Integer) sfResolve("seed")).intValue();
            delay = ((Integer) sfResolve("interval")).intValue();
            generator = new TheGenerator();
            generator.start();
        } catch (SmartFrogException sfex) {
            Reference refName = sfCompleteNameSafe();
            terminateComponent(this, sfex, refName);
            throw sfex;
        }
    }
    /**
     * Life cycle method for component termination.
     * @param tr the reson that lead to component termination
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        try {
            if (generator != null) {
                generator.stop();
            }
        } catch (Exception e) {
            // ignore
        }
        super.sfTerminateWith(tr);
    }

    /**
     * Inner class that acts as a thread and adds an integer to the current
     * value vector maintained by the parent object NetElemImpl with the
     * help of seed, diff and min values.
     */
    class TheGenerator extends Thread {
        /**
         * Interface method for thread.
         */
        public void run() {
            Random r = new Random(seed);

            while (true) {
                int v = Math.abs((r.nextInt() % diff)) + min;
                System.out.println(name + " generating " + v);
                addValue(v);

                try {
                    sleep(delay * 1000);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }
}
