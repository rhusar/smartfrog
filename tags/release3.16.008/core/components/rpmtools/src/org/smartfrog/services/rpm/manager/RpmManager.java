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
package org.smartfrog.services.rpm.manager;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created 14-Apr-2008 16:47:04
 */


public interface RpmManager extends Remote {

    /** executable to run */
    String ATTR_EXECUTABLE="executable";

    /**
     * The rpm command before the file
     */
    String ATTR_COMMAND = "command";

    /** list of arguments to use in the command before the file */
    String ATTR_ARGUMENTS="arguments";

    /**
     * Install the RPMs on startup
     */
    String ATTR_INSTALL = "install";

    /**
     * uninstall the RPMs on termination?
     */
    String ATTR_UNINSTALL_ON_TERMINATION ="uninstallOnTermination";

    /**
     * uninstall the RPMs on termination?
     */
    String ATTR_UNINSTALL_ON_STARTUP = "uninstallOnStartup";

    /**
     * Ping for managed files during a liveness check?
     */
    String ATTR_PROBE_ON_LIVENESS = "probeOnLiveness";

    /**
     * Ping for managed files during a liveness check?
     */
    String ATTR_PROBE_ON_STARTUP = "probeOnStartup";
    /**
     * Should we apply in bulk? That is, group apply everything in one go?
     */
    String ATTR_BULK_OPERATION = "bulkOperation";

    /**
     * How to handle a failure to install
     */
    String ATTR_FAIL_ON_INSTALL_ERROR = "failOnInstallError";

    /**
     * How to handle a failure to uninstall
     */
    String ATTR_FAIL_ON_UNINSTALL_ERROR = "failOnUninstallError";

    /**
     * Should scripts be skipped during installation?
     */
    String ATTR_INSTALL_NO_SCRIPTS = "installSkipScripts";

    /**
     * Should scripts be skipped during uninstallation?
     */
    String ATTR_UNINSTALL_NO_SCRIPTS = "uninstallSkipScripts";

    /**
     * Should dependencies be ignored during uninstallation?
     */
    String ATTR_UNINSTALL_IGNORE_DEPENDENCIES = "uninstallIgnoreDependencies";


    /**
     * String used to detect a debian system and bail out early:
     * {@value}
     * "To install rpm packages on Debian systems, use alien."
     */
    String ATTR_DEBIAN_SYSTEM="DEBIAN_SYSTEM";

    /**
     * String used to detect a missing database: {@value}
     * "cannot open Packages"
     */
    String ATTR_MISSING_DATABASE = "MISSING_DATABASE";


    /**
     * String used to detect an unowned file: {@value}.
     * "is not owned by any package"
     */
    String ATTR_IS_NOT_OWNED = "IS_NOT_OWNED";

    
    /**
     * Add another file to the list of RPMs that need managing
     *
     * @param rpm the RPM to manage
     * @throws SmartFrogException if unable to manage this file
     * @throws RemoteException    network problems
     */
    public void manage(RpmFile rpm) throws SmartFrogException, RemoteException;



}
