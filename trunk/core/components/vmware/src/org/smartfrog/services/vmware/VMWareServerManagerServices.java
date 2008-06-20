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
import java.util.HashMap;
import java.util.ArrayList;


public interface VMWareServerManagerServices extends Remote {

    /** {@value} */
    String ATTR_MASTER_IMAGES_DIR = "masterImages";

    /** {@value} */
    String ATTR_COPY_IMAGES_DIR = "copyImages";

    /** {@value} */
    String ATTR_VIXLIBRARYPATH_WIN = "vixLibraryPathWin";

    /** {@value} */
    String ATTR_VIXLIBRARYNAME_WIN = "vixLibraryNameWin";

    /** {@value} */
    String ATTR_VIXLIBRARYPATH_LINUX = "vixLibraryPathLinux";

    /** {@value} */
    String ATTR_VIXLIBRARYPNAME_LINUX = "vixLibraryNameLinux";

    /** {@value} */
    String ATTR_NUM_RETRIES = "numRetries";

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     *
     * @param inVMName The name of the VM.
     * @return "success" or an error message
     * @throws SmartFrogException problems with the virtual machines
     * @throws RemoteException network problems
     */
    public String startVM(String inVMName) throws RemoteException, SmartFrogException;

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     *
     * @param inVMName The name of the VM.
     * @return "success" or an error message
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String shutDownVM(String inVMName) throws RemoteException, SmartFrogException;

    /**
     * Suspends a virtual machine. Has to be running.
     *
     * @param inVMName The name of the VM.
     * @return "success" or an error message
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String suspendVM(String inVMName) throws RemoteException, SmartFrogException;

    /**
     * Resets a virtual machine.
     *
     * @param inVMName The name of the VM.
     * @return "success" or an error message
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String resetVM(String inVMName) throws RemoteException, SmartFrogException;

    /**
     * Gets the power state of a virtual machine.
     *
     * @param inVMName The name of the VM.
     * @return The power state or 0
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public int getPowerState(String inVMName) throws RemoteException, SmartFrogException;

    /**
     * Gets the tools state of a virtual machine.
     * @param inVMName The name of the VM.
     * @return The state of the tools or 0.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public int getToolsState(String inVMName) throws RemoteException, SmartFrogException;

    /**
     * Gets the controlled virtual machines.
     * @return The image modules of this machine.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public ArrayList<VMWareImageModule> getControlledMachines() throws RemoteException, SmartFrogException;

    /**
     * Shuts down the VMWare Server and all running machines as well.
     * @throws RemoteException network problems
     * @return "success" or an error message
     */
    public String shutdownVMWareServerService() throws RemoteException;

    /**
     * Starts the VMWare Server and all machines in the designated vm folder.
     * @throws RemoteException network problems
     * @return "success" or an error message
     */
    public String startVMWareServerService() throws RemoteException;

    /**
     * Return a list of the vmware images in the master folder.
     * @return "success" or an error message
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String getMasterImages() throws RemoteException, SmartFrogException;

    /**
     * Create a new instance of a master copy.
     * @param inVMMaster Name of the master image.
     * @param inVMCopyName Name of the new copy.
     * @param inGuestUser User account of the guest OS.
     * @param inGuestPass Password for the user account.
     * @return "success" or an error message
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String createCopyOfMaster(String inVMMaster, String inVMCopyName, String inGuestUser, String inGuestPass) throws RemoteException, SmartFrogException;

    /**
     * Delete a instance of a master copy.
     * @param inVMName  The name of the VM.
     * @return "success" or an error message
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String deleteCopy(String inVMName) throws RemoteException, SmartFrogException;

    /**
     * Changes the display name of a virtual machine.
     * @param inVMName The name of the VM.
     * @param inNewName The new name for the machine.
     * @return "success" or an error message.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String renameVM(String inVMName, String inNewName) throws RemoteException, SmartFrogException;

    /**
     * Gets the value of an attribute of a VM.
     * @param inVMName The name of the VM.
     * @param inKey The attribute key.
     * @return The value of the key.
     * @throws RemoteException
     */
    public String getVMAttribute(String inVMName, String inKey) throws RemoteException;

    /**
     * Sets the value of an attribute of a VM.
     * @param inVMName The name of the VM.
     * @param inKey The attribute key.
     * @param inValue The value for the key.
     * @return Returns the old value of the key.
     * @throws RemoteException
     */
    public String setVMAttribute(String inVMName, String inKey, String inValue) throws RemoteException;

//    /**
//     * Copies a file from the host OS into the guest OS of the specified VM.
//     * @param inVMPath The vm which contains the guest OS.
//     * @param inSourceFile The path on the host OS.
//     * @param inTargetFile The path on the guest OS.
//     * @return "success" or an error message.
//     * @throws RemoteException
//     */
//    public String copyFileFromHostToGuestOS(String inVMPath, String inSourceFile, String inTargetFile) throws RemoteException;

    /**
     * Executes a program within the guest os.
     * @param inVMName The name of the VM.
     * @param inCommand The (full!) path to the program.
     * @param inParameters The parameters for the program. (No relative pathes allowed here, too.)
     * @param inNoWait Wait for the program to exit?
     * @return "success" or an error message.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public String executeInGuestOS(String inVMName, String inCommand, String inParameters, boolean inNoWait) throws RemoteException, SmartFrogException;

    /**
     * Takes a snapshot of a virtual machine.
     * @param inVMName The name of the virtual machine.
     * @param inSnapshotName The name for the snapshot.
     * @param inSnapshotDescription The description of the snapshot.
     * @param inIncludeMemory Should the memory of the virtual machine be included in the snapshot, too?
     * @return "success" or an error message.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public String takeSnapshot(String inVMName, String inSnapshotName, String inSnapshotDescription, boolean inIncludeMemory) throws RemoteException, SmartFrogException;

    /**
     * Reverts a virtual machine to a snapshot.
     * @param inVMName The name of the virtual machine.
     * @param inSnapshotName The name of the snapshot.
     * @return "success" or an error message.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public String revertVMToSnapshot(String inVMName, String inSnapshotName) throws RemoteException, SmartFrogException;
}
