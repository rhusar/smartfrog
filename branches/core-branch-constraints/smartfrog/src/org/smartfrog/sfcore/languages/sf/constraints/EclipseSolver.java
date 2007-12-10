/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.sf.constraints;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.smartfrog.SFParse;
import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.LinkResolutionState;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;
import org.smartfrog.sfcore.reference.AttribReferencePart;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
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
public class EclipseSolver extends CoreSolver implements Runnable, QueueListener {
	
    /**
     * Default Eclipse options 
     */
	private EclipseEngineOptions m_eclipseEngineOptions;  
    
	/**
	 * Eclipse engine
	 */
	private EclipseEngine m_eclipse;

	/**
	 * Record object to send to eclipse side
	 */
    Object m_get_val;
    /**
     * Thread for main eclipse goal
     */
    Thread m_ecr;
    
    /**
     * sfConfig browser for purpose of setting user variables 
     */
    CDBrowser m_cdbm;
    
    /**
     * Are there user variables?
     */
    boolean m_isuservars;

    /**
     * Component description pertaining to Constraint type being solved
     */
    ComponentDescription m_solve_comp;
    
    /**
     * Queue for coms TO eclipse
     */
    ToEclipseQueue m_java_to_eclipse;
    
    /**
     * Queue for coms FROM eclipse
     */
    FromEclipseQueue m_eclipse_to_java;
    /**
     * Lock for object, so that solve method waits until constraint goal done before exiting
     */
    ReentrantLock m_solverLock = new ReentrantLock();
    /**
     * Condition for lock, so that solve method waits until constraint goal done before exiting
     */
    Condition m_solverFinished = m_solverLock.newCondition();
    /**
     * Indicates whether the eclipse secondary thread (for main eclipse goal) has sought and lock yet
     */
    boolean m_ecr_sought_lock = false;
    /**
     * Latest constraint goal has finished?
     */
    boolean m_cons_finished = false;
    
    /**
     * Stores any exception thrown from Eclipse thread
     */
    Exception m_general_error = null;
    
    /**
     * Relative path to constraint theory files
     */
	private final String pathswitch = "/../constraints/";
	
	/**
	 * Name of core theory file
	 */
	private final String coreFileSuffix = "core.ecl";
	/**
	 * Name of additional theory file
	 */
    private final String theoryFileSuffix = "base.ecl";
    
    /**
     * Property indicating where to find additional user-specified theory files
     */
    private final String theoryFilePath = "opt.smartfrog.sfcore.languages.sf.constraints.theoryFilePath";
    
    /**
     * Eclipse main goal to run in secondary thread
     */
    public void run(){  	
    	try {
    		m_eclipse.rpc("sfsolve");
    	} catch (Exception e){
    		m_general_error=e;
    		yieldLockFromECRThread();
    	}
    }
        
    /**
     * Prepare the solver
     */
    void prepareSolver() throws SmartFrogResolutionException {
        String sfhome = SFSystem.getEnv("SFHOME");
        
        if (sfhome==null){
    		throw new SmartFrogResolutionException("Environment variable SFHOME must be set. Context: constraint processing");
    	}
                
        String corefile = sfhome+pathswitch+coreFileSuffix;
        String thfile = sfhome+pathswitch+theoryFileSuffix;
       
        
        // create the theory
        try {
            prepareTheory(corefile,thfile);
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
    
    /**
     * Prepare the engine with two theories
     */
    public void prepareTheory(String coreFile, String coreFile2) throws Exception {
		//Set up eclipse...
    	m_eclipseEngineOptions  = new EclipseEngineOptions();
		
		// Connect the Eclipse's standard streams to the JVM's
		m_eclipseEngineOptions.setUseQueues(false);
	
		// Initialise Eclipse
		m_eclipse = EmbeddedEclipse.getInstance(m_eclipseEngineOptions);
	
		//Consult core theory file
		m_eclipse.compile(new File(coreFile));
		
		//Consult core theory file
		m_eclipse.compile(new File(coreFile2));
		
		// Set up the java representation of two queue streams
	    m_java_to_eclipse = m_eclipse.getToEclipseQueue("java_to_eclipse");
	    m_eclipse_to_java = m_eclipse.getFromEclipseQueue("eclipse_to_java");

	    m_eclipse_to_java.setListener(this);	 
    }
      
    /**
     * Run eclipse goal...
     */
    public void runGoal(String goal){/*Do nothing for now*/};
    
    /**
     * Stop solving altogether
     */
    public void stopSolving() throws Exception {
    	m_get_val = "sfstop";
    	try{
			m_java_to_eclipse.setListener(this);
		}catch(Exception e){}
		m_ecr=null;
    }   

    /**
     * Solve a Constraint goal
     */
    public void solve(ComponentDescription comp, Vector attrs, Vector values, Vector goal, Vector autos, boolean isuservars) throws Exception {    	   	
    	//Set comp as current solving comp descr
    	m_solve_comp = comp;
    	
    	//Set is user vars?
    	m_isuservars = isuservars;
    	
    	//Construct goal
    	String _attrs = mapValueJE(attrs);
    	String _values = mapValueJE(values); 
    	String _goal = mapValueJE(goal);
    	String _autos = mapValueJE(autos);
    	String verbose = (SFParse.opts.verbose? "true":"false");
    	    	
    	m_get_val = "sfsolve("+_attrs+", "+_values+", "+
    					LinkResolutionState.getLRS().addConstraintEval(comp.sfContext())+", "+_goal+", "+_autos+", "+verbose+")";
    	
        //System.out.println(m_get_val);
    	
    	//Allocate new thread for goal and start it
    	m_cons_finished = m_ecr_sought_lock = false;
    	
    	if (m_ecr==null) {
    		m_ecr = new Thread(this); 
	    	m_ecr.start();
    	}	
    	
    	//Let rip...
    	try{ m_java_to_eclipse.setListener(this); } catch(Exception e){}
		
    	//Lock me to make sure I don't complete before constraint eval does...
		m_solverLock.lock();
		while (!m_cons_finished) m_solverFinished.await();
		m_solverLock.unlock();
		
		if (m_general_error!=null) throw m_general_error;
   }
    
    /**
     * Maps Java object to String for transfer to Eclipse 
     * @param v  object to be transformed
     * @return eclipse string
     */
    private String mapValueJE(Object v) throws SmartFrogResolutionException { 
        return mapValueJE(v, false);
    }

    /**
     * Maps Java object to String for transfer to Eclipse 
     * @param v  object to be transformed
     * @param quoted indicates whether Strings should be double quoted
     * @return eclipse string
     */
    private String mapValueJE(Object v, boolean quoted) throws SmartFrogResolutionException { 
    	if (v instanceof FreeVar) {
    		FreeVar fv = (FreeVar) v;
    		if (fv.getConsEvalKey()!=null){	    		
	    		if (fv.getConsEvalIdx()==-1){
	    			fv.setConsEvalIdx(LinkResolutionState.getLRS().getConsEvalIdx()+1);  //+1 as yet to add entry
	    			//System.out.println("Setting The Index! "+fv.getConsEvalIdx());
	    			String range_s = "null";
	    			Object range = fv.getRange();
	    			if (range!=null){
	    				if (range instanceof String) range_s = (String) range;
	    				else if (range instanceof Vector) range_s = mapValueJE(range);
	    				else throw new SmartFrogResolutionException("Invalid range specifier for FreeVar: "+v);
	    			}
	    			
	    			Object defVal = fv.getDefVal();
	    			String defVal_s = (defVal!=null? defVal_s=", "+defVal.toString() : "");
	    			return "sfvar("+range_s+defVal_s+")";
	    			
	    		} else return "sfref("+fv.getConsEvalIdx()+", "+fv.getConsEvalKey()+")";
    		} else {
    			return v.toString();
    		}
    	}
    	else if (v instanceof Number) return v.toString();
        else if (v instanceof Vector) {
        	String val = "[";
        	boolean first=true;
        	Iterator it = ((Vector)v).iterator();
        	while (it.hasNext()) {
        		if (first) first=false;
        		else val += ", ";
        		
        		val += mapValueJE(it.next(), quoted);
        	}
        	val += "]";
        	return val;
        } else if (v instanceof String) {
        	if (quoted) return "\""+v+"\"";
        	else return (String)v;
        } else if (v instanceof SFNull){
        	return "sfnull";
        } else if (v instanceof SFReference) {
        	ReferencePart rp = ((SFReference)v).firstElement();
        	if (rp instanceof AttribReferencePart) {
        		String ret_s = ((AttribReferencePart)rp).getValue().toString();
        		if (quoted) ret_s = "\""+ret_s+"\"";
        		return ret_s;
        	}
        	else return null;
        }
        else if (v instanceof ComponentDescription) return "sfcd";
        else if (v instanceof Boolean) return (((Boolean)v).booleanValue() ? "true" : "false");
        else return null;
    }
    

    /**
     * Maps Eclipse object to Java object after transfer from Eclipse 
     * @param v  object to be transformed
     * @return transformed object
     */
    private Object mapValueEJ(Object v)  {
    	return mapValueEJ(v, false);
    }
    
    /**
     * Maps Eclipse object to Java object after transfer from Eclipse 
     * @param v  object to be transformed
     * @param ref_create indicates whether references should be created for atoms -- used for subtyping directives
     * @return transformed object
     */
    private Object mapValueEJ(Object v, boolean ref_create)  {
    	if (v==null) return new FreeVar();
    	else if (v.equals(Collections.EMPTY_LIST)) return new Vector();
    	else if (v instanceof Number) return v;
        else if (v instanceof Collection) {
            Vector result = new Vector();
            Iterator it = ((Collection)v).iterator();  
            while (it.hasNext()) result.add(mapValueEJ(it.next(), ref_create));
            return result;
        } else if (v instanceof String) return v;
        else if (v instanceof Atom){
        	Atom va = (Atom) v;
        	if (va.functor().equals("sfnull")) return SFNull.get();
        	else {
        		String va_s = va.functor();
        		try {
        			if (ref_create) return Reference.fromString(va_s);
        			return va_s;
        		} catch (SmartFrogResolutionException sfre){
        			throw new SmartFrogEclipseRuntimeException("mapValueEJ: Trouble in creating reference from attribute string for subtyping. ");	
        		}
        	}
        } else throw new SmartFrogEclipseRuntimeException("mapValueEJ: unknown data *from* solver " + v);	
    }	    

    /**
     * Unchecked Eclipse Exception, for throwing from Listeners dataAvailable, dataRequest 
     * @author anfarr
     *
     */
    static public class SmartFrogEclipseRuntimeException extends RuntimeException {
    	SmartFrogEclipseRuntimeException(Throwable cause){
    		super(null, cause);
    	}
    	SmartFrogEclipseRuntimeException(String msg, Throwable cause){
    		super(msg, cause);
    	}
    	SmartFrogEclipseRuntimeException(String msg){
    		super(msg, null);
    	}
    }
    
    EclipseStatus m_est;
    Vector m_rangeAttrs = new Vector();
    EclipseCDAttr m_ecda;
    
    public class EclipseStatus{
    	private boolean done;
    	private int undo;
    	private boolean back;
    	private String ref;
    	private String val;

    	public void undo(){
    		m_get_val = new Atom("back");
			try{
		    	m_java_to_eclipse.setListener(EclipseSolver.this);
			} catch (Exception e){
	            throw new SmartFrogEclipseRuntimeException("Unanable to set JtoE listener");            	
	        }
    	}
    	
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
    		}
			try{
				m_java_to_eclipse.setListener(EclipseSolver.this);
			} catch (Exception e){
	            throw new SmartFrogEclipseRuntimeException("Unable to set JtoE listener");            	
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
        
        public String getUndoLabel(){
        	if (ref!=null) return ref+" currently set to "+val;
        	else return "";
        }
    }
    
    
    public class EclipseCDAttr {
    	private String name;
    	private Object val;
    	private Object range;
    	private boolean set;
    	private ComponentDescription cd;
    	private Class vclass;
    	
    	public boolean isSet(){
    		return set;
    	}
    	
    	public boolean process_sel(String entry){
    		m_ecda = this;
    		Collection c = (Collection) range;
    	    Iterator iter = c.iterator();
    		while (iter.hasNext()){
    			String el = iter.next().toString();
    			if (el.equals(entry)){			
    				m_get_val = new CompoundTermImpl("set", new Atom(name), new Atom(entry));  	
    				try{
    			    	m_java_to_eclipse.setListener(EclipseSolver.this);
    				} catch (Exception e){
    		            throw new SmartFrogEclipseRuntimeException("Unanable to reset JtoE listener");            	
    		        }
    				
    				return true;
    			} 
    		}
    		return false;
    	}
    	        
        public String getRangeAsString(){
    		return range.toString();
    	}
        
    	public String getAttrName(){
    		return name;
    	}
    	
    	public String toString(){
    		if (set){
    			return name+" has value: "+val;
    		} else {
    			return name+" ranges over: "+range;
    		}
    	}
    }
    
    void populateBrowser(){
           Object root = m_cdbm.attr(null, "sfConfig", false);
	       populateBrowser(top, root, false);	
	    }
	    
	    void populateBrowser(ComponentDescription cd, Object root, boolean showme){
	    	
	    	Iterator attriter = cd.sfAttributes();
	    	Context cxt = cd.sfContext();
	    	//String cxts = create_ref_str(cd);
	    	while (attriter.hasNext()){
	    		Object attr = attriter.next();
	    		
	    		Object val = null;
	    		
	    		try {
	    		   val = cxt.sfResolveAttribute(attr);
	    		}  catch (Exception e){
	    			throw new SmartFrogEclipseRuntimeException("Can not resolve attribute when populating browser", e);
	    		}
	    		
	    		if (val instanceof SFApplyReference){
	    			SFApplyReference val_ar = (SFApplyReference) val;
	    			val = val_ar.getComponentDescription(); 
	    		} 
	    		
	    		if (val instanceof ComponentDescription){
	    			boolean showkids = (!showme && m_solve_comp==val);
	    			Object chroot = m_cdbm.attr(root, attr.toString(), showme);
	    			populateBrowser((ComponentDescription)val, chroot, showkids);
	    		} else {	    		
		    		EclipseCDAttr ecda = new EclipseCDAttr();
		    		ecda.cd = cd;
		    		ecda.name = attr.toString();
		    		ecda.val = val;
	                m_cdbm.attr(root, ecda, showme);
	                try {
	                	if (cd==m_solve_comp && cxt.sfContainsTag(attr, "sfConstraintUserVar") && val instanceof FreeVar) {
	             		   m_rangeAttrs.add(ecda);
	             		   ecda.set=false;             		   
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
	    	
         String classname = System.getProperty("org.smartfrog.sfcore.languages.sf.constraints.CDBrowser");  
         if (classname==null || !m_isuservars){
         	m_get_val = new Atom("done");
			try{
				m_java_to_eclipse.setListener(EclipseSolver.this);
			} catch (Exception e){
	            throw new SmartFrogEclipseRuntimeException("Unable to set JtoE listener");            	
	        }
         	return;
         }
         
         try {            
         	m_cdbm = (CDBrowser) SFClassLoader.forName(classname).newInstance(); 
         
         } catch (Exception e){
         	throw new SmartFrogEclipseRuntimeException("Can not instantiate CD Browser");
         	
         }
         
         populateBrowser();
         
         
         if (m_rangeAttrs.size()!=0) {
         	
	        	List ranges = new LinkedList();
	        	Iterator raiter = m_rangeAttrs.iterator();
	        	while (raiter.hasNext()){
	        		EclipseCDAttr ecda = (EclipseCDAttr) raiter.next();
                    ranges.add(ecda.getAttrName());	        		
	        	}
	     
	        	m_est = new EclipseStatus();
	        	m_cdbm.setES(m_est);
	        	m_get_val = new CompoundTermImpl("range", ranges);
	        	
         } else {
         	m_get_val = new Atom("done");
         }
             	
			try{
				m_java_to_eclipse.setListener(EclipseSolver.this);
			} catch (Exception e){
	            throw new SmartFrogEclipseRuntimeException("Unable to set JtoE listener");            	
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
	    			ecda.range=mapValueEJ(range);
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
	    }
	    
	    void set(CompoundTerm ct){
	    	m_est.undo = ((Integer)ct.arg(1)).intValue();
	    	Object ref = ct.arg(2);
	    	m_est.ref = (ref!=null? ((CompoundTerm)ref).functor() : null);
	    	Object val = ct.arg(3);
	    	m_est.val = (val!=null? ((CompoundTerm)val).functor() : null);
	    	m_est.back = ((CompoundTerm)ct.arg(4)).functor().equals("back");
	    	m_get_val = new Atom("range");
	    	try{
				m_java_to_eclipse.setListener(EclipseSolver.this);
			} catch (Exception e){
	            throw new SmartFrogEclipseRuntimeException("Unable to set JtoE listener");            	
	        }
	    }
    
     
	    /**
	     * Called when Eclipse flushes source
	     */ 
	    public void dataAvailable(Object source)
	    {	    	

	        FromEclipseQueue m_iqueue = null;
	        EXDRInputStream m_iqueue_formatted = null;
	    	
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
	         
	         if (func.equals("sffailed")) {
	        	 throw new SmartFrogEclipseRuntimeException("Unable to solve constraints. General failure.");
	         }
	         else if (func.equals("sfuser")) sfuser();	         
	         else if (func.equals("range")) range(ct);
	         else if (func.equals("set")) set(ct);
	         else if (func.equals("norange")) throw new SmartFrogEclipseRuntimeException("Unable to collect range information for sfConsUser tagged attributes. Probably because it has not been set in constraint annotations");
	         //If done on constraint goal, yield lock so solve() may complete
	         else if (func.equals("sfdonegoal")) yieldLockFromECRThread();
	         //Setting an attributes value 
	         else if (func.equals("sfset")){
	        	 int idx = ((Integer) ct.arg(1)).intValue();
	        	 Object val = mapValueEJ(ct.arg(2));
	        	 String qual = ((Atom) ct.arg(3)).functor();
	        	 Collection ctar = (Collection) ct.arg(4);
	        	 int cidx = ((Integer) ct.arg(5)).intValue();
	        	 
	        	 
	        	 boolean sfcd = false;
	        	 
	        	 if (val instanceof String){
	        		 String val_s = (String) val;
	        	     if (val_s.indexOf("sfcd")==0) {
	        	    	 sfcd=true;
	        	    	 val = val_s.substring(4);	 
	        	     }
	        	 }
	        	 	        	 
	        	 FreeVar fv=null;
	        	 if (qual.equals("first")){
	        		 val = fv = new FreeVar();
	        	 }
	        	 
	        	 Iterator tar_iter = ctar.iterator();
	        	 LinkResolutionState.getLRS().backtrackConstraintAss(idx, cidx);

	        	 if (sfcd) val = LinkResolutionState.getLRS().adjustSetValue(val);
	        	 
	        	 boolean first=true;
	        	 while (tar_iter.hasNext()){
	        		 CompoundTerm prim = (CompoundTerm) tar_iter.next();
	        		 cidx=((Integer)prim.arg(1)).intValue();
	        		 String key=((Atom)prim.arg(2)).functor();
	        		 
	        		 if (first){
	        			 if (fv!=null){
	        				fv.setConsEvalIdx(cidx); 
	        				fv.setConsEvalKey(key);
	        			 }
	        			 first=false;
	        		 }
	        		 try {
	        			 boolean success = LinkResolutionState.getLRS().addConstraintAss(m_solve_comp, idx, key, val, cidx);
	        			 m_get_val = ""+success;
	        		 } catch (SmartFrogResolutionException smfre){
	        			 throw new SmartFrogEclipseRuntimeException(smfre);
	        		 }
         	    	 try{ m_java_to_eclipse.setListener(this); } catch(Exception e){}
	        	 }
	        	 
	         } else if (func.equals("sfsubtype")){
	        	 String attr = ((Atom) ct.arg(1)).functor();
	        	 Vector types = (Vector) mapValueEJ(ct.arg(2), true);  //by this stage we know its a Vector
	        	 LinkResolutionState.getLRS().setTyping(attr, types);
	         }
	    }
	
	    /**
	     * Yields lock so that solve() may finish
	     *
	     */
	    void yieldLockFromECRThread(){
	       	 m_cons_finished=true;
	    	 m_solverFinished.signalAll();
	    	 m_solverLock.unlock();
	    }
	    
	    /**
	     * Called when Eclipse demands data 
	     */
	    public void dataRequest(Object source)
	    {
	        ToEclipseQueue m_oqueue = null;
	        EXDROutputStream m_oqueue_formatted = null;
	    	
	    	if (!m_ecr_sought_lock) {
	    		m_ecr_sought_lock = true;
	    		m_solverLock.lock();
	    	}
	    	
	    	if(m_oqueue == null){
				m_oqueue = (ToEclipseQueue) source;
				m_oqueue_formatted = new EXDROutputStream(m_oqueue);
		    }
		    	    	
	    	try { 
		    	if (m_get_val!=null) {
		    		m_oqueue_formatted.write(m_get_val);
		    		m_java_to_eclipse.setListener(null);
		    		m_get_val=null;
		    	} else {
		    		throw new SmartFrogEclipseRuntimeException("dataRequest: No data available to write. ");
		    	}
	    	} catch (IOException ioe){
	    		throw new SmartFrogEclipseRuntimeException("dataRequest: Unable to *write* on output stream. ", ioe);
    	    }
	    }
}

