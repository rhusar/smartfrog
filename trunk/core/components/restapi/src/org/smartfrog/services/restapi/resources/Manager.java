/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.restapi.resources;

import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.Liveness;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.Enumeration;
import java.util.ArrayList;
import java.rmi.RemoteException;

/**
 *
 */
@Path("/manager/{application}")
public class Manager extends AbstractRestResource {

    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("application/xml")
    public String getAsXML(@PathParam("application") String application) {
        // Return some cliched textual content
        return "<undefined/>";
    }

    @GET
    @Produces("application/json")
    public String getAsJSON(@PathParam("application") String application) {
        // Return some cliched textual content
        return "undefined";
    }

    @GET
    @Produces("application/x-smartfrog")
    public String getAsSmartFrog(@PathParam("application") String application) {
        // Return some cliched textual content
        return "undefined";
    }

    @PUT
    @Consumes("application/x-smartfrog")
    @Produces("application/json")
    public String put(String message,
                      @PathParam("application") String application,
                      @Context UriInfo context) {
        ProcessCompound process = getProcessCompound();
        //TODO


        return("deployed");
    }

    List<Prim> getListOfApplications() throws RemoteException {
        ProcessCompound process = getProcessCompound();
        Enumeration<Liveness> children = process.sfChildren();
        List<Prim> applications = new ArrayList<Prim>();
        while (children.hasMoreElements()) {
            Liveness liveness = children.nextElement();
            applications.add((Prim) liveness);
        }
        return applications;
    }

/*
    @POST
    @Consumes("application/x-smartfrog")
    public String doPost2(FormURLEncodedProperties formData) {
    }
*/

}
