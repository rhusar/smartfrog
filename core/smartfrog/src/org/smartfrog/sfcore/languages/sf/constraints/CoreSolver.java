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

public class CoreSolver {
	private static Class solverClass = null;
    private static CoreSolver solver;
    protected SFComponentDescription top;
    protected SFComponentDescription orig;
    
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
	                	
	                try {classname = System.getProperty("org.smartfrog.sfcore.languages.sf.constraints.SolverClassName");
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
	        	solver.prepareSolver();
	        }
    	} catch (Exception e) {/*Do nothing*/}
        return solver; 
    }

    /**
     * 
     */
    public void resetDescriptionMarkers(){ 
    	top = null; 
    	orig = null;
    }

    /**
     * 
     */
    public void setDescriptionMarkers(SFComponentDescription mark){ 
    	if (orig==null) orig=mark; 
    	top = mark;
    }
        
    /**
     * Gets the component description pertaining to sfConfig 
     * @return sfConfig component description
     */
    public SFComponentDescription getRootDescription(){ return top; }
        
    /**
     *  
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
     * Indicates no more solving to be done for current sfConfig description
     * @throws Exception
     */
    public void stopSolving() throws Exception{}    
    
    protected void prepareSolver() throws SmartFrogResolutionException {}
    
    public void setShouldUndo(boolean undo){}
    
	/**
	 * Add undo action to current lhr
	 * @param ctxt
	 * @param key
	 * @param value
	 */
	public void addUndo(Context ctxt, Object key, Object value){}
		
	/**
	 * Add undo action to current lhr
	 * @param fv
	 * @param type
	 */
	public void addUndo(FreeVar fv, int type){}

	public Context hasBacktrackedTo(){ return null; }
	
	public void resetDoneBacktracking(){}
	
	public boolean getConstraintsPossible(){ return false; }
	
	public boolean ignoreComponentDescription(SFComponentDescription sfcd){
		return sfcd.sfContext().get("sfIsGenerator")!=null;
	}
	
    public void tidyConstraintBasedDescription(SFComponentDescription mark) throws SmartFrogResolutionException{
		  try {
			  if (mark==getOriginalDescription()){
				  tidyConstraintBasedDescriptionWkr();
			  }
		  } catch (Exception e){
			  throw new SmartFrogResolutionException(e);
		  }
	}
	    
	  private void tidyConstraintBasedDescriptionWkr() throws Exception {
	  //Do a visit to every cd removing constraint annotations... 
		  try {
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
     			   } catch (Exception sfre){ throw new RuntimeException(sfre);}
     		 }
     	   }, true);
     	   
		  } finally {    	   
				  resetDescriptionMarkers();
				  stopSolving();
		  }
        }	    

}
