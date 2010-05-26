package org.smartfrog.services.www.jaxrs.test.testapp;

import org.smartfrog.services.www.jaxrs.JaxRsApplication;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * Created 26-May-2010 15:22:29
 */
@Path("/error/{code:.+}")
public class ErrorResource extends AbstractJaxRsResource {

    @GET
    public Response doGet(@PathParam("code") int code) {
        JaxRsApplication app = getJaxRsApplication();
        app.getLog().info("Getting error "+ code);
        Response.ResponseBuilder builder = Response.status(code);
        Response response = builder.build();
        return response;
    }
}
