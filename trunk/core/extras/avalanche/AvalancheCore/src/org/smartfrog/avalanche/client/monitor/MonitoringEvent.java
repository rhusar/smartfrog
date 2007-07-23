/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.avalanche.client.monitor;

import java.util.Map;

/**
 * This may be removed. 
 * @author sanjaydahiya
 *
 */
public class MonitoringEvent {

	private String moduleId ; 
	private String moduleState ;
	private String msg ;
	private Map propertyBag ;
	
	public String getModuleId(){
		return moduleId;
	}
	
	public String getModuleState(){
		return moduleState ;
	}

	public String getMsg(){
		return msg;
	}
	public Map getPropertyBag(){
		return propertyBag;
	}
	
	public void setModuleId(String id){
		this.moduleId = id ;
	}
	public void setModuleState(String state){
		this.moduleState = state ;
	}

	public void setMsg(String m){
		this.msg = m ;
	}
	public void addToPropertyBag(String key, String value){
		this.propertyBag.put(key, value);
	}
}
