package org.smartfrog.sfcore.languages.csf.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.csf.constraints.CDBrowserModel;
import org.smartfrog.sfcore.languages.csf.constraints.PrologSolver;
import org.smartfrog.sfcore.languages.csf.csfcomponentdescription.FreeVar;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;

import com.parctechnologies.eclipse.Atom;
import com.parctechnologies.eclipse.CompoundTerm;
import com.parctechnologies.eclipse.CompoundTermImpl;
import com.parctechnologies.eclipse.EXDRInputStream;
import com.parctechnologies.eclipse.EXDROutputStream;
import com.parctechnologies.eclipse.EclipseEngine;
import com.parctechnologies.eclipse.EclipseEngineOptions;
import com.parctechnologies.eclipse.EmbeddedEclipse;
import com.parctechnologies.eclipse.FromEclipseQueue;
import com.parctechnologies.eclipse.QueueListener;
import com.parctechnologies.eclipse.ToEclipseQueue;

/**
 * Implmentation of solver for Eclipse
 */
public class EclipseSolver extends PrologSolver  {

    // Default Eclipse options
    private EclipseEngineOptions m_eclipseEngineOptions;  
    // Object representing the Eclipse process
    private EclipseEngine m_eclipse;
    
    ToEclipseQueue m_java_to_eclipse;
    FromEclipseQueue m_eclipse_to_java;
    Object m_get_val;
    CDBrowserModel m_cdbm;
    QueueListener m_ql;
    
    public void prepareTheory(ComponentDescription cd, String coreFile, String prologFile) throws Exception {
		//Eclipse Options
		m_eclipseEngineOptions  = new EclipseEngineOptions();
	
		// Connect the Eclipse's standard streams to the JVM's
		m_eclipseEngineOptions.setUseQueues(false);
	
		// Initialise Eclipse
		m_eclipse = EmbeddedEclipse.getInstance(m_eclipseEngineOptions);
	
		//Consult core theory file
		m_eclipse.compile(new File(coreFile));
		
		//Consult theory file
	    m_eclipse.compile(new File(prologFile));   	
    
	    // Set up the java representation of two queue streams
	    m_java_to_eclipse = m_eclipse.getToEclipseQueue("java_to_eclipse");
	    m_eclipse_to_java = m_eclipse.getFromEclipseQueue("eclipse_to_java");
	
	    // add a TermConsumer as a listener to the eclipse_to_java FromEclipseQueue
	    m_ql = new EclipseJavaQL(cd);
	    m_eclipse_to_java.setListener(m_ql);
	    m_java_to_eclipse.setListener(m_ql);

    }
    
    public void runGoal(String goal) throws Exception {
    	m_eclipse.rpc(goal);
    }
    
    public String ann_preprocess(String goal, String context) throws Exception {
    	//Note that we prefix and affix sos and eos calls
    	String query="SF1=SFGs, preprocess(\""+context+"\", ("+goal+"), SFGs).";
    	CompoundTerm ct=m_eclipse.rpc(query);
    	String res = (String)((CompoundTerm)ct.arg(1)).arg(1);
    	return "sos, "+res+", eos";
    }
    
    public String agg_preprocess(String goal) throws Exception {
    	String query="SF1=SFGs, preprocess2(("+goal+"), SFGs).";
    	CompoundTerm ct=m_eclipse.rpc(query);
    	return (String)((CompoundTerm)ct.arg(1)).arg(1);
    }
   
    public void destroy() throws Exception {
    	//Close queues
    	m_eclipse_to_java.close();
    	m_java_to_eclipse.close();
    	
    	//Destroy the Eclipse process
    	((EmbeddedEclipse) m_eclipse).destroy();
    }
      
    class SmartFrogEclipseRuntimeException extends RuntimeException {
    	SmartFrogEclipseRuntimeException(String msg, Throwable cause){
    		super(msg, cause);
    	}
    	SmartFrogEclipseRuntimeException(String msg){
    		super(msg, null);
    	}
    }
    
    EclipseStatus m_est;
    List m_rangeAttrs = new ArrayList();
    EclipseCDAttr m_ecda;
    
    public class EclipseStatus{
    	private boolean done;
    	private int undo;
    	private boolean back;

    	public void done(){
    		m_cdbm.kill();
    		Iterator atiter = m_rangeAttrs.iterator();
    		m_get_val=new Atom("done");
    		while (atiter.hasNext()){
    			EclipseCDAttr ecda = (EclipseCDAttr) atiter.next();
    			try{
    				ecda.cd.sfReplaceAttribute(ecda.name, ecda.val);
    			} catch (Exception e){
		            throw new SmartFrogEclipseRuntimeException("Unable to set attr val in CD after user set");            	
		        }
				try{
    				m_java_to_eclipse.setListener(m_ql);
				} catch (Exception e){
		            throw new SmartFrogEclipseRuntimeException("Unable to set JtoE listener");            	
		        }
    		}
    	}
    	
        public boolean isDone(){
        	return done;
        }

        public void setBack(boolean back){
        	this.back=back;
        }
        
        public boolean isBack(){
        	return back;
        }
        
        public int getUndo(){
        	return undo;
        }
    }
    
    
    public class EclipseCDAttr {
    	private Object name;
    	private Object attr;
    	private Object val;
    	private Object range;
    	private boolean set;
    	private ComponentDescription cd;
    	private Class vclass;
    	
    	public boolean isSet(){
    		return set;
    	}
    	
    	public void undo(){
    		m_get_val = new Atom("back");
			try{
		    	m_java_to_eclipse.setListener(m_ql);
			} catch (Exception e){
	            throw new SmartFrogEclipseRuntimeException("Unanable to set JtoE listener");            	
	        }
    	}
    	
    	public boolean process_sel(String entry){
    		m_ecda = this;
    		Collection c = (Collection) range;
    	    Iterator iter = c.iterator();
    		while (iter.hasNext()){
    			String el = iter.next().toString();
    			if (el.compareTo(entry)==0){			
    				m_get_val = new CompoundTermImpl("set", attr.toString(), mapEntryJE(entry));  	
    				try{
    			    	m_java_to_eclipse.setListener(m_ql);
    				} catch (Exception e){
    		            throw new SmartFrogEclipseRuntimeException("Unanable to reset JtoE listener");            	
    		        }
    				
    				return true;
    			} 
    		}
    		return false;
    	}
    	
    	Object mapEntryJE(String entry){
    		if (m_ecda.vclass==String.class) return entry;
    		else if (m_ecda.vclass==Integer.class){
    			try{
    				return Integer.parseInt(entry);
    			} catch (Exception e){
    				throw new SmartFrogEclipseRuntimeException("Unanable to parse Integer entry");  
    			}
    		} else if (m_ecda.vclass==Double.class){
    			try{
    				return Double.parseDouble(entry);
    			} catch (Exception e){
    				throw new SmartFrogEclipseRuntimeException("Unanable to parse Double entry");  
    			}
    		}
    		throw new SmartFrogEclipseRuntimeException("Unanable to parse entry");  
    	}
        
        public String getRangeAsString(){
    		return range.toString();
    	}
        
    	public Object getAttr(){
    		return attr;
    	}
    	
    	public String toString(){
    		String attr_s1 = attr.toString();
    		String attr_s = attr_s1.substring(1, attr_s1.length()-1);
    		if (set){
    			return ""+attr_s+" has value: "+val;
    		} else {
    			return""+attr_s+" ranges over: "+range;
    		}
    	}
    }
      
    
    class EclipseJavaQL implements QueueListener { 
    	
    	class AddUndo extends SimpleUndo {
    		String attr;
    		void undo(){
    			try {
    				cd.sfRemoveAttribute(attr);
    			} catch (SmartFrogRuntimeException sfe){
    			   throw new SmartFrogEclipseRuntimeException("dataAvailable: sfundo: Unable to *undo* latest. ", sfe);
    			}
    		}
    		AddUndo(ComponentDescription cd, String attr){
    			this.cd = cd;
    			this.attr = attr;    			
    			this.addme();
    		}
    	}
    	    	
    	class CompAddUndo extends SimpleUndo {
    		List comp_undo_stack;
    		void undo(){
 	    		Iterator iter = comp_undo_stack.iterator();
				while (iter.hasNext()){
					SimpleUndo undo = (SimpleUndo) iter.next();
					undo.undo();
				}
    		}
    		CompAddUndo(){
    			super();
    			comp_undo_stack = new Vector();
    			this.addme();
    		}
    	} 	

    	class FreeVarNullUndo extends SimpleUndo {
    		FreeVar fv;
    		void undo(){
    			fv.setProvData(null);
     		}
       		FreeVarNullUndo(CompAddUndo cad, FreeVar fv){
    			this.fv = fv;
    			cad.comp_undo_stack.add(this);
    		}
    	}    	
    	
    	abstract class SimpleUndo {   
    		ComponentDescription cd;
    		
    		abstract void undo();
    		void addme(){
    			m_undo_stack.add(this);
    		}   		
    		SimpleUndo remove(){
    			return (SimpleUndo) m_undo_stack.remove(m_undo_stack.size()-1);
    		}    		
    	}
    	
    	class SimpleReference {
    		String prefix;
    		String attr;
    	}
    	
    	List m_undo_stack = new Vector();
	    FromEclipseQueue m_iqueue = null;
	    EXDRInputStream m_iqueue_formatted = null;
	    ToEclipseQueue m_oqueue = null;
	    EXDROutputStream m_oqueue_formatted = null;
	    ComponentDescription m_cd;
	    boolean m_error=false;
	    String m_error_msg;
	    
	    EclipseJavaQL(ComponentDescription cd){
	    	m_cd = cd;  
	    }
	    	
	    
	    void populateBrowser(){
           Object root = m_cdbm.attr(null, "sfConfig");
	       populateBrowser(m_cd, root);	
	    }
	    
	    void populateBrowser(ComponentDescription cd, Object root){
	    	
	    	Iterator attriter = cd.sfAttributes();
	    	Context cxt = cd.sfContext();
	    	String cxts = create_ref_str(cd);
	    	while (attriter.hasNext()){
	    		Object attr = attriter.next();
	    		
	    		Object val = null;
	    		
	    		try {
	    		   val = cxt.sfResolveAttribute(attr);
	    		}  catch (Exception e){
	    			throw new SmartFrogEclipseRuntimeException("Can not resolve attribute when populating browser", e);
	    		}
	    		
	    		if (val instanceof ComponentDescription){
	    			Object chroot = m_cdbm.attr(root, attr.toString());
	    			populateBrowser((ComponentDescription)val, chroot);
	    		} else {	    		
		    		EclipseCDAttr ecda = new EclipseCDAttr();
		    		ecda.cd = cd;
		    		ecda.name = attr;
		    		ecda.attr = "\""+cxts+attr+"\"";
		    		ecda.val = val;
	                m_cdbm.attr(root, ecda);
	                try {
	                	if (cxt.sfContainsTag(attr, "sfConsUser") && 
	                			((val instanceof FreeVar && ((FreeVar)val).getProvData()==null) || val instanceof SFNull)) {
                		   m_rangeAttrs.add(ecda);
                		   ecda.set=false;
                		   
                		   if (cxt.sfContainsTag(attr, "sfConsString")) ecda.vclass = String.class;
                		   else if (cxt.sfContainsTag(attr, "sfConsInteger")) ecda.vclass = Integer.class;
                		   else if (cxt.sfContainsTag(attr, "sfConsDouble")) ecda.vclass = Double.class;
                		   else throw new SmartFrogEclipseRuntimeException("(For now) need to tag sfConsUser attrs with one of sfConsString, sfConsInteger or sfConsDouble");
                		   
	                    } else {
	 		    			ecda.set=true;
	 		    		}
	 	    		}  catch (Exception e){
	 	    			throw new SmartFrogEclipseRuntimeException("Can not check attribute for tag", e);
	 	    		}
		    		
	    		}
	    		
	    	}
	    	
	    }
	    
	    void sfuser(){
	    	
            String classname = System.getProperty("org.smartfrog.sfcore.languages.csf.constraints.CDBrowser");
            
            
            if (classname==null){
            	m_get_val = new Atom("done");
            	return;
            }
            
            try {            
            	m_cdbm = (CDBrowserModel) SFClassLoader.forName(classname).newInstance(); 
            
            } catch (Exception e){
            	throw new SmartFrogEclipseRuntimeException("Can not instantiate CD Browser");
            	
            }
                        
            populateBrowser();
            
            if (m_rangeAttrs.size()!=0) {
            	
	        	List ranges = new LinkedList();
	        	Iterator raiter = m_rangeAttrs.iterator();
	        	while (raiter.hasNext()){
	        		EclipseCDAttr ecda = (EclipseCDAttr) raiter.next();
                    ranges.add(ecda.getAttr());	        		
	        	}
	     
	        	m_est = new EclipseStatus();
	        	m_cdbm.setES(m_est);
	        	m_get_val = new CompoundTermImpl("range", ranges);
	        	
            } else {
            	m_get_val = new Atom("done");
            }
                	
	    }
	    
	    
	    void range(CompoundTerm ct){
	    	Collection c = (Collection)ct.arg(1);
	    	Iterator citer = c.iterator();
	    	Iterator riter = m_rangeAttrs.iterator();
	    	boolean all_done=true;
	    	
	    	while (citer.hasNext()){
	    		EclipseCDAttr ecda = (EclipseCDAttr) riter.next();
	    		Collection range = (Collection) citer.next();
	    		if (range.size()>1) {
	    			ecda.range=range;
	    			ecda.set=false;
	    			all_done=false;
	    		} else {
	    			Iterator range_iter = range.iterator();
	    			ecda.val = mapValueEJ(range_iter.next());
	    			ecda.set=true;
	    		}
	    	}
	    	
	    	m_est.done=all_done;
	    	m_cdbm.redraw();
	    	try{
	    	   m_java_to_eclipse.setListener(null);
	    	} catch (Exception e){
            	throw new SmartFrogEclipseRuntimeException("Unanable to clear JtoE listener");            	
            }
	    }
	    
	    void set(CompoundTerm ct){
	    	m_est.undo = ((Integer)ct.arg(1)).intValue();
	    	m_est.back = ((Atom)ct.arg(2)).functor().compareTo("back")==0;
	    	m_get_val = new Atom("range");
	    }
	    
	    // Called when Eclipse flushes source
	    public void dataAvailable(Object source)
	    {	    	
	    		    	
	       if(m_iqueue == null){
			m_iqueue = (FromEclipseQueue) source;
			m_iqueue_formatted = new EXDRInputStream(m_iqueue);
	       }
	
	         CompoundTerm ct = null;
	         try{ 
	           ct = (CompoundTerm) m_iqueue_formatted.readTerm();
	         } catch (IOException ioe){
	        	 throw new SmartFrogEclipseRuntimeException("dataAvailable: Unable to *read* from input stream. ", ioe);
	         }
	         
	         String func = ct.functor();
	         
	         if (func.compareTo("sfuser")==0){
	        	 sfuser();
	        	 return;
	         }
	         
	         if (func.compareTo("range")==0){
	        	 range(ct);
	        	 return;
	         }
	         
	         if (func.compareTo("set")==0){
	        	 set(ct);
	        	 return;
	         }
	         
	         if (func.compareTo("norange")==0){
	        	 throw new SmartFrogEclipseRuntimeException("Unable to collect range information for sfConsUser tagged attributes. Probably because it has not been set in constraint annotations");
	         }
	         
	         if (func.compareTo("sfundo")==0){
	        	 SimpleUndo undo = (SimpleUndo) m_undo_stack.remove(m_undo_stack.size()-1);
	        	 undo.undo();
		         m_get_val = new Atom("success");
		         return;
	         }
	         
	         String ref_s = (String) ct.arg(1);
	         Reference ref = null;
	         ComponentDescription sfcd = null;
	         	         
	         if (ref_s!=null && ref_s.compareTo("")!=0){
	        	 try {
	        	    ref = Reference.fromString(ref_s);
	        	 } catch (SmartFrogResolutionException sfe){
	        		 throw new SmartFrogEclipseRuntimeException("dataAvailable: Unable to *convert reference* from input stream. ", sfe);
	        	 }
	         }
 
        	 SimpleReference attr_sr = split_ref((String) ct.arg(2));
        	 Reference attr_pre = null;
        	         	 
        	 if (attr_sr.prefix!=null){
        		 try {
            		 attr_pre = Reference.fromString(attr_sr.prefix);
            	 } catch (SmartFrogResolutionException sfe){
            		 throw new SmartFrogEclipseRuntimeException("dataAvailable: Unable to construct reference from attribute string. ", sfe);
            	 }
        	 }
        	 
        	 try {
        		 ComponentDescription sfcd1 = (ref!=null?(ComponentDescription) m_cd.sfResolve(ref):m_cd);
        		 sfcd = (attr_pre!=null?(ComponentDescription) sfcd1.sfResolve(attr_pre):sfcd1);
         	 } catch (SmartFrogResolutionException sfe){
        		 throw new SmartFrogEclipseRuntimeException("dataAvailable: Unable to *resolve reference/attr prefix* from input stream on *root* Component Description. ",sfe);
        	 }
	         
         	 
	         if (func.compareTo("sfset")==0){
	        	 //setting
	        	 
	        	 Object eval = ct.arg(3);
	        	 Object val = mapValueEJ(eval); //convert properly 
	        	 Object obj = null;
	        	 
	        	 try {
	        		 obj = sfcd.sfResolve(attr_sr.attr);
	        	 } catch (SmartFrogResolutionException sfe){
	        		 ; //not an error
	        	 }
	        	 
	        	 try{
		        	 if (obj==null){
		        		 sfcd.sfAddAttribute(attr_sr.attr, val);
		        		 new AddUndo(sfcd, attr_sr.attr);
		        	 } else {
		        		 set_value(val, obj, new CompAddUndo());
		        	 }
	        	 } catch (SmartFrogRuntimeException sfe){
	        		 throw new SmartFrogEclipseRuntimeException("dataAvailable: sfset: Unable to *add/replace attribute* in Component Description. ", sfe);
		         }
	        	 
	        	 
	        	 String attr_s ="\""+create_ref_str(sfcd)+attr_sr.attr+"\"";
	        	 Atom ref_a = new Atom(attr_s);
				 m_get_val = new CompoundTermImpl("success", ref_a, eval);
				 
	         } else if (func.compareTo("sfget")==0) {
	        	 //getting 
	        	 Object obj = null;
	        	 try {
	        		 obj = sfcd.sfResolve(attr_sr.attr);
	        	 } catch (SmartFrogResolutionException sfe){
	        	    ; //not an error...	 
	        	 }
	        	 
	        	 boolean obj_fv = (obj!=null && obj instanceof FreeVar);
	        	 Object data=null;
	        	 Object val=null;
	        	 
	        	 String attr_s ="\""+create_ref_str(sfcd)+attr_sr.attr+"\"";
	        	 Atom ref_a = new Atom(attr_s);
	        	 	        	 
	        	 if (obj!=null && (!obj_fv || (data=((FreeVar)obj).getProvData())!=null)){
	        		 val = (obj_fv?mapValueJE(data):mapValueJE(obj)); 
	        	 } else {
    				 val = null;
        		 } 	 
	        	 m_get_val = new CompoundTermImpl("success", ref_a, val);
	        	 
	         }	
	    }
	
	    // Required to implement QueueListener
	    public void dataRequest(Object source)
	    {
	    	if(m_oqueue == null){
				m_oqueue = (ToEclipseQueue) source;
				m_oqueue_formatted = new EXDROutputStream(m_oqueue);
		    }
		    
	    	try { 
		    	if (m_get_val!=null) {
		    		m_oqueue_formatted.write(m_get_val);
		    		m_get_val=null;
		    	} else {
		    		throw new SmartFrogEclipseRuntimeException("dataRequest: No data available to write. ");
		    	}
	    	} catch (IOException ioe){
	    		throw new SmartFrogEclipseRuntimeException("dataRequest: Unable to *write* on output stream. ", ioe);
    	    }
	    }
	    
	    private SimpleReference split_ref(String ref){
	    	SimpleReference sref = new SimpleReference();
	    	int idx = ref.lastIndexOf(":");
	    	if (idx!=-1){
	    		sref.prefix = ref.substring(0, idx);
	    		sref.attr = ref.substring(idx+1, ref.length());
	    	} else sref.attr = ref;
	    	return sref;
	    }
	    
	    private Object mapValueJE(Object v) { 
	    	if (v instanceof FreeVar){
	    		FreeVar fv = (FreeVar)v;
	    		if ((v=fv.getProvData())==null) return null; //note side-effect
	    	}
	    	
	    	if (v instanceof Number) return v;
	        else if (v instanceof Vector) {
	        	Iterator it = ((Collection)v).iterator();
	        	if (!it.hasNext()) return Collections.EMPTY_LIST;
	        	LinkedList result = new LinkedList();
	            while (it.hasNext()) result.add(mapValueJE(it.next()));
	            return result;
	        } else if (v instanceof String) return v;
	        else if (v instanceof SFNull) return null;
	        else return null;
	    }
	    
	    private Object mapValueEJ(Object v)  {
    		if (v.equals(Collections.EMPTY_LIST)) return new Vector();
	    	else if (v instanceof Number) return v;
	        else if (v instanceof Collection) {
	            Vector result = new Vector();
	            Iterator it = ((Collection)v).iterator();  
	            while (it.hasNext()) result.add(mapValueEJ(it.next()));
	            return result;
	        } else if (v instanceof String) return v;
	        else throw new SmartFrogEclipseRuntimeException("mapValueEJ: unknown data *from* solver " + v);	
	    }
	    
	    private void set_value(Object v, Object av, CompAddUndo cad) {
	    	set_value(v, av, cad, null, 0);
	    }
	        
	    //To do: get (from freevar) and final freevar copying...
	    
	    private void set_value(Object v, Object av, CompAddUndo cad, Vector p, int idx){	    	
	    	if (av instanceof FreeVar){
	    		FreeVar avfv = (FreeVar) av;
	    		Object prov_data = avfv.getProvData();
	    		if (prov_data!=null) av=prov_data;  //note side-effect for following
	    		else {	
	    		   new FreeVarNullUndo(cad, avfv);
	    		   avfv.setProvData(v);
	    		   return;
	    		}
	    	}
	    	
	    	if (av instanceof Vector) {
	               if (v instanceof Collection){
	            	   int idx1=0;
	            	   Iterator av_iter = ((Collection)av).iterator();  
	            	   Iterator v_iter = ((Collection)v).iterator();  
	            	   while (av_iter.hasNext()){
	            		   if (!v_iter.hasNext()) throw new SmartFrogEclipseRuntimeException("set_value: value to be set from solver is not unifiable with current value. current: " + av +", new: "+v);	
	       		    	
	            		   Object v1 = v_iter.next();
	            		   Object av1 = av_iter.next();
	            		   set_value(v1, av1, cad, (Vector)av, idx1++);
	            	   }
	            	   if (v_iter.hasNext()) throw new SmartFrogEclipseRuntimeException("set_value: value to be set from solver is not unifiable with current value. current: " + av +", new: "+v);	   
	               } else throw new SmartFrogEclipseRuntimeException("set_value: value to be set from solver is not unifiable with current value. current: " + av +", new: "+v);			    	
		    } else {
		    	if ((av instanceof SFNull && v instanceof SFNull) ||
			         ((av instanceof String && v instanceof String || av instanceof Number && v instanceof Number) &&
			                 av.toString().compareTo(v.toString())==0))
			    {
		    		return;
		    	}
		    	 	
		    	//error...
	    		throw new SmartFrogEclipseRuntimeException("set_value: value to be set from solver is not unifiable with current value. current: " + av +", new: "+v);			    		

		    }    	
	    }
    }
}

