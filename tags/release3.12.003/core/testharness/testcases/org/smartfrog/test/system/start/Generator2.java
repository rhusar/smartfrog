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

package org.smartfrog.test.system.start;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Random;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;


public class Generator2 extends NetElemImpl implements Remote {
    private int seed;
    private int delay;
    private int diff;
    private int min;
    private Thread generator;

    public Generator2() throws java.rmi.RemoteException {
    }

    public void sfStart() throws SmartFrogException, RemoteException {
        try {
            super.sfStart();
            min = ((Integer) sfResolve("min")).intValue();
            diff = ((Integer) sfResolve("max")).intValue() - min + 1;
            seed = ((Integer) sfResolve("seed")).intValue();
            delay = ((Integer) sfResolve("interval")).intValue();
            generator = new TheGenerator();
            generator.start();
        } catch (Exception ex) {
            // any exception causes termination
            Reference componentName = sfCompleteNameSafe();
            sfTerminate(TerminationRecord.abnormal("Compound sfStart failure: " + ex,
                    componentName));
        }
    }

    public void sfTerminateWith(TerminationRecord tr) {
        try {
            if (generator != null) {
                generator.stop();
            }
        } catch (Exception e) {
        }

        super.sfTerminateWith(tr);
    }

    class TheGenerator extends Thread {
        public void run() {
            Random r = new Random(seed);

            while (true) {
                int v = Math.abs((r.nextInt() % diff)) + min;
                System.out.println(name + " generating " + v);
                addValue(v);

                try {
                    sleep(delay * 1000);
                } catch (Exception e) {
                }
            }
        }
    }
}
