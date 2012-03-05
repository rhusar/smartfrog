package org.smartfrog.services.hadoop.bluemine.jobs

import org.smartfrog.services.hadoop.grumpy.GrumpyJob
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapred.JobConf
import org.smartfrog.services.hadoop.grumpy.GrumpyTools
import groovy.util.logging.Commons
import org.apache.hadoop.io.Text
import org.smartfrog.services.hadoop.grumpy.ClusterConstants
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.apache.hadoop.io.IntWritable

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
class BluemineJob extends GrumpyJob {

    BluemineJob(String jobName) {
        super(jobName)
    }

    BluemineJob(Configuration conf) {
        super(conf)
    }

    BluemineJob(Configuration conf, String jobName) {
        super(conf, jobName)
    }
    
    @Override
    String toString() {
        StringBuilder builder = new StringBuilder(200)
        builder.append("mapred.input.dir ").append(configuration.get("mapred.input.dir"))
        builder.append("mapred.output.dir ").append(configuration.get("mapred.output.dir"))
        builder.append("Lib jars ").append(configuration.get(ClusterConstants.JOB_KEY_JARS))
        builder.append("mapred.mapoutput.key.class").append(configuration.get(ClusterConstants.MAP_KEY_CLASS)) 
        builder.toString()
    }

    /**
     * Create a basic job with the given M & R jobs. 
     * The Groovy JAR is added as another needed JAR; the mapClass is set as the main jar of the job
     * @param name job name
     * @param conf configuration
     * @param mapClass mapper
     * @param reduceClass reducer
     * @return
     */
    static BluemineJob createBasicJob(String name,
                                        JobConf conf,
                                        Class mapClass,
                                        Class reduceClass) {
        BluemineJob job = new BluemineJob(conf, name)
        
        job.addGroovyJar();
        log.info(" map class is $mapClass reduce class is $reduceClass");
        String jar = GrumpyTools.findContainingJar(mapClass)
        log.info(" map class is at $jar");
        job.jarByClass = mapClass
        job.mapperClass = mapClass
        job.reducerClass = reduceClass
        job
    }
}
