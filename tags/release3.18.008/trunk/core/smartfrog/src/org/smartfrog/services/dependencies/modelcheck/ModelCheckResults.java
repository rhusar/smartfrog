/** (C) Copyright 1998-2009 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.dependencies.modelcheck;

import java.util.Vector;

public class ModelCheckResults {

	private Vector<ModelCheckResult> results = new Vector<ModelCheckResult>();
	
	private class ModelCheckResult {
		String prop;
		String in;
		boolean result;
		
		ModelCheckResult(String prop, String in, boolean result){
			this.prop=prop;
			this.result=result;
			
			if (in.equals("")) this.in=null;
			else this.in=in;
            
		}
	}

	public void addResult(String prop, String in, boolean result){
		results.add(new ModelCheckResult(prop, in, result));
	}

	public int numResults(){
		return results.size();
	}
	
	public boolean getResult(int idx){
		return results.get(idx).result;
	}
	
	public String getProp(int idx){
		return results.get(idx).prop;
	}
	
	public String getIn(int idx){
		return results.get(idx).in;
	}
}
