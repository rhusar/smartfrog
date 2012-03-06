package org.smartfrog.services.hadoop.bluemine.jobs

import groovy.util.logging.Commons
import org.apache.hadoop.fs.FileUtil
import org.apache.hadoop.mapred.JobConf
import org.smartfrog.services.hadoop.bluemine.mr.BluemineOptions
import org.smartfrog.services.hadoop.grumpy.GrumpyTools

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
abstract class BlueMain {

    protected abstract boolean execute(String[] args);

    String getName() {"BlueMain"}

    protected static executeAndExit(BlueMain main, String[] args) {
        try {
            boolean success = main.execute(args)
            System.exit((success ? 0 : -1))
        } catch (MainArgException e) {
            log.error(e.toString())
            System.exit(e.exitCode)
        } catch (Exception e) {
            log.error(e.toString(), e)
            System.exit(-2)
        }
    }

    protected OptionAccessor parseCommandLine(String[] args) {
        CliBuilder cli = new CliBuilder(usage: "${name} [-jt jobtracker] [-d hdfs] [-v] [-p  properties] -s sourcedir -o outdir  ")
        // Create the list of options.

        cli.with {
            h longOpt: 'help', 'Show usage information'
            j longOpt: 'job-tracker', args: 1, argName: 'tracker', 'URL of Job Tracker'
            f longOpt: 'filesystem', args: 1, argName: 'hdfs', 'URL of Job tracker'
            p longOpt: 'properties', args: 1, argName: 'properties', 'Property file'
            s longOpt: 'sourcedir', args: 1, argName: 'src', 'directory of source files'
            o longOpt: 'outdir', args: 1, argName: 'out', 'directory for destination files'
            'do' longOpt: 'deloutdir', args: 1, argName: 'do', 'output directory -delete first'
            v longOpt: 'verbose', 'verbose job output'
        }

        OptionAccessor options = cli.parse(args)
        if (!options) {
            options = null;
        }

        // Show usage text when -h or --help option is used.
        if (options.h) {
            cli.usage()
            options = null;
        }
        return options
    }

    protected static File requiredFile(String name) {
        File file = new File(name)
        if (!file.exists()) {
            throw new MainArgException("File not found" + file.canonicalPath);
        }
        file
    }

    protected static File requiredDir(String name) {
        File dir = requiredFile(name)
        if (!dir.directory) {
            throw new MainArgException("Not a directory: " + dir.canonicalPath)
        }
        dir
    }

    protected static File maybeCreateDir(String name) {
        File dir = new File(name)
        if (!dir.exists()) {
            //this is what we want
            if (!dir.mkdirs()) {
                throw new MainArgException("Failed to create directory " + dir.canonicalPath)
            }
        } else {
            if (!dir.directory) {
                throw new MainArgException("Not a directory: " + dir.canonicalPath)
            }
        }
        dir
    }

    protected static File outputDir(String name, boolean delete) {
        File dir = new File(name)
        if (dir.exists()) {
            if (delete) {
                FileUtil.fullyDelete(dir)
            } else {
                throw new MainArgException("Output directory exists and deletion not enabled")
            }
        }
        dir
    }

    /**
     * extract source and dest dirs from the options, set them on the job,
     * then execute it 
     * @param options
     * @param job
     * @return whether or not the job completed successfully
     */
    protected boolean bindAndExecute(OptionAccessor options, BluemineJob job) {
        boolean verbose = options.v
        bindSourceDir(options, job)
        bindOutputDir(options, job)
        job.configuration.setInt("mapred.submit.replication", 1)
        log.info(job.toString())
        job.submit()
        return job.waitForCompletion(verbose)
    }

    /**
     * Bind up the output direcory -this may be cleared on startup if requested
     * @param options output directory
     * @param job job 
     */
    protected bindOutputDir(OptionAccessor options, BluemineJob job) {
        File outDir
        if (options.o) {
            outDir = outputDir(options.o, false)
        } else
        if (options."do") {
            outDir = outputDir(options."do", true)
        } else {
            throw new MainArgException("No output directory")
        }
        job.setupOutput(outDir)
    }

    /**
     * Bind up the source direcory -if not set fail
     * @param options output directory
     * @param job job 
     */
    protected bindSourceDir(OptionAccessor options, BluemineJob job) {
        String source = options.s
        if (!source) {
            throw new MainArgException("No source")
        }
        File srcDir
        srcDir = requiredFile(source)
        job.addInput(srcDir)
    }

    /**
     * Load a property file, which must exist
     * @param conf configuration to load into
     * @param propertyFilename filename
     */
    protected void loadPropertyFile(JobConf conf, String propertyFilename) {
        if (propertyFilename) {
            File propFile = requiredFile(propertyFilename)
            Properties props = new Properties()
            props.load(new FileInputStream(propFile))
            props.each { name, value ->
                conf.set(name.toString(), value.toString())
            }
        }
    }

    protected void setFilesystemURL(JobConf conf, OptionAccessor options) {
        String hdfsURL = BluemineOptions.DEFAULT_FS;
        if (options.f) {
            hdfsURL = options.f
            log.info("fs.default.name = $hdfsURL")
            conf.set("fs.default.name", hdfsURL)
        }
    }

    protected void setTrackerURL(JobConf conf, OptionAccessor options) {
        String jtURL = BluemineOptions.DEFAULT_JOB_TRACKER;

        if (options.j) {
            jtURL = options.j
        }
        log.info("Job tracker URL = $jtURL")
        GrumpyTools.checkURL(jtURL, 15000)
        conf.set("mapred.job.tracker", jtURL)
    }

}
