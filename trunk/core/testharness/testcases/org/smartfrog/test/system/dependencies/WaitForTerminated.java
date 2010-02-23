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
package org.smartfrog.test.system.dependencies;

import org.smartfrog.sfcore.prim.Prim;

/**
 * created 23-Feb-2010 11:03:35
 */

public class WaitForTerminated {
    static void wait(Class clazz, Prim parent, String child) throws Exception {
        Spinner s = new Spinner(clazz.getName(), 5000, 30000);
        Prim m = null;
        while (true) {
            try {
                m = (Prim) parent.sfResolve(child);
                m = (m.sfIsTerminating() || m.sfIsTerminated() ? null : m);
            } catch (Exception ignored) {
                m = null;
            }
            if (m == null) break; //from true...
            s.sleep();
        }
    }
}
