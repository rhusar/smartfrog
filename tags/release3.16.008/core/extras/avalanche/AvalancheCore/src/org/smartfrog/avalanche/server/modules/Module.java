/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jan 29, 2005
 *
 */
package org.smartfrog.avalanche.server.modules;

import java.io.InputStream;

/**
 * @author sanjay
 *
 */
public interface Module {
	/*
	 * Returns an Array of sub modules of this module.
	 */
	public Module[] list() throws RepositoryConnectException;
	/*
	 * Returns an Array of sub modules of this modulem matched by filter.
	 */
	public Module[] list(ModuleFilter filter) throws RepositoryConnectException;
	/*
	 * Returns array of module IDs of sub modules of this module.    
	 */
	public String[] listModules() throws RepositoryConnectException;
	/*
	 * Returns module ID of this module.
	 */
	public String getModuleID();
	/*
	 * Returns an absolute path for this module, the path format depends 
	 * on the implementation type of the module.   
	 */
	public String getModulePath();
	/**
	 * @param moduleID
	 * @return null if module not found, else the module. 
	 * @throws RepositoryConnectException 
	 * @throws ModuleNotFoundException 
	 */
	public Module getModule(String moduleID) 
		throws RepositoryConnectException;
	/**
	 * Creates a new module, as a child module of the present module. 
	 * @param moduleID
	 * @return crated module if successful
	 * @throws RepositoryConnectException if the repository is not avaiable, 
	 * 	applicable only for remote repositories. 
	 * @throws ModuleCreationException if the module ID is null or invalid. 
	 */
	public Module newModule(String moduleID) 
		throws RepositoryConnectException, ModuleCreationException ;

	/**
	 * 
	 * Create a new module as child node of the current module, the new module created will 
	 * be a leaf module and its contents would be read from InputStream.  
	 * @param moduleId
	 * @param source
	 * @return
	 * @throws ModuleCreationException
	 * @throws RepositoryConnectException
	 */
	public Module newModule(String moduleId, InputStream source)
		throws ModuleCreationException, RepositoryConnectException ;
	

	/**
	 * Calling this method would delete the module from the repository.
	 * Another way of deleting the modules is from the repository object. both have exactly same 
	 * impact. It deletes the module physically from the back end storage. 
	 * @throws RepositoryConnectException
	 */
	public void delete()
		throws RepositoryConnectException;
	
	/**
	 * 
	 * Returns the content of the file representing the module as an InputStream. If the 
	 * module represents a directory then it returns null. 
	 *  
	 * @return
	 * @throws RepositoryConnectException
	 * @deprecated use the other method instead.
	 */
	public InputStream getContent() throws RepositoryConnectException;
	
	/**
	 * 
	 * Write content in the output stream passed. 
	 * @param stream
	 * @return true if the content was written false if the module is not a leaf node and 
	 * content can not be written. 
	 * @throws RepositoryConnectException
	 */
	public boolean getContent(java.io.OutputStream stream) throws RepositoryConnectException;
	/**
	 * If the module represents a file then it returns true if its a directory 
	 * then it returns false.
	 * @return
	 */
	public boolean isLeafModule();
	
	/**
	 * returns the parent module of this module. if the module is repository root then it 
	 * returns null.
	 * @return
	 */
	public Module getParent();
	
	/**
	 * 
	 * returns the reference to root repository of the module. All modules are associated with a root
	 * repository, which can be used to create/delete or retrive any module.   
	 * @return
	 */
	public Repository getRepository();
	
	/**
	 * 
	 * returns the implementation type of this module, this method should be used only in cases
	 * strong type binding with implementation is needed. 
	 * @return
	 */
	public int getType();
}
