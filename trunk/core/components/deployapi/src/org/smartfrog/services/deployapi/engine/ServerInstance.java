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

import nu.xom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.deployapi.binding.DescriptorHelper;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.components.DeploymentServer;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.wsrf.Property;
import org.smartfrog.services.deployapi.transport.wsrf.PropertyMap;
import org.smartfrog.services.deployapi.transport.wsrf.WSRPResourceSource;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.filesystem.filestore.AddedFilestore;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;


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

    private ActionQueue queue = new ActionQueue();

    private ActionWorker workers[];

    private AddedFilestore filestore;

    private File tempdir;

    private DescriptorHelper descriptorHelper;

    private String protocol="http";
    private String hostname=Constants.LOCALHOST;
    private int port=5050;
    private String path=Constants.CONTEXT_PATH +Constants.SERVICES_PATH +Constants.SYSTEM_PATH;
    private String location="unknown";


    public static final int WORKERS = 1;
    public static final long TIMEOUT = 0;

    private static final Log log= LogFactory.getLog(ServerInstance.class);

    private URL systemsURL;
    private static final String BUILD_TIMESTAMP = "$Date$";

    /**
     * Create a new server instance, and bind it to be our current
     * server
     * @param owner owning prim
     * @return the object
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public static ServerInstance createServerInstance(Prim owner) throws
            SmartFrogException, RemoteException {
        ServerInstance serverInstance = new ServerInstance(owner);
        instance=serverInstance;
        return instance;
    }

    private ServerInstance(Prim owner)
            throws SmartFrogException, RemoteException {
        location = owner.sfResolve(DeploymentServer.ATTR_LOCATION,
                location, false);
        protocol = owner.sfResolve(DeploymentServer.ATTR_PROTOCOL,
                protocol,false);
        hostname = owner.sfResolve(DeploymentServer.ATTR_HOSTNAME,
                hostname, false);
        if(hostname.length()==0) {
            hostname=Constants.LOCALHOST;
        }
        port = owner.sfResolve(DeploymentServer.ATTR_PORT,
                port, false);
        String ctx= owner.sfResolve(DeploymentServer.ATTR_CONTEXTPATH,
                Constants.CONTEXT_PATH, false);
        String servicespath = owner.sfResolve(DeploymentServer.ATTR_SERVICESPATH,
                Constants.SERVICES_PATH, false);
        path= ctx+servicespath+Constants.SYSTEM_PATH;
        File javatmpdir=new File(System.getProperty("java.io.tmpdir"));
        String absolutePath = FileSystem.lookupAbsolutePath(owner,
                DeploymentServer.ATTR_FILESTORE_DIR,
                null,
                javatmpdir,
                false,
                null);
        if(absolutePath!=null) {
            tempdir=new File(absolutePath);
        }
        try {
            init();
        } catch (IOException e) {
            throw SmartFrogException.forward(e);
        }
    }



    private void init() throws IOException {
        systemsURL = new URL(protocol,hostname, port,path);
        jobs = new JobRepository(systemsURL);
        workers = new ActionWorker[WORKERS];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new ActionWorker(queue, TIMEOUT);
            workers[i].start();
        }
        if(tempdir==null) {
            tempdir = File.createTempFile("filestore", ".dir");
            //little bit of a race condition here.
            tempdir.delete();
        }
        descriptorHelper=new DescriptorHelper(tempdir);
        AddedFilestore filestore = new AddedFilestore(tempdir);
        log.debug("Creating server instance "+toString());

        //now create our property map
        initPropertyMap();
    }

    /**
     * initiate a graceful shutdown of workers. we do this by pushing a shutdown
     * request for every worker
     */
    public void terminate() throws RemoteException {
        for (int i = 0; i < workers.length; i++) {
            queue.push(new EndWorkerAction());
        }
        filestore.deleteAllEntries();
        jobs.terminate();
    }

    /**
     * liveness check
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
    public JobRepository getJobs() {
        return jobs;
    }

    /**
     * get the current instance; creating it if needed
     *
     * @return the current instance
     */
    public static ServerInstance currentInstance() {
        if (instance == null) {
            throw new RuntimeException("No configured ServerInstance");
        }
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

    /**
     * queue an action for execution
     *
     * @param action
     */
    public void queue(Action action) {
        queue.push(action);
    }

    private void initPropertyMap() {
        properties=new PropertyMap();
        properties.addStaticProperty(Constants.PROPERTY_MUWS_RESOURCEID,
                XomHelper.makeResourceId(resourceID));
        properties.addStaticProperty(Constants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS,
                makeStaticStatus());
        properties.add(new ActiveSystemsProperty(this));
    }

    /**
     * Get a property value
     *
     * @param property
     * @return null for no match;
     * @throws org.smartfrog.services.deployapi.transport.faults.BaseException
     *          if they feel like it
     */
    public Element getProperty(QName property) {
        return properties.getProperty(property);
    }

    @SuppressWarnings("deprecation")
    private Element makeStaticStatus() {
        Element status=XomHelper.element(Constants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS);
        Element portal =XomHelper.apiElement("portal");
        status.appendChild(portal);

        portal.appendChild(
                XomHelper.apiElement("name",Constants.BUILD_INFO_IMPLEMENTATION_NAME));
        portal.appendChild(
                XomHelper.apiElement("build", BUILD_TIMESTAMP));
        portal.appendChild(
                XomHelper.apiElement("location", location));
        portal.appendChild(
                XomHelper.apiElement("home", Constants.BUILD_INFO_HOMEPAGE));
        Date now = new Date();
        BigInteger tzoffset = BigInteger.valueOf(now.getTimezoneOffset());
        portal.appendChild(
                XomHelper.apiElement("timezoneUTCOffset", tzoffset.toString()));

        Element languages = XomHelper.apiElement("languages");
        Element cdl=XomHelper.apiElement("item");
        Element name=XomHelper.apiElement("name", Constants.BUILD_INFO_CDL_LANGUAGE);
        Element uri= XomHelper.apiElement("uri", Constants.XML_CDL_NAMESPACE);
        cdl.appendChild(name);
        cdl.appendChild(uri);
        languages.appendChild(cdl);
        status.appendChild(languages);
        Element sfrog = XomHelper.apiElement("item");
        name = XomHelper.apiElement("name", Constants.BUILD_INFO_SF_LANGUAGE);
        uri = XomHelper.apiElement("uri", Constants.SMARTFROG_NAMESPACE);
        sfrog.appendChild(name);
        sfrog.appendChild(uri);
        languages.appendChild(sfrog);

        Element notifications=XomHelper.apiElement("notifications");
        Element wsrf=XomHelper.apiElement("item", Constants.WSRF_WSNT_NAMESPACE);
        notifications.appendChild(wsrf);
        status.appendChild(notifications);

        Element options=XomHelper.apiElement("options");
        status.appendChild(options);
        return status;
    }


    /**
     * This is a live property that can be served up
     */
    private static class ActiveSystemsProperty implements Property {

        private ServerInstance owner;

        public ActiveSystemsProperty(ServerInstance owner) {
            this.owner = owner;
        }

        public QName getName() {
            return Constants.PROPERTY_PORTAL_ACTIVE_SYSTEMS;
        }

        public Element getValue() {
            Element response=XomHelper.element(Constants.PROPERTY_PORTAL_ACTIVE_SYSTEMS);
            JobRepository jobs = owner.getJobs();
            for (Application job : jobs) {
                response.appendChild(job.getEndpointer().copy());
            }
            return response;
        }
    }
}
