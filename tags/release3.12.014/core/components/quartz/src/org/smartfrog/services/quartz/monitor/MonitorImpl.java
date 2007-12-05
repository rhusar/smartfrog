/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/
package org.smartfrog.services.quartz.monitor;

import org.smartfrog.services.quartz.collector.DataSource;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.Vector;

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
public class MonitorImpl extends PrimImpl implements Prim, Runnable, Monitor, DataSource {
    private static final String VMSTAT_COMMAND = "vmstat -n ";
    private Process process = null;
    private BufferedReader pOut = null;
    private BufferedReader pErr = null;
    private volatile boolean terminated = false;
    private int delay = 5; // number of seconds between samples
    private Log log;
    private int perMinute = 60 / delay;

    private String vmstatCmd = VMSTAT_COMMAND + delay + "\n";
    private int splitIndex = 14;

    private Vector last10 = new Vector(10);
    private int intLast10 = 0;
    private Vector last30 = new Vector(30);
    private int intLast30 = 0;
    private Vector last60 = new Vector(60);
    private int intLast60 = 0;
    private int intLastMinute = 0;
    private int current = 0;
    private String name = "";

    //Standard remotable constructor
    public MonitorImpl() throws RemoteException {
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        log=sfLog();
        delay = sfResolve(DELAY, 5, false);

        vmstatCmd =VMSTAT_COMMAND + delay + ((char) 10);
        perMinute = 60 / delay;

        name = sfCompleteName().toString();

        log.info("cpu monitor deployed");
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        Thread myThread = new Thread(this);
        myThread.start();

        log.info("cpu monitor started");
    }

    public synchronized void sfTerminateWith(TerminationRecord tr) {
        terminated = true;
        super.sfTerminateWith(tr);
        log.info("cpu monitor terminated");
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
            log.debug("|------- averageLast10 = " + intLast10);
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
            log.debug("|------- averageLast30 = " + intLast30);
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
            log.debug("|------- averageLast60 = " + intLast60);
        } else {
            last60.addElement(new Integer(newMin));
        }

        intLastMinute = newMin;
    }

    private synchronized void startProcess() throws IOException {
        log.info( "starting process");

        if (!terminated) {
            log.debug(" running command:" + vmstatCmd);
            process = Runtime.getRuntime().exec(vmstatCmd);
            pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            pErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
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
                            log.debug(
                                "monitored value..." + current +
                                "  total so far " + totalCPU);
                            count++;

                            if (count == perMinute) {
                                log.debug(
                                    "updating with average for last minute " +
                                    (totalCPU / perMinute));
                                updateFigures(totalCPU / perMinute);
                                count = 0;
                                totalCPU = 0;
                            }
                        }
                    } catch (IOException e) {
                        log.error("ignoring IOException ioe = ", e);
                    }

                    try {
                        process.exitValue(); // check termiantion
                        startProcess(); // failed - restart
                    } catch (IllegalThreadStateException e) {
                        // do nothing, not terminated
                    }
                } catch (Exception e) {
                    log.error("Exception in Process",e);
                }
            }
        } catch (Exception e) {
            log.error(e);
        } finally {
            FileSystem.close(pErr);
            FileSystem.close(pOut);
            if (process != null) {
                try {
                    process.destroy();
                    process = null;
                } catch (Exception ignored) {
                }
            }
        }
    }
}
