package org.smartfrog.sfcore.languages.sf.constraints.propositions;


public class FewerThan extends Proposition {
	
	Boolean decide_prop(Boolean result, int rem) {
		if (result.booleanValue()){
			card++;
			if (card>ref_card) return new Boolean(false);
			else return null;
		} else if (rem==0) return new Boolean(true);
		return null;
	}
}
