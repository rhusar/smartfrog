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
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.assertions.ExceptionThrower;

import java.rmi.RemoteException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * created 26-Sep-2006 11:55:05
 */

public class ExceptionThrowerImpl extends PrimImpl implements ExceptionThrower {

    public ExceptionThrowerImpl() throws RemoteException {
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        String classname=sfResolve(ATTR_CLASSNAME,"",true);
        String message= sfResolve(ATTR_MESSAGE, "", false);
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
