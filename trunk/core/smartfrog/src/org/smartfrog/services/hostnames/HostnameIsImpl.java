/** (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hostnames;

import org.smartfrog.sfcore.workflow.conditional.conditions.AbstractConditionPrim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * scan through a list of hostnames and check that the local hostname matches it
 */
public class HostnameIsImpl extends AbstractConditionPrim implements HostnameIs {
    protected final Reference refHostname;

    public HostnameIsImpl() throws RemoteException {
        refHostname = new Reference(ATTR_HOSTNAMES);
    }

    @Override
    public boolean evaluate() throws RemoteException, SmartFrogException {
        Vector<String> hostnames = ListUtils.resolveStringList(this, refHostname, true);
        String actual = HostnameUtils.getLocalHostname();


        for (String expected : hostnames) {
            if (actual.startsWith(expected)) {
                return true;
            }
        }
        String expected = ListUtils.stringify(hostnames, "[", ", ", "]");
        setFailureText("Expected hostname in \"" + expected + "\" but got \"" + actual + "\"");
        return false;
    }


}
