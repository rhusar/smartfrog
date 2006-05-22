/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.www.cargo;

import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.State;
import org.codehaus.cargo.container.configuration.Configuration;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.www.JavaEnterpriseApplication;
import org.smartfrog.services.www.JavaWebApplication;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.services.www.cargo.delegates.CargoDelegateEarApplication;
import org.smartfrog.services.www.cargo.delegates.CargoDelegateWebApplication;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.security.SFClassLoader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Vector;


/**
 * Cargo component deploys using cargo
 */
public class CargoServerImpl extends PrimImpl implements CargoServer, Runnable {

    private LocalContainer container;
    private Configuration configuration;
    private File dir;
    private String containerClassname;
    private String codebase;
    private Thread thread;
    private boolean started;
    private boolean starting;
    private Log log;


    public CargoServerImpl() throws RemoteException {
    }

    public Log getLog() {
        return log;
    }

    public LocalContainer getContainer() {
        return container;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * deploy a web application. Deploys a web application identified by the
     * component passed as a parameter; a component of arbitrary type but which
     * must have the mandatory attributes identified in {@link
     * JavaWebApplication}; possibly even extra types required by the particular
     * application server.
     *
     * @param webApplication the web application. this must be a component whose
     *                       attributes include the mandatory set of attributes
     *                       defined for a JavaWebApplication component.
     *                       Application-server specific attributes (both
     *                       mandatory and optional) are also permitted
     *
     * @return an entry referring to the application
     *
     * @throws RemoteException    on network trouble
     * @throws SmartFrogException on any other problem
     */
    public JavaWebApplication deployWebApplication(Prim webApplication)
            throws RemoteException, SmartFrogException {
        return new CargoDelegateWebApplication(webApplication, this);
    }

    /**
     * Deploy an EAR file
     *
     * @param enterpriseApplication
     *
     * @return an entry referring to the application
     *
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public JavaEnterpriseApplication deployEnterpriseApplication(Prim enterpriseApplication)
            throws RemoteException, SmartFrogException {
        return new CargoDelegateEarApplication(enterpriseApplication, this);
    }

    /**
     * Deploy a servlet context. This can be initiated with other things.
     * <p/>
     * This should be called from sfDeploy. The servlet is not deployed
     *
     * @param servlet
     *
     * @return a token referring to the application
     *
     * @throws RemoteException    on network trouble
     * @throws SmartFrogException on any other problem
     */
    public ServletContextIntf deployServletContext(Prim servlet)
            throws RemoteException, SmartFrogException {
        throw new SmartFrogException("Servlet contexts are not supported");
    }


    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
        log = LogFactory.getLog(this);
        String dirname = FileSystem.lookupAbsolutePath(this,
                ATTR_HOME,
                null,
                null,
                true,
                null);
        //ConfigurationFactory factory = new DefaultConfigurationFactory();
        String name = sfResolve(ATTR_CONFIGURATION_CLASS, "", true);
        codebase = sfResolve(SmartFrogCoreKeys.SF_CODE_BASE,
                (String) null,
                false);
        configuration = (Configuration) createClassInstance(name);
        dir = new File(dirname);
        containerClassname = sfResolve(ATTR_CONTAINER_CLASS, "", true);
        Class containerClass = loadClass(containerClassname);
        Class signature[] = new Class[1];
        signature[0] = Container.class;
        Object args[] = new Object[1];
        args[0] = configuration;
        container = (LocalContainer) instantiate(containerClass,
                signature,
                args);

        //at this point the container is instantiated

        //set the home
        String home = FileSystem.lookupAbsolutePath(this, ATTR_EXTRA_CLASSPATH,
                null, null, true, null);
        container.setHome(home);

        //monitoring relays to smartfrog
        container.setMonitor(new MonitorToSFLog(log));

        //add any extra classpath
        Vector classes = null;
        classes = sfResolve(ATTR_EXTRA_CLASSPATH, classes, false);
        if (classes != null) {
            final int size = classes.size();
            String[] classArray = new String[size];
            Iterator classesIterator = classes.listIterator();
            for (int i = 0; i < size; i++) {
                Object o = classesIterator.next();
                String pathElement = o.toString();
                File pathElementFile = new File(pathElement);
                if (!pathElementFile.exists()) {
                    throw new SmartFrogDeploymentException(
                            "Path element not found:"
                                    + pathElementFile.getAbsolutePath());
                }
                classArray[i] = pathElementFile.getAbsolutePath();
            }
            container.setExtraClasspath(classArray);
        }


    }


    /**
     * Create a new container
     *
     * @param clazz
     * @param signature
     * @param arguments
     *
     * @return
     *
     * @throws SmartFrogException
     */
    private Object instantiate(Class clazz,
                               Class[] signature,
                               Object[] arguments) throws SmartFrogException {
        Object instance;
        try {
            Constructor constructor = clazz.getConstructor(signature);
            instance = constructor.newInstance(arguments);
        } catch (NoSuchMethodException e) {
            throw SmartFrogException.forward(e);

        } catch (InvocationTargetException e) {
            throw SmartFrogException.forward(e);

        } catch (InstantiationException e) {
            throw SmartFrogException.forward(e);

        } catch (IllegalAccessException e) {
            throw SmartFrogException.forward(e);
        }
        return instance;
    }


    /**
     * Create an instance of a class
     *
     * @param name
     *
     * @return
     *
     * @throws SmartFrogException
     */
    private Object createClassInstance(String name) throws SmartFrogException {
        Object instance;
        Class clazz = loadClass(name);
        Class signature[] = null;
        Object arguments[] = null;
        instance = instantiate(clazz, signature, arguments);
        return instance;
    }

    /**
     * Load a class
     *
     * @param name
     *
     * @return
     *
     * @throws SmartFrogException
     */
    private Class loadClass(String name) throws SmartFrogException {
        Class newclass = null;
        try {
            newclass = SFClassLoader.forName(name, codebase, true);
        } catch (ClassNotFoundException e) {
            throw SmartFrogException.forward(e);
        }
        return newclass;
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        startInNewThread();
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (started) {
            try {
                container.stop();
            } finally {
                started = false;
                container = null;
            }
        }
    }

    /**
     * Liveness call in to check if this component is still alive. This method
     * can be overriden to check other state of a component. An example is
     * Compound where all children of the compound are checked. This basic check
     * updates the liveness count if the ping came from its parent. Otherwise
     * (if source non-null) the liveness count is decreased by the
     * sfLivenessFactor attribute. If the count ever reaches 0 liveness failure
     * on tha parent has occurred and sfLivenessFailure is called with source
     * this, and target parent. Note: the sfLivenessCount must be decreased
     * AFTER doing the test to correctly count the number of ping opportunities
     * that remain before invoking sfLivenessFailure. If done before then the
     * number of missing pings is reduced by one. E.g. if sfLivenessFactor is 1
     * then a sfPing from the parent sets sfLivenessCount to 1. The sfPing from
     * a non-parent would reduce the count to 0 and immediately fail.
     *
     * @param source source of call
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *                                  component is terminated
     * @throws java.rmi.RemoteException for consistency with the {@link
     *                                  org.smartfrog.sfcore.prim.Liveness}
     *                                  interface
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (!started) {
            throw new SmartFrogLivenessException(
                    "Application Server not started");
        }
        State state = container.getState();
        if (state.isStopped()) {
            throw new SmartFrogLivenessException(
                    "Application Server has stopped");
        }
    }

    /**
     * Start the component in a new thread. Synchronized.
     * @throws SmartFrogException
     */
    private synchronized void startInNewThread() throws SmartFrogException {
        if (started || starting) {
            throw new SmartFrogException("We are already running!");
        }
        //note that we are starting. The new thread moves to started when it begins.
        starting=true;
        thread = new Thread(this);
        thread.run();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to
     * create a thread, starting the thread causes the object's <code>run</code>
     * method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take
     * any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        container.start();
        synchronized (this) {
            started = true;
            starting = false;
        }
        //end of life
        thread = null;
    }

}
