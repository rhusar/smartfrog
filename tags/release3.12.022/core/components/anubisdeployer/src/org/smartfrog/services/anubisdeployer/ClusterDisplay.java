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
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import java.awt.BorderLayout;
import java.util.Hashtable;
import java.util.Enumeration;

public class ClusterDisplay
    extends SFDisplay implements Prim, ClusterStatus {

    ClusterMonitor clusterMonitor;
    ClusterPane clusterPane;

    // /////////////////////////////////////
    String freeRoleString = "free";
    // /////////////////////////////////////
    //how to handle service colours...
    // free = 0
    // others: (index mod (maxC-1)) + 1
    int colourIndex = 1;
    int maxColourIndex = 9;
    Integer freeColour = new Integer(0);
    Integer nextColour() {
        colourIndex++;
        if (colourIndex > maxColourIndex) colourIndex = 1;
        return new Integer(colourIndex);
    }
    // /////////////////////////////////////
    Integer getColour(String service) {
	int hc = service.hashCode();
	hc = hc < 0 ? -hc : hc;
	//System.out.println("service: " + service +" code: " + ((hc % maxColourIndex) +1));
	return new Integer((hc % maxColourIndex) +1 );
    }
    // /////////////////////////////////////

    public ClusterDisplay() throws RemoteException {
//        super();
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

        //System.out.println("setting cluster status");
        display.setTextScreen(d.toString());

        //System.out.println("creating cluster table");
        int size = d.sfContext().size();
        Object[][] dataSet = new Object[size][3];
           /*{ {"cero", new Integer(0),
            new Integer(0)}, {"uno", new Integer(76), new Integer(1)}, {"dos",
            new Integer(2), new Integer(2)}, {"tres", new Integer(100),
            new Integer(3)}, {"cuatro", new Integer(100), new Integer(4)},
            {"cinco", new Integer(100), new Integer(5)}, {"seis",
            new Integer(100), new Integer(6)}, {"siete", new Integer(100),
            new Integer(7)}, {"ocho", new Integer(100), new Integer(8)},
            {"nueve", new Integer(100), new Integer(9)}, {"diez", new Integer(4),
            new Integer(10)}, {"once", new Integer(4), new Integer(11)}
            };
            */

        Object[] headers = new Object[] {"Machine", "Role", "Service"};

        //iterate through the cd, setting the host, the role, and the colour...
        int index = 0;
        for (Enumeration e = d.sfContext().keys(); e.hasMoreElements(); ) {
            //System.out.println("next node");
            Object hostname = e.nextElement();
            dataSet[index][0] = hostname;

            ComponentDescription c = (ComponentDescription)d.sfContext().get(hostname);
            Object role = freeRoleString;
            Object colour = freeColour;

            try {
                //System.out.println("checking reservation");

                ComponentDescription reservations = (ComponentDescription) c.sfResolve("reservations", false);
                if (reservations.sfContext().size() > 0) {
                    //System.out.println("found reservation");

                    //pull out the first one, get service and role
                    String serviceId = (String) reservations.sfAttributes().next();
                    colour = getColour(serviceId.substring(0, serviceId.indexOf('.')));

                    role = ((ComponentDescription) ((ComponentDescription)reservations
                               .sfContext().get(serviceId))
                               .sfContext().get("description"))
                               .sfContext().get("role");
                } // else leave as default free swettings
            } catch (SmartFrogResolutionException e1) {
                //shouldn't happen...
            }

            dataSet[index][1] = role;
            dataSet[index][2] = colour;
            index++;
        }
        //System.out.println("fininshed nodes, uopdating pane");

        try {
            clusterPane.setData(dataSet, headers);
        } catch (Exception ex1) {
        }
        //System.out.println("done updating the pane");

    }
}
