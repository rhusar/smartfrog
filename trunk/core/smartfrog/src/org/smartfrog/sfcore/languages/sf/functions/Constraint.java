/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.sfcore.languages.sf.functions;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintConstants;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.constraints.FreeVar;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintResolutionState.ConstraintContext;
import org.smartfrog.sfcore.languages.sf.constraints.propositions.Proposition;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.ApplyReference;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

/**
 * Defines the Constraint function.
 */
public class Constraint extends BaseFunction implements MessageKeys {
	
	static class Argument {
		Object loc;
		Object arg;
		Argument(Object loc, Object arg){
			this.loc=loc; this.arg=arg;
		}
	}
	
	static class Arguments {
		HashMap<String, Object> args = new HashMap<String, Object>();
		Vector<Argument> argsa = new Vector<Argument>();
		Vector<Object> argsv = new Vector<Object>();
		
		void put(Object loc, Object arg){
			args.put(loc.toString(), arg);
			argsa.add(new Argument(loc, arg));
		    argsv.add(arg);	
		}
		
		Object get(Object loc){
			return args.get(loc.toString());
		}
		
		public String toString(){
			String ret="[";
			for (int i=0;i<argsv.size();i++){
				if (i>0) ret+=", ";
				ret+= argsv.get(i).toString();
			}
			return ret+"]";
		}
		
		public String toFullString(){
			return toFullVector().toString();
		}
		
		public Vector toFullVector(){
			Vector args= new Vector();
			for (int i=0;i<argsv.size();i++){
				Vector arg = new Vector();
				args.add(arg);
				arg.add(argsa.get(i).loc);
				arg.add(argsa.get(i).arg);
			}
			return args;
		}
		
		Vector getArgs(){
			return argsv;
		}
	}
	
	static public class CompositeSource {
		HashMap context;
		Object source;
		Reference path;
		ComponentDescription update;
		String prefix; 
		Object key;
		Object retval;
		Object unify;
		Arguments arguments=new Arguments();
		boolean freevars=false;
		int idx=-1;
		
		CompositeSource(HashMap context, 
				        Object source, 
				        String prefix, 
				        Reference path, 
				        Object key, 
				        ComponentDescription update,
				        Object unify){
			this.context=context;
			this.source=source;
			this.prefix=prefix;
			this.update=update;
			this.key=key;
			this.path=path;
			this.unify=unify;
		}
	}
		
    /**
     * The method to implement the functionality of the if-then-else function.
     *
     * @return an Object representing the answer
     * @throws SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected Object doFunction()  throws SmartFrogFunctionResolutionException {
    	//If constraint resolution is not pertinent or possible return
    	    	    	
    	/**
    	 * Records the attributes used for consraint goal preds 
    	 */
    	Vector goal_attrs = new Vector();
    	
    	/**
    	 * Record the constraint goals to be processed
    	 */
    	Vector goal = new Vector();
    	
    	/**
    	 * Records the attributes other than constraint goal preds
    	 */
    	Vector attrs = new Vector();
    	
    	/**
    	 * Record the values of the attributes other than constraint goal preds
    	 */
    	Vector values = new Vector();
    	
    	/**
    	 * Automatic variables...
    	 */
    	Vector autos = new Vector();
    	    	    	
    	/**
    	 * User variables present?
    	 */
    	boolean isuservars=false;
    	
    	HashMap<FreeVar, Object> assigns = new HashMap<FreeVar, Object>(); 
    	
    	System.out.println("In constraint...");
    	
    	if (!CoreSolver.getInstance().getConstraintsPossible()) return comp; 
    	
		CoreSolver.getInstance().setShouldUndo(true);

		Vector<CompositeSource> aggs=new Vector<CompositeSource>();
		
		System.out.println("Fetching aggregate sources");
		getAggregateSources(comp, aggs);
		
		for (int i=0;i<aggs.size();i++){
			CompositeSource cs = aggs.get(i);
	    	
	    	//System.out.println("%%%%%%%%%%%%%%%%%%%%%%"+cs+orgContext.get(cs.key));
	    	
	    	extractArgumentsFromSource(cs);		    	
	    	
	    	//System.out.println("+++++++++++++++++++++"+cs.key+":"+cs.arguments);
	    	
	    	//Unify arguments...
	    	unify(cs.arguments, cs.unify, assigns);
	    	String csargs = cs.arguments.toString();
	    	//System.out.println("CSARGS!!!"+cs.key+":"+csargs);
	    	orgContext.put(cs.key, cs.arguments.getArgs());
	    	
	    	try {
	    		if (cs.freevars) orgContext.sfAddTag(cs.key, ConstraintConstants.FREEVARS_TAG);	
	    	} catch (SmartFrogContextException context){/*Shouldn't happen*/}
		}	
				
		//System.out.println("111");
		
		CoreSolver.getInstance().setShouldUndo(false);
		
		Object ret_key=null;
			    		
    	//Process attributes, either constraint goals or other...
    	Enumeration attr_enum = orgContext.keys();
    	while (attr_enum.hasMoreElements()){
    		Object key = attr_enum.nextElement();
    		Object val = orgContext.get(key);
    		try {

    			if (orgContext.sfContainsTag(key, ConstraintConstants.RETURN_TAG)) ret_key=key;
    			
    			if (orgContext.sfContainsTag(key, ConstraintConstants.CONSTRAINT_TAG)) goal_attrs.add(key);
    			else { 
    				
    				if (val instanceof String && !isLegal((String)val)) continue;	

	    			//Set the attribute name originating this FreeVar
	    			if (val instanceof FreeVar) {
	    				FreeVar fv = (FreeVar) val;
	    				if (fv.getConsEvalKey()==null) fv.setConsEvalKey(key);
	    				
	    				//Make sure range is appropriated in free var
	    				fv.constructRange(comp);
	    				
	    				if (orgContext.sfContainsTag(key, ConstraintConstants.AUTOVAR_TAG)) autos.add(key);
		    			else if (orgContext.sfContainsTag(key, ConstraintConstants.USERVAR_TAG)) isuservars=true;
	    				
	    			} else if (val instanceof ComponentDescription){
	    				
	    				ComponentDescription cd = (ComponentDescription) val;
	    				
	    				if (cd.sfContext().get("IsConstraintVar")!=null){
	    					
	    					FreeVar fv = new FreeVar();
		    				
		    				//System.out.println("cd:"+cd);
	    					
	    					//range
	    					Object range = null;
	    					if ((range=cd.sfContext().get(ConstraintConstants.RANGE))!=null) fv.setRange(range);
	    					else if ((range=cd.sfContext().get(ConstraintConstants.RANGEREF))!=null) fv.setRangeRef(range);
	    					else if (cd.sfContext().get(ConstraintConstants.IRANGE)!=null) fv.setRange(new Integer(0));
	    					else if (cd.sfContext().get(ConstraintConstants.BRANGE)!=null) fv.setRange(new Boolean(true));
	    						
	    					//System.out.println("22222");
	    					
	    					//qualification
	    					Object qual_val=null;
	    					if ((qual_val=cd.sfContext().get(ConstraintConstants.AUTOVAR))!=null){
	    						//System.out.println("Yes we have an autovar!");
	    						if (qual_val instanceof SFNull) {
	    							//System.out.println("Yes we are null...");
	    							autos.add(key);
	    						} else if (qual_val instanceof SFReference){
	    							Reference auto_ref=null;
	    							try {
	    								auto_ref=((SFReference) qual_val).sfAsReference();
	    							} catch (SmartFrogCompilationException sfce){ throw new SmartFrogFunctionResolutionException(sfce);}
    								Object label = cd.sfResolve(auto_ref);
    								//System.out.println("label:"+(label==null));
	    							
    								ComponentDescription label_cd = null;
    								try {
    									label_cd = (ComponentDescription) label;
    								} catch (ClassCastException cce){
    									throw new SmartFrogFunctionResolutionException("Reference for autovar does not resolve to a Labelling CD, ref:"+qual_val+" in var dec: "+cd+", in constraint: "+comp); 	
    								}
    								
    								Object label_key = label_cd.sfParent().sfAttributeKeyFor(label);
    								CoreSolver.getInstance().addAutoVar(label_key, fv);
	    						}
	    					} else if ((qual_val=cd.sfContext().get(ConstraintConstants.DEFVAR))!=null){
	    						fv.setDefVal(qual_val);
	    					} else if (cd.sfContext().get(ConstraintConstants.USERVAR)!=null){
	    						isuservars=true;
    							orgContext.sfAddTag(key, ConstraintConstants.USERVAR_TAG);
	    					}
	    						
	    					
	    					//System.out.println("AUTOEFFECTCDPARENT"+cd);
	    						
	    					//effects
	    					ComponentDescription autoEffectsCD=null;
	    					try {autoEffectsCD = (ComponentDescription) cd.sfContext().get(ConstraintConstants.AUTOEFFECTS); }
	    					catch (ClassCastException cce){/**do**/}
	    					
	    					if (autoEffectsCD!=null){
	    						//System.out.println("AUTOEFFECTCD"+autoEffectsCD);
	    						
	    						Enumeration ae_enum = autoEffectsCD.sfContext().keys();
	    						
	    						Vector<Reference> autoEffects = new Vector<Reference>();
		    					while (ae_enum.hasMoreElements()){
		    						try { autoEffects.add(((SFReference) autoEffectsCD.sfContext().get(ae_enum.nextElement())).sfAsReference()); }
									catch (ClassCastException cce){ throw new SmartFrogFunctionResolutionException("Policy for autovar is not Reference in var dec: "+cd+", in constraint: "+comp); }
		    					}
		    					fv.setAutoEffectCD(autoEffectsCD);
		    					fv.setAutoEffects(autoEffects);
	    					}
	    								    			
		    				fv.constructRange(cd);
		    				fv.setConsEvalKey(key);
		    				
		    				
		    				val=fv;  //so that freevar gets added instead...
		    				//Insert this new FreeVar...
		    				orgContext.put(key, fv);
		    				
			    			
		    				//System.out.println("In In In4"+key+":"+val+":"+orgContext);
	    					
	    				}		    				
	    			}
	    			
	    			attrs.add(key);  
    				values.add(val);
    				//System.out.println("Just Added"+key+":"+val);
	    		}
    		} catch (Exception e){/**Shouldn't happen*/}
    	}
    	    	   
    	//System.out.println("222");
    	
    	//Sort the goal in lex order
    	Collections.sort(goal_attrs);   	
    	
    	//Construct goal
    	Iterator goal_iter = goal_attrs.iterator();
    	while (goal_iter.hasNext()) {
    		goal.add(orgContext.get(goal_iter.next()));
    	}    	
    	
    	//Add empty goal if no goal...
    	if (goal_attrs.size()==0) goal.add("nil");
    	
    	//Construct constraint context...
    	ConstraintContext cc = new ConstraintContext((ComponentDescription)rr, comp, arkey, ar, ret_key);
    	
    	//System.out.println("333");
    	
    	//Solve goal
    	try {
    	   CoreSolver.getInstance().solve(cc, attrs, values, goal, autos, isuservars, assigns);
    	} catch (Exception e){ 
    		e.printStackTrace();
    		////System.out.println("WE ARE IN ERROR!!!");
    	    throw new Error("Error in solving constraints:"+e+" in: "+orgContext);
    	}   
        	
    	//VAR effects...
    	try {
    		//System.out.println("Applying freevar effects..."+cc.getFVs());
    		
	    	Vector<FreeVar> fvs = cc.getFVs();
	    	for (FreeVar fv: fvs){
	    		fv.applyAutoEffects();
	    	}
    	} catch (SmartFrogResolutionException sfre){ throw  new SmartFrogFunctionResolutionException(sfre);}
    	
    	ConstraintContext cc_new = CoreSolver.getInstance().hasBacktrackedTo();
    	if (cc_new!=null) { 
    		cc=cc_new;
    		assignContext(cc);	
    	}
    	boolean hasBacktracked = (cc_new!=null);
    	
    	//System.out.println("�1");
    	
    	////System.out.println("HAS BACKTRACKED!!!"+hasBacktracked);
    	
    	//Mark (poss. backtracked) constraint as done...
    	CoreSolver.getInstance().setShouldUndo(true);
    
    	if (!Proposition.getResult()) { 	
    		////System.out.println("WE ARE EVALLING PRPS!");
    		if (arkey!=null) ((ComponentDescription)rr).sfContext().put(arkey, comp);  //this will be undone if backtracking occurs...
			
			
			boolean backtracked=Proposition.evaluatePropositions(false); //to backtrack? rename
			////System.out.println("Post backtracking"+backtracked);
	    	while (backtracked){
	    		////System.out.println("Failing...");
	    		try {CoreSolver.getInstance().fail();} catch (Exception e){/***/}
	    		////System.out.println("Reevaling props...");    
	    		
	        	cc_new = CoreSolver.getInstance().hasBacktrackedTo();
	        	if (cc_new!=null) { 
	        		cc=cc_new;
	        		assignContext(cc);	
	        	}
	        	hasBacktracked = (cc_new!=null);
	        	
	        	//fileAggregates(comp);
	        	if (arkey!=null) ((ComponentDescription)rr).sfContext().put(arkey, comp);
	    		backtracked=Proposition.evaluatePropositions(true);
	    	}
	    	
	    	//Write back apply reference for now...
	    	////System.out.println("Writing back...");
	    	if (arkey!=null) ((ComponentDescription)rr).sfContext().put(arkey, ar); 
    	}
    	
    	//We're done...
    	orgContext.put(ConstraintConstants.FunctionClassStatus, ConstraintConstants.FCS_DONE);
    	
    	//System.out.println("�3");
    	
    	
    	CoreSolver.getInstance().setShouldUndo(false);
   	
    	//System.out.println("�4");
    	
    	
		if (hasBacktracked) {
			CoreSolver.getInstance().resetDoneBacktracking();
			throw new SmartFrogConstraintBacktrackError();
		}
    
		ret_key=cc.getRetKey();
		
		//System.out.println("�5"+ret_key);
    	
		
		
    	Object ret_val = (ret_key!=null?orgContext.get(ret_key):comp);
    	//System.out.println("RETURNING!!!"+ret_val);
    	
    	return ret_val; 
    }

    void unify(Arguments arguments, Object val, HashMap<FreeVar, Object> assigns) throws SmartFrogFunctionResolutionException  {
    	//System.out.println("Pre-specified value:"+val);
    	
    	String error_s = "aggregated value attribute has illegal pre-specfied value:"+val;
    	if (val==null || val instanceof SFNull) return;  //Nothing to do...
    	if (!(val instanceof Vector)) throw new SmartFrogFunctionResolutionException(error_s+", not a Vector!");
    	
    	Vector val_vec = (Vector) val;
    	for (int i=0; i<val_vec.size();i++){
    		Object vitem = val_vec.get(i);
    		////System.out.println("VITEM!!!"+vitem);
    		
    		if (!(vitem instanceof Vector)) {
    			////System.out.println("Thrown VITEM!!!"+vitem);
    			throw new SmartFrogFunctionResolutionException(error_s+", should be a vector of [loc,value] pairs"); //items should be a vec of [loc, value], we ignore val...
    		}
    		Vector vitem_vec = (Vector) vitem;
    		
    		if (vitem_vec.size()!=2) {
    			////System.out.println("Thrown VITEM!!!"+vitem);
    			throw new SmartFrogFunctionResolutionException(error_s+", should be a vector of [loc,value] pairs"); //wrong size, so ignore val...
    		}
    		
    		Object loc = vitem_vec.get(0);
    		Object vvalue = vitem_vec.get(1);
    		Object avalue = arguments.get(loc);
    		
    		if (avalue instanceof FreeVar){
    			//if (vvalue instanceof SFNull) vvalue=new FreeVar();
    			////System.out.println("Location:"+loc+":"+avalue.toString()+":"+vvalue.toString());
    			assigns.put((FreeVar)avalue, vvalue);
    		} else if (!avalue.equals(vvalue)) throw new SmartFrogFunctionResolutionException(error_s+", ununifiable values");	
    	}
    }
    
    
    //The next two methods are not currently used, but may be at some point and are left in as such...
    void unify_(Vector arguments, Object val, Vector goal){
    	
    	if (!(val instanceof Vector)) return;  //Not unifiable on type
    	
    	String goal_s=unify_vec(arguments, (Vector)val);
    	
    	if (goal_s!=null) goal.add(goal_s);
    }
    
    String unify_vec(Vector arguments, Vector val_vec){
    	String goal_s=null;	
    	if (val_vec.size()!=arguments.size()) return null;  //Not unifiable on size
    	
    	for (int i=0; i<val_vec.size();i++){
    		Object vitem = val_vec.get(i);
    		Object aitem = arguments.get(i);
    		if (vitem instanceof Vector && aitem instanceof Vector) {
    			String goal_s1 = unify_vec((Vector) aitem, (Vector) vitem);
    			if (goal_s1==null) return null;
    			if (goal_s!=null) goal_s += ", "+goal_s1; 
    			else goal_s=goal_s1;
    		} else if (aitem instanceof FreeVar) {
    			String goal_s1 = aitem.toString()+"="+vitem.toString();
    			if (goal_s!=null) goal_s += ", "+goal_s1; 
    			else goal_s=goal_s1;
    		} else if (!(aitem.equals(vitem))) return null;
    	}
    	if (goal_s!=null) return goal_s;
    	else return "";
    }
    	
    void assignContext(ConstraintContext cc){
    	if (cc!=null){
    		ar=cc.getAR();
    		comp=cc.getCD();
    		rr=cc.getParent();
    		arkey=cc.getKey();
    		orgContext=comp.sfContext();
    	}
    }
    
    public static void getAggregateSources(ComponentDescription comp, Vector<CompositeSource> css) throws SmartFrogFunctionResolutionException{
    	System.out.println("YESYES");
    	
    	Context context = (Context) comp.sfContext().copy();
		Enumeration en = context.keys();
		Object key=null;
		
		System.out.println("Comp"+comp);
		
		Object array = null;
		Object path = null;
		Object cinfo = null;
		HashMap cihm = new HashMap();
		
		cinfo = context.remove(ConstraintConstants.CONTEXT);
		array = context.remove(ConstraintConstants.ARRAY);	
		path = context.remove(ConstraintConstants.PATH);
		
		System.out.println("111");
		
		
		if (path!=null && path!=SFNull.get()){  //mandatory for aggregates and updates...
		
			System.out.println("222");
			
			if (array!=SFNull.get()){
				Reference array_ref=null;
				
				//"Hand" resolve
		    	if (!(array instanceof Reference)) throw new SmartFrogFunctionResolutionException("array attribute: "+array+" in comp: "+comp+" should be a reference");	
		    	
		    	if (array instanceof SFReference) {
		    		try {array_ref=((SFReference)array).sfAsReference();}
		    		catch (SmartFrogCompilationException sfce){throw new SmartFrogFunctionResolutionException(sfce);}
		    	} else array_ref= (Reference) array;
		    	
		    	try { array = resolve(comp, array_ref); } catch (Exception e) {throw new SmartFrogFunctionResolutionException("Can not resolve array ref:"+array_ref+" in: "+comp);}
				if (!(array instanceof ComponentDescription) && !(array instanceof Prim)) throw new SmartFrogFunctionResolutionException("array ref:"+array_ref+" in: "+comp+" does not resolve to a Prim/ComponentDescription");	
			} else array=comp;
		
			System.out.println("333");
			
			if (!(path instanceof Reference)) throw new SmartFrogFunctionResolutionException("path in comp: "+comp+" must be a Reference");
			
			//Get all the context info together...
			if (cinfo!=null) {
				//Must be a component description
				ComponentDescription cinfo_cd = (ComponentDescription) cinfo;  /*Elaborate*/
				Enumeration cienum = cinfo_cd.sfContext().keys();
				while (cienum.hasMoreElements()){
					Object cikey = cienum.nextElement();
					cihm.put(cikey, cinfo_cd.sfContext().get(cikey));
				}
			} //we assume that if it were present it would be as a cd...
			
			System.out.println("444");
			
			
			//Prefix...
			String prefix = null;
			try { prefix = (String) resolve(comp,ConstraintConstants.PREFIX); } 
			catch (Exception e){
				if (array!=null) throw new SmartFrogFunctionResolutionException("Unable to resolve prefix in comp: "+comp+" must be a String"); 
			}
			context.remove(ConstraintConstants.PREFIX);
			
			System.out.println("555");
			
			
			Enumeration restKeys = context.keys();
			while (restKeys.hasMoreElements()){
				Object restKey = restKeys.nextElement();
				Object restVal = context.get(restKey);
				if (!(restVal instanceof ComponentDescription)) continue;
				
				ComponentDescription restComp = (ComponentDescription) restVal;
				
				if (restComp.sfContext().get(ConstraintConstants.AGG_SPEC)!=null){
					Object unify = restComp.sfContext().get(ConstraintConstants.UNIFY);
					
					System.out.println("ADDING:::"+cihm+":"+array+":"+prefix+":"+path+":"+restKey+":"+restComp+":"+unify);
			       	CompositeSource cs = new CompositeSource(cihm, 
			       												array, 
			       												(String)prefix, 
			       												(Reference)path, 
			       												restKey,
			       												restComp, 
			       												unify);
			    		css.add(cs);
					
				}
			}
			
		}				
		//System.out.println("Leaving getCS");
    } 
        
    static void extractArgumentsFromSource(CompositeSource cs) throws SmartFrogFunctionResolutionException {
    	Object cssource = cs.source;
    	Prim p = (cssource instanceof Prim?(Prim)cssource:null);
    	ComponentDescription c = (cssource instanceof ComponentDescription?(ComponentDescription)cssource:null);
    	
    	
    	Enumeration en = null; 
    	try {en=(p!=null?p.sfContext():c.sfContext()).keys();} catch (Exception e){/*Shouldn't happen*/}
    	while (en.hasMoreElements()){
    		
    		String key = en.nextElement().toString();
        	
    		if (key.startsWith(cs.prefix)){
    			////System.out.println("In with the prefix...");
    			
    			Object source = null;
    			try { source = resolve(cssource, key); } catch (Exception e) {throw new SmartFrogFunctionResolutionException("Can not resolve source ref:"+key+" in: "+source);}	
    			p = (source instanceof Prim?(Prim)source:null);
    	    	c = (source instanceof ComponentDescription?(ComponentDescription)source:null);
    	    
    	    	
    			if (p!=null || c!=null){
    		
    				Context context = null; 
    				try {context=(p!=null?p.sfContext():c.sfContext());}catch (Exception e){/*Shouldn't happen*/}
    				
    				Object loc=null;
    				loc = context.get(ConstraintConstants.INDEX);
    				if (loc==null) {
    					int i=0;
    					while (true){
    						String index = ConstraintConstants.INDEX+i;
    						Object loc1 = context.get(index);
    						if (loc1==null) break;
    						else if (loc==null) loc = new Vector();
    						((Vector)loc).add(loc1); 
    						i++;
    					}
    				}
    				
    				if (loc==null) throw new SmartFrogFunctionResolutionException("No location information available in extracting aggregation from array:"+cssource); 
    				
    				////System.out.println("111"+cs.context);
    				
    				//Add resolving context...
    				Iterator keys = cs.context.keySet().iterator();
    				while (keys.hasNext()){
    					Object ckey = keys.next();
    					Object cval = cs.context.get(ckey);
    					try {
    					if (c instanceof Prim) ((Prim)c).sfReplaceAttribute(ckey, cval);
    		    		else ((ComponentDescription)c).sfReplaceAttribute(ckey, cval);
    					} catch(Exception e){/*Shouldn't happen*/}
    				}
    				
    				////System.out.println("222"+c.sfContext());
    				
    				////System.out.println("Source not null...");
    				
    				//Get the update record and pull out pred...
    				Reference pred = null;
    				try { pred = (Reference) cs.update.sfContext().get("pred"); }
    				catch (ClassCastException cce){/*Do nothing*/}
    				if (pred!=null){
    					////System.out.println("Pred not null..."+cs.pred);
    					//Reference pred = cs.pred.copyandRemoveLazy();
    					try {
    					if (pred instanceof SFReference) pred=((SFReference) pred).sfAsReference();
    					} catch (SmartFrogCompilationException sfce){ throw new SmartFrogFunctionResolutionException(sfce);}
    					////System.out.println("Pred not null..."+pred);
    					
    					Object eval_pred = null;
    					try {
    						eval_pred = resolve(p,c,pred);
    					} catch (Exception e)
    					{
    						////System.out.println("FART::::"+e);
    						/*Intentionally Leave*/
    					}
    					if (eval_pred!=null && eval_pred instanceof Boolean) {
    						////System.out.println("Is pred false?..."+eval_pred);
    						if (!((Boolean)eval_pred).booleanValue()) continue; //round while...
    					}
    					else if (eval_pred==null || !(eval_pred instanceof SFNull)) throw new SmartFrogFunctionResolutionException("In extracting values as per source, pred "+pred+" should yield Boolean from: "+source);
    				}
    				////System.out.println("We have a match...");
    				cs.source=source;		
    				ComponentResolution cr = getComponentResolution(cs.source,cs.path);
    	    		cs.arguments.put(loc, cr.val);
    				if (cr.val instanceof FreeVar) cs.freevars=true;
    				
    				////System.out.println("And the other side...1");
    				//Remove resolving context...
    				keys = cs.context.keySet().iterator();
    				while (keys.hasNext()){
    					Object ckey = keys.next();
    					Object cval = cs.context.get(ckey);
    					try {
    					if (c instanceof Prim) ((Prim)c).sfRemoveAttribute(ckey);
    		    		else ((ComponentDescription)c).sfRemoveAttribute(ckey);
    					} catch(Exception e){/*Shouldn't happen*/}
    				}
    				////System.out.println("And the other side...2");
    			}
    			
    		}
    	}    	
    }
        
    static public class ComponentResolution {
    	Object pc;
    	Object key;
    	Object val;
    	
    	public Object getpc(){return pc;}
    	public Object getkey(){return key;}
    	public Object getval(){return val;}
    	
    	ComponentResolution(){}
    	ComponentResolution(Object pc, Object key, Object val){ this.pc=pc; this.key=key; this.val=val;}
    }
    
    static public ComponentResolution getSourceKey(Reference ref, Object source) throws SmartFrogResolutionException {
    	ComponentResolution cr = new ComponentResolution();
    	  	
    	if (ref instanceof SFReference) {
    		try {ref=((SFReference)ref).sfAsReference();}
    		catch (SmartFrogCompilationException sfce){throw new SmartFrogFunctionResolutionException(sfce);}
    	}
    	
    	ReferencePart last = ref.lastElement();
    	
    	//System.out.println("GSK...");
    	//System.out.println("1+source+_ref"+source+ref_);
    	
    	if (!(last instanceof HereReferencePart)) throw new SmartFrogResolutionException("Last part of reference:"+ref+" should be a HereReferencePart");
    	cr.key = ((HereReferencePart) last).getValue();
    	
    	//System.out.println("2");
    	
    	if (ref.size()>1){
    		ref.removeLastElement();
    		try {
    			cr.pc = resolve(source,ref); 
    		} catch (Exception e) { throw new SmartFrogFunctionResolutionException("No such path/ref: "+ref+" from source: "+source, e);}
    		if (!(cr.pc instanceof Prim || cr.pc instanceof ComponentDescription)) throw new SmartFrogResolutionException("No such path/ref: "+ref+" from source: "+source);
    	} else cr.pc=source;
    	
    	//System.out.println("GSK3..."+cr.pc+":"+source);    	
    	

    	return cr;
    }
    
    static private ComponentResolution getComponentResolution(Object source, Reference path_) throws SmartFrogFunctionResolutionException {
    	ComponentResolution cr = null;
    	try {
    		cr = getSourceKey(path_, source);    
    		Reference ref = new Reference(ReferencePart.here(cr.key));
    		cr.val = cr.pc instanceof Prim?((Prim)cr.pc).sfResolve(ref):((ComponentDescription)cr.pc).sfResolve(ref);
    	}catch (Exception e ){/*Shouldn't happen*/}	
    	return cr;
    }
    

    static Object resolve(Object so, Reference r) throws Exception {
    	Prim p = (so instanceof Prim? (Prim)so: null);
    	ComponentDescription c = (so instanceof ComponentDescription? (ComponentDescription)so: null);
    	return resolve(p, c, r);
    }
    
    static Object resolve(Object so, String s) throws Exception {
    	Prim p = (so instanceof Prim? (Prim)so: null);
    	ComponentDescription c = (so instanceof ComponentDescription? (ComponentDescription)so: null);
    	return resolve(p, c, s);
    }
    
    static Object resolve(Prim p, ComponentDescription c, Reference r) throws Exception {
    	////System.out.println("Forcing laziness...");
    	Object v=(p!=null?p.sfResolve(r):c.sfResolve(r));
    	return v;
    }
    
    static Object resolve(Prim p, ComponentDescription c, String s) throws Exception {
    	////System.out.println("Forcing laziness...");
    	Object v=(p!=null?p.sfResolve(s):c.sfResolve(s));
    	return v;
    }
    
    static Object resolveParent(Object c){
    	try {
    	if (c instanceof Prim){
    		return ((Prim)c).sfResolveParent();
    	} else if (c instanceof ComponentDescription) {
    		return ((ComponentDescription)c).sfResolveParent();
    	} 
    	} catch (Exception e){/*Shouldn't happen*/}
    	return null;
    }
    
    static Object resolveKey(Object k, Object c) {
    	Reference r = new Reference(ReferencePart.here(k));
    	Object val=null;
    	try {
    	  val = resolve(c,r);
    	} catch (Exception e){/*Shouldn't happen*/}
    	return val;
    }
        
    static void replaceAttribute(Object k, Object v, Object c){
    	try {
    		if (v instanceof SFNull) v=null;
    		if (g_updateContext!=null) g_updateContext.add(new ComponentResolution(c,k,v));
    		else replaceAttributeWkr(k, v, c);
    	} catch (Exception e){/*Shouldn't happen*/}
    }

    static void replaceAttributeWkr(Object k, Object v, Object c){    	
    	if (c instanceof Prim) replaceAttributeWkrPrim(k, v, (Prim)c);  
		else replaceAttributeWkrComp(k, v, (ComponentDescription)c);
    }
    
    static void replaceAttributeWkrPrim(Object k, Object v, Prim p){    
    	try{
    		if (v!=null) p.sfReplaceAttribute(k, v);
    		else p.sfRemoveAttribute(k);
    		
    		if (v instanceof ComponentDescription){
    			((ComponentDescription)v).setPrimParent(p);
    		}
    		
    	}catch(Exception e){/**/}
    }
    
    static void replaceAttributeWkrComp(Object k, Object v, ComponentDescription c){  
    	try{
    		/*try {
    		System.out.println("PARENT:"+c.sfParent());
    		System.out.println("PARENT:PARENT"+c.sfParent().sfParent());
    		}catch(Exception e){/**}*/
    		
    		if (v!=null) c.sfReplaceAttribute(k, v);
    		else c.sfRemoveAttribute(k);
    		
    		if (v instanceof ComponentDescription){
    			((ComponentDescription)v).setParent(c);
    		}
    		
    	}catch(Exception e){/**/}
    }
    
    static private Vector<ComponentResolution> g_updateContext; 
    static public boolean isUpdateContextLocked(){
    	return (g_updateContext!=null);
    }
    static public void lockUpdateContext(){
    	if (g_updateContext==null) g_updateContext= new Vector<ComponentResolution>();
    }
    static public void applyUpdateContext(){
    	if (g_updateContext!=null){
    		for (int i=0;i<g_updateContext.size();i++){
    			ComponentResolution cr = g_updateContext.get(i);
    			
    			System.out.println("Replacing...1"+cr.key+":"+cr.val+":"+cr.pc);
    			
    			replaceAttributeWkr(cr.key, cr.val, cr.pc);
    		}
    	}
    	g_updateContext=null;
    }
    
    /**
     * Unchecked error. Used in popping call stack to get back to linkResolve()
     * @author anfarr
     *
     */
    public static class SmartFrogConstraintBacktrackError extends Error{};
    
    /**
     * Checks whether string "sent" to Eclipse is "legal"...  
     * @param val String to be checked
     * @return Whether legal
     */
    private boolean isLegal(String val){
    	if (val.indexOf(0x21)>-1) return false;
    	if (val.indexOf(0x22)>-1) return false;
    	if (val.indexOf(0x23)>-1) return false;
    	if (val.indexOf(0x24)>-1) return false;
    	if (val.indexOf(0x25)>-1) return false;
    	if (val.indexOf(0x26)>-1) return false;
    	if (val.indexOf(0x27)>-1) return false;
    	if (val.indexOf(0x28)>-1) return false;
    	if (val.indexOf(0x29)>-1) return false;
    	if (val.indexOf(0x2A)>-1) return false;
    	if (val.indexOf(0x2B)>-1) return false;
    	if (val.indexOf(0x2C)>-1) return false;
    	if (val.indexOf(0x2D)>-1) return false;
    	if (val.indexOf(0x2E)>-1) return false;
    	if (val.indexOf(0x2F)>-1) return false;	
    	return true;
    }
        	
    public static boolean leaveResolve(ComponentDescription comp, Object name){
    	boolean skip=false;	
    	try {
    		
    		//Because we don't want these attributes to be pre-evaluated in an SF/Apply Reference
    		//Nothing to do with laziness, we just want control over when it happens...
    		
    		//if (override && ...)  skip=true;
            /*else*/ skip = comp.sfContainsTag(name, ConstraintConstants.IGNORE_TAG);
            
            } catch (SmartFrogContextException sfce) {/*Shouldn't happen*/}
    	
    	return skip;
    }
}
