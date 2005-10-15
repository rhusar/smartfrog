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

package org.smartfrog.services.utils.setproperty;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Vector;
import java.util.Enumeration;

/** Implement our JVM manipulator */
public class SystemPropertiesImpl extends PrimImpl implements SystemProperties {

    private Properties proplist = new Properties();
    private boolean setOnStartup = false;
    private boolean setOnDeploy = false;
    private boolean unsetOnTerminate = false;

    public SystemPropertiesImpl() throws RemoteException {
    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
        setOnStartup = sfResolve(ATTR_SETONSTARTUP, setOnStartup, true);
        setOnDeploy = sfResolve(ATTR_SETONDEPLOY, setOnDeploy, true);
        unsetOnTerminate = sfResolve(ATTR_UNSETONTERMINATE,
                unsetOnTerminate,
                true);
        if (setOnDeploy) {
            loadAndSetProperties();
        }
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        if (setOnStartup) {
            loadAndSetProperties();
        } else {
            //this is so that we have the list for termination
            loadProperties();
        }
        new ComponentHelper(this).sfSelfDetachAndOrTerminate("normal",
                "SystemProperties",
                this.sfCompleteNameSafe(),
                null);

    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        if (unsetOnTerminate && proplist != null) {
            try {
                clearProperties();
            } catch (SmartFrogException e) {
                //ignore

            } catch (RemoteException e) {
                //ignore
            }
        }
        super.sfTerminateWith(status);
    }

    /**
     * load the properties in
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    private void loadProperties()
            throws SmartFrogException, RemoteException {
        Vector propVector = null;
        proplist = new Properties();
        propVector = sfResolve(ATTR_PROPERTIES, propVector, true);
        if (propVector != null) {
            for (Enumeration en = propVector.elements();
                 en.hasMoreElements();) {
                Vector element = (Vector) en.nextElement();
                String key = element.firstElement().toString();
                String value = element.lastElement().toString();
                proplist.put(key, value);
            }
        }
    }

    /**
     * load in the property list and set it
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    private void loadAndSetProperties() throws SmartFrogException,
            RemoteException {
        loadProperties();
        Enumeration keys = proplist.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = proplist.getProperty(key);
            setProperty(key, value);
        }
    }

    /**
     * @throws SmartFrogException
     * @throws RemoteException
     */
    private void clearProperties() throws SmartFrogException,
            RemoteException {
        Enumeration keys = proplist.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            unsetProperty(key);
        }
    }

    /**
     * Set a property in this JVM
     *
     * @param name  name of the property
     * @param value value of the property
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  -may wrap a security exception
     * @throws java.rmi.RemoteException
     */
    public void setProperty(String name, String value)
            throws SmartFrogException, RemoteException {
        try {
            System.setProperty(name, value);
        } catch (SecurityException e) {
            throw SmartFrogException.forward("setting " + name + " to " + value,
                    e);
        }
    }

    /**
     * Unset a property in this JVM
     * Not supported on java1.4
     * @param name
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  -may wrap a security exception
     * @throws java.rmi.RemoteException
     */
    public void unsetProperty(String name)
            throws SmartFrogException, RemoteException {
        try {
          // TODO: use introspection. 
            //   System.clearProperty(name);
        } catch (SecurityException e) {
            throw SmartFrogException.forward("clearing " + name,
                    e);
        }
    }
}
