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

package org.smartfrog.services.persistence.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Different database vendors implement slight variations on the SQL language and error codes.
 * These differences are most notable in the names of data types used in data definition. The
 * Many vendors also implement specific SQL queries or non-SQL expressions that can be used to 
 * "ping" the database through their JDBC interface.
 * This class holds the mappings from internal common data types to the vendor specific key words,
 * the ping queries, and mappings from internal representation of error conditions to the vendor specific
 * codes for those error conditions.
 */
public class SQLDialect {
	
	/**
	 * Data type keywords used by the dialect
	 */
	public enum DataType {
		INTEGER, TEXT, LARGE_BINARY, LARGE_CHARACTER
	}
	/**
	 * Map of data type keywords
	 */
	private Map<DataType, String> types = new HashMap<DataType, String>() {
		{
			put(DataType.INTEGER,         "integer");
			put(DataType.TEXT,            "varchar");
			put(DataType.LARGE_BINARY,    "long varbinary");
			put(DataType.LARGE_CHARACTER, "long varchar");
		}
	};
	/**
	 * setDataType is a setter method for the data types used by the dialect. 
	 * This method should be used to declare the key words that correspond to the
	 * data types in the enum DataType. These declarations would normally 
	 * be stated in the default constructor for the class.
	 * 
	 * @param type a DataType 
	 * @param literal the keyword that corresponds to the type
	 */
	protected void setDataType(DataType type, String literal) {
		types.put(type, literal);
	}
	/**
	 * This method is used to obtain the keyword string 
	 * corresponding to the given data type.
	 * 
	 * @param type the data type
	 * @return the kwyword string corresponding to the data type
	 */
	public String getType(DataType type) {
		return types.get(type);
	}
	
	/**
	 * Validation query - the default value is "select 1"
	 */
	private String validationQuery = "select 1";
	/**
	 * Validation query result as a string. The 
	 * result string is the first column of the first row. The dfault 
	 * value is "1"
	 */
	private String validationResult = "1";
	/**
	 * Setter method for the validation query. This should be used to set
	 * the validation query in a constructor or initialiser for the class. The 
	 * result string is the first column of the first row. 
	 * 
	 * The default query is "select 1"
	 * The default result is "1"
	 * 
	 * @param query the query - can be null
	 * @param result the result - assumed to be a single string
	 */
	protected void setValidationQuery(String query, String result) {
		validationQuery = query;
		validationResult = result;
	}
	/**
	 * Getter method for the validation query
	 * @return the query - can be null
	 */
	public String getValidationQuery() { return validationQuery; }
	/**
	 * Getter method for the validation result.
	 * @return the result string
	 */
	public String getValidationResult() { return validationResult; }
	
	/**
	 * error types used by the dialect. this is not a complete
	 * list of error codes for the vendor's database server, but
	 * a list of these that are interpreted by the persistence framework.
	 * Each error type corresponds to one or more error codes for the dialect.
	 */
	public enum ErrorType {
		BROKEN_CONNECTION, READ_ONLY, NO_SUCH_TABLE, TABLE_ALREADY_EXISTS 
	}
	/**
	 * A map of ErrorType to error codes. Each error type has a set of corresponding
	 * error codes.
	 */
	private Map<ErrorType, Set<Integer>> errors = new HashMap<ErrorType, Set<Integer>>() {
		{
			for( ErrorType e : ErrorType.values() ) {
				put(e, new HashSet<Integer>());
			}
		}
	};
	/**
	 * Setter method for the error codes used by the dialect. The method should
	 * be used in a constructor or initialiser to declare the error codes that correspond 
	 * to the error types in ErrorType. The error code is a value that may be returned
	 * by java.sql.SQLException.getErrorCode() when the vendors JDBC driver throws an
	 * exception.
	 * 
	 * @param error - the error type
	 * @param code - an error code that corresponds to the error type
	 */
	protected void addErrorCode(ErrorType error, int code) {
		errors.get(error).add(Integer.valueOf(code));
	}
	/**
	 * A method that checks if the given error code corresponds to the given error type.
	 * 
	 * @param error - the error type
	 * @param code - the error code
	 * @return true if the error code corresponds to the error type
	 */
	public boolean isErrorType(ErrorType error, int code) {
		return errors.get(error).contains(Integer.valueOf(code));
	}
	
}
