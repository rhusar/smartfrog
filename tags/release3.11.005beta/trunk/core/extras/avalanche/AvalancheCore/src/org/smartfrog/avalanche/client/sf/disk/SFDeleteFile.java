/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.client.sf.disk;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;


public class SFDeleteFile extends PrimImpl implements Prim {
	private String fileToDelete = null ;
	boolean failOnDelete = false ; 
	TerminationRecord t = null ; 
	
	public SFDeleteFile() throws RemoteException {
		super();
	}
	
	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		fileToDelete = (String)sfResolve("fileToDelete", true);
		failOnDelete = sfResolve("failOnDelete", false, false) ;
		
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		Reference name = sfCompleteName();
		String filePath = null ; 
		boolean status = true ; 
		
		filePath = (new File(fileToDelete)).getAbsolutePath();
		// if there is only one '/' in the filepath refuse to delete it. 
		if( filePath.equals("") || 
			filePath.lastIndexOf(File.separatorChar) == filePath.indexOf(File.separatorChar) ){
			
			sfLog().err("File for deletion is one level from root, not deleting : " + filePath);
			status = false; 
             t = TerminationRecord.abnormal(
            		 "File for deletion is one level from root, not deleting : " + filePath, name);
		}else{
			try{
				sfLog().info("Deleting file : " + filePath );
				DiskUtils.forceDelete(filePath);
			}catch(IOException e){
				if( failOnDelete ){
					sfLog().err("File delete failed, failOnDelete was true terminating with error. filePath : "
							+ filePath);
					status = false; 
		             t = TerminationRecord.abnormal(
		            		 "File delete failed : " + filePath, name);
				}
				sfLog().err("File delete failed, failOnDelete was false continuing. filePath : "
						+ filePath);
			}
		}
		
		if( status ){
            t = TerminationRecord.normal(name);
		}
		
        Runnable terminator = new Runnable() {
                public void run() {
                    sfTerminate(t);
                }   
            };  
        new Thread(terminator).start();
	}

}
