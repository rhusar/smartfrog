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

package org.smartfrog.examples.arithnet;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * Defines the delay component.
 */ 
public class Delay extends NetElemImpl implements Remote {
    private int delay = 10;
    
    /**
     * Constructs the Delay object
     * @throws RemoteException if fails to Constructs the object remotely
     * 
     */
    public Delay() throws java.rmi.RemoteException {
    }
    
    /**
     * Dummy implementation of the evaluate. Makes the thread to sleep for 
     * delay seconds.
     * @param from placeholder for the type
     * @param value integer value to be evaluated 
     * @return returns the same value
     */
    protected int evaluate(String from, int value) {
        try {
            Thread.sleep(delay * 1000);
        } catch (Exception e) {
            // ignore
        }

        return value;
    }
    
    /**
     * Deploys the component
     * @throws SmartFrogException if framework is unable to deploy the 
     * component 
     * @throws RemoteException if remote or network error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        // get optional attribute "delay"
        delay = sfResolve("delay", delay, false);
    }
}
