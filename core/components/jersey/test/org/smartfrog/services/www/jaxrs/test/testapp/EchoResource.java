package org.smartfrog.services.www.jaxrs.test.testapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Created 26-May-2010 15:22:29
 */
@Path("/echo/{message:.+}")
public class EchoResource extends AbstractJaxRsResource {

    @GET
    @Produces("text/plain")
    public String doGet(@PathParam("message") String message) {
        return message;
    }



}