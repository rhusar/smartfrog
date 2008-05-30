package org.smartfrog.services.dependencies2.modelcheck;

import java.util.Vector;

public class ModelCheckResults {

	private Vector<ModelCheckResult> results = new Vector<ModelCheckResult>();
	
	private class ModelCheckResult {
		int index;
		String prop;
		String in;
		boolean result;
		
		ModelCheckResult(int index, String prop, boolean result){
			this.index=index;
			this.prop=prop;
			this.result=result;
		}
		ModelCheckResult(int index, String prop, String in, boolean result){
			this(index, prop, result);
			this.in=in;
		}
	}

	public void addResult(int index, String prop, boolean result){
		results.add(new ModelCheckResult(index, prop, result));
	}

	public void addResult(int index, String prop, String in, boolean result){
		results.add(new ModelCheckResult(index, prop, in, result));
	}

	
}
