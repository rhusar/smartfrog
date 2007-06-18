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





/**
 * An interface for a DNS top level component.
 *
 * 
 */
public interface DNSData extends DNSComponent {


    /**
     * Returns the "default" view or if none has been specified
     * as "default" one at random.
     *
     * @return The "default" view or if none has been specified
     * as "default" one at random.
     */
    public DNSView getMainView();


   /**
     * Replaces a new view object as child of this top level component.
     * If it is not present it is just added. We gave it
     * an attribute equal to its name, and if it is a "default" view, we
     * also bound it with the DEFAULT_VIEW attribute.
     *
     * @param view  A view data object that replaces/adds.
     * @return The old view with the same name or null.
     */
    public DNSView replaceView(DNSView view);



    /**
     * Gets the global options component associated with this hierarchy.
     *
     * @return The global options component associated with this hierarchy.
     */
    public DNSOptions getOptions();


    /**
     * Gets the host name that will appear in the NS records as 
     *  master of the zone.
     *
     * @return The host name that will appear in the NS records as 
     *  master of the zone.
     *
     */
    public String getNameServerHostName();

}
