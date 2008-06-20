/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.core.proposed;

import java.io.IOException;
import java.util.Iterator;

/**
 * A source of (key,value) pairs
 */


public interface ConfigurationSource extends Iterable<String>{

    /**
     * Get a key
     * @param key the key to look up
     * @return the value or null
     * @throws IOException for any failure to look up the key, other than it not being there.
     */
    String get(String key) throws IOException;

    /**
     * Create a new configuration source from this one.
     * @return a new source
     */
    ConfigurationSource copy();

    /**
     * Get an iterator over all the keys.
     * This iterator should be robust against changes in the source.
     * @return the iterator
     */
    Iterator<String> iterator();

}
