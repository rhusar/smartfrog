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
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintConstants;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.constraints.FreeVar;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintResolutionState.ConstraintContext;
import org.smartfrog.sfcore.languages.sf.constraints.propositions.Proposition;
import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;
import org.smartfrog.sfcore.prim.Prim;
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
		Object update;
		String prefix; 
		Reference pred;
		Object key;
		Object retval;
		Arguments arguments=new Arguments();
		boolean freevars=false;
		int idx=-1;
		
		CompositeSource(HashMap context, Object source, String prefix, Reference path, Reference pred, Object key, Object update){
			this.context=context;
			this.source=source;
			this.prefix=prefix;
			this.update=update;
			this.key=key;
			this.path=path;
			this.pred=pred;
		}
		
		public String toString(){
			return ""+context+":"+source+":"+prefix+":"+path+":"+pred+":"+key+":"+update+":"+arguments+":"+freevars+":"+idx;
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
    	
    	//System.out.println("In constraint...");
    	
    	if (!CoreSolver.getInstance().getConstraintsPossible()) return comp; 
    	
		CoreSolver.getInstance().setShouldUndo(true);

		Vector<CompositeSource> aggs=new Vector<CompositeSource>();
		getCompositeSources(comp, aggs, null, true);
		
		for (int i=0;i<aggs.size();i++){
			CompositeSource cs = aggs.get(i);
	    	
	    	//System.out.println("%%%%%%%%%%%%%%%%%%%%%%"+cs+orgContext.get(cs.key));
	    	
	    	extractArgumentsFromSource(cs);		    	
	    	
	    	//System.out.println("+++++++++++++++++++++"+cs.key+":"+cs.arguments);
	    	
	    	//Unify arguments...
	    	Object value = orgContext.get(cs.key);
	    	unify(cs.arguments, value, assigns);
	    	String csargs = cs.arguments.toString();
	    	//System.out.println("CSARGS!!!"+cs.key+":"+csargs);
	    	orgContext.put(cs.key, cs.arguments.getArgs());
	    	
	    	try {
	    		if (cs.freevars) orgContext.sfAddTag(cs.key, "sfFreeVars");	
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

    			if (orgContext.sfContainsTag(key, "sfReturn")) ret_key=key;
    			
    			if (orgContext.sfContainsTag(key, "sfConstraint")) goal_attrs.add(key);
    			else { 
    				
    				if (val instanceof String && !isLegal((String)val)) continue;	

	    			//Set the attribute name originating this FreeVar
	    			if (val instanceof FreeVar) {
	    				FreeVar fv = (FreeVar) val;
	    				if (fv.getConsEvalKey()==null) fv.setConsEvalKey(key);
	    				
	    				//Make sure range is appropriated in free var
	    				fv.constructRange(comp);
	    				
	    				if (orgContext.sfContainsTag(key, "sfConstraintAutoVar")) autos.add(key);
		    			else if (orgContext.sfContainsTag(key, "sfConstraintUserVar")) isuservars=true;
	    				
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
    							orgContext.sfAddTag(key, "sfConstraintUserVar");
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
    
    	//File preliminary results...
    	//fileAggregates(comp);
    	
    	//Do propositions
		//Speculatively, write the constraint in to its future home, to assess propositions...
    	////System.out.println("KEYKEY:"+arkey+":"+comp+":"+orgContext);
    	
    	//System.out.println("�2");
    	
    	
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
    	orgContext.put("sfFunctionClassStatus", "done");
    	
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
    	if (val instanceof SFNull) return;  //Nothing to do...
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
    
    void fileAggregates(ComponentDescription comp) throws SmartFrogFunctionResolutionException {
		Vector<CompositeSource> aggs=new Vector<CompositeSource>();
		getCompositeSources(comp, aggs, null, true);
		
		for (int i=0;i<aggs.size();i++){
			CompositeSource cs = aggs.get(i);
	    	try {
				if (orgContext.sfContainsTag(cs.key, "sfFreeVars")){
					////System.out.println("UPDATE UPDATE:"+cs.key+":"+cs.update);
		        	if (cs.prefix==null) updateValue(cs);
		        	else updateArrayOfValues(cs);	
				}
	    	} catch (SmartFrogContextException context){/*Shouldn't happen*/}
		}	
    }
    
    public static void getCompositeSources(ComponentDescription comp, Vector<CompositeSource> cs, java.util.Vector<Object> other, boolean mpred) throws SmartFrogFunctionResolutionException{
    	Context context = comp.sfContext();
		Enumeration en = context.keys();
		Object key=null;
		
		////System.out.println("Comp"+comp);
		
		while ((key=getNextKey(en,comp))!=null){   
			//Context information firstly...
			HashMap contextInfo_hm = new HashMap();
			Object contextInfo = getTaggedValue("sfContext", key, comp);
			while (contextInfo!=null){
				contextInfo_hm.put(key, contextInfo);
				contextInfo = getTaggedValue("sfContext", getNextKey(en,comp), comp);
			}
			
	    	Object source = getTaggedValue("sfSource", getNextKey(en,comp), comp, false);
	    	
	    	//System.out.println("Source"+source);
	    	
	    	if (source==null) {
	    		/*Object val = getTaggedValue("sfReturn", key, comp);
	    		if (val!=null){  
	    			//sort ret val!
	    			g_retval=val;
	    		} else*/ if (other!=null) other.add(key);
	    		leftover=null;
	    		continue; //round while...
	    	}
	    	
	    	//"Hand" resolve
	    	if (!(source instanceof Reference)) throw new SmartFrogFunctionResolutionException("Tagged source: "+source+" in comp: "+comp+" should be a reference");	
	    	Reference source_ref=null;
	    	if (source instanceof SFReference) {
	    		try {source_ref=((SFReference)source).sfAsReference();}
	    		catch (SmartFrogCompilationException sfce){throw new SmartFrogFunctionResolutionException(sfce);}
	    	}
	    	else source_ref= (Reference) source;
	    	
	    	//System.out.println("SourceRef"+source_ref);
	    	
			Object prefix = getTaggedValue("sfPrefix", getNextKey(en,comp), comp);
			Object path = getTaggedValue("sfPath", getNextKey(en,comp), comp, false);    
			
			if (prefix!=null || path!=null) {
				try { source = resolve(comp, source_ref); } catch (Exception e) {throw new SmartFrogFunctionResolutionException("Can not resolve source ref:"+source_ref+" in: "+comp);}
				if (!(source instanceof ComponentDescription) && !(source instanceof Prim)) throw new SmartFrogFunctionResolutionException("Source ref:"+source_ref+" in: "+comp+" does not resolve to a Prim/ComponentDescription");
			} else {
				path=source;
				source=comp;
			}
			
			Object pred = null;
			Object update = null;
						
			//System.out.println("PREFIX:PATH"+prefix+path);
			
			if (path!=null && (!(path instanceof Reference))) throw new SmartFrogFunctionResolutionException("Tagged path in comp: "+comp+" must be a Reference");
			
			boolean first=true;
			
			if (mpred){
				while (true){
					pred = getTaggedValue("sfPred", getNextKey(en,comp), comp, false);
					update = getTaggedValue("sfUpdate", getNextKey(en,comp), comp);
					
					//System.out.println("PRED:UPDATE"+pred+update);
					
					if (pred!=null && !(pred instanceof Reference)){
						if ((pred instanceof Boolean && ((Boolean)pred).booleanValue()) || pred instanceof SFNull) pred=null;
						else throw new SmartFrogFunctionResolutionException("Tagged pred: "+pred+" in comp: "+comp+" is not Reference or Boolean true");
					}
					
					if (pred!=null && update==null) throw new SmartFrogFunctionResolutionException("Tagged source in comp: "+comp+" with no update");
					
					if (pred==null && update==null){
						if (first) {
							//System.out.println("1Adding");
							CompositeSource comp_src = new CompositeSource(contextInfo_hm, source, (String)prefix, (Reference)path, null, null, null);
							cs.add(comp_src);
						}
						break;
					}
					
					first=false;				
					
					//System.out.println("2Adding");
					CompositeSource comp_src = new CompositeSource(contextInfo_hm, source, (String)prefix, (Reference)path, (Reference)pred, lastKey, update);
					cs.add(comp_src);
				}
			} else {
				//System.out.println("nextup...");
				
				pred = getTaggedValue("sfPred", getNextKey(en,comp), comp, false);
				update = getTaggedValue("sfUpdate", getNextKey(en,comp), comp);
				
				//System.out.println("PRED:UPDATE"+pred+update);
				
				if (pred!=null && !(pred instanceof Reference)){
					if ((pred instanceof Boolean && ((Boolean)pred).booleanValue())  || pred instanceof SFNull) pred=null;
					else throw new SmartFrogFunctionResolutionException("Tagged pred: "+pred+" in comp: "+comp+" is not Reference or Boolean true");
				}
				
				if (update==null) throw new SmartFrogFunctionResolutionException("Tagged source in comp: "+comp+" with no update");
				CompositeSource comp_src = new CompositeSource(contextInfo_hm, source, (String)prefix, (Reference)path, (Reference)pred, lastKey, update);
				cs.add(comp_src);
			}
		}
		
		//System.out.println("Leaving getCS");
		
    } 
    
    static private abstract class AggregationOp {
    	CompositeSource cs;
    	AggregationOp(CompositeSource cs){
    		this.cs=cs;
    	}
    	abstract void doIt(Object loc) throws SmartFrogFunctionResolutionException;
    }
    
    static private class ExtractOp extends AggregationOp{
    	ExtractOp(CompositeSource cs){ super(cs); }
    	void doIt(Object loc) throws SmartFrogFunctionResolutionException {
    		ComponentResolution cr = getComponentResolution(cs.source,cs.path);
    		cs.arguments.put(loc, cr.val);
			if (cr.val instanceof FreeVar) cs.freevars=true;
    	}
    }
    
    static private class UpdateOp extends AggregationOp{
    	UpdateOp(CompositeSource cs){ super(cs); cs.idx=0;}
    	void doIt(Object loc) throws SmartFrogFunctionResolutionException{
    		updateValue(cs);
    		cs.idx++;
    	}
    }
    
    static void doAggregationUpdate(AggregationOp ao) throws SmartFrogFunctionResolutionException {
    	CompositeSource cs = ao.cs;
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
    				loc = context.get("sfIndex");
    				if (loc==null) {
    					int i=0;
    					while (true){
    						String index = "sfIndex"+i;
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
    				if (cs.pred!=null){
    					////System.out.println("Pred not null..."+cs.pred);
    					//Reference pred = cs.pred.copyandRemoveLazy();
    					Reference pred = cs.pred;
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
    					else if (eval_pred==null || !(eval_pred instanceof SFNull)) throw new SmartFrogFunctionResolutionException("In extracting values as per source, pred "+cs.pred+" should yield Boolean from: "+source);
    				}
    				////System.out.println("We have a match...");
    				cs.source=source;
    				ao.doIt(loc);
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
    
    static void extractArgumentsFromSource(CompositeSource cs) throws SmartFrogFunctionResolutionException {
    	boolean freevar=false;
    	
    	////System.out.println("Extracting arguments from source...");
    	
    	/*SINGLE ARGUMENTS???
    	 * if (cs.prefix==null){
    		ComponentResolution cr = getComponentResolution(cs.source, cs.path);
    		cs.arguments.add(cr.val);
    		cs.freevars = (cr.val instanceof FreeVar);
    	}*/
    	
    	doAggregationUpdate(new ExtractOp(cs));
    	
    }
    
    static void updateArrayOfValues(CompositeSource cs) throws SmartFrogFunctionResolutionException {	
    	doAggregationUpdate(new UpdateOp(cs));
    }
    	
    
    static void updateValues(java.util.Vector<CompositeSource> css) throws SmartFrogFunctionResolutionException  {
    	for (int i=0; i<css.size(); i++){
        	CompositeSource cs = css.get(i);
        	if (cs.prefix==null) updateValue(cs);
        	else updateArrayOfValues(cs);
    	}
    }
        	       
    static void updateSimpleValue(Object source, Object key) throws SmartFrogFunctionResolutionException {
    	////System.out.println("updateSimpleValue("+key);
    	
    	Prim p = (source instanceof Prim?(Prim)source:null);
    	ComponentDescription c = (source instanceof ComponentDescription?(ComponentDescription)source:null);
    	
    	Object val = null;
    	
    	try {val = resolve(p,c,key.toString());} catch (Exception e){/*Intentionally Do Nothing*/}
		Object s = source;
		while (true){
    		s = resolveParent(s);
    		////System.out.println("s:::"+s);
    		if (s==null) throw new SmartFrogFunctionResolutionException("Failed to find non-tagged source: "+key+" in effects: "+s+" with no update");
    		Object cval = resolveKey(key,s);
    		////System.out.println("Value returned:"+cval+val);
    		if (cval!=null) {
    			replaceAttribute(key,val,s);
    			break;
    		}
    	}
		////System.out.println("done!");
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
    
    static void updateValue(CompositeSource cs) throws SmartFrogFunctionResolutionException {
    	//System.out.println("Update value!!!");
    	
    	ComponentResolution cr = getComponentResolution(cs.source, cs.path);
    	
    	//System.out.println("Past cr"+cr);
    	
    	Object update = cs.update;
    	
    	if (cs.idx>-1 && cs.update instanceof java.util.Vector){
    		java.util.Vector vec = (java.util.Vector) cs.update;
    		if (cs.idx<vec.size())  update = vec.get(cs.idx); 
    	}
    	
    	//System.out.println("Pre ra update:"+update);
    	if (update!=null && !(update instanceof FreeVar)) {
    		replaceAttribute(cr.key, update, cr.pc);
    		try {
    		String name = (cr.pc instanceof Prim? ((Prim)cr.pc).sfParent().sfAttributeKeyFor(cr.pc):
    			((ComponentDescription)cr.pc).sfParent().sfAttributeKeyFor(cr.pc)).toString();
    		//System.out.println("Replacing attribute:"+cr.key+" with: "+update+" in: "+name);
    		}catch(Exception e){/***/}
    	}
    	//System.out.println("Past ra"+cr);
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
    		if (g_updateContext!=null) g_updateContext.add(new ComponentResolution(c,k,v));
    		else if (c instanceof Prim) ((Prim)c).sfReplaceAttribute(k, v);
    		else ((ComponentDescription)c).sfReplaceAttribute(k, v);
    	} catch (Exception e){/*Shouldn't happen*/}
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
    			try {
    				if (cr.pc instanceof Prim) ((Prim)cr.pc).sfReplaceAttribute(cr.key, cr.val);
    				else ((ComponentDescription)cr.pc).sfReplaceAttribute(cr.key, cr.val);
    			}catch(Exception e){/*Shouldn't happen*/}
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
        
    static Object leftover=null;    
    static Object lastKey=null;
    
    static Object getNextKey(Enumeration e, ComponentDescription comp){
        return (lastKey=getNextKeyWkr(e, comp));
    }
    
    static Object getNextKeyWkr(Enumeration e, ComponentDescription comp){
    	//System.out.println("getNextKey");
		if (leftover!=null){
			Object ret=leftover;
			leftover=null;
			//System.out.println("return:"+ret);
			return ret;
		}
		
    	if (e.hasMoreElements()) {
    		Object ret= e.nextElement();
    		//System.out.println("return:"+ret);
    		return ret;
		} else return null;
    }
    
	static Object getTaggedValue(String tag, Object key, ComponentDescription comp){
		if (key==null) return null;
		////System.out.println("RESOLUTIONPRIOR:"+":"+comp+":"+tag+":"+key);
		try {
		if (comp.sfContainsTag(key, tag)){
			////System.out.println("GOING IN:"+":"+comp+":"+tag+":"+key);
			Object val = resolve(comp,key.toString());
			////System.out.println("RESOLUTION:"+tag+":"+key+":"+val);
			return val;
		} } catch (Exception e){/*Intentionally Leave*/}
		//finally{     	SFReference.resolutionForceEager=false;  
		//}
		leftover=key;
		return null;
	}
	
	static Object getTaggedValue(String tag, Object key, ComponentDescription comp, boolean resolve){
		if (key==null) return null;
		if (resolve) return getTaggedValue(tag, key, comp); 
		else try {
			////System.out.println("RESOLUTIONPRIOR:"+":"+comp+":"+tag+":"+key);
			if (comp.sfContainsTag(key, tag)){
				////System.out.println("GOING IN:"+":"+comp+":"+tag+":"+key);
				
				Object val = comp.sfContext().get(key);
				////System.out.println("RESOLUTION:"+tag+":"+key+":"+val);
				return val;
			} } catch (SmartFrogException e){/*Intentionally Leave*/}
			leftover=key;
			return null;
	}
	
    public static boolean leaveResolve(ComponentDescription comp, Object name){
    	boolean skip=false;	
    	try {
    		//if (override && ...)  skip=true;
            /*else*/ skip = comp.sfContainsTag(name, "sfSource") ||
            	        comp.sfContainsTag(name, "sfValuePath") ||
            	        comp.sfContainsTag(name, "sfPath") ||
                        comp.sfContainsTag(name, "sfPred") ||
                        comp.sfContainsTag(name, "sfPart") || 
                        comp.sfContainsTag(name, "sfPartRef");  
            
            } catch (SmartFrogContextException sfce) {/*Shouldn't happen*/}
    	
    	return skip;
    }
}
