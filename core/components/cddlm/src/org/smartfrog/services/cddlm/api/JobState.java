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
import org.smartfrog.services.cddlm.generated.api.types._deployRequest;
import org.smartfrog.sfcore.prim.Prim;

import java.lang.ref.WeakReference;

/**
 * created Aug 5, 2004 3:00:26 PM
 */

public class JobState {

    public JobState() {
    }

    /**
     * fill in from a job request
     * @param request
     */
    public JobState(_deployRequest request) {
        setRequest(request);
    }

    /**
     * app uri
     */
    private URI uri;

    /**
     * name of app
     */
    private String name;

    /**
     * hostname, may be null
     */

    private String hostname;

    /**
     * what are we bonded to
     */
    private WeakReference primReference;

    /**
     * what handles callbacks
     */
    private CallbackRaiser callbacks;

    /**
     * job info
     */

    private _deployRequest request;

    public CallbackRaiser getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(CallbackRaiser callbacks) {
        this.callbacks = callbacks;
    }

    public WeakReference getPrimReference() {
        return primReference;
    }

    public void setPrimReference(WeakReference primReference) {
        this.primReference = primReference;
    }

    public _deployRequest getRequest() {
        return request;
    }

    /**
     * set the request. Also, get name and uri from the message
     *
     * @param request
     */
    public void setRequest(_deployRequest request) {
        this.request = request;
        name = request.getName().toString();
        uri = Processor.makeURIFromApplication(name);
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * get the prim; raise a fault if it is terminated
     *
     * @return
     * @throws AxisFault
     */
    public Prim resolvePrimFromJob() throws AxisFault {
        final WeakReference primReference = getPrimReference();
        if (primReference == null) {
            throw Processor.raiseNoSuchApplicationFault(
                    "application not found");
        }
        Object weakRef = primReference.get();
        if (weakRef == null) {
            //TODO return a terminated reference
            throw Processor.raiseNoSuchApplicationFault(
                    "application is no longer active");
        }
        Prim prim = (Prim) weakRef;
        return prim;
    }

    /**
     * equality is URI only
     *
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobState)) {
            return false;
        }

        final JobState jobState = (JobState) o;

        if (uri != null ? !uri.equals(jobState.uri) : jobState.uri != null) {
            return false;
        }

        return true;
    }

    /**
     * hash code is from the URI
     * @return
     */
    public int hashCode() {
        return (uri != null ? uri.hashCode() : 0);
    }
}
