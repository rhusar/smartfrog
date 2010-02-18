/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.avalanche.client.persist;

import com.sleepycat.je.DatabaseException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.File;
import java.rmi.RemoteException;

/**
 * Smartfrog component provides persistance service to other smartfrog components. 
 * Components can store any object in the persistance service and then retrive it at a later point
 * the persisted data exist even if smartfrog daemon is restarted.  
 * 
 * @author sanjaydahiya
 *
 */
public class SFPersistanceService extends PrimImpl implements Prim {
	private String avalancheHome ; 
	private BDBHelper helper ; 
	public SFPersistanceService() throws RemoteException {
		super();
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		avalancheHome = System.getProperty("AVALANCHE_HOME") ;
		
		String ahome = (String)sfResolve("avalancheHome"); 
		if( null != ahome ){
			avalancheHome = ahome ; 
		}
		
		try{
			helper.init(avalancheHome + File.separator +  "data") ;
		}catch(DatabaseException e){
			throw new SmartFrogException(e);
		}
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
	}

	public synchronized void sfTerminateWith(TerminationRecord arg0) {
		try{
			helper.close();
		}catch(DatabaseException e){
			sfLog().error(e);
		}

		super.sfTerminateWith(arg0);
	}
	
	public void put(String key, String value) throws SmartFrogException{
		try{
			helper.put(key, value);
		}catch(DatabaseException e){
			throw new SmartFrogException(e);
		}
	}
	public Object get(String key, Class cls) throws SmartFrogException{
		Object value = null ; 
		try{
			value = helper.get(key, cls); 
		}catch( DatabaseException e){
			throw new SmartFrogException(e);
		}
		return value ; 
	}
	
	public void delete(String key) throws SmartFrogException{
		try{
			helper.delete(key);
		}catch(DatabaseException e){
			throw new SmartFrogException(e);
		}
	}
}
