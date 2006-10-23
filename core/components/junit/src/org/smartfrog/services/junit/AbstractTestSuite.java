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
package org.smartfrog.services.junit;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * This is an abstract base class for test suites.
 * Its key feature is that it provides a thread local context variable that can be used to retrieve the current
 * context of a running test. 
 * created 10-Oct-2006 11:39:29
 */

public abstract class AbstractTestSuite extends PrimImpl implements TestSuite {


    private static final ThreadLocal<RunnerConfiguration> configurationContext =new ThreadLocal<RunnerConfiguration>();

    private static final ThreadLocal<Prim> testSuiteContext =new ThreadLocal<Prim>();

    protected AbstractTestSuite() throws RemoteException {
    }

    /**
     * bind to the configuration. A null parameter means 'stop binding'
     *
     * @param configuration config to bind to
     * @throws java.rmi.RemoteException on network trouble
     * @throws SmartFrogException for other problems
     */
    public void bind(RunnerConfiguration configuration) throws RemoteException, SmartFrogException {
        boolean overwriting=false;
        boolean overwritingourselves=false;
        synchronized(configurationContext) {
            RunnerConfiguration current = configurationContext.get();
            if(current !=null && configuration!=null) {
                overwriting=true;
                overwritingourselves= current ==configuration;
            }
            configurationContext.set(configuration);
            //set or reset the test suite context
            if(configuration!=null) {
                if (testSuiteContext.get() != null) {
                    overwriting = true;
                }

                testSuiteContext.set(this);
            } else {
                testSuiteContext.set(null);
            }
        }
        if (overwriting) {
            //warn that something got overwritten. It is probably harmless, but can
            //cause interesting behaviour
            sfLog().info("Overwriting an existing thread-local configuration context.\n"
                +"Multiple thread runners may be active in the same thread\n"
                +"or the tests are themselves deploying tests.");
            if(overwritingourselves) {
                sfLog().info("P.S. The component is overwriting its own configuration");
            }
        }
    }


    /**
     * Get the thread local configuration.
     * @return the configuration (may be null)
     */
    public static RunnerConfiguration getConfiguration() {
        return configurationContext.get();
    }

    /**
     * Get the thread local test suite
     * @return the configuration (may be null)
     */
    public static Prim getTestSuite() {
        return testSuiteContext.get();
    }


}
