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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.AxisFault;
import org.ggf.xbeans.cddlm.api.InitializeRequestDocument;
import org.ggf.xbeans.cddlm.api.InitializeResponseDocument;
import org.ggf.xbeans.cddlm.api.TerminateResponseDocument;
import org.ggf.xbeans.cddlm.api.TerminateRequestDocument;
import org.ggf.xbeans.cddlm.api.PingRequestDocument;
import org.ggf.xbeans.cddlm.api.PingResponseDocument;
import org.ggf.xbeans.cddlm.api.RunRequestDocument;
import org.ggf.xbeans.cddlm.api.RunResponseDocument;
import org.ggf.xbeans.cddlm.smartfrog.SmartFrogDeploymentDescriptorType;
import org.smartfrog.services.deployapi.engine.Job;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.endpoints.Processor;
import org.smartfrog.services.deployapi.transport.endpoints.XmlBeansEndpoint;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.binding.bindings.InitializeBinding;
import org.smartfrog.services.deployapi.binding.bindings.TerminateBinding;
import org.smartfrog.services.deployapi.binding.bindings.PingBinding;
import org.smartfrog.services.deployapi.binding.bindings.RunBinding;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.prim.Prim;

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

public class RunProcessor extends SystemProcessor {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(RunProcessor.class);


    public RunProcessor(XmlBeansEndpoint owner) {
        super(owner);
    }

    public OMElement process(OMElement request) throws RemoteException {
        jobMustExist();

        RunBinding binding = new RunBinding();
        RunRequestDocument doc = binding.convertRequest(request);
        Utils.maybeValidate(doc);


        RunResponseDocument responseDoc;
        responseDoc=job.run();
        OMElement responseOM = binding.convertResponse(responseDoc);
        return responseOM;
    }


}
