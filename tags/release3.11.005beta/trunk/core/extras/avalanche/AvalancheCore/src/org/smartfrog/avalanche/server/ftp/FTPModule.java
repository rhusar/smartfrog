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
import org.apache.commons.net.ftp.FTPFile;
import org.smartfrog.avalanche.server.modules.Module;
import org.smartfrog.avalanche.server.modules.ModuleCreationException;
import org.smartfrog.avalanche.server.modules.ModuleFilter;
import org.smartfrog.avalanche.server.modules.Repository;
import org.smartfrog.avalanche.server.modules.RepositoryConnectException;
import org.smartfrog.avalanche.util.DiskUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @author bnaveen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class FTPModule implements Module {
	private static final int DIRECTORY=0;
	private static final int FILE=1; 
	public static final String FSEPERATOR="/"; //TODO :check file seperator "\"
	
	private FTPModule parent ;
	private String moduleId ; 
	protected FTPRepository repository ;
	protected FTPClient ftp;
	private int fileType;
	protected String filePath;
		
	private static Log log = LogFactory.getLog(FTPModule.class);

	/**
	 * Creates a new FTPModule. This is the default constructor that should be used whenever creating a new Module
	 * @param p 	: This is the parent Module
	 * @param mid	: This is the moduleID
	 * @param ftype : This is the fileType (file or directory) 
	 * @throws RepositoryConnectException
	 */
	private FTPModule(FTPModule p, String mid, int ftype) throws RepositoryConnectException
	{
		parent=p;
		moduleId=mid;
		if(p==null)
		{
			repository=null;
			ftp=null;
			filePath=null;
		}
		else
		{
			repository=p.repository;
			ftp=repository.ftp;
			filePath=parent.filePath+FSEPERATOR+moduleId;
		}
		
		fileType=ftype;
	}
	
	/**
	 * Creates a new FTPModule. This constructor should be used only in the case of creating a Repository. All other cases
	 * use the other constructor.
	 */
	protected FTPModule()
	{
		parent=null;
		moduleId="/";
		repository=null; //This will be pointed to Repository in FTPRepository constructor
		ftp=null; //new ftp object will be constructed in FTPRepository constructor
		fileType=DIRECTORY;
		filePath=null; //This will be set in FTPRepository constructor 
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#newModule(java.lang.String)
	 * Creates a new directory in the given Module and returns Module interface for the same
	 */
	public Module newModule(String mID) throws RepositoryConnectException,
			ModuleCreationException {
		if(mID==null)
			throw new ModuleCreationException("No name specified for creating directory");
		String newPath=filePath+FSEPERATOR+mID;
		try{
			ftp.makeDirectory(newPath);
		}catch (Exception e){
			log.error("Unable to create new Directory "+ newPath,e);
			throw new ModuleCreationException("Unable to create new Directory "+ newPath,e);
		}
		return(new FTPModule(this,mID,DIRECTORY));
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#newModule(java.lang.String, java.io.InputStream)
	 * Creates a new file in the given Module and returns Module interface for the same
	 */
	public Module newModule(String mID, InputStream source)
	throws ModuleCreationException, RepositoryConnectException {
		if(mID==null)
			throw new ModuleCreationException("No name specified for creating file");
		
		String newPath=filePath+FSEPERATOR+mID;
		try{
			ftp.storeFile(newPath,source);
		}catch (Exception e){
			log.error("Unable to create new File "+ newPath,e);
			throw new ModuleCreationException("Unable to create new File "+ newPath,e);
		}
		return(new FTPModule(this,mID,FILE));
		
	}
	
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#delete()
	 * Deletes a Modules recursively. So be careful while calling this method. All its subdirectories will be deleted
	 * even though they are not empty.
	 */
	public void delete() throws RepositoryConnectException {
		
		try{
			if(fileType==FILE)
			{
				ftp.deleteFile(filePath);
				return;
			}
			FTPFile files[]=ftp.listFiles(filePath);
			if(files!=null)
			{
				for(int i=0;i<files.length;i++)
				{
					Module tmpMod=getModule(files[i].getName());
					tmpMod.delete();
				}
				ftp.removeDirectory(filePath);
			}
			
		}catch (Exception e){
			log.error("Unable to delete Module "+moduleId,e);
			throw new RepositoryConnectException("Unable to delete Module "+moduleId,e);
		}
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#getContent()
	 */
	public InputStream getContent() throws RepositoryConnectException {
		
		try{
			return ftp.retrieveFileStream(filePath);
		}catch (Exception e){
			log.error("Unable to get file contents "+ filePath,e);
			throw new RepositoryConnectException("Unable to get file contents "+ filePath,e);
		}
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#getModule(java.lang.String)
	 */
	public Module getModule(String mID) throws RepositoryConnectException
    {
		
		try{
			FTPFile files[]=ftp.listFiles(filePath);
			if(files==null)
				return null;
			for(int i=0;i<files.length;i++)
				if(mID.equals(files[i].getName())==true)
				{
					if(files[i].isDirectory()==true)
						return(new FTPModule(this,mID,DIRECTORY));
					else
						return(new FTPModule(this,mID,FILE));
						
				}
		}catch (Exception e){
			log.error("Unable to create Module "+mID,e);
			throw new RepositoryConnectException("Unable to create Module "+mID,e);
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#getModuleID()
	 */
	public String getModuleID() {
		return moduleId;
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#getModulePath()
	 */
	public String getModulePath() {
		return filePath.substring(repository.getPath().length()); //Return path relative to repository
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#getParent()
	 */
	public Module getParent() {
		return parent;
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#getRepository()
	 */
	public Repository getRepository() {
		return repository;
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#getType()
	 */
	public int getType() {
		return org.smartfrog.avalanche.server.modules.RepositoryFactory.FTP;
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#isLeafModule()
	 */
	public boolean isLeafModule() {
		if(fileType==DIRECTORY)
			return false;
		else
			return true;
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#list()
	 */
	public Module[] list() throws RepositoryConnectException {
		
		
		try{
			if(fileType==FILE)  //If type is File then only the file will be returned
			{
				
				Module modules[]={this};
				return modules;
			}
			FTPFile files[]=ftp.listFiles(filePath);
			Module modules[]=new Module[files.length];
			if(files!=null)
			{
				for(int i=0;i<files.length;i++)
				{
					Module tmpMod=getModule(files[i].getName());
					modules[i]=tmpMod;
				}
				return modules;
			}
			else 
				return null;
			
		}catch (Exception e){
			log.error("Unable to List Modules in "+moduleId,e);
			throw new RepositoryConnectException("Unable to list Modules in "+moduleId,e);
		}
		
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#list(org.smartfrog.avalanche.repository.ModuleFilter)
	 */
	public Module[] list(ModuleFilter filter) throws RepositoryConnectException {
		
		try{
			if(fileType==FILE)
			{
				
				Module modules[]={this};
				return modules;
			}
			FTPFile files[]=ftp.listFiles(filePath);
			Module modules[]=new Module[files.length];
			if(files!=null)
			{
				for(int i=0;i<files.length;i++)
				{
					Module tmpMod=getModule(files[i].getName());
					if(filter.accept(tmpMod))
						modules[i]=tmpMod;
				}
				return modules;
			}
			else 
				return null;
			
		}catch (Exception e){
			log.error("Unable to List Modules in "+moduleId,e);
			throw new RepositoryConnectException("Unable to list Modules in "+moduleId,e);
		}
		
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#listModules()
	 */
	public String[] listModules() throws RepositoryConnectException {
		
		String fNames[]=null;
		try{
			fNames=ftp.listNames(filePath); //This gives path from root
			if(fNames!=null)
			{
				for(int i=0;i<fNames.length;i++)
					fNames[i]=fNames[i].substring(filePath.length()+1);
			}
		}catch(Exception e){
			log.error("Unable to list Module contents "+moduleId,e);
			throw new RepositoryConnectException("Unable to list Module contents "+moduleId,e);
		}
		return fNames;
	}
	/* (non-Javadoc)
	 * @see org.smartfrog.avalanche.repository.Module#getContent(java.io.OutputStream)
	 */
	public boolean getContent(OutputStream stream)
			throws RepositoryConnectException {
		
		boolean ret = false;
		try{
			if( isLeafModule() ){
				InputStream istream=ftp.retrieveFileStream(filePath);
				DiskUtils.fCopy(istream, stream );
				istream.close();
				ret = ftp.completePendingCommand();
			}
		}catch(IOException e){
			log.error("Error reading file : " + filePath, e);
			throw new RepositoryConnectException(e);
		}
		return ret;
	}
}
