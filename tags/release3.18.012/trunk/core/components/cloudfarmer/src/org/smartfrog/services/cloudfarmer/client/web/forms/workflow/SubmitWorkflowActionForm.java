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

package org.smartfrog.services.cloudfarmer.client.web.forms.workflow;

import org.smartfrog.services.cloudfarmer.client.web.model.RemoteDaemon;
import org.smartfrog.services.cloudfarmer.client.web.model.workflow.Workflow;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;

/**
 * Submit a full workflow
 */
public class SubmitWorkflowActionForm extends AbstractQueueJobActionForm {


    private static final long serialVersionUID = 1405460243288986738L;

    public SubmitWorkflowActionForm() {
        setFileOrTextRequired(true);
    }

    /**
     * Call this to queue the job. subclasses should call the type-specific RemoteDaemon.queue method
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws IOException        Network or other I/O issues
     */
    @Override
    public Workflow queue(RemoteDaemon daemon) throws SmartFrogException, IOException {
        return daemon.queue(this);
    }
}
