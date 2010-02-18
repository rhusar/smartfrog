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
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.RootLocator;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.net.InetAddress;

/**
 * @author steve loughran
 *         created 19-Mar-2004 17:42:51
 */

public class NoProcessTest extends TestCase {


    public void testNoProcessDetected() throws Exception {
        assertNull(SFProcess.getProcessCompound());
        RootLocator locator=SFProcess.getRootLocator();
        InetAddress self= InetAddress.getByName("127.0.0.1");
        ProcessCompound process = null;
        try {
            process = locator.getRootProcessCompound(self);
            fail("we should have got an error");
        } catch (Exception e) {
            assert(e.getMessage().contains("Connection refused"));
        } finally {
            //TODO: clean up?
        }
    }
}
