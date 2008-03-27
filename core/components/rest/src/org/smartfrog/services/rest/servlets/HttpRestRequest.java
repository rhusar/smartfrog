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

package org.smartfrog.services.rest.servlets;

import org.smartfrog.services.rest.exceptions.InvalidURIException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class seeks to encapsulate the necessary information
 * about an HTTP request for use in servicing a request within
 * the SmartFrog REST interface. The {@link HttpServletRequest}
 * class can not be directly transmitted around a SmartFrog
 * system due to the containment of a stream.
 *
 * @author Derek Mortimer
 * @version 1.0
 */
public class HttpRestRequest implements Serializable
{
	/**
	 * Creates a new <code>HttpRestRequest</code> using the information
	 * contained within the <code>HttpServletRequest</code> object.
	 *
	 * @param servletRequest A reference to the object containing the incoming request information.
	 * @throws InvalidURIException If the provided URI is malformed beyond the point of coping.
	 */
	public HttpRestRequest(HttpServletRequest servletRequest) throws InvalidURIException
	{
		// store all of the regular HTTP request information
		requestURI = servletRequest.getRequestURI();
		requestMethod = servletRequest.getMethod();
		contextPath = servletRequest.getContextPath();
		servletPath = servletRequest.getServletPath();
		serverName = servletRequest.getServerName();
		serverPort = servletRequest.getServerPort();
		queryString = servletRequest.getQueryString();

		protocol = servletRequest.getProtocol();
		scheme = servletRequest.getScheme();
		secure = servletRequest.isSecure();

		contentLength = servletRequest.getContentLength();
		contentType = servletRequest.getContentType();
		characterEncoding = servletRequest.getCharacterEncoding();

		// store the incoming entity in a byte array,
		// to cope with possible binary submissions.
		byte[] temp = null;

		try
		{
			InputStream is = servletRequest.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			int c;
			while ((c = is.read()) != -1)
				bos.write(c);

			temp = bos.toByteArray();
		}
		catch (IOException ioe)
		{
			if (temp == null) temp = new byte[0];
		}
		finally
		{
			contents = temp;
		}

		headers = new HashMap();
		for (Enumeration e = servletRequest.getHeaderNames(); e.hasMoreElements();)
		{
			String key = (String) e.nextElement();
			String val = servletRequest.getHeader(key);

			headers.put(key, val);
		}

		parameters = new HashMap();
		for (Enumeration e = servletRequest.getParameterNames(); e.hasMoreElements();)
		{
			String key = (String) e.nextElement();
			String val = servletRequest.getParameter(key);

			parameters.put(key, val);
		}

		attributes = new HashMap();
		for (Enumeration e = servletRequest.getAttributeNames(); e.hasMoreElements();)
		{
			String key = (String) e.nextElement();
			Object val = servletRequest.getAttribute(key);

			attributes.put(key, val);
		}


		// Extract smartfrog-specific rest information from the incoming request

		String[] context = contextPath.replaceAll("(^/|/$)", "").split("/");
		
		// remove leading/trailing slashes from URI and explode on "/"
		String[] raw = requestURI.replaceAll("(^/|/$)", "").split("/");

		// we require at least "/<hostname>/<port number>/"
		if (raw.length < 2)
			throw new InvalidURIException("Insufficient information was supplied in the URI provided.");

		if (context.length == 0) {
		 	targetHost = raw[0];
			try {
				targetPort = Integer.parseInt(raw[1]);
			}
				catch (NumberFormatException nfe)
			{
				throw new InvalidURIException("The port specified (" + raw[1] + ") could not be parsed as an integer.");
			}
		} else {
			targetHost = raw[context.length];
			try {
				targetPort = Integer.parseInt(raw[context.length+1]);
			}
				catch (NumberFormatException nfe)
			{
				throw new InvalidURIException("The port specified (" + raw[1] + ") could not be parsed as an integer.");
			}
		}

		/*try
		{
			targetPort = Integer.parseInt(raw[1]);
		}
		catch (NumberFormatException nfe)
		{
			throw new InvalidURIException("The port specified (" + raw[1] + ") could not be parsed as an integer.");
		}*/

		if (context.length == 0) {
			
		// 2 parts means they're requesting the root context (i.e. an empty resource path)
			if (raw.length == 2)
			{
				resourcePath = new String[]{};
			}
			else
			{
				resourcePath = new String[raw.length - 2];

				for (int i = 2; i < raw.length; i++)
				{
					resourcePath[i - 2] = raw[i];
				}
			}

		} else {
			if ((raw.length - context.length) == 2)
			{
				resourcePath = new String[]{};
			}
			else
			{
				resourcePath = new String[raw.length - (context.length + 2)];

				for (int i = (context.length + 2) ; i < raw.length ; i++)
				{
					resourcePath[i - (context.length + 2)] = raw[i];
				}
			}
		}

		// expand - Option to force recursion into a components description when
		// constructing its XML tree. Currently unused.
		String param = servletRequest.getParameter("expand");
		expandAll = (!((param == null) || (param.equals("")))) && Boolean.valueOf(param).booleanValue();

		// followReferences, for GET requests, should we transparently resolve through references
		param = servletRequest.getParameter("followReferences");
		followReferences = ((param == null) || (param.length()==0)) || Boolean.valueOf(param).booleanValue();
	}

	/**
	 * Returns the length, in bytes, of the request body or -1 if the length is not known.
	 *
	 * @return An integer containing the length, in bytes, of the request body or -1 if the length is not known.
	 */
	public int getContentLength()
	{
		return contentLength;
	}

	/**
	 * Returns the MIME content type associated with the incoming request body.
	 *
	 * @return A String containing the MIME content type of the incoming request body.
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * Returns the character encoding associated with the incoming request body.
	 *
	 * @return A String containing the character encoding of the incoming request body.
	 */
	public String getCharacterEncoding()
	{
		return characterEncoding;
	}

	/**
	 * Returns the raw contents of the incoming request body.
	 *
	 * @return An array of bytes containing the raw data of the incoming request body.
	 */
	public byte[] getContents()
	{
		return contents;
	}

	/**
	 * Returns the HTTP method (that is, GET, PUT, POST or DELETE) associated with this request.
	 *
	 * @return A String containing the HTTP method associated with this request.
	 */
	public String getMethod()
	{
		return requestMethod;
	}

	/**
	 * Returns the HTTP protocol version (for example, HTTP/1.1) associated with this request.
	 *
	 * @return A String containing the HTTP protocol version associated with this request.
	 */
	public String getProtocol()
	{
		return protocol;
	}

	/**
	 * Returns the protocol scheme (for example, http or https) associated with this request.
	 *
	 * @return A String containing the protocol scheme associated with this request.
	 */
	public String getScheme()
	{
		return scheme;
	}

	/**
	 * Return the full URI associated with this request.
	 *
	 * @return A String containing the full URI associated with this request, always ending with '/'.
	 */
	public String getRequestURI()
	{
		if (requestURI.endsWith("/"))
			return requestURI;
		else
			return requestURI + "/";
	}

	/**
	 * Returns the context path associated with the currently
	 * executing web application. Used in construction of URIs.
	 *
	 * @return A String containing the context path associated with the executing web application.
	 */
	public String getContextPath()
	{
		return contextPath;
	}

	/**
	 * Returns the path associated with the currently executing servlet.
	 *
	 * @return A String containing the path associated with the currently executing servlet.
	 */
	public String getServletPath()
	{
		return servletPath;
	}

	/**
	 * Returns the full contents of the query string associated with this request.
	 *
	 * @return A String containing the query string associated with this request.
	 */
	public String getQueryString()
	{
		return queryString;
	}

	/**
	 * Returns all of the defined attribute names for this request.
	 *
	 * @return An {@link Iterator} whose contents represent all of the attribute names associated with this request.
	 */
	public Iterator getAttributeNames()
	{
		return attributes.keySet().iterator();
	}

	/**
	 * Returns the value of a named attribute.
	 *
	 * @param name The textual name of the attribute to be discovered.
	 * @return The object referenced by the textual name if it exists, <code>null</code> otherwise.
	 */
	public Object getAttribute(String name)
	{
		return attributes.get(name);
	}

	/**
	 * Returns all of the defined header names for this request.
	 *
	 * @return An Iterator whose contents represent all of the header names associated with this request.
	 */
	public Iterator getHeaderNames()
	{
		return headers.keySet().iterator();
	}

	/**
	 * Returns the value of a named header.
	 *
	 * @param name The textual name of the header to be discovered.
	 * @return The textual value of the header referenced by the textual name if it exists, <code>null</code> otherwise.
	 */
	public String getHeader(String name)
	{
		return (String) headers.get(name);
	}

	/**
	 * Returns all of the defined parameter names for this request.
	 *
	 * @return An Iterator whose contents represent all of the parameter names associated with this request.
	 */
	public Iterator getParameterNames()
	{
		return parameters.keySet().iterator();
	}

	/**
	 * Returns the full mapping of parameter names to their corresponding values.
	 *
	 * @return A {@link Map} whose contents is the complete mapping of parameter names to their values.
	 */
	public Map getParameterMap()
	{
		return parameters;
	}

	/**
	 * Returns the specific value of a named parameter.
	 *
	 * @param name The textual name of the parameter to be discovered.
	 * @return The textual value of the parameter referenced by the textual name if it exists, <code>null</code> otherwise.
	 */
	public String getParameter(String name)
	{
		return (String) parameters.get(name);
	}

	/**
	 * Returns the server name associated with this request.
	 *
	 * @return A String containing the name of the server on which this request is being processed.
	 */
	public String getServerName()
	{
		return serverName;
	}

	/**
	 * Returns the port number associated with this request.
	 *
	 * @return An integer containing the number of the port on which this request is being serviced.
	 */
	public int getServerPort()
	{
		return serverPort;
	}

	/**
	 * Ascertains whether the request desires deep expansion in the creation of
	 * XML representations of resources. Currently unused. Associated with the
	 * 'expand' request parameter.
	 *
	 * @return <code>true</code> if deep expansion is desired, <code>false</code> otherwise.
	 */
	public boolean getExpandAll()
	{
		return expandAll;
	}

	/**
	 * Ascertains whether the request desires transparent following of References
	 * within the SmartFrog system. Associated with the 'followReferences' request
	 * parameter.
	 *
	 * @return <code>true</code> if transparent following is desired, <code>false</code> otherwise.
	 */
	public boolean getFollowReferences()
	{
		return followReferences;
	}

	/**
	 * Returns the host name of the target SmartFrog Daemon specified in this request
	 *
	 * @return A string containing the name of the SmartFrog Daemon targetted by this request.
	 */
	public String getTargetHostname()
	{
		return targetHost;
	}

	/**
	 * Returns the port number of the target SmartFrog Daemon specified in this request
	 *
	 * @return An integer containing the port number of the SmartFrog Daemon targetted by this request.
	 */
	public int getTargetPort()
	{
		return targetPort;
	}

	/**
	 * Returns an array detailing the path, from root, to the smartfrog resource specified by this request.
	 *
	 * @return A String array whose contents represent the full set of steps from root to the target resource.
	 */
	public String[] getTargetResourcePath()
	{
		return resourcePath;
	}

	/**
	 * Returns the URI associated with the specific resource within the SmartFrog system. This URI will contain
	 * less information than the full request URI ({@link #getRequestURI}).
	 *
	 * @return A String containing the URI associated with the targetted SmartFrog resource. Only valid if the
	 * root context is known.
	 */
	public String getTargetResourceURI()
	{
		// full URI with the /<hostname>/<portNumber> prefix removed
		return getRequestURI().replaceFirst("^/.+/[\\d]{1,5}", "");
	}

	/**
	 * Identifies the textual name of the target resource for this request. This is the last item
	 * specified in the request URI.
	 *
	 * @return A string containing the name of the targetted resource for this request.
	 */
	public String getTargetResourceName()
	{
		if (resourcePath.length == 0)
		{
			return org.smartfrog.sfcore.common.SmartFrogCoreKeys.SF_ROOT_PROCESS;
		}
		else
		{
			return resourcePath[resourcePath.length - 1];
		}
	}

	/**
	 * Sets an attribute name/value pair for this request.
	 *
	 * @param name The textual name of the attribute to be stored.
	 * @param value The value of the attribute to be stored.
	 */
	public void setAttribute(String name, Object value)
	{
		attributes.put(name, value);
	}

	/**
	 * Remove a named attribute from this request.
	 *
	 * @param name The textual name of the attribute to be removed.
	 */
	public void removeAttribute(String name)
	{
		attributes.remove(name);
	}

	/**
	 * Ascertains if this request is being serviced over secure channels (for example, https or sftp).
	 *
	 * @return <code>true</code> if the request is deemed to be secure, <code>false</code> otherwise.
	 */
	public boolean isSecure()
	{
		return secure;
	}

	/**
	 * Copies all of the information contained within this object into a J2EE {@link HttpServletRequest} object
	 * so that it may be used appropriately for generating a response to the client.
	 *
	 * @param servletRequest The request to be updated with the contained information.
	 */
	public void update(HttpServletRequest servletRequest)
	{
		for (Iterator i = attributes.keySet().iterator(); i.hasNext();)
		{
			String key = (String) i.next();
			servletRequest.setAttribute(key, attributes.get(key));
		}
	}

	/**
	 * Constructs a {@link Reference} object from an array of strings for use in resolution. Each entry in the array
	 * is converted to a {@link ReferencePart} and added to an overall <code>Reference</code> object.
	 *
	 * @param path A string array whose parts will be used to construct a <code>Reference</code> object.
	 * @return A resolvable <code>Reference</code> object.
	 */
	public static Reference buildReference(String[] path)
	{
		return buildReference(path, path.length);
	}

	/**
	 * Constructs a <code>Reference</code> object using an array of strings but only navigating as far as the
	 * specified depth.
	 *
	 * @param path A string array whose parts will be used to construct a <code>Reference</code> object.
	 * @param depth The number of elements to be used in the <code>Reference</code> construction.
	 * @return A resolvable <code>Reference</code> object.
	 */
	public static Reference buildReference(String[] path, int depth)
	{
		if (depth >= path.length)
			throw new IllegalArgumentException("Depth specified is greater than the total length of the path array");

		Reference reference = new Reference();

		for (int i = 0; i < depth; i++) {
			reference.addElement(ReferencePart.attrib(path[i]));
		}

		return reference;
	}

	// members for standard HTTP request information
	private final String requestMethod;
	private final String requestURI;
	private final String contextPath;
	private final String servletPath;

	private final String serverName;
	private final int serverPort;

	private final String queryString;
	private final String protocol;
	private final String scheme;
	private final boolean secure;

	private final int contentLength;
	private final String contentType;
	private final byte[] contents;
	private final String characterEncoding;

	private final HashMap headers;
	private final HashMap parameters;
	private final HashMap attributes;

	// members for SmartFrog REST information
	private final boolean	expandAll;
	private final boolean	followReferences;
	private final String	targetHost;
	private final int		targetPort;
	private  String[] 	resourcePath = new String[]{};
}
