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

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.passwords.PasswordHelper;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.security.BasicAuthenticator;
import org.mortbay.jetty.Server;

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

    public JettySecurityRealmImpl() throws RemoteException {
    }


    /**
     * Can be called to start components.
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        jettyHelper.bindToServer();

        Vector users = null, constraints = null;
        String name = sfResolve(ATTR_NAME, "", true);
        realm = new HashUserRealm(name);
        users = sfResolve(USERS, users, true);
        for (Object user : users) {
            Vector v = (Vector) user;
            if (v.size() < 3) {
                throw new SmartFrogResolutionException(sfCompleteName(), USERS,ERROR_USER_ELEMENT_TOO_SHORT);
            }
            String username = v.elementAt(0).toString();
            Object passwordEntry = v.elementAt(1);
            String password = PasswordHelper.extractPassword(this, USERS, passwordEntry);
            realm.put(username, password);
            //add the roles
            for (int role = 2; role < v.size(); role++) {
                realm.addUserToRole(username, v.elementAt(role).toString());
            }
        }
        //now the constraints
        constraints = sfResolve(CONSTRAINTS, constraints, true);
        ConstraintMapping[] mappings = new ConstraintMapping[constraints.size()];
        for (int entry = 0; entry < constraints.size(); entry++) {
            Vector oneConstraint = (Vector) constraints.elementAt(entry);
            int roleCount = oneConstraint.size() - 2;
            if (roleCount <= 0) {
                throw new SmartFrogResolutionException(sfCompleteName(), CONSTRAINTS,
                        ERROR_CONSTRAINT_LIST_LENGTH_WRONG + entry);
            }
            String path = oneConstraint.elementAt(0).toString();
            String constraintName = oneConstraint.elementAt(1).toString();
            String[] roles = new String[roleCount];
            for (int roleEntry = 0; roleEntry < roleCount; roleEntry++) {
                roles[roleEntry] = oneConstraint.elementAt(2 + roleEntry).toString();
            }
            Constraint cons = new Constraint();
            cons.setName(constraintName);
            cons.setRoles(roles);
            ConstraintMapping mapping = new ConstraintMapping();
            mapping.setPathSpec(path);
            mapping.setConstraint(cons);
            mappings[entry] = mapping;
        }
        security = new SecurityHandler();
        security.setAuthenticator(new BasicAuthenticator());
        security.setConstraintMappings(mappings);
        jettyHelper.getServer().addLifeCycle(security);
        jettyHelper.getServer().addUserRealm(realm);
    }


    /**
     * deregister the security entry
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (security != null) {
            Server server = jettyHelper.getServer();
            if (server != null) {
                server.removeHandler(security);
                server.removeUserRealm(realm);
            }
        }
    }
}
