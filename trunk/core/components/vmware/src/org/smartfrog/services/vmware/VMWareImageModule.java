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
        if (file.exists() && file.getName().endsWith(".vmx"))
            newModule = new VMWareImageModule(inImagePath);

        return newModule;
    }

//      VMFox code
//          to be used when VMFox is running correctly
//    /**
//     * Executes a command (or doesn't) based on the power state conitions.
//     * If no condition is required use: powerCondExecVMFoxCommand(POWER_COND_NONE, true, [command]);
//     * @param inFlags The flags which are required. (Use POWER_STATUS_ flags and combine them bitwise.)
//     * @param inStrict Determines whether all bits of the flag have to be set (true) or not (false).
//     * @param inCommand The command which should be executed.
//     * @return True if it was executed and no error occured, false otherwise.
//     */
//    private boolean powerCondExecVMFoxCommand(int inFlags, boolean inStrict, String inCommand)
//    {
//        // check the flags
//        int iResult = getPowerState() & inFlags;
//
//        // determine wether the conditions have been fulfilled
//        if ((inStrict ? (iResult == inFlags) : (iResult != 0)))
//        {
//            try {
//                String strOutput = vmComm.execVMcmd(inCommand);
//
//                if (strOutput.length() == 0)
//                    return true;
//                else
//                    strLastError = strOutput;
//            } catch (IOException e) {
//                // TODO: error logging
//            }
//        }
//        return false;
//    }

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
                String strOutput = vmComm.execVMcmd("vmrun", inCommand);

                if (strOutput.length() == 0)
                    return true;
                else
                    strLastError = strOutput;
            } catch (Exception e) {
                return false;
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

//      VMFox code
//          to be used when VMFox is running correctly
//    /**
//     * @return the powerstate of this machine
//     */
//    public int getPowerState()
//    {
//        try {
//            String strOutput = vmComm.execVMcmd(strImagePath + " powerstate");
//
//            // parse the output status id
//            return Integer.parseInt(strOutput);
//        } catch (IOException e) {
//            // TODO: error logging
//        }
//
//        return STATUS_ERROR;
//    }

    /**
     * Get the power state of this VM
     * @return
     */
    public int getPowerState()
    {
        try {
            // execute the command
            String strOutput = vmComm.execVMcmd("vmware-cmd", strImagePath + " getstate");

            if (strOutput.trim().endsWith("on"))
                return POWER_STATUS_POWERED_ON;
            else if (strOutput.trim().endsWith("off"))
                return POWER_STATUS_POWERED_OFF;
            else
                // don't know which are the other output strings
                return STATUS_ERROR;
        } catch (Exception e) {
            return STATUS_ERROR;
        }
    }

//      VMFox code
//          to be used when VMFox is running correctly
//    /**
//     * @return the tools state of this machine
//     */
//    public int getToolsState()
//    {
//        try {
//            String strOutput = vmComm.execVMcmd(strImagePath + " toolsstate");
//
//            // parse the output status id
//            return Integer.parseInt(strOutput);
//        } catch (IOException e) {
//            // TODO: error logging
//        }
//
//        return STATUS_ERROR;
//    }

// VMFox code
//    /**
//     * Starts the machine. Has to be powered off or suspended.
//     * @return True if the start command has been given and no error occured, false otherwise.
//     */
//    public boolean startUp() {
//        // only try to start the machine if it's powered off or suspended
//        return powerCondExecVMFoxCommand(   POWER_STATUS_POWERED_OFF | POWER_STATUS_SUSPENDED,
//                                            false,
//                                            strImagePath + " start");
//    }
//
//    /**
//     * Shuts this virtual machine down. Has to be powered on.
//     * @return True if the shutdown command has been given and no error occured, false otherwise.
//     */
//    public boolean shutDown() {
//        // only try to shutdown if the machine is running
//        return powerCondExecVMFoxCommand(   POWER_STATUS_POWERED_ON,
//                                            true,
//                                            strImagePath + " stop");
//    }
//
//    /**
//     * Suspends the virtual machine. Has to be running.
//     * @return True if the suspend command has been given and no error occured, false otherwise.
//     */
//    public boolean suspend() {
//        // only try to suspend if the machine is running
//        return powerCondExecVMFoxCommand(   POWER_STATUS_POWERED_ON,
//                                            true,
//                                            strImagePath + " suspend");
//    }
//
//    /**
//     * Resets a virtual machine.
//     * @return True if the reset command has been given and no error occured, false otherwise.
//     */
//    public boolean reset() {
//        return powerCondExecVMFoxCommand(   POWER_COND_NONE,
//                                            true,
//                                            strImagePath + " reset");
//  }

    /**
     * Starts the machine. Has to be powered off or suspended.
     * @return True if the start command has been given and no error occured, false otherwise.
     */
    public boolean startUp() {
        // only try to start the machine if it's powered off or suspended
        return powerCondExecVMFoxCommand(   POWER_STATUS_POWERED_OFF | POWER_STATUS_SUSPENDED,
                                            false,
                                            "start " + strImagePath);
    }

    /**
     * Shuts this virtual machine down. Has to be powered on.
     * @return True if the shutdown command has been given and no error occured, false otherwise.
     */
    public boolean shutDown() {
        // only try to shutdown if the machine is running
        return powerCondExecVMFoxCommand(   POWER_STATUS_POWERED_ON,
                                            true,
                                            "stop " + strImagePath);
    }

    /**
     * Suspends the virtual machine. Has to be running.
     * @return True if the suspend command has been given and no error occured, false otherwise.
     */
    public boolean suspend() {
        // only try to suspend if the machine is running
        return powerCondExecVMFoxCommand(   POWER_STATUS_POWERED_ON,
                                            true,
                                            "suspend " + strImagePath);
    }

    /**
     * Resets a virtual machine.
     * @return True if the reset command has been given and no error occured, false otherwise.
     */
    public boolean reset() {
        return powerCondExecVMFoxCommand(   POWER_COND_NONE,
                                            true,
                                            "reset " + strImagePath);
    }

//      VMFox code
//          to be used when VMFox is running correctly
//    /**
//     * Registers a virtual machine.
//     * @return True if the register command has been given and no error occured, false otherwise.
//     */
//    public boolean registerVM() {
//        return powerCondExecVMFoxCommand(   POWER_COND_NONE,
//                                            true,
//                                            strImagePath + " register");
//    }
//
//    /**
//     * Unregisters a virtual machine. Has to be shut down or suspended.
//     * @return True if the unregister command has been given and no error occured, false otherwise.
//     */
//    public boolean unregisterVM() {
//        return powerCondExecVMFoxCommand(   POWER_STATUS_POWERED_OFF | POWER_STATUS_SUSPENDED,
//                                            false,
//                                            strImagePath + " unregister");
//    }

    /**
     * Registers a virtual machine.
     * @return True if the register command has been given and no error occured, false otherwise.
     */
    public boolean registerVM() {
        try {
            String strOutput = vmComm.execVMcmd("vmware-cmd", "-s register " + strImagePath);
            if (strOutput.replace("\r","").replace("\n","").trim().endsWith(" = 1"))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Unregisters a virtual machine. Has to be shut down or suspended.
     * @return True if the unregister command has been given and no error occured, false otherwise.
     */
    public boolean unregisterVM() {
        try {
            String strOutput = vmComm.execVMcmd("vmware-cmd", "-s unregister " + strImagePath);
            if (strOutput.replace("\r","").replace("\n","").trim().endsWith(" = 1"))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }
}
