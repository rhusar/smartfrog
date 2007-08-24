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
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.io.FilenameFilter;


// TODO: think of a better approach to error handling when getting an image module failed
public class VMWareServerManager extends PrimImpl implements VMWareServerManagerServices {

     /**
     * The list of images this manager controls.
     */
    private ArrayList<VMWareImageModule> listImgModules;

    /**
     * The folder where all vm images are stored which are under control of the daemon.
     */
    private static final String VM_IMAGES_FOLDER = "./VMImages";

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

    /**
     * Gets the tools state of a virtual machine.
     *
     * @param inVMPath The full path to the machine.
     * @return
     */
    public int getToolsState(String inVMPath) throws RemoteException {
        // get a machine module
        VMWareImageModule tmp = getMachineModule(inVMPath);

        // check if it worked
        if (tmp != null)
            return tmp.getToolsState();

        // an error occurred
        return VMWareImageModule.STATUS_ERROR;
    }

    /**
     * Gets the running virtual machines.
     *
     * @return
     */
    public String getRunningMachines() throws RemoteException {
        // execute the command
        try {
            return vmComm.execVMcmd("list");
        } catch (IOException e) {

        }
        return "";
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
        if (newMod != null)
            listImgModules.add(newMod);

        return newMod;
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
