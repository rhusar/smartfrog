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

package org.smartfrog.services.xunit.listeners.html;

import org.smartfrog.services.xunit.base.TestListener;
import org.smartfrog.services.xunit.base.TestSuite;
import org.smartfrog.services.xunit.listeners.xml.XmlTestIndexImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.File;
import java.nio.charset.Charset;
import java.rmi.RemoteException;

/**
 * TODO: implemeent
 */
public class HtmlTestIndexImpl extends XmlTestIndexImpl implements HtmlTestIndex {
    private String cssURL;
    private String cssResource;
    private String cssData;
    ComponentHelper helper;

    public HtmlTestIndexImpl() throws RemoteException {
        helper = new ComponentHelper(this);
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException
     *                                  failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        cssURL = sfResolve(ATTR_CSS_URL, cssURL, false);
        cssResource = sfResolve(ATTR_CSS_RESOURCE, cssResource, false);
        if (cssResource != null) {
            //load in the data from the class
            helper.loadResourceToString(cssResource, Charset.forName("UTF-8"));
        }
        cssData = sfResolve(ATTR_CSS_DATA, cssData, false);

    }

    /**
     * notify indexer that a test suite has started
     * @param suite test suite
     * @param hostname host starting the tests
     * @param processname process of the tests
     * @param suitename name of the suite
     * @param timestamp when they started
     * @param listener who is listening to it
     * @param filename the file being created
     * @throws SmartFrogException SmartFrog trouble
     * @throws RemoteException In case of network/rmi error
     */

    public void testSuiteStarted(TestSuite suite,
                                 String hostname,
                                 String processname,
                                 String suitename,
                                 long timestamp,
                                 TestListener listener,
                                 File filename)
            throws RemoteException, SmartFrogException {
        super.testSuiteStarted(suite,
                hostname,
                processname,
                suitename,
                timestamp,
                listener,
                filename);
    }
}
