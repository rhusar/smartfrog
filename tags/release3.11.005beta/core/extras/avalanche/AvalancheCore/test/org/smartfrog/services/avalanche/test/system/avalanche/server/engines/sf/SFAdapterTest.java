/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package tests.org.smartfrog.avalanche.server.engines.sf;

import junit.framework.TestCase;
import org.smartfrog.services.sfinterface.SmartFrogAdapterImpl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SFAdapterTest extends TestCase {

	/*
	 * Test method for 'org.smartfrog.avalanche.server.engines.sf.SFAdapter.submit(String, String, String, Map, String[])'
	 */
	public void testSubmit() {

	}

	/*
	 * Test method for 'org.smartfrog.avalanche.server.engines.sf.SFAdapter.isActive(String)'
	 */
	public void testIsActive() {

	}

	/*
	 * Test method for 'org.smartfrog.avalanche.server.engines.sf.SFAdapter.getSFAttributes(String)'
	 */
	public void testGetSFAttributes() throws Exception{
		String url = "/Users/sanjaydahiya/dev/workspace/Avalanche-Core/src/org.smartfrog/avalanche/client/monitor/anubis/AnubisDeployer.sf" ;
		Map map = SmartFrogAdapterImpl.getAllAttribute(url);
		
		Set s = map.keySet();
		Iterator it = s.iterator();
		while(it.hasNext()){
			System.out.println("Next -- "+ (String)it.next());
		}
	}

	/*
	 * Test method for 'org.smartfrog.avalanche.server.engines.sf.SFAdapter.stopDaemon(String)'
	 */
	public void testStopDaemon() {

	}

}
