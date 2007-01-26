/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.test;

import java.io.PrintStream;

/**
 * A class to redirect std and error output to buffers.
 * Redirection begins on construction; terminates when
 * #endRedirection is called.
 *
 * The output and error streams are available for assertion checking.
 *
 * @author steve loughran
 * Date: 18-Feb-2004
 * Time: 14:48:31
 */
public class OutputLogger {

    /**
     * output stream
     */
    private TestOutputStream stdout=new TestOutputStream();

    /**
     * errors
     */
    private TestOutputStream stderr=new TestOutputStream();

    /**
     *  cache of std out
     */
    private PrintStream cachedStdOut;

    /**
     * cache of std err
     */
    private PrintStream cachedStdErr;

    /**
     * constructor starts to save data the moment it is created
     */
    public OutputLogger() {
        save();
    }

    /**
     * save the streams
     */
    private void save() {
        flush();
        cachedStdOut = System.out;
        cachedStdErr = System.err;
        System.setOut(stdout.createPrintStream());
        System.setErr(stderr.createPrintStream());
    }

    /**
     * flush stdio streams
     */
    public void flush() {
        System.err.flush();
        System.out.flush();
    }

    /**
     * endRedirection the old std out streams
     */
    public void endRedirection() {
        flush();
        System.setOut(cachedStdOut);
        System.setErr(cachedStdErr);
        cachedStdOut=null;
        cachedStdErr=null;
    }

    /**
     * test for the output stream containing a string
     * @param substring string to search for
     * @return true if the substring is in the cached stream
     */
    public boolean stdoutContains(String substring) {
        return stdout.contains(substring);
    }

    /**
     * test for the error stream containing a string
     * @param substring string to search for
     * @return true if the substring is in the cached stream
     */
    public boolean stderrContains(String substring) {
        return stderr.contains(substring);
    }


}
