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

package org.smartfrog.services.dependencies.scripting;

import java.util.Vector;

import org.smartfrog.services.dependencies.threadpool.ThreadPool;
import org.smartfrog.services.scripting.RemoteScriptPrim;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.languages.sf.functions.BaseFunction;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

/** 
 * Does aggregation, pasting aggregated output into contained apply references
 */
public class ScriptWrapper extends BaseFunction implements MessageKeys {
		
	private static final String SCRIPT = "script";
	private static final String SCRIPTPRIM = "scriptPrim";
	private static final String COPYATTRIBUTES = "scriptAttributes";
	
    /**
     * 
     *
     * @return an Object representing the answer
     * @throws SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected Object doFunction()  throws SmartFrogFunctionResolutionException {    
	    try {
	    	System.out.println("555*** DO FUNCTION *** About to PING22...:"+comp);
	    	RemoteScriptPrim scriptPrim = (RemoteScriptPrim) comp.sfResolve(new Reference(ReferencePart.here(SCRIPTPRIM)));
	    	String script = (String) comp.sfResolve(new Reference(ReferencePart.here(SCRIPT)));
	    	Vector copyAttributes = (Vector) comp.sfResolve(COPYATTRIBUTES);
	    	
	    	System.out.println("Script attributes..."+ copyAttributes);
	    	
	    	for (int i=0; i< copyAttributes.size(); i++){
	    		System.out.println("In script attribute setting...");
	    		
	    		Object k = copyAttributes.get(i);
	    		String ks = k.toString();
	    		System.out.println("Actual key:"+ks);
				Object v = comp.sfResolve(new Reference(ReferencePart.attrib(k.toString())));
				
				System.out.println("In script attribute setting..."+k+":"+ks+":"+v);
				
				((Prim) scriptPrim).sfReplaceAttribute(k, v);
	    	}
			((Prim) scriptPrim).sfAddAttribute("sfnull", SFNull.get());
	    	
	    	System.out.println("*** DO FUNCTION *** Have we go the script?"+scriptPrim+script);
	    	if (script!=null){
	    		System.out.println("*** 1DO FUNCTION *** YES!");
	    		scriptPrim.eval(script);  
	    		System.out.println("*** 2DO FUNCTION *** YES!");
	    		System.out.println("*** 3DO FUNCTION *** YES!");
	    	} else System.out.println("*** DO FUNCTION *** NO!");
	    	
	    	//Clean the prim...
	    	for (int i=0; i< copyAttributes.size(); i++){
	    		Object k = copyAttributes.get(i); 
				((Prim) scriptPrim).sfRemoveAttribute(k);
			}
			((Prim) scriptPrim).sfRemoveAttribute("sfnull");
	    	
	    } catch (Exception e){System.out.println("My error!"+e);}
        return SFNull.get();  //return NULL
    }
}
