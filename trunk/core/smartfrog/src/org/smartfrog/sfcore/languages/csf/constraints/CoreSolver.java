package org.smartfrog.sfcore.languages.csf.constraints;

import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;
import org.smartfrog.sfcore.security.SFClassLoader;

/**
 * Created by IntelliJ IDEA.
 * User: pcg
 * Date: 17-Oct-2005
 * Time: 15:48:22
 * To change this template use File | Settings | File Templates.
 */
abstract public class CoreSolver implements Solver {
    private static String solverClassname = "org.smartfrog.sfcore.languages.csf.constraints.NullSolver";

    private static Class solverClass = null;
    private static Solver solver = null;

    /**
     * Obtain an instance of the solver for the constraints. This is either an instance of
     * the class defined in the property "org.smartfrog.sfcore.languages.csf.consrtaints.SolverClassName"
     * or, if not defined, the default NullSolver that simply checks that there are no free variables.
     *
     * @return An instance of the solver class
     * @throws org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException
     *
     */
    public static Solver solver() throws SmartFrogCompileResolutionException {
        if (solver == null) {
            try {
                String classname = System.getProperty("org.smartfrog.sfcore.languages.csf.consrtaints.SolverClassName");
                if (classname != null) solverClassname = classname;
                solverClass = SFClassLoader.forName(solverClassname);
                solver = (Solver) solverClass.newInstance();
            } catch (Exception e) {
                throw new SmartFrogCompileResolutionException("Unable to construct constraint solver", e);
            }
        }
        return solver;
    }

}
