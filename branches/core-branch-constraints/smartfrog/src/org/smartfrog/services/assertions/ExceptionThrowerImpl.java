/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.assertions;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.services.assertions.ExceptionThrower;

import java.rmi.RemoteException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * created 26-Sep-2006 11:55:05
 */

public class ExceptionThrowerImpl extends PrimImpl implements ExceptionThrower {

    private boolean throwOnStartup;
    private boolean throwOnDeploy;
    private boolean throwOnPing;
    private String classname;
    private String message;

    public ExceptionThrowerImpl() throws RemoteException {
    }


    /**
     * Called after instantiation for deployment purposes. Heart monitor is started and if there is a parent the
     * deployed component is added to the heartbeat. Subclasses can override to provide additional deployment behavior.
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        classname = sfResolve(ATTR_CLASSNAME, "", true);
        message = sfResolve(ATTR_MESSAGE, "", true);
        throwOnStartup = sfResolve(ATTR_THROW_ON_STARTUP, false, true);
        throwOnDeploy = sfResolve(ATTR_THROW_ON_DEPLOY, false, true);
        throwOnPing = sfResolve(ATTR_THROW_ON_PING, false, true);
        if(throwOnDeploy) {
            raiseException(classname, message);
        }
    }

    /**
     * Startup may raise an exception if that is required from the component settings
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        if (throwOnStartup) {
            raiseException(classname, message);
        }
    }


    /**
     * Liveness call in to check if this component is still alive.
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (throwOnPing) {
            try {
                raiseException(classname, message);
            } catch (SmartFrogException e) {
                throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(e);
            }
        }
    }

    /**
     * Raise an exception
     * @param classname name of the class to use
     * @param message message to raise
     * @throws RemoteException network problems or this exception was asked for
     * @throws SmartFrogException which can be the exception, or wrap something inside
     * @throws RuntimeException if that was asked for
     */
    protected void raiseException(String classname, String message) throws RemoteException, SmartFrogException {
        Throwable instance;
        try {
            Class eClass = getClass().getClassLoader().loadClass(classname);
            Class oneStringCtor[] = {
                    String.class
            };
            Constructor constructor = eClass.getConstructor(oneStringCtor);
            Object args[]= {
                    message
            };
            instance = (Throwable) constructor.newInstance(args);
        } catch (ClassNotFoundException e) {
            instance = e;
        } catch (NoSuchMethodException e) {
            instance = e;

        } catch (InstantiationException e) {
            instance = e;

        } catch (IllegalAccessException e) {
            instance = e;

        } catch (InvocationTargetException e) {
            instance = e;

        }
        if(instance instanceof RuntimeException) {
            throw (RuntimeException)instance;
        }
        if (instance instanceof RemoteException) {
            throw (RemoteException) instance;
        }
        throw SmartFrogException.forward(instance);
    }


}
