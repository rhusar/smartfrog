/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.services.vmware;

public class VMWareCommunicator {


//      VMFox
//          to be used when VMFox is running correctly
//    /**
//     * The commandline-tool to be used.
//     */
//    private static final String VMWARE_CMD = "VMFox";
//
//     /**
//     * Executes the VMWARE_CMD with the given parameters and returns the output.
//     * @param strParameters
//     * @return
//     * @throws java.io.IOException
//     */
//    public String execVMcmd(String strParameters) throws IOException {
//        // execute the command
//        Process ps = Runtime.getRuntime().exec(VMWARE_CMD + " " + strParameters);
//
//        // read the output stream
//        byte[] iOutput = new byte[ps.getInputStream().available()];
//        ps.getInputStream().read(iOutput);
//
//        // convert the output
//        return new String(iOutput);
//    }


     /**
     * Executes the VMWARE_CMD with the given parameters and returns the output.
     * @param strParameters
     * @return
     * @throws java.io.IOException
     */
    public String execVMcmd(String strCommandLineTool, String strParameters) throws Exception {
        // execute the command
        Process ps = Runtime.getRuntime().exec(strCommandLineTool + " " + strParameters);
        ps.waitFor();

        // read the output stream
        byte[] iOutput = new byte[ps.getInputStream().available()];
        ps.getInputStream().read(iOutput);

        // convert the output
        return new String(iOutput);
    }
}
