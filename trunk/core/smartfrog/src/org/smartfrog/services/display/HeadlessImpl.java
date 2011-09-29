/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.display;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.awt.GraphicsEnvironment;
import java.rmi.RemoteException;

/**
 * created 02-Jul-2007 12:32:30
 */

public class HeadlessImpl extends PrimImpl implements Headless {

    public static final String AWT_HEADLESS = "java.awt.headless";


    public HeadlessImpl() throws RemoteException {
    }


    /**
     * Call {@link GraphicsEnvironment#isHeadless()}
     *
     * @return true iff the system is headless
     */
    @Override
    public boolean evaluate() {
        return GraphicsEnvironment.isHeadless();
    }


    /**
     * Switch the system into headless mode
     *
     * @param headless the new headless value
     */
    @Override
    public void setHeadless(boolean headless) {
        System.setProperty(AWT_HEADLESS, Boolean.toString(headless));
    }


    /**
     * When you come to resolve the {@link #ATTR_HEADLESS} attribute, test the current value by trying to create a
     * window.
     *
     * @param name the name of the attribute
     * @return the resolved value
     * @throws SmartFrogResolutionException
     */
    @Override
    public Object sfResolveHere(Object name) throws SmartFrogResolutionException {
        if (ATTR_HEADLESS.equals(name)) {
            return evaluate();
        } else {
            return super.sfResolveHere(name);
        }
    }

    /**
     * When replacing the  {@link #ATTR_HEADLESS} attribute, the state of the process is changed to prevent headless
     * operation
     *
     * @param name  attribute to replace
     * @param value value
     * @return the old value if present, null otherwise. It old value was a component description, then its prim parent
     *         is reset.
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    @Override
    public synchronized Object sfReplaceAttribute(Object name, Object value)
            throws SmartFrogRuntimeException, RemoteException {
        Object result = super.sfReplaceAttribute(name, value);
        if (ATTR_HEADLESS.equals(name)) {
            setHeadless(((Boolean) value));
        }
        return result;
    }
}
