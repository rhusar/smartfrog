/*
 Service Location Protocol - SmartFrog components.
 Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
 
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
 
 This library was originally developed by Glenn Hisdal at the 
 European Organisation for Nuclear Research (CERN) in Spring 2004. 
 The work was part of a master thesis project for the Norwegian 
 University of Science and Technology (NTNU).
 
 For more information: http://home.c2i.net/ghisdal/slp.html 
 */

package org.smartfrog.services.comm.slp;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

/**
 * Implements an SLP Locator component for SmartFrog. Can be used to locate any service. The result of the discovery is
 * a ServiceLocationEnumeration with the discovered Service URLs.
 */
public class SFSlpLocatorImpl extends PrimImpl implements Prim, SFSlpLocator {
    protected Locator locator;
    protected ServiceType serviceType;
    protected Vector scope_list;
    protected String searchFilter;
    protected ServiceLocationEnumeration discoveryResults = null;
    protected int discoveryInterval = 0;
    protected int discoveryDelay = 0;
    protected boolean returnEnumeration = false;
    private SmartFrogThread locatorThread = null;
    private Object wtSync = new Object();
    private volatile boolean amWaiting = true;
    private volatile boolean runThread = true;
    private LogSF slpLog = null;
    public static final String EXCEPTION_NO_SERVICE_TYPE = "SLP: No service type given";
    public static final String EXCEPTION_NO_SLP_SERVICE = "No SLP service found";
    private Throwable discoveryException;

    public SFSlpLocatorImpl() throws RemoteException {
        super();
    }

    /** Gets an SLP Locator object, and starts a thread to locate services. */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        Properties properties = new Properties();
        // get slp configuration
        String s = (String) sfResolve(ATTR_SLP_CONFIG_INTERFACE);
        if (!s.equals("")) properties.setProperty("net.slp.interface", s);
        properties.setProperty("net.slp.multicastMaximumWait", sfResolve(ATTR_SLP_CONFIG_MC_MAX).toString());
        properties.setProperty("net.slp.randomWaitBound", sfResolve(ATTR_SLP_CONFIG_RND_WAIT).toString());
        properties.setProperty("net.slp.initialTimeout", sfResolve(ATTR_SLP_CONFIG_RETRY).toString());
        properties.setProperty("net.slp.unicastMaximumWait", sfResolve(ATTR_SLP_CONFIG_RETRY_MAX).toString());
        properties.setProperty("net.slp.DAActiveDiscoveryInterval", sfResolve(ATTR_SLP_CONFIG_DA_FIND).toString());
        properties.setProperty("net.slp.DAAddresses", sfResolve(ATTR_SLP_CONFIG_DA_ADDRESSES).toString());
        properties.setProperty("net.slp.useScopes", sfResolve(ATTR_SLP_CONFIG_SCOPE_LIST).toString());
        properties.setProperty("net.slp.mtu", sfResolve(ATTR_SLP_CONFIG_MTU).toString());
        properties.setProperty("net.slp.port", sfResolve(ATTR_SLP_CONFIG_PORT).toString());
        properties.setProperty("net.slp.locale", sfResolve(ATTR_SLP_CONFIG_LOCALE).toString());
        properties.setProperty("net.slp.multicastAddress", sfResolve(ATTR_SLP_CONFIG_MC_ADDR).toString());
        properties.setProperty("net.slp.debug", sfResolve(ATTR_SLP_CONFIG_DEBUG).toString());
        properties.setProperty("net.slp.logErrors", sfResolve(ATTR_SLP_CONFIG_LOG_ERRORS).toString());
        properties.setProperty("net.slp.logMsg", sfResolve(ATTR_SLP_CONFIG_LOG_MSG).toString());
        properties.setProperty("net.slp.logfile", sfResolve(ATTR_SLP_CONFIG_LOGFILE).toString());
        properties.setProperty("net.slp.sflog", sfResolve(ATTR_SLP_CONFIG_SFLOG).toString());

        // get locator configuration
        discoveryDelay = ((Integer) sfResolve(ATTR_LOCATOR_DISCOVERY_DELAY)).intValue();
        discoveryInterval = ((Integer) sfResolve(ATTR_LOCATOR_DISCOVERY_INTERVAL)).intValue();
        returnEnumeration = ((Boolean) sfResolve(ATTR_RETURN_ENUMERATION)).booleanValue();

        // get parameters for service discovery.
        String srvTypeStr = sfResolve(ATTR_SERVICE_TYPE).toString();
        if (srvTypeStr.equals("")) {
            throw new SmartFrogResolutionException(EXCEPTION_NO_SERVICE_TYPE, this);
        }
        serviceType = new ServiceType(srvTypeStr);
        searchFilter = sfResolve(ATTR_SEARCH_FILTER).toString();
        scope_list = (Vector) sfResolve(ATTR_SEARCH_SCOPES);

        // get the locator
        ServiceLocationManager.setProperties(properties);
        locator = ServiceLocationManager.getLocator(new Locale(properties.getProperty("net.slp.locale")));
        if (scope_list.isEmpty()) scope_list = ServiceLocationManager.findScopes();
        // get SmartFrog log, if requested.
        if (properties.getProperty("net.slp.sflog").equalsIgnoreCase("true")) {
            slpLog = sfGetLog(properties.getProperty("net.slp.logfile"));
            locator.setSFLog(slpLog);
        }

        // start locator thread
        locatorThread = new SmartFrogThread() {
            public void execute() throws Throwable {
                locateServices();
            }
        };
        locatorThread.start();
    }

    /** Stops the locator thread. */
    public synchronized void sfTerminateWith(TerminationRecord r) {
        runThread = false; // will stop the locator thread.
        super.sfTerminateWith(r);
    }

    /**
     * Override the parent by looking for a child called result over SLP.
     *
     * @param r
     * @param index
     * @return any resolved object
     * @throws SmartFrogResolutionException if the SLP service could not be found, or resolution failed for other
     *                                      reasons.
     * @throws RemoteException
     */
    public synchronized Object sfResolve(Reference r, int index) throws SmartFrogResolutionException, RemoteException {
        ReferencePart part = r.elementAt(index);
        if (part instanceof HereReferencePart) {
            HereReferencePart here = (HereReferencePart) part;
            Object value = here.getValue();
            if (ATTR_RESULT.equals(value.toString())) {
                sfLog().debug("Starting service discovery...");
                // discover it...
                if (discoveryResults == null) {
                    waitForDiscovery();
                }
                sfLog().debug("Discovery completed...");
                if(discoveryException!=null) {
                    throw (SmartFrogResolutionException) SmartFrogResolutionException.forward(discoveryException);
                }
                if(discoveryResults==null) {
                    throw new SmartFrogResolutionException("No discovery results");
                }
                if (returnEnumeration) {
                    // return the unmodified enumeration
                    return discoveryResults;
                } else {
                    // return first object.
                    return getDiscoveredObject(discoveryResults);
                }
            }
        }
        return super.sfResolve(r, index);

    }

    /** Runs the thread used to locate services. */
    protected void locateServices() {
        if (discoveryDelay != 0) {
            try {
                Thread.sleep(discoveryDelay);
            } catch (InterruptedException ie) {
            }
        }
        while (runThread) {
            try {
                if (serviceType != null) {
                    discoveryResults = locator.findServices(serviceType, scope_list, searchFilter);
                }
                stopWaiting();
            } catch (ServiceLocationException ex) {
                discoveryException= ex;
                // error during discovery.
                if (slpLog != null) {
                    slpLog.error("Error during discovery", ex);
                }
                sfLog().error("Error during discovery", ex);
                // stop thread...
                runThread = false;
                stopWaiting();
            }
            if (discoveryInterval == 0) {
                runThread = false;
            } else {
                try {
                    Thread.sleep(discoveryInterval);
                } catch (InterruptedException ie) {
                }
            }
        }
    }

    /** Waits until the service discovery is completed. Used from sfResolve. */
    protected void waitForDiscovery() {
        synchronized (wtSync) {
            while (amWaiting) {
                try {
                    wtSync.wait();
                } catch (Exception e) {
                }
            }
            amWaiting = true;
        }
    }

    /** Called when the service discovery is complete. */
    protected void stopWaiting() {
        synchronized (wtSync) {
            amWaiting = false;
            wtSync.notifyAll();
        }
    }

    /**
     * Look up an object
     *
     * @param sle an enumeration to use
     * @return the object discovered.
     * @throws SmartFrogResolutionException if discovery failed
     */
    protected Object getDiscoveredObject(ServiceLocationEnumeration sle) throws SmartFrogResolutionException {

        if (sle.hasMoreElements()) {
            ServiceURL url = (ServiceURL) sle.nextElement();
            // if the URL has a host part, we have a String or InetAddress.
            String host = url.getHost();
            if (host.length()==0) {
                String path = url.getURLPath();
                if (path.length() == 0) {
                    return host + path; // String with hostname and path.
                }
                // No path: create InetAddress.
                try {
                    InetAddress addr = InetAddress.getByName(host);
                    return addr;
                } catch (UnknownHostException unknown) {
                    return host; // returning host as String
                }
            }
            // no host => some object. Could be String/Integer/RemoteStub, ...
            // try to get object from URLPath.
            Object pathObject = url.getURLPathObject();
            if (pathObject == null) {
                // String/number/boolean. Currently returning String...
                return url.getURLPath().substring(1);
            } else {
                return pathObject; // returning object.
            }
        }

        throw new SmartFrogResolutionException(EXCEPTION_NO_SLP_SERVICE, this);
    }
}

