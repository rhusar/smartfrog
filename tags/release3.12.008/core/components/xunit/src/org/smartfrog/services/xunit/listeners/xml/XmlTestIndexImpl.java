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

package org.smartfrog.services.xunit.listeners.xml;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.xunit.base.TestListener;
import org.smartfrog.services.xunit.base.TestSuite;
import org.smartfrog.services.xunit.serial.Statistics;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 */
public class XmlTestIndexImpl extends PrimImpl implements XmlTestIndex {
    protected Log log;
    protected ComponentHelper helper = new ComponentHelper(this);
    protected String outputDir;
    protected List results=new ArrayList();
    protected boolean dirty=true;
    private Statistics total = new Statistics();


    public XmlTestIndexImpl() throws RemoteException {
    }

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
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        String dir = FileSystem.lookupAbsolutePath(this,
                ATTR_OUTPUT_DIRECTORY,
                null,
                null,
                true,
                null);
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
        ResultSet rs = new ResultSet(suite,hostname, processname,suitename, filename);
        results.add(rs);
        dirty=true;
    }


    /**
     *  This is currently unused. What is it for?
     */
/*

    public static class BackgroundUpdateThread implements Runnable {
        
        int sleepIntervalMillis;
        Thread thread;
        boolean shouldExit;
        
        
        public synchronized void terminate() {
            shouldExit=true;
            thread.interrupt();
        }
        

        public void run() {
            
            
            
        }
    }
    
*/

    /**
     * run through all the results; return true if any one of them changed
     * (which implies a rebuild of the page is needed)
     * @return true if a result changed
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    public boolean updateResults() throws SmartFrogResolutionException,
            RemoteException {
        total = new Statistics();
        boolean changed=false;
        for (Object result : results) {
            ResultSet resultSet = (ResultSet) result;
            changed |= resultSet.update(total);
        }
        return changed;
    }

    public static final class ResultSet implements Serializable {
        public Statistics statistics;
        public TestSuite suite;
        public String hostname;
        public String processname;
        public String suitename;
        public File filename;

        public ResultSet() {
        }

        public ResultSet(
                         TestSuite suite,
                         String hostname,
                         String processname, String suitename, File filename) {
            this.suite = suite;
            this.hostname = hostname;
            this.processname = processname;
            this.suitename = suitename;
            this.filename = filename;
            statistics = new Statistics();
        }

        public boolean isFinished() {
            return suite==null;
        }

        public synchronized boolean update(Statistics total) throws SmartFrogResolutionException,
                RemoteException {
            boolean changed=false;
            if(suite!=null) {
                Statistics updated=new Statistics();
                boolean finished=updated.retrieveResultAttributes((Prim) suite);
                if(finished) {
                    //stop updating
                    suite=null;
                }
                if(!statistics.isEqual(updated)) {
                    statistics=updated;
                    changed=true;
                }
            }
            total.add(statistics);
            return changed;
        }
    }



}
