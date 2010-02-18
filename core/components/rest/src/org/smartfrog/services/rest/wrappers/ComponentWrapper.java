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

package org.smartfrog.services.rest.wrappers;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import org.smartfrog.services.rest.Restful;
import org.smartfrog.services.rest.XmlConstants;
import org.smartfrog.services.rest.data.ComponentStub;
import org.smartfrog.services.rest.data.ResolutionResult;
import org.smartfrog.services.rest.exceptions.MethodNotSupportedException;
import org.smartfrog.services.rest.exceptions.RestException;
import org.smartfrog.services.rest.servlets.HttpRestRequest;
import org.smartfrog.services.rest.servlets.HttpRestResponse;
import org.smartfrog.services.rest.servlets.ParsedResourceRequest;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.Iterator;

/**
 * The component wrapper is used to encapsulate {@link ComponentDescription} and {@link Prim}
 * objects and their descendants.
 *
 * @author Derek Mortimer
 * @version 1.0
 */
public class ComponentWrapper implements Restful
{
	/**
	 * Creates a new instance of the wrapper and verifies the correctness of the parameters provided.
	 *
	 * @param result The result object, containing reference to the subject and its owner.
	 * @param restRequest The request object containing all necessary information for this request.
	 *
	 * @throws RemoteException If a network error occurs during remote invocation of this method.
	 */
	public ComponentWrapper(ResolutionResult result, HttpRestRequest restRequest) throws RemoteException
	{
		 subject = result.getSubject();

		if (!(	(subject instanceof Prim)			||
				(subject instanceof ComponentStub)	||
				(subject instanceof ComponentDescription)))
			throw new IllegalArgumentException("Subject is not a wrappable SmartFrog Component.\n" +
					"Expected descendant of Prim or ComponentDescription, got '" + subject.getClass().getName() + "'");

		this.result = result;
		this.restRequest = restRequest;

		//Object owner = result.getOwner();
		owner = result.getOwner();
		ownerContext = (owner instanceof Prim) ?
			((Prim) owner).sfContext() : ((ComponentDescription) owner).sfContext();

	}

	public void doDelete(HttpRestRequest restRequest, HttpRestResponse restResponse)
			throws MethodNotSupportedException, RemoteException, RestException
	{
		if (result.getSubject() instanceof Prim)
		{
			try
			{
				Reference rootReference = result.getRootProcessCompound().sfCompleteName();
				TerminationRecord tr = TerminationRecord.normal("REST HTTP DELETE Requested", rootReference);

				((Prim) result.getSubject()).sfDetachAndTerminate(tr);

				String response = HttpRestResponse.generateResponseXML("OK", "The selected component has been " +
						" detached from the SmartFrog tree and terminated.");

				restResponse.setContentType(XmlConstants.APPLICATION_XML);
				restResponse.setContentLength(response.length());
				restResponse.setContents(response.getBytes());
			}
			catch (Exception e)
			{
				throw new RestException(e.getMessage(), e);
			}
		}
		else
		{
			try
			{
				Context context = (result.getOwner() instanceof Prim) ?
						((Prim) result.getOwner()).sfContext() : ((ComponentDescription) result.getOwner()).sfContext();

				String targetName = restRequest.getTargetResourceName();
				if (owner instanceof Prim) {
					((Prim) owner).sfRemoveAttribute(targetName);
				} else {
					((ComponentDescription) owner).sfRemoveAttribute(targetName);
				}
				String response = HttpRestResponse.generateResponseXML("OK", "The selected component description has " +
						"been removed from the SmartFrog tree.");

				restResponse.setContentType(XmlConstants.APPLICATION_XML);
				restResponse.setContentLength(response.length());
				restResponse.setContents(response.getBytes());
				restResponse.setStringContents(response);
			}
			catch (Exception e)
			{
				throw new RestException(e.getMessage(), e);
			}
		}
	}

	public void doGet(HttpRestRequest restRequest, HttpRestResponse restResponse)
			throws MethodNotSupportedException, RemoteException, RestException
	{
		String responseType = restRequest.getresponseType();
		if (responseType == null || responseType.equals("XML")) {
			Document xmlResponse = getXMLRepresentation();

			restResponse.setContentType(XmlConstants.APPLICATION_XML);
			restResponse.setContentLength(xmlResponse.toXML().length());
			restResponse.setContents(xmlResponse.toXML().getBytes());
			restResponse.setDocument(xmlResponse);
			restResponse.setStringContents(xmlResponse.toXML());
		} 
	}

	public void doPost(HttpRestRequest restRequest, HttpRestResponse restResponse)
			throws MethodNotSupportedException, RemoteException, RestException
	{
		try
		{
			ParsedResourceRequest resourceRequest = new ParsedResourceRequest(restRequest);

			SFParser parser = new SFParser(resourceRequest.getParserLanguage());
			ComponentDescription description = parser.sfParseComponentDescription(resourceRequest.getPayload());
			
			String response;

			if (resourceRequest.getTargetType().equals("description"))
			{
				if (result.getOwner() instanceof Prim)
				{
					description.setPrimParent((Prim) result.getOwner());
				}
				else
				{
					description.setParent((ComponentDescription) result.getOwner());
				}


			if (owner instanceof Prim) {
				((Prim) owner).sfAddAttribute(restRequest.getTargetResourceName(), description);
			} else {
				((ComponentDescription) owner).sfAddAttribute(restRequest.getTargetResourceName(), description);
			}
				response = HttpRestResponse.generateResponseXML("OK", "The description was successfully parsed and" +
						" added to the SmartFrog tree as an attribute.");
			}
			else
			{
				ProcessCompound rootProcess = result.getRootProcessCompound();

				if (!(result.getOwner() instanceof Prim))
				{
					throw new Exception("Exception while attempting to deploy a description with a parent" +
							" that does not descend from Prim");
				}

				if (result.ownerIsRoot())
				{
					rootProcess.sfCreateNewApp(restRequest.getTargetResourceName(), description, null);
					response = HttpRestResponse.generateResponseXML("OK", "The component was successfully deployed" +
							" as a new application on the specified target daemon.");
				}
				else
				{
					rootProcess.sfCreateNewChild(restRequest.getTargetResourceName(), (Prim) result.getOwner(), description, null);
					response = HttpRestResponse.generateResponseXML("OK", "The component was successfully deployed" +
							"as a child of the specified parent on the requested SmartFrog Daemon");
				}
			}
			restResponse.setContentType(XmlConstants.APPLICATION_XML);
			restResponse.setContentLength(response.length());
			restResponse.setContents(response.getBytes());
			restResponse.setStringContents(response);
		}
		catch (Exception e)
		{
			throw new RestException(e.getMessage(), e);
		}
	}

	public void doPut(HttpRestRequest restRequest, HttpRestResponse restResponse)
			throws MethodNotSupportedException, RemoteException, RestException
	{
		try
		{
			ParsedResourceRequest resourceRequest = new ParsedResourceRequest(restRequest);

			SFParser parser = new SFParser(resourceRequest.getParserLanguage());
			ComponentDescription description = parser.sfParseComponentDescription(resourceRequest.getPayload());

			String response;

			if (resourceRequest.getTargetType().equals("description"))
			{
				if (result.getOwner() instanceof Prim)
				{
					description.setPrimParent((Prim) result.getOwner());
				}
				else
				{
					description.setParent((ComponentDescription) result.getOwner());
				}

			if (owner instanceof Prim) {
				((Prim) owner).sfReplaceAttribute(restRequest.getTargetResourceName(), description);
			} else {
				((ComponentDescription) owner).sfReplaceAttribute(restRequest.getTargetResourceName(), description);
			}

				response = HttpRestResponse.generateResponseXML("OK", "The description was successfully parsed and" +
						" added to the SmartFrog tree as an attribute.");
			}
			else
			{
				ProcessCompound rootProcess = result.getRootProcessCompound();

				if (!(result.getOwner() instanceof Prim))
				{
					throw new RestException("Exception while attempting to deploy a description with a parent" +
							" that does not descend from Prim");
				}

				if (result.ownerIsRoot())
				{
					rootProcess.sfCreateNewApp(restRequest.getTargetResourceName(), description, null);
					response = HttpRestResponse.generateResponseXML("OK", "The component was successfully deployed" +
							" as a new application on the specified target daemon.");
				}
				else
				{
					rootProcess.sfCreateNewChild(restRequest.getTargetResourceName(), (Prim) result.getOwner(), description, null);
					response = HttpRestResponse.generateResponseXML("OK", "The component was successfully deployed" +
							"as a child of the specified parent on the requested SmartFrog Daemon");
				}
			}

			restResponse.setContentType(XmlConstants.APPLICATION_XML);
			restResponse.setContentLength(response.length());
			restResponse.setContents(response.getBytes());
			restResponse.setStringContents(response);
		}
        catch (RestException e) {
            throw e;
        }
        catch (Exception e)
		{
			throw new RestException(e.getMessage(), e);
		}
	}

	public Document getXMLRepresentation() throws RemoteException
	{
		Element root = new Element("resource");

		String resourceType = "";

		if (result.getSubject() instanceof Prim)
			resourceType = "component";
		else if (result.getSubject() instanceof ComponentDescription)
			resourceType = "description";
		else if (result.getSubject() instanceof Reference)
			resourceType = "reference";
		else
			resourceType = "attribute";

		String resourceLink = restRequest.getScheme() + "://" + restRequest.getServerName() + ":" +
				restRequest.getServerPort() + restRequest.getRequestURI();
				//restRequest.getServerPort() + restRequest.getContextPath() + restRequest.getRequestURI();

		// each resource has a name, type, class and link
		Attribute rName =	new Attribute("name", restRequest.getTargetResourceName());
		Attribute rType =	new Attribute("type", resourceType);
		Attribute rClass =	new Attribute("class", result.getSubject().getClass().getName());
		Attribute rLink =	new Attribute("href", resourceLink);

		root.addAttribute(rName);
		root.addAttribute(rType);
		root.addAttribute(rClass);
		root.addAttribute(rLink);

		Context context = (result.getSubject() instanceof Prim) ?
				((Prim) result.getSubject()).sfContext() : ((ComponentDescription) result.getSubject()).sfContext();
		try
		{
			for (Iterator i = context.sfAttributes(); i.hasNext();)
			{
				Object key = i.next();
				Object val = context.sfResolveAttribute(key);

				Element subResource = new Element("subresource");

				String subResourceType = "";

				if (val instanceof Prim)
					subResourceType = "component";
				else if (val instanceof ComponentDescription)
					subResourceType = "description";
				else if (val instanceof Reference)
					subResourceType = "reference";
				else
					subResourceType = "attribute";

				String subResourceLink = resourceLink + key;

				Attribute srName =	new Attribute("name", (String) key);
				Attribute srType =	new Attribute("type", subResourceType);
				Attribute srClass =	new Attribute("class", val.getClass().getName());
				Attribute srLink =	new Attribute("href", subResourceLink);

				subResource.addAttribute(srName);
				subResource.addAttribute(srType);
				subResource.addAttribute(srClass);
				subResource.addAttribute(srLink);

				root.appendChild(subResource);
			}
		}
		catch (SmartFrogContextException ignored) { }
		return new Document(root);
	}

	private final Object owner;
	private final Object subject;
	private Context ownerContext;
	private final ResolutionResult result;
	private final HttpRestRequest restRequest;
}
