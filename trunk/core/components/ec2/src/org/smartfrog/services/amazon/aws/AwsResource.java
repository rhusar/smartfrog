/*
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

* Copyright 2005-2007 Noelios Consulting.
*
* The contents of this file are subject to the terms of the Common Development
* and Distribution License (the "License"). You may not use this file except in
* compliance with the License.
*
* You can obtain a copy of the license at
* http://www.opensource.org/licenses/cddl1.txt See the License for the specific
* language governing permissions and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each file and
* include the License file at http://www.opensource.org/licenses/cddl1.txt If
* applicable, add the following below this CDDL HEADER, with the fields
* enclosed by brackets "[]" replaced with your own identifying information:
* Portions Copyright [yyyy] [name of copyright owner]
*/
package org.smartfrog.services.amazon.aws;

import org.restlet.data.Response;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.resource.Representation;
import org.restlet.Client;




/**
 * this is a factoring out of the base resource concept from the S3 code.
  */
public class AwsResource {

    private String awsID;
    private String secretKey;

    /**
     * Create an operation; bind it to the specific user
     * @param awsID the AWS User ID
     * @param secretKey the secret key of the user.
     */
    public AwsResource(String awsID, String secretKey) {
        this.awsID = awsID;
        this.secretKey = secretKey;
    }

    public AwsResource(AwsResource parent) {
        this(parent.awsID,parent.secretKey);
    }

    private Response handleAuthorized(Method method, String uri,
            Representation entity) {
        // Send an authenticated request
        Request request = new Request(method, uri, entity);
        request.setChallengeResponse(new ChallengeResponse(
                ChallengeScheme.HTTP_AWS, awsID, secretKey));
        return new Client(Protocol.HTTPS).handle(request);
    }

    public Response authorizedHead(String uri) {
        return handleAuthorized(Method.HEAD, uri, null);
    }

    public Response authorizedGet(String uri) {
        return handleAuthorized(Method.GET, uri, null);
    }

    public Response authorizedPut(String uri, Representation entity) {
        return handleAuthorized(Method.PUT, uri, entity);
    }

    public Response authorizedDelete(String uri) {
        return handleAuthorized(Method.DELETE, uri, null);
    }

    public String getAwsID() {
        return awsID;
    }

    protected String getSecretKey() {
        return secretKey;
    }

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
        return "an AWS operation on behalf of "+awsID;
    }
}
