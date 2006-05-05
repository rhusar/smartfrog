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

package org.smartfrog.services.anubisdeployer.dynamicwebserver.thresholder;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.HashMap;
import java.net.InetAddress;

import org.smartfrog.examples.dynamicwebserver.balancer.Balancer;
import org.smartfrog.examples.dynamicwebserver.gui.graphpanel.DataSource;
import org.smartfrog.examples.dynamicwebserver.logging.LogWrapper;
import org.smartfrog.examples.dynamicwebserver.logging.Logger;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

/**
 * <p/>
 * Description: Thresholder component.
 * </p>
 */

public class ThresholderImpl extends CompoundImpl implements Thresholder,
        Compound, SmartFrogCoreKeys {
    int upperThreshold;
    int lowerThreshold;
    int pollFrequency;
    int repeatMeasures;
    int stabilizationMeasures;
    int minInstances;
    int maxInstances;

    ComponentDescription template = null;
    Balancer balancer = null;
    String dataSourceName = null;
    DataSource dataSource = null;
    LogWrapper logger;


    boolean isAuto = true;
    ComponentDescription cd = null;
    String componentNamePrefix = "webserver";
    int stabilizationCounter = 0;
    Vector measures = new Vector();
    String name = "";

    // these are the basic control methods
    Object lock = new Object();
    private Object setInstancesLock = new Object();
    private Object instanceLock = new Object();

    private int instanceCount = 0;

    /* start polling for the values */
    Poller poller = null;
    boolean pollingFinished = false;

    private HashMap childServerMapping = new HashMap();

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
        maxInstances = sfResolve(MAXINSTANCES, 1, false);
        if (maxInstances < minInstances) maxInstances = minInstances;
        if (maxInstances < 1) {
            maxInstances = 1;
        }
        if (minInstances < 1) {
            minInstances = 1;
        }

        template = (ComponentDescription) sfResolve(TEMPLATE, true);
        balancer = (Balancer) sfResolve(BALANCER, false);

        dataSourceName = sfResolve(DATASOURCENAME, "dataSource", false);
        dataSource = (DataSource) sfResolve(DATASOURCE, true);
        isAuto = sfResolve(ISAUTO, true, false);
        logger = new LogWrapper((Logger) sfResolve(LOGTO, false));

        name = sfCompleteName().toString();
        logger.log(name, "thresholder deployed");
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        logger.log(name, "thresholder starting");

        if (dataSource == null) {
            Reference source = new Reference();
            source.addElement(ReferencePart.here(componentNamePrefix + 0));
            source.addElement(ReferencePart.here(dataSourceName));

            logger.logOptional(name, "looking for data source reference " + source);
            logger.logOptional(name, "found " + sfResolve(source));

            dataSource = (DataSource) sfResolve(source);
            logger.logOptional(name, "data source found");
        } else {
            logger.logOptional(name, "have the data source statically defined");
        }

        startThresholdPolling();

        logger.log(name, "thresholder started");
    }

    public synchronized void sfTerminateWith(TerminationRecord t) {
        stopThresholdPolling();
        super.sfTerminateWith(t);
        logger.log(name, "thresholder terminated");
    }

    public void setUpperThreshold(int t) {
        synchronized (lock) {
            upperThreshold = t;
        }

        logger.logOptional(name, "upper limit set to " + t);
    }

    public void setLowerThreshold(int t) {
        synchronized (lock) {
            lowerThreshold = t;
        }

        logger.logOptional(name, "lower limit set to " + t);
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
        logger.logOptional(name, "set auto to " + b);
    }

    private int currentInstances() {
        return sfChildren.size();
    }

    public synchronized Object sfReplaceAttribute(Object name, Object value)
            throws SmartFrogRuntimeException, RemoteException {
        if (name.equals(MININSTANCES)) {
            if (minInstances < 1) minInstances = 1;
            minInstances = ((Integer) value).intValue();
            if (minInstances > maxInstances) {
                maxInstances = minInstances;
                super.sfReplaceAttribute(MAXINSTANCES, new Integer(maxInstances));
            }
        } else if (name.equals(MAXINSTANCES)) {
            if (maxInstances < 1) maxInstances = 1;
            maxInstances = ((Integer) value).intValue();
            if (minInstances > maxInstances) {
                minInstances = maxInstances;
                super.sfReplaceAttribute(MININSTANCES, new Integer(minInstances));
            }
        }
        return super.sfReplaceAttribute(name, value);
    }


    public void setMinInstances(int i) {
        synchronized (setInstancesLock) {
            try {
                sfReplaceAttribute(MININSTANCES, new Integer(minInstances));
            }
            catch (Exception ex) {
                logger.log(name, "Trying set minimum instances to " + minInstances + ". " + ex.toString());
            }
        }
    }

    public void setMaxInstances(int i) {
        synchronized (setInstancesLock) {
            try {
                sfReplaceAttribute(MAXINSTANCES, new Integer(minInstances));
            }
            catch (Exception ex) {
                logger.log(name, "Trying set minimum instances to " + minInstances + ". " + ex.toString());
            }
        }
    }

    protected void startInstance() throws Exception {
        synchronized (instanceLock) {
            Prim deployed = null;

            logger.logOptional(name, "starting instance");
            stabilizationCounter = stabilizationMeasures;

            // read the template file
            try {
                logger.logOptional(name, "instance being created");
                /*
                * @SF4: may need to fix something here...?
                */
                deployed = sfCreateNewChild(componentNamePrefix + instanceCount++, template, null);
                logger.logOptional(name, "instance created");

                String host = ((InetAddress)deployed.sfResolve("sfHost", false)).getCanonicalHostName();
                childServerMapping.put(deployed, host);

                //System.out.println("adding alancer bhost " + host);
                if (balancer != null) {
                    //System.out.println("really adding alancer bhost " + host);
                    balancer.addServer(host);
                    //System.out.println("done adding alancer bhost " + host);                   

                }
                logger.logOptional(name, "started instance");

            } catch (Exception e) {
                logger.logOptional(name,
                        "help... exception in starting instance");
                //e.printStackTrace();

                try {
                    deployed.sfDetachAndTerminate(TerminationRecord.normal(null));
                } catch (Exception ex) {
                }
            }
        }
    }

    protected void stopInstance() throws Exception {
        synchronized (instanceLock) {
            logger.logOptional(name, "stoping instance");

            Prim child = (Prim) sfChildren.elementAt(0);
            String server = (String) childServerMapping.get(child);
            if (balancer != null) {
                balancer.removeServer(server);
            }
            child.sfDetachAndTerminate(TerminationRecord.normal(sfCompleteNameSafe()));
            childServerMapping.remove(child);
            stabilizationCounter = stabilizationMeasures;

            logger.logOptional(name, "stopped instance");
        }
    }

    /**
     * Overwrite of the compound - to protect against child termintions killing service
     *
     * @param status termination status of sender
     * @param comp   sender of termination
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        if (sfContainsChild(comp)) {
            try {
                sfRemoveChild(comp);
            } catch (Exception e) {
                //shouldn't happen
            }
            String server = (String) childServerMapping.get(comp);

            if (balancer != null) {
                try {
                    balancer.removeServer(server);
                } catch (Exception e) { // help - no recovery...
                    TerminatorThread terminator = new TerminatorThread(this, e, sfCompleteNameSafe());
                    terminator.start();
                }
            }
            childServerMapping.remove(comp);
        }
    }

    /**
     * Override of Compounds liveness failure handling
     *
     * @param source  source of update
     * @param target  target that update was trying to reach
     * @param failure error that occurred
     */
    protected void sfLivenessFailure(Object source, Object target, Throwable failure) {
        if (target.equals(sfParent)) {
            super.sfLivenessFailure(source, target, failure);
        } else {
            try {
                sfRemoveChild((Prim) target);
            } catch (Exception e) {
                //shouldn't happen
            }
            String server = (String) childServerMapping.get(target);
            try {
                balancer.removeServer(server);
            } catch (Exception e) { // help - no recovery...
                TerminatorThread terminator = new TerminatorThread(this, e, sfCompleteNameSafe());
                terminator.start();
            }
            childServerMapping.remove(target);
        }
    }

    protected synchronized void startThresholdPolling() {
        logger.logOptional(name, "starting threshold poller...");
        poller = new Poller();
        poller.start();
    }

    protected synchronized void stopThresholdPolling() {
        logger.logOptional(name, "stopping threshold poller...");
        pollingFinished = true;
    }

    protected class Poller extends Thread {
        public void run() {

            logger.logOptional(name, "poller running");

            while (!pollingFinished) {
                boolean removeOne = false;
                boolean addOne = false;
                int instances = currentInstances();
                logger.logOptional(name, "poller web server instances " + instances);

                try {
                    int value = dataSource.getData();
                    measures.add(new Integer(value));

                    logger.logOptional(name, "poller measure obtained " + value);


                    if (measures.size() > repeatMeasures) {
                        logger.logOptional(name, "poller has sufficient measures to proceed");

                        measures.remove(0);

                        // we now have enough measures to start testing...
                        if (stabilizationCounter > 0) {
                            // don't do anything if not stabilized...
                            logger.logOptional(name, "poller not stabilized yet");
                            stabilizationCounter--;
                        } else {
                            logger.logOptional(name, "poller stabilized");

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
                            logger.logOptional(name, "poller average of repeat measures is " + avg);

                            if (avg > upperThreshold) {
                                logger.logOptional(name, "poller upper threshold met...");
                                if (isAuto) {
                                    addOne = true;
                                }
                            } else if (avg < lowerThreshold) {
                                logger.logOptional(name, "poller lower threshold met...");
                                if (isAuto) {
                                    removeOne = true;
                                }
                            }
                        }
                    }

                    if (instances < minInstances) {
                        logger.logOptional(name, "too few instances");
                        addOne = true;
                    } else if (instances > maxInstances) {
                        logger.logOptional(name, "too many instances");
                        removeOne = true;
                    }

                    if (addOne && instances < maxInstances) {
                        logger.logOptional(name, "starting instance");
                        startInstance();
                    } else if (removeOne && instances > minInstances) {
                        logger.logOptional(name, "stopping instance");
                        stopInstance();
                    }

                    logger.logOptional(name, "poller sleeping");
                    sleep(pollFrequency);
                    logger.logOptional(name, "poller awake");
                } catch (Exception e) {
                    logger.logOptional(name, "excpetion caught in the poller");
                    e.printStackTrace();
                }
            }

            logger.logOptional(name, "poller stopped");
        }
    }
}
