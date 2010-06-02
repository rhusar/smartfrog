package org.smartfrog.services.www.jaxrs.test.testapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created 26-May-2010 15:22:29
 */
@Path("/error/{code:.+}")
public class ErrorResource extends AbstractJaxRsResource {

    @GET
    public Response doGet(@PathParam("code") int code) {
        getLog().info("Getting error " + code);
        Response.ResponseBuilder builder = Response.status(code);
        Response response = builder.build();
        
        if (response.getStatus() != code) {
            throw new WebApplicationException(code);
        }
        return response;
    }


}
