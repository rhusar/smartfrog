/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www.tomcat;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.www.JavaWebApplication;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * webapp for tomcat TODO: move to httpclient simply to avoid having a single
 * authenticator for the whole JVM
 */

public class TomcatDelegateWebApplication
        extends TomcatDelegateApplicationContext implements JavaWebApplication {

    private String contextPath = "/";
    private String webApp = null;
    private String absolutePath;
    private Reference usernameRef = new Reference(ReferencePart.here(
            TomcatServer.ATTR_USERNAME));
    private Reference passwordRef = new Reference(ReferencePart.here(
            TomcatServer.ATTR_PASSWORD));
    private Reference portRef = new Reference(ReferencePart.here(TomcatServer.ATTR_PORT));
    private String username = "";
    private String password = "";
    private int port;
    private String host = null;
    private TomcatManagerAuthenticator authenticator;
    private String managerURL;
    private String manager = null;


    protected TomcatDelegateWebApplication(TomcatServer server,
                                           Prim declaration) {
        super(server, declaration);
    }


    /**
     * deploy this thing
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void deploy() throws SmartFrogException, RemoteException {
        webApp =
                FileSystem.lookupAbsolutePath(getDeclaration(),
                        ATTR_FILE,
                        null,
                        null,
                        true,
                        null);
        //sanity check
        File webappFile = new File(webApp);
        if (!webappFile.exists()) {
            throw new SmartFrogDeploymentException(ERROR_FILE_NOT_FOUND +
                    webappFile);
        }
        contextPath = getDeclaration().sfResolve(ATTR_CONTEXT_PATH,
                (String) null,
                true);
        if (!contextPath.startsWith("/")) {
            contextPath = "/" + contextPath;
        }
        absolutePath = contextPath;
        getDeclaration().sfReplaceAttribute(ATTR_ABSOLUTE_PATH, absolutePath);
        //Deploy a WAR File
        port = getServerPrim().sfResolve(portRef, 0, true);
        username = getServerPrim().sfResolve(usernameRef, username, true);
        password = getServerPrim().sfResolve(passwordRef, password, true);
        port = getServerPrim().sfResolve(portRef, port, true);
        authenticator = new TomcatManagerAuthenticator(username, password);
        manager = getServerPrim().sfResolve(TomcatServer.ATTR_MANAGER,
                manager,
                true);
        host = "localhost";
        if (!manager.startsWith("/")) {
            manager = "/" + manager;
        }
        if (!manager.endsWith("/")) {
            manager = manager + "/";
        }
        managerURL = "http://" + host + ":" + port + manager;

    }

    /**
     * start the component
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void start() throws SmartFrogException, RemoteException {
        terminate();
        Authenticator.setDefault(authenticator);
        //Install and start the webapp
        String warfile;
        //warfile= "jar:" + webApp; + "!/";
        warfile = webApp;
        String commandStr = "install?path=" + contextPath + "&war=" + warfile;
        executeManagerCommand(commandStr, true);
        commandStr = "start?path=" + contextPath;
        executeManagerCommand(commandStr, true);
    }

    /**
     * this method is here for server-specific implementation classes,
     *
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void terminate() throws RemoteException, SmartFrogException {
        Authenticator.setDefault(authenticator);
        String commandStr = "remove?path=" + contextPath;
        executeManagerCommand(commandStr, false);
    }

    /**
     * liveness check
     *
     * @throws SmartFrogLivenessException
     * @throws RemoteException
     */
    public void ping() throws SmartFrogLivenessException, RemoteException {
        //TODO:liveness
    }

    /**
     * This executes the Tomcat Manager commands. This could be and maybe should
     * be updated to return the status of the command as oppossed to assuming it
     * worked.
     */
    private void executeManagerCommand(String urlString, boolean checkresponse)
            throws SmartFrogException {
        BufferedReader in = null;
        String fullURL = managerURL + urlString;
        try {
            URL url = new URL(fullURL);
            InputStream content = (InputStream) url.getContent();
            in = new BufferedReader(new InputStreamReader(content));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = in.readLine()) != null) {
                System.out
                        .println(line);//Should really return this maybe check the text for an error
                response.append(line);
                response.append("\n");
            }
            if (checkresponse && response.indexOf("FAIL") >= 0) {
                throw new SmartFrogException("Command " + fullURL + " Failed\n" + response);
            }
        } catch (IOException e) {
            throw SmartFrogException.forward(e);
        } finally {
            FileSystem.close(in);
        }
    }
}
