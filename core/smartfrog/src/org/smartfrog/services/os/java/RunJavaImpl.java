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
package org.smartfrog.services.os.java;

import org.smartfrog.services.os.runshell.RunShellImpl;
import org.smartfrog.services.filesystem.FileIntf;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.utils.PlatformHelper;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * created 21-May-2004 17:23:22
 */

public class RunJavaImpl extends RunShellImpl implements RunJava {

    private final static PlatformHelper platform = PlatformHelper.getLocalPlatform();;
    /**
     * a log
     */
    private Log log;

    public RunJavaImpl() throws RemoteException {
    }

    /**
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  deployment failure
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        log = sfGetApplicationLog();
    }

    /**
     * Reads SF description = initial configuration.
     * Override this to read/set properties before we read ours, but remember to call
     * the superclass afterwards
     */
    protected void readSFAttributes() throws SmartFrogException, RemoteException {
        final boolean debugEnabled = log.isDebugEnabled();
        Vector args=new Vector();
        //get classname and classpath and verify that one is defined
        String classname=null;
        String jar=null;
        classname=sfResolve(ATTR_CLASSNAME,classname,false);
        jar= platform.convertFilename(sfResolve(ATTR_JARFILE, jar, false));

        Vector environment = (Vector) sfResolve(ATTR_ENVIRONMENT, (Vector) null, false);
        Vector flatEnv = flatten(environment, null, "=", null);

        Vector jvmArgs=(Vector) sfResolve(ATTR_JVM_ARGS,(Vector)null,false);

        Vector sysProperties=(Vector)sfResolve(ATTR_SYSPROPERTIES, (Vector) null, false);
        Vector flatSysProperties = flatten(sysProperties, "-D", "=", "");
        Boolean assertions=(Boolean) sfResolve(ATTR_ASSERTIONS,(Boolean)null,false);
        Boolean sysAssertions = (Boolean) sfResolve(ATTR_SYSTEMASSERTIONS, (Boolean) null, false);
        Vector arguments=sfResolve(ATTR_ASSERTIONS, (Vector) null, false);


        addArgs(args, flatSysProperties);
        addArgs(args, jvmArgs);

        //assertions
        if(assertions!=null && assertions.booleanValue()) {
            log.debug("enable application assertions");
            args.add("-ea:...");
        }
        if (sysAssertions != null && sysAssertions.booleanValue()) {
            log.debug("enable system assertions");
            args.add("-esa");
        }

        //classpath setup
        String classpath=buildClasspath(false);
        if ( debugEnabled ) {
            log.debug("classpath=" + classpath!=null?classpath:"(empty)");
        }

        if(classpath!=null) {
            args.add("-classpath");
            args.add(classpath);
        }

        //endorsed dirs
        String endorsedDirs=buildEndorsedDirs();
        if(endorsedDirs!=null) {
            if(debugEnabled) {
                log.debug("endorsed dirs="+ endorsedDirs);
            }
            args.add("-Djava.endorsed.dirs="+endorsedDirs);
        }

        //set the classname or jar to run
        if (jar == null && classname == null) {
            throw new SmartFrogInitException("One of " + ATTR_CLASSNAME + " and " + ATTR_JARFILE + " must be supplied");
        }
        if(classname!=null) {
            if ( debugEnabled ) {
                log.debug("classname=" + classname);
            }
            args.add(classname);
        } else {
            if ( debugEnabled ) {
                log.debug("running jar =" + jar);
            }
            args.add("-jar");
            args.add(jar);
        }

        //add any command arguments
        addArgs(args,arguments);


        //now patch our attributes, ready for our parent class
        sfReplaceAttribute(varEnvProp, flatEnv);
        sfReplaceAttribute(varShellArguments, args);

        //and verify that the shell command is not empty
        String javacmd=sfResolve(varShellCommand,(String)null,true);
        if ( debugEnabled ) {
            log.debug("final command ="+javacmd);
        }

        //now invoke our superclass; this relies on 
        super.readSFAttributes();
    }

    /**
     * append one collection to another, if the appendee is not empty
     * @param v one to append to
     * @param v2 one to append, can be null
     */
    private void addArgs(Collection v, Collection v2) {
        if(v2!=null) {
            v.addAll(v2);
        }
    }


    /**
     * take a vector of name value pairs like [["a",true],["b",3]] and create something like
     * ["a=true","b=3"] with configurable prefix, joiner and suffix strings
     * @param source
     * @param prefix
     * @param joiner
     * @param suffix
     * @return a merged/flattened array or null if an empty list came in
     * @throws SmartFrogInitException
     */
    private Vector flatten(List source,String prefix,String joiner,String suffix) throws SmartFrogInitException {
        if(source==null) {
            return null;
        }
        if(prefix==null) {
            prefix="";
        }
        if(joiner==null) {
            joiner="";
        }
        if(suffix==null) {
            suffix="";
        }
        Vector results=new Vector(source.size());
        Iterator it=source.listIterator();
        while (it.hasNext()) {
            Vector subvector= (Vector) it.next();
            if(subvector.size()!=2) {
                throw new SmartFrogInitException("Wrong number of list elements");
            }
            Iterator subit=subvector.iterator();
            String key = (String) subit.next();
            //take any value and stringify it -we dont care about its underlying type
            Object valueObj=(Object) subit.next();
            String value;
            value=valueObj.toString();
            String entry=prefix+key+joiner+value+suffix;
            results.add(entry);
        }
        return results;
    }

    /**
     * recursive support for classpaths, lets us cross reference more easily.
     * @param mandatory
     * @return
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    private String buildClasspath(boolean mandatory) throws SmartFrogResolutionException, RemoteException {
        Vector classpathV=(Vector)sfResolve(ATTR_CLASSPATH,(Vector)null,mandatory);
        if(classpathV==null) {
            return null;
        }
        String path=makePath(classpathV);
        return path;
    }

    /**
     * build the endorsed dirs system
     * @return
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    private String buildEndorsedDirs() throws SmartFrogResolutionException, RemoteException {
        final boolean debugEnabled = log.isDebugEnabled();
        log.debug("building endorsed directory parameter");
        Vector pathV = (Vector) sfResolve(ATTR_ENDORSED_DIRS, (Vector) null, false);
        if(pathV==null) {
            if(debugEnabled ) {
                log.debug(ATTR_ENDORSED_DIRS+" is unset");
            }
            return null;
        }
        Vector classpathFlat=RunJavaUtils.recursivelyFlatten(pathV);
        Iterator entries=classpathFlat.iterator();
        StringBuffer result= new StringBuffer();
        while (entries.hasNext()) {
            Object entry = (Object) entries.next();
            String dirname=entry.toString();
            dirname = platform.convertFilename(dirname);
            if(debugEnabled) {
                log.debug("endorsed dir/file = "+dirname);
            }
            //turn this into a directory if it is a file
            File file=new File(dirname);
            if(file.exists() && file.isFile()) {
                String parentDir=file.getParent();
                if(parentDir!=null) {
                    if ( debugEnabled ) {
                        log.debug("extracted parent dir "+parentDir);
                    }
                    dirname=parentDir;
                } else {
                    //there is no parent dir. what to do?
                    log.warn("No parent directory for "+dirname
                            +" cannot add it to the endorsed directory list");
                }
            }
            result.append(dirname);
            result.append(platform.getPathSeparator());
        }
        String path = result.toString();
        return path;
    }

    /**
     * turn a path vector into a flat path string; use the given separator
     * @param pathVector
     * @return
     */
    private String makePath(Vector pathVector) throws RemoteException {
        Vector classpathFlat=RunJavaUtils.recursivelyFlatten(pathVector);
        Iterator entries=classpathFlat.iterator();
        StringBuffer result= new StringBuffer();
        while (entries.hasNext()) {
            Object entry = (Object) entries.next();
            if(entry instanceof FileIntf) {
                FileIntf file=(FileIntf) entry;
                appendOnePathEntry(result,file.getAbsolutePath());
            } else if (entry instanceof JavaPackage) {
                JavaPackage jpackage=(JavaPackage) entry;
                //jpackage.

            } else if (entry instanceof String) {
                String file;
                file = platform.convertFilename((String)entry);
                appendOnePathEntry(result,file);
            }
        }
        return result.toString();
    }

    private void appendOnePathEntry(StringBuffer result, String entry) {
        result.append(entry);
        result.append(platform.getPathSeparator());
    }

}
