/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.shared;

import java.io.Serializable;
import java.util.Map;


public class MonitoringEventDefaultImpl implements MonitoringEvent, Serializable {
	private String moduleId ;
	private String host ;
	private String moduleState ;
	private String msg ;
	private Map propertyBag ;
	private String instanceName = null;
	private int messageType ; 
	private String timestamp ; 
	private String lastAction ; 
	
	public String getModuleId(){
		return moduleId;
	}
	
	public String getHost(){
		return host;
	}
	public String getModuleState(){
		return moduleState ;
	}
	public String getInstanceName(){
		return instanceName;  
	}
	public int getMessageType(){
		return messageType ; 
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
	public void setInstanceName(String name){
		this.instanceName = name ;
	}
	public void setMessageType(int msgtype){
		this.messageType = msgtype;
	}

	public void setMsg(String m){
		this.msg = m ;
	}
	public void addToPropertyBag(String key, String value){
		this.propertyBag.put(key, value);
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
	
	public String toString(){
		return "MonitoringEventDefaultImpl[moduleId="+ moduleId +
										",host=" + host +
										",instanceName=" + instanceName +
										",msg=" + msg +
										",lastAction=" + lastAction +
										",moduleState=" + moduleState +
										",timestamp=" + timestamp +"]";
	}
	
	
}
