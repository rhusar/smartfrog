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

package org.smartfrog.examples.orchdws.balancer;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * <p>
 * Description: Balancer defines the remote interface to the balancer
 * component, allowing a client to add to or remove servers from the set of
 * servers that the balancer uses to balance the load from clients.
 * </p>
 *
 */
public interface Balancer extends Remote {
    //
    // Definition of SmartFrog parameter names for the component
    public final String NAME = "name";
    public final String PORT = "port";
    public final String HOSTSPORT = "hostsPort";
    public final String LOGTO = "logTo";
    public final String SERVERHOSTS = "serverHosts";
    public final String LBBPREFIX = "lbbs:server";
    public final String LBBSUFFIX = ":binding";
    public final String TIMER = "timerstop";
    public final String SLEEP = "sleep";
    
    public void enableServerInstance(int instance) throws RemoteException;
    public boolean disableServerInstance(int instance) throws RemoteException;
    public String lookUpHost(int instance) throws RemoteException;
}
