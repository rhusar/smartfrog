

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

import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.conf.Configuration
import org.apache.commons.logging.LogFactory
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat

/**
 * This class 
 */
class GrumpyJob extends Job {

    static final LOG = LogFactory.getLog(GrumpyJob.class)

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
        LOG.info("Output directory is ${outputURL}")
        FileOutputFormat.setOutputPath(this, new Path(outputURL));
    }

    void setupInput(String inputURL) {
        LOG.info("Input Path is ${inputURL}")
        FileInputFormat.addInputPath(this, new Path(inputURL));
    }


    void setupOutput(File output) {
        String outputURL = GrumpyTools.convertToUrl(output)
        setupOutput(outputURL)
    }

    void setupInput(File input) {
        String inputURL = GrumpyTools.convertToUrl(input)
        setupInput(inputURL)
    }



}
