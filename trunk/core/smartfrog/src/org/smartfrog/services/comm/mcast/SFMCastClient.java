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

package org.smartfrog.services.comm.mcast;

/**
 * MultiCast Client Interface. Defines names of all the attributes required by
 * this component.
 */
public interface SFMCastClient {
    /** String name for optional attribute "debug" */
    final static String ATR_DEBUG = "debug";

    /** String name for mandatory attribute "mcastAddress" */
    final static String ATR_MCASTADDRESS = "mcastAddress";

    /** String name for mandatory attribute "servers" */
    final static String ATR_SERVERS = "servers";

   /** String name for optional attribute "mcastPort" */
    final static String ATR_MCASTPORT = "mcastPort";


}
