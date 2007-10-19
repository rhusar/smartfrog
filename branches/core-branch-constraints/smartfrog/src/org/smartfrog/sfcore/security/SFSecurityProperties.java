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

package org.smartfrog.sfcore.security;

import java.io.IOException;
import java.io.InputStream;


import org.smartfrog.sfcore.common.SmartFrogCoreProperty;


/**
 * A class that describes all the SF specific security properties that we have
 * introduced.
 *
 */
public class SFSecurityProperties {
    /** Base property name for all the SF security related properties.
     * @value  org.smartfrog.sfcore.security.
     * */
    public static final String propBaseSecurity = SmartFrogCoreProperty.propBase +
        "sfcore.security.";

    /** Property name to activate SF security features .*/
    public static final String propSecurityOn = propBaseSecurity + "activate";

    /**
     * Property name to describe the URL, relative file path or resource inside
     * a jar file of the main SF security properties file.
     */
    public static final String propPropertiesFileName = propBaseSecurity +
        "propFile";

    /**
     * Property name to describe the URL, relative file path or resource inside
     * a jar file of the key store resource.
     */
    public static final String propKeyStoreName = propBaseSecurity +
        "keyStoreName";

    /**
     * Property name that describes the password needed to unlock the key store.
     */
    public static final String propKeyStorePasswd = propBaseSecurity +
        "keyStorePassword";

    /** If set to true it will turn on security trace messages. */
    public static final String propDebug = propBaseSecurity + "debug";

    /**
     * Property name to a built-in java rmi property that limits downloading
     * stubs and RMIClientSocketFactories to the CLASSPATH or the
     * java.rmi.server.codebase. This is CRITICAL for security.
     */
    public static final String propUseCodebaseOnly = "java.rmi.server.useCodebaseOnly";

    /**
     * Property name to a built-in java rmi property that makes remote object\z
     * IDs difficult to guess.
     */
    public static final String propRandomIDs = "java.rmi.server.randomIDs";

    /** The name of the main SF security property file. */
    private static String propertiesFileName = "SFSecurity.properties";


    /**
     * flag to indicate that security is required. If security did not initialize
     * and this flag is set, SmartFrog will not execute
     * @value org.smartfrog.sfcore.security.required
     */
    public static final String propSecurityRequired = propBaseSecurity + "required";

    /**
     * Class Constructor. Nobody should call this.
     */
    private SFSecurityProperties() {
    }

    /**
     * Reads the main SF security properties file from a URL, resource inside a
     * jar file or relative file path. WARNING: This file could contain
     * confidential data; it is critical that the OS keeps it read only for
     * the SF daemon, and NO ACCESS for everybody else.
     */
    static void readSecurityProperties() {
        InputStream is = null;
        try {
            propertiesFileName = System.getProperty(propPropertiesFileName,
                    propertiesFileName);

            // At this point there is NO security, so we don't have integrity
            // check of this file... (we rely on the OS).
            is = SFClassLoader.getResourceAsStream(propertiesFileName);

            if (is != null) {
                System.getProperties().load(is);
            }
        } catch (IOException e) {
            // Can't load the file, use defaults...
            //System.out.println("SFSSecurityInit::readSecurityProperties " + "Can't read " + is);
            // @todo review how to put this in a special Logger.
            String errStr="SFSSecurityInit::readSecurityProperties " +"Can't read " + is;
            System.err.println(errStr);
        }
    }
}
