/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.logging.LogSF;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;
import java.rmi.RemoteException;

/**
 * This is a helper class to check regular expressions in text/html content, and raise an error
 * on no match. Also turns a compilation problem into a SmartFrogDeploymentException
 *
 */

public class RegexpCheck {
    private String responseRegexp;
    private Pattern responsePattern;
    /**
     * our log
     */
    protected LogSF log;


    public static final String ERROR_NO_MATCH = "Response body does not match regular expression ";
    public static final String ERROR_UNABLE_TO_COMPILE = "Unable to compile ";
    public static final String FAILED_TO_REPLACE_ATTRIBUTE = "failed to replace attribute ";


    /**
     * Create one instance
     * @param responseRegexp regular expression to parse
     * @param log log for output
     */
    public RegexpCheck(String responseRegexp, LogSF log) {
        this.responseRegexp = responseRegexp;
        this.log = log;
    }

    public String getResponseRegexp() {
        return responseRegexp;
    }

    /**
     * Set the response regular expression
     *
     * @param responseRegexp the regular expression
     * @throws SmartFrogDeploymentException if the syntax would not compile
     */
    public void setResponseRegexp(String responseRegexp) throws SmartFrogDeploymentException {
        this.responseRegexp = responseRegexp;
        if (responseRegexp != null && responseRegexp.length() > 0) {
            try {
                responsePattern = Pattern.compile(responseRegexp);
            } catch (PatternSyntaxException e) {
                throw new SmartFrogDeploymentException(ERROR_UNABLE_TO_COMPILE + responseRegexp, e);
            }
        } else {
            responsePattern = null;
        }
    }


    /**
     * Checks the regular expression and adds groups as attributes
     * @param target option owner for attribute values
     * @param body         body of the response
     * @throws SmartFrogLivenessException if need be
     */
    public void validate(Prim target, String body)
            throws SmartFrogLivenessException {
        if (responsePattern != null) {
            Matcher matcher = responsePattern.matcher(body);
            if (!matcher.matches()) {
                throw new SmartFrogLivenessException(ERROR_NO_MATCH + responseRegexp
                        + '\n' + body);
            }
            try {
                if (target != null) {
                    //we use <= because there is always, implicitly, a group 0
                    for (int i = 0; i <= matcher.groupCount(); i++) {
                        String group = matcher.group(i);
                        log.info("Matched response group" + i + ": " + group);
                        target.sfReplaceAttribute("group" + i, group);
                    }
                }
            } catch (SmartFrogRuntimeException e) {
                log.ignore(FAILED_TO_REPLACE_ATTRIBUTE, e);
            } catch (RemoteException e) {
                log.ignore(FAILED_TO_REPLACE_ATTRIBUTE, e);
            }
        }
    }
}
