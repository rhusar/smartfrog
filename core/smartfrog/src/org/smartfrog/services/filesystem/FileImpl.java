/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.PlatformHelper;

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
     * a log
     */
    private Log log;

    public FileImpl() throws RemoteException {
    }

    /**
     * all our binding stuff. reads attributes, builds the filename, checks it, updates attributes
     *
     * @throws RemoteException
     * @throws SmartFrogRuntimeException
     */
    public void bind() throws RemoteException, SmartFrogRuntimeException {

        boolean debugEnabled = log.isDebugEnabled();

        File parentDir = null;
        String dir = lookupAbsolutePath(this,
                varDir,
                (String) null,
                (File) null,
                false,
                null);
        if (dir != null) {
            if (debugEnabled) {
                log.debug("dir=" + dir);
            }
            parentDir = new File(dir);
        }
        String filename = lookupAbsolutePath(this,
                        varFilename,
                        (String) null,
                        parentDir,
                        true,
                        null);

        File file=new File(parentDir,filename);
        if ( debugEnabled ) {
            log.debug("absolute file=" + file.toString());
        }
        bind(file);

        //now test our state

        mustExist =getBool(varMustExist,false,false);
        mustRead = getBool(varMustWrite, false, false);
        mustWrite = getBool(varMustRead, false, false);
        mustBeDir = getBool(varMustBeDir, false, false);
        mustBeFile = getBool(varMustBeFile, false, false);
        testOnStartup = getBool(varTestOnStartup, false, false);
        testOnLiveness = getBool(varTestOnLiveness, false, false);

        exists = file.exists();
        boolean isDirectory;
        boolean isFile;
        boolean isEmpty;
        long timestamp;
        long length;
        if(exists) {
            isDirectory = file.isDirectory();
            if(isDirectory && debugEnabled ) {
                log.debug("file is a directory");
            }
            isFile = file.isFile();
            if(isFile && debugEnabled ) {
                log.debug("file is a normal file");
            }
            timestamp = file.lastModified();
            length = file.length();
        } else {
            if(debugEnabled) {
                log.debug("file does not exist");
            }
            isDirectory=false;
            isFile = false;
            timestamp=-1;
            length=0;
        }
        isEmpty = length == 0;

        setAttribute(varExists, exists);
        setAttribute(varIsDirectory,isDirectory);
        setAttribute(varIsFile,isFile);
        setAttribute(varIsEmpty,isEmpty);
        setAttribute(varTimestamp, timestamp);
        setAttribute(varLength, length);

        String name=getFile().getName();
        sfReplaceAttribute(varShortname,name);

    }

    private void setAttribute(String attr, String  value) throws SmartFrogRuntimeException, RemoteException {
        if ( log.isDebugEnabled() ) {
            log.debug(attr + " = " + value);
        }
        sfReplaceAttribute(attr, value);
    }


    private void setAttribute(String attr, boolean flag) throws SmartFrogRuntimeException, RemoteException {
        if ( log.isDebugEnabled() ) {
            log.debug(attr + " = " + flag);
        }
        sfReplaceAttribute(attr,Boolean.valueOf(flag));
    }

    /**
     * get a boolean value of an attribute
     * @param attr
     * @param value
     * @param mandatory
     * @return
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    private boolean getBool(String attr,boolean value,boolean mandatory) throws SmartFrogResolutionException,
            RemoteException {
        Boolean b=(Boolean) sfResolve(attr,Boolean.valueOf(value),mandatory);
        return b.booleanValue();
    }
    private void setAttribute(String attr, long value) throws SmartFrogRuntimeException, RemoteException {
        if(log.isDebugEnabled() ) {
            log.debug(attr+" = "+ value);
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
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        log= sfGetApplicationLog();
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
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *                                  component is terminated
     * @throws java.rmi.RemoteException for consistency with the {@link org.smartfrog.sfcore.prim.Liveness} interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if(testOnLiveness) {
            testFileState();
        }

    }

    /**
     * do our file state test
     * @throws SmartFrogLivenessException if a test failed
     */
    protected void testFileState() throws SmartFrogLivenessException {
        if ( log.isDebugEnabled() ) {
            log.debug("liveness check will look for "+getFile().toString());
        }

        if(mustExist && !getFile().exists()) {
            throw new SmartFrogLivenessException("File "+getFile().getAbsolutePath()+" does not exist");
        }
        if(mustRead && !getFile().canRead()) {
            throw new SmartFrogLivenessException("File " + getFile().getAbsolutePath() + " is not readable");
        }
        if (mustWrite && !getFile().canWrite()) {
            throw new SmartFrogLivenessException("File " + getFile().getAbsolutePath() + " is not writeable");
        }
    }


    /**
     * This static call is a helper for any component that wants
     * to get either an absolute path or a FileIntf binding to an attribute.
     * The attribute is looked up on a component. If it is bound to anything
     * that implements FileIntf, then that component is asked for an absolute path.
     * if it is bound to a string, then the string is turned into an absolute path,
     * relative to any directory named, after the string is converted into platform
     * appropriate forward/back slashes.
     * @param component component to look up the path from
     * @param attribute the name of the attribute to look up
     * @param defval a default value. This should already be in the local format for the target platform,
     * and absolute. Can be null. No used when mandatory is true
     * @param baseDir optional base directory for a relative file when constructing from a string
     * @param mandatory flag that triggers the throwing of a SmartFrogResolutionException when things
     * go wrong
     * @param platform a platform to use for converting filetypes. Set to null to use
     * the default helper for this platform.
     * @return
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    public static String lookupAbsolutePath(Prim component,
                                         Reference attribute,
                                         String defval,
                                         File baseDir,
                                         boolean mandatory,
                                         PlatformHelper platform)
            throws SmartFrogResolutionException, RemoteException {
        Object referenceObj= component.sfResolve(attribute,mandatory);
        if(referenceObj==null) {
            //mandatory must be false, because we did not get a value.
            return defval;
        }
        if(referenceObj instanceof FileIntf) {
            //file interface: get the info direct from the component
            FileIntf fileComponent=(FileIntf) referenceObj;
            String path=fileComponent.getAbsolutePath();
            return path;
        } else if(referenceObj instanceof String) {
            //string: convert that into an absolute path
            //without any directory info. so its relative to "here"
            //wherever "here" is for the process
            String filename=(String)referenceObj;
            if(platform==null) {
                platform = PlatformHelper.getLocalPlatform();
            }
            filename = platform.convertFilename(filename);
            File newfile;
            //create a file from the string
            if(baseDir!=null) {
                newfile=new File(baseDir,filename);
            } else {
                newfile = new File(filename);
            }
            String path=newfile.getAbsolutePath();
            return path;
        } else {
            //at this point the type is not supported. So
            //we have to advise the caller that they have an illegal type.
            Reference owner;
            owner= ComponentHelper.completeNameSafe(component);
            throw SmartFrogResolutionException.illegalClassType(attribute,owner);
        }
    }

    /**
     * This static call is a helper for any component that wants
     * to get either an absolute path or a FileIntf binding to an attribute.
     * The attribute is looked up on a component. If it is bound to anything
     * that implements FileIntf, then that component is asked for an absolute path.
     * if it is bound to a string, then the string is turned into an absolute path,
     * relative to any directory named, after the string is converted into platform
     * appropriate forward/back slashes.
     *
     * @param component component to look up the path from
     * @param attribute the name of the attribute to look up
     * @param defval    a default value. This should already be in the local format for the target platform,
     *                  and absolute. Can be null. Not used when mandatory is true
     * @param baseDir   optional base directory for a relative file when constructing from a string
     * @param mandatory flag that triggers the throwing of a SmartFrogResolutionException when things
     *                  go wrong
     * @param platform  a platform to use for converting filetypes. Set to null to use
     *                  the default helper for this platform.
     * @return
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    public static String lookupAbsolutePath(Prim component,
                                            String attribute,
                                            String defval,
                                            File baseDir,
                                            boolean mandatory,
                                            PlatformHelper platform)
            throws SmartFrogResolutionException, RemoteException {
        return lookupAbsolutePath(component,new Reference(attribute),defval,baseDir, mandatory,platform);
    }


    /**
     * Look up the absolutePath attribute of any component,
     * then turn it into a file.
     * @param component component to resolve against
     * @return file representing the path.
     * @throws SmartFrogResolutionException If the attribute is not defined.
     * @throws RemoteException
     */
    public static File resolveAbsolutePath(Prim component)
            throws SmartFrogResolutionException,
            RemoteException {
        String absolutePath = component.sfResolve(FileUsingComponent.ATTR_ABSOLUTE_PATH, "", true);
        File file = new File(absolutePath);
        return file;
    }

    /**
     * Look up the absolutePath attribute of any FileUsingComponent, then turn it into a
     * file.
     * Note that the RPC method is not used; only sf attributes. Thus the coupling
     * is much looser.
     * @param component component to resolve against
     * @return file representing the path.
     * @throws SmartFrogResolutionException If the attribute is not defined.
     * @throws RemoteException
     */
    public static File resolveAbsolutePath(FileUsingComponent component)
            throws SmartFrogResolutionException, RemoteException {
        return resolveAbsolutePath((Prim)component);
    }

}
