/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cddlm.api;

import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.generated.api.types.LookupApplicationRequest;

import java.rmi.RemoteException;

/**
 * created Aug 4, 2004 4:26:22 PM
 */

public class LookupApplicationProcessor extends Processor {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(CreateProcessor.class);

    public LookupApplicationProcessor(SmartFrogHostedEndpoint owner) {
        super(owner);
    }

    public URI lookupApplication(LookupApplicationRequest lookupApplication)
            throws RemoteException {
        org.apache.axis.types.NCName appname = lookupApplication.getApplication();
        if (appname == null) {
            throw raiseBadArgumentFault(ERROR_NO_APPLICATION);
        }
        URI uri = makeURI(appname.toString());
        return uri;
    }

}
