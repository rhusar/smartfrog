/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.test;

import org.smartfrog.sfcore.common.OptionSet;

/**
 * Class to run smartfrog in a local JVM.
 * Once the daemon is started, it stays started, as the engine does not like to unload.
 * @author steve loughran
 */
public class LocalJVMTestBase extends SmartFrogTestBase {

    public LocalJVMTestBase(String name) {
        super(name);
    }


    /**
     * set the daemon options;
     * @param daemonOptions options to set
     */
    public static void setDaemonOptions(OptionSet daemonOptions) {
        LocalJVMTestBase.daemonOptions = daemonOptions;
    }

    /**
     * set the daemon options from a string array
     * @param args
     */
    public void setDaemonOptions(String[] args) {
        OptionSet opts=new OptionSet(args);
        setDaemonOptions(opts);
    }

    protected static OptionSet daemonOptions;


    /**
     * start a local daemon if the options request it
     */
    protected void setUp() throws Exception {
       TestHelper.demandStartDaemon(daemonOptions);

    }



}
