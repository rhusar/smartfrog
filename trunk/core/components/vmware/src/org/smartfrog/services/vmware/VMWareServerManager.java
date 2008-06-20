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

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;


public class VMWareServerManager extends PrimImpl implements VMWareServerManagerServices {
    /** The folder where all vm images are stored which are under control of the daemon. */
    private String vmImagesFolder;
    private String vmMasterFolder;

    /** Used to communicate with the vmware server. */
    private VMWareCommunicator vmComm = null;
    private static final String VMPATH = "vmpath";
    private static final String VMACTION = "vmaction";

    // number of retries
    private int NumRetries = 0;

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
        setVmImagesFolder((String)sfResolve(ATTR_COPY_IMAGES_DIR, true));
        setVmMasterFolder((String)sfResolve(ATTR_MASTER_IMAGES_DIR, true));

        // get the number of retries
        NumRetries = sfResolve(ATTR_NUM_RETRIES, 0, false);

        // generate the control modules for the vm images
        loadExistingVMImages();
    }

    /**
     * Start up
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

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
     * @param inVMName The name of the VM.
     * @return true if the vm started
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String startVM(String inVMName) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);

        // get a machine module
        VMWareImageModule tmp = vmComm.getImageModule(strVMPath);

        // check if it worked
        SmartFrogException ex = null;
        if (tmp != null) {
            for (int i = NumRetries; i >= 0; --i) {
                try {
                    tmp.startUp();
                } catch (SmartFrogException e) {
                    sfLog().info("retrying startVM", e);
                    ex = e;
                    continue;
                }
                ex = null;
                break;
            }
        }
        else strResponse = "Failed to start \"" + strVMPath + "\": Image module not existing.";

        if (ex != null)
            throw failure("start",strVMPath,ex);

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
     * @param inUsername Username for the quest os.
     * @param inPassword Password for the guest os.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String createCopyOfMaster(String inVMMaster, String inVMCopyName, String inUsername, String inPassword) throws RemoteException, SmartFrogException {
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

          // set the user credentials
            if ((inUsername != null) && (inPassword != null)) {
                newImg.setGuestOSUser(inUsername);
                newImg.setGuestOSPasswd(inPassword);
            }
            else sfLog().error("user/pass are null");
         } catch (Exception e) {
            throw failure("copy", strMasterName, e);
        }

        return strResponse;
    }

    /**
     * Deletes a virtual machine. (Including the files.)
     * @param inVMName The name of the VM.
     * @return "success" or an error message
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String deleteCopy(String inVMName) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);

        try {
            if(!vmComm.deleteVirtualMachine(strVMPath))
                strResponse = "Failed to delete \"" + strVMPath + "\": Image module not existing.";

        } catch (SmartFrogException e) {
            throw failure("delete",strVMPath , e);
        }

        return strResponse;
    }

    /**
     * Changes the display name of a virtual machine.
     *
     * @param inVMName  The name of the VM.
     * @param inNewName The new name for the machine.
     * @return "success" or an error message.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String renameVM(String inVMName, String inNewName) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);

        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(strVMPath);

            // check if it worked
            if (tmp != null) {
                tmp.rename(inNewName);
            }
            else strResponse = "Failed to rename \"" + strVMPath + "\": Image module not existing.";
        } catch (SmartFrogException e) {
            throw failure("rename", strVMPath, e);
        }

        // an error occurred
        return strResponse;
    }

    /**
     * Gets the value of an attribute of a VM.
     *
     * @param inVMName The name of the VM.
     * @param inKey    The attribute key.
     * @return The value of the key.
     * @throws java.rmi.RemoteException
     */
    public String getVMAttribute(String inVMName, String inKey) throws RemoteException {
        String strVMPath = constructVMPath(inVMName);

        String strResult = "";

        // get a machine module
        VMWareImageModule tmp = vmComm.getImageModule(strVMPath);

        // check if it worked
        SmartFrogException ex = null;
        if (tmp != null) {
            for (int i = NumRetries; i >= 0; --i) {
                try {
                    strResult = tmp.getAttribute(inKey);
                } catch (SmartFrogException e) {
                    ex = e;
                    sfLog().info("retrying getVMAttribute", e);
                    continue;
                }
                ex = null;
                break;
            }
        }

        if (ex != null)
            sfLog().error("Failed to get attribute of \"" + strVMPath + "\"", ex);


        // an error occurred
        return strResult;
    }

    /**
     * Sets the value of an attribute of a VM.
     *
     * @param inVMName The name of the VM.
     * @param inKey    The attribute key.
     * @param inValue  The value for the key.
     * @return Returns the old value of the key.
     * @throws java.rmi.RemoteException
     */
    public String setVMAttribute(String inVMName, String inKey, String inValue) throws RemoteException {
        String strVMPath = constructVMPath(inVMName);

        String strResult = "";

        // get a machine module
        VMWareImageModule tmp = vmComm.getImageModule(strVMPath);

        // check if it worked
        SmartFrogException ex = null;
        if (tmp != null) {
            for (int i = NumRetries; i >= 0; --i) {
                try {
                    strResult = tmp.setAttribute(inKey, inValue);
                } catch (SmartFrogException e) {
                    ex = e;
                    sfLog().info("retrying setVMAttribute", e);
                    continue;
                }
                ex = null;
                break;
            }
        }

        if (ex != null)
            sfLog().error("Failed to rename \"" + strVMPath + "\"", ex);

        // an error occurred
        return strResult;
    }

    /**
     * Shuts down a virtual machine. Has to be powered on.
     *
     * @param inVMName The name of the VM.
     * @return Returns "success" or an error message.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String shutDownVM(String inVMName) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);

        // get a machine module
        VMWareImageModule module = vmComm.getImageModule(strVMPath);

        // check if it worked
        SmartFrogException ex = null;
        if (module != null) {
            for (int i = NumRetries; i >= 0; --i) {
                try {
                    module.shutDown();
                } catch (SmartFrogException e) {
                    ex = e;
                    sfLog().info("retrying shutDownVM", e);
                    continue;
                }
                ex = null;
                break;
            }
        }
        else strResponse = "Failed to shut down \"" + strVMPath + "\": Image module not existing.";

        if (ex != null)
            throw failure("shutdown",strVMPath , ex);

        // an error occurred
        return strResponse;
    }

    /**
     * Suspends a virtual machine. Has to be running.
     *
     * @param inVMName The name of the VM.
     * @return Returns "success" or an error message.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String suspendVM(String inVMName) throws RemoteException, SmartFrogException{
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);


        // get a machine module
        VMWareImageModule module = vmComm.getImageModule(strVMPath);

        // check if it worked
        SmartFrogException ex = null;
        if (module != null) {
            for (int i = NumRetries; i >= 0; --i) {
                try {
                    module.suspend();
                } catch (SmartFrogException e) {
                    ex = e;
                    sfLog().info("retrying suspendVM", e);
                    continue;
                }
                ex = null;
                break;
            }
        }
        else strResponse = "Failed to suspend \"" + strVMPath + "\": Image module does not exist";

        if (ex != null)
            throw failure("suspend",strVMPath, ex);

        return strResponse;
    }

    /**
     * Resets a virtual machine.
     * @param inVMName The name of the VM.
     * @return "success" or an error message.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public String resetVM(String inVMName) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);

        // get a machine module
        VMWareImageModule module = vmComm.getImageModule(strVMPath);

        // check if it worked
        SmartFrogException ex = null;
        if (module != null) {
            for (int i = NumRetries; i >= 0; --i) {
                try {
                    module.reset();
                } catch (SmartFrogException e) {
                    ex = e;
                    sfLog().info("retrying resetVM", e);
                    continue;
                }
                ex = null;
                break;
            }
        }
        else strResponse = "Failed to reset \"" + strVMPath + "\": Image module not existing.";

        if (ex != null)
            throw failure("reset", strVMPath, ex);

        // an error occurred
        return strResponse;
    }

    /**
     * Gets the power state of a virtual machine.
     * @param inVMName The name of the VM..
     * @return The status code or STATUS_ERROR.
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public int getPowerState(String inVMName) throws RemoteException, SmartFrogException {
        String strVMPath = constructVMPath(inVMName);

        int iResult = 0;

        // get a machine module
        VMWareImageModule module = vmComm.getImageModule(strVMPath);

        // check if it worked
        SmartFrogException ex = null;
        if (module != null) {
            for (int i = NumRetries; i >= 0; --i) {
                try {
                    iResult = module.getPowerState();
                } catch (SmartFrogException e) {
                    ex = e;
                    sfLog().info("retrying getPowerState", e);
                    continue;
                }
                ex = null;
                break;
            }
        }

        if (ex != null)
            throw failure("get power state",strVMPath , ex);

        // an error occurred
        return iResult;
    }

    /**
     * Gets the tools state of a virtual machine.
     *
     * @param inVMName The name of the VM.
     * @return the state of the tools, 0 for an error
     * @throws RemoteException network problems
     * @throws SmartFrogException problems with the virtual machines
     */
    public int getToolsState(String inVMName) throws RemoteException, SmartFrogException {
        String strVMPath = constructVMPath(inVMName);

        int iResult = 0;

        // get a machine module
        VMWareImageModule module = vmComm.getImageModule(strVMPath);

        // check if it worked
        SmartFrogException ex = null;
        if (module != null) {
            for (int i = NumRetries; i >= 0; --i) {
                try {
                    iResult = module.getToolsState();
                } catch (SmartFrogException e) {
                    ex = e;
                    sfLog().info("retrying getToolsState", e);
                    continue;
                }
                ex = null;
                break;
            }
        }

        if (ex != null)
            throw failure("get tool state", strVMPath, ex);

        // an error occurred
        return iResult;
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

    public String executeInGuestOS(String inVMName, String inCommand, String inParameters, boolean inNoWait) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);

        // get a machine module
        VMWareImageModule module = vmComm.getImageModule(strVMPath);

        // check if it worked
        SmartFrogException ex = null;
        if (module != null) {
            for (int i = NumRetries; i >= 0; --i) {
                try {
                    module.executeInGuestOS(inCommand, inParameters, inNoWait);
                } catch (SmartFrogException e) {
                    ex = e;
                    sfLog().info("retrying executeInGuestOS", e);
                    continue;
                }
                ex = null;
                break;
            }
        } else
            strResponse = String.format("Failed to execute: \"%s %s\": Image module not existing.", inCommand, inParameters);

        if (ex != null)
            throw failure(String.format("execute \"%s %s\" in guest os of", inCommand, inParameters), strVMPath, ex);

        return strResponse;
    }

    public String takeSnapshot(String inVMName, String inSnapshotName, String inSnapshotDescription, boolean inIncludeMemory) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);

        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(strVMPath);

            // check if it worked
            if (tmp != null) {
                tmp.takeSnapshot(inSnapshotName, inSnapshotDescription, inIncludeMemory);
            }
            else strResponse = "Failed to take a snapshot of \"" + strVMPath + "\": Image module not existing.";
        } catch (SmartFrogException e) {
            throw failure("takeSnapshot", strVMPath, e);
        }

        // an error occurred
        return strResponse;
    }

    public String revertVMToSnapshot(String inVMName, String inSnapshotName) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);

        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(strVMPath);

            // check if it worked
            if (tmp != null) {
                tmp.revertToSnapshot(inSnapshotName);
            }
            else strResponse = "Failed to revert to named snapshot of \"" + strVMPath + "\": Image module not existing.";
        } catch (SmartFrogException e) {
            throw failure("revertVMToSnapshot", strVMPath, e);
        }

        // an error occurred
        return strResponse;
    }

    public String revertVMToSnapshot(String inVMName) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);

        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(strVMPath);

            // check if it worked
            if (tmp != null) {
                tmp.revertToSnapshot();
            }
            else strResponse = "Failed to revert to current snapshot of \"" + strVMPath + "\": Image module not existing.";
        } catch (SmartFrogException e) {
            throw failure("revertVMToSnapshot", strVMPath, e);
        }

        // an error occurred
        return strResponse;
    }

    /**
     * Deletes the current snapshot of a virtual machine.
     * @param inVMName The name of the virtual machine.
     * @param inDeleteChildren Delete the children of the snapshot as well?
     * @return "success" or an error message.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public String deleteVMSnapshot(String inVMName, boolean inDeleteChildren) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);

        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(strVMPath);

            // check if it worked
            if (tmp != null) {
                tmp.deleteSnapshot(inDeleteChildren);
            }
            else strResponse = "Failed to delete to current snapshot of \"" + strVMPath + "\": Image module not existing.";
        } catch (SmartFrogException e) {
            throw failure("deleteVMSnapshot", strVMPath, e);
        }

        // an error occurred
        return strResponse;
    }

    /**
     * Deletes a named snapshot of a virtual machine.
     * @param inVMName The name of the virtual machine.
     * @param inSnapshotName The name of the snapshot.
     * @param inDeleteChildren Delete the children of the snapshot as well?
     * @return "success" or an error message.
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public String deleteVMSnapshot(String inVMName, String inSnapshotName, boolean inDeleteChildren) throws RemoteException, SmartFrogException {
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);

        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(strVMPath);

            // check if it worked
            if (tmp != null) {
                tmp.deleteSnapshot(inSnapshotName, inDeleteChildren);
            }
            else strResponse = "Failed to delete to a named snapshot of \"" + strVMPath + "\": Image module not existing.";
        } catch (SmartFrogException e) {
            throw failure("deleteVMSnapshot", strVMPath, e);
        }

        // an error occurred
        return strResponse;
    }

    /**
     * Waits for the tools in the guest OS to come up.
     * @param inVMName Name of the virtual machine.
     * @param inTimeout The timeout in seconds. 0 means there is no timeout.
     * @return "success" or an error message.
     * @throws SmartFrogException
     */
    public String waitForTools(String inVMName, int inTimeout) throws SmartFrogException {
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);

        try {
            // get a machine module
            VMWareImageModule tmp = vmComm.getImageModule(strVMPath);

            // check if it worked
            if (tmp != null) {
                tmp.waitForTools(inTimeout);
            }
            else strResponse = "Failed to wait for tools to come up in guest os of \"" + strVMPath + "\": Image module not existing.";
        } catch (SmartFrogException e) {
            throw failure("waitForTools", strVMPath, e);
        }

        // an error occurred
        return strResponse;
    }

    /**
     * Constructs the full path to the .vmx file of a VM.
     * @param inVMName The name of the VM.
     * @return
     */
    private String constructVMPath(String inVMName) {
        return vmImagesFolder + File.separator + inVMName + File.separator + inVMName + ".vmx";
    }

    /**
     * Sets the username and password for the guest operating system of a virtual machine.
     * @param inVMName The name of the virtual machine.
     * @param inUser The username.
     * @param inPass The password.
     * @return "success" or an error message.
     */
    public String setGuestOSCredentials(String inVMName, String inUser, String inPass) {
        // error string
        String strResponse = "success";
        String strVMPath = constructVMPath(inVMName);

        // get a machine module
        VMWareImageModule tmp = vmComm.getImageModule(strVMPath);

        // check if it worked
        if (tmp != null) {
            tmp.setGuestOSUser(inUser);
            tmp.setGuestOSPasswd(inPass);
        }
        else strResponse = "Failed to set credentials for guest os of \"" + strVMPath + "\": Image module not existing.";

        // an error occurred
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
