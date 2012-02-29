package org.smartfrog.services.hadoop.grumpy

import groovy.util.logging.Commons
import org.apache.commons.logging.Log
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hdfs.server.common.HdfsConstants.StartupOption
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapred.JobConf

/**
 * This is a groovy test base for Hadoop MR jobs
 */
@Commons
abstract class GrumpyHadoopTestBase extends GroovyTestCase
        implements Closeable {

    final static String TEST_DATA_DIR = "test.build.data"
    final static String HADOOP_LOG_DIR = "hadoop.log.dir"

    /**
     * Test property used to define the input directory of data:
     * {@value}
     */

    public static final String TEST_INPUT_DATA_DIR = "test.input.data.dir"

    /**
     * Test property used to define the output directory of data:
     * {@value}
     */

    public static final String TEST_OUTPUT_DATA_DIR = "test.output.data.dir"
    protected LocalMRCluster mrCluster
    protected LocalDFSCluster dfsCluster

    void createMrCluster(int nodes, String fsURI, JobConf conf) {
        mrCluster = LocalMRCluster.createInstance(nodes, fsURI, 1, null, conf)
    }

    void createDfsCluster(int nodes, Properties properties) {
        JobConf conf = createClusterJobConf()
        ConfUtils.copyProperties(conf, properties);
        dfsCluster = LocalDFSCluster.createInstance(0, conf,
                nodes, false, true, true, StartupOption.FORMAT, null, null, null);

    }

    JobConf createClusterJobConf() {
        return new JobConf();
    }



    @Override
    protected void tearDown() {
        close()
        super.tearDown()
    }

    @Override
    void close() {
        mrCluster?.close()
        mrCluster = null
        dfsCluster?.close()
        dfsCluster = null
    }


    Configuration createJobConfiguration() {
        Configuration conf = new Configuration();
        return conf

    }

    GrumpyJob createTextKeyIntValueJob(String name,
                                       Configuration conf,
                                       Class mapClass,
                                       Class reduceClass) {
        GrumpyJob job = createBasicJob(name,
                conf,
                mapClass,
                reduceClass)
        job.setMapOutputKeyClass(Text.class)
        job.setMapOutputValueClass(IntWritable.class)
        return job
    }

    GrumpyJob createBasicJob(String name,
                             Configuration conf,
                             Class mapClass,
                             Class reduceClass) {
        GrumpyJob job = new GrumpyJob(conf, name)
        job.setJarByClass(mapClass)
        job.setMapperClass(mapClass)
        job.setReducerClass(reduceClass)
        return job
    }

    void setupOutput(GrumpyJob job, String outputURL) {
        job.setupOutput(outputURL)
    }

    void setupInput(GrumpyJob job, String inputURL) {
        job.setupInput(inputURL)
    }

    void setupOutput(GrumpyJob job, File output) {
        job.setupOutput(output)
    }

    void setupInput(GrumpyJob job, File input) {
        job.setupInput(input)
    }

    File getTestDataDir() {
        File dataDirectory = getSyspropFile(TEST_INPUT_DATA_DIR)
        if (!dataDirectory.exists()) {
            throw new IOException("Property ${TEST_INPUT_DATA_DIR} is set to a nonexistent directory ${dataDirectory}")
        }
        if (!dataDirectory.isDirectory()) {
            throw new IOException("Property ${TEST_INPUT_DATA_DIR} is not a directory: ${dataDirectory}")
        }
        return dataDirectory;
    }

    /**
     * Get the filename from a specific property file
     * @param propertyName mandatory property name
     * @return the file referred to (may be relative)
     * @throws IOException if the property is unset
     */
    protected File getSyspropFile(String propertyName) throws IOException {
        String dataDir = System.getProperty(propertyName)
        if (!dataDir) {
            throw new IOException("Unset property: ${propertyName} ");
        }
        File dataDirectory = new File(dataDir)
        return dataDirectory
    }

    /**
     * Set up the output dir for tests
     * @param testDir the test directory under the directory set by
     * the property {@link #TEST_OUTPUT_DATA_DIR}
     * @return the output directory for the job
     * @throws IOException if the property is unset
     */
    File prepareTestOutputDir(GrumpyJob job, String testDir)
    throws IOException {
        File outDir = getSyspropFile(TEST_OUTPUT_DATA_DIR)
        log.info("${TEST_OUTPUT_DATA_DIR} = ${outDir}")
        File jobOutDir = new File(outDir, testDir);
        GrumpyTools.deleteDirectoryTree(jobOutDir)
        return jobOutDir
    }

    String convertToUrl(File file) {
        return file.toURI().toString();
    }

    File getDataFile(String filename) {
        File dataDir = getTestDataDir()
        File testData = new File(dataDir, filename)
        if (!testData.exists()) {
            throw new IOException("Missing file ${testData}")
        }
        return testData
    }

    File addTestOutputDir(GrumpyJob job, String subdir) {
        File dir = prepareTestOutputDir(job, subdir)
        setupOutput(job, dir)
        return dir
    }


    void runJob(GrumpyJob job) {
        boolean success = job.waitForCompletion(true)
        assertTrue("Job failed", success)
    }

    int dumpDir(Log dumpLog, File dir) {
        return GrumpyTools.dumpDir(dumpLog, dir)
    }
}
