/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.utils.ComponentHelper;


import java.io.File;
import java.rmi.RemoteException;

/**
 * Implement a class that can dynamically map to a file
 * created 27-May-2004 10:47:34
 */

public class FileImpl extends FileUsingComponentImpl implements FileIntf {

    private boolean mustExist;
    private boolean mustRead;
    private boolean mustWrite;
    private boolean mustBeFile;
    private boolean mustBeDir;
    private boolean exists;
    private boolean testOnLiveness;
    private boolean testOnStartup;

    /**
     * Constructor
     * @throws RemoteException  In case of network/rmi error
     */
    public FileImpl() throws RemoteException {
    }

    /**
     * all our binding stuff. reads attributes, builds the filename, checks it,
     * updates attributes
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogRuntimeException runtime failure
     */
    public void bind() throws RemoteException, SmartFrogRuntimeException {

        boolean debugEnabled = sfLog().isDebugEnabled();

        File parentDir = null;
        String dir = FileSystem.lookupAbsolutePath(this,
                ATTR_DIR,
                null,
                null,
                false,
                null);
        if (dir != null) {
            if (sfLog().isDebugEnabled()) {
                sfLog().debug("dir=" + dir);
            }
            parentDir = new File(dir);
        }
        String filename = FileSystem.lookupAbsolutePath(this,
                ATTR_FILENAME,
                (String) null,
                parentDir,
                true,
                null);

        File file = new File(filename);
        if (debugEnabled) {
            sfLog().debug("absolute file=" + file.toString());
        }
        bind(file);

        //now test our state

        mustExist = getBool(ATTR_MUST_EXIST, false, false);
        mustRead = getBool(ATTR_MUST_WRITE, false, false);
        mustWrite = getBool(ATTR_MUST_READ, false, false);
        mustBeDir = getBool(ATTR_MUST_BE_DIR, false, false);
        mustBeFile = getBool(ATTR_MUST_BE_FILE, false, false);
        testOnStartup = getBool(ATTR_TEST_ON_STARTUP, false, false);
        testOnLiveness = getBool(ATTR_TEST_ON_LIVENESS, false, false);

        exists = file.exists();
        boolean isDirectory;
        boolean isFile;
        boolean isEmpty;
        boolean isHidden;
        long timestamp;
        long length;
        if (exists) {
            isDirectory = file.isDirectory();
            if (isDirectory && debugEnabled) {
                sfLog().debug("file is a directory");
            }
            isFile = file.isFile();
            if (isFile && debugEnabled) {
                sfLog().debug("file is a normal file");
            }
            timestamp = file.lastModified();
            length = file.length();
            isHidden = file.isHidden();
        } else {
            if (debugEnabled) {
                sfLog().debug("file does not exist");
            }
            isDirectory = false;
            isFile = false;
            timestamp = -1;
            length = 0;
            isHidden = false;
        }
        isEmpty = length == 0;

        setAttribute(ATTR_EXISTS, exists);
        setAttribute(ATTR_IS_DIRECTORY, isDirectory);
        setAttribute(ATTR_IS_FILE, isFile);
        setAttribute(ATTR_IS_EMPTY, isEmpty);
        setAttribute(ATTR_IS_HIDDEN, isHidden);
        setAttribute(ATTR_TIMESTAMP, timestamp);
        setAttribute(ATTR_LENGTH, length);

        String name = getFile().getName();
        setAttribute(ATTR_SHORTNAME, name);

    }

    /**
     * Set attribute value
     * @param attr attribute name
     * @param value attriute value
     * @throws SmartFrogRuntimeException error in setting
     * @throws RemoteException  In case of network/rmi error
     */
    private void setAttribute(String attr, String value)
            throws SmartFrogRuntimeException, RemoteException {
        if (sfLog().isDebugEnabled()) {
            sfLog().debug(attr + " = " + value);
        }
        sfReplaceAttribute(attr, value);
    }

    /**
     * Set boolean value of an attribute
     * @param attr attribute name
     * @param flag boolean value of an attribute
     * @throws SmartFrogRuntimeException error in setting
     * @throws RemoteException  In case of network/rmi error
     */
    private void setAttribute(String attr, boolean flag)
            throws SmartFrogRuntimeException, RemoteException {
        if (sfLog().isDebugEnabled()) {
            sfLog().debug(attr + " = " + flag);
        }
        sfReplaceAttribute(attr, Boolean.valueOf(flag));
    }

    /**
     * get a boolean value of an attribute
     *
     * @param attr attribute name
     * @param value boolean value of an attribute
     * @param mandatory flag indicating if attrbute is mandatory or optional
     * @return boolean value
     * @throws SmartFrogResolutionException error in resolving
     * @throws RemoteException In case of network/rmi error
     */
    private boolean getBool(String attr, boolean value, boolean mandatory)
            throws SmartFrogResolutionException,
            RemoteException {
        Boolean b = (Boolean) sfResolve(attr, Boolean.valueOf(value), mandatory);
        return b.booleanValue();
    }

    /**
     * Set long value of an attribute
     * @param attr attribute name
     * @param value long value of an attribute
     * @throws SmartFrogRuntimeException error in setting
     * @throws RemoteException  In case of network/rmi error
     */
    private void setAttribute(String attr, long value)
            throws SmartFrogRuntimeException, RemoteException {
        if (sfLog().isDebugEnabled()) {
            sfLog().debug(attr + " = " + value);
        }
        sfReplaceAttribute(attr, new Long(value));
    }

    /**
     * Called after instantiation for deployment purposed. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        bind();
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        if (testOnStartup) {
            testFileState();
        }
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,"File "+getFile().getAbsolutePath(),this.sfCompleteNameSafe(),null);
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *                                  component is terminated
     * @throws java.rmi.RemoteException for consistency with the {@link
     *                                  org.smartfrog.sfcore.prim.Liveness}
     *                                  interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException,
            RemoteException {
        super.sfPing(source);
        if (testOnLiveness) {
            testFileState();
        }

    }

    /**
     * do our file state test
     *
     * @throws SmartFrogLivenessException if a test failed
     */
    protected void testFileState() throws SmartFrogLivenessException {
        if (sfLog().isDebugEnabled()) {
            sfLog().debug("liveness check will look for " + getFile().toString());
        }

        if (mustExist && !getFile().exists()) {
            throw new SmartFrogLivenessException("File " +
                    getFile().getAbsolutePath() +
                    " does not exist");
        }
        if (mustRead && !getFile().canRead()) {
            throw new SmartFrogLivenessException("File " +
                    getFile().getAbsolutePath() +
                    " is not readable");
        }
        if (mustWrite && !getFile().canWrite()) {
            throw new SmartFrogLivenessException("File " +
                    getFile().getAbsolutePath() +
                    " is not writeable");
        }
    }


}
