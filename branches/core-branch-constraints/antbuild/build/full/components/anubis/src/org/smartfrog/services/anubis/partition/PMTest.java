/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.anubis.partition;


import java.rmi.RemoteException;

import org.smartfrog.services.anubis.partition.comms.MessageConnection;
import org.smartfrog.services.anubis.partition.views.View;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class PMTest extends PrimImpl implements Prim, PartitionNotification {

    class Tester extends Thread {
        Partition         partition    = null;
        int               remote       = 0;
        Tester(Partition p, int r) {
            super("Anubis: Test Driver");
            partition = p;
            remote = r;
        }
        public void run() {
            MessageConnection c = partition.connect(remote);
            c.sendObject("Hello World");
            c.sendObject("Goodbye World");
            // c.disconnect();
        }
    }

    boolean   done      = false;
    Partition partition = null;
    String    myName    = null;
    int       remote    = -1;

    public PMTest() throws RemoteException {
        super();
        System.out.println("Created test");
    }

    public void sfDeploy() throws SmartFrogException, RemoteException  {
        super.sfDeploy();
        partition = (Partition)sfResolve("partition");
        myName = sfCompleteName().toString();
        partition.register(this);
        try { remote = ((Integer)sfResolve("remote")).intValue(); }
        catch (Exception ex) { remote = -1; done = true; }
        System.out.println("Deployed test");
    }

    public void sfStart() throws SmartFrogException, RemoteException  {
        super.sfStart();
        System.out.println(myName + "Started test");
    }

    public void sfTerminateWith(TerminationRecord status) {
        System.out.println(myName + "Terminating test");
        super.sfTerminateWith(status);
    }

    public void partitionNotification(View view, int leader) {
        System.out.println(myName + "Test: notification " + view + " leader is " + leader);
        if( !done && view.contains(remote)) {
            done = true;
            new Tester(partition, remote).start();
        }
    }

    public void objectNotification(Object obj, int node, long time) {
        System.out.println(myName + "Test: received object " + obj);
    }


}
