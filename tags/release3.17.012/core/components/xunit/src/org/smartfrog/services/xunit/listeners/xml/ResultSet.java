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
package org.smartfrog.services.xunit.listeners.xml;

import org.smartfrog.services.xunit.serial.Statistics;
import org.smartfrog.services.xunit.base.TestSuite;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;

import java.io.Serializable;
import java.io.File;
import java.rmi.RemoteException;

/**
 * This class contains results
 */
public final class ResultSet implements Serializable {
    public Statistics statistics;
    public TestSuite suite;
    public String hostname;
    public String processname;
    public String suitename;
    public File filename;

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

    /**
     * Test for being finished.
     * @return true if we are considered finished.
     */
    public boolean isFinished() {
        return suite==null;
    }

    /**
     * Update the results if the statistics have changed; synchronized.
     * The statistics are retrieved from the test suite we are bound to; if changed
     * the total is updated.
     * @param ongoingTotal total statistics; this is updated as the operation proceeds
     * @return true if the results are newer.
     * @throws SmartFrogResolutionException a failure to retrieve the existing results
     * @throws RemoteException network problems
     */
    public synchronized boolean update(Statistics ongoingTotal) throws SmartFrogResolutionException,
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
        ongoingTotal.add(statistics);
        return changed;
    }
}
