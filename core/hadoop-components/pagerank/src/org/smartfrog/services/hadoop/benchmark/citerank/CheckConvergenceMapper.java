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

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.StringTokenizer;

public class CheckConvergenceMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, DoubleWritable> {

    private Text KEY_NAME = new Text("tolerance");

    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, DoubleWritable> output, Reporter report)
            throws IOException {
        StringTokenizer st = new StringTokenizer(value.toString());
        st.nextToken(); // page
        double rank = Double.parseDouble(st.nextToken());
        double previous_rank = Double.parseDouble(st.nextToken());

        output.collect(KEY_NAME, new DoubleWritable(Math.abs(rank - previous_rank)));
    }

}