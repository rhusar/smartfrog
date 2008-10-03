/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.components.cluster;

import org.apache.hadoop.net.NetUtils;
import org.smartfrog.services.hadoop.components.HadoopConfiguration;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * Created 28-May-2008 15:30:01
 */

public class CheckPortImpl extends HadoopComponentImpl implements HadoopConfiguration, CheckableCondition {

    /**
     * {@value}
     */
    public static final String ATTR_CLUSTER = "cluster";
    /**
     * {@value}
     */
    public static final String ATTR_ADDRESS_ATTRIBUTE = "attribute";


    /**
     * If non null, takes priority over anything else
     * {@value}
     */
    public static final String ATTR_ADDRESS = "address";

    /**
     * {@value}
     */
    public static final String ATTR_CONNECT_TIMEOUT = "connectTimeout";
    /**
     * {@value}
     */
    public static final String ATTR_LIVENESS_TIMEOUT = "livenessTimeout";
    private InetSocketAddress address;
    private int connectTimeout;
    private boolean checkOnLiveness;
    private boolean checkOnStartup;
    private long livenessTimeout;

    public CheckPortImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        ManagedConfiguration configuration = createConfiguration(ATTR_CLUSTER);
        connectTimeout = sfResolve(ATTR_CONNECT_TIMEOUT, 0, true);
        int timeout = sfResolve(ATTR_LIVENESS_TIMEOUT, 0, true);
        if (timeout > 0) {
            livenessTimeout = System.currentTimeMillis() + timeout;
        }
        String addressInline = sfResolve(ATTR_ADDRESS, "", true);
        if (addressInline.length() > 0) {
            address = NetUtils.createSocketAddr(addressInline);
        } else {
            String addressAttribute = sfResolve(ATTR_ADDRESS_ATTRIBUTE, "", true);
            address = configuration.bindToNetwork(addressAttribute,
                    "stubOldAddressNameShouldNotResolve",
                    "stubOldAddressPortShouldNotResolve");
        }
        sfLog().info("Checking host:port " + address.toString());
        checkOnLiveness = sfResolve(ATTR_CHECK_ON_LIVENESS, false, true);
        checkOnStartup = sfResolve(ATTR_CHECK_ON_LIVENESS, false, true);
        if (checkOnStartup) {
            try {
                checkThePort();
            } catch (IOException e) {
                throw new SmartFrogDeploymentException(e, this);
            }
        }
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null, null, sfCompleteName(), null);
    }


    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (checkOnLiveness) {
            boolean raiseErrors;
            if (livenessTimeout == 0) {
                raiseErrors = true;
            } else {
                raiseErrors = System.currentTimeMillis() > livenessTimeout;
            }
            try {
                checkThePort();
            } catch (IOException e) {
                if (raiseErrors) {
                    throw new SmartFrogLivenessException(e, this);
                }
            } catch (SmartFrogException e) {
                throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(e);
            }
        }
    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        try {
            checkThePort();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Here is where the port gets probed
     *
     * @throws IOException        failure to connect, including timeout
     * @throws SmartFrogException for security exceptions
     */
    private void checkThePort() throws IOException, SmartFrogException {
        Socket s = null;
        try {
            s = new Socket();
            s.connect(address, connectTimeout);
        } catch (SecurityException e) {
            throw new SmartFrogException("Failed to connect to " + address.toString(), e);
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    sfLog().ignore("When closing " + s, e);
                }
            }
        }
    }
}
