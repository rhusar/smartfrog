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

import org.smartfrog.avalanche.server.RepositoryConfig;

import java.io.InputStream;

/**
 * @author sanjay
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Repository extends Module {
	public void connect() throws RepositoryConnectException ;
	public void disconnect() throws RepositoryConnectException ;
	
	/*
	 * Returns read only repository configuration. 
	 */
	public RepositoryConfig getConfig();
	/*
	 * Returns a module by it s absolute pathname
	 */
	public Module getModuleByPath(String path) 
		throws RepositoryConnectException;
	/*
	 * Resolves module path by its parent and returns the module. 
	 */
	public Module getModule(Module parent, String path) 
		throws RepositoryConnectException;
	
	/*
	 * Resolves module path by its parent and returns the module. 
	 */
	public Module newModule(Module parent, String path) 
		throws ModuleCreationException, RepositoryConnectException;
	
	public Module newModule(Module parent, String moduleId, InputStream source)
		throws ModuleCreationException, RepositoryConnectException ;
	
	/**
	 * delete a child module
	 * @param m
	 * @throws RepositoryConnectException
	 */
	public void deleteModule(Module m)
		throws RepositoryConnectException;
	
}
