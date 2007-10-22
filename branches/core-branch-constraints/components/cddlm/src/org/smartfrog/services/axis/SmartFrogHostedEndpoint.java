/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.axis;



/**
 * Date: 10-Mar-2004
 * Time: 20:07:32
 */
public class SmartFrogHostedEndpoint {
    /**
     * name of whoever created this endpoint
     */
    private static AxisImpl owner;

    /**
     * set the owner
     * we do this as there is a need for instances to be bound.
     * Maybe Axis should have a Factory option so that you can
     * dynamically create stuff pre-configured.
     * @param owner
     */

    static public void setOwner(AxisImpl owner) {
        SmartFrogHostedEndpoint.owner=owner;
    }


    /**
     * get the owner
     * @return
     */
    public static AxisImpl getOwner() {
        return owner;
    }
}
