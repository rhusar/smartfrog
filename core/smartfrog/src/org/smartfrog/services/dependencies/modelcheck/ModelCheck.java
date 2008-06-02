package org.smartfrog.services.dependencies.modelcheck;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.smartfrog.services.dependencies2.statemodel.state.State;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.languages.sf.functions.And;
import org.smartfrog.sfcore.languages.sf.functions.Implies;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;
import org.smartfrog.sfcore.reference.Reference;

/*TO DO: the list of stuff presented has been deferred as it is not sufficiently interesting to put in now
  It should be put in at some point however for better robustness.  Just quite tedious!
  
  (1) Attributes named in dependencies should be a subset of the orchestration state. It is an error otherwise but we don't currently check.
  (2) 
*/

public class ModelCheck implements PhaseAction {

	static String sfhome="";
	static String smv_file="";
	static String dat_file="";
	static String cpt_id=null;
	static String filePrefix="";
	
	static {
		sfhome = System.getenv("SFHOME");
		if (sfhome!=null){
			int dist = sfhome.indexOf("dist");
			if (dist>-1 && sfhome.length()==dist+4){
				sfhome = sfhome.substring(0,dist);
			}
			smv_file = sfhome+"/NuSMV/modelcheck.smv";
			dat_file = sfhome+"/NuSMV/modelcheck.dat";
			filePrefix = sfhome+"/NuSMV/vrun";
		} 
	}
	
	ComponentDescription model;
	HashMap<String,ComponentDescription> components = new HashMap<String,ComponentDescription>();
	Vector<ComponentDescription> modelTerminators = new Vector<ComponentDescription>();
	PrintWriter pw;
	int transition_count;
	Vector<String> main_decs = new Vector<String>();
	String mt_terminate_str=null;
	Vector<ComponentDescription> verificationRecords = null;
	
	
	public void doit() throws SmartFrogResolutionException {
        if (sfhome==null) throw new SmartFrogResolutionException("SFHOME ENVIRONMENT VARIABLE MUST BE SET...");
		
        try {
			GregorianCalendar gc = new GregorianCalendar();
			
			pw = new PrintWriter(new FileOutputStream(smv_file));
			pw.println("----Auto Generated: "+gc.getTime());
		
		    model.visit(new ModelVisitor(), true);
		    
		} catch (Exception e){
			return; //nothing fancy for now...
		}
        
		process_model();
		write_model();
		
		pw.close();
		
		boolean notFailed=true;
		
		NuSMVInterface nusmvi = new NuSMVInterface();
		nusmvi.runCheck();
		ModelCheckResults mcrs = nusmvi.getResults();
		int numResults = mcrs.numResults();
		for (int i=0; i<numResults; i++){
			boolean result=mcrs.getResult(i);
			if (i<2) {
				if (!result) notFailed = false;
				String check = (i==0?"DEADLOCK":"LIVELOCK");
				System.out.println("*"+i+"*"+check+" CHECK:"+(result?"PASSES":"FAILS"));
				if (!result) System.out.println("***See dump in file:"+ModelCheck.filePrefix+i);
				System.out.println("**********************************************");
			} else {
				if (!result) notFailed = false;
				ComponentDescription vr = (ComponentDescription) verificationRecords.get(i-2);
				vr.sfContext().put("result", new Boolean(result));
				vr.sfContext().put("failureRecord", formatFileStr(ModelCheck.filePrefix)+i);
				String proposition = (String) vr.sfContext().get("proposition");
				
				System.out.println("*"+i+"*"+proposition+":"+(result?"PASSES":"FAILS"));
				if (!result) {
					System.out.println("***Verification record:");
					System.out.println(vr.toString());
				}
				
			}
		}
		
		if (!notFailed) throw new SmartFrogResolutionException("Verification Run failure.  See foregoing output from parse for details.");
	

	}

	public String formatFileStr(String fs){
		String result = "";
		
		int i=0;
		while (i<fs.length()){
			char ch = fs.charAt(i);
			if (ch=='\\' || ch=='/'){
				if (i+1==fs.length() || (!(fs.charAt(i+1)=='\\' || fs.charAt(i+1)=='/'))) result+='/'; 
			} else result+=ch;
			i++;
		}
		
		return result;
	}
	
	public void forComponent(SFComponentDescription cd, String phaseName, Stack path) {
		this.model=cd;
	}

	private void process_model() throws SmartFrogResolutionException {
		//Process individual components...
		Iterator comp_iter = components.keySet().iterator();
		while (comp_iter.hasNext()){
			String key = (String) comp_iter.next();
			
			ComponentDescription  comp = (ComponentDescription) components.get(key);
			
			cpt_id = (String) comp.sfContext().get("sfUniqueComponentID");
			process_component(key, comp);
			cpt_id = null;
		}
		
		//Process termination conditions...
		process_termination_conditions();
	}
	
	private void process_termination_conditions() throws SmartFrogResolutionException {		
		Iterator mt_iter = modelTerminators.iterator();
		while (mt_iter.hasNext()){
			ComponentDescription mtcomp = (ComponentDescription) mt_iter.next();
			Object terminate_cond = mtcomp.sfContext().get("terminateCond");
			String terminate_str = parse_attribute(terminate_cond, mtcomp);
			
			if (terminate_str==null) continue;
			
			if (mt_terminate_str==null) mt_terminate_str = "("+terminate_str;
			else mt_terminate_str += " | "+terminate_str;
		}
		if (mt_terminate_str!=null) mt_terminate_str += ")";
	}
	
	private String getSFClass(ComponentDescription comp){
		if (comp.sfContext().get("sfUniqueComponentID")!=null) return State.class.getName();
		else return (String) comp.sfContext().get("sfClass");
	}
	
	private String[] process_external_dependency(ComponentDescription dep, ComponentDescription by) throws SmartFrogResolutionException {
		Reference on_ref = (Reference) ((Reference) dep.sfContext().get("on"));
		SFReference.resolutionForceEager = true;
		ComponentDescription on = (ComponentDescription) dep.sfResolve(on_ref); //Exc?
		SFReference.resolutionForceEager = false;
		String sfClass = getSFClass(on); 
		boolean includeOn = true;
		
		//Get "on" enablement...
		//Don't include State enablement, unless it is "asAndConnector"...
		if (sfClass.equals(State.class.getName())) includeOn = ((Boolean)on.sfContext().get("asAndConnector")).booleanValue();
			
		HashMap<String,Vector<String>> on_dep = null; 
		if (includeOn) on_dep = process_on_dependencies(on, sfClass);
		
		String on_dep_s = null;
		if (on_dep!=null) {
			Vector<String> on_dep_vec = on_dep.get("sfGlobalDependency");
			if (on_dep_vec!=null) on_dep_s = on_dep_vec.get(0);			
		}
		
		//Get "r->e"...
		String relevant_s = parse_attribute(dep.sfContext().get("relevant"), dep);
		String enabled_s = parse_attribute(dep.sfContext().get("enabled"), dep);
		String re_str = null;
		
		if (enabled_s!=null){
			if (relevant_s!=null){
				re_str = "("+relevant_s+Resolver.getFunctionRepresentation(Implies.class.getName())+enabled_s+")";
			} else re_str = enabled_s;
		}
		
		String[] dep_final_sa = null;
		
		if (re_str!=null || on_dep_s!=null){
			String dep_final_s = null;
			if (re_str!=null && on_dep_s!=null) dep_final_s = "("+on_dep_s+Resolver.getFunctionRepresentation(And.class.getName())+re_str+")";
			else if (re_str!=null) dep_final_s = re_str;
			else dep_final_s = on_dep_s;
			
			Object transition_obj = dep.sfContext().get("transition");
			String transition_s = null;
			if (transition_obj!=null && transition_obj instanceof String) transition_s = (String) transition_obj;
			
		    if (transition_s!=null) dep_final_sa = new String[]{transition_s, dep_final_s};
		    else dep_final_sa = new String[]{dep_final_s};
		}
		return dep_final_sa;
	}
	
	private HashMap<String,Vector<String>> process_on_dependencies(ComponentDescription comp) throws SmartFrogResolutionException {
		String sfClass = getSFClass(comp);
		return process_on_dependencies(comp, sfClass);
	}
	
	private HashMap<String,Vector<String>> process_on_dependencies(ComponentDescription comp, String sfClass) throws SmartFrogResolutionException {
		HashMap<String,Vector<String>> dependencies_map = null;
		
		Object dependencies_obj = comp.sfContext().get("sfDependencies");
		String operator = Resolver.getFunctionRepresentation(sfClass);
		String global_dep_s=null;
		
		if (dependencies_obj!=null && dependencies_obj instanceof Vector){  //Exc?
			Iterator deps_iter = ((Vector) dependencies_obj).iterator();
			while (deps_iter.hasNext()){
				ComponentDescription dep = (ComponentDescription) deps_iter.next(); //Exc?
				String[] dep_sa = process_external_dependency(dep, comp);
				if (dep_sa!=null){
					if (dep_sa.length==1){  //Not transition specific...
						if (global_dep_s==null) global_dep_s = dep_sa[0];
						else global_dep_s+=operator+dep_sa[0];
					} else if (dep_sa.length==2){  //Transition specific...
						String transition_s = dep_sa[0];
						if (dependencies_map==null) dependencies_map = new HashMap<String,Vector<String>>();
						Vector<String> entry = dependencies_map.get(transition_s);
						if (entry==null){
							entry = new Vector<String>();
							dependencies_map.put(transition_s,entry);
						}
						entry.add(dep_sa[1]);
					}
				}
			}
		}
		
		//Add global dependency, if extant...
		if (global_dep_s!=null) {
			global_dep_s = Resolver.getFinalFunctionRepresentation(sfClass, global_dep_s);
			if (dependencies_map==null) dependencies_map = new HashMap<String,Vector<String>>();
			Vector<String> global_dep_vec = new Vector<String>();
			global_dep_vec.add(global_dep_s);
			dependencies_map.put("sfGlobalDependency", global_dep_vec);
		}
		
		return dependencies_map;
	}
	
	private HashSet<String> otherComponents=null;
	private HashMap<String, Vector<String>> dependencies=null;
	private HashSet<String> attributes=null;
	private void process_component(String compname, ComponentDescription comp)  throws SmartFrogResolutionException {
		//Process dependencies...
		otherComponents = new HashSet<String>();
		
		dependencies = process_on_dependencies(comp);
		
		//Process transitions...
		transition_count=0;
		attributes = new HashSet<String>();
		Enumeration key_enum = comp.sfContext().keys();
		while (key_enum.hasMoreElements()){
			String tname = (String) key_enum.nextElement();
			Object to = comp.sfContext().get(tname);
			if (to instanceof ComponentDescription){
				ComponentDescription tcd = (ComponentDescription) to;
				if (tcd.sfContext().get("sfIsStateComponentTransition")==null) continue;
				transition_count++;
				process_transition(compname, tname, tcd);
			} else continue;
		}
		comp.sfContext().put("sfTransitionCount", new Integer(transition_count));
		
		Vector<String> others_vec = new Vector<String>(otherComponents);
		comp.sfContext().put("sfOtherComponents", others_vec);
		
		Vector attrs_vec = new Vector(attributes);
		comp.sfContext().put("sfStateData", attrs_vec);	
		
		attributes=null;
		dependencies=null;
		otherComponents=null;
	}
	
	private void process_transition(String compname, String tname, ComponentDescription tcomp)  throws SmartFrogResolutionException {
		Object dependency = tcomp.sfContext().get("dependency");
		if (dependency==null) throw new SmartFrogResolutionException("Dependency is null in transition:"+tname+" in state component:"+compname); 
		parse_dependency(compname, tname, tcomp, dependency);
		
		Object statefunction = tcomp.sfContext().get("statefunction");
		if (statefunction==null || (!(statefunction instanceof ComponentDescription))) throw new SmartFrogResolutionException("Statefunction is null in transition:"+tname+" in state component:"+compname); 
		parse_statefunction(compname, tname, tcomp, (ComponentDescription) statefunction);
	}
	
	private String parse_attribute(Object attr, ComponentDescription comp) throws SmartFrogResolutionException {
		String result=null;
		
		Resolver resolver = new Resolver();
		result = resolver.argumentToMCString(attr, comp);
		resolver.addInOtherComponents(otherComponents);
		return result;
	}
	
	private void parse_dependency(String compname, String tname, ComponentDescription tcomp, Object dep) throws SmartFrogResolutionException {			
		//This is the embedded dependency 
		String dep_s=parse_attribute(dep, tcomp);
		
		if (dependencies!=null){
			//Now also get externally exacted dependencies...
			//Transition specific...
			String tdeps_str = null;
			Object tdeps_obj = dependencies.get(tname);
			if (tdeps_obj!=null){
				Vector tdeps_vec = (Vector<String>) tdeps_obj;
				Iterator tdeps_iter = tdeps_vec.iterator();
				while (tdeps_iter.hasNext()){
					if (tdeps_str==null) tdeps_str = (String) tdeps_iter.next();
					else tdeps_str += Resolver.getFunctionRepresentation(And.class.getName())+tdeps_iter.next();
				}
			}
			
			//Non-transition specific...
			Object glob_deps_vec = dependencies.get("sfGlobalDependency");
			String glob_deps_str= null;
			if (glob_deps_vec!=null){
				glob_deps_str = (String) ((Vector) glob_deps_vec).get(0);
			}
			
			if (tdeps_str!=null){
				dep_s += Resolver.getFunctionRepresentation(And.class.getName())+tdeps_str;
			}
			if (glob_deps_str!=null){
				dep_s += Resolver.getFunctionRepresentation(And.class.getName())+glob_deps_str;
			}
		}
		
		if (dep_s!=null) tcomp.sfContext().put("sfDepString", dep_s);
	}
	
	private void parse_statefunction(String compname, String tname, ComponentDescription tcomp, ComponentDescription sfcomp) throws SmartFrogResolutionException {
		ComponentDescription sf_strs = new SFComponentDescriptionImpl(null, (SFComponentDescription) tcomp, new ContextImpl(), true);
		tcomp.sfContext().put("statefunction_strs", sf_strs);
		Enumeration keys = sfcomp.sfContext().keys();
		while (keys.hasMoreElements()){
			String key = (String) keys.nextElement();
			attributes.add(key);
			Object sf = sfcomp.sfContext().get(key);
			Object sf_out = null;
			if (sf instanceof ComponentDescription){ //Choice
				ComponentDescription sfcomp2 = (ComponentDescription) sf;
				sf_out = new Vector();
				Enumeration keys2 = sfcomp2.sfContext().keys();
				while (keys2.hasMoreElements()){
					String key2 = (String) keys2.nextElement();
					Object sf2 = sfcomp2.sfContext().get(key2);
					Object sf_out2 = parse_attribute(sf, sfcomp2);
					((Vector)sf_out).add(sf_out2);
				}
			} else sf_out = parse_attribute(sf, sfcomp); //Not choice
			
			sf_strs.sfContext().put(key, sf_out);
		}	
	}
	
	private void write_model() throws SmartFrogResolutionException {
		Iterator comp_iter = components.keySet().iterator();
		while (comp_iter.hasNext()){
			
			//COMPONENT HEADER: MODULE ...
			String key = (String) comp_iter.next();
			ComponentDescription sf_cd = components.get(key);
			pw.print("MODULE component_"+key);
			
			String main_s = key+" : process component_"+key;
			
			Vector others_vec = (Vector) sf_cd.sfContext().get("sfOtherComponents");
			if (others_vec.size()==0) {
				pw.println("");
				main_s+=";";
			}
			else {
				pw.print("(");
				main_s += "(";
				boolean first=true;
				Iterator viter = others_vec.iterator();
				while(viter.hasNext()){
					if (first) first=false;
					else {
						pw.print(", ");
						main_s += ", ";
					}
					
					String other = viter.next().toString(); 
					pw.print(other);
					main_s += other;
				}
				pw.println(")");
				main_s += ");";
			}
			main_decs.add(main_s);
			
			//COMPONENT VAR
			pw.println("VAR");
			
			Vector attrs_vec = (Vector) sf_cd.sfContext().get("sfStateData");
			Iterator attr_iter = attrs_vec.iterator();
			while (attr_iter.hasNext()){
				String akey = (String) attr_iter.next();
				String decl = "   "+akey+" : ";
				Object cur = sf_cd.sfContext().get(akey);
				if (cur==null){/*throw exception*/}
				if (cur instanceof Integer) decl+=" integer;";
				else if (cur instanceof Boolean) decl+=" boolean;";
				else ; /*throw */
				pw.println(decl);
			}
			Integer tc = (Integer) sf_cd.sfContext().get("sfTransitionCount");
			pw.println("   sfTransitionInProgress : 0.."+tc.intValue()+";");
			pw.println();
			
			//COMPONENT ASSIGN
			pw.println("ASSIGN");
			
			//init
			attr_iter = attrs_vec.iterator();
			while (attr_iter.hasNext()){
				String akey = (String) attr_iter.next();
				String init = "   init("+akey+") := ";
				Object cur = sf_cd.sfContext().get(akey);
				if (cur==null){/*throw exception*/}
				if (cur instanceof Integer) init+= cur.toString()+";";  
				else if (cur instanceof Boolean) init+= (((Boolean)cur).booleanValue()?"1":"0")+";"; 
				else ; /*throw */
				pw.println(init);
			}
			pw.println("   init(sfTransitionInProgress) := 0;");
			pw.println();
			
			//next
			//prepare...
			HashMap<String,Vector<String>> next_hm = new HashMap<String,Vector<String>>();
			
			attr_iter = attrs_vec.iterator();
			while (attr_iter.hasNext()){
				String akey = (String) attr_iter.next();
				Vector<String> next_vec = new Vector<String>();
				next_hm.put(akey, next_vec);	
			}
				
				
			//Cycle through transitions...
			Vector<String> transition_deps = new Vector<String>();
			Enumeration key_enum = sf_cd.sfContext().keys();
			int trans=0;
			while (key_enum.hasMoreElements()){
				String tname = (String) key_enum.nextElement();
				Object to = sf_cd.sfContext().get(tname);
				if (to instanceof ComponentDescription){
					ComponentDescription tcd = (ComponentDescription) to;
					if (tcd.sfContext().get("sfIsStateComponentTransition")==null) continue;
					trans++;
					
					transition_deps.add((String)tcd.sfContext().get("sfDepString"));
					ComponentDescription sfs_cd = (ComponentDescription) tcd.sfContext().get("statefunction_strs");
					
					//Go through each attribute adding transition...
					attr_iter = next_hm.keySet().iterator();
					while (attr_iter.hasNext()){
						String attr = (String) attr_iter.next();
						
						Object attr_obj = sfs_cd.sfContext().get(attr);
						String attr_str = null;
						if (attr_obj instanceof Vector){
							Vector attr_str_vec = (Vector) attr_obj;
							Iterator attr_str_iter = attr_str_vec.iterator();
							while (attr_str_iter.hasNext()) {
								String attr_str_add = (String)attr_str_iter.next();
								if (attr_str==null) attr_str = "{"+attr_str_add;
								else attr_str += ", "+attr_str_add;
							}
							attr_str+="}";
						} else attr_str = (String)attr_obj;
						
						Vector<String> next_vec = next_hm.get(attr);
						if (attr_str==null) next_vec.add("            sfTransitionInProgress="+trans+" : "+attr+";");
						else next_vec.add("            sfTransitionInProgress="+trans+" : "+attr_str+";");
					}
				} else continue;
			}

			attr_iter = attrs_vec.iterator();
			while (attr_iter.hasNext()){
				String akey = (String) attr_iter.next();
				Vector<String> next_vec = next_hm.get(akey);
				next_vec.add("            1 : "+akey+";");
				
				pw.println("   next("+akey+") := case");
				Iterator nv_iter = next_vec.iterator();
				while (nv_iter.hasNext()){
					pw.println(nv_iter.next());
				}
				pw.println("            esac;");
				pw.println();
			}				
				
			//next(sfTransitionInProgress)
			pw.println("   next(sfTransitionInProgress) := case");
			for (int ti=0;ti<transition_deps.size();ti++){
				pw.println("                       sfTransitionDependency"+(ti+1)+" & sfTransitionInProgress=0 : "+(ti+1)+";");
			}
			pw.println("                       1 : 0;");
			pw.println("                       esac;");
			pw.println();
			
			//DEFINE!!!
			pw.println("DEFINE");
			for (int ti=0;ti<transition_deps.size();ti++){
				pw.println("   sfTransitionDependency"+(ti+1)+" := "+transition_deps.get(ti)+";");
			}
			pw.print("   deadlock := sfTransitionInProgress=0 & ");
			for (int ti=0;ti<transition_deps.size();ti++){
				pw.print(" !sfTransitionDependency"+(ti+1)); 
				if (ti<transition_deps.size()-1) pw.print(" & ");
			}
			pw.println(";");
			
			pw.println();
			pw.println("FAIRNESS running");
			pw.println();
			
		}
		
		//MODEL
		pw.println("MODULE main");
		
		//VAR
		pw.println("VAR");
		Iterator main_iter = main_decs.iterator();
		while (main_iter.hasNext()){
			pw.println("   "+main_iter.next());
		}
		
		//DEFINE
		pw.println("DEFINE");
		pw.print("   deadlock := ");
		comp_iter = components.keySet().iterator();
		boolean first = true;
		while (comp_iter.hasNext()){
			if (first) first=false;
			else pw.print(" & ");
			pw.print(comp_iter.next().toString()+".deadlock");
		}
		pw.println(";");
		
		pw.println("   modelTerminated := "+(mt_terminate_str!=null?mt_terminate_str:"1")+";");
		
		//SPEC
		pw.println("SPEC AG(deadlock -> modelTerminated)");  //deadlock
		pw.println("SPEC AG EF modelTerminated");  //livelock
		if (verificationRecords!=null){
			for (int idx=1; idx<=verificationRecords.size();idx++){
				ComponentDescription vr = (ComponentDescription) verificationRecords.get(idx-1);
	
				//Need to break a verification proposition down based on {} curly braces...
				String vprop_str = (String) vr.sfContext().get("proposition");
				boolean ltl_spec = ((Boolean) vr.sfContext().get("ltl")).booleanValue();
				String vprop_out_str = "";
				while (true){
					int sidx=0;
					int eidx=-1;
					if ((eidx=vprop_str.indexOf('{'))>=0){
						//save up to {
						
						vprop_out_str += vprop_str.substring(sidx, eidx);
						int eidx2=vprop_str.indexOf('}');
						String ref_str = vprop_str.substring(eidx+1, eidx2);
						vprop_str = vprop_str.substring(eidx2+1);
						sidx=eidx2+1;
						
						Resolver resolver = new Resolver();
						vprop_out_str += resolver.argumentToMCString(Reference.fromString(ref_str), vr);
					} else break; //from while...
				}
				vprop_out_str += vprop_str;
				pw.println((ltl_spec?"LTL":"")+"SPEC "+vprop_out_str+";");			
			}
		}
	}
	
	private static int g_UCID=0;
	private class ModelVisitor implements CDVisitor{
		public void actOn(ComponentDescription cd, Stack pathStack) throws SmartFrogResolutionException {
			Object cpt_id_obj = cd.sfContext().get("sfUniqueComponentID");
			if (cpt_id_obj!=null) {
				if (cpt_id_obj instanceof Boolean && !((Boolean)cpt_id_obj).booleanValue()){
					//Actually a composite terminator...
					modelTerminators.add(cd);
				} else {
					//Actually a component....
					String name = "UCID"+g_UCID++;
				    String suffix = (String) cd.sfParent().sfAttributeKeyFor(cd);
				    String cpt_id = name+suffix;
					components.put(cpt_id, (ComponentDescription) cd);
					cd.sfContext().put("sfUniqueComponentID", cpt_id);
				}
			} else if (cd.sfContext().get("sfIsDependency")!=null) {
				//Register with the dependency with the "by"...
				//Exc?
				Reference by_ref = (Reference) cd.sfContext().get("by");				
				
				SFReference.resolutionForceEager=true;
				ComponentDescription by = (ComponentDescription) cd.sfResolve(by_ref);
				SFReference.resolutionForceEager=false;
				
				Object deps = by.sfContext().get("sfDependencies");
				Vector<ComponentDescription> deps_vec = null;
				if (deps!=null){
					deps_vec = (Vector<ComponentDescription>) deps;
				} else {
					deps_vec = new Vector<ComponentDescription>();
					by.sfContext().put("sfDependencies", deps_vec);
				}
				deps_vec.add(cd);
			} else if (cd.sfContext().get("sfIsVerificationRecord")!=null){
				if (verificationRecords==null) verificationRecords = new Vector<ComponentDescription>();
				verificationRecords.add(cd);
			}
		}
	}
}
