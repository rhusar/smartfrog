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
package org.smartfrog.services.rpm.dirload;

import org.smartfrog.services.filesystem.FileUsingCompoundImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.Executable;
import org.smartfrog.sfcore.utils.WorkflowThread;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;
import java.io.File;
import java.io.FilenameFilter;

/**
 * Created 08-Dec-2008 16:47:21
 */

public class DirLoaderImpl extends FileUsingCompoundImpl implements DirLoader, Executable {

    private String patternText;
    private Pattern pattern;
    private Prim parent;
    private int onFailure;
    private String application;
    private WorkflowThread worker;

    //private HashMap<File, DeployedDir> directories = newMap();
    private Vector<String> hostList;
    private String[] hosts;

    private HashMap<File, DeployedDir> newMap() {
        return new HashMap<File, DeployedDir>();
    }

    public DirLoaderImpl() throws RemoteException {
    }


    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        //bind to our filesystem
        bind(true, null);
        //read in the settings
        application = sfResolve(ATTR_APPLICATION, application, true);
        patternText = sfResolve(ATTR_PATTERN, patternText, true);
        try {
            pattern = Pattern.compile(patternText);
        } catch (PatternSyntaxException e) {
            throw new SmartFrogDeploymentException("Failed to compile regular expression " + patternText, e);
        }
        parent = sfResolve(ATTR_PARENT, parent, true);
        onFailure = sfResolve(ATTR_ONFAILURE, onFailure, true);
        hostList = ListUtils.resolveStringList(this, new Reference(ATTR_HOSTS), true);
        if (hostList.size() > 0) {
            hosts = hostList.toArray(new String[hostList.size()]);
        }
        worker = new WorkflowThread(this, this, true);
        worker.start();
    }

    /**
     * Execute the operation by scanning and deploying everything
     *
     * @throws Throwable
     */
    public void execute() throws Throwable {
        List<DeployedDir> targets = scan();
        Stack<DeployedDir> deployed = new Stack<DeployedDir>();
        Collections.sort(targets);
        boolean deployTargets = true;
        boolean rollback = false;
        for (DeployedDir target : targets) {
            if (deployTargets) {
                target.getApplicationFile();
                boolean isDeployed = deployOneTarget(target);
                if (!isDeployed) {
                    switch (onFailure) {
                        case FAILURE_HALT:
                            //stop deploying anything else
                            deployTargets = false;
                            break;
                        case FAILURE_ROLLBACK:
                            deployTargets = false;
                            //mark for rollback
                            rollback = true;
                            break;
                        case FAILURE_SKIP:
                        default:
                            break;
                    }
                } else {
                    deployed.push(target);
                }
            }
        }
        //now any triggered rollback
        if (rollback) {
            for (DeployedDir target : deployed) {
                TerminationRecord tr = TerminationRecord.normal("Rolling back", null);
                target.getApplication().sfTerminate(tr);
            }
        }

    }

    /**
     * Deploy one target. If the deployment succeeds, target has its application field set to the Prim
     *
     * @param target target to deploy
     *
     * @return true if the deployment went ahead
     * @throws SmartFrogException deployment problems
     * @throws RemoteException    network
     */
    private boolean deployOneTarget(DeployedDir target) throws SmartFrogException, RemoteException {
        ConfigurationDescriptor desc = new ConfigurationDescriptor();
        desc.setName(target.getName());
        desc.setUrl(target.getURL().toExternalForm());
        desc.setActionType(ConfigurationDescriptor.Action.ACT_DEPLOY);
        if (hosts != null) {
            desc.setHosts(hosts);
        }
        Object result = desc.execute(null);
        if (result instanceof Prim) {
            target.setApplication((Prim) result);
            return true;
        } else {
            //trouble. Retain the value?
            return false;
        }
    }

    private List<DeployedDir> scan() {
        File[] files = getFile().listFiles(new ApplicationFilter(pattern, application));
        List<DeployedDir> targets = new ArrayList(files.length);
        //look through the list for arrivals and departures; queue arrivals for deployment.
        //alternatively: queue everything for ordered deployment and let the deployer decide.
        for (File file : files) {
            DeployedDir dd = new DeployedDir(file, file.getName(), application);
            targets.add(dd);
        }
        return targets;
    }


    /**
     * Filter that only accepts applications
     */
    public class ApplicationFilter implements FilenameFilter {
        private Pattern pattern;
        private String application;

        public ApplicationFilter(Pattern pattern, String application) {
            this.pattern = pattern;
            this.application = application;
        }

        /**
         * A file is accepted if <ol> <li>It is a directory</li> <li>It matches the pattern</li> <li>It contains a file
         * that matches the application string</li> </ol>
         *
         * @param dir  parent dir
         * @param name filename
         * @return true for a match
         */
        public boolean accept(File dir, String name) {
            Matcher matcher = pattern.matcher(name);
            if (!matcher.matches()) {
                return false;
            }
            File entry = new File(dir, name);
            if (!entry.isDirectory()) {
                return false;
            }
            File applicationFile = new File(entry, application);
            return applicationFile.exists() && applicationFile.isFile();
        }
    }


}
