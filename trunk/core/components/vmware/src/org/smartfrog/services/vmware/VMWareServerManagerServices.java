/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.services.vmware;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface VMWareServerManagerServices extends Remote {
    /**
     * Registes a virtual machine with the vmware server.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean registerVM(String inVMPath) throws RemoteException;

    /**
     * Unregisters a virtual machine with the vmware server.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean unregisterVM(String inVMPath) throws RemoteException;

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean startVM(String inVMPath) throws RemoteException;

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean stopVM(String inVMPath) throws RemoteException;

    /**
     * Suspends a virtual machine. Has to be running.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean suspendVM(String inVMPath) throws RemoteException;

    /**
     * Resets a virtual machine.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean resetVM(String inVMPath) throws RemoteException;

    /**
     * Gets the power state of a virtual machine.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public int getPowerState(String inVMPath) throws RemoteException;

//      VMFox code
//          to be used when VMFox is running correctly    
//    /**
//     * Gets the tools state of a virtual machine.
//     * @param inVMPath The full path to the machine.
//     * @return
//     */
//    public int getToolsState(String inVMPath) throws RemoteException;

    /**
     * Gets the controlled virtual machines.
     * @return
     */
    public String getControlledMachines() throws RemoteException;

    /**
     * Shuts down the VMWare Server and all running machines as well.
     * @return
     */
    public boolean shutdownVMWareServerService() throws RemoteException;

    /**
     * Starts the VMWare Server and all machines in the designated vm folder.
     * @return
     */
    public boolean startVMWareServerService() throws RemoteException;

    /**
     * Return a list of the vmware images in the master folder.
     * @return
     * @throws RemoteException
     */
    public String getMasterImages() throws RemoteException;

    /**
     * Create a new instance of a master copy.
     * @param inVMMaster
     * @param inVMCopyName
     * @return
     * @throws RemoteException
     */
    public boolean createCopyOfMaster(String inVMMaster, String inVMCopyName) throws RemoteException;

    /**
     * Delete a instance of a master copy.
     * @param inVMPath
     * @return
     * @throws RemoteException
     */
    public boolean deleteCopy(String inVMPath) throws RemoteException;
}
