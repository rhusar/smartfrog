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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.axis2.om.OMElement;
import org.apache.xmlbeans.XmlObject;
import org.ggf.cddlm.utils.QualifiedName;
import org.ggf.xbeans.cddlm.api.ActiveSystemsDocument;
import org.ggf.xbeans.cddlm.api.NameUriListType;
import org.ggf.xbeans.cddlm.api.PortalInformationType;
import org.ggf.xbeans.cddlm.api.StaticPortalStatusDocument;
import org.ggf.xbeans.cddlm.api.StaticPortalStatusType;
import org.ggf.xbeans.cddlm.api.SystemReferenceListType;
import org.ggf.xbeans.cddlm.api.UriListType;
import org.ggf.xbeans.cddlm.wsrf.muws.p1.IdentityPropertiesType;
import org.ggf.xbeans.cddlm.wsrf.wsa2003.EndpointReferenceType;
import org.smartfrog.services.deployapi.components.AddedFilestore;
import org.smartfrog.services.deployapi.components.DeploymentServer;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.deployapi.transport.wsrf.WSRPResourceSource;
import org.smartfrog.services.deployapi.binding.DescriptorHelper;
import org.smartfrog.services.deployapi.binding.Axis2Beans;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.Date;
import java.rmi.RemoteException;

import nu.xom.Element;


/**
 * This is a server instance Date: 10-Aug-2004 Time: 22:13:26
 */
public class ServerInstance implements WSRPResourceSource {

    /**
     * a private instance
     */
    private static ServerInstance instance;

    private String resourceID = Utils.createNewID();

    private StaticPortalStatusDocument staticStatus;

    private JobRepository jobs;

    private ActionQueue queue = new ActionQueue();

    private ActionWorker workers[];

    private AddedFilestore filestore;

    private File tempdir;

    private DescriptorHelper descriptorHelper;

    private String protocol="http";
    private String hostname=LOCALHOST;
    private int port=5050;
    private String path=CONTEXT_PATH+SERVICES_PATH+SYSTEM_PATH;
    private String location="unknown";

    //private CdlParser cdlParser;

    public static final int WORKERS = 1;
    public static final long TIMEOUT = 0;

    private static Log log= LogFactory.getLog(ServerInstance.class);
    private static final String CONTEXT_PATH = "/";
    private static final String SERVICES_PATH = "services/";
    private static final String SYSTEM_PATH = "System/";
    private URL systemsURL;
    public static final String LOCALHOST = "localhost";

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
            hostname=LOCALHOST;
        }
        port = owner.sfResolve(DeploymentServer.ATTR_PORT,
                port, false);
        String ctx= owner.sfResolve(DeploymentServer.ATTR_CONTEXTPATH,
                CONTEXT_PATH, false);
        String servicespath = owner.sfResolve(DeploymentServer.ATTR_SERVICESPATH,
                SERVICES_PATH, false);
        path= ctx+servicespath+SYSTEM_PATH;
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
        staticStatus = createStaticStatusInfo();
        systemsURL = new URL(protocol,hostname, port,path);
                //new URL("http://127.0.0.1:5050/services/System/");
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


    private StaticPortalStatusDocument createStaticStatusInfo() {
        StaticPortalStatusDocument doc= StaticPortalStatusDocument.Factory.newInstance();
        StaticPortalStatusType status = doc.addNewStaticPortalStatus();
        PortalInformationType portalInfo = status.addNewPortal();
        portalInfo.setName(Constants.BUILD_INFO_IMPLEMENTATION_NAME);
        portalInfo.setBuild("$Date$");
        portalInfo.setLocation(location);
        portalInfo.setHome(Constants.BUILD_INFO_HOMEPAGE);
        Date now=new Date();
        BigInteger tzoffset=BigInteger.valueOf(now.getTimezoneOffset());
        portalInfo.setTimezoneUTCOffset(tzoffset);
        NameUriListType languages = status.addNewLanguages();
        NameUriListType.Item item = languages.addNewItem();
        item.setName(Constants.BUILD_INFO_CDL_LANGUAGE);
        item.setUri(Constants.XML_CDL_NAMESPACE);
        item = languages.addNewItem();
        item.setName(Constants.BUILD_INFO_SF_LANGUAGE);
        item.setUri(Constants.SMARTFROG_NAMESPACE);
        UriListType notifications = status.addNewNotifications();
        notifications.addNewItem().setStringValue(Constants.WSRF_WSNT_NAMESPACE);
        UriListType options = status.addNewOptions();

        return doc;
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

    /**
     * Get a resource
     *
     * @param resource
     * @return null for no match;
     * @throws BaseException if they feel like it
     */
    public OMElement getResource(QName resource) {
        QualifiedName query= Utils.convert(resource);
        XmlObject result=null;
        Element xom=null;
        OMElement resultElement = null;
        if(Constants.PROPERTY_PORTAL_STATIC_PORTAL_STATUS.equals(query)) {
            result= staticStatus;
        }
        if (Constants.PROPERTY_MUWS_RESOURCEID.equals(query)) {
            xom = XomHelper.makeResourceId(resourceID);
        }
        if(Constants.PROPERTY_PORTAL_ACTIVE_SYSTEMS.equals(query)) {
            result= getJobList().getActiveSystems();
        }
        //conver to Axiom
        if(result!=null) {
            resultElement = Axis2Beans.convertDocument(result);
            return resultElement;
        }
        if(xom!=null) {
            resultElement=Utils.xomToAxiom(xom);
        }
        return resultElement;
    }

    /**
     * Get the job list
     * @return
     */
    private ActiveSystemsDocument getJobList() {
        ActiveSystemsDocument doc=ActiveSystemsDocument.Factory.newInstance();
        SystemReferenceListType systems = doc.addNewActiveSystems();
        int size=jobs.size();
        EndpointReferenceType apps[]=new EndpointReferenceType[size];
        int counter=0;
        for(Job job:jobs) {
            apps[counter++]=(EndpointReferenceType)job.getEndpoint().copy();
        }
        systems.setSystemArray(apps);
        return doc;
    }

    /**
     * Returns a string representation of the object. 
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "Server @"+systemsURL+" filestore:"+tempdir;
    }
}
