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

import java.io.Serializable;
import java.util.Vector;

import org.smartfrog.sfcore.common.Copying;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Local Constraint variable, to be bound during constraint resolution.
 */
public final class FreeVar implements Copying, Cloneable, Serializable {

    static final long serialVersionUID = -2618542538185314519L;

    /**
     * Default value for VAR
     */
    private Object defVal;

    /**
     * Get the VAR's default value
     *
     * @return Object value
     */
    public Object getDefVal() {
        return defVal;
    }

    /**
     * The constraint evaluation index that originates this FreeVar The constraint strings in a single description get
     * aggregated, but there are potentially multiple descriptions whose aggregated constraint strings get evaluated one at
     * a time as part of link resolution. This value reflects the evaluation # of the corresponding description containing
     * this local VAR.
     */
    private int cidx = -1;

    /**
     * Setter for constraint evaluation index that originates this FreeVar
     *
     * @param cidx integer index
     */
    public void setConsEvalIdx(int cidx) {
        /*Set undo information*/
        CoreSolver.getInstance().addUndoFVInfo(this);
        /*And set...*/
        this.cidx = cidx;
    }

    /**
     * Getter for constraint evaluation index that originates this FreeVar
     *
     * @return integer index
     */
    public int getConsEvalIdx() {
        return cidx;
    }

    /**
     * The attribute of the Constraint description that originates this FreeVar
     */
    private Object ckey;

    /**
     * Setter for attribute of the Constraint description that originates this FreeVar
     *
     * @param ckey Object
     */
    public void setConsEvalKey(Object ckey) {
        this.ckey = ckey;
    }

    /**
     * Getter for attribute of the Constraint description that originates this FreeVar
     *
     * @return Object ckey
     */
    public Object getConsEvalKey() {
        return ckey;
    }

    /**
     * Resets constraint evaluation index and key values
     */
    public void resetConsEvalInfo() {
        this.cidx = -1;
        this.ckey = null;
    }

    /**
     * Set VAR to be a description which is subtype of...
     *
     * @param typeInfo an ordered vector of prototype type names reflecting type
     */
    public void setTyping(Vector typeInfo) {
        if (this.typeInfo != null) {
            throw new RuntimeException("Attempted to apply a subtyping constraint to an attribute " + ckey
                    + " which already has subtype information set");
        }
        this.typeInfo = typeInfo;
    }

    /**
     * Get (component description) typing for VAR
     *
     * @return ordered vector of prototype type names if typing has been previously set, otherwise null
     */
    public Vector getTyping() {
        return typeInfo;
    }

    /**
     * Reset typing to null
     */
    public void clearTyping() {
        typeInfo = null;
    }

    /*
      * Vector containing typing information
      */
    private Vector typeInfo = null;

    /**
     * Static Index for VARs
     */
    private static int nextId = 0;

    /**
     * My id
     */
    private int id;

    /**
     * Range of VAR
     */
    private Object range;

    /**
     * Reference to range of VAR
     */
    private Reference rangeRef;

    /**
     * Get range of VAR
     *
     * @return Object range of VAR
     */
    public Object getRange() {
        return range;
    }

    /**
     * Set range of VAR, given component description which contains it
     *
     * @param comp ComponentDescription containing local VAR
     * @return Object range of VAR
     * @throws SmartFrogFunctionResolutionException if resolution fails
     *
     */
    public Object setRange(ComponentDescription comp) throws SmartFrogFunctionResolutionException {
        if (rangeRef != null) {
            try {
                range = comp.sfResolve(rangeRef);
            } catch (SmartFrogResolutionException e) {
                throw new SmartFrogFunctionResolutionException(
                        "Cannot resolve range for reference: " + rangeRef + " in constraint: " + comp, e);
            }
        }
        return range;
    }

    /**
     * Constructor
     */
    public FreeVar() {
        id = nextId++;
    }

    /**
     * Constructor
     *
     * @param range  Either a reference (which will be resolved in due course), a String or a Vector
     * @param defVal Default value -- String, Integer or ComponentDescription
     */
    public FreeVar(Object range, Object defVal) {
        this();
        if (range instanceof Reference) {
            rangeRef = (Reference) range;
        } else {
            this.range = range;
        }

        this.defVal = defVal;
    }

    /**
     * Get My int ID
     *
     * @return integer id
     */

    public int getId() {
        return id;
    }

    /**
     * Pretty print me
     */
    public String toString() {
        return "VAR" + getId();
    }

    /**
     * Deep copying is for wimps! Just a shallow one will do...
     */
    public Object copy() {
        /**Don't bother with a deep copy!!!**/
        return clone();
    }

    /**
     * Shallow copy...
     */
    public Object clone() {
        Object cloned = null;
        try {
            cloned = super.clone();
        } catch (CloneNotSupportedException cnse){ /*won't happen*/}
        ((FreeVar)cloned).id = nextId++;
    	return cloned;
    }

}
