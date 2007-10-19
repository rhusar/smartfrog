/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jan 29, 2005
 *
 */
package org.smartfrog.avalanche.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Holder for configration attributes. Attributes vary depending on different
 * type of repositories. 
 * @author sanjay
 */

public class RepositoryConfig {
	private HashMap attributes = new HashMap();
	
	public RepositoryConfig(){
		
	}
	
	/**
	 * Copy constructor. 
	 * @param cfg RepositoryConfig object to copy from
	 */
	public RepositoryConfig(RepositoryConfig cfg){
		attributes = new HashMap(cfg.getConfigData());
	}
	/*
	 * Returns null if attribute does not exist.
	 */
	public String getAttribute(String key){
		return (String)attributes.get(key);
	}
	/*
	 * Accepts any value except null. throws exception if null is set
	 */
	public void setAttribute(String key, String value){
		attributes.put(key, value);
	}
	public String[] listAttributes(){
		return (String[])attributes.keySet().toArray(new String[0]);
	}
	public Map getConfigData(){
		return attributes;
	}
}
