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

package org.smartfrog.sfcore.languages.sf.constraints;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.security.SFClassLoader;

/**
 * Base class for constraint solver
 * @author anfarr
 *
 */
public class CoreSolver {
	/**
	 * Solver class
	 */
	private static Class solverClass = null;
    
	/**
	 * Single instance of solver
	 */
	private static CoreSolver solver;
    
	/**
	 * Current relative root component description in link resolution
	 */
	protected SFComponentDescription top;
    
	/**
	 * Current absolute root description in link resolution
	 */
	protected SFComponentDescription orig;
    
	/**
	 * Solver class property
	 */
	public static final String g_solver_className = "org.smartfrog.sfcore.languages.sf.constraints.SolverClassName";
	
	/**
	 * Protected Constructor
	 */
	protected CoreSolver(){}
	
    /**
     * Attempt to obtain an instance of the solver for the constraints. 
     *
     * @return An instance of the solver class
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *
     */
    public static CoreSolver getInstance(){
    	try {
	        try {           	
	        	if (solver==null){
	                String classname=null; 
	                	
	                try {classname = System.getProperty(g_solver_className);
	                }catch (Exception e){/*Do nothing*/}
	                
	                try {if (classname==null) classname = System.getenv("SOLVERCLASS");
	                }catch (Exception e){/*Do nothing*/}
	              
	                if (classname != null){ 
		                solverClass = SFClassLoader.forName(classname);
		                solver = (CoreSolver) solverClass.newInstance();
	                }
	        	} 
	        } finally {
	        	if (solver==null) solver = new CoreSolver();
	        }
    	} catch (Exception e) {/*Do nothing*/}
        return solver; 
    }

    /**
     * Resets to null relative and absolute descriptions maintained herein
     */
    public void resetDescriptionMarkers(){ 
    	top = null; 
    	orig = null;
    }

    /**
     * Sets relative and absolute (absolute only if not already set) descriptions maintained herein
     */
    public void setDescriptionMarkers(SFComponentDescription mark){ 
    	if (orig==null) orig=mark; 
    	top = mark;
    }
        
    /**
     * Gets the current relative root component description in link resolution  
     * @return sfConfig component description
     */
    public SFComponentDescription getRootDescription(){ return top; }
        
    /**
     *  Gets the current absolute root component description in link resolution  
     * @return sfConfig component description
     */
    public SFComponentDescription getOriginalDescription(){ return orig; }
    
    /**
     * Solve constraint strings pertaining to a Constraint type...  
     * @param comp  Pertaining Component Description
     * @param attrs Attributes thereof
     * @param values Values thereof
     * @param goal Constraint goal to be solved
     * @param autos Automatic variable attributes
     * @param isuservars Whether there are user variables
     * @throws Exception
     */
    public void solve(ComponentDescription comp, Vector attrs, Vector values, Vector goal, Vector autos, boolean isuservars)  throws Exception {}
    
    /**
     * Called to indicate no more solving to be done for current sfConfig description
     */
    protected void stopSolving(){}    
    
    /**
     * Indicates whether operations such as "put" on Contexts should be recorded on undo stack for link resolution, as may need to be backtracked as part of constraint solving
     * @param undo
     */
    public void setShouldUndo(boolean undo){}
    
	/**
	 * Adds single undo action to current lhr for undo attribute setting in a Context
   	 * @param ctxt context for undo action
     * @param key  key to undo put value
   	 * @param value value to restore
	 */
	public void addUndoPut(Context ctxt, Object key, Object value){}
		
	/**
	 * Adds single undo action to current lhr for undoing FreeVar info setting
   	 * @param fv  FreeVar
     */
	public void addUndoFVInfo(FreeVar fv){}
	
	/**
	 * Adds single undo action to current lhr for undoing FreeVar type string setting
   	 * @param fv  FreeVar
     */
	public void addUndoFVTypeStr(FreeVar fv){}
	
	/**
	 * On backtracking, we have backtracked to the returned context
	 * @return Context to which backtracking has unwound to, null if no backtracking
	 */
	public Context hasBacktrackedTo(){ return null; }
	
	/**
	 * Reset "backtracked to" Context information
	 */
	public void resetDoneBacktracking(){}
	
	/**
	 * Get whether we are capable of solving constraints...
	 * @return whether we are capable of solving constraints...
	 */
	public boolean getConstraintsPossible(){ return false; }
	
	/**
	 * Determines whether component description should be ignored in course of link resolution.
	 * So far array generators should be.
	 * @param sfcd  component description in question
	 * @return whether component description should be ignored in course of link resolution
	 */
	public boolean ignoreComponentDescription(SFComponentDescription sfcd){
		return sfcd.sfContext().get("sfIsGenerator")!=null;
	}
	
	/**
	 * Tidy absolute root component descriptions after constraint solving.
	 * @param mark Root (either relative or absolute) component description currently being resolved
	 * @throws SmartFrogResolutionException
	 */
	public void tidyConstraintBasedDescription(SFComponentDescription mark) throws SmartFrogResolutionException{
	  
		if (mark!=getOriginalDescription()) return;
		try {  
		   //Do a visit to every cd removing constraint annotations... 
     	   this.getOriginalDescription().visit(new CDVisitor(){
     		   public void actOn(ComponentDescription node, java.util.Stack path){
     			   //Start by removing any child generator types
     			   //CHOOSE NOT TO DO THIS FOR NOW. 
     			   /*Enumeration keys = node.sfContext().keys();
     			   while (keys.hasMoreElements()){
     				   Object key = keys.nextElement();
     				   Object val = node.sfContext().get(key);
     				   if (val instanceof ComponentDescription){
     					   ComponentDescription val_cd = (ComponentDescription) val;
     					   Object is_gen = val_cd.sfContext().get("sfIsGenerator");
     					   if (is_gen!=null) node.sfContext().remove(key);
     				   }
     			   }*/
     			   
     			   //What about done function status etc?
     			   Object functionClassStatus=node.sfContext().get("sfFunctionClassStatus");                			
     			   try {  
                        if (functionClassStatus!=null && functionClassStatus.equals("done")){
                     	   node.sfContext().remove("sfFunctionClassStatus");
                     	   
                     	   //FIX for agg constraint...
                     	   node.sfContext().remove("sfConstraintAggregated");
                     	   Object functionClass=node.sfContext().get("sfFunctionClass");
                     	   node.sfContext().remove("sfFunctionClass");
                     	   
                     	   //FIX -- Can it be any other type if in here...?
                     	   if (functionClass.equals("org.smartfrog.sfcore.languages.sf.functions.Constraint")){
                         	   Enumeration attr_enum = node.sfContext().keys();
                         	   Vector attr_keys = new Vector();
                         	   while (attr_enum.hasMoreElements()){
                         		   Object _key = attr_enum.nextElement();
                         		   //Check not FreeVar as this should have been completed...
                         		   Object _val = node.sfContext().get(_key);
                         		   if (_val instanceof FreeVar) throw new SmartFrogResolutionException("VAR(s) are left in: "+node);	                            		   
                         			   if (node.sfContext().sfContainsTag(_key, "sfConstraint")) {
                         				   attr_keys.add(_key);
                         			   }
                         	   }
                         	   Iterator attr_iter = attr_keys.iterator();
                         	   while (attr_iter.hasNext()) node.sfContext().remove(attr_iter.next());
                         	   }
                        }
     			   } catch (Exception e){ throw new RuntimeException(e);}
     		 }
     	   }, true);
		  } catch (Exception e){ throw new SmartFrogResolutionException(e);  
		  } finally {    	   
				  resetDescriptionMarkers();
				  stopSolving();
		  }
        }	    

}
