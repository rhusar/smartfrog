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

package org.smartfrog.sfcore.common;

/**
 * All the special key words used in SmartFrog core system should be defined here.
 */
public interface SmartFrogCoreKeys {

    /**
     * Attribute used to determine the host to use to
     * locate the root process compound on that host.
     *
     * Value {@value}
     * @see org.smartfrog.sfcore.processcompound.PrimHostDeployerImpl
     */
    public final static String SF_PROCESS_HOST = "sfProcessHost";

    /**
     * Attribute used to determine the process/subprocess name where a component runs.
     *
     * Value {@value}
     */
    public final static String SF_PROCESS = "sfProcess";



    /**
     * Attribute used to name a process/subprocess.
     *
     * Value {@value}
     */
    public final static String SF_PROCESS_NAME = "sfProcessName";

    /**
     * Attribute used to name a component.
     *
     * Value {@value}
     */
    public final static String SF_PROCESS_COMPONENT_NAME = "sfProcessComponentName";

    /**
     * Registry port used by the rootProcess daemon.
     *
     * Value {@value}
     */
    public final static String SF_ROOT_LOCATOR_PORT = "sfRootLocatorPort";
   /**
     * Registry bind adderess used by the rootProcess daemon.
     *
     * Value {@value}
     */
    public final static String SF_ROOT_LOCATOR_BIND_ADDRESS = "sfRootLocatorBindAddress";    

    /**
     * Attribute with garbage collection time out for SubProcesses.
     *
     * Value {@value}
     */
    public final static String SF_SUBPROCESS_GC_TIMEOUT = "sfSubprocessGCTimeout";

    /**
     * Attribute used to determine the host address where a component runs.
     *
     * Value {@value}
     */
    public final static String SF_HOST = "sfHost";

    /**
     * Attribute used to define if subProcesses can be used.
     *
     * Value {@value}
     */
    public final static String SF_PROCESS_ALLOW ="sfProcessAllow";

    /**
     * Attribute used to define what  subProcesses attributes to overwrite when
     * it is deployed.
     *
     * Value {@value}
     */
    public final static String SF_PROCESS_CONFIG ="sfProcessConfig";

    /**
     * Attribute used to define what variables should be added to a subProcesses
     * jvm environment.
     *
     * Value {@value}
     */
    public final static String SF_PROCESS_ENV_VARS ="sfProcessEnvVars";

    /**
     * Attribute with subprocess deployment timeout.
     *
     * Value {@value}
     */
    public final static String SF_PROCESS_TIMEOUT ="sfProcessTimeout";

    /**
     *  Attribute that holds the process java start command.
     *
     * Value {@value}
     */
    public final static String SF_PROCESS_JAVA ="sfProcessJava";

    /**
     * Attribute that holds the class name for subprocesses.
     *
     * Value {@value}
     */
    public final static String SF_PROCESS_CLASS ="sfProcessClass";
    /**
     * Attribute that holds the class name for subprocesses.
     *
     * Value {@value}
     */
    public final static String SF_PROCESS_CLASSPATH ="sfProcessClassPath";
    /**
     * Attribute that holds the class name for subprocesses.
     *
     * Value {@value}
     */
    public final static String SF_PROCESS_REPLACE_CLASSPATH ="sfProcessReplaceClassPath";

    /**
     * Attribute that holds the class name for subprocesses.
     *
     * Value {@value}
     */
    public final static String SF_PROCESS_SF_CODEBASE ="sfProcessCodeBase";
    /**
     * Attribute that holds the class name for subprocesses.
     *
     * Value {@value}
     */
    public final static String SF_PROCESS_REPLACE_SF_CODEBASE ="sfProcessReplaceCodeBase";

    /**
     * Attribute that holds the class name for deployer.
     *
     * Value {@value}
     */
    public final static String SF_DEPLOYER_CLASS ="sfDeployerClass";

    /**
     * Attribute that determines asynchronous or synchronous termination
     * of compound.
     *
     * Value {@value}
     * @see org.smartfrog.sfcore.compound.CompoundImpl
     */
    public final static String SF_SYNC_TERMINATE = "sfSyncTerminate";

    /**
     * Attribute that holds the class that implements a component.
     *
     * Value {@value}
     */
    public final static String SF_CLASS="sfClass";

    /**
     * Attribute that determines the resolution root of a SmartFrog description.
     *
     * Value {@value}
     */
    public final static String SF_CONFIG="sfConfig";


    /**
     * Attritute that determines the definition of a schema.
     *
     * Value {@value}
     */
    public final static String SF_SCHEMA_DESCRIPTION="sfSchemaDescription";

    /**
     * Attribute that defines the codebase for a component.
     *
     * Value {@value}
     */
    public final static String SF_CODE_BASE = "sfCodeBase";


    /**
     * Attribute that defines how often to send liveness in seconds.
     *
     * Value {@value}
     */
    public final static String SF_LIVENESS_DELAY= "sfLivenessDelay";

    /**
     * Attribute that defines how many multiples of liveness delay to wait
     * till a liveness failure of the parent is declared.
     *
     * Value {@value}
     */
    public final static String SF_LIVENESS_FACTOR="sfLivenessFactor";

    /**
     * Attribute that defines if a component has to accept remote method calls.
     *
     * Value {@value}
     */
    public final static String SF_EXPORT="sfExport";

    /**
     * Attribute that defines the port that a component will use to accept
     * remote method calls.
     *
     * Value {@value}
     */
    public final static String SF_EXPORT_PORT="sfExportPort";


    /**
     * Attribute that defines the root locator class.
     *
     * Value {@value}
     */
    public final static String SF_ROOT_LOCATOR_CLASS = "sfRootLocatorClass";

    /**
     * Attribute that hold the boot time of the root process daemon.
     *
     * Value {@value}
     */
    public final static String SF_BOOT_DATE = "sfBootDate";

    /**
     * Attribute that holds version of the root process daemon
     *
     * Value {@value}
     */
    public final static String SF_VERSION = "sfVersion";


    // Strings used to name certain things in the framework

    /**
     * Name used to name root process.
     *
     * Value {@value}
     */
    public final static String SF_ROOT_PROCESS = "rootProcess";

    /**
     * Name used to refer to the root reference in a particular hierachy of
     * components or description.
     *
     * Value {@value}
     */
    public final static String SF_ROOT = "ROOT";

    /**
     * Name used to name a root process deployed without registry.
     *
     * Value {@value}
     */
    public final static String SF_RUN_PROCESS = "sfRunProcess";

    /**
     * Name used to prefix the name of unnamed deployments.
     *
     * Value {@value}
     */
    public final static String SF_UNNAMED = "unnamed_";

    /**
     * Name to name a deploy phase failure.
     *
     * Value {@value}
     */
    public final static String SF_DEPLOY_FAILURE = "sfDeployFailure";

    /**
     * Name to name a start phase failure.
     *
     * Value {@value}
     */
    public final static String SF_START_FAILURE = "sfStartFailure";

    /**
     * Name to name a start phase failure.
     *
     * Value {@value}
     */
    public final static String SF_CORE_LOG = "SFCORE_LOG";

    /**
     * Attribute name to get the name of the application logger.
     *
     * Value {@value}
     */
    public final static String SF_APP_LOG_NAME = "sfLog";

    /**
     * Attribute name to get the name of the diagnostics report in ProcessCompound.
     *
     * Value {@value}
     */
    public final static String SF_DIAGNOSTICS_REPORT = "sfDiagnosticsReport";

    /**
     * Tag to mark an attribute as final, after which placement fails
     * Value {@value}
     */
    public static final String SF_FINAL= "sfFinal";


    /**
     * Time attribute set in ActionDeploy deployment
     *
     * Value {@value}
     */
    String SF_TIME_PARSE = "sfParseTime";
    /**
     * Time attribute set in ActionDeploy deployment
     *
     * Value {@value}
     */
    String SF_TIME_DEPLOY = "sfDeployTime";

    /**
     * Time attribute set in ActionDeploy deployment
     *
     * Value {@value}
     */
    String SF_TIME_START = "sfStartTime";

    /**
     * Time this component was started
     *
     * Value {@value}
     */
    String SF_TIME_STARTED_AT = "sfStartedAtTime";

    /**
     * The language attribute, which can be set/found from the context.
     * {@value}
     */
    String KEY_LANGUAGE = "#language";
}
