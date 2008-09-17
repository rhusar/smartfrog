/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.monitor;

public class ModuleState {
	private String moduleId ; 
	private String status ;
	private String message ;
	private String host;
	
	public String getModuleId(){
		return moduleId;
	}
	public String getStatus(){
		return status;
	}
	public String getMessage(){
		return message; 
	}
	public String getHost(){
		return host;
	}
	
	public void setModuleId(String id){
		moduleId = id;
	}
	public void setStatus(String s){
		status = s;
	}
	public void setMessage(String m){
		message = m;
	}
	public void setHost(String h){
		this.host = h;
	}
	
	public String toString(){
		return "HostID="+host+",moduleId="+moduleId+",STATUS="+status+",MSG="+message;
	}
}
