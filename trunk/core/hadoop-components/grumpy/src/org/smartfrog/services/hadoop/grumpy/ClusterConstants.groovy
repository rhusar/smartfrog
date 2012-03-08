package org.smartfrog.services.hadoop.grumpy

/**
 *
 */
public interface ClusterConstants {
    String TEST_DATA_DIR = "test.build.data"
    String HADOOP_LOG_DIR = "hadoop.log.dir"

    String JOB_KEY_FILES = "tmpfiles";

    /** comma separated list of JARS that are uploaded to the distributed cache on job submission. 
     * No spaces before/after filesnames.
     * @see org.apache.hadoop.mapred.JobClient#copyAndConfigureFiles
     * */
    String JOB_KEY_JARS = "tmpjars";
    /** comma separated list of archive files */
    String JOB_KEY_ARCHIVES = "tmparchives";

    String MAP_KEY_CLASS = "mapred.mapoutput.key.class"

    String MAPRED_INPUT_DIR = "mapred.input.dir"
    String MAPRED_OUTPUT_DIR = "mapred.output.dir"
    String MAPRED_DISABLE_TOOL_WARNING = "mapred.used.genericoptionsparser";
}