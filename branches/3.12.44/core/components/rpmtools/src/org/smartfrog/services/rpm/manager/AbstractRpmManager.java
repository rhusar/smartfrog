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
package org.smartfrog.services.rpm.manager;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.workflow.eventbus.EventPrimImpl;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created 14-Apr-2008 17:11:58
 */

public class AbstractRpmManager extends EventPrimImpl implements RpmManager, Iterable<RpmFile> {

    private ArrayList<RpmFile> rpms = new ArrayList<RpmFile>(1);
    /**
     * Install the RPMs on startup
     */
    private boolean install;

    /**
     * uninstall the RPMs on termination?
     */
    private boolean uninstallOnTermination;

    /**
     * uninstall the RPMs on termination?
     */
    private boolean uninstallOnStartup;

    /**
     * Ping for managed files during a liveness check?
     */
    private boolean probeOnLiveness;

    /**
     * Ping for managed files during a liveness check?
     */
    private boolean probeOnStartup;

    /**
     * Should we apply in bulk? That is, group apply everything in one go?
     */
    private boolean bulkOperation;

    /**
     * How to handle a failure to install
     */
    private boolean failOnInstallError;

    /**
     * How to handle a failure to uninstall. Only relevent in a startup uninstall
     */
    private boolean failOnUninstallError;

    /**
     * Should scripts be skipped during installation?
     */
    private boolean installSkipScripts;

    /**
     * Should scripts be skipped during uninstallation?
     */
    private boolean uninstallSkipScripts;

    /**
     * Should dependencies be ignored during uninstallation?
     */
    private boolean uninstallIgnoreDependencies;

    public AbstractRpmManager() throws RemoteException {
    }


    /**
     * start up
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        probeOnStartup = sfResolve(ATTR_PROBE_ON_STARTUP, true, true);
        probeOnLiveness = sfResolve(ATTR_PROBE_ON_LIVENESS, true, true);
        install = sfResolve(ATTR_INSTALL, true, true);
        uninstallOnTermination = sfResolve(ATTR_UNINSTALL_ON_TERMINATION, true, true);
        uninstallOnStartup = sfResolve(ATTR_UNINSTALL_ON_STARTUP, true, true);
        uninstallIgnoreDependencies = sfResolve(ATTR_UNINSTALL_IGNORE_DEPENDENCIES, true, true);
        uninstallSkipScripts = sfResolve(ATTR_UNINSTALL_NO_SCRIPTS, true, true);
        if (uninstallOnStartup) {
            uninstallOnStartup();
        }
        if (install) {
            installOnStartup();
        }
        if (probeOnStartup) {
            probeOnStartup();
        }
        triggerWorkflowTerminationInStartup();
    }

    /**
     * Deregisters from all current registrations.
     *
     * @param status Record having termination details of the component
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (uninstallOnTermination) {
            try {
                uninstallOnTermination();
            } catch (SmartFrogException e) {
                sfLog().error("When terminating: " + e, e);
            } catch (RemoteException e) {
                sfLog().error("When terminating: " + e, e);
            }
        }
    }


    /**
     * Override point: place to implement something
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    protected void installOnStartup() throws SmartFrogException, RemoteException {
    }


    /**
     * Override point: place to implement something
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    protected void uninstallOnStartup() throws SmartFrogException, RemoteException {
    }

    /**
     * Override point: place to implement something
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    protected void uninstallOnTermination() throws SmartFrogException, RemoteException {
    }

    /**
     * Override point: probe on startup. Any installation will already have executed
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    protected void probeOnStartup() throws SmartFrogException, RemoteException {
        probeAllFiles();
    }

    /**
     * Override point
     */
    protected void triggerWorkflowTerminationInStartup() {
        //trigger workflow termination if requested
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null, null, null, null);
    }


    /**
     * Returns an iterator over the rpms
     *
     * @return an Iterator.
     */
    public Iterator<RpmFile> iterator() {
        return rpms.listIterator();
    }

    /**
     * Add another file to the list of RPMs that need managing
     *
     * @param rpm the RPM to manage
     *
     * @throws SmartFrogException if unable to manage this file
     * @throws RemoteException network problems
     */
    public void manage(RpmFile rpm) throws SmartFrogException, RemoteException {
        rpms.add(rpm);
        onNewFileAdded(rpm);
    }

    /**
     * Override point: notification of a new artifact added, it is already on the rpms list at this point.
     *
     * @param rpm the newly added RPM
     */
    protected void onNewFileAdded(RpmFile rpm) {

    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     *
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException for consistency with the {@link Liveness} interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (probeOnLiveness) {
            probeOnLiveness();
        }
    }

    private void probeOnLiveness() throws SmartFrogLivenessException, RemoteException {
        probeAllFiles();
    }

    protected void probeAllFiles() throws SmartFrogLivenessException, RemoteException {
        for (RpmFile rpm : this) {
            probe(rpm);
        }
    }

    /**
     * Override point: ping the file
     *
     * @param rpm the file to ping
     *
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException for consistency with the {@link Liveness} interface
     */
    protected void probe(RpmFile rpm) throws SmartFrogLivenessException, RemoteException {

    }

    public boolean isProbeOnLiveness() {
        return probeOnLiveness;
    }

    public boolean isProbeOnStartup() {
        return probeOnStartup;
    }
}