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
import org.smartfrog.SFSystem;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorType;
import org.smartfrog.services.cddlm.generated.api.types._deployRequest;
import org.smartfrog.services.cddlm.generated.api.types._deployResponse;
import org.smartfrog.services.cddlm.generated.api.types._deploymentDescriptorType_data;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class is *NOT* re-entrant. Create one for each deployment.
 * created Aug 4, 2004 3:58:37 PM
 */

public class DeployProcessor extends Processor {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(DeployProcessor.class);
    private static final String WRONG_MESSAGE_ELEMENT_COUNT = "wrong number of message elements";
    private static final String UNSUPPORTED_LANGUAGE = "Unsupported language";

    private _deployRequest request;
    private OptionProcessor options;

    public DeployProcessor(SmartFrogHostedEndpoint owner) {
        super(owner);
    }

    public _deployRequest getRequest() {
        return request;
    }

    public OptionProcessor getOptions() {
        return options;
    }

    public _deployResponse deploy(_deployRequest deploy) throws AxisFault {
        request=deploy;
        //get the options out the way
        options = new OptionProcessor(getOwner());
        options.process(deploy.getOptions());
        DeploymentDescriptorType dd = deploy.getDescriptor();
        URI source = dd.getSource();
        String applicationName = deploy.getName().toString();
        //create a new jobstate
        JobState jobState = new JobState(deploy);
        //this is our URI
        URI applicationReference = jobState.getUri();

        if ( source != null ) {
            throwNotImplemented();
        } else {
            //here we deploy inline
            determineLanguageAndDeploy();

        }
        //add the job state to the store
        ServerInstance.currentInstance().getJobs().add(jobState);
        _deployResponse response = new _deployResponse(applicationReference);
        return response;
    }

    /**
     * process a smartfrog deployment
     *
     * @throws AxisFault
     */
    public void determineLanguageAndDeploy() throws AxisFault {
        DeploymentDescriptorType descriptor= request.getDescriptor();
        MessageElement[] messageElements=descriptor.getData().get_any();
        if(messageElements.length!=1) {
            throw raiseBadArgumentFault(WRONG_MESSAGE_ELEMENT_COUNT);
        }

        MessageElement descriptorData= messageElements[0];
        descriptorData.getNamespaceURI();
        QName qname=descriptorData.getQName();
        int lan=determineLanguage(qname);
        switch(lan) {
            case Constants.LANGUAGE_UNKNOWN:
                throw raiseUnsupportedLanguageFault(UNSUPPORTED_LANGUAGE);
            case Constants.LANGUAGE_SMARTFROG:
                throw raiseUnsupportedLanguageFault(UNSUPPORTED_LANGUAGE);
            case Constants.LANGUAGE_XML_CDL:
                deployCDL(descriptorData);
                break;
            case Constants.LANGUAGE_ANT:
                deployAnt(descriptorData);
                break;
            default:
                throw raiseUnsupportedLanguageFault(UNSUPPORTED_LANGUAGE);
        }


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
    private void deployCDL(MessageElement elt) throws AxisFault {
        throw raiseUnsupportedLanguageFault(UNSUPPORTED_LANGUAGE);
    }

    /**
     * ant deployment
     * @todo
     * @param elt
     * @throws AxisFault
     */
    private void deployAnt( MessageElement elt)
            throws AxisFault {

        throw raiseUnsupportedLanguageFault(UNSUPPORTED_LANGUAGE);
    }



    /**
     * process a smartfrog deployment of type <smartfrog></smartfrog>
     * @throws AxisFault
     */
    private void deploySmartFrog(MessageElement descriptorElement) throws AxisFault {
        descriptorElement.getValue();

        throwNotImplemented();

/*

        try {
            String applicationName = request.getName().toString();
            DeploymentDescriptorType dd = request.getDescriptor();
            _deploymentDescriptorType_data data = dd.getData();
            String descriptor = data.get_any().toString();


            log.info("processing descriptor " + descriptor);
            File tempFile = saveStringToFile(descriptor, ".sf");
            String url = tempFile.toURI().toURL().toExternalForm();
            deployThroughSFSystem(null, applicationName, url, null);
        } catch (IOException e) {
            throw AxisFault.makeFault(e);
        }
        */
    }

    /**
     * save a string to a file
     * @param descriptor
     * @param extension
     * @return
     * @throws IOException
     */
    private File saveStringToFile(String descriptor, String extension) throws IOException {
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
            close(out);
        }

    }


    /**
     * close without complaining
     *
     * @param out output; can be null
     */
    private void close(OutputStream out) {
        if ( out != null ) {
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
    private void deployThroughSFSystem(String hostname, String application,
                                         String url,
                                         String subprocess) throws AxisFault {
        try {
            ConfigurationDescriptor config = new ConfigurationDescriptor(application, url);
            config.setHost(hostname);
            config.setActionType(ConfigurationDescriptor.Action.DEPLOY);
            if ( subprocess != null ) {
                config.setSubProcess(subprocess);
            }
            log.info("Deploying " + url + " to " + hostname);
            //deploy, throwing an exception if we cannot
            config.execute(SFProcess.getProcessCompound());
            SFSystem.runConfigurationDescriptor(config, true);

        } catch (SmartFrogException exception) {
            throw AxisFault.makeFault(exception);
        } catch (Exception exception) {
            throw AxisFault.makeFault(exception);
        }
    }
}
