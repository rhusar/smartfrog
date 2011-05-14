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
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Iterator;

public class CheckConvergenceReducer extends MapReduceBase
        implements Reducer<Text, DoubleWritable, Text, DoubleWritable> {

    @Override
    public void reduce(Text key,
                       Iterator<DoubleWritable> values,
                       OutputCollector<Text, DoubleWritable> output,
                       Reporter report) throws IOException {
        double tolerance = 0;
        while (values.hasNext()) {
            DoubleWritable value = values.next();
            tolerance += value.get();
        }
        output.collect(key, new DoubleWritable(tolerance));
    }

}