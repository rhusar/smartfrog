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

import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.common.*;

import java.util.*;
import java.net.InetAddress;

import java.rmi.*;

/**
    Implements an SLP Locator component for SmartFrog.
    Can be used to locate any service. The result of the discovery is
    a ServiceLocationEnumeration with the discovered Service URLs.
*/
public class SFSlpLocatorImpl extends PrimImpl implements Prim, SFSlpLocator {
    protected Locator locator;
    protected ServiceType serviceType;
    protected Vector scope_list;
    protected String searchFilter;
    protected ServiceLocationEnumeration discoveryResults = null;
    protected int discoveryInterval = 0;
    protected int discoveryDelay = 0;
    private Thread locatorThread = null;
    private Object wtSync = new Object();
    private volatile boolean amWaiting = true;
    private boolean runThread = true;
    
    public SFSlpLocatorImpl() throws RemoteException {
        super();
    }
    
    // lifecycle methods...
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();  

        Properties properties = new Properties();
        // get slp configuration
        String s = (String)sfResolve("slp_config_interface");
        if(!s.equals("")) properties.setProperty("net.slp.interface", s);
        properties.setProperty("net.slp.multicastMaximumWait", sfResolve("slp_config_mc_max").toString());
        properties.setProperty("net.slp.randomWaitBound", sfResolve("slp_config_rnd_wait").toString());
        properties.setProperty("net.slp.initialTimeout", sfResolve("slp_config_retry").toString());
        properties.setProperty("net.slp.unicastMaximumWait", sfResolve("slp_config_retry_max").toString());
        properties.setProperty("net.slp.DAActiveDiscoveryInterval", sfResolve("slp_config_da_find").toString());
        properties.setProperty("net.slp.DAAddresses", sfResolve("slp_config_daAddresses").toString());
        properties.setProperty("net.slp.useScopes", sfResolve("slp_config_scope_list").toString());
        properties.setProperty("net.slp.mtu", sfResolve("slp_config_mtu").toString());
        properties.setProperty("net.slp.port", sfResolve("slp_config_port").toString());
        properties.setProperty("net.slp.locale", sfResolve("slp_config_locale").toString());
        properties.setProperty("net.slp.multicastAddress", sfResolve("slp_config_mc_addr").toString());
        properties.setProperty("net.slp.debug", sfResolve("slp_config_debug").toString());
        properties.setProperty("net.slp.logErrors", sfResolve("slp_config_log_errors").toString());
        properties.setProperty("net.slp.logMsg", sfResolve("slp_config_log_msg").toString());
        properties.setProperty("net.slp.logfile", sfResolve("slp_config_logfile").toString());
        
        // get locator configuration
        discoveryDelay = ((Integer)sfResolve("locator_discovery_delay")).intValue();
        discoveryInterval = ((Integer)sfResolve("locator_discovery_interval")).intValue();
        
        // get parameters for service discovery.
        String srvTypeStr = sfResolve("serviceType").toString();
        if(srvTypeStr.equals("")) {
            throw new SmartFrogException("SLP: No service type given");
        }
        serviceType = new ServiceType(srvTypeStr);
        searchFilter = sfResolve("searchFilter").toString();
        scope_list = (Vector)sfResolve("searchScopes");
        
        // get the locator
        ServiceLocationManager.setProperties(properties);
        try {
            locator = ServiceLocationManager.getLocator(new Locale(properties.getProperty("net.slp.locale")));
            if(scope_list.isEmpty())scope_list = ServiceLocationManager.findScopes();
        }catch(Exception ex) {
            ex.printStackTrace();
            throw (SmartFrogException) SmartFrogException.forward(ex);
        }
        
        // start locator thread
        locatorThread = new Thread() {
            public void run() {
                locateServices();
            }
        };
        locatorThread.start();
    }
    
    
    public synchronized void sfTerminateWith(TerminationRecord r) {
        runThread = false; // will stop the locator thread.
        super.sfTerminateWith(r);
    }
    
    public synchronized Object sfResolve(Reference r, int index) throws SmartFrogResolutionException {
        // If the requested object is the result, we try to find services using SLP.
        String s = r.elementAt(index).toString();
        try {
            if("HERE result".equals(s) ) {
                //System.out.println("Starting service discovery...");
                // discover it...
                if(discoveryResults == null) {
                    waitForDiscovery();
                }
                //System.out.println("Discovery completed...");
                return getDiscoveredObject(discoveryResults);
            }
            else {
                return super.sfResolve(r, index);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
            throw (SmartFrogResolutionException) SmartFrogResolutionException.forward(ex);
        }
    }
    
    /**
        Runs the thread used to locate services.
    */
    protected void locateServices() {
        if(discoveryDelay != 0) {
            try {
                Thread.sleep(discoveryDelay);
            }catch(InterruptedException ie) { }
        }
        while(runThread) {
            try {
                if(serviceType != null) {
                    discoveryResults = locator.findServices(serviceType, scope_list, searchFilter);
                }
                stopWaiting();
            }catch(ServiceLocationException ex) {
                // error during discovery.
                ex.printStackTrace();
                // stop thread...
                runThread = false;
                stopWaiting();
            }
            if(discoveryInterval == 0) {
                runThread = false;
            }
            else {
                try {
                    Thread.sleep(discoveryInterval);
                }catch(InterruptedException ie) { }
            }
        }
    }
    
    /**
        Waits until the service discovery is completed.
        Used from sfResolve.
    */
    protected void waitForDiscovery() {
        synchronized(wtSync) {
            while(amWaiting) {
                try {
                    wtSync.wait();
                }catch(Exception e) { }
            }
            amWaiting = true;
        }
    }
    
    /**
        Called when the service discovery is complete.
    */
    protected void stopWaiting() {
        synchronized(wtSync) {
            amWaiting = false;
            wtSync.notifyAll();
        }
    }
    
    /**
        Returns the object discovered.
    */
    protected Object getDiscoveredObject(ServiceLocationEnumeration sle) throws SmartFrogException {
        while(sle.hasMoreElements()) {
            ServiceURL url = (ServiceURL)sle.nextElement();
            // if the URL has a host part, we have a String or InetAddress.
            String host = url.getHost();
            if(!host.equals("")) {
                String path = url.getURLPath();
                if(!path.equals("")) {
                    return host+path; // String with hostname and path.
                }
                // No path: create InetAddress.
                try {
                    InetAddress addr = InetAddress.getByName(host);
                    return addr;
                }catch(Exception ex) {
                    return host; // returning host as String
                }
            }
            // no host => some object. Could be String/Integer/RemoteStub, ...
            // try to get object from URLPath.
            Object pathObject = url.getURLPathObject();
            if(pathObject == null) {
                // String/number/boolean. Currently returning String...
                return url.getURLPath().substring(1);
            }
            else {
                return pathObject; // returning object.
            }
        }
        
        throw new SmartFrogException("SLP: No service found");
    }
}

