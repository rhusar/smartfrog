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

import com.sun.jna.ptr.IntByReference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VMWareImageModule {
    /**
     * The path to the vmware image file.
     */
    private String imagePath = "";

    /**
     * Reference to the communicator class
     */
    private VMWareCommunicator vmComm = null;

    private String guestOSUser = "";
    private String guestOSPasswd = "";

    /**
     * The vmware handle of this image.
     */
    private IntByReference iVMHandle = new IntByReference(VMWareVixLibrary.VixHandle.VIX_INVALID_HANDLE);

    /**
     * Constructor.
     * @param inImagePath The path to the image.
     * @param inComm Reference to the vmware communicator class.
     */
    private VMWareImageModule(String inImagePath, VMWareCommunicator inComm) {
        imagePath = inImagePath;
        vmComm = inComm;
    }

    /**
     * Gets the path to the .vmx image file.
     * @return the path
     */
    public String getVMPath() {
        return imagePath;
    }

    /**
     * Gets the handle of this virtual machine image.
     * @return The vm handle.
     */
    public IntByReference getVMHandle() {
        return iVMHandle;
    }

    /**
     * Gets the user name for the guest OS.
     * @return The user name for the guest OS.
     */
    public String getGuestOSUser() {
        return guestOSUser;
    }

    /**
     * Sets the user name for the guest OS.
     * @param strGuestOSUser The user name for the guest OS.
     */
    public void setGuestOSUser(String strGuestOSUser) {
        guestOSUser = strGuestOSUser;
    }

    /**
     * Gets the user password for the guest OS.
     * @return The user password for the guest OS.
     */
    public String getGuestOSPasswd() {
        return guestOSPasswd;
    }

    /**
     * Sets the user password for the guest OS.
     * @param strGuestOSPasswd The user password for the guest OS.
     */
    public void setGuestOSPasswd(String strGuestOSPasswd) {
        guestOSPasswd = strGuestOSPasswd;
    }

    /**
     * Creates a new VMWare Image Module if the path is valid.
     * @param inImagePath Valid path to a .vmx file.
     * @param inComm Reference to the vmware communicator class.
     * @return A new instance.
     */
    public static VMWareImageModule createImageModule(String inImagePath, VMWareCommunicator inComm) throws FileNotFoundException
    {
        // validate the path
        File file = new File(inImagePath);
        if (file.exists() && file.getName().endsWith(".vmx")) {
            return new VMWareImageModule(inImagePath, inComm);
        }

        throw new FileNotFoundException(inImagePath);
    }

    /**
     * Get the power state of this VM
     * @return
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public int getPowerState() throws SmartFrogException {
        return vmComm.getPowerState(this);
    }

    /**
     * Get the tools state of this VM
     * @return
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public int getToolsState() throws SmartFrogException {
        return vmComm.getToolsState(this);
    }

    /**
     * Starts the machine. Has to be powered off or suspended.
     * @throws SmartFrogException
     */
    public void startUp() throws SmartFrogException {
        vmComm.startVM(this);
    }

    /**
     * Shuts down this virtual machine. Has to be powered on.
     * @throws SmartFrogException
     */
    public void shutDown() throws SmartFrogException {
        vmComm.stopVM(this);
    }

    /**
     * Suspends the virtual machine. Has to be running.
     * @throws SmartFrogException
     */
    public void suspend() throws SmartFrogException {
        vmComm.suspendVM(this);
    }

    /**
     * Resets a virtual machine.
     * @throws SmartFrogException
     */
    public void reset() throws SmartFrogException {
        vmComm.resetVM(this);
    }

    /**
     * Registers a virtual machine.
     * @throws SmartFrogException
     */
    public void registerVM() throws SmartFrogException {
        vmComm.registerVM(this);
    }

    /**
     * Unregisters a virtual machine. Has to be shut down or suspended.
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public void unregisterVM() throws SmartFrogException {
        vmComm.unregisterVM(this);
    }

    /**
     * Gets the value of a key of the .vmx configuration file.
     * @param inKey
     * @return Returns the value of the key.
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public String getAttribute(final String inKey) throws SmartFrogException
    {
        BufferedReader reader=null;
        try {
            // open the .vmx file
            File vmxFile = new File(this.imagePath);
            reader = new BufferedReader(new FileReader(vmxFile));

            // create pattern
            Pattern pattern = Pattern.compile("^\\s*" + Matcher.quoteReplacement(inKey) + "\\s*=\\s*\"(.*)\"\\s*$");

            // read the content and search for the attribute
            String strLine;
            while((strLine = reader.readLine()) != null)
            {
                // match the line
                Matcher matcher = pattern.matcher(strLine);
                if (matcher.matches()) {
                    reader.close();
                    return matcher.group(1);
                }
            }

            // attribute not found
            return "";
        } catch (IOException e) {
            throw new SmartFrogException("Error while getting attribute.", e);
        } finally {
            FileSystem.close(reader);
        }
    }

    /**
     * Sets the given attribute to the given value. NOTE: Setting non-existing values will have no effect and won't cause errors.
     * @param inKey key
     * @param inValue value
     * @return Returns "success" or an error message.
     * @throws SmartFrogException on a filure to set
     */
    public String setAttribute(final String inKey, final String inValue) throws SmartFrogException
    {
        BufferedReader reader = null;
        BufferedWriter writer=null;
        try {
            // the old value of the attribute, if it existed before
            String strOldVal = "";

            // open the .vmx file to read from
            File vmxFile = new File(imagePath);
            reader = new BufferedReader(new FileReader(vmxFile));

            // create a new file to write to
            File newFile = new File(vmxFile.getAbsolutePath() + "_new");
            writer = new BufferedWriter(new FileWriter(newFile));

            // prepare the pattern
            Pattern pattern = Pattern.compile("^\\s*" + Matcher.quoteReplacement(inKey) + "\\s*=\\s*\"(.*)\"\\s*$");

            // parse the file
            boolean bFound = false;
            String strLine;
            while((strLine = reader.readLine()) != null)
            {
                if (!bFound) {
                    // while the attribute has not yet been found keep matching
                    Matcher matcher = pattern.matcher(strLine);
                    if (matcher.matches()) {
                        // write the new value
                        writer.write(Matcher.quoteReplacement(inKey) + " = \"" + inValue + "\"\n");

                        // set the return value
                        strOldVal = matcher.group(1);
                        bFound = true;
                    }
                    else writer.write(strLine + "\n");
                } else {
                    // attribute has been replaced, just keep copying
                    writer.write(strLine + "\n");
                }
            }

            // add the attribute if it hasn't been in the file before
            if (!bFound)
                writer.write(Matcher.quoteReplacement(inKey) + " = \"" + inValue + "\"\n");

            // close the new file
            writer.close();
            writer=null;

            // close and delete the old file
            reader.close();
            reader=null;
            vmxFile.delete();

            // rename the new file
            newFile.renameTo(vmxFile);

            return strOldVal;
        } catch (IOException e) {
            throw new SmartFrogException("Error while setting attribute.", e);
        } finally {
            FileSystem.close(reader);
            FileSystem.close(writer);
        }
    }

    /**
     * Deletes this virtual machine.
     * @return
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public void delete() throws SmartFrogException
    {
        try {
            shutDownAndUnregister();

            // get the folder of the virtual machine
            File folder = (new File(imagePath)).getParentFile();

            // get the files in this folder and delete them
            File[] files = folder.listFiles();
            for (File curFile : files)
                if (!curFile.delete()) {
                    throw new SmartFrogException("Could not delete \"" + curFile.getAbsolutePath() + "\"");
                }

            // delete the folder
            if (!folder.delete()) {
                throw new SmartFrogException("Could not delete \"" + folder.getAbsolutePath() + "\"");
            }

        } catch (SmartFrogException e) {
            throw new SmartFrogException(imagePath + ": Failed to delete.", e);
        }
    }

    private void shutDownAndUnregister() throws SmartFrogException {
        // get the power state
        int iPowerState = VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERED_OFF;
        try {
            iPowerState = getPowerState();
        } catch (VIXException e) {
            // ignore the file not found exception which will be thrown if
            // virtual machine has not been registered with the vmware server
            if (e.getErrorCode() != VMWareVixLibrary.VixError.VIX_E_FILE_NOT_FOUND) {
                throw e;
            }
        }

        // shut down the vm if it's not powered off or suspended
        if (iPowerState != VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERED_OFF &&
            iPowerState != VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_SUSPENDED) {
            if (!vmComm.convertPowerState(iPowerState).equals("Could not retrieve power state.")) {
                shutDown();
            }
        }

        // unregister the vm
        unregisterVM();
    }

    /**
     * Renames this virtual machine.
     * @param inNewName The new name for this virtual machine.
     * @throws SmartFrogException
     */
    public void rename(String inNewName) throws SmartFrogException
    {
        if (inNewName.length() == 0)
            throw new SmartFrogException("An empty string is not a valid name.");

        try {
            shutDownAndUnregister();

            // just rename the folder of the virtual machine and change the display name
            // the reason for this are the virtual machine disks: there is little to
            // none documentation about their structure/content. even the -######.vmdk
            // files contain a little amount of configuration information additionally
            // to the data

            File vmxFile = new File(imagePath);
            File vmFolder = vmxFile.getParentFile();

            String newVMPath = vmFolder.getAbsolutePath() + File.separator + inNewName + ".vmx";

            // check if there is already a VM existing with that name
            if (vmComm.getImageModule(newVMPath) != null)
                throw new SmartFrogException("A VM with the desired name \"" + inNewName + "\" is already existing");

            // set the display name
            setAttribute("displayName", inNewName);

            // set the execution rights
            vmxFile.setExecutable(true, false);

            // rename the .vmx file
            vmxFile.renameTo(new File(newVMPath));

            // rename the folder
            vmFolder.renameTo(new File(vmFolder.getParent() + File.separator + inNewName));

            // refresh the path to this vm
            imagePath = vmFolder.getParent() + File.separator + inNewName + File.separator + inNewName + ".vmx";

            // register the vm again
            registerVM();

        } catch (Exception e) {
            throw new SmartFrogException(imagePath + ": Failed to rename.", e);
        }
    }

    /**
     * Copies a file from the host OS to the guest OS within the VM.
     * @param inSourceFile The file on the host OS.
     * @param inTargetFile The file on the guest OS.
     * @throws SmartFrogException
     */
    public void copyFileFromHostToGuestOS(String inSourceFile, String inTargetFile) throws SmartFrogException {
        vmComm.copyFileFromHostToGuestOS(this, inSourceFile, inTargetFile);
    }

    /**
     * Executes a command in the guest os.
     * @param inCommand
     * @param inParameters
     * @param inNoWait
     * @throws SmartFrogException
     */
    public void executeInGuestOS(String inCommand, String inParameters, boolean inNoWait) throws SmartFrogException {
        vmComm.executeInGuestOS(this, inCommand, inParameters, inNoWait);
    }

    /**
     * Takes a snapshot of a virtual machine.
     * @param inDesc A descriptino for the snapshot.
     * @param inName The name for the snapshot.
     * @param inIncludeMemory Also include the whole memory?
     * @throws SmartFrogException
     */
    public void takeSnapshot(String inName, String inDesc, boolean inIncludeMemory) throws SmartFrogException {
        vmComm.takeSnapshot(this, inName, inDesc, inIncludeMemory);
    }

    /**
     * Deletes a named snapshot of a virtual machine.
     * @param inName The name of the snapshot.
     * @param inRemoveChildren Remove the children of this snashot, too?
     * @throws SmartFrogException
     */
    public void deleteSnapshot(String inName, boolean inRemoveChildren) throws SmartFrogException {
        vmComm.deleteSnapshot(this, inName, inRemoveChildren);
    }

    /**
     * Deletes a named snapshot of a virtual machine.
     * @param inRemoveChildren Remove the children of this snashot, too?
     * @throws SmartFrogException
     */
    public void deleteSnapshot(boolean inRemoveChildren) throws SmartFrogException {
        vmComm.deleteSnapshot(this, inRemoveChildren);
    }

    /**
     * Reverts a virtual machine to the snapshot with the given name.
     * @param inName The name of the snapshot.
     * @throws SmartFrogException
     */
    public void revertToSnapshot(String inName) throws SmartFrogException {
        vmComm.revertToSnapshot(this, inName);
    }

    /**
     * Reverts a virtual machine to its current snapshot.
     * @throws SmartFrogException
     */
    public void revertToSnapshot() throws SmartFrogException {
        vmComm.revertToSnapshot(this);
    }

    /**
     * Waits for the tools in the guest OS to come up.
     * @param inTimeout The timeout in seconds. 0 means there is not timeout.
     * @throws SmartFrogException
     */
    public void waitForTools(int inTimeout) throws SmartFrogException {
        vmComm.waitForTools(this, inTimeout);
    }

//    /**
//     * Writes an environment variable within the guest operating system.
//     * @param inName The name of the environment variable.
//     * @param inValue The value of the environment variable.
//     */
//    public void writeGuestEnvVar(String inName, String inValue) throws SmartFrogException {
//        vmComm.writeGuestEnvVar(this, inName, inValue);
//    }
//
//    /**
//     * Reads a environment variable of the guest os.
//     * @param inName The name of the environment variable.
//     * @return The content of the variable.
//     * @throws SmartFrogException
//     */
//    public String readGuestEnvVar(String inName) throws SmartFrogException {
//        return vmComm.readGuestEnvVar(this, inName);
//    }

	// apparently not supported by VMware server
//	/**
//	 * Creates a directory in the guest os of this vm.
//	 * @param inDir The path of the directory.
//	 * @throws SmartFrogException
//	 */
//	public void mkdirInGuest(String inDir) throws SmartFrogException {
//		vmComm.mkdirInGuest(this, inDir);
//	}

	// apparently not supported by VMware server
//	/**
//	 * Checks if a directory exists in the guest operating system of this vm.
//	 * @param inDir The directory.
//	 * @return <code>true</code> or <code>false</code>.
//	 * @throws SmartFrogException
//	 */
//	public boolean existsDirInGuest(String inDir) throws SmartFrogException {
//		return vmComm.existsDirInGuest(this, inDir);
//	}
}
