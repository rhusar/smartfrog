package org.smartfrog.examples.sudoku;

import java.util.Vector;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.smartfrog.SFParse;
import org.smartfrog.SFParse.RawParseModifier;
import org.smartfrog.examples.sudoku.BaseSudokuApplication.Location;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

/** 
 * Resource which has only one representation. 
 * 
*/  
public class SudokuResource extends Resource {  
   
    public SudokuResource(Context context, Request request, Response response) {  
         super(context, request, response);  
   
         // This representation has only one type of representation.  
         getVariants().add(new Variant(MediaType.TEXT_PLAIN));  
     }  
   
     /** 
      * Returns a full representation for a given variant. 
      */  
     @Override  
     public Representation represent(Variant variant) throws ResourceException { 
    	 
    	//Collect together puzzle input...
 		final Vector puzzle = new Vector();
 		
 		Form form = getRequest().getResourceRef().getQueryAsForm();
   	    for (Parameter para : form){
   	       String sq_s = para.getName();
   	       String val_s = para.getValue();
   		   Integer sq_i = Integer.parseInt(sq_s);
   		   Integer val_i = Integer.parseInt(val_s);
   	       Location l = BaseSudokuApplication.convertLocation(sq_i.intValue());
 		   Vector entry = new Vector();
 		   entry.add(l.loc);
 		   entry.add(val_i.intValue());
 		   puzzle.add(entry);
 		}
 		
   	    BaseSudokuApplication.properties();
 		
   	    ComponentDescription cd = null;
 		
 		try {
 			cd=SFParse.parseFileToDescription(BaseSudokuApplication.sudoko9, new RawParseModifier(){
 	
 			public void modify(ComponentDescription cd){
 				ComponentDescription sfConfig = (ComponentDescription) cd.sfContext().get("sfConfig");
 				sfConfig.sfContext().put("puzzle", puzzle);
 			}
 		});
 		} catch (Throwable t){System.out.println("Caught exception");/*Do nothing*/}
 		
 		String return_str ="";
 		
 		if (cd!=null){
 			Vector result = (Vector) cd.sfContext().get("puzzle");
 			
 			for(int i=0; i<result.size(); i++){
 				Vector square = (Vector) result.get(i);
 				Vector loc = (Vector) square.get(0);
 				int r = ((Integer)loc.get(0)).intValue();
 				int c = ((Integer)loc.get(1)).intValue();
 				int sq = BaseSudokuApplication.convertLocationToIndex(r, c);
 				int v = ((Integer)square.get(1)).intValue();
 				return_str += sq+"="+v+"&";
 			}
 		} 
    	 
        Representation representation = new StringRepresentation(return_str, MediaType.TEXT_PLAIN);  
        return representation;  
     }  
}