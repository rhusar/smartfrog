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

import org.apache.axis.AxisFault;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.generated.api.types.CallbackEnum;
import org.smartfrog.services.cddlm.generated.api.types.CallbackInformationType;
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;

/**
 * extract callback information from a job, attach it to a job created Sep 2,
 * 2004 5:42:51 PM
 */

public class CallbackProcessor extends Processor {

    public CallbackProcessor(SmartFrogHostedEndpoint owner) {
        super(owner);
    }

    public void process(JobState job, CallbackInformationType callbacks)
            throws AxisFault {
        CallbackEnum type = null;
        if (callbacks != null) {
            type = callbacks.getType();
        }
        if (type != null) {
            throw raiseUnsupportedCallbackFault(
                    DeployApiConstants.UNSUPPORTED_CALLBACK_WIRE_MESSAGE);
        }

    }
}


