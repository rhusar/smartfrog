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

/**
 * Interface for Component Description browsers used to display sfComfig hierarchy for the purpose of setting user variables
 * @author anfarr
 *
 */
public interface CDBrowser {
	
	/**
	 * Sets an unspecified object as pertaining to the state of the solving engine
	 * @param es
	 */
	void setES(Object es);
	
	/**
	 * Add attribute to the hierarchy to be displayed
	 * @param d -- my parent as unspecified object
	 * @param av  -- my attribute name / value as unspecified object
	 * @param showme -- am I in the immediate component description to be displayed? 
	 * @return tree node which will be used later on to add child attributes to
	 */
	Object attr(Object d, Object av, boolean showme);
	
	/**
	 * Redraw the browser
	 */
	void redraw();
	
	/**
	 * Kill the browser...
	 */
	void kill();
}
