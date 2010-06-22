/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.logging.jcl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;

/**
 * Created 22-Jun-2010 12:19:55
 */

public class CreateCommonsLogImpl extends PrimImpl {
    public static final String ATTR_LOG_NAME = "logName";
    public static final String ATTR_STARTED_MESSAGE = "started";
    public static final String ATTR_TERMINATED_MESSAGE = "terminated";
    public static final String ATTR_MESSAGES = "messages";
    public static final String ATTR_EXPECTED_CLASSNAME = "expectedClassname";
    private Log log;
    private String terminated="";

    public CreateCommonsLogImpl() throws RemoteException {
    }

    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        String logName = sfResolve(ATTR_LOG_NAME,"", true);
        String started = sfResolve(ATTR_STARTED_MESSAGE, "", true);
        terminated = sfResolve(ATTR_TERMINATED_MESSAGE, "", true);
        log = LogFactory.getLog(logName);
        if(!started.isEmpty()) {
            log.info(started);
        }
        String expectedClassname = sfResolve(ATTR_EXPECTED_CLASSNAME, "", true);
        String actualClassname =log.getClass().getName();
        if (!expectedClassname.isEmpty()) {
            if (!expectedClassname.equals(actualClassname)) {
                throw new SmartFrogDeploymentException("Expected logging classname to be \"" 
                        + expectedClassname + "\""
                        + "but got \""+actualClassname+"\"");
            }
        }
        ComponentHelper ch=new ComponentHelper(this);
        ch.sfSelfDetachAndOrTerminate(TerminationRecord.NORMAL,
                "Succesfully deployed a commons log bound to "+ actualClassname,sfCompleteName(), null);
    }

    @Override
    protected void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (!terminated.isEmpty()) {
            log.info(terminated);
        }

    }
}
