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
package org.smartfrog.services.cddlm.engine;

import org.apache.axis.AxisFault;
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.smartfrog.services.cddlm.api.CallbackRaiser;
import org.smartfrog.services.cddlm.api.OptionProcessor;
import org.smartfrog.services.cddlm.api.Processor;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorType;
import org.smartfrog.services.cddlm.generated.api.types._deployRequest;
import org.smartfrog.services.cddlm.cdl.CdlDocument;
import org.smartfrog.sfcore.prim.Prim;

import javax.xml.namespace.QName;
import java.lang.ref.WeakReference;

/**
 * created Aug 5, 2004 3:00:26 PM
 */

public class JobState {

    public JobState() {
    }

    /**
     * fill in from a job request
     *
     * @param request
     */
    public JobState(_deployRequest request, OptionProcessor options)
            throws AxisFault {
        bind(request, options);
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

    /**
     * any fault
     */
    private Throwable fault;


    /**
     * a deployment descriptor
     */

    private MessageElement descriptor;

    /**
     * CDL document; will be null for a CDL file
     */
    private CdlDocument cdlDocument;

    /**
     * the language
     */
    private int language = -1;

    private String languageName = null;

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

    public Throwable getFault() {
        return fault;
    }

    public void setFault(Throwable fault) {
        this.fault = fault;
    }

    /**
     * get the message descriptor. may be null
     *
     * @return
     */
    public MessageElement getDescriptor() {
        return descriptor;
    }

    /**
     * get the language
     *
     * @return
     */
    public int getLanguage() {
        return language;
    }

    public String getLanguageName() {
        return languageName;
    }

    /**
     * set the request. The name is extracted here; it remains null if currently
     * undefined
     *
     * @param requestIn
     */
    public void bind(_deployRequest requestIn, OptionProcessor options)
            throws AxisFault {
        this.request = requestIn;

        if (options != null && options.getName() != null) {
            name = options.getName();
        }
        DeploymentDescriptorType descriptorType = request.getDescriptor();
        if (descriptorType != null && descriptorType.getData() != null) {

            MessageElement[] messageElements = descriptorType.getData()
                    .get_any();
            if (messageElements.length != 1) {
                throw Processor.raiseBadArgumentFault(
                        Processor.WRONG_MESSAGE_ELEMENT_COUNT);
            }

            descriptor = messageElements[0];
            descriptor.getNamespaceURI();
            QName qname = descriptor.getQName();
            languageName = qname.toString();
            language = Processor.determineLanguage(qname);
        }

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

    public CdlDocument getCdlDocument() {
        return cdlDocument;
    }

    public void setCdlDocument(CdlDocument cdlDocument) {
        this.cdlDocument = cdlDocument;
    }

    public void bindToPrim(Prim prim) {
        primReference = new WeakReference(prim);
    }

    /**
     * get the prim; raise a fault if it is terminated
     *
     * @return
     * @throws AxisFault
     */
    public Prim resolvePrimFromJob() throws AxisFault {
        if (primReference == null) {
            throw Processor.raiseNoSuchApplicationFault(
                    "job exists but reference is undefined");
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
     * get the prim
     *
     * @return the prim reference or null for no such reference.
     */
    public Prim resolvePrimNonFaulting() {
        if (primReference != null) {
            return (Prim) primReference.get();
        }
        return null;
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
     *
     * @return
     */
    public int hashCode() {
        return (uri != null ? uri.hashCode() : 0);
    }
}
