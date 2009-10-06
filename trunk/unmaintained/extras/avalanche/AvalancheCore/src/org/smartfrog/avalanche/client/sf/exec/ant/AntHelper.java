/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Aug 15, 2005
 *
 */
package org.smartfrog.avalanche.client.sf.exec.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import java.io.File;
import java.io.PrintStream;

/**
 * @author sanjay, Aug 15, 2005
 *
 * TODO 
 */
public class AntHelper {
	
	protected File buildFile ;
	protected Project project = new Project();
	protected DefaultLogger listener = new DefaultLogger(); 
	protected File baseDir ; 

	/**
	 * 
	 */
	public AntHelper() {
		super();
	}
	/**
	 * Set the Ant build file to execute. call one of execute methods to run 
	 * any target in this file.
	 * @param f
	 */
	public void init(File f){
		this.buildFile = f;
		project.init();
	//	ProjectHelper ph = ProjectHelper.getProjectHelper();
	//	ph.parse(project, buildFile);
		ProjectHelper.configureProject(project, buildFile);
		
		// set default output to System.out
		listener.setOutputPrintStream(System.out);
		listener.setErrorPrintStream(System.out);
		
		listener.setMessageOutputLevel(Project.MSG_INFO);
		
		project.addBuildListener(listener);
		
	}
	public void setOutputStream(PrintStream out){
		listener.setOutputPrintStream(out);
	}
	public void setErrorStream(PrintStream out){
		listener.setErrorPrintStream(out);
	}
	public void execute() throws BuildException{
		project.executeTarget(project.getDefaultTarget());
	}
	public void execute(String target) throws BuildException{
		project.executeTarget(target);
	}
	public void setBaseDir(File dir){
		baseDir = dir ; 
		project.setBaseDir(dir);
	}
	public static void main(String []args){
		Project project = new Project();
		project.init();
		String buildFile = "E:\\sanjay\\dev\\projects\\workspace\\Avalanche-Core\\sample.xml";
		ProjectHelper ph = ProjectHelper.getProjectHelper();
		ph.parse(project, new File(buildFile));
		DefaultLogger log = new DefaultLogger();
		log.setOutputPrintStream(System.out);
		log.setErrorPrintStream(System.out);
		log.setMessageOutputLevel(Project.MSG_DEBUG);
		project.addBuildListener(log);
		
		project.executeTarget("rpmComponentJar");
		project.executeTarget("allJars");
		System.out.println("Done..");
		
	}
}
