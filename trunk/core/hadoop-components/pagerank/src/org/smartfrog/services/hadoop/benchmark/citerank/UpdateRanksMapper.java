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
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class UpdateRanksMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
    public static final String PAGES = "pages";
    public static final String RANK = "rank";

    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter report)
            throws IOException {
        StringTokenizer st = new StringTokenizer(value.toString());
        String page = st.nextToken();
        double rank = Double.parseDouble(st.nextToken());
        st.nextToken(); // previous rank
        if (st.hasMoreTokens()) {
            ArrayList<String> links = new ArrayList<String>();
            while (st.hasMoreTokens()) {
                links.add(st.nextToken());
            }

            StringBuffer sb = new StringBuffer().append(PAGES + "\t").append(rank).append("\t");
            for (String link : links) {
                output.collect(new Text(link), new Text(RANK + "\t" + (rank / links.size())));
                sb.append(link).append("\t");
            }
            output.collect(new Text(page), new Text(sb.toString()));
        } else {
            output.collect(new Text(page), new Text(PAGES + "\t" + rank));
        }
    }

}
