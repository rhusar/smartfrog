/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.shared;

import org.smartfrog.services.anubis.locator.AnubisValue;
import org.smartfrog.services.anubis.locator.names.ProviderInstance;

import java.util.Map;

/**
 * Avalanche Monitoring event implementation for Anubis. 
 * @author sanjaydahiya
 *
 */
public class AnubisMonitoringEvent extends AnubisValue implements MonitoringEvent {
	private String moduleId ;
	private String host ;
	private String moduleState ;
	private String msg ;
	private String instanceName ; 
	private int messageType ; 
	private Map propertyBag ;
	private String timestamp ;
	private String lastAction ; 
	
	public AnubisMonitoringEvent(ProviderInstance i){
		super(i);
	}
	public String getModuleId(){
		return moduleId;
	}
	
	public String getHost(){
		return host;
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
	public void setHost(String h){
		this.host = h ;
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
	public String getInstanceName() {
		return instanceName;
	}
	public int getMessageType() {
		return messageType;
	}
	public void setInstanceName(String name) {
		this.instanceName = name ;
	}
	public void setMessageType(int type) {
		this.messageType = type ; 
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String t) {
		this.timestamp = t;
	}
	public String getLastAction() {
		return lastAction;
	}

	public void setLastAction(String action) {
		this.lastAction = action; 
	}
	
}
