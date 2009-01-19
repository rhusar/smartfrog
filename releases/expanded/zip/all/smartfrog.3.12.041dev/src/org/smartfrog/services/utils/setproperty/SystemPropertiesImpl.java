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

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Implement our JVM manipulator
 */
public class SystemPropertiesImpl extends PrimImpl implements SystemProperties {

    private Properties proplist = new Properties();
    private boolean setOnStartup = false;
    private boolean setOnDeploy = false;
    private boolean setOnEarlyDeploy = false;
    private boolean unsetOnTerminate = true;
    private boolean ignoreSecurityExceptions = false;
    private LogSF log;
    private static final Reference REF_PROPERTIES = new Reference(ATTR_PROPERTIES);

    /**
     * constructor
     * @throws RemoteException if the super throws it
     */
    public SystemPropertiesImpl() throws RemoteException {
    }


    /**
     * This is a very early deploy phases
     *
     * @param parent parent
     * @param cxt    context
     * @throws SmartFrogDeploymentException failure to read attributes, or a wrapped security exception
     * @throws RemoteException    network trouble
     */
    public synchronized void sfDeployWith(Prim parent, Context cxt)
            throws SmartFrogDeploymentException, RemoteException {
        try {
            sfContext = cxt;
            log = sfGetApplicationLog();
            setOnEarlyDeploy = resolveBool(cxt, ATTR_SETONEARLYDEPLOY);
            ignoreSecurityExceptions = resolveBool(cxt, ATTR_IGNORESECURITYEXCEPTIONS);
            if (setOnEarlyDeploy) {
                loadAndSetProperties();
            }

        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error(t.getMessage(), t);
            }
            throw new SmartFrogDeploymentException(t, this);
        }
        super.sfDeployWith(parent, cxt);
    }

    private boolean resolveBool(Context cxt, String name) throws SmartFrogContextException {
        try {
            return ((Boolean) cxt.sfResolveAttribute(name)).booleanValue();
        } catch (SmartFrogContextException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to read mandatory attribute: " + e.toString(), e);
            }
            throw e;
        }
    }


    /**
     * Called after instantiation for deployment purposes. Heart monitor is started and if there is a parent the
     * deployed component is added to the heartbeat. Subclasses can override to provide additional deployment behavior.
     *
     * @throws SmartFrogException failure to read attributes, or a wrapped security exception
     * @throws RemoteException    network trouble
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();

        setOnStartup = sfResolve(ATTR_SETONSTARTUP, setOnStartup, true);
        setOnDeploy = sfResolve(ATTR_SETONDEPLOY, setOnDeploy, true);
        unsetOnTerminate = sfResolve(ATTR_UNSETONTERMINATE, unsetOnTerminate, true);
        if (setOnDeploy) {
            loadAndSetProperties();
        }
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure to read attributes, or a wrapped security exception
     * @throws RemoteException    network trouble
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
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,
                "SystemProperties",
                null,
                null);
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        if (unsetOnTerminate && proplist != null) {
            clearProperties();
        }
        super.sfTerminateWith(status);
    }

    /**
     * load the properties in
     *
     * @throws SmartFrogException failure to read attributes, or a wrapped security exception
     * @throws RemoteException    network trouble
     */
    private void loadProperties()
            throws SmartFrogException, RemoteException {
        proplist = ListUtils.resolveProperties(this, REF_PROPERTIES, true);
    }

    /**
     * load in the property list and set it
     *
     * @throws SmartFrogException failure to read attributes, or a wrapped security exception
     * @throws RemoteException    network trouble
     */
    private void loadAndSetProperties() throws SmartFrogException,
            RemoteException {
        loadProperties();
        Enumeration<?> keys = proplist.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = proplist.getProperty(key);
            setProperty(key, value);
        }
    }

    /**
     * clear the properties Failures are logged at ignore level.
     */
    private void clearProperties() {
        Enumeration<?> keys = proplist.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            try {
                unsetProperty(key);
            } catch (SmartFrogException e) {
                sfLog().ignore(e);
            }
        }
    }

    /**
     * Set a property in this JVM
     *
     * @param name  name of the property
     * @param value value of the property
     * @throws SmartFrogException may wrap a security exception
     */
    public void setProperty(String name, String value)
            throws SmartFrogException {
        String action = "setting " + name + " to " + value;
        try {
            sfLog().info(action);
            innerSetProperty(name, value);
        } catch (SecurityException e) {
            if(!ignoreSecurityExceptions) {
                throw SmartFrogException.forward(action, e);
            } else {
                log.ignore(action,e);
            }
        }
    }

    /**
     * Override point, set a single property.
     * @param name property name
     * @param value value
     * @throws SmartFrogException for smartfrog problems
     * @throws SecurityException if need be
     */
    protected void innerSetProperty(String name, String value) throws SmartFrogException {
        System.setProperty(name, value);
    }

    /**
     * Unset a property in this JVM
     *
     * @param name name of the property
     * @throws SmartFrogException may wrap a security exception
     */
    public void unsetProperty(String name)
            throws SmartFrogException {
        String action = "clearing " + name;
        sfLog().debug(action);
        try {
            innerUnsetProperty(name);
        } catch (SecurityException e) {
            if (!ignoreSecurityExceptions) {
                throw SmartFrogException.forward("Failed to clear" + name, e);
            } else {
                log.ignore(action, e);
            }
        }
    }

    /**
     * Unset a property
     * @param name property name
     * @throws SmartFrogException for smartfrog problems
     * @throws SecurityException if need be
     */
    protected void innerUnsetProperty(String name) throws SmartFrogException {
        System.clearProperty(name);
    }
}
