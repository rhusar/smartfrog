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
package org.smartfrog.services.logging.jcl.test.system;

import org.smartfrog.test.DeployingTestBase;

/**
 * Created 22-Jun-2010 12:35:56
 */

public class CommonsLogTest extends DeployingTestBase {
    public static final String PACKAGE = "/org/smartfrog/services/logging/jcl/test/system/" ;
    public CommonsLogTest(String name) {
        super(name);
    }

    public void NotestCreateCommonsLogWorks() throws Throwable {
        expectSuccessfulTestRunOrSkip(PACKAGE, "testCreateCommonsLogWorks.sf");
    }

    public void testCommonsLoggingBinding() throws Throwable {
        expectSuccessfulTestRunOrSkip(PACKAGE, "testCommonsLoggingBinding.sf");
    }

    public void testCommonsLoggingBindingWorks() throws Throwable {
        expectSuccessfulTestRunOrSkip(PACKAGE, "testCommonsLoggingBindingWorks.sf");
    } 
}
