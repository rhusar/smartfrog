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

package org.smartfrog.sfcore.processcompound;

import org.smartfrog.sfcore.common.DumperCDImpl;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.deployer.SFDeployer;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimDeployerImpl;
import org.smartfrog.sfcore.reference.Reference;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

/**
 * Implements a specialized description deployer. This deployer uses the
 * sfProcessHost and sfRootLocatorPort attribute to locate the appropriate
 * remote ProcessCompound to forward descriptions to. Also registers the
 * component with the processcompound after deployment. If
 * sfProcessComponentName is specified in the target the component is
 * registered with that name. Otherwise the registration is called with null,
 * causing a name to be made up.
 *
 */
public class PrimHostDeployerImpl extends PrimDeployerImpl {


    /** Efficiency holder of sfProcessHost attribute. */
    protected static final Reference refProcessHost =
        new Reference(SmartFrogCoreKeys.SF_PROCESS_HOST);

    /** Efficiency holder of sfRootLocatorPort attribute. */
    protected static final Reference refRootLocatorPort =
        new Reference(SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT);

    /**
     * Constructs the PrimHostDeployerImpl with ComponentDescription.
     *
     * @param descr target to operate on
     */
    public PrimHostDeployerImpl(ComponentDescription descr) {
        super(descr);
    }

    /**
     * Returns the process compound on a particular host and with a particular
     * process name. "sfProcessHost" is used to determine the host to use to
     * locate the root process compound on that host. If the process host is
     * not specified the local process compound is returned.
     *
     * @return ProcessCompound on host with name
     *
     * @throws Exception if failed to find process compound
     */
    protected ProcessCompound getProcessCompound() throws Exception {
        InetAddress hostAddress = null;
        Object hostname = null;
        try {
            hostname = target.sfResolve(refProcessHost);
            if (hostname instanceof String) {
                hostAddress = InetAddress.getByName((String) hostname);
            } else if (hostname instanceof InetAddress) {
                hostAddress = (InetAddress) hostname;
            } else {
                Object name = getProcessComponentName();
                throw new SmartFrogDeploymentException(
                        refProcessHost, null, name, target, null,
                        "illegal sfProcessHost class: found " + hostname + ", of class " + hostname.getClass(),
                        null, hostname);
            }
        } catch (SmartFrogResolutionException resex) {
            return SFProcess.getProcessCompound();
        } catch (UnknownHostException unhex) {
            Object name = getProcessComponentName();
            throw new SmartFrogDeploymentException(refProcessHost, null, name, target, null, "Unknown host: " + hostname, unhex, hostname);
        }

        return SFProcess.getRootLocator().getRootProcessCompound(hostAddress);
    }

    /**
     * Overrides superclass behaviour to forward description to another process
     * based on sfProcessHost attribute.
     *
     * @param parent parent for deployed component
     *
     * @return The Component Reference after it gets deployed
     *
     * @throws SmartFrogDeploymentException if failed to deploy target
     */
    protected Prim deploy(Prim parent) throws SmartFrogDeploymentException {

        try {
            ProcessCompound pc = null;

            try {
                pc = getProcessCompound();
            } catch (Exception e) {
                throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward(e);
            }

            ProcessCompound local = SFProcess.getProcessCompound();

            if (pc.equals(local)) {
                if (parent == null) {
                    return local.sfDeployComponentDescription(null, parent, target, null);
                } else {
                    return super.deploy(parent);
                }
            } else {
                deployEnvironmentIfNeeded(pc);

                // Name = null : The name of the component on the remote process is generated automatically,
                // even though the component has a name in the local process. Not pretty.
                // This could be changed easily by adding the component name as sfProcessComponentName, but might break backward compatibility.
                return pc.sfDeployComponentDescription(null, parent, target, null);
            }
        } catch (Exception ex) {            
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward
                    ("Failed to deploy the component description: " + target, ex);
        }
    }

    private void deployEnvironmentIfNeeded(ProcessCompound pc) throws Exception {
        Prim appEnvironment = resolveAppEnvironment();
        if (appEnvironment != null) {
            ComponentDescription appEnvDescr = dumpEnvironment(appEnvironment);
            // sfProcessComponentName is
            //  only used for initial deployment of the application environment, on the target root process.
            // When deploying to a subprocess it needs to be removed because otherwise the environment will be registered with the rootProcess directly
            String name = (String) appEnvDescr.sfRemoveAttribute(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME);
            // Same problem for the sfProcessHost attribute
            appEnvDescr.sfRemoveAttribute(SmartFrogCoreKeys.SF_PROCESS_HOST);

            Prim deployedAppEnv = (Prim) pc.sfResolveHere(name, false);
            if (deployedAppEnv == null) {
                Prim deployedEnv = pc.sfCreateNewApp(name, appEnvDescr, null);
                if (sfLog().isDebugEnabled())
                    sfLog().debug("Propagated application environment. Deployed environment name: " + deployedEnv.sfCompleteName());
            } else {
                checkIsSame(deployedAppEnv, appEnvDescr, pc, name);
            }
        }
    }

    private void checkIsSame(Prim deployedAppEnv, ComponentDescription appEnvironment, ProcessCompound targetPC, Object appEnvName) throws SmartFrogException {
        ComponentDescription existingEnvDescription = dumpEnvironment(deployedAppEnv);
        if (! existingEnvDescription.equals(appEnvironment))
            throw new SmartFrogDeploymentException
                    (MessageUtil.formatMessage(EXISTING_APP_ENV_IS_DIFFERENT, targetPC, appEnvName));
        
    }

    private Prim resolveAppEnvironment() throws SmartFrogResolutionException {
        try {
            ComponentDescription sfMeta = (ComponentDescription) target.sfResolveHere(SmartFrogCoreKeys.SF_METADATA);
            return (Prim) SFDeployer.resolveMetadataAttribute(sfMeta, SmartFrogCoreKeys.SF_APPLICATION_ENVIRONMENT, null);
        } catch (ClassCastException cce) {
            throw environmentError(cce);
        } catch (SmartFrogResolutionException re) {
            throw environmentError(re);
        }
    }

    private ComponentDescription dumpEnvironment(Prim appEnvironment) throws SmartFrogException {
        try {
            DumperCDImpl dumper = new DumperCDImpl(appEnvironment);
            appEnvironment.sfDumpState(dumper.getDumpVisitor());
            return dumper.getComponentDescription(1000);
        } catch (RemoteException e) {
            throw new SmartFrogDeploymentException
                    ("The application environment has remote parts, which is not recommended. Access to one of the remote parts failed.", e);
        } catch (SmartFrogException e) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward
                    ("Could not dump the application environment for deployment to remote process", e);
        }
    }

    private static SmartFrogResolutionException environmentError(Exception e) {
        return (SmartFrogResolutionException) SmartFrogResolutionException.forward
                ("The sfApplicationEnvironment attribute does not point to a correct environment or is missing", e);
    }
}
