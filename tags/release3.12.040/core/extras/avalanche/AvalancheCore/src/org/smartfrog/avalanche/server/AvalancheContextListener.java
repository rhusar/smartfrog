/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Aug 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.smartfrog.avalanche.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Context listener for Servlet container, this is executed when server starts in the beginning. 
 * Sets up connection with XMPP server and register for events. 
 */
public class AvalancheContextListener implements ServletContextListener {
    private static final Log log = LogFactory.getLog(AvalancheContextListener.class);
    private ServerSetup setup = new ServerSetup();
    private Scheduler scheduler = null;

    /**
	 * Needs following attributes in the web.xml for webapp
	 * 	xmppServer (default is localhost) 
	 * 	xmppServerPort (default is 5223)
	 * 	useSSLForXMPP ( default is true )
	 * 	xmppServerAdminUsername (mandatory)
	 * 	xmppServerAdminPassword (mandatory)
	 * 	avalancheHome (AVALANCHE_HOME on server)
	 */
	public void contextInitialized(ServletContextEvent evt) {
		// set up Avalanche XMPP adapters, this assumes XMPP server is already up 
		// running 
		try{
			String securityOn = evt.getServletContext().getInitParameter("securityOn");
			setup.getFactory().setAttribute(setup.getFactory().SECURITY_ON, securityOn);
            String xmppServer = evt.getServletContext().getInitParameter("xmppServer");
            if (null != xmppServer) {
                setup.setXmppServer(xmppServer);
                setup.getFactory().setAttribute(setup.getFactory().XMPP_SERVER_NAME, xmppServer);
            }
            String xmppServerPort = evt.getServletContext().getInitParameter("xmppServerPort");
            if (null != xmppServerPort) {
                setup.setXmppServerPort(Integer.parseInt(xmppServerPort));
            }
            String useSSL = evt.getServletContext().getInitParameter("useSSLForXMPP");
            if (null != useSSL) {
                setup.setUseSSLForXMPP(Boolean.parseBoolean(useSSL));
            }
            
			String xmppAdmin = evt.getServletContext().getInitParameter("xmppServerAdminUsername");
			String xmppAdminPass = evt.getServletContext().getInitParameter("xmppServerAdminPassword");
			setup.setXmppServerAdminUser(xmppAdmin);
			setup.setXmppServerAdminPassword(xmppAdminPass);
				
			String avalancheHome = evt.getServletContext().getInitParameter("avalancheHome");
			setup.setAvalancheHome(avalancheHome);
			
		//	String avalancheServerOS = evt.getServletContext().getInitParameter("avalancheServerOS");
		//	setup.setAvalancheServerOS(avalancheServerOS);
			
			String avalancheServerName = evt.getServletContext().getInitParameter("avalancheServerName");
			setup.getFactory().setAttribute(setup.getFactory().AVALANCHE_SERVER_NAME, avalancheServerName);
	        
			//Start Quartz Scheduler
			log.info("Initializing");
	        	SchedulerFactory schedFact = new StdSchedulerFactory();
			scheduler = schedFact.getScheduler();
			log.info("Initialization Complete");
			scheduler.start();
			log.info("Started Scheduler");
		//	setup.setScheduler(scheduler);
			//Scheduler scheduler1 = setup.getScheduler();
			//log.info("Scheduler===" + scheduler1.getSchedulerName());
			setup.startup();
			evt.getServletContext().setAttribute("avalancheFactory", setup.getFactory()) ;
			evt.getServletContext().setAttribute("scheduler", scheduler) ;
			
		}catch(Exception e){
			log.fatal("Avalanche InitializAtion failed : ", e);
		}
	}

	/**
	 * This is executed when server is shuttig down. Current implementation
	 * closes all database handles and stops Smartfrog on server. 
	 */
    public void contextDestroyed(ServletContextEvent evt) {
        try {
            if (scheduler != null) {
                log.info("Shutting Down");
                scheduler.shutdown(true);
                log.info("Shutdown Complete");
                scheduler = null;
            }
            setup.shutdown();
        } catch (Exception e) {
            log.fatal("Avalanche InitializAtion failed : ", e);
        }
	}

}
