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

import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Defines the Constraint function.
 */
public class Atomic extends Proposition {
	private Reference reference;
	
	Boolean decide_prop(Boolean result, int rem){return null;}
		
    Boolean evaluate(boolean backtracked){
    	//Reference ref_copy = reference.copyandRemoveLazy();
    	
    	//System.out.println("AP: evaluate.."); 
		Boolean result=null;  
		try {
		    result=(Boolean) comp.sfResolve(reference);
		} catch (Exception e){/*Intentionally do nothing...*/}

		return result;
	}
			
	void initialise() throws SmartFrogResolutionException{
		//System.out.println("AP: initialise..."); 
		//Every time we are called, we start again...
		context = comp.sfContext();
		if (reference==null){
		   	Enumeration en = context.keys();
			while (en.hasMoreElements()){
				Object key = en.nextElement();
				if (key.toString().startsWith("sf")) continue;
				
				//try{
					//if (comp.sfContainsTag(key, "sfProp")){
						Object val = context.get(key);
						if (val instanceof SFReference) {
							try {
								reference=((SFReference)val).sfAsReference();
							}catch(SmartFrogCompilationException sfce){throw new SmartFrogResolutionException(sfce);}
							break; //only want one reference!
						} else throw new SmartFrogResolutionException("sfProp in Atomic Proposition is not a Reference, key:"+key+" in: "+context);
					//}
				//} catch (SmartFrogContextException e) {/*Shouldn't happen*/}
			}
			if (reference==null) 
				throw new SmartFrogResolutionException("Atomic Proposition with no reference!: "+context);
		}
    }
}
