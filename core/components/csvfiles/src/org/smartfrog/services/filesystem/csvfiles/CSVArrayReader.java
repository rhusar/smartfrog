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

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.filesystem.TupleReaderThread;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * component to test the CSV file read operations;
 * lets you validate headers and such like Created 20-Feb-2008 15:50:56
 */

public class CSVArrayReader extends AbstractCSVProcessor implements Remote {

    /**
     * Column number
     */
    public static final String ATTR_COLUMN = "column";
    /**
     * optional target prim to set the attribute on
     */
    public static final String ATTR_TARGET = "target";
    /**
     * name of the attribute to set
     */
    public static final String ATTR_TARGET_ATTRIBUTE = "targetAttribute";

    /**
     * skip empty "" entries?
     */
    public static final String ATTR_SKIP_EMPTY_FIELDS = "skipEmptyFields";

    /**
     * skip empty "" entries?
     */
    public static final String ATTR_SKIP_NARROW_LINES = "skipNarrowLines";
    /**
     * {@value}
     */
    private static final String ATTR_TRIM_FIELDS = "trimFields";
    /**
     * the attribute we set
     */
    public static final String ATTR_RESULT = "result";
    private Prim target;
    private boolean skipEmptyFields;
    private String targetAttribute;
    private int column;
    private boolean skipNarrowLines;
    private boolean trimFields;


    public CSVArrayReader() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        CSVFileRead source = (CSVFileRead) sfResolve(ATTR_SOURCE, (Prim) null, true);
        target = sfResolve(ATTR_TARGET, (Prim) null, false);
        targetAttribute = sfResolve(ATTR_TARGET_ATTRIBUTE, "", target != null);
        trimFields = sfResolve(ATTR_TRIM_FIELDS, false, true);
        skipEmptyFields = sfResolve(ATTR_SKIP_EMPTY_FIELDS, false, true);
        skipNarrowLines = sfResolve(ATTR_SKIP_NARROW_LINES, false, true);
        column = sfResolve(ATTR_COLUMN, 0, true);
        setReader(new ReaderThread(source));
        getReader().start();
    }


    /**
     * do the work in a thread which triggers workflow events afterwards
     */
    private class ReaderThread extends TupleReaderThread {
        private Vector<String> result;

        /**
         * Create a basic thread
         *
         * @param source the data source
         */
        private ReaderThread(CSVFileRead source) {
            super(CSVArrayReader.this, source, true);

        }

        /**
         * we've started. do any preparation
         *
         * @throws SmartFrogException SmartFrog problems
         * @throws RemoteException network problems
         */
        @Override
        protected void onStarted() throws SmartFrogException, RemoteException {
            result = new Vector<String>();
        }

        /**
         * we've finished (successfully)
         *
         * @throws SmartFrogException SmartFrog problems
         * @throws RemoteException network problems
         */
        @Override
        protected void onFinished() throws SmartFrogException, RemoteException {
            //end of lines set the results
            sfReplaceAttribute(ATTR_RESULT, result);
            if (target != null) {
                target.sfReplaceAttribute(targetAttribute, result);
            }
        }

        /**
         * Process one line of the data source
         *
         * @param line line to process
         * @throws SmartFrogException SmartFrog problems
         * @throws RemoteException network problems
         */
        @Override
        protected void processOneLine(String[] line) throws SmartFrogException, RemoteException {
            int w = line.length;
            if (w < column) {
                //too narrow for this column
                if (skipNarrowLines) {
                    return;
                } else {
                    throw new SmartFrogDeploymentException("Too narrow, line #" + getCount() + ": "
                            + CSVFileReadImpl.merge(line), CSVArrayReader.this);
                }
            }
            String columnValue = line[column - 1];
            if (trimFields) {
                columnValue = columnValue.trim();
            }
            if (columnValue.length() == 0 && skipEmptyFields) {
                return;
            }
            result.add(columnValue);
        }

    }

}