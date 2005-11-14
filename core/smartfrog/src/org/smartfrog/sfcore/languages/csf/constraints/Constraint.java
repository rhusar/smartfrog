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

package org.smartfrog.sfcore.languages.csf.constraints;


import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.Copying;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.util.Hashtable;


/**
 * Defines the basic Constraint implementation.
 * The class is basically a holder for the query string (#suchThat#...#), the theory string (#where#...#) and the
 * component to which the constraint applies. In addition, the constraint can hold arbitrary data (an Object) to represent
 * state the solver may require to associate with this specific constraint.
 * <p/>
 * It also contains the logic for deciding which solver should be used - this is static and is common to all
 * contraints. It is not possible to use a different solver for each constraint.
 */
public class Constraint implements Copying {


    private String query = null;
    private Object solverState = null;
    private ComponentDescription cd = null;
    private Hashtable bindings;

    /**
     * the Name of the component
     */
    protected Reference name;

    /**
     * Constructor for a Constraint, passing the query string, the theory string and the component to which it is
     * bound. Also has a number of useful get/set methods for the solvers to store state associated with the constraint:
     *    a hastable of bindings of variables defininig the context for the solver
     *    the component description on which this conatrsint is deifned
     *    an object representing any other solver specific state
     *
     * @param query  the query string  #suchThat#...#
     */
    public Constraint(String query) {
        setQuery(query);
    }


    public String toString() {
        return new StringBuffer().append(" #suchThat#").append(query).append("#").toString();
    }

    /**
     * Get the solver state associated with this constraint
     *
     * @return the state
     */
    public Object getSolverState() {
        return solverState;
    }

    /**
     * Set the solver state for this constraint
     *
     * @param s the state
     */
    public void setSolverState(Object s) {
        solverState = s;
    }

    /**
     * Get the ComponentDescription - state used by the solvers
     *
     * @return the ComponentDescription
     */
    public ComponentDescription getComponent() {
        return cd;
    }

    /**
     * Get the ComponentDescription - state used by the solvers
     *
     * @param cd the ComponentDescription
     */
    public void setComponent(ComponentDescription cd) {
        this.cd = cd;
    }

    /**
     * Get the bindings - state used by the solvers
     *
     * @return the ComponentDescription
     */
    public Hashtable getBindings() {
        return bindings;
    }

    /**
     * Get the bindings - state used by the solvers
     *
     * @param bindings the ComponentDescription
     */
    public void setBindings(Hashtable bindings) {
        this.bindings = bindings;
    }

    /**
     * Get the query string #suchThat#...#
     *
     * @return the query string
     */
    public String getQuery() {
        return query;
    }

    /**
     * Get the query string #suchThat#...#
     *
     * @param query the query string
     */
    public void setQuery(String query) {
        this.query = query;
    }

    public Object copy() {
        return new Constraint(query);
    }

    public Object clone() {
        return new Constraint(query);
    }
}
