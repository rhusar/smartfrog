package org.smartfrog.sfcore.languages.sf.constraints.propositions;


public class ForAll extends Proposition {
	
	Boolean decide_prop(Boolean result, int rem) {
		//System.out.println("ForAll: result_prop..."); 
		if (!result.booleanValue()) return result;  //failed...
		else if (rem==0) return new Boolean(true); //success...
		else return null; 
	}
}
