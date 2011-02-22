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

package org.smartfrog.services.dns;

import java.util.Vector;






/**
 * An interface for gloabl DNS options.
 *
 * 
 */
public interface DNSOptions extends DNSComponent {


    /**
     * Gets  a directory where all the config files are.
     *
     * @return  Directory where all the config files are.
     */
    public String getDirectory();


    /**
     * Gets A vector of IP addresses to forward queries that we cannot resolve
     *   locally.
     *
     * @return A vector of IP addresses to forward queries that we cannot
     * resolve  locally.
     */
    public Vector getForwarders();


    /**
     * Gets A vector of  interfaces to listen on for requests.
     *
     * @return A vector of  interfaces to listen on for requests.
     */
    public Vector getListenOn();


}
