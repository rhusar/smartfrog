/* (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
import org.restlet.util.Series;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Parameter;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.smartfrog.services.restlet.datasources.InprocDataSource;
import org.smartfrog.services.restlet.datasources.RestletDataSource;
import org.smartfrog.services.www.AbstractLivenessPageComponent;
import org.smartfrog.services.www.LivenessPageChecker;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.logging.LogSF;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Implementation of a reslet resource Created 28-Nov-2007 17:30:37
 */

public class RemoteRestletResourceImpl extends AbstractLivenessPageComponent
        implements RemoteRestletResource {

    private ChallengeScheme challengeScheme;
    private String username;
    private String password;
    private Protocol protocol;
    private boolean followRedirects;
    public static final String UNSUPPORTED_MEDIA_TYPE = "Unsupported media type:";
    public static final String UNKNOWN_VERB = "Unknown verb: ";
    public static final String ATTR_DATASOURCE = "datasource";
    public static final String ERROR_DIFFERENT_JVM = "Cannot access data from a data source in a different JVM";

    private Vector<Vector<Object>> startActions, terminateActions, livenessActions;
    private LogSF log;
    private int readTimeout;

    public RemoteRestletResourceImpl() throws RemoteException {
    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is started and if there is a parent the
     * deployed component is added to the heartbeat. Subclasses can override to provide additional deployment behavior.
     * Attributees that require injection are handled during sfDeploy().
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
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
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();

        buildLivenessChecker();
        buildAuthentication();
        protocol = Protocol.valueOf(getLivenessPage().getTargetURL().getProtocol());
        followRedirects = getLivenessPage().getFollowRedirects();
        readTimeout = sfResolve(ATTR_READ_TIMEOUT, 0, true);

        startActions = ListUtils.resolveNTupleList(this,
                new Reference(ATTR_STARTACTIONS), 3, true);
        livenessActions = ListUtils.resolveNTupleList(this,
                new Reference(ATTR_LIVENESSACTIONS),
                3,
                true);
        terminateActions = ListUtils.resolveNTupleList(this,
                new Reference(ATTR_TERMINATEACTIONS),
                3,
                true);

        execute(startActions);

        ComponentHelper helper = new ComponentHelper(this);

        //and do a termination if asked for
        helper.sfSelfDetachAndOrTerminate(null, getURL(), null, null);
    }

    /**
     * Liveness call in to check if this component is still alive. This component executes its liveness actions -all of
     * them!
     *
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for network trouble
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
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
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
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    protected void buildLivenessPageAuthentication()
            throws SmartFrogException, RemoteException {
        //do nothing here
    }


    /**
     * Do the real authentication setup here
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    protected void buildAuthentication()
            throws SmartFrogException, RemoteException {
        String auth = sfResolve(ATTR_AUTHORIZATION, "", true);
        if (auth.length() != 0) {
            challengeScheme = ChallengeScheme.valueOf(auth);
            username = resolveUsername();
            password = resolvePassword();
        }

    }

    /**
     * Build up a new request
     *
     * @param method    method
     * @param localData local data (can be null)
     * @return the request
     */
    protected Request buildRequest(Method method, Representation localData) {
        // Send an authenticated request
        Request request = new Request(method, getURL(), localData);
        //request.
        if (challengeScheme != null) {
            request.setChallengeResponse(createChallengeResponse());
        }
        return request;
    }

    /**
     * Get the URL of the connection
     *
     * @return the URL
     */
    public String getURL() {
        return getLivenessPage().getUrlAsString();
    }

    /**
     * Create a new CR scheme using the local challenge scheme, username and password
     *
     * @return the CR scheme of this user.
     */
    protected ChallengeResponse createChallengeResponse() {
        return new ChallengeResponse(
                challengeScheme, username, password);
    }


    /**
     * Create a new client
     *
     * @return the client
     */
    protected Client createClient() {
        return new Client(getProtocol());
    }

    /**
     * Get the protocol extracted from the URI
     *
     * @return the protocol of the request
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * Create a client and handle the request
     *
     * @param request the request
     * @return the response
     */
    protected Response handle(Request request) {
        Client client = createClient();
        //add redirection support
        Series<Parameter> params = client.getContext().getParameters();
        params.add("followRedirects", Boolean.toString(followRedirects));
        params.add("readTimeout", Integer.toString(readTimeout));
        return client.handle(request);
    }

    /**
     * Create a client and handle the request, then call validate() to check it
     *
     * @param method method to run
     * @param data   any data
     * @return the response
     * @throws RestletOperationException for validation failures and errors
     */
    protected Response handleAndValidate(Method method, Representation data,
                                         int minResponseCode,
                                         int maxResponseCode)
            throws RestletOperationException {
        Request request = buildRequest(method, data);
        Response response = handle(request);
        validate(request, response, minResponseCode, maxResponseCode);
        return response;
    }

    /**
     * Check that the response was valid
     *
     * @param request         the request issues
     * @param response        the response we check
     * @param minResponseCode allowed min response code
     * @param maxResponseCode allowed max response code
     * @throws RestletOperationException for validation failures and errors
     */
    protected void validate(Request request, Response response,
                            int minResponseCode,
                            int maxResponseCode)
            throws RestletOperationException {
        LivenessPageChecker checker = getLivenessPage();
        Status status = response.getStatus();
        int responseCode = status.getCode();
        if (responseCode < minResponseCode || responseCode > maxResponseCode) {
            if(responseCode>=1000) {
                throw new RestletOperationException(request,
                        "Internal Restlet Error "+status.toString()+'\n'
                                +status.getDescription() + '\n'
                                +status.getUri(),
                        response,
                        this);
            }

            throw new RestletOperationException(
                    request,
                    "Status code " + responseCode +" ("+status.toString()+")"
                            + " is out of range of "
                            + minResponseCode
                            + '-'
                            + maxResponseCode,
                    response,
                    this);
        }
        Representation responseData = response.getEntity();
        if (responseData != null) {
            MediaType mediaType = responseData.getMediaType();
            if (mediaType == null) {
                //we have a null media type here. That may or may not be expected.
                log.debug("No media type in the response");

            } else {
                String type = mediaType.getName();
                if (!checker.isMimeTypeInRange(type)) {
                    throw new RestletOperationException(request,
                            UNSUPPORTED_MEDIA_TYPE + type,
                            response,
                            this);
                }
            }
        }
    }


    protected Response get(int minResponseCode, int maxResponseCode) throws RestletOperationException {
        return handleAndValidate(Method.GET, null, minResponseCode, maxResponseCode);
    }

    protected Response head(int minResponseCode, int maxResponseCode) throws RestletOperationException {
        return handleAndValidate(Method.HEAD, null, minResponseCode, maxResponseCode);
    }

    protected Response options(int minResponseCode, int maxResponseCode) throws RestletOperationException {
        return handleAndValidate(Method.OPTIONS, null, minResponseCode, maxResponseCode);
    }

    protected Response delete(int minResponseCode, int maxResponseCode) throws RestletOperationException {
        return handleAndValidate(Method.DELETE, null, minResponseCode, maxResponseCode);
    }

    protected Response post(Representation data, int minResponseCode, int maxResponseCode)
            throws RestletOperationException {
        return handleAndValidate(Method.POST, data, minResponseCode, maxResponseCode);
    }

    protected Response put(Representation data, int minResponseCode, int maxResponseCode)
            throws RestletOperationException {
        return handleAndValidate(Method.PUT, data, minResponseCode, maxResponseCode);
    }

    protected void execute(Vector<Vector<Object>> operations)
            throws RemoteException, SmartFrogException {
        if (operations == null) {
            return;
        }
        for (Vector<Object> operation : operations) {
            String verb = operation.get(0).toString();
            int minResponse = (Integer) operation.get(1);
            int maxResponse = (Integer) operation.get(2);
            execute(verb, minResponse, maxResponse);
        }
    }

    protected Response execute(String verb, int minResponseCode, int maxResponseCode)
            throws RemoteException, SmartFrogException {
        Response response = null;
        if (GET.equals(verb)) {
            response = get(minResponseCode, maxResponseCode);
        } else if (HEAD.equals(verb)) {
            response = head(minResponseCode, maxResponseCode);
        } else if (OPTIONS.equals(verb)) {
            response = options(minResponseCode, maxResponseCode);
        } else if (POST.equals(verb)) {
            response = post(loadRepresentation(), minResponseCode, maxResponseCode);
        } else if (PUT.equals(verb)) {
            response = put(loadRepresentation(), minResponseCode, maxResponseCode);
        } else if (DELETE.equals(verb)) {
            response = delete(minResponseCode, maxResponseCode);
        } else {
            throw new RestletOperationException(UNKNOWN_VERB + verb);
        }
        //now postprocess
        return response;
    }

    /**
     * Load the representation by locating the attached data source and asking it for the data
     *
     * @return the representation to upload
     * @throws RemoteException    network problems
     * @throws SmartFrogException any other problem
     */
    public Representation loadRepresentation()
            throws RemoteException, SmartFrogException {
        RestletDataSource restletDataSource = (RestletDataSource) sfResolve(
                ATTR_DATASOURCE,
                (Prim) null,
                false);
        if (restletDataSource == null) {
            return null;
        }

        InprocDataSource inprocDataSource;
        try {
            inprocDataSource = (InprocDataSource) restletDataSource;
        } catch (ClassCastException e) {
            throw new SmartFrogException(ERROR_DIFFERENT_JVM, this);
        }
        Representation localData = inprocDataSource.loadRepresentation();
        return localData;
    }

}

