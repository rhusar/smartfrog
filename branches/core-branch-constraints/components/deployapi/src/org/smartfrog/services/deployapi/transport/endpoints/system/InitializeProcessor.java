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

import nu.xom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.services.cddlm.cdl.base.LifecycleStateEnum;
import org.smartfrog.services.deployapi.binding.DescriptorHelper;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.engine.ServerInstance;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.DeploymentLanguage;
import org.smartfrog.services.deployapi.transport.endpoints.alpine.WsrfHandler;

import java.io.File;
import java.io.IOException;

/**
 * This class is *NOT* re-entrant. Create one for each deployment. created Aug
 * 4, 2004 3:58:37 PM
 */

public class InitializeProcessor extends SystemProcessor {
    /** log */
    private static final Log log = LogFactory.getLog(InitializeProcessor.class);

    private OptionProcessor options;
    public static final String ERROR_NO_DESCRIPTOR = "No descriptor element";
    private Element request;
    DescriptorHelper helper;


    public InitializeProcessor(WsrfHandler owner) {
        super(owner);
        helper = ServerInstance.currentInstance().getDescriptorHelper();
    }

    public Element process(SoapElement request) throws IOException {
        jobMustExist();
        Element rootElement = request;
        helper.validateRequest(rootElement);
        Element response = initialize(rootElement);
        return response;
    }

    public OptionProcessor getOptions() {
        return options;
    }

    /** deployment */
    public Element initialize(Element requestIn) {
        this.request = requestIn;
        //get the options out the way
        options = new OptionProcessor(getOwner());

        job.bind(requestIn, options);
        SoapElement optionSet= (SoapElement) requestIn.getFirstChildElement("options",
                Constants.CDL_API_TYPES_NAMESPACE);
        options.process(optionSet);

        boolean deployed = false;

        //here we deploy inline
        try {
            deployed = determineLanguageAndDeploy();
        } catch (Exception e) {
            job.enterFailedState(e.toString());
            throw translateException(e);
        }
/*
        if (deployed) {
            job.enterStateNotifying(LifecycleStateEnum.running, null);
        }*/
        Element response = XomHelper.apiElement("initializeResponse");
        return response;
    }

    /** process a smartfrog deployment */
    private  boolean determineLanguageAndDeploy() throws IOException {

        DeploymentLanguage language = job.getLanguage();
        File file=null;
        switch (language) {
            case smartfrog:
                file = deploySmartFrog();
                break;
            case cdl:
                file = deployCDL();
                break;
            case unknown:
            default:
                throw raiseUnsupportedLanguageFault(language.getNamespace());
        }
        if (options.isValidateOnly()) {
            //finishing here.
            return false;
        }
        job.deployApplication(file, language);
        return true;
    }


    /** process a smartfrog deployment of type smartfrog XML */
    private File deploySmartFrog() throws IOException {
        File file = helper.saveInlineSmartFrog(request);
        file.deleteOnExit();
        return file;

    }

    /** process a smartfrog deployment of type smartfrog CDL
     * @return a file with the right extension for the language 
     *  */
    private File deployCDL() throws IOException {
        return helper.extractBodyToFile(request, job.getExtension());
    }

}


