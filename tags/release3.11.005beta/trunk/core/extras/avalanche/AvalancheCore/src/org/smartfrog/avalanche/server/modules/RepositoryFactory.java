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
import org.smartfrog.avalanche.server.ftp.FTPRepositoryFactory;

/**
 * @author sanjay
 *
 * Abstract factory for repositories, supported factory types are DISK, FTP, XINDICE  
 */
public abstract class RepositoryFactory {
	public static final int DISK = 1;
	public static final int FTP = 2;
	public static final int XINDICE = 3;
	
	/**
	 * Creates a factory object for the requested factory type. 
	 * Use the factory to create repository and its objects.  
	 * @param factoryType type of repository factory to create, must be a supported factory type. 
	 * @return
	 * @throws RepositoryFactoryNotFoundException if the factory type is not supported. 
	 */
	public static RepositoryFactory getFactory(int factoryType) 
		throws RepositoryFactoryNotFoundException{
		
		RepositoryFactory factory = null ;
		
		switch(factoryType){
		case DISK : 
			//factory = new DiskRepositoryFactory();
			break ;
		case FTP:
			factory = new FTPRepositoryFactory();
			break;
		case XINDICE:
		//	factory = new XindiceRepositoryFactory();
			break;
		}
		
		if ( null == factory ){
			throw new RepositoryFactoryNotFoundException();
		}
		return factory;
	}
	/**
	 * Repository Config attributes are specific to the type of repository, look javadocs of 
	 * inherited classes.  
	 *  
	 * @param config
	 * @return
	 * @throws RepositoryConfigException the configuration doesnt contain all required attributes 
	 * for creation of the repository. See javadoc of the factory type you are trying to create.  
	 */
	public abstract Repository getRepository(RepositoryConfig config) throws RepositoryConfigException ;
}
