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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.axis.types.URI;
import org.apache.axis.AxisFault;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.generated.api.types._deployResponse;
import org.smartfrog.services.cddlm.generated.api.types._deployRequest;
import org.smartfrog.services.cddlm.generated.api.types.DeploymentDescriptorType;
import org.smartfrog.services.cddlm.generated.api.types.UnboundedXMLOtherNamespace;
import org.smartfrog.services.cddlm.generated.api.types._deploymentDescriptorType_data;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.SFSystem;

import java.io.File;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * created Aug 4, 2004 3:58:37 PM
 */

public class DeployProcessor extends Processor {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(DeployProcessor.class);

    public DeployProcessor(SmartFrogHostedEndpoint owner) {
        super(owner);
    }

    public _deployResponse deploy(_deployRequest deploy) throws AxisFault {
        DeploymentDescriptorType dd = deploy.getDescriptor();
        URI source=dd.getSource();
        String applicationName = deploy.getName().toString();
        URI applicationReference=makeURIFromApplication(applicationName);
        _deployResponse response=new _deployResponse(applicationReference);
        if(source!=null) {
            deployThroughSFSystem(null, applicationName, source.toString(), null);
            return response;
        }
    }


    public _deployResponse deploySmartFrog(_deployRequest deploy)
            throws AxisFault {
        String applicationName = deploy.getName().toString();
        DeploymentDescriptorType dd = deploy.getDescriptor();
        _deploymentDescriptorType_data data=dd.getData();
        data.get_any()
        String descriptor=data.get_any()
        File tempFile=saveStringToFile(descriptor,".sf");
        String url = tempFile.toURI().toURL().toExternalForm();
        deployThroughSFSystem(null,deploy.getName().toString(), url,null);

    }

    private File saveStringToFile(String descriptor,String extension) throws IOException {
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
     * @param out output; can be null
     */
    private void close(OutputStream out) {
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
    private String deployThroughSFSystem(String hostname, String application,
                                         String url,
                                         String subprocess) throws AxisFault {
        try {
            ConfigurationDescriptor deploy = new ConfigurationDescriptor(application, url);
            deploy.setHost(hostname);
            deploy.setActionType(ConfigurationDescriptor.Action.DEPLOY);
            if (subprocess != null) {
                deploy.setSubProcess(subprocess);
            }
            log.info("Deploying " + url + " to " + hostname);
            //deploy, throwing an exception if we cannot
            deploy.execute(SFProcess.getProcessCompound());
            SFSystem.runConfigurationDescriptor(deploy, true);

            //SFSystem.deployAComponent(hostname,url,application,remote);
            return "urn://" + hostname + "/" + application;
        } catch (SmartFrogException exception) {
            throw AxisFault.makeFault(exception);
        } catch (Exception exception) {
            throw AxisFault.makeFault(exception);
        }
    }
}
