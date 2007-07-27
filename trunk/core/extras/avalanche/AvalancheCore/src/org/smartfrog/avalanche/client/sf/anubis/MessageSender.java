/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.client.sf.anubis;

import org.smartfrog.avalanche.shared.MonitoringConstants;
import org.smartfrog.avalanche.shared.MonitoringEvent;

import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;

public class MessageSender {
	QueueConnection qc = null; 
	Queue queue = null ;
	private String serverName = "localhost" ;
	private int port = 8001 ;
	private String queueName = MonitoringConstants.DEPLOY_JMS_QUEUE ;
	String userName = "system" ;
	String password = "system" ;
	
	
	public void init() throws Exception{
        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY, 
             "org.mom4j.jndi.InitialCtxFactory"); 
        
        p.put(Context.PROVIDER_URL, "xcp://" + serverName + ":" + port); 
        
        Context ctx = new InitialContext(p); 
        QueueConnectionFactory qcf = 
            (QueueConnectionFactory) 
                ctx.lookup("QueueConnectionFactory"); 
        queue = (Queue)ctx.lookup(queueName); 
        
        qc = qcf.createQueueConnection(userName, password); 
	}
	
	public void sendMessage(MonitoringEvent event) throws Exception{
		QueueSession qs = 
			qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE); 
		QueueSender qsend = qs.createSender(queue);
		MapMessage mm = qs.createMapMessage();
		
		mm.setString(MonitoringEvent.HOST,event.getHost() );
		mm.setString(MonitoringEvent.INSTANCE_NAME, event.getInstanceName()) ;
		mm.setInt(MonitoringEvent.MESSAGE_TYPE, event.getMessageType());
		mm.setString(MonitoringEvent.MODULEID, event.getModuleId());
		mm.setString(MonitoringEvent.MODULE_STATE, event.getModuleState());
		mm.setString(MonitoringEvent.MESSAGE, event.getMsg());
		
		System.out.println("MessageSender.sendMessage() - Sending event to JMS Q") ;
		qsend.send(mm);
		System.out.println("MessageSender.sendMessage() - Message sent successfully to JMS.") ;
		
		qsend.close();
		qs.close();
	}
	
	public void close() throws Exception{
		qc.close();
		System.out.println("Sender closed");
	}
}
