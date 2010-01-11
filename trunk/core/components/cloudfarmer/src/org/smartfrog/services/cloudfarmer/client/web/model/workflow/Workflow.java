/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.cloudfarmer.client.web.model.workflow;

import org.smartfrog.services.cloudfarmer.client.common.AbstractEndpoint;
import org.smartfrog.services.cloudfarmer.client.common.BaseRemoteDaemon;
import org.smartfrog.services.cloudfarmer.client.web.exceptions.UnboundException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This represents a workflow on the cluster
 */
public class Workflow extends AbstractEndpoint {

    private String name;
    private long started;
    private long finished;
    private boolean successful;
    private String description;
    private String exitText;
    private String classname;
    private Prim remoteApplication;
    private BaseRemoteDaemon owner;

    public Workflow() {
    }

    public Workflow(String baseURL) {
        super(baseURL);
    }

    public Workflow(BaseRemoteDaemon owner, Object key, Prim prim) throws RemoteException, SmartFrogException {
        setOwner(owner);
        setName(key.toString());
        setRemoteApplication(prim);
        classname = prim.sfResolveHere("sfClass").toString();
        description = prim.sfResolve("description", "", false);
    }

    public BaseRemoteDaemon getOwner() {
        return owner;
    }

    void setOwner(BaseRemoteDaemon owner) {
        this.owner = owner;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getClassname() {
        return classname;
    }


    /**
     * @return the started
     */
    public long getStarted() {
        return started;
    }

    /**
     * @param started the started to set
     */
    public void setStarted(long started) {
        this.started = started;
    }

    /**
     * @return the finished
     */
    public long getFinished() {
        return finished;
    }

    /**
     * @param finished the finished to set
     */
    public void setFinished(long finished) {
        this.finished = finished;
    }

    /**
     * @return the successful
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * @param successful the successful to set
     */
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * @return the exitText
     */
    public String getExitText() {
        return exitText;
    }

    /**
     * @param exitText the exitText to set
     */
    public void setExitText(String exitText) {
        this.exitText = exitText;
    }

    private void setRemoteApplication(Prim remoteApplication) {
        this.remoteApplication = remoteApplication;
    }


    public Prim getRemoteApplication() {
        return remoteApplication;
    }

    protected void failIfUnbound() throws IOException {
        if (remoteApplication == null) {
            throw new UnboundException("The workflow is not bound to a remote application");
        }
    }

    /**
     * Kill the job
     *
     * @param normal      set to indicate a normal termination
     * @param exitMessage exit text
     * @throws IOException network problems
     */
    public synchronized void terminate(boolean normal, String exitMessage) throws IOException {
        if (remoteApplication != null) {
            TerminationRecord status;
            status = new TerminationRecord(normal ? TerminationRecord.NORMAL : TerminationRecord.ABNORMAL,
                    exitMessage,
                    null);
            remoteApplication.sfTerminate(status);
            remoteApplication = null;
        }

    }

    /**
     * Dump the deployed workflow to text
     *
     * @return the dumped application string
     * @throws IOException        network problems
     * @throws SmartFrogException SmartFrog problems
     */
    public String dump() throws IOException, SmartFrogException {
        failIfUnbound();
        ComponentDescription diagnostics = remoteApplication.sfDiagnosticsReport();
        return diagnostics.toString();
    }

    /**
     * Ping the component for health
     *
     * @throws IOException        network problems
     * @throws SmartFrogException SmartFrog problems
     */
    public void ping() throws IOException, SmartFrogException {
        failIfUnbound();
        remoteApplication.sfPing(this);
    }

    @Override
    public String toString() {
        return "Application " + name +
                (owner != null ?
                        (" @ " + owner.getHostname()) : "");
    }


}
