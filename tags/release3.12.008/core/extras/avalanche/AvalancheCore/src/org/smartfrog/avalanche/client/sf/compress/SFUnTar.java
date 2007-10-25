/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Aug 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.compress;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * @author sandya
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SFUnTar extends PrimImpl implements Prim {
	private final String TARFILE = "tarFile";
	private final String OUTPUTDIR = "outputDir";
	private final String SHDTERMINATE = "shouldTerminate";
	
	private String tarFile, outputDir;
	boolean shouldTerminate = true;

	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFUnTar() throws RemoteException {
		super();
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		try {
			// mandatory attributes
			tarFile = sfResolve(TARFILE, tarFile, true);
			outputDir = sfResolve(OUTPUTDIR, outputDir, true);
			
			// optional attribute
			shouldTerminate = sfResolve(SHDTERMINATE, true, false);			
		}catch (ClassCastException e) {
			sfLog().err("Unable to resolve Component",e);
			throw new SmartFrogException("Unable to resolve Component",e);
		}		
	}
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		
		outputDir = outputDir.replace('\\', File.separatorChar);
		outputDir = outputDir.replace('/', File.separatorChar);
		File dir = new File(outputDir);
		
		tarFile = tarFile.replace('\\', File.separatorChar);
		tarFile = tarFile.replace('/', File.separatorChar);
		File file = new File(tarFile);
		
		sfLog().info("Starting uncompressing file : " + file + ", in dir : " + dir);
		try{
			//ZipUtils.unTar(file, dir);
			ZipUtils.unTarProc(file, dir);
		}catch(IOException ioe){
			sfLog().err("Error : " + ioe.getMessage());
			throw new SmartFrogException(ioe);
		}
		sfLog().info("Finished un-tar file ");
				
        // terminate synchronously
		TerminationRecord tr = new TerminationRecord("normal", "Terminating ...", sfCompleteName());
		sfTerminate(tr);
	}


	public synchronized void sfTerminateWith(TerminationRecord status) {
		super.sfTerminateWith(status);
	}

}
