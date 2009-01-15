/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.deploydir;

import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.io.File;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created 10-Mar-2008 14:29:31
 */

public class DeployingDirectoryWatcherImpl extends DirectoryWatcherImpl implements DeployingDirectoryWatcher {

    private String applicationPrefix;
    /*
        private Prim deployParent;
        private ProcessCompound parent;
        private static final String ERROR_BAD_PARENT = "Not a ProcessCompound";
    */
    public static final String ATTR_DEPLOYED_DIRECTORY_INFO = "sfDeployedDirectoryInfo";

    public DeployingDirectoryWatcherImpl() throws RemoteException {
    }

    /**
     * Resolve our attributes. Can be subclassed, in which case the parent should be called
     *
     * @throws SmartFrogException smartfrog trouble
     * @throws RemoteException    network trouble
     */
    protected void resolveAttributes() throws SmartFrogException, RemoteException {
        super.resolveAttributes();
        applicationPrefix = sfResolve(ATTR_APP_PREFIX, "", true);
/*
        deployParent = sfResolve(ATTR_DEPLOY_PARENT, (Prim) null, true);
        if (deployParent == null) {
            parent =
        }
        if (deployParent == null || deployParent instanceof ProcessCompound) {
            parent = (ProcessCompound) deployParent;
        } else {
            throw new SmartFrogDeploymentException(ERROR_BAD_PARENT, parent);
        }
*/
    }

    /**
     * Notify of a directory changed Base class just prints out a notice of what changeda
     *
     * @param current the current directory
     * @param added   added files
     * @param removed removed files
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    network problems
     */
    public void directoryChanged(List<File> current, List<File> added, List<File> removed)
            throws SmartFrogException, RemoteException {
        super.directoryChanged(current, added, removed);
        undeploy(removed);
        deploy(added);
    }

    /**
     * Undeploy the listed applications; do nothing if they are not live
     *
     * @param applications directories of applications
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    network problems
     */
    protected void undeploy(List<File> applications) throws SmartFrogException, RemoteException {

        for (File app : applications) {
            String name = name(app);
            try {
                undeploy(app);
            } catch (SmartFrogException e) {
                sfLog().warn("When terminating " + name + "from " + app, e);
            } catch (RemoteException e) {
                sfLog().warn("When terminating " + name + "from " + app, e);
            }
        }
    }


    /**
     * Undeploy the component
     * @param application
     * @return
     * @throws SmartFrogException
     * @throws RemoteException
     */
    protected boolean undeploy(File application) throws SmartFrogException, RemoteException {
        String name = name(application);
        String subprocess = subprocess(application, name);
        ProcessCompound targetProcess = SFProcess.sfSelectTargetProcess((InetAddress) null, subprocess);
        Object child = null;
        try {
            child = targetProcess.sfResolve(name);
        } catch (SmartFrogResolutionException e) {
            sfLog().info("Not currently deployed: " + name);
            return false;
        } catch (RemoteException e) {
            sfLog().info("Network problems when undeploying: " + name);
            return false;
        }
        if (child instanceof Prim) {
            Prim runningApp = (Prim) child;
            Object dirInfo = runningApp.sfResolve(ATTR_DEPLOYED_DIRECTORY_INFO, false);
            if (dirInfo != null && dirInfo instanceof DirectoryApplication) {
                DirectoryApplication dirApp = (DirectoryApplication) dirInfo;
                sfLog().info("About to undeploy " + dirApp);
                TerminationRecord record = TerminationRecord.normal("Terminated when the source directory was deleted",
                        runningApp.sfCompleteName());
                //terminate...this will notify the parent
                runningApp.sfTerminate(record);
                return true;
            }
        }
        return false;
    }

    /**
     * Determine the subprocess of an application
     *
     * @param application application to work with
     * @return currently, null
     */
    private String subprocess(File application, String name) {
        return null;
    }


    /**
     * Deploy the given applications.
     *
     * @param applications directories of applications
     * @return a list of configuration descriptors
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    network problems
     */
    protected List<ConfigurationDescriptor> deploy(List<File> applications) throws SmartFrogException, RemoteException {
        List<ConfigurationDescriptor> results = new ArrayList<ConfigurationDescriptor>(applications.size());
        for (File dir : applications) {
            String name = name(dir);
            ConfigurationDescriptor cd;
            try {
                DirectoryApplication app = new DirectoryApplication(dir, name);
                cd = deploy(app);
            } catch (Exception e) {
                //IO problems reading the property file
                cd = new ConfigurationDescriptor();
                cd.setResult(ConfigurationDescriptor.Result.FAILED,
                        "Failed to deploy " + name,
                        e);
            }
            results.add(cd);
        }
        return results;
    }

    /**
     * Deploy the given applications.
     *
     * @param application application to deploy
     * @return the outcome
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    network problems
     */
    protected ConfigurationDescriptor deploy(DirectoryApplication application)
            throws SmartFrogException, RemoteException {
        String name = name(application.getDirectory());
        sfLog().info("About to undeploy " + application);
        Context context = new ContextImpl();
        ConfigurationDescriptor action = new ConfigurationDescriptor();
        action.setActionType(ConfigurationDescriptor.Action.ACT_DEPLOY);
        action.setContext(context);
        action.setName(name);
        action.setUrl(application.getResource());
        Object result = action.execute(null);
        if (result instanceof Prim) {
            Prim prim = (Prim) result;
            prim.sfReplaceAttribute(ATTR_DEPLOYED_DIRECTORY_INFO, application);
        }
        return action;
    }


    /**
     * Get the name of the application
     *
     * @param application the application to name
     * @return the calculated name of this application
     */
    protected String name(File application) {
        return applicationPrefix + application.getName();
    }


}
