package org.smartfrog.services.www.jaxrs.test.testapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created 02-Jun-2010 17:21:17
 */

@Path("/undeclared")
public class UndeclaredResource {
    @GET
    @Produces("text/html")
    public String doGet() {
        return "This should not be here, implies that classpath scanning is at work";
    }
}
