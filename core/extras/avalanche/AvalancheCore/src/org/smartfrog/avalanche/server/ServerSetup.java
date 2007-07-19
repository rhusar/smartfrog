/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server;

import org.jivesoftware.smack.XMPPException;
import org.smartfrog.avalanche.server.monitor.handlers.ActiveProfileUpdateHandler;
import org.smartfrog.avalanche.server.monitor.handlers.HostUpdateHandler;
import org.smartfrog.avalanche.server.monitor.handlers.HostUpdateRosterHandler;
import org.smartfrog.avalanche.shared.xmpp.XMPPAdapter;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;

/**
 * Wrapper class for Avalanche server initialization and shutdown. This can be used independently 
 * to embed Avalanche server in a program. @see startup() starts Avalanche server. 
 * Following example starts Avalanche server, adds a new Host in the Server, 
 * waits for a key stroke and then deletes host and shuts down the server.  
 * <pre>
 * <code>
 * 		ServerSetup setup = new ServerSetup();
		setup.setAvalancheHome("/tmp/avalancheTest");
		setup.setXmppServer("192.168.1.101");
		setup.setXmppServerAdminUser("admin");
		setup.setXmppServerAdminPassword("admin");
		setup.setXmppServerPort(5223);
		
		setup.startup();
		System.out.println("Avalanche Server is started properly ... ");
		
		// add a host now and see if the user for the host is created properly
		AvalancheFactory factory = setup.getFactory();
		HostManager hm = factory.getHostManager();
		try{
			HostType host = hm.newHost("192.168.1.102");
		}catch(Exception e){
			System.out.println("Error !! Host already exists");
		}
		
		System.in.read();
		try{
			hm.removeHost("192.168.1.102");
		}catch(Exception e){
			System.out.println("Error !! Failed deleting host");
		}
		setup.shutdown();
	}

 * </code>
 * </pre>
 * @author sanjaydahiya
 *
 */
public class ServerSetup {
    private String xmppServer = "localhost";
	private int xmppServerPort = 0;

    private String xmppServerAdminUser ;
	private String xmppServerAdminPassword ; 
	private String useSSLForXMPP ;

    private String avalancheHome ;
	
	private String eventListenerUser = "avl" ;
	private String eventListenerPwd = "xlistener" ;

	private static Log log = LogFactory.getLog(ServerSetup.class);

    // Create XMPP Adapters with SSL support
    static XMPPAdapter adminAdapter = null;
	static XMPPAdapter listenerAdapter = null;
	
    AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);

	public String getAvalancheHome() {
		return avalancheHome;
	}


	public void setAvalancheHome(String avalancheHome) {
		this.avalancheHome = avalancheHome;
	}


	public AvalancheFactory getFactory() {
		return factory;
	}


	public String getUseSSLForXMPP() {
		return useSSLForXMPP;
	}


	public void setUseSSLForXMPP(String useSSLForXMPP) {
		this.useSSLForXMPP = useSSLForXMPP;
	}


	public String getXmppServer() {
		return xmppServer;
	}


	public void setXmppServer(String xmppServer) {
		this.xmppServer = xmppServer;
	}

	public String getXmppServerAdminPassword() {
		return xmppServerAdminPassword;
	}


	public void setXmppServerAdminPassword(String xmppServerAdminPassword) {
		this.xmppServerAdminPassword = xmppServerAdminPassword;
	}


	public String getXmppServerAdminUser() {
		return xmppServerAdminUser;
	}


	public void setXmppServerAdminUser(String xmppServerAdminUser) {
		this.xmppServerAdminUser = xmppServerAdminUser;
	}


	public int getXmppServerPort() {
		return xmppServerPort;
	}


	public void setXmppServerPort(int xmppServerPort) {
		this.xmppServerPort = xmppServerPort;
	}

	/**
	 * Starts up Avalanche server. Avalanche server must be installed and @see setAvalancheHome(String)
	 * should be set properly before calling this method. 
	 * @throws Exception
	 */
    public void startup() throws Exception {
        log.info("Initializing Avalanche ...");
        log.info("Using Avalanche Home : " + avalancheHome);

        //factory.init(avalancheHome, avalancheServerOS);
        factory.init(avalancheHome);

        // set up Avalanche XMPP adapters, this assumes XMPP server is already up
        // running
        try {
            // Creating Adapter to the server specified
            adminAdapter = new XMPPAdapter(xmppServer, true);

            // Setting the server port
            if (xmppServerPort != 0)
                adminAdapter.setXmppServerPort(xmppServerPort);

            // Setting SSL mode
            if (null != useSSLForXMPP)
                adminAdapter.setUseSSL((new Boolean(useSSLForXMPP)).booleanValue());

            // Setting username and password
            adminAdapter.setXmppUserName(xmppServerAdminUser);
            adminAdapter.setXmppPassword(xmppServerAdminPassword);

            // Initialize the connection
            adminAdapter.init();
            try {
                // Log in to the XMPP Server
                adminAdapter.login();
                // Create the listening user
                adminAdapter.createUser(eventListenerUser, eventListenerPwd, "admin");
            } catch (XMPPException e) {
                log.error("Error while creating listening user. Exception: " + e);
            }

            // create a new connection for listener
            listenerAdapter = new XMPPAdapter(xmppServer, true);

            // Setting the server port
            if (xmppServerPort != 0)
                listenerAdapter.setXmppServerPort(xmppServerPort);

            // Setting SSL mode
            if (null != useSSLForXMPP)
                listenerAdapter.setUseSSL((new Boolean(useSSLForXMPP)).booleanValue());

            // Setting username and password
            listenerAdapter.setXmppUserName(eventListenerUser);
            listenerAdapter.setXmppPassword(eventListenerPwd);

            // Initialize the connection
            listenerAdapter.init();
            try {
                // Log in to the XMPP server as listening user
                listenerAdapter.login();
            } catch (XMPPException e) {
                // Log error
                log.error("Error while logging onto XMPP server. Exception: " + e);
            }

            // handler chain for events coming from client nodes.
            listenerAdapter.addHandler(new ActiveProfileUpdateHandler(this));
            // register inbuilt event listeners
            listenerAdapter.registerListeners();

            // register Roster Handlers with the HostManager
            // this causes Avalanche to register/un register for events
            // of the hosts.
            HostUpdateHandler hostHandler = new HostUpdateRosterHandler(adminAdapter, listenerAdapter);

            HostManager hostManager = factory.getHostManager();
            hostManager.addHandler(hostHandler);

            // TODO : Start Smartfrog on server if its not already running using avalancheHome

        } catch (XMPPException e) {
            log.fatal("Avalanche InitializAtion failed : ", e);
        }
    }
	
	public XMPPAdapter getAdminAdapter(){
		return adminAdapter ; 
	}
	
	public XMPPAdapter getListenerAdapter(){
		return listenerAdapter ; 
	}
	
	/**
	 * Shuts down Avalanche server. 
	 * @throws Exception
	 */
	public void shutdown() throws Exception{
		System.out.println("Shutting down Avalanche ...");
		log.info("Shutting down Avalanche ...");
		if( null != factory){
			factory.close();
			factory = null ;
		}else{
			log.error("AvalancheContextListener:contextDestroyed() - Shutting down, Avalanche was not initialized completely");
			System.out.println("AvalancheContextListener:contextDestroyed() - Shutting down, Avalanche was not initialized completely");
		}
		
/*		try{
			if( SFAdapter.isActive("localhost")){
				SFAdapter.stopDaemon("localhost");
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
		*/	
	}

}
