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

import com.parctechnologies.eclipse.Atom;

/**
 * Records status information for sfConfig browser used by EclipseSolver in setting user variables
 * @author anfarr
 *
 */
class EclipseStatus {
	/**
	 * Are we done with setting user variables?
	 */
	private boolean m_done;
	
	/**
	 *  Records whether we undid the setting of a value
	 */
	private boolean m_back;
	
	/**
	 * Size of undo stack
	 */
	private int m_undo;
	
	/**
	 * Last attribute set
	 */
	private String m_attr;
	
	/**
	 * Value of attribute set
	 */
	private String m_val;
	
	/**
	 * Eclipse Solver
	 */
	private EclipseSolver m_solver;
	
	/**
	 * Constructor, 
	 * @param solver Eclipse Solver
	 */
	EclipseStatus(EclipseSolver solver){
		m_solver = solver;
	}
	
	/**
	 * Undo previous browser setting operation
	 */
	public void undo(){
		m_solver.javaToEclipse(new Atom("back"));
	}
	
	/**
	 * We are done with setting user variables for current Constraint
	 */
	public void done(){
		m_solver.killBrowser();
		m_solver.javaToEclipse(new Atom("done"));
	}
	
	/**
	 * Are we done with setting user variables?
	 * @return done status
	 */
    public boolean isDone(){
    	return m_done;
    }

    /**
     * Sets whether done with setting user variables?
     * @param done are we done?
     */
    public void setDone(boolean done){
    	this.m_done=done;
    }
      
    /**
     * Did we undo?
     * @return Whether we just did an undo...
     */
    public boolean isBack(){
    	return m_back;
    }

    /**
     * Sets whether we did an undo
     * @param back Did we?
     */
    public void setBack(boolean back){
    	this.m_back=back;
    }
      
    /**
     * Gets current size of undo stack
     * @return size of undo stack
     */
    public int getUndo(){
    	return m_undo;
    }
    
    /**
     * Sets size of undo stack
     * @param undo size of undo stack
     */
    public void setUndo(int undo){
    	m_undo=undo;
    }
    
    /**
     * Gets label for undo button
     * @return label for undo button
     */
    public String getUndoLabel(){
    	if (m_attr!=null) return m_attr+" currently set to: "+m_val;
    	else return "";
    }
        
    /**
	 * Sets last attribute set
	 * @param Last attribute set
	 */
    public void setAttr(String attr){
    	m_attr=attr;
    }

    /**
	 * Sets value of last attribute set
	 * @param Value of last attribute set
	 */
    public void setValue(String val){
    	m_val=val;
    }
    
}
