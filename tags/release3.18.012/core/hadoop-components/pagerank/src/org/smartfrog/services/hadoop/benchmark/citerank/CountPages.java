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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.ToolRunner;

public class CountPages extends CiteRankTool {

    @Override
    protected String getName() {
        return "CountPages";
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            return usage(IN_AND_OUT);
        }


        JobConf conf = createInputOutputConfiguration(args);

        conf.setMapperClass(CountPagesMapper.class);
        conf.setCombinerClass(CountPagesReducer.class);
        conf.setReducerClass(CountPagesReducer.class);

        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(LongWritable.class);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(LongWritable.class);

        conf.setNumMapTasks(CiteRankTool.NUM_MAP_TASKS);
        conf.setNumReduceTasks(1);

        return runJob(conf);
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new Configuration(), new CountPages(), args);
        System.exit(exitCode);
    }

}
