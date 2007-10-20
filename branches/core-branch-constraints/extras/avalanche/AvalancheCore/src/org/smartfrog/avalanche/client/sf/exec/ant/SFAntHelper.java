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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;

/**
 * @author sanjay, Aug 15, 2005
 *
 * TODO 
 */
public class SFAntHelper extends PrimImpl implements Prim{
	
	public static final String BUILD_FILE = "buildFile" ;
	public static final String OUTPUT_LOGFILE = "outputLogFile" ;
	public static final String TARGET = "target" ;
	public static final String BASE_DIR  =  "baseDirectory" ;
	
	private AntHelper helper = new AntHelper() ;
	private File buildFile ;
	private File logFile ;
	private File baseDir ;
	
	private String target ;
	
	
	private static Log log = LogFactory.getLog(SFAntHelper.class);
	
	public SFAntHelper() throws RemoteException{
		
	}
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		try{
			if( null != logFile ){
				helper.setOutputStream(new PrintStream(new FileOutputStream(logFile)));
			}
			
			helper.init(buildFile);
			if( null != baseDir ){
				helper.setBaseDir(baseDir);
			}
			if ( null != target ){
				helper.execute(target);
			}else{
				// default target
				helper.execute();
			}
		}catch(Exception e){
			log.error(e);
			throw new SmartFrogException(e);
		}
	}
	
	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		String fName = (String)sfResolve(BUILD_FILE);
		if( null != fName){
			buildFile = new File(fName);
		}else{
			log.error("No Build file in Ant Config description");
			throw new SmartFrogException("Error No Build File !");
		}
		
		String lFile = (String)sfResolve(OUTPUT_LOGFILE);
		if( null != lFile){
			logFile = new File(lFile);
		}
		target = (String)sfResolve(TARGET);
		

	}

	/* (non-Javadoc)
	 * @see org.smartfrog.sfcore.prim.PrimImpl#sfTerminateWith(org.smartfrog.sfcore.prim.TerminationRecord)
	 */
	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}
}
