package org.smartfrog.sfcore.languages.csf.constraints;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.csf.csfcomponentdescription.CSFComponentDescription;
import org.smartfrog.sfcore.languages.csf.csfcomponentdescription.FreeVar;

abstract public class PrologSolver extends CoreSolver {
	private final String pathswitch = "/../constraints/";
	private final String coreFileSuffix = "core.ecl";
    private final String theoryFileSuffix = "base.ecl";
    private final String theoryFilePath = "opt.smartfrog.sfcore.languages.csf.constraints.theoryFilePath";
    private CSFComponentDescription top;
    private Vector constraints = new Vector();

    /**
     * Implemention of the solver interface method., Solve the constraints and bind the variables.
     * <p/>
     * 1) Provides initial processing of query and theory strings to identify references into the component description hierarchy,
     * and resolves them in place.
     * <p/>
     * 2) Invokes an abstract method SolveBindings to evaluate the bindings for the free variables
     * <p/>
     * 3) Maps the variable bindings back into the components descritpions, ensuring that all variables are now bound.
     * <p/>
     *
     * @param cd the component description at the root of the tree
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *
     */
    public void solve(CSFComponentDescription cd) throws SmartFrogResolutionException {
        top = cd;

        String sfhome = SFSystem.getEnv("SFHOME");
        
        if (sfhome==null){
    		throw new SmartFrogResolutionException("Environment variable SFHOME must be set. Context: constraint processing");
    	}
                
        String corefile = sfhome+pathswitch+coreFileSuffix;
        String thfile = sfhome+pathswitch+theoryFileSuffix;
       
        // create the theory
        try {
            prepareTheory(cd,corefile,thfile);
        } catch (Exception e) {
            throw new SmartFrogResolutionException("Unable to parse base theory for constraint resolution. ", e);
        }

        //Add the path root
        String thpath = System.getProperty(theoryFilePath);
        
        if (thpath!=null){
        	try {
        		runGoal("add_path(\""+thpath+"\")");
        	} catch (Exception e) {
                throw new SmartFrogResolutionException("Unable to add root theory file path. ", e);
            }	
        }
        
        // collect and process the constraints
        try {
            collectConstraints();
        } catch (Exception e) {
            throw new SmartFrogResolutionException("Error collecting constraints during constraint resolution", e);
        }

        if (constraints.size()!=0) {       
		    // solve the constraints
		    try {
			    solveConstraints();
		    } catch (Exception e) {
			throw new SmartFrogResolutionException("Error in solving constraints", e);
		    }	
	    }

 		try {
	    	destroy();
	    } catch (Exception e){
	    	throw new SmartFrogResolutionException("Problem with destroying constraint solver", e);
	    }
    }


    private void collectConstraints() throws Exception {
        top.visit(new ConstraintCollector(), false);
 
        //Need to adjust search priorities
		int highest=0;
		for (int i=0;i<constraints.size();i++){
			Constraint c = (Constraint) constraints.get(i);
		    int priority = c.getPriority();
		    if (priority>highest) highest=priority;
        	String context = c.getComponent().sfCompleteName().toString();
        	c.setQuery(ann_preprocess(c.getQuery(), remove_ref_prefix(context)));
		}
		for (int i=0;i<constraints.size();i++){
			Constraint constraint = (Constraint) constraints.get(i);
		    int priority = constraint.getPriority();
		    if (constraint.isDoCons()) {
			constraint.setPriority(priority + highest + 1); 
		    }
		}
		Collections.sort(constraints);
		//System.out.println("ordered constraints " + constraints);
    }
    
    protected String remove_ref_prefix(String ref){
    	String ref1 = "";
    	int idx = ref.indexOf(":");
    	if (idx!=-1) ref1 = ref.substring(idx+1, ref.length());
    	return ref1;
    }
    
    protected String create_ref_str(ComponentDescription cd){
    	String ref = cd.sfCompleteName().toString();
    	String ref1 = remove_ref_prefix(ref);
    	if (ref1.compareTo("")==0) return ref1;
    	else return ref1+":";
    }

    private void solveConstraints() throws Exception {
        StringBuffer totalConstraint = new StringBuffer();

        totalConstraint.append("hash_create(sfvar(0)), ");
        boolean first=true;
        for (Enumeration e = constraints.elements(); e.hasMoreElements();) {
        	if (first) first=false;
        	else totalConstraint.append(", ");
        	Constraint c = (Constraint) e.nextElement();
        	totalConstraint.append(c.getQuery());
        }
        totalConstraint.append(", sfuser(sfvar(0))");
        //Preprocess agg constraint goal
	    String goal=agg_preprocess(totalConstraint.toString());
         
	    //System.out.println(goal);
        runGoal(goal);
        
        
        //Retrieve FreeVar mappings...
        top.visit(new BindingMapper(), false);
    }
 
    // handle all the constraints, and whilst about it, collect details of all the variables
    private class ConstraintCollector implements CDVisitor {
        public void actOn(ComponentDescription cd, Stack s) throws SmartFrogException {
            // collect the constraints
            Vector cs = ((CSFComponentDescription) cd).getConstraints();
            for (Enumeration e = cs.elements(); e.hasMoreElements();) {
                Constraint c = (Constraint) e.nextElement();
                c.setComponent(cd);
                constraints.add(c);
            }
        }
     }
       
    private class BindingMapper implements CDVisitor {
        public void actOn(ComponentDescription cd, Stack s) throws Exception {
            for (Iterator i = cd.sfAttributes(); i.hasNext();) {
                Object key = i.next();
                Object value = cd.sfResolveHere(key);
                if (value instanceof FreeVar) {
                	FreeVar fv = (FreeVar) value;
                	Object data = fv.getProvData();
                	if (data==null) {
                		throw new SmartFrogResolutionException("Unbound variables after constraint solving.");
                	}
                	else cd.sfReplaceAttribute(key, data);
                } else if (value instanceof Vector) {
                    replaceVarsInVector((Vector)value, cd, key);
                }
            }
        }

        private void replaceVarsInVector(Vector vec, ComponentDescription cd, Object key) throws Exception {
            for (int i = 0; i< vec.size(); i++) {
                Object value = vec.elementAt(i);
                if (value==null) throw new SmartFrogResolutionException("Unbound variables after constraint solving.");
                else if (value instanceof FreeVar) {
                	FreeVar fv = (FreeVar) value;
                	Object data = fv.getProvData();
                	if (data==null) {
               		throw new SmartFrogResolutionException("Unbound variables after constraint solving.");
                	}
                	else vec.set(i, data);
                } else if (value instanceof Vector) {
                    replaceVarsInVector((Vector)value, cd, key);
                }
            }
        }
    }
    
    abstract public void prepareTheory(ComponentDescription cd, String coreFile, String prologFile) throws Exception;
    abstract public void runGoal(String goal) throws Exception;
    abstract public String ann_preprocess(String goal, String context) throws Exception;
    abstract public String agg_preprocess(String goal) throws Exception;
    abstract public void destroy() throws Exception;
}
