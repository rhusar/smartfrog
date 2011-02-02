/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.persistence.storage;

import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;

/**
 * CDString is a utility with methods to encode component descriptions as
 * strings and reconstruct component descriptions from a string. These string
 * encoded component descriptions are much smaller when serialised then the
 * Original component would be and are much faster to serialise/deserialise.
 */
public class CDString {
    public static final String CD_PREFIX = "sfCD";

    public static final String EAGER = "E";

    public static final String LAZY = "L";

    public static final int IS_EAGER_CHAR = 4;

    public static final int PREFIX_LEN = 5;

    public static String toString(ComponentDescription cd) throws SmartFrogResolutionException {
        return CD_PREFIX + (cd.getEager() ? EAGER : LAZY) + cd.toString();
    }

    public static ComponentDescription fromString(String str) throws SmartFrogParseException, SmartFrogException {
        ComponentDescription cd;
        String eagerChar = str.substring(IS_EAGER_CHAR, IS_EAGER_CHAR + 1);
        Vector parsePhases = new Vector();
        parsePhases.add("raw");
        Phases phases = (new SFParser("sf")).sfParse(str.substring(PREFIX_LEN), null);
        phases = phases.sfResolvePhases(parsePhases);
        cd = phases.sfAsComponentDescription();
        cd.setEager(EAGER.equals(eagerChar));
        return cd;
    }

    public static boolean isCDString(Object obj) {
        if (obj instanceof String) {
            return ((String) obj).startsWith(CD_PREFIX);
        } else {
            return false;
        }
    }
}
