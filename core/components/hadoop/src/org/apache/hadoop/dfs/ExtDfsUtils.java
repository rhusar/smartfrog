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
package org.apache.hadoop.dfs;

import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.io.File;
import java.util.Collection;

/**
 * Utility mehtods that need to be in the right package
 *
 */

public class ExtDfsUtils {

    private ExtDfsUtils() {
    }

    /**
     * Format a name node
     * @param dirsToFormat directories to format
     * @param conf the configuration
     * @throws IOException
     */
    public static void formatNameNode(Collection<File> dirsToFormat, Configuration conf) throws IOException {
        FSNamesystem nsys = new FSNamesystem(new FSImage(dirsToFormat), conf);
        nsys.dir.fsImage.format();
    }
}
