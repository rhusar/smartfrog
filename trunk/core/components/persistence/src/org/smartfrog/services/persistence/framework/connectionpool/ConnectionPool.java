/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.persistence.framework.connectionpool;

import java.sql.Connection;
import java.sql.SQLException;

import org.smartfrog.services.persistence.database.SQLDialect;
import org.smartfrog.services.persistence.database.Statements;

/**
 * This interface provides access to an SQL connection pool. 
 * 
 * @see java.sql.Connection
 */
public interface ConnectionPool {
    /**
     * Get a database connection from the pool. 
     * 
     * @return Connection - a connection to the database 
     * @throws SQLException TODO
     */
    public Connection getConnection() throws SQLException;
    
    /**
     * Initialise the connection pool. Completion of this
     * method does not imply that the database can actually 
     * be accessed - only that the pool has been prepared.
     */
    public void createConnectionPool();
    
    /**
     * Close the connection pool. Any open connections will be terminated
     * with incomplete transactions aborted.
     */
    public void closeConnectionPool();
    
    /**
     * Get the vendor sql dialect. JDBC passes through
     * vendor specific error codes and status strings. 
     * This class can be used to understand these for a 
     * specific database.
     * 
     * @return dialect 
     */
    public SQLDialect getDialect();
    
    /**
     * Get the vendor specific SQL statements. Vendors implement
     * slightly different variations of SQL. This class contains
     * statements that have been tailored to the dialect of
     * the specific database.
     * 
     * @return statements
     */
    public Statements getStatements();
}
