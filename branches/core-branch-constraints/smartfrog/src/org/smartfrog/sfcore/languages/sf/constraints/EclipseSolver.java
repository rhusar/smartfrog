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
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.LinkResolutionState;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import com.parctechnologies.eclipse.Atom;
import com.parctechnologies.eclipse.CompoundTerm;
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
public class EclipseSolver extends PrologSolver implements Runnable, QueueListener {
	
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
     * Eclipse main goal to run in secondary thread
     */
    public void run(){  	
    	try {
    		m_eclipse.rpc("sfsolve");
    	} catch (Exception e){}
    }
    
    /**
     * Prepare the engine with two theories
     */
    public void prepareTheory(ComponentDescription cd, String coreFile, String coreFile2) throws Exception {
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
    public void solve(Context cxt, Vector attrs, Vector values, Vector logic, Vector goal) throws Exception{    	
    	
    	//Construct goal
    	String _attrs = mapValueJE(attrs);
    	String _values = mapValueJE(values, true); 
    	String _goal = mapValueJE(goal);
    	    	
    	m_get_val = "sfsolve("+_attrs+", "+_values+", "+
    					LinkResolutionState.getLRS().addConstraintEval(cxt)+", "+_goal+")";
    	
    	System.out.println(m_get_val);
    	
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
   }
    
    /**
     * Maps Java object to String for transfer to Eclipse 
     * @param v  object to be transformed
     * @return eclipse string
     */
    private String mapValueJE(Object v) { 
        return mapValueJE(v, false);
    }

    /**
     * Maps Java object to String for transfer to Eclipse 
     * @param v  object to be transformed
     * @param quoted indicates whether Strings should be double quoted
     * @return eclipse string
     */
    private String mapValueJE(Object v, boolean quoted) { 
    	if (v instanceof FreeVar) {
    		FreeVar fv = (FreeVar) v;
    		if (fv.getConsEvalKey()!=null){	    		
	    		if (fv.getConsEvalIdx()==-1){
	    			fv.setConsEvalIdx(LinkResolutionState.getLRS().getConsEvalIdx()+1);  //+1 as yet to add entry
	    			//System.out.println("Setting The Index! "+fv.getConsEvalIdx());
	    			return v.toString();
	    		} else {
	    			return "sfref("+fv.getConsEvalIdx()+", "+fv.getConsEvalKey()+")";
	    		}
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
        	else return v.toString();
        } else if (v instanceof SFNull){
        	return "sfnull";
        }
        else return null;
    }
    
    /**
     * Maps Eclipse object to Java object after transfer from Eclipse 
     * @param v  object to be transformed
     * @return transformed object
     */
    private Object mapValueEJ(Object v)  {
		if (v.equals(Collections.EMPTY_LIST)) return new Vector();
    	else if (v instanceof Number) return v;
        else if (v instanceof Collection) {
            Vector result = new Vector();
            Iterator it = ((Collection)v).iterator();  
            while (it.hasNext()) result.add(mapValueEJ(it.next()));
            return result;
        } else if (v instanceof String) return v;
        else if (v instanceof Atom){
        	Atom va = (Atom) v;
        	if (va.functor().equals("sfnull")) return SFNull.get();
        	else throw new SmartFrogEclipseRuntimeException("mapValueEJ: unknown data *from* solver " + v);
        } else if (v==null) return new FreeVar();
        else throw new SmartFrogEclipseRuntimeException("mapValueEJ: unknown data *from* solver " + v);	
    }	    

    /**
     * Unchecked Eclipse Exception, for throwing from Listeners dataAvailable, dataRequest 
     * @author anfarr
     *
     */
    class SmartFrogEclipseRuntimeException extends RuntimeException {
    	SmartFrogEclipseRuntimeException(String msg, Throwable cause){
    		super(msg, cause);
    	}
    	SmartFrogEclipseRuntimeException(String msg){
    		super(msg, null);
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
	         	    
	         //If done on constraint goal, yield lock so solve() may complete
	         if (func.equals("sfdonegoal")) yieldLockFromECRThread();
	         //Setting an attributes value 
	         else if (func.equals("sfset")){
	        	 int idx = ((Integer) ct.arg(1)).intValue();
	        	 Object val = mapValueEJ(ct.arg(2));
	        	 Collection ctar = (Collection) ct.arg(3);
	        	 int cidx = ((Integer) ct.arg(4)).intValue();
	        	 
	        	 Iterator tar_iter = ctar.iterator();
	        	 LinkResolutionState.getLRS().backtrackConstraintAss(idx, cidx);
	        	 
	        	 while (tar_iter.hasNext()){
	        		 CompoundTerm prim = (CompoundTerm) tar_iter.next();
	        		 cidx=((Integer)prim.arg(1)).intValue();
	        		 String key=((Atom)prim.arg(2)).functor();
	        		 LinkResolutionState.getLRS().addConstraintAss(idx, key, val, cidx);
	        		 
	        	 }
	        	 
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

