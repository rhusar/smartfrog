/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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

import org.apache.axis.types.URI;
import org.smartfrog.services.cddlm.generated.api.types.CallbackListType;
import org.smartfrog.services.cddlm.generated.api.types.LanguageListType;
import org.smartfrog.services.cddlm.generated.api.types.ServerInformationType;
import org.smartfrog.services.cddlm.generated.api.types.StaticServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types._languageListType_language;

import java.math.BigInteger;

/**
 * This is a server instance Date: 10-Aug-2004 Time: 22:13:26
 */
public class ServerInstance {

    /**
     * a private instance
     */
    private static ServerInstance instance;

    private StaticServerStatusType staticStatus;

    private JobRepository jobs;

    public ServerInstance() {
        staticStatus = createStaticStatusInfo();
        jobs = new JobRepository();
    }

    public StaticServerStatusType getStaticServerStatus() {
        return staticStatus;
    }


    private StaticServerStatusType createStaticStatusInfo() {
        ServerInformationType serverInfo = new ServerInformationType();
        serverInfo.setName(Constants.PRODUCT_NAME);
        serverInfo.setHome(Processor.makeURI(Constants.SMARTFROG_HOMEPAGE));
        serverInfo.setDiagnostics(null);
        serverInfo.setBuild(Constants.CVS_INFO);
        serverInfo.setLocation("unknown");
        serverInfo.setTimezoneUTCOffset(new BigInteger("0"));

        //languages
        //creating the array before sending
        _languageListType_language[] list = new _languageListType_language[Constants.LANGUAGES.length /
                3];
        int counter = 0;
        for (int i = 0; i + 2 < Constants.LANGUAGES.length; i += 3) {
            String name = Constants.LANGUAGES[i];
            String version = Constants.LANGUAGES[i + 1];
            URI namespace = Processor.makeURI(Constants.LANGUAGES[i + 2]);
            list[counter++] = new _languageListType_language(name,
                    version,
                    namespace);
        }
        LanguageListType languages = new LanguageListType(list);

        //callbacks are easy
        CallbackListType callbacks = new CallbackListType(Constants.CALLBACKS);

        StaticServerStatusType status;
        status = new StaticServerStatusType(serverInfo,
                languages,
                callbacks);
        return status;
    }

    public JobRepository getJobs() {
        return jobs;
    }

    /**
     * get the current instance; creating it if needed
     *
     * @return
     */
    public static ServerInstance currentInstance() {
        if (instance == null) {
            instance = new ServerInstance();
        }
        return instance;
    }
}
