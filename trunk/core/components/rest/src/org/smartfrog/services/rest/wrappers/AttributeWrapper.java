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
import org.smartfrog.services.rest.data.ResolutionResult;
import org.smartfrog.services.rest.exceptions.MethodNotSupportedException;
import org.smartfrog.services.rest.exceptions.RestException;
import org.smartfrog.services.rest.servlets.HttpRestRequest;
import org.smartfrog.services.rest.servlets.HttpRestResponse;
import org.smartfrog.services.rest.servlets.ParsedResourceRequest;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**
 * The attribute wrapper is used to encapsulate {@link Reference} and
 * all non-traversable SmartFrog objects and their descendants.
 *
 * @author Derek Mortimer
 * @version 1.0
 */
public class AttributeWrapper implements Restful
{
	/**
	 * Creates a new instance of the wrapper containing the subject and its owner.
	 *
	 * @param result The result containing a reference to the subject and its owner.
	 * @param restRequest The request object containing all of the necessary request information.
	 *
	 * @throws RemoteException If a network error occurs during attempt remote invocation of this method.
	 */
	public AttributeWrapper(ResolutionResult result, HttpRestRequest restRequest) throws RemoteException
	{
		this.result = result;
		this.restRequest = restRequest;

		owner = result.getOwner();
		//Object owner = result.getOwner();

		ownerContext = (owner instanceof Prim) ?
			((Prim) owner).sfContext() : ((ComponentDescription) owner).sfContext();
	}

	public void doDelete(HttpRestRequest restRequest, HttpRestResponse restResponse)
			throws MethodNotSupportedException, RemoteException, RestException
	{
		try
		{
			String targetName = restRequest.getTargetResourceName();

			String response;
			if (result.getSubject() instanceof Reference)
				response = HttpRestResponse.generateResponseXML("OK", "The specified Reference has been successfully" +
						" removed from the SmartFrog tree.");
			else
				response = HttpRestResponse.generateResponseXML("OK", "The specified attribute has been successfully" +
						" removed from the SmartFrog tree.");

			if (owner instanceof Prim) {
				((Prim) owner).sfRemoveAttribute(restRequest.getTargetResourceName());
			} else {
				((ComponentDescription) owner).sfRemoveAttribute(restRequest.getTargetResourceName());
			}
			restResponse.setContentType(XmlConstants.APPLICATION_XML);
			restResponse.setContentLength(response.length());
			restResponse.setContents(response.getBytes());
		}
		catch (Exception e)
		{
			throw new RestException(e.getMessage(), e);
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
		} else if (responseType.equals("HTML")){
			String result = getHTMLRepresentation();
			restResponse.setContentType("text/html");
			restResponse.setContentLength(result.length());
			restResponse.setContents(result.getBytes());
		}
	}

	public void doPost(HttpRestRequest restRequest, HttpRestResponse restResponse)
			throws MethodNotSupportedException, RemoteException, RestException
	{
		try
		{
			ParsedResourceRequest resourceRequest = new ParsedResourceRequest(restRequest);

			SFParser parser = new SFParser();

			Object data;
			String response;

			if (resourceRequest.getTargetType().equals("attribute"))
			{
				data = parser.sfParsePrimitiveValue(resourceRequest.getPayload());
				response = HttpRestResponse.generateResponseXML("OK", "The provided value was suscessfully parsed" +
						" as a primitive value and stored as an attribute.");
			}
			else
			{
				data = parser.sfParseReference(resourceRequest.getPayload());
				response = HttpRestResponse.generateResponseXML("OK", "The provided value was successfully parsed" +
						" as a valid SmartFrog reference and added to the tree.");
			}
			if (owner instanceof Prim) {
				((Prim) owner).sfAddAttribute(restRequest.getTargetResourceName(), data);
			} else {
				((ComponentDescription) owner).sfAddAttribute(restRequest.getTargetResourceName(), data);
			}

			restResponse.setContentType(XmlConstants.APPLICATION_XML);
			restResponse.setContentLength(response.length());
			restResponse.setContents(response.getBytes());
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

			SFParser parser = new SFParser();

			Object data;
			String response;

			if (resourceRequest.getTargetType().equals("attribute"))
			{
				data = parser.sfParsePrimitiveValue(resourceRequest.getPayload());
				response = HttpRestResponse.generateResponseXML("OK", "The provided value was suscessfully parsed" +
						" as a primitive value and stored as an attribute.");
			}
			else
			{
				data = parser.sfParseReference(resourceRequest.getPayload());
				response = HttpRestResponse.generateResponseXML("OK", "The provided value was successfully parsed" +
						" as a valid SmartFrog reference and added to the tree.");
			}

			if (owner instanceof Prim) {
				((Prim) owner).sfReplaceAttribute(restRequest.getTargetResourceName(), data);
			} else {
				((ComponentDescription) owner).sfReplaceAttribute(restRequest.getTargetResourceName(), data);
			}
			restResponse.setContentType(XmlConstants.APPLICATION_XML);
			restResponse.setContentLength(response.length());
			restResponse.setContents(response.getBytes());
		}
		catch (Exception e)
		{
			throw new RestException(e.getMessage(), e);
		}
	}

	public Document getXMLRepresentation() throws RemoteException
	{
		Element root = new Element("resource");

		String resourceLink = restRequest.getScheme() + "://" + restRequest.getServerName() + ":" +
				restRequest.getServerPort() + restRequest.getRequestURI();
				//restRequest.getServerPort() + restRequest.getContextPath() + restRequest.getRequestURI();

		String resourceType = (result.getSubject() instanceof Reference) ? "reference" : "attribute";

		// each resource has a name, type, class and link
		Attribute rName =	new Attribute("name", restRequest.getTargetResourceName());
		Attribute rType =	new Attribute("type", resourceType);
		Attribute rClass =	new Attribute("class", result.getSubject().getClass().getName());
		Attribute rLink =	new Attribute("href", resourceLink);

		root.addAttribute(rName);
		root.addAttribute(rType);
		root.addAttribute(rClass);
		root.addAttribute(rLink);

		// dump the contents of it's toString() method into the root tag as CDATA
		root.appendChild(result.getSubject().toString());

		return new Document(root);
	}

	public String getHTMLRepresentation() throws RemoteException
	{
		

		String resourceLink = restRequest.getScheme() + "://" + restRequest.getServerName() + ":" +
				restRequest.getServerPort() + restRequest.getRequestURI();
				//restRequest.getServerPort() + restRequest.getContextPath() + restRequest.getRequestURI();

		String resourceType = (result.getSubject() instanceof Reference) ? "reference" : "attribute";

		String out = "<html> <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"resourceTable\" id=\"resourceTable\" width=\"100%\">";
	 	out = out + "<caption bgcolor=\"darkblue\"><h2>Resources for <a href=" + resourceLink+ "?responseType=HTML>" +  restRequest.getTargetResourceName() + "</a> " + resourceType + "</h2></caption></table>";        
		out = out + " <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"subresourceTable\" id=\"subresourceTable\" width=\"70%\">";
		out = out + "<tr class=\"captionRow\" bgcolor=\"lightblue\">  <td>Resource Name</td>  <td>Type</td> <td>Class</td> <td>Link</td> <td>Value</td>	</tr>";
		out = out + "<tr bgcolor=\"lightyellow\">";
	    	out = out + "<td>" + restRequest.getTargetResourceName() + "</a></td>";
	    	out = out + "<td>"+  resourceType + "</td>";
	    	out = out + "<td>" + result.getSubject().getClass().getName() + "</td>";
	    	out = out + "<td>" + resourceLink + "</td>";
	    	out = out + "<td>" + result.getSubject().toString() + "</td>";
		out = out + "</tr>";
		out = out + "</table></html>";
		return out;
	}

	private final Object owner; 
	private final Context ownerContext;
	private final ResolutionResult result;
	private final HttpRestRequest restRequest;
}
