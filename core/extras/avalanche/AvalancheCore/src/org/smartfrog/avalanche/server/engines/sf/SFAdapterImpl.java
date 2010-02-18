/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.engines.sf;

import org.smartfrog.sfcore.processcompound.SFProcess;

/**
 * 
 * @author sanjaydahiya
 * @deprecated Used
 */
public class SFAdapterImpl {
	private String sfHome = null ;
	private int port = 0 ;
	
	protected SFAdapterImpl(){
		
	}
	
	/** 
	 * This method checks on localhost on the given port if sfdaemon is running
	 * it uses that, otherwise it starts a new daemon .
     * @param sfHome used
     * @param port used
	 * @return unsed
     * @throws Exception unused
	 */
	public static SFAdapterImpl getInstance(String sfHome, int port) throws Exception{
		SFAdapterImpl adapter = new SFAdapterImpl();
		adapter.sfHome = sfHome ;
		adapter.port = port ; 
		
		SFProcess.getRootLocator().getRootProcessCompound(null, port);
		
		return adapter; 
	}

}
