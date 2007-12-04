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

package org.smartfrog.sfcore.languages.sf.sfcomponentdescription;

import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.constraints.FreeVar;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Encapsulates the state that is maintained during link resolution.
 * We need to maintain a stack of component descriptions that we can pop on backtracking.
 *
 */
public class LinkResolutionState {

	/**
	 * Records link resolution history.  Concerned with maintaining undo information.
	 */
	private Vector linkHistory = new Vector(); 
	
	/**
	 * Maintains a history of Constraint goals that have been (successfully) evaluated
	 */
	private Vector constraintEvalHistory = new Vector();
	/**
	 * Indicates current component description being processed
	 */
	private LRSRecord currentLRSRecord; 

    /**
     * Gets the current LRS record
     * @return the current record
     */
    public LRSRecord getLRSRecord(){ return currentLRSRecord; }

	
	/**
	 * Indicates current link history record being used.
	 */
    private LinkHistoryRecord currentLHRecord;
    /**
     * Indicates index of attribute currently being processed in current description
     */
    private int currentIndex=0;
    /**
     * Increases the index of attribute currently being processed in current description
     *
     */
    public void incIdx(){ currentIndex++; }
    
    /**
     * Gets the index of attribute currently being processed in current description
     * @return the index
     */
    public int getIdx(){ return currentIndex; }
    

    /**
     * Used to maintain whether backtracking -- as part of constraint solving -- has just occurred in processing current attribute. 
     */
    private Context backtrackedTo=null;
	/**
	 * Gets whether backtracking has occurred recently
	 * @return backtracking flag
	 */
    public Context hasBacktrackedTo() { return backtrackedTo; }
    /**
     * Resets flag wrt backtracking
     *
     */
    public void resetDoneBacktracking() { backtrackedTo=null; }

    
    /**
     * The single instance of this class
     */
    private static LinkResolutionState g_lrs;
    /**
     * Gets the single instance of this class
     * @return single LinkResolutionState instance
     */
    public static LinkResolutionState getLRS() { 
    	if (g_lrs==null){
    		g_lrs = new LinkResolutionState();
    	}
    	return g_lrs; 
    } 
	    
    /**
     * Indicates that constraint resolution is possible. This is determined by whether the solver can be allocated.
     * Also will be false if no Constraint descriptions are present in a source.
     */
    private static boolean g_constraintsPossible = false;
    /**
     * Indicates whether we have tried to allocate a solver
     */
    private static boolean g_constraintsTried = false;
    
    /**
     * Gets whether constraint solving is possible
     * @return
     */
    public static boolean getConstraintsPossible(){ return g_constraintsPossible; }
    /**
     * Resets flag indicating that allocation of constraint solver has been tried. 
     *
     */
    public static void resetConstraintsTried(){ g_constraintsPossible = g_constraintsTried = false;}    
    
    /**
     * Indicates whether manipulations of context attributes should currently have undo records created for them
     */
    private static boolean g_constraintsShouldUndo = false;
    /**
     * Gets whether manipulations of context attributes should currently have undo records created for them
     * @return
     */
    public static boolean getConstraintsShouldUndo(){ return g_constraintsPossible && g_constraintsShouldUndo; }
    /**
     * Sets whether manipulations of context attributes should currently have undo records created for them
     * @param shouldUndo
     */
    public static void setConstraintsShouldUndo(boolean shouldUndo){ g_constraintsShouldUndo = shouldUndo; }
    
    /**
     * Tries to allocate constraint solver if parameter type is a Constraint
     * @param type
     */
    public static void setConstraintRelevance(Reference type){
    	String type_s = type.toString();
    	if (!type_s.equals("Constraint") || g_constraintsTried) return;
    	if (CoreSolver.getInstance()!=null) g_constraintsPossible=true;
    	g_constraintsTried = true;
    }
    
    /**
     * Resets LinkResolutionState state
     *
     */
    public static void resetConstraintRelevance(){
    	if (g_constraintsPossible){
    		CoreSolver cs = CoreSolver.getInstance();
    		try { if (cs!=null) cs.stopSolving(); } catch (Exception e){}
    	}
    	g_constraintsTried = g_constraintsPossible = false;
    	g_lrs = new LinkResolutionState();
    }
       
    /**
     * Constructs LinkResolutionState, allocating a link history record
     *
     */
	LinkResolutionState(){
		currentLHRecord = new LinkHistoryRecord();
		linkHistory.add(currentLHRecord);
	}
	
	/**
	 * Maintains a record pertaining to a single constraint evaluation
	 * @author anfarr
	 *
	 */
	private class ConstraintEvalHistoryRecord {
		/**
		 * Indicates the current component description being processed 
		 */
		LRSRecord lrsr;
		/**
		 * Indicates the current attribute being processed, triggering constraint evaluation 
		 */
		int idx;
		/**
		 * The context of the component description
		 */
		Context cxt;
	}
	
	/**
	 * Link history record
	 * @author anfarr
	 *
	 */
     private class LinkHistoryRecord {
    	 /**
    	  * The undo stack for this record
    	  */
		Vector undo_stack = new Vector();
		/**
		 * Add a single undo record
		 * @param lrsu
		 */
		void addUndo(LRSUndoRecord lrsu){ undo_stack.add(lrsu); }
		/**
		 * Undo all actions recorded herein
		 *
		 */
		void undoAll() {
			for (int i=undo_stack.size()-1;i>=0;i--){ 
				((LRSUndoRecord) undo_stack.remove(i)).undo();
			}
		}
	}

     /**
      * Undo action: put, for putting values for attributes in contexts
      */
    public static final int g_LRSUndo_PUT = 0x0;

    /**
     * Undo action: put, for putting values for key into FreeVars
     */
    public static final int g_LRSUndo_PUTFVKEY = 0x1;

    /**
     * Undo action: put, for putting values for idx into FreeVars
     */
    public static final int g_LRSUndo_PUTFVIDX = 0x2;
    
    public static final int g_LRSUndo_PUTFVTYPESTR = 0x3;
    
    
    /**
     * Maintains single undo actions
     * @author anfarr
     *
     */
    private class LRSUndoRecord{
        /**
         * Pertinent context for undo action
         */
    	Context ctxt;
    	/**
    	 * Key for undo action
    	 */
    	Object key;
    	/**
    	 * Value to eg put back in context for key
    	 */
    	Object value;
    	/**
    	 * Type of action, eg put
    	 */
    	int type;
    	
    	FreeVar fv;
    	
    	int idx;
    	
    	/**
    	 * Constructs single undo action for g_LRSUndo_PUT
    	 * @param type
    	 * @param ctxt
    	 * @param key
    	 * @param value
    	 */
    	LRSUndoRecord(Context ctxt, Object key, Object value){
    		this.type = g_LRSUndo_PUT; this.ctxt=ctxt; this.key=key; this.value=value;
    	}
    	
    	/**
    	 * Constructs single undo action for g_LRSUndo_PUTFVKEY
    	 * @param type
    	 * @param ctxt
    	 * @param key
    	 * @param value
    	 */
    	LRSUndoRecord(FreeVar fv, Object key){
    		this.type = g_LRSUndo_PUTFVKEY; this.fv=fv; this.key=key;
    	}
    	
    	/**
    	 * Constructs single undo action for g_LRSUndo_PUTFVIDX
    	 * @param type
    	 * @param ctxt
    	 * @param key
    	 * @param value
    	 */
    	LRSUndoRecord(FreeVar fv, int idx){
    		this.type = g_LRSUndo_PUTFVIDX; this.fv=fv; this.idx=idx;
    	}
    	
    	/**
    	 * Constructs single undo action for g_LRSUndo_PUTFVIDX
    	 * @param type
    	 * @param ctxt
    	 * @param key
    	 * @param value
    	 */
    	LRSUndoRecord(FreeVar fv){
    		this.type = g_LRSUndo_PUTFVTYPESTR; this.fv=fv; 
    	}
    	
    	/**
    	 * Does the undo!
    	 *
    	 */
    	void undo(){
    		switch (type){
    		case g_LRSUndo_PUT: if (value!=null) ctxt.put(key, value); 
    		                    else ctxt.remove(key); break;
    		case g_LRSUndo_PUTFVKEY: fv.setConsEvalKey(key); break;
    		case g_LRSUndo_PUTFVIDX: fv.setConsEvalIdx(idx); break;
    		case g_LRSUndo_PUTFVTYPESTR: fv.clearTyping(); break;
    		}		
    	}
    }
     
	/**
	 * Link Resolution State Record
	 * @author anfarr
	 *
	 */
	public class LRSRecord {
		/**
		 * Current description being processed
		 */
		SFComponentDescription me;
		/**
		 * My parent description
		 */
		LRSRecord par;
		/**
		 * My index into my parent
		 */
		int my_idx;
	
		/**
		 * Get my parent description
		 * @return
		 */
		public LRSRecord getParent(){ return par; }
		/**
		 * Get my index wrt my parent
		 * @return
		 */
		public int getMyIndex(){ return my_idx; }
		/**
		 * Get current component description
		 * @return
		 */
		public SFComponentDescription getSFCD(){ return me; }
	}
	
	/**
	 * Pop a record off the link history
	 * @return
	 */
	public LRSRecord pop(){
	   currentIndex = currentLRSRecord.my_idx;
       currentLRSRecord = currentLRSRecord.getParent();
       currentIndex++;
       return currentLRSRecord;
	}
		
	/**
	 * Create and add a record to link history
	 * @param me
	 * @param par
	 * @param idx
	 */
	public LRSRecord addLRSRecord(SFComponentDescription me, int idx){
		LRSRecord lrsr = new LRSRecord();
		lrsr.me = me;
		lrsr.par = currentLRSRecord;
		lrsr.my_idx = idx;
		
		currentIndex = 0;
		currentLRSRecord = lrsr;
		return currentLRSRecord;
	}
		
	/**
	 * Add undo action to current lhr
	 * @param type
	 * @param ctxt
	 * @param key
	 * @param value
	 */
	public void addUndo(Context ctxt, Object key, Object value){
		currentLHRecord.addUndo(new LRSUndoRecord(ctxt, key, value));
	}
		

	/**
	 * Add undo action to current lhr
	 * @param type
	 * @param ctxt
	 * @param key
	 * @param value
	 */
	public void addUndo(FreeVar fv, Object key){
		currentLHRecord.addUndo(new LRSUndoRecord(fv, key));
	}

	/**
	 * Add undo action to current lhr
	 * @param type
	 * @param ctxt
	 * @param key
	 * @param value
	 */
	public void addUndo(FreeVar fv, int idx){
		currentLHRecord.addUndo(new LRSUndoRecord(fv, idx));
	}
	
	/**
	 * Add undo action to current lhr
	 * @param type
	 * @param ctxt
	 * @param key
	 * @param value
	 */
	public void addUndo(FreeVar fv){
		currentLHRecord.addUndo(new LRSUndoRecord(fv));
	}
	
	
	public void setTyping(String attr, Vector types){
		int last_cidx = constraintEvalHistory.size()-1;
		ConstraintEvalHistoryRecord cehr = (ConstraintEvalHistoryRecord) constraintEvalHistory.get(last_cidx);
		Object val = cehr.cxt.get(attr);
		if (val instanceof FreeVar){
			FreeVar fv = (FreeVar) val;
			fv.setTyping(attr, types);
			//need to add an undo record for the typing...
			addUndo(fv);
		}
	}
	
	public Object adjustSetValue(Object key){
		int last_cidx = constraintEvalHistory.size()-1;
		ConstraintEvalHistoryRecord cehr = (ConstraintEvalHistoryRecord) constraintEvalHistory.get(last_cidx);
		Object val = cehr.cxt.get(key);
	    if (val!=null && val instanceof ComponentDescription) return val;
	    else return key;
	}
	
	/**
	 * Add an assingment of an attribute within a description
	 * @param idx The appropriate link history record
	 * @param key Attribute to set 
	 * @param val Value to set
	 * @param cidx The appropriate constraint eval record
	 */
	
	public boolean addConstraintAss(ComponentDescription solve_comp, int idx, String key, Object val, int cidx) throws SmartFrogResolutionException {
		ConstraintEvalHistoryRecord cehr = (ConstraintEvalHistoryRecord) constraintEvalHistory.get(cidx);
		
		//Get typing information...
		Object cur_val = cehr.cxt.get(key);
		Vector types=null;
		if (cur_val instanceof FreeVar) types = ((FreeVar) cur_val).getTyping();
		
		if (types!=null && (!(val instanceof ComponentDescription) || 
				!ofTypes(solve_comp, (ComponentDescription)val, types))) return false; 
		
		//set the value prescribed
		g_constraintsShouldUndo=true;
		cehr.cxt.put(key, val);		
		//System.out.println("Setting "+key+":"+cehr.cxt.get(key)+" in "+cidx);
		g_constraintsShouldUndo=false;
		
		return true;
	}
	
	public boolean ofTypes(ComponentDescription solve_comp, ComponentDescription comp, Vector types) throws SmartFrogResolutionException {
		Context type_cxt = null;
		try {
		   type_cxt = SFComponentDescriptionImpl.composeTypes(solve_comp, types);
		} catch (SmartFrogResolutionException smfre){
			throw new SmartFrogResolutionException("Unable to compose types in sub-type evaluation.");
		}
		return type_cxt.ofType(comp);
	}
	
	public void backtrackConstraintAss(int idx, int cidx){
		ConstraintEvalHistoryRecord cehr = (ConstraintEvalHistoryRecord) constraintEvalHistory.get(cidx); 
		
		//need to backtrack cidx...
		int constraintEvalHistoryLastIdx = constraintEvalHistory.size()-1;

		if (backtrackedTo==null && cidx<constraintEvalHistoryLastIdx) backtrackedTo = cehr.cxt;
				
		for (int i=constraintEvalHistoryLastIdx; i>cidx; i--) constraintEvalHistory.remove(i);
		currentIndex = cehr.idx;
		currentLRSRecord = cehr.lrsr;
	
		//need to backtrack histroy as approp...
		for (int i=linkHistory.size()-1; i>idx; i--){
             LinkHistoryRecord lhr = (LinkHistoryRecord) linkHistory.remove(i);
             lhr.undoAll();
		}
		
		//create new history...
		currentLHRecord = new LinkHistoryRecord();
		linkHistory.add(currentLHRecord);		
	}
	
	/**
	 * Add a record to cons eval history
	 * @param cxt Given context
	 * @return Latest record index
	 */
    public int addConstraintEval(Context cxt){ 
    	int idx = constraintEvalHistory.size();
    	ConstraintEvalHistoryRecord cehr = new ConstraintEvalHistoryRecord();
    	cehr.lrsr = currentLRSRecord;
    	cehr.idx = currentIndex;
    	cehr.cxt = cxt;
    	constraintEvalHistory.add(cehr);
    	return idx;
    }
    
    /**
     * Gets the latest record index of the cons eval history
     * @return latest index
     */
    public int getConsEvalIdx(){
    	return constraintEvalHistory.size()-1;
    }
}
