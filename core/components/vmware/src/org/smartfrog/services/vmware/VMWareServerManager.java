/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.services.vmware;

import org.smartfrog.sfcore.prim.PrimImpl;

import org.smartfrog.sfcore.prim.TerminationRecord;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.regex.*;
import java.io.*;


// TODO: think of a better approach to error handling when getting an image module failed
public class VMWareServerManager extends PrimImpl implements VMWareServerManagerServices {

     /**
     * The list of images this manager controls.
     */
    private ArrayList<VMWareImageModule> listImgModules;

    /**
     * The folder where all vm images are stored which are under control of the daemon.
     */
    private static String VM_IMAGES_FOLDER;
    private static String VM_MASTER_FOLDER;

    /**
     * Used to communicate with the vmware server.
     */
    private VMWareCommunicator vmComm = new VMWareCommunicator();

    public VMWareServerManager() throws RemoteException {
        
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        // create the list of image modules
        listImgModules = new ArrayList<VMWareImageModule>();

        sfLog().info("VMWare Server Manager deployed.");
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        // get the vm image folders
        String strSFHome = System.getenv("SFHOME");
        if (strSFHome != null) {
            VM_IMAGES_FOLDER = strSFHome + File.separator + "vmcopyimages";
            VM_MASTER_FOLDER = strSFHome + File.separator + "vmmasterimages";
        }
        else throw new SmartFrogException("Environment variable \"SFHOME\" not set.");

        // generate the control modules for the vm images
        generateModulesFromImgFolder();

        // start all virtual machines
        for (VMWareImageModule img : listImgModules)
            img.startUp();  // TODO: error handling

        sfLog().info("VMWareServerManager started.");
    }

    /**
     * A filename filter which only accepts ".vmx" files.
     */
    private class vmxFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.endsWith(".vmx");
        }
    }

    // TODO: add createVMImage functionality

    /**
     * Should only be called in sfStart()!
     * Generates a VMWareImageModule for each .vmx file in the designated vm images folder.
     */
    private void generateModulesFromImgFolder()
    {
        // get the folder
        File folder = new File(VM_IMAGES_FOLDER);
        if (folder.exists())
        {
            // get the files in the folder
            File[] files = folder.listFiles(new vmxFileFilter());
            if (files != null)
                for (File curFile : files)
                {
                    // create a new image module and add it to the list if successful
                    VMWareImageModule newImg = VMWareImageModule.createImageModule(curFile.getAbsolutePath());
                    if (newImg != null)
                        listImgModules.add(newImg);

                    // register it with the vm in case some aren't yet
                    newImg.registerVM();
                }
        }
    }

    /**
     * Unregisters a virtual machine with the vmware server.
     *
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean unregisterVM(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null)
            return tmp.unregisterVM();

        // an error occurred
        return false;
    }

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     *
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean startVM(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null)
            return tmp.startUp();

        // an error occured
        return false;
    }

    /**
     * Return a list of the vmware images in the master folder.
     * @return
     * @throws RemoteException
     */
    public String getMasterImages() throws RemoteException {
        String strResult = "";

        // get the files
        File folder = new File(VM_MASTER_FOLDER);
        if (folder.exists()) {
            File[] files = folder.listFiles();
            for (File f : files) {
                if (f.getName().endsWith(".vmx")) {
                    strResult += f.getName() + "\n";
                }
            }
        }

        return strResult;
    }

    /**
     * Loads the whole content of a file into a stringbuffer.
     * @param inFile
     * @return
     * @throws Exception
     */
    private String loadIntoBuffer(File inFile) throws Exception {
        // open the file
        FileInputStream in = new FileInputStream(inFile);

        // set the buffer size
        byte[] buffer = new byte[in.available()];

        // read the content
        in.read(buffer);

        // close the file
        in.close();

        return new String(buffer);
    }

    /**
     * Create a new instance of a master copy.
     *
     * @param inVMMaster
     * @param inVMCopyName
     * @return
     * @throws java.rmi.RemoteException
     */
    public boolean createCopyOfMaster(String inVMMaster, String inVMCopyName) throws RemoteException {
        String copyVMX;
        try {
            // first copy the master .vmx file
            copyVMX = VM_IMAGES_FOLDER + File.separator + inVMCopyName;
            if (!copyVMX.endsWith(".vmx"))
                copyVMX += ".vmx";

            if (copy(VM_MASTER_FOLDER + File.separator + inVMMaster, copyVMX)) {
                // read all of the .vmx file
                File fileVMX = new File(copyVMX);
                String buffer = loadIntoBuffer(fileVMX);
                String[] strLines = buffer.replace("\r", "").split("\n");

                // extract the name of the new vm
                String strNewName = fileVMX.getName().replace(".vmx", "");

                // open the vmx file to write the changes
                FileWriter out = new FileWriter(fileVMX);

                // parse the lines
                int iHDDIndex = 0;
                for (String line : strLines) {
                    if (line.startsWith("scsi") && line.contains(".fileName = ")) {
                        // get the old name
                        String strOld = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));

                        // in case there are more than one hdd append an index
                        String strNewHDD = strNewName + iHDDIndex;

                        // replace it
                        line = line.replace(strOld, strNewHDD + ".vmdk");

                        // copy the .vmdk files
                        File folder = new File(VM_MASTER_FOLDER);
                        strOld = strOld.replace(".vmdk", "");
                        File[] files = folder.listFiles();
                        for (File f : files) {
                            if ((f.getName().startsWith(strOld + "-f") && f.getName().endsWith(".vmdk"))) {
                                // copy the file
                                if (!copy(f.getPath(), VM_IMAGES_FOLDER + File.separator + f.getName().replace(strOld, strNewHDD)))
                                    return false;
                            } else if (f.getName().equals(strOld + ".vmdk")) {
                                // it's the .vmdk config file
                                // read it's content
                                String strBuffer = loadIntoBuffer(f);

                                // replace the old names
                                strBuffer = strBuffer.replace(strOld, strNewHDD);

                                // write it to the new destination
                                FileWriter o = new FileWriter(VM_IMAGES_FOLDER + File.separator + f.getName().replace(strOld, strNewHDD));
                                o.write(strBuffer);
                                o.close();
                            }
                        }

                        ++iHDDIndex;
                    }
                    else if (line.startsWith("displayName = ")) {
                        // replace the display name
                        line = "displayName = \"" + strNewName + "\"";
                    }
                    out.write(line + "\n");
                }

                // close the vmx file
                out.close();

                // set execution rights if were using linux
                if (!System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                    Process ps = Runtime.getRuntime().exec("chmod 755 " + fileVMX.getPath());
                    ps.waitFor();
                }
            }
            else return false;
        } catch (Exception e) {
            sfLog().error("VMWareServerManager createCopyOfMaster failed: " + e.getMessage());
            return false;
        }

        // register the file with the vmware server
        return registerVM(copyVMX);
    }

    /**
     * Copies a file.
     * @param from
     * @param to
     * @return
     */
    private boolean copy(String from, String to) throws IOException
    {
        sfLog().info("VMWareServerManager creating copy. From: " + from + " to: " + to);
        File fromFile = new File(from);
        File toFile = new File(to);

        if (fromFile.exists() && fromFile.isFile()) {
            // create the input stream
            FileInputStream in = new FileInputStream(fromFile);

            // create the output stream
            FileOutputStream out = new FileOutputStream(toFile);

            // set the buffer to 4mb
            byte[] buffer = new byte[4096];

            // copy
            int len;
            while ((len = in.read(buffer)) > 0)
                out.write(buffer, 0, len);

            in.close();
            out.close();

            return true;
        }
        else return false;
    }

    /**
     * Delete a instance of a master copy.
     *
     * @param inVMPath
     * @return
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
        if (file.exists() && file.isFile() && file.getName().endsWith(".vmx"))
            return file.delete();
        else
            return false;
    }

    /**
     * Starts a virtual machine. Has to be powered off or suspended.
     *
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean stopVM(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null)
            return tmp.shutDown();

        // an error occurred
        return false;
    }

    /**
     * Suspends a virtual machine. Has to be running.
     *
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean suspendVM(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null)
            return tmp.suspend();

        // an error occurred
        return false;
    }

    /**
     * Resets a virtual machine.
     *
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean resetVM(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null)
            return tmp.reset();

        // an error occurred
        return false;
    }

    /**
     * Gets the power state of a virtual machine.
     *
     * @param inVMPath The full path to the machine.
     * @return
     */
    public int getPowerState(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null)
            return tmp.getPowerState();

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

        for (VMWareImageModule mod : listImgModules)
            strResult += mod.getVMPath() + "\n";

        return strResult;
    }

    /**
     * Gets an existing machine module or attempts to create a new one.
     * @param inVMPath The path to the .vmx file.
     * @return Returns an VMWareImageModule or null if inVMPath isn't valid.
     */
    private VMWareImageModule getMachineModule(String inVMPath)
    {
        // parse the list of image modules
        for (VMWareImageModule imgMod : listImgModules)
            if (imgMod.getVMPath().equals(inVMPath))
                return imgMod;

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
     * @param inVMPath
     */
    private void removeMachineModule(String inVMPath) {
        // find the appropriate module
        for (VMWareImageModule mod : listImgModules)
            if (mod.getVMPath().equals(inVMPath))
            {
                listImgModules.remove(mod);
                break;
            }
    }

    /**
     * Shuts down the VMWare Server and all running machines as well.
     *
     * @return
     */
    public boolean shutdownVMWareServerService() throws RemoteException {
        // shutdown the vmware server service, which will automatically shut down all vms
        if (System.getProperty("os.name").toLowerCase().startsWith("windows"))
            try {
                Runtime.getRuntime().exec("net.exe stop VMWare");
            } catch (IOException e) {
                return false;
            }
        else
            try {
                Runtime.getRuntime().exec("/etc/init.d/vmware stop");
            } catch (IOException e) {
                return false;
            }

        return true;
    }

    /**
     * Starts the vmware server service.
     */
    public boolean startVMWareServerService() throws RemoteException
    {
        // start the vmware server service
        if (System.getProperty("os.name").toLowerCase().startsWith("windows"))
            try {
                Runtime.getRuntime().exec("net.exe start VMWare");
            } catch (IOException e) {
                return false;
            }
        else
            try {
                Runtime.getRuntime().exec("/etc/init.d/vmware start");
            } catch (IOException e) {
                return false;
            }

        return true;
    }

    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);

        // shut down every virtual machine manually to be indepentant of the vmserver service behaviour
        for (VMWareImageModule img : listImgModules)
            img.shutDown();     // TODO: error handling

        // shut down the vmware server service
        try {
            shutdownVMWareServerService();
        } catch (RemoteException e) {

        }
    }

    /**
     * Registes a virtual machine with the vmware server.
     * @param inVMPath The full path to the machine.
     * @return
     */
    public boolean registerVM(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null)
            return tmp.registerVM();

        // an error occurred
        return false;
    }
}
