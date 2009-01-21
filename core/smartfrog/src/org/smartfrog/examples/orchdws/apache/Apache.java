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

package org.smartfrog.examples.orchdws.apache;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * This interface defines the methods that can be called upon Apache by a
 * Remote client.
 */
public interface Apache extends Remote {
    public final String LOCATION = "location";
    public final String BASECONFIGLOCATION = "baseConfigLocation";
    public final String CONFIGLOCATION = "configLocation";
    public final String BASECONFIGFILENAME = "baseConfigFileName";
    public final String CONFIGFILENAME = "configFileName";
    public final String ENVVARS = "envVars";
    public final String APACHECTLLOCATION = "apachectlLocation";
    public final String INTERCHECKTIME = "interCheckTime";
    public final String LOGTO = "logTo";
    public final String MANAGEDAEMON = "manageDaemon";
    public final String SFSERVERINSTANCE = "instance";

    /**
     * As the method name indicates, this simply starts the httpd daemon
     * @throws RemoteException in case of Remote/network error
     */
    public void startDaemon() throws RemoteException;

    /**
     * As the method name indicates, this simply stops the httpd daemon
     * @throws RemoteException in case of Remote/network error
     */
    public void stopDaemon() throws RemoteException;

    /**
     * This refreshes the httpd daemon and therfore updates the current
     * configuration. (Activates any changes))
     * @throws RemoteException in case of Remote/network error
     */
    public void refreshDaemon() throws RemoteException;

    /**
     * This would be used to reset/update the configuration of Apache.
     * @param scriptURL script url
     * @param fileName file name
     * @throws RemoteException in case of Remote/network error
     */
    public void refreshConfig(String scriptURL, String fileName)
        throws RemoteException;
    
    public void setApacheState(boolean newState) throws RemoteException;
}
