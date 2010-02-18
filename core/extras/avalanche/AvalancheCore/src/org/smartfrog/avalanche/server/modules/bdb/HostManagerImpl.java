/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.modules.bdb;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.core.host.HostDocument;
import org.smartfrog.avalanche.core.host.HostType;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.DuplicateEntryException;
import org.smartfrog.avalanche.server.HostManager;
import org.smartfrog.avalanche.server.modules.bdb.bindings.HostBinding;
import org.smartfrog.avalanche.server.monitor.handlers.HostUpdateHandler;

import java.util.ArrayList;
import java.util.Iterator;

public class HostManagerImpl implements HostManager{
	private Database database ; 
    private static Log log = LogFactory.getLog(HostManagerImpl.class);
    private static HostBinding hostBinding = new HostBinding();
    private ArrayList handlers = new ArrayList(); 
    
    public HostManagerImpl(Database db){
    		this.database = db ; 
    }
    
    public HostType getHost(String hostId) throws DatabaseAccessException{
    		HostType host = null ;
    		
        	try{
	    		DatabaseEntry key = new DatabaseEntry(hostId.getBytes());
	    		DatabaseEntry value = new DatabaseEntry();
	    		
	    		database.get(null, key, value, LockMode.DEFAULT);
	    		
	    		HostDocument hdoc = (HostDocument)hostBinding.entryToObject(value);
	    		if( null != hdoc ){
	    			host = hdoc.getHost();
	    		}
        	}catch(DatabaseException e){
        		throw new DatabaseAccessException(e);
        	}
    		return host;
    }
    
    public void addHandler(HostUpdateHandler handler){
    		handlers.add(handler);
    }

    public String []listHosts() throws DatabaseAccessException{
    		String []hosts = new String[0];
    		try{
	    		ArrayList listHolder = new ArrayList();
	    		
	    		Cursor cursor = database.openCursor(null, null);
	    		DatabaseEntry key = new DatabaseEntry();
	    		DatabaseEntry value = new DatabaseEntry();
	    		for(OperationStatus stat=cursor.getFirst(key, value, LockMode.DEFAULT); 
	    			stat == OperationStatus.SUCCESS ; 
	    			stat=cursor.getNext(key, value, LockMode.DEFAULT)){
	    			
	    			listHolder.add(new String(key.getData()));
	    		}
	    		hosts = (String[]) listHolder.toArray(hosts);
	    		cursor.close(); 
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    		return hosts;
    }

    /**
     * Removes a specfic host by its HostType object
     * @param host is the HostType object of the host to be deleted
     * @throws DatabaseAccessException
     */
    public void removeHost(HostType host) throws DatabaseAccessException{
    		try{
    			database.delete(null, new DatabaseEntry(host.getId().getBytes()));
	    		Iterator itor = handlers.iterator();
	    		while(itor.hasNext()){
	    			((HostUpdateHandler)itor.next()).hostDeleted(host);
	    		}
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    }

    /**
     * It will update store the updated HostType object in the database
     * @param host is the updated HostType object
     * @throws DatabaseAccessException
     */
    public void setHost(HostType host) throws DatabaseAccessException{
    		try{
	    		DatabaseEntry key = new DatabaseEntry(host.getId().getBytes());
	    		DatabaseEntry value = new DatabaseEntry();
	    		HostDocument hdoc = HostDocument.Factory.newInstance();
	    		hdoc.setHost(host);
	    		hostBinding.objectToEntry(hdoc, value);
	    		database.put(null, key, value);
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    }

    /**
     * It will create a new HostType entry in the database
     * and will also create XMPP user accounts
     * @param hostId specifies the name of the host to be created
     * @return the HostType object of the newly created host
     * @throws DatabaseAccessException
     * @throws DuplicateEntryException
     */
    public HostType newHost(String hostId) throws DatabaseAccessException, DuplicateEntryException{
    		HostType host = null ; 
    		try{
	    		DatabaseEntry key = new DatabaseEntry(hostId.getBytes());
	    		DatabaseEntry value = new DatabaseEntry();
	    		
	    		HostDocument hdoc = HostDocument.Factory.newInstance();
	    		host = hdoc.addNewHost();
	    		host.setId(hostId);
	    		
	    		hostBinding.objectToEntry(hdoc, value);
	    		
	    		if( OperationStatus.KEYEXIST == database.putNoOverwrite(null, key, value )){
	    			throw new DuplicateEntryException("Host already exists : "+ hostId);
	    		}
	    		Iterator itor = handlers.iterator();
	    		while(itor.hasNext()){
	    			((HostUpdateHandler)itor.next()).hostAdded(host);
	    		}
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    		return host ;
    }
    
    public void close(){
    		try{
    			database.close();
    		}catch(DatabaseException e){
    			log.error(e);
    		}
    }

}
