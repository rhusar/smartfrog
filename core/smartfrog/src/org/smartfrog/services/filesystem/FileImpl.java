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

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.utils.PlatformHelper;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.io.File;

/**
 * Implement a class that can dynamically map to a file
 * created 27-May-2004 10:47:34
 */

public class FileImpl extends PrimImpl implements FileIntf {

    private File file;
    private boolean mustExist;
    private boolean mustRead;
    private boolean mustWrite;
    private boolean exists;

    public FileImpl() throws RemoteException {
    }

    public String getAbsolutePath() throws RemoteException {
        return file.getAbsolutePath();
    }

    /**
     * all our binding stuff. reads attributes, builds the filename, checks it, updates attributes
     *
     * @throws RemoteException
     * @throws SmartFrogRuntimeException
     */
    public void bind() throws RemoteException, SmartFrogRuntimeException {

        PlatformHelper platform= PlatformHelper.getLocalPlatform();
        String filename = sfResolve(varFilename,(String)null,true);
        filename=platform.convertFilename(filename);
        String dir= sfResolve(varDir, (String) null, false);
        if(dir!=null) {
            dir= platform.convertFilename(dir);
            File parent=new File(dir);
            file=new File(parent,filename);
        } else {
            file=new File(filename);
        }

        mustExist =getBool(varMustExist,false,false);
        mustRead = getBool(varMustWrite, false, false);
        mustWrite = getBool(varMustRead, false, false);

        exists = file.exists();
        boolean isDirectory;
        boolean isFile;
        boolean isEmpty;
        long timestamp;
        long length;
        if(exists) {
            isDirectory = file.isDirectory();
            isFile = file.isFile();
            timestamp = file.lastModified();
            length = file.length();
        } else {
            isDirectory=isFile=false;
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

        String path= getAbsolutePath();
        sfReplaceAttribute(varAbsolutePath,path);
        String uri=file.toURI().toString();
        sfReplaceAttribute(varURI, uri);
        String name=file.getName();
        sfReplaceAttribute(varShortname,name);

    }

    private void setAttribute(String attr, boolean flag) throws SmartFrogRuntimeException, RemoteException {
        sfReplaceAttribute(attr,new Boolean(flag));
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
        bind();
    }

    /**
     * Returns the string of the remote reference if this primitive was
     * exported, the superclass toString if not.
     *
     * @return string form for this component
     */
    public String toString() {
        return file!=null?file.getAbsolutePath() : "uninitialized file";
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
        if(mustExist && !file.exists()) {
            throw new SmartFrogLivenessException("File "+file.getAbsolutePath()+" does not exist");
        }
        if(mustRead && !file.canRead()) {
            throw new SmartFrogLivenessException("File " + file.getAbsolutePath() + " is not readable");
        }
        if (mustWrite && !file.canWrite()) {
            throw new SmartFrogLivenessException("File " + file.getAbsolutePath() + " is not writeable");
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
     *                  and absolute. Can be null. No used when mandatory is true
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
}
