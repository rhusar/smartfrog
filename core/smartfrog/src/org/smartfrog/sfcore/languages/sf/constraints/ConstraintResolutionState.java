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

import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl.LRSRecord;

/**
 * Maintains state of constraint resolution, done as part of link resolution
 * @author anfarr
 *
 */

public class ConstraintResolutionState {
	/**
	 * Records link resolution history.  Concerned with maintaining undo information when constraints are used
	 */
	//It is necessary to maintain link history separately as backtracking occurs to a finer granularity than Constraint types
	private Vector<LinkHistoryRecord> linkHistory = new Vector<LinkHistoryRecord>(); 
	
	/**
	 * Maintains a history of Constraint goals that have been (successfully) evaluated
	 */
	private Vector<ConstraintEvalHistoryRecord> constraintEvalHistory = new Vector<ConstraintEvalHistoryRecord>();
	 
	/**
	 * Indicates current link history record being used.
	 */
    private LinkHistoryRecord currentLHRecord;
    
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
     */
    public void resetDoneBacktracking() { backtrackedTo=null; }
        
    /**
     * Indicates whether manipulations of context attributes should currently have undo records created for them
     */
    private boolean m_constraintsShouldUndo = false;
    
    /**
     * Sets whether manipulations of context attributes should currently have undo records created for them
     * @param shouldUndo
     */
    public void setConstraintsShouldUndo(boolean shouldUndo){ m_constraintsShouldUndo = shouldUndo; }        	
       
    /**
     * Constructs ConstraintResolutionState, allocating a link history record
     *
     */
	ConstraintResolutionState(){
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
		 * Indicates the current LRSRecord being processed 
		 */
		LRSRecord lrsr;
		/**
		 * Indicates the current attribute being processed, triggering constraint evaluation 
		 */
		int idx;
		/**
		 * The context of the component description being processed
		 */
		Context cxt;
	}
	
	/**
	 * Link history record
	 */
     private class LinkHistoryRecord {
    	 /**
    	  * The undo stack for this record
    	  */
		Vector<LRSUndoRecord> undo_stack = new Vector<LRSUndoRecord>();
		/**
		 * Add a single undo record
		 * @param lrsu undo record to add
		 */
		void addUndo(LRSUndoRecord lrsu){ undo_stack.add(lrsu); }
		/**
		 * Undo all actions recorded herein
		 */
		void undoAll() {
			for (int i=undo_stack.size()-1;i>=0;i--){ 
				undo_stack.remove(i).undo();
			}
		}
	}

    /**
     * Abstract class for undo actions
     */
    abstract private class LRSUndoRecord{    	
    	/**
    	 * Does the undo!
    	 *
    	 */
    	abstract void undo();
    }
     
    /**
     * Put in context undo action
     */
    private class LRSUndoPut extends LRSUndoRecord {
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
    	 * Constructs single undo action to undo attribute setting in a Context
    	 * @param ctxt context for undo action
    	 * @param key  key to undo put value
    	 * @param value value to restore
    	 */
    	LRSUndoPut(Context ctxt, Object key, Object value){
    		this.ctxt=ctxt; this.key=key; this.value=value;
    	}
    	    	   	
    	/**
    	 * Does the undo!
    	 *
    	 */
    	void undo(){
    		if (value!=null) ctxt.put(key, value); 
    		else ctxt.remove(key);
    	}
    }
     

    /**
     * Undo on FreeVar info setting
     */
    private class LRSUndoFVInfo extends LRSUndoRecord {
    	/**
    	 * FreeVar to undo information setting on...
    	 */
    	FreeVar fv;
    	    	    	    	
    	/**
    	 * Constructs single undo action for undoing FreeVar manipulation
    	 * @param fv  FreeVar
    	 * @param type What type of undoing should we do?  Either undo type setting (g_LRSUndo_PUTFVTYPESTR) or cons eval info (g_LRSUndo_PUTFVINFO)
    	 */
    	LRSUndoFVInfo(FreeVar fv){
    		this.fv=fv; 
    	}
    	
    	/**
    	 * Does the undo!
    	 *
    	 */
    	void undo(){
    		fv.resetConsEvalInfo(); 	
    	}
    }
     

    /**
     * Undo on FreeVar type string setting
     */
    private class LRSUndoFVTypeStr extends LRSUndoRecord {
    	/**
    	 * FreeVar to undo type string setting on...
    	 */
    	FreeVar fv;
    	    	    	    	
    	/**
    	 * Constructs single undo action for undoing FreeVar manipulation
    	 * @param fv  FreeVar
    	 * @param type What type of undoing should we do?  Either undo type setting (g_LRSUndo_PUTFVTYPESTR) or cons eval info (g_LRSUndo_PUTFVINFO)
    	 */
    	LRSUndoFVTypeStr(FreeVar fv){
    		this.fv=fv; 
    	}
    	
    	/**
    	 * Does the undo!
    	 *
    	 */
    	void undo(){
    		fv.clearTyping();
    	}
    }
    
	/**
	 * Adds single undo action to current lhr for undo attribute setting in a Context
   	 * @param ctxt context for undo action
     * @param key  key to undo put value
   	 * @param value value to restore
	 */
	public void addUndoPut(Context ctxt, Object key, Object value){
		if (m_constraintsShouldUndo) currentLHRecord.addUndo(new LRSUndoPut(ctxt, key, value));
	}
		
	/**
	 * Adds single undo action to current lhr for undoing FreeVar info stetting
   	 * @param fv  FreeVar
     */
	public void addUndoFVInfo(FreeVar fv){
		currentLHRecord.addUndo(new LRSUndoFVInfo(fv));
	}	
	
	/**
	 * Adds single undo action to current lhr for undoing FreeVar type string stetting
   	 * @param fv  FreeVar
     */
	public void addUndoFVTypeStr(FreeVar fv){
		currentLHRecord.addUndo(new LRSUndoFVTypeStr(fv));
	}	
	
	/**
	 * Sets typing for FreeVar attribute in current constraint context
	 * @param attr FreeVar attribute
	 * @param types Vector of type references representing typing
	 */
	public void setTyping(String attr, Vector types){
		//Get current constraint context record
		int last_cidx = constraintEvalHistory.size()-1;
		ConstraintEvalHistoryRecord cehr = constraintEvalHistory.get(last_cidx);
		
		//Get value object for attribute
		Object val = cehr.cxt.get(attr);
		if (val instanceof FreeVar){
			//And if FreeVar...
			FreeVar fv = (FreeVar) val;
			//Set typing information...
			fv.setTyping(types);
			//need to add an undo record for the typing...
			addUndoFVTypeStr(fv);
		}
	}
	
	/**
	 * If a value to-be-set for a FreeVar is the name of an attribute in the current context and which resolves to a component description, then protocol dictates that we want to use this attribute's value...
	 * @param key Value to be provisionally used in set
	 * @return Given key unless key is an attrribute in current context which resolves to a component description, then returns value of said attribute
	 */
	public Object adjustSetValue(Object key){
		//Get current constraint context record
		int last_cidx = constraintEvalHistory.size()-1;
		ConstraintEvalHistoryRecord cehr = constraintEvalHistory.get(last_cidx);
		
		//Is the key an attribute?
		Object val = cehr.cxt.get(key);
		
		//If so, and component description then return val else return key...
	    if (val!=null && val instanceof ComponentDescription) return val;
	    else return key;
	}
	
	/**
	 * Add an assignment of an attribute within a description
	 * @param idx The appropriate link history record
	 * @param key Attribute to set 
	 * @param val Value to set
	 * @param cidx The appropriate constraint eval record
	 */
	
	public boolean addConstraintAss(ComponentDescription solve_comp, String key, Object val, int cidx) throws SmartFrogResolutionException {
		//Get constraint record pertaining to the attribute to be set...
		ConstraintEvalHistoryRecord cehr = constraintEvalHistory.get(cidx);
		
		//Get typing information for attribute to be set...
		Object cur_val = cehr.cxt.get(key);
		Vector types=null;
		if (cur_val instanceof FreeVar) types = ((FreeVar) cur_val).getTyping();
		
		//If value to be set is not of correct type then bail...
		if (types!=null && (!(val instanceof ComponentDescription) || 
				!ofTypes(solve_comp, (ComponentDescription)val, types))) return false; 
		
		//set the value prescribed
		m_constraintsShouldUndo=true;
		cehr.cxt.put(key, val);		
		//System.out.println("Setting "+key+":"+cehr.cxt.get(key)+" in "+cidx);
		m_constraintsShouldUndo=false;
		
		return true;
	}
	/** Checks that a candidate component description is of a given typing 
	 * @param solve_comp ComponentDescription pertaining to current Constraint context
	 * @param comp ComponentDescription representing candidate value for setting
	 * @param types Vector of type references representing typing
	 * @return true if of given typing, false otherwise
	 * @throws SmartFrogResolutionException
	 */
	public boolean ofTypes(ComponentDescription solve_comp, ComponentDescription comp, Vector types) throws SmartFrogResolutionException {
		Context type_cxt = null;
		//Compose the benchmark type
		try {
		   type_cxt = SFComponentDescriptionImpl.composeTypes(solve_comp, types).sfContext();
		} catch (SmartFrogResolutionException smfre){
			throw new SmartFrogResolutionException("Unable to compose types in sub-type evaluation.");
		}
		//Return if of composed benchmark type...
		return type_cxt.ofType(comp);
	}
	
	public void backtrackConstraintAss(int idx, int cidx){
		//Get constraint record pertaining to current position in constraint solving
		ConstraintEvalHistoryRecord cehr = constraintEvalHistory.get(cidx); 
		
		//Have we backtracked?
		int constraintEvalHistoryLastIdx = constraintEvalHistory.size()-1;
		if (backtrackedTo==null && cidx<constraintEvalHistoryLastIdx) backtrackedTo = cehr.cxt;
				
		//Backtrack constraint record history as appropriate...
		for (int i=constraintEvalHistoryLastIdx; i>cidx; i--) constraintEvalHistory.remove(i);
		CoreSolver.getInstance().getRootDescription().setLRSIdx(cehr.idx);
		CoreSolver.getInstance().getRootDescription().setLRSRecord(cehr.lrsr);
	
		//Backtrack histroy as approp...
		for (int i=linkHistory.size()-1; i>idx; i--) linkHistory.remove(i).undoAll();
		
		//Create new history...
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
    	cehr.lrsr = CoreSolver.getInstance().getRootDescription().getLRSRecord();
    	cehr.idx = CoreSolver.getInstance().getRootDescription().getLRSIdx();
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
