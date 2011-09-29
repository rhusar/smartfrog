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

package org.smartfrog.vast.helper;

import java.util.ArrayList;
import java.io.*;

/**
 * Helper class to setup everything required to run the TestRunner on the test node.
 */
public class Setup {
    /**
     * List of the names of the network interface cards.
     */
    private ArrayList<String> listNICNames;

    /**
     * Helper class for the os native functions.
     */
    private Helper helper;

	/**
	 * Output file.
	 */
	private PrintStream out;

	public Setup() {
		try {
			out = new PrintStream(new FileOutputStream("helper.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		helper = HelperFactory.getHelper(out);
	}
    
    public static void main(String args[]) {
        Setup setup = new Setup();
        setup.start(args);
	}

	private void printUsage() {
		out.println("----- Usage: -----");
		out.println("helper.jar [option]");
		out.println();
		out.println("----- Options: -----");
		out.println("-nic [ip] [mask] (opt)gw [gw ip]\tSets an NIC to the specified IP using the given subnet mask.\n\t\t\t\tOptionally sets this NIC as default gw for the given gw address.\n\t\t\t\tMight occur more than once. The NIC which will be used is determined by the occurance: \n\t\t\t\tfirst discovered NIC will be used for the first specified -nic option.");
		out.println("-hname [hostname]\t\tSet the hostname for this machine.\n");
		out.println("-dns [hostname] [ip]\t\tAdd a DNS entry.");
		out.println("-help \t\t\t\tTo display this help.");
	}

	/**
     * Entry point for the setup.
     * @param args The command line arguments passed to the setup.
     */
    public void start(String args[]) {
		// get the NIC names (and thus the count)
        listNICNames = helper.retrieveNICNames();

		int iNICIndex = 0;
		for (int i = 0; i < args.length; ++i) {
			try {
				if (args[i].equals("-nic")) {
					if (iNICIndex < listNICNames.size()) {
						helper.setNetworkAddress(listNICNames.get(iNICIndex), args[i+1], args[i+2]);
						++iNICIndex;
					}
					else System.err.println("No more NIC available");

					i+=2;
				}
				else if (args[i].equals("gw")) {
					helper.setDefaultGateway(args[i+1], listNICNames.get(iNICIndex - 1));
					i+=1;
				}
				else if(args[i].equals("-hname")) {
					helper.setHostname(args[i+1]);
					i+=1;
				}
				else if(args[i].equals("-dns")) {
					helper.addDNSEntry(args[i+1], args[i+2]);
					i+=2;
				}
				else if (args[i].equals("-help")) {
					printUsage();
					return;
				}
			} catch (IndexOutOfBoundsException e) {
				out.println("wrong parameters");
				printUsage();
				return;
			}
		}
    }
}
