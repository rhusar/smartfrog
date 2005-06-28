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

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.security.SFSecurityProperties;

/**
 * Single location for all JVM properties
 * created Jul 6, 2004 2:46:28 PM
 */

public class SmartFrogJVMProperties {

    private SmartFrogJVMProperties() {
    }

    /**
     * prefix for any ant magic properties
     * @value ant.tasks.smartfrog.
     */
    public static final String ANT_MAGIC_PROPERTY_PREFIX = "ant.tasks.smartfrog.";

    /**
     * property that should be set to true for extra debugging
     * @value ant.tasks.smartfrog.debug
     */
    public static final String ANT_DEBUG_PROPERTY = ANT_MAGIC_PROPERTY_PREFIX + "debug";

    /**
     * name of the process.
     * 
     * {@value}
     */
    public static final String PROCESS_NAME =       SmartFrogCoreProperty.propBaseSFProcess + SmartFrogCoreKeys.SF_PROCESS_NAME;
            
    /**
     * classloader
     * @see org.smartfrog.sfcore.security.SFClassLoader#SF_CODEBASE_PROPERTY
     */
    protected static final String CODEBASE =       SFClassLoader.SF_CODEBASE_PROPERTY;
    /**
     * the name of a root process
     */
    public static final String ROOT_PROCESS =      SmartFrogCoreKeys.SF_ROOT_PROCESS;
    public static final String PROCESS_COMPOUND=   SmartFrogCoreProperty.propBaseSFProcess;

    /**
     * log stack traces.
     *
     * Value {@value}
     */

    public static final String LOG_STACK_TRACE =   SmartFrogCoreProperty.propLogStackTrace;
    
    /**
     * @value "org.smartfrog.sfcore.processcompound.sfRootLocatorPort";
     */

    public static final String ROOT_LOCATOR_PORT = SmartFrogCoreProperty.propBaseSFProcess + SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT;
            
    /**
     * @value org.smartfrog.sfcore.processcompound.sfLivenessDelay
     */
    public static final String LIVENESS_DELAY =    SmartFrogCoreProperty.propBaseSFProcess + SmartFrogCoreKeys.SF_LIVENESS_DELAY;

    /**
     * @value org.smartfrog.sfcore.processcompound.sfLivenessFactor
     */
    public static final String LIVENESS_FACTOR =   SmartFrogCoreProperty.propBaseSFProcess + SmartFrogCoreKeys.SF_LIVENESS_FACTOR;

    /**
     * @value org.smartfrog.sfcore.processcompound.fProcessAllow
     */
    public static final String PROCESS_ALLOW =     SmartFrogCoreProperty.propBaseSFProcess + SmartFrogCoreKeys.SF_PROCESS_ALLOW;

    /**
     * @value org.smartfrog.sfcore.processcompound.sfProcessTimeout
     */
    public static final String PROCESS_TIMEOUT =   SmartFrogCoreProperty.propBaseSFProcess + SmartFrogCoreKeys.SF_PROCESS_TIMEOUT;

    /**
     * @value org.smartfrog.sfcore.processcompound.sfDefault.sfDefault
     */
    public static final String SF_DEFAULT =        SmartFrogCoreProperty.propBaseSFProcess +  "sfDefault.sfDefault";  
    
    public static final String INIFILE =           SmartFrogCoreProperty.iniFile;

    /**
     * @value org.smartfrog.sfcore.security.keyStorePassword
     */
    public static final String KEYSTORE_PASSWORD = SFSecurityProperties.propKeyStorePasswd;

    /**
     * @value org.smartfrog.sfcore.security.keyStoreName
     */
    public static final String KEYSTORE_NAME =      SFSecurityProperties.propKeyStoreName;

    /**
     * @value org.smartfrog.sfcore.security.propFile
     */
    public static final String KEYSTORE_PROPFILE = SFSecurityProperties.propPropertiesFileName;

    public static final String SECURITY_ENABLED =  SFSecurityProperties.propSecurityOn;

    /**
     * entry point to smartfrog
     * @value "org.smartfrog.SFSystem";
     */ 
    public static final String SMARTFROG_ENTRY_POINT = "org.smartfrog.SFSystem";
    
    
    public static final String GUI_ENTRY_POINT = "org.smartfrog.tools.gui.browser.SFGui";

    public static final String MANAGEMENT_ENTRY_POINT = "org.smartfrog.services.management.SFDeployDisplay";
        
    /**
     * parser entry point
     * @value org.smartfrog.SFParse
     */ 
    public static final String PARSER_ENTRY_POINT = "org.smartfrog.SFParse";
    public static final String PARSER_OPTION_QUIET = "-q";
    public static final String PARSER_OPTION_VERBOSE = "-v";
    public static final String PARSER_OPTION_FILENAME = "-f";
    public static final String PARSER_OPTION_R = "-r";
}
