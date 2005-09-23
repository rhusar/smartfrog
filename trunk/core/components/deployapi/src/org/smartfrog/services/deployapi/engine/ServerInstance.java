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


package org.smartfrog.services.deployapi.engine;

import org.ggf.xbeans.cddlm.api.StaticPortalStatusType;
import org.ggf.xbeans.cddlm.api.PortalInformationType;
import org.ggf.xbeans.cddlm.api.NameUriListType;
import org.ggf.xbeans.cddlm.api.UriListType;
import org.ggf.xbeans.cddlm.wsrf.muws.p1.IdentityPropertiesType;
import org.ggf.cddlm.utils.QualifiedName;
import org.smartfrog.services.deployapi.transport.wsrf.WSRPResourceSource;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.system.Constants;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlString;

import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.util.Date;
import java.net.URL;
import java.net.MalformedURLException;


/**
 * This is a server instance Date: 10-Aug-2004 Time: 22:13:26
 */
public class ServerInstance implements WSRPResourceSource {

    /**
     * a private instance
     */
    private static ServerInstance instance;

    private String resourceID = Utils.createNewID();

    private StaticPortalStatusType staticStatus;

    private JobRepository jobs;

    private ActionQueue queue = new ActionQueue();

    private ActionWorker workers[];

    //private CdlParser cdlParser;

    public static final int WORKERS = 1;
    public static final long TIMEOUT = 0;

    /**
     * construct the server. Workers get started too.
     */
    public ServerInstance() {
        staticStatus = createStaticStatusInfo();

        try {
            URL systemsURL = new URL("http://127.0.0.1:5050/services/System/");
            jobs = new JobRepository(systemsURL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        workers = new ActionWorker[WORKERS];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new ActionWorker(queue, TIMEOUT);
            workers[i].start();
        }
        /*
        //TODO: use the smartfrog resource loader & sfCodebase;
        ResourceLoader loader = new ResourceLoader(this.getClass());
        try {
            cdlParser = new CdlParser(loader, true);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        */
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

    public StaticPortalStatusType getStaticServerStatus() {
        return staticStatus;
    }


    private StaticPortalStatusType createStaticStatusInfo() {
        StaticPortalStatusType status = StaticPortalStatusType.Factory.newInstance();
        PortalInformationType portalInfo = status.addNewPortal();
        portalInfo.setName("SmartFrog CDDLM Implementation");
        portalInfo.setBuild("$Date$");
        portalInfo.setLocation("unknown");
        portalInfo.setHome("http://smartfrog.org/");
        Date now=new Date();
        BigInteger tzoffset=BigInteger.valueOf(now.getTimezoneOffset());
        portalInfo.setTimezoneUTCOffset(tzoffset);
        NameUriListType jobLanguages = status.addNewJoblanguages();
        NameUriListType.Item item = jobLanguages.addNewItem();
        item.setName("CDL");
        item.setUri(Constants.XML_CDL_NAMESPACE);
        item = jobLanguages.addNewItem();
        item.setName("SmartFrog");
        item.setUri(Constants.SMARTFROG_NAMESPACE);
        UriListType notifications = status.addNewNotifications();
        notifications.addNewItem().setStringValue(Constants.WSRF_WSNT_NAMESPACE);
        UriListType options = status.addNewOptions();

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
     * queue an action for execution
     *
     * @param action
     */
    public void queue(Action action) {
        queue.push(action);
    }

    /**
     * Get a resource
     *
     * @param resource
     * @return null for no match;
     * @throws BaseException if they feel like it
     */
    public XmlObject getResource(QName resource) {
        QualifiedName query= Utils.convert(resource);
        if(Constants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS.equals(query)) {
            return staticStatus;
        }
        if (Constants.PROPERTY_MUWS_RESOURCEID.equals(query)) {
            IdentityPropertiesType identity=IdentityPropertiesType.Factory.newInstance();
            identity.setResourceId(resourceID);
            return identity;
        }
        return null;
    }
}
