package org.smartfrog.services.www.tomcat;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;


/**
 * This component manages a WAR file lifecycle in the Apache Tomcat webserver.
 * This component is not deployed at the same time as the Tomcat component
 * because of timing issues. If you deploy them both at the same time Tomcat
 * will not be running when the request to load a file file is sent and an
 * error will be recieved.
 */
public class WARImpl extends PrimImpl implements Prim {

    boolean terminated = false;

    Reference nameRef = new Reference(ReferencePart.here("name"));
    Reference filePathRef = new Reference(ReferencePart.here("warPath"));
    Reference locationRef = new Reference(ReferencePart.here("location"));

    Reference hostRef = new Reference(ReferencePart.here("host"));
    Reference usernameRef = new Reference(ReferencePart.here(TomcatServer.ATTR_USERNAME));
    Reference passwordRef = new Reference(ReferencePart.here(TomcatServer.ATTR_PASSWORD));
    Reference portRef = new Reference(ReferencePart.here(TomcatServer.ATTR_PORT));

    String host = "";
    String username = "";
    String password = "";
    int port;

    String name = "";
    String warFilePath = "";
    String warFileLocation = "";

    //Standard Remote constructor
    public WARImpl() throws RemoteException {
    }

    /**
     * Retrieve the parameters from the sf file
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        try {
            name = sfResolve(nameRef).toString();
            warFilePath = sfResolve(filePathRef).toString();
            warFileLocation = sfResolve(locationRef).toString();
            host = sfResolve(hostRef).toString();
            username = sfResolve(usernameRef).toString();
            password = sfResolve(passwordRef).toString();
            String portStr = sfResolve(portRef).toString();
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException nfe) {
                //never
            }
        } catch (SmartFrogResolutionException re) {
            //name not provided, so get name from tree - returns a reference
            name = sfCompleteName().toString();
        }
        Authenticator.setDefault(new TomcatManagerAuthenticator(username, password));
    }

    /**
     * This loads a WAR file using a command like:
     * <p/>
     * http://sse0622:8080/manager/install?path=/metropolis&war=jar:http://sse0983:8080/metropolis.war!/
     * <p/>
     * or:
     * <p/>
     * http://localhost:8080/manager/install?path=/metropolis&war=jar:file:C:\Apache%20Tomcat%204.0\webapps\metropolis.war!/
     */
    public void loadWAR(String warPath, String warLocation) throws RemoteException {
        String commandStr = "http://" + host + ":" + port + "/manager/install?path=/" + warPath + "&war=jar:" + warLocation + "!/";
        executeManagerCommand(commandStr);
    }

    /**
     * This unloads an existing WAR file using a command like:
     * <p/>
     * http://localhost:8080/manager/remove?path=/examples
     */
    public void unloadWAR(String warPath) throws RemoteException {
        String commandStr = "http://" + host + ":" + port + "/manager/remove?path=/" + warPath;
        executeManagerCommand(commandStr);
    }

    /**
     * This starts an existing WAR file using a command like:
     * <p/>
     * http://localhost:8080/manager/start?path=/examples
     */
    public void startWAR(String warPath) throws RemoteException {
        String commandStr = "http://" + host + ":" + port + "/manager/start?path=/" + warPath;
        executeManagerCommand(commandStr);
    }

    /**
     * This stops an existing WAR file using a command like:
     * <p/>
     * http://localhost:8080/manager/stop?path=/examples
     */
    public void stopWAR(String warPath) throws RemoteException {
        String commandStr = "http://" + host + ":" + port + "/manager/stop?path=/" + warPath;
        executeManagerCommand(commandStr);
    }

    /**
     * This executes the Tomcat Manager commands. This could be and maybe
     * should be updated to return the status of the command as oppossed
     * to assuming it worked.
     */
    private void executeManagerCommand(String urlString) {
        try {
            URL url = new URL(urlString);
            InputStream content = (InputStream) url.getContent();
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);//Should really return this maybe check the text for an error
            }
        } catch (MalformedURLException e) {
            System.out.println("Invalid URL");
        } catch (IOException e) {
            System.out.println("Error reading URL");
        }
    }


    /**
     * Load the WAR file
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        this.loadWAR(warFilePath, warFileLocation);
    }

    /**
     * Unload the WAR file and then Terminate the component
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        try {
            this.unloadWAR(warFilePath);
        } catch (RemoteException re) {
        }
        terminated = true;
        super.sfTerminateWith(tr);
    }

}
