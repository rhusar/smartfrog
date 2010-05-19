/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www.bulkio.client;

import org.smartfrog.services.logging.jcl.front.CommonsLogFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.WorkerThreadPrimImpl;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * Created 18-May-2010 12:08:59
 */

public class BulkIOClientImpl extends WorkerThreadPrimImpl implements BulkIOClient {

    public BulkIOClientImpl() throws RemoteException {
    }

    /**
     * Instantiate and start a bulk IO operation
     *
     * @throws SmartFrogException SF problems
     * @throws RemoteException    network problems
     */
    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        BulkIOWorkerThread ioworker = new BulkIOWorkerThread();
        ioworker.start();
    }

    /**
     * Ask for the thread to die
     *
     * @param workflowThread a non-null thread
     */
    @Override
    protected void terminateWorkerThread(WorkflowThread workflowThread) {
        BulkIOWorkerThread ioWorker = (BulkIOWorkerThread) workflowThread;
        ioWorker.interrupt();
        SmartFrogThread.requestThreadTermination(workflowThread);
    }

    /**
     * The worker
     */
    class BulkIOWorkerThread extends WorkflowThread {
        private AbstractBulkIOClient ioclient;

        /**
         * Create the worker; includes reading in all parameters
         *
         * @throws SmartFrogException resolution/instantiation problems
         * @throws RemoteException    network problems
         */
        BulkIOWorkerThread() throws SmartFrogException, RemoteException {
            super(BulkIOClientImpl.this, true);
            String ioclass = sfResolve(ATTR_IOCLASS, "", true);
            try {
                Class ioclassFactory = SFClassLoader.forName(ioclass);
                ioclient = (AbstractBulkIOClient) ioclassFactory.newInstance();
            } catch (Throwable e) {
                throw new SmartFrogResolutionException("Failed to instantiate a AbstractBulkIOClient instance "
                        + "from the classname " + ioclass + ": " + e,
                        e,
                        BulkIOClientImpl.this);
            }
            ioclient.setLog(CommonsLogFactory.createInstance(sfLog()));
            ioclient.size = sfResolve(ATTR_SIZE, 0L, true);
            ioclient.connectTimeout = sfResolve(ATTR_CONNECT_TIMEOUT, 0, true);
            ioclient.chunked = sfResolve(ATTR_CHUNKED, true, true);
            ioclient.chunkLength = sfResolve(ATTR_CHUNK_LENGTH, 0, true);
            ioclient.operation = sfResolve(ATTR_OPERATION, "", true);
            ioclient.useFormUpload = sfResolve(ATTR_USE_FORM_UPLOAD, true, true);
            ioclient.format = sfResolve(ATTR_FORMAT, "", true);
            String targetURLpath = sfResolve(ATTR_URL, "", true);

            try {
                ioclient.setUrl(new URL(targetURLpath));
            } catch (MalformedURLException e) {
                throw new SmartFrogResolutionException("Bad URL \"" + targetURLpath + "\" : " + e,
                        e,
                        BulkIOClientImpl.this);
            }
        }

        /**
         * do the operation
         *
         * @throws Throwable
         */
        @Override
        public void execute() throws Throwable {
            try {
                ioclient.execute();
            } catch (IOException e) {
                sfLog().error("Failed to execute " + ioclient + ": " + e, e);
                throw e;
            }
        }

        /**
         * Interrupt the worker
         */
        public void interrupt() {
            ioclient.interrupt();
        }
    }
}
