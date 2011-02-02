/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/
package org.smartfrog.services.dependencies.legacy.examples;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.smartfrog.services.dependencies.legacy.statemodel.state.ThreadedState;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.prim.Prim;

public class ManagedEntity extends ThreadedState implements Prim {
	
	public ManagedEntity() throws RemoteException {super();}  
	
	public boolean requireThread(HashMap data){ return true; }  
	
	public HashMap threadBody(HashMap data){
		
		HashMap save = new HashMap();
		
		boolean created = ((Boolean) data.get("created")).booleanValue();
		boolean sink = ((Boolean) data.get("sink")).booleanValue();
		String name = (String) data.get("name");
		
		//For test...
		String cur_output="";
		Context parent=null;
		try { 
			parent = sfParent().sfContext();
			cur_output = (String) parent.get("output"); 
		} catch(Exception e){} 		
		//
		
		if (!created) {
			//System.out.println(name+" setting created to true");
			save.put("created", true);
			
			//For test
			cur_output+=(name+"c");
			//
		} 
		
		if (created || sink) {
			//System.out.println(name+" setting removed to true");
			save.put("removed", true);
			
			//For test
			cur_output+=(name+"r");
			//
		} 
		
		//For test
		parent.put("output",cur_output); 
		//
		
		return save;
	}

}
