/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

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

package org.smartfrog.vast.testing.shared;

import org.smartfrog.vast.testing.networking.messages.VastMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SUTAction extends Remote {
	public static final String ATTR_HOST = "Host";
	public static final String ATTR_WAIT = "Wait";
	public static final String ATTR_NAME = "Name";
	public static final String ATTR_PROCESS_NAME = "ProcessName";


	public String getName() throws RemoteException;

	public int getWait() throws RemoteException;

	public String getHost() throws RemoteException;

	public VastMessage getActionMessage() throws RemoteException;

	public String getProcessName() throws RemoteException;
}
