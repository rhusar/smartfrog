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
public class CopyOfEclipseSolver implements Runnable, QueueListener {
	
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
    		m_eclipse.rpc("start");
    	} catch (Exception e){}
    }
    
    /**
     * Prepare the engine with two theories
     */
    public void prepareTheory(String coreFile) throws Exception {
		//Set up eclipse...
    	m_eclipseEngineOptions  = new EclipseEngineOptions();
		
		// Connect the Eclipse's standard streams to the JVM's
		m_eclipseEngineOptions.setUseQueues(false);
	
		// Initialise Eclipse
		m_eclipse = EmbeddedEclipse.getInstance(m_eclipseEngineOptions);
	
		//Consult core theory file
		m_eclipse.compile(new File(coreFile));
		
		// Set up the java representation of two queue streams
	    m_java_to_eclipse = m_eclipse.getToEclipseQueue("java_to_eclipse");
	    //m_eclipse_to_java = m_eclipse.getFromEclipseQueue("eclipse_to_java");

	    m_java_to_eclipse.setListener(this);	 
	    
	   	new Thread(this).start();
 
    }
      

    
    
    
     
	    /**
	     * Called when Eclipse flushes source
	     */ 
	    public void dataAvailable(Object source)
	    {	}    	

	    /**
	     * Called when Eclipse demands data 
	     */
	    public void dataRequest(Object source)
	    {
	        ToEclipseQueue m_oqueue = null;
	        EXDROutputStream m_oqueue_formatted = null;
	    	
	    	if(m_oqueue == null){
				m_oqueue = (ToEclipseQueue) source;
				m_oqueue_formatted = new EXDROutputStream(m_oqueue);
		    }
		    	    	
	    	try { 
		    	String go = "sfsolve1"; 
		    	m_oqueue_formatted.write(go);
		    	go = "sfsolve2"; 
		    	m_oqueue_formatted.write(go);
		    	
	    	} catch (IOException ioe){
	    		
	    	}
	    }
	    
	    public static void main(String[] args){
	    	CopyOfEclipseSolver c = new CopyOfEclipseSolver(); 
	    	try{ c.prepareTheory("C:\\test.ecl"); } catch (Exception e){}
	    }
}

