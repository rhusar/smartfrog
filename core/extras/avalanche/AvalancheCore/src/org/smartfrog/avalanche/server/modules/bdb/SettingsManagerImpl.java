/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.modules.bdb;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.SettingsManager;
import org.smartfrog.avalanche.server.modules.bdb.bindings.SFConfigBinding;
import org.smartfrog.avalanche.server.modules.bdb.bindings.SettingsBinding;
import org.smartfrog.avalanche.settings.sfConfig.SfConfigsDocument;
import org.smartfrog.avalanche.settings.sfConfig.SfConfigsType;
import org.smartfrog.avalanche.settings.xdefault.SettingsDocument;
import org.smartfrog.avalanche.settings.xdefault.SettingsType;

public class SettingsManagerImpl implements SettingsManager{
	private Database database ; 
    private static Log log = LogFactory.getLog(SettingsManagerImpl.class);
    private static SFConfigBinding sfConfigbinding = new SFConfigBinding();
    private static SettingsBinding settingsBinding = new SettingsBinding();
        
    public static final String DEFAULTS = "defaultSettings" ;
    public static final String SFCONFIGS = "sfConfigs" ;
    
    public SettingsManagerImpl(Database db){
    		this.database = db ;
    }
    
    /**
     * Pass null for standard settings. 
     * @param id
     * @return
     * @throws DatabaseException
     */
    public SettingsType getDefaultSettings() throws DatabaseAccessException{
    		SettingsType settings = null ;
    		
    		DatabaseEntry key = new DatabaseEntry(DEFAULTS.getBytes());
    		DatabaseEntry value = new DatabaseEntry();
    		
    		try{
	    		OperationStatus status = database.get(null, key, value, LockMode.DEFAULT);
	    		if( OperationStatus.NOTFOUND == status ){
	    			SettingsDocument sdoc = SettingsDocument.Factory.newInstance();
	    			settings = sdoc.addNewSettings();
	    			settingsBinding.objectToEntry(sdoc, value);
	    			database.put(null, key, value);
	    			
	    		}else{
		    		SettingsDocument sdoc = (SettingsDocument)settingsBinding.entryToObject(value);
		    		if( null != sdoc ){
		    			settings = sdoc.getSettings();
		    		}
	    		}
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    		return settings ;
    }
    
    public void setDefaultSettings(SettingsType settings) throws DatabaseAccessException{
    		SettingsDocument sdoc  = SettingsDocument.Factory.newInstance();
    		sdoc.setSettings(settings);
    		
    		DatabaseEntry key = null ;
			 key = new DatabaseEntry(DEFAULTS.getBytes());
    		
    		DatabaseEntry value = new DatabaseEntry();
    		settingsBinding.objectToEntry(sdoc, value);
    		try{
    			database.put(null, key, value);
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    }
    
    
    public SfConfigsType getSFConfigs() throws DatabaseAccessException {
    		SfConfigsType settings = null ;
		
		DatabaseEntry key = new DatabaseEntry(SFCONFIGS.getBytes());
		DatabaseEntry value = new DatabaseEntry();
		
		try{
    		OperationStatus status = database.get(null, key, value, LockMode.DEFAULT);
    		if( OperationStatus.NOTFOUND == status ){
    			SfConfigsDocument sdoc = SfConfigsDocument.Factory.newInstance();
    			settings = sdoc.addNewSfConfigs();
    			sfConfigbinding.objectToEntry(sdoc, value);
    			database.put(null, key, value);
    			
    		}else{
    			SfConfigsDocument sdoc = (SfConfigsDocument)sfConfigbinding.entryToObject(value);
    			if( null != sdoc ){
    				//settings = sdoc.addNewSfConfigs();
    				settings = sdoc.getSfConfigs();
    			}
    		}
		}catch(DatabaseException e){
			throw new DatabaseAccessException(e);
		}
		return settings ;
	}

	public void setSfConfigs(SfConfigsType sfc) throws DatabaseAccessException {
		SfConfigsDocument sdoc  = SfConfigsDocument.Factory.newInstance();
		sdoc.setSfConfigs(sfc);
		
		DatabaseEntry key = null ;
		 key = new DatabaseEntry(SFCONFIGS.getBytes());
		
		DatabaseEntry value = new DatabaseEntry();
		sfConfigbinding.objectToEntry(sdoc, value);
		try{
			database.put(null, key, value);
		}catch(DatabaseException e){
			throw new DatabaseAccessException(e);
		}
		
	}

	public void close(){
    		try{
    			database.close();
    		}catch(DatabaseException e){
    			log.error(e);
    		}
    }

}
