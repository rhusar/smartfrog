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

package org.smartfrog.services.cloudfarmer.client.web.model.cluster;

import org.smartfrog.services.cloudfarmer.client.web.exceptions.UnimplementedException;
import org.smartfrog.services.cloudfarmer.client.common.AbstractEndpoint;

import java.io.IOException;

/**
 * struts model of a Hadoop cluster
 */
public class HadoopCluster extends AbstractEndpoint {

    private String name;
    private long started;
    private int size;
    private String workflowURL;
    private String dataURL;
    private boolean manuallyAdded;

    public HadoopCluster() {
    }

    public HadoopCluster(String baseURL) {
        super(baseURL);
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
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the workflowURL
     */
    public String getWorkflowURL() {
        return workflowURL;
    }

    /**
     * @param workflowURL the workflowURL to set
     */
    public void setWorkflowURL(String workflowURL) {
        this.workflowURL = workflowURL;
    }

    /**
     * @return the dataURL
     */
    public String getDataURL() {
        return dataURL;
    }

    /**
     * @param dataURL the dataURL to set
     */
    public void setDataURL(String dataURL) {
        this.dataURL = dataURL;
    }

    public boolean isManuallyAdded() {
        return manuallyAdded;
    }

    public void setManuallyAdded(boolean manuallyAdded) {
        this.manuallyAdded = manuallyAdded;
    }


    /**
     * Create a new instance of a workflow server. This is not shared
     *
     * @return a new workflow server
     */
    /*   public WorkflowServer createWorkflowServer() {
        return new WorkflowServer(workflowURL);
    }*/

    /**
     * Add a node
     */
    public void addNode() throws IOException {
        throw new UnimplementedException("TODO");
    }
}
