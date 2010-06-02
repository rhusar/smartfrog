/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/

package org.smartfrog.services.www.jaxrs;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.security.SFClassLoader;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This represents a JAX RS application
 */

public class JaxRsServicesImpl extends PrimImpl implements JaxRsServices, JaxRsLocalServices {

    private JaxRsApplication application;
    private Map<String, Object> endpoints;
    public static final String ERROR_NOT_LOCAL = "The application provided is not local";

    public JaxRsServicesImpl() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        application = new JaxRsApplication(this);
        ComponentDescription classes = null;
        classes = sfResolve(ATTR_ENDPOINT_CLASSES, classes, true);
        endpoints = new HashMap<String, Object>();
        Iterator attrs = classes.sfAttributes();
        while (attrs.hasNext()) {
            String name = attrs.next().toString();
            String classname = classes.sfResolve(name, "", true);
            Class aClass = null;
            try {
                sfLog().info("Deploying JAX-RS endpoint " + classname);
                aClass = SFClassLoader.forName(classname);
            } catch (ClassNotFoundException e) {
                throw new SmartFrogDeploymentException(
                        "Endpoint class " + name
                                + " value \"" + classname
                                + "\" does not load in "
                                + getRuntimeString()
                                + ". Cause: " + e,
                        e,
                        this);
            }
            try {
                Object endpointer = createEndpoint(aClass);
                endpoints.put(classname, endpointer);
            } catch (IllegalArgumentException e) {
                throw new SmartFrogDeploymentException(
                        "Endpoint class " + name + " value \"" + classname
                                + "\" cannot be loaded in "
                                + getRuntimeString()
                                + ". Cause: " + e,
                        e,
                        this);
            } catch (UnsupportedOperationException e) {

            }
        }

    }

    /**
     * Get the runtime description
     *
     * @return the runtime
     */
    public String getRuntimeString() {
        return getRuntime().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        endpoints = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuntimeDelegate getRuntime() {
        return RuntimeDelegate.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JaxRsApplication getApplication() {
        return application;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UriBuilder createUriBuilder() {
        return getRuntime().createUriBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T createEndpoint(java.lang.Class<T> tClass)
            throws java.lang.IllegalArgumentException, java.lang.UnsupportedOperationException {
        return getRuntime().createEndpoint(application, tClass);
    }

    /**
     * Get the local services API
     *
     * @param appStub the application stub
     * @return the local api
     * @throws RemoteException if the application is remote
     */
    public static JaxRsLocalServices getLocalServices(JaxRsServices appStub) throws RemoteException {
        if (appStub instanceof JaxRsLocalServices) {
            return (JaxRsLocalServices) appStub;
        } else {
            throw new RemoteException(ERROR_NOT_LOCAL);
        }
    }

    public Map<String, Object> getEndpoints() {
        return endpoints;
    }
    

}
