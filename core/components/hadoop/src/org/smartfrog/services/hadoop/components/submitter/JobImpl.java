/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.components.submitter;

import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;

import java.rmi.RemoteException;

/**
 * A Job to submit
 */

public class JobImpl extends FileUsingComponentImpl implements Job {

    public JobImpl() throws RemoteException {
    }

    /**
     * binds to a file and asserts that it exists. Could do other job validation, but this would make it more brittle to
     * change
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        boolean fileRequired=sfResolve(ATTR_FILEREQUIRED,true,true);
        //bind the file
        if(fileRequired) {
            bind(true, null);
            if (!getFile().exists()) {
                throw new SmartFrogLifecycleException("Missing JAR file " + getFile());
            }
            sfReplaceAttribute(MAPRED_JAR,getFile().toString());
        }
    }
}
