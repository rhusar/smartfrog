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

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.reference.Reference;

import java.util.Vector;
import java.util.Locale;
import java.util.Properties;

import org.smartfrog.services.comm.slp.ServiceURL;
import org.smartfrog.services.comm.slp.ServiceLocationEnumeration;
import org.smartfrog.services.comm.slp.ServiceLocationManager;
import org.smartfrog.services.comm.slp.Locator;
import org.smartfrog.services.comm.slp.ServiceType;
import org.smartfrog.services.comm.slp.util.SLPDefaults;

/**
    A special SmartFrog deployer class that will use SLP to look for an
    advertised ProcessCompound in which to deploy the component.
*/
public class SFSlpDeployerImpl extends PrimProcessDeployerImpl {
    protected static final String SERVICE_TYPE = "service:sf-processcompound";
    protected static final Reference refConcreteType = new Reference("concreteType");
    protected static final Reference refSearchFilter = new Reference("searchFilter");
    protected static final Reference refScopes = new Reference("searchScopes");
    protected static final Reference refConfig = new Reference("slpConfig");
    protected static final Reference isDone = new Reference("discoveryDone");
    
    public SFSlpDeployerImpl(ComponentDescription descr) {
        super(descr);
    }
    
    protected ProcessCompound getProcessCompound() throws Exception {
        // get concrete type, search filter and scopes...
        String concreteType=null, searchFilter=null;
        Vector scopes=null;
        try {
            concreteType = (String)target.sfResolve(refConcreteType);
        }catch(SmartFrogResolutionException ex) {
            concreteType = "";
        }
        try {
            searchFilter = (String)target.sfResolve(refSearchFilter);
        }catch(SmartFrogResolutionException ex) {
            searchFilter = "";
        }
        try {
            scopes = (Vector)target.sfResolve(refScopes);
        }catch(SmartFrogResolutionException ex) {
            // don't care...
        }
        
        // create service type
        String serviceType = SERVICE_TYPE;
        if(!concreteType.equals("")) serviceType += ":"+concreteType;
        
        // perform SLP Discovery...
        boolean discoveryCompleted = true;
        try {
            target.sfResolve(isDone);
        }catch(SmartFrogResolutionException e) {
            discoveryCompleted = false;
        }
        if(!discoveryCompleted) {
            ServiceLocationEnumeration urls = null;
            ServiceType type = null;
            Context ctxt = target.getContext();
            try {
                if(scopes == null || scopes.isEmpty()) {
                    scopes = ServiceLocationManager.findScopes();
                }
                type = new ServiceType(serviceType);
                ServiceLocationManager.setProperties( getSlpConfiguration(ctxt) );
                Locator loc = ServiceLocationManager.getLocator(new Locale("en"));
                urls = loc.findServices(type, scopes, searchFilter);
            
                // take the first URL
                ServiceURL url = (ServiceURL)urls.nextElement();
            
                if(url != null) {
                    // set smartfrog attributes to use...
                    ctxt.put("sfProcessHost", url.getHost());
                    String pname = url.getURLPath();
                    if(pname.startsWith("/")) pname = pname.substring(1);
                    if(!pname.equals("")) {
                        ctxt.put("sfProcessName", pname);
                    }
                }
            }catch(Exception ex) {
                // don't care...
                System.out.println(ex.toString());
                ex.printStackTrace();
            }
            ctxt.put("discoveryDone", "true");
            ctxt.remove("slpConfig");
        }
        return super.getProcessCompound();
    }
    
    protected Properties getSlpConfiguration(Context context) {
        Properties properties = new Properties( SLPDefaults.getDefaultProperties() );
        // try to find configuration
        ComponentDescription descr = null;
        try {
            descr = (ComponentDescription)target.sfResolve(refConfig);
        }catch(SmartFrogResolutionException ex) {
            return properties; // use defaults...
        }
        
        // read configuration...
        try {
            String s = (String)sfResolve(descr, "slp_config_interface");
            if(!s.equals("")) properties.setProperty("net.slp.interface", s);
            properties.setProperty("net.slp.multicastMaximumWait", sfResolve(descr, "slp_config_mc_max").toString());
            properties.setProperty("net.slp.randomWaitBound", sfResolve(descr, "slp_config_rnd_wait").toString());
            properties.setProperty("net.slp.initialTimeout", sfResolve(descr, "slp_config_retry").toString());
            properties.setProperty("net.slp.unicastMaximumWait", sfResolve(descr, "slp_config_retry_max").toString());
            properties.setProperty("net.slp.DAActiveDiscoveryInterval", sfResolve(descr, "slp_config_da_find").toString());
            properties.setProperty("net.slp.DAAddresses", sfResolve(descr, "slp_config_daAddresses").toString());
            properties.setProperty("net.slp.useScopes", sfResolve(descr, "slp_config_scope_list").toString());
            properties.setProperty("net.slp.mtu", sfResolve(descr, "slp_config_mtu").toString());
            properties.setProperty("net.slp.port", sfResolve(descr, "slp_config_port").toString());
            properties.setProperty("net.slp.locale", sfResolve(descr, "slp_config_locale").toString());
            properties.setProperty("net.slp.multicastAddress", sfResolve(descr, "slp_config_mc_addr").toString());
            properties.setProperty("net.slp.debug", sfResolve(descr, "slp_config_debug").toString());
            properties.setProperty("net.slp.logErrors", sfResolve(descr, "slp_config_log_errors").toString());
            properties.setProperty("net.slp.logMsg", sfResolve(descr, "slp_config_log_msg").toString());
            properties.setProperty("net.slp.logfile", sfResolve(descr, "slp_config_logfile").toString());
        }catch(Exception e) {
            // ignored...
            System.out.println(e.toString());
            e.printStackTrace();
        }
        
        return properties;
    }
    
    private Object sfResolve(ComponentDescription cd, String ref) throws SmartFrogResolutionException {
        return cd.sfResolve(new Reference(ref));
    }
}

