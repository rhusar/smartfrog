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

import org.smartfrog.services.filesystem.UriIntf;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Vector;

/**
 * created Sep 30, 2004 11:58:59 AM
 */

public class JavaPackageImpl extends PrimImpl implements JavaPackage {
    private Vector requiredClasses;
    private Vector requiredResources;
    private Vector uriClasspathList;
    private Vector classpathList;
    private Vector sources;
    private boolean useCodebase;
    private ComponentHelper helper;
    private String uriClasspath;
    private String classpath;

    /**
     * our log
     */
    private Log log;
    public JavaPackageImpl() throws RemoteException {
    }

    /**
     * Called after instantiation for deployment purposed. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        log = LogFactory.getOwnerLog(this, this);
        helper = new ComponentHelper(this);
        //now read values and set up classpath
        readValuesAndSetUpClasspath();
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
        super.sfStart();
        readValuesAndSetUpClasspath();
        checkForRequiredClasses();
    }

    /**
     * this method does the core of the work
     * <ol>
     * <li>read in the attributes
     * <li>fetch the current codebase if needed
     * <li>flatten the filelist
     * <li>extract URIs from everything
     * <li>merge duplicates
     * <li>create (and set) the output attributes
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void readValuesAndSetUpClasspath() throws SmartFrogException, RemoteException {
        boolean debugEnabled = log.isDebugEnabled();
        sources = sfResolve(ATTR_SOURCE,
                        (Vector) null,
                        false);
        requiredClasses =  sfResolve(ATTR_REQUIRED_CLASSES,
                (Vector) null,
                false);

        requiredResources = sfResolve(ATTR_REQUIRED_RESOURCES,
                (Vector) null,
                false);
        //set an empty class list to null
        if(requiredClasses!=null && requiredClasses.size()==0) {
            requiredClasses=null;
        }
        //now (recursively) flatten the file list.
        sources=RunJavaUtils.recursivelyFlatten(sources);

        if(sources==null) {
            sources =new Vector();
        }
        //now iterate over the (flatter) file list and get stuff from it
        uriClasspathList = new Vector(sources.size());
//        classpathList = new Vector(sources.size());

        //extract the list from the codebase.
        useCodebase = sfResolve(ATTR_USECODEBASE, false, false);
        if(useCodebase) {
            String codebase= helper.getCodebase();
            Vector elements= ListUtils.crack(codebase);
            uriClasspathList.addAll(elements);
        }


        Iterator it = sources.listIterator();
        while (it.hasNext()) {
            Object elt = it.next();
            if(elt instanceof String) {
                uriClasspathList.add(elt);
            } else if(elt instanceof UriIntf) {
                //URIs are resolved to strings
                UriIntf uriIntf=(UriIntf) elt;
                String uriString = uriIntf.getURI().toString();
                uriClasspathList.add(uriString);
            } else if(elt instanceof JavaPackage) {
                //recursive retrieval
                JavaPackage p=(JavaPackage) elt;
                Vector v=p.getUriClasspathList();
                if(v!=null) {
                    uriClasspathList.addAll(v);
                }
            }
        }

        //now merge duplicates
        uriClasspathList=RunJavaUtils.mergeDuplicates(uriClasspathList);

        sfReplaceAttribute(ATTR_URICLASSPATHLIST,uriClasspathList);

        uriClasspath= RunJavaUtils.makeSpaceSeparatedString(uriClasspathList);
        if (debugEnabled) {
            log.debug("classpath =" + uriClasspath);
        }
        sfReplaceAttribute(ATTR_URICLASSPATH,uriClasspath);
    }

    /**
     * given that the uris have been built, now extract the URIs
     *
     */
    private void extractClasspathFromUris() {
        assert uriClasspathList!=null;
    }

    /**
     * look into the classpath and load the classes, then verify that any needed
     * declared required classes are actually present. Throw a liveness exception if not
     * @throws SmartFrogException
     */
    public void checkForRequiredClasses() throws SmartFrogException {
        if(requiredClasses==null && requiredResources==null) {
            return;
        }
        ClassLoader loader=null;
        if (requiredClasses != null) {
            Iterator classes=requiredClasses.iterator() ;
            while (classes.hasNext()) {
                String classname = (String) classes.next();
                checkForClass(loader,classname);
            }
        }
        if (requiredResources != null) {
            Iterator resources = requiredResources.iterator();
            while (resources.hasNext()) {
                String resource = (String) resources.next();
                checkForResource(loader, resource);
            }
        }


    }

    /**
     * test for a resource being present
     * We also search the parent tree, which is potentially wrong
     * @todo: only look in the specified files
     * @param loader
     * @param resource
     */
    private void  checkForResource(ClassLoader loader,String resource)
            throws SmartFrogLivenessException {
        InputStream in=null;
        try {
            in = SFClassLoader.getResourceAsStream(resource,uriClasspath,false);
            /*
            URL url=loader.getResource(resource);
            */
            if(in==null) {
                throw new SmartFrogLivenessException("could not find "+resource
                    +" in "+uriClasspath,this);
            }
        } finally {
            if(in!=null) {
                try {
                    in.close();
                } catch (IOException e) {

                }
            }
        }
    }

    /**
     * test for a class being present.
     * We also search the parent tree, which is potentially wrong
     * @todo: map to a resource and load without side effects
     * @param loader
     * @param classname
     * @throws SmartFrogLivenessException
     */
    private void checkForClass(ClassLoader loader, String classname)
            throws SmartFrogLivenessException  {
        String resource=RunJavaUtils.makeResource(classname);
        checkForResource(loader,resource);
    }

/*
    private ClassLoader getClassloader() {

    }
*/
    /**
     * get the vector of uris
     *
     * @return the classpath as a vector
     */
    public Vector getUriClasspathList() {
        return uriClasspathList;
    }


}
