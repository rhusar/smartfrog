package org.smartfrog.services.hadoop.bluemine.jobs

import groovy.util.logging.Commons
import org.apache.hadoop.mapred.JobConf
import org.smartfrog.services.hadoop.bluemine.mr.BluemineOptions
import org.smartfrog.services.hadoop.bluemine.mr.EventCountReducer
import org.smartfrog.services.hadoop.bluemine.mr.MapToDevice
import org.smartfrog.services.hadoop.grumpy.ClusterConstants
import org.apache.hadoop.fs.FileUtil

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
@Commons
class DevCount {

    static void main(String[] args) {
        JobConf conf = new JobConf()


        def cli = new CliBuilder(usage: 'DevCount [-jt jobtracker] [-d hdfs] [-v] [-p  properties] -s sourcedir -o outdir  ')
        // Create the list of options.

        cli.with {
            h longOpt: 'help', 'Show usage information'
            j longOpt: 'job-tracker', args: 1, argName: 'tracker', 'URL of Job Tracker'
            d longOpt: 'hdfs-url', args: 1, argName: 'hdfs', 'URL of Job tracker'
            p longOpt: 'properties', args: 1, argName: 'properties', 'Property file'
            s longOpt: 'sourcedir', args: 1, argName: 'src', 'directory of source files'
            o longOpt: 'outdir', args: 1, argName: 'out', 'directory for destination files'
            v longOpt: 'verbose', 'verbose job output'
        }

        def options = cli.parse(args)
        if (!options) {
            return
        }

        // Show usage text when -h or --help option is used.
        if (options.h) {
            cli.usage()
            return
        }
        String jtURL = BluemineOptions.DEFAULT_JOB_TRACKER;

        if (options.jt) {
            jtURL = options.jt
        }
        conf.set("mapred.job.tracker", jtURL);


        String hdfsURL = BluemineOptions.DEFAULT_FS;
        if (options.hdfs) {
            hdfsURL = options.hdfs
            conf.set("fs.default.name", hdfsURL);
        }
        if (options.p) {
            File propFile = requiredFile(options.p)
            Properties props = new Properties()
            props.load(new FileInputStream(propFile))
            props.each { name, value ->
                conf.set(name.toString(), value.toString())
            }
        }
        File srcDir = null, outDir = null
        if (options.s) {
            srcDir = requiredFile(options.s)
        } else {
            log.error("No source");
            System.exit(-1);
        }
        if (options.o) {
            outDir = outputDir(options.o)
        } else {
            log.error("No output directory");
            System.exit(-1);
        }

        boolean verbose = options.v
        BluemineJob job = BluemineJob.createBasicJob("devcount",
                conf,
                MapToDevice,
                EventCountReducer)
        job.addInput(srcDir)
        job.setupOutput(outDir)
        job.configuration.setInt("mapred.submit.replication", 1);
        log.info(job.toString())
        job.submit()
        boolean success = job.waitForCompletion(verbose)
        System.exit((success ? 0 : -2))
    }

    protected static File requiredFile(String name) {
        File file = new File(name)
        if (!file.exists()) {
            throw new FileNotFoundException(file.canonicalPath);
        }
        file
    }

    protected static File requiredDir(String name) {
        File dir = requiredFile(name)
        if (!dir.directory) {
            throw new IOException("Not a directory: " + dir.canonicalPath)
        }
        dir
    }

    protected static File maybeCreateDir(String name) {
        File dir = new File(name)
        if (!dir.exists()) {
            //this is what we want
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create directory "+ dir.canonicalPath)
            }
        } else {
            if (!dir.directory) {
                throw new IOException("Not a directory: " + dir.canonicalPath)
            }
        }
        dir
    }

    protected static File outputDir(String name) {
        File dir = new File(name)
        if (dir.exists()) {
            //trouble.
            FileUtil.fullyDelete(dir)
            
        }
        dir
    }

}
