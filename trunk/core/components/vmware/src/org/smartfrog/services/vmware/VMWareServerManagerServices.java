/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */
package org.smartfrog.services.vmware;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface VMWareServerManagerServices extends Remote {

    /** {@value} */
    String ATTR_MASTER_IMAGES_DIR="masterImages";

    /** {@value} */
    String ATTR_COPY_IMAGES_DIR = "copyImages";

    /**
     * Registes a virtual machine with the vmware server.
     *
     * @param inVMPath The full path to the machine.
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public boolean registerVM(String inVMPath) throws RemoteException, SmartFrogException;

    /**
     * Unregisters a virtual machine with the vmware server.
     *
     * @param inVMPath The full path to the machine.
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public boolean unregisterVM(String inVMPath) throws RemoteException, SmartFrogException;

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     *
     * @param inVMPath The full path to the machine.
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public boolean startVM(String inVMPath) throws RemoteException, SmartFrogException;

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     *
     * @param inVMPath The full path to the machine.
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public boolean stopVM(String inVMPath) throws RemoteException, SmartFrogException;

    /**
     * Suspends a virtual machine. Has to be running.
     *
     * @param inVMPath The full path to the machine.
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public boolean suspendVM(String inVMPath) throws RemoteException, SmartFrogException;

    /**
     * Resets a virtual machine.
     *
     * @param inVMPath The full path to the machine.
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public boolean resetVM(String inVMPath) throws RemoteException, SmartFrogException;

    /**
     * Gets the power state of a virtual machine.
     *
     * @param inVMPath The full path to the machine.
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public int getPowerState(String inVMPath) throws RemoteException, SmartFrogException;

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
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     *
     */
    public String getControlledMachines() throws RemoteException, SmartFrogException;

    /**
     * Shuts down the VMWare Server and all running machines as well.
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public boolean shutdownVMWareServerService() throws RemoteException, SmartFrogException;

    /**
     * Starts the VMWare Server and all machines in the designated vm folder.
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public boolean startVMWareServerService() throws RemoteException, SmartFrogException;

    /**
     * Return a list of the vmware images in the master folder.
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */

    public String getMasterImages() throws RemoteException, SmartFrogException;

    /**
     * Create a new instance of a master copy.
     *
     * @param inVMMaster
     * @param inVMCopyName
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public void createCopyOfMaster(String inVMMaster, String inVMCopyName) throws RemoteException, SmartFrogException;

    /**
     * Delete a instance of a master copy.
     *
     * @param inVMPath
     * @throws RemoteException network problems
     * @throws SmartFrogException other problems
     */
    public boolean deleteCopy(String inVMPath) throws RemoteException, SmartFrogException;
}
