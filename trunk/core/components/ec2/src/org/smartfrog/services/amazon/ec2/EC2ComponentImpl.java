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
package org.smartfrog.services.amazon.ec2;

import com.xerox.amazonws.ec2.Jec2;
import org.smartfrog.services.passwords.PasswordHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created 25-Mar-2008 13:36:29
 */

public class EC2ComponentImpl extends PrimImpl implements EC2Component {

    private Jec2 ec2binding;
    private String id;
    private String key;
    protected static final ArrayList<String> EMPTY_ARGUMENTS = new ArrayList<String>();
    private WorkflowThread worker;

    public EC2ComponentImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        id = sfResolve(ATTR_ID, "", true);
        key = PasswordHelper.resolvePassword(this, ATTR_KEY, true);
        ec2binding = bindToEC2();
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        terminateWorker();
    }

    /**
     * Terminate any worker thread; synchronized. After the request is made the
     * worker field is always null.
     */
    protected void terminateWorker() {
        WorkflowThread thread;
        synchronized (this) {
            thread = worker;
            worker = null;
        }
        SmartFrogThread.requestThreadTermination(thread);
    }

    /**
     * Get the current Jec2 binding
     *
     * @return the current binding
     */
    public Jec2 getEc2binding() {
        return ec2binding;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    /**
     * Create a new EC2 binding
     *
     * @return a new binding with the current key/password
     */
    protected Jec2 bindToEC2() {
        return new Jec2(id, key);
    }


    public WorkflowThread getWorker() {
        return worker;
    }

    public void setWorker(WorkflowThread worker) {
        this.worker = worker;
    }
}
