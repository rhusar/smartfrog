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
package org.smartfrog.test.process;

import junit.framework.TestCase;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.test.unit.sfcore.StartDaemon;

import java.net.InetAddress;

/**
 * created Oct 14, 2005 4:14:04 PM
 */

public class DaemonTest extends TestCase {

    private static final String home = System.getProperty("test.smartfrog.dist.dir");

    public DaemonTest(String name) {
        super(name);
    }

    public void testStartDaemon() throws Exception {
        StartDaemon obj = new StartDaemon(home);
        ProcessCompound daemon = StartDaemon.getSFDaemon();
    }

    public void testStopDaemon() throws Exception {
        TerminationRecord tr = new TerminationRecord("Process Terminated", null, null);
        ProcessCompound sfDaemon = SFProcess.getRootLocator().getRootProcessCompound(SFProcess.sfDeployedHost(), 3800);
        TerminatorThread terminator = new TerminatorThread(sfDaemon, tr).detach().quietly();
        terminator.start();
    }
}
