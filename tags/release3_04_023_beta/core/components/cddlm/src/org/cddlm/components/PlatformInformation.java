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
package org.cddlm.components;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * created 27-Apr-2004 11:33:13
 */
public interface PlatformInformation extends Remote {
    /*
    processor;
    OS;
    Family; //WinNT, Win9x, Unix, Mac, ...
    DirectorySeparator "/";
    OnBattery; //false
    OnNetwork; //true
    */

    /**
     * attribute for processor
     */
    public static final String PROCESSOR = "processor";

    /**
     * name of OS
     */
    public static final String OS = "OS";

    /**
     * family of OS
     */
    public static final String FAMILY = "family";

    /**
     * file separator
     */
    public static final String FILE_SEPARATOR = "fileSeparator";

    /**
     * file separator
     */
    public static final String LINE_SEPARATOR = "lineSeparator";

    /**
     * path separator
     */
    public static final String PATH_SEPARATOR = "pathSeparator";

    /**
     * get the processor
     *
     * @return
     */
    public String getProcessor() throws RemoteException;

    /**
     * OS name
     *
     * @return
     */
    public String getOS() throws RemoteException;

    /**
     * OS family
     *
     * @return
     */
    public String getFamily() throws RemoteException;

    /**
     * file separator
     *
     * @return "/", "\" or whatever the file separator is
     */
    public String getFileSeparator() throws RemoteException;

    /**
     * get the line separator
     *
     * @return \n, \r and whatever mainframes have
     */
    public String getLineSeparator() throws RemoteException;

    /**
     * get the path separator
     *
     * @return
     */
    public String getPathSeparator() throws RemoteException;
}
