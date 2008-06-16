package org.smartfrog.authoringtool.services;

import java.util.HashMap;
import java.util.Map;

import org.openarchitectureware.workflow.WorkflowRunner;
import org.openarchitectureware.workflow.monitor.NullProgressMonitor;



public class SFCodeGenTrigger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
	public void generate()
	{
		System.out.println("Trigger invoked");
		
		String wfFile = "somePath\\workflow.oaw";
		Map properties = new HashMap();
		Map slotContents = new HashMap();
		new WorkflowRunner().run(wfFile ,new NullProgressMonitor(), properties, slotContents);
		

	}
}
