/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.persistence.framework.interfaceguard;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.smartfrog.services.persistence.framework.activator.PendingTermination;
import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;

/**
 * <p>The interface guard is used to guard access to interface in persistence components.
 * The guard can be open or closed. Open indicates that recoverable components may make their
 * external interfaces available for access. Closed means the recoverable components should
 * not accept remote method invocations. The ability to cut off external interfaces is a key
 * part of the sand boxing behaviour of the persistence framework. The interface guard operates
 * as a semaphore so that all components will observe the closing or opening of the interfaces
 * at the same time.</p>
 * <p>For convienience the interface guard also contains the pending termination list</p>
 */
public class InterfaceGuardImpl implements InterfaceGuardSetter, InterfaceGuard, PendingTermination {
    
    private Set pending = new HashSet();
    private boolean isOpen = false;
    private long openCloseEventCounter = 0;
    private LogSF log = LogFactory.getLog(this.getClass().toString());
    
    /* (non-Javadoc)
     * @see com.hp.smartfrog.services.persistence.database.InterfaceGuard#isOpen()
     */
    public synchronized boolean isOpen() {
        return isOpen;
    }
    
    /* (non-Javadoc)
     * @see com.hp.smartfrog.services.persistence.database.InterfaceGuard#isClosed()
     */
    public synchronized boolean isClosed() {
       return !isOpen;
    }
    
    /* (non-Javadoc)
     * @see com.hp.smartfrog.services.persistence.database.InterfaceGuardSetter#open()
     */
    public synchronized void open() {
        if( isOpen ) {
            return;
        }
        isOpen = true;
        openCloseEventCounter++;
        if( log.isDebugEnabled() ) {
            log.debug("Interface Guard: The interfaces are set to OPEN");
        }
    }
    
    /* (non-Javadoc)
     * @see com.hp.smartfrog.services.persistence.database.InterfaceGuardSetter#close()
     */
    public synchronized void close() {
        if( !isOpen ) {
            return;
        }
        isOpen = false;
        openCloseEventCounter++;
        
        if( log.isDebugEnabled() ) {
            log.debug("Interface Guard: The interfaces are set to CLOSED");
        }
        
        /**
         * unload components in the pending state - these will not be caught 
         * by traversing from the process compound as they are 
         * dangling in space.
         */
        unloadPending();
    }

    /* (non-Javadoc)
     * @see com.hp.smartfrog.services.persistence.database.InterfaceGuardSetter#add()
     */
    public synchronized void add(RComponent rcomponent) {
        pending.add(rcomponent);
    }

    /* (non-Javadoc)
     * @see com.hp.smartfrog.services.persistence.database.InterfaceGuardSetter#remove()
     */
    public synchronized void remove(RComponent rcomponent) {
        pending.remove(rcomponent);
    }
    
    /**
     * unload components in the pending state - these will not be caught by
     * traversing from the process compound as they are dangling in space.
     */
    private void unloadPending() {
        Iterator iter = pending.iterator();
        while( iter.hasNext() ) {
            try {
                ((RComponent)iter.next()).sfUnload();
            } catch (RemoteException e) {
                if( log.isErrorEnabled() ) {
                    log.error("RemoteException when unloading a component pending termination - ignoring and continue", e);
                }
            }
        }
        pending.clear();
    }
}
