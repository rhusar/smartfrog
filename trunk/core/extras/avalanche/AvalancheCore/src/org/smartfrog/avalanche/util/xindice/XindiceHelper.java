/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Apr 24, 2005
 *
 */
package org.smartfrog.avalanche.util.xindice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xindice.core.Collection;
import org.apache.xindice.core.DBException;
import org.apache.xindice.tools.command.Command;
import org.apache.xindice.util.Configuration;
import org.smartfrog.avalanche.server.RepositoryConfig;
import org.smartfrog.avalanche.server.modules.ModuleCreationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;


/**
 * @author sanjay, Apr 24, 2005
 *
 * Helper class for initializing xindice embeded database.  
 */
public class XindiceHelper {
	public static final String DB_PATH 	= 	"dbPath" ;
	public static final String DRIVER 	= 	"driver" ;
	public static final String MANAGED 	=	"managed";
	public static final String ROOT 	=	"root";


	private String m_driver = "org.apache.xindice.client.xmldb.embed.DatabaseImpl";
	private String m_root = "xmldb:xindice-embed:///db/";

	private org.apache.xindice.core.Database m_db = null; 

	private static final Log log = LogFactory.getLog(XindiceHelper.class);
	
	/**
	 * Constructor 
	 */
	public XindiceHelper() {
		super();
	}

	/**
	 * Initialize an xindice database.  
	 * 
	 * @param cfg database conig object see XindiceRepository for details of attributes.  
	 * @throws Exception
	 */
	public void init(RepositoryConfig cfg) throws ModuleCreationException{
		if( null != m_db ){
			log.info("Database already initialized, ignoring");
			return;
		}
		String dbPath = cfg.getAttribute(DB_PATH);
		// default for managed is true
		String managed = cfg.getAttribute(MANAGED);
		managed = (managed ==null)?"true" : managed ;
		
		String driver = cfg.getAttribute(DRIVER);
		driver = (driver == null )?m_driver : driver ;
		
		String dbRoot = cfg.getAttribute(ROOT);
		dbRoot = (dbRoot ==null)?m_root : dbRoot ; 
		
		try{			
			Database database = (Database)Class.forName(driver).newInstance();
			database.setProperty("db-home", dbPath);
			
			database.setProperty("managed",managed);

	       org.xmldb.api.DatabaseManager.registerDatabase(database);
	       org.xmldb.api.base.Collection rootCollection = org.xmldb.api.DatabaseManager.getCollection(dbRoot);

	       org.apache.xindice.client.xmldb.services.DatabaseInstanceManager xdbInstMgrService
	               = (org.apache.xindice.client.xmldb.services.DatabaseInstanceManager)
	                   rootCollection.getService("DatabaseInstanceManager", Command.XMLDBAPIVERSION);
	       
	       m_db = org.apache.xindice.core.Database.getDatabase("db");
	       
		}catch(XMLDBException e){
			log.fatal("Xindice database initialization failed", e);
			throw new ModuleCreationException(e);
		}catch(ClassNotFoundException e){
			log.fatal("Xindice database initialization failed", e);
			throw new ModuleCreationException(e);
		}catch(InstantiationException e){
			log.fatal("Xindice database initialization failed", e);
			throw new ModuleCreationException(e);
		}catch(IllegalAccessException e){
			log.fatal("Xindice database initialization failed", e);
			throw new ModuleCreationException(e);
		}
	}
	/**
	 * Add a new document in the given collection. 
	 * @param collection collection to add document
	 * @param doc 
	 */
	public void storeDocument(String collection, Document doc) throws DBException{
		if (null == m_db ){
			log.error("storeDocument was called before DB initialization");
			throw new DBException(0, "Database not initialized error");
		}
		try{
			Collection col = m_db.getCollection(collection);
			col.insertDocument(doc);
		}catch(DBException e){
			log.error("Document add failed in the collection"+ collection,e );
			throw e;
		}

	}
	
	/**
	 * Close the database connection
	 * @throws DBException if close fails.
	 */
	public void close() throws DBException{
		if (null == m_db ){
			log.info("close was called before DB initialization or after closing ");
			return ;
		}		
		try{
			m_db.close();
			m_db = null;
		}catch(DBException e){
			log.error("Error in Xindice DB connection close", e);
			throw e;
		}
	}
	
	/**
	 * Store the document in the collection with a given document Id, if a document already 
	 * exists with the same key it will be overwritten. 
	 * @param collection collection to add the document into. 
	 * @param docId key for the document
	 * @param doc document to store
	 * @throws DBException if doc add fails.
	 */
	public void storeDocument(String collection, String docId, Document doc) throws DBException{
		if (null == m_db ){
			log.error("storeDocument was called before DB initialization");
			throw new DBException(0, "Database not initialized error");
		}		
		try{
			Collection col = m_db.getCollection(collection);
			col.insertDocument(docId, doc);
		}catch(DBException e){
			log.error("Document add failed in collection :"+collection +" docId : "+docId, e);
			throw e;
		}
	}
	
	/**
	 * gets a collection if it exists at the path 
	 * @param collectionPath
	 * @return
	 * @throws DBException
	 */
	public Collection getCollection(String collectionPath) throws DBException{
		if (null == m_db ){
			log.error("getCollection was called before DB initialization");
			throw new DBException(0, "Database not initialized error");
		}
		
		return m_db.getCollection(collectionPath);
	}
	/**
	 * returns the database associated with the helper object
	 * @return database will be null if the db has not been initiated.
	 */
	public org.apache.xindice.core.Database getDatabase(){
		return m_db ;
	}
	/**
	 * 
	 * Create a default configuration for the collections to be created in the database. 
	 * @param collectionName
	 * @return
	 */
	protected Document getCollectionConfig(String collectionName){
        Document doc = new DocumentImpl();

        Element colEle = doc.createElement("collection");
        colEle.setAttribute("compressed", "false");
        colEle.setAttribute("name", collectionName);
        doc.appendChild(colEle);

        Element filEle = doc.createElement("filer");
        filEle.setAttribute("class", "org.apache.xindice.core.filer.BTreeFiler");
        colEle.appendChild(filEle);

        return doc;
}
	
	/**
	 * Create a new collection in the database at the given path.
	 * @param path
	 * @param name
	 * @return
	 * @throws DBException
	 */
	public Collection createCollection (String path, String name) throws DBException{
		if (null == m_db ){
			log.error("createCollection was called before DB initialization");
			throw new DBException(0, "Database not initialized error");
		}
		Document cfgDoc = getCollectionConfig( name );
		Configuration cfg = new Configuration ( cfgDoc , false);
		Collection parent = m_db.getCollection(path);
		if ( null == parent ){
			throw new DBException(1, "Parent collection doesnt exist " + path);
		}
		Collection col = null; 
		if( (col = parent.getCollection(name)) == null ){
			col = parent.createCollection(name, cfg);
		}
		return col;
	}
	
}
