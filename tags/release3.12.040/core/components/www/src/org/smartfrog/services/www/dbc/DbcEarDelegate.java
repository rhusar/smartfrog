/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www.dbc;

import org.smartfrog.services.www.JavaEnterpriseApplication;
import org.smartfrog.sfcore.prim.Prim;

/**
 * This delegate will queue copy operations when deploying EAR files.
 * created 19-Jun-2006 16:57:38
 */

public class DbcEarDelegate extends AbstractDbcDelegate implements JavaEnterpriseApplication {

    public DbcEarDelegate(DeployByCopyServerImpl server, Prim owner) {
        super(server, owner);
    }

    /**
     * Return the extension
     * @return ".ear"
     */
    public String getExtension() {
        return ".ear";
    }
}
