/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.junitmr;

import org.apache.hadoop.io.Text;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created 17-Mar-2009 13:59:23
 */

public class JUnitMRUtils {
    /**
     * {@value }
     */
    private static final String SUITE_METHOD_NAME = "suite";

    static String readText(DataInput in) throws IOException {
        Text t = new Text();
        t.readFields(in);
        String s = t.toString();
        return s;
    }

    static void writeText(DataOutput out, String s) throws IOException {
        Text t = new Text(s);
        t.write(out);
    }


    /**
     * get the tests from the class, either as a suite or as introspected tests. There is no verification here that a
     * class is a test suite!
     *
     * @param clazz class with the test
     * @return the test
     * @throws JUnitMRException if the test suite setup failed
     */
    public static Test extractTest(Class clazz) throws JUnitMRException {
        //todo: verify that the class implements test or testsuite
        try {
            // check if there is a suite method
            Method method = clazz.getMethod(SUITE_METHOD_NAME);
            return (Test) method.invoke(null);
        } catch (NoSuchMethodException e) {
            //if not, assume that it is a testclass and do it that way
            return new TestSuite(clazz);
        } catch (IllegalAccessException e) {
            throw new JUnitMRException("No access to the method " + SUITE_METHOD_NAME
                    + " in class " + clazz, e);
        } catch (InvocationTargetException e) {
            throw new JUnitMRException("Exception in " + SUITE_METHOD_NAME
                    + " in class " + clazz, e.getCause());
        }
    }


    /**
     * load the test class, using the secure classloader framework.
     *
     * @param classname class
     * @return the loaded class
     */
    public static Class loadTestClass(String classname)
            throws ClassNotFoundException {
        return Thread.currentThread().getClass().getClassLoader().loadClass(classname);
    }
}
