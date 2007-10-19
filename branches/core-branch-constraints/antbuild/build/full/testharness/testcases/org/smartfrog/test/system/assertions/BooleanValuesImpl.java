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

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;

/**
 * This is a simple component to test assertion evaluation
 * Date: 30-Apr-2004
 * Time: 23:07:30
 */
public class BooleanValuesImpl extends PrimImpl implements BooleanValues {

    private Prim target;

    public BooleanValuesImpl() throws RemoteException {

    }


    public synchronized void sfStart()
        throws SmartFrogException, RemoteException {
        super.sfStart();
        target=sfResolve(ATTR_TARGET, target,false);
        boolean toggle=sfResolve(ATTR_TOGGLE,false,false);
        if(toggle) {
            //toggle if asked
            toggle();
        }
        //terminate if asked
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,null,null,null);

    }

    /**
     * always evaluates to true
     *
     * @return true
     * @throws java.rmi.RemoteException
     */
    public boolean getTrue() throws RemoteException {
        return true;
    }

    /**
     * @return false
     * @throws java.rmi.RemoteException
     */
    public boolean getFalse() throws RemoteException {
        return false;
    }

    /**
     * get whatever the value attribute is set to
     *
     * @throws java.rmi.RemoteException
     */
    public boolean getValue() throws RemoteException, SmartFrogResolutionException {
        boolean b=sfResolve(BooleanValues.ATTR_VALUE,true,false);
        return b;
    }

    /**
     * throw a runtime fault when invoking.
     * Here to test how well RTE-s get marshalled over the wire.
     *
     * @return
     * @throws java.rmi.RemoteException
     * @throws RuntimeException         always.
     */
    public boolean throwRuntimeException() throws RemoteException {
        throw new RuntimeException("invoked throwRuntimeException()");
    }

    /**
     * Toggle our local value. Will also set the value of any prim set in the
     * target attribute to the final value (no toggle, just a set).
     * @return
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public boolean toggle()
        throws RemoteException, SmartFrogException {
        boolean current=getValue();
        boolean next=!current;
        Boolean newValue = Boolean.valueOf(next);
        sfReplaceAttribute(BooleanValues.ATTR_VALUE, newValue);
        if(target!=null) {
            target.sfReplaceAttribute(BooleanValues.ATTR_VALUE, newValue);
        }
        return next;
    }

}
