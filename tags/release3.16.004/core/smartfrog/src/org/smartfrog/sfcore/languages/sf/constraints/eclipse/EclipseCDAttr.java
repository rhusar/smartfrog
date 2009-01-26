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

package org.smartfrog.sfcore.languages.sf.constraints.eclipse;

import java.util.Collection;
import java.util.Iterator;

import org.smartfrog.sfcore.languages.sf.constraints.FreeVar;

import com.parctechnologies.eclipse.Atom;
import com.parctechnologies.eclipse.CompoundTermImpl;

/**
 * Records information for a single attribute, used in EclipseSolver's sfConfig browser for user variables
 * @author anfarr
 *
 */
class EclipseCDAttr {
	/**
	 * Name of attribute
	 */
	private String name;
	
	/**
	 * Value of attribute
	 */
	private Object val;
	
	/**
	 * Range of attribute
	 */
	private Object range;
	
	/**
	 * Whether set
	 */
	private boolean set;
	
	/**
	 * Eclipse Solver
	 */
	private EclipseSolver solver;
	
	/**
	 * Constructor,
	 * @param solver Eclipse Solver
	 * @param name Name of attribute
	 * @param val Value of attribute
	 */
	EclipseCDAttr(EclipseSolver solver, String name, Object val){
		this.solver = solver;
		this.name = name;
		this.val = val;
	}
	
	/**
	 * Is attribute value set
	 * @return Is value set?
	 */
	boolean isSet(){
		return set;
	}
	
	/**
	 * Set whether attribute value is set
	 * @param set Is set?
	 */
	void set(boolean set){
		this.set =set;
	}
	
	/**
	 * Set range of attribute
	 * @param range 
	 */
	void setRange(Object range){
		this.range =range;
	}
	
	/**
	 * Set value of attribute
	 * @param val
	 */
	void setValue(Object val){
		this.val =val;
	}
	
	/**
	 * Process setting of attribute value (from range)
	 * @param entry Attribute value
	 * @return Whether processing is successful
	 */
	boolean process_sel(String entry){
		solver.setEclipseCDAttr(this);
		Collection c = (Collection) range;
	    Iterator iter = c.iterator();
		while (iter.hasNext()){
			String el = iter.next().toString();
			if (el.equals(entry)){			
				if (val instanceof FreeVar && ((FreeVar)val).getRange() instanceof Boolean){
					if (entry.equals("true")) entry="1"; 
					else entry="0";
				}
				solver.javaToEclipse(new CompoundTermImpl("set", new Atom(name), new Atom(entry)));
				return true;
			} 
		}
		return false;
	}
	        
	/**
	 * Gets range of attribute as a string
	 * @return range as string
	 */
    public String getRangeAsString(){
		return range.toString();
	}
    
    /**
     * Gets attribute name
     * @return attribute name
     */
	public String getAttrName(){
		return name;
	}
	
	public Object getValue(){
		return val;
	}
	
	public String toString(){
		if (set){
			return name +" has value: "+ val;
		} else {
			return name +" ranges over: "+ range;
		}
	}
}
