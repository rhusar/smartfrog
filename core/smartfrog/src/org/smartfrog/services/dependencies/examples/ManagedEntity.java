package org.smartfrog.services.dependencies.examples;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.smartfrog.services.dependencies.statemodel.state.ThreadedState;
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
		
		if (createdprev && !created) {
			System.out.println(name+" setting created to true");
			save.put("created", true);
		}
		
		if (removedsucc) {
			System.out.println(name+" setting removed to true");
			save.put("removed", true);
		} 
		
		return save;
	}

}
