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
package org.smartfrog.services.rpm.local;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;
import java.io.IOException;

/**
 * abstract local RPM Tool implementation.
 */
public abstract class AbstractLocalRPMToolImpl extends PrimImpl implements LocalRPMTool {
    private String rpmPackage;
    private String options;
    private RPMUtils rpmUtils;
    public static final String ERROR_UNABLE_TO_INSTALL = "Unable to Install RPM package ";
    public static final String ERROR_UNABLE_TO_UNINSTALL = "Unable to Uninstall RPM package ";
    protected static final String ERROR_UNABLE_TO_UPGRADE = "Unable to Upgrade RPM package ";

    public AbstractLocalRPMToolImpl() throws RemoteException {
    }


    /**
     * At deploy time we resolve the package name and the options, and create
     * a new rpmutils instance
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException network problems
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
        rpmPackage = sfResolve(ATTR_RPM_PACKAGE, "", true);
        options = sfResolve(ATTR_OPTIONS, "", true);
        rpmUtils = new RPMUtils(sfLog());
    }

    protected String getRpmPackage() {
        return rpmPackage;
    }

    protected String getOptions() {
        return options;
    }

    protected RPMUtils getRpmUtils() {
        return rpmUtils;
    }

    protected void upgradePackage(String options) throws SmartFrogException {
        try {
            getRpmUtils().UpgradePackage(getRpmPackage(), options);
        } catch (IOException e) {
            sfLog().error(ERROR_UNABLE_TO_UPGRADE + getRpmPackage(), e);
            throw new SmartFrogException(ERROR_UNABLE_TO_UPGRADE +
                    getRpmPackage(), e);
        }
    }

    protected void installPackage(String options) throws SmartFrogException {
        try {
            getRpmUtils().InstallPackage(getRpmPackage(), options);
        } catch (IOException e) {
            sfLog().error(ERROR_UNABLE_TO_INSTALL + getRpmPackage(), e);
            throw new SmartFrogException(ERROR_UNABLE_TO_INSTALL +
                    getRpmPackage(), e);
        }
    }

    protected void uninstallPackage(String options) throws SmartFrogException {
        try {
            getRpmUtils().UninstallPackage(getRpmPackage(), options);
        } catch (IOException e) {
            sfLog().error(ERROR_UNABLE_TO_UNINSTALL + getRpmPackage(), e);
            throw new SmartFrogException(ERROR_UNABLE_TO_UNINSTALL +
                    getRpmPackage(), e);
        }
    }
}
