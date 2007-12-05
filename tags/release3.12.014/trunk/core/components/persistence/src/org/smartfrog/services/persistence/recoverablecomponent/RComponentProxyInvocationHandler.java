/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP
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

package org.smartfrog.services.persistence.recoverablecomponent;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

import org.smartfrog.services.persistence.storage.StorageException;


/**
 * An invocation handler to recoverable components.
 *
 * References to recoverable components can rebind themselves automatically
 * when the target component is recreated after some failure. These references
 * are implemented as dynamic proxies that use the RComponentProxyInvocationHandler
 * as their invocation handler.
 *
 * The invocation handler uses three objects: the "direct" object; a "proxy
 * locator" object; and a "local override" object; The direct object is the
 * target recoverable component and the invocation handler actually holds an RMI
 * reference to it. In the event that the RMI reference becomes invalid, say
 * due to a failure of the target, a new one is obtained from the locator object.
 * This step is how the proxy rebinds to a new incarnation of the recoverable
 * component. The last object, the local override, intercepts some methods that
 * can be performed locally instead of invoking the target recoverable component.
 * These include methods that are required to work even with the recoverble component
 * is absent - the method "isDead" is an example.
 */
public class RComponentProxyInvocationHandler implements InvocationHandler,
        Serializable {

    static final long serialVersionUID = 0L;

    /**
     * DirectObject is a reference (stub) for the actual object
     */
    Object directObject;

    /**
     * proxylocator is
     */
    RComponentProxyLocator proxylocator;

    /**
     * localOverrideStub is an object that handles local operations - it does not
     * forward the method invocations to the actual object. These are
     * methods that may need to work in the absence of the actual object.
     */
    RComponentProxyStubImpl localOverrideStub;


    /**
     * Constructor.
     * Sets the direct object to be the component paremeter,
     * constructs a proxy locator and constructs an object that will handle
     * invocations that must be done locally (i.e. anything that may need
     * to be done in the absence of the actual object).
     *
     * @param component RComponent
     * @throws StorageException
     * @throws RuntimeException
     */
    public RComponentProxyInvocationHandler(RComponent component) throws
            StorageException, RuntimeException {
        directObject = component;

        try {
            proxylocator = component.getProxyLocator();
        } catch (RemoteException exc) {
            throw new RuntimeException("Proxy was being remotely created!", exc);
        } catch (StorageException ex) {
            throw ex;
        }

        localOverrideStub = new RComponentProxyStubImpl(proxylocator);
    }


    /**
     * Obtains the proxy for the given component with an instance of this
     * class as the invocation handler for the proxy.
     *
     * @param obj RComponent
     * @return Object
     * @throws RuntimeException
     * @throws StorageException
     * @throws IllegalArgumentException
     */
    static public Object sfGetProxy(RComponent obj) throws RuntimeException,
            StorageException, IllegalArgumentException {

        Class[] objinterfvector = obj.getClass().getInterfaces();
        Class[] interfvector = new Class[objinterfvector.length + 1];

        interfvector[0] = RComponentProxyStub.class;
        for (int i = 0; i < objinterfvector.length; i++) {
            interfvector[i + 1] = objinterfvector[i];
        }

        return java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                interfvector,
                new RComponentProxyInvocationHandler(obj));
    }


    /**
     * Perform an invocation. The method attempts the invocation on the local
     * invocation object first to see if it can do it. This is a kind of
     * interceptor for invocations that can be done here. If that doesn't
     * work it tries to invoke the actual object instead. If that doesn't
     * work due to remote invocation exception it goes into a loop trying
     * to rebind to the actual object and do the invocation again. If the
     * rebind returns that the object is dead then the loop terminates.
     *
     *
     * @param proxy Object
     * @param method Method
     * @param args Object[]
     * @return Object
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws
            Throwable {

        Object result = null;
        boolean finished = true;


        /**
         * Attempt the invocation against the local over-ride methods
         */
        try {
            result = method.invoke(localOverrideStub, args);
        } catch (InvocationTargetException exc) {
            throw exc.getTargetException();
        } catch (IllegalArgumentException exc2) {
            finished = false;
        }

        /**
         * If we are done return
         */
        if (finished) {
            return result;
        }

        /**
         * Attempt the invocation against the actual object
         */
        try {

            result = method.invoke(directObject, args);

        } catch (InvocationTargetException exc) {
            Throwable invocationException = exc.getTargetException();

            /**
             * While the invocation is failing due to a remote invocation
             * exception try rebinding and invoking again.
             */
            while (invocationException instanceof RemoteException) {

                /**
                 * Sleep for a while
                 */
                try {
                    Thread.sleep(RComponent.StubWait);
                } catch (InterruptedException exc2) {}


                try {
                    /**
                     * Check the actual object is not dead
                     */
                    if (proxylocator.isDead()) {
                        RemoteException rex = new RemoteException("Component already terminated.");
                        throw rex;
                    }

                    /**
                     * rebind
                     */
                    directObject = proxylocator.getRComponentStub();

                    invocationException = null;
                    /**
                     * Attempt the invocation again
                     */
                    result = method.invoke(directObject, args);

                } catch (ProxyLocatorException exc2) {
                    /**
                     * problems getting the new stub - retry later
                     * notice that e has not been assigned null if this exception was thrown
                     */
                } catch (InvocationTargetException exc2) {
                    /**
                     * problems during the invocation
                     */
                    invocationException = exc2.getTargetException();
                }

                if (invocationException == null) { // correct execution
                    return result;
                }


                /**
                 * in any case, if sfPing and not dead, returns OK
                 */
                if (method.getName().equals("sfPing")) {
                    return null;
                }
            }
            throw invocationException;
        }
        return result;
    }


    /**
     * Serialization - always writes out the remote stub for the direct object
     * and the proxy locator.
     *
     * @param out ObjectOutputStream
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {

        out.writeObject(RemoteObject.toStub((Remote) directObject));
        out.writeObject(proxylocator);
    }


    /**
     * Serialization - reads the direct object (remote stub) and the proxy
     * locator and recreates a local invocation object.
     *
     * @param in ObjectInputStream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {

        directObject = in.readObject();
        proxylocator = (RComponentProxyLocator) in.readObject();
        localOverrideStub = new RComponentProxyStubImpl(proxylocator);
    }
}
