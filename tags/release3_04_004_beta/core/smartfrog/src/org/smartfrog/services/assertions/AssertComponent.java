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
package org.smartfrog.services.assertions;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.io.File;


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
        boolean equalityIgnoresCase = sfResolve(EQUALITY_IGNORES_CASE,false,true);
        String evaluatesTrue = sfResolve(EVALUATES_TRUE, (String) null, false);
        String evaluatesFalse = sfResolve(EVALUATES_FALSE, (String) null,
                false);
        String attribute=sfResolve(Assert.HAS_ATTRIBUTE, (String) null, false);
        String attributeEquals = sfResolve(Assert.ATTRIBUTE_EQUALS,
                (String) null,
                false);
        assertTrue(isTrue, IS_TRUE);
        assertTrue(!isFalse, IS_FALSE);

        Prim prim= maybeResolveReference();
        if ((prim != null) && (evaluatesTrue != null)) {
            assertTrue(evaluate(prim, evaluatesTrue), EVALUATES_TRUE);
        }

        if ((prim != null) && (evaluatesFalse != null)) {
            assertTrue(!evaluate(prim, evaluatesFalse), EVALUATES_FALSE);
        }

        if ((prim != null) && (evaluatesFalse != null)) {
            assertTrue(!evaluate(prim, evaluatesFalse), EVALUATES_FALSE);
        }

        if(prim !=null && attribute!=null) {
            //look for a named attribute existing
            assertTrue(prim.sfResolve(attribute,false)!=null,
                    "Resolving attribute "+attribute+" of "+prim);
            if(attributeEquals!=null) {
                //do string match if needed
                String attrValue=prim.sfResolve(attribute, (String)null,true);
                assertEqualStrings(attributeEquals, attrValue, equalityIgnoresCase);
            }
        }

        //file existence check
        String filename=sfResolve(FILE_EXISTS, (String) null, false);
        if(filename!=null) {
            File file=new File(filename);
            assertTrue(file.exists() && file.isFile(), FILE_EXISTS+ " "+filename);
        }

        //directory existence
        filename = sfResolve(DIR_EXISTS, (String) null, false);
        if (filename != null) {
            File dir = new File(filename);
            assertTrue(dir.exists() && dir.isDirectory(), DIR_EXISTS + " " + filename);
        }

        String equals1=null;
        String equals2 = null;
        equals1=sfResolve(Assert.EQUALS_STRING1,equals1,false);
        equals2 = sfResolve(Assert.EQUALS_STRING2, equals2, false);
        if((equals1==null && equals2!=null)
            || equals1!=null && equals2==null) {
            assertTrue(false, Assert.EQUALS_STRING1 +" and "+
                    Assert.EQUALS_STRING2+" are both defined");
        }
        if(equals1!=null) {
            assertEqualStrings(equals1,equals2,equalityIgnoresCase);
        }

    }

    private void assertEqualStrings(String equals1, String equals2,
                                    boolean equalityIgnoresCase)
            throws SmartFrogAssertionException {
        boolean fact;
        if(equalityIgnoresCase) {
            fact = equals1.equals(equals2);
        } else {
            fact = equals1.equalsIgnoreCase(equals2);
        }
        assertTrue(fact,
            equals2+" equals "+equals1);
    }

    /**
     * try and resolve a reference, return null if there was some kind of failure
     * including lazy references not yet ready.
     * @return
     * @throws RemoteException
     */
    private Prim maybeResolveReference() throws RemoteException {
        Reference reference = new Reference();
        Prim prim = null;
        try {
            reference = sfResolve(REFERENCE, (Reference) reference, false);
            if ( reference == null ) {
                //there was no reference
                return null;
            }
            prim = sfResolve(reference, (Prim) null, false);
        } catch (SmartFrogResolutionException ignore) {
            //the reason we ignore this is to handle lazy resolution
            //by ignoring it.
        }
        return prim;
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
            throw new SmartFrogAssertionException(createAssertionMessage(test));
        }
    }

    /**
     * get the failure message.
     * This is done by attempting to resolve the message, falling back to a declared one
     * if there is no declared message, or the resolution process failed.
     * @param test
     * @return
     */
    private String createAssertionMessage(String test) {
        String message = "Assertion " + test
                                    + " did not hold";
        try {
            message=sfResolve(MESSAGE,message,false);
        } catch (SmartFrogResolutionException ignore) {

        } catch (RemoteException ignore) {

        }
        return message;
    }

    /**
     * evaluate a named method on an object; expect it to return a boolean
     *
     * @param target  object to invoke
     * @param methodName name of method
     * @return the boolean value of the invocation
     * @throws SmartFrogAssertionException
     */
    public boolean evaluate(Object target, String methodName)
            throws SmartFrogException, RemoteException {
        try {

            Class clazz = target.getClass();
            Class[] params = new Class[0];
            Method method = clazz.getMethod(methodName, params);
            Object result = null;
            result = method.invoke(target, null);

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

            String errorText = "invoking " + methodName + " on " + target;
            throw new SmartFrogAssertionException(errorText, e);
        } catch (Exception e) {
            String errorText = "invoking " + methodName + " on " + target;
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
    public void sfPing(Object source) throws SmartFrogLivenessException,
                                                            RemoteException {
        super.sfPing(source);

        try {
            boolean checkOnLiveness = sfResolve(CHECK_ON_LIVENESS, true, false);
            if(checkOnLiveness) {
                checkAssertions();
            }
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

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        boolean checkOnStartup=sfResolve(CHECK_ON_STARTUP, true, false);
        if(checkOnStartup) {
            checkAssertions();
        }
    }
}
