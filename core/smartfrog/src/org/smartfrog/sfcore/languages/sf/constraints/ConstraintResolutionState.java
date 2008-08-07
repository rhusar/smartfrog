package org.smartfrog.sfcore.languages.sf.constraints;

import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl.LRSRecord;

public class ConstraintResolutionState {
  /**
   * Records link resolution history.  Concerned with maintaining undo
   * information when constraints are used
   */
  private Vector linkHistory = new Vector();

  /**
   * Maintains a history of Constraint goals that have been (successfully)
   * evaluated
   */
  private Vector constraintEvalHistory = new Vector();

  /**
   * Indicates current link history record being used.
   */
  private LinkHistoryRecord currentLHRecord;

  /**
   * Used to maintain whether backtracking -- as part of constraint solving --
   * has just occurred in processing current attribute.
   */
  private Context backtrackedTo = null;

  /**
   * Gets whether backtracking has occurred recently
   *
   * @return backtracking flag
   */
  public Context hasBacktrackedTo() {
    return backtrackedTo;
  }

  /**
   * Resets flag wrt backtracking
   */
  public void resetDoneBacktracking() {
    backtrackedTo = null;
  }

  /**
   * Indicates whether manipulations of context attributes should currently
   * have undo records created for them
   */
  private static boolean g_constraintsShouldUndo = false;

  /**
   * Sets whether manipulations of context attributes should currently have
   * undo records created for them
   *
   * @param shouldUndo
   */
  public static void setConstraintsShouldUndo(boolean shouldUndo) {
    g_constraintsShouldUndo = shouldUndo;
  }

  /**
   * Constructs ConstraintResolutionState, allocating a link history record
   */
  ConstraintResolutionState() {
    currentLHRecord = new LinkHistoryRecord();
    linkHistory.add(currentLHRecord);
  }

  /**
   * Maintains a record pertaining to a single constraint evaluation
   *
   * @author anfarr
   */
  private class ConstraintEvalHistoryRecord {
    /**
     * Indicates the current component description being processed
     */
    LRSRecord lrsr;
    /**
     * Indicates the current attribute being processed, triggering constraint
     * evaluation
     */
    int idx;
    /**
     * The context of the component description
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
    Vector undo_stack = new Vector();

    /**
     * Add a single undo record
     *
     * @param lrsu
     */
    void addUndo(LRSUndoRecord lrsu) {
      undo_stack.add(lrsu);
    }

    /**
     * Undo all actions recorded herein
     */
    void undoAll() {
      for (int i = undo_stack.size() - 1; i >= 0; i--) {
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
  public static final int g_LRSUndo_PUTFVINFO = 0x1;

  /**
   * Undo action: put, for putting typing info into FreeVars
   */
  public static final int g_LRSUndo_PUTFVTYPESTR = 0x2;


  /**
   * Maintains single undo actions
   */
  private class LRSUndoRecord {
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

    /**
     * Constructs single undo action to undo attribute setting in a Context
     *
     * @param ctxt  context for undo action
     * @param key   key to undo put value
     * @param value value to restore
     */
    LRSUndoRecord(Context ctxt, Object key, Object value) {
      this.type = g_LRSUndo_PUT;
      this.ctxt = ctxt;
      this.key = key;
      this.value = value;
    }

    /**
     * Constructs single undo action for undoing FreeVar manipulation
     *
     * @param fv   FreeVar
     * @param type What type of undoing should we do?  Either undo type setting
     *             (g_LRSUndo_PUTFVTYPESTR) or cons eval info
     *             (g_LRSUndo_PUTFVINFO)
     */
    LRSUndoRecord(FreeVar fv, int type) {
      this.type = type;
      this.fv = fv;
    }

    /**
     * Does the undo!
     */
    void undo() {
      switch (type) {
        case g_LRSUndo_PUT:
          if (value != null) {
            ctxt.put(key, value);
          } else {
            ctxt.remove(key);
          }
          break;
        case g_LRSUndo_PUTFVINFO:
          fv.resetConsEvalInfo();
          break;
        case g_LRSUndo_PUTFVTYPESTR:
          fv.clearTyping();
          break;
      }
    }
  }

  /**
   * Adds single undo action to current lhr for undo attribute setting in a
   * Context
   *
   * @param ctxt  context for undo action
   * @param key   key to undo put value
   * @param value value to restore
   */
  public void addUndo(Context ctxt, Object key, Object value) {
    currentLHRecord.addUndo(new LRSUndoRecord(ctxt, key, value));
  }

  /**
   * Adds single undo action to current lhr for undoing FreeVar manipulation
   *
   * @param fv   FreeVar
   * @param type What type of undoing should we do?  Either undo type setting
   *             (g_LRSUndo_PUTFVTYPESTR) or cons eval info (g_LRSUndo_PUTFVINFO)
   */
  public void addUndo(FreeVar fv, int type) {
    currentLHRecord.addUndo(new LRSUndoRecord(fv, type));
  }

  public void setTyping(String attr, Vector types) {
    int last_cidx = constraintEvalHistory.size() - 1;
    ConstraintEvalHistoryRecord cehr
            = (ConstraintEvalHistoryRecord) constraintEvalHistory
            .get(last_cidx);
    Object val = cehr.cxt.get(attr);
    if (val instanceof FreeVar) {
      FreeVar fv = (FreeVar) val;
      fv.setTyping(types);
      //need to add an undo record for the typing...
      addUndo(fv, g_LRSUndo_PUTFVTYPESTR);
    }
  }

  public Object adjustSetValue(Object key) {
    int last_cidx = constraintEvalHistory.size() - 1;
    ConstraintEvalHistoryRecord cehr
            = (ConstraintEvalHistoryRecord) constraintEvalHistory
            .get(last_cidx);
    Object val = cehr.cxt.get(key);
    if (val != null && val instanceof ComponentDescription) {
      return val;
    } else {
      return key;
    }
  }

  /**
   * Add an assingment of an attribute within a description
   *
   * @param idx  The appropriate link history record
   * @param key  Attribute to set
   * @param val  Value to set
   * @param cidx The appropriate constraint eval record
   */

  public boolean addConstraintAss(ComponentDescription solve_comp, int idx,
                                  String key, Object val, int cidx)
          throws SmartFrogResolutionException {
    ConstraintEvalHistoryRecord cehr
            = (ConstraintEvalHistoryRecord) constraintEvalHistory.get(cidx);

    //Get typing information...
    Object cur_val = cehr.cxt.get(key);
    Vector types = null;
    if (cur_val instanceof FreeVar) types = ((FreeVar) cur_val).getTyping();

    if (types != null && (!(val instanceof ComponentDescription) ||
            !ofTypes(solve_comp, (ComponentDescription) val, types))) {
      return false;
    }

    //set the value prescribed
    g_constraintsShouldUndo = true;
    cehr.cxt.put(key, val);
    //System.out.println("Setting "+key+":"+cehr.cxt.get(key)+" in "+cidx);
    g_constraintsShouldUndo = false;

    return true;
  }

  public boolean ofTypes(ComponentDescription solve_comp,
                         ComponentDescription comp, Vector types)
          throws SmartFrogResolutionException {
    Context type_cxt = null;
    try {
      type_cxt = SFComponentDescriptionImpl.composeTypes(solve_comp, types)
              .sfContext();
    } catch (SmartFrogResolutionException smfre) {
      throw new SmartFrogResolutionException(
              "Unable to compose types in sub-type evaluation.");
    }
    return type_cxt.ofType(comp);
  }

  public void backtrackConstraintAss(int idx, int cidx) {
    ConstraintEvalHistoryRecord cehr
            = (ConstraintEvalHistoryRecord) constraintEvalHistory.get(cidx);

    //need to backtrack cidx...
    int constraintEvalHistoryLastIdx = constraintEvalHistory.size() - 1;

    if (backtrackedTo == null && cidx < constraintEvalHistoryLastIdx) {
      backtrackedTo = cehr.cxt;
    }

    for (int i = constraintEvalHistoryLastIdx; i > cidx; i--) {
      constraintEvalHistory.remove(i);
    }
    CoreSolver.getInstance().getRootDescription().setLRSIdx(cehr.idx);
    CoreSolver.getInstance().getRootDescription().setLRSRecord(cehr.lrsr);

    //need to backtrack histroy as approp...
    for (int i = linkHistory.size() - 1; i > idx; i--) {
      LinkHistoryRecord lhr = (LinkHistoryRecord) linkHistory.remove(i);
      lhr.undoAll();
    }

    //create new history...
    currentLHRecord = new LinkHistoryRecord();
    linkHistory.add(currentLHRecord);
  }

  /**
   * Add a record to cons eval history
   *
   * @param cxt Given context
   * @return Latest record index
   */
  public int addConstraintEval(Context cxt) {
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
   *
   * @return latest index
   */
  public int getConsEvalIdx(){
    	return constraintEvalHistory.size()-1;
    }

}
