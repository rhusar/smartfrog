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
package org.smartfrog.services.deployapi.engine;

import nu.xom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.wsa.AlpineEPR;
import org.smartfrog.services.deployapi.binding.DescriptorHelper;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.DeploymentLanguage;
import org.smartfrog.services.deployapi.system.LifecycleStateEnum;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.endpoints.system.OptionProcessor;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.faults.DeploymentException;
import static org.smartfrog.services.deployapi.transport.faults.FaultRaiser.ERROR_NO_LANGUAGE_DECLARED;
import static org.smartfrog.services.deployapi.transport.faults.FaultRaiser.raiseBadArgumentFault;
import static org.smartfrog.services.deployapi.transport.faults.FaultRaiser.translateException;
import org.smartfrog.services.deployapi.transport.wsrf.PropertyMap;
import org.smartfrog.services.deployapi.transport.wsrf.WSRPResourceSource;
import org.smartfrog.services.deployapi.transport.wsrf.WsrfUtils;
import org.smartfrog.services.filesystem.filestore.AddedFilestore;
import org.smartfrog.services.filesystem.filestore.FileEntry;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.ActionDeploy;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;


/**
 * created Aug 5, 2004 3:00:26 PM
 */

public class Application implements WSRPResourceSource {

    private final static Log log = LogFactory.getLog(Application.class);
    private String jsdlLanguage;
    private String extension;
    /**
     * app uri
     */
    private String id;

    /**
     * name of app
     */
    private String name;

    /**
     * hostname, may be null
     */

    private String hostname;

    private String address;

    private DeploymentLanguage language;

    /**
     * where the file gets saved/downloaded to
     */
    private File descriptorFile;

    /**
     * Any JSDL file that came with it
     */
    private File jsdlFile;

    /**
     * what properties are set?
     */
    private PropertyMap properties = new PropertyMap();

    /**
     * what are we bonded to
     */
    private WeakReference<Prim> primReference;

    /**
     * what handles callbacks
     */
    private CallbackRaiser callbackRaiser;

    /**
     * job info
     */

    private Element request;

    /**
     * our Xom epr
     */
    private Element endpointer;


    /**
     * a deployment descriptor
     */

//    private MessageElement descriptorBody;

    /**
     * CDL document; will be null for a CDL file
     */
//    private CdlDocument cdlDocument;


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
     * type of the callback
     */
    private URI callbackType;

    /**
     * url for callbacks
     */
    private URL callbackURL;

//   private NotificationInformationType callbackInformation;

    /**
     * callback sequence counter
     */
    private int callbackSequenceID = 0;


    /**
     * callback identifier
     */
    private String callbackIdentifier;

    /**
     * enter terminated state
     */
    private TerminationRecord terminationRecord;

    private AlpineEPR alpineEPR;

    private ServerInstance owner;

    /**
     * Attached files
     */
    private List<FileEntry> attachments=new ArrayList<FileEntry>();

    public Application(String id, ServerInstance owner) {
        setId(id);
        this.owner=owner;
        addInitialProperties();
        enterStateNotifying(LifecycleStateEnum.instantiated, "id=" + id);
    }

    /**
     * In which initial properties are set
     */
    private void addInitialProperties() {
        WsrfUtils.addManagementCharacteristics(properties, CddlmConstants.CDL_API_SYSTEM_CAPABILITY);
        properties.addStaticProperty(CddlmConstants.PROPERTY_SYSTEM_STARTED_TIME, "");
        properties.addStaticProperty(CddlmConstants.PROPERTY_SYSTEM_TERMINATED_TIME, "");
    }

    /**
     * add a property
     *
     * @param property  qname
     * @param timestamp timestamp. can be null for now.
     */
    protected void addTimeProperty(QName property, Date timestamp) {
        if (timestamp == null) {
            timestamp = new Date();
        }
        SoapElement elt = new SoapElement(property);
        elt.appendChild(Utils.toIsoTime(timestamp));
        properties.addStaticProperty(property, elt);

    }

    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object. A subclass
     * overrides the <code>finalize</code> method to dispose of system resources
     * or to perform other cleanup.
     *
     * @throws Throwable the <code>Exception</code> raised by this method
     */
    protected void finalize() throws Throwable {
        super.finalize();
    }


    public String getCallbackIdentifier() {
        return callbackIdentifier;
    }

    public void setCallbackIdentifier(String callbackIdentifier) {
        this.callbackIdentifier = callbackIdentifier;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        endpointer = Utils.makeAddress(address, Constants.WS_ADDRESSING_NAMESPACE);
        alpineEPR = new AlpineEPR(address);
        endpointer = alpineEPR.toXom(Constants.ENDPOINT_REFERENCE, Constants.WS_ADDRESSING_NAMESPACE, "wsa");
    }

    public AlpineEPR getAlpineEPR() {
        return alpineEPR;
    }

    public Element getEndpointer() {
        return endpointer;
    }

    /**
     * clear all callback information
     */
/*
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


*/
    public WeakReference getPrimReference() {
        return primReference;
    }

    public void setPrimReference(WeakReference<Prim> primReference) {
        this.primReference = primReference;
    }

    /**
     * get the message descriptor. may be null
     */
/*
    public MessageElement getDescriptorBody() {
        return descriptorBody;
    }
*/
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

    public DeploymentLanguage getLanguage() {
        return language;
    }

    public String getJsdlLanguage() {
        return jsdlLanguage;
    }

    public File getDescriptorFile() {
        return descriptorFile;
    }

    public File getJsdlFile() {
        return jsdlFile;
    }

    public Element getRequest() {
        return request;
    }


    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
        properties.addStaticProperty(Constants.PROPERTY_MUWS_RESOURCEID,
                XomHelper.makeResourceId(id));
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


    public void bindToPrim(Prim prim) {
        primReference = new WeakReference<Prim>(prim);
    }

    /**
     * get the prim; raise a fault if it is terminated
     *
     * @return resolved element
     */

    public Prim resolvePrim() {
        if (primReference == null) {
            throw new BaseException("job exists but reference is undefined");
        }
        Prim weakRef = primReference.get();
        if (weakRef == null) {
            throw new BaseException("application has already terminated");
            //TODO return a terminated reference
        }
        Prim prim = weakRef;
        return prim;
    }

    /**
     * get the prim
     *
     * @return the prim reference or null for no such reference.
     */
    public Prim resolvePrimNonFaulting() {
        if (primReference != null) {
            return primReference.get();
        }
        return null;
    }

    /**
     * equality is URI only
     *
     * @param o
     * @return true for a match
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Application)) {
            return false;
        }

        final Application job = (Application) o;

        return !(id != null ? !id.equals(job.id) : job.id != null);

    }

    /**
     * hash code is from the URI
     *
     * @return #code
     */
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    public String getExtension() {
        return extension;
    }

    /**
     * set the request. The name is extracted here; it remains null if currently
     * undefined
     *
     * @param requestIn
     */


    public void bind(Element requestIn, OptionProcessor options) {
        this.request = requestIn;

        Element descriptor = requestIn.getFirstChildElement(DescriptorHelper.DESCRIPTOR,
                DescriptorHelper.TNS);

/*
        if (options != null && options.getName() != null) {
            name = options.getName();
        }
        */


        if (descriptor == null) {
            throw raiseBadArgumentFault("missing deployment descriptor");
        }

        //extract language from descriptor
        String languageURI = descriptor.getAttributeValue(
                DescriptorHelper.LANGUAGE, DescriptorHelper.TNS);
        if (languageURI == null) {
            throw raiseBadArgumentFault(
                    ERROR_NO_LANGUAGE_DECLARED);
        }
        language = DeploymentLanguage.eval(languageURI);

        extension = language.getExtension();


    }


    /**
     * first pass impl of deployment; use sfsystem
     *
     * @param hostname
     * @param application
     * @param language
     * @param url
     * @return
     * @
     */
    private Prim deployThroughSFSystem(String hostname, String application,
                                       DeploymentLanguage language, String url,
                                       String subprocess) {
        try {
            ConfigurationDescriptor config = new ConfigurationDescriptor(
                    application, url);
            config.setHost(hostname);
            config.setActionType(ConfigurationDescriptor.Action.DEPLOY);
            if (subprocess != null) {
                config.setSubProcess(subprocess);
            }
            config.setContextAttribute(ActionDeploy.KEY_LANGUAGE,language.getExtension());
            log.info("Deploying " + url + " to " + hostname+" as "+language.getDescription());
            //deploy, throwing an exception if we cannot
            Object result = config.execute(null);
            enterStateNotifying(LifecycleStateEnum.initialized, "initialized");
            enterStateNotifying(LifecycleStateEnum.running, "running");
            if (result instanceof Prim) {
                return (Prim) result;
            } else {
                final String message = "got something not a Prim back from a deployer";
                log.info(message);
                throw new BaseException(message + " " + result.toString());
            }

        } catch (Exception exception) {
            throw translateException(exception);
        }
    }


    public void deployApplication(File file, DeploymentLanguage language) {

        if (state != LifecycleStateEnum.instantiated) {
            throw new DeploymentException(Constants.F_LIFECYCLE_EXCEPTION);
        }
        String url = file.toURI().toString();
        Prim runningJobInstance;
        runningJobInstance =
                deployThroughSFSystem(hostname, getId(), language,url, null);
        bindToPrim(runningJobInstance);
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
            QName propname = null;
            switch (state) {
                case instantiated:
                    propname = Constants.PROPERTY_SYSTEM_CREATED_TIME;
                    break;

                case initialized:
                    break;
                case running:
                    propname = Constants.PROPERTY_SYSTEM_STARTED_TIME;
                    break;

                case failed:
                case terminated:
                    propname = Constants.PROPERTY_SYSTEM_TERMINATED_TIME;
                    break;
            }
            if (propname != null) {
                addTimeProperty(propname,
                        null);
            }
            if (callbackRaiser != null) {
                callbackRaiser.raiseLifecycleEvent(this,
                        resolvePrimNonFaulting(),
                        null);
            }
        }
    }


    public synchronized void enterFailedState(String text) {
        enterStateNotifying(LifecycleStateEnum.failed, text);
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


    public String toString() {
        return "job ID=" + id + " address=" + address + " state=" + state.toString();
    }

    /**
     * Get a resource
     *
     * @param resource
     * @return null for no match;
     * @throws BaseException if they feel like it
     */
    public Element getProperty(QName resource) {
        return properties.getProperty(resource);

    }


    /**
     * Terminate a job
     *
     * @param reason why
     * @return
     * @throws java.rmi.RemoteException
     */
    public synchronized boolean terminate(String reason)
            throws RemoteException {
        Prim target = resolvePrimNonFaulting();
        if (state == LifecycleStateEnum.terminated) {
            log.info("job already terminated");
            return true;
        }
        TerminationRecord termination;
        termination =
                new TerminationRecord(TerminationRecord.NORMAL,
                        reason,
                        null);
        if (target != null) {
            target.sfTerminate(termination);
        }
        enterTerminatedStateNotifying(termination);
        return true;
    }


    public Element ping(Element request) {
        Prim target = resolvePrimNonFaulting();
        if (target == null) {
            throw new BaseException(Constants.F_LIVENESS_EXCEPTION);
        }
        try {
            //TODO: ping the app to determine its real health
            target.sfPing(null);
            //TODO: state
            return XomHelper.apiElement(Constants.API_ELEMENT_PING_RESPONSE);
        } catch (SmartFrogLivenessException e) {
            throw new BaseException(Constants.F_LIVENESS_EXCEPTION, e);
        } catch (RemoteException e) {
            throw new BaseException(Constants.F_LIVENESS_EXCEPTION, e);
        }
    }

    /**
     * start turning
     *
     * @return
     * @throws RemoteException
     * @throws BaseException
     */
    public synchronized Element run() throws RemoteException {
        Prim target = resolvePrim();
        try {
            target.sfStart();
            Element response = XomHelper.apiElement(Constants.API_ELEMENT_RUN_RESPONSE);
            return response;
        } catch (SmartFrogException e) {
            throw new BaseException(e);
        }
    }


    /**
     * Create a temporary file
     * @param extension
     * @return
     */
    public FileEntry createNewTempFile(String extension) throws IOException {
        AddedFilestore filestore = owner.getFilestore();
        FileEntry entry = filestore.createNewFile("file", extension);
        addAttachment(entry);
        return entry;
    }

    public synchronized void addAttachment(FileEntry entry) {
        attachments.add(entry);
    }


}
