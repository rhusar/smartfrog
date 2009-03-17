/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.junitmr;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

import junit.framework.TestResult;
import junit.framework.Test;

/**
 * Created 17-Mar-2009 13:18:16
 */

public class JunitMR {


    public static class JunitMap extends
            Mapper<Text, Text, Text, SingleTestRun> {

        /**
         * Called once for each key/value pair in the input split. Most applications should override this, but the default
         * is the identity function.
         */
        @Override
        protected void map(Text key, Text value,
                           Context context)
                throws IOException, InterruptedException {
            super.map(key, value, context);
            String test = value.toString();
            TestResult result = new TestResult();
            try {
                Class testClass = JUnitMRUtils.loadTestClass(test);
                Test testSuite = JUnitMRUtils.extractTest(testClass);
                SingleTestRun run = new SingleTestRun();
                result.addListener(run);
                testSuite.run(result);
                context.write(key, run);
            } catch (ClassNotFoundException e) {
                throw new IOException("Could not load "+test,e);
            }

        }
        
    }

    public static class JUnitReducer
            extends Reducer<Text, SingleTestRun, Text, TestSummary> {

        public void reduce(Text key, Iterable<SingleTestRun> values,
                           Context context)
                throws IOException, InterruptedException {
            TestSummary summary = new TestSummary();
            summary.name = key.toString();
            for (SingleTestRun outcome : values) {
                summary.attempts++;
                if (outcome.succeeded) {
                    summary.successes++;
                } else {
                    summary.failures++;
                }
                if (outcome.skipped) {
                    summary.skips++;
                }
                if (!outcome.text.isEmpty()) {
                    //do something with text here
                }
            }
            //push out the summary
            context.write(key, summary);
        }
    }

}
