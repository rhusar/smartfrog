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
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 *
 */
public abstract class CiteRankTool extends Configured implements Tool {
    public static final String RANK_COUNT = "rank.count";
    public static final String RANK_DANGLING = "rank.dangling";
    public static final String RANKS_HTML = "ranks.html";
    public static final int NUM_REPLICAS = 1;
    public static final int NUM_MAP_TASKS = 6;
    public static final String JAR_FILENAME = "build" + File.separator + "citerank.jar";
    public static final int CHECK_CONVERGENCE_FREQUENCY = 10;
    public static final String SORTED_RANKS = "sorted-ranks";
    public static final String CONVERGENCE = "convergence";
    public static final String DANGLING = "dangling";
    public static final String COUNT = "count";
    public static final String PREVIOUS_RANKS = "previous-ranks";
    public static final String CURRENT_RANKS = "current-ranks";
    protected static final Log LOG = LogFactory.getLog(CheckConvergence.class);
    protected static final String IN_AND_OUT = "<input path> <output path>";

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
        conf.setJar(JAR_FILENAME);
        conf.setNumMapTasks(NUM_MAP_TASKS);

        conf.setInt("dfs.replication", NUM_REPLICAS);
        return conf;
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

    protected static int runJob(JobConf conf) throws IOException {
        JobClient.runJob(conf);
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
        Path outpath = new Path(args[1]);
        FileSystem.get(getConf()).delete(outpath, true);

        JobConf conf = createConfiguration();
        FileInputFormat.addInputPath(conf, inpath);
        FileOutputFormat.setOutputPath(conf, outpath);
        return conf;
    }

    /**
     * Run the instance against our configuration
     *
     * @param instance tool instance
     * @param args list of arguments
     * @throws Exception anything that went wrong
     */
    protected void exec(CiteRankTool instance, String... args) throws Exception {
        ToolRunner.run(getConf(), instance, args);
    }
}
