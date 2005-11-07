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
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.www.JavaEnterpriseApplication;
import org.smartfrog.services.www.JavaWebApplication;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.security.SFClassLoader;

import java.io.File;
import java.rmi.RemoteException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * Cargo component deploys using cargo
 */
public class CargoServerImpl extends PrimImpl implements CargoServer {

    private Container container;
    private Configuration configuration;
    private File dir;
    private String containerClassname;


    public CargoServerImpl() throws RemoteException {
    }


    /**
     * deploy a web application.
     * Deploys a web application identified by the component passed as a parameter; a component of arbitrary
     * type but which must have the mandatory attributes identified in {@link JavaWebApplication};
     * possibly even extra types required by the particular application server.
     *
     * @param webApplication the web application. this must be a component whose attributes include the
     *                       mandatory set of attributes defined for a JavaWebApplication component. Application-server specific attributes
     *                       (both mandatory and optional) are also permitted
     * @return an entry referring to the application
     * @throws RemoteException    on network trouble
     * @throws SmartFrogException on any other problem
     */
    public JavaWebApplication deployWebApplication(Prim webApplication) throws RemoteException, SmartFrogException {
        throw new SmartFrogException("not implemented");
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
        throw new SmartFrogException("not implemented");
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
    public ServletContextIntf deployServletContext(Prim servlet) throws RemoteException, SmartFrogException {
        throw new SmartFrogException("not supported");
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
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        String dirname = FileSystem.lookupAbsolutePath(this, ATTR_DIRECTORY, null, null, true, null);
        ConfigurationFactory factory = new DefaultConfigurationFactory();
        String name = sfResolve(ATTR_CONFIGURATION_CLASS, "", true);
        //TODO
        //String codebase = sfResolve("sfcodebase");
        configuration = (Configuration) createClassInstance(name);
        dir = new File(dirname);
        containerClassname = sfResolve(ATTR_CONTAINER_CLASS, "", true);
        Class containerClass = loadClass(containerClassname);
        Class signature[] = new Class[1];
        signature[0] = Container.class;
        Object args[] = new Object[1];
        args[0] = configuration;
        container = (Container) instantiate(containerClass, signature, args);

        //at this point the container is instantiated
    }

    private Object createClassInstance(String name) throws SmartFrogException {
        Object instance;
        Class clazz = loadClass(name);
        Class signature[] = null;
        Object arguments[] = null;
        instance = instantiate(clazz, signature, arguments);
        return instance;
    }

    private Object instantiate(Class clazz, Class[] signature, Object[] arguments) throws SmartFrogException {
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

    private Class loadClass(String name) throws SmartFrogException {
        Class newclass = null;
        try {
            newclass = SFClassLoader.forName(name);
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
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        //dynamically load the class of 'classname' and instantiate
        //TODO
        //then run container.start() in a new thread
    }
}
