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
     * locate the root process compound on that host
     * @see PrimHostDeployerImpl
     */
    public final static String SF_PROCESS_HOST = "sfProcessHost";

    /**
     * Attribute used to determine the process/subprocess name where a component runs
     */
    public final static String SF_PROCESS = "sfProcess";



    /**
     * Attribute used to name a process/subprocess
     */
    public final static String SF_PROCESS_NAME = "sfProcessName";

    /**
     * Attribute used to name a component
     */
    public final static String SF_PROCESS_COMPONENT_NAME = "sfProcessComponentName";

    /**
     * Registry port used by the rootProcess daemon
     */
    public final static String SF_ROOT_LOCATOR_PORT = "sfRootLocatorPort";

    /**
     * Attribute with garbage collection time out for SubProcesses
     */
    public final static String SF_SUBPROCESS_GC_TIMEOUT = "sfSubprocessGCTimeout";

    /**
     * Attribute used to determine the host address where a component runs
     */
    public final static String SF_HOST = "sfHost";

    /**
     * Attribute used to define if subProcesses can be used
     */
    public final static String SF_PROCESS_ALLOW ="sfProcessAllow";

    /**
     * Attribute with subprocess deployment timeout
     */
    public final static String SF_PROCESS_TIMEOUT ="sfProcessTimeout";

    /**
     *  Attribute that holds the process java start command
     */
    public final static String SF_PROCESS_JAVA ="sfProcessJava";

    /**
     * Attribute that holds the class name for subprocesses
     */
    public final static String SF_PROCESS_CLASS ="sfProcessClass";

    /**
     * Attribute that holds the class name for deployer
     */
    public final static String SF_DEPLOYER_CLASS ="sfDeployerClass";

    /**
     * Attribute that determines asynchronous or synchronous termination
     * of compound
     * @see CompoundImpl
     */
    public final static String SF_SYNC_TERMINATE = "sfSyncTerminate";

    /**
     * Attribute that holds the class that implements a component
     */
    public final static String SF_CLASS="sfClass";

    /**
     * Attribute that determines the resolution root of a SmartFrog description
     */
    public final static String SF_CONFIG="sfConfig";


    /**
     * Attritute that determines the definition of a schema
     */
    public final static String SF_SCHEMA_DESCRIPTION="sfSchemaDescription";

    /**
     * Attribute that defines the codebase for a component
     */
    public final static String SF_CODE_BASE = "sfCodeBase";


    /**
     * Attribute that defines how often to send liveness in seconds.
     */
    public final static String SF_LIVENESS_DELAY= "sfLivenessDelay";

    /**
     * Attribute that defines how many multiples of livenss delay to wait
     * till a liveness failure of the parent is declared
     */
    public final static String SF_LIVENESS_FACTOR="sfLivenessFactor";

    /**
     * Attribute that defines if a component has to accept remote method calls
     */
    public final static String SF_EXPORT="sfExport";

    /**
     * Attribute that defines the root locator class
     */
    public final static String SF_ROOT_LOCATOR_CLASS = "sfRootLocatorClass";

    /**
     * Attribute that hold the boot time of the root process daemon
     */
    public final static String SF_BOOT_DATE = "sfBootDate";



    // Strings used to name certain things in the framework

    /**
     * Name used to name root process
     */
    public final static String SF_ROOT_PROCESS = "rootProcess";

    /**
     * Name used to refer to the root reference in a particular hierachy of
     * components or description
     */
    public final static String SF_ROOT = "ROOT";

    /**
     * Name used to name a root process deployed without registry
     */
    public final static String SF_RUN_PROCESS = "sfRunProcess";

    /**
     * Name used to prefix the name of unnamed deployments
     */
    public final static String SF_UNNAMED = "unnamed_";

    /**
     * Name to name a deploy phase failure
     */
    public final static String SF_DEPLOY_FAILURE = "sfDeployFailure";

    /**
     * Name to name a start phase failure
     */
    public final static String SF_START_FAILURE = "sfStartFailure";

    /**
     * Name to name a start phase failure
     */
    public final static String SF_CORE_LOG = "SFCORE_LOG";


}
