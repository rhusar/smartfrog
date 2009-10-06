/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Feb 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.smartfrog.avalanche.server.ftp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.smartfrog.avalanche.server.RepositoryConfig;
import org.smartfrog.avalanche.server.modules.Module;
import org.smartfrog.avalanche.server.modules.ModuleCreationException;
import org.smartfrog.avalanche.server.modules.Repository;
import org.smartfrog.avalanche.server.modules.RepositoryConfigException;
import org.smartfrog.avalanche.server.modules.RepositoryConnectException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author bnaveen
 *
 * List of attributes for Repository configuration.
 * ftpServer : name or ip of the ftp server
 * userName : user name to connect with
 * password
 * path : optional, if present cd to this after connecting.
 * fileFormat : This is either binary or ascii. Default is binary
 * 
 *  ---  optional if using ftp proxy
 * ftp.proxyHost (default: <none>)
 * ftp.proxyPort (default: 80 if ftp.proxyHost specified)
 * ftp.nonProxyHosts (default: <none>)
 *  --- optional if using socks proxy 
 * socksProxyHost
 * socksProxyPort (default: 1080)
 */
/**
 * @author bnaveen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FTPRepository extends FTPModule implements Repository {
	public static final String PATH = "path" ;
	public static final String FTPSERVER = "ftpServer";
	public static final String USERNAME = "userName";
	public static final String PASSWORD = "password";
	public static final String FILEFORMAT = "fileFormat";
	public static final String PROXYHOST = "ftp.proxyHost";
	public static final String PROXYPORT = "ftp.proxyPort";
	public static final String SOCKSPROXYHOST = "socksProxyHort";
	public static final String SOCKSPROXYPORT = "socksProxyPort";
	
	private RepositoryConfig config ;
	private static Log log = LogFactory.getLog(FTPRepository.class);
	private String ftpServer, userName, password, path, fileFormat;
		
	/**
	 * 
	 */
	protected FTPRepository(RepositoryConfig cfg) throws RepositoryConfigException{
		
		this.config = cfg;
		super.ftp= new FTPClient();
		super.repository=this;
		/*
		 *	validate mandatory attributes   
		 */
		ftpServer = config.getAttribute(FTPSERVER);
		if( null == ftpServer ){
			log.error("FTP server location not specified");
			throw new RepositoryConfigException("FTP server location not specified");
		}
		userName = config.getAttribute(USERNAME);
		if( null == userName){
			log.error("FTP username location not specified");
			throw new RepositoryConfigException("FTP username location not specified");
		}
		password = config.getAttribute(PASSWORD);
		
		path = config.getAttribute(PATH);
		// get optional attributes for proxy settings. 
		String ftpProxyHost = config.getAttribute(PROXYHOST);
		if( null != ftpProxyHost){
      		System.getProperties().put( "ftp.proxyHost" ,ftpProxyHost);
		}
		String ftpProxyPort = config.getAttribute(PROXYPORT);
		if( null != ftpProxyPort){
      		System.getProperties().put( "ftp.proxyPort" ,ftpProxyPort);
		}
		// socks proxy ..	
		String socksProxyHost = config.getAttribute(SOCKSPROXYHOST);
		if( null != socksProxyHost){
      		System.getProperties().put( "socksProxyHost" ,socksProxyHost);
		}
		String socksProxyPort = config.getAttribute(SOCKSPROXYPORT);
		if( null != socksProxyPort){
      		System.getProperties().put( "socksProxyPort" ,socksProxyPort);
		}
		
		fileFormat=config.getAttribute(FILEFORMAT);
		if(fileFormat==null)		//default is BINARY format
			fileFormat="binary";
				
		//super.filePath=path;
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Repository#connect()
	 * This method will login to ftpServer with userName and password
	 */
	public void connect() throws RepositoryConnectException {
		
		//Connect to FTPServer
		try{
			int reply;
			ftp.connect(ftpServer);
			log.info("Connecting to " + ftpServer + ".");
			
			reply=ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply))
			{
				ftp.disconnect();
				log.error("FTP Server "+ ftpServer + " refused connection");
				throw new RepositoryConnectException("FTP Server "+ftpServer+" refused connection");
			}
						
		}
		catch(IOException e){
			if(ftp.isConnected())
			{
				try {
				ftp.disconnect();
				} catch(IOException f){
					log.warn("Disconnection of "+ ftpServer+" Failed", e);
					throw new RepositoryConnectException("Disconnection of "+ftpServer +" Failed",e);
				}
			}
			log.error("Connection to FTP Server"+ ftpServer+"Failed", e);
			throw new RepositoryConnectException("Connection to FTP Server "+ftpServer +" Failed",e);
		}
		//Loging to FTPServer
		try{
			if(!ftp.login(userName,password))
			{
				ftp.logout();
				log.error("Logging to "+ftpServer+" failed");
				throw new RepositoryConnectException("Logging to "+ftpServer+" failed");
				
			}
			
			//Set fileType for transfer
			if(fileFormat.equalsIgnoreCase("ascii"))
				ftp.setFileType(FTPClient.ASCII_FILE_TYPE);
			else
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
						
			//Change working directory
			if(path==null)
				path=ftp.printWorkingDirectory();
			else
				ftp.changeWorkingDirectory(path);
			super.filePath=path;
		}catch(IOException e){
			log.error(ftpServer+" login Failed.",e);
			throw new RepositoryConnectException(ftpServer+" login Failed.",e);
		}
		
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Repository#disconnect()
	 */
	public void disconnect() throws RepositoryConnectException {
		// disconnect and close connection. Is logout required?
		try{
			log.info("Disconnecting from " + ftpServer + ".");
			if(ftp.isConnected())
			{
				ftp.disconnect();
			}
		}catch(IOException e){
			log.warn("Disconnect failed",e);
			throw new RepositoryConnectException("Disconnect failed",e);
		}
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Repository#getConfig()
	 */
	public RepositoryConfig getConfig() {
		return new RepositoryConfig(config);
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Repository#getModuleByPath(java.lang.String)
	 */
	public Module getModuleByPath(String path) throws RepositoryConnectException {
		return getModule(this, path);
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Repository#getModule(org.smartfrog.avalanche.repository.Module, java.lang.String)
	 * This function will return a Module corresponding to the given path relative to the parent Module. For this, the function
	 * iterates recursively generating modules on the fly for the file names in the path.
	 * This function does not throw ModuleNotFoundException. This Exception should be removed from Repository interface. Instead,
	 * if module is not found, it will return null.
	 */
	public Module getModule(Module parent, String mpath)
			throws RepositoryConnectException {
		
				
		if(mpath==null)
			return null;
		
		if(parent==null) //relative to repostiory
			parent=this;
		
		mpath=mpath.trim(); //Everything is relative to Repository path
		
		String files[]=mpath.split("/");
		
		Module curModule=null;
		
		for(int i=0;i<files.length;i++)
		{
			if(files[i]==null)
				continue;
			curModule=parent.getModule(files[i]);
			if(curModule==null)
				break;
			parent=curModule;
		}
		
		return curModule;
		
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Repository#newModule(org.smartfrog.avalanche.repository.Module, java.lang.String)
	 * Creates a new Directory in the remote FTP server and returns Module interface to the same. 
	 */
	public Module newModule(Module parent, String mpath)
			throws ModuleCreationException, RepositoryConnectException {
				
		if(mpath==null)
			return null;
		
		if(parent==null) //relative to repostiory
			parent=this;
		
		mpath=mpath.trim();
		
		String files[]=mpath.split("/");
		
		Module curModule=null;
		for(int i=0;i<files.length;i++)
		{
			if(files[i]==null)
				continue;
			
			curModule=parent.getModule(files[i]);
			if(curModule==null)
			{
				curModule=parent.newModule(files[i]);
			}	
			parent=curModule;
		}
		
		return curModule;
			
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Repository#newModule(org.smartfrog.avalanche.repository.Module, java.lang.String, java.io.InputStream)
	 * Creates a Remote File in FTP machine and returns a Module interface for the same
	 */
	public Module newModule(Module parent, String mpath, InputStream source)
			throws ModuleCreationException, RepositoryConnectException {
		
		if(mpath==null)
			return null;
		
		if(parent==null) //relative to repostiory
			parent=this;
		
		mpath=mpath.trim(); //Everything is relative to Repository path
		
		String files[]=mpath.split("/");
		
		Module curModule=null;
		
		int i;
		for(i=0;i<files.length-1;i++)
		{
			if(files[i]==null)
				continue;
			
			curModule=parent.getModule(files[i]);
			if(curModule==null)
				curModule=parent.newModule(files[i]);
				
			parent=curModule;
		}
		curModule=parent.newModule(files[i],source);
		
		return curModule;
	}

	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Repository#deleteModule(org.smartfrog.avalanche.repository.Module)
	 * Deletes the Module(either file or directory) recursively. So be careful while calling this method
	 * as it may deleted all the contents in the directory recursively.
	 */
	public void deleteModule(Module m) throws RepositoryConnectException {
		m.delete();

	}
	
	/*
	 * This is specific to FTP repository and is used by the FTPModule.
	 * This method returns the repository Path.
	 */
	public String getPath()
	{
		return path;
	}
	
}
