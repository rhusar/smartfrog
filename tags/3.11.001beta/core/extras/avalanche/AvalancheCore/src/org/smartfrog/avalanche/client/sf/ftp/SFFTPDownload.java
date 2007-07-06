/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jul 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.ftp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.IOException;
import java.rmi.RemoteException;

public class SFFTPDownload extends PrimImpl implements Prim {
	private static final String FTPSERVER = "server";
	private static final String USERNAME = "userName";
	private static final String PASSWORD = "password";
	private static final String DOWNLOADFILE = "downloadFile";
	private static final String LOCALFILE = "localFile";
	private static final String TRMODE = "trMode";
	private static Log log = LogFactory.getLog(SFFTPDownload.class);
	
	String server, userName, password;
	String downloadFile, localFile, trMode;	
	FTPDownload ftpDownload;
	
	/**
	 * @throws java.rmi.RemoteException
	 */
	public SFFTPDownload() throws RemoteException {
		super();
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		try{
			sfLog().info("Logging in FTP Server : " + server);
			if(ftpDownload.loginFtpServer()){
				sfLog().info("FTP Login successful.");
			}else{
				sfLog().err("FTP Login failed.");
				throw new SmartFrogException("FTP Login failed");
			}
		}catch(IOException ioe){
			sfLog().err("FTP Error: failed while logging in : " + ioe.getMessage());
			throw new SmartFrogException("FTP Error: failed while logging in : " ,ioe);
		} 
		try{
			sfLog().info("Starting FTP Download : " +downloadFile);
			if( ftpDownload.downloadFile(trMode, downloadFile, localFile) ){
				sfLog().info("FTP Download completed, remote file : " + 
						downloadFile + ", localFile :" + localFile);
			}else{
				sfLog().err("FTP Download failed, remote file : " + downloadFile );
				throw new SmartFrogException("FTP Download failed, remote file : " + downloadFile);
			}
		}catch(IOException e){
			sfLog().err("FTP Error! failed while downloading file ");
			throw new SmartFrogException("FTP Error! failed while downloading file : " ,e);
		}
		
		sfLog().info("Creating termination record : FTP Component .. ");
		TerminationRecord tr = new TerminationRecord("normal", "Terminating ...", sfCompleteName());
		sfLog().info("Terminating FTP Component .. ");
		this.sfTerminate(tr);
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		try{
			server = (String)sfResolve(FTPSERVER);
			userName = (String)sfResolve(USERNAME);
			password = (String)sfResolve(PASSWORD);
			downloadFile = (String)sfResolve(DOWNLOADFILE);
			localFile = (String)sfResolve(LOCALFILE);
			trMode = (String)sfResolve(TRMODE);
			ftpDownload = new FTPDownload(server, userName, password);
			
			// fail here itself if parameters are not proper
			if( null == server || null == userName || null == downloadFile 
					|| null == localFile || null == password ){
				String msg = "Error, FTP Arguments are not complete " + 
								"Server : " + server + "\n" + 
								"User Name : " + userName + "\n" + 
								"Password : " + password + "\n" + 
								"downloadFile : " + downloadFile + "\n" + 
								"localFile : " + localFile + "\n" ; 
				sfLog().err(msg);
				throw new SmartFrogException(msg);
			}
			
		}catch(ClassCastException e){
			log.error("Unable to resolve Component",e);
			// TODO : see if its better to terminate than throwing exception.
			throw new SmartFrogException("Unable to resolve Component",e);
		}
	}

	public synchronized void sfTerminateWith(TerminationRecord status) {
		try{
			// possibly terminating abnormally 
			if( ftpDownload.logoutFtpServer() ){
				sfLog().info("Disconnected from FTP Server");
			}else{
				sfLog().error("Error: Disconnected from FTP Server");
			}
		}catch(IOException e){
			// terminate 
			sfLog().error("FTP Error : Failed to disconnet from server");
		}
		super.sfTerminateWith(status);
	}

}
