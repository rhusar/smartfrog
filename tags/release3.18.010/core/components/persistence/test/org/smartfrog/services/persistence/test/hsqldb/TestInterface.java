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

package org.smartfrog.services.persistence.test.hsqldb;

import java.rmi.RemoteException;
import java.rmi.Remote;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

/**
 * This is a interface for activator thread which will takes care of the life cycle
 * of the recoverable components.
 * 
 */
public interface TestInterface extends Prim ,Remote  {

	
	public void activate()throws RemoteException,
			SmartFrogException;

	public void deactivate()throws RemoteException,
			SmartFrogException;

	public void  simulateFailover() throws RemoteException,
			SmartFrogException;

	public void waitForActivation() throws RemoteException,
			SmartFrogException;

}