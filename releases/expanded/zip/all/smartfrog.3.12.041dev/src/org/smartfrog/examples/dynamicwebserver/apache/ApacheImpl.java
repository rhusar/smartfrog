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


package org.smartfrog.examples.dynamicwebserver.apache;

import org.smartfrog.examples.dynamicwebserver.gui.graphpanel.DataSource;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.Vector;


/**
 * This class implements the Compound interface because it can "contain"
 * Virtual Hosts components. The Apache interface is the Remoteable interface
 * and the Runnable interface is used to monitor the httpd process. The httpd
 * process is started in sfStart by setting the apacheState variable to true
 * and ended in sfTerminate by setting the apacheState variable to false.
 */
public class ApacheImpl extends CompoundImpl implements Compound, Apache,
        DataSource, Runnable {

    private String location = "";
    private String baseConfigLocation = "";
    private String configLocation = "";
    private String apachectlLocation = "";
    private boolean manageDaemon = true;

    private int interCheckTime;

    private Vector envVars;
    private boolean terminated = false;
    private boolean apacheState = false;
    private boolean shouldRefresh = false;
    private Thread thread;
    private int threadCount = 0;

    /**
     * Standard Remotable constructor
     *
     * @throws RemoteException in case of Remote/network error
     */
    public ApacheImpl() throws RemoteException {
    }

    /**
     * This method retrieves the paramters from the .sf file.
     *
     * @throws SmartFrogException error in deploying the component
     * @throws RemoteException    in case of Remote/network error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        if (sfLog().isDebugEnabled()) sfLog().debug("apache deploying");        

        interCheckTime = sfResolve(INTERCHECKTIME, 15, false);

        location = sfResolve(LOCATION, "", true);
        baseConfigLocation = sfResolve(BASECONFIGLOCATION, "", true);
        configLocation = sfResolve(CONFIGLOCATION, "", true);
        envVars = (Vector) sfResolve(ENVVARS, true);
        apachectlLocation = sfResolve(APACHECTLLOCATION, "", true);
        manageDaemon = sfResolve(MANAGEDAEMON, manageDaemon, false);

        if (sfLog().isInfoEnabled()) sfLog().info("apache deployed");
    }

    /**
     * This sets a flag that will start the httpd process running.
     *
     * @throws SmartFrogException error in starting the component
     * @throws RemoteException    in case of Remote/network error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        if (sfLog().isDebugEnabled()) sfLog().debug("apache starting");
        setApacheState(true);
        if (sfLog().isInfoEnabled()) sfLog().info("apache started");
    }

    /**
     * This shuts down Apache by requesting that the ApacheState  variable be
     * set to false.
     *
     * @param tr TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        if (sfLog().isInfoEnabled()) sfLog().info("terminating apache - setting runstate to false");
        setApacheState(false);
        terminated = true;
        super.sfTerminateWith(tr);
    }

    /**
     * The logic behind this is: if Apache is not running and the desired state
     * is to be running then Set Apaches state to running call start() else
     * Set Apaches state to the required state end It is not possible to
     * simply change the variable to true and start a new thread because it
     * may already be running and you would end up starting too many httpd
     * processes.
     *
     * @param newState new state of Apache
     */
    public synchronized void setApacheState(boolean newState) {
        if (sfLog().isDebugEnabled()) sfLog().debug("setting apache state to " + newState + " from " + apacheState);

        if ((!apacheState) && (newState)) {
            apacheState = newState;
            if (sfLog().isDebugEnabled()) sfLog().debug("starting apache thread");
            thread = new Thread(this);
            thread.start();
            if (sfLog().isDebugEnabled()) sfLog().debug("started apache thread");
        } else {
            if (sfLog().isDebugEnabled()) sfLog().debug("really setting apache to " + newState);
            apacheState = newState;
        }
    }

    /**
     * Get Apache state
     * @return boolean
     */
    public synchronized boolean getApacheState() {
        return apacheState;
    }

    /**
     * This method is used to 'maintain' a processes desired state. In the case
     * of the Apache httpd if the number of httpd listener threads is 0 then
     * the process is restarted  This method is linux/unix specific since it
     * uses the command: "ps -A | grep httpd" This is not possible to do on
     * windows without writing lots of messy  W32 code and using JNI.
     */
    public void run() {
        boolean needRestart = false;
        Process p;
        if (sfLog().isInfoEnabled()) sfLog().info("apache monitor thread running");

        try {
            if (manageDaemon)
                p = Runtime.getRuntime().exec(apachectlLocation + " start");
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) sfLog().error (e);
        }

        if (sfLog().isInfoEnabled()) sfLog().info( "httpd started");

        while (getApacheState()) {
            if (needRestart) {
                try {
                    if (sfLog().isDebugEnabled()) sfLog().debug("restarting apache");
                    if (manageDaemon)
                        p = Runtime.getRuntime().exec(apachectlLocation + " start");
                    needRestart = false;
                } catch (IOException e) {
                    if (sfLog().isErrorEnabled()) sfLog().error (e);
                }
            }
            //end if (needRestart)

            try {
                Thread.sleep(interCheckTime * 1000);
            } catch (InterruptedException ignored) {
                if (sfLog().isIgnoreEnabled()) sfLog().ignore (ignored);
            }

            try {
                String test = null;
                String shellLocation = "/bin/bash";
                Process p2 = Runtime.getRuntime().exec(shellLocation);
                BufferedReader pOut2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                DataOutputStream dos2 = new DataOutputStream(p2.getOutputStream());

                dos2.writeBytes("ps -A | grep httpd" + ((char) 10));
                dos2.writeBytes("exit 0" + ((char) 10));
                dos2.flush();
                dos2.close();

                p2.waitFor();

                test = pOut2.readLine();

                int count = 0;

                while (test != null) {
                    count++;
                    test = pOut2.readLine();
                }

                pOut2.close();
                threadCount = count;
                if (sfLog().isDebugEnabled()) sfLog().debug("Currently " + count + " httpd threads are running.");

                /**
                 * If the count is 0 is means there are no daemon processes
                 * running. This sets the needRestart flag to true which will
                 * be picked up in the next iteration.
                 */
                if (count == 0) {
                    needRestart = true;
                }

                if (shouldRefresh) {
                    if (manageDaemon) {
                        Process pGraceful = Runtime.getRuntime().exec(apachectlLocation + " graceful");
                        pGraceful.waitFor();
                    }
                    shouldRefresh = false;
                }
            } catch (Exception e) {
                if (sfLog().isErrorEnabled()) sfLog().error (" Error checking for processes - ignored",e);
            }
        }

        try {
            synchronized (this) {
                // kill only if we still want to kill apache
                // someone may have restarted it with a new thread
                if (!getApacheState()) {
                    if (sfLog().isDebugEnabled()) sfLog().debug("stopping apache");
                    if (manageDaemon)
                        p = Runtime.getRuntime().exec(apachectlLocation + " stop");
                    if (sfLog().isInfoEnabled()) sfLog().info("apache stopped");
                }
            }
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) sfLog().error (e);
        }
    }

    /**
     * This method is not used by the ApacheImpl class but would be used by a
     * Remote client.
     *
     * @throws RemoteException in case of Remote/network error
     */
    public void startDaemon() throws RemoteException {
        setApacheState(true);
    }

    /**
     * This method is not used by the ApacheImpl class but would be used by a
     * Remote client.
     *
     * @throws RemoteException in case of Remote/network error
     */
    public void stopDaemon() throws RemoteException {
        setApacheState(false);
    }

    /**
     * This method is not used by the ApacheImpl class but would be used by a
     * Remote client.
     *
     * @throws RemoteException in case of Remote/network error
     */
    public void refreshDaemon() throws RemoteException {
        shouldRefresh = true;
    }

    /**
     * This method is not currently used but could be used to maintain a
     * desired configuration of Apache and then periodically refresh Apache to
     * this state.
     *
     * @param scriptURL script url
     * @param fileName  file name
     * @throws RemoteException in case of Remote/network error
     */
    public void refreshConfig(String scriptURL, String fileName)
            throws RemoteException {
    }

    /**
     * Returns an int containing the number of threads - implementation of
     * DataSource interface
     *
     * @return int
     * @throws RemoteException in case of Remote/network error
     */
    public int getData() throws RemoteException {
        return threadCount;
    }
}
