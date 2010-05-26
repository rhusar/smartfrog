/** (C) Copyright 1998-2009 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.dependencies.statemodel.state;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

/**
 */
public interface StateChangeNotification extends Remote {
   //child down to State, where it is handled
   public void handleStateChange() throws RemoteException, SmartFrogException;
   public boolean isThreadedComposite() throws RemoteException, SmartFrogException;
   public String getDesiredStatusAsString() throws RemoteException, SmartFrogResolutionException;
   public String getModelInfoAsString(String refresh) throws RemoteException, SmartFrogResolutionException;
    public String getTransitionLogAsString() throws RemoteException, SmartFrogResolutionException;

   public String getServiceStateDetails() throws RemoteException, SmartFrogResolutionException;
    //public String getTerminationDetails() throws RemoteException, SmartFrogResolutionException;
    public String getServiceStateObserved(String key) throws RemoteException, SmartFrogResolutionException;
    public String getServiceStateDesired(String key) throws RemoteException, SmartFrogResolutionException;
    public String getServiceStateContainer() throws RemoteException, SmartFrogResolutionException;


}
