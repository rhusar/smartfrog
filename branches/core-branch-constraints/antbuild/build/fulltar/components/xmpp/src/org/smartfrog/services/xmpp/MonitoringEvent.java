/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.services.xmpp;

import java.util.Map;
/**
 * Interface to monitoring event information sent from client 
 * nodes to server. This is independent of the protocol used for monitoring 
 * abd should be implemented for a specific Monitoring protocol type. 
 * @author sanjaydahiya
 *
 */
public interface MonitoringEvent {

	public static String HOST = "_host" ;
	public static String MODULEID = "_moduleId";
	public static String MESSAGE = "_msg" ;
	public static String MODULE_STATE = "_moduleState" ;
	public static String INSTANCE_NAME = "_instanceName" ;
	public static String MESSAGE_TYPE = "_msgType" ;
	public static String ACTION_NAME = "_actionName" ;
	
	public String getModuleId() ;
	public String getInstanceName();
	
	public String getHost() ;
	public String getModuleState() ; 
	public int getMessageType();
	

	public String getMsg() ;
	public Map<String, String> getPropertyBag(); 
	public String getTimestamp();
	public String getLastAction();
	
	public void setModuleId(String id); 
	public void setHost(String h) ; 
	public void setModuleState(String state) ; 
	public void setInstanceName(String instanceName);
	public void setMessageType(int type);
	
	public void setMsg(String m) ; 
	public void setTimestamp(String t);
	public void addToPropertyBag(String key, String value) ; 
	public void setLastAction(String action);
}
