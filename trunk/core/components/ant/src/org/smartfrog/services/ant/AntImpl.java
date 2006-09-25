/** (C) Copyright Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.ant;

import org.apache.tools.ant.Task;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.util.Iterator;

/**
 */
public class AntImpl extends PrimImpl implements Prim, Ant, Runnable {
    private AntProject antProject;
    private AntRuntime runtime;
    private SmartFrogException caughtException;
    private boolean exitAntNow=false;


    /**
     * Constructor for the Ant object.
     *
     * @throws RemoteException In case of network/rmi error
     */
    public AntImpl() throws RemoteException {
    }

    // LifeCycle methods

    /**
     * @throws SmartFrogException In case of error in deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        LogSF log = sfLog();
        antProject = new AntProject(this, log);
        runtime = new AntRuntime(this);
        sfReplaceAttribute(ATTR_RUNTIME, runtime);

        sfLog().info("end sfDeploy");

    }


    private void executeNestedAntTasks() throws RemoteException, SmartFrogException {
        Object attribute = null;
        Object value = null;
        Iterator a = this.sfAttributes();
        try {
            for (Iterator i = this.sfValues(); i.hasNext() && !exitAntNow;) {
                attribute = a.next();
                value = i.next();
                if (value instanceof ComponentDescription) {
                    try {
                        if (((ComponentDescription) value).sfContainsAttribute(ATTR_TASK_NAME)) {
                            Task task = antProject.getTask((String) attribute, (ComponentDescription) value);
                            task.execute();
                        } else if (((ComponentDescription) value).sfContainsAttribute(ATTR_ANT_ELEMENT)) {
                            Object element = antProject.getElement((String) attribute, (ComponentDescription) value);
                        } else {
                            //System.out.println("@todo: something with attribute: "+ attribute + " "+value+";");
                        }
                    } catch (Exception ex) {
                        Throwable thr = ex;
                        if (thr instanceof java.lang.reflect.InvocationTargetException) {
                            thr = ex.getCause();
                        }
                        throw SmartFrogException.forward("Error executing: " + attribute, thr);
                    }
                }
            }
        } finally {
            //set the static properties after we finish
            runtime.setStaticProperties(antProject.getProject().getProperties());
        }

    }

    /**
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        boolean asynch = false;
        if ( ((boolean) sfResolve(ATR_ASYNCH, asynch , asynch))) {
           new Thread(this).run();
        } else {
            exec();
        }

    }

    /**
     * @param t TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord t) {
        shutDownAnt();
        super.sfTerminateWith(t);
    }

    // End LifeCycle methods

    // Read Attributes from description


    // Main component action methods

    /**
     * Get a property from ant.
     * @param name
     * @return the property; or null for no match or no ant
     */
    public synchronized String getAntProperty(String name) {
        if(antProject!=null) {
            return antProject.getProject().getProperty(name);
        }
        return null;
    }

    /**
     * shut ant down; get the properties and set them as static things under the runtime
     * add ant.exit.code
     */
    private synchronized void shutDownAnt() {
        //code to end ant
        if (antProject == null) {
            return;
        }
        exitAntNow=true;
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        exec();
    }

    /**
     * Executes Ant Tasks and triggers the detach and/or termination of a component
     * according to the values of the boolean attributes 'sfShouldDetach', 'sfShouldTerminate'
     * and 'sfShouldTerminateQuietly'
     */
    private void exec() {
        try {
            executeNestedAntTasks();
        } catch (RemoteException e) {
            caughtException=SmartFrogException.forward(e);
        } catch (SmartFrogException e) {
            caughtException=e;
        }
        //cleanup time
        ComponentHelper helper = new ComponentHelper(this);
        helper.sfSelfDetachAndOrTerminate(
                null,
                "end of ant tasks",
                null,
                caughtException
        );
    }

    /**
     * Liveness call in to check if this component is still alive. This method
     * can be overriden to check other state of a component. An example is
     * Compound where all children of the compound are checked. This basic
     * check updates the liveness count if the ping came from its parent.
     * Otherwise (if source non-null) the liveness count is decreased by the
     * sfLivenessFactor attribute. If the count ever reaches 0 liveness
     * failure on tha parent has occurred and sfLivenessFailure is called with
     * source this, and target parent. Note: the sfLivenessCount must be
     * decreased AFTER doing the test to correctly count the number of ping
     * opportunities that remain before invoking sfLivenessFailure. If done
     * before then the number of missing pings is reduced by one. E.g. if
     * sfLivenessFactor is 1 then a sfPing from the parent sets
     * sfLivenessCount to 1. The sfPing from a non-parent would reduce the
     * count to 0 and immediately fail.
     *
     * @param source source of call
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *                                  component is terminated
     * @throws java.rmi.RemoteException for consistency with the {@link org.smartfrog.sfcore.prim.Liveness} interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if(caughtException!=null) {
            throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(caughtException);
        }
    }
}
