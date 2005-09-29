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
package org.smartfrog.services.deployapi.transport.endpoints.system;

import org.apache.axis2.AxisFault;
import org.apache.axis2.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.ggf.xbeans.cddlm.api.InitializeRequestDocument;
import org.ggf.xbeans.cddlm.api.InitializeResponseDocument;
import org.ggf.xbeans.cddlm.smartfrog.SmartFrogDeploymentDescriptorType;
import org.smartfrog.services.deployapi.binding.bindings.InitializeBinding;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.endpoints.XmlBeansEndpoint;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.prim.Prim;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * This class is *NOT* re-entrant. Create one for each deployment. created Aug
 * 4, 2004 3:58:37 PM
 */

public class InitializeProcessor extends SystemProcessor {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(InitializeProcessor.class);

    private OptionProcessor options;
    public static final String ERROR_NO_DESCRIPTOR = "No descriptor element";
    private File descriptorFile;

    public InitializeProcessor(XmlBeansEndpoint owner) {
        super(owner);
    }

    public OMElement process(OMElement request) throws AxisFault {
        jobMustExist();
        InitializeBinding binding = new InitializeBinding();
        InitializeRequestDocument doc = binding.convertRequest(request);
        Utils.maybeValidate(doc);
        InitializeResponseDocument responseDoc;
        responseDoc = initialize(doc.getInitializeRequest());
        OMElement responseOM = binding.convertResponse(responseDoc);
        return responseOM;
    }

    public OptionProcessor getOptions() {
        return options;
    }

    /**
     * deployment
     */
    public InitializeResponseDocument initialize(InitializeRequestDocument.InitializeRequest request) {

        //get the options out the way
        options = new OptionProcessor(getOwner());

        job.bind(request, options);
        options.process(request.getOptions());


        boolean deployed = false;

        //here we deploy inline
        try {
            deployed = determineLanguageAndDeploy();
        } catch (Exception e) {
            job.enterFailedState(e.toString());
            throw translateException(e);
        }

        if (deployed) {
            job.enterStateNotifying(Constants.LifecycleStateEnum.running, null);
        }
        InitializeResponseDocument response = InitializeResponseDocument.Factory.newInstance();
        return response;
    }

    /**
     * process a smartfrog deployment
     *
     */
    public boolean determineLanguageAndDeploy() throws IOException, XmlException {
        Constants.DeploymentLanguage language = job.getLanguage();
        boolean deployed = false;
        switch (language) {
            case smartfrog:
                deployed = deploySmartFrog();
                break;
            case cdl:
/*
                deployed = deployCDL();
                break;
*/
            case unknown:
            default:
                throw raiseUnsupportedLanguageFault(language.getNamespace());
        }
        return deployed;
    }

    /**
     * CDL deploymenet
     *
     * @
     * @todo implement
     */
/*    private boolean deployCDL()  {
        MessageElement descriptorElement = job.getDescriptorBody();
        if (descriptorElement == null) {
            throw raiseBadArgumentFault(ERROR_NO_DESCRIPTOR);
        }
        CdlDocument document;
        try {
            CdlParser parser = ServerInstance.currentInstance()
                    .getCdlParser();
            assert parser != null;
            document = parser.parseMessageElement(descriptorElement);
        } catch (Exception e) {
            throw translateException(e);
        }
        if (options.isValidateOnly()) {
            //finishing here.
            return false;
        }
        //deploy but do nothing with it.
        job.setCdlDocument(document);
        return true;
    }*/


    /**
     * process a smartfrog deployment of type <smartfrog></smartfrog>
     *
     * @
     */
    private boolean deploySmartFrog() throws IOException, XmlException {
        descriptorFile = job.getDescriptorFile();
        SmartFrogDeploymentDescriptorType sfxml = SmartFrogDeploymentDescriptorType.Factory.parse(descriptorFile);

        String applicationName = job.getName();
        String version = sfxml.getVersion();
        if (!Constants.SMARTFROG_XML_VERSION.equals(version)) {
            raiseUnsupportedLanguageFault("Unsupported SmartFrog version");
        }

        if (options.isValidateOnly()) {
            //finishing here.
            return false;
        }
        File tempFile = null;

        try {
            String descriptor = sfxml.getStringValue();
            log.info("processing descriptor " + descriptor);
            tempFile = saveStringToFile(descriptor, ".sf");
            String url = tempFile.toURI().toURL().toExternalForm();
            Prim runningJobInstance;
            runningJobInstance =
                    deployThroughSFSystem(null, applicationName, url, null);
            job.bindToPrim(runningJobInstance);
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
     * @throws java.io.IOException
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
     * @
     */
    private Prim deployThroughSFSystem(String hostname, String application,
                                       String url,
                                       String subprocess) {
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
                throw new BaseException(message + " " + result.toString());
            }

        } catch (Exception exception) {
            throw translateException(exception);
        }
    }

}
