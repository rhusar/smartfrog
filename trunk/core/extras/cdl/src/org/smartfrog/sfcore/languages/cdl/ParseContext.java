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
package org.smartfrog.sfcore.languages.cdl;

import java.util.HashMap;
import java.util.Properties;

/**
 * This class represents the context of the execution; the
 * created 08-Jun-2005 13:21:27
 */

public class ParseContext {

    HashMap imports=new HashMap();

    /**
     * option lookup for remote deployment
     */
    HashMap options=new HashMap();

    /**
     * properties are any extra properties set at deploy time
     */

    Properties properties;

    public HashMap getImports() {
        return imports;
    }

    public HashMap getOptions() {
        return options;
    }

    public Properties getProperties() {
        return properties;
    }
}
