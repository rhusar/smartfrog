/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.services.vmware;

import java.io.IOException;
import java.io.File;

public class VMWareImageModule {

    /**
     * The path to the vmware image file.
     */
    private String  strImagePath    = "";
    private String  strLastError    = "";

    /**
     * Class to communicate with the vmware server
     */
    private VMWareCommunicator vmComm = new VMWareCommunicator();

    public static final int STATUS_ERROR                    = -1;
    public static final int POWER_COND_NONE                 = 0x0000;

    // power status of a vm, using the original bit flag values
    public static final int POWER_STATUS_POWERING_OFF       = 0x0001;
    public static final int POWER_STATUS_POWERED_OFF        = 0x0002;
    public static final int POWER_STATUS_POWERING_ON        = 0x0004;
    public static final int POWER_STATUS_POWERED_ON         = 0x0008;
    public static final int POWER_STATUS_SUSPENDING         = 0x0010;
    public static final int POWER_STATUS_SUSPENDED          = 0x0020;
    public static final int POWER_STATUS_TOOLS_RUNNING      = 0x0040;
    public static final int POWER_STATUS_RESETTING          = 0x0080;
    public static final int POWER_STATUS_BLOCKED_ON_MSG     = 0x0100;

    // tools status flags
    public static final int TOOLS_STATUS_UNKNOWN            = 0x0001;
    public static final int TOOLS_STATUS_RUNNING            = 0x0002;
    public static final int TOOLS_STATUS_NOT_INSTALLED      = 0x0004;

    /**
     * Constructor.
     */
    private VMWareImageModule(String inImagePath) {
        strImagePath = inImagePath;
    }

    /**
     * Gets the path to the .vmx image file.
     * @return
     */
    public String getVMPath()
    {
        return strImagePath;
    }

    /**
     * Creates a new VMWare Image Module if the path is valid.
     * @param inImagePath Valid path to a .vmx file.
     * @return A new instance on success or null on failure.
     */
    public static VMWareImageModule createImageModule(String inImagePath) {
        // the retured object
        VMWareImageModule newModule = null;

        // validate the path
        File file = new File(inImagePath);
        if (file.exists() && file.isFile())
            newModule = new VMWareImageModule(inImagePath);

        return newModule;
    }

    /**
     * Executes a command (or doesn't) based on the power state conitions.
     * If no condition is required use: powerCondExecVMFoxCommand(POWER_COND_NONE, true, [command]);
     * @param inFlags The flags which are required. (Use POWER_STATUS_ flags and combine them bitwise.)
     * @param inStrict Determines whether all bits of the flag have to be set (true) or not (false).
     * @param inCommand The command which should be executed.
     * @return True if it was executed and no error occured, false otherwise.
     */
    private boolean powerCondExecVMFoxCommand(int inFlags, boolean inStrict, String inCommand)
    {
        // check the flags
        int iResult = getPowerState() & inFlags;

        // determine wether the conditions have been fulfilled
        if ((inStrict ? (iResult == inFlags) : (iResult != 0)))
        {
            try {
                String strOutput = vmComm.execVMcmd(inCommand);

                if (strOutput.length() == 0)
                    return true;
                else
                    strLastError = strOutput;
            } catch (IOException e) {
                // TODO: error logging
            }
        }
        return false;
    }

     /**
     *
     * @return the last error message
     */
    public String getLastErrorMessage() {
        return strLastError;
    }

    /**
     * @return the powerstate of this machine
     */
    public int getPowerState()
    {
        try {
            String strOutput = vmComm.execVMcmd(strImagePath + " powerstate");

            // parse the output status id
            return Integer.parseInt(strOutput);
        } catch (IOException e) {
            // TODO: error logging
        }

        return STATUS_ERROR;
    }

    /**
     * @return the tools state of this machine
     */
    public int getToolsState()
    {
        try {
            String strOutput = vmComm.execVMcmd(strImagePath + " toolsstate");

            // parse the output status id
            return Integer.parseInt(strOutput);
        } catch (IOException e) {
            // TODO: error logging
        }

        return STATUS_ERROR;
    }

    /**
     * Starts the machine. Has to be powered off or suspended.
     * @return True if the start command has been given and no error occured, false otherwise.
     */
    public boolean startUp() {
        // only try to start the machine if it's powered off or suspended
        return powerCondExecVMFoxCommand(   POWER_STATUS_POWERED_OFF | POWER_STATUS_SUSPENDED,
                                            false,
                                            strImagePath + " start");
    }

    /**
     * Shuts this virtual machine down. Has to be powered on.
     * @return True if the shutdown command has been given and no error occured, false otherwise.
     */
    public boolean shutDown() {
        // only try to shutdown if the machine is running
        return powerCondExecVMFoxCommand(   POWER_STATUS_POWERED_ON,
                                            true,
                                            strImagePath + " stop");
    }

    /**
     * Suspends the virtual machine. Has to be running.
     * @return True if the suspend command has been given and no error occured, false otherwise.
     */
    public boolean suspend() {
        // only try to suspend if the machine is running
        return powerCondExecVMFoxCommand(   POWER_STATUS_POWERED_ON,
                                            true,
                                            strImagePath + " suspend");
    }

    /**
     * Resets a virtual machine.
     * @return True if the reset command has been given and no error occured, false otherwise.
     */
    public boolean reset() {
        return powerCondExecVMFoxCommand(   POWER_COND_NONE,
                                            true,
                                            strImagePath + " reset");
    }

    /**
     * Registers a virtual machine.
     * @return True if the register command has been given and no error occured, false otherwise.
     */
    public boolean registerVM() {
        return powerCondExecVMFoxCommand(   POWER_COND_NONE,
                                            true,
                                            strImagePath + " register");
    }

    /**
     * Unregisters a virtual machine. Has to be shut down or suspended.
     * @return True if the unregister command has been given and no error occured, false otherwise.
     */
    public boolean unregisterVM() {
        return powerCondExecVMFoxCommand(   POWER_STATUS_POWERED_OFF | POWER_STATUS_SUSPENDED,
                                            false,
                                            strImagePath + " unregister");
    }
}
