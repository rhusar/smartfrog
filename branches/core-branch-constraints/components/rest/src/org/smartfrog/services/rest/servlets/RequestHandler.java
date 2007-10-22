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

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import org.smartfrog.SFSystem;
import org.smartfrog.services.rest.Restful;
import org.smartfrog.services.rest.XmlConstants;
import org.smartfrog.services.rest.data.AttributeStub;
import org.smartfrog.services.rest.data.ComponentStub;
import org.smartfrog.services.rest.data.ResolutionResult;
import org.smartfrog.services.rest.exceptions.InvalidURIException;
import org.smartfrog.services.rest.exceptions.MethodNotSupportedException;
import org.smartfrog.services.rest.exceptions.RestException;
import org.smartfrog.services.rest.wrappers.RESTWrapperFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.security.SFGeneralSecurityException;
import org.smartfrog.sfcore.reference.Reference;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;

/**
 * Used to handle all incoming requests within the SmartFrog REST interface.
 *
 * @author Derek Mortimer
 * @version 1.0
 */
public class RequestHandler extends HttpServlet
{
	/**
	 * Performs initialisation tasks for the Servlet. Namely,
	 * ensures the local SmartFrog system has been initialised
	 * and is ready for reference resolution.
	 *
	 * @throws ServletException If an unexpected error occurs during the initialisation.
	 */
	public void init() throws ServletException
	{
		try
		{
			SFSystem.initSystem();
		}
		catch (SmartFrogException sfe)
		{
			throw new ServletException("Unable to initialise SmartFrog Daemon.", sfe);
		}
		catch (SFGeneralSecurityException sfgse)
		{
			throw new ServletException("A security exception prohibited the SmartFrog Daemon from being initialised.", sfgse);
		}
	}

	/**
	 * Performs an HTTP DELETE request on the resource specified by the URI. Default behaviour
	 * is that attributes, descriptions and references are removed using sfRemoveAttribute and
	 * components are removed using sfDetachAndTerminate.
	 */
	public void doDelete(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException
	{
		try
		{
			// Generated Rest Request and Response objects for transmission within an SF tree
			HttpRestRequest	restRequest		= new HttpRestRequest(servletRequest);
			HttpRestResponse restResponse	= new HttpRestResponse();

			// Use the request to resolve the subject and its owner
			ResolutionResult result = resolveResources(restRequest);

			// If the owner implements Restful, directly call the method, otherwise, wrap and execute.
			if (result.getSubject() instanceof Restful)
				((Restful) result.getSubject()).doDelete(restRequest, restResponse);
			else
				RESTWrapperFactory.wrap(result, restRequest).doDelete(restRequest, restResponse);


			// The rest request and response objects should now contain all of the
			// information relative to this service request, so update the servlet
			// request/response objects and serve the data.
			restRequest.update(servletRequest);
			restResponse.update(servletResponse);

			// A buffered output stream is used incase the return content is binary
			BufferedOutputStream os = new BufferedOutputStream(servletResponse.getOutputStream());
			os.write(restResponse.getContents());

			// flush and close
			os.close();
		}
		// SMARTFROG REST INTERFACE EXCEPTIONS
		catch (InvalidURIException iurie)
		{
			reportException(iurie, servletResponse, HttpServletResponse.SC_NOT_FOUND);
		}
		catch (SmartFrogResolutionException sfre)
		{
			reportException(sfre, servletResponse, HttpServletResponse.SC_NOT_FOUND);
		}
		catch (MethodNotSupportedException mnse)
		{
			reportException(mnse, servletResponse, HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		// CATCH ALL FOR OTHER EXCEPTIONS
		catch (Exception e)
		{
			reportException(e, servletResponse);
		}
	}

	/**
	 * Performs an HTTP GET request on the resource specified by the URI. Default behaviour is
	 * simple to return the value of getXmlRepresentation as specified in the {@link Restful}
	 * interface.
	 */
	public void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException
	{
		try
		{
			HttpRestRequest	restRequest		= new HttpRestRequest(servletRequest);
			HttpRestResponse restResponse	= new HttpRestResponse();

			ResolutionResult result = resolveResources(restRequest);

			if (result.getSubject() instanceof Restful)
				((Restful) result.getSubject()).doGet(restRequest, restResponse);
			else
				RESTWrapperFactory.wrap(result, restRequest).doGet(restRequest, restResponse);


			restRequest.update(servletRequest);
			restResponse.update(servletResponse);

			BufferedOutputStream os = new BufferedOutputStream(servletResponse.getOutputStream());
			os.write(restResponse.getContents());

			os.close();
		}
		// SMARTFROG REST INTERFACE EXCEPTIONS
		catch (InvalidURIException iurie)
		{
			reportException(iurie, servletResponse, HttpServletResponse.SC_NOT_FOUND);
		}
		catch (SmartFrogResolutionException sfre)
		{
			reportException(sfre, servletResponse, HttpServletResponse.SC_NOT_FOUND);
		}
		catch (MethodNotSupportedException mnse)
		{
			reportException(mnse, servletResponse, HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		// CATCH ALL FOR OTHER EXCEPTIONS
		catch (Exception e)
		{
			reportException(e, servletResponse);
		}
	}

	/**
	 * Performs an HTTP POST request on the resource specified by the URI. Default behaviour is
	 * to parse the incoming contents as a {@link ComponentDescription}, {@link Reference} or boxed
	 * primitive object (that is, Integer, Boolean et cetera) and then act accordingly (that is,
	 * store as an attribute or deploy as a component). POST requests will generate an exception
	 * if the target resource already exists.
	 */
	public void doPost(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException
	{
		try
		{
			HttpRestRequest	 restRequest	= new HttpRestRequest(servletRequest);
			HttpRestResponse restResponse	= new HttpRestResponse();

			// perform incoming XML validation before we continue
			ParsedResourceRequest resourceRequest		= new ParsedResourceRequest(restRequest);

			// the second parameter forces the creation of stub
			// objects should the target resource not already exist.
			ResolutionResult result = resolveResources(restRequest, true);

			// post requests are forbidden on resources that already exist
			if (!((result.getSubject() instanceof AttributeStub) || (result.getSubject() instanceof ComponentStub)))
				throw new MethodNotSupportedException("POST requests are forbidden on resources that already exist");

			if (result.getSubject() instanceof Restful)
				((Restful) result.getSubject()).doPost(restRequest, restResponse);
			else
				RESTWrapperFactory.wrap(result, restRequest).doPost(restRequest, restResponse);

			restRequest.update(servletRequest);
			restResponse.update(servletResponse);

			BufferedOutputStream os = new BufferedOutputStream(servletResponse.getOutputStream());
			os.write(restResponse.getContents());

			os.close();
		}
		// SMARTFROG REST INTERFACE EXCEPTIONS
		catch (InvalidURIException iurie)
		{
			reportException(iurie, servletResponse, HttpServletResponse.SC_NOT_FOUND);
		}
		catch (SmartFrogResolutionException sfre)
		{
			reportException(sfre, servletResponse, HttpServletResponse.SC_NOT_FOUND);
		}
		catch (MethodNotSupportedException mnse)
		{
			reportException(mnse, servletResponse, HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		// CATCH ALL FOR OTHER EXCEPTIONS
		catch (Exception e)
		{
			reportException(e, servletResponse);
		}
	}

	/**
	 * Performs an HTTP PUT request on the resource specified by the URI.
     * Default behaviour is
	 * to parse the incoming contents as a {@link ComponentDescription}, {@link Reference} or boxed
	 * primitive object (that is, Integer, Boolean et cetera) and then act accordingly (that is,
	 * store as an attribute or deploy as a component). PUT requests may over-write existing attributes
	 * but may cause exceptions if attempts are made to redeploy identically named components.
	 */
	public void doPut(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException
	{
		try
		{
			HttpRestRequest	 restRequest	= new HttpRestRequest(servletRequest);
			HttpRestResponse restResponse	= new HttpRestResponse();

			ParsedResourceRequest resourceRequest		= new ParsedResourceRequest(restRequest);

			ResolutionResult result = resolveResources(restRequest, true);

			if (result.getSubject() instanceof Restful)
				((Restful) result.getSubject()).doPut(restRequest, restResponse);
			else
				RESTWrapperFactory.wrap(result, restRequest).doPut(restRequest, restResponse);

			restRequest.update(servletRequest);
			restResponse.update(servletResponse);

			BufferedOutputStream os = new BufferedOutputStream(servletResponse.getOutputStream());
			os.write(restResponse.getContents());

			os.close();
		}
		// SMARTFROG REST INTERFACE EXCEPTIONS
		catch (InvalidURIException iurie)
		{
			reportException(iurie, servletResponse, HttpServletResponse.SC_NOT_FOUND);
		}
		catch (SmartFrogResolutionException sfre)
		{
			reportException(sfre, servletResponse, HttpServletResponse.SC_NOT_FOUND);
		}
		catch (MethodNotSupportedException mnse)
		{
			reportException(mnse, servletResponse, HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		// CATCH ALL FOR OTHER EXCEPTIONS
		catch (Exception e)
		{
			reportException(e, servletResponse);
		}
	}


	/**
	 * Given a valid HttpRestRequest, this utility function will obtain references
	 * to the subject of the request and its owner.
	 */
	private ResolutionResult resolveResources(HttpRestRequest restRequest) throws Exception
	{
		return resolveResources(restRequest, false);
	}

	/**
	 * Given a valid HttpRestRequest, this utility function will obtain references
	 * to the subject of the request and its owner. createStubs is a parameter, which,
	 * when set to true means that if the subject identified by this request does not
	 * exist, a stub object of the right type should be created (that is, for PUT and
	 * POST requests) so the application can continue to function.
	 */
	private ResolutionResult resolveResources(HttpRestRequest restRequest, boolean createStubs) throws Exception
	{
		ProcessCompound rootProcess =
			SFProcess.getRootLocator().getRootProcessCompound(
						InetAddress.getByName(restRequest.getTargetHostname()),		// Address
						restRequest.getTargetPort()									// Port Number
					);

		Object owner = null, subject;
		String[] path = restRequest.getTargetResourcePath();

		try
		{
			if (path.length == 0)
			{
				owner = rootProcess;
				subject = rootProcess;
			}
			else if (path.length == 1)
			{
				owner = rootProcess;

				// we need to use get to obtain Reference objects, we can later resolve
				// these as per the preference of the user (i.e. ?followReferences=<true|false>)
				subject = rootProcess.sfContext().get(restRequest.getTargetResourceName());

				// throw an exception so our handler still functions correctly
				if (subject == null)
					throw new SmartFrogResolutionException(restRequest.getTargetResourceName() + " could not be" +
							" found in the specified parent resource");
			}
			else
			{
				owner = rootProcess.sfResolve(HttpRestRequest.buildReference(path, path.length - 1));

				if (!((owner instanceof Prim) || (owner instanceof ComponentDescription)))
					throw new InvalidURIException("The owner of the resource specified (" + path[path.length - 2] + ")" +
							" is not a traversable SmartFrog component.");
                if (owner instanceof Prim) {
                    subject = ((Prim)owner).sfContext().get(restRequest.getTargetResourceName());
                } else {
                    subject = ((ComponentDescription)owner).sfContext().get(restRequest.getTargetResourceName());
                }

				if (subject == null)
					throw new SmartFrogResolutionException(restRequest.getTargetResourceName() + " could not be" +
							" found in the specified parent resource");
			}

			if (restRequest.getMethod().equalsIgnoreCase("GET") &&
				restRequest.getFollowReferences() &&
				(subject instanceof Reference))
			{
				// Resolve the given reference with respect to its owner
				subject = (owner instanceof Prim) ?
							((Prim) owner).sfResolve((Reference) subject) :
							((ComponentDescription) owner).sfResolve((Reference) subject);
			}
		}
		catch (SmartFrogResolutionException sfre)
		{
			/*
			 *	This utility function is designed for use with resolution on
			 *	requests for both existing and non-existing resources. This
			 *	means when a resolution exception arises, it is possible the
			 *	situation is as it should be and the exception should only be
			 *	allowed to bubble up under certain circumstances
			 */
			if ((owner == null) || (!createStubs))
				throw sfre;

			// being here means we do not want the event to bubble so we need to
			// process the incoming xml to ascertain desired resource type
			if (restRequest.getContents().length == 0)
				throw new RestException("createStubs cannot be set to true when no XML request is provided" +
						" to ascertain resource type");

			ParsedResourceRequest resourceRequest = new ParsedResourceRequest(restRequest);

			if (resourceRequest.getTargetType().matches("^(component|description)$"))
				subject = new ComponentStub();
			else
				subject = new AttributeStub();
		}

		return new ResolutionResult(subject, owner, rootProcess);
	}

	/**
	 * Given an exception and an HttpServletResponse instance, this utility method will
	 * transcode an exception into XML format and present it to the user along with
	 * an HTTP 500 Internal Server Error response.
	 */
	private void reportException(Throwable t, HttpServletResponse servletResponse) throws ServletException
	{
		reportException(t, servletResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	/**
	 * As with the previous method except the user is able to define their own HTTP status code
	 * used in the response.
	 */
	private void reportException(Throwable t, HttpServletResponse servletResponse, int statusCode) throws ServletException
	{
		try
		{
			Element root = new Element("exception");

			// Exception type
			Attribute eClass = new Attribute("class", t.getClass().getName());
			root.addAttribute(eClass);

			// Display cause (if defined)
			if (t.getCause() != null)
			{
				String cause = t.getCause().getClass().getName();
				Attribute cClass = new Attribute("cause", cause);

				root.addAttribute(cClass);
			}

			// Display informative message (if defined)
			if (t.getMessage() != null)
			{
				Element message = new Element("message");

				message.appendChild(t.getMessage());
				root.appendChild(message);
			}

			// Generate stack trace
			Element trace = new Element("trace");

			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			t.printStackTrace(printWriter);

			trace.appendChild(result.toString());
			root.appendChild(trace);

			// build XML document
			Document d = new Document(root);

			// wipe the response and send the message
			servletResponse.reset();
			servletResponse.setStatus(statusCode);
			servletResponse.setContentType(XmlConstants.APPLICATION_XML);
			servletResponse.setContentLength(d.toXML().length());
			servletResponse.getWriter().print(d.toXML());
		}
		catch (Throwable unhandleable) {
			throw new ServletException("An exception was encountered while attempting to report on an exception" +
					" encountered during the processing of the request. As a result, the system is unable to " +
					" continue processing this request.", unhandleable);
		}
	}
}
