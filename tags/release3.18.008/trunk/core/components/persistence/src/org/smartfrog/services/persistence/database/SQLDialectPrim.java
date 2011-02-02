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

import java.rmi.RemoteException;
import java.util.Iterator;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

/**
 * This is a Prim object which will populate an SQLDialect object with 
 * vendor specific dialect provided through a smartfrog description.
 */
public class SQLDialectPrim extends PrimImpl implements Prim {
	
	private static final Reference DATATYPES_REF = new Reference("dataTypes");
	private static final Reference ERRORCODES_REF = new Reference("errorCodes");
	private static final Reference VALIDATION_REF = new Reference("connectionValidation");
	private static final Reference QUERY_REF = new Reference("query");
	private static final Reference RESULT_REF = new Reference("result");
	
	private SQLDialect dialect = null;
	
	/**
	 * return the dialect object - this object is populated with the
	 * vendor specific dialect the first time this method is called.
	 * 
	 * @return the dialect object
	 * @throws RemoteException 
	 * @throws SmartFrogResolutionException 
	 */
	public synchronized SQLDialect resolveDialect() throws SmartFrogResolutionException, RemoteException {
		if( dialect == null ) {
			dialect = setDialect();
		}
		return dialect;
	}

	public SQLDialectPrim() throws RemoteException {
	}

	private SQLDialect setDialect() throws SmartFrogResolutionException, RemoteException {
		
		SQLDialect dialect = new SQLDialect();
		
		ComponentDescription dataTypes = sfResolve(DATATYPES_REF, (ComponentDescription)null, true);
		ComponentDescription errorCodes = sfResolve(ERRORCODES_REF, (ComponentDescription)null, true);
		ComponentDescription validation = sfResolve(VALIDATION_REF, (ComponentDescription)null, true);
		
		/**
		 * get the data types. We only pick out types we know, anything else
		 * is ignored
		 */
		for( SQLDialect.DataType type : SQLDialect.DataType.values() ) {
			if( dataTypes.sfContainsAttribute(type.toString()) ) {
				dialect.setDataType(type, dataTypes.sfResolve(type.toString(), "", true));
			}
		}
		
		/**
		 * get the error codes
		 */
		for( SQLDialect.ErrorType type : SQLDialect.ErrorType.values() ) {
			if( errorCodes.sfContainsAttribute(type.toString()) ) {
				ComponentDescription codes = errorCodes.sfResolve(type.toString(), (ComponentDescription)null, true);
				Iterator iter = codes.sfAttributes();
				while( iter.hasNext() ) {
					Object name = iter.next();
					Object value = codes.sfResolveHere(name);
					if( value instanceof Integer ) {
						dialect.addErrorCode(type, ((Integer) value).intValue());
					} else {
						throw new SmartFrogResolutionException("Non-integer error code of type " + type + ", with name " + name, this);
					}
				}
			}
		}
		
		/**
		 * get the validation query and response
		 */
		String query = validation.sfResolve(QUERY_REF, "", true);
		String result = validation.sfResolve(RESULT_REF, "", true);
		dialect.setValidationQuery(query, result);
		
		/**
		 * return the result
		 */
		return dialect;
	}

}
