package org.smartfrog.services.www.jaxrs.jersey;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.WebApplicationFactory;
import com.sun.jersey.spi.service.ServiceConfigurationError;
import com.sun.jersey.spi.service.ServiceFinder;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.security.SFClassLoader;

import java.rmi.RemoteException;

/**
 * Created 02-Jun-2010 13:09:14
 */

public class JerseyDiagnostics extends PrimImpl {

    public static final String SERVICE_TO_FIND = "serviceToFind";
    
    
    public JerseyDiagnostics() throws RemoteException {
    }

    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        WebApplication webApp = getDeployedApplication();
        if (webApp == null) {
            sfLog().warn("no Web application");
        } else {
            sfLog().info("Jersey Web application "+webApp
            +"\nclass="+webApp.getClass());
        }
        
        String classname = sfResolve(SERVICE_TO_FIND,"", true);
        if(!classname.isEmpty()) {
            try {
                Class aClass = SFClassLoader.forName(classname);
                ServiceFinder foundServices = findServiceProviderFor(aClass);
                int counter=0;
                for (Object service:foundServices) {
                    counter++;
                    sfLog().info("Service "+ counter+" class "+ service.getClass() +" :" +service.toString()); 
                }
            } catch (Throwable e) {
                throw new SmartFrogDeploymentException("Failed to find service implementations of "+classname+": "+e, e);

            }
        }
        
    }

    public static WebApplication getDeployedApplication() throws ContainerException {
        WebApplication webApplication = WebApplicationFactory.createWebApplication();
        return webApplication;
    }
    
    public static ServiceFinder findServiceProviderFor(Class clazz) throws ServiceConfigurationError {
        ServiceFinder serviceFinder = ServiceFinder.find(clazz);
        return serviceFinder;
    }
    
    
}
