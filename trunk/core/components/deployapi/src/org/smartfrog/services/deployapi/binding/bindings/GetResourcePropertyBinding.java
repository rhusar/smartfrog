package org.smartfrog.services.deployapi.binding.bindings;

import org.ggf.xbeans.cddlm.wsrf.wsrp.GetResourcePropertyDocument;
import org.ggf.xbeans.cddlm.wsrf.wsrp.GetResourcePropertyResponseDocument;
import org.smartfrog.services.deployapi.binding.EndpointBinding;

/**
 */
public class GetResourcePropertyBinding extends EndpointBinding<GetResourcePropertyDocument,
        GetResourcePropertyResponseDocument> {

    /**
     * create a request object
     *
     */
    public GetResourcePropertyDocument createRequest() {
        return GetResourcePropertyDocument.Factory.newInstance(getInOptions());
    }

    /**
     * create a request object
     *
     */
    public GetResourcePropertyResponseDocument createResponse() {
        return GetResourcePropertyResponseDocument.Factory.newInstance(getOutOptions());
    }
}
