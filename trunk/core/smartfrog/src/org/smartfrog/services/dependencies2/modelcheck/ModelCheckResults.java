package org.smartfrog.services.dependencies2.modelcheck;

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
