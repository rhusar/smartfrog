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
package org.smartfrog.services.rpm.dirload;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * Created 08-Dec-2008 16:49:38
 */

public class DeployedDir implements Comparable {

    private File path;
    private File applicationFile;

    private Prim application;

    private String name;

    private Status status = Status.NEW;

    public DeployedDir(File path, String name, String applicationFilename) {
        this.path = path;
        this.name = name;
        applicationFile = new File(path, applicationFilename);
    }

    public File getPath() {
        return path;
    }

    public File getApplicationFile() {
        return applicationFile;
    }


    public void setApplication(Prim application) {
        this.application = application;
    }

    public Prim getApplication() {
        return application;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * test for the application file being valid
     *
     * @return true if the application file exists and is a simple file
     */
    public boolean isApplicationFileValid() {
        return applicationFile.exists() && applicationFile.isFile();
    }

    /**
     * Get the URL of the application file
     *
     * @return the URL
     * @throws SmartFrogRuntimeException if it will not parse
     */
    public URL getURL() throws SmartFrogRuntimeException {
        URI uri = getApplicationFile().toURI();
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new SmartFrogRuntimeException("URI=" + uri + " " + e, e);
        }
    }

    /**
     * Compares this object with the specified object for order.  Returns a negative integer, zero, or a positive
     * integer as this object is less than, equal to, or greater than the specified object.
     *
     * the sorting is done using the application name as the comparison
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object.
     * @throws ClassCastException if the specified object's type prevents it from being compared to this object.
     */
    public int compareTo(Object o) {
        DeployedDir other = (DeployedDir) o;
        return name.compareTo(other.name);
    }

    public enum Status {
        NEW,
        DEPLOYED,
        TERMINATED,
        FAILED,
    }
}
