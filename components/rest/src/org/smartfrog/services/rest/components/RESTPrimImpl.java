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

package org.smartfrog.services.rest.components;

import nu.xom.Document;
import nu.xom.Element;
import org.smartfrog.services.rest.Restful;
import org.smartfrog.services.rest.exceptions.MethodNotSupportedException;
import org.smartfrog.services.rest.exceptions.RestException;
import org.smartfrog.services.rest.servlets.HttpRestRequest;
import org.smartfrog.services.rest.servlets.HttpRestResponse;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * This class exhibits exactly the same semantics as a {@link Prim} component
 * with the addition that the methods to allow REST management are directly
 * implemented (doGet, doPut, doPost, doDelete) as per the {@link Restful} interface.
 * This class has no functionality and is simply intended as an example.
 *
 * @author Derek Mortimer
 * @version 1.0
 * @see Restful
 */
public class RESTPrimImpl extends PrimImpl implements RESTPrim
{
	public RESTPrimImpl() throws RemoteException { super(); }

	public void doGet(HttpRestRequest restRequest, HttpRestResponse restResponse) throws MethodNotSupportedException, RemoteException, RestException
	{
		restResponse.setContentType("text/xml");
		restResponse.setContents(getXMLRepresentation().toXML().getBytes());
	}

	public void doPut(HttpRestRequest restRequest, HttpRestResponse restResponse) throws MethodNotSupportedException, RemoteException, RestException
	{
		throw new MethodNotSupportedException("PUT not allowed on RestPrim!");
	}

	public void doPost(HttpRestRequest restRequest, HttpRestResponse restResponse) throws MethodNotSupportedException, RemoteException, RestException
	{
		throw new MethodNotSupportedException("POST not allowed on RestPrim!");
	}

	public void doDelete(HttpRestRequest restRequest, HttpRestResponse restResponse) throws MethodNotSupportedException, RemoteException, RestException
	{
		throw new MethodNotSupportedException("DELETE not allowed on RestPrim!");
	}

	public Document getXMLRepresentation() throws RemoteException
	{
		Element root = new Element("root");
		root.appendChild("Specialised Output Here!");

		Document d = new Document(root);
		return d;
	}
}