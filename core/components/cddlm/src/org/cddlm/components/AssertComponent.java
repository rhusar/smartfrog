/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP
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
package org.cddlm.components;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;


/**
 * created 28-Apr-2004 11:40:53
 */
public class AssertComponent extends PrimImpl implements Assert {
    //~ Constructors -----------------------------------------------------------

    public AssertComponent() throws RemoteException {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * verify that assertions are valid.
     *
     * @throws RemoteException
     * @throws SmartFrogAssertionException
     */
    public void checkAssertions()
            throws RemoteException, SmartFrogException,
            SmartFrogAssertionException {
        boolean isTrue = sfResolve(IS_TRUE, true, false);
        boolean isFalse = sfResolve(IS_FALSE, false, false);
        Reference reference = sfResolve(REFERENCE, (Reference) null, false);
        String evaluatesTrue = sfResolve(EVALUATES_TRUE, (String) null, false);
        String evaluatesFalse = sfResolve(EVALUATES_FALSE, (String) null,
                false);
        assertTrue(isTrue, IS_TRUE);
        assertTrue(!isFalse, IS_FALSE);

        if ((reference != null) && (evaluatesTrue != null)) {
            assertTrue(evaluate(reference, evaluatesTrue), EVALUATES_TRUE);
        }

        if ((reference != null) && (evaluatesFalse != null)) {
            assertTrue(!evaluate(reference, evaluatesFalse), EVALUATES_FALSE);
        }
    }

    /**
     * make an assertion
     *
     * @param fact
     * @param test
     * @throws SmartFrogAssertionException
     */
    protected void assertTrue(boolean fact, String test)
            throws SmartFrogAssertionException {
        if (!fact) {
            throw new SmartFrogAssertionException("Assertion " + test
                    + " did not hold");
        }
    }

    /**
     * evaluate a named method on an object; expect it to return a boolean
     *
     * @param reference  object to invoke
     * @param methodName name of method
     * @return the boolean value of the invocation
     * @throws SmartFrogAssertionException
     */
    public boolean evaluate(Object reference, String methodName)
            throws SmartFrogException, RemoteException {
        try {
            Class clazz = reference.getClass();
            Class[] params = new Class[0];
            Method method = clazz.getMethod(methodName, params);
            Object result = null;
            result = method.invoke(reference, null);

            if (!(result instanceof Boolean)) {
                throw new SmartFrogAssertionException("method " + methodName
                        + " is not boolean");
            }

            Boolean b = (Boolean) result;

            return b.booleanValue();
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RemoteException) {
                throw (RemoteException) (e.getCause());
            }

            if (e.getCause() instanceof SmartFrogException) {
                throw (SmartFrogException) (e.getCause());
            }

            String errorText = "invoking " + methodName + " on " + reference;
            throw new SmartFrogAssertionException(errorText, e);
        } catch (Exception e) {
            String errorText = "invoking " + methodName + " on " + reference;
            throw new SmartFrogAssertionException(errorText, e);
        }
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *          component is terminated
     */
    public void sfPing(Object source) throws SmartFrogLivenessException {
        super.sfPing(source);

        try {
            checkAssertions();
        } catch (RemoteException e) {
            throw new SmartFrogLivenessException(e);
        } catch (SmartFrogLivenessException e) {
            throw e;
        } catch (SmartFrogException e) {
            throw new SmartFrogLivenessException(e);
        }
    }

    /**
     * Called after instantiation for deployment purposed. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
    }
}
