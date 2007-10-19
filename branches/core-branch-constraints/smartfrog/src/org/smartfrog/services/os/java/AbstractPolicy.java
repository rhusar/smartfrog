/**
 * (C) Copyright 2005 Hewlett-Packard Development Company, LP This library is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version. This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA For
 * more information: www.smartfrog.org
 */
package org.smartfrog.services.os.java;

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/** base of our policies */
public abstract class AbstractPolicy extends PrimImpl {

    /** what artifacts are separated by {@value} */
    public static final String ARTIFACT_SEPARATOR = "-";

    /**
     * Create an artifact filename. This claims to be maven1, but really it is also maven2, as if there is a classifier,
     * we use that as well. There just near zero chance of that finding a match against the classic M1 repository.
     *
     * @param library
     * @return the filename of an artifact using maven separation rules
     */
    protected String createMavenArtifactName(SerializedArtifact library) throws SmartFrogRuntimeException {
        SerializedArtifact.assertValid(library, false);
        StringBuffer buffer = new StringBuffer();
        buffer.append(library.artifact);
        if (nonEmpty(library.version)) {
            buffer.append(ARTIFACT_SEPARATOR);
            buffer.append(library.version);
        }
        if (nonEmpty(library.classifier)) {
            buffer.append(ARTIFACT_SEPARATOR);
            buffer.append(library.classifier);
        }
        if (nonEmpty(library.extension)) {
            buffer.append('.');
            buffer.append(library.extension);
        }
        return buffer.toString();
    }

    /** @throws RemoteException  */
    protected AbstractPolicy() throws RemoteException {
        super();
    }

    /**
     * test that a string is not empty
     *
     * @param s
     * @return true iff the string is non null, and not ""
     */
    protected boolean nonEmpty(String s) {
        return s != null && s.length() > 0;
    }
}
