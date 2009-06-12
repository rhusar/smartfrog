package org.smartfrog.examples.sudoku;

import java.io.IOException;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Directory;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.Protocol;

public class SudokuApplicationServer extends Application {
	
	/** 
	 * Creates a root Restlet that will receive all incoming calls. 
	 */  
	@Override  
	public synchronized Restlet createRoot() {  
	   // Create a router Restlet that routes each call to a new instance of SudokoResource.  
	   Router router = new Router(getContext());  
	 
	   // Defines only one route  
	   String ROOT_URI = "file:///home/andrew/sudrestlet";   
		 
	   router.attach("/solver", SudokuResource.class);  
	   router.attach("/sudoku", new Directory(getContext(), ROOT_URI));
	  
	   return router;  
	}  
	
	
	public static void main(String[] args) throws IOException {  
		 try {  
			 
			 //Set Properties for SF solving...
			 //BaseSudokuApplication.properties();
			 
	         // Create a new Component.  
	         Component component = new Component();  
	   
	         // Add a new HTTP server listening on port 8182.  
	         component.getServers().add(Protocol.HTTP, 8182);  
	         component.getClients().add(Protocol.FILE);  
	   
	         // Attach the sample application.  
	         component.getDefaultHost().attach(new SudokuApplicationServer());  
	         
	         // Start the component.  
	         component.start();  
	         	         
		 } catch (Exception e) {  
	         // Something is wrong.  
	         e.printStackTrace();  
		 }  
	}
}
