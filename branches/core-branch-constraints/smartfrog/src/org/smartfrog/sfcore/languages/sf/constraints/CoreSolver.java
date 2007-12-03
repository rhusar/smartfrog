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
     
    /**
     * Sets the component description pertaining to sfConfig 
     * @param top
     */
    public void setRootDescription(ComponentDescription top){ this.top = top; }
    
    /**
     * Prepare solver...
     * @throws SmartFrogResolutionException
     */
    abstract void prepareSolver() throws SmartFrogResolutionException;
    
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
    abstract public void solve(ComponentDescription comp, Vector attrs, Vector values, Vector goal, Vector autos, boolean isuservars)  throws Exception;
    
    /**
     * Indicates no more solving to be done for current sfConfig description
     * @throws Exception
     */
    abstract public void stopSolving() throws Exception;        
}
