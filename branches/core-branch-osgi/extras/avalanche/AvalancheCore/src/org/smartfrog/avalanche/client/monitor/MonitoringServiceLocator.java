/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.avalanche.client.monitor;

import org.smartfrog.avalanche.client.monitor.ganglia.GmetricAdapter;

public class MonitoringServiceLocator {
	
	public static final int GANGLIA = 1;
	
	private int defaultService = GANGLIA ;
	
	/**
	 * Initializes the monitoring service client used to 
	 * send distributed events to server. 
	 * @param serviceId Idntifier  of the service. Available Ids are 
	 * defined in this class itself.
	 * @return null if no service found for the service Id.
	 */
	public MonitoringService locateService(int serviceId){
		MonitoringService svc = null ;
		switch(serviceId){
		case GANGLIA:
			svc = new GmetricAdapter();
			break; 
		default : 
			break;
		}
		return svc ;
	}
	
	/**
	 * Returns the configured default service in the configuration files. 
	 * In this version it just returns GANGLIA. 
	 * @return
	 */
	public MonitoringService getDefaultService(){
		return locateService(defaultService);
	}

}
