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
    Implements an SLP advertiser component for SmartFrog.
    The component can be used to advertise any service.
    The service type, attributes and URL are set in the SF description.
*/
public class SFSlpAdvertiserImpl extends PrimImpl implements Prim, SFSlpAdvertiser {
    protected Advertiser advertiser;
    protected Properties properties;
    protected Vector serviceURLs;
    protected Vector serviceAttributes;
    
    public SFSlpAdvertiserImpl() throws RemoteException {
        super();
    }
    
    /**
        Creates the SLP advertiser object.
    */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();  
        properties = new Properties();

        // get slp configuration
        String s = (String)sfResolve("slp_config_interface", "", false);
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
        
        // get service adv params.
        Vector serviceTypes = (Vector)sfResolve(ATTRIB_SRVTYPE);
        Vector toAdvertise = (Vector)sfResolve(ATTRIB_TO_ADVERTISE);
        Vector lifetimes = (Vector)sfResolve(ATTRIB_LIFETIME);
        createAttributes( (Vector)sfResolve(ATTRIB_ATTRIBUTES) );
        
        // check that all vectors are the same size.
        int size = toAdvertise.size();
        if(serviceTypes.size() != size ||
           lifetimes.size() != size ||
           serviceAttributes.size() != size) {
            // error in .sf file
            throw new SmartFrogException("SLP: You need to specify a type/lifetime/attribute for EACH service to advertise");
        }
        
        // create the URLs
        serviceURLs = new Vector();
        buildURLs(toAdvertise, serviceTypes, lifetimes);
        
        // get advertiser
        ServiceLocationManager.setProperties(properties);
        try {
            advertiser = ServiceLocationManager.getAdvertiser(new Locale(properties.getProperty("net.slp.locale")));
        }catch(Exception ex) {
            throw (SmartFrogException) SmartFrogException.forward(ex);
        }
    }    
    
    /**
        Registers the service with the advertiser, starting the advertisement
    */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        try {
            // advertise URLs
            Iterator aIter = serviceAttributes.iterator();
            Iterator uIter = serviceURLs.iterator();
            while(uIter.hasNext()) {
                advertiser.register((ServiceURL)uIter.next(), (Vector)aIter.next());
            }
        }catch(Exception ex) {
            throw (SmartFrogException) SmartFrogException.forward(ex);
        }
    }
    
    /**
        Deregisters the service.
        After this, the service is no longer advertised.
    */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        // deregister the service(s).
        try {
            Iterator uIter = serviceURLs.iterator();
            while(uIter.hasNext()) {
                advertiser.deregister( (ServiceURL)uIter.next() );
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        
	super.sfTerminateWith(tr);
    }

    protected void createAttributes(Vector attributes) throws SmartFrogException {
        serviceAttributes = new Vector();
        Iterator iter = attributes.iterator();
        Vector attrs;
        Vector values;
        Vector srvAttribs;
        String id;
        try {
            while(iter.hasNext()) {
                srvAttribs = new Vector();
                attrs = (Vector)iter.next();
                for(Iterator aIter = attrs.iterator(); aIter.hasNext(); ) {
                    values = (Vector)aIter.next();
                    id = (String)values.remove(0);
                    ServiceLocationAttribute a = new ServiceLocationAttribute(id, values);
                    srvAttribs.add(a);
                }
                serviceAttributes.add(srvAttribs);
            }
        }catch(Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }


    protected void buildURLs(Vector toAdvertise, Vector serviceTypes, Vector lifetimes) throws SmartFrogException, RemoteException {
        Iterator srvIter = toAdvertise.iterator();
        Iterator typeIter = serviceTypes.iterator();
        Iterator lifeIter = lifetimes.iterator();
        
        while(srvIter.hasNext()) {
            String location = (String)srvIter.next();
            String sType = (String)typeIter.next();
            if(sType.equals("")) {
                throw new SmartFrogException("SLP: Empty service type given");
            }            
            int lifetime = ((Integer)lifeIter.next()).intValue();
            
            ServiceURL url = new ServiceURL(sType+"://"+location, lifetime);
            serviceURLs.add(url);
        }
    }
    
    public void register(ServiceURL url, Vector attributes) throws ServiceLocationException {
        advertiser.register(url, attributes);
        serviceURLs.add(url);
    }
    
    public void deregister(ServiceURL url) throws ServiceLocationException {
        advertiser.deregister(url);
        serviceURLs.remove(url);
    }
}

