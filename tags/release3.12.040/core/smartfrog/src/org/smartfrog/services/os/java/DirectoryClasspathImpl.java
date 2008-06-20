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

    /**
     * construct
     * @throws RemoteException if the parent does
     */
    public DirectoryClasspathImpl() throws RemoteException {
    }





    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws SmartFrogException
     *                                  error while deploying
     * @throws RemoteException In case of network/rmi error
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
     * @throws SmartFrogException
     *                                  failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        if (!isEarly()) {
            bind();
        }
        maybeStartTerminator();
    }


    /**
     * Is the compoenent set for early binding
     * @return
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    private boolean isEarly() throws SmartFrogResolutionException,
            RemoteException {
        return sfResolve(ATTR_EARLY, false, true);
    }


    /**
     * bind to the base directory
     * @throws SmartFrogException if the bise directory is missing or not a directory
     * @throws RemoteException on network trouble
     */
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
