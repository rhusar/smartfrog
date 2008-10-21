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
package org.smartfrog.services.hadoop.conf;

import org.apache.hadoop.fs.Path;

/**
 * Created 18-Apr-2008 14:34:53
 */


public interface ConfigurationAttributes {

    /**
     * Filename of JAR file to upload {@value}
     */
    String MAPRED_JAR = "mapred.jar";

    /**
     * Set the {@link Path} of the input directory for the map-reduce job.
     *
     * @param dir the {@link Path} of the input directory for the map-reduce job.
     */
    String MAPRED_INPUT_DIR = "mapred.input.dir";

    /**
     * local dirs (string[]) {@value}
     */
    String MAPRED_LOCAL_DIR = "mapred.local.dir";

    /**
     * local dirs (string[]) {@value}
     */
    String MAPRED_OUTPUT_DIR = "mapred.output.dir";


    /**
     * {@value} default,"/tmp/hadoop/mapred/system"
     */
    String MAPRED_SYSTEM_DIR = "mapred.system.dir";

    String USER_NAME = "user.name";

    String KEEP_FAILED_TASK_FILES = "keep.failed.task.files";

    /**
     * a regular expression for task names that should be kept. The regular expression ".*_m_000123_0" would keep the
     * files for the first instance of map 123 that ran.
     */

    String KEEP_TASK_FILES_PATTERN = "keep.task.files.pattern";

    /**
     * the current working directory for the default file system.
     */
    String MAPRED_WORKING_DIR = "mapred.working.dir";

    String MAPRED_COMPRESS_MAP_OUTPUT = "mapred.compress.map.output";
    String MAPRED_INPUT_FORMAT_CLASS = "mapred.input.format.class";
    String MAPRED_INPUT_KEY_CLASS = "mapred.input.key.class";
    String MAPRED_INPUT_VALUE_CLASS = "mapred.input.value.class";
    String MAPRED_JOB_SPLIT_FILE = "mapred.job.split.file";
    String MAPRED_MAPPER_CLASS = "mapred.mapper.class";

    String MAPRED_MAP_OUTPUT_COMPRESSION_TYPE = "mapred.map.output.compression.type";
    String MAPRED_MAP_OUTPUT_COMPRESSION_CODEC = "mapred.map.output.compression.codec";
    String MAPRED_MAP_OUTPUT_KEY_CLASS = "mapred.mapoutput.key.class";
    String MAPRED_MAP_OUTPUT_VALUE_CLASS = "mapred.mapoutput.value.class";
    String MAPRED_MAP_RUNNER_CLASS = "mapred.map.runner.class";

    String MAPRED_OUTPUT_FORMAT_CLASS = "mapred.output.format.class";
    String MAPRED_OUTPUT_KEY_CLASS = "mapred.output.key.class";
    String MAPRED_OUTPUT_KEY_COMPARATOR_CLASS = "mapred.output.key.comparator.class";
    String MAPRED_OUTPUT_VALUE_CLASS = "mapred.output.value.class";
    String MAPRED_OUTPUT_VALUE_GROUPFN_CLASS = "mapred.output.value.groupfn.class";

    String MAPRED_PARTITIONER_CLASS = "mapred.partitioner.class";

    String FS_DEFAULT_NAME = "fs.default.name";

    String IO_FILE_BUFFER_SIZE = "io.file.buffer.size";

    String IO_SEQFILE_COMPRESS_BLOCKSIZE = "io.seqfile.compress.blocksize";
    String IO_SERIALIZATIONS = "io.serializations";


    String FS_LOCAL_BLOCK_SIZE = "fs.local.block.size";

    /**
     * URL to GET when the job is finished
     * The uri can contain 2 special parameters: $jobId and $jobStatus. Those, if present, are
     * replaced by the job's identifier and completion-status respectively.</p>
     *
     */
    String JOB_END_NOTIFICATION_URL = "job.end.notification.url";

    String MAPRED_COMBINER_CLASS = "mapred.combiner.class";
    String MAPRED_SPECULATIVE_EXECUTION = "mapred.speculative.execution";
    /**
     * Should speculative execution be used for this job for map tasks?
     */
    String MAPRED_MAP_TASKS_SPECULATIVE_EXECUTION = "mapred.map.tasks.speculative.execution";
    /**
     * Should speculative execution be used for this job for reduce tasks?
     */
    String MAPRED_REDUCE_TASKS_SPECULATIVE_EXECUTION = "mapred.reduce.tasks.speculative.execution";
    ;

    /**
     * The number of map tasks for this job.
     */
    String MAPRED_MAP_TASKS = "mapred.map.tasks";

    /**
     * the number of reduce tasks for this job.
     */
    String MAPRED_REDUCE_TASKS = "mapred.reduce.tasks";

    /**
     * the max number of attempts per map task.
     */
    String MAPRED_MAP_MAX_ATTEMPTS = "mapred.map.max.attempts";

    /**
     * the max number of attempts per reduce task.
     */
    String MAPRED_REDUCE_MAX_ATTEMPTS = "mapred.reduce.max.attempts";

    /**
     * the user-specified job name. This is only used to identify the job to the user.
     */
    String MAPRED_JOB_NAME = "mapred.job.name";

    /**
     * the JobPriority for this job.
     */
    String MAPRED_JOB_PRIORITY = "mapred.job.priority";

    /**
     * the job tracker URL
     */
    String MAPRED_JOB_TRACKER = "mapred.job.tracker";
    /**
     * the string that means the job tracker is local
     */
    String MAPRED_JOB_TRACKER_LOCAL = "local";

    /**
     * The job tracker HTTP URL
     */
    String MAPRED_JOB_TRACKER_HTTP_ADDRESS = "mapred.job.tracker.http.address";


    /**
     * the maximum no. of failures of a given job per tasktracker. If the no. of task failures exceeds
     * <code>noFailures</code>, the tasktracker is <i>blacklisted</i> for this job.
     */
    String MAPRED_MAX_TRACKER_FAILURES = "mapred.max.tracker.failures";

    /**
     * the maximum percentage of map tasks that can fail without the job being aborted.
     */
    String MAPRED_MAX_MAP_FAILURES_PERCENT = "mapred.max.map.failures.percent";

    /**
     * the maximum percentage of reduce tasks that can fail without the job being aborted_
     */
    String MAPRED_MAX_REDUCE_FAILURES_PERCENT = "mapred.max.reduce.failures.percent";


    /**
     * whether the task profiling is enabled.
     */
    String MAPRED_TASK_PROFILE = "mapred.task.profile";


    String MAPRED_TASK_TRACKER_HTTP_ADDRESS = "mapred.task.tracker.http.address";

    /**
     * debug Script for the mapred job for failed map tasks.
     */
    String MAPRED_MAP_TASK_DEBUG_SCRIPT = "mapred.map.task.debug.script";
    /**
     * debug Script for the mapred job for failed reduce tasks.
     */
    String MAPRED_REDUCE_TASK_DEBUG_SCRIPT = "mapred.reduce.task.debug.script";


    /**
     * Get the user-specified session identifier. The default is the empty string.
     *
     * The session identifier is used to tag metric data that is reported to some performance metrics system via the
     * org.apache.hadoop.metrics API.  The session identifier is intended, in particular, for use by Hadoop-On-Demand
     * (HOD) which allocates a virtual Hadoop cluster dynamically and transiently. HOD will set the session identifier
     * by modifying the hadoop-site.xml file before starting the cluster.
     *
     * When not running under HOD, this identifer is expected to remain set to the empty string.
     */
    String SESSION_ID = "session.id";

    String DFS_DATA_DIR = "dfs.data.dir";
    String DFS_NAME_DIR = "dfs.name.dir";
    String DFS_NAMENODE_STARTUP = "dfs.namenode.startup";
    String DFS_HTTP_ADDRESS = "dfs.http.address";
    String HADOOP_LOG_DIR = "hadoop.log.dir";
    String DFS_DATANODE_HTTPS_ADDRESS = "dfs.datanode.https.address";
}
