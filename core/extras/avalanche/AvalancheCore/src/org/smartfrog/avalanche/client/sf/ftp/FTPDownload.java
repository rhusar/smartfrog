/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on May 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.smartfrog.avalanche.client.sf.ftp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author sandya
 * Download a file from any ftp server given the user name and password along
 * with remote file on the server and local file path where the file has to be 
 * transferred 
 */
public class FTPDownload {
	  private String ftpServer;
	  private String userName;
	  private String passWord;
	  private final String transferMode = "binary"; // default is binary
	  private FTPClient ftp = null;
	  
	  private static Log log = LogFactory.getLog(FTPDownload.class);	  
	  
	/**
	 * @param server
	 * @param uname
	 * @param passwd
	 */
	public FTPDownload(String server, String uname, String passwd) {	
	    ftpServer = server;
	    userName = uname;
	    passWord = passwd;
	    
	    ftp = new FTPClient();
	  }
	  
	/*
	 * Login to the ftpServer with the given userName and passWord
	 * Returns true if login is successful else returns false
	 */
	public boolean loginFtpServer() throws IOException
	  {
	    int reply;

        try{
        	// Connect to FTP Server
        	ftp.connect(ftpServer);
			log.info("Connecting to FTP Server : " + ftpServer);
			
			reply=ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				log.error("FTP Server "+ ftpServer + " refused connection");
				return false;
			}
        } catch(IOException e) {
        	if(ftp.isConnected()) {
        		try {
        			ftp.disconnect();
        		} catch(IOException f) {
        			log.warn("Disconnection of "+ ftpServer+" failed\n", e);
        		}
        	}
			log.error("Connection to FTP Server "+ ftpServer +" failed\n", e);
			throw e;
        }
        
		try{
			// Log in to the FTP Server with userName and passWord
			if(!ftp.login(userName,passWord)) {
				ftp.logout();
				log.error("Logging to "+ftpServer+" failed");
				return false;
			}
			
		} catch(IOException e) {
			log.error("Login to FTP Server " + ftpServer + " failed.\n",e);
		}
		log.info("FTP login successful on FTP server " + ftpServer + 
				" for the user " + userName + ".");
		return true;
	  }
	
	/*
	 * Downloads a file from FTP Server. Uses the default transfer mode 
	 */
	public boolean downloadFile(String downloadFile, String localFile) throws IOException{
		return(downloadFile(transferMode, downloadFile, localFile));
	}
	
	/*
	 * Downloads a file from FTP Server using the transfer mode specified
	 * Returns true if file is downloaded successfully else return false
	 */
	public boolean downloadFile(String trMode, String downloadFile, String localFile) throws IOException {
		OutputStream output = null;
		boolean fileRetrieved = false;
		
		File remoteFile = new File(downloadFile); // not a good idea to create a File object, this file eists only on server
		String remoteFileName = remoteFile.getName();
		String remotePath = remoteFile.getParent();
		
	    localFile = localFile.replace('\\', File.separatorChar);
	    localFile = localFile.replace('/', File.separatorChar);
	    String prefixPath = new File(localFile).getParent();
	    File pPath = new File(prefixPath);
	    boolean success = true;
	    if (!pPath.exists()) {
	    	success = new File(prefixPath).mkdirs();
	    }
	    if (!success) {
	    	log.error("Cannot create destinatino directory " + prefixPath);
	    	return false;
	    }
	    
	    try {
            // If user sets transfer mode, use it. Otherwise use the default
			// transfer mode which is 'binary'
			if (trMode.equalsIgnoreCase("ascii"))
				ftp.setFileType(FTPClient.ASCII_FILE_TYPE);
			else
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			
			if (!ftp.changeWorkingDirectory(remotePath))
			{
				log.error("Cannot change to the directory " + remotePath + ".\n");
				throw new IOException("FTP Error! Cannot change to the directory" + remotePath );
			}
				
	        output = new FileOutputStream(localFile);
	        fileRetrieved = ftp.retrieveFile(downloadFile, output);
		    output.close();
	    } catch (FTPConnectionClosedException e) {
	    	if (ftp.isConnected()) {
	    		try {
	    			ftp.disconnect();
	    		} catch (IOException f) {
	    			log.error("Error while disconnecting from FTP Server.\n", f);		
	    		}
	    	}
	    	log.error("FTP Server " + ftpServer + " closed connection.\n", e);
	    	throw e;
	    }  
	    
	    if (!fileRetrieved)
	    	log.error("The file " + remoteFileName + " could not be retrieved from FTP Server.");
	    else
	    	log.info("The file " + remoteFileName + " is downloaded from FTP Server.");
	    
	    return fileRetrieved;
	}
		
	public boolean logoutFtpServer() throws IOException{
		boolean success = false;
		try {
			success = ftp.logout();			
		} catch (FTPConnectionClosedException e) {
			log.error("FTP Server " + ftpServer + " closed connection\n", e);
			throw e;
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					log.error("Error while disconnecting from FTP Server" + ftpServer +
							".\n", ioe);
				}
			}
		}
		if (success)
			log.info("Successfully logged out from FTP Server " + ftpServer + ".");
		
		return success;
	}
}