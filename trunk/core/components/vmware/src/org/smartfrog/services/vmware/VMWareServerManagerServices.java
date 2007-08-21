/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.services.vmware;

import java.rmi.Remote;


public interface VMWareServerManagerServices extends Remote {
    /**
     * Registes a virtual machine with the vmware server.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean registerVM(String inVMPath);

    /**
     * Unregisters a virtual machine with the vmware server.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean unregisterVM(String inVMPath);

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean startVM(String inVMPath);

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean stopVM(String inVMPath);

    /**
     * Suspends a virtual machine. Has to be running.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean suspendVM(String inVMPath);

    /**
     * Resets a virtual machine.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean resetVM(String inVMPath);

    /**
     * Gets the power state of a virtual machine.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public int getPowerState(String inVMPath);

    /**
     * Gets the tools state of a virtual machine.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public int getToolsState(String inVMPath);

    /**
     * Gets the running virtual machines.
     * @return
     */
    public String getRunningMachines();

    /**
     * Shuts down the VMWare Server and all running machines as well.
     * @return
     */
    public boolean shutdownVMWareServerService();

    /**
     * Starts the VMWare Server and all machines in the designated vm folder.
     * @return
     */
    public boolean startVMWareServerService();
}
