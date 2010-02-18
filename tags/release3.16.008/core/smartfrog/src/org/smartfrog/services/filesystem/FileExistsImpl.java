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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.io.File;

/**
 * A component to validate files
 */
public class FileExistsImpl extends FileUsingComponentImpl implements FileExists {

    private long minSize = -1, maxSize=-1;
    private boolean canBeFile, canBeDir;
    private String lastError="";

    public FileExistsImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        //set up all the filename bindings
        bindWithDir(true, "");
        //get te other values
        minSize = sfResolve(ATTR_MIN_SIZE, minSize, true);
        maxSize = sfResolve(ATTR_MAX_SIZE, maxSize, true);
        canBeFile = sfResolve(ATTR_CAN_BE_FILE, true, true);
        canBeDir = sfResolve(ATTR_CAN_BE_DIR, true, true);
        //maybe check on startup
        boolean checkOnStartup= sfResolve(ATTR_CHECKONSTARTUP, true, true);
        if(checkOnStartup) {
            if(!evaluate()) {
                //on failure, throw the last error
                throw new SmartFrogDeploymentException(lastError,this);
            }
            //and look at workflow options
            new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,null,null,null);
        }
    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    public synchronized boolean evaluate() throws RemoteException, SmartFrogException {
        File f = getFile();
        if (!f.exists()) {
            lastError = "Does not exist: " + file;
            sfLog().debug(lastError);
            return false;
        }
        boolean isFile = f.isFile();
        if (isFile && !canBeFile) {
            lastError = "Is of type file: " + file;
            sfLog().debug(lastError);
        }
        boolean isDir = f.isDirectory();
        if (isDir && !canBeDir) {
            lastError = "Is a directory: " + file;
            sfLog().debug(lastError);
        }
        if (minSize >= 0 && f.length() < minSize) {
            lastError = "Too short " + file + " - size of " + f.length() + " is below the minSize of "
                    + minSize;
            sfLog().debug(lastError);
            return false;
        }
        if (maxSize >= 0 && f.length() > maxSize) {
            lastError = "Too long " + file + " - size of " + f.length() + " is above the maxSize of "
                    + maxSize;
            sfLog().debug(lastError);
            return false;
        }
        return true;
    }
}
