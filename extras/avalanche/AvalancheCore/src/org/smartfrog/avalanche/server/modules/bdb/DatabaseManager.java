/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.modules.bdb;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

public class DatabaseManager {
	Environment env ; 
	private static Log log = LogFactory.getLog(DatabaseManager.class);
	
	DatabaseManager (String dbHome) throws DatabaseException{
		EnvironmentConfig cfg = new EnvironmentConfig();
		
		cfg.setTransactional(true);
		cfg.setAllowCreate(true);
		
		env = new Environment(new File(dbHome), cfg); 
	}
	
	public Database getDatabase(String dbName) throws DatabaseException{
		DatabaseConfig dbConfig = new DatabaseConfig();
	    dbConfig.setAllowCreate(true);
	    dbConfig.setSortedDuplicates(false);
	    dbConfig.setTransactional(true);
	    Database db = env.openDatabase(null, dbName, dbConfig);
	
	    // Open the database that you use to store your class information.
	    // The db used to store class information does not require duplicates
	    // support.
	    dbConfig.setSortedDuplicates(false);
	    return db ;
	}
	public void close(){
		try{
			env.close();
		}catch(DatabaseException e){
			log.error(e);
		}
	}
}
