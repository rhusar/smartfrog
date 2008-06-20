/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class manages the communication with the vmware server service.
 */
public class VMWareCommunicator {
    private static final int NULL_HANDLE = VMWareVixLibrary.VixHandleType.VIX_HANDLETYPE_NONE;
    private static final Log log= LogFactory.getLog(VMWareCommunicator.class);

    /**
     * Callback for getRunningVMs and getRegisteredVMs
     */
    private static class ItemDiscovery implements VMWareVixLibrary.VixEventProc {
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
        private ItemDiscovery(VMWareVixLibrary inVixLib) {
            vixLib = inVixLib;
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
                if (lErr == VMWareVixLibrary.VixError.VIX_OK) {
                    listItems.add(pItem.getString(0));
                }

                vixLib.Vix_FreeBuffer(pItem);
            }
        }
    }

    /**
     * Username of the target machines user.
     */
    private String strUserName = null;

    /**
     * Password of the target machines user. TODO: Might be a security issue to store the password in plaintext in the memory.
     */
    private String strPassword = null;
    /**
     * Hostname to connect to.
     */
    private String strHostname = null;
    /**
     * Port to connect to.
     */
    private int iPort = 0;

    /**
     * The native library of the vix API.
     */
    private VMWareVixLibrary vixLib = null;

    /**
     * The host handle for the VIX api.
     */
    private IntByReference iHostHandle = new IntByReference(VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE);

    /**
     * The list of image modules.
     */
    private ArrayList<VMWareImageModule> listImageModule = new ArrayList<VMWareImageModule>();

    /**
     * Converts a powerstate into a readable string.
     * @param inState The powerstate of a virtual machine.
     * @return The readable string.
     */
    public String convertPowerState(int inState) {
        String strResponse = "";

        // the power state is a bitmask
        int iTmp = inState & 0x000F;
        switch (iTmp) {
            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERED_OFF:
                strResponse += "Powered off. ";
                break;
            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERED_ON:
                strResponse += "Powered on. ";
                break;
            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERING_OFF:
                strResponse += "Powering off. ";
                break;
            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERING_ON:
                strResponse += "Powering on. ";
                break;
            default:
                break;
        }

        iTmp = inState & 0x00F0;
        switch (iTmp) {
            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_RESETTING:
                strResponse += "Resetting. ";
                break;
            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_SUSPENDED:
                strResponse += "Suspended. ";
                break;
            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_SUSPENDING:
                strResponse += "Suspending. ";
                break;
            case VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_TOOLS_RUNNING:
                strResponse += "Tools running. ";
                break;
            default:
                break;
        }

        if ((inState & 0x0F00) == VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_BLOCKED_ON_MSG) {
            strResponse += "Blocked on message. ";
        }

        if (strResponse.length() == 0)
            strResponse = "Could not retrieve power state.";

        return strResponse;
    }

    /**
     * Converts a toolsstate into a readable string.
     * @param inState The toolsstate of a virtual machine.
     * @return The readable string.
     */
    public String convertToolsState(int inState) {
        switch (inState)
        {
            case VMWareVixLibrary.VixToolsState.VIX_TOOLSSTATE_NOT_INSTALLED:
                return "Tools not installed.";
            case VMWareVixLibrary.VixToolsState.VIX_TOOLSSTATE_RUNNING:
                return "Tools running.";
            case VMWareVixLibrary.VixToolsState.VIX_TOOLSSTATE_UNKNOWN:
                return "Tools state unknown.";
            default:
                return "Unknown tools state: " + inState;
        }
    }

    /**
     * Default constructor.
     * @param inVixLibraryPath Path where the vix library can be found.
     * @param inVixLibraryName Name of the vix library.
     * @throws Exception Loading the VIX library may result in exceptions.
     */
    public VMWareCommunicator(String inVixLibraryPath, String inVixLibraryName) throws Exception {
        // set the system property "jna.library.path" to the given path
        System.setProperty("jna.library.path", inVixLibraryPath);
        // load the library
        vixLib = (VMWareVixLibrary)Native.loadLibrary(inVixLibraryName, VMWareVixLibrary.class);
    }

    /**
     * Constructor.
     * @param inVixLibraryPath Path where the vix library can be found.
     * @param inVixLibraryName Name of the vix library.
     * @param inUser Username of the target machines user (which is running the vmware server).
     * @param inPass Password of the target machines user.
     * @param inHost Hostname of the target machine.
     * @param inPort Port to connect to on the target machine.
     * @throws Exception Loading the VIX library may result in exceptions.
     */
    public VMWareCommunicator(String inVixLibraryPath, String inVixLibraryName, String inUser, String inPass, String inHost, int inPort) throws Exception {
        this(inVixLibraryPath, inVixLibraryName);
        strUserName = inUser;
        strPassword = inPass;
        strHostname = inHost;
        iPort = inPort;
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
     * @throws VIXException the inner exception.
     * @throws SmartFrogException problems connecting
     */
    private void convertToException(long inErrorCode, boolean inReconnect) throws SmartFrogException {
        // has there been an error at all?
        if (inErrorCode != (long)VMWareVixLibrary.VixError.VIX_OK) {
            if (inReconnect) {
                // Due to the fact that VixJob_Wait will block infinitely
                // after returning an error once we have to reconnect.
                try {
                    reconnect();
                } catch (SmartFrogException e) {
                    log.error(e);
                }
            }

            // generate the exception
            throw new VIXException(vixLib.Vix_GetErrorText(inErrorCode, null), inErrorCode);
        }
    }

    /**
     * Connects to the virtual machine service on the specified host.
     * @throws SmartFrogException  problems connecting
     */
    private void connect() throws SmartFrogException {
        try {
            // connect if there isn't a valid host handle
            if (iHostHandle.getValue() == VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE) {
                // connect to the vmware server service
                int iJobHandle = vixLib.VixHost_Connect(VMWareVixLibrary.VIX_API_VERSION,
                                                        VMWareVixLibrary.VixServiceProvider.VIX_SERVICEPROVIDER_VMWARE_SERVER,
                                                        strHostname,
                                                        iPort,
                                                        strUserName,
                                                        strPassword,
                                                        0,
                                                        VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE,
                                                        null,
                                                        null);

                // wait for the job to finish
                long lErr = vixLib.VixJob_Wait(iJobHandle,
                                               VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_JOB_RESULT_HANDLE,
                                               iHostHandle,
                                               VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);

                // not needed anymore
                vixLib.Vix_ReleaseHandle(iJobHandle);

                // if there has been an error throw an exception
                convertToException(lErr, false);
            }
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException("Error while connecting", e);
        }
    }

    /**
     * Disconnects and re-connects to the vmware server.
     * @throws SmartFrogException if we cannot reconnect
     */
    public void reconnect() throws SmartFrogException {
        disconnect();
        connect();
    }

    /**
     * Disconnects from the virtual machine server host.
     */
    public void disconnect() {
        if (iHostHandle.getValue() != VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE) {
            // release the host handles and delete their modules
            for (VMWareImageModule curImg : listImageModule) {
                if (curImg.getVMHandle().getValue() != VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE) {
                    // release handle
                    vixLib.Vix_ReleaseHandle(curImg.getVMHandle().getValue());
                    
                    curImg.getVMHandle().setValue(VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE);
                }
            }

            // disconnect
            vixLib.VixHost_Disconnect(iHostHandle.getValue());
            iHostHandle.setValue(VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE);
        }
    }

    /**
     * Creates a VMWareImageModule from an existing virtual machine image.
     * @param inPath The path to the virtual machine image. (to the .vmx file)
     * @return The newly created image module.
     * @throws FileNotFoundException if there is no file
     */
    public VMWareImageModule createImageModule(String inPath) throws FileNotFoundException {
        // try to create the new image module
        VMWareImageModule newImg = VMWareImageModule.createImageModule(inPath, this);

        if (newImg != null) {
            // if the creation has been successful add the module to the list
            listImageModule.add(newImg);
        }

        return newImg;
    }

    /**
     * Gets an image module.
     * @param inPath The path to the image module.
     * @return Reference to the image module if existing or null.
     */
    public VMWareImageModule getImageModule(String inPath) {
        for (VMWareImageModule curImg : listImageModule) {
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
        for (VMWareImageModule curImg : listImageModule) {
            if (curImg.getVMPath().equals(inPath)) {
                listImageModule.remove(curImg);
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes a virtual machine image from the filesystem.
     * @param inPath The path to the virtual machine image. (to the .vmx file)
     * @return True if the image has been found and deleted, false otherwise.
     * @throws SmartFrogException  Problems with the VM
     */
    public boolean deleteVirtualMachine(String inPath) throws SmartFrogException {
        // get the image module
        VMWareImageModule img = getImageModule(inPath);
        if (img != null) {
            // delete the files
            img.delete();
            // remove it from the list
            listImageModule.remove(img);
            return true;
        }
        return false;
    }

    /**
     * Creates a copy of a virtual machine image.
     * @param inSourceImage The path to the virtual machine image. (to the .vmx file)
     * @param inDestFolder The destination folder where the copy should be put into.
     * @throws SmartFrogException  Problems with the VM
     */
    public void copyVirtualMachine(String inSourceImage, String inDestFolder) throws SmartFrogException {
        try {
            // check for the existance of the target folder
            File destFolder = new File(inDestFolder);
            if (!destFolder.exists()) {
                if (!destFolder.mkdir()) {
                    throw new SmartFrogException("Failed to create \"" + inDestFolder + "\"");
                }
            }
            // get the files of the source
            File srcFile = new File(inSourceImage);
            if (srcFile.exists()) {
                File[] files = srcFile.getParentFile().listFiles();
                for (File curFile : files) {
                    // copy the file
                    File newFile = new File(destFolder.getAbsolutePath() + File.separator + curFile.getName());
                    FileSystem.fCopy(curFile, newFile);

                    // if it's a .vmx file set the creation of a new uuid
                    if (curFile.getAbsolutePath().endsWith(".vmx")) {
                        BufferedWriter writer;
                        writer = new BufferedWriter(new FileWriter(newFile, true));
                        writer.write("uuid.action = \"create\"\n");
                        writer.close();

                        // set execution flag
                        newFile.setExecutable(true, false);
                    }
                }
            }
            else {
                throw new SmartFrogException("Source image does not exist: " + inSourceImage);
            }
        } catch (IOException e) {
            // clean up files that have already been written
            File destFolder = new File(inDestFolder);
            if (destFolder.exists()) {
                for (File file : destFolder.listFiles())
                    file.delete();
                destFolder.delete();
            }
            
            throw new SmartFrogException("Error while copying VM", e);
        }
    }

    /**
     * Acquires a handle for the virtual machine if not already acquired.
     * @param inImg The virtual machine image module.
     * @throws SmartFrogException Problems with the VM
     */
    public void acquireVMHandle(VMWareImageModule inImg) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure the connection
            connect();

            // is there already a handle?
            if (inImg.getVMHandle().getValue() == VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE){
                // open the vm image
                iJobHandle = vixLib.VixVM_Open( iHostHandle.getValue(),
                                                    inImg.getVMPath(),
                                                    null,
                                                    null);
                // wait for the job to complete
                long lErr = vixLib.VixJob_Wait(iJobHandle,
                                                VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_JOB_RESULT_HANDLE,
                                                inImg.getVMHandle(),
                                                VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
                convertToException(lErr, true);
            }
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while acquiring VM handle", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Gets a single porperty of a virtual machine.
     * @param inImg The virtual machine module.
     * @param inVixPropertyID Must be of type VMWareVixLibrary.VixPropertyID
     * @return the value
     * @throws SmartFrogException Problems with the VM
     */
    public int getProperty(VMWareImageModule inImg, int inVixPropertyID) throws SmartFrogException {
        try {
            // ensure there is a valid connection and vm handle
            acquireVMHandle(inImg);
            // get the power state property
            IntByReference iState = new IntByReference();
            long lErr = vixLib.Vix_GetProperties(   inImg.getVMHandle().getValue(),
                                                    inVixPropertyID,
                                                    iState,
                                                    VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            convertToException(lErr, true);
            // return the power state
            return iState.getValue();
        } catch(SmartFrogException e) {
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
     * @throws SmartFrogException Problems with the VM
     */
    public int getPowerState(VMWareImageModule inImg) throws SmartFrogException {
        return getProperty(inImg, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_VM_POWER_STATE);
    }

    /**
     * Gets the tools state of a virtual machine.
     * @param inImg The virtual machine module.
     * @return The tools state.
     * @throws SmartFrogException Problems with the VM
     */
    public int getToolsState(VMWareImageModule inImg) throws SmartFrogException {
        return getProperty(inImg, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_VM_TOOLS_STATE);
    }

    /**
     * Gets the items of the specified type.
     * @param inVixFindItemType Has to be of type VMWareVixLibrary.VixFindItemType
     * @return The items.
     * @throws SmartFrogException Problems with the VM
     */
    private String[] getItems(int inVixFindItemType) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that there is a valid connection
            connect();

            // get the items
            ItemDiscovery proc = new ItemDiscovery(vixLib);
            iJobHandle = vixLib.VixHost_FindItems( iHostHandle.getValue(),
                                                            inVixFindItemType,
                                                            VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE,
                                                            -1,
                                                            proc,
                                                            null);

            // wait for the job to complete
            long lErr = vixLib.VixJob_Wait(iJobHandle,
                                                VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            convertToException(lErr, true);

            // return the vm pathes
            return (String[])proc.listItems.toArray();
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException("Error while getting items", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Gets the virtual machines that are currently running on the vmware server.
     * @return An array of vm names.
     * @throws SmartFrogException connectivity problems
     */
    public String[] getRunningVMs() throws SmartFrogException {
        return getItems(VMWareVixLibrary.VixFindItemType.VIX_FIND_RUNNING_VMS);
    }

    /**
     * Gets the virtual machines that are registered on the vmware server.
     * @return An array of vm names.
     * @throws SmartFrogException connectivity problems
     */
    public String[] getRegisteredVMs() throws SmartFrogException {
        return getItems(VMWareVixLibrary.VixFindItemType.VIX_FIND_REGISTERED_VMS);
    }

    /**
     * Registers a virtual machine on the vmware server.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException Problems with the VM
     */
    public void registerVM (VMWareImageModule inImg) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            connect();

            // register the virtual machine
            iJobHandle = vixLib.VixHost_RegisterVM(iHostHandle.getValue(),
                                                            inImg.getVMPath(),
                                                            null,
                                                            null);
            // wait for the job to complete
            long lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            releaseHandle(iJobHandle);
            convertToException(lErr, true);
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while registering VM", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Unregisters a virtual machine on the vmware server.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException Problems with the VM/Operation
     */
    public void unregisterVM(VMWareImageModule inImg) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            connect();

            // unregister the virtual machine
            iJobHandle = vixLib.VixHost_UnregisterVM(  iHostHandle.getValue(),
                                                                inImg.getVMPath(),
                                                                null,
                                                                null);

            // wait for the job to complete
            long lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            convertToException(lErr, true);
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while unregistering VM", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Starts a virtual machine.
     * @param inImg The virtual machine module.
     * @param inVixVMPowerOpOptions Must be of type VMWareVixLibrary.VixVMPowerOpOptions
     * @throws SmartFrogException Problems with the VM/Operation
     */
    public void startVM(VMWareImageModule inImg, int inVixVMPowerOpOptions) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            acquireVMHandle(inImg);

            // start the virtual machine
            iJobHandle = vixLib.VixVM_PowerOn( inImg.getVMHandle().getValue(),
                                                        inVixVMPowerOpOptions,
                                                        VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE,
                                                        null,
                                                        null);

            // wait for the job to complete
            long lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            convertToException(lErr, true);
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while starting VM", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Starts a virtual machine with normal power options.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException failure to start the VM
     */
    public void startVM(VMWareImageModule inImg) throws SmartFrogException {
        startVM(inImg, VMWareVixLibrary.VixVMPowerOpOptions.VIX_VMPOWEROP_NORMAL);
    }

    /**
     * Stops a virtual machine.
     * @param inImg The virtual machine module.
     * @param inVixVMPowerOpOptions Must be of type VMWareVixLibrary.VixVMPowerOpOptions
     * @throws SmartFrogException Problems with the VM/Operation
     */
    public void stopVM(VMWareImageModule inImg, int inVixVMPowerOpOptions) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            acquireVMHandle(inImg);

            // stop the virtual machine
            iJobHandle = vixLib.VixVM_PowerOff( inImg.getVMHandle().getValue(),
                                                        inVixVMPowerOpOptions,
                                                        null,
                                                        null);

            // wait for the job to complete
            long lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            convertToException(lErr, true);
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while stopping VM", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Stops a virtual machine with normal power options.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException  Problems with the VM/Operation
     */
    public void stopVM(VMWareImageModule inImg) throws SmartFrogException {
        stopVM(inImg, VMWareVixLibrary.VixVMPowerOpOptions.VIX_VMPOWEROP_NORMAL);
    }

    /**
     * Suspend a virtual machine.
     * @param inImg The virtual machine module.
     * @param inVixVMPowerOpOptions Must be of type VMWareVixLibrary.VixVMPowerOpOptions
     * @throws SmartFrogException  Problems with the VM/Operation
     */
    public void suspendVM(VMWareImageModule inImg, int inVixVMPowerOpOptions) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            acquireVMHandle(inImg);

            // resumes the virtual machine
            iJobHandle = vixLib.VixVM_Suspend( inImg.getVMHandle().getValue(),
                                                        inVixVMPowerOpOptions,
                                                        null,
                                                        null);

            // wait for the job to complete
            long lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            convertToException(lErr, true);
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while suspending VM", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Suspends a virtual machine with normal power options.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException  Problems with the VM/Operation
     */
    public void suspendVM(VMWareImageModule inImg) throws SmartFrogException {
        suspendVM(inImg, VMWareVixLibrary.VixVMPowerOpOptions.VIX_VMPOWEROP_NORMAL);
    }

    /**
     * Resets a virtual machine.
     * @param inImg The virtual machine module.
     * @param inVixVMPowerOpOptions Must be of type VMWareVixLibrary.VixVMPowerOpOptions
     * @throws SmartFrogException  Problems with the VM/Operation
     */
    public void resetVM(VMWareImageModule inImg, int inVixVMPowerOpOptions) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            acquireVMHandle(inImg);

            // resets the virtual machine
            iJobHandle = vixLib.VixVM_Reset(   inImg.getVMHandle().getValue(),
                                                        inVixVMPowerOpOptions,
                                                        null,
                                                        null);

            // wait for the job to complete
            long lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            convertToException(lErr, true);
        } catch (SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while resetting VM", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Resets a virtual machine with normal power options.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException Problems with the VM/Operation
     */
    public void resetVM(VMWareImageModule inImg) throws SmartFrogException {
        resetVM(inImg, VMWareVixLibrary.VixVMPowerOpOptions.VIX_VMPOWEROP_NORMAL);
    }

    /**
     * Establishes the user credentials for the in-guest-OS operations.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException Problems with the VM/Operation
     */
    private void loginInGuestOS(VMWareImageModule inImg) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            acquireVMHandle(inImg);


            // logs into the virtual machine
            log.info(String.format("logging into guest os as: %s, using password: %s",inImg.getGuestOSUser(), inImg.getGuestOSPasswd()));
            iJobHandle = vixLib.VixVM_LoginInGuest(inImg.getVMHandle().getValue(),
                                                            inImg.getGuestOSUser(),
                                                            inImg.getGuestOSPasswd(),
                                                            0,
                                                            null,
                                                            null);

            // wait for the job to complete
            long lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            releaseHandle(iJobHandle);
            convertToException(lErr, true);
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while creating user credentials for VM", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Removes the user credentials previously created by <code>loginInGuestOS()</code>.
     * @param inImg The virtual machine module.
     * @throws SmartFrogException Problems with the VM/Operation
     */
    private void logoutFromGuestOS(VMWareImageModule inImg) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            acquireVMHandle(inImg);

            // resets the virtual machine
            iJobHandle = vixLib.VixVM_LogoutFromGuest( inImg.getVMHandle().getValue(),
                                                                null,
                                                                null);

            // wait for the job to complete
            long lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            convertToException(lErr, true);
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while removing user credentials for VM", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     *
     * @param inImg image to work with
     * @param inSourceFile source file
     * @param inTargetFile destination file
     * @throws SmartFrogException Problems with the VM/Operation
     */
    public void copyFileFromHostToGuestOS (VMWareImageModule inImg, String inSourceFile, String inTargetFile) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that user credentials are established
            loginInGuestOS(inImg);

            // resets the virtual machine
            iJobHandle = vixLib.VixVM_CopyFileFromHostToGuest( inImg.getVMHandle().getValue(),
                                                                        inSourceFile,
                                                                        inTargetFile,
                                                                        0,
                                                                        VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE,
                                                                        null,
                                                                        null);

            // wait for the job to complete
            long lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            convertToException(lErr, true);

            // delete the user credentials again
            // seems not to be existant in the vix library delivered with vmware server
            // his.logoutFromGuestOS(inImg);
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while copying file from host to guest OS", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Executes a program within the guest os.
     * @param inImg image to work with
     * @param inCommand The (full!) path to the program.
     * @param inParameters The parameters for the program. (No relative pathes allowed here, too.)
     * @param inNoWait Wait for the program to exit?
     * @throws SmartFrogException
     */
    public void executeInGuestOS(VMWareImageModule inImg, String inCommand, String inParameters, boolean inNoWait) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that user credentials are established
            loginInGuestOS(inImg);

            int iOption = (inNoWait ? VMWareVixLibrary.VixRunProgramOptions.VIX_RUNPROGRAM_RETURN_IMMEDIATELY : 0);

            iJobHandle = vixLib.VixVM_RunProgramInGuest( inImg.getVMHandle().getValue(),
                                                                inCommand,
                                                                inParameters,
                                                                iOption,
                                                                VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE,
                                                                null,
                                                                null);

            // wait for the job to complete
            long lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            convertToException(lErr, true);

            // delete the user credentials again
            // seems not to be existant in the vix library delivered with vmware server
            // his.logoutFromGuestOS(inImg);
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while executing program in guest OS", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Release any non null handle
     * @param jobHandle handle
     * @return the null handle value, for putting in to variables
     */
    private int releaseHandle(int jobHandle) {
        // release the job handle
        if(jobHandle!= NULL_HANDLE) {
            vixLib.Vix_ReleaseHandle(jobHandle);
        }
        return NULL_HANDLE;
    }

    /**
     * Takes a snapshot of a virtual machine.
     * @param inImg The VM image.
     * @param inDesc A descriptino for the snapshot.
     * @param inName The name for the snapshot.
     * @param inIncludeMemory Also include the whole memory?
     */
    public void takeSnapshot(VMWareImageModule inImg, String inName, String inDesc, boolean inIncludeMemory) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            acquireVMHandle(inImg);

            // take the snapshot
            iJobHandle = vixLib.VixVM_CreateSnapshot( inImg.getVMHandle().getValue(),
                                                        inName,
                                                        inDesc,
                                                        (inIncludeMemory ? VMWareVixLibrary.VixCreateSnapshotOptions.VIX_SNAPSHOT_INCLUDE_MEMORY : 0),
                                                        VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE,
                                                        null,
                                                        null);

            // wait for the job to complete
            long lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            convertToException(lErr, true);
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while taking snapshot of VM", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Deletes a named snapshot of a virtual machine.
     * @param inImg The virtual machine image.
     * @param inName The name of the snapshot.
     * @param inRemoveChildren Remove the children of this snashot, too?
     * @throws SmartFrogException
     */
    public void deleteSnapshot(VMWareImageModule inImg, String inName, boolean inRemoveChildren) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            acquireVMHandle(inImg);

            // get the snapshot handle
            IntByReference snapHandle = new IntByReference(VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE);
            long lErr = vixLib.VixVM_GetNamedSnapshot(inImg.getVMHandle().getValue(),
                                                        inName,
                                                        snapHandle);

            convertToException(lErr, true);

            if (snapHandle.getValue() != VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE) {
                iJobHandle = vixLib.VixVM_RemoveSnapshot(inImg.getVMHandle().getValue(),
                                                            snapHandle.getValue(),
                                                            (inRemoveChildren ? VMWareVixLibrary.VixRemoveSnapshotOptions.VIX_SNAPSHOT_REMOVE_CHILDREN : 0),
                                                            null,
                                                            null);

                // wait for the job to complete
                lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
                convertToException(lErr, true);
            }
            else throw new SmartFrogException("Failed to delete named snapshot: " + inName);
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while deleting named snapshot", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Deletes the current snapshot of a virtual machine.
     * @param inImg The virtual machine image.
     * @param inRemoveChildren Remove the children of this snashot, too?
     * @throws SmartFrogException
     */
    public void deleteSnapshot(VMWareImageModule inImg, boolean inRemoveChildren) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            acquireVMHandle(inImg);

            // get the snapshot handle
            IntByReference snapHandle = new IntByReference(VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE);
            long lErr = vixLib.VixVM_GetCurrentSnapshot(inImg.getVMHandle().getValue(),
                                                        snapHandle);

            convertToException(lErr, true);

            if (snapHandle.getValue() != VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE) {
                iJobHandle = vixLib.VixVM_RemoveSnapshot(inImg.getVMHandle().getValue(),
                                                            snapHandle.getValue(),
                                                            (inRemoveChildren ? VMWareVixLibrary.VixRemoveSnapshotOptions.VIX_SNAPSHOT_REMOVE_CHILDREN : 0),
                                                            null,
                                                            null);

                // wait for the job to complete
                lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
                convertToException(lErr, true);
            }
            else throw new SmartFrogException("Failed to delete current snapshot");
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while deleting current snapshot", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Reverts a virtual machine to the snapshot with the given name.
     * @param inImg The VM image.
     * @param inName The name of the snapshot.
     * @throws SmartFrogException
     */
    public void revertToSnapshot(VMWareImageModule inImg, String inName) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            acquireVMHandle(inImg);

            // get the snapshot handle
            IntByReference snapHandle = new IntByReference(VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE);
            long lErr = vixLib.VixVM_GetNamedSnapshot(inImg.getVMHandle().getValue(),
                                                        inName,
                                                        snapHandle);

            convertToException(lErr, true);

            if (snapHandle.getValue() != VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE) {
                // revert to snapshot and prevent it from powering on
                iJobHandle = vixLib.VixVM_RevertToSnapshot(inImg.getVMHandle().getValue(),
                                                            snapHandle.getValue(),
                                                            VMWareVixLibrary.VixVMPowerOpOptions.VIX_VMPOWEROP_SUPPRESS_SNAPSHOT_POWERON,
                                                            VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE,
                                                            null,
                                                            null);

                // wait for the job to complete
                lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
                convertToException(lErr, true);
            }
            else throw new SmartFrogException("Failed to get named snapshot: " + inName);
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while getting named snapshot", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Reverts a virtual machine to its current snapshot.
     * @param inImg The VM image.
     * @throws SmartFrogException
     */
    public void revertToSnapshot(VMWareImageModule inImg) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            acquireVMHandle(inImg);

            // get the snapshot handle
            IntByReference snapHandle = new IntByReference(VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE);
            long lErr = vixLib.VixVM_GetCurrentSnapshot(inImg.getVMHandle().getValue(),
                                                            snapHandle);

            convertToException(lErr, true);

            if (snapHandle.getValue() != VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE) {
                // revert to snapshot and prevent it from powering on
                iJobHandle = vixLib.VixVM_RevertToSnapshot(inImg.getVMHandle().getValue(),
                                                            snapHandle.getValue(),
                                                            VMWareVixLibrary.VixVMPowerOpOptions.VIX_VMPOWEROP_SUPPRESS_SNAPSHOT_POWERON,
                                                            VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE,
                                                            null,
                                                            null);

                // wait for the job to complete
                lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
                convertToException(lErr, true);
            }
            else throw new SmartFrogException("Failed to get current snapshot");
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while getting current snapshot", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }

    /**
     * Waits for the tools to come up in a virtual machine.
     * @param inImg The VM image.
     * @param inTimeout The timeout in seconds. 0 means no timeout.
     * @throws SmartFrogException
     */
    public void waitForTools(VMWareImageModule inImg, int inTimeout) throws SmartFrogException {
        int iJobHandle = NULL_HANDLE;
        try {
            // ensure that a connection is established
            acquireVMHandle(inImg);

            // wait for the tools the virtual machine
            iJobHandle = vixLib.VixVM_WaitForToolsInGuest( inImg.getVMHandle().getValue(),
                                                        inTimeout,
                                                        null,
                                                        null);

            // wait for the job to complete
            long lErr = vixLib.VixJob_Wait(iJobHandle, VMWareVixLibrary.VixPropertyID.VIX_PROPERTY_NONE);
            convertToException(lErr, true);
        } catch(SmartFrogException e) {
            // don't convert vix exceptions into smartfrog exceptions
            throw e;
        } catch (Exception e) {
            // any exception will be caught and wrapped because the native vix library may produce exceptions
            throw new SmartFrogException(inImg.getVMPath() + ": Error while waiting for tools", e);
        } finally {
            releaseHandle(iJobHandle);
        }
    }
}
