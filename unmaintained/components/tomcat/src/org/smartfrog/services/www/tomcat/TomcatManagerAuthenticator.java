package org.smartfrog.services.www.tomcat;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * This class is used to connect to the Tomcat Manager tool. It basically
 * handles the login.
 */
public class TomcatManagerAuthenticator extends Authenticator {

    String username = "";
    String password = "";

    /**
     * Constructor is simply passed the username and password
     */
    public TomcatManagerAuthenticator(String u, String p) {
        username = u;
        password = p;
    }

    /**
     * This returns the PasswordAuthentication object.
     */
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password.toCharArray());
    }

}
