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

import nu.xom.Attribute;
import nu.xom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.services.deployapi.binding.DescriptorHelper;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.components.DeploymentServer;
import org.smartfrog.services.deployapi.notifications.EventSubscriberManager;
import org.smartfrog.services.deployapi.notifications.SubscriptionServiceStore;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.wsrf.NotificationSubscription;
import org.smartfrog.services.deployapi.transport.wsrf.PropertyMap;
import org.smartfrog.services.deployapi.transport.wsrf.WSRPResourceSource;
import org.smartfrog.services.deployapi.transport.wsrf.WsrfUtils;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.filesystem.filestore.AddedFilestore;
import org.smartfrog.services.xml.java5.Xom5Utils;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * This is a server instance Date: 10-Aug-2004 Time: 22:13:26
 */
public class ServerInstance implements WSRPResourceSource {

    /**
     * a private instance
     */
    private static ServerInstance instance;

    private String resourceID = Utils.createNewID();

    private PropertyMap properties;

    private JobRepository jobs;

    private AddedFilestore filestore;


    //keep all portal subscriptions
    //private EventSubscriberManager subscriptions;

    //weak reference store of all subscriptions
    private SubscriptionServiceStore subscriptionStore=new SubscriptionServiceStore();

    private File tempdir=null;

    private DescriptorHelper descriptorHelper;

    private String protocol = "http";
    private String hostname = Constants.LOCALHOST;
    private int port = 5050;
    private String path = Constants.CONTEXT_PATH + Constants.SERVICES_PATH + Constants.SYSTEM_PATH;
    private String subscriptionsPath = Constants.CONTEXT_PATH + Constants.SERVICES_PATH + Constants.SUBSCRIPTION_PATH;
    private String location = "unknown";


    private static final Log log = LogFactory.getLog(ServerInstance.class);

    private URL systemsURL;

    private int requests = 0;

    private int failures = 0;

    private static final String BUILD_TIMESTAMP = "$Date$";

    //URL for subscriptions
    private URL subscriptionURL;

    /**
     * Create a new server instance, and bind it to be our current
     * server
     *
     * @param owner owning prim
     * @return the object
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public static ServerInstance createServerInstance(Prim owner) throws
            SmartFrogException, RemoteException {
        ServerInstance serverInstance = new ServerInstance(owner);
        instance = serverInstance;
        return instance;
    }

    private ServerInstance(Prim owner)
            throws SmartFrogException, RemoteException {
        location = owner.sfResolve(DeploymentServer.ATTR_LOCATION,
                location, false);
        protocol = owner.sfResolve(DeploymentServer.ATTR_PROTOCOL,
                protocol, false);
        hostname = owner.sfResolve(DeploymentServer.ATTR_HOSTNAME,
                hostname, false);
        if (hostname.length() == 0) {
            hostname = Constants.LOCALHOST;
        }
        port = owner.sfResolve(DeploymentServer.ATTR_PORT,
                port, false);
        String ctx = owner.sfResolve(DeploymentServer.ATTR_CONTEXTPATH,
                Constants.CONTEXT_PATH, false);
        String servicespath = owner.sfResolve(DeploymentServer.ATTR_SERVICESPATH,
                Constants.SERVICES_PATH, false);
        String systemPath = owner.sfResolve(DeploymentServer.ATTR_SYSTEM_PATH,
                Constants.SYSTEM_PATH, false);
        path = ctx + servicespath + systemPath;
        subscriptionsPath = ctx + servicespath
                + owner.sfResolve(DeploymentServer.ATTR_SUBSCRIPTIONS_PATH,
                    Constants.SUBSCRIPTION_PATH, false);
        //temp dir set up
        File javatmpdir = new File(System.getProperty("java.io.tmpdir"));
        String absolutePath = FileSystem.lookupAbsolutePath(owner,
                DeploymentServer.ATTR_FILESTORE_DIR,
                null,
                javatmpdir,
                false,
                null);
        if (absolutePath != null) {
            tempdir = new File(absolutePath);
        }
        try {
            init();
        } catch (IOException e) {
            throw SmartFrogException.forward(e);
        }
    }


    private void init() throws IOException {
        systemsURL = new URL(protocol, hostname, port, path);
        subscriptionURL = new URL(protocol, hostname, port, subscriptionsPath);
        jobs = new JobRepository(systemsURL, this, createEventExecutorService());
        if (tempdir == null) {
            tempdir = File.createTempFile("filestore", ".dir");
            //little bit of a race condition here.
            tempdir.delete();
        }
        descriptorHelper = new DescriptorHelper(tempdir);
        filestore = new AddedFilestore(tempdir);
        log.debug("Creating server instance " + toString());

        //now create our property map
        initPropertyMap();
    }

    /**
     * initiate a graceful shutdown of workers. we do this by pushing a shutdown
     * request for every worker
     */
    public void terminate() throws RemoteException {
        filestore.deleteAllEntries();
        jobs.shutdown();
    }

    /**
     * liveness check
     *
     * @throws SmartFrogLivenessException
     */
    public void ping() throws SmartFrogLivenessException {
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "Server @" + systemsURL + " filestore:" + tempdir;
    }

    /**
     * Get the job repository
     * @return
     */
    public JobRepository getJobs() {
        return jobs;
    }

    public EventSubscriberManager getSubscriptions() {
        return jobs.getSubscriptions();
    }

    public String getSubscriptionsPath() {
        return subscriptionsPath;
    }

    /**
     * Get the URL for subscriptions
     * @return
     */
    public URL getSubscriptionURL() {
        return subscriptionURL;
    }

    /**
     * get the current instance; bailing out if none exists
     *
     * @return the current instance
     * @throws AlpineRuntimeException if needed
     */
    public static ServerInstance currentInstance() {
        if (instance == null) {
            throw new AlpineRuntimeException("No configured ServerInstance");
        }
        return instance;
    }


    /**
     * Get the server or null if none is defined
     * @return the current server instance
     */
    public static ServerInstance getServerInstanceOrNull() {
        return instance;
    }

    public DescriptorHelper getDescriptorHelper() {
        return descriptorHelper;
    }

    public AddedFilestore getFilestore() {
        return filestore;
    }

    public String getResourceID() {
        return resourceID;
    }

    private void initPropertyMap() {
        properties = new PropertyMap();
        properties.addStaticProperty(Constants.PROPERTY_MUWS_RESOURCEID,
                XomHelper.makeResourceId(resourceID));
        properties.addStaticProperty(Constants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS,
                makeStaticStatus());

        //this is the dynamic property that lists active systems
        properties.add(new ActiveSystemsProperty(this));

        WsrfUtils.addManagementCharacteristics(properties, CddlmConstants.CDL_API_PORTAL_CAPABILITY);

        //the list of topics
        /*
          <wstop:Topic name="SystemCreatedEvent"
    messageTypes="muws-p1-xs:ManagementEvent">
  </wstop:Topic>
  */
        List<Element> topics=new ArrayList<Element>();
        SoapElement systemCreatedEvent=new SoapElement(CddlmConstants.PROPERTY_WSNT_TOPIC);
        systemCreatedEvent.addAttribute(new Attribute("name", "SystemCreatedEvent"));
        systemCreatedEvent.addNamespaceDeclaration("muws-p1-xs",CddlmConstants.MUWS_P1_NAMESPACE);
        systemCreatedEvent.addAttribute(new Attribute("messageTypes", "muws-p1-xs:ManagementEvent"));
        topics.add(systemCreatedEvent);
        WsrfUtils.addWsTopics(properties, topics,true,WsrfUtils.DEFAULT_TOPIC_DIALECTS);
    }

    /**
     * Get a property value
     *
     * @param property
     * @return null for no match;
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     *          if they feel like it
     */
    public List<Element> getProperty(QName property) {
        return properties.getProperty(property);
    }

    @SuppressWarnings("deprecation")
    private Element makeStaticStatus() {
        Element status = Xom5Utils.element(Constants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS);
        Element portal = XomHelper.apiElement(StatusElements.PORTAL);
        status.appendChild(portal);

        portal.appendChild(
                XomHelper.apiElement(StatusElements.NAME, Constants.BUILD_INFO_IMPLEMENTATION_NAME));
        portal.appendChild(
                XomHelper.apiElement(StatusElements.BUILD, BUILD_TIMESTAMP));
        portal.appendChild(
                XomHelper.apiElement(StatusElements.LOCATION, location));
        portal.appendChild(
                XomHelper.apiElement(StatusElements.HOME, Constants.BUILD_INFO_HOMEPAGE));
        Date now = new Date();
        BigInteger tzoffset = BigInteger.valueOf(now.getTimezoneOffset());
        portal.appendChild(
                XomHelper.apiElement(StatusElements.TIMEZONE_UTCOFFSET, tzoffset.toString()));

        Element languages = XomHelper.apiElement(StatusElements.LANGUAGES);
        Element cdl = XomHelper.apiElement(StatusElements.ITEM);
        Element name = XomHelper.apiElement(StatusElements.NAME, Constants.BUILD_INFO_CDL_LANGUAGE);
        Element uri = XomHelper.apiElement(StatusElements.URI, Constants.XML_CDL_NAMESPACE);
        cdl.appendChild(name);
        cdl.appendChild(uri);
        languages.appendChild(cdl);
        status.appendChild(languages);
        Element sfrog = XomHelper.apiElement(StatusElements.ITEM);
        name = XomHelper.apiElement(StatusElements.NAME, Constants.BUILD_INFO_SF_LANGUAGE);
        uri = XomHelper.apiElement(StatusElements.URI, Constants.SMARTFROG_NAMESPACE);
        sfrog.appendChild(name);
        sfrog.appendChild(uri);
        languages.appendChild(sfrog);

        Element notifications = XomHelper.apiElement(StatusElements.NOTIFICATIONS);
        Element wsrf = XomHelper.apiElement(StatusElements.ITEM, Constants.WSRF_WSNT_NAMESPACE);
        notifications.appendChild(wsrf);
        status.appendChild(notifications);

        Element options = XomHelper.apiElement(StatusElements.OPTIONS);
        status.appendChild(options);
        return status;
    }

    public synchronized void resetStatistics() {
        failures = 0;
        requests = 0;
    }

    public int getRequests() {
        return requests;
    }

    public int getFailures() {
        return failures;
    }

    public void incrementRequests() {
        synchronized (this) {
            requests++;
        }
    }

    public void incrementFailures() {
        synchronized (this) {
            failures++;
        }
    }

    /**
     * Control point for managing
     * @return
     */
    public ExecutorService createEventExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    public SubscriptionServiceStore getSubscriptionStore() {
        return subscriptionStore;
    }

    /**
     * subscribe to portal events
     * @param subscription
     */
    public void subscribeToPortalEvents(NotificationSubscription subscription) {
        log.info("Adding portal subscription "+subscription);
        EventSubscriberManager subscriptions = getSubscriptions();
        subscriptions.add(subscription);
        getSubscriptionStore().add(subscription);
    }
}
