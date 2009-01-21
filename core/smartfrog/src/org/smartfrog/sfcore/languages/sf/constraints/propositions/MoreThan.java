package org.smartfrog.sfcore.languages.sf.constraints.propositions;


public class MoreThan extends Proposition {
	
	Boolean decide_prop(Boolean result, int rem) {
		if (result.booleanValue()){
			card++;
			if (card>ref_card) return new Boolean(true);
			else return null;
		} else if (card+rem<=ref_card) return new Boolean(false);
		return null;
	}
}
