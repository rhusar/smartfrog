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

import org.smartfrog.services.comm.slp.util.SLPDefaults;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.reference.Reference;

import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

/**
 * A special SmartFrog deployer class that will use SLP to look for an advertised ProcessCompound in which to deploy the
 * component.
 */
public class SFSlpDeployerImpl extends PrimProcessDeployerImpl {
    protected static final Reference refServiceType = new Reference("serviceType");
    protected static final Reference refSearchFilter = new Reference("searchFilter");
    protected static final Reference refScopes = new Reference("searchScopes");
    protected static final Reference refConfig = new Reference("slpConfig");
    protected static final Reference isDone = new Reference("discoveryDone");

    protected String serviceType = null;
    protected String searchFilter = null;
    protected Vector scopes = null;

    public SFSlpDeployerImpl(ComponentDescription descr) {
        super(descr);
    }

    /** Does a search for a ProcessCompound using SLP. */
    protected ProcessCompound getProcessCompound() throws Exception {

        // perform SLP Discovery...
        boolean discoveryCompleted = true;
        try {
            target.sfResolve(isDone);
        } catch (SmartFrogResolutionException e) {
            discoveryCompleted = false;
        }
        if (!discoveryCompleted) {
            ServiceLocationEnumeration urls = null;
            ServiceType type = null;
            Properties p = getSlpConfiguration();
            try {
                if (scopes == null || scopes.isEmpty()) {
                    scopes = ServiceLocationManager.findScopes();
                }
                type = new ServiceType(serviceType);
                ServiceLocationManager.setProperties(p);
                Locator loc = ServiceLocationManager.getLocator(new Locale(p.getProperty("net.slp.locale")));
                urls = loc.findServices(type, scopes, searchFilter);

                // take the first URL
                ServiceURL url = (ServiceURL) urls.nextElement();

                if (url != null) {
                    // set smartfrog attributes to use...
                    target.sfReplaceAttribute("sfProcessHost", url.getHost());
                    String pname = url.getURLPath();
                    if (!pname.equals("")) {
                        target.sfReplaceAttribute("sfProcessName", pname.substring(1));
                    }
                }
            } catch (Exception ex) {
                // don't care...
                System.out.println(ex.toString());
                ex.printStackTrace();
            }
            target.sfReplaceAttribute("discoveryDone", "true");
            target.sfRemoveAttribute("slpConfig");
        }
        return super.getProcessCompound();
    }

    /** Reads the SLP configuration from the description. */
    protected Properties getSlpConfiguration() throws SmartFrogResolutionException {
        Properties properties = new Properties(SLPDefaults.getDefaultProperties());
        // try to find configuration
        ComponentDescription descr = null;

        // get the SLP configuration.
        // This component description MUST be present with at least the service type given.
        descr = (ComponentDescription) target.sfResolve(refConfig);

        // get service type, filter and scopes
        serviceType = (String) descr.sfResolve(refServiceType); // required
        try {
            searchFilter = (String) descr.sfResolve(refSearchFilter);
        } catch (SmartFrogResolutionException ex) {
            searchFilter = "";
        }
        try {
            scopes = (Vector) descr.sfResolve(refScopes);
        } catch (SmartFrogResolutionException ex) {
            // don't care...
        }

        // read configuration...
        try {
            String s = (String) descr.sfResolve("slp_config_interface");
            if (!s.equals("")) properties.setProperty("net.slp.interface", s);
            properties.setProperty("net.slp.multicastMaximumWait", descr.sfResolve("slp_config_mc_max").toString());
            properties.setProperty("net.slp.randomWaitBound", descr.sfResolve("slp_config_rnd_wait").toString());
            properties.setProperty("net.slp.initialTimeout", descr.sfResolve("slp_config_retry").toString());
            properties.setProperty("net.slp.unicastMaximumWait", descr.sfResolve("slp_config_retry_max").toString());
            properties.setProperty("net.slp.DAActiveDiscoveryInterval", descr.sfResolve("slp_config_da_find").toString());
            properties.setProperty("net.slp.DAAddresses", descr.sfResolve("slp_config_daAddresses").toString());
            properties.setProperty("net.slp.useScopes", descr.sfResolve("slp_config_scope_list").toString());
            properties.setProperty("net.slp.mtu", descr.sfResolve("slp_config_mtu").toString());
            properties.setProperty("net.slp.port", descr.sfResolve("slp_config_port").toString());
            properties.setProperty("net.slp.locale", descr.sfResolve("slp_config_locale").toString());
            properties.setProperty("net.slp.multicastAddress", descr.sfResolve("slp_config_mc_addr").toString());
            properties.setProperty("net.slp.debug", descr.sfResolve("slp_config_debug").toString());
            properties.setProperty("net.slp.logErrors", descr.sfResolve("slp_config_log_errors").toString());
            properties.setProperty("net.slp.logMsg", descr.sfResolve("slp_config_log_msg").toString());
            properties.setProperty("net.slp.logfile", descr.sfResolve("slp_config_logfile").toString());
        } catch (Exception e) {
            // ignored...
            System.out.println(e.toString());
            e.printStackTrace();
        }

        return properties;
    }
}

