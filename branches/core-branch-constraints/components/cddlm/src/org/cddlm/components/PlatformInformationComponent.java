/**
 * (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP This library
 * is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any
 * later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not,
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA For more information: www.smartfrog.org
 */
package org.cddlm.components;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;


/**
 * created 27-Apr-2004 11:33:00
 */
public class PlatformInformationComponent extends PrimImpl
        implements PlatformInformation {
    /**
     * cached value
     */
    private String processor;

    /**
     * cached value
     */
    private String operatingSystem;

    /**
     * cached value
     */
    private String family;

    /**
     * cached value
     */
    private String fileSeparator;

    /**
     * cached value
     */
    private String lineSeparator;

    /**
     * cached value
     */
    private String pathSeparator;

    /**
     * Creates a new PlatformInformationComponent object.
     *
     * @throws RemoteException DOCUMENT ME!
     */
    public PlatformInformationComponent() throws RemoteException {
    }

    /**
     * all this info comes from System info
     */
    protected void extractInformation() {
        //TODO: determine CPU type
        processor = "unknown"; //System.getProperty("process");
        operatingSystem = System.getProperty("os.name");
        family = System.getProperty("os.arch");
        fileSeparator = System.getProperty("file.separator");
        lineSeparator = System.getProperty("path.separator");
        pathSeparator = System.getProperty("line.separator");
    }

    /**
     * get the processor
     *
     * @return
     */
    public String getProcessor() {
        return processor;
    }

    /**
     * OS name
     *
     * @return
     */
    public String getOS() {
        return operatingSystem;
    }

    /**
     * OS family
     *
     * @return
     */
    public String getFamily() {
        return family;
    }

    /**
     * file separator
     *
     * @return "/", "\" or whatever the file separator is
     */
    public String getFileSeparator() {
        return fileSeparator;
    }

    /**
     * get line separator
     *
     * @return "\n", "\n\r", or whatever
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     * get path separator
     *
     * @return ";" ":" or something else
     */
    public String getPathSeparator() {
        return pathSeparator;
    }

    /**
     * Called after instantiation for deployment purposed. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while
     *                                  deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
        extractInformation();
        sfAddAttribute(PROCESSOR, getProcessor());
        sfAddAttribute(FAMILY, getFamily());
        sfAddAttribute(OS, getOS());
        sfAddAttribute(LINE_SEPARATOR, getLineSeparator());
        sfAddAttribute(FILE_SEPARATOR, getFileSeparator());
        sfAddAttribute(PATH_SEPARATOR, getPathSeparator());
    }
}
