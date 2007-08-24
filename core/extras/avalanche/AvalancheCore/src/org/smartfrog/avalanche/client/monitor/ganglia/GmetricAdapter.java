/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.avalanche.client.monitor.ganglia;

import org.smartfrog.services.xmpp.MonitoringEvent;
import org.smartfrog.avalanche.client.monitor.MonitoringService;
import org.smartfrog.avalanche.client.monitor.Provider;

import java.util.Iterator;
import java.util.Map;

/**
 * gmetric adapter - calls gmetric, whcih should be installed prior.
 * Next implementation should write to UDP socket directly.  
 * @author sanjaydahiya
 *
 */
public class GmetricAdapter implements MonitoringService {

	private String gmetricPath = "/usr/bin/gmetric" ;
	
	private static final String STATUS = "STATUS" ;
	private static final String MSG = "MSG";
	
	public void registerProvider(Provider p) {
		// can send without registering for ganglia
	}

	public void unRegisterProvider(Provider p) {
		//	can send without registering for ganglia

	}
	
	/* 
	 * Ignores the provider argument, it should be null for Ganglia. 
	 * @see org.smartfrog.avalanche.client.monitor.MonitoringService#notify(org.smartfrog.avalanche.client.monitor.Provider, org.smartfrog.avalanche.client.monitor.MonitoringEvent)
	 */
	public void notify(Provider p, MonitoringEvent e) {
		// Create a commandline and send to gmetric
		// assume gmetric is available in path.
		
		if( null == e){
			// log and exit 
		}
		String key = e.getModuleId();
		
		String value = STATUS +"=" + e.getModuleState() + "," +
						MSG + "=" + e.getMsg();
		
		Map props = e.getPropertyBag();
		
		if (null != props){
			Iterator it = props.keySet().iterator();
			while(it.hasNext()){
				String k = (String)it.next();
				String v = (String) props.get(k);
				
				value += k + "=" + v + "," ;
			}
		}
		
		// create a command line now 
		String []cmd = new String[] {gmetricPath, "-n", key, "-v", value, "-t", "string" }; 
		//String cmdLine = gmetricPath + " -n " + key + " -v " + value + " -t string" ;
		//System.out.println("GExec : " + cmdLine);
		try{
			Process process = Runtime.getRuntime().exec(cmd);
			// wait for the event to go before exiting.
			process.waitFor();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
