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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * component to test the file read operations; lets you validate headers and such like Created 20-Feb-2008 15:50:56
 */

public class CSVFileReadTester extends AbstractCSVProcessor implements Remote {

    private int minCount, maxCount;
    private Vector<Vector<String>> lines;


    /**
     * {@value} : array of lines to check
     */
    public static final String ATTR_LINES = "lines";

    /**
     * min number of lines {@value}
     */
    public static final String ATTR_MINCOUNT = "minCount";
    /**
     * max number of lines {@value}
     */
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
        CSVFileRead source = (CSVFileRead) src;
        lines = (Vector<Vector<String>>) sfResolve(ATTR_LINES, lines, true);
        minCount = sfResolve(ATTR_MINCOUNT, 0, true);
        maxCount = sfResolve(ATTR_MAXCOUNT, 0, true);
        setReader(new ReaderThread(source));
        getReader().start();
    }



    private class ReaderThread extends CSVReaderThread {
 
        /**
         * Create a reader
         * @param source CSV source
         * @see Thread#Thread(ThreadGroup,Runnable,String)
         */
        private ReaderThread(CSVFileRead source) {
            super(CSVFileReadTester.this, source);
        }

        /**
         * read the thread in, validate the values, then maybe terminate the component
         *
         * @throws Throwable if anything went wrong
         */
        public void execute() throws Throwable {
            String[] line;
            int count = 0;
            source.start();
            while ((line = source.getNextLine()) != null) {
                if (isTerminationRequested()) {
                    //bail out completely
                    return;
                }
                if (sfLog().isInfoEnabled()) {
                    sfLog().info(CSVFileReadImpl.merge(line));
                }
                if (lines.size() > count) {
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
         * compare two lines, fail if they mismatch
         *
         * @param element  element number
         * @param line     line read in
         * @param expected expected line
         * @throws SmartFrogException if there is a count mismatch, or a value is not as expected
         */
        private void compareLine(int element, String[] line, Vector<String> expected) throws SmartFrogException {
            String merged = CSVFileReadImpl.merge(line);
            int size = expected.size();
            int actual = line.length;
            if (actual != size) {
                throw new SmartFrogException("Line " + element + " is wrong width; expected " + size + " but got "
                        + actual + " elements\n" + merged);
            }
            for (int i = 0; i < size; i++) {
                String expectedElt = expected.elementAt(i);
                String actualElt = line[i];
                if (!expectedElt.equals(actualElt)) {
                    throw new SmartFrogException(
                            "Line " + element + " does not match expected element " + i
                                    + " expected=\"" + expectedElt + '\"'
                                    + " actual=\"" + actualElt + '\"'
                                    +":\n" + merged);
                }
            }

        }

    }

}
