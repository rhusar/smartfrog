/** (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.longhaul.server;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Iterator;

/** List the applications that are running */
@Path("/applications/")
public class Applications extends EndpointBase {

    @GET
    @Produces("text/plain")
    public String doGetAsText() throws RemoteException {
        List<ChildApplication> children = getApplications();
        StringBuilder builder = new StringBuilder();
        for (ChildApplication child : children) {
            builder.append(child.key).append("\n");
        }
        return builder.toString();
    }

    @GET
    @Produces("text/json")
    public String doGetAsJSON() throws RemoteException {
        List<ChildApplication> children = getApplications();
        StringBuilder builder = new StringBuilder();
        builder.append("applications {\n");
        for (ChildApplication child : children) {
            builder.append("  ")
                    .append(child.key)
                    .append(" ; ")
                    .append("\n");
        }
        builder.append("}");
        return builder.toString();
    }


    @GET
    @Produces("text/html")
    public String doGetAsHtml() throws RemoteException {
        List<ChildApplication> children = getApplications();
        StringBuilder builder = new StringBuilder();
        builder.append("<html><head><title>Applications</title></head>\n");
        builder.append("<body>");
        builder.append("<span>Applications</span>");
        for (ChildApplication child : children) {
            builder.append("<span>")
                    .append("<a href=\"").append(child.getSafename()).append("\"/>")
                    .append(child.key)
                    .append("</a>")
                    .append("</span>\n");
        }
        builder.append("</body>");
        return builder.toString();

    }


    @GET
    @Produces("text/plain")
    @Path("/{application}/")
    public String getApplicationAsText(@PathParam("application") String application)
            throws SmartFrogException, RemoteException {
        return getApplicationAsSF(application);
    }

    @GET
    @Produces("text/smartfrog")
    @Path("/{application}/")
    public String getApplicationAsSF(@PathParam("application") String application)
            throws SmartFrogException, RemoteException {
        ComponentDescription diagnostics = extractDiagnosticsReport(application);
        StringBuilder response = new StringBuilder();
        response.append(diagnostics.toString());
        return response.toString();
    }

    private ComponentDescription extractDiagnosticsReport(String application)
            throws RemoteException, SmartFrogException {
        Prim child = lookupApplication(application);
        ComponentDescription diagnostics = child.sfDiagnosticsReport();
        return diagnostics;
    }

    @GET
    @Produces("text/json")
    @Path("/{application}/")
    public String getApplicationAsJSON(@PathParam("application") String application)
            throws SmartFrogException, RemoteException {
        ComponentDescription diagnostics = extractDiagnosticsReport(application);
        StringBuilder response = new StringBuilder();
        response.append("applications {\n");
        Iterator attrs = diagnostics.sfAttributes();
        while (attrs.hasNext()) {
            Object key = attrs.next();
            Object value = diagnostics.sfResolveHere(key);
            response.append("  ")
                    .append(key.toString())
                    .append(" ")
                    .append(value.toString())
                    .append("\n");
        }
        response.append("}");
        return response.toString();
    }
}
