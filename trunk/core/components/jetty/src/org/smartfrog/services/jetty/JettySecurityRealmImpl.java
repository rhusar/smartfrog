/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.jetty;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.security.Password;
import org.smartfrog.services.passwords.PasswordHelper;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Created 25-Oct-2007 17:00:02
 */

public class JettySecurityRealmImpl extends PrimImpl implements JettySecurityRealm {
    protected JettyHelper jettyHelper = new JettyHelper(this);
    protected HashUserRealm realm;
    protected SecurityHandler security;
    public static final String ERROR_USER_ELEMENT_TOO_SHORT = "User element too short";
    public static final String ERROR_CONSTRAINT_LIST_LENGTH_WRONG = "Constraint list length wrong: entry#";
    private static final Reference CONSTRAINTS = new Reference(ATTR_CONSTRAINTS);
    private static final Reference USERS = new Reference(ATTR_USERS);
    private static final int ROLE_BASE = 3;
    public static final String ERROR_NO_SECURITY_CONSTRAINTS = "No security constraints were specified";

    public JettySecurityRealmImpl() throws RemoteException {
    }


    /**
     * Can be called to start components.
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        Server server = jettyHelper.bindToServer();

        Vector users = null, constraints = null;
        String name = sfResolve(ATTR_NAME, "", true);
        boolean checkWelcomeFiles = sfResolve(ATTR_CHECK_WELCOME_FILES, true, true);
        realm = new HashUserRealm(name);
        users = sfResolve(USERS, users, true);
        for (Object user : users) {
            Vector v = (Vector) user;
            if (v.size() < 3) {
                throw new SmartFrogResolutionException(sfCompleteName(), USERS, ERROR_USER_ELEMENT_TOO_SHORT);
            }
            String username = v.elementAt(0).toString();
            Object passwordEntry = v.elementAt(1);
            String password = PasswordHelper.extractPassword(this, USERS, passwordEntry);
            Password pass = new Password(password);
            realm.put(username, pass);
            //add the roles
            StringBuilder rolelist = new StringBuilder("[ ");
            for (int role = 2; role < v.size(); role++) {
                final String rolename = v.elementAt(role).toString();
                rolelist.append("'").append(rolename).append("' ");
                realm.addUserToRole(username, rolename);
            }
            rolelist.append("]");
            sfLog().info("Added User " + username
                    + " in roles " + rolelist);
        }
        //now the constraints
        constraints = sfResolve(CONSTRAINTS, constraints, true);
        ConstraintMapping[] mappings = new ConstraintMapping[constraints.size()];
        if(constraints.size()==0) {
            throw new SmartFrogResolutionException(sfCompleteName(), ERROR_NO_SECURITY_CONSTRAINTS);
        }
        for (int entry = 0; entry < constraints.size(); entry++) {
            Vector oneConstraint = (Vector) constraints.elementAt(entry);
            int roleCount = oneConstraint.size() - ROLE_BASE;
            if (roleCount <= 0) {
                throw new SmartFrogResolutionException(sfCompleteName(), CONSTRAINTS,
                        ERROR_CONSTRAINT_LIST_LENGTH_WRONG + entry);
            }
            String constraintName = oneConstraint.elementAt(0).toString();
            String path = oneConstraint.elementAt(1).toString();
            String method = oneConstraint.elementAt(2).toString();
            if (method.trim().isEmpty()) {
                method = null;
            }
            String[] roles = new String[roleCount];
            for (int roleEntry = 0; roleEntry < roleCount; roleEntry++) {
                roles[roleEntry] = oneConstraint.elementAt(ROLE_BASE + roleEntry).toString();
            }
            Constraint cons = new JettyConstraint();
            cons.setName(constraintName);
            cons.setRoles(roles);
            cons.setAuthenticate(true);
            JettyConstraintMapping mapping = new JettyConstraintMapping();
            mapping.setPathSpec(path);
            mapping.setMethod(method);
            mapping.setConstraint(cons);
            mappings[entry] = mapping;
            sfLog().info("Added Constraint " + mapping.toString());
        }
        security = new JettySecurityHandler();
        String authentication = sfResolve(ATTR_AUTHENTICATION, "", true);
        security.setAuthMethod(authentication);
        security.setUserRealm(realm);
        security.setConstraintMappings(mappings);
        security.setCheckWelcomeFiles(checkWelcomeFiles);
        //server.addUserRealm(realm);
        server.addLifeCycle(security);
        //now, we have to insert this handler at the front of the list. Calling addHandler places it at 
        //the end, which only leads to java.lang.IllegalStateException: Committed in the stack traces
        jettyHelper.insertHandler(security);
        
    }


    /**
     * deregister the security entry
     *
     * @param status termination status
     */
    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (security != null) {
            Server server = jettyHelper.getServer();
            if (server != null) {
                server.removeLifeCycle(security);
                server.removeHandler(security);
                server.removeUserRealm(realm);
            }
        }
    }

    /**
     * This is just a security manager with a bit more logging of what is goin on.
     */
    private class JettySecurityHandler extends SecurityHandler {

        @Override
        public void doStart() throws Exception {
            super.doStart();
            sfLog().info("Starting security Handler with auth method " + getAuthMethod());
            if (getAuthenticator() == null) {
                throw new SmartFrogDeploymentException("Failed to start the security handler: "
                        + "unrecognised Authenticator method: " + getAuthMethod());
            }
        }

        @Override
        protected void doStop() throws Exception {
            sfLog().info("Stopping security Handler");
            super.doStop();
        }

        @Override
        public boolean checkSecurityConstraints(String pathInContext, Request request, Response response)
                throws IOException {
            boolean allowed = super.checkSecurityConstraints(pathInContext, request, response);
            if (sfLog().isDebugEnabled()) {
                if (allowed) {
                    sfLog().debug("Allowing request on " + pathInContext);
                } else {
                    sfLog().debug("Rejecting request on " + pathInContext);
                }
            }
            return allowed;
        }

        @Override
        public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
                throws IOException, ServletException {
            if (sfLog().isDebugEnabled()) {
                sfLog().info("Handling request to " + target);
            }
            super.handle(target, request, response, dispatch);
        }
    }

    /**
     * A constraint mapping with a useful string form
     */
    private static class JettyConstraintMapping extends ConstraintMapping {

        @Override
        public String toString() {
            return "Constraint " + getConstraint().toString() + " mapped to " + getPathSpec()
                    + (getMethod() != null ? (" for " + getMethod()) : "");
        }
    }

    /**
     * A constraint with a useful string form
     */
    private static class JettyConstraint extends Constraint {


        /* ------------------------------------------------------------ */
        public String toString() {
            String[] roles = getRoles();
            StringBuilder roledump = new StringBuilder();
            if (roles != null) {
                roledump.append(" roles[" + roles.length + "] : [ ");
                for (String role : roles) {
                    roledump.append("'");
                    roledump.append(role);
                    roledump.append("' ");
                }
                roledump.append("]");
            }
            return super.toString() + roledump + " authenticate:"+getAuthenticate();
        }
    }
}
