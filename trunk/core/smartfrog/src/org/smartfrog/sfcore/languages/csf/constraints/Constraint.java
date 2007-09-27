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


import org.smartfrog.sfcore.common.Copying;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;


/**
 * Defines the basic Constraint implementation.
 * The class is basically a holder for the query string (#suchThat#...#), the theory string (#where#...#) and the
 * component to which the constraint applies. In addition, the constraint can hold arbitrary data (an Object) to represent
 * state the solver may require to associate with this specific constraint.
 * <p/>
 * It also contains the logic for deciding which solver should be used - this is static and is common to all
 * contraints. It is not possible to use a different solver for each constraint.
 */
public class Constraint implements Copying, Comparable {

    private boolean docons = false;
    private String query = null;
    private int priority = 0; 

    private Object solverState = null;
    private ComponentDescription cd = null;

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
    public Constraint(String query, int priority, boolean docons) {
       setQuery(query);
       setPriority(priority);
       setDoCons(docons);
    }
   
    public String toString() {
	    return "#cons:"+priority+"#"+query+"#";
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
     * Get the query string #suchThat#...#
     *
     * @return the query string
     */
    public String getQuery() {
        return query;
    }

    /**
     * Get the query string for #suchThat#...#
     *
     * @param query the query string
     */
    public void setQuery(String query) {
        this.query = query;
    }


    /**
     * Whether a "do" constraint
     *
     * @return whether do constraint
     */
    public boolean isDoCons() {
        return docons;
    }

    /**
     * Get the do constraint
     *
     * @param docons the constraint
     */
    public void setDoCons(boolean docons) {
        this.docons = docons;
    }

    
       /**
     * Get the priority for #suchThat:ppp#...#
     *
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Get the query string #suchThat#...#
     *
     * @param priority the priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Object copy() {
	    return new Constraint(query, priority, docons);
    }

    public Object clone() {
        return copy();
    }

   /**
    * Compares this object with the specified object for order.  Returns a
    * negative integer, zero, or a positive integer as this object is less
    * than, equal to, or greater than the specified object.<p>
    * <p/>
    * In the foregoing description, the notation
    * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
    * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
    * <tt>0</tt>, or <tt>1</tt> according to whether the value of <i>expression</i>
    * is negative, zero or positive.
    * <p/>
    * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
    * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
    * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
    * <tt>y.compareTo(x)</tt> throws an exception.)<p>
    * <p/>
    * The implementor must also ensure that the relation is transitive:
    * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
    * <tt>x.compareTo(z)&gt;0</tt>.<p>
    * <p/>
    * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt>
    * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
    * all <tt>z</tt>.<p>
    * <p/>
    * It is strongly recommended, but <i>not</i> strictly required that
    * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
    * class that implements the <tt>Comparable</tt> interface and violates
    * this condition should clearly indicate this fact.  The recommended
    * language is "Note: this class has a natural ordering that is
    * inconsistent with equals."
    *
    * @param o the Object to be compared.
    * @return a negative integer, zero, or a positive integer as this object
    *         is less than, equal to, or greater than the specified object.
    * @throws ClassCastException if the specified object's type prevents it
    *                            from being compared to this Object.
    */
   public int compareTo(Object o) {
      if (o == null) throw new NullPointerException();
      
      if (getPriority() == ((Constraint)o).getPriority()) return 0;
      else if (getPriority() < ((Constraint)o).getPriority()) return -1;
      else return 1;
   }
}
