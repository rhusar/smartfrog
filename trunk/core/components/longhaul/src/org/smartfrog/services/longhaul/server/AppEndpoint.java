package org.smartfrog.services.longhaul.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.processcompound.ProcessCompound;

@Path("/applications/{application}")
public class AppEndpoint extends EndpointBase {

    
    
    public AppEndpoint() {
	
    }

    @GET
    @Produces("text/smartfrog")
    public String getSystem(@PathParam("application") String application) {
	
	return "";

     }

}
