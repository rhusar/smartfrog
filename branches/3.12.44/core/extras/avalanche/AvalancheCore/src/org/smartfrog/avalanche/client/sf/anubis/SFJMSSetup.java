/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.client.sf.anubis;

import org.mom4j.api.Mom4jConfig;
import org.mom4j.api.Mom4jConsole;
import org.mom4j.api.Mom4jFactory;
import org.mom4j.config.ConfigImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.rmi.RemoteException;

public class SFJMSSetup extends PrimImpl implements Prim {

	Mom4jConsole server = null ;	 
	private String jmsConfigFile = null ;
	
	public SFJMSSetup() throws RemoteException{
		super();
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		
		jmsConfigFile = (String)sfResolve("jmsConfigFile") ;
		
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		
		try{
			Mom4jConfig cfg = 
				new ConfigImpl(jmsConfigFile);
			server =	 Mom4jFactory.start(cfg, true);
		}catch(IOException e){
			sfLog().fatal("Error ! JMS config file " + "" + " not found", e);
			throw new SmartFrogException("Error ! JMS startup failed", e);
		}catch(SAXException se){
			sfLog().fatal("Error ! JMS config file " + "" + " is not valid", se);
			throw new SmartFrogException("Error ! JMS startup failed", se);
		}catch(ParserConfigurationException pe){
			sfLog().fatal("Error ! JMS config file " + "" + " is not valid", pe);
			throw new SmartFrogException("Error ! JMS startup failed", pe);
		}
        sfLog().info("server started ...") ;
		if( null == server ){
			// TODO : fail with error
			throw new SmartFrogException("Error ! JMS startup failed");
		}
	}

	public synchronized void sfTerminateWith(TerminationRecord arg0) {
		super.sfTerminateWith(arg0);
		if( null != server){
			server.stop();
			sfLog().info("JMS server stopped ...");
		}
	}

}
