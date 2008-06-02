package org.smartfrog.services.dependencies.modelcheck;

import java.io.File;

public class NuSMVInterface {

	private static boolean library_loaded=false;	
	
        public native static void run(String inputFile, String filePrefix, String dump_file, ModelCheckResults mcrs);

	private ModelCheckResults mcrs = new ModelCheckResults();
	
	static {
		//Just windows for now...
		try {
			System.loadLibrary("NuSMVInterface");
			library_loaded=true;
		} catch (Exception e){}
	}
	
	public boolean runCheck(){
		if (!library_loaded) return false;
		run(ModelCheck.smv_file, ModelCheck.filePrefix, ModelCheck.filePrefix+"all", mcrs);
		return true;
	}
	
	public ModelCheckResults getResults(){
		return mcrs;
	}
	
	public void printResults(){
		int numResults = mcrs.numResults();
		for (int i=0; i<numResults; i++){
			String in = mcrs.getIn(i);
			System.out.println("Prop:"+mcrs.getProp(i)+(in!=null?" in:"+in:"")+" is:"+mcrs.getResult(i));
		}
	}
	

	public static void main(String args[]){
	    new NuSMVInterface().runCheck();
	}

}
