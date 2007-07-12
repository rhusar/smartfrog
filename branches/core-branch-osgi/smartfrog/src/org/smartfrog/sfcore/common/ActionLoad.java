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
package org.smartfrog.sfcore.common;

/**
 * A variant of ActionDeploy that can be used to load but not start an
 * application. This is used by the testharness. 
 *  created 10-Jul-2007 15:48:18
 */

public class ActionLoad extends ActionDeploy {


    /**
     * Override point: get the start flag for this configuration.
     * This override always returns false.
     *
     * @param configuration the configuration which is being deployed
     * @return false.
     */
    protected boolean getStartFlag(ConfigurationDescriptor configuration) {
        return false;
    }

}
