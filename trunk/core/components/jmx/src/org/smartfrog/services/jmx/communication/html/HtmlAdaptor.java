/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jmx.communication.html;

import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Set;
import java.util.Iterator;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.management.*;

//import openjmx.adaptor.http.HttpAdaptor;
//import openjmx.adaptor.http.HttpAdaptorMBean;
//import openjmx.adaptor.http.XSLTProcessor;
//import openjmx.adaptor.http.ProcessorMBean;
//import openjmx.adaptor.http.AdaptorSocketFactory;
//import openjmx.adaptor.http.ssl.SSLFactory;

import mx4j.tools.adaptor.http.HttpAdaptor;
import mx4j.tools.adaptor.http.XSLTProcessor;
import mx4j.tools.adaptor.http.ProcessorMBean;
import mx4j.tools.adaptor.AdaptorServerSocketFactory;  //AdaptorSocketFactory;
import mx4j.tools.adaptor.ssl.SSLAdaptorServerSocketFactory; //import mx4j.adaptor.http.ssl.SSLFactory;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 *  Implement the HTML Adaptor
 *
 *@version        1.0
 */
public class HtmlAdaptor extends PrimImpl implements Prim, HtmlAdaptorMBean, MBeanRegistration, NotificationBroadcaster {

    MBeanServer server;
    ObjectName objectName;
    HttpAdaptor httpAdaptor;
    String mbeanServerId = null;

    private NotificationBroadcasterSupport broadcaster = new NotificationBroadcasterSupport();

    private long sequenceNumber = 0;

    /**
     *  Constructor for the HtmlAdaptor object
     *
     *@exception  RemoteException  Description of the Exception
     */
    public HtmlAdaptor() throws RemoteException {
        httpAdaptor = new HttpAdaptor();
    }

    /**
     *  Allows configuring the port and to be started when the MBeanServer has
     *  registered this MBean succesfully
     *
     * @param port network port
     */
    public HtmlAdaptor(int port) throws RemoteException {
        setPort(port);
    }


// MBeanRegistration interface

    /**
     *  Keep a reference to the MBeanServer and assigned ObjectName
     *
     *@param  server                   Description of the Parameter
     *@param  name                     Description of the Parameter
     *@return                          Description of the Return Value
     *@exception  java.lang.Exception  Description of the Exception
     */

    public ObjectName preRegister(MBeanServer server, ObjectName name) throws java.lang.Exception {
        this.server = server;
        objectName = name;
        mbeanServerId = (String) server.getAttribute(new ObjectName("JMImplementation:type=MBeanServerDelegate"), "MBeanServerId");
        return httpAdaptor.preRegister(server, name);
    }

    /**
     *  If registration has not been succesfull, set reference to MBeanServer to
     *  null.
     *
     *@param  registrationDone  Description of the Parameter
     */
    public void postRegister(Boolean registrationDone) {
        if (!registrationDone.booleanValue()) {
            synchronized (this) {
                server = null;
            }
        }
        httpAdaptor.postRegister(registrationDone);
    }

    /**
     *  Stops this server
     *
     *@exception  java.lang.Exception  Description of the Exception
     */
    public void preDeregister() throws java.lang.Exception {
        httpAdaptor.preDeregister();
    }


    /**
     *  Does nothing
     */
    public void postDeregister() {
        httpAdaptor.postDeregister();
    }


// NotificationBroadcaster interface

    /**
     * Adds a listener to a registered MBean.
     *
     * @param listener The listener object which will handle the notifications emitted by the registered MBean.
     * @param filter The filter object. If filter is null, no filtering will be performed before handling notifications.
     * @param handback An opaque object to be sent back to the listener when a notification is emitted. This object
     * cannot be used by the Notification broadcaster object. It should be resent unchanged with the notification
     * to the listener.
     *
     * @exception IllegalArgumentException Listener parameter is null.
     */
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
        throws java.lang.IllegalArgumentException {
        broadcaster.addNotificationListener(listener, filter, handback);
    }

    /**
     * Removes a listener from a registered MBean.
     *
     * @param listener The listener object which will handle the notifications emitted by the registered MBean.
     * This method will remove all the information related to this listener.
     *
     * @exception ListenerNotFoundException The listener is not registered in the MBean.
     */
    public void removeNotificationListener(NotificationListener listener)
        throws ListenerNotFoundException {
        broadcaster.removeNotificationListener(listener);
    }

    /**
     * Returns a NotificationInfo object contaning the name of the Java class of the notification
     * and the notification types sent.
     */
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[]{
            new MBeanNotificationInfo(new String[]{AttributeChangeNotification.ATTRIBUTE_CHANGE},
                                      AttributeChangeNotification.ATTRIBUTE_CHANGE,
                                      "Notify a change of state")
        };
    }

    private void sendStateNotification(boolean newState) {
        String message = "";
        if (newState == true) message = "Service started";
        else message = "Service stopped";

        Boolean newValue = new Boolean(newState);
        Boolean oldValue = new Boolean(!newState);

        broadcaster.sendNotification(
                    new AttributeChangeNotification(
                        this.objectName,
                        sequenceNumber++,
                        System.currentTimeMillis(),
                        message,
                        "Active",
                        "java.lang.Boolean",
                        oldValue,
                        newValue));
    }




// CommunicatorMBean interface

    /**
     *  Returns protocol that this Adaptor uses. Always HTTP.
     *
     *@return    HTTPServer's port
     */
    public String getProtocol() {
        return "HTTP";
    }


    /**
     *  Returns the port where the server is running on. Default is 80
     *
     *@return    HTTPServer's port
     */
    public int getPort() {
        return httpAdaptor.getPort();
    }


    /**
     *  Sets the value of the server's port
     *
     *@param  port                                 the new port's value
     *@exception  java.lang.IllegalStateException  Description of the Exception
     */
    public void setPort(int port) throws java.lang.IllegalStateException {
        if (isActive()) {
            throw new IllegalStateException("You should stop this HTMLAdaptor first");
        }
        httpAdaptor.setPort(port);
    }


    /**
     *  Return the host name the server will be listening to. If null the server
     *  listen at the localhost
     *
     *@return    the current hostname
     */
    public String getHost() {
        String host = httpAdaptor.getHost();
        if (host.equals("localhost")) {
            try {
                host = InetAddress.getLocalHost().getHostName();
            } catch (Exception e) {}
        }
        return host;
    }


    /**
     *  Sets the host name where the server will be listening
     *
     *@param  host                                 Server's host
     *@exception  java.lang.IllegalStateException  Description of the Exception
     */
    public void setHost(String host) throws java.lang.IllegalStateException {
        if (isActive()) {
            throw new IllegalStateException("You should stop this HTMLAdaptor first");
        }
        httpAdaptor.setHost(host);
    }


    /**
     *  Sets the object which will post process the XML results. The last value
     *  set between the setProcessor and setPostProcessorName will be the valid
     *  one
     *
     *@param  processor  a processor object
     */
    public void setProcessor(ProcessorMBean processor) {
        httpAdaptor.setProcessor(processor);
    }


    /**
     *  Sets the object name which will post process the XML result. The last
     *  value set between the setProcessor and setPostProcessorName will be the
     *  valid one. The MBean will be verified to be of instance
     *  HttpPostProcessor
     *
     *@param  processorName  The new processorName value
     */
    public void setProcessorName(ObjectName processorName) {
        httpAdaptor.setProcessorName(processorName);
    }


    /**
     *  Returns the object being used as a processor
     *
     *@return    the processor object
     */
    public ProcessorMBean getProcessor() {
        return httpAdaptor.getProcessor();
    }


    /**
     *  Returns the ObjectName of the processor being used
     *
     *@return    processor objectname
     */
    public ObjectName getProcessorName() {
        return httpAdaptor.getProcessorName();
    }

    /**
     * Sets the object which create the server sockets
     *
     * @param factory the socket factory
     */
    public void setSocketFactory(AdaptorServerSocketFactory factory) {
        httpAdaptor.setSocketFactory(factory);
    }

    /**
     * Authentication Method
     *
     * @return authentication method
     */
    public String getAuthenticationMethod() {
        return httpAdaptor.getAuthenticationMethod();
    }

    /**
     * Sets the Authentication Method.
     *
     * @param method none/basic/digest
     */
    public void setAuthenticationMethod(String method) {
        httpAdaptor.setAuthenticationMethod(method);
    }

    /**
     * Adds an authorization
     *
     * @param username authorized username
     * @param password authorized password
     */
    public void addAuthorization(String username, String password) {
        httpAdaptor.addAuthorization(username, password);
    }


    /**
     *  Indicates whether the server's running
     *
     *@return         The active value
     */
    public boolean isActive() {
        return httpAdaptor.isActive();
    }


    /**
     *  Starts the server
     */
    public void start() {
        if (isActive()) return;
        try {
            httpAdaptor.start();
            if (isActive()) sendStateNotification(true);
        } catch (Exception e) { e.printStackTrace(); }
    }


    /**
     *  Stops the server
     */
    public void stop() {
        if (!isActive()) return;
        httpAdaptor.stop();
        sendStateNotification(false);
    }


    /**
     *  Restarts the server. Useful when changing the Server parameters
     *
     *@exception  IOException  Description of the Exception
     */
    public void restart() throws IOException {
        try {
            // httpAdaptor.reStart(); //Deprecapeted for RC1 or MX4J
            httpAdaptor.stop();
            httpAdaptor.start();
        } catch (Exception e) {}
    }


    /**
     *  Prim Interface ****
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        try {
            httpAdaptor.setHost ((String) sfResolveHere("host"));
            httpAdaptor.setPort(((Integer) sfResolveHere("port")).intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        // Configure XML Processor
        String rootDirectory = null;
        String pathInJar = null;
        try {
            rootDirectory = (String) sfResolve("rootDirectory");
        } catch (Exception e) {}
        try {
            pathInJar = (String) sfResolve("pathInJar");
        } catch (Exception e) {}

        QueryExp xmlExp = Query.match(Query.classattr(), Query.value("mx4j.tools.adaptor.http.XSLTProcessor"));
        Set mbeanSet = null;
        try {
          mbeanSet = server.queryNames(new ObjectName("*:*"), xmlExp);
        } catch (MalformedObjectNameException ex) {
          throw SmartFrogException.forward(ex);
        }
        if (!mbeanSet.isEmpty()) {
            for (Iterator i = mbeanSet.iterator(); i.hasNext(); ) {
                try {
                    ObjectName xsltProcessorName = (ObjectName) i.next();
                    if (rootDirectory != null) {
                        server.setAttribute(xsltProcessorName, new Attribute("File", rootDirectory));
                    }
                    if (pathInJar != null) {
                       server.setAttribute(xsltProcessorName, new Attribute("PathInJar", pathInJar));
                    }
                    httpAdaptor.setProcessorName(xsltProcessorName);
                    break;
                }
                catch (Exception e) { e.printStackTrace(); }
            }
        } else {
            try {
                XSLTProcessor xsltProcessor = new XSLTProcessor();
                if (rootDirectory != null) {
                    xsltProcessor.setFile(rootDirectory);
                }
                else if (pathInJar != null) {
                    xsltProcessor.setPathInJar(pathInJar);
                }
                httpAdaptor.setProcessor(xsltProcessor);
                //ObjectName xsltName = new ObjectName(server.getDefaultDomain()+":name=XSLTProcessor,type=sf.jmx.srv.xml,server="+mbeanServerId);
                ObjectName xsltName = new ObjectName(server.getDefaultDomain()+":name=XSLTProcessor,type=sf.jmx.srv.xml");
                server.registerMBean(xsltProcessor, xsltName);
            }
            catch (Exception e) { e.printStackTrace(); }
        }

        try {
            String authentication = (String) sfResolve("authentication");
            if (authentication != null) httpAdaptor.setAuthenticationMethod(authentication);
        } catch (Exception e) {}


        try {
            Context authorization = ((ComponentDescription) sfResolve("authorization")).sfContext();
            if (authorization != null) {
                for (Enumeration e = authorization.keys(); e.hasMoreElements(); ) {
                    String username = (String) e.nextElement();
                    String password = (String) authorization.get(username);
                    httpAdaptor.addAuthorization(username, password);
                }
            }
        } catch (Exception e) {}


        // Configure SSL service
        if (((Boolean)sfResolve("ssl")).booleanValue()) {
            SSLAdaptorServerSocketFactory sslFactory = new SSLAdaptorServerSocketFactory();
            try {
                String keyManagerAlgorithm = (String)sfResolve("keyManagerAlgorithm");
                sslFactory.setKeyManagerAlgorithm(keyManagerAlgorithm);
            }
            catch (Exception e) { e.printStackTrace(); }
            try {
                String keyStoreFileName = (String)sfResolve("keyStoreFileName");
                sslFactory.setKeyStoreName(keyStoreFileName);
            }
            catch (Exception e) { e.printStackTrace(); }
            try {
                String keyStorePassword = (String)sfResolve("keyStorePassword");
                sslFactory.setKeyStorePassword(keyStorePassword);
            }
            catch (Exception e) { e.printStackTrace(); }
            try {
                String keyManagerPassword = (String)sfResolve("keyManagerPassword");
                sslFactory.setKeyManagerPassword(keyManagerPassword);
            }
            catch (Exception e) { e.printStackTrace(); }
            try {
                String keyStoreType = (String)sfResolve("keyStoreType");
                sslFactory.setKeyStoreType(keyStoreType);
            }
            catch (Exception e) { e.printStackTrace(); }
            try {
                String sslProtocol = (String)sfResolve("sslProtocol");
                sslFactory.setSSLProtocol(sslProtocol);
            }
            catch (Exception e) { e.printStackTrace(); }
            httpAdaptor.setSocketFactory(sslFactory);
            try {
              ObjectName sslFactoryName = new ObjectName( "Services:name=SSLFactory,type=sf.jmx.srv.ssl,server=" + mbeanServerId);
              server.registerMBean(sslFactory, sslFactoryName);
            } catch (Exception ex1) {
              throw SmartFrogException.forward(ex1);
            }
        }
        start();
        if (sfLog().isInfoEnabled()){ sfLog().info("  -> HTMLAdaptor started");}
    }

}
