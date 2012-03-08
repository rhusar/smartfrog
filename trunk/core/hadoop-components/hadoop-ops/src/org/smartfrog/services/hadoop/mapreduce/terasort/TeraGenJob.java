/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartfrog.services.hadoop.mapreduce.terasort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.smartfrog.services.hadoop.grumpy.ClusterConstants;
import org.smartfrog.services.hadoop.mapreduce.submitter.JobRunningTool;

/**
 * Generate the official terasort input data set. The user specifies the number of rows and the output directory and
 * this class runs a map/reduce program to generate the data. The format of the data is: <ul> <li>(10 bytes key) (10
 * bytes rowid) (78 bytes filler) \r \n <li>The keys are random characters from the set ' ' .. '~'. <li>The rowid is the
 * right justified row id as a int. <li>The filler consists of 7 runs of 10 characters from 'A' to 'Z'. </ul>
 * <p/>
 * <p> To run the program: <b>bin/hadoop jar hadoop-examples-*.jar teragen 10000000000 in-dir</b>
 */
@SuppressWarnings({"deprecation"})

public class TeraGenJob extends JobRunningTool {

    static long getNumberOfRows(Configuration job) {
        return job.getLong(TeraConstants.TERASORT_NUM_ROWS, 0);
    }

    static void setNumberOfRows(Configuration job, long numRows) {
        job.setLong("terasort.num-rows", numRows);
    }

    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    @Override
    public RunningJob runJob(String[] args) throws Exception {
        JobConf job = (JobConf) getConf();
        setNumberOfRows(job, Long.parseLong(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setJobName("TeraGen");
        job.setJarByClass(TeraGenJob.class);
        job.setMapperClass(TeraGenMapper.class);
        job.setNumReduceTasks(0);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormat(TeraGenRangeInputFormat.class);
        job.setOutputFormat(TeraOutputFormat.class);
        job.setBoolean(ClusterConstants.MAPRED_DISABLE_TOOL_WARNING, true);
        return JobClient.runJob(job);
    }


}
