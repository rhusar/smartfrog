package org.smartfrog.osgi;

import org.osgi.framework.*;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.deployer.PrimFactory;
import org.smartfrog.sfcore.deployer.CodeRepository;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;

/**
 * A {@link PrimFactory} implementation that gets components instances from the OSGi service registry.
 * As OSGi services can come and go, this factory creates dynamic proxies that track the requested service.
 * When it becomes unavailable, any invocation of its method will throw Exception.
 *
 * For now, we suppose that the interface name we get from the component description is the name of
 * an interface that extends Prim. In the case of user error, nasty ClassCastExceptions will pop up.
 */
public class ServicePrimFactoryImpl extends PrimImpl implements PrimFactory {

    private BundleContext daemonBundleContext = null;
    private final Method sfTerminateMethod;

    /**
     * Attribute name for the name of the interface whose implementation will be used as a SmartFrog component.
     */
    public static final String INTERFACE_NAME_ATTRIBUTE = "serviceInterface";

    /**
     * Creates the component factory.
     * @throws RemoteException If PrimImpl constructor fails with this exception.
     * @throws NoSuchMethodException Should not happen (except in case of programmer error).
     */
    public ServicePrimFactoryImpl() throws RemoteException, NoSuchMethodException {
        sfTerminateMethod = Prim.class.getMethod("sfTerminate", new Class[]{ TerminationRecord.class });
    }

    /**
     * Deploys the factory. The only thing to do here is to get the bundle context from where it's kept.
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    public void sfDeploy() throws SmartFrogResolutionException, RemoteException {
        daemonBundleContext = OSGiUtilities.getDaemonBundleContext(this);
    }


    public ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }

    public CodeRepository getCodeRepository() {
        try {
            return (CodeRepository) sfResolve(SmartFrogCoreKeys.SF_CODE_REPOSITORY);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param askedFor The contents of the sfMeta attribute of the Prim we're deploying.
     * @return The newly created Prim instance.
     * @throws SmartFrogResolutionException If resolution of the interface name attribute fails.
     * @throws SmartFrogDeploymentException Should not happen (except in case of programmer error).
     */
    public Prim getComponent(ComponentDescription askedFor)
            throws SmartFrogDeploymentException
    {

        String interfaceName;
        try {
            interfaceName = (String) askedFor.sfResolveHere(INTERFACE_NAME_ATTRIBUTE);
        } catch (SmartFrogResolutionException e) {
            throw new SmartFrogDeploymentException(
                    MessageUtil.formatMessage(MSG_UNRESOLVED_REFERENCE, INTERFACE_NAME_ATTRIBUTE),
                    e,
                    this,
                    askedFor.sfContext()
            );
        }

        final UnregisterOnTerminateInvocationHandler invocationHandler =
                new UnregisterOnTerminateInvocationHandler(interfaceName);

        // We pass our classloader: the newly created proxy class will
        // be attached to our classloader.
        return (Prim) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {Prim.class},
                invocationHandler);
    }

    private class UnregisterOnTerminateInvocationHandler implements InvocationHandler, ServiceListener {
        private final String targetInterface;
        private ServiceReference targetReference;
        private Prim target;

        private UnregisterOnTerminateInvocationHandler(String targetInterface)
                throws SmartFrogDeploymentException
        {
            this.targetInterface = targetInterface;

            targetReference = daemonBundleContext.getServiceReference(targetInterface);

            if (targetReference == null) {
                sfLog().warn("Service not available at component creation: " + targetInterface);
                target = null;
            } else
                target = (Prim) daemonBundleContext.getService(targetReference);

            try {
                // We'll only be notified of events for services implementing the interface interfaceName.
                daemonBundleContext.addServiceListener(
                        // Need to check whether leaking "this" is dangerous here or not. I suspect it is...
                        this,
                        "(" + Constants.OBJECTCLASS + "=" + targetInterface + ")"
                );
            } catch (InvalidSyntaxException e) {
                // This really oughts to be a RuntimeException. Dammit!
                throw new SmartFrogDeploymentException
                        ("Programming error when registering an OSGi service listener", e);
            }
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Throwing RemoteExceptions because all methods in Prim do throw it (since it's a remote interface).
            // If I threw SmartFrogRuntimeExceptions instead, an used could get UndeclaredThrowableExceptions that he's
            // not going to handle. And the semantics of RemoteExceptions are reasonably similar to this...
            // Anyway, checked exceptions stink.
            if (target == null)
                throw new RemoteException
                        ("The OSGi service implementing this component is not available. Target interface: " + targetInterface);

            Object returnValue = method.invoke(target, args);

            if (method.equals(sfTerminateMethod))
                unregisterService();

            return returnValue;
        }

        private void unregisterService() {
            daemonBundleContext.ungetService(targetReference);
        }

        public void serviceChanged(ServiceEvent serviceEvent) {
            if (serviceEvent.getServiceReference().equals(targetReference)
                    && serviceEvent.getType() == ServiceEvent.UNREGISTERING
                    && target != null)
            {
                daemonBundleContext.ungetService(targetReference);
                target = null;
            } else if (serviceEvent.getType() == ServiceEvent.REGISTERED && target == null) {
                targetReference = serviceEvent.getServiceReference();
                target = (Prim) daemonBundleContext.getService(targetReference);
            }

        }
    }
}
