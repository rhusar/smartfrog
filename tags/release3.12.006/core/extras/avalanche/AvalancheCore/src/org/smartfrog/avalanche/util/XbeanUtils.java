/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Aug 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.smartfrog.avalanche.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xindice.core.Collection;
import org.apache.xindice.core.DBException;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class XbeanUtils {
	
	private static Log log = LogFactory.getLog(XbeanUtils.class);	
	
	public XbeanUtils() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * Converts a XmlObject to DOM Document, this is a copy of the obj and is not live.
	 * if argument is null or is not a Document but is a fragment instead, this method returns null.
	 * @param obj
	 * @return
	 */
	public static Document toDOM(XmlObject obj){
		Document doc = null;
		
		if( null != obj){
			Node n = obj.newDomNode();
			if (n instanceof Document) {
				doc = (Document) n;
			}else{
				// not a document .. fail
			}
		}
		
		return doc;
	}
	
	/**
	 * Saves a document in the given collection, if save fails it returns null.  
	 * @param key
	 * @param doc
	 * @param col
	 * @throws DBException if save to collection fails
	 */
	public static synchronized boolean save(Object key, Document doc, Collection col) throws DBException{
		boolean ret = false; 
		if( null != doc && null != col){
			try{
				//System.out.println("Document to save key: " + key);
				//XMLUtils.docToStream(doc, System.out);
			}catch(Exception e){
				e.printStackTrace();
			}
			if( key == null){
				// insert if no key
				col.insertDocument(doc);
			}else{
				// check if key exists
				if( col.getDocument(key) != null){
					col.setDocument(key, doc);
				}else{
					// no key, insert new with key
					col.insertDocument(key, doc);
				}
			}
			ret = true;
			col.flushSymbolTable();
		}
		return ret;
	}
	
	public static boolean save(Object key, XmlObject obj, Collection col) throws DBException{
		Document doc = toDOM(obj);
		log.debug("Saving XmlObject : " + obj);
		return save(key, doc, col);
	}
}
