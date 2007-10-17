package org.smartfrog.sfcore.languages.sf.constraints;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

abstract public class PrologSolver extends CoreSolver {
	private final String pathswitch = "/../constraints/";
	private final String coreFileSuffix = "core.ecl";
    private final String theoryFileSuffix = "base.ecl";
    private final String theoryFilePath = "opt.smartfrog.sfcore.languages.sf.constraints.theoryFilePath";
    
    void prepareSolver() throws SmartFrogResolutionException {
        String sfhome = SFSystem.getEnv("SFHOME");
        
        if (sfhome==null){
    		throw new SmartFrogResolutionException("Environment variable SFHOME must be set. Context: constraint processing");
    	}
                
        String corefile = sfhome+pathswitch+coreFileSuffix;
        String thfile = sfhome+pathswitch+theoryFileSuffix;
       
        
        // create the theory
        try {
            prepareTheory(top,corefile,thfile);
        } catch (Exception e) {
            throw new SmartFrogResolutionException("Unable to parse base theory for constraint resolution. ", e);
        }
         
        //Add the path root
        String thpath = System.getProperty(theoryFilePath);
        
        if (thpath!=null){
        	try {
        		runGoal("add_path(\""+thpath+"\")");
        	} catch (Exception e) {
                throw new SmartFrogResolutionException("Unable to add root theory file path. ", e);
            }	
        }		
    }
    
    protected String remove_ref_prefix(String ref){
    	String ref1 = "";
    	int idx = ref.indexOf(":");
    	if (idx!=-1) ref1 = ref.substring(idx+1, ref.length());
    	return ref1;
    }
    
    protected String create_ref_str(ComponentDescription cd){
    	String ref = cd.sfCompleteName().toString();
    	String ref1 = remove_ref_prefix(ref);
    	if (ref1.compareTo("")==0) return ref1;
    	else return ref1+":";
    }
    
    abstract public void prepareTheory(ComponentDescription cd, String coreFile, String prologFile) throws Exception;
    abstract public void runGoal(String goal) throws Exception;
}
