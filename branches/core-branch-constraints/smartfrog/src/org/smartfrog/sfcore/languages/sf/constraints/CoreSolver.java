package org.smartfrog.sfcore.languages.sf.constraints;

import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.LinkResolutionState;
import org.smartfrog.sfcore.security.SFClassLoader;

abstract public class CoreSolver {
    private static Class solverClass = null;
    private static CoreSolver solver;
    protected Vector constraints;
    protected ComponentDescription top;

    
    /**
     * Attempt to obtain an instance of the solver for the constraints. 
     *
     * @return An instance of the solver class
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *
     */
    
    public static CoreSolver getInstance(){
        try {           	
        	if (solver==null){
                String classname = System.getProperty("org.smartfrog.sfcore.languages.sf.constraints.SolverClassName");
                if (classname == null) return null;
                solverClass = SFClassLoader.forName(classname);
                solver = (CoreSolver) solverClass.newInstance();
                solver.prepareSolver();
        	}
        } catch (Exception e) {}
        return solver;
    }
     
    public void setRootDescription(ComponentDescription top){ this.top = top; }
    
    abstract void prepareSolver() throws SmartFrogResolutionException;
    abstract public void solve(ComponentDescription comp, Vector attrs, Vector values, Vector logic, Vector goal, Vector autos, boolean isuservars)  throws Exception;
    abstract public void stopSolving() throws Exception;
    abstract public void prepareTheory(ComponentDescription cd, String coreFile, String prologFile) throws Exception;
    abstract public void runGoal(String goal) throws Exception;
}
