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
package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Created 18-Mar-2009 16:00:30
 */

public class TextListFileImplImpl extends TextFileImpl {

    public TextListFileImplImpl() throws RemoteException {
    }

    /**
     * {@inheritDoc
     *
     * @throws SmartFrogException resolution problems
     * @throws RemoteException    networking
     */
    @Override
    @SuppressWarnings({"RefusedBequest"})
    protected String buildText() throws SmartFrogException, RemoteException {
        Vector<String> lines = ListUtils.resolveStringList(this, new Reference(ATTR_TEXT), true);
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            builder.append(line);
            builder.append('\n');
        }
        return builder.toString();
    }
}
