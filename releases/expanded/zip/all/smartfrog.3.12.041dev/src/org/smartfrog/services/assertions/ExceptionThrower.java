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
package org.smartfrog.services.assertions;

import java.rmi.Remote;

/**
 * created 26-Sep-2006 11:51:41
 */

public interface ExceptionThrower extends Remote {
    public String ATTR_CLASSNAME="classname";

    public String ATTR_MESSAGE="message";

    /** {@value} */
    public String ATTR_THROW_ON_STARTUP = "throwOnStartup";
    /** {@value} */
    public String ATTR_THROW_ON_DEPLOY = "throwOnDeploy";
    /** {@value} */
    public String ATTR_THROW_ON_PING = "throwOnPing";

}
