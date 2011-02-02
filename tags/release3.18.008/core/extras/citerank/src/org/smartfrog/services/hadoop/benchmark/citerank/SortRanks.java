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
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.ToolRunner;

public class SortRanks extends CiteRankTool {

    @Override
    protected String getName() {
        return "SortRanks";
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            return usage(IN_AND_OUT);
        }

        JobConf conf = createInputOutputConfiguration(args);
        conf.setMapperClass(SortRanksMapper.class);
        conf.setReducerClass(SortRanksReducer.class);
        conf.setOutputKeyComparatorClass(DoubleWritableDecreasingComparator.class);
        conf.setMapOutputKeyClass(DoubleWritable.class);
        conf.setMapOutputValueClass(Text.class);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        conf.setNumReduceTasks(1); // inefficient, use InputSampler with v0.20.x
        return runJob(conf);
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new Configuration(), new SortRanks(), args);
        System.exit(exitCode);
    }

}

class DoubleWritableDecreasingComparator extends DoubleWritable.Comparator {

    @Override
    public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
        return -super.compare(b1, s1, l1, b2, s2, l2);
    }

}
