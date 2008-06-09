/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

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

package org.smartfrog.vast.architecture;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.avalanche.server.AvalancheServer;
import org.smartfrog.avalanche.server.engines.sf.SFAdapter;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.ArrayList;
import java.net.UnknownHostException;

/**
 * The environment builder class.
 */
public class EnvironmentConstructorImpl extends CompoundImpl implements EnvironmentConstructor {
    /**
     * Reference to the avalanche server component.
     */
    AvalancheServer refAvlServer = null;

    /**
     * The list containing the configuration details for the physical machine.
     */
    private ArrayList<PhysicalMachineConfig> listPhysicalMachines = new ArrayList<PhysicalMachineConfig>();

    /**
     * The list containing the configuration details for the virtual machines.
     */
    private ArrayList<VirtualMachineConfig> listVirtualMachines = new ArrayList<VirtualMachineConfig>();

    public EnvironmentConstructorImpl() throws RemoteException {

    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        
        sfLog().info("deploying");

        // get the reference to the avalanche server
        refAvlServer = (AvalancheServer) sfResolve(ATTR_AVALANCHE, true);

        sfLog().info("successfully deployed");
    }

    protected void sfDeployWithChildren() throws SmartFrogDeploymentException {
        super.sfDeployWithChildren();

        sfLog().info("deploying with children");

        // iterate through the attributes of this description
        Enumeration e = sfContext().keys();
        while (e.hasMoreElements()) {
            Object key = e.nextElement();

            if (key.equals(ATTR_PHYSICAL_MACHINES)) {
                // entries of the physical machines found
                ComponentDescription phyParent = (ComponentDescription) sfContext().get(key);
                if (phyParent != null) {

                    // iterate through the phycial machine descriptions
                    Enumeration phy = phyParent.sfContext().keys();
                    while (phy.hasMoreElements()) {
                        ComponentDescription phyObj = (ComponentDescription) phyParent.sfContext().get(phy.nextElement());
                        if (phyObj != null) {
                            // create a new physical machine object
                            PhysicalMachineConfig conf = new PhysicalMachineConfig();

                            // set the appropriate values
                            try {
                                // set the access mode
                                resolveBasicMachineAttributes(phyObj, conf);

                            } catch (SmartFrogResolutionException ex) {
                                sfLog().error("error while resolving a physical machine description", ex);
                                throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward(ex);
                            }

                            this.listPhysicalMachines.add(conf);
                        }
                    }
                }
            } else if (key.equals(ATTR_VIRTUAL_MACHINES)) {
                // entries of the physical machines found
                ComponentDescription virtParent = (ComponentDescription) sfContext().get(key);
                if (virtParent != null) {

                    // iterate through the phycial machine descriptions
                    Enumeration virt = virtParent.sfContext().keys();
                    while (virt.hasMoreElements()) {
                        ComponentDescription virtObj = (ComponentDescription) virtParent.sfContext().get(virt.nextElement());
                        if (virtObj != null) {
                            // create a new physical machine object
                            VirtualMachineConfig conf = new VirtualMachineConfig();

                            // set the appropriate values
                            try {
                                resolveBasicMachineAttributes(virtObj, conf);

                                conf.setAffinity(virtObj.sfResolve(VirtualMachineConfig.ATTR_AFFINITY, "", false));
                                conf.setName((String) virtObj.sfResolve(VirtualMachineConfig.ATTR_NAME, true));
                                conf.setSourceImage((String) virtObj.sfResolve(VirtualMachineConfig.ATTR_SOURCE_IMAGE, true));
                            } catch (SmartFrogResolutionException ex) {
                                sfLog().error("error while resolving a virtual machine description", ex);
                                throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward(ex);
                            }

                            this.listVirtualMachines.add(conf);
                        }
                    }
                }
            }
        }

        sfLog().info("successfully deployed with children");
    }

    /**
     * Resolves the basic attributes which are equal for physical and virtual machines.
     * @param inMachineDesc The ComponentDescription with the attributes.
     * @param inConf The configuration where the attributes should be stored.
     * @throws SmartFrogResolutionException
     * @throws SmartFrogDeploymentException
     */
    private void resolveBasicMachineAttributes(ComponentDescription inMachineDesc, PhysicalMachineConfig inConf) throws SmartFrogResolutionException, SmartFrogDeploymentException {
        // resolve the access modes
        ComponentDescription AModes = (ComponentDescription) inMachineDesc.sfContext().get(PhysicalMachineConfig.ATTR_ACCESS_MODES);
        resolveConnectionModes(AModes, inConf.getListAccessModes());

        // resolve the transfer modes
        ComponentDescription TModes = (ComponentDescription) inMachineDesc.sfContext().get(PhysicalMachineConfig.ATTR_TRANSFER_MODES);
        resolveConnectionModes(TModes, inConf.getListTransferModes());

        // resolve the arguments
        ComponentDescription Arguments = (ComponentDescription) inMachineDesc.sfContext().get(PhysicalMachineConfig.ATTR_ARGUMENTS);
        if (Arguments != null) {
            // for each argument
            Enumeration enumArgs = Arguments.sfContext().keys();
            while (enumArgs.hasMoreElements()) {
                // add the argument to the list
                ComponentDescription arg = (ComponentDescription) Arguments.sfContext().get(enumArgs.nextElement());

                Argument newArg = new Argument();
                newArg.setName((String) arg.sfResolve(PhysicalMachineConfig.ATTR_ARG_NAME, true));
                newArg.setValue((String) arg.sfResolve(PhysicalMachineConfig.ATTR_ARG_VALUE, true));

                inConf.getListArguments().add(newArg);
            }
        }

        // resolve the remaining unique attributes
        inConf.setHostname(inMachineDesc.sfResolve(PhysicalMachineConfig.ATTR_HOSTNAME, "", false));
        inConf.setIpAddress(inMachineDesc.sfResolve(PhysicalMachineConfig.ATTR_IPADDRESS, "", false));
        if (inConf.getHostname().equals("") && inConf.getIpAddress().equals(""))
            throw new SmartFrogDeploymentException("Neither Hostname nor IpAddress are set.");

        inConf.setArchitecture((String) inMachineDesc.sfResolve(PhysicalMachineConfig.ATTR_ARCHITECTURE, true));
        inConf.setOS((String) inMachineDesc.sfResolve(PhysicalMachineConfig.ATTR_OS, true));
        inConf.setPlatform((String) inMachineDesc.sfResolve(PhysicalMachineConfig.ATTR_PLATFORM, true));
    }

    /**
     * Resolves the connection modes of a component description.
     * @param inModeList The list where the modes should be stored.
     * @param inModes The parent component description which contains the connection modes.
     * @throws SmartFrogResolutionException
     */
    private void resolveConnectionModes(ComponentDescription inModes, ArrayList<ConnectionMode> inModeList) throws SmartFrogResolutionException {
        if (inModes != null) {
            // for each connection mode
            Enumeration enumModes = inModes.sfContext().keys();
            while (enumModes.hasMoreElements()) {
                ComponentDescription Mode = (ComponentDescription) inModes.sfContext().get(enumModes.nextElement());

                // add an connection mode to the list
                ConnectionMode conMode = new ConnectionMode();

                conMode.setType((String) Mode.sfResolve(PhysicalMachineConfig.ATTR_MODE_TYPE, true));
                conMode.setUser((String) Mode.sfResolve(PhysicalMachineConfig.ATTR_MODE_USERNAME, true));
                conMode.setPassword((String) Mode.sfResolve(PhysicalMachineConfig.ATTR_MODE_PASSWORD, true));
                conMode.setIsDefault(Mode.sfResolve(PhysicalMachineConfig.ATTR_MODE_IS_DEFAULT, false, false));

                inModeList.add(conMode);
            }
        }
    }

    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        sfLog().info("starting");

        // update/create the physical machines in the database
        updatePhysicalMachines();

        // ignite them
        ignitePhysicalHosts();

        sfLog().info("successfully started");
    }

    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);

        // stop the daemons on the ignites machines
        stopPhysicalHosts();
    }

    /**
     * Stops the daemons running on the physical hosts.
     */
    private void stopPhysicalHosts() {
        for (PhysicalMachineConfig phy : listPhysicalMachines) {
            try {
                SFAdapter.stopDaemon(phy.getName());
            } catch (Exception e) {
                sfLog().error("Error while stopping physical host: " + phy.getName(), e);
            }
        }
    }

    /**
     * Physical machines will be created/updated according to the sf description file.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    private void updatePhysicalMachines() throws RemoteException, SmartFrogException {
        // first add the physical hosts which should be ignited
        // to the database of avalanche
        for (PhysicalMachineConfig phy : listPhysicalMachines) {
            // update the unique attributes
            refAvlServer.updateHost(    phy.getName(),
                                        phy.getArchitecture(),
                                        phy.getPlatform(),
                                        phy.getOS());

            // clear the lists
            refAvlServer.clearAccessModes(phy.getName());
            refAvlServer.clearTransferModes(phy.getName());
            refAvlServer.clearArguments(phy.getName());

            // add the access modes
            for (ConnectionMode mode : phy.getListAccessModes()) {
                refAvlServer.addAccessMode( phy.getName(),
                                            mode.getType(),
                                            mode.getUser(),
                                            mode.getPassword(),
                                            mode.getIsDefault());
            }

            // add the transfer modes
            for (ConnectionMode mode : phy.getListTransferModes()) {
                refAvlServer.addTransferMode(   phy.getName(),
                                                mode.getType(),
                                                mode.getUser(),
                                                mode.getPassword(),
                                                mode.getIsDefault());
            }

            // add the arguments
            for (Argument arg : phy.getListArguments()) {
                refAvlServer.addArgument(   phy.getName(),
                                            arg.getName(),
                                            arg.getValue());
            }
        }
    }

    private void ignitePhysicalHosts() throws SmartFrogException, RemoteException {
        // construct the host name array
        sfLog().info("igniting hosts:");
        String [] hostNames = new String [listPhysicalMachines.size()];
        for (int i = 0; i < hostNames.length; ++i) {
            sfLog().info(listPhysicalMachines.get(i).getName());
            hostNames[i] = listPhysicalMachines.get(i).getName();
        }

        // ignite the hosts
        refAvlServer.igniteHosts(hostNames);
    }
}
