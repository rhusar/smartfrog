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


package org.smartfrog.services.junit.listeners;

import org.smartfrog.services.junit.TestListenerFactory;

/**
 * Date: 12-Jun-2004 Time: 00:16:06
 */

public interface XmlListenerFactory extends TestListenerFactory {

    /**
     * name of a directory for output
     */
    String OUTPUT_DIRECTORY = "outputDirectory";

    /**
     * flag to include hostname logic in file/dir choice
     */
    String USE_HOSTNAME = "useHostname";

    //String OUTPUT_FILE="outputFile";

    String PREAMBLE = "preamble";

    /**
     * get the filename of this
     *
     * @return
     */
    String getFilename();


}
