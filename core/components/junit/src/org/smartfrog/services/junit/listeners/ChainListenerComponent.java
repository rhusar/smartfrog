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
package org.smartfrog.services.junit.listeners;

import org.smartfrog.services.junit.TestListener;
import org.smartfrog.services.junit.TestListenerFactory;
import org.smartfrog.services.junit.TestSuite;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.rmi.RemoteException;

/**
 * This is the component that
 * created 21-Apr-2006 11:27:58
 */

public class ChainListenerComponent extends PrimImpl implements TestListenerFactory {

    /**
     * The name of a factory
     */
    public static final String ATTR_LISTENERS="listeners";

    private List factories;
    private Log log;
    private ComponentHelper helper = new ComponentHelper(this);

    public ChainListenerComponent() throws RemoteException {
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
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        log = helper.getLogger();
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        Vector factoryList=null;
        factoryList=sfResolve(ATTR_LISTENERS, factoryList,true);
        factoryList=helper.resolveVectorReferences(factoryList);
        factories=new ArrayList(factoryList.size());
        Iterator fit=factoryList.iterator();
        while (fit.hasNext()) {
            Object next = fit.next();
            TestListenerFactory factory = (TestListenerFactory) next;
            factories.add(factory);
        }
    }

    /**
     * bind to a caller
     *
     * @param suite     the test suite that is about to run. May be null,
     *                  especially during testing.
     * @param hostname  name of host
     * @param suitename name of test suite
     * @param timestamp start timestamp (UTC)
     * @return a listener to talk to
     */
    public TestListener listen(TestSuite suite, String hostname, String suitename, long timestamp)
            throws RemoteException, SmartFrogException {
        return new ChainListener(factories, suite, hostname,suitename,timestamp);
    }
}
