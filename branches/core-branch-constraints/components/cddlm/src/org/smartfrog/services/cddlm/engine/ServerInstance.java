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


package org.smartfrog.services.cddlm.engine;

import org.apache.axis.AxisFault;
import org.apache.axis.types.URI;
import org.smartfrog.services.cddlm.api.Constants;
import org.smartfrog.services.cddlm.api.Processor;
import org.smartfrog.services.cddlm.cdl.CdlParser;
import org.smartfrog.services.cddlm.cdl.ResourceLoader;
import org.smartfrog.services.cddlm.generated.api.types.LanguageListType;
import org.smartfrog.services.cddlm.generated.api.types.LanguageListTypeLanguage;
import org.smartfrog.services.cddlm.generated.api.types.ServerInformationType;
import org.smartfrog.services.cddlm.generated.api.types.StaticServerStatusType;
import org.smartfrog.services.cddlm.generated.api.types.StringListType;
import org.xml.sax.SAXException;

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

    private ActionQueue queue = new ActionQueue();

    private ActionWorker workers[];

    private CdlParser cdlParser;

    public static final int WORKERS = 1;
    public static final long TIMEOUT = 0;

    /**
     * construct the server. Workers get started too.
     */
    public ServerInstance() {
        staticStatus = createStaticStatusInfo();
        jobs = new JobRepository();
        workers = new ActionWorker[WORKERS];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new ActionWorker(queue, TIMEOUT);
            workers[i].start();
        }
        //TODO: use the smartfrog resource loader & sfCodebase;
        ResourceLoader loader = new ResourceLoader(this.getClass());
        try {
            cdlParser = new CdlParser(loader, true);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * initiate a graceful shutdown of workers. we do this by pushing a shutdown
     * request for every worker
     */
    public void stop() {
        for (int i = 0; i < workers.length; i++) {
            queue.push(new EndWorkerAction());
        }
    }

    public StaticServerStatusType getStaticServerStatus() {
        return staticStatus;
    }


    private StaticServerStatusType createStaticStatusInfo() {
        ServerInformationType serverInfo = new ServerInformationType();
        serverInfo.setName(Constants.PRODUCT_NAME);
        try {
            serverInfo.setHome(Processor.makeURI(Constants.SMARTFROG_HOMEPAGE));
        } catch (AxisFault axisFault) {
            throw new RuntimeException(axisFault);
        }
        serverInfo.setDiagnostics(null);
        serverInfo.setBuild(Constants.CVS_INFO);
        serverInfo.setLocation("unknown");
        serverInfo.setTimezoneUTCOffset(new BigInteger("0"));

        //languages
        //creating the array before sending
        LanguageListTypeLanguage[] list = new LanguageListTypeLanguage[Constants.LANGUAGES.length /
                3];
        int counter = 0;
        for (int i = 0; i + 2 < Constants.LANGUAGES.length; i += 3) {
            String name = Constants.LANGUAGES[i];
            String version = Constants.LANGUAGES[i + 1];
            URI namespace = null;
            try {
                namespace = Processor.makeURI(Constants.LANGUAGES[i + 2]);
            } catch (AxisFault axisFault) {
                throw new RuntimeException(axisFault);

            }
            list[counter++] = new LanguageListTypeLanguage(name,
                    version,
                    namespace);
        }
        LanguageListType languages = new LanguageListType(list);

        //callbacks are easy
        StringListType callbacks = new StringListType(Constants.CALLBACKS);
        StringListType options = new StringListType(
                Constants.SUPPORTED_OPTIONS);

        StaticServerStatusType status;
        status = new StaticServerStatusType(serverInfo,
                languages,
                callbacks,
                options);
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

    /**
     * get our CDL parser
     *
     * @return
     */
    public CdlParser getCdlParser() {
        return cdlParser;
    }

    /**
     * queue an action for execution
     *
     * @param action
     */
    public void queue(Action action) {
        queue.push(action);
    }

}
