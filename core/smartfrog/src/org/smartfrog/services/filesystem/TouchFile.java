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
package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This is a minimal component whose aim in life is to touch files.
 * It has no life after starting, and no remote interface.
 * created 19-Apr-2004 13:57:24
 */

public class TouchFile extends PrimImpl {

    public TouchFile() throws RemoteException {
    }

    public final static String FILENAME = "file";
    public final static String AGE = "age";


    /**
     * this is called at runtime
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        //mark us as not existing beyond this call.
        targetForTermination();
        //get the file
        String file = sfResolve(FILENAME, (String) null, true);
        long age = -1;
        age = sfResolve(AGE, age, false);
        try {
            touch(file, age);
        } catch (IOException e) {
            throw SmartFrogException.forward(e);
        }
    }

    /**
     * mark this task for termination by spawning a separate thread to do it.
     * as {@link Prim#sfTerminate} and {@link Prim#sfStart()} are synchronized,
     * the thread blocks until sfStart has finished.
     * Note that we detach before terminating; this stops our timely end propagating.
     */
    private void targetForTermination() {
        //spawn the thread to terminate normally
        Runnable terminator = new Runnable() {
            public void run() {
                Reference name;
                try {
                    name = sfCompleteName();
                } catch (RemoteException e) {
                    name = null;

                }
                sfDetachAndTerminate(TerminationRecord.normal(name));
            }
        };

        new Thread(terminator).start();
    }

    /**
     * touch a file
     *
     * @param filename file to create
     * @param age      timestamp (optional, use -1 for current time)
     * @throws IOException
     */
    protected void touch(String filename, long age) throws IOException {
        File file = new File(filename);
        file.createNewFile();
        if (age >= 0) {
            file.setLastModified(age);
        }
    }
}
