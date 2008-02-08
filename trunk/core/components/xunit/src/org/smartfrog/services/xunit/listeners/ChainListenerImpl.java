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
package org.smartfrog.services.xunit.listeners;

import org.smartfrog.services.xunit.base.TestListener;
import org.smartfrog.services.xunit.base.TestListenerFactory;
import org.smartfrog.services.xunit.base.TestSuite;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.compound.CompoundImpl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * This is the component that
 * created 21-Apr-2006 11:27:58
 */

public class ChainListenerImpl extends CompoundImpl implements TestListenerFactory {

    /**
     * The name of a factory
     */
    public static final String ATTR_LISTENERS="listeners";

    private List<TestListenerFactory> factories;

    /**
     * simple constructor
     * @throws RemoteException
     */
    public ChainListenerImpl() throws RemoteException {
    }


    /**
     * Called after instantiation for deployment purposed. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws SmartFrogException  error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        Vector factoryList=null;
        factoryList=sfResolve(ATTR_LISTENERS, factoryList,true);
        factories=new ArrayList<TestListenerFactory>(factoryList.size());
        for(Object elt:factoryList) {
            TestListenerFactory factory = (TestListenerFactory) elt;
            factories.add(factory);
        }
        //the children are live, so let's add them to the listener list.
        for (Prim child : sfChildList()) {
            if (child instanceof TestListenerFactory) {
                factories.add((TestListenerFactory) child);
            }
        }

    }

    /**
     * Start listening to a test suite
     *
     * @param suite     the test suite that is about to run. May be null,
     *                  especially during testing.
     * @param hostname  name of host
     * @param processname name of the process
     * @param suitename name of test suite
     * @param timestamp start timestamp (UTC)
     * @return a listener to talk to
     * @throws RemoteException network problems
     * @throws SmartFrogException code problems
     */
    public TestListener listen(TestSuite suite, String hostname, String processname, String suitename, long timestamp)
            throws RemoteException, SmartFrogException {
        return new ChainListener(factories, suite, hostname, processname, suitename,timestamp);
    }
}
