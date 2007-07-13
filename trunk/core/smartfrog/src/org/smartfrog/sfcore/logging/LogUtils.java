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
package org.smartfrog.sfcore.logging;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

/** created 05-Mar-2007 12:15:51 */

public class LogUtils {
    /**
     * Get an exception from either the cause parameter, or, if that is null, from anything in
     * the termination record
     * @param cause exception to use first
     * @param tr the termination record that may (if not null) return an exception
     * @return a throwable or null
     */
    public static Throwable extractCause(SmartFrogException cause, TerminationRecord tr) {
        Throwable throwable = cause;
        if(cause==null && tr!=null && tr.getCause()!=null) {
            throwable=tr.getCause();
        }
        return throwable;
    }

    /**
     * Append a termination record to a log message.
     * @param tr termination record --can be null.
     * @return a string including the preceeding newline
     */
    public static String stringify(TerminationRecord tr) {
        return "\n" + tr != null ? tr.toString() : "";
    }
}
