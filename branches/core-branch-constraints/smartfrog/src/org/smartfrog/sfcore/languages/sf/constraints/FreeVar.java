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

package org.smartfrog.sfcore.languages.sf.constraints;

import java.io.Serializable;
import java.util.Vector;

import org.smartfrog.sfcore.common.Copying;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.EclipseSolver.SmartFrogEclipseRuntimeException;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.LinkResolutionState;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Define a TBD entry in the syntax. Is used as a free variable to be bound
 * by coonstraint resolution.
 */
public class FreeVar implements Copying, Cloneable, Serializable {

	static final long serialVersionUID = -2618542538185314519L;
	
	/**
	 * Default value for VAR
	 */
	private Object defVal;
	
	/**
	 * Get the VAR's default value
	 * @return
	 */
	public Object getDefVal() { return defVal; }
	
	/**
	 * The constraint evaluation index that originates this FreeVar
	 */
	private int cidx=-1;
	/**
	 * Setter for constraint evaluation index that originates this FreeVar
	 * @param cidx
	 */
	public void setConsEvalIdx(int cidx){ 
		LinkResolutionState.getLRS().addUndo(this, this.cidx);
		this.cidx = cidx;    	
	}
	/**
	 * Getter for constraint evaluation index that originates this FreeVar
	 * @return
	 */
	public int getConsEvalIdx() { return cidx; }
	
	/**
	 * The attribute of the Constraint description that originates this FreeVar
	 */
	private Object ckey;
	
	/**
	 * Setter for attribute of the Constraint description that originates this FreeVar
	 * @param cidx
	 */
	public void setConsEvalKey(Object ckey){
		LinkResolutionState.getLRS().addUndo(this, this.ckey);	
		this.ckey = ckey; 
	}
	/**
	 * Getter for attribute of the Constraint description that originates this FreeVar
	 * @return
	 */
	public Object getConsEvalKey() { return ckey; }
	

	/**
	 * Set that VAR is a subtype of...
	 * @param attr
	 */
	public void setTyping(String attr, Vector typeInfo) {
		if (this.typeInfo!=null) throw new SmartFrogEclipseRuntimeException("Attempted to apply a subtyping constraint to an attribute: "+attr+" which already has subtype information set.");
		this.typeInfo=typeInfo;
	}
	
	/**
	 * Get the vector of types
	 * @return
	 */
	public Vector getTyping(){ 
		return typeInfo;
	}
	
	public void clearTyping(){
		typeInfo=null;
	}
	
	private Vector typeInfo=null;
	
    private static int nextId = 0;

    private int id;

    private Object range;
    private Reference rangeRef;
    
    public Object getRange(){ 
    	return range; 
    }
    
    public Object setRange(ComponentDescription comp) throws SmartFrogFunctionResolutionException { 
        if (rangeRef!=null){
        	String ref = rangeRef.toString();
        	try {
        		range = comp.sfResolve(rangeRef);
        	} catch (SmartFrogResolutionException e){
        		throw new SmartFrogFunctionResolutionException("Can resolve range for reference: "+rangeRef+" in constraint: "+comp, e);
        	}
        }
    	return range; 
    }
    
    
    public FreeVar() {
        id = nextId++;
    }
  
    public FreeVar( Object range, Object defVal ) {
    	this();   	
    	if (range instanceof Reference) rangeRef = (Reference) range; 
    	else this.range = range;
    	
    	this.defVal = defVal;
    }
        
    public int getId() {
        return id;
    }

    public String toString() {
    	return "VAR" + getId();
    }

    public Object copy(){
        /**Don't bother with a deep copy!!!**/
    	return clone();
    }

    public Object clone() {
    	Object cloned=null;
        try {
        	cloned = super.clone();
        } catch (CloneNotSupportedException cnse){ /*won't happen*/}
    	return cloned;
    }

}
