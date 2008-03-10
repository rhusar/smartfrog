/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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

/**
 * Some parts of this class are derived from the SqlExec task of Apache Ant 1.7
 */
/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.smartfrog.services.database.core;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.reference.Reference;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Implement a SQL transaction. This class was written while looking at Ant's
 * SQLExec, though there wasnt any direct cut and paste. Its a lot simpler, as
 * it offers less options for SQL processing, and corrects some ambiguities in
 * Ant's task created 20-Sep-2006 17:25:59
 */

public class TransactionImpl extends AsyncJdbcOperation implements Transaction {
    public static final String ERROR_NO_COMMANDS = "No commands declared";
    public static final String ERROR_TOO_MANY_COMMANDS = "Too many command attributes";
    private List<String> commands=new ArrayList<String>();
    private boolean escapeProcessing = false;
    private String delimiter;
    private int expectedStatementCount = -1;
    private boolean failOnSqlError;
    protected static final String ENCODING = "UTF-8";
    protected static final Charset UTF8 = Charset.forName(ENCODING);
    private boolean printHeaders;
    private boolean printResults;


    public TransactionImpl() throws RemoteException {
    }


    /**
     * Get the command list
     * @return the list of commands (may be empty) 
     */
    public List<String> getCommands() {
        return commands;
    }


    /**
     * The startup operation is to read the commands in then execute them by way of {@link #executeStartupCommands()}.
     * Subclasses may change this behaviour
     * @throws SmartFrogException for smartfrog problems
     * @throws RemoteException for network problems.
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();

        delimiter = sfResolve(ATTR_DELIMITER, delimiter, true);
        escapeProcessing = sfResolve(ATTR_ESCAPE, escapeProcessing, true);
        expectedStatementCount = sfResolve(ATTR_EXPECTED_STATEMENT_COUNT,
                expectedStatementCount, false);
        failOnSqlError = sfResolve(ATTR_FAIL_ON_SQL_ERROR,
                failOnSqlError,
                true);
        printResults = sfResolve(ATTR_PRINTRESULTS, printResults, true);
        printHeaders = sfResolve(ATTR_PRINTHEADERS, printHeaders, true);
        readCommands();
        executeStartupCommands();
    }


    /**
     * stop the worker thread if it is running.
     *
     * @param status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        checkAndRunTerminationCommands();
    }

    /**
     * Check for and run the termination commands. Any exceptions raised by {@link #runTerminationCommands()} are logged
     * at the warn level but otherwise ignored
     */
    protected void checkAndRunTerminationCommands() {
        Throwable caught = null;
        if (hasTerminationCommands()) {
            try {
                runTerminationCommands();
            } catch (SQLException e) {
                caught = e;
            } catch (SmartFrogException e) {
                caught = e;
            } catch (RemoteException e) {
                caught = e;
            }
            if (caught != null) {
                sfLog().warn("Caught while terminating the component", caught);
            }
        }
    }

    /**
     * Override point: termination commands.
     * All exceptions should be caught and printed here.
     * The default implementation does nothing.
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    network problems
     * @throws SQLException       SQL problems
     */
    protected void runTerminationCommands() throws SmartFrogException, RemoteException, SQLException {

    }

    /**
     * Override point: Return true if the component has termination time SQL commands to run
     *
     * @return false
     */
    protected boolean hasTerminationCommands() {
        return false;
    }
    /**
     * Execute any commands to run during {@link #sfStart()}.
     * This delegates to {@link #startCommandThread()}
     * @throws SmartFrogDeploymentException for smartfrog problems
     * @throws SmartFrogResolutionException for smartfrog problems
     * @throws RemoteException for network problems.
     */
    protected void executeStartupCommands()
            throws SmartFrogDeploymentException, SmartFrogResolutionException, RemoteException {
        startCommandThread();
    }

    /**
     * Execute any commands in the {@link #commands} list by starting a separate thread
     * @throws SmartFrogDeploymentException for smartfrog problems
     * @throws SmartFrogResolutionException for smartfrog problems
     * @throws RemoteException for network problems.
     */
    protected void startCommandThread()
            throws SmartFrogDeploymentException, SmartFrogResolutionException, RemoteException {
        if (commands.size() > 0) {
            //work to do. check the connection (synchronously), then start the worker
            checkConnection();
            startWorkerThread();
        } else {
            //no work to do.
            getLog().debug("No SQL statements to execute; skipping");
        }
    }

    /**
     * Read in the commands from file, a resource, the {@link #ATTR_COMMAND}
     * string or {@link #ATTR_SQL_COMMANDS} list.
     * @throws SmartFrogException for smartfrog problems
     * @throws RemoteException for network problems.
     */
    protected void readCommands() throws RemoteException, SmartFrogException {
        int count = 0;
        String command = sfResolve(ATTR_COMMAND, (String) null, false);
        if (command != null) {
            count++;
        }
        File commandFile = FileSystem.lookupAbsoluteFile(this,
                ATTR_COMMAND_FILE,
                null,
                null,
                false,
                null);
        if (commandFile != null) {
            count++;
        }
        String resource = sfResolve(ATTR_COMMAND_RESOURCE,
                (String) null,
                false);
        if (resource != null) {
            count++;
        }
        Vector<String> commandList = null;
        commandList= ListUtils.resolveStringList(this,new Reference(ATTR_SQL_COMMANDS),false);
        if (commandList != null) {
            count++;
        }
        if (count == 0) {
            throw new SmartFrogDeploymentException(ERROR_NO_COMMANDS);
        }
        if (count > 1) {
            throw new SmartFrogDeploymentException(ERROR_TOO_MANY_COMMANDS);
        }

        //everything is validated. read in the commands from file, resource or string
        if (commandFile != null) {
            try {
                StringBuffer buffer;
                buffer = FileSystem.readFile(commandFile, UTF8);
                command = buffer.toString();
            } catch (IOException e) {
                throw new SmartFrogDeploymentException("Failed to read " + commandFile,
                        e);
            }
        }
        if (resource != null) {
            command = getHelper().loadResourceToString(resource, UTF8);
        }

        //at this point, command contains the entire value of the command attribute, resource or file.
        //we now crack it into a List of commands

        if (command != null) {
            try {
                commands = crackCommands(command, delimiter);
            } catch (IOException e) {
                throw new SmartFrogDeploymentException("when parsing\n " + command,
                        e);
            }
        } else {
            commands=commandList;
            if (commandList == null) {
                command = "";
            }
        }

        int size = commands.size();
        if (expectedStatementCount >= 0 && expectedStatementCount != size) {
            throw new SmartFrogDeploymentException("Expected " + expectedStatementCount
                    + " statements, but after parsing, there were " + size
                    + " from \n" + command);
        }
    }

    /**
     * this runs in the other tread. Run the commands one by one
     *
     * @param connection the open connection.
     *
     * @throws SQLException SQL execution problems
     * @throws SmartFrogException for smartfrog problems
     */
    public void performOperation(Connection connection)
            throws SQLException, SmartFrogException {
        executeCommands(connection, commands.iterator());
    }

    /**
     * Crack the command string into a list of commands (which may be zero), at
     * every line-ending delimiter. delimiters not at tne d
     *
     * @param sql SQL command
     * @param commandDelimiter the delimiter for commands.
     *
     * @return a (possibly empty) list of commands.
     *
     * @throws IOException if the string cannot be read reliably
     */
    public List<String> crackCommands(String sql, String commandDelimiter)
            throws IOException {
        List<String> list = new ArrayList<String>();
        int delimiterSize = commandDelimiter.length();
        String line;
        StringBuilder buffer = new StringBuilder();
        BufferedReader in = new BufferedReader(new StringReader(sql));

        while ((line = in.readLine()) != null) {
            if (buffer.length() > 0) {
                //add a newline if the buffer is ongoing
                buffer.append("\n");
            }
            //add the line
            buffer.append(line);
            String trimmed = line.trim();
            if (trimmed.endsWith(commandDelimiter)) {
                //this is the end of the line, so we break it here
                String sqlcommand = buffer.substring(0,
                        buffer.length() - delimiterSize);
                //don't add empty commands, but dont strip off trailing whitespace for the sake
                //of comments.
                if (sqlcommand.trim().length() > 0) {
                    list.add(sqlcommand);
                }
                //reset the buffer
                buffer.replace(0, buffer.length(), "");
            }
        }
        //end of the line. If there isnt a trailing semicolon, it is still a command
        String tail = buffer.toString();
        if (tail.trim().length() > 0) {
            list.add(tail);
        }
        return list;
    }

    /**
     * Execute a sequence of commands. It takes an iterator so anything can act
     * as a source of commands; there isnt even any need for the type to be
     * string, as long as the toString() method of each iterated value returns a
     * single SQL command (without delimeter)
     *
     * @param connection  connection to use
     * @param commandIterator iterator over the commands to run
     *
     * @throws SmartFrogDeploymentException
     */
    public void executeCommands(Connection connection,
                                Iterator<String> commandIterator)
            throws SmartFrogDeploymentException {
        while (commandIterator.hasNext()) {
            String command = commandIterator.next();
            executeOneCommand(connection, command);
        }
    }

    /**
     * Directly derived from apache code, has apache copyright until rewritten
     * better
     *
     * @param connection connection to use
     * @param command    comand to issue
     *
     * @throws SmartFrogDeploymentException
     */
    public void executeOneCommand(Connection connection, String command)
            throws SmartFrogDeploymentException {
        ResultSet resultSet = null;

        try {
            Statement statement = connection.createStatement();
            if(sfLog().isInfoEnabled()) {
                sfLog().info(command);
            }

            statement.setEscapeProcessing(escapeProcessing);
            boolean hasResultSet;
            int updateCount;
            int updateCountTotal = 0;
            int warningCount=0;

            hasResultSet = statement.execute(command);
            updateCount = statement.getUpdateCount();
            resultSet = statement.getResultSet();
            do {
                if (!hasResultSet) {
                    if (updateCount != -1) {
                        updateCountTotal += updateCount;
                    }
                    processNoResults(statement);
                } else {
                    warningCount += logWarnings(resultSet.getWarnings());
                    processResults(statement, resultSet);
                }
                hasResultSet = statement.getMoreResults();
                if (hasResultSet) {
                    //close the previous result set
                    closeResultSetQuietly(resultSet);
                    resultSet=null;
                    //get the next data
                    updateCount = statement.getUpdateCount();
                    resultSet = statement.getResultSet();
                }
            } while (hasResultSet);
            if (updateCountTotal > 0) {
                getLog().info(updateCountTotal + " rows affected");
            }

            SQLWarning warning = connection.getWarnings();
            warningCount += logWarnings(warning);
            connection.clearWarnings();
        } catch (SQLException e) {
            getLog().error("Failed to execute: " + command, e);
            if (failOnSqlError) {
                throw translate("When executing " + command, e);
            }
        } finally {
            closeResultSetQuietly(resultSet);
        }
    }

    /**
     * Close a result set 'quietly' if is not null
     * @param resultSet result set; can be null
     */
    protected void closeResultSetQuietly(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                getLog().error("when closing the result set", e);
            }
        }
    }

    /**
     * Override point: action on no results
     * @param statement the statement to process
     * @throws SQLException if needed
     */
    protected void processNoResults(Statement statement) throws SQLException {
        sfLog().info("--no results--");
    }

    /**
     * Override point, act on the results
     *
     * @param statement the statement to process
     * @param results the results of the operation
     *
     * @throws SQLException if needed
     */
    protected void processResults(Statement statement,ResultSet results)
            throws SQLException {
        if (printResults) {
            printResults(results);
        }
    }


    /**
     * Print a result set to the log at info level.
     *
     * @param results to process
     *
     * @throws SQLException if something failed
     */
    protected void printResults(ResultSet results) throws SQLException {
        ResultSetMetaData md = results.getMetaData();
        int columnCount = md.getColumnCount();
        StringBuilder line = new StringBuilder();
        if (printHeaders) {
            for (int col = 1; col < columnCount; col++) {
                line.append(md.getColumnName(col));
                line.append(",");
            }
            line.append(md.getColumnName(columnCount));
            getLog().info(line);
            line = new StringBuilder();
        }
        while (results.next()) {
            boolean first = true;
            for (int col = 1; col <= columnCount; col++) {
                String columnValue = results.getString(col);
                if (columnValue != null) {
                    columnValue = columnValue.trim();
                }

                if (first) {
                    first = false;
                } else {
                    line.append(",");
                }
                line.append(columnValue);
            }
            getLog().info(line);
            line = new StringBuilder();
        }
    }
}
