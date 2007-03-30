package org.smartfrog.sfcore.languages.csf.plugins;

import java.io.File;
import java.util.List;

import org.smartfrog.sfcore.languages.csf.constraints.PrologSolver;

import com.parctechnologies.eclipse.CompoundTerm;
import com.parctechnologies.eclipse.EclipseEngine;
import com.parctechnologies.eclipse.EclipseEngineOptions;
import com.parctechnologies.eclipse.EmbeddedEclipse;


/**
 * Implmentation of solver for Eclipse
 */
public class EclipseSolver extends PrologSolver {

    // Default Eclipse options
    private EclipseEngineOptions m_eclipseEngineOptions;  
    // Object representing the Eclipse process
    private EclipseEngine m_eclipse;
    // Query Results
    private List m_results;
       
    public void prepareTheory(String prologFile) throws Exception {
	//Eclipse Options
	m_eclipseEngineOptions  = new EclipseEngineOptions();

	// Connect the Eclipse's standard streams to the JVM's
	m_eclipseEngineOptions.setUseQueues(false);

	// Initialise Eclipse
	m_eclipse = EmbeddedEclipse.getInstance(m_eclipseEngineOptions);

	//Consult theory file
    	m_eclipse.compile(new File(prologFile));   	
    }

    public void solveQuery(StringBuffer constraint) throws Exception {
    	CompoundTerm ct = m_eclipse.rpc(constraint.toString());
    	m_results = (List)((CompoundTerm)ct.arg(1)).arg(2);
    	
    	// Destroy the Eclipse process
    	((EmbeddedEclipse) m_eclipse).destroy();
    }
    
    public Object getBinding(int var){
    	return m_results.get(var);
    }
}

