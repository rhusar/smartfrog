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
import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * This is our SOAP service
 * @author steve loughran
 *         created 04-Mar-2004 13:44:57
 */

public class DeploymentEndpoint {


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
     * @param appname
     * @param data
     * @throws AxisFault
     */
    public void deploy(String language, String hostname, String appname, String data) throws AxisFault {
        verifySupported(language);
        throw new AxisFault("Not yet implemented");
    }

    /**
     * deploy a named resource
     * @param language
     * @param appname
     * @param url
     * @throws AxisFault
     */
    public void deployURL(String language, String hostname, String appname, String url) throws RemoteException
            {
        verifySupported(language);
        boolean remote= hostname.length() != 0 && !"localhost".equalsIgnoreCase(hostname) ;
        try {
            SFSystem.deployAComponent(hostname,url,appname,remote);
        } catch (SmartFrogException exception) {
            AxisFault.makeFault(exception);
        }
    }


}


