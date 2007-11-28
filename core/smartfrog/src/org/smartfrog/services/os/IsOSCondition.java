/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.smartfrog.services.os;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.workflow.conditional.Condition;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Locale;

/**
 * Code to test what OS this is from. Based on Ant's OS condition. Created 28-Nov-2007 13:24:16
 */

public class IsOSCondition extends PrimImpl implements Condition {


    /**
     * takes the string value of a property to look for: {@value}
     */
    public static final String ATTR_OS = "os";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_ARCHITECTURE = "architecture";
    public static final String ATTR_VERSION = "version";
    public static final String ATTR_SUPPORTED = "supported";

    private static final String OS_NAME =
            System.getProperty("os.name").toLowerCase(Locale.US);
    private static final String OS_ARCH =
            System.getProperty("os.arch").toLowerCase(Locale.US);
    private static final String OS_VERSION =
            System.getProperty("os.version").toLowerCase(Locale.US);
    private static final String PATH_SEPERATOR =
            System.getProperty("path.separator");


    private boolean supported;
    public static final String WINDOWS = "windows";
    public static final String XP = "xp";
    private static final String VISTA = "vista";
    public static final String MAC = "mac";
    public static final String NONSTOP_KERNEL = "nonstop_kernel";
    public static final String OPENVMS = "openvms";
    public static final String SOLARIS = "solaris";
    public static final String HP_UX = "hp-ux";
    public static final String AIX = "aix";
    public static final String FREEBSD = "freebsd";
    public static final String LINUX = "linux";

    public IsOSCondition() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        Vector supportedOS = null;
        supportedOS = sfResolve(ATTR_OS, supportedOS, true);
        sfReplaceAttribute(ATTR_NAME, OS_NAME);
        sfReplaceAttribute(ATTR_ARCHITECTURE, OS_ARCH);
        sfReplaceAttribute(ATTR_VERSION, OS_VERSION);


        boolean isWindows = OS_NAME.contains(WINDOWS);
        boolean isXP = isWindows && OS_NAME.contains(XP);
        boolean isVista = isWindows && OS_NAME.contains(VISTA);
        boolean isMac = OS_NAME.contains(MAC);
        boolean isNonstop = OS_NAME.contains(NONSTOP_KERNEL);
        boolean isVMS = OS_NAME.contains(OPENVMS);
        boolean isSolaris = OS_NAME.contains(SOLARIS);
        boolean isHPUX = OS_NAME.contains(HP_UX);
        boolean isAIX = OS_NAME.contains(AIX);
        boolean isBSD = OS_NAME.contains(FREEBSD);
        boolean isLinux = OS_NAME.contains(LINUX);
        boolean isUnix = ":".equals(PATH_SEPERATOR) && !isVMS;


        supported = false;
        for (Object os : supportedOS) {
            String name = os.toString().toLowerCase(Locale.US);
            if (WINDOWS.equals(name)) {
                supported = isWindows;
            } else if ("windowsxp".equals(name)) {
                supported = isXP;
            } else if ("windowsvista".equals(name)) {
                supported = isVista;
            } else if (NONSTOP_KERNEL.equals(name)) {
                supported = isNonstop;
            } else if (SOLARIS.equals(name)) {
                supported = isSolaris;
            } else if (MAC.equals(name)) {
                supported = isMac;
            } else if (HP_UX.equals(name)) {
                supported = isHPUX;
            } else if (AIX.equals(name)) {
                supported = isAIX;
            } else if (FREEBSD.equals(name)) {
                supported = isBSD;
            } else if (LINUX.equals(name)) {
                supported = isLinux;
            } else if ("unix".equals(name)) {
                supported = isUnix;
            }
            if (supported) {
                //exit the loop
                break;
            }
        }

        //set the supported flag
        sfReplaceAttribute(ATTR_SUPPORTED, supported);

    }


    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     */
    public boolean evaluate() {
        return supported;
    }
}