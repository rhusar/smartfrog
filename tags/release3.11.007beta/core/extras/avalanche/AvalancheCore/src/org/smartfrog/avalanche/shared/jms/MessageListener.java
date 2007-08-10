/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.shared.jms;

import org.smartfrog.avalanche.shared.MonitoringConstants;
import org.smartfrog.avalanche.shared.MonitoringEvent;
import org.smartfrog.avalanche.shared.MonitoringEventDefaultImpl;

import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;

public class MessageListener {
	
	QueueConnection qc = null; 
	Queue queue = null ;
	private String serverName = "localhost" ;
	private int port = 8001 ;
	private String queueName = MonitoringConstants.DEPLOY_JMS_QUEUE ;
	String userName = "system" ;
	String password = "system" ;
	int jmsTimeout = 1000 ;
	
	/**
	 * default is localhost
	 * @param name
	 */
	public void setJMSServer(String name){
		this.serverName = name; 
	}
	
	public String getServerName(){
		return serverName ;
	}
	
	/**
	 * default is 8001
	 * @param p
	 */
	public void setJMSServerPort(int p){
		this.port = p ; 
	}
	
	public int getJMSServerPort(){
		return port ;
	}
	
	/**
	 * default is avalanceDeployQueue
	 * @param name
	 */
	public void setJMSQueueName(String name){
		this.queueName = name ;
	}
	
	/**
	 * default is system
	 * @param name
	 */
	public void serJMSUserName(String name){
		this.userName = name ; 
	}
	
	public String getJMSUserName(){
		return this.userName ;
	}
	
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
	
	public MonitoringEvent receive() throws Exception{
		MonitoringEvent event = null;
		// TODO : no need to open a new session every time .. fix it 
        QueueSession qs = 
            qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE); 
        QueueReceiver qr = qs.createReceiver(queue); 
        // TODO : Fix message timeout
        System.out.println("MonitoringEvent.receive() : Checking for new message on Q");
        MapMessage mm = (MapMessage)qr.receive(jmsTimeout);
        
        if(mm != null) { 
        		System.out.println("Message received");
        		event = new MonitoringEventDefaultImpl();
        	    event.setHost(mm.getString(MonitoringEvent.HOST));
        	    event.setInstanceName(mm.getString(MonitoringEvent.INSTANCE_NAME)) ;
        	    event.setModuleId(mm.getString(MonitoringEvent.MODULEID));
        	    event.setModuleState(mm.getString(MonitoringEvent.MODULE_STATE));
        	    event.setMsg(mm.getString(MonitoringEvent.MODULE_STATE));
        	    event.setMessageType(mm.getInt(MonitoringEvent.MESSAGE_TYPE));
        	    System.out.println("MessageListener.receive() - " + event);
        	    
        }else{
        		System.out.println("No message found in queue");
        }
        
        // TODO : put it in try / finally
        qr.close();
        qs.close();
        return event ; 
	}
	
	public void close() throws Exception{
		qc.close();
	}

}
