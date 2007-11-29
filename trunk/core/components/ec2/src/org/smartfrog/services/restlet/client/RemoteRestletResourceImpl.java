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
package org.smartfrog.services.restlet.client;

import org.restlet.Client;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.smartfrog.services.restlet.datasources.InprocDataSource;
import org.smartfrog.services.restlet.datasources.RestletDataSource;
import org.smartfrog.services.www.AbstractLivenessPageComponent;
import org.smartfrog.services.www.LivenessPageChecker;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.logging.LogSF;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Implementation of a reslet resource
 * Created 28-Nov-2007 17:30:37
 *
 */

public class RemoteRestletResourceImpl extends AbstractLivenessPageComponent
        implements RemoteRestletResource {

    private ChallengeScheme challengeScheme;
    private String username;
    private String password;
    private Protocol protocol;
    public static final String UNSUPPORTED_MEDIA_TYPE = "Unsupported media type:";
    public static final String UNKNOWN_VERB = "Unknown verb: ";
    public static final String ATTR_DATASOURCE = "datasource";
    public static final String ERROR_DIFFERENT_JVM = "Cannot access data from a data source in a different JVM";

    private List<String> startActions, terminateActions, livenessActions;
    private LogSF log;

    public RemoteRestletResourceImpl() throws RemoteException {
    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior. Attributees that require injection are handled during
     * sfDeploy().
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
        log = sfLog();
    }

    /**
     * Start up by creating the liveness checker
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();

        buildLivenessChecker();
        buildAuthentication();
        protocol = Protocol.valueOf(getLivenessPage().getTargetURL().getProtocol());

        startActions= ListUtils.resolveStringList(this,new Reference(ATTR_STARTACTIONS),true);
        livenessActions = ListUtils.resolveStringList(this,
                new Reference(ATTR_LIVENESSACTIONS),
                true);
        terminateActions = ListUtils.resolveStringList(this,
                new Reference(ATTR_TERMINATEACTIONS),
                true);

        execute(startActions);

        ComponentHelper helper = new ComponentHelper(this);

        //and do a termination if asked for
        helper.sfSelfDetachAndOrTerminate(null, getLivenessPage().getUrlAsString(), null, null);
    }

    /**
     * Liveness call in to check if this component is still alive.
     * This component executes its liveness actions -all of them!
     *
     * @param source source of call
     *
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException for consistency with the {@link Liveness}
     * interface
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        try {
            execute(livenessActions);
        } catch (SmartFrogException e) {
            throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(e);
        }
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        try {
            execute(terminateActions);
        } catch (RemoteException e) {
            log.ignore(e);
        } catch (SmartFrogException e) {
            log.ignore(e);
        }
    }

    /**
     * We skip authentication so it is not included in logs or other output.
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    protected void buildLivenessPageAuthentication()
            throws SmartFrogException, RemoteException {
        //do nothing here
    }


    /**
     * Do the real authentication setup here
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    protected void buildAuthentication()
            throws SmartFrogException, RemoteException {
        String auth = sfResolve(ATTR_AUTHORIZATION, "", true);
        challengeScheme = ChallengeScheme.valueOf(auth);

        username = resolveUsername();
        if (username != null) {
            password = resolvePassword();
        }
    }

    /**
     * Build tup a new request
     * @param method method
     * @param localData local data (can be null)
     * @return the request
     */
    protected Request buildRequest(Method method, Representation localData) {
        // Send an authenticated request
        Request request = new Request(method, getLivenessPage().getUrlAsString(), localData);
        request.setChallengeResponse(createChallengeResponse());
        return request;
    }

    /**
     * Create a new CR scheme using the local challenge scheme,
     * username and password
     * @return
     */
    protected ChallengeResponse createChallengeResponse() {
        return new ChallengeResponse(
                challengeScheme, username, password);
    }


    /**
     * Create a new client
     * @return the client
     */
    protected Client createClient() {
        return new Client(getProtocol());
    }

    /**
     * Get the protocol extracted from the URI
     * @return the protocol of the request
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * Create a client and handle the request
     * @param request the request
     * @return the response
     */
    protected Response handle(Request request) {
        Client client = createClient();
        return client.handle(request);
    }

    /**
     * Create a client and handle the request, then call {@link #validate(Response)} to check it
     *
     * @param method method to run
     * @param data any data
     * @return the response
     * @throws RestletOperationException for validation failures and errors
     */
    protected Response handleAndValidate(Method method,Representation data)
            throws RestletOperationException {
        Request request = buildRequest(method, data);
        Response response = handle(request);
        validate(response);
        return response;
    }

    /**
     * Check that the response was valid
     * @param response
     * @throws RestletOperationException for validation failures and errors
     */
    protected void validate(Response response)
            throws RestletOperationException {
        LivenessPageChecker checker = getLivenessPage();
        int responseCode = response.getStatus().getCode();
        if (checker.isStatusOutOfRange(responseCode)) {
            throw new RestletOperationException("Status code " + responseCode + " is out of range",
                    response,
                    this);
        }
        Representation responseData = response.getEntity();
        if (responseData != null) {
            String type = responseData.getMediaType().getName();
            if (!checker.isMimeTypeInRange(type)) {
                throw new RestletOperationException(UNSUPPORTED_MEDIA_TYPE + type,
                        response,
                        this);
            }
        }
    }


    protected Response get() throws RestletOperationException {
      return handleAndValidate(Method.GET, null);
    }

    protected Response head() throws RestletOperationException {
        return handleAndValidate(Method.HEAD, null);
    }

    protected Response options() throws RestletOperationException {
        return handleAndValidate(Method.OPTIONS, null);
    }

    protected Response delete() throws RestletOperationException {
        return handleAndValidate(Method.DELETE, null);
    }

    protected Response post(Representation data)
            throws RestletOperationException {
        return handleAndValidate(Method.DELETE, data);
    }

    protected Response put(Representation data) throws RestletOperationException {
        return handleAndValidate(Method.DELETE, data);
    }

    protected void execute(List<String> verbs)
            throws RemoteException, SmartFrogException {
        if (verbs == null) {
            return;
        }
        for (String verb : verbs) {
            execute(verb);
        }
    }

    protected Response execute(String verb)
            throws RemoteException, SmartFrogException {
        Response response=null;
        if(GET.equals(verb)) {
            response=get();
        } else if(HEAD.equals(verb)) {
            response=head();
        } else if (OPTIONS.equals(verb)) {
            response = options();
        } else if (POST.equals(verb)) {
            response = post(loadRepresentation());
        } else if (PUT.equals(verb)) {
            response = post(loadRepresentation());
        } else if (DELETE.equals(verb)) {
            response = delete();
        } else {
            throw new RestletOperationException(UNKNOWN_VERB +verb);
        }
        //now postprocess
        return response;
    }

    /**
     * Load the representation by locating the attached data source and
     * asking it for the data
     * @return the representation to upload
     * @throws RemoteException network problems
     * @throws SmartFrogException any other problem
     */
    public Representation loadRepresentation()
            throws RemoteException, SmartFrogException {
        RestletDataSource restletDataSource = (RestletDataSource) sfResolve(
                ATTR_DATASOURCE,
                (Prim) null,
                true);

        InprocDataSource inprocDataSource;
        try {
            inprocDataSource =(InprocDataSource) restletDataSource;
        } catch (ClassCastException e) {
            throw new SmartFrogException(ERROR_DIFFERENT_JVM,this);
        }
        Representation localData= inprocDataSource.loadRepresentation();
        return localData;
    }

}

