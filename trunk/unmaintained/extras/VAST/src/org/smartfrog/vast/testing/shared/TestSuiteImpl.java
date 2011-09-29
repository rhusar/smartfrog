/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.vast.testing.shared;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.CompoundImpl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

public class TestSuiteImpl extends CompoundImpl implements TestSuite {
	private ArrayList<SUTTestSequence> TestSequences = new ArrayList<SUTTestSequence>();

	public TestSuiteImpl() throws RemoteException {

	}

	public ArrayList<SUTTestSequence> getTestSequences() throws RemoteException, SmartFrogException {
		return TestSequences;
	}

	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();

		try {
			// retrieve the attribute names
			Vector<String> seqs = (Vector<String>) sfResolve(ATTR_TEST_SEQUENCES, true);

			// resolve them
			for (String seqKey : seqs)
				TestSequences.add( (SUTTestSequence) sfResolve(seqKey, true) );

//			for (SUTTestSequence seq : TestSequences) {
//				// print actions
//				sfLog().info("Actions:");
//				for (SUTAction act : seq.getActions()) {
//					sfLog().info("Action:");
//					sfLog().info(String.format("Host: %s", act.getHost()));
//					sfLog().info(String.format("Name: %s", act.getName()));
//					sfLog().info(String.format("ScriptName: %s", act.getScriptName()));
//					sfLog().info(String.format("Wait: %d", act.getWait()));
//				}
//
//				// print result
//				sfLog().info("Result:");
//				sfLog().info(String.format("Name: %s", seq.getResult().getName()));
//				for (SUTAttribute attr : seq.getResult().getAttributes()) {
//					sfLog().info("Attribute:");
//					sfLog().info(String.format("Host: %s", attr.getHost()));
//					sfLog().info(String.format("Name: %s", attr.getName()));
//					sfLog().info(String.format("Value: %s", attr.getValue()));
//				}
//			}

		} catch (Exception e) {
			sfLog().error(e);
			throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(e);
		}
	}
}
