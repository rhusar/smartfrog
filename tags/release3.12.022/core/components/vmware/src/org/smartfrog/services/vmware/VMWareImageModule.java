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
import org.smartfrog.services.filesystem.FileSystem;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jna.ptr.IntByReference;

public class VMWareImageModule {
    /**
     * The path to the vmware image file.
     */
    private String  strImagePath    = "";

    /**
     * Reference to the communicator class
     */
    private VMWareCommunicator vmComm = null;

    private String  strGuestOSUser = "";
    private String  strGuestOSPasswd = "";

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
        this.strImagePath   = inImagePath;
        this.vmComm         = inComm;
    }

    /**
     * Gets the path to the .vmx image file.
     * @return the path
     */
    public String getVMPath()
    {
        return strImagePath;
    }

    /**
     * Gets the handle of this virtual machine image.
     * @return The vm handle.
     */
    public IntByReference getVMHandle() {
        return this.iVMHandle;
    }

    /**
     * Gets the user name for the guest OS.
     * @return The user name for the guest OS.
     */
    public String getGuestOSUser() {
        return strGuestOSUser;
    }

    /**
     * Sets the user name for the guest OS.
     * @param strGuestOSUser The user name for the guest OS.
     */
    public void setGuestOSUser(String strGuestOSUser) {
        this.strGuestOSUser = strGuestOSUser;
    }

    /**
     * Gets the user password for the guest OS.
     * @return The user password for the guest OS.
     */
    public String getGuestOSPasswd() {
        return strGuestOSPasswd;
    }

    /**
     * Sets the user password for the guest OS.
     * @param strGuestOSPasswd The user password for the guest OS.
     */
    public void setGuestOSPasswd(String strGuestOSPasswd) {
        this.strGuestOSPasswd = strGuestOSPasswd;
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
        if (file.exists() && file.getName().endsWith(".vmx"))
        {
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
        return this.vmComm.getPowerState(this);
    }

    /**
     * Get the tools state of this VM
     * @return
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public int getToolsState() throws SmartFrogException {
        return this.vmComm.getToolsState(this);
    }

    /**
     * Starts the machine. Has to be powered off or suspended.
     * @throws SmartFrogException
     */
    public void startUp() throws SmartFrogException {
        this.vmComm.startVM(this);
    }

    /**
     * Shuts down this virtual machine. Has to be powered on.
     * @throws SmartFrogException
     */
    public void shutDown() throws SmartFrogException {
        this.vmComm.stopVM(this);
    }

    /**
     * Suspends the virtual machine. Has to be running.
     * @throws SmartFrogException
     */
    public void suspend() throws SmartFrogException {
        this.vmComm.suspendVM(this);
    }

    /**
     * Resets a virtual machine.
     * @throws SmartFrogException
     */
    public void reset() throws SmartFrogException {
        this.vmComm.resetVM(this);
    }

    /**
     * Registers a virtual machine.
     * @throws SmartFrogException
     */
    public void registerVM() throws SmartFrogException {
        this.vmComm.registerVM(this);
    }

    /**
     * Unregisters a virtual machine. Has to be shut down or suspended.
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public void unregisterVM() throws SmartFrogException {
        this.vmComm.unregisterVM(this);
    }

    /**
     * Gets the value of a key of the .vmx configuration file.
     * @param inKey
     * @return Returns the value of the key.
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public String getAttribute(final String inKey) throws SmartFrogException
    {
        try {
            // open the .vmx file
            File vmxFile = new File(this.strImagePath);
            BufferedReader reader = new BufferedReader(new FileReader(vmxFile));

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

            reader.close();

            // attribute not found
            return "";
        } catch (IOException e) {
            throw new SmartFrogException("Error while getting attribute.", e);
        }
    }

    /**
     * Sets the given attribute to the given value. NOTE: Setting non-existing values will have no effect and won't cause errors.
     * @param inKey
     * @param inValue
     * @return Returns "success" or an error message.
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public String setAttribute(final String inKey, final String inValue) throws SmartFrogException
    {
        try {
            // the old value of the attribute, if it existed before
            String strOldVal = "";

            // open the .vmx file to read from
            File vmxFile = new File(this.strImagePath);
            BufferedReader reader = new BufferedReader(new FileReader(vmxFile));

            // create a new file to write to
            File newFile = new File(vmxFile.getAbsolutePath() + "_new");
            BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));

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

            // close and delete the old file
            reader.close();
            vmxFile.delete();

            // rename the new file
            newFile.renameTo(vmxFile);

            return strOldVal;
        } catch (IOException e) {
            throw new SmartFrogException("Error while setting attribute.", e);
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
            // get the power state
            int iPowerState = VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERED_OFF;
            try {
                iPowerState = this.getPowerState();
            } catch (VIXException e) {
                // ignore the file not found exception which will be thrown if
                // virtual machine has not been registered with the vmware server
                if (e.getErrorCode() != VMWareVixLibrary.VixError.VIX_E_FILE_NOT_FOUND)
                    throw e;
            }

            // shut down the vm if it's not powered off or suspended
            if (iPowerState != VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERED_OFF &&
                iPowerState != VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_SUSPENDED)
                this.shutDown();

            // unregister the vm
            this.unregisterVM();

            // get the folder of the virtual machine
            File folder = (new File(this.strImagePath)).getParentFile();

            // get the files in this folder and delete them
            File[] files = folder.listFiles();
            for (File curFile : files)
                if (!curFile.delete())
                    throw new SmartFrogException("Could not delete \"" + curFile.getAbsolutePath() + "\"");

            // delete the folder
            if (!folder.delete())
                throw new SmartFrogException("Could not delete \"" + folder.getAbsolutePath() + "\"");

        } catch (SmartFrogException e) {
            throw new SmartFrogException(this.strImagePath + ": Failed to delete.", e);
        }
    }

    /**
     * Renames this virtual machine.
     * @param inNewName The new name for this virtual machine.
     * @throws SmartFrogException
     */
    public void rename(String inNewName) throws SmartFrogException
    {
        if (inNewName.equals(""))
            throw new SmartFrogException("An empty string is not a valid name.");

        try {
            // get the power state
            int iPowerState = VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERED_OFF;
            try {
                iPowerState = this.getPowerState();
            } catch (VIXException e) {
                // ignore the file not found exception which will be thrown if
                // virtual machine has not been registered with the vmware server
                if (e.getErrorCode() != VMWareVixLibrary.VixError.VIX_E_FILE_NOT_FOUND)
                    throw e;
            }

            // shut down the vm if it's not powered off or suspended
            if (iPowerState != VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_POWERED_OFF &&
                iPowerState != VMWareVixLibrary.VixPowerState.VIX_POWERSTATE_SUSPENDED)
                this.shutDown();

            // unregister the vm
            this.unregisterVM();

            // just rename the folder of the virtual machine and change the display name
            // the reason for this are the virtual machine disks: there is little to
            // none documentation about their structure/content. even the -######.vmdk
            // files contain a little amount of configuration information additionally
            // to the data

            // set the display name
            this.setAttribute("displayName", inNewName);

            File vmxFile = new File(strImagePath);
            File vmFolder = vmxFile.getParentFile();

            // set the execution rights
            vmxFile.setExecutable(true, false);

            // rename the .vmx file
            vmxFile.renameTo(new File(vmFolder.getAbsolutePath() + File.separator + inNewName + ".vmx"));

            // rename the folder
            vmFolder.renameTo(new File(vmFolder.getParent() + File.separator + inNewName));

            // refresh the path to this vm
            this.strImagePath = vmFolder.getAbsolutePath() + File.separator + inNewName + ".vmx";

            // register the vm again
            this.registerVM();

        } catch (Exception e) {
            throw new SmartFrogException(this.strImagePath + ": Failed to rename.", e);
        }
    }

    /**
     * Copies a file from the host OS to the guest OS within the VM.
     * @param inSourceFile The file on the host OS.
     * @param inTargetFile The file on the guest OS.
     * @throws SmartFrogException
     */
    public void copyFileFromHostToGuestOS(String inSourceFile, String inTargetFile) throws SmartFrogException {
        if (this.strGuestOSUser.equals(""))
            throw new SmartFrogException(this.strImagePath + ": Username required for copying files from host to guest OS.");

        this.vmComm.copyFileFromHostToGuestOS(this, inSourceFile, inTargetFile);
    }
}
