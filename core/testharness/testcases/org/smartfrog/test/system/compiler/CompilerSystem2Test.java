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


package org.smartfrog.test.system.compiler;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.OptionSet;
import org.smartfrog.test.LocalJVMTestBase;



/**
 * test the compiler with test TCN5
 */
public class CompilerSystem2Test extends LocalJVMTestBase {

    public CompilerSystem2Test(String s) {
        super(s);
        setDaemonOptions(new String[] { "" } );
    }


    public void testCaseTCN5() throws Exception {
        //construct the string array argument
            String[] args = {"-h", "localhost", "-n", "ex1",
                     getRelativeFile("org/smartfrog/test/system/compiler/tcn5.sf").toString(),
                    "-e"};
            // Invoke SFSystem to deploy the test

            OptionSet options=new OptionSet(args);

            SFSystem.runSmartFrog(options);
    }
}
