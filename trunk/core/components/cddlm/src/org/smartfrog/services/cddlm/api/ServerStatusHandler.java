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

import org.smartfrog.services.cddlm.generated.api.types.ServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types._serverStatusRequest;
import org.smartfrog.services.cddlm.generated.api.types.ServerInformationType;
import org.smartfrog.services.cddlm.generated.api.types.LanguageListType;
import org.smartfrog.services.cddlm.generated.api.types.StaticServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types.DynamicServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types._languageListType_language;
import org.smartfrog.services.cddlm.generated.api.types.CallbackListType;
import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;

/**
 * created Aug 4, 2004 10:15:34 AM
 */

public class ServerStatusHandler {
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * generate the server status information
     * @param serverStatus
     * @return
     * @throws RemoteException
     */
    public ServerStatusType serverStatus(_serverStatusRequest serverStatus)
            throws RemoteException {
        ServerInformationType serverInfo = new ServerInformationType();
        serverInfo.setName(Constants.PRODUCT_NAME);
        serverInfo.setHome(EndpointHelper.makeURI(Constants.SMARTFROG_HOMEPAGE));
        serverInfo.setDiagnostics(null);
        serverInfo.setBuild(getBuildInfo());

        //languages
        //creating the array before sending
        _languageListType_language[] list=new _languageListType_language[Constants.LANGUAGES.length/3];
        int counter=0;
        for(int i=0;i+2<Constants.LANGUAGES.length;i+=3) {
            String name = Constants.LANGUAGES[i];
            String version = Constants.LANGUAGES[i+1];
            URI namespace = EndpointHelper.makeURI(Constants.LANGUAGES[i+2]);
            list[counter++]=new _languageListType_language(name,
                    version,
                    namespace);
        }
        LanguageListType languages = new LanguageListType(list);

        //callbacks are easy
        CallbackListType callbacks=new CallbackListType(Constants.CALLBACKS);

        StaticServerStatusType staticStatus;
        staticStatus = new StaticServerStatusType(serverInfo,
                languages,
                callbacks);


        DynamicServerStatusType dynamicStatus = new DynamicServerStatusType();
        ServerStatusType status = new ServerStatusType(staticStatus,
                dynamicStatus);

        return status;
    }

    private static String getBuildInfo() {
        return Constants.CVS_INFO;
    }
}
