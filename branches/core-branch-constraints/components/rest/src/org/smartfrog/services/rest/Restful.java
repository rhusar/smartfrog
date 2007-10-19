/**
	(C) Copyright 2006 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.rest;

import nu.xom.Document;
import org.smartfrog.services.rest.exceptions.MethodNotSupportedException;
import org.smartfrog.services.rest.exceptions.RestException;
import org.smartfrog.services.rest.servlets.HttpRestRequest;
import org.smartfrog.services.rest.servlets.HttpRestResponse;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines a set of methods which must be implemented
 * to make a resource manageable in a HTTP-REST based
 * context. As per the REST recommendations the HTTP
 * operations supported are GET, PUT, POST and DELETE.
 *
 * @author Derek Mortimer &lt;derek.mortimer@hp.com&gt;
 * @version 1.0
 */
public interface Restful extends Remote
{
	/**
	 * Performs an HTTP GET request on this resource. As per
	 * the HTTP 1.1 RFC, a GET request should simply return
	 * a representation of the resource and also cause no
	 * alterations to the resource (that is, be idompotent).
	 *
	 * @param restRequest Serializable request object containing all request related data.
	 * @param restResponse Serializable response object containing all response related data.
	 * @throws MethodNotSupportedException When the requested HTTP operation cannot be carried out on this resource.
	 * @throws RemoteException When a network error occurs during attempted remote invocation of this method.
	 * @throws RestException When an internal exception occurs that should be reported to the user.
	 */
	public void doGet(HttpRestRequest restRequest, HttpRestResponse restResponse)
			throws MethodNotSupportedException, RemoteException, RestException;

	/**
	 * Performs an HTTP PUT request on this resource. As per
	 * the HTTP 1.1 RFC, ATOM Specification and REST recommendations,
	 * PUT is used to create a new resource or update one if it exists.
	 * Multiple successive identical PUT requests should yield the same
	 * result (that is, be idompotent). This behaviour may not always
	 * be attainable within a SmartFrog system (for example, trying to PUT a
	 * component when one already exists).
	 *
	 * @param restRequest Serializable request object containing all request related data.
	 * @param restResponse Serializable response object containing all response related data.
	 * @throws MethodNotSupportedException When the requested HTTP operation cannot be carried out on this resource.
	 * @throws RemoteException When a network error occurs during attempted remote invocation of this method.
	 * @throws RestException When an internal exception occurs that should be reported to the user.
	 */
	public void doPut(HttpRestRequest restRequest, HttpRestResponse restResponse)
			throws MethodNotSupportedException, RemoteException, RestException;

	/**
	 * Performs an HTTP POST request on the specified resource. As
	 * per the HTTP 1.1 RFC, ATOM specification and REST recommendations,
	 * POST is used solely to create new resources within the system and
	 * will yield a MethodNotSupportedException (and consequently,
	 * an HTTP 405 response).
	 *
	 * @param restRequest Serializable request object containing all request related data.
	 * @param restResponse Serializable response object containing all response related data.
	 * @throws MethodNotSupportedException When the requested HTTP operation cannot be carried out on this resource.
	 * @throws RemoteException When a network error occurs during attempted remote invocation of this method.
	 * @throws RestException When an internal exception occurs that should be reported to the user.
	 */
	public void doPost(HttpRestRequest restRequest, HttpRestResponse restResponse)
			throws MethodNotSupportedException, RemoteException, RestException;

	/**
	 * Performs an HTTP DELETE request on the specified resource. As
	 * per the HTTP 1.1 RFC, a request of this nature will delete the
	 * resource named. This method is not idempotent in that, if a successive
	 * identical DELETE request is submitted, an HTTP 404 error will be
	 * generated as the resource will no longer exist within the system.
	 *
	 * @param restRequest Serializable request object containing all request related data.
	 * @param restResponse Serializable response object containing all response related data.
	 * @throws MethodNotSupportedException When the requested HTTP operation cannot be carried out on this resource.
	 * @throws RemoteException When a network error occurs during attempted remote invocation of this method.
	 * @throws RestException When an internal exception occurs that should be reported to the user.
	 */
	public void doDelete(HttpRestRequest restRequest, HttpRestResponse restResponse)
			throws MethodNotSupportedException, RemoteException, RestException;

	/**
	 * Constructs an XML representation of this resource, presumably
	 * to be used in servicing HTTP GET requests ({@link #doGet}).
	 *
	 * @return A well-formed XML document to be transmitted in an HTTP response.
	 * @throws RemoteException When a network error occurs during attempted remote invocation of this method.
	 */
	public Document getXMLRepresentation() throws RemoteException;
}
