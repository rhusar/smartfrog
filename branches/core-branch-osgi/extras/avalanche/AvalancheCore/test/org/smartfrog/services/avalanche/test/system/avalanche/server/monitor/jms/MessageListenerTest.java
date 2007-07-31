/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package tests.org.smartfrog.avalanche.server.monitor.jms;

import junit.framework.TestCase;
import org.smartfrog.avalanche.server.monitor.jms.ListenerThread;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MessageListenerTest extends TestCase {

	/*
	 * Test method for 'org.smartfrog.avalanche.shared.jms.MessageListener.receive()'
	 */
	public void testReceive()  throws Exception{
		
		ListenerThread lt = new ListenerThread();
		Thread  thread = new Thread(lt);
		thread.start();
		
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		in.readLine();
		
		System.out.println("Waiting for thread to stop ... ");
		lt.tryStop();
		thread.join();
		System.out.println("Terminating ... ");
		
	}
}
