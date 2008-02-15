/*
 * Copyright 2005-2007 Noelios Consulting.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the "License"). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.txt See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and
 * include the License file at http://www.opensource.org/licenses/cddl1.txt If
 * applicable, add the following below this CDDL HEADER, with the fields
 * enclosed by brackets "[]" replaced with your own identifying information:
 * Portions Copyright [yyyy] [name of copyright owner]
 */

package org.smartfrog.services.restlet.overrides;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.util.Helper;

import java.util.Arrays;
import java.util.List;

/**
 *
 * This class exists to work around limitations in the 1.1 Restlet implementation, namely no
 * support for http proxies.
 * Created 29-Jan-2008 12:47:51
 *
 */

public class ProxyEnabledClient extends Client {

    /**
     * The helper provided by the implementation.
     */
    private Helper helper;

    /**
     * Constructor.
     *
     * @param context   The context.
     * @param protocols The connector protocols.
     */
    public ProxyEnabledClient(Context context, List<Protocol> protocols) {
        super(context, protocols);

        if ((protocols != null) && (protocols.size() > 0)) {
            helper=new ProxyEnabledHttpClientHelper(this);
        }
    }

    /**
     * Constructor.
     *
     * @param context  The context.
     * @param protocol The connector protocol.
     */
    public ProxyEnabledClient(Context context, Protocol protocol) {
        this(context, Arrays.asList(protocol));
    }

    /**
     * Constructor.
     *
     * @param protocols The connector protocols.
     */
    public ProxyEnabledClient(List<Protocol> protocols) {
        this(null, protocols);
    }

    /**
     * Constructor.
     *
     * @param protocol The connector protocol.
     */
    public ProxyEnabledClient(Protocol protocol) {
        this(null, protocol);
    }

    /**
     * Returns the helper provided by the implementation.
     *
     * @return The helper provided by the implementation.
     */
    public ProxyEnabledHttpClientHelper getHelper() {
        return (ProxyEnabledHttpClientHelper) helper;
    }

    /**
     * Handles a call.
     *
     * @param request  The request to handle.
     * @param response The response to update.
     */
    public void handle(Request request, Response response) {
        init(request, response);
        if (getHelper() != null)
            getHelper().handle(request, response);
    }

    @Override
    public void start() throws Exception {
        if (isStopped()) {
            super.start();
            if (getHelper() != null)
                getHelper().start();
        }
    }

    @Override
    public void stop() throws Exception {
        if (isStarted()) {
            if (getHelper() != null)
                getHelper().stop();
            super.stop();
        }
    }
    
    /**
     * Bind to the local proxy settings
     */
    public void bindToSystemProxySettings() {
        getHelper().bindToSystemProxySettings();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return getHelper()==null?"Uninitialized http client":("HttpClient with "+getHelper().toString());
    }
}
