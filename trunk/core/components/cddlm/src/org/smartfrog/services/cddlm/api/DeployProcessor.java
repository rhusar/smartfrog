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
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorType;
import org.smartfrog.services.cddlm.generated.api.types._deployRequest;
import org.smartfrog.services.cddlm.generated.api.types._deployResponse;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

import javax.xml.namespace.QName;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.rmi.RemoteException;

/**
 * This class is *NOT* re-entrant. Create one for each deployment. created Aug
 * 4, 2004 3:58:37 PM
 */

public class DeployProcessor extends Processor {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(DeployProcessor.class);
    private static final String WRONG_MESSAGE_ELEMENT_COUNT = "wrong number of message elements";

    private _deployRequest request;
    private OptionProcessor options;
    private JobState job;

    public DeployProcessor(SmartFrogHostedEndpoint owner) {
        super(owner);
    }

    public _deployRequest getRequest() {
        return request;
    }

    public OptionProcessor getOptions() {
        return options;
    }

    /**
     * deployment
     *
     * @param deploy
     * @return
     * @throws AxisFault
     */
    public _deployResponse deploy(_deployRequest deploy) throws AxisFault {

        JobRepository repository;
        repository = ServerInstance.currentInstance().getJobs();

        //get the options out the way
        options = new OptionProcessor(getOwner());
        options.process(deploy.getOptions());

        //create a new jobstate
        job = new JobState(deploy, options);
        //then assign it missing parts. This does not add it to the repository yet
        repository.assignNameAndUri(job);
        request = deploy;

        CallbackProcessor callbackProcessor = new CallbackProcessor(getOwner());
        callbackProcessor.process(job, deploy.getCallback());

        DeploymentDescriptorType dd = deploy.getDescriptor();
        if (dd == null) {
            throw raiseBadArgumentFault("missing deployment descriptor");
        }
        URI source = dd.getSource();
        //this is our URI
        URI applicationReference = job.getUri();
        boolean deployed = false;
        if (source != null) {
            throwNotImplemented();
        } else {
            //here we deploy inline
            deployed = determineLanguageAndDeploy(job);

        }
        //add the job state to the store
        if (deployed) {
            repository.add(job);
        }
        _deployResponse response = new _deployResponse(applicationReference);
        return response;
    }

    /**
     * process a smartfrog deployment
     *
     * @param job
     * @throws AxisFault
     */
    public boolean determineLanguageAndDeploy(JobState job) throws AxisFault {
        DeploymentDescriptorType descriptor = request.getDescriptor();
        MessageElement[] messageElements = descriptor.getData().get_any();
        if (messageElements.length != 1) {
            throw raiseBadArgumentFault(WRONG_MESSAGE_ELEMENT_COUNT);
        }

        MessageElement descriptorData = messageElements[0];
        descriptorData.getNamespaceURI();
        QName qname = descriptorData.getQName();
        String languageAsString = qname.toString();
        int lan = determineLanguage(qname);
        boolean deployed = false;
        switch (lan) {
            case Constants.LANGUAGE_SMARTFROG:
                deployed = deploySmartFrog(descriptorData);
                break;
            case Constants.LANGUAGE_XML_CDL:
                deployed = deployCDL(descriptorData);
                break;
            case Constants.LANGUAGE_ANT:
                deployed = deployAnt(descriptorData);
                break;
            case Constants.LANGUAGE_UNKNOWN:
            default:
                throw raiseUnsupportedLanguageFault(languageAsString);
        }
        return deployed;
    }


    /**
     * go from qname to language enum
     *
     * @param qname
     * @return
     */
    public static int determineLanguage(QName qname) {
        String uri = qname.getNamespaceURI();
        int l = Constants.LANGUAGE_UNKNOWN;
        for (int i = 0; i < Constants.LANGUAGE_NAMESPACES.length; i++) {
            if (Constants.LANGUAGE_NAMESPACES[i].equals(uri)) {
                l = i;
                break;
            }
        }
        return l;
    }

    /**
     * CDL deploymenet
     *
     * @param elt
     * @throws AxisFault
     * @todo implement
     */
    private boolean deployCDL(MessageElement elt) throws AxisFault {
        throw raiseUnsupportedLanguageFault("CDL is unsupported");
    }

    /**
     * ant deployment
     *
     * @param elt
     * @throws AxisFault
     * @throws AxisFault for any problem
     * @todo implement
     */
    private boolean deployAnt(MessageElement elt)
            throws AxisFault {

        throw raiseUnsupportedLanguageFault("Ant is unsupported");
    }


    /**
     * process a smartfrog deployment of type <smartfrog></smartfrog>
     *
     * @throws AxisFault
     */
    private boolean deploySmartFrog(MessageElement descriptorElement)
            throws AxisFault {
        String applicationName = job.getName();
        String version = descriptorElement.getAttributeNS(
                descriptorElement.getNamespaceURI(), "version");
        if (!"1.0".equals(version)) {
            raiseUnsupportedLanguageFault("Unsupported SmartFrog version");
        }

        if (options.isValidateOnly()) {
            //finishing here.
            return false;
        }
        File tempFile = null;

        try {
            String descriptor = descriptorElement.getValue();


            log.info("processing descriptor " + descriptor);
            tempFile = saveStringToFile(descriptor, ".sf");
            String url = tempFile.toURI().toURL().toExternalForm();
            Prim runningJobInstance;
            runningJobInstance =
                    deployThroughSFSystem(null, applicationName, url, null);
            job.bindToPrim(runningJobInstance);
        } catch (IOException e) {
            throw AxisFault.makeFault(e);
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
        return true;
    }

    /**
     * save a string to a file
     *
     * @param descriptor
     * @param extension
     * @return
     * @throws IOException
     */
    private File saveStringToFile(String descriptor, String extension)
            throws IOException {
        File tempFile = File.createTempFile("deploy", extension);
        OutputStream out = null;
        try {
            //save the descriptor to a file
            out = new BufferedOutputStream(new FileOutputStream(tempFile));
            PrintWriter pw = new PrintWriter(out);
            pw.write(descriptor);
            pw.flush();
            pw.close();
            out.close();
            out = null;
            return tempFile;
        } finally {
            closeQuietly(out);
        }

    }


    /**
     * close without complaining
     *
     * @param out output; can be null
     */
    private static void closeQuietly(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * first pass impl of deployment; use sfsystem
     *
     * @param hostname
     * @param application
     * @param url
     * @return
     * @throws AxisFault
     */
    private Prim deployThroughSFSystem(String hostname, String application,
            String url,
            String subprocess) throws AxisFault {
        try {
            ConfigurationDescriptor config = new ConfigurationDescriptor(
                    application, url);
            config.setHost(hostname);
            config.setActionType(ConfigurationDescriptor.Action.DEPLOY);
            if (subprocess != null) {
                config.setSubProcess(subprocess);
            }
            log.info("Deploying " + url + " to " + hostname);
            //deploy, throwing an exception if we cannot
            Object result = config.execute(null);
            if (result instanceof Prim) {
                return (Prim) result;
            } else {
                final String message = "got something not a prim back from a deployer";
                log.info(message);
                throw new AxisFault(message + " " + result.toString());
            }

        } catch (SmartFrogException exception) {
            throw translateSmartFrogException(exception);
        } catch (RemoteException exception) {
            throw AxisFault.makeFault(exception);
        }
    }

}
