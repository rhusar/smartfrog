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
package org.smartfrog.sfcore.languages.csf.csfcomponentdescription;

import org.smartfrog.sfcore.languages.csf.constraints.Constraint;
import org.smartfrog.sfcore.languages.csf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.csf.constraints.Solver;
import org.smartfrog.sfcore.languages.sf.Phase;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.common.*;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Defines the context class used by Components. Context implementations
 * need to respect the ordering and copying requirements imposed by
 * Components.
 */
public class CSFComponentDescriptionImpl extends SFComponentDescriptionImpl
        implements Serializable, Cloneable, CSFComponentDescription, MessageKeys {


    /**
     * The list of constraints associated with this component description
     */
    protected Vector constraints = new Vector();


    /**
     * Constuctor.
     *
     * @param types   supertypes for component
     * @param parent parent component
     * @param cxt    context for description
     * @param eager  eager flag
     */
    public CSFComponentDescriptionImpl(Vector types, CSFComponentDescription parent, Context cxt, boolean eager) {
        super(types, parent, cxt, eager);
    }


    /**
     * Set new coonstraints for this component.
     *
     * @param constraints new constraints for description
     * @return the previous constraints
     */
    public Vector setConstraints(Vector constraints) {
       this.constraints = constraints;
       return constraints;
    }

    /**
     * Return coonstraints for this component.
     *
     * @return the vector of constraints
     */
    public Vector getConstraints() {
        return constraints; 
    }

    /**
     * Creates a deep copy of the compiled component. Parent, type and eager
     * flag are the same in the copy. Resolvers are blanked in the copy to avoid
     * resolution confusion. Resolution data object reference is copied if it
     * implements the Copying interface, otherwise the pointer is shared with
     * the copy.
     *
     * @return copy of component
     */
    public Object copy() {
        CSFComponentDescription res = (CSFComponentDescription) clone();
        res.setTypes((Vector) types.clone());
        res.setContext((Context) sfContext.copy());
        res.setParent(parent);
        res.setEager(eager);

        Vector copiedConstraints = new Vector();
        for (Enumeration e = constraints.elements(); e.hasMoreElements();) {
            Constraint c = (Constraint) e.nextElement();
            copiedConstraints.add(c.copy());
        }
        res.setConstraints(copiedConstraints);

        for (Enumeration e = sfContext.keys(); e.hasMoreElements();) {
            Object value = res.sfContext().get(e.nextElement());

            if (value instanceof CSFComponentDescription) {
                ((CSFComponentDescription) value).setParent(res);
            }
        }
        return res;
    }

    /**
     * Internal method that constraint resolves a parsed component.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *          failed to type resolve
     */
    public void constraintResolve() throws SmartFrogResolutionException {
        // if solver class - instance.solve(this);
        Solver solver = CoreSolver.solver();
        solver.solve(this);
    }

    /**
     * Public method to carry out specific resolution actions as defined by the
     * phases provided.
     *
     * @param phaseList a vector of strings defining the names of the
     *               phases
     * @return the resultant Phases object, ready for the
     *         next phase action or convertion into the core ComponentDescription
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *          In case of SmartFrog system error
     */
    public Phases sfResolvePhases(Vector phaseList)
            throws SmartFrogException {
        CSFComponentDescription actOn = this;

        for (Enumeration e = phaseList.elements(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            try {
                if (name.equals("type")) {
                    actOn.typeResolve();
                } else if (name.equals("place")) {
                    actOn.placeResolve();
                } else if (name.equals("sfConfig")) {
                    actOn = (CSFComponentDescription) sfResolve(sfConfigRef);
                } else if (name.equals("link")) {
                    actOn.linkResolve();
                } else if (name.equals("constraint")) {
                    actOn.constraintResolve();
                } else if (name.equals("print")) {
                    //org.smartfrog.sfcore.common.Logger.log(actOn.toString());
                    org.smartfrog.SFSystem.sfLog().out(actOn.toString());
                } else {
                    actOn.visit(new Phase(name), false);
                }
            } catch (Throwable thr) {
                throw SmartFrogResolutionException.forward(name, thr);
            }

        }

        return actOn;
    }

    /**
     * Public method to get the set of phases defined in the component. This
     * will either be the phaseList attribute, or the standard set defined as
     * though the phaseList attribute had been defined phaseList ["type",
     * "place", "sfConfig", "link", "function"]; The attribute is removed to
     * tidy the definition, but the result is cached for later use
     *
     * @return Vector of Phases
     */
    public Vector sfGetPhases() {
        if (phases == null) {
            phases = (Vector) sfContext.get("phaseList");

            if (phases == null) {
                phases = new Vector();
                phases.add("type");
                phases.add("place");
                phases.add("function");
                phases.add("sfConfig");
                phases.add("link");
                phases.add("constraint");
            } else {
                sfContext.remove("phaseList");
            }
        }

        return phases;
    }

    /**
     * Add a constraint to this component description
     */
    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }

    /**
     * Overrides method in SFCOmponentDescriptionImpl
     * Adds copying of constraints
     *
     * @param superType super type to copy from
     */
    protected void subtype(SFComponentDescription superType) throws SmartFrogTypeResolutionException {
        super.subtype(superType);
        for (Enumeration e = ((CSFComponentDescription) superType).getConstraints().elements(); e.hasMoreElements();) {
            Constraint c = (Constraint) e.nextElement();
            constraints.add(c.copy());
        }
    }

    /**
     * Writes this component description on a writer. Used by toString. Should
     * be used instead of toString to write large descriptions to file, since
     * memory can become a problem given the LONG strings created
     *
     * @param ps     writer to write on
     * @param indent the indent to use for printing offset
     * @throws IOException failure while writing
     */
    public void writeOn(Writer ps, int indent) throws IOException {
        super.writeOn(ps, indent);
        for (Enumeration e = constraints.elements(); e.hasMoreElements();) {
            ps.write((e.nextElement()).toString());
        }
    }
}
