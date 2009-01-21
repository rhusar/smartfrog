package org.smartfrog.sfcore.languages.sf.constraints.propositions;

import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.security.SFClassLoader;

abstract public class Proposition implements Cloneable{
	private String tag_s;
	private ComponentDescription source;
	private Proposition proposition;
	private Reference source_ref;
	private String prefix_s;
	private Vector<String> toTry = new Vector<String>();
	private Vector<String> toTryOrig = new Vector<String>();
	private String trying;
	protected ComponentDescription comp;
	protected Context context;
	protected int card=0;
	protected int ref_card=0;
	private static Vector<ComponentDescription> prop_cds= new Vector<ComponentDescription>();
	private static Vector<Proposition> propositionsOrig= new Vector<Proposition>();
	private static Vector<Proposition> propositions;
	
	public static void compilePropositions(ComponentDescription comp) throws SmartFrogResolutionException{
		//Get propositions from model...
		
		//System.out.println("Compile Props");		
		if (comp != CoreSolver.getInstance().getOriginalDescription()) return;
		//System.out.println("Really Compile Props");
			
		//SFApplyReference.g_ignoreApplyReference=true;
    	//try {
	    	final Vector<ComponentDescription> props = new Vector<ComponentDescription>();
	    	try {
		    	comp.visit(new CDVisitor(){
		    		public void actOn(ComponentDescription cd, Stack pathStack) throws SmartFrogResolutionException {
		    			//System.out.println("Are we a prop?"+cd);
		    			
		    			Object is_prop = cd.sfContext().get("sfIsProposition");
		    			//System.out.println(is_prop);
		    			//System.out.println(is_prop!=null?"not null":"null");
		    		    
		    		    ComponentDescription p = cd.sfParent();
		    		    Object is_p_prop = p.sfContext().get("sfIsProposition");
		    			if (is_prop!=null && is_p_prop==null) {
		    				prop_cds.add(cd);
		    				//System.out.println("Prop? yes");
		    			} //else System.out.println("Prop? no");
		    		}
		    	}, true);}
	    	catch (Exception e){/***/}
		   
	    	
	    	//System.out.println("Making Props"+prop_cds+prop_cds.size());
			//Turn them into Propositions!
			for (int i=0;i<prop_cds.size();i++){
				ComponentDescription prop_cd = prop_cds.get(i);
				ComponentDescription p = prop_cd.sfParent();
				Proposition prop = createProposition(prop_cd, p, p.sfAttributeKeyFor(comp));
				propositionsOrig.add(prop);
			}
			
			propositions = (Vector<Proposition>) propositionsOrig.clone();
    	/*} finally {
    		//SFApplyReference.g_ignoreApplyReference=false;
    	}*/
	}
	
	public static boolean getResult(){
		return (propositions==null || propositions.size()==0);
	}
	
	public static boolean g_EvaluatingPropositions=false;
	public static boolean evaluatePropositions(boolean backtracked){
		g_EvaluatingPropositions=true;  //This should be changed to just do LAZY refs...
    	try {
			//System.out.println("Evaluating propositions given:"+backtracked);
			
			//System.out.println("Result:"+getResult());
			
			if (backtracked) propositions = (Vector<Proposition>) propositionsOrig.clone();
			else if (getResult()) return false;  //no backtracking...
			
			Vector<Proposition> remProps = new Vector<Proposition>();
			
			//System.out.println("Here here"+propositions.size()+propositions);
			
			for (int i=0;i<propositions.size();i++){
				Boolean result=null;
				Proposition prop=propositions.get(i);
				//System.out.println("To evaluate:"+prop);
				
				result=prop.evaluate(backtracked);
				if (result!=null){
					if (result.booleanValue()) remProps.add(prop); 
					else return true;  //for backtracking...
				}
			}
			propositions.removeAll(remProps);
			return false;
    	} finally{
    		g_EvaluatingPropositions=false;
    	}
	}
				
    abstract Boolean decide_prop(Boolean result, int rem);
	
	Boolean evaluate(boolean backtracked) {
		//System.out.println("Proposition: evaluate..."+this);
		
		//System.out.println("toTryOrigSize:"+toTryOrig.size());
		
		if (toTryOrig.size()==0)
			if (!compileArrayMembers()) return null;  //Array not ready yet...
		
		//System.out.println("222");
		
		if (backtracked) {
			toTry= (Vector<String>) toTryOrig.clone();
			card=0;
		}
		
		//System.out.println("To TryOrig:"+toTryOrig);
		//System.out.println("To Try:"+toTry);
		
		
		while(nextup()){
			//System.out.println("To try:"+proposition+toTry.size());
			Boolean result=proposition.evaluate(backtracked);
			//System.out.println("Result is: "+result);
			if (result==null) {
				//Put trying back in...
				toTry.insertElementAt(trying, 0);
				return null;  //no result returned...
			}
			
			//System.out.println("Deciding result");
			result=decide_prop(result, toTry.size());
			//System.out.println("Result is: "+result);
			if (result!=null) {
				toTry= (Vector<String>) toTryOrig.clone();
				return result;
			}
		}
		return null;
	}
    
    void initialise() throws SmartFrogResolutionException {
    	//System.out.println("Proposition: initialise..."); 
    	
    	Object prop_key=null;
		Object prop_obj=null;
		//Find me a tag (and a source etc whilst at it)!
		Enumeration en = context.keys();
		
		//System.out.println("111"); 
		while (en.hasMoreElements()){
			Object key = en.nextElement();
			Object val = context.get(key);
			try{
				if (comp.sfContainsTag(key, "sfTag")) tag_s = (String) val.toString();
				else if (comp.sfContainsTag(key, "sfSource") && source_ref==null){
					if (val instanceof Reference) source_ref = (Reference) val;
					else throw new SmartFrogResolutionException("sfSource must be a Reference in Proposition: "+context);
				}
				else if (comp.sfContainsTag(key, "sfProp")){
					prop_key=key;
					prop_obj=val;
				} 
				else if (comp.sfContainsTag(key, "sfPrefix")){
					if (val instanceof String) prefix_s = (String) val;
					else throw new SmartFrogResolutionException("sfPrefix must be a String in: "+context);
				}
				else if (comp.sfContainsTag(key, "sfCard")){
					if (val instanceof Integer) ref_card = ((Integer) val).intValue();
					else throw new SmartFrogResolutionException("sfCard must be an Integer in: "+context);
				}
				
				if (source_ref!=null && tag_s!=null && proposition!=null && prefix_s!=null) break; //from while
			} catch (SmartFrogContextException e) {/*Shouldn't happen*/}
		}
		//System.out.println("222"); 
		
    	if (tag_s==null || source_ref==null || prop_obj==null || prefix_s==null) throw new SmartFrogResolutionException("Proposition should have sfTag, sfSource, sfProp and sfPrefix attributes: "+context);
	
    	//Get the source...
    	
    	if (source_ref instanceof SFReference) {
    		try {source_ref=((SFReference)source_ref).sfAsReference();}
    		catch (SmartFrogCompilationException sfce){throw new SmartFrogFunctionResolutionException(sfce);}
    	}
    	
    	//System.out.println("333"+source_ref); 
    	
    	proposition = Proposition.createProposition(prop_obj, comp, prop_key);
		if (proposition==null) throw new SmartFrogResolutionException("Can not create Proposition instance from: "+prop_key+":"+prop_obj+" in: "+comp); 
    	
		//System.out.println("888"); 
		
    }	
     
    boolean compileArrayMembers() {
    	//
    	//System.out.println("Seeking to compile members...");
    	try {source = (ComponentDescription) comp.sfResolve(source_ref);} 
    	catch (Exception e) {/***/}
    	
    	if (source==null) return false; 
    	
    	//System.out.println("Indeed compiling...");
    	
    	//Get the array members...
    	Enumeration en2 = source.sfContext().keys();
    	while (en2.hasMoreElements()){
    		String key = en2.nextElement().toString();
    		if (key.startsWith(prefix_s)) toTryOrig.add(key);
    	}
    	
    	toTry = (Vector<String>) toTryOrig.clone();
    	
    	return true;
    }
    
    
    protected boolean nextup(){
    	//System.out.println("Proposition: nextup..."); 
    	
    	if (toTry.size()==0) return false;
    	trying = toTry.remove(0); 
    	
    	Reference toTry_ref = new Reference();
    	toTry_ref.setEager(false);
    	toTry_ref.addElements(source_ref);
    	toTry_ref.addElement(ReferencePart.here(trying));
    	
    	//System.out.println("Reference to try..."+toTry_ref);
    	
    	//Add the one trying to comp with the key tag
    	try {
    		comp.sfReplaceAttribute(tag_s, toTry_ref);
    		comp.sfAddTag(tag_s, "sfSource");
    	} catch (SmartFrogException e){/**/}
    	
    	//System.out.println("Proposition:nextup()"+context);
    	
    	return true;
    }
    
    public static Proposition createProposition(Object val, ComponentDescription comp, Object key) 
    throws SmartFrogResolutionException{
    	//System.out.println("createProposition..."+comp); 
	       Proposition prop=null;
		   if (val instanceof ComponentDescription) {
			   //System.out.println("CP: comp yes..."); 
		       ComponentDescription prop_cd = (ComponentDescription) val;
			   String propClass = null;
			   try {
		           propClass = (String) prop_cd.sfResolveHere("sfPropositionClass");
			   } catch (Exception e){
		    	   throw new SmartFrogResolutionException("inappropriate proposition class spec", e);
		       }
	 
		       try {prop = (Proposition) SFClassLoader.forName(propClass).newInstance();} 
		       catch (Exception e) {throw new SmartFrogResolutionException("failed to create prop class:"+propClass, e);}
		       
		       //System.out.println("CP: finishing creation..."); 
		       
		       prop.comp=(ComponentDescription)val;
		       prop.context=prop.comp.sfContext();
		       prop.initialise();
	       }
	       return prop;
    }
	
}
