/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

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
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintResolutionState.ConstraintContext;
import org.smartfrog.sfcore.languages.sf.constraints.propositions.Proposition;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;

/**
 * Base class for constraint solver
 *
 * @author anfarr
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


    private static String solverClassname;
    private static String instanceMessage;
    private static Throwable instanceFailureCause;

    /**
     * Solver class property: {@value}
     */
    public static final String PROP_SOLVER_CLASSNAME = "org.smartfrog.sfcore.languages.sf.constraints.SolverClassName";
    private static final String ATTR_FUNCTION_CLASS_STATUS = "sfFunctionClassStatus";
    private static final String DONE = "done";
    private static final String ATTR_CONSTRAINT_AGGREGATED = "sfConstraintAggregated";
    private static final String ATTR_FUNCTION_CLASS = "sfFunctionClass";
    private static final String CONSTRAINT_FUNCTION = "org.smartfrog.sfcore.languages.sf.functions.Constraint";
    private static final String ATTR_CONSTRAINT = "sfConstraint";
    private static final String ENV_SOLVERCLASS = "SOLVERCLASS";
    private static final String ATTR_IS_GENERATOR = "sfIsArrayGenerator";
    public static final String ERROR = "ERROR: ";
    public static final String ERROR_COULD_NOT_INSTANTIATE_SOLVER = ERROR +"Could not instantiate solver ";
    public static final String ERROR_NO_CLASS = ERROR + "Class not found: ";
    public static final String ERROR_NOT_A_CORE_SOLVER = ERROR +"Not a core solver: ";
    /**
     * The classname of the eclipse solver : {@value}
     */
    public static final String ECLIPSE_SOLVER = "org.smartfrog.sfcore.languages.sf.constraints.eclipse.EclipseSolver";
    /**
     * The classname of the core solver : {@value}
     */
    public static final String CORE_SOLVER = "org.smartfrog.sfcore.languages.sf.constraints.CoreSolver";

    /**
     * Protected Constructor
     */
    protected CoreSolver() {
    }

    /**
     * Attempt to obtain an instance of the solver for the constraints.
     *
     * @return An instance of the solver class.
     *
     */
    public static synchronized CoreSolver getInstance() {
        if (solver == null) {
            //fallback state
            solver = new CoreSolver();
            //now determine the classname
            String classname = null;

            classname = System.getProperty(PROP_SOLVER_CLASSNAME);
            if (classname == null) {
                classname = System.getenv(ENV_SOLVERCLASS);
            }
            solverClassname = classname;
            if (classname != null) {
                try {
                    instanceMessage = "Using solver " + classname;
                    solverClass = SFClassLoader.forName(classname);
                    Object instance = solverClass.newInstance();
                    if (!(instance instanceof CoreSolver)) {
                        instanceMessage = ERROR_NOT_A_CORE_SOLVER + solverClass;
                    } else {
                        solver = (CoreSolver) instance;
                    }
                } catch (ClassNotFoundException e) {
                    failToInstantiate(ERROR_NO_CLASS,e);
                } catch (InstantiationException e) {
                    failToInstantiate(ERROR_COULD_NOT_INSTANTIATE_SOLVER, e);
                } catch (IllegalAccessException e) {
                    failToInstantiate(ERROR_COULD_NOT_INSTANTIATE_SOLVER, e);
                } catch (Throwable thrown) {
                    //very unexpected
                    failToInstantiate(ERROR_COULD_NOT_INSTANTIATE_SOLVER, thrown);
                }
            } else {
                instanceMessage = "No solver defined : using default solver";
            }
        }
        return solver;
    }

    /**
     * Fail to instantiate with both an error message and a thrown exception
     * @param error the error text
     * @param thrown what was thrown (required)
     */
    private static void failToInstantiate(String error, Throwable thrown) {
        instanceMessage = error + solverClassname + " : " + thrown.toString();
        instanceFailureCause = thrown;
    }

    /**
     * Entry point for testharness: reset the internal solver instance variables
     */
    public static synchronized void resetSolverInstance() {
        solverClass = null ;
        solverClassname = null;
        solver = null;
        instanceMessage = null;
        instanceFailureCause = null;
    }

    /**
     * Get the solver class  or null
     * @return the current seolver class.
     */
    public static synchronized String getSolverClassname() {
        return solverClassname;
    }

    /**
     * Gives an error message on the last attempt to create an instance of the solver.
     * @return the message or null
     */
    public static String getInstanceMessage() {
        return instanceMessage;
    }

    /**
     * Get the exception that was the cause of the last failure to create an instance
     * @return an exception or null
     */
    public static Throwable getInstanceFailureCause() {
        return instanceFailureCause;
    }

    /**
     * Resets to null relative and absolute descriptions maintained herein
     */
    public void resetDescriptionMarkers() {
        top = null;
        orig = null;
    }

    /**
     * Sets relative and absolute (absolute only if not already set) descriptions maintained herein
     */
    public void setDescriptionMarkers(SFComponentDescription mark) {
        if (orig == null) {
            orig = mark;
        }
        top = mark;
    }

    /**
     * Gets the current relative root component description in link resolution
     *
     * @return sfConfig component description
     */
    public SFComponentDescription getRootDescription() {
        return top;
    }

    /**
     * Gets the current absolute root component description in link resolution
     *
     * @return sfConfig component description
     */
    public SFComponentDescription getOriginalDescription() {
        return orig;
    }
    
    public void fail() throws Exception {}

    /**
     * Solve constraint strings pertaining to a Constraint type...
     *
     * @param cc       Pertaining Constraint Context
     * @param attrs      Attributes thereof
     * @param values     Values thereof
     * @param goal       Constraint goal to be solved
     * @param autos      Automatic variable attributes
     * @param isuservars Whether there are user variables
     * @param assigns 
     * @throws Exception
     */
    public void solve(ConstraintContext cc, Vector attrs, Vector values, Vector goal, Vector autos,
                      boolean isuservars, HashMap<FreeVar,Object> assigns) throws Exception {
    }

    /**
     * Called to indicate no more solving to be done for current sfConfig description
     */
    public void stopSolving() {
    }

    /**
     * Indicates whether operations such as "put" on Contexts should be recorded on undo stack for link resolution, as
     * may need to be backtracked as part of constraint solving
     *
     * @param undo
     */
    public void setShouldUndo(boolean undo) {
    }

    /**
     * Adds single undo action to current lhr for undo attribute setting in a Context
     *
     * @param ctxt  context for undo action
     * @param key   key to undo put value
     * @param value value to restore
     */
    public void addUndoPut(Context ctxt, Object key, Object value) {
    }

    /**
     * Adds single undo action to current lhr for undoing FreeVar info setting
     *
     * @param fv FreeVar
     */
    public void addUndoFVInfo(FreeVar fv) {
    }

    /**
     * Adds single undo action to current lhr for undoing FreeVar type string setting
     *
     * @param fv FreeVar
     */
    public void addUndoFVTypeStr(FreeVar fv) {
    }

    public void addUndoFVAutoVarEffect(FreeVar fv, Vector<Reference> autoEffects){}
    
    public void addUndoFVAutoVarEffect(FreeVar fv){}
    
    
    /**
     * On backtracking, we have backtracked to the returned context
     *
     * @return ComponentDescription to which backtracking has unwound to, null if no backtracking
     */
    public ConstraintContext hasBacktrackedTo() {
        return null;
    }

    /**
     * Reset "backtracked to" Context information
     */
    public void resetDoneBacktracking() {
    }

    /**
     * Get whether we are capable of solving constraints...
     *
     * @return whether we are capable of solving constraints...
     */
    public boolean getConstraintsPossible() {
        return false;
    }

    /**
     * Determines whether component description should be ignored in course of link resolution. So far array generators
     * should be.
     *
     * @param sfcd component description in question
     * @return whether component description should be ignored in course of link resolution
     */
    public boolean ignoreComponentDescription(SFComponentDescription sfcd) {
        return sfcd.sfContext().get(ATTR_IS_GENERATOR) != null;
    }

    public void doConstraintsWork(Object key) throws SmartFrogResolutionException {}
       
    public void addAutoVar(Object key, FreeVar var) throws SmartFrogResolutionException {}
    
    /**
     * Tidy absolute root component descriptions after constraint solving.
     *
     * @param mark Root (either relative or absolute) component description currently being resolved
     * @throws SmartFrogResolutionException on any form of trouble
     */
    public void tidyConstraintBasedDescription(SFComponentDescription mark) throws SmartFrogResolutionException {

        if (mark != getOriginalDescription()) {
            return;
        }
        //if (!Proposition.getResult()) throw new SmartFrogResolutionException("Unsatisified propositions in description");
        
        try {
            //Do a visit to every cd removing constraint annotations...
            getOriginalDescription().visit(new CDVisitor() {
                public void actOn(ComponentDescription node, Stack path) {
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
                    Object functionClassStatus = node.sfContext().get(ATTR_FUNCTION_CLASS_STATUS);
                    try {
                        if (functionClassStatus != null && functionClassStatus.equals(DONE)) {
                            node.sfContext().remove(ATTR_FUNCTION_CLASS_STATUS);

                            //FIX for agg constraint...
                            node.sfContext().remove(ATTR_CONSTRAINT_AGGREGATED);
                            Object functionClass = node.sfContext().get(ATTR_FUNCTION_CLASS);
                            node.sfContext().remove(ATTR_FUNCTION_CLASS);

                            //FIX -- Can it be any other type if in here...?
                            if (functionClass.equals(CONSTRAINT_FUNCTION)) {
                                Enumeration attr_enum = node.sfContext().keys();
                                Vector attr_keys = new Vector();
                                while (attr_enum.hasMoreElements()) {
                                    Object _key = attr_enum.nextElement();
                                    //Check not FreeVar as this should have been completed...
                                    Object _val = node.sfContext().get(_key);
                                    if (_val instanceof FreeVar) {
                                        throw new SmartFrogResolutionException("VAR(s) are left in: " + node);
                                    }
                                    if (node.sfContext().sfContainsTag(_key, ATTR_CONSTRAINT)) {
                                        attr_keys.add(_key);
                                    }
                                }
                                for (Object attr_key : attr_keys) {
                                    node.sfContext().remove(attr_key);
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }, true);
        } catch (Exception e) {
            throw new SmartFrogResolutionException(e);
        } finally {
            resetDescriptionMarkers();
            stopSolving();
        }
    }
    
    public class CoreSolverFatalError extends Error{
    	public CoreSolverFatalError(Throwable t){
    		super(t.toString());
    	}
    }

}
