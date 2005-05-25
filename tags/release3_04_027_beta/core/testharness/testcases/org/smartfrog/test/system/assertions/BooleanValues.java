/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.test.system.assertions;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is a simple component to test assertion evaluation
 * Date: 30-Apr-2004
 * Time: 23:07:37
 */

public interface BooleanValues extends Remote {

    /**
     * a value attribute; boolean, of course
     */
    public static final String ATTR_VALUE ="value";

    /**
     * always evaluates to true
     * @return true
     * @throws RemoteException
     */
    public boolean getTrue() throws RemoteException;

    /**
     *
     * @return false
     * @throws RemoteException
     */
    public boolean getFalse() throws RemoteException;

    /**
     * get whatever the value attribute is set to
     * @throws RemoteException
     */
    public boolean getValue() throws RemoteException, SmartFrogResolutionException;

    /**
     * throw a runtime fault when invoking
     * @return
     * @throws RemoteException
     * @throws java.lang.RuntimeException always.
     */
    public boolean throwRuntimeException() throws RemoteException;

}
