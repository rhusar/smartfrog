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
package org.smartfrog.services.junit;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * This is the test runner.
 * It keeps all its public state in a configuration object that can be got/cloned and serialized to suites
 * created 15-Apr-2004 15:44:41
 */

public class TestRunnerComponent extends CompoundImpl implements TestRunner {

    private Logger log;
    private ComponentHelper helper;

    public TestRunnerComponent() throws RemoteException {
        helper = new ComponentHelper(this);
        log=helper.getLogger();
    }

    /**
     * who listens to the tests?
     * This is potentially remote
     */
    private RunnerConfiguration configuration=new RunnerConfiguration();


    /**
     * validate our settings, bail out if they are invalid
     *
     * @throws SmartFrogInitException
     */
    private void validate() throws SmartFrogInitException {
        if (configuration.getFork() == true) {
            throw new SmartFrogInitException("forking is not yet implemented");
        }
    }

    /**
     * Deploy the compound. Deployment is defined as iterating over the context
     * and deploying any parsed eager components.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure deploying compound or
     *                                  sub-component
     * @throws java.rmi.RemoteException In case of Remote/nework error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
    }

    /**
     * Starts the compound. This sends a synchronous sfStart to all managed
     * components in the compound context. Any failure will cause the compound
     * to terminate
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failed to start compound
     * @throws java.rmi.RemoteException In case of Remote/nework error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        //this will deploy all our children, including the test suites
        super.sfStart();
        Object o = sfResolve(ATTRIBUTE_LISTENER, configuration.getListener(), true);
        if (!(o instanceof TestListener)) {
            throw new SmartFrogException("The attribute " + ATTRIBUTE_LISTENER
                    + "must refer to an implementation of TestListener");
        }
        configuration.setListener((TestListener) o);
        configuration.setFork(sfResolve(ATTRIBUTE_FORK, configuration.getFork(), false));
        configuration.setKeepGoing(sfResolve(ATTRIBUTE_KEEPGOING, configuration.getKeepGoing(), false));
        validate();
        //TODO: execute the tests in all the suites attached to this class
    }

    protected void runAllTests() throws SmartFrogException, RemoteException {
        Enumeration e=sfChildren();
        while (e.hasMoreElements()) {
            Object o = (Object) e.nextElement();
            if(o instanceof TestSuite) {
                TestSuite suiteComponent=(TestSuite) o;
                suiteComponent.bind(getConfiguration());
                suiteComponent.runTests();
            }
        }
    }

    public TestListener getListener() {
        return configuration.getListener();
    }


    public void setListener(TestListener listener) {
        configuration.setListener(listener);
    }

    public boolean getKeepGoing() {
        return configuration.getKeepGoing();
    }

    public void setKeepGoing(boolean keepGoing) {
        configuration.setKeepGoing(keepGoing);
    }

    public boolean getFork() {
        return configuration.getFork();
    }

    public void setFork(boolean fork) {
        configuration.setFork(fork);
    }


    public RunnerConfiguration getConfiguration() {
        return configuration;
    }
}
