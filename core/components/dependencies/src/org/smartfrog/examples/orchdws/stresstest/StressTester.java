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


package org.smartfrog.examples.orchdws.stresstest;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Description of the Interface
 */
public interface StressTester extends Remote {
    public final String HOST = "host";
    public final String HOSTS = "hosts";
    public final String PORT = "port";
    public final String PAGE = "page";
    public final String FREQUENCY = "frequency";
    public final String FACTOR = "factor";
    public final String NUMHITS = "numHits";
    public final String CONTROLGUI = "controlGui";
    public final String LOGTO = "logTo";
    public final String STRESSINGENABLED = "stressingEnabled";

    /**
     * Sets the page attribute of the StressTester object
     *
     * @param page The new page value
     *
     * @exception RemoteException Description of the Exception
     */
    public void setPage(String page) throws RemoteException;

    /**
     * Sets the frequency attribute of the StressTester object
     *
     * @param frequency The new frequency value
     *
     * @exception RemoteException Description of the Exception
     */
    public void setFrequency(int frequency) throws RemoteException;

    /**
     * Stops thread
     *
     * @exception RemoteException Description of the Exception
     */
    public void stop() throws RemoteException;
}
