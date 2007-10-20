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

package org.smartfrog.services.logger;


import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 * Logger Test component
 */ 
public class LoggerTest extends PrimImpl implements Prim {

    private SFLogger logger = null;

    /**
     * Constructs object
     *
     * @throws RemoteException in case of network/rmi error
     */
    public LoggerTest() throws RemoteException {
    }
    
    /**
     * Starts the Logger component.
     *
     * @throws SmartFrogException in case of error in deploying
     * @throws RemoteException in case of network/emi error
     */ 
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
        //read logger attribute
        logger = getDefaultLogger();
        if (logger == null) {
            return;
        }
        // name
        String name = (String) sfResolve("sfProcessComponentName", false);
        String host = sfResolve("sfProcessHost","localhost", false);
        String id = name + ":" + host;

        //now test the proxy stuff
        LoggerProxy proxy = new LoggerProxy(logger);
        //being a test and all, we check the flags match
        assert proxy.isDebug() == logger.isDebug();

        proxy.logInfo(id +":==>Info msg1");
        proxy.logWarning(id +":==>Warning msg1");
        proxy.logInfo(id +":==>Info msg1");
        proxy.logError(id +":==>Log Error1");
        proxy.logDebug(id + ":==>Log Debug");

        proxy.flush();



    }
    
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        super.sfTerminateWith(tr);
    }
    /**
     *
     */
    protected SFLogger getDefaultLogger() throws SmartFrogException, 
                                                              RemoteException{
        SFLogger log = (SFLogger) sfResolve("mylogger", false);
        return log;
    }
}
