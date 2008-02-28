/**
 * (C) Copyright 2007 Hewlett-Packard Development Company, LP
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * <p/>
 * For more information: www.smartfrog.org
 */

package org.smartfrog.services.www;

import org.smartfrog.services.passwords.PasswordHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Vector;


/**
 * This is a factoring out of the liveness checking; a class that
 * reads in the URL details from its attributes, and configures a liveness
 * page checker from these values.
 */
public abstract class AbstractLivenessPageComponent extends PrimImpl implements HttpAttributes {

    /**
     * the class that contains all the checking code. This is on the side for
     * reuse in other components.
     */
    protected LivenessPageChecker livenessPage;
    public static final String EMPTY_USERNAME = "Empty "+ATTR_USERNAME+" attribute on a component which requires a username";


    protected AbstractLivenessPageComponent() throws RemoteException {
    }


    public LivenessPageChecker getLivenessPage() {
        return livenessPage;
    }

    /**
     * Create and configure the LivenessChecker from the attributes of the component
     * @throws RemoteException network problems
     * @throws SmartFrogException smartfrog problems
     */
    protected void buildLivenessChecker() throws
            RemoteException,
            SmartFrogException {
        livenessPage = new LivenessPageChecker(this);

        String url = sfResolve(ATTR_URL, (String) null, false);

        if (url != null && url.length()>0) {
            livenessPage.bindToURL(url);
        } else {
            livenessPage.setHost(sfResolve(ATTR_HOST,
                    livenessPage.getHost(),
                    true));
            livenessPage.setPort(sfResolve(ATTR_PORT,
                    livenessPage.getPort(),
                    true));
            livenessPage.setProtocol(sfResolve(ATTR_PROTOCOL,
                    livenessPage.getProtocol(),
                    true));
            livenessPage.setPath(sfResolve(ATTR_PATH,
                    livenessPage.getPath(),
                    false));
            livenessPage.setPage(sfResolve(ATTR_PAGE,
                    livenessPage.getPage(),
                    false));
            buildLivenessPageAuthentication();
            buildLivenessPageQueryString();
        }

        Vector<String> mimeTypes = ListUtils.resolveStringList(this, new Reference(ATTR_MIME_TYPES),true);
        livenessPage.setMimeTypes(mimeTypes);
        livenessPage.setMinimumResponseCode(sfResolve(ATTR_MINIMUM_RESPONSE_CODE,
                0,true));
        livenessPage.setMaximumResponseCode(sfResolve(ATTR_MAXIMUM_RESPONSE_CODE,
                0, true));
        livenessPage.setFollowRedirects(sfResolve(ATTR_FOLLOW_REDIRECTS,
                livenessPage.getFollowRedirects(), true));
        livenessPage.setFetchErrorText(sfResolve(ATTR_ERROR_TEXT,
                livenessPage.getFetchErrorText(), true));
        //regular expression: this may raise an exception
        livenessPage.setResponseRegexp(sfResolve(ATTR_RESPONSE_REGEXP,"", true));
        livenessPage.setLogResponse(sfResolve(ATTR_LOGRESPONSE, false, false));

        //header vector
        Vector<Vector<String>> headers;
        headers= ListUtils.resolveStringTupleList(this,new Reference(ATTR_HEADERS),true);
        livenessPage.setHeaders(headers);

        livenessPage.setConnectTimeout(sfResolve(ATTR_CONNECT_TIMEOUT,0,true));



        //now tell the liveness page it is deployed
        livenessPage.onStart();
        if (url == null) {
            //set the URL if it was not already set
            URL targetURL = livenessPage.getTargetURL();
            sfReplaceAttribute(ATTR_URL, targetURL.toString());
        }
    }

    protected void buildLivenessPageQueryString()
            throws SmartFrogException, RemoteException {
        Vector queries = sfResolve(ATTR_QUERIES, (Vector) null, false);
        livenessPage.buildQueryString(queries);
    }

    /**
     * Configure the liveness page authentication: username and password
     * if the username is supplied
     * @throws SmartFrogException for trouble, including a missing password
     * @throws RemoteException network trouble
     */
    protected void buildLivenessPageAuthentication()
            throws SmartFrogException, RemoteException {
        boolean required=sfResolve(ATTR_AUTH_REQUIRED,false,true);
        String username = resolveUsername(required);

        if (username != null && username.length()>0) {
            String password = resolvePassword();
            livenessPage.setUsername(username);
            livenessPage.setPassword(password);
        }
    }

    /**
     * Resolve the password
     * @return the password
     * @throws SmartFrogException for trouble, including a missing password
     * @throws RemoteException network trouble
     */
    protected String resolvePassword()
            throws SmartFrogException, RemoteException {
        return PasswordHelper.resolvePassword(this,ATTR_PASSWORD,true);
    }

    /**
     * Resolve the username
     * @param required flag to set to true if the username is required
     * @return the username or null
     * @throws SmartFrogResolutionException for failure to resolve
     * @throws RemoteException network trouble
     */
    protected String resolveUsername(boolean required)
            throws SmartFrogResolutionException, RemoteException {
        String username = sfResolve(ATTR_USERNAME, (String) null, required);
        if (required && username.length() == 0) {
            throw new SmartFrogResolutionException(sfCompleteName(), EMPTY_USERNAME);
        }
        return username;
    }

    /**
     * @return string form for this component
     */
    public String toString() {
        //delegate
        if (livenessPage != null) {
            return livenessPage.toString();
        } else {
            return "undeployed liveness checker";
        }
    }
}
