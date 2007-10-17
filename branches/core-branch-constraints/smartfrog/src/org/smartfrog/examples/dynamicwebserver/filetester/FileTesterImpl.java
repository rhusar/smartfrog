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

package org.smartfrog.examples.dynamicwebserver.filetester;

import java.io.File;
import java.rmi.RemoteException;

import org.smartfrog.examples.dynamicwebserver.logging.Logger;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventPrimImpl;


public class FileTesterImpl extends EventPrimImpl implements FileTester, Prim {
    TerminationRecord t;

    public FileTesterImpl() throws RemoteException {
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        String filename;
        Logger logger;
        Reference name = sfCompleteName();

        filename = sfResolve(FILENAME, "", true);
        logger = (Logger) sfResolve(LOGTO, false);

        File f = new File(filename);

        if (f.exists()) {
            t = TerminationRecord.normal(name);

            if (logger != null) {
                logger.log(name.toString(), "file " + filename + "exists");
            }
        } else {
            t = TerminationRecord.abnormal("file not found", name);

            if (logger != null) {
                logger.log(name.toString(),
                    "file " + filename + "does not exist");
            }
        }

        Runnable terminator = new Runnable() {
                public void run() {
                    sfTerminate(t);
                }
            };

        new Thread(terminator).start();
    }
}
