
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

/*
 */
package org.smartfrog.tools.eclipse.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.Properties;


/**
 * Miscellaneous utility methods 
 */
public class Util
{
    private static final String WINDOWS_OS_PREFIX = "windows"; //$NON-NLS-1$
    private static final String UNIX_COMMAND = "env"; //$NON-NLS-1$
    private static final String NT_COMMAND = "cmd.exe /c set"; //$NON-NLS-1$
    private static final String OS_WINDOWS_XP = "windows xp"; //$NON-NLS-1$
    private static final String OS_WINDOWS_2000 = "windows 2000"; //$NON-NLS-1$
    private static final String OS_NT = "nt"; //$NON-NLS-1$
    private static final String WINDOWS_9_COMMAND = "command.com /c set"; //$NON-NLS-1$
    private static final String WINDOWS_9 = "windows 9"; //$NON-NLS-1$
    private static final String OS_NAME = "os.name"; //$NON-NLS-1$

    /**
     * Return specified environment value,
     * @param name         environment name
     * @return value        if found, otherwise null
     */
    public static String getEnv(String name)
    {
        String value = null;

        try {
            Properties envVars = getEnvVars();
            value = envVars.getProperty(name);
        } catch (Throwable ignore) {
        }

        return value;
    }

    /**
     * Return the environment variables
     * @throws Throwable
     */
    public static Properties getEnvVars()
        throws Throwable
    {
        Process p = null;
        Properties envVars = new Properties();
        Runtime r = Runtime.getRuntime();
        String OS = System.getProperty(OS_NAME).toLowerCase();

        if (OS.indexOf(WINDOWS_9) > -1) {
            p = r.exec(WINDOWS_9_COMMAND);
        } else if (( OS.indexOf(OS_NT) > -1 ) ||
                ( OS.indexOf(OS_WINDOWS_2000) > -1 ) ||
                ( OS.indexOf(OS_WINDOWS_XP) > -1 )) {
            p = r.exec(NT_COMMAND);
        } else {
            p = r.exec(UNIX_COMMAND);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
        String line;

        while (( line = br.readLine() ) != null) {
            int idx = line.indexOf('=');
            String key = line.substring(0, idx);
            String value = line.substring(idx + 1);
            envVars.setProperty(key, value);
        }

        return envVars;
    }

    /**
     * Check whether the current OS is windows or not.
     * @return true, running in Windows
     */
    public static boolean isWindows()
    {
        boolean ret = false;
        String osName = System.getProperty(OS_NAME);

        if (osName.toLowerCase().startsWith(WINDOWS_OS_PREFIX)) {
            ret = true;
        }

        return ret;
    }
    

    /**
     * 
     * @return class separator depend on different OS
     */
    public static String getClassSeparator()
    {
    	return ISmartFrogConstants.CLASS_SEPARATOR;
    	
    }
}
