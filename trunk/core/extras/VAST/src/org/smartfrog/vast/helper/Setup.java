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
import java.lang.IllegalArgumentException;

/**
 * Helper class to setup everything required to run the TestRunner on the test node.
 */
public class Setup {
    /**
     * List of the names of the network interface cards.
     */
    ArrayList<String> listNICNames;

    /**
     * Helper class for the os native functions.
     */
    Helper helper;

    public Setup() {
        helper = HelperFactory.getHelper();
    }
    
    public static void main(String args[]) {
        Setup setup = new Setup();
        setup.start(args);
	}

    /**
     * Entry point for the setup.
     * @param args The command line arguments passed to the setup.
     */
    public void start(String args[]) {
        for (String str : args)
            System.out.println(str);

		// get the NIC names (and thus the count)
        listNICNames = helper.retrieveNICNames();

        if ((args.length % 2 != 0) || (listNICNames.size() < (args.length / 2)))
            throw new IllegalArgumentException("Wrong argument count.");

        // set the network cards to the given addresses
        for (int i = 0; i < args.length; i += 2) {
            helper.setNetworkAddress(listNICNames.get(i%2), args[i], args[i+1]);
        }
    }
}
