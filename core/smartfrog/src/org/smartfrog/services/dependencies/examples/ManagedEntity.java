package org.smartfrog.services.dependencies.examples;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.smartfrog.services.dependencies.statemodel.state.ThreadedState;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.prim.Prim;

public class ManagedEntity extends ThreadedState implements Prim {
	
	public ManagedEntity() throws RemoteException {super();}  
	
	public boolean requireThread(HashMap data){ return true; }  
	
	public HashMap threadBody(HashMap data){
		
		HashMap save = new HashMap();
		
		boolean created = ((Boolean) data.get("created")).booleanValue();
		boolean createdprev = ((Boolean) data.get("createdprev")).booleanValue();
		boolean removedsucc = ((Boolean) data.get("removedsucc")).booleanValue();
		String name = (String) data.get("name");
		
		//For test...
		String cur_output="";
		Context parent=null;
		try { 
			parent = sfParent().sfContext();
			cur_output = (String) parent.get("output"); 
		} catch(Exception e){} 
		
		if (parent!=null) System.out.println(""+parent);
		//
		
		if (createdprev && !created) {
			System.out.println(name+" setting created to true");
			save.put("created", true);
			
			//For test
			cur_output+=(name+"c");
			//
		}
		
		if (removedsucc) {
			System.out.println(name+" setting removed to true");
			save.put("removed", true);
			
			//For test
			cur_output+=(name+"r");
			//
		} 
		
		//For test
		parent.put("output",cur_output); 
		if (parent!=null) System.out.println(""+parent);
		//
		
		return save;
	}

}
