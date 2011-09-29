/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.archives;

import org.smartfrog.services.filesystem.FileUsingComponent;


/**
 * Created 08-Jun-2009 14:51:55
 */


public interface Unzip extends FileUsingComponent {
    
    String ATTR_DESTDIR = "destDir";
    
    String ATTR_FAIL_ON_EMPTY_ARCHIVE = "failOnEmptyArchive";
    
    //String ATTR_SCAN_FOR_UNICODE_EXTRA_FIELDS = "scanForUnicodeExtraFields";
    
    String ATTR_OVERWRITE = "overwrite";
    
    String ATTR_STRIP_ABSOLUTE_PATH_SPEC = "stripAbsolutePathSpec";
    
    String ATTR_BUFFERSIZE = "bufferSize";
}
