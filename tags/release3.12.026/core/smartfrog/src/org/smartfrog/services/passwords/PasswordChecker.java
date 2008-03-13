/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.passwords;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This component is here mainly to test password providers, but you can also
 * use it to verify that a password meets min/max length values
 */
public class PasswordChecker extends PrimImpl implements Remote {

    /** component attribute {@value} */
    public static final String ATTR_PROVIDER = "passwordProvider";

    /** component attribute {@value} */
    public static final String ATTR_MIN_SIZE = "minSize";

    /** component attribute {@value} */
    public static final String ATTR_MAX_SIZE = "maxSize";

    /** component attribute {@value} */
    public static final String ATTR_VALUE = "password";

    /** component attribute {@value} */
    public static final String ATTR_EXPECTED = "expected";

    /** component attribute {@value} */
    public static final String ATTR_PUBLISH = "publish";

    /** component attribute {@value} */
    public static final String ATTR_PATTERN = "pattern";
    public static final String ERROR_TOO_SHORT = "Password is shorter than minimum size";
    public static final String ERROR_TOO_LONG = "Password is longer than maximum size";
    public static final String ERROR_WRONG_VALUE = "Password does not match expected value";
    public static final String ERROR_NO_MATCH = "Response body does not match regular expression ";
    public static final String ERROR_UNABLE_TO_COMPILE = "Unable to compile ";

    public PasswordChecker() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart()
        throws SmartFrogException, RemoteException {
        super.sfStart();

        PasswordProvider provider =
            (PasswordProvider) sfResolve(ATTR_PROVIDER, (Prim) null, true);
        int minSize = sfResolve(ATTR_MIN_SIZE, -1, false);
        int maxSize = sfResolve(ATTR_MAX_SIZE, -1, false);
        String expected = sfResolve(ATTR_EXPECTED, (String) null, false);
        String regexp = sfResolve(ATTR_PATTERN, "", true);

        String password = provider.getPassword();
        if (password == null) {
            throw new SmartFrogException("Null password returned");
        }

        if (sfResolve(ATTR_PUBLISH, false, false)) {
            sfReplaceAttribute(ATTR_VALUE, password);
        }
        if (password.length() < minSize) {
            throw new SmartFrogException(ERROR_TOO_SHORT);
        }
        if (maxSize > 0 && password.length() > maxSize) {
            throw new SmartFrogException(ERROR_TOO_LONG);
        }
        if (expected != null && !password.equals(expected)) {
            throw new SmartFrogException(
                    ERROR_WRONG_VALUE);
        }
        if(regexp.length()>0) {
            try {
                Pattern pattern = Pattern.compile(regexp);
                Matcher matcher = pattern.matcher(password);
                if (!matcher.matches()) {
                    throw new SmartFrogLivenessException(ERROR_NO_MATCH + regexp);
                }
            } catch (PatternSyntaxException e) {
                throw new SmartFrogDeploymentException(ERROR_UNABLE_TO_COMPILE + regexp, e);
            }
        }


        new ComponentHelper(this).sfSelfDetachAndOrTerminate(TerminationRecord.normal(sfCompleteName()));

    }
}
