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
package org.smartfrog.services.hadoop.components.dfs;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.tools.DFSck;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Deploy a filesystem check
 */

public class DfsFsckImpl extends DfsOperationImpl {

    public DfsFsckImpl() throws RemoteException {
    }


    /**
     * call {@link DFSck} to check the filesystem. This may take an indeterminate amount of time.
     *
     * @throws Exception on any failure
     */
    @Override
    protected void performDfsOperation(FileSystem fileSystem,
                                       ManagedConfiguration conf)
            throws Exception {
        DFSck fsck = new DFSck(conf);
        List<String> args = new ArrayList<String>();
        addIfSet(args, "move");
        addIfSet(args, "delete");
        addIfSet(args, "blocks");
        addIfSet(args, "locations");
        addIfSet(args, "racks");
        addIfSet(args, "openForWrite", "-openforwrite");
        String[] argv = args.toArray(new String[args.size()]);
        fsck.run(argv);
    }

    /**
     * add an attribute if it resolves to a boolean
     * @param args argument list to add to
     * @param attribute the attribute
     * @throws SmartFrogResolutionException no resolution
     * @throws RemoteException network trouble
     */
    private void addIfSet(List<String> args, String attribute)
            throws SmartFrogResolutionException, RemoteException {
        addIfSet(args, attribute, "-" + attribute);
    }

    /**
     * Add an argument if set
     * @param args argument list to add to
     * @param attribute the attribute
     * @param argument the argument to append
     * @throws SmartFrogResolutionException no resolution
     * @throws RemoteException network trouble
     */
    private void addIfSet(List<String> args, String attribute, String argument)
            throws SmartFrogResolutionException, RemoteException {
        if (sfResolve(attribute, true, true)) {
            args.add(argument);
        }
    }


}
