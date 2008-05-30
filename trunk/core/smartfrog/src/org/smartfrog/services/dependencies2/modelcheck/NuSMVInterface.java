package org.smartfrog.services.dependencies2.modelcheck;

import java.io.File;

public class NuSMVInterface {

	private static boolean library_loaded=false;	
	
	public native static void run(String command);
	
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
		String command = ModelCheck.sfhome+"NuSMV/WIN/bin/NuSMV "+ModelCheck.smv_file+" > "+ModelCheck.dat_file;	
		
    	run(command);
		return true;
	}
	
	static public void main(String[] args){
		NuSMVInterface nusmv = new NuSMVInterface();
		nusmv.run_check();
	}
}
