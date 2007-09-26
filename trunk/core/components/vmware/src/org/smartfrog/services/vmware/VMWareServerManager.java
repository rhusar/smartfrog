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
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.nio.charset.Charset;


// TODO: think of a better approach to error handling when getting an image module failed
public class VMWareServerManager extends PrimImpl implements VMWareServerManagerServices {

    /** The list of images this manager controls. */
    private ArrayList<VMWareImageModule> listImgModules = new ArrayList<VMWareImageModule>();

    /** The folder where all vm images are stored which are under control of the daemon. */
    private String vmImagesFolder;
    private String vmMasterFolder;

    /** Used to communicate with the vmware server. */
    private VMWareCommunicator vmComm = new VMWareCommunicator();
    private static final Charset BUFFER_ENCODING = Charset.defaultCharset();
    private static final String VMDK = ".vmdk";

    public VMWareServerManager() throws RemoteException {

    }




    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        // get the vm image folders
        setVmImagesFolder(FileSystem.lookupAbsolutePath(this,ATTR_COPY_IMAGES_DIR,null,null,true,null));
        setVmMasterFolder(FileSystem.lookupAbsolutePath(this, ATTR_MASTER_IMAGES_DIR, null, null, true, null));

        // generate the control modules for the vm images
        generateModulesFromImgFolder();

        // start all virtual machines
        for (VMWareImageModule img : listImgModules) {
            img.startUp();  // TODO: error handling
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
                    // create a new image module and add it to the list if successful
                    VMWareImageModule newImg = VMWareImageModule.createImageModule(curFile.getAbsolutePath());
                    if (newImg != null) {
                        listImgModules.add(newImg);
                    }

                    // register it with the vm in case some aren't yet
                    newImg.registerVM();
                }
            }
        }
    }

    /**
     * Unregisters a virtual machine with the vmware server.
     *
     * @param inVMPath The full path to the machine.
     * @return true if the upgrade worked
     */
    public boolean unregisterVM(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null) {
            return tmp.unregisterVM();
        }

        // an error occurred
        return false;
    }

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     *
     * @param inVMPath The full path to the machine.
     * @return true if the vm started
     */
    public boolean startVM(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null) {
            return tmp.startUp();
        }

        // an error occured
        return false;
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
        // first copy the master .vmx file
        copyVMX = getVmImagesFolder() + File.separator + inVMCopyName;
        if (!copyVMX.endsWith(vmxFileFilter.VMX)) {
            copyVMX += vmxFileFilter.VMX;
        }
        File fileVMX = new File(copyVMX);
        FileWriter out = null;
        try {

            copy(getVmMasterFolder() + File.separator + inVMMaster, copyVMX);
            // read all of the .vmx file
            String buffer = FileSystem.readFile(fileVMX, BUFFER_ENCODING).toString();
            String[] strLines = buffer.replace("\r", "").split("\n");

            // extract the name of the new vm
            String strNewName = fileVMX.getName().replace(vmxFileFilter.VMX, "");

            // open the vmx file to write the changes
            out = new FileWriter(fileVMX);

            // parse the lines
            int iHDDIndex = 0;
            for (String line : strLines) {
                if (line.startsWith("scsi") && line.contains(".fileName = ")) {
                    // get the old name
                    String strOld = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));

                    // in case there are more than one hdd append an index
                    String strNewHDD = strNewName + iHDDIndex;

                    // replace it
                    line = line.replace(strOld, strNewHDD + VMDK);

                    // copy the .vmdk files
                    File folder = new File(getVmMasterFolder());
                    strOld = strOld.replace(VMDK, "");
                    File[] files = folder.listFiles();
                    for (File file : files) {
                        String destPath = getVmImagesFolder() + File.separator + file.getName()
                                .replace(strOld, strNewHDD);
                        File destFile = new File(destPath);
                        if ((file.getName().startsWith(strOld + "-f") && file.getName().endsWith(VMDK))) {
                            // copy the file
                            FileSystem.fCopy(file, destFile);
                        } else if (file.getName().equals(strOld + VMDK)) {
                            // it's the .vmdk config file
                            // read it's content
                            String strBuffer = FileSystem.readFile(file, BUFFER_ENCODING).toString();
                            // replace the old names
                            strBuffer = strBuffer.replace(strOld, strNewHDD);
                            FileSystem.writeTextFile(destFile, strBuffer, BUFFER_ENCODING);
                        }
                    }

                    ++iHDDIndex;
                } else if (line.startsWith("displayName = ")) {
                    // replace the display name
                    line = "displayName = \"" + strNewName + "\"";
                }
                out.write(line + "\n");
            }

            // close the vmx file
            out.flush();
            out.close();
            out=null;
            // set execution rights if were using linux
            chmod(fileVMX, "755");
        } catch (IOException e) {
            throw SmartFrogException.forward("Failed to copy " + inVMMaster + " to " + inVMCopyName,
                    e);
        } finally {
            FileSystem.close(out);
        }


        // register the file with the vmware server
        if (!registerVM(copyVMX)) {
            throw new SmartFrogException("Failed to register the VM");
        }
    }

    private void chmod(File fileVMX, String mask) throws SmartFrogException {
        String command = "chmod " + mask + fileVMX.getPath();
        try {
            if (!System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                Process ps = Runtime.getRuntime().exec(command);
                ps.waitFor();
            }
        } catch (IOException e) {
            throw SmartFrogException.forward("Failed to execute "+command,e);
        } catch (InterruptedException e) {
            throw SmartFrogException.forward("Failed to execute " + command, e);
        }
    }

    /**
     * Copies a file.
     *
     * @param from source
     * @param to dest
     * @throws IOException on failure to copy
     */
    private void copy(String from, String to) throws IOException {
        sfLog().info("VMWareServerManager creating copy. From: " + from + " to: " + to);
        File fromFile = new File(from);
        File toFile = new File(to);
        FileSystem.fCopy(fromFile,toFile);
    }

    /**
     * Delete a instance of a master copy.
     *
     * @param inVMPath
     * @throws java.rmi.RemoteException
     */
    public boolean deleteCopy(String inVMPath) throws RemoteException {
        // first stop the vm
        stopVM(inVMPath);

        // then unregister it
        unregisterVM(inVMPath);

        // remove the image module
        removeMachineModule(inVMPath);

        // then get the file
        File file = new File(inVMPath);

        // then delete it
        if (file.exists() && file.isFile() && file.getName().endsWith(vmxFileFilter.VMX)) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     *
     * @param inVMPath The full path to the machine.
     */
    public boolean stopVM(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null) {
            return tmp.shutDown();
        }

        // an error occurred
        return false;
    }

    /**
     * Suspends a virtual machine. Has to be running.
     *
     * @param inVMPath The full path to the machine.
     */
    public boolean suspendVM(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null) {
            return tmp.suspend();
        }

        // an error occurred
        return false;
    }

    /**
     * Resets a virtual machine.
     *
     * @param inVMPath The full path to the machine.
     */
    public boolean resetVM(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null) {
            return tmp.reset();
        }

        // an error occurred
        return false;
    }

    /**
     * Gets the power state of a virtual machine.
     *
     * @param inVMPath The full path to the machine.
     */
    public int getPowerState(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null) {
            return tmp.getPowerState();
        }

        // an error occurred
        return VMWareImageModule.STATUS_ERROR;
    }

//      VMFox code
//          to be used when VMFox is running correctly
//    /**
//     * Gets the tools state of a virtual machine.
//     *
//     * @param inVMPath The full path to the machine.
//     * @return
//     */
//    public int getToolsState(String inVMPath) throws RemoteException {
//        // get a machine module
//        VMWareImageModule tmp = getMachineModule(inVMPath);
//
//        // check if it worked
//        if (tmp != null)
//            return tmp.getToolsState();
//
//        // an error occurred
//        return VMWareImageModule.STATUS_ERROR;
//    }

    public String getControlledMachines() throws RemoteException {
        String strResult = "";

        for (VMWareImageModule mod : listImgModules) {
            strResult += mod.getVMPath() + "\n";
        }

        return strResult;
    }

    /**
     * Gets an existing machine module or attempts to create a new one.
     *
     * @param inVMPath The path to the .vmx file.
     * @return Returns an VMWareImageModule or null if inVMPath isn't valid.
     */
    private VMWareImageModule getMachineModule(String inVMPath) {
        // parse the list of image modules
        for (VMWareImageModule imgMod : listImgModules) {
            if (imgMod.getVMPath().equals(inVMPath)) {
                return imgMod;
            }
        }

        // no module found, create a new one
        VMWareImageModule newMod = VMWareImageModule.createImageModule(inVMPath);

        // if valid add it to the list
        if (newMod != null) {
            listImgModules.add(newMod);
        }

        return newMod;
    }

    /**
     * Removes a vm image module.
     *
     * @param inVMPath
     */
    private void removeMachineModule(String inVMPath) {
        // find the appropriate module
        for (VMWareImageModule mod : listImgModules) {
            if (mod.getVMPath().equals(inVMPath)) {
                listImgModules.remove(mod);
                break;
            }
        }
    }

    /**
     * Shuts down the VMWare Server and all running machines as well.
     *
     */
    public boolean shutdownVMWareServerService() throws RemoteException {
        // shutdown the vmware server service, which will automatically shut down all vms
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            try {
                Runtime.getRuntime().exec("net.exe stop VMWare");
            } catch (IOException e) {
                return false;
            }
        } else {
            try {
                Runtime.getRuntime().exec("/etc/init.d/vmware stop");
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    /** Starts the vmware server service. */
    public boolean startVMWareServerService() throws RemoteException {
        // start the vmware server service
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            try {
                Runtime.getRuntime().exec("net.exe start VMWare");
            } catch (IOException e) {
                sfLog().error("failed to start vmware", e);
                return false;
            }
        } else {
            try {
                Runtime.getRuntime().exec("/etc/init.d/vmware start");
            } catch (IOException e) {
                sfLog().error("failed to start vmware",e);
                return false;
            }
        }

        return true;
    }

    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);

        // shut down every virtual machine manually to be indepentant of the vmserver service behaviour
        for (VMWareImageModule img : listImgModules) {
            img.shutDown();     // TODO: error handling
        }

        // shut down the vmware server service
        try {
            shutdownVMWareServerService();
        } catch (RemoteException e) {
            sfLog().ignore(e);
        }
    }

    /**
     * Registes a virtual machine with the vmware server.
     *
     * @param inVMPath The full path to the machine.
     * @return true if it worked
     *
     */
    public boolean registerVM(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null) {
            return tmp.registerVM();
        }

        // an error occurred
        return false;
    }
}
