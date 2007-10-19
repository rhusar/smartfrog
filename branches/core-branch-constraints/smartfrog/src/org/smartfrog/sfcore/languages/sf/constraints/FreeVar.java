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

import org.smartfrog.sfcore.common.Copying;
import org.smartfrog.sfcore.common.LinkResolutionState;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.ResolutionState;

/**
 * Define a TBD entry in the syntax. Is used as a free variable to be bound
 * by coonstraint resolution.
 */
//public class FreeVar implements ComponentResolver, Copying {
public class FreeVar implements Copying {

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
	
    private static int nextId = 0;

    private int id;

    private boolean indexFixed;
    
    public FreeVar() {
        id = nextId++;
        indexFixed = false;
    }

    public FreeVar( int id ) {
        this.id = id;
        indexFixed = true;
    }

    public FreeVar( String image ) {
        int startPoint = image.indexOf('/');
        if (startPoint == -1) {
            this.id = nextId++;
            indexFixed = false;
        } else {
            int index = new Integer(image.substring(startPoint+1)).intValue();
            this.id = index;
            indexFixed = true;
        }
    }
    /**
     * Internal method that place resolves a parsed component. Place resolving
     * places all attributes which have a reference (eager) as attribute name in
     * their target component.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *          failed to place resolve
     */
    public void placeResolve() throws SmartFrogResolutionException {
        //return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Internal recursive method for doing the actual placement resolution.
     * Implementors place any attributes with an eager reference as key in the
     * prospective component.
     *
     * @param resState resolution state
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *          failed to place resolve
     */
    public void doPlaceResolve(ResolutionState resState) throws SmartFrogResolutionException {
        //return this;
    }

    /**
     * Internal method that type resolves a parsed component. Place resolving
     * finds all supertypes and flattens them out.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *          failed to type resolve
     */
    public void typeResolve() throws SmartFrogResolutionException {
        if (indexFixed)    ;
            //return this;
        else     ;
            //return new FreeVar();
    }

    /**
     * Internal recursive method for doing the actual type resolution.
     *
     * @param resState resolution state
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *          failed to type resolve
     */
    public void doTypeResolve(ResolutionState resState) throws SmartFrogResolutionException {
         if (indexFixed)     ;
            //return this;
        else                ;
            //return new FreeVar();
    }

    /**
     * Internal method that performs a pre-deployment resolution on the object
     * implementing this interface. Pre-deployment resolution means finding all
     * eager reference values, resolving them, and copying the result into the
     * target component
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *          failed to deploy resolve
     */
    public void linkResolve() throws SmartFrogResolutionException {
        //return this;
    }

    /**
     * Internal recursive method for doing the actual link resolution.
     *
     * @param resState resolution state
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *          failed to deploy resolve
     */
    public void doLinkResolve(ResolutionState resState) throws SmartFrogResolutionException {
        //return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getId() {
        return id;
    }

    public String toString() {
    	return "VAR" + getId();
    }

    public Object copy(){
       return new FreeVar();
    }

    public Object clone() {
       return new FreeVar();
    }

}
