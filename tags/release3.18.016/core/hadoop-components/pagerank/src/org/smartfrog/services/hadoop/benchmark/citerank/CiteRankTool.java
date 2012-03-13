/** (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.benchmark.citerank;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 *
 */
public abstract class CiteRankTool extends Configured implements Tool {
    /** {@value} */
    public static final String RANK_COUNT = "rank.count";
    /** {@value} */
    public static final String RANK_DANGLING = "rank.dangling";
    /** {@value} */
    public static final String RANKS_HTML = "ranks.html";
    /** {@value} */
    public static final int NUM_REPLICAS = 1;
    /** {@value} */
    public static final int NUM_MAP_TASKS = 6;
    /** {@value} */
    public static final String JAR_FILENAME = "build" + File.separator + "citerank.jar";
    /** {@value} */
    public static final int CHECK_CONVERGENCE_FREQUENCY = 10;
    /** {@value} */
    public static final String SORTED_RANKS = "sorted-ranks";
    /** {@value} */
    public static final String CONVERGENCE = "convergence";
    /** {@value} */
    public static final String DANGLING = "dangling";
    /** {@value} */
    public static final String COUNT = "count";
    /** {@value} */
    public static final String PREVIOUS_RANKS = "previous-ranks";
    /** {@value} */
    public static final String CURRENT_RANKS = "current-ranks";
    /** {@value} */
    protected static final String IN_AND_OUT = "<input path> <output path>";

    /**
     * Property to get the JAR from
     */
    protected static final String TEST_JAR_PATH = "test.component.jar.path";

    protected static final Log LOG = LogFactory.getLog(CiteRankTool.class);
    public static final String OPTION_PIXELS_ONLY = "pixels.only";
    public static final String OPTION_REPORT_CITESEER_URL = "report.citeseer.url";

    private static Counters counters;
    
    
    /**
     * Get the name of this tool
     *
     * @return the name of the tool
     */
    protected abstract String getName();

    /**
     * Create a configuration bound to this class, with various options set up
     *
     * @return the job
     */
    protected JobConf createConfiguration() {
        JobConf conf = new JobConf(getConf(), this.getClass());
        conf.setJobName(getName());
        conf.setJar(getJarName());
        conf.setNumMapTasks(NUM_MAP_TASKS);

        conf.setInt("dfs.replication", NUM_REPLICAS);
        return conf;
    }

  /**
   * work out the JAR name. looks for the system property {@link #TEST_JAR_PATH} if set,
   * else uses {@link #JAR_FILENAME}
   * @return the filename.
   */
  protected String getJarName() {
    return System.getProperty(TEST_JAR_PATH, JAR_FILENAME);
  }

  /**
     * Print a usage string and return an error code
     *
     * @param args arguments to follow the name
     * @return an error value
     */
    protected int usage(String args) {
        LOG.error("Usage: " + getName() + " " + args);
        return -1;
    }

    
    public static synchronized void resetCounters() {
        counters = null;
    } 
    
    public static synchronized void addCounters(Counters values) {
        if (counters==null) {
            counters = values;
        } else {
            counters.incrAllCounters(values);
        }
    }
    
    public static synchronized Counters getCounters() {
        return counters;
    }
    
    protected static int runJob(JobConf conf) throws IOException {
        RunningJob job = JobClient.runJob(conf);
        addCounters(job.getCounters());
        return 0;
    }

    protected static void close(Closeable c) throws IOException {
        if (c != null) {
            c.close();
        }
    }

    protected Path buildOutputPath(String pathName) throws IOException {
        Path outpath = new Path(pathName);
        FileSystem.get(getConf()).delete(outpath, true);
        return outpath;
    }

    protected JobConf createInputOutputConfiguration(String[] args) throws IOException {
        Path inpath = new Path(args[0]);
        Path outpath = buildOutputPath(args[1]);
        JobConf conf = createConfiguration();
        FileInputFormat.addInputPath(conf, inpath);
        FileOutputFormat.setOutputPath(conf, outpath);
        return conf;
    }

    /**
     * Run the instance against our configuration
     *
     * @param description
     * @param instance tool instance
     * @param args list of arguments
     * @throws Exception anything that went wrong
     */
    protected void exec(String description, CiteRankTool instance, String... args) throws Exception {
        LOG.info("Running " + description);
        ToolRunner.run(getConf(), instance, args);
    }
    /*
         [java] 09/06/25 16:47:03 INFO mapred.JobClient: Job complete: job_local_0002
     [java] 09/06/25 16:47:03 INFO mapred.JobClient: Counters: 11
     [java] 09/06/25 16:47:03 INFO mapred.JobClient:   File Systems
     [java] 09/06/25 16:47:03 INFO mapred.JobClient:     Local bytes read=423569384
     [java] 09/06/25 16:47:03 INFO mapred.JobClient:     Local bytes written=390279157
     [java] 09/06/25 16:47:03 INFO mapred.JobClient:   Map-Reduce Framework
     [java] 09/06/25 16:47:03 INFO mapred.JobClient:     Reduce input groups=1
     [java] 09/06/25 16:47:03 INFO mapred.JobClient:     Combine output records=4
     [java] 09/06/25 16:47:03 INFO mapred.JobClient:     Map input records=717172
     [java] 09/06/25 16:47:03 INFO mapred.JobClient:     Reduce output records=1
     [java] 09/06/25 16:47:03 INFO mapred.JobClient:     Map output bytes=10040408
     [java] 09/06/25 16:47:03 INFO mapred.JobClient:     Map input bytes=17258927
     [java] 09/06/25 16:47:03 INFO mapred.JobClient:     Combine input records=717175
     [java] 09/06/25 16:47:03 INFO mapred.JobClient:     Map output records=717172
     [java] 09/06/25 16:47:03 INFO mapred.JobClient:     Reduce input records=1
     [java] 09/06/25 16:47:03 INFO citerank.CiteRankTool: Running InitializeRanks

     */
    
    
    
}
