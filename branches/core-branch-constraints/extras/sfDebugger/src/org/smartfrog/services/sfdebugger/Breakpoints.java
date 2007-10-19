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

This library was developed along with Manjunatha H S and Vedavyas H Raichur 
from Sri JayChamrajendra College of Engineering, Mysore, India. 
The work was part of the final semester Project work.

*/

package org.smartfrog.services.sfdebugger;

import java.io.InputStream;
import java.util.Vector;
import java.util.Stack;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;

/**
  * Defines a way to get the static breakpoints vector
  *
  */

public class Breakpoints extends ComponentDescriptionImpl implements CDVisitor {

	
	static ComponentDescription tree;

        private static Context cxt;
	
	private static Phases myPhases;

	private static InputStream stream;
	
	private static int root = 0;

	private CDVisitor CD = null;

	private static Vector BreakpointsVec = new Vector();

	public Breakpoints() {
		
        super(null,cxt,true);
		 
	}
	
/**
 * This method creates the vector of breakpoints
 * @param url of the component description file
 * @return Vector of Breakpoints
 */	
	public Vector getBreakpoints(String url) {
	    
		try {
		String language = SFParser.getLanguageFromUrl(url);
  	  
		stream = SFClassLoader.getResourceAsStream(url);
		
		BreakpointsVec.add("HERE Root");
		
		CD = new Breakpoints();
		
            	myPhases = new SFParser(language).sfParse(stream);
            
        	} catch (SmartFrogParseException e) {
            
            		e.printStackTrace();
        	} catch (SmartFrogException e) {
            	        e.printStackTrace();
        	}
		try {
		    
            		myPhases=myPhases.sfResolvePhases();
            
        	} catch (SmartFrogException e1) {
            
            		e1.printStackTrace();
        	}
		
		try {
		    
            		tree = myPhases.sfAsComponentDescription();
           
        	} catch (SmartFrogCompilationException e2) {
            
            		e2.printStackTrace();
        	}
        
        	try {
            
            		tree.visit(CD,true);
            
        	} catch (Exception e3) {
            
            		e3.printStackTrace();
        	}
        
       			return BreakpointsVec;
	}
	
/**
 * This method returns the component description
 * @return Component Description
 */	   
	public ComponentDescription getCompDesc(){
	       return tree;
	   }

/**
 * This method is called for every node in the tree 
 * for each node visited its component name is added into the breakpoint vector
 */
		public void actOn(ComponentDescription node, Stack stack) throws Exception {
		
	    		Reference ref;
	    		Object value;
	    			
			ref = node.sfCompleteName();

	    	        	if(root != 0)	
				BreakpointsVec.add(ref.toString());
	    	                root++;
	    	    
	       	//}//end for
	}//end actOn
}//end class





 
