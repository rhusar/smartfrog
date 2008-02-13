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
    final String ATTR_MASTER_IMAGES_DIR = "masterImages";

    /** {@value} */
    final String ATTR_COPY_IMAGES_DIR = "copyImages";

    /** {@value} */
    final String ATTR_VIXLIBRARYPATH_WIN = "vixLibraryPathWin";

    /** {@value} */
    final String ATTR_VIXLIBRARYNAME_WIN = "vixLibraryNameWin";

    /** {@value} */
    final String ATTR_VIXLIBRARYPATH_LINUX = "vixLibraryPathLinux";

    /** {@value} */
    final String ATTR_VIXLIBRARYPNAME_LINUX = "vixLibraryNameLinux";

    /** {@value} */
    final String ATTR_SERVER_INSTALLED = "serverInstalled";

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     *
     * @param inVMPath The full path to the machine.
     * @throws RemoteException network problems
     * @return "success" or an error message
     */
    public String startVM(String inVMPath) throws RemoteException;

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     *
     * @param inVMPath The full path to the machine.
     * @throws RemoteException network problems
     * @return "success" or an error message
     */
    public String shutDownVM(String inVMPath) throws RemoteException;

    /**
     * Suspends a virtual machine. Has to be running.
     *
     * @param inVMPath The full path to the machine.
     * @throws RemoteException network problems
     * @return "success" or an error message
     */
    public String suspendVM(String inVMPath) throws RemoteException;

    /**
     * Resets a virtual machine.
     *
     * @param inVMPath The full path to the machine.
     * @throws RemoteException network problems
     * @return "success" or an error message
     */
    public String resetVM(String inVMPath) throws RemoteException;

    /**
     * Gets the power state of a virtual machine.
     *
     * @param inVMPath The full path to the machine.
     * @throws RemoteException network problems
     * @return The power state or 0
     */
    public int getPowerState(String inVMPath) throws RemoteException;

    /**
     * Gets the tools state of a virtual machine.
     * @param inVMPath The full path to the machine.
     * @return The state of the tools or 0.
     * @throws java.rmi.RemoteException
     */
    public int getToolsState(String inVMPath) throws RemoteException;

    /**
     * Gets the controlled virtual machines.
     * @throws RemoteException network problems
     * @return "success" or an error message
     */
    public String getControlledMachines() throws RemoteException;

    /**
     * Return a list of the vmware images in the master folder.
     * @throws RemoteException network problems
     * @return "success" or an error message
     */
    public String getMasterImages() throws RemoteException;

    /**
     * Create a new instance of a master copy.
     * @param inVMMaster
     * @param inVMCopyName
     * @return "success" or an error message
     * @throws RemoteException network problems
     */
    public String createCopyOfMaster(String inVMMaster, String inVMCopyName) throws RemoteException;

    /**
     * Delete a instance of a master copy.
     * @param inVMPath
     * @throws RemoteException network problems
     * @return "success" or an error message
     */
    public String deleteCopy(String inVMPath) throws RemoteException;

    /**
     * Changes the display name of a virtual machine.
     * @param inVMPath The path to the .vmx file.
     * @param inNewName The new name for the machine.
     * @return "success" or an error message.
     * @throws RemoteException
     */
    public String renameVM(String inVMPath, String inNewName) throws RemoteException;

    /**
     * Copies a file from the host OS into the guest OS of the specified VM.
     * @param inVMPath The vm which contains the guest OS.
     * @param inSourceFile The path on the host OS.
     * @param inTargetFile The path on the guest OS.
     * @return "success" or an error message.
     * @throws RemoteException
     */
    public String copyFileFromHostToGuestOS(String inVMPath, String inSourceFile, String inTargetFile) throws RemoteException;
}
