/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on May 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.exec.simple;

//import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**StartComponent.java
 * @author bnaveen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StartComponent {
	private static Log log = LogFactory.getLog(StartComponent.class);
	private String componentName;
	private String command;
	private String env[];
	private Process proc;
	
    private BufferedReader stdInput=null; 
    private BufferedReader stdError=null; 
       
	/**
	 * @throws java.rmi.RemoteException
	 */
	public StartComponent(String cName, String cPath, String envp){
		componentName=new String(cName);
		command=new String(cPath);
		env = envp.split(",");		
		log.info("Setting env : " + envp);
	}

	public void startApplication() throws IOException {
		Runtime run;
				
		log.info("Starting Component "+componentName);
		run=Runtime.getRuntime();
		log.info("Starting application " + command);
		
		int exitVal = 0;
		try {
			System.out.println("Running the command : "+command);
			proc = run.exec(command, env);
			stdInput = new BufferedReader( new InputStreamReader(proc.getInputStream() )) ;
			stdError = new BufferedReader( new InputStreamReader(proc.getErrorStream() ));
			
			readOutput();
			exitVal = proc.waitFor();			
			if (exitVal != 0) {
				log.error("Return for the command " + command + " is " + exitVal);
				String err = "Error : ";
				String s = null;
				while ((s=stdError.readLine()) != null) {
					err += s; 
				}
				throw new IOException("Return value for the command " + command + " is  not 0." +  err);
			}
		} catch (IOException e) {
			log.error("Error while executing the command : " + command, e);
			throw new IOException(e.toString());			
		} catch (InterruptedException ie) {
			log.error("Error while executing the command : " + command, ie);
			throw new IOException(ie.toString());
		}
	}
	
	public void readOutput() throws IOException {
		try {
               // read the output from the command
               Thread thr = new Thread(new IReader());
               thr.start();
               
		} catch(Exception e) {
			log.error("Unable to get Output " + command,e);
			throw new IOException(e.toString());
		}
		
	}
	
	public boolean isRunning(int index){
		try {
			proc.exitValue();
			return false; //The application has exited
		}catch(IllegalThreadStateException e) {
			return true;
		}
	}
	
	public void stopApplication() 
			throws IOException, InterruptedException{
		readOutput();
		try {
			proc.destroy();
			proc.waitFor();
			log.debug("Destroyed Process " + command +" forcibly");
			
			stdInput.close();
			stdError.close();
			log.info(componentName + " : Process exited with return value " +
							proc.exitValue());
		} catch(IOException ioe) {
			log.error("Unable to close InputStream and ErrorStream for " +
					command, ioe);
			throw new IOException(ioe.toString());
		} catch(IllegalThreadStateException ite) {
			log.error("Unable to kill the process " + command, ite);
			throw new IllegalStateException(ite.toString());
		} catch(InterruptedException ie) {
			log.error("Interrupted. Waiting for process " + command +
					" to die", ie);
			throw new InterruptedException(ie.toString());
		}	
	}
	
	private class IReader implements Runnable{
			
			public void run(){
				String s = null ;
				String err = null;
				try{
					while ((s = stdInput.readLine()) != null) {
						//System.out.println(s);
						log.info(s);
					}
					err = stdError.readLine();
					while ((s = stdError.readLine()) != null) {
						//System.err.println(s);
			        	err += s;
		                log.error(s);
					}
					
					if (null != err) {
						throw new IOException(err);
					}
				} catch(IOException ioe){
					log.error(ioe);
				}
			}
	}
}


