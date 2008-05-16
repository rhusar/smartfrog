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
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.ArrayList;


public class VMWareServerManager extends PrimImpl implements VMWareServerManagerServices {
    /** The folder where all vm images are stored which are under control of the daemon. */
    private String vmImagesFolder;
    private String vmMasterFolder;

    /** Used to communicate with the vmware server. */
    private VMWareCommunicator vmComm = null;
    private static final String VMPATH = "vmpath";
    private static final String VMACTION = "vmaction";

    public VMWareServerManager() throws RemoteException {

    }

    /**
     * Startup: check that VMWare is installed
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        // check wether the VMware Server is installed on this system
        boolean installed = sfResolve(ATTR_SERVER_INSTALLED, false, true);
        if (!installed) {
            throw new SmartFrogDeploymentException("A compatible version of VMware is not installed on this system");
        }
    }

    /**
     * Start up
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        // get the vix properties
        String strVixLibPath, strVixLibName;
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            strVixLibPath = sfResolve(ATTR_VIXLIBRARYPATH_WIN, "", true);
            strVixLibName = sfResolve(ATTR_VIXLIBRARYNAME_WIN, "", true);
        }
        else {
            strVixLibPath = sfResolve(ATTR_VIXLIBRARYPATH_LINUX, "", true);
            strVixLibName = sfResolve(ATTR_VIXLIBRARYPNAME_LINUX, "", true);
        }

        // create the jna wrapper library
        try {
            vmComm = new VMWareCommunicator(strVixLibPath, strVixLibName);
        } catch (Exception e) {
            sfLog().error("Error while creating the VMware communicator.", e);
            throw new SmartFrogDeploymentException("Error while creating the VMware communicator.", e);
        }

        // get the vm image folders
        setVmImagesFolder(FileSystem.lookupAbsolutePath(this,ATTR_COPY_IMAGES_DIR,null,null,true,null));
        setVmMasterFolder(FileSystem.lookupAbsolutePath(this, ATTR_MASTER_IMAGES_DIR, null, null, true, null));

        // generate the control modules for the vm images
        loadExistingVMImages();

        // start all virtual machines
//        for (VMWareImageModule img : vmComm.getImageModuleList()) {
//            try {
//                img.startUp();
//            } catch (SmartFrogException e) {
//                sfLog().err(e);
//            }
//        }

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

    /**
     * Converts a powerstate into a readable string.
     * @param inState The powerstate of a virtual machine.
     * @return The readable string.
     */
    public String convertPowerState(int inState) {
        return vmComm.convertPowerState(inState);
    }

    /**
     * Converts a toolsstate into a readable string.
     * @param inState The toolsstate of a virtual machine.
     * @return The readable string.
     */
    public String convertToolsState(int inState) {
        return vmComm.convertToolsState(inState);
    }

    /**
     * Should only be called in <code>sfStart()</code>!
     * Generates a <code>VMWareImageModule</code> for each vm image in a subfolder of <code>vmImagesFolder</code>
     */
    private void loadExistingVMImages() {
        // get the folder
        File folder = new File(vmImagesFolder);
        if (folder.exists()) {
            // get all subfolders
            File[] files = folder.listFiles();
            for (File cur : files) {
                if (cur.isDirectory()) {
                    // check if there is a valid (Avalanche conform :D) vmware image in this folder
                    File img = new File(cur,cur.getName() + ".vmx");
                    if (img.exists()) {
                        // create a VMWareImageModule
                        try {
                            vmComm.createImageModule(img.getAbsolutePath());
                        } catch (FileNotFoundException e) {
                            sfLog().error("loadExistingVMImages: Failed to load virtual machine: ",e);
                        }
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
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String startVM(String inVMPath) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";

        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (tmp != null) {
                tmp.startUp();
            }
            else strResponse = "Failed to start \"" + inVMPath + "\": Image module not existing.";

        } catch (SmartFrogException e) {
            throw failure("start",inVMPath,e);
        }

        return strResponse;
    }

    private SmartFrogException failure(String action,String vmpath,Exception e) {
        String message = "Failed to " + action + " " + vmpath;
        sfLog().error(message, e);
        SmartFrogException sfex= SmartFrogException.forward(message,e);
        sfex.put(VMPATH,vmpath);
        sfex.put(VMACTION, action);
        return sfex;
    }


    /**
     * Return a list of the vmware images in the master folder.
     *
     * @return a string containing every master image (.vmx file) listed on a separate line
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String getMasterImages() throws RemoteException, SmartFrogException{
        String strResult = "";

        // get the master folder
        File folder = new File(getVmMasterFolder());
        if (folder.exists()) {
            // get the folders of the master images
            File[] files = folder.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    strResult += f.getName() + "\n";
                }
            }
        }

        return strResult;
    }


    /**
     * Create a new copy of a master image.
     * @param inVMMaster Name of the master image.
     * @param inVMCopyName Name of the new copy.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String createCopyOfMaster(String inVMMaster, String inVMCopyName) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";

        // compose the locations
        String strMasterName = getVmMasterFolder() + File.separator + inVMMaster + File.separator + inVMMaster + ".vmx";
        String strCopyDest = getVmImagesFolder() + File.separator + inVMCopyName;

        try {
            // do the copy
            vmComm.copyVirtualMachine(strMasterName, strCopyDest);

            // create a new image module for the copy
            VMWareImageModule newImg = vmComm.createImageModule(strCopyDest + File.separator + inVMMaster + ".vmx");

            // rename the copy
            newImg.rename(inVMCopyName);

        } catch (Exception e) {
            throw failure("copy", strMasterName, e);
        }

        return strResponse;
    }

    /**
     * Deletes a virtual machine. (Including the files.)
     * @param inVMPath The path to the .vmx file.
     * @return "success" or an error message
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String deleteCopy(String inVMPath) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";

        try {
            if(!vmComm.deleteVirtualMachine(inVMPath))
                strResponse = "Failed to delete \"" + inVMPath + "\": Image module not existing.";

        } catch (SmartFrogException e) {
            throw failure("delete",inVMPath , e);
        }

        return strResponse;
    }

    /**
     * Changes the display name of a virtual machine.
     *
     * @param inVMPath  The path to the .vmx file.
     * @param inNewName The new name for the machine.
     * @return "success" or an error message.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String renameVM(String inVMPath, String inNewName) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";

        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (tmp != null) {
                tmp.rename(inNewName);
            }
            else strResponse = "Failed to rename \"" + inVMPath + "\": Image module not existing.";
        } catch (SmartFrogException e) {
            throw failure("rename", inVMPath, e);
        }

        // an error occurred
        return strResponse;
    }

    /**
     * Gets the value of an attribute of a VM.
     *
     * @param inVMPath The path to the .vmx file.
     * @param inKey    The attribute key.
     * @return The value of the key.
     * @throws java.rmi.RemoteException
     */
    public String getVMAttribute(String inVMPath, String inKey) throws RemoteException {
        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (tmp != null) {
                return tmp.getAttribute(inKey);
            }
        } catch (SmartFrogException e) {
            sfLog().error("Failed to get attribute of \"" + inVMPath + "\"", e);
        }

        // an error occurred
        return "";
    }

    /**
     * Sets the value of an attribute of a VM.
     *
     * @param inVMPath The path to the .vmx file.
     * @param inKey    The attribute key.
     * @param inValue  The value for the key.
     * @return Returns the old value of the key.
     * @throws java.rmi.RemoteException
     */
    public String setVMAttribute(String inVMPath, String inKey, String inValue) throws RemoteException {
        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (tmp != null) {
                return tmp.setAttribute(inKey, inValue);
            }
        } catch (SmartFrogException e) {
            sfLog().error("Failed to rename \"" + inVMPath + "\"", e);
        }

        // an error occurred
        return "";
    }

//    /**
//     * Copies a file from the host OS into the guest OS of the specified VM.
//     *
//     * @param inVMPath     The vm which contains the guest OS.
//     * @param inSourceFile The path on the host OS.
//     * @param inTargetFile The path on the guest OS.
//     * @return "success" or an error message.
//     * @throws java.rmi.RemoteException
//     */
//    public String copyFileFromHostToGuestOS(String inVMPath, String inSourceFile, String inTargetFile) throws RemoteException {
//        // error string
//        String strResponse = "success";
//
//        try {
//            // get a machine module
//            VMWareImageModule tmp = vmComm.getImageModule(inVMPath);
//
//            // check if it worked
//            if (tmp != null) {
//                tmp.copyFileFromHostToGuestOS(inSourceFile, inTargetFile);
//            }
//            else strResponse = "Failed to copy file from host OS to guest OS in \"" + inVMPath + "\": Image module not existing.";
//        } catch (SmartFrogException e) {
//            sfLog().error("Failed to copy file from host OS to guest OS in \"" + inVMPath + "\"", e);
//            strResponse = "Exception while copying file from host OS to guest OS in \"" + inVMPath + "\": " + e.toString();
//        }
//
//        // an error occurred
//        return strResponse;
//    }

    /**
     * Shuts down a virtual machine. Has to be powered on.
     *
     * @param inVMPath The full path to the machine.
     * @return Returns "success" or an error message.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String shutDownVM(String inVMPath) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";

        try {
            // get a machine module
            VMWareImageModule module = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (module != null) {
                module.shutDown();
            }
            else strResponse = "Failed to shut down \"" + inVMPath + "\": Image module not existing.";
        } catch (SmartFrogException e) {
            throw failure("shutdown",inVMPath , e);
        }

        // an error occurred
        return strResponse;
    }

    /**
     * Suspends a virtual machine. Has to be running.
     *
     * @param inVMPath The full path to the machine.
     * @return Returns "success" or an error message.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String suspendVM(String inVMPath) throws RemoteException, SmartFrogException{
        // error string
        String strResponse = "success";

        try {
            // get a machine module
            VMWareImageModule module = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (module != null) {
                module.suspend();
            }
            else strResponse = "Failed to suspend \"" + inVMPath + "\": Image module does not exist";

        } catch (SmartFrogException e) {
            throw failure("suspend",inVMPath, e);
        }

        return strResponse;
    }

    /**
     * Resets a virtual machine.
     * @param inVMPath The full path to the machine.
     * @return "success" or an error message.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String resetVM(String inVMPath) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";

        try {
            // get a machine module
            VMWareImageModule module = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (module != null) {
                module.reset();
            }
            else strResponse = "Failed to reset \"" + inVMPath + "\": Image module not existing.";

        } catch (SmartFrogException e) {
            throw failure("reset",inVMPath , e);
        }

        // an error occurred
        return strResponse;
    }

    /**
     * Gets the power state of a virtual machine.
     * @param inVMPath The full path to the machine.
     * @return The status code or STATUS_ERROR.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public int getPowerState(String inVMPath) throws RemoteException, SmartFrogException {
        try {
            // get a machine module
            VMWareImageModule module = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (module != null) {
                return module.getPowerState();
            }
        } catch (SmartFrogException e) {
            throw failure("get power state",inVMPath , e);
        }

        // an error occurred
        return 0;
    }

    /**
     * Gets the tools state of a virtual machine.
     *
     * @param inVMPath The full path to the machine.
     * @return the state of the tools, 0 for an error
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public int getToolsState(String inVMPath) throws RemoteException, SmartFrogException {
        try {
            // get a machine module
            VMWareImageModule module = vmComm.getImageModule(inVMPath);

            // check if it worked
            if (module != null) {
                return module.getToolsState();
            }
        } catch (SmartFrogException e) {
            throw failure("get tool state", inVMPath , e);
        }

        // an error occurred
        return 0;
    }

    /**
     * Gets the list of vmware images currently under control of the vmware manager.
     * @return The image modules of this machine.
     * @throws RemoteException network problems
     */
    public ArrayList<VMWareImageModule> getControlledMachines() throws RemoteException {
        return vmComm.getImageModuleList();
    }

    /**
     * Shuts down the VMWare Server and all running machines as well.
     * @return "success" or an error message.
     */
    public String shutdownVMWareServerService() throws RemoteException {
        // error string
        String strResponse = "success";

        // shutdown the vmware server service, which will automatically shut down all vms
        try {
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                Process ps = Runtime.getRuntime().exec("net.exe stop \"VMware Registration Service\"");
                try {
                    ps.waitFor();
                } catch (InterruptedException e) {
                    sfLog().error(e);
                }
                Runtime.getRuntime().exec("net.exe stop \"VMware Authorization Service\"");
            } else {
                Runtime.getRuntime().exec("/etc/init.d/vmware stop");
            }
        } catch (IOException e) {
            sfLog().error("Failed to shut down vmware server", e);
            strResponse = "Exception while shutting down vmware server: " + e.toString();
        }

        return strResponse;
    }

    /** Starts the vmware server service. */
    public String startVMWareServerService() throws RemoteException {
        // error string
        String strResponse = "success";

        try {
            // start the vmware server service
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                Process ps = Runtime.getRuntime().exec("net.exe start \"VMware Authorization Service\"");
                try {
                    ps.waitFor();
                } catch (InterruptedException e) {
                    sfLog().error(e);
                }
                Runtime.getRuntime().exec("net.exe start \"VMware Registration Service\"");
            } else {
                Runtime.getRuntime().exec("/etc/init.d/vmware start");
            }
        } catch (IOException e) {
            sfLog().error("Failed to start vmware server", e);
            strResponse = "Exception while starting vmware server: " + e.toString();
        }

        return strResponse;
    }

    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);

        // shut down every virtual machine manually to be indepentant of the vmserver service behaviour
        vmComm.disconnect();

        // shut down the vmware server service
//        try {
//            shutdownVMWareServerService();
//        } catch (RemoteException e) {
//            sfLog().error(e);
//        }
    }
}
