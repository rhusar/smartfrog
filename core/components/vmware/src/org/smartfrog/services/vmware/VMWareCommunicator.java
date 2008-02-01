/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.services.vmware;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Pointer;

/**
 * This class manages the communication with the vmware server service.
 */
public class VMWareCommunicator {
    /**
     * Callback for getRunningVMs and getRegisteredVMs
     */
    private class ItemDiscovery implements VMWareVixLibrary.VixEventProc {
        /**
         * Contains the found items.
         */
        public ArrayList<String> listItems = new ArrayList<String>();

        /**
         * Reference to the vix library.
         */
        private VMWareVixLibrary vixLib = null;

        /**
         * Constructor.
         * @param inVixLib The vix library wrapper.
         */
        public ItemDiscovery(VMWareVixLibrary inVixLib) {
            this.vixLib = inVixLib;
        }

        /**
         * Procedures of this type are called when an event happens on a handle.
         *
         * @param handle
         * @param eventType
         * @param moreEventInfo
         * @param clientData
         */
        public void callback(int handle, int eventType, int moreEventInfo, Pointer clientData) {
            // has an item been found?
            if (eventType == VMWareVixLibrary.VixEventType.VIX_EVENTTYPE_FIND_ITEM) {
                // get the item
                Pointer pItem = null;
                long lErr = vixLib.Vix_GetProperties(   moreEventInfo,
                                                        VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_FOUND_ITEM_LOCATION,
                                                        pItem,
                                                        VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);

                // store the item
                if (lErr == VMWareVixLibrary.VixError.VIX_OK)
                    this.listItems.add(pItem.getString(0));

                vixLib.Vix_FreeBuffer(pItem);
            }
        }
    }

    /**
     * Username of the target machines user.
     */
    private String                          strUserName = null;

    /**
     * Password of the target machines user. TODO: Might be a security issue to store the password in plaintext in the memory.
     */
    private String                          strPassword = null;
    /**
     * Hostname to connect to.
     */
    private String                          strHostname = null;
    /**
     * Port to connect to.
     */
    private int                             iPort = 0;

    /**
     * The native library of the vix API.
     */
    VMWareVixLibrary                        vixLib = null;

    /**
     * The host handle for the VIX api.
     */
    private IntByReference                  iHostHandle = new IntByReference(VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE);

    /**
     * The list of image modules.
     */
    private ArrayList<VMWareImageModule>    listImageModule = new ArrayList<VMWareImageModule>();

    /**
     * Default constructor.
     * @throws Exception Loading the VIX library may result in exceptions.
     */
    public VMWareCommunicator() throws Exception {
        this.vixLib = VMWareVixLibrary.instance;
    }

    /**
     * Constructor.
     * @param inUser Username of the target machines user (which is running the vmware server).
     * @param inPass Password of the target machines user.
     * @param inHost Hostname of the target machine.
     * @param inPort Port to connect to on the target machine.
     * @throws Exception Loading the VIX library may result in exceptions.
     */
    public VMWareCommunicator(String inUser, String inPass, String inHost, int inPort) throws Exception {
        this();
        this.strUserName    = inUser;
        this.strPassword    = inPass;
        this.strHostname    = inHost;
        this.iPort          = inPort;
    }

    /**
     * Gets the list of virtual machine images.
     * @return
     */
    public ArrayList<VMWareImageModule> getImageModuleList() {
        return listImageModule;
    }

    /**
     * Converts an error code into an VIXException.
     * @param inErrorCode The error code.
     * @param inReconnect If true the connection to the vmware server will be re-established.
     * @throws VIXException
     */
    private void convertToException(long inErrorCode, boolean inReconnect) throws VIXException, SmartFrogException {
        // has there been an error at all?
        if (inErrorCode != (long)VMWareVixLibrary.VixError.VIX_OK) {
            if (inReconnect) {
                // Due to the fact that VixJob_Wait will block infinitely
                // after returning an error once we have to reconnect.
                this.reconnect();
            }

            // genereate the exception
            throw new VIXException(this.vixLib.Vix_GetErrorText(inErrorCode, null), inErrorCode);
        }
    }

    /**
     * Connects to the virtual machine service on the specified host.
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    private void connect() throws SmartFrogException {
        try {
            // connect if there isn't a valid host handle
            if (this.iHostHandle.getValue() == VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE) {
                // connect to the vmware server service
                int iJobHandle = vixLib.VixHost_Connect(VMWareVixLibrary.VIX_API_VERSION,
                                                        VMWareVixLibrary.VixServiceProvider.VIX_SERVICEPROVIDER_VMWARE_SERVER,
                                                        this.strHostname,
                                                        this.iPort,
                                                        this.strUserName,
                                                        this.strPassword,
                                                        0,
                                                        VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE,
                                                        null,
                                                        null);

                // wait for the job to finish
                long lErr = vixLib.VixJob_Wait(iJobHandle,
                                               VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_JOB_RESULT_HANDLE,
                                               this.iHostHandle,
                                               VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);

                // not needed anymore
                vixLib.Vix_ReleaseHandle(iJobHandle);

                // if there has been an error throw an exception
                convertToException(lErr, false);
            }
        } catch(VIXException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException("Error while connecting", e);
        }
    }

    /**
     * Disconnects and re-connects to the vmware server.
     * @throws SmartFrogException 
     */
    public void reconnect() throws SmartFrogException {
        this.disconnect();
        this.connect();
    }

    /**
     * Disconnects from the virtual machine server host.
     */
    public void disconnect() {
        if (this.iHostHandle.getValue() != VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE) {
            // release the host handles and delete their modules
            for (VMWareImageModule curImg : this.listImageModule) {
                if (curImg.getVMHandle().getValue() != VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE) {
                    // release handle
                    vixLib.Vix_ReleaseHandle(curImg.getVMHandle().getValue());
                    
                    curImg.getVMHandle().setValue(VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE);
                }
            }

            // disconnect
            vixLib.VixHost_Disconnect(this.iHostHandle.getValue());
            this.iHostHandle.setValue(VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE);
        }
    }

    /**
     * Creates a VMWareImageModule from an existing virtual machine image.
     * @param inPath The path to the virtual machine image. (to the .vmx file)
     * @return The newly created image module or null.
     */
    public VMWareImageModule createImageModule(String inPath) {
        // try to create the new image module
        VMWareImageModule newImg = VMWareImageModule.createImageModule(inPath, this);

        // if the creation has been successful add the module to the list
        if (newImg != null) {
            this.listImageModule.add(newImg);
        }

        return newImg;
    }

    /**
     * Gets an image module.
     * @param inPath The path to the image module.
     * @return Reference to the image module if existing or null.
     */
    public VMWareImageModule getImageModule(String inPath) {
        for (VMWareImageModule curImg : this.listImageModule) {
            if (curImg.getVMPath().equals(inPath)) {
                return curImg;
            }
        }

        // no module found
        return null;
    }

    /**
     * Deletes a VMWareImageModule without deleting the files from the filesystem.
     * @param inPath The path to the virtual machine image. (to the .vmx file)
     * @return True if the module has been found and removed, false otherwise.
     */
    public boolean deleteImageModule(String inPath) {
        for (VMWareImageModule curImg : this.listImageModule) {
            if (curImg.getVMPath().equals(inPath)) {
                this.listImageModule.remove(curImg);
                return true;
            }
        }

        return false;
    }

    /**
     * Deletes a virtual machine image from the filesystem.
     * @param inPath The path to the virtual machine image. (to the .vmx file)
     * @return True if the image has been found and deleted, false otherwise.
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public boolean deleteVirtualMachine(String inPath) throws SmartFrogException {
        // get the image module
        VMWareImageModule img = this.getImageModule(inPath);

        if (img != null) {
            // delete the files
            img.delete();

            // remove it from the list
            this.listImageModule.remove(img);

            return true;
        }

        return false;
    }

    /**
     * Creates a copy of a virtual machine image.
     * @param inSourceImage The path to the virtual machine image. (to the .vmx file)
     * @param inDestFolder The destination folder where the copy should be put into.
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public void copyVirtualMachine(String inSourceImage, String inDestFolder) throws SmartFrogException {
        try {
            // check for the existance of the target folder
            File destFolder = new File(inDestFolder);
            if (!destFolder.exists())
                if (!destFolder.mkdir())
                    throw new SmartFrogException("Failed to create \"" + inDestFolder + "\"");

            // get the files of the source
            File srcFile = new File(inSourceImage);
            File[] files = srcFile.getParentFile().listFiles();
            for (File curFile : files) {
                // copy the file
                File newFile = new File(destFolder.getAbsolutePath() + File.separator + curFile.getName());
                FileSystem.fCopy(curFile, newFile);

                // if it's a .vmx file set the creation of a new uuid
                if (curFile.getAbsolutePath().endsWith(".vmx")) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(newFile, true));
                    writer.write("uuid.action = \"create\"\n");
                    writer.close();

                    // set execution flag
                    newFile.setExecutable(true, false);
                }
            }
        } catch (IOException e) {
            throw new SmartFrogException("Error while copying VM", e);
        }
    }

    /**
     * Acquires a handle for the virtual machine if not already acquired.
     * @param inImg The virtual machine image module.
     * @throws SmartFrogException
     */
    public void acquireVMHandle(VMWareImageModule inImg) throws SmartFrogException {
        try {
            // ensure the connection
            connect();

            // is there already a handle?
            if (inImg.getVMHandle().getValue() == VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE){
                // open the vm image
                int iJobHandle = vixLib.VixVM_Open( this.iHostHandle.getValue(),
                                                    inImg.getVMPath(),
                                                    null,
                                                    null);

                // wait for the job to complete
                long lErr = vixLib.VixJob_Wait(iJobHandle,
                                                VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_JOB_RESULT_HANDLE,
                                                inImg.getVMHandle(),
                                                VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);

                // release the job handle
                vixLib.Vix_ReleaseHandle(iJobHandle);

                convertToException(lErr, true);
            }
        } catch(VIXException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while acquiring VM handle", e);
        }
    }

    /**
     * Gets a specific state of a virtual machine.
     * @param inImg The virtual machine module.
     * @param inVixPropertyID Must be of type VMWareVixLibrary.VixPropertyID
     * @return
     */
    public int getState(VMWareImageModule inImg, int inVixPropertyID) throws SmartFrogException {
        try {
            // ensure there is a valid connection and vm handle
            this.acquireVMHandle(inImg);

            // get the power state property
            IntByReference iState = new IntByReference();
            long lErr = vixLib.Vix_GetProperties(   inImg.getVMHandle().getValue(),
                                                    inVixPropertyID,
                                                    iState,
                                                    VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);

            convertToException(lErr, true);

            // return the power state
            return iState.getValue();
        } catch(VIXException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while getting state", e);
        }
    }

    /**
     * Gets the power state of a virtual machine.
     * @param inImg The virtual machine module.
     * @return The power state.
     */
    public int getPowerState(VMWareImageModule inImg) throws SmartFrogException {
        return this.getState(inImg, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_VM_POWER_STATE);
    }

    /**
     * Gets the tools state of a virtual machine.
     * @param inImg The virtual machine module.
     * @return The tools state.
     * @throws SmartFrogException
     */
    public int getToolsState(VMWareImageModule inImg) throws SmartFrogException {
        return this.getState(inImg, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_VM_TOOLS_STATE);
    }

    /**
     * Gets the items of the specified type.
     * @param inVixFindItemType Has to be of type VMWareVixLibrary.VixFindItemType
     * @return The items.
     * @throws SmartFrogException
     */
    private String[] getItems(int inVixFindItemType) throws SmartFrogException {
        try {
            // ensure that there is a valid connection
            this.connect();

            // get the items
            ItemDiscovery proc = new ItemDiscovery(this.vixLib);
            int iJobHandle = this.vixLib.VixHost_FindItems( this.iHostHandle.getValue(),
                                                            inVixFindItemType,
                                                            VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE,
                                                            -1,
                                                            proc,
                                                            null);

            // wait for the job to complete
            long lErr = this.vixLib.VixJob_Wait(iJobHandle,
                                                VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);

            // release the job handle
            this.vixLib.Vix_ReleaseHandle(iJobHandle);

            convertToException(lErr, true);

            // return the vm pathes
            return (String[])proc.listItems.toArray();
        } catch(VIXException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException("Error while getting items", e);
        }
    }

    /**
     * Gets the virtual machines that are currently running on the vmware server.
     * @return An array of vm names.
     * @throws SmartFrogException
     */
    public String[] getRunningVMs() throws SmartFrogException {
        return this.getItems(VMWareVixLibrary.VixFindItemType.VIX_FIND_RUNNING_VMS);
    }

    /**
     * Gets the virtual machines that are registered on the vmware server.
     * @return An array of vm names.
     * @throws SmartFrogException
     */
    public String[] getRegisteredVMs() throws SmartFrogException {
        return this.getItems(VMWareVixLibrary.VixFindItemType.VIX_FIND_REGISTERED_VMS);
    }

    /**
     * Registers a virtual machine on the vmware server.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException
     */
    public void registerVM (VMWareImageModule inImg) throws SmartFrogException {
        try {
            // ensure that a connection is established
            this.connect();

            // register the virtual machine
            System.err.println(String.format("%d, %s", this.iHostHandle.getValue(), inImg.getVMPath()));
            int iJobHandle = this.vixLib.VixHost_RegisterVM(this.iHostHandle.getValue(),
                                                            inImg.getVMPath(),
                                                            null,
                                                            null);

            // wait for the job to complete
            System.err.println("Before wait.");
            long lErr = this.vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            System.err.println("Error: " + lErr);

            // release the job handle
            this.vixLib.Vix_ReleaseHandle(iJobHandle);

            convertToException(lErr, true);

            System.err.println("After Exception.");
        } catch(VIXException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while registering VM", e);
        }
    }

    /**
     * Unregisters a virtual machine on the vmware server.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException
     */
    public void unregisterVM(VMWareImageModule inImg) throws SmartFrogException {
        try {
            // ensure that a connection is established
            this.connect();

            // unregister the virtual machine
            int iJobHandle = this.vixLib.VixHost_UnregisterVM(  this.iHostHandle.getValue(),
                                                                inImg.getVMPath(),
                                                                null,
                                                                null);

            // wait for the job to complete
            long lErr = this.vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);

            // release the job handle
            this.vixLib.Vix_ReleaseHandle(iJobHandle);

            convertToException(lErr, true);
        } catch(VIXException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while unregistering VM", e);
        }
    }

    /**
     * Starts a virtual machine.
     * @param inImg The virtual machine module.
     * @param inVixVMPowerOpOptions Must be of type VMWareVixLibrary.VixVMPowerOpOptions
     * @throws SmartFrogException
     */
    public void startVM(VMWareImageModule inImg, int inVixVMPowerOpOptions) throws SmartFrogException {
        try {
            // ensure that a connection is established
            this.acquireVMHandle(inImg);

            // start the virtual machine
            int iJobHandle = this.vixLib.VixVM_PowerOn( inImg.getVMHandle().getValue(),
                                                        inVixVMPowerOpOptions,
                                                        VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE,
                                                        null,
                                                        null);

            // wait for the job to complete
            long lErr = this.vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);

            // release the job handle
            this.vixLib.Vix_ReleaseHandle(iJobHandle);

            convertToException(lErr, true);
        } catch(VIXException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while starting VM", e);
        }
    }

    /**
     * Starts a virtual machine with normal power options.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException
     */
    public void startVM(VMWareImageModule inImg) throws SmartFrogException {
        this.startVM(inImg, VMWareVixLibrary.VixVMPowerOpOptions.VIX_VMPOWEROP_NORMAL);
    }

    /**
     * Stops a virtual machine.
     * @param inImg The virtual machine module.
     * @param inVixVMPowerOpOptions Must be of type VMWareVixLibrary.VixVMPowerOpOptions
     * @return True if the machine could be stopped, false otherwise.
     * @throws SmartFrogException
     */
    public void stopVM(VMWareImageModule inImg, int inVixVMPowerOpOptions) throws SmartFrogException {
        try {
            // ensure that a connection is established
            this.acquireVMHandle(inImg);

            // stop the virtual machine
            int iJobHandle = this.vixLib.VixVM_PowerOff( inImg.getVMHandle().getValue(),
                                                        inVixVMPowerOpOptions,
                                                        null,
                                                        null);

            // wait for the job to complete
            long lErr = this.vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);

            // release the job handle
            this.vixLib.Vix_ReleaseHandle(iJobHandle);

            convertToException(lErr, true);
        } catch(VIXException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while stopping VM", e);
        }
    }

    /**
     * Stops a virtual machine with normal power options.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException
     */
    public void stopVM(VMWareImageModule inImg) throws SmartFrogException {
        this.stopVM(inImg, VMWareVixLibrary.VixVMPowerOpOptions.VIX_VMPOWEROP_NORMAL);
    }

    /**
     * Suspend a virtual machine.
     * @param inImg The virtual machine module.
     * @param inVixVMPowerOpOptions Must be of type VMWareVixLibrary.VixVMPowerOpOptions
     * @throws SmartFrogException
     */
    public void suspendVM(VMWareImageModule inImg, int inVixVMPowerOpOptions) throws SmartFrogException {
        try {
            // ensure that a connection is established
            this.acquireVMHandle(inImg);

            // resumes the virtual machine
            int iJobHandle = this.vixLib.VixVM_Suspend( inImg.getVMHandle().getValue(),
                                                        inVixVMPowerOpOptions,
                                                        null,
                                                        null);

            // wait for the job to complete
            long lErr = this.vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);

            // release the job handle
            this.vixLib.Vix_ReleaseHandle(iJobHandle);

            convertToException(lErr, true);
        } catch(VIXException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while suspending VM", e);
        }
    }

    /**
     * Suspends a virtual machine with normal power options.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException
     */
    public void suspendVM(VMWareImageModule inImg) throws SmartFrogException {
        this.suspendVM(inImg, VMWareVixLibrary.VixVMPowerOpOptions.VIX_VMPOWEROP_NORMAL);
    }

    /**
     * Resets a virtual machine.
     * @param inImg The virtual machine module.
     * @param inVixVMPowerOpOptions Must be of type VMWareVixLibrary.VixVMPowerOpOptions
     * @throws SmartFrogException
     */
    public void resetVM(VMWareImageModule inImg, int inVixVMPowerOpOptions) throws SmartFrogException {
        try {
            // ensure that a connection is established
            this.acquireVMHandle(inImg);

            // resets the virtual machine
            int iJobHandle = this.vixLib.VixVM_Reset(   inImg.getVMHandle().getValue(),
                                                        inVixVMPowerOpOptions,
                                                        null,
                                                        null);

            // wait for the job to complete
            long lErr = this.vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);

            // release the job handle
            this.vixLib.Vix_ReleaseHandle(iJobHandle);

            convertToException(lErr, true);
        } catch(VIXException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        }catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while resetting VM", e);
        }
    }

    /**
     * Resets a virtual machine with normal power options.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException
     */
    public void resetVM(VMWareImageModule inImg) throws SmartFrogException {
        this.resetVM(inImg, VMWareVixLibrary.VixVMPowerOpOptions.VIX_VMPOWEROP_NORMAL);
    }
}
