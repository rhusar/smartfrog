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
 * created Aug 4, 2004 3:58:37 PM
 */

public class DeployProcessor extends Processor {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(DeployProcessor.class);
    private static final String WRONG_MESSAGE_ELEMENT_COUNT = "wrong number of message elements";
    private static final String UNSUPPORTED_LANGUAGE = "Unsupported language";

    public DeployProcessor(SmartFrogHostedEndpoint owner) {
        super(owner);
    }

    public _deployResponse deploy(_deployRequest deploy) throws AxisFault {
        DeploymentDescriptorType dd = deploy.getDescriptor();
        URI source = dd.getSource();
        String applicationName = deploy.getName().toString();
        if ( source != null ) {
            try {
                URL url = makeURL(source);
                deployThroughSFSystem(null,
                        applicationName,
                        source.toString(),
                        null);
            } catch (MalformedURLException e) {
                throw AxisFault.makeFault(e);
            }
        } else {
            //here we deploy inline
            deploySmartFrog(deploy);

        }
        //create a new jobstate
        JobState jobState=new JobState(deploy);
        URI applicationReference = jobState.getUri();
        //add it to the store
        ServerInstance.currentInstance().getJobs().add(jobState);
        _deployResponse response = new _deployResponse(applicationReference);
        return response;

    }

    /**
     * process a smartfrog deployment
     *
     * @param deploy
     * @throws AxisFault
     */
    public void determineLanguageAndDeploy(_deployRequest deploy) throws AxisFault {
        DeploymentDescriptorType descriptor= deploy.getDescriptor();
        MessageElement[] messageElements=descriptor.getData().get_any();
        if(messageElements.length!=1) {
            throw raiseBadArgumentFault(WRONG_MESSAGE_ELEMENT_COUNT);
        }

        MessageElement elt= messageElements[0];
        elt.getNamespaceURI();
        QName qname=elt.getQName();
        int lan=determineLanguage(qname);
        if(lan==Constants.LANGUAGE_UNKNOWN) {
            throw raiseBadArgumentFault(UNSUPPORTED_LANGUAGE);
        }
        switch(lan) {
            case Constants.LANGUAGE_UNKNOWN:
                throw raiseBadArgumentFault(UNSUPPORTED_LANGUAGE);
            case Constants.LANGUAGE_SMARTFROG:
                throw raiseBadArgumentFault(UNSUPPORTED_LANGUAGE);
            case Constants.LANGUAGE_XML_CDL:
                throw raiseBadArgumentFault(UNSUPPORTED_LANGUAGE);
                //break;
            default:
                throw raiseBadArgumentFault(UNSUPPORTED_LANGUAGE);
        }


    }

    private AxisFault raiseBadArgumentFault(String message) {
        return raiseFault(Constants.CDDLM_BAD_ARGUMENT, message);
    }

    private URL makeURL(URI source) throws MalformedURLException {
        return new URL(source.toString());
    }

    public static int determineLanguage( QName qname) {
        String uri=qname.getNamespaceURI();
        int l=Constants.LANGUAGE_UNKNOWN;
        for(int i=0;i<Constants.LANGUAGE_NAMESPACES.length;i++ ) {
            if(Constants.LANGUAGE_NAMESPACES[i].equals(uri)) {
                l=i;
                break;
            }
        }
        return l;
    }

    /**
     * process a smartfrog deployment
     * @param deploy
     * @throws AxisFault
     */
    public void deploySmartFrog(_deployRequest deploy)
            throws AxisFault {
        try {
            String applicationName = deploy.getName().toString();
            DeploymentDescriptorType dd = deploy.getDescriptor();
            _deploymentDescriptorType_data data = dd.getData();
            String descriptor = data.get_any().toString();
            log.info("processing descriptor " + descriptor);
            File tempFile = saveStringToFile(descriptor, ".sf");
            String url = tempFile.toURI().toURL().toExternalForm();
            deployThroughSFSystem(null, applicationName, url, null);
        } catch (IOException e) {
            throw AxisFault.makeFault(e);
        }
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
