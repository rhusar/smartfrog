/** (C) Copyright Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.anubisdeployer;

import java.rmi.RemoteException;

import org.smartfrog.services.display.SFDisplay;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import java.awt.BorderLayout;

public class ClusterDisplay
    extends SFDisplay implements Prim, ClusterStatus {

    ClusterMonitor clusterMonitor;
    ClusterPane clusterPane;

    public ClusterDisplay() throws RemoteException {
    }

    public synchronized void sfDeploy() throws SmartFrogException,
        RemoteException {
        super.sfDeploy();
        clusterMonitor = (ClusterMonitor)sfResolve("clusterMonitor", true);
        try {
            clusterPane = new ClusterPane();
            display.tabPane.add(clusterPane, "Cluster View", 0);
            display.tabPane.setSelectedIndex(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public synchronized void sfStart() throws SmartFrogException,
        RemoteException {
        super.sfStart();
        clusterMonitor.registerForClusterStatus(this);
    }

    public synchronized void sfTerminateWith(TerminationRecord t) {
        try {
            clusterMonitor.deregisterForClusterStatus(this);
        } catch (Exception e) {}
    }

    public void clusterStatus(ComponentDescription d) throws RemoteException {
        display.setTextScreen(d.toString());
        //Data Example
        Object[][] dataSet = new Object[][] { {"cero", new Integer(0),
            new Integer(0)}, {"uno", new Integer(76), new Integer(1)}, {"dos",
            new Integer(2), new Integer(2)}, {"tres", new Integer(100),
            new Integer(3)}, {"cuatro", new Integer(100), new Integer(4)},
            {"cinco", new Integer(100), new Integer(5)}, {"seis",
            new Integer(100), new Integer(6)}, {"siete", new Integer(100),
            new Integer(7)}, {"ocho", new Integer(100), new Integer(8)},
            {"nueve", new Integer(100), new Integer(9)}, {"diez", new Integer(4),
            new Integer(10)}, {"once", new Integer(4), new Integer(11)}
        };

        Object[] headers = new Object[] {"Machine", "Role", "Cluster"};

        try {
            clusterPane.setData(dataSet, headers);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
