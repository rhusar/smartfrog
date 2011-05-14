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

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

public class UpdateRanksReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

    private long count;
    private double dangling;
    private double d = 0.85;

    @Override
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter report)
            throws IOException {
        double rank = 0;
        double previous_rank = -1;
        StringBuffer links = new StringBuffer();

        while (values.hasNext()) {
            Text value = values.next();
            StringTokenizer st = new StringTokenizer(value.toString());

            if (UpdateRanksMapper.PAGES.equals(st.nextToken())) {
                previous_rank = Double.parseDouble(st.nextToken());
                while (st.hasMoreTokens()) {
                    links.append(st.nextToken()).append("\t");
                }
            } else {
                rank += Double.parseDouble(st.nextToken());
            }
        }
        rank = d * (rank) + d * dangling / count + (1 - d) / count;
        output.collect(key, new Text(rank + "\t" + previous_rank + "\t" + links.toString()));
    }

    @Override
    public void configure(JobConf conf) {
        count = Long.parseLong(conf.get(CiteRankTool.RANK_COUNT));
        dangling = Double.parseDouble(conf.get(CiteRankTool.RANK_DANGLING));
    }

}