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
package org.smartfrog.services.filesystem.csvfiles;

import org.smartfrog.services.filesystem.FileIntf;
import org.smartfrog.services.filesystem.TupleDataSource;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * 1. When deployed, the file is read
 * 2. You can get an individual line.

 */


public interface CSVFileRead extends FileIntf, TupleDataSource {

    /** {@value} */
    String ATTR_HEADER_LINES = "headerLines";
    /** {@value} */
    String ATTR_SEPARATOR = "separator";
    /** {@value} */
    String ATTR_QUOTE_CHAR = "quoteChar";

    /**
     * min number of lines {@value}
     */
    public static final String ATTR_MINCOUNT = "minCount";
    /**
     * max number of lines {@value}
     */
    public static final String ATTR_MAXCOUNT = "maxCount";

    /**
     * minimum width; -1 for do not check {@value}
     */
    public static final String ATTR_MINWIDTH = "minWidth";
    /**
     * max width; -1 for do not check {@value}
     */
    public static final String ATTR_MAXWIDTH = "maxWidth";

}
