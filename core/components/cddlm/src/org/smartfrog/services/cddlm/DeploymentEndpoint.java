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
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**
 * This is our SOAP service
 * @author steve loughran
 *         created 04-Mar-2004 13:44:57
 */

public class DeploymentEndpoint extends SmartfrogHostedEndpoint {


    /**
     * our log
     */
    private Log log = LogFactory.getLog(this.getClass().getName());


    protected final static String[] languages= {
        "SmartFrog"
    };
    /**
     * list our languages
     * @return
     */
    public String[] listLanguages() {
        return languages;
    }


    /**
     * verify we support this language
     * @param language
     * @throws AxisFault
     */
    protected void verifySupported(String language) throws AxisFault {
        if(language.length()>0 && !languages[0].equalsIgnoreCase(language)) {
            throw new AxisFault("Unsupported Language :"+language);
        }
    }

    /**
     * deploy a file; save it to a temporary location and then deploy it
     * @param language
     * @param application
     * @param data
     * @throws AxisFault
     */
    public void deploy(String language, String hostname, String application, String data) throws AxisFault {
        verifySupported(language);
        throw new AxisFault("Not yet implemented");
    }

    /**
     * deploy a named resource
     * @param language
     * @param application
     * @param url
     * @throws AxisFault
     */
    public String deployURL(String language, String hostname, String application, String url) throws RemoteException
            {
        verifySupported(language);
        if(hostname.length() == 0) {
            hostname="localhost";
        }
        boolean remote= !"localhost".equalsIgnoreCase(hostname) ;
        try {
            log.info("Deploying "+url+" to "+hostname);
            SFSystem.deployAComponent(hostname,url,application,remote);
            return "urn://"+ application;
        } catch (SmartFrogException exception) {
            throw AxisFault.makeFault(exception);
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }


    /**
     * undeploy an application
     * @param hostname
     * @param application
     */
    public void undeploy(String hostname, String application) throws AxisFault {
        /*
        TerminationRecord tr = new TerminationRecord(TerminationRecord.NORMAL,
                "force to terminate", null);
        Prim obj = ((Prim) obj.sfResolveHere(token));
        obj.sfTerminate(tr);
        */
        try {
            Reference ownerName = null;
            try {
                ownerName = (((Prim) owner).sfCompleteName());
            } catch (Exception ex) {
                //ignore  //TODO: Check
            }
            try {
                Prim appToUndeploy = (Prim) owner.sfResolveHere(application);
                appToUndeploy.
                        sfTerminate(new TerminationRecord("normal",
                                "External Management Action", ownerName));
            } catch (ClassCastException cce) {
                try {
                    if (application.equals("rootProcess")) {
                        ((Prim) owner.sfResolve((Reference) owner.
                                sfResolveHere(application))).
                                sfTerminate(new TerminationRecord("normal",
                                        "External Management Action", ownerName));
                    }
                } catch (Exception ex) {
                    //TODO: Check exception handling
                    if ((ex.getCause() instanceof java.net.SocketException) ||
                            (ex.getCause() instanceof java.io.EOFException)) {
                        Logger.log(MessageUtil.formatMessage(MessageKeys.MSG_SF_TERMINATED));
                    } else {
                        Logger.log(ex);
                    }
                }
            }
        } catch (RemoteException e) {
            throw AxisFault.makeFault(e);
        } catch (SmartFrogResolutionException e) {
            throw AxisFault.makeFault(e);

        }

    }

}