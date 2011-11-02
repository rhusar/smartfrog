package org.smartfrog.services.www.context;

import org.smartfrog.services.os.java.LoadClassImpl;
import org.smartfrog.services.www.ServletComponent;
import org.smartfrog.services.www.ServletContextComponentDelegate;
import org.smartfrog.services.www.ServletContextIntf;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**
 * Created 25-May-2010 12:43:57
 */

public abstract class DelegatedServletComponentImpl extends ServletContextComponentImpl {
    /**
     * our delegate
     */
    private ServletContextComponentDelegate delegate;
    private static final Reference REF_CLASSNAME = new Reference(ServletComponent.ATTR_CLASSNAME);


    protected DelegatedServletComponentImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        //here we are bound to our context
        ServletContextIntf servletContext = getServletContext();

        //validate our classpath
        String classname=sfResolve(REF_CLASSNAME,"",true);
        Class clazz = LoadClassImpl.loadClass(this, classname);
        //create an instance which is then debugged
        
        Object instance = LoadClassImpl.createInstance(clazz);
        if(sfLog().isDebugEnabled()) {
            sfLog().debug("Created delegate instance " + instance);
        }

        //create and bind the delegate
        delegate = addComponent(servletContext);
        delegate.start();
    }

    protected abstract ServletContextComponentDelegate addComponent(ServletContextIntf servletContext)
            throws RemoteException, SmartFrogException;

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     *
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link
     *                                    org.smartfrog.sfcore.prim.Liveness} interface
     */
    @Override
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if(sfIsStarted && delegate != null) {
            delegate.ping();
        }
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    @Override
    public synchronized void sfTerminateWith(TerminationRecord status) {
        if (delegate != null) {
            try {
                delegate.terminate();
                delegate = null;
            } catch (RemoteException ignored) {
                //swallowed

            } catch (SmartFrogException ignored) {
                //swallowed
            }
        }
        super.sfTerminateWith(status);
    }
}
