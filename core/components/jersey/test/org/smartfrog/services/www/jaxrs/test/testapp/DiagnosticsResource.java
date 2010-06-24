package org.smartfrog.services.www.jaxrs.test.testapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;

/**
 * Created 02-Jun-2010 17:21:17
 */

@Path("/diagnostics")
public class DiagnosticsResource extends AbstractJaxRsResource {
    @GET
    @Produces("text/html")
    public String doGet(
            @Context Request request
            //,
            //        @Context Application app
    ) {
        StringBuilder builder = new StringBuilder();

        builder.append("This is the diagnostics page. Application field: " + getApplication());
//        builder.append(" application parameter: "+ app);
        builder.append("request: " + request);
        return builder.toString();
    }
/*    
    @GET
    @Path("application")
    public String doGet(@Context Application app) {
        return " application parameter: " + app;
    }*/
}