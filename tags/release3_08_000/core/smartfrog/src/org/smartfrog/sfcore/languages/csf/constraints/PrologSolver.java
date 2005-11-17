package org.smartfrog.sfcore.languages.csf.constraints;

import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.languages.csf.csfcomponentdescription.CSFComponentDescription;
import org.smartfrog.sfcore.languages.csf.csfcomponentdescription.FreeVar;
import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.reference.Reference;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: pcg
 * Date: 21-Oct-2005
 * Time: 14:02:19
 * To change this template use File | Settings | File Templates.
 */
abstract public class PrologSolver extends CoreSolver {
    private final String theoryFile = "/org/smartfrog/sfcore/languages/csf/constraints/prologTheory.prolog";
    private final String theoryFileProperty = "opt.smartfrog.sfcore.languages.csf.constraints.prologTheoryFile";
    private final String varNameBase = "SFV";
    private final char referenceDelimiter = '@';
    private CSFComponentDescription top;
    private Hashtable bindings = null;
    private Hashtable initialBindings = new Hashtable();
    private Vector constraints = new Vector();
    private Vector allVariables = new Vector(); // all variables in the description, should be empty after mapping back...

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
     * @throws org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException
     *
     */
    public void solve(CSFComponentDescription cd) throws SmartFrogCompileResolutionException {
        top = cd;
        System.out.println("solving");

        // create the theory
        try {
            String filename = System.getProperty(theoryFileProperty);
            if (filename == null) filename = theoryFile;
            InputStream prologStream = SFClassLoader.getResourceAsStream(filename);
            //System.out.println(filename);
            prepareTheory(prologStream);
        } catch (Exception e) {
            throw new SmartFrogCompileResolutionException("unable to parse base theory for constraint resolution", e);
        }

        // collect and process the constraints
        try {
            collectConstraints();
        } catch (Exception e) {
            throw new SmartFrogCompileResolutionException("Error collectiong constraints during contraint resolution", e);
        }

        // solve the constraints
        bindings = solveConstraints(initialBindings);

        if (bindings == null) {
            throw new SmartFrogCompileResolutionException("Constraint resolution failed - probable inconsistency in constraints");
        }

        // map values back
        try {
            mapBindings();
        } catch (Exception e) {
            throw new SmartFrogCompileResolutionException("Error updating description with variable bindings during constraint resolution", e);
        }
    }

    /**
     * Process the references in the constraint - delimited by the solver chaarcter - looking up the value in the hierarchy starting from the component
     * to which the constraint is attached. Also deal with character escapes...
     */
    private String processReferences(ComponentDescription cd,
                                     String pString) throws SmartFrogCompileResolutionException {
        int index = 0;
        int length = pString.length();
        char refDel = referenceDelimiter();
        char theChar;
        //System.out.println("processing string " + pString);
        StringBuffer fixed = new StringBuffer(length);

        while (index < length) {
            theChar = pString.charAt(index++);
            if (theChar == refDel) {
                if (index >= length) throw new SmartFrogCompileResolutionException("reference not terminated" + fixed + "... on component " + cd.sfCompleteName());

                theChar = pString.charAt(index++);
                if (theChar == refDel) { // we don't have a reference
                    fixed.append(theChar);
                } else {// we have the start of a reference
                    fixed.append(' ');

                    Reference ref = null;
                    try {//extract the reference
                        StringBuffer refString = new StringBuffer();
                        while (theChar != refDel) {
                            refString.append(theChar);
                            if (index >= length) throw new SmartFrogCompileResolutionException("reference not terminated " + fixed + "... on component " + cd.sfCompleteName());
                            theChar = pString.charAt(index++);
                        }
                        ref = Reference.fromString(refString.toString());
                        //System.out.println("ref is " + ref);
                    } catch (SmartFrogResolutionException e) {
                        throw new SmartFrogCompileResolutionException("unable to build reference at " + fixed.toString());
                    }

                    Object o = null;
                    try {
                        o = cd.sfResolve(ref);
                        //System.out.println("found " + o);
                    } catch (SmartFrogResolutionException e) {
                        throw new SmartFrogCompileResolutionException("unable to resolve reference " + ref.toString() + " on component " + cd.sfCompleteName());
                    }

                    if (o instanceof FreeVar) {
                        fixed.append(freeVariableName((FreeVar) o));
                    } else {
                        FreeVar fv = new FreeVar();
                        String fvname = freeVariableName(fv);
                        fixed.append(fvname);
                        initialBindings.put(fvname, mapValueIn(o));
                    }

                    fixed.append(' ');
                }
            } else {
                fixed.append(theChar);
            }
        }
        return fixed.toString();
    }

    private void collectConstraints() throws Exception {
        top.visit(new ConstraintCollector(), false);
    }

    private Hashtable solveConstraints(Hashtable bindings) throws SmartFrogCompileResolutionException {
        StringBuffer totalConstraint = new StringBuffer();

        for (Enumeration b = bindings.keys(); b.hasMoreElements();) {
            String name = (String) b.nextElement();
            totalConstraint.append(name);
            totalConstraint.append("=");
            totalConstraint.append(bindings.get(name).toString());
            totalConstraint.append(",");
        }

        for (Enumeration e = constraints.elements(); e.hasMoreElements();) {
            String query = ((Constraint) e.nextElement()).getQuery();
            totalConstraint.append(query);
            totalConstraint.append(", ");
        }
        totalConstraint.append(" SFVRESULT = [");
        for (Enumeration n = allVariables.elements(); n.hasMoreElements();) {
            String fv = freeVariableName((FreeVar) n.nextElement());
            totalConstraint.append(fv);
            totalConstraint.append(",");
        }
        totalConstraint.append("'done'].");
        Hashtable results = solveQuery(totalConstraint, bindings);
        if (results == null)
            throw new SmartFrogCompileResolutionException("No solution found to constraints");
        else {
            Vector resultVector = (Vector) results.get("SFVRESULT");
            //System.out.println(results);
            //System.out.println(resultVector);
            //System.out.println(allVariables);
            results = new Hashtable();
            for (int k = 0; k < allVariables.size(); k++) {
                results.put(freeVariableName((FreeVar)allVariables.elementAt(k)), resultVector.elementAt(k));
            }
            //System.out.println(results);
            return results;
        }
    }

    private void mapBindings() throws Exception {
        allVariables = new Vector();
        top.visit(new BindingMapper(), false);
        if (allVariables.size() > 0) {
            throw new SmartFrogCompileResolutionException("Unbound variable(s) in attribute(s) " + allVariables);
        }
    }

    /**
     * Method to convert an SF value to an object suitable for the solver
     * This should be the inverse of mapValueOut
     *
     * @param v the value to convert
     * @return the converted value
     */
    public Object mapValueIn(Object v) throws SmartFrogCompileResolutionException {
        if (v instanceof Number) {
            return v;
        } else if (v instanceof Boolean) {
            return v;
        } else if (v instanceof String) {
            return "'" + v + "'";
        } else if (v instanceof SFNull) {
            return "'sfnull'";
        } else if (v instanceof Vector) {
            Vector result = new Vector();
            for (Enumeration e = ((Vector) v).elements(); e.hasMoreElements();) {
                result.add(mapValueIn(e.nextElement()));
            }
            return result;
        } else if (v instanceof FreeVar) {
            return varNameBase + ((FreeVar) v).getId();
        } else {
            throw new SmartFrogCompileResolutionException("unable to handle SF data in constraint: " + v);
        }
    }

    /**
     * Method to convert an object returned by the solver into one suited for SF
     * This should be the inverse of mapValueIn
     *
     * @param v the value to convert
     * @return the converted value
     */
    public Object mapValueOut(Object v) throws SmartFrogCompileResolutionException {
        if (v instanceof Number) {
            return v;
        } else if (v instanceof Boolean) {
            return v;
        } else if (v instanceof Vector) {
            Vector result = new Vector();
            for (Enumeration e = ((Vector) v).elements(); e.hasMoreElements();) {
                result.add(mapValueOut(e.nextElement()));
            }
            return result;
        } else if (v instanceof String) {
            String vs = (String) v;
            if (vs.equals("'sfnull'")) {
                return SFNull.get();
            } else if ((vs.charAt(0) == '\'') & (vs.charAt(vs.length() - 1) == '\'')) {
                return vs.substring(1, vs.length() - 1);
            } else if (vs.startsWith(varNameBase)) {
                String ind = vs.substring(varNameBase.length(), vs.length());
                int index = new Integer(ind).intValue();
                return new FreeVar(index);
            } else {
                throw new SmartFrogCompileResolutionException("unknown data returned from solver " + v);
            }
        } else {
            throw new SmartFrogCompileResolutionException("unknown data returned from solver " + v);
        }
    }

    private String freeVariableName(FreeVar v) {
        return varNameBase + v.getId();
    }

    /**
     * Return the reference delimiter for the query and theory strings. Two consecutive characters are used to escape
     * the character.
     *
     * @return '@' as the delimiter
     */
    private char referenceDelimiter() {
        return referenceDelimiter;
    }

    // handle all the constraints, and whilst about it, collect details of all the variables
    private class ConstraintCollector implements CDVisitor {
        public void actOn(ComponentDescription cd, Stack s) throws SmartFrogException {
            // collect the constraints
            Vector cs = ((CSFComponentDescription) cd).getConstraints();
            for (Enumeration e = cs.elements(); e.hasMoreElements();) {
                Constraint c = (Constraint) e.nextElement();
                c.setQuery(processReferences(cd, c.getQuery()));
                c.setComponent(cd);
                constraints.add(c);
            }
            //collect the variables that require binding
            for (Iterator i = cd.sfValues(); i.hasNext();) {
                Object value = i.next();
                if (value instanceof Vector) {
                    findVarsInVector((Vector) value);
                } else if (value instanceof FreeVar) {
                    allVariables.add(value);
                }
            }
        }

        private void findVarsInVector(Vector value) {
            for (Enumeration e = value.elements(); e.hasMoreElements();) {
                Object n = e.nextElement();
                if (n instanceof FreeVar) {
                    allVariables.add(n);
                } else if (n instanceof Vector) {
                    findVarsInVector((Vector) n);
                }
            }
        }
    }

    private class BindingMapper implements CDVisitor {
        public void actOn(ComponentDescription cd, Stack s) throws Exception {
            for (Iterator i = cd.sfAttributes(); i.hasNext();) {
                Object key = i.next();
                Object value = cd.sfResolveHere(key);
                if (value instanceof FreeVar) {
                    String freevar = freeVariableName((FreeVar) value);
                    Object boundValue = bindings.get(freevar);
                    if (boundValue == null) {
                        allVariables.add(cd.sfCompleteName().toString() + ":" + key);
                    } else {
                        cd.sfReplaceAttribute(key, mapValueOut(boundValue));
                    }
                } else if (value instanceof Vector) {
                    replaceVarsInVector((Vector)value, cd, key);
                }
            }
        }

        private void replaceVarsInVector(Vector value, ComponentDescription cd, Object key) {
            for (int i = 0; i< value.size(); i++) {
                Object n = value.elementAt(i);
                if (n instanceof FreeVar) {
                    //System.out.println(n + " " + value + " " + bindings);
                    Object b = bindings.get(freeVariableName((FreeVar)n));
                    //System.out.println(b);
                    if (b == null) {
                        allVariables.add(cd.sfCompleteName().toString() + ":" + key);
                    } else {
                        value.set(i,b);
                    }
                } else if (n instanceof Vector) {
                    replaceVarsInVector((Vector) n, cd, key);
                }
            }
        }
    }

    abstract public void prepareTheory(InputStream prologStream) throws IOException ;


    abstract public Hashtable solveQuery(StringBuffer totalConstraint, Hashtable bindings);
}
