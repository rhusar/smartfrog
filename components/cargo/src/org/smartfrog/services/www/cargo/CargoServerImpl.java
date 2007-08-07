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

import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.State;
import org.codehaus.cargo.container.EmbeddedLocalContainer;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
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
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;


/**
 * Cargo component deploys using cargo
 */
public class CargoServerImpl extends PrimImpl implements CargoServer, Runnable {

    private LocalContainer container;
    private LocalConfiguration configuration;
    private String containerClassname;
    private String codebase;
    private Thread thread;
    //internal state tracking
    private int state;
    public static final int STATE_UNCONFIGURED = 0;
    public static final int STATE_STARTING = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_FAILED = 3;
    public static final int STATE_TERMINATED = 4;
    //any exception caught from the background thread
    private Throwable caughtException;
    private Log log;
    public static final int DEFAULT_CARGO_PORT = 8080;
    private Vector properties;
    private String home;
    private File homedir;
    private int port;
    private Vector extraClasspath;
    private String configurationClass;
    //list of things to deploy
    private List deployables=new ArrayList();
    private File configurationDir;
    private ComponentHelper helper;


    public CargoServerImpl() throws RemoteException {
        helper = new ComponentHelper(this);
    }

    public Log getLog() {
        return log;
    }

    public LocalContainer getContainer() {
        return container;
    }

    public LocalConfiguration getConfiguration() {
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
     * @return an entry referring to the application
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
     * @return an entry referring to the application
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
     * @return a token referring to the application
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
    }

    private void readConfigurationParameters() throws RemoteException, SmartFrogException {
        log = LogFactory.getLog(this);
        //ConfigurationFactory factory = new DefaultConfigurationFactory();
        home = FileSystem.lookupAbsolutePath(this, ATTR_HOME,
                null, null, false, null);
        if(home!=null) {
            homedir = new File(home);
        }

        String configDir= FileSystem.lookupAbsolutePath(this, ATTR_CONFIG_DIR,
                null, null, false, null);
        if(configDir!=null) {
            configurationDir = new File(configDir);
            configurationDir.getParentFile().mkdirs();
        } else {
            configurationDir = FileSystem.createTempFile("cargo", ".dir", null);
        }

        configurationClass = sfResolve(ATTR_CONFIGURATION_CLASS, "", true);
        codebase = sfResolve(SmartFrogCoreKeys.SF_CODE_BASE,
                (String) null,
                false);

        containerClassname = sfResolve(ATTR_CONTAINER_CLASS, "", true);

        //classpath setup
        extraClasspath = sfResolve(ATTR_EXTRA_CLASSPATH, extraClasspath, false);

        //properties
        properties=sfResolve(ATTR_PROPERTIES,properties,false);

        //add any direct bindings from JavaWebApplicationServer to cargo options

        //the port is only set if it !=8080. This is to avoid bothering
        //with raising warning messages for runtimes that don't support it.
        port = sfResolve(ATTR_PORT,DEFAULT_CARGO_PORT,false);
    }


    /**
     * This is something for a background thread to do.
     */
    protected void createAndConfigureContainer() throws SmartFrogException, RemoteException {

        configuration = createLocalConfiguration(configurationClass, configurationDir);

        if (properties != null) {
            Iterator it = properties.iterator();
            int count = 0;
            while (it.hasNext()) {
                Vector tuple = (Vector) it.next();
                if (tuple.size() != 2) {
                    throw new SmartFrogDeploymentException("Element "
                            + count
                            + " in " + ATTR_PROPERTIES + "is not a two element list");
                }
                String propertyName = tuple.get(0).toString();
                String value = tuple.get(1).toString();
                setConfigurationProperty(propertyName, value);
                count++;
            }
        }
        if (port != DEFAULT_CARGO_PORT) {
            setConfigurationProperty(ServletPropertySet.PORT, Integer.toString(port));
        }

        try {
            configuration.setProperty("home",homedir.getCanonicalPath());
        } catch (IOException e) {
            throw new SmartFrogDeploymentException("Failed to create a canonical path from "+homedir);

        }

        container = (LocalContainer) construct(containerClassname,
                LocalConfiguration.class,
                configuration);

        //at this point the container is instantiated
        EmbeddedLocalContainer embedded = null;
        InstalledLocalContainer installed = null;

        //maybe it is an embedded container
        if (container instanceof EmbeddedLocalContainer) {
            embedded = (EmbeddedLocalContainer) container;
        } else if (container instanceof InstalledLocalContainer) {
            //set the home stuff
            installed = (InstalledLocalContainer) container;
            //installed.setHome(homedir);
            //CARGO-09
            installed.setHome(homedir.getAbsolutePath());
        }

        //monitoring relays to smartfrog
        container.setLogger(new MonitorToSFLog(log));

        //home dir



        if (extraClasspath != null) {
            final int size = extraClasspath.size();
            String[] classArray = new String[size];
            URL[] urlArray = new URL[size];
            Iterator classesIterator = extraClasspath.listIterator();
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
                try {
                    urlArray[i] = pathElementFile.toURL();
                } catch (MalformedURLException e) {
                    throw SmartFrogDeploymentException.forward(e);
                }
            }
            if (installed != null) {
                //installed deployments get a classpath set
                installed.setExtraClasspath(classArray);
            } else {
                if (embedded != null) {
                    //embedded deployments have a new classpath
                    URLClassLoader loader = new URLClassLoader(urlArray);
                    embedded.setClassLoader(loader);
                } else {
                    log.warn("Classpath setup is not supported with this container " + container.getClass());
                }
            }
        }

        //add the list of deployables, which have to be set before deployment begins.
        deployDeployables();

        //bind the container to the config
        container.setConfiguration(configuration);

    }


    private void setConfigurationProperty(String propertyName, String value) {
        if(log.isDebugEnabled()) {
            log.debug("Setting property "+propertyName +" :="+value);
        }
        if(!configuration.getCapability().supportsProperty(propertyName)) {
            configuration.setProperty(propertyName, value);
        } else {
            log.warn("Unsupported property " + propertyName);
        }
    }


    /**
     * Create a new container
     *
     * @param clazz
     * @param signature
     * @param arguments
     * @return
     * @throws SmartFrogException
     */
    private Object instantiate(Class clazz,
                               Class[] signature,
                               Object[] arguments) throws SmartFrogException {
        try {
            Object instance;
            Constructor constructor = clazz.getConstructor(signature);
            instance = constructor.newInstance(arguments);
            return instance;
        } catch (NoSuchMethodException e) {
            throw SmartFrogException.forward(e);

        } catch (InvocationTargetException e) {
            throw SmartFrogException.forward(e);

        } catch (InstantiationException e) {
            throw SmartFrogException.forward(e);

        } catch (IllegalAccessException e) {
            throw SmartFrogException.forward(e);
        }
    }


    /**
     * create a local configuration
     *
     * @param name
     * @return
     * @throws SmartFrogException
     */
    private LocalConfiguration createLocalConfiguration(String name,File dir) throws SmartFrogException,
            RemoteException {
/*
        Class argclass = File.class;
        Object arg = dir;
        Object instance = construct(name, argclass, arg);
*/
        Class argclass = String.class;
        Object arg = dir.getAbsolutePath();
        Object instance = construct(name, argclass, arg);
        return (LocalConfiguration) instance;
    }

    /**
     * construct something that takes one parameter
     * @param classname
     * @param argclass
     * @param arg
     * @return
     * @throws SmartFrogException
     */
    private Object construct(String classname, Class argclass, Object arg) throws SmartFrogException, RemoteException {
        Object instance;
        Class clazz = helper.loadClass(classname);
        Class signature[] = new Class[] {
            argclass
        };
        Object arguments[] = new Object[] {
                arg
        };
        instance = instantiate(clazz, signature, arguments);
        return instance;
    }

    /**
     * run through the list of things to deploy, and deploy them once the app
     * server is up and running.
     * This happens after the server is configured, but before it is started
     */
    protected void deployDeployables() {
        Iterator it=deployables.iterator();
        while (it.hasNext()) {
            Deployable deployable = (Deployable) it.next();
            getConfiguration().addDeployable(deployable);
        }
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
        readConfigurationParameters();
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
        if (state==STATE_RUNNING) {
            try {
                container.stop();
            } finally {
                setState(STATE_TERMINATED);
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
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *                                  component is terminated
     * @throws java.rmi.RemoteException for consistency with the {@link
     *                                  org.smartfrog.sfcore.prim.Liveness}
     *                                  interface
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if(caughtException!=null) {
            throw (SmartFrogLivenessException)SmartFrogLivenessException.forward(caughtException);
        }

        int s = state;
        switch(state) {
            case STATE_UNCONFIGURED:
                throw new SmartFrogLivenessException(
                        "Component has not yet been configured");
            case STATE_STARTING:
                //in startup phase. We will eventually enter
                //the running phase or some error will arise.
                break;

            case STATE_RUNNING:
                State cstate = container.getState();
                if (cstate.isStopped()) {
                    throw new SmartFrogLivenessException(
                            "Application Server has stopped");
                }
                break;

            case STATE_TERMINATED:
                throw new SmartFrogLivenessException(
                        "Application Server is terminated");
            case STATE_FAILED:
                //should be caught by the caught-exception check earlier
                throw new SmartFrogLivenessException(
                        "Application Server failed");

            default:
                throw new SmartFrogLivenessException("unknown state");
        }
    }

    /**
     * Queue up another deployable
     * @param deployable
     */
    public void addDeployable(Deployable deployable) {
        deployables.add(deployable);
    }

    /**
     * Start the component in a new thread. Synchronized.
     *
     * @throws SmartFrogException
     */
    private synchronized void startInNewThread() throws SmartFrogException {
        if(thread!=null) {
            throw new SmartFrogException("We are already running!");
        }
        //note that we are starting. The new thread moves to started when it begins.
        setState(STATE_STARTING);
        thread = new Thread(this);
        thread.run();
    }

    private synchronized void setState(int state) {
        this.state=state;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        try {
            createAndConfigureContainer();
            //now start the container
            container.start();
            setState(STATE_RUNNING);
        } catch (SmartFrogException e) {
            caughtException = e;
            setState(STATE_FAILED);
        } catch (RemoteException e) {
            caughtException = e;
            setState(STATE_FAILED);
        } finally {
            //end of life
            thread = null;
        }
    }


}
