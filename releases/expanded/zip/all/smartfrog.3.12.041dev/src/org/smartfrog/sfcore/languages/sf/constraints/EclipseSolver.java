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
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
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
 * Implementation of solver for Eclipse
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
	 * Object to send to eclipse side
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
     * Location (relative switch) of eclipse theory file specific to smartfrog constraint solving within Eclipse installation
     */
    private static final String g_eclipse_switch = "/smartfrog/";
       
    /**
     * Property prefix for theory files...
     */
    private static final String g_theoryFile_prefix = "opt.smartfrog.sfcore.languages.sf.constraints.theoryFile";
    
    /**
     * Property indicating where to find eclipse installation
     */
    public static final String g_eclipseDir_specifier = "opt.smartfrog.sfcore.languages.sf.constraints.eclipseDir";
    //NOTE: eclipseDir property is a temporary solution pending resolution of best way to include eclipse in release...
    
    /**
     * Property indicating where to find eclipse installation
     */
    private static final String g_userVar_browser = "org.smartfrog.sfcore.languages.sf.constraints.CDBrowser";
    
    /**
     * Resolution state for constraint solving...
     */
    private ConstraintResolutionState m_cresState;
    
    /**
     * Status of solving for reflection in user var sfConfig browser
     */
    EclipseStatus m_est;
    
    /**
     * Attributes with ranges to be sought...
     */
    Vector<EclipseCDAttr> m_rangeAttrs = new Vector<EclipseCDAttr>();
    
    /**
     * Current attribute in user var sfConfig browser being manipulated
     */
    private EclipseCDAttr m_ecda;
        
    
    /**
     * Protected constructor
     */
    protected EclipseSolver() throws SmartFrogRuntimeException {
    	prepareSolver();
    }
    
    /**
     * Set cuurrent attribute in user var sfConfig browser being manipulated
     */
    public void setEclipseCDAttr(EclipseCDAttr ecda){
    	m_ecda = ecda;
    }
    
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
     * Constraints are possible (ie supported) if this class is loaded...
     */
    public boolean getConstraintsPossible(){ return true; }
    
    /**
     * Prepare the solver
     */
    private void prepareSolver() throws SmartFrogResolutionException {
    	//Prepare theory...
    	
        String unable = "Unable to load Eclipse-based solver:";
  	   
    	// Get Eclipse Installation Directory... 
	    String eclipseDir = System.getProperty(g_eclipseDir_specifier);
	    if (eclipseDir==null) eclipseDir=System.getenv("ECLIPSEDIRECTORY");
    
	    //Throw on lack of info re eclipse directory...
	    if (eclipseDir==null) throw new SmartFrogResolutionException(unable+" eclipse directory property unspecified ("+g_eclipseDir_specifier+")"); 
	    
	    //Set up eclipse...
    	m_eclipseEngineOptions  = new EclipseEngineOptions(new File(eclipseDir));
	
    	// Connect the Eclipse's standard streams to the JVMs
		m_eclipseEngineOptions.setUseQueues(false);

		// Initialise Eclipse
		try {
			m_eclipse = EmbeddedEclipse.getInstance(m_eclipseEngineOptions);
		} catch (Exception e){
			throw new SmartFrogResolutionException(unable+" can not get eclipse instance, "+e); 
		}
	
		int idx=0;
		String theoryFile = System.getProperty(g_theoryFile_prefix+idx);
		if (theoryFile==null) theoryFile = "core.ecl";
		
		while (theoryFile!=null){
			//Consult core theory files
			//eclipseDir/...  is not necessarily the best place to put the theory files...
			try {
				m_eclipse.compile(new File(eclipseDir+g_eclipse_switch+theoryFile));
			} catch (Exception e){
				throw new SmartFrogResolutionException(unable+" can not compile theory source: "+theoryFile+" , "+e); 
			}
			idx++;
			theoryFile = System.getProperty(g_theoryFile_prefix+idx);
		}
		
		// Set up the java representation of two queue streams
		try {
			m_java_to_eclipse = m_eclipse.getToEclipseQueue("java_to_eclipse");
			m_eclipse_to_java = m_eclipse.getFromEclipseQueue("eclipse_to_java");
			m_eclipse_to_java.setListener(this);
		} catch (Exception e){
			throw new SmartFrogResolutionException(unable+" general queue exception, "+e); 
		}
   }    
    
	/**
	 * Reset "backtracked to" Context information
	 */
    public void resetDoneBacktracking(){
    	if (m_cresState!=null) m_cresState.resetDoneBacktracking();
    }
    
	/**
	 * On backtracking, we have backtracked to the returned context
	 * @return Context to which backtracking has unwound to, null if no backtracking
	 */
    public Context hasBacktrackedTo(){
    	if (m_cresState!=null) return m_cresState.hasBacktrackedTo();
    	else return null;
    }
    
    /**Should we undo actions involving contexts and FreeVars?
     * @param undo Should we?
     */
    public void setShouldUndo(boolean undo){
    	if (m_cresState!=null) m_cresState.setConstraintsShouldUndo(undo);
    }
    
	/**
	 * Adds single undo action to current lhr for undo attribute setting in a Context
   	 * @param ctxt context for undo action
     * @param key  key to undo put value
   	 * @param value value to restore
	 */
	public void addUndoPut(Context ctxt, Object key, Object value){
		if (m_cresState!=null) m_cresState.addUndoPut(ctxt, key, value);
	}
		
	/**
	 * Adds single undo action to current lhr for undoing FreeVar info setting
   	 * @param fv  FreeVar
     */
	public void addUndoFVInfo(FreeVar fv){
		if (m_cresState!=null) m_cresState.addUndoFVInfo(fv);
	}	
	
	/**
	 * Adds single undo action to current lhr for undoing FreeVar type string setting
   	 * @param fv  FreeVar
     */
	public void addUndoFVTypeStr(FreeVar fv){
		if (m_cresState!=null) m_cresState.addUndoFVTypeStr(fv);
	}	
         
    /**
     * Stop solving altogether
     */
    public void stopSolving() {
    	javaToEclipse("sfstop");
		m_ecr=null;	
		m_cresState = null;
    }   

    /**
     * Solve a Constraint goal
     */
    public void solve(ComponentDescription comp, Vector attrs, Vector values, Vector goal, Vector autos, boolean isuservars) throws Exception {    	   	
    	
    	//New Constraint Evaluation History, if appropriate...
    	if (m_cresState==null) m_cresState = new ConstraintResolutionState();
    	
    	//Set comp as current solving comp descr
    	m_solve_comp = comp;
    	
    	//Set is user vars?
    	m_isuservars = isuservars;
    	
    	//Construct goal
    	String _attrs = mapValueJE(attrs);
    	String _values = mapValueJE(values); 
    	String _goal = mapValueJE(goal);
    	String _autos = mapValueJE(autos);
    	String verbose = (SFParse.isVerboseOptSet() ? "true":"false");
    	    	
    	m_get_val = "sfsolve("+_attrs+", "+_values+", "+
    					m_cresState.addConstraintEval(comp.sfContext())+", "+_goal+", "+_autos+", "+verbose+")";
    	

    	//Allocate new thread for goal and start it
    	m_cons_finished = m_ecr_sought_lock = false;
    	if (m_ecr==null) {
    		m_ecr = new Thread(this); 
	    	m_ecr.start();
    	}	
    	
    	//Let rip...
    	javaToEclipse(); 
    	
    	//Lock me to make sure I don't complete before constraint eval does...
		m_solverLock.lock();
    	if (!m_cons_finished) m_solverFinished.await();
		m_solverLock.unlock();
	
		//Has Eclipse solver thread bombed?
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
	    			fv.setConsEvalIdx(m_cresState.getConsEvalIdx()+1);  //+1 as yet to add entry
	    			
	    			String range_s = "null";
	    			Object range = fv.getRange();
	    			if (range!=null){
	    				if (range instanceof String) range_s = (String) range;
	    				else if (range instanceof Vector) range_s = mapValueJE(range);
	    				else throw new SmartFrogResolutionException("Invalid range specifier for FreeVar: "+v);
	    			}
	    			
	    			Object defVal = fv.getDefVal();
	    			String defVal_s = (defVal!=null? ", "+defVal.toString() : "");
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
        			throw new RuntimeException("mapValueEJ: Trouble in creating reference from attribute string for subtyping. ");	
        		}
        	}
        } else throw new RuntimeException("mapValueEJ: unknown data *from* solver " + v);	
    }	    
    
   
    /**
     * Populate sfConfig browser for setting user variables...
     */    
    private void populateBrowser(){
           Object root = m_cdbm.attr(null, "sfConfig", false);
	       populateBrowser(orig, root, false);	
	}
	
    /**
     * Populate sfConfig browser for setting user variables...
     * @param cd current component description being added to browser
     * @param root browser object corresponding to sfConfig 
     * @param showme whether I should be shown -- determined by whether I am the Constraint component description currently being solved... 
     */
	private void populateBrowser(ComponentDescription cd, Object root, boolean showme){ 	
	    	Iterator attriter = cd.sfAttributes();
	    	Context cxt = cd.sfContext();
	    	//String cxts = create_ref_str(cd);
	    	while (attriter.hasNext()){
	    		Object attr = attriter.next();
	    		Object val = null;
	    		
	    		try {
	    		   val = cxt.sfResolveAttribute(attr);
	    		}  catch (Exception e){
	    			throw new RuntimeException("Can not resolve attribute when populating browser", e);
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
		    		EclipseCDAttr ecda = new EclipseCDAttr(this, attr.toString(), val);
		    		
	                m_cdbm.attr(root, ecda, showme);
	                try {
	                	if (cd==m_solve_comp && cxt.sfContainsTag(attr, "sfConstraintUserVar") && val instanceof FreeVar) {
	             		   m_rangeAttrs.add(ecda);
	             		   ecda.set(false);             		   
	                    } else {
	 		    			ecda.set(true);
	 		    		}
	 	    		}  catch (Exception e){
	 	    			throw new RuntimeException("Can not check attribute for tag", e);
	 	    		}
		    		
	    		}
	    		
	    	}
	    	
	    }
	    
	/**
	 * Kill sfConfig browser for user variables
	 */
	public void killBrowser(){
		m_cdbm.kill();
	}

	/**
	 * Communicate supplied parameter to Eclipse side 
	 * @param val Object to be sent
	 */
	public void javaToEclipse(Object val){
		m_get_val = val;  	
		javaToEclipse();
	}
	
	/**
	 * Communicate m_get_val to Eclipse side 
	 */
	public void javaToEclipse(){
		try{
	    	m_java_to_eclipse.setListener(this);
		} catch (Exception e){
            throw new RuntimeException("Unanable to reset JtoE listener");            	
        }
	}


	/**
	 * Initiated by Eclipse side -- process user variables in Constraint description...
	 */
    void sfuser(){
    	
    	//If no user variables, then we are done...
    	if (!m_isuservars){
        	javaToEclipse(new Atom("done"));
         	return;
        }
    	
    	//Get sfConfig browser class
         String classname = System.getProperty(g_userVar_browser);   
         if (classname==null) throw new RuntimeException("Can not instantiate CD Browser, property not set...");
         
         try {            
         	m_cdbm = (CDBrowser) SFClassLoader.forName(classname).newInstance(); 
         } catch (Exception e){
         	throw new RuntimeException("Can not instantiate CD Browser");
         }
         
         //Populate the browser
         populateBrowser();
         
         //User variables to be set in m_rangeAttrs, communicate these to eclipse side to get ranges...
         if (m_rangeAttrs.size()!=0) {
         	
	        	List ranges = new LinkedList();
	        	Iterator raiter = m_rangeAttrs.iterator();
	        	while (raiter.hasNext()){
	        		EclipseCDAttr ecda = (EclipseCDAttr) raiter.next();
                    ranges.add(ecda.getAttrName());	        		
	        	}
	     
	        	m_est = new EclipseStatus(this);
	        	m_cdbm.setES(m_est);
	        	javaToEclipse(new CompoundTermImpl("range", ranges));
	        	
         } else {
        	 javaToEclipse(new Atom("done"));
         }
          
	    }
	    
	    /**
	     * We have ranges for user variables from eclipse, need to display them...
	     * @param ct CompoundTerm containing range information
	     */
	    void range(CompoundTerm ct){
	    	Collection c = (Collection)ct.arg(1);
	    	Iterator citer = c.iterator();
	    	Iterator riter = m_rangeAttrs.iterator();
	    	boolean all_done=true;
	    	
	    	while (citer.hasNext()){
	    		EclipseCDAttr ecda = (EclipseCDAttr) riter.next();
	    		Collection range = (Collection) citer.next();
	    		if (range.size()>1) {
	    			ecda.setRange(mapValueEJ(range));
	    			ecda.set(false);
	    			all_done=false;
	    		} else {
	    			Iterator range_iter = range.iterator();
	    			ecda.setValue(mapValueEJ(range_iter.next()));
	    			ecda.set(true);
	    		}
	    	}
	    	
	    	m_est.setDone(all_done);
	    	m_cdbm.redraw();
	    }
	    
	    /**
	     * Last set or undo has been processed by eclipse.  Eclipse reflects this information back to us, and in turn we ask Eclipse for updated range info...
	     * @param ct
	     */
	    void set(CompoundTerm ct){	    	
	    	m_est.setUndo(((Integer)ct.arg(1)).intValue());
	    	Object ref = ct.arg(2);
	    	m_est.setAttr((ref!=null? ((CompoundTerm)ref).functor() : null));
	    	Object val = ct.arg(3);
	    	m_est.setValue((val!=null? ((CompoundTerm)val).functor() : null));
	    	m_est.setBack(((CompoundTerm)ct.arg(4)).functor().equals("back"));
	    	javaToEclipse(new Atom("range"));
	    }
    
     
	    /**
	     * Called when Eclipse flushes FromEclipse queue
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
	        	 throw new RuntimeException("dataAvailable: Unable to *read* from input stream. ", ioe);
	         }
	         
	         String func = ct.functor();
	         
	         //Handle general failure...
	         if (func.equals("sffailed")) {
	        	 throw new RuntimeException("Unable to solve constraints. General failure.");
	         }
	         
	         //Handle eclipse dictating that user variables are filled in
	         else if (func.equals("sfuser")) sfuser();	         
	         
	         //Handle range info for user variables from eclipse
	         else if (func.equals("range")) range(ct);
	         
	         //Handle set information for user variable from eclipse
	         else if (func.equals("set")) set(ct);
	         
	         //Handle inability to collect range information for user variables 
	         else if (func.equals("norange")) throw new RuntimeException("Unable to collect range information for sfConsUser tagged attributes. Probably because it has not been set in constraint annotations");
	         
	         //Handle done on constraint goal, yield lock so solve() may complete
	         else if (func.equals("sfdonegoal")) yieldLockFromECRThread();

	         //Handle regular setting of an attributes value 
	         else if (func.equals("sfset")){
	        	 
	        	 //Variable setting index in constraint solving...  
	        	 int idx = ((Integer) ct.arg(1)).intValue();
	        	 
	        	 //Variable value
	        	 Object val = mapValueEJ(ct.arg(2));
	        	 
	        	 //Qualification for setting
	        	 String qual = ((Atom) ct.arg(3)).functor();
	        	 
	        	 //List of attributes to be set with value
	        	 Collection ctar = (Collection) ct.arg(4);
	        	 
	        	 //Index of Constraint context responsible 
	        	 int cidx = ((Integer) ct.arg(5)).intValue();
	        	 
	        	 //Get whether the value to be set is actually a component description, and if so adjust accordingly...
	        	 if (val instanceof String){
	        		 String val_s = (String) val;
	        	     if (val_s.indexOf("sfcd")==0) {
	        	    	 val = m_cresState.adjustSetValue(val_s.substring(4));
	        	     }
	        	 }
	        	 
	        	 //Backtrack as necessary...
	        	 m_cresState.backtrackConstraintAss(idx,cidx);

	        	 //Assign value to appropriate attributes...
	        	 boolean first=true;
	        	 Iterator tar_iter = ctar.iterator();
	        	 while (tar_iter.hasNext()){
	        		 CompoundTerm prim = (CompoundTerm) tar_iter.next();
	        		 cidx=((Integer)prim.arg(1)).intValue();
	        		 String key=((Atom)prim.arg(2)).functor();
	        		 
	        		 if (first){
	        			 //For VARs within Vectors which are first up then we need to set eval idx and key...
	    	        	 if (qual.equals("first")){
	    	        		FreeVar fv = new FreeVar();
	    	        		val = fv;
	        				fv.setConsEvalIdx(cidx); 
	        				fv.setConsEvalKey(key);
	        			 }
	        			 first=false;
	        		 }
	        		 try {
	        			 boolean success = m_cresState.addConstraintAss(m_solve_comp, key, val, cidx);
	        			 m_get_val = ""+success;
	        		 } catch (SmartFrogResolutionException smfre){
	        			 throw new RuntimeException(smfre);
	        		 }
         	    	 javaToEclipse();
	        	 }
	        	 
	         //Handle if we are asserting a sub type...
	         } else if (func.equals("sfsubtype")){
	        	 String attr = ((Atom) ct.arg(1)).functor();
	        	 Vector types = (Vector) mapValueEJ(ct.arg(2), true);  //by this stage we know its a Vector
	        	 m_cresState.setTyping(attr, types);
	         }
	    }
	
	    /**
	     * Yields lock so that solve() may finish
	     *
	     */
	    void yieldLockFromECRThread(){
	    	//Need to own lock in order to signal to opposition...
	    	 if (!m_ecr_sought_lock)  m_solverLock.lock();
	    	
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
		    		throw new RuntimeException("dataRequest: No data available to write. ");
		    	}
	    	} catch (IOException ioe){
	    		throw new RuntimeException("dataRequest: Unable to *write* on output stream. ", ioe);
    	    }
	    }	    
}

