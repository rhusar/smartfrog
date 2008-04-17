/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.services.avalanche.test.system.avalanche.server.monitor;

import junit.framework.TestCase;
import org.smartfrog.avalanche.server.monitor.ModuleState;
import org.smartfrog.avalanche.server.monitor.ganglia.GmondAdapter;
import org.smartfrog.avalanche.util.XMLUtils;

public class MonitoringServerTest extends TestCase {
	
	public void testUpdate(){
		GmondAdapter adapter = new GmondAdapter();
		adapter.update();
		System.out.println(adapter.getState());
		try{
			XMLUtils.docToStream(adapter.getStateAsXML(), System.out);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		ModuleState state = adapter.getModuleState("ie9908.india.hp.com","Tomcat");
		System.out.println();
		if( null != state )
			System.out.println(state.toString());
		else{
			System.out.println("state is null");
			fail();
		}
		
		System.out.println("Listing hosts ... ") ;
		String []hosts = adapter.listHosts();
		for(int i=0;i<hosts.length;i++){
			System.out.println("HOST: "+ hosts[i]) ;
		}
	}
}
