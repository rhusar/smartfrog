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
package org.smartfrog.services.cddlm;

import org.apache.axis.AxisFault;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;
import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.rmi.RemoteException;

/**
 * This is our SOAP service
 * created 04-Mar-2004 13:44:57
 */

public class DeploymentEndpoint extends SmartfrogHostedEndpoint {


    /**
     * our log
     */
    private Log log = LogFactory.getLog(this.getClass().getName());


    protected final static String[] languages = {
        "SmartFrog",
        "CDDLM-XML"
    };

    /**
     * list our languages
     *
     * @return
     */
    public String[] listLanguages() {
        return languages;
    }


    /**
     * verify we support this language
     *
     * @param language
     * @throws AxisFault
     */
    protected void verifySupported(String language) throws AxisFault {
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equalsIgnoreCase(language)) {
                return;
            }
        }
        throw new AxisFault("Unsupported language :" + language);
    }

    /**
     * deploy a file; save it to a temporary location and then deploy it
     *
     * @param language
     * @param application
     * @param data
     * @throws AxisFault
     */
    public void deploy(String language, String hostname, String application,
                       String data) throws AxisFault {
        verifySupported(language);
        throw new AxisFault("Not yet implemented");
    }

    /**
     * deploy a named resource
     *
     * @param language
     * @param hostname
     * @param application
     * @param url
     * @return
     * @throws RemoteException
     */
    public String deployURL(String language, String hostname,
                            String application, String url, String[] codebase)
            throws RemoteException {
        verifySupported(language);
        if (hostname.length() == 0) {
            hostname = "localhost";
        }

        return deployThroughActions(hostname, application, url, null);
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
                                         String subprocess
                                         ) throws AxisFault {
        try {
            ConfigurationDescriptor deploy=new ConfigurationDescriptor(application,url);
            deploy.setHost(hostname);
            deploy.setActionType(ConfigurationDescriptor.Action.DEPLOY);
            if(subprocess!=null) {
                deploy.setSubProcess(subprocess);
            }
            log.info("Deploying " + url + " to " + hostname);
            //deploy, throwing an exception if we cannot
            deploy.execute(SFProcess.getProcessCompound());
            SFSystem.runConfigurationDescriptor(deploy,true);

            //SFSystem.deployAComponent(hostname,url,application,remote);
            return "urn://" + hostname + "/" + application;
        } catch (SmartFrogException exception) {
            throw AxisFault.makeFault(exception);
        } catch (Exception exception) {
            throw AxisFault.makeFault(exception);
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
    private String deployThroughActions(String hostname, String application,
                                        String url,
                                        String[] codebase) throws AxisFault {
        try {
            ConfigurationDescriptor config = new ConfigurationDescriptor(
                    application, url);
            config.setHost(hostname);
            config.setActionType(ConfigurationDescriptor.Action.DEPLOY);
            log.info("Deploying " + url + " to " + hostname + "as " + application);
            if (codebase != null) {
                log.warn("codebase is not yet supported");
            }
            //deploy, throwing an exception if we cannot
            config.execute(SFProcess.getProcessCompound());
            Object targetC = config.execute(null);
            return targetC.toString();
            //return "urn://" + hostname + "/" + application;
        } catch (SmartFrogException exception) {
            throw AxisFault.makeFault(exception);
        } catch (Exception exception) {
            throw AxisFault.makeFault(exception);
        }
    }

    /**
     * get the root process compound of this process
     *
     * @return
     */
    protected ProcessCompound getRootProcessCompount() {
        return SFProcess.getProcessCompound();
    }

    /**
     * undeploy an application
     *
     * @param hostname
     * @param application
     */
    public void undeploy(String hostname, String application) throws AxisFault {

        try {
            ConfigurationDescriptor config = new ConfigurationDescriptor();
            config.setHost(hostname);
            config.setActionType(ConfigurationDescriptor.Action.DETaTERM);
            log.info("Undeploying " + application + " on " + hostname);
            //deploy, throwing an exception if we cannot
            config.execute(SFProcess.getProcessCompound());
            Object targetC = config.execute(null);
        } catch (SmartFrogException exception) {
            throw AxisFault.makeFault(exception);
        } catch (Exception exception) {
            throw AxisFault.makeFault(exception);
        }

    }


}