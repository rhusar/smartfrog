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
import org.apache.axis.types.NCName;
import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.cddlm.api.CallbackRaiser;
import org.smartfrog.services.cddlm.api.OptionProcessor;
import org.smartfrog.services.cddlm.api.Processor;
import org.smartfrog.services.cddlm.cdl.CdlDocument;
import org.smartfrog.services.cddlm.cdl.XomAxisHelper;
import org.smartfrog.services.cddlm.generated.api.types.ApplicationStatusType;
import org.smartfrog.services.cddlm.generated.api.types.CreateRequest;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorType;
import org.smartfrog.services.cddlm.generated.api.types.LifecycleEventRequest;
import org.smartfrog.services.cddlm.generated.api.types.LifecycleStateEnum;
import org.smartfrog.services.cddlm.generated.api.types.NotificationInformationType;
import org.smartfrog.services.cddlm.generated.api.types.UnboundedXMLAnyNamespace;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import javax.xml.namespace.QName;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * created Aug 5, 2004 3:00:26 PM
 */

public class JobState {

    private final static Log log = LogFactory.getLog(JobState.class);

    public JobState() {
    }

    /**
     * fill in from a job request
     *
     * @param request
     */
    public JobState(CreateRequest request, OptionProcessor options)
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
    private CallbackRaiser callbackRaiser;

    /**
     * job info
     */

    private CreateRequest request;

    /**
     * any fault
     */
    private AxisFault fault;


    /**
     * a deployment descriptor
     */

    private MessageElement descriptorBody;

    /**
     * CDL document; will be null for a CDL file
     */
    private CdlDocument cdlDocument;


    /**
     * description string from the JSDL
     */
    private String description;


    /**
     * lifecycle state
     */
    private LifecycleStateEnum state = LifecycleStateEnum.undefined;

    /**
     * state information string
     */
    private String stateInfo;

    /**
     * the language
     */
    private int language = -1;

    /**
     * name of the language (one of the constants)
     */
    private String languageName = null;
    /**
     * type of the callback
     */
    private URI callbackType;

    /**
     * url for callbacks
     */
    private URL callbackURL;

    private NotificationInformationType callbackInformation;

    /**
     * callback sequence counter
     */
    private int callbackSequenceID = 0;

    /**
     * enter terminated state
     */
    private TerminationRecord terminationRecord;

    /**
     * callback identifier
     */
    private String callbackIdentifier;

    public String getCallbackIdentifier() {
        return callbackIdentifier;
    }

    public void setCallbackIdentifier(String callbackIdentifier) {
        this.callbackIdentifier = callbackIdentifier;
    }

    public CallbackRaiser getCallbackRaiser() {
        return callbackRaiser;
    }

    public void setCallbackRaiser(CallbackRaiser callbackRaiser) {
        this.callbackRaiser = callbackRaiser;
    }

    /**
     * clear all callback information
     */
    public void clearCallbackData() {
        callbackRaiser = null;
        callbackIdentifier = null;
        callbackType = null;
        callbackURL = null;
        callbackInformation = null;
        resetSequenceCounter();
    }

    public NotificationInformationType getCallbackInformation() {
        return callbackInformation;
    }

    public void setCallbackInformation(
            NotificationInformationType callbackInformation) {
        this.callbackInformation = callbackInformation;
    }

    public WeakReference getPrimReference() {
        return primReference;
    }

    public void setPrimReference(WeakReference primReference) {
        this.primReference = primReference;
    }

    public CreateRequest getRequest() {
        return request;
    }

    public AxisFault getFault() {
        return fault;
    }

    public void setFault(AxisFault fault) {
        this.fault = fault;
    }

    /**
     * get the message descriptor. may be null
     *
     * @return
     */
    public MessageElement getDescriptorBody() {
        return descriptorBody;
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

    public URI getCallbackType() {
        return callbackType;
    }

    public void setCallbackType(URI callbackType) {
        this.callbackType = callbackType;
    }

    public URL getCallbackURL() {
        return callbackURL;
    }

    public void setCallbackURL(URL callbackURL) {
        this.callbackURL = callbackURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * set the request. The name is extracted here; it remains null if currently
     * undefined
     *
     * @param requestIn
     */
    public void bind(CreateRequest requestIn, OptionProcessor options)
            throws AxisFault {
        this.request = requestIn;

        if (options != null && options.getName() != null) {
            name = options.getName();
        }
        DeploymentDescriptorType descriptorType = request.getDescriptor();
        if (descriptorType != null) {
            //extract language from descriptor
            URI languageURI = descriptorType.getLanguage();
            if (languageURI == null) {
                throw Processor.raiseBadArgumentFault(
                        Processor.ERROR_NO_LANGUAGE_DECLARED);
            }
            languageName = languageURI.toString();
            language = Processor.determineLanguage(languageName);

            //now extract body from message
            if (descriptorType.getBody() != null) {
                MessageElement[] messageElements = descriptorType.getBody()
                        .get_any();
                if (messageElements.length != 1) {
                    throw Processor.raiseBadArgumentFault(
                            Processor.ERROR_WRONG_MESSAGE_ELEMENT_COUNT);
                }

                descriptorBody = messageElements[0];
            }
            descriptorBody.getNamespaceURI();
            QName qname = descriptorBody.getQName();
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


    public ApplicationStatusType createApplicationStatus() {
        ApplicationStatusType status = new ApplicationStatusType();
        status.setName(new NCName(name));
        status.setReference(getUri());
        status.setState(getState());
        status.setStateInfo(stateInfo);
        status.setNotification(callbackInformation);
        List extras = new LinkedList();
        /*
        if(callbackURL!=null) {
            try {
                StringBuffer buffer=new StringBuffer();
                buffer.append("<callback xmlns='http://smartfrog.org'>");
                buffer.append("<url>");
                buffer.append(callbackURL);
                buffer.append("</url>");
                if(callbackIdentifier!=null) {
                    buffer.append("<id>");
                    buffer.append(callbackIdentifier);
                    buffer.append("</id>");
                }
                buffer.append("</callback>");
                MessageElement me = XomAxisHelper.parseAndConvert(buffer.toString());
                extras.add(me);
            } catch (Exception e) {
                log.info("ignoring",e);
            }
        }
        */
        if (fault != null) {
            /* TODO: implement. this doesnt compile on jikes
            MessageContext msgContext = MessageContext.getCurrentContext();
            StringWriter sw = new StringWriter();
            SerializationContext context = new org.apache.axis.encoding.SerializationContext(sw,
                    msgContext);
            try {
                fault.output(context);
                sw.flush();
                MessageElement me = XomAxisHelper.parseAndConvert(
                        sw.toString());
                UnboundedXMLOtherNamespace extendedState = new UnboundedXMLOtherNamespace();
                extendedState.set_any(XomAxisHelper.toArray(me));
                status.setExtendedState(extendedState);
            } catch (Exception e) {
                //ignore and continue
                log.warn(e);
            }
            */
        }
        //add our extra state
        MessageElement[] elements = XomAxisHelper.toArray(extras);
        if (elements != null) {
            UnboundedXMLAnyNamespace extendedState = new UnboundedXMLAnyNamespace();
            extendedState.set_any(elements);
            status.setExtendedState(extendedState);
        }
        return status;
    }

    public LifecycleStateEnum getState() {
        return state;
    }

    public void setState(LifecycleStateEnum state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public TerminationRecord getTerminationRecord() {
        return terminationRecord;
    }

    /**
     * enter a state, send notification if this is different from a state we
     * were in before This method is synchronous, you cannot enter a state till
     * the last one was processed.
     *
     * @param newState new state to enter
     */
    public synchronized void enterStateNotifying(LifecycleStateEnum newState,
            String info) {
        stateInfo = info;
        if (!newState.equals(state)) {
            state = newState;
            if (callbackRaiser != null) {
                callbackRaiser.raiseLifecycleEvent(this,
                        resolvePrimNonFaulting(),
                        null);
            }
        }
    }

    /**
     * terminate, send a message out
     *
     * @param record
     */
    public synchronized void enterTerminatedStateNotifying(
            TerminationRecord record) {
        this.terminationRecord = record;
        enterStateNotifying(LifecycleStateEnum.terminated, record.toString());
    }


    /**
     * get the next sequence counter; every call will be different.
     *
     * @return
     */
    public synchronized int getNextSequenceNumber() {
        return ++callbackSequenceID;
    }

    /**
     * reset the sequence ID counter
     */
    public synchronized void resetSequenceCounter() {
        callbackSequenceID = 0;
    }

    /**
     * create a lifecycle event message, all filled in with: URI, callback
     * identifier, timestamp, status, sequence ID
     *
     * @return
     */
    public LifecycleEventRequest createLifecycleEventMessage() {
        ServerInstance server = ServerInstance.currentInstance();
        LifecycleEventRequest event = new LifecycleEventRequest();
        event.setApplicationReference(uri);
        event.setIdentifier(callbackIdentifier);
        event.setSequenceID(BigInteger.valueOf(getNextSequenceNumber()));
        event.setTimestamp(BigInteger.valueOf(System.currentTimeMillis() /
                1000));
        ApplicationStatusType status = createApplicationStatus();
        event.setStatus(status);
        return event;
    }
}
