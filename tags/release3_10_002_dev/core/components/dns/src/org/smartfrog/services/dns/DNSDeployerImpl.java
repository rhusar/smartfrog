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

package org.smartfrog.services.dns;

import org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.xbill.DNS.Resolver;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;






/**
 * A prim deployer that uses DNS SRV records to locate process compounds.
 *
 * 
 * 
 */
public class DNSDeployerImpl extends PrimProcessDeployerImpl {


    /** Whether to ignore exceptions and keep on trying with other target. */
    boolean tryAll = false;

    /** Whether to  to query the user to select the target. */
    boolean interactive = false;
    
    /**  A default name for the service exported by a SF deployment engine. */
    String serviceTypeName = null;

    /**  A set of attribute/value pairs that must match contents in the
       TXT record.*/
    ComponentDescription mustHave = 
        new ComponentDescriptionImpl(null,
                                     new ContextImpl(), false);
    
    /** An optional ip address for the DNS server. */
    String dnsResolverAddress = null;

    /** A default port for the DNS server. */
    int dnsResolverPort = DNSComponent.DEFAULT_PORT;

    /** Use TCP for DNS queries. */
    boolean dnsResolverTCP = true;

    /** All the valid service instances that have been found by
        this deployer. */
    DNSServiceInstance[] allInstances = new DNSServiceInstance[0];

    /** An attribute name for alternative location info of the process
        compound. */
    public static final String SF_PROCESS_LOCATOR = "sfProcessLocator";


    /** An attribute name for this deployer configuration. */
    public static final String DNS_DEPLOYER = "dnsDeployer";


     /** Efficiency holder of sfProcessHost attribute. */
    protected static final Reference REF_PROCESS_HOST =
        new Reference(SmartFrogCoreKeys.SF_PROCESS_HOST);

    /** Efficiency holder of sfRootLocatorPort attribute. */
    protected static final Reference REF_ROOT_LOCATOR_PORT =
        new Reference(SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT);

    /** Efficiency holder of sfProcessLocator attribute. */
    protected static final Reference REF_PROCESS_LOCATOR =
        new Reference(SF_PROCESS_LOCATOR);

    /** Efficiency holder of dnsDeployer attribute. */
    protected static final Reference REF_DNS_DEPLOYER =
        new Reference(DNS_DEPLOYER);

    /** Efficiency holder for sfProcessName reference. */
    protected static final Reference REF_PROCESS_NAME = new Reference(
                SmartFrogCoreKeys.SF_PROCESS_NAME);


    // Attributes in the sf description

    public static final String TRY_ALL = "tryAll";
    public static final String INTERACTIVE = "interactive";
    public static final String DNS_RESOLVER_TCP = "dnsResolverTCP";
    public static final String DNS_RESOLVER_PORT = "dnsResoverPort";
    public static final String MUST_HAVE = "mustHave";
    public static final String DNS_RESOLVER_ADDRESS = "dnsResolverAddress";
    public static final String SERVICE_TYPE_NAME = "serviceTypeName";

   /**
     * Creates a new <code>DNSDeployerImpl</code> instance.
     *
     * @param descr a <code>ComponentDescription</code> value
     */
    public DNSDeployerImpl(ComponentDescription descr) {

        super(descr);
    }


    /**
     * Adds the process locator info to a description. It should 
     * sign the sub-description if security is on.
     *
     * @param cd A target description to include the process locator info.
     * @param service A service that provides the process locator info.
     */
    public static void addProcessLocator(ComponentDescription cd, 
                                         DNSServiceInstance service) {

        Context topCtx = cd.sfContext();
        Context plCtx = new ContextImpl();
        // Patch the service address in case I use a custom DNS server ...
        plCtx.put(SmartFrogCoreKeys.SF_PROCESS_HOST, service.getHostAddress());
        plCtx.put(SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT,
                  new Integer(service.getPort()));
        ComponentDescription pl =
            new ComponentDescriptionImpl(cd, plCtx, false);
        // TO DO: NEED TO SIGN AND ADD THE SIGNATURE!!!
        topCtx.put(SF_PROCESS_LOCATOR, pl);
    }


    /**
     * Deploy the target description to obtain a Prim object.
     *
     * @param name a <code>Reference</code> value
     * @param parent a <code>Prim</code> value
     * @param params a <code>Context</code> value
     * @return a <code>Prim</code> value
     * @exception SmartFrogDeploymentException if an error occurs
     */
    public Prim deploy(Reference name, Prim parent, Context params)
        throws SmartFrogDeploymentException {

        if (isPatchNeeded()) {
            configureDeployer();
            allInstances = findTargets();
           if (allInstances.length == 0) {
               throw new SmartFrogDeploymentException("no services found");
           }
           if (interactive) {
               DNSServiceInstance chosen = pickOne(allInstances);
               addProcessLocator(target, chosen);
               return super.deploy(name, parent, params);
           } else if (!tryAll) {
               addProcessLocator(target, allInstances[0]);
               return super.deploy(name, parent, params);
           } else {
               for (int i = 0; i< allInstances.length; i++) {
                   try {
                       addProcessLocator(target, allInstances[i]);
                       return super.deploy(name, parent, params);
                   } catch (Exception e) {
                       // try the next one
                       System.out.println("Can't deploy in " 
                                          + allInstances[i] + e); 
                   }
               }
           }
        }
        return super.deploy(name, parent, params);
    }


    /**
     * Returns the process compound with a particular process name.
     * @return process compound on host with name
     *
     * @throws Exception if failed to find process compound
     */
    protected ProcessCompound getProcessCompound() throws Exception {

        
       // check whether sfProcessHost is already defined
        boolean sfProcessHostOK = false;
        try {
            target.sfResolve(REF_PROCESS_HOST);
            sfProcessHostOK = true;
        } catch (Exception e) {
            // ignore and continue.
        }
        
        // check whether sfProcessLocator is already defined
        boolean sfLocatorProcessHostOK = false;
        try {
            ComponentDescription pl = 
                (ComponentDescription) target.sfResolve(REF_PROCESS_LOCATOR);
            pl.sfResolve(REF_PROCESS_HOST);
            sfLocatorProcessHostOK = true;
        } catch (Exception e) {
            // ignore and continue.
        }
        
        
        // get root process compound:
        // 1- If sfProcessHost defined use the standard path
        // 2- (1 is False) if sfProcessLocator.sfProcessHost defined use 
        //    the new path 
        // 3-  (both 1, 2 are false) if none defined use the 
        //    standard path (i.e., local)
        ProcessCompound hostCompound = 
            ((sfProcessHostOK || !sfLocatorProcessHostOK) 
             ? super.getProcessCompound()
             : getLocatorProcessCompound());
        
        return hostCompound;
    }

    /**
     * Resolves a process compound using the hostname/port info in the
     * sfProcessLocator field in the description. 
     *
     * @return a <code>ProcessCompound</code> value
     * @exception Exception if an error occurs
     */
    ProcessCompound getLocatorProcessCompound()
        throws Exception {

        InetAddress hostAddress = null;
        Object hostname = null;
        ProcessCompound hostCompound = null;

        ComponentDescription pl = (ComponentDescription) 
            target.sfResolve(REF_PROCESS_LOCATOR);
        hostname = pl.sfResolve(REF_PROCESS_HOST);
        if (hostname instanceof String) {
            hostAddress = InetAddress.getByName((String) hostname);
        } else if (hostname instanceof InetAddress) {
            hostAddress = (InetAddress) hostname;
        } else {
            Object name = null;
            if (target.sfContext().containsKey(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME)) {
                name =target.sfResolveHere(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME,false);
            }
            throw new SmartFrogDeploymentException(REF_PROCESS_HOST,null,name,target,null,"illegal sfProcessHost class: found " + hostname + ", of class " + hostname.getClass(), null, hostname);
        }

        int portNum = -1;
        try {
            Integer port =  (Integer) pl.sfResolve(REF_ROOT_LOCATOR_PORT);
            portNum = port.intValue();
        } catch (Exception e) {
            // continue with default port...            
        }
        
        hostCompound = SFProcess.getRootLocator().
            getRootProcessCompound(hostAddress, portNum);
               
        // need to handle sub-processes since we don't call super...

        // try to look up process name; if it exists, look up in the
        // root process compound (parent of the current?)
        String processName = null;

        try {
            processName = (String) target.sfResolve(REF_PROCESS_NAME);
        } catch (SmartFrogResolutionException resex) {
            // there is no process name, so use the specified hosts root/current process
            return hostCompound;
        }

        return hostCompound.sfResolveProcess(processName, target);
    }

 
    /**
     * Interactive function to pick up one service.
     *
     * @param all A set of compatible services.
     * @return A service chosen by the user.
     * @exception SmartFrogDeploymentException if an error occurs
     */
    public static DNSServiceInstance pickOne(DNSServiceInstance[]  all)
        throws SmartFrogDeploymentException {

        
        return DNSDeployerGuiImpl.getInstance(all).pickOne();

    }


    /**
     * Adds to the target description appropriate destination information.
     *
     * @exception SmartFrogDeploymentException if an error occurs
     */
    DNSServiceInstance[] findTargets() 
        throws SmartFrogDeploymentException {

        try {
            DNSServiceFilter filter = 
                new DNSServiceBasicFilterImpl(mustHave.sfContext());
            Resolver resol = DNSServiceImpl.getResolver(dnsResolverAddress,
                                                        dnsResolverTCP,
                                                        dnsResolverPort);
            DNSServiceQuery serv = new DNSServiceImpl(serviceTypeName, null);
            DNSServiceInstance[] all = serv.lookup(resol, filter);
            return all;
        } catch (DNSException e) {           
            throw new SmartFrogDeploymentException("cannot resolve", e);
        }
    }       


    /**
     * Configures this deployer from a description.
     *
     * @exception SmartFrogDeploymentException if an error occurs 
     * while configuring this deployer.
     */
    void configureDeployer() 
        throws SmartFrogDeploymentException {

        ComponentDescription dd = null;
        try {
            dd = (ComponentDescription) target.sfResolve(REF_DNS_DEPLOYER);
        } catch (Exception e) {
            throw new SmartFrogDeploymentException("can't resolve "
                                                   + REF_DNS_DEPLOYER,
                                                   e);
        }
   
        Context ctx = dd.sfContext();        
        tryAll = DNSComponentImpl.getBoolean(ctx,TRY_ALL, tryAll);
        interactive = DNSComponentImpl.getBoolean(ctx, INTERACTIVE,
                                                  interactive);
        dnsResolverTCP = DNSComponentImpl.getBoolean(ctx, DNS_RESOLVER_TCP,
                                                     dnsResolverTCP);
        dnsResolverPort =  
            DNSComponentImpl.getInteger(ctx, DNS_RESOLVER_PORT,
                                        dnsResolverPort);
        dnsResolverAddress = 
            DNSComponentImpl.getString(ctx, DNS_RESOLVER_ADDRESS,
                                       dnsResolverAddress);
        mustHave = DNSComponentImpl.getCD(ctx, MUST_HAVE, mustHave);
        serviceTypeName =  
            DNSComponentImpl.getString(ctx, SERVICE_TYPE_NAME,
                                       serviceTypeName);
        if (serviceTypeName == null) {
            throw new SmartFrogDeploymentException("missing serviceTypeName");
        }
    }

    /**
     * Whether this description needs to patch the process locator info.
     *
     */    
    boolean isPatchNeeded() {

        boolean patchNeeded = true;

        // check whether sfProcessHost is already defined
        try {
            target.sfResolve(REF_PROCESS_HOST);
            patchNeeded = false;
        } catch (Exception e) {
            // ignore and continue.
        }

        // check if sfProcessLocator.sfProcessHost is already defined
        if (patchNeeded) {
            try {
                ComponentDescription pl = 
                    (ComponentDescription) target.sfResolve(REF_PROCESS_LOCATOR);
                pl.sfResolve(REF_PROCESS_HOST);
                patchNeeded = false;
            } catch (Exception e) {
                // ignore and continue.
            }
        }

        // check if it has deployer configuration data
        if (patchNeeded) {
            try {
                ComponentDescription dd = 
                    (ComponentDescription) target.sfResolve(REF_DNS_DEPLOYER);
            } catch (Exception e) {
                /* cannot find deployer config data, assume that is local
                   deployer. */
                patchNeeded = false;
            }
        }
        return patchNeeded;
    }
    


}
