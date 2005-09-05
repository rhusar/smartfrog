package org.smartfrog.services.www.tomcat;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.www.JavaWebApplication;

import java.rmi.RemoteException;
import java.net.Authenticator;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 */
public class TomcatWebApplication extends TomcatDelegateApplicationContext implements JavaWebApplication {

    private Reference usernameRef = new Reference(ReferencePart.here(TomcatServer.ATTR_USERNAME));
    private Reference passwordRef = new Reference(ReferencePart.here(TomcatServer.ATTR_PASSWORD));
    private Reference portRef = new Reference(ReferencePart.here(TomcatServer.ATTR_PORT));
    private Reference nameRef = new Reference(ReferencePart.here("name"));

    private Reference hostRef = new Reference(ReferencePart.here("host"));

    private int port;

    private String name = "";
    private String manager = "/manager/";
    private String webApp = "";
    private String warFileLocation = "";
    private String host = "";
    private String username = "";
    private String password = "";

    public static final String ERROR_WARFILE_NOT_FOUND = "Web application not found:";

    public TomcatWebApplication(TomcatServer server, Prim declaration) {
        super(server, declaration);
    }

    /**
     * deploy this thing
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void deploy() throws SmartFrogException, RemoteException {
        name = getDeclaration().sfResolve(nameRef).toString();
        webApp =
                FileSystem.lookupAbsolutePath(getDeclaration(),
                        ATTR_WARFILE,
                        null,
                        null,
                        true,
                        null);
        //sanity check
        File webappFile = new File(webApp);
        if (!webappFile.exists()) {
            throw new SmartFrogDeploymentException(ERROR_WARFILE_NOT_FOUND +
                    webappFile);
        }
        host = getDeclaration().sfResolve(hostRef).toString();
        username = getServerPrim().sfResolve(usernameRef).toString();
        password = getServerPrim().sfResolve(passwordRef).toString();
        port = getServerPrim().sfResolve(portRef, 0, true);
        //TODO: This should not be default;
        Authenticator.setDefault(new TomcatManagerAuthenticator(username, password));
    }


    private String createCommand(String action) {
        String commandStr = "http://" + host + ":" + port + "/manager/stop?path=/" + warPath;
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
     * start the component
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void start() throws SmartFrogException, RemoteException {

    }

    /**
     * this method is here for server-specific implementation classes,
     *
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void undeploy() throws RemoteException, SmartFrogException {

    }

    /**
     * liveness check
     *
     * @throws SmartFrogLivenessException
     * @throws RemoteException
     */
    public void ping() throws SmartFrogLivenessException, RemoteException {

    }
}
