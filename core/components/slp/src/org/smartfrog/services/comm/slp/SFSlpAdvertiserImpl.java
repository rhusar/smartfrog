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

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.reference.Reference;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.RemoteStub;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

public class SFSlpAdvertiserImpl extends PrimImpl implements Prim, SFSlpAdvertiser {
    /** The Advertiser object */
    protected Advertiser advertiser;
    /** The ServiceURL for the advertised service */
    protected ServiceURL serviceURL;
    /** The Service Type for the advertised service */
    protected String serviceType;
    /** The attributes for the advertised service */
    protected Vector serviceAttributes;
    /** The lifetime for the advertised service */
    protected int serviceLifetime;
    /** The advertised object. (String/Prim/Integer/Boolean...) */
    protected Object toAdvertise;
    /** Advertise reference ? */
    protected boolean advertiseReference;
    /** Log */
    protected LogSF slpLog = null;

    // references.
    public static final Reference toAdvertiseRef = new Reference(ATTR_TO_ADVERTISE);
    public static final Reference serviceTypeRef = new Reference(ATTR_SERVICE_TYPE);
    public  static final String ATTR_SERVICE_ATTRIBUTES = "serviceAttributes";
    public static final Reference serviceAttributeRef = new Reference(ATTR_SERVICE_ATTRIBUTES);
    public static final Reference serviceLifetimeRef = new Reference(ATTR_SERVICE_LIFETIME);
    public static final Reference advertiseReferenceRef = new Reference(ATTR_ADVERTISE_REFERENCE);

    public SFSlpAdvertiserImpl() throws RemoteException {
        super();
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        // get configureation for SLP
        Properties p = getSlpConfiguration();

        // get properties for the service to advertise.
        toAdvertise = sfContext().get(ATTR_TO_ADVERTISE); //sfResolve(toAdvertiseRef);
        if (toAdvertise == null) {
            throw new SmartFrogException("SLP: Could not find '"+ATTR_TO_ADVERTISE+"' attribute");
        }
        serviceType = (String) sfResolve(serviceTypeRef);
        serviceAttributes = (Vector) sfResolve(serviceAttributeRef);
        serviceLifetime = ((Integer) sfResolve(serviceLifetimeRef)).intValue();
        advertiseReference = ((Boolean) sfResolve(advertiseReferenceRef)).booleanValue();

        // convert attributes to ServiceLocationAttribute objects.
        Iterator iter = serviceAttributes.iterator();
        serviceAttributes = new Vector();
        while (iter.hasNext()) {
            Vector v = (Vector) iter.next();
            String id = (String) v.remove(0); // remove id.
            ServiceLocationAttribute a = new ServiceLocationAttribute(id, v);
            serviceAttributes.add(a);
        }

        // check if we want to advertise reference.
        if (toAdvertise instanceof Reference) {
            if (advertiseReference) {
                // build reference to advertise
                Reference ref = sfCompleteName();
                ref.addElements((Reference) toAdvertise);
                toAdvertise = ref;
            } else {
                // get the object to advertise
                toAdvertise = sfResolve(toAdvertiseRef);
            }
        }

        // build URL.
        createServiceURL();

        // create advertiser...
        try {
            ServiceLocationManager.setProperties(p);
            advertiser = ServiceLocationManager.getAdvertiser(new Locale(p.getProperty("net.slp.locale")));
            // create log, if requested.
            if (p.getProperty("net.slp.sflog").equalsIgnoreCase("true")) {
                slpLog = sfGetLog(p.getProperty("net.slp.logfile"));
                advertiser.setSFLog(slpLog);
            }
        } catch (ServiceLocationException ex) {
            throw SmartFrogException.forward(ex);
        }
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        // advertise service...
        try {
            advertiser.register(serviceURL, serviceAttributes);
        } catch (ServiceLocationException ex) {
            throw  SmartFrogException.forward(ex);
        }
    }

    public synchronized void sfTerminateWith(TerminationRecord r) {
        //deregister service
        try {
            advertiser.deregister(serviceURL);
        } catch (ServiceLocationException ex) {
        }

        super.sfTerminateWith(r);
    }

    protected Properties getSlpConfiguration() throws SmartFrogException, RemoteException {
        Properties properties = new Properties();
        String s = (String) sfResolve("slp_config_interface", "", false);
        if (!s.equals("")) properties.setProperty("net.slp.interface", s);
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
        properties.setProperty("net.slp.sflog", sfResolve("slp_config_sflog").toString());

        return properties;
    }

    protected void createServiceURL() throws SmartFrogException, RemoteException {
        if (toAdvertise instanceof Prim) {
            createRemoteStubURL();
        } else if (toAdvertise instanceof Number ||
                toAdvertise instanceof Boolean) {
            serviceURL = new ServiceURL(serviceType + ":///" + toAdvertise.toString(), serviceLifetime);
        } else if (toAdvertise instanceof InetAddress) {
            serviceURL = new ServiceURL(serviceType + "://" + ((InetAddress) toAdvertise).getCanonicalHostName());
        } else if (toAdvertise instanceof String) {
            createStringURL();
        } else {
            // Some object. Put base64 encoded string representation of
            // object in the URLPath.
            serviceURL = new ServiceURL(serviceType, toAdvertise, serviceLifetime);
        }
    }

    protected void createRemoteStubURL() throws SmartFrogException {
        RemoteStub s;
        if (toAdvertise instanceof RemoteStub) {
            s = (RemoteStub) toAdvertise;
        } else {
            s = (RemoteStub) ((PrimImpl) toAdvertise).sfExportRef();
        }

        serviceURL = new ServiceURL(serviceType, s, serviceLifetime);
    }

    protected void createStringURL() throws SmartFrogException, RemoteException {
        Context c = sfContext();
        Object value = c.get(ATTR_TO_ADVERTISE);
        if (value instanceof Reference) {
            // check if we are advertising the process compound.
            Reference r = (Reference) value;
            if (r.toString().endsWith("sfProcess")) {
                // advertising process compound. Need to get hostname.
                r.setElementAt(new HereReferencePart("sfHost"), r.size() - 1);
                InetAddress host = (InetAddress) sfResolve(r);
                serviceURL = new ServiceURL(serviceType + "://" + host.getCanonicalHostName() + "/" + toAdvertise.toString(),
                        serviceLifetime);
                return;
            }
        }

        // Check if string could be a URL.
        String adv = toAdvertise.toString();
        String host = adv;
        String path = "";
        if (adv.indexOf("/") != -1) {
            host = adv.substring(0, adv.indexOf("/"));
            path = adv.substring(adv.indexOf("/"));
        }
        boolean url = false;
        try {
            InetAddress a = InetAddress.getByName(host);
            String urlStr = a.getHostName() + path;
            serviceURL = new ServiceURL(serviceType + "://" + urlStr, serviceLifetime);
            url = true;
        } catch (Exception ex) {
        }

        if (!url) {
            // not a url. Just add String to URLPath.
            serviceURL = new ServiceURL(serviceType + ":///" + toAdvertise.toString(), serviceLifetime);
        }
    }
}

