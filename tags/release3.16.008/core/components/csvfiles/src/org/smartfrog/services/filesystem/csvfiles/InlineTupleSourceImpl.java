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
package org.smartfrog.services.filesystem.csvfiles;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.filesystem.TupleDataSource;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Iterator;

/**
 * Created 22-Sep-2008 17:11:19
 */

public class InlineTupleSourceImpl extends PrimImpl implements TupleDataSource {

    Vector<Vector<String>> data;
    private static final Reference refData = new Reference("data");
    private Iterator<Vector<String>> iterator;

    public InlineTupleSourceImpl() throws RemoteException {

    }

    /**
     * Get the next line
     *
     * @return the next line, all broken up, or null for no new lines.
     * @throws RemoteException    network problems
     * @throws SmartFrogException parsing/file IO problems
     */
    public String[] getNextTuple() throws RemoteException, SmartFrogException {
        if(iterator==null || !iterator.hasNext()) {
            return null;
        }
        Vector<String> row = iterator.next();
        return row.toArray(new String[row.size()]);
    }

    /**
     * Go back to the start of the file
     *
     * @throws RemoteException    network problems
     * @throws SmartFrogException parsing/file IO problems
     */
    public void start() throws RemoteException, SmartFrogException {
        data = ListUtils.resolveStringNTupleList(this, refData, -1, true);
        iterator = data.iterator();
    }

    /**
     * Close the reader. harmless if we are already closed
     *
     * @throws RemoteException    network problems
     * @throws SmartFrogException parsing/file IO problems
     */
    public void close() throws RemoteException, SmartFrogException {
        data = null;
        iterator = null;
    }
}
