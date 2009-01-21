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

package org.smartfrog.sfcore.languages.sf.constraints.propositions;

import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

/**
 * Defines the Constraint function.
 */
abstract public class BaseProposition extends Proposition {
	private Vector<Proposition> propsOrig=new Vector<Proposition>(); 
	private Vector<Proposition> props; 
	protected boolean first=false;
	
	Boolean evaluate(boolean backtracked){
		//System.out.println("Proposition: evaluate...");
		
		if (backtracked) props= (Vector<Proposition>) propsOrig.clone(); 
		
		for (int i=0;i<props.size();i++){
			Proposition prop= props.get(i);
			Boolean result=prop.evaluate(backtracked);
			if (result==null) {
				//Remove all those before...
				for (int j=0; j<i; j++) props.remove(0);
				return null;  //no result returned...
			}
			
			result=decide_prop(result, props.size()-i-1);
			if (result!=null) {
				props= (Vector<Proposition>) propsOrig.clone(); 
				return result;
			}
		}
		return null; //this never occurs...
	}
	
	void initialise() throws SmartFrogResolutionException{
		//Every time we are called, we start again...
		//System.out.println("BaseProp: evaluate..."); 
		
		Enumeration en = context.keys();
		while (en.hasMoreElements()){
			Object key = en.nextElement();
			try{
				Object val = context.get(key);
				if (comp.sfContainsTag(key, "sfProp")){
					Proposition prop = createProposition(val, comp, key);
					if (prop==null) throw new SmartFrogResolutionException("sfProp does not yield valid proposotion for key:"+key+" in: "+context);
					else propsOrig.add(prop);
				} 
			} catch (SmartFrogContextException e) {/*Shouldn't happen*/}
		}
		if (propsOrig.size()==0) throw new SmartFrogResolutionException("Prop with no propositions: "+context);
		props = (Vector<Proposition>) propsOrig.clone();
	}
}
