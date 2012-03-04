/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.hadoop.grumpy

import groovy.util.logging.Commons
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat

/**
 * This class 
 */
@Commons
class GrumpyJob extends Job {

    GrumpyJob(String jobName) {
        super(null, jobName)
    }

    GrumpyJob(Configuration conf) {
        super(conf)
    }

    GrumpyJob(Configuration conf, String jobName) {
        super(conf, jobName)
    }


    void setupOutput(String outputURL) {
        log.info("Output directory is ${outputURL}")
        FileOutputFormat.setOutputPath(this, new Path(outputURL));
    }

    void addInput(String inputURL) {
        log.info("Input Path is ${inputURL}")
        FileInputFormat.addInputPath(this, new Path(inputURL));
    }


    void setupOutput(File output) {
        String outputURL = GrumpyTools.convertToUrl(output)
        setupOutput(outputURL)
    }

    void addInput(File input) {
        String inputURL = GrumpyTools.convertToUrl(input)
        addInput(inputURL)
    }

    void addJarList(List jarlist) {
        String listAsString = GrumpyTools.joinList(jarlist,",")
        configuration.set(ClusterConstants.JOB_KEY_JARS, listAsString)
    }

    /**
     * Add the groovy jar. if this is groovy-all, you get everything.
     */
    String addGroovyJar() {
        return addJar(GString.class)
    }

    String addJar(Class jarClass) {
        String file = GrumpyTools.findContainingJar(jarClass)
        if (!file) {
            throw new FileNotFoundException("No JAR containing class \"${jarClass}\"")
        }
        log.info("Jar containing class ${jarClass} is ${file}")
        addJar(file)
        file
    }
    
    void addJar(String jarFile) {
        String jarlist = configuration.get(ClusterConstants.JOB_KEY_JARS, null)
        if (jarlist != null) {
            jarlist = jarlist + "," + jarFile;
        } else {
            jarlist = jarFile;
        }
        configuration.set(ClusterConstants.JOB_KEY_JARS, jarlist)
    }
    
    
}
