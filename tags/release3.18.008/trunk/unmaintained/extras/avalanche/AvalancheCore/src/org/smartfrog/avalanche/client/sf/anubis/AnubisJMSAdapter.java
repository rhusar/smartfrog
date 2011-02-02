/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.avalanche.client.sf.anubis;

import org.smartfrog.services.xmpp.MonitoringEvent;
import org.smartfrog.services.anubis.locator.AnubisListener;
import org.smartfrog.services.anubis.locator.AnubisValue;

/**
 * extends AnubisListener, this should be registered on the server side
 * Smartfrog JVM. 
 * @author sanjaydahiya
 *
 */
public class AnubisJMSAdapter extends AnubisListener {
	
	private MessageSender sender = new MessageSender();

    public AnubisJMSAdapter(String name){
		super(name);
		try{
			sender.init();
		}catch(Exception e){
			log.error(e);
		}
	}

	public void newValue(AnubisValue val) {
		log.info("AnubisJMSAdapter.newValue() ..");
		MonitoringEvent e = (MonitoringEvent)val.getValue();
		// FIXME
	//	MonitoringEvent e = (MonitoringEvent)vald.getValue() ;
		e.setTimestamp(""+val.getTime());
        log.info("AnubisJMSAdapter.newValue() .." + e.toString());
		try{
			sender.sendMessage(e);
            log.info("AnubisJMSAdapter.newValue() .. message sent successfully to JMS server from Anubis");
		}catch(Exception ex){
			// FIXME : do something ..
            log.error(ex);
		}
	}

	public void removeValue(AnubisValue arg0) {

	}
}
