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

package org.smartfrog.services.persistence.framework.connectionpool;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface is used to manage the size of a connection pool.
 */
public interface ConnectionPoolSize extends Remote {
    /**
     * release one connection from the connection pool. this will reduce the
     * connection pool size by one and actual drop a connection from the pool. If 
     * the connection pool is closed or has a maximum size of 1 this has no effect.
     * 
     * @return true if it was done, false if not
     * @throws RemoteException
     */
    public boolean freeOneConnection() throws RemoteException;
    
    /**
     * reset the connection pool size to the original settings.
     * @throws RemoteException
     */
    public void resetMaxSize() throws RemoteException;
    
    /**
     * get the current connection pool size. this is the max size limit,
     * not the number that are actually in the pool.
     * 
     * @return size
     * @throws RemoteException
     */
    public int getCurrentMaxSize() throws RemoteException;
    
    /**
     * get the default connection pool size. this is the size defined in
     * the configuration and is the size that would be set by the reset method.
     * 
     * @return size
     * @throws RemoteException
     */
    public int getDefaultMaxSize() throws RemoteException;
}
