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

import junit.framework.Test;
import junit.framework.TestResult;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created 17-Mar-2009 13:18:16
 */

public class JunitMR {


    /**
     * This mapper executes the junit test classes listed
     */
    public static class JunitMap extends
            Mapper<Text, Text, Text, SingleTestRun> {

        /**
         * Called once for each key/value pair in the input split. Most applications should override this, but the
         * default is the identity function.
         */
        @Override
        @SuppressWarnings({"RefusedBequest"})
        protected void map(Text key, Text test, Context context)
                throws IOException, InterruptedException {
            TestResult result = new TestResult();
            Class<?> testClass = loadClass(context, test);
            Test testSuite = JUnitMRUtils.extractTest(testClass);
            //set the configuration if it is supported
            if (testSuite instanceof JUnitHadoopContext) {
                JUnitHadoopContext ctx = (JUnitHadoopContext) testSuite;
                ctx.setConfiguration(context.getConfiguration());
            }
            TestSuiteRun tsr = new TestSuiteRun();
            result.addListener(tsr);
            testSuite.run(result);
            for (SingleTestRun singleTestRun : tsr.getTests()) {
                context.write(new Text(singleTestRun.name), singleTestRun);
            }
        }

      private Class<?> loadClass(Context context, Text test)
              throws IOException {
        return loadClass(context, test.toString());
      }

      private Class<?> loadClass(Context context, String test) throws IOException {
            try {
                return context.getConfiguration().getClassLoader().loadClass(test);
            } catch (ClassNotFoundException e) {
                throw (IOException) new IOException("Could not load " + test).initCause(e);
            }
        }

protected void map2(Text key, Text test, Context context)
        throws IOException, InterruptedException {
  TestResult result = new TestResult();
  Class<?> testClass = loadClass(context, test);
  Test testSuite = JUnitMRUtils.extractTest(testClass);
  TestSuiteRun tsr = new TestSuiteRun();
  result.addListener(tsr);
  testSuite.run(result);
  for (SingleTestRun singleTestRun : tsr.getTests()) {
    context.write(new Text(singleTestRun.name),
            singleTestRun);
  }
}

    }

    /**
     * This reducer takes the test results and summmarises them for each test method
     */
    public static class JUnitReducer
            extends Reducer<Text, SingleTestRun, Text, TestSummary> {

        @SuppressWarnings({"RefusedBequest"})
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
                if (outcome.text.length() > 0) {
                    //do something with text here
                }
            }
            //push out the summary
            context.write(key, summary);
        }
    }

}
