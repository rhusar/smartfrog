package org.smartfrog.services.hadoop.testing

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hdfs.server.common.HdfsConstants.StartupOption
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.smartfrog.services.hadoop.instances.LocalDFSCluster
import org.smartfrog.services.hadoop.instances.LocalMRCluster
import org.smartfrog.services.hadoop.operations.conf.ConfUtils

/**
 * This is a groovy test base for Hadoop MR jobs
 */
abstract class GroovyHadoopTestBase extends GroovyTestCase implements Closeable {

    LocalMRCluster mrCluster
    LocalDFSCluster dfsCluster
    Log log = LogFactory.getLog(this.getClass())

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
        dfsCluster=null
    }


    Configuration createJobConfiguration() {
        Configuration conf = new Configuration();
        return conf

    }

    Job createTextKeyIntValueJob(String name,
                                 Configuration conf,
                                 Class mapClass,
                                 Class reduceClass) {
        Job job = new Job(conf, name)
        job.setJarByClass(mapClass)
        job.setMapperClass(mapClass)
        job.setReducerClass(reduceClass)
        job.setMapOutputKeyClass(Text.class)
        job.setMapOutputValueClass(IntWritable.class)
        return job
    }

    void setupOutput(Job job, String outputURL) {
        log.info("Output directory is ${outputURL}")
        FileOutputFormat.setOutputPath(job, new Path(outputURL));
    }

    void setupInput(Job job, String inputURL) {
        log.info("Input Path is ${inputURL}")
        FileInputFormat.addInputPath(job, new Path(inputURL));
    }


    void setupOutput(Job job, File output) {
        String outputURL = convertToUrl(output)
        setupOutput(job, outputURL)
    }

    void setupInput(Job job, File input) {
        String inputURL = convertToUrl(input)
        setupInput(job, inputURL)
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

    protected File getSyspropFile(String propertyName) {
        String dataDir = System.getProperty(propertyName)
        if (!dataDir) {
            throw new IOException("Unset property: ${propertyName} ");
        }
        File dataDirectory = new File(dataDir)
        return dataDirectory
    }

    File prepareTestOutputDir(String subdir) {
        File outDir = getSyspropFile(TEST_OUTPUT_DATA_DIR)
        log.info("${TEST_OUTPUT_DATA_DIR} = ${outDir}")
        File jobOutDir = new File(outDir, subdir);
        if (jobOutDir.exists()) {
            if (jobOutDir.isDirectory()) {
                log.info("Cleaning up " + jobOutDir)
                //delete the children
                jobOutDir.eachFile { file ->
                    log.info("deleting " + file)
                    file.delete()
                }
                jobOutDir.delete()
            } else {
                throw new IOException("Not a directory: ${jobOutDir}")
            }
        } else {
            //not found, do nothing
            log.debug("No output dir yet")
        }
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


    File addTestOutputDir(Job job, String subdir) {
        File dir = prepareTestOutputDir(subdir)
        setupOutput(job, dir)
        return dir
    }


    void runJob(Job job) {
        boolean success = job.waitForCompletion(true)
        assertTrue("Job failed", success)
    }

    int dumpDir(Log dumpLog, File dir) {
        if(!dir.exists()) {
            log.warn("Not found: ${dir}");
            return -1;
        }
        if(!dir.isDirectory()) {
            log.warn("Not a directory: ${dir}");
            return -1;
        }
        int count = 0;
        dir.eachFile { file ->
            count ++
            dumpFile(dumpLog, file)
        }
        return count;
    }

    void dumpFile(Log log, File file) {
        log.info("File : ${file} of size ${file.length()}")
    }
}
