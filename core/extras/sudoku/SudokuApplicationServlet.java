package org.smartfrog.examples.sudoku;

import java.io.IOException;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Directory;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.Protocol;

public class SudokuApplicationServlet extends Application {
	
	/** 
	 * Creates a root Restlet that will receive all incoming calls. 
	 */  
	@Override  
	public synchronized Restlet createRoot() {  
	   // Create a router Restlet that routes each call to a new instance of SudokoResource.  
	   Router router = new Router(getContext());  
	 
	   router.attachDefault(SudokuResource.class);  
	  
	   return router;  
	}  
	
}
