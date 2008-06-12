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

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Vector;
import java.util.List;
import java.rmi.RemoteException;

/**
 * Serializable RPM class, which models much of the RPM.
 */

public final class RpmFile implements Serializable, RpmErrors {

    /**
     * RPM name
     */
    public String name;

    /**
     * RPM Version string
     */
    public String version;

    /**
     * Any description for error messages
     */
    public String description;

    /**
     * Filename of the RPM
     */
    public String rpmFile;

    /**
     * Delete the file during termination?
     */
    public boolean deleteOnTermination;

    /**
     * A list of managed files
     */
    public Vector<String> managedFiles = new Vector<String>(0);
    private static final Reference REF_FILES = new Reference(RpmPackage.ATTR_FILES);


    /**
     * Simple constructor. The ManagedFiles list is set to an empty list
     */
    public RpmFile() {
    }

    /**
     * Full constructor
     *
     * @param name         RPM name
     * @param version      RPM version
     * @param description  description
     * @param rpmFile      the RPM file itself
     * @param managedFiles a list of managed files, can be null
     */
    public RpmFile(String name, String version, String description, String rpmFile,
                   Vector<String> managedFiles) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.rpmFile = rpmFile;
        if (managedFiles != null) {
            this.managedFiles = managedFiles;
        } else {
            this.managedFiles = new Vector<String>(0);
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getRpmFile() {
        return rpmFile;
    }

    public List<String> getManagedFiles() {
        return managedFiles;
    }

    /**
     * Build an RPM file from a source prim
     * @param source source of the RPM information
     * @throws SmartFrogResolutionException failure to resolve
     * @throws RemoteException network trouble
     */
    public RpmFile(Prim source) throws SmartFrogResolutionException, RemoteException {
        name = source.sfResolve(RpmPackage.ATTR_NAME, "", true);
        version = source.sfResolve(RpmPackage.ATTR_VERSION, "", true);
        description = source.sfResolve(RpmPackage.ATTR_DESCRIPTION, "", true);
        rpmFile = FileSystem.lookupAbsolutePath(source, RpmPackage.ATTR_RPMFILE, null, null, false, null);
        deleteOnTermination = source.sfResolve(RpmPackage.ATTR_DELETEONTERMINATION, false, true);
        managedFiles = ListUtils.resolveStringList(source, REF_FILES,true);
    }

    /**
     * Equality is based purely on name, not even the version or filename gets a look in.
     *
     * @param o the other object
     * @return the object
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RpmFile that = (RpmFile) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }

        return true;
    }

    /**
     * Hash Code
     *
     * @return the hash code
     */
    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        return result;
    }

    /**
     * Get the standard package name
     * @return the package name of the form name-version
     */
    public String getRpmPackageName() {
        StringBuilder b = new StringBuilder();
        b.append(name).append('-').append(version);
        return b.toString();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("RPM file").append(getRpmPackageName());
        b.append('\n');
        b.append(rpmFile);
        if (deleteOnTermination) {
            b.append("\n-deleting on termination");
        }

        if (description != null) {
            b.append('\n').append(description);
        }

        return super.toString();
    }

    /**
     * Add another managed file to the list
     *
     * @param filename file to add
     */
    public void addManagedFile(String filename) {
        managedFiles.add(filename);
    }

    /**
     * Validate the RPM file itself, by checking it is there and not a directory.
     *
     * @throws FileNotFoundException if the file is not there.
     */
    public void validateRpmFile() throws FileNotFoundException {
        File file = toFile();
        if (!file.exists()) {
            throw new FileNotFoundException(ERROR_NO_SUCH_FILE + file + '\n' + this);
        }

        if (!file.isFile() && !file.isDirectory()) {
            throw new FileNotFoundException(ERROR_NOT_AN_RPM_FILE + file + '\n' + this);
        }
    }

    /**
     * Convert to a file in the local filesystem syntax
     *
     * @return a file representing the rpm file
     */
    public File toFile() {
        return new File(rpmFile);
    }

    /**
     * Iterate through all the managed files and verify that they exist
     *
     * @throws FileNotFoundException if one is missing
     */
    public void verifyAllManagedFilesExist() throws FileNotFoundException {
        for (String filename : managedFiles) {
            File file = new File(filename);
            if (!file.exists()) {
                throw new FileNotFoundException(ERROR_NO_SUCH_FILE + file + '\n' + this);
            }
        }
    }
}
