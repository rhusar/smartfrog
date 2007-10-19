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
package org.smartfrog.test.system.sfcore.languages.cdl.execute;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.test.unit.sfcore.languages.cdl.Filenames;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * created 31-Jan-2006 13:43:11
 */

public abstract class CdlDeployingTestBase extends DeployingTestBase implements Filenames {
    public static final int SPIN_LAG = 1000;
    public static final String FILES = "files/sfcdl/";
    public static final String VALID= FILES + "valid/";
    public static final String INVALID = FILES + "invalid/";
    public static final int DEFAULT_TIMEOUT = 30;

    public CdlDeployingTestBase(String name) {
        super(name);
    }

    protected void deployAndTerminate(String name) throws Throwable {
        Prim prim=null;
        try {
            prim = deployExpectingSuccess(getResourceBase() + name , name);
            assertLivenessSuccess(prim);
            assertLivenessSuccess(prim);
            assertLivenessSuccess(prim);
            assertLivenessSuccess(prim);
            assertLivenessSuccess(prim);
        } finally {
            terminate(prim);
        }
    }

    protected String getResourceBase() {
        return CdlComponentTest.VALID_CDL_FILES;
    }

    public void terminate(Prim prim) throws RemoteException {
        terminateWithSpin(prim,DEFAULT_TIMEOUT);
    }

    public void terminateWithSpin(Prim prim, int timeout) throws RemoteException {
        if (prim != null) {
            terminateApplication(prim);
            boolean ok = spinUntilTerminated(prim, timeout);
            assertTrue("Failed to terminate application",ok);
        }
    }

    protected boolean spinUntilTerminated(Prim prim,int timeout) throws RemoteException {
        try {
            long timeLimit =System.currentTimeMillis()+timeout*1000;
            boolean tooLong =false;
            while(!prim.sfIsTerminated() && !tooLong) {
                Thread.sleep(SPIN_LAG);
                tooLong =System.currentTimeMillis()>timeLimit;
            }
            return !prim.sfIsTerminated();
        } catch (InterruptedException e) {
            return false;
        } catch (java.rmi.NoSuchObjectException noSuchObject) {
            //hey, we are not here any more.
            return true;
        }
    }
}
