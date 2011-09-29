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

package org.smartfrog.examples.orchdws.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.Vector;

import org.smartfrog.examples.dynamicwebserver.gui.graphpanel.DataSource;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;


/**
 * There are basically two ways in which this component could work.
 * <p>1) Send
 * the status every minute to the Control object. The problem with this is
 * that if the control object was controlling 1000 machines this could means
 * lots of traffic and not necessarily the most scalable option.</p>
 * <p>2) Notify the
 * Control object when the CPU usgae exceeds a certain level. The Control
 * object could then periodically poll the Monitor component. But at all times
 * it would be possible for the Control object to query the Monitor component.
 * </p>
 */
public class CPUMonitorImpl extends PrimImpl implements Prim, Runnable,
    CPUMonitor, DataSource {
    static final String cmd = "vmstat -n ";
    Process p = null;
    BufferedReader pOut = null;
    BufferedReader pErr = null;
    boolean terminated = false;
    int delay = 5; // number of seconds between samples

    int perMinute = 60 / delay;

    String vmstatCmd = cmd + delay + ((char) 10);
    int splitIndex = 14;

    Vector last10 = new Vector(10);
    private int intLast10 = 0;
    Vector last30 = new Vector(30);
    private int intLast30 = 0;
    Vector last60 = new Vector(60);
    private int intLast60 = 0;
    private int intLastMinute = 0;
    private int current = 0;

    //Standard remotable constructor
    public CPUMonitorImpl() throws RemoteException {
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {

        delay = sfResolve(DELAY, 5, false);

        vmstatCmd = cmd + delay + ((char) 10);
        perMinute = 60 / delay;

        if (sfLog().isDebugEnabled()) sfLog().debug ("cpu monitor deployed");
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        Thread myThread = new Thread(this);
        myThread.start();

        if (sfLog().isDebugEnabled()) sfLog().debug ("cpu monitor started");
    }

    public synchronized void sfTerminateWith(TerminationRecord tr) {
        synchronized (this) {
            terminated = true;
        }

        super.sfTerminateWith(tr);
        if (sfLog().isDebugEnabled()) sfLog().debug ("cpu monitor terminated");
    }

    public int getData() throws RemoteException {
        return getCurrent();
    }

    public int getCurrent() throws RemoteException {
        return current;
    }

    public int getAverageLastMinute() throws RemoteException {
        return intLastMinute;
    }

    public int getAverageLast10Minutes() throws RemoteException {
        return intLast10;
    }

    public int getAverageLast30Minutes() throws RemoteException {
        return intLast30;
    }

    public int getAverageLast60Minutes() throws RemoteException {
        return intLast60;
    }

    public void updateFigures(int newMin) {
        int sizeLast10 = last10.size();

        if (sizeLast10 > 10) {
            last10.removeElementAt(0);
            last10.addElement(new Integer(newMin));

            int total = 0;

            for (int i = 0; i < 10; i++) {
                Integer tmpInt = (Integer) last10.elementAt(i);
                total += tmpInt.intValue();
            }

            intLast10 = (int) total / 10;
            if (sfLog().isDebugEnabled()) sfLog().debug ( " |------- averageLast10 = " + intLast10);
        } else {
            last10.addElement(new Integer(newMin));
        }

        int sizeLast30 = last30.size();

        if (sizeLast30 > 30) {
            last30.removeElementAt(0);
            last30.addElement(new Integer(newMin));

            int total = 0;

            for (int i = 0; i < 30; i++) {
                Integer tmpInt = (Integer) last30.elementAt(i);
                total += tmpInt.intValue();
            }

            intLast30 = (int) total / 30;
            if (sfLog().isDebugEnabled()) sfLog().debug (" |------- averageLast30 = " + intLast30);
        } else {
            last30.addElement(new Integer(newMin));
        }

        int sizeLast60 = last60.size();

        if (sizeLast60 > 60) {
            last60.removeElementAt(0);
            last60.addElement(new Integer(newMin));

            int total = 0;

            for (int i = 0; i < 60; i++) {
                Integer tmpInt = (Integer) last60.elementAt(i);
                total += tmpInt.intValue();
            }

            intLast60 = (int) total / 60;
            if (sfLog().isDebugEnabled()) sfLog().debug (" |------- averageLast60 = " + intLast60);
        } else {
            last60.addElement(new Integer(newMin));
        }

        intLastMinute = newMin;
    }

    private synchronized void startProcess() throws IOException {
        if (sfLog().isDebugEnabled()) sfLog().debug ( "starting process");

        if (!terminated) {
            if (sfLog().isDebugEnabled()) sfLog().debug ( " running command:" + vmstatCmd);
            p = Runtime.getRuntime().exec(vmstatCmd);
            pOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
            pErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            pOut.readLine();
            pOut.readLine(); // throw away the header lines...
        }
    }

    /**
     *
     */
    public void run() {
        try {
            startProcess();

            int count = 0; // number of measurements taken (mod delay/60)
            int totalCPU = 0; // aggregate of the current minute's measures

            while (!terminated) {
                try {
                    //Every delay until 1 minutes data is obtained...
                    try {
                        String s = pOut.readLine();
                        if (s != null) {
                            String[] a = s.trim().split("\\W+");
                            s = a[splitIndex];

                            current = 100 -
                                (Integer.parseInt(s.trim()));

                            totalCPU = totalCPU + current;
                            if (sfLog().isDebugEnabled()) sfLog().debug ( "monitored value..." + current + "  total so far " + totalCPU);
                            count++;

                            if (count == perMinute) {
                                if (sfLog().isDebugEnabled()) sfLog().debug ( "updating with average for last minute " + (totalCPU / perMinute));
                                updateFigures(totalCPU / perMinute);
                                count = 0;
                                totalCPU = 0;
                            }
                        }
                    } catch (IOException e) {
                        if (sfLog().isErrorEnabled()) sfLog().error ("ignoring IOException ioe = " + e,e);
                    }

                    try {
                        p.exitValue(); // check termiantion
                        startProcess(); // failed - restart
                    } catch (IllegalThreadStateException e) {
                        // do nothing, not terminated
                    }
                } catch (Exception e) {
                    if (sfLog().isErrorEnabled()) sfLog().error ("Exception in Process (1) = " + e,e);
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                pErr.close();
            } catch (Exception e) {
            }

            try {
                pOut.close();
            } catch (Exception e) {
            }

            try {
                p.destroy();
            } catch (Exception e) {
            }
        }
    }
}
