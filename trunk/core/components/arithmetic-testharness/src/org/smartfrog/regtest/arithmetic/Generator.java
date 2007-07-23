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

package org.smartfrog.regtest.arithmetic;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Random;
import java.lang.InterruptedException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Defines the generator component.
 */
public class Generator extends NetElemImpl implements Remote {

    private String name = "Generator";

    /**
     * Seed value for the generator.
     */
    private int seed;
    /**
     * Delay value for the generator.
     */
    private int delay;

    /**
     * Difference value for the generator.
     */
    private int diff;
    /**
     * Minimum value for the generator.
     */
    private int min;
    /**
     * Generator thread.
     */
    private Thread generator;

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
    public void sfStart() throws SmartFrogException, RemoteException {
        try {
            super.sfStart();
            min = ((Integer) sfResolve("min")).intValue();
            diff = ((Integer) sfResolve("max")).intValue() - min + 1;
            seed = ((Integer) sfResolve("seed")).intValue();
            delay = ((Integer) sfResolve("interval")).intValue();
            name = this.sfCompleteNameSafe().toString();
            generator = new TheGenerator(this);
            generator.start();
	    //generator.join();    //Mod Idia Team
        } catch (Exception ex) {
            System.out.println("DEBUG TERMINATE SFSTART");
            Reference refName = sfCompleteNameSafe();
            terminateComponent(this, ex, refName);
            throw SmartFrogException.forward(ex);
        }

    }
    /**
     * Life cycle method for component termination.
     * @param tr the reson that lead to component termination
     */
    public void sfTerminateWith(TerminationRecord tr) {
        try {
            if (generator != null) {
                generator.stop();
            }
        } catch (Exception e) {
            // ignore
        }
        super.sfTerminateWith(tr);
    }


    public String getName() {
        return name;
    }

    public int getSeed() {
        return seed;
    }

    public int getDelay() {
        return delay;
    }

    public int getDiff() {
        return diff;
    }


    public void setDiff(int diff) {
        this.diff = diff;
    }

    public int getMin() {
        return min;
    }

    /**
     * Inner class that acts as a thread and adds an integer to the current
     * value vector maintained by the parent object NetElemImpl with the
     * help of seed, diff and min values.
     */
    private static class TheGenerator extends Thread {
        private Generator owner;


        public TheGenerator(Generator owner) {
            this.owner = owner;
        }

        /**
         * Interface method for thread.
         */
        public void run() {
            Random r = new Random(owner.getSeed());
            this.setName(owner.getName());
            while (true) {
                owner.setDiff(10);
                int v = Math.abs((r.nextInt() % owner.getDiff())) + owner.getMin();
                System.out.println("\n\n*****************************************"
                                  +"\n  GENERATOR:"+" Result: "+ v +", "+owner.getName());
                owner.addValue(v);
                try {
                    sleep(owner.getDelay() * 1000);
                } catch (Exception e) {
                    System.out.println("DEBUG TERMINATE RUN ,"+e.toString());
                }
            }
        }
    }
}
