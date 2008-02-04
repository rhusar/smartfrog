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

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.*;
import java.rmi.RemoteException;


public class VMWareServerManager extends PrimImpl implements VMWareServerManagerServices {
    /** The folder where all vm images are stored which are under control of the daemon. */
    private String vmImagesFolder;
    private String vmMasterFolder;

    private static String ATTR_VIXLIBRARYPATH_WIN = "vixLibraryPathWin";
    private static String ATTR_VIXLIBRARYNAME_WIN = "vixLibraryNameWin";
    private static String ATTR_VIXLIBRARYPATH_LINUX = "vixLibraryPathLinux";
    private static String ATTR_VIXLIBRARYPNAME_LINUX = "vixLibraryNameLinux";

    /** Used to communicate with the vmware server. */
    private VMWareCommunicator vmComm = null;

    public VMWareServerManager() throws RemoteException {

    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        // get the vix properties
        String strVixLibPath, strVixLibName;
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            strVixLibPath = (String) sfResolve(ATTR_VIXLIBRARYPATH_WIN, true);
            strVixLibName = (String) sfResolve(ATTR_VIXLIBRARYNAME_WIN, true);
        }
        else {
            strVixLibPath = (String) sfResolve(ATTR_VIXLIBRARYPATH_LINUX, true);
            strVixLibName = (String) sfResolve(ATTR_VIXLIBRARYPNAME_LINUX, true);
        }

        // create the jna wrapper library
        sfLog().info("Loading: " + strVixLibPath + strVixLibName);
        try {
            this.vmComm = new VMWareCommunicator(strVixLibPath, strVixLibName);
        } catch (Exception e) {
            sfLog().error("Error while creating the VMware communicator.", e);
            throw new SmartFrogLifecycleException("Error while creating the VMware communicator.", e);
        }

        // get the vm image folders
        setVmImagesFolder(FileSystem.lookupAbsolutePath(this,ATTR_COPY_IMAGES_DIR,null,null,true,null));
        setVmMasterFolder(FileSystem.lookupAbsolutePath(this, ATTR_MASTER_IMAGES_DIR, null, null, true, null));

        // generate the control modules for the vm images
        generateModulesFromImgFolder();

        // start all virtual machines
        for (VMWareImageModule img : vmComm.getImageModuleList()) {
            try {
                img.startUp();
            } catch (SmartFrogException e) {
                sfLog().err(e);
            }
        }

        sfLog().info("VMWareServerManager started.");
    }

    public String getVmImagesFolder() {
        return vmImagesFolder;
    }

    public void setVmImagesFolder(String vmImagesFolder) {
        this.vmImagesFolder = vmImagesFolder;
    }

    public String getVmMasterFolder() {
        return vmMasterFolder;
    }

    public void setVmMasterFolder(String vmMasterFolder) {
        this.vmMasterFolder = vmMasterFolder;
    }

    /** A filename filter which only accepts ".vmx" files. */
    private static class vmxFileFilter implements FilenameFilter {
        private static final String VMX = ".vmx";

        public boolean accept(File dir, String name) {
            return name.endsWith(VMX);
        }
    }

    // TODO: add createVMImage functionality

    /**
     * Should only be called in sfStart()! Generates a VMWareImageModule for each .vmx file in the designated vm images
     * folder.
     */
    private void generateModulesFromImgFolder() {
        // get the folder
        File folder = new File(getVmImagesFolder());
        if (folder.exists()) {
            // get the files in the folder
            File[] files = folder.listFiles(new vmxFileFilter());
            if (files != null) {
                for (File curFile : files) {
                    try {
                        // create a new image module and add it to the list if successful
                        VMWareImageModule newImg = this.vmComm.createImageModule(curFile.getAbsolutePath());

                        // register it with the vm in case some aren't yet
                        newImg.registerVM();
                    } catch (Exception e) {
                        sfLog().trace(e);
                    }
                }
            }
        }
    }

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     *
     * @param inVMPath The full path to the machine.
     * @return true if the vm started
     */
    public String startVM(String inVMPath) throws RemoteException {
        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (tmp != null) {
                tmp.startUp();
                return "success";
            }
        } catch (SmartFrogException e) {
            sfLog().error("Failed to start \"" + inVMPath + "\"", e);
        }

        // an error occured
        return "Failed to start \"" + inVMPath + "\". Please review the logfile for detailed information.";
    }

    /**
     * Return a list of the vmware images in the master folder.
     *
     * @return a string containing every master image (.vmx file) listed on a separate line
     * @throws RemoteException
     */
    public String getMasterImages() throws RemoteException {
        String strResult = "";

        // get the files
        File folder = new File(getVmMasterFolder());
        if (folder.exists()) {
            File[] files = folder.listFiles();
            for (File f : files) {
                if (f.getName().endsWith(vmxFileFilter.VMX)) {
                    strResult += f.getName() + "\n";
                }
            }
        }

        return strResult;
    }


    /**
     * Create a new instance of a master copy.
     *
     * @param inVMMaster
     * @param inVMCopyName
     * @throws java.rmi.RemoteException
     */
    public void createCopyOfMaster(String inVMMaster, String inVMCopyName) throws RemoteException,SmartFrogException {
        String copyVMX;
        
        copyVMX = getVmImagesFolder() + File.separator + inVMCopyName;
        if (!copyVMX.endsWith(vmxFileFilter.VMX)) {
            copyVMX += vmxFileFilter.VMX;
        }

        this.vmComm.copyVirtualMachine(getVmMasterFolder() + File.separator + inVMMaster, getVmImagesFolder() + File.separator + inVMCopyName);

        VMWareImageModule newImg = this.vmComm.createImageModule(getVmImagesFolder() + File.separator + inVMCopyName + File.separator + inVMMaster);
        newImg.rename(inVMCopyName);

        // register the file with the vmware server
        newImg.registerVM();

        // TODO: remove created image on error
    }

    /**
     * Delete a instance of a master copy.
     *
     * @param inVMPath
     * @throws java.rmi.RemoteException network problems
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  other problems
     * @returns "success" or an error message
     */
    public String deleteCopy(String inVMPath) throws RemoteException {
        try {
            this.vmComm.deleteVirtualMachine(inVMPath);
        } catch (SmartFrogException e) {
            sfLog().error("Failed to delete \"" + inVMPath + "\"", e);
            return "Failed to suspend \"" + inVMPath + "\". Please review the logfile for detailed information.";
        }
        return "success"; // TODO: better error messages
    }

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     *
     * @param inVMPath The full path to the machine.
     * @returns Returns "success" or an error message.
     */
    public String stopVM(String inVMPath) throws RemoteException {
        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (tmp != null) {
                tmp.shutDown();
                return "success";
            }
        } catch (SmartFrogException e) {
            sfLog().error("Failed to stop \"" + inVMPath + "\"", e);
        }

        // an error occurred
        return "Failed to stop \"" + inVMPath + "\". Please review the logfile for detailed information.";
    }

    /**
     * Suspends a virtual machine. Has to be running.
     *
     * @param inVMPath The full path to the machine.
     * @returns Returns "success" or an error message.
     */
    public String suspendVM(String inVMPath) throws RemoteException {
        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (tmp != null) {
                tmp.suspend();
                return "success";
            }

        } catch (SmartFrogException e) {
            sfLog().error("Failed to suspend \"" + inVMPath + "\"", e);
        }

        // an error occurred
        return "Failed to suspend \"" + inVMPath + "\". Please review the logfile for detailed information.";
    }

    /**
     * Resets a virtual machine.
     * @param inVMPath The full path to the machine.
     * @return "success" or an error message.
     */
    public String resetVM(String inVMPath) throws RemoteException {
        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (tmp != null) {
                tmp.reset();
                return "success";
            }
        } catch (SmartFrogException e) {
            sfLog().error("Failed to reset \"" + inVMPath + "\"", e);
        }

        // an error occurred
        return "Failed to reset \"" + inVMPath + "\". Please review the logfile for detailed information.";
    }

    /**
     * Gets the power state of a virtual machine.
     * @param inVMPath The full path to the machine.
     * @return The status code or STATUS_ERROR.
     */
    public int getPowerState(String inVMPath) throws RemoteException {
        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (tmp != null)
                return tmp.getPowerState();
        } catch (SmartFrogException e) {
            sfLog().error("Failed to get the power state of \"" + inVMPath + "\"", e);
        }

        // an error occurred
        return -1;
    }

    /**
     * Gets the list of vmware images currently under control of the vmware manager.
     * @return Returns a new-line-separated list of the paths of the images.
     * @throws RemoteException
     */
    public String getControlledMachines() throws RemoteException {
        return getControlledMachines("\n");
    }

    /**
     * Gets the list of vmware images currently under control of the vmware manager.
     * @param inSeparator The separator which should be used.
     * @return Rrturns a list of the paths of the images separated by inSeparator.
     * @throws RemoteException
     */
    public String getControlledMachines(String inSeparator) throws RemoteException {
        String strResult = "";

        for (VMWareImageModule mod : vmComm.getImageModuleList()) {
            strResult += mod.getVMPath() + inSeparator;
        }

        return strResult;
    }

    /**
     * Shuts down the VMWare Server and all running machines as well.
     * @return "success" or an error message.
     */
    public String shutdownVMWareServerService() throws RemoteException {
        // shutdown the vmware server service, which will automatically shut down all vms
        try {
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                Runtime.getRuntime().exec("net.exe stop VMWare");
            } else {
                Runtime.getRuntime().exec("/etc/init.d/vmware stop");
            }
        } catch (IOException e) {
            sfLog().error("Failed to shut down vmware server", e);
            return "Failed to shut down vmware server. Please review the logfile for detailed information.";
        }

        return "success";
    }

    /** Starts the vmware server service. */
    public String startVMWareServerService() throws RemoteException {
        try {
            // start the vmware server service
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                    Runtime.getRuntime().exec("net.exe start VMWare");
            } else {
                    Runtime.getRuntime().exec("/etc/init.d/vmware start");
            }
        } catch (IOException e) {
            sfLog().error("Failed to start vmware server", e);
            return "Failed to start vmware server. Please review the logfile for detailed information.";
        }

        return "success";
    }

    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);

        // shut down every virtual machine manually to be indepentant of the vmserver service behaviour
        this.vmComm.disconnect();

        // shut down the vmware server service
//        try {
//            shutdownVMWareServerService();
//        } catch (RemoteException e) {
//            sfLog().error(e);
//        }
    }
}
