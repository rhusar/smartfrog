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
import org.apache.axis.types.URI;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.engine.JobState;
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;
import org.smartfrog.services.cddlm.generated.api.types.CallbackAddressType;
import org.smartfrog.services.cddlm.generated.api.types.CallbackEnum;
import org.smartfrog.services.cddlm.generated.api.types.CallbackInformationType;
import org.smartfrog.services.cddlm.generated.api.types._setCallbackRequest;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * extract callback information from a job, attach it to a job Can be called in
 * a setCallback call, or as part of a deployment operation
 * <p/>
 * created Sep 2, 2004 5:42:51 PM
 */

public class CallbackProcessor extends Processor {
    public static final String ERROR_NO_CALLBACK = "No callback information";
    public static final String ERROR_NO_ADDRESS = "No address for callbacks";
    public static final String ERROR_NO_URI = "No URI in the address";
    public static final String ERROR_NO_CALLBACK_TYPE = "no callback type specified";
    public static final String ERROR_BAD_CALLBACK_URL = "Bad callback URL : ";

    public CallbackProcessor(SmartFrogHostedEndpoint owner) {
        super(owner);
    }

    /**
     * called by DeployProcessor
     *
     * @param job
     * @param callbacks
     * @param required
     * @throws AxisFault
     */
    public void process(JobState job, CallbackInformationType callbacks,
            boolean required)
            throws AxisFault {
        CallbackEnum type = null;
        if (callbacks == null) {
            if (!required) {
                job.clearCallbackData();
                return;
            } else {
                throw raiseBadArgumentFault(ERROR_NO_CALLBACK);
            }
        }

        type = callbacks.getType();
        if (type == null) {
            throw raiseBadArgumentFault(ERROR_NO_CALLBACK_TYPE);
        }
        if (!DeployApiConstants.CALLBACK_CDDLM_PROTOTYPE.equals(
                type.getValue())) {
            throw raiseUnsupportedCallbackFault(
                    DeployApiConstants.UNSUPPORTED_CALLBACK_WIRE_MESSAGE);
        }

        CallbackAddressType address = callbacks.getAddress();
        if (address == null) {
            throw raiseBadArgumentFault(ERROR_NO_ADDRESS);
        }

        URI uri = address.getUri();
        if (uri == null) {
            throw raiseBadArgumentFault(ERROR_NO_URI);
        }
        URL url;
        try {
            url = makeURL(uri);
        } catch (MalformedURLException e) {
            throw raiseBadArgumentFault(
                    ERROR_BAD_CALLBACK_URL + uri.toString());
        }
        job.setCallbackURL(url);
        job.setCallbackType(type.getValue());
        job.setCallbackRaiser(null);

    }

    /**
     * handle the request from the endpoint by looking up the app and handing
     * off to our internal process method
     */
    public boolean process(_setCallbackRequest setCallback) throws AxisFault {
        final URI appURI = setCallback.getApplication();
        if (appURI == null) {
            throw raiseBadArgumentFault(ERROR_NO_APPLICATION);
        }
        JobState job = lookupJob(appURI);
        process(job, setCallback.getCallback(), false);

        return true;
    }
}


