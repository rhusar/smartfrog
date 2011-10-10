/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xunit.listeners;

import org.smartfrog.services.xunit.base.TestListener;
import org.smartfrog.services.xunit.base.TestListenerFactory;
import org.smartfrog.services.xunit.base.TestSuite;
import org.smartfrog.services.xunit.serial.LogEntry;
import org.smartfrog.services.xunit.serial.TestInfo;
import org.smartfrog.services.xunit.serial.ThrowableTraceInfo;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 * Routes log messages to the sfLog of the log listener instance, converting received stack traces into logged stacks
 */

@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
public class LogListenerImpl extends PrimImpl
        implements TestListenerFactory {

    /**
     * create an instance of the console listener
     * @throws RemoteException network problems
     */
    public LogListenerImpl() throws RemoteException {
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    @Override
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
    }


    /**
     * Print a line
     * @param line line to print
     */
    public void log(String line) {
        sfLog().info(line);
    }

    /**
     * Log trouble
     * @param message mesage
     * @param test test info
     */
    private void logTrouble(String message, TestInfo test) {
        StringBuilder buffer = new StringBuilder(128);
        buffer.append(message);
        buffer.append(test.getName());
        buffer.append(" on ");
        buffer.append(test.getHostname());
        buffer.append('\n');
        buffer.append(test.getText());
        log(buffer.toString());
        log(test.getFault());
    }

    /**
     * Log an exception, recursively
     * @param fault fault to log
     */
    private void log(ThrowableTraceInfo fault) {
        if (fault == null) {
            return;
        }
        sfLog().info(fault.toString(), fault.extractToException());
    }

    /**
     * Log an exception, recursively
     * @param fault fault to log
     */
    private void error(ThrowableTraceInfo fault) {
        if (fault == null) {
            return;
        }
        sfLog().error(fault.toString(), fault.extractToException());
    }

    /**
     * Log an exception, recursively
     * @param fault fault to log
     */
    private void warn(ThrowableTraceInfo fault) {
        if (fault == null) {
            return;
        }
        sfLog().warn(fault.toString(), fault.extractToException());
    }


    /**
     * Start listening to a test suite
     *
     * @param suite     the test suite that is about to run. May be null,
     *                  especially during testing.
     * @param hostname  name of host
     * @param processname name of the process
     * @param suitename name of test suite
     * @param timestamp start timestamp (UTC)
     * @return a listener to talk to
     * @throws RemoteException network problems
     * @throws SmartFrogException code problems
     */
    @Override
    public TestListener listen(TestSuite suite, String hostname,
                               String processname, String suitename,
                               long timestamp) throws RemoteException,
            SmartFrogException {
        LogTestListener listener = new LogTestListener(hostname, processname, suitename, timestamp);
        listener.info("Created " + listener);
        return listener;
    }

    /**
     * this class exists to forward tests to the console
     */
    protected class LogTestListener implements TestListener {

        String hostname;
        String processname;
        String suitename;
        long timestamp;

        private LogTestListener(final String hostname,
                                    final String processname,
                                    final String suitename,
                                    final long timestamp) {
            this.hostname = hostname;
            this.processname = processname;
            this.suitename = suitename;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("LogTestListener: ");
            builder.append("hostname=").append(hostname).append("; ");
            builder.append("processname=").append(processname).append("; ");
            builder.append("suitename=").append(suitename).append("; ");
            builder.append("timestamp=").append(timestamp).append("; ");
            return builder.toString();
        }

        /**
         * end this test suite. After calling this, caller should discard all
         * references; they may no longer be valid. <i>No further methods may be
         * called</i>
         */
        @Override
        public void endSuite() throws RemoteException, SmartFrogException {
            info("Ending test suite");
        }

        @Override
        public void addError(TestInfo test) throws RemoteException {
            logTrouble("Error:", test);
        }

        @Override
        public void addFailure(TestInfo test) throws RemoteException {
            logTrouble("Failure:", test);
        }

        @Override
        public void endTest(TestInfo test) throws RemoteException {
            info("   ending " +
                    test.getName() +
                    " on " +
                    test.getHostname());

        }

        @Override
        public void startTest(TestInfo test) throws RemoteException {
            info("Starting " +
                    test.getName() +
                    " on " +
                    test.getHostname());
        }

        @Override
        public void log(LogEntry event) throws RemoteException {
            info(event.toString());
        }

        /**
         * Print a line
         * @param line line to print
         */
        public void info(String line) {
            LogListenerImpl.this.log(line);
        }

        @Override
        public String sfRemoteToString() throws RemoteException {
            return LogListenerImpl.this.sfRemoteToString();
        }
    }
}
