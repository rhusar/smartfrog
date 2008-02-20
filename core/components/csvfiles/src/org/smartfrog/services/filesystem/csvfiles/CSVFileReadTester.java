/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.filesystem.csvfiles;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * component to test the file read operations; lets you validate headers and such like Created 20-Feb-2008 15:50:56
 */

public class CSVFileReadTester extends PrimImpl {

    private CSVFileRead source;
    private ReaderThread reader;
    private int minCount, maxCount;
    private Vector<Vector<String>> lines;

    /**
     * Source component
     */
    public static final String ATTR_SOURCE = "source";
    /**
     * {@value} : array of lines to check
     */
    public static final String ATTR_LINES = "lines";

    /**
     * exact match required
     */
    public static final String ATTR_MINCOUNT = "minCount";
    public static final String ATTR_MAXCOUNT = "maxCount";

    public CSVFileReadTester() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        Prim src = sfResolve(ATTR_SOURCE, (Prim) null, true);
        source = (CSVFileRead) src;
        lines = (Vector<Vector<String>>) sfResolve(ATTR_LINES, lines, true);
        minCount = sfResolve(ATTR_MINCOUNT, 0, true);
        maxCount = sfResolve(ATTR_MAXCOUNT, 0, true);
        reader = new ReaderThread(source);
        reader.start();
    }


    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        SmartFrogThread.requestThreadTermination(reader);

    }

    private class ReaderThread extends SmartFrogThread {
        private CSVFileRead source;

        /**
         * Create a basic thread
         *
         * @see Thread#Thread(ThreadGroup,Runnable,String)
         */
        private ReaderThread(CSVFileRead source) {
            this.source = source;
        }

        /**
         * read the thread in, validate the values, then maybe terminate the component
         *
         * @throws Throwable if anything went wrong
         */
        public void execute() throws Throwable {
            String[] line;
            int count = 0;
            while ((line = source.getNextLine()) != null) {
                if (isTerminationRequested()) {
                    //bail out completely
                    return;
                }
                if (sfLog().isInfoEnabled()) {
                    sfLog().info(merge(line));
                }
                if (lines.size() >= count) {
                    Vector<String> expected = lines.elementAt(count);
                    compareLine(count, line, expected);
                }
                count++;
                if (maxCount >= 0 && count > maxCount) {
                    throw new SmartFrogException("Too many lines", CSVFileReadTester.this);
                }
            }
            if (count < minCount) {
                throw new SmartFrogException("Too few lines -expected " + minCount + " but got " + count,
                        CSVFileReadTester.this);
            }
            //end of lines
        }

        /**
         * Runs the {@link #execute()} method, catching any exception it throws and storing it away for safe keeping
         * After the run, the notify object is notified, and we trigger a workflow termination
         */
        public void run() {
            super.run();
            TerminationRecord tr = new TerminationRecord(
                    getThrown() == null ? TerminationRecord.NORMAL : TerminationRecord.ABNORMAL,
                    "CSV file read",
                    sfCompleteNameSafe(),
                    getThrown());
            new ComponentHelper(CSVFileReadTester.this).targetForWorkflowTermination(tr);
        }

        /**
         * compare two lines, fail if they mismatch
         *
         * @param element  element number
         * @param line     line read in
         * @param expected expected line
         * @throws SmartFrogException if there is a count mismatch, or a value is not as expected
         */
        private void compareLine(int element, String[] line, Vector<String> expected) throws SmartFrogException {
            String merged = merge(line);
            int size = expected.size();
            int actual = line.length;
            if (actual != size) {
                throw new SmartFrogException("Line " + element + " is wrong width; expected " + size + " but got "
                        + actual + " elements\n" + merged);
            }
            for (int i = 0; i < size; i++) {
                String expectedElt = expected.elementAt(i);
                String actualElt = line[i];
                if (!actualElt.equals(expected)) {
                    throw new SmartFrogException(
                            "Line " + element + " does not match expected element " + i + " \"" + expectedElt + "\":"
                                    + merged);
                }
            }

        }

        /**
         * Merge a string array to a CSV text line
         *
         * @param line the line to merge
         * @return the line back in CSV form
         */
        private String merge(String[] line) {
            StringBuilder b = new StringBuilder();
            boolean first = true;
            for (String entry : line) {
                b.append(first ? "\"" : ", \"");
                first = false;
                b.append(entry);
                b.append("\"");
            }
            return b.toString();
        }
    }
}
