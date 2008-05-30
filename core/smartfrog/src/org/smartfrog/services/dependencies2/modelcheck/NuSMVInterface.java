package org.smartfrog.services.dependencies2.modelcheck;

import java.io.File;

public class NuSMVInterface {

	private static boolean library_loaded=false;	
	
	public native static void run(String inputFile, ModelCheckResults mcrs);
	
	private ModelCheckResults mcrs = new ModelCheckResults();
	
	static {
		//Just windows for now...
		String prefix = ModelCheck.sfhome+"NuSMV/WIN/lib/NuSMVInterface";
		File file_check = new File(prefix+".dll");
		try {
			if (file_check.exists()) System.load(file_check.getAbsolutePath());
			library_loaded=true;
		} catch (Exception e){}
	}
	
	public boolean run_check(){
		if (!library_loaded) return false;
		
    	run(ModelCheck.smv_file, mcrs);
		return true;
	}
	
	public ModelCheckResults getResults(){
		return mcrs;
	}
	
	
}
