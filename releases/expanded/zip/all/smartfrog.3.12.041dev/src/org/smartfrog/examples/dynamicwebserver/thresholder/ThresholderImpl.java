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

package org.smartfrog.examples.dynamicwebserver.thresholder;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.examples.dynamicwebserver.balancer.Balancer;
import org.smartfrog.examples.dynamicwebserver.gui.graphpanel.DataSource;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

/**
 * <p>
 * Description: Thresholder component.
 * </p>
 *
 */

public class ThresholderImpl extends CompoundImpl implements Thresholder, Compound, SmartFrogCoreKeys  {
    int upperThreshold;
    int lowerThreshold;
    int pollFrequency;
    int repeatMeasures;
    int stabilizationMeasures;
    int minInstances;
    protected Vector servers = null;
    ComponentDescription template = null;
    Balancer balancer = null;
    String dataSourceName = null;
    DataSource dataSource = null;
    int currentInstances = 0;
    int targetInstances = 0;
    protected int maxNumber;
    boolean isAuto = true;
    ComponentDescription cd = null;
    String componentNamePrefix = "comp";
    int stabilizationCounter = 0;
    Vector measures = new Vector();

    // these are the basic control methods
    Object lock = new Object();
    private Object setInstancesLock = new Object();
    private Object instanceLock = new Object();

    /* start polling for the values */
    Poller poller = null;
    boolean pollingFinished = false;

    public ThresholderImpl() throws RemoteException {
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        upperThreshold = sfResolve(UPPERTHRESHOLD, 100, false);
        lowerThreshold = sfResolve(LOWERTHRESHOLD, 10, false);
        pollFrequency = sfResolve(POLLFREQUENCY, 5, false) * 1000;
        repeatMeasures = sfResolve(REPEATMEASURES, 3, false);
        stabilizationMeasures = sfResolve(STABILIZATIONMEASURES, 5, false);
        minInstances = sfResolve(MININSTANCES, 1, false);
        servers = sfResolve(SERVERS, new Vector(), true);

        maxNumber = servers.size();

        if (maxNumber < 1) {
            maxNumber = 1;
        }

        template = (ComponentDescription) sfResolve(TEMPLATE, true);
        balancer = (Balancer) sfResolve(BALANCER, false);

        dataSourceName = sfResolve(DATASOURCENAME, "dataSource", false);
        dataSource = (DataSource) sfResolve(DATASOURCE, true);
        isAuto = sfResolve(ISAUTO, true, false);
        if (sfLog().isDebugEnabled()) sfLog().debug ("thresholder deployed");
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        if (sfLog().isDebugEnabled()) sfLog().debug ("thresholder starting");
        setTargetInstances(minInstances);
        if (sfLog().isDebugEnabled()) sfLog().debug ("thresholder instances running");

        if (dataSource == null) {
            Reference source = new Reference();
            source.addElement(ReferencePart.here(componentNamePrefix + 0));
            source.addElement(ReferencePart.here(dataSourceName));

            if (sfLog().isDebugEnabled()) sfLog().debug ( "looking for data source reference " + source);
            if (sfLog().isDebugEnabled()) sfLog().debug ("found " + sfResolve(source));

            dataSource = (DataSource) sfResolve(source);
            if (sfLog().isDebugEnabled()) sfLog().debug ("data source found");
        } else {
            if (sfLog().isDebugEnabled()) sfLog().debug ("have the data source statically defined");
        }

        startThresholdPolling();

        if (sfLog().isDebugEnabled()) sfLog().debug ("thresholder started");
    }

    public synchronized void sfTerminateWith(TerminationRecord t) {
        stopThresholdPolling();
        super.sfTerminateWith(t);
        if (sfLog().isDebugEnabled()) sfLog().debug ("thresholder terminated");
    }

    public void setUpperThreshold(int t) {
        synchronized (lock) {
            upperThreshold = t;
        }

        if (sfLog().isInfoEnabled()) sfLog().info ("upper limit set to " + t);
    }

    public void setLowerThreshold(int t) {
        synchronized (lock) {
            lowerThreshold = t;
        }

        if (sfLog().isInfoEnabled()) sfLog().info ("lower limit set to " + t);
    }

    public int upperThreshold() throws RemoteException {
        synchronized (lock) {
            return this.upperThreshold;
        }
    }

    public int lowerThreshold() throws RemoteException {
        synchronized (lock) {
            return this.lowerThreshold;
        }
    }

    public synchronized void setAuto(boolean b) {
        isAuto = b;
        if (sfLog().isDebugEnabled()) sfLog().debug ("set auto to " + b);
    }

    protected boolean minInstancesChanged() {
        int newMin;

        try {
            newMin = sfResolve(MININSTANCES, 0, false);
        } catch (Exception e) {
            newMin = minInstances;
        }

        if (minInstances != newMin) {
            minInstances = newMin;

            return true;
        }

        return false;
    }

    public void setMinInstances(int i) {
        synchronized (setInstancesLock) {
            try {
              if (i < 0) {
                minInstances = 1;
              }
              else {
                minInstances = i;
                sfReplaceAttribute(MININSTANCES, new Integer(minInstances));
              }

              if (sfLog().isDebugEnabled()) sfLog().debug ("set minimum instances to " + minInstances);

              setTargetInstances(targetInstances);
            }
            catch (Exception ex) {
              if (sfLog().isErrorEnabled()) sfLog().error ("Trying set minimum instances to "+minInstances + ". "+ex.toString());
            }
        }
    }

    // these are internal methods that should not be used directly
    protected void setTargetInstances(int target) {
        synchronized (setInstancesLock) {
            if (sfLog().isDebugEnabled()) sfLog().debug ( "min instances " + minInstances);

            if (target < minInstances) {
                targetInstances = minInstances;
            } else {
                targetInstances = target;
            }

            if (target > maxNumber) {
                targetInstances = maxNumber;
            }

            if (sfLog().isDebugEnabled()) sfLog().debug ( "setting target instances to: " + targetInstances + ", current instances: " + currentInstances);

            try {
                while (currentInstances != targetInstances) {
                    if (currentInstances < targetInstances) {
                        startInstance();
                    } else if (currentInstances > targetInstances) {
                        stopInstance();
                    }
                }
            } catch (Exception e) {
                if (sfLog().isWarnEnabled()) sfLog().warn (" exception caught in ajusting number of instances", e);
            }
        }
    }

    protected void startInstance() throws Exception {
        synchronized (instanceLock) {
            Prim deployed = null;
            if (sfLog().isDebugEnabled()) sfLog().debug ( "starting instance");
            stabilizationCounter = stabilizationMeasures;

            // read the template file
            try {
                String server = (String) servers.elementAt(currentInstances);

                Context instanceContext = new ContextImpl();
                instanceContext.put(SF_PROCESS_HOST, server);

                if (sfLog().isDebugEnabled()) sfLog().debug ( "instance being created");
                deployed = sfDeployComponentDescription(componentNamePrefix + currentInstances, this, template, instanceContext);
                if (sfLog().isDebugEnabled()) sfLog().debug ( "instance created");

                deployed.sfDeploy();
                if (sfLog().isDebugEnabled()) sfLog().debug ( "deployed");
                deployed.sfStart();
                if (sfLog().isDebugEnabled()) sfLog().debug ( "started");

                if (balancer != null) {
                    balancer.addServer(server);
                }
            } catch (Exception e) {
                if (sfLog().isWarnEnabled()) sfLog().warn ("help... exception in starting instance", e);

                try {
                    if(deployed!=null) {
                    	deployed.sfDetachAndTerminate(TerminationRecord.normal(null));
                    }
                } catch (Exception ex) {
                    if (sfLog().isIgnoreEnabled()) sfLog().ignore (ex);
                }

                throw e;
            }

            // add the instanceData
            // resolve and deploy, named the same as the instance data
            if (sfLog().isDebugEnabled()) sfLog().debug ( "started instance");
            currentInstances += 1;
        }
    }

    protected void stopInstance() throws Exception {
        synchronized (instanceLock) {
            if (sfLog().isDebugEnabled()) sfLog().debug ( "stoping instance");
            currentInstances -= 1;

            String server = (String) servers.elementAt(currentInstances);

            stabilizationCounter = stabilizationMeasures;

            Reference instRef = new Reference(ReferencePart.here(componentNamePrefix +
                        currentInstances));
            Prim instance = ((Prim) sfResolve(instRef));

            instance.sfDetachAndTerminate(TerminationRecord.normal(null));

            if (sfLog().isDebugEnabled()) sfLog().debug ( "stopped instance");

            if (balancer != null) {
                balancer.removeServer(server);
            }
        }
    }

    protected synchronized void startThresholdPolling() {
        if (sfLog().isDebugEnabled()) sfLog().debug ( "starting threshold poller...");
        poller = new Poller();
        poller.start();
    }

    protected synchronized void stopThresholdPolling() {
        if (sfLog().isDebugEnabled()) sfLog().debug ("stopping threshold poller...");
        pollingFinished = true;
    }

    protected class Poller extends Thread {
        public void run() {
            if (sfLog().isDebugEnabled()) sfLog().debug ( "poller running");

            while (!pollingFinished) {
                try {
                    int value = dataSource.getData();
                    if (sfLog().isDebugEnabled()) sfLog().debug ( "poller measure obtained " +  value);

                    if (minInstancesChanged()) {
                        setTargetInstances(targetInstances);
                    }

                    measures.add(new Integer(value));

                    if (measures.size() > repeatMeasures) {
                        if (sfLog().isDebugEnabled()) sfLog().debug ( "poller has sufficient measures to proceed");

                        measures.remove(0);

                        // we now have enough measures to start testing...
                        if (stabilizationCounter > 0) {
                            // don't do anything if not stabilized...
                            if (sfLog().isDebugEnabled()) sfLog().debug ( "poller not stabilized yet");
                            stabilizationCounter--;
                        } else {
                            if (sfLog().isDebugEnabled()) sfLog().debug ("poller stabilized");

                            // have enough measures and are stabilised...
                            // note it would be more efficient to keep a running total (adding and subtracting
                            // values as they are added and removed from the vector
                            // also would be more eficient to multiply the thresholds by the repeats once, not do
                            // the averaging division each time.
                            // keeping the logic here, however, makes it easier to maintain and modify during testing...!
                            int avg = 0;

                            for (Enumeration e = measures.elements();
                                    e.hasMoreElements();) {
                                avg += ((Integer) e.nextElement()).intValue();
                            }

                            avg = avg / repeatMeasures;
                            if (sfLog().isDebugEnabled()) sfLog().debug ( "poller average of repeat measures is " + avg);

                            if (avg > upperThreshold) {
                                if (sfLog().isInfoEnabled()) sfLog().info ( "poller upper threshold met...avg is "+avg);

                                if (isAuto) {
                                    setTargetInstances(currentInstances + 1);
                                }
                            } else if (avg < lowerThreshold) {
                                if (sfLog().isInfoEnabled()) sfLog().info ("poller lower threshold met...avg is "+avg);
                                if (isAuto) {
                                    setTargetInstances(currentInstances - 1);
                                }
                            }
                        }
                    }

                    if (sfLog().isDebugEnabled()) sfLog().debug ("poller sleeping");
                    sleep(pollFrequency);
                    if (sfLog().isDebugEnabled()) sfLog().debug ("poller awake");
                } catch (Exception e) {
                    if (sfLog().isWarnEnabled()) sfLog().warn ("exception caught in the poller. "+e.getMessage(),e);
                }
            }

            if (sfLog().isDebugEnabled()) sfLog().debug ("poller stopped");
        }
    }
}
