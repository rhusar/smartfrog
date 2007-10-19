package org.smartfrog.services.os.java;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.io.File;
import java.io.FileFilter;
import java.rmi.RemoteException;


/**
 */
public class DirectoryClasspathImpl extends AbstractClasspathImpl implements DirectoryClasspath {
    public static final String ERROR_NO_DIRECTORY = "No directory ";
    public static final String ERROR_NOT_A_DIRECTORY = "Not a directory ";

    public DirectoryClasspathImpl() throws RemoteException {
    }





    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
        if (isEarly()) {
            bind();
        }
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        if (!isEarly()) {
            bind();
        }
        maybeStartTerminator();
    }


    private boolean isEarly() throws SmartFrogResolutionException,
            RemoteException {
        return sfResolve(ATTR_EARLY, false, true);
    }


    private void bind() throws SmartFrogException, RemoteException {
        String dir= FileSystem.lookupAbsolutePath(this,ATTR_DIRECTORY,null,null,true,null);
        File baseDir=new File(dir);
        if(!baseDir.exists()) {
            throw new SmartFrogDeploymentException(ERROR_NO_DIRECTORY +baseDir);
        }
        if(!baseDir.isDirectory()) {
            throw new SmartFrogDeploymentException(ERROR_NOT_A_DIRECTORY + baseDir);
        }
        File[] files = baseDir.listFiles(new JarFilter());
        setClasspathAttributes(files);
    }

    /**
     * Filter out all but jar files, case-sensitive on all platforms
     */
    private static class JarFilter implements FileFilter {
        /**
         * Tests whether or not the specified abstract pathname should be included
         * in a pathname list.
         *
         * @param pathname The abstract pathname to be tested
         *
         * @return <code>true</code> if and only if <code>pathname</code> should be
         *         included
         */
        public boolean accept(File pathname) {
            String name = pathname.getName();
            return name.endsWith(".jar");
        }
    }
}
