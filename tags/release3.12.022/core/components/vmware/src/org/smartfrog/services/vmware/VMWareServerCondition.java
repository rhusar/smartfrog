/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/

package org.smartfrog.services.vmware;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.workflow.conditional.Condition;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.io.IOException;
import java.io.File;

/**
 * Checks whether vmware server is installed on the machine.
 */
public class VMWareServerCondition extends PrimImpl implements Condition {

    /**
     * Constructor.
     * @throws RemoteException
     */
    public VMWareServerCondition() throws RemoteException {

    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws java.rmi.RemoteException for network problems
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            // get the windows directory
            String strWinDir = System.getenv("windir");
            sfLog().error("windir: " + strWinDir);
            
            // check for the existance of "vmx86.sys"
            File file = new File(strWinDir + File.separator + "system32" + File.separator + "drivers" + File.separator + "vmx86.sys");
            return file.exists();
        } else {
            // check if "/etc/init.d/vmware" exists
            File file = new File("/etc/init.d/vmware");
            return file.exists();
        }
    }
}
