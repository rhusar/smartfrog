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

import au.com.bytecode.opencsv.CSVReader;
import org.smartfrog.services.filesystem.FileImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Created 20-Feb-2008 14:55:17
 */

public class CSVFileReadImpl extends FileImpl implements CSVFileRead {
    public static final String ERROR_UNBOUND = "Not bound to any file";
    public static final String ERROR_NO_FILE = "There is no file ";
    private CSVReader reader;
    private char separator;
    private char quote;
    private int headerLines;
    private int count = 0;
    public static final String ERROR_STRING_TOO_LONG = "String too long";
    public static final String ERROR_STRING_TOO_SHORT = "String too short";
    private static final Reference SEPARATOR = new Reference(ATTR_SEPARATOR);
    private static final Reference QUOTE = new Reference(ATTR_QUOTE_CHAR);
    public static final String ERROR_CSV_READER_IS_NOT_OPEN = "CSV reader is not open";
    private int maxCount;
    private int minCount;
    private int minWidth;
    private int maxWidth;
    public static final String ERROR_LINE_WIDTH_WRONG = "Line width is out of the range [";
    public static final String ERROR_TOO_MANY_LINES = "Too many lines, stopped at line ";
    public static final String ERROR_TOO_FEW_LINES = "Too few lines -expected ";


    public CSVFileReadImpl() throws RemoteException {
    }


    /**
     * Override point: base implementation is empty. This method is called in sfStart() after the file state tests are
     * successful, and before we look for propertys that request termination of the the component
     *
     * @throws SmartFrogException SF problems
     * @throws RemoteException    network problems.
     */
    @Override
    protected void onComponentStarted() throws SmartFrogException, RemoteException {
        separator = resolveSingleChar(SEPARATOR);
        quote = resolveSingleChar(QUOTE);
        headerLines = sfResolve(ATTR_HEADER_LINES, 0, true);
        minCount = sfResolve(ATTR_MINCOUNT, 0, true);
        maxCount = sfResolve(ATTR_MAXCOUNT, 0, true);
        minWidth = sfResolve(ATTR_MINWIDTH, 0, true);
        maxWidth = sfResolve(ATTR_MAXWIDTH, 0, true);
        start();
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        closeQuietly();
    }

    /**
     * Close without leaving any error messages
     */
    protected void closeQuietly() {
        try {
            close();
        } catch (RemoteException e) {
            sfLog().ignore(e);
        } catch (SmartFrogException e) {
            sfLog().ignore(e);
        }
    }

    /**
     * resolve a single mandatory char
     *
     * @param ref attribute to resolve
     * @return the single char read
     * @throws SmartFrogResolutionException resolution failure
     * @throws RemoteException              network problems
     */
    char resolveSingleChar(Reference ref) throws SmartFrogResolutionException, RemoteException {
        String s = sfResolve(ref, "", true);
        if (s.length() == 0) {
            throw new SmartFrogResolutionException(ref, sfCompleteName(), ERROR_STRING_TOO_SHORT);
        }
        if (s.length() > 1) {
            throw new SmartFrogResolutionException(ref, sfCompleteName(), ERROR_STRING_TOO_LONG);
        }
        return s.charAt(0);
    }


    /**
     * Get the next line
     *
     * @return the next line, all broken up, or null for no new lines.
     * @throws RemoteException              network problems
     * @throws SmartFrogDeploymentException parsing/file IO problems, or wrong dimensions of the array
     */
    public synchronized String[] getNextTuple() throws RemoteException, SmartFrogException {
        if (reader == null) {
            throw new SmartFrogLifecycleException(ERROR_CSV_READER_IS_NOT_OPEN);
        }
        try {
            String[] result = reader.readNext();
            if (result == null) {
                if (count < minCount) {
                    throw new SmartFrogDeploymentException(ERROR_TOO_FEW_LINES + minCount + " but got " + count,
                            this);
                }

            } else {
                count++;
                if (maxCount >= 0 && count > maxCount) {
                    throw new SmartFrogDeploymentException(ERROR_TOO_MANY_LINES + count + ":\n"
                            + merge(result),
                            this);
                }
                int width = result.length;
                if (width < minWidth || (maxWidth >= 0 && width > maxWidth)) {
                    throw new SmartFrogDeploymentException(
                            ERROR_LINE_WIDTH_WRONG + minWidth + ',' + maxWidth + "]: " + width
                                    + '\n' + merge(result),
                            this);
                }
            }
            return result;
        } catch (IOException e) {
            throw new SmartFrogDeploymentException("Reading from " + getFile(), e, this);
        }
    }

    /**
     * Close the reader. harmless if we are already closed
     *
     * @throws RemoteException    network problems
     * @throws SmartFrogException parsing/file IO problems
     */
    public synchronized void close() throws RemoteException, SmartFrogException {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                throw new SmartFrogException("Closing " + getFile(), e);
            } finally {
                reader = null;
            }
        }
    }

    /**
     * Go back to the start of the file
     *
     * @throws RemoteException    network problems
     * @throws SmartFrogException parsing/file IO problems
     */
    public synchronized void start() throws RemoteException, SmartFrogException {

        File csvfile = getFile();
        if (csvfile == null) {
            throw new SmartFrogDeploymentException(ERROR_UNBOUND);
        }
        try {
            sfLog().info("Reading CSV file " + csvfile);
            reader = new CSVReader(new FileReader(csvfile), separator, quote, headerLines);
            count = 0;
        } catch (FileNotFoundException ignored) {
            throw new SmartFrogDeploymentException(ERROR_NO_FILE + csvfile.getAbsolutePath());
        }

    }

    /**
     * Merge a string array to a CSV text line
     *
     * @param line the line to merge
     * @return the line back in CSV form
     */
    public static String merge(String[] line) {
        StringBuilder b = new StringBuilder();
        boolean first = true;
        for (String entry : line) {
            b.append(first ? "\"" : ", \"");
            first = false;
            b.append(entry);
            b.append('\"');
        }
        return b.toString();
    }
}
