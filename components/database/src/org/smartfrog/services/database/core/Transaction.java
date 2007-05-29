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
package org.smartfrog.services.database.core;

/**
 * a list of commands
 */
public interface Transaction extends JdbcOperation {

    /**
     * a command to execute {@value}
     */
    String ATTR_COMMAND = "sql";

    /**
     * a list of commands to execute, each undelimited command in a separate
     * element {@value}
     */
    String ATTR_SQL_COMMANDS = "sqlCommands";

    /**
     * text file containing SQL commands to run {@value}
     */
    String ATTR_COMMAND_FILE = "sqlFile";

    /**
     * name of text resource containing SQL commands to run {@value}
     */
    String ATTR_COMMAND_RESOURCE = "sqlResource";

    /**
     * delimiter char between SQL statements, usually ; {@value}
     */
    String ATTR_DELIMITER = "delimiter";

    /**
     * should SQL commands be escaped before execution? {@value}
     */
    String ATTR_ESCAPE = "escapeProcessing";

    /**
     * this is primarily for internal testing, but can be used for debugging
     * statement parsing problems. It is the count of statements expected after
     * the string/file/resource is parsed. If there is a mismatch, an exception
     * is thrown. {@value}
     */
    String ATTR_EXPECTED_STATEMENT_COUNT = "expectedStatementCount";

    /**
     * failOnSqlError extends boolean { description "should we fail on an SQL
     * error?"; {@value}
     */
    String ATTR_FAIL_ON_SQL_ERROR = "failOnSqlError";

    /**
     * should the column headers be printed?" {@value}
     */
    String ATTR_PRINTHEADERS = "printHeaders";

    /**
     * should the results be printed? {@value}
     */
    String ATTR_PRINTRESULTS = "printResults";
}
