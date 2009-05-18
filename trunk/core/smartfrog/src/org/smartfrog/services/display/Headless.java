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
package org.smartfrog.services.display;

import org.smartfrog.sfcore.workflow.conditional.Condition;

import java.rmi.RemoteException;


/**
 * A condition that can be used to evaluate or set the headless state of a machine
 */


public interface Headless extends Condition {
    /**
     * headless attribute: this is true when the system is headless.
     */

    String ATTR_HEADLESS = "headless";

    /**
     * Set the headless flag on the remote machine. Setting the headless attribute
     * has the same effect
     * @param headless should the machine be set to headless or not?
     * @throws RemoteException network problems
     */
    void setHeadless(boolean headless) throws RemoteException;
}
