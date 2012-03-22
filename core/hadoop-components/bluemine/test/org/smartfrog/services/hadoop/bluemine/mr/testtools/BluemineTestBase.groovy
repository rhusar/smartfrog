package org.smartfrog.services.hadoop.bluemine.mr.testtools

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.IntWritable
import org.smartfrog.services.hadoop.bluemine.reducers.CountReducer
import org.smartfrog.services.hadoop.grumpy.GrumpyHadoopTestBase
import org.smartfrog.services.hadoop.grumpy.GrumpyJob
import org.smartfrog.services.hadoop.bluemine.output.ExtTextOutputFormat
import org.smartfrog.services.hadoop.bluemine.BluemineOptions

/**
 *
 */
class BluemineTestBase extends GrumpyHadoopTestBase implements BluemineOptions {

    public static final String TEST_INPUT_DATA_DIR = "test.input.data.dir"
    public static final String TEST_OUTPUT_DATA_DIR = "test.output.data.dir"
    public static final String TEST_INPUT_DATA_FILE = "test.input.data.file"
    public static final String GATE1_50K = "gate1-50k.csv"
    public static final String GATE1_SMALL = "gate1-small.csv"
    

    public static final String SMILEY = "gate1,2afaf990ce75f0a7208f7f012c8d12ad,,2006-10-30,16:06:54,Smiley"
    public static final String NO_NAME = "gate1,02e73779c77fcd4e9f90a193c4f3e7ff,,2006-10-30,16:07:15,"
    public static final String SMILEY2 = "gate1,2afaf990ce75f0a7208f7f012c8d12ad,,2006-10-30,16:07:24,Smiley"
    public static final String SMILEY3 = "gate1,2afaf990ce75f0a7208f7f012c8d12ad,,2006-10-30,16:07:54,Smiley"
    public static final String SMILEY4 = "gate1,2afaf990ce75f0a7208f7f012c8d12ad,,2006-10-30,16:08:56,Smiley"

    public static final String COMMA1 = "0017F2A49B6F,5c598739138321f92971dc6f6ec41344,,2006-10-30,21:34:11,,) Where am i?"
    public static final String COMMA2 = "0017F2A49B6F,5c598739138321f92971dc6f6ec41344,,2007-09-06,21:34:11,)\"\', Where am i?"
    public static final String VKLAPTOP = "gate3,f1191b79236083ce59981e049d863604,,2006-1-1,23:06:57,vklaptop"
    public static final String[] LINES = [
            NO_NAME,
            SMILEY,
            VKLAPTOP,
            COMMA1
    ]

    /**
     * These are erroneous records that show up in the real dataset
     */
    protected static final String[] BAD_LINES = [
            ",45c015c602e28f3f790e2937ff7a8a0b,,2009-01-21,09:14,",
            ",,"
    ]
    /**
     * Add the small gate1 input set to a job as the input
     * @param job job to patch
     */
    void addTestDataset(GrumpyJob job) {
        addDataset(job, GATE1_50K)
    }

    protected void addDataset(GrumpyJob job, String dataset) {
        String sourceFile = System.getProperty(TEST_INPUT_DATA_FILE, dataset);
        File file = getDataFile(sourceFile)
        addInput(job, file)
    }

    /**
     * Create an initial MR job 
     * @param testname name of the test (which defines the output directoyr too
     * @param mapClass class to use in map
     * @param reduceClass class to use in reduction
     * @return ( job : GrumpyJob , output directory : file )
     */
    List createMRJob(String testname, Class mapClass, Class reduceClass) {
        GrumpyJob job
        File outDir
        (job, outDir) = createMRJobNoDataset(testname, mapClass, reduceClass)
        addTestDataset(job)
        [job, outDir]
    }

    protected List createMRJobNoDataset(String testname, Class mapClass, Class reduceClass) {
        Configuration conf = createJobConfiguration()
        GrumpyJob job = createTextKeyIntValueJob(testname,
                conf,
                mapClass,
                reduceClass)
        File outDir = addTestOutputDir(job, testname)
        [job, outDir]
    }

    /**
     * Run an event job against the specified mapper, using int
     * as the output value of the map, and the Count reducer as the reducer
     * @param name job name
     * @param mapper mapper class
     * @return the output directory
     *
     */
    File runCountJob(String name, Class mapper) {
        GrumpyJob job
        File outDir
        (job, outDir) = createMRJob(name,
                                    mapper,
                                    CountReducer)
        job.mapOutputValueClass = IntWritable
        job.outputFormatClass = ExtTextOutputFormat
        runJob(job)
        dumpDir(LOG, outDir)
        outDir
    }

}
