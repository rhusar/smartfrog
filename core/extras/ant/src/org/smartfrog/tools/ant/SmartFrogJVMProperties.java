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
package org.smartfrog.tools.ant;

/**
 * Single location for all JVM properties
 * created Jul 6, 2004 2:46:28 PM
 */

public class SmartFrogJVMProperties {

    /**
     * name of the process
     */
    public static final String PROCESS_NAME = "org.smartfrog.sfcore.processcompound.sfProcessName";
    /**
     * keep in sync with whatever the classloader uses, including
     * the format it takes
     * @see org.smartfrog.sfcore.security.SFClassLoader#SF_CODEBASE_PROPERTY
     */
    protected static final String CODEBASE_PROPERTY = "org.smartfrog.codebase";
    /**
     * the name of a root process
     */
    public static final String ROOT_PROCESS =      "rootProcess";
    public static final String LOG_STACK_TRACE =   "org.smartfrog.logger.logStackTrace";
    public static final String ROOT_LOCATOR_PORT = "org.smartfrog.sfcore.processcompound.sfRootLocatorPort";
    public static final String LIVENESS_DELAY =    "org.smartfrog.sfcore.processcompound.sfLivenessDelay";
    public static final String LIVENESS_FACTOR =   "org.smartfrog.sfcore.processcompound.sfLivenessFactor";
    public static final String PROCESS_ALLOW =     "org.smartfrog.sfcore.processcompound.fProcessAllow";
    public static final String PROCESS_TIMEOUT =   "org.smartfrog.sfcore.processcompound.sfProcessTimeout";
    public static final String SF_DEFAULT =        "org.smartfrog.sfcore.processcompound.sfDefault.sfDefault";
    public static final String INIFILE =           "org.smartfrog.iniFile";
    public static final String SMARTFROG_ENTRY_POINT = "org.smartfrog.SFSystem";
    public static final String KEYSTORE_PASSWORD = "org.smartfrog.sfcore.security.keyStorePassword";
    public static final String KEYSTORE_NAME =     "org.smartfrog.sfcore.security.keyStoreName";
    public static final String KEYSTORE_PROPFILE = "org.smartfrog.sfcore.security.propFile";
    public static final String PARSER_ENTRY_POINT = "org.smartfrog.SFParse";
    public static final String PARSER_OPTION_QUIET = "-q";
    public static final String PARSER_OPTION_VERBOSE = "-v";
    public static final String PARSER_OPTION_FILENAME = "-f";
    public static final String PARSER_OPTION_R = "-r";
}
