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

package org.smartfrog.examples.dynamicwebserver.stresstest;

import java.rmi.RemoteException;

import org.smartfrog.examples.dynamicwebserver.logging.LogWrapper;
import org.smartfrog.examples.dynamicwebserver.logging.Logger;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;


/**
 * Description of the Class
 */
public class StressControlGuiImpl extends PrimImpl implements Prim,
    StressControlGui {
    /*
     *  The reference to the object controlling the stress test clients
     */
    StressManagerFrame gui;
    String name;
    String title;
    int sets;
    String positionDisplay = "SW";
    LogWrapper logger;

    /**
     * Constructor for the StressTesterImpl object
     *
     * @exception RemoteException Description of the Exception
     */
    public StressControlGuiImpl() throws RemoteException {
    }

    public boolean registerStressClient(StressTester stressComp) {
        String nameComp = "default";

        try {
            nameComp = ((Prim) stressComp).sfCompleteName().toString();
            gui.register(nameComp, stressComp);

            return true;
        } catch (Exception e) {
            logger.err(name,
                "registration failed: " + nameComp + ", " + e.getMessage());
        }

        return false;
    }

    public boolean deregisterStressClient(StressTester stressComp) {
        String nameComp = "default";

        try {
            nameComp = ((Prim) stressComp).sfCompleteName().toString();
            gui.deRegister(nameComp);

            return true;
        } catch (Throwable e) {
            logger.err(name,
                "deregistration failed: " + nameComp + ", " + e.getMessage());
        }

        return false;
    }

    public void setFrequency(int freq) {
        gui.setFrequency(freq);
    }

    /**
     * Standard sfDeploy()
     *
     * @exception SmartFrogException Description of the Exception
     * @throws RemoteException DOCUMENT ME!
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        name = sfCompleteName().toString();

        /*
         *  any component specific init code
         */
        title = sfResolve(TITLE, "", true);
        sets = sfResolve(INITIALVALUE, sets, false);
        logger = new LogWrapper((Logger) sfResolve(LOGTO, false));

        positionDisplay = sfResolve(POSITION_DISPLAY, positionDisplay, false);

        gui = new StressManagerFrame(title);
        gui.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.smartfrog.services.display.WindowUtilities.setPositionDisplay(null,
            gui, positionDisplay);
    }

    /**
     * Standard sfStart()
     *
     * @exception SmartFrogException Description of the Exception
     * @throws RemoteException DOCUMENT ME!
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        gui.setValue(sets);
        gui.setVisible(true);
        super.sfStart();
    }

    /**
     * Standard sfTerminateWith()
     *
     * @param tr Description of the Parameter
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        //terminate the thread nicely if needed
        //... could interrupt its sleep, but not necessary
        //in general, since the thread initiates the termination, this will be irrelevant
        //but do so in case termination is through error or management action
        gui.dispose();
        super.sfTerminateWith(tr);
    }
}
