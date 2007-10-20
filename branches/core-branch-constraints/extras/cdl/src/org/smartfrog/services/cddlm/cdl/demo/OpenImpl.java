/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.cddlm.cdl.demo;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.io.IOException;

/**

 */
public class OpenImpl extends PrimImpl implements Open {
    public static final String DEFAULT_EXECUTABLE = "open";

    public OpenImpl() throws RemoteException {
    }


    private String executable=null;
    private String filename;


    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
        executable = sfResolve(ATTR_EXECUTABLE, executable, false);
        if(executable==null || executable.length()==0) {
            executable=DEFAULT_EXECUTABLE;
        }
        filename = sfResolve(ATTR_FILENAME, filename, false);

        if (executable == null || executable.length() == 0) {
            throw new SmartFrogDeploymentException("No executable");
        }
    }

    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        int outcome=0;
        if(filename!=null) {
            outcome=open(filename);
        }
        ComponentHelper helper = new ComponentHelper(this);
        helper.sfSelfDetachAndOrTerminate(
                outcome==0?TerminationRecord.NORMAL
                :TerminationRecord.ABNORMAL,
                "Opened",
                null,
                null);
    }

    public int open(String filename) throws SmartFrogException,RemoteException {

        ProcessBuilder builder = new ProcessBuilder(executable, filename);
        Process process=null;
        try {
            process = builder.start();
            return process.waitFor();

        } catch (IOException e) {
            throw new SmartFrogException("Failed to run "+builder.toString(),e);
        } catch(InterruptedException e) {
            throw new SmartFrogException("Failed to run " + builder.toString(),e);
        } finally {
            if(process!=null) {
                process.destroy();
            }
        }
    }

}
