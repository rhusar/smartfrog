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

package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.languages.sf.functions.BaseOperator;
import org.smartfrog.sfcore.utils.PlatformHelper;

/**

 */
public class FilepathFunction extends BaseOperator {
    /**
     * merge two file paths with path conversion
     *
     * @param left parent
     * @param right child
     * @return result
     */
    protected Object doOperator(Object left, Object right) {

        PlatformHelper platform = PlatformHelper.getLocalPlatform();
        String directory;
        String childPath;
        directory = platform.convertFilename(left.toString());
        childPath = platform.convertFilename(right.toString());
        String slash = platform.getFileSeparator();
        //add a trailing slash to the dir
        if (!directory.endsWith(slash)) {
            directory += slash;
        }
        //and trim any leading slash off the child
        if (childPath.startsWith(slash)) {
            childPath = childPath.substring(1);

        }

        return directory+childPath;
    }
}

