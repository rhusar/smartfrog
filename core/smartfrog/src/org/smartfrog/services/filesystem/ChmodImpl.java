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

package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Component to set the access permissions of a file or directory.
 */
public class ChmodImpl extends PrimImpl implements Chmod {

    private String target,
            userPermissions,
            groupPermissions,
            otherPermissions,
            octalNotation;

    private boolean recursively;
    public static final String ERROR_NO_PERMISSIONS_SPECIFIED = "No permissions specified.";
    public static final String ERROR_NO_WINDOWS = "Chmod not supported under Windows.";

    public ChmodImpl() throws RemoteException {

    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     * Attributees that require injection are handled during sfDeploy().
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        // if this is a windows system terminate
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            sfLog().error(ERROR_NO_WINDOWS);
            throw new SmartFrogException(ERROR_NO_WINDOWS);
        }

        // resolve the attributes
        target = FileSystem.lookupAbsolutePath(this, ATTR_TARGET, null, null, true, null);
        userPermissions = sfResolve(ATTR_USER_PERMISSIONS, "", false);
        groupPermissions = sfResolve(ATTR_GROUP_PERMISSIONS, "", false);
        otherPermissions = sfResolve(ATTR_OTHER_PERMISSIONS, "", false);
        octalNotation = sfResolve(ATTR_OCTAL_CODE, "", false);
        recursively = sfResolve(ATTR_RECURSIVELY, false, true);

        if (userPermissions.equals("") &&
            groupPermissions.equals("") &&
            otherPermissions.equals("") &&
            octalNotation.equals("")) {
            sfLog().error(ERROR_NO_PERMISSIONS_SPECIFIED);
            throw new SmartFrogException(ERROR_NO_PERMISSIONS_SPECIFIED);
        }
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
     public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        // compose the options
        List<String> commands=new ArrayList<String>(4);
        commands.add("chmod");

        if (recursively) {
            commands.add("-R ");
        }


        if (octalNotation.length() != 0) {
            commands.add(octalNotation);
        } else {
            boolean comma = false;
            String modeString = "";
            if (userPermissions.length() != 0) {
                modeString = 'u' + userPermissions;
                comma = true;
            }
            if (groupPermissions.length() != 0) {
                // multiple user classes have to be comma-separated
                modeString = (comma ? "," : "") + 'g' + groupPermissions;
                comma = true;
            }
            if (otherPermissions.length() != 0) {
                // multiple user classes have to be comma-separated
                modeString = (comma ? "," : "") + 'o' + otherPermissions;
            }
            commands.add(modeString);
        }

        // execute the command
        String[] commandArray = commands.toArray(new String[commands.size()]);
        String details="";
        for(String s:commandArray) {
            details+=s;
            details+=" ";
        }
        sfLog().debug(details);
        try {
            Runtime.getRuntime().exec(commandArray);
        }
        catch (IOException e) {
            sfLog().error("Error while executing: " + details, e);
            throw SmartFrogException.forward("Error while executing: " + details, e);
        }
        //now see if we are to self terminate
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(TerminationRecord.NORMAL,
                "chmod executed",
                sfCompleteNameSafe(),
                null);
    }
}
