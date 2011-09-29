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
package org.smartfrog.services.rpm.manager;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.Serializable;

/**
 *
 * info about a file that the RPM tool manages
 *
 */

public class RpmManagedFile implements Serializable {

    private String filename;
    private RpmFile rpm;

    /**
     * For the serialization code
     */
    private RpmManagedFile() {
    }

    /**
     * Create a new file
     * @param filename file to manage
     * @param rpm owner
     */
    public RpmManagedFile(String filename, RpmFile rpm) {
        this.filename = filename;
        this.rpm = rpm;
    }

    public String getFilename() {
        return filename;
    }

    public RpmFile getRpm() {
        return rpm;
    }

    /**
     * Validate the RPM file itself, by checking it is there and not a directory.
     *
     * @throws FileNotFoundException if the file is not there.
     */
    public void validateRpmFile() throws FileNotFoundException {
        File file = toFile();
        if (!file.exists()) {
            throw new FileNotFoundException(RpmErrors.ERROR_NO_SUCH_FILE + file + " from " + rpm);
        }

        if (!file.isFile() && !file.isDirectory()) {
            throw new FileNotFoundException(RpmErrors.ERROR_NOT_AN_RPM_FILE + file + " from " + rpm);
        }
    }

    /**
     * Convert to a file in the local filesystem syntax
     *
     * @return a file representing the rpm file
     */
    public File toFile() {
        return new File(filename);
    }

}
