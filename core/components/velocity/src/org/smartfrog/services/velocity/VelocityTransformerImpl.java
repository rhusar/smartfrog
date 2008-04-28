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
package org.smartfrog.services.velocity;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.services.utils.setproperty.PropertiesUtils;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.utils.WorkflowThread;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.rmi.RemoteException;
import java.util.Properties;
import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

/**
 * Created 24-Apr-2008 14:26:28
 */

public class VelocityTransformerImpl extends PrimImpl {

    private static final Reference REF_PROPERTYLIST = new Reference("propertyList");
    private static final Reference REF_PROPERTIES = new Reference("properties");
    private static final Reference REF_ENGINE_PROPERTIES = new Reference("engineProperties");
    private Properties engineProperties;
    private Properties contextProperties;
    private VelocityEngine engine;
    /**
     * the template to use
     */
    Template template;

    WorkflowThread worker;
    public static final String ERROR_NO_TEMPLATE = "Failed to load velocity template ";
    public static final String ERROR_NO_VELOCITY = "Failed to start velocity";
    private File destFile;


    public VelocityTransformerImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        engineProperties = PropertiesUtils.resolveAndBuild(this,REF_ENGINE_PROPERTIES,true);
        contextProperties = PropertiesUtils.resolveAndBuild(this, REF_PROPERTIES, true);
        Properties listprops = ListUtils.resolveProperties(this, REF_PROPERTYLIST, true);
        PropertiesUtils.concat(contextProperties, listprops, true);
        try {
            engine = new VelocityEngine();
            engine.init(engineProperties);
        } catch (Exception e) {
            throw SmartFrogLifecycleException.forward(ERROR_NO_VELOCITY, e, this);
        }

        String templateName=sfResolve("template","",true);
        try {
            template=engine.getTemplate(templateName);
        } catch (Exception e) {
            throw SmartFrogLifecycleException.forward(ERROR_NO_TEMPLATE +templateName, e, this);
        }

        destFile = FileSystem.lookupAbsoluteFile(this, "destFile", null, null, true, null);


        worker=new VelocityWorker();
        worker.start();
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        WorkflowThread w = worker;
        worker = null;
        SmartFrogThread.requestThreadTermination(w);
    }

    /**
     * The async worker thread
     */
    private class VelocityWorker extends WorkflowThread {

        private VelocityContext context;

        /**
         * Creates a new worker
         */
        private VelocityWorker() {
            super(VelocityTransformerImpl.this, true, new Object());
        }

        /**
         * If this thread was constructed using a separate {@link Runnable} run object, then that <code>Runnable</code>
         * object's <code>run</code> method is called; otherwise, this method does nothing and returns. <p> Subclasses of
         * <code>Thread</code> should override this method.
         *
         * @throws Throwable if anything went wrong
         */
        @Override
        public void execute() throws Throwable {
            context = new VelocityContext(contextProperties);
            BufferedWriter writer = null;
            try {
                OutputStream output = new FileOutputStream(destFile);
                writer = new BufferedWriter(new OutputStreamWriter(output));
                template.merge(context, writer);
                writer.flush();
                writer.close();
                writer = null;
            } finally {
                FileSystem.close(writer);
            }

        }
    }

}
