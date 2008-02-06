package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.io.IOException;

/**
 * Component to set the access permissions of a file or directory.
 */
public class ChmodImpl extends PrimImpl implements Chmod {

    private String  strTarget,
                    strUserPermissions,
                    strGroupPermissions,
                    strOtherPermissions,
                    strOctalNotation;

    private boolean bRecursively;

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
            // TODO: how to properly terminate components?
            sfLog().error("Chmod not supported under Windows.");
            throw new SmartFrogException("Chmod not supported under Windows.");
        }

        // resolve the attributes
        strTarget               = FileSystem.lookupAbsolutePath(this, ATTR_TARGET, null, null, true, null);
        strUserPermissions      = sfResolve(ATTR_USER_PERMISSIONS, "", false);
        strGroupPermissions     = sfResolve(ATTR_GROUP_PERMISSIONS, "", false);
        strOtherPermissions     = sfResolve(ATTR_OTHER_PERMISSIONS, "", false);
        strOctalNotation        = sfResolve(ATTR_OCTAL_CODE, "", false);
        bRecursively            = sfResolve(ATTR_RECURSIVELY, false, true);

        if (strUserPermissions.equals("") &&
            strGroupPermissions.equals("") &&
            strOtherPermissions.equals("") &&
            strOctalNotation.equals("")) {
            sfLog().error("No permissions specified.");
            throw new SmartFrogException("No permissions specified.");
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
        String strOptions = "";

        if (bRecursively)
            strOptions = "-R ";

        if (!strOctalNotation.equals("")) {
            strOptions = strOctalNotation;
        }
        else {
            boolean bComma = false;
            if (!strUserPermissions.equals("")) {
                strOptions += "u" + strUserPermissions;
                bComma = true;
            }
            if (!strGroupPermissions.equals("")) {
                // multiple user classes have to be comma-separated
                if (bComma)
                    strOptions += "'";

                strOptions += "g" + strGroupPermissions;
                bComma = true;
            }
            if (!strOtherPermissions.equals("")) {
                // multiple user classes have to be comma-separated
                if (bComma)
                    strOptions += "'";
                
                strOptions += "o" + strOtherPermissions;
            }
        }

        // compose the command string
        String strCommand = String.format("chmod %s %s",strOptions, strTarget);

        // execute the command
        try {
            Runtime.getRuntime().exec(strCommand);
        }
        catch (Exception e) {
            sfLog().error("Error while executing: " + strCommand, e);
            throw new SmartFrogException("Error while executing: " + strCommand, e);
        }
    }
}
