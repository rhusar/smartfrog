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

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class InitializeRanksMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

    private double rank;

    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter report)
            throws IOException {
        StringTokenizer st = new StringTokenizer(value.toString());
        Text page = null;
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        Set<String> links = new HashSet<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (first) {
                page = new Text(token);
                sb.append(rank).append("\t"); // current rank
                sb.append(rank).append("\t"); // previous rank
                first = false;
            } else {
                // to remove duplicated links and self-references
                if ((links.add(token)) && (!page.toString().equals(token))) {
                    sb.append(token).append("\t");
                }
            }
        }

        output.collect(page, new Text(sb.toString()));
    }

    @Override
    public void configure(JobConf conf) {
        long count = Long.parseLong(conf.get(CiteRankTool.RANK_COUNT));
        rank = 1.0 / count;
    }

}
