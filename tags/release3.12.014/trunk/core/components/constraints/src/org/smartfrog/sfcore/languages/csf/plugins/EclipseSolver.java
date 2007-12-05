package org.smartfrog.sfcore.languages.csf.plugins;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.csf.constraints.PrologSolver;
import org.smartfrog.sfcore.languages.csf.csfcomponentdescription.FreeVar;
import org.smartfrog.sfcore.reference.Reference;

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
    //Queues
    ToEclipseQueue m_java_to_eclipse;
    FromEclipseQueue m_eclipse_to_java;
       
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
	    QueueListener ql = new EclipseJavaQL(cd);
	    m_eclipse_to_java.setListener(ql);
	    m_java_to_eclipse.setListener(ql);

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
      
    public class SmartFrogEclipseRuntimeException extends RuntimeException {
    	SmartFrogEclipseRuntimeException(String msg, Throwable cause){
    		super(msg, cause);
    	}
    	SmartFrogEclipseRuntimeException(String msg){
    		super(msg, null);
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
	    Object m_get_val;
	    boolean m_error=false;
	    String m_error_msg;
	    
	    EclipseJavaQL(ComponentDescription cd){
	    	m_cd = cd;  
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
	         
	         //System.out.println(ct);
	         
	         String func = ct.functor();
	         
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
	        else if (v instanceof SFNull) return new Atom("sfnull");
	        else return null;
	    }
	    
	    private Object mapValueEJ(Object v)  {
    		if (v.equals(Collections.EMPTY_LIST)) return new Vector();
	    	else if (v instanceof Atom){
	        	Atom va = (Atom) v;
	        	if (va.functor().compareTo("sfnull")==0) return SFNull.get();
	        	else if (va.functor().compareTo("sfvar")==0) return new FreeVar();
	        	else throw new SmartFrogEclipseRuntimeException("mapValueEJ: unknown data *from* solver " + v);
	        } else if (v instanceof Number) return v;
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
		    	if ( (av instanceof SFNull && v instanceof SFNull) ||
		    		 (av instanceof String && v instanceof String && ((String)av).compareTo(v)==0) ||
		    		 (av instanceof Number && v instanceof Number && (av.toString()).compareTo(v.toString())==0)) {		    		
		    		
		    		return;
		    	}
		    	 	
		    	//error...
	    		throw new SmartFrogEclipseRuntimeException("set_value: value to be set from solver is not unifiable with current value. current: " + av +", new: "+v);			    		

		    }    	
	    }
    }
}

