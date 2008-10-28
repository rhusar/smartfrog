/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/
package org.smartfrog.services.hadoop.components.cluster;

import org.apache.hadoop.util.Service;
import org.smartfrog.services.hadoop.common.HadoopUtils;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.services.hadoop.core.SFHadoopException;
import org.smartfrog.services.hadoop.core.ServiceStateChangeHandler;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This is a component that deploys a hadoop service.
 */

public abstract class HadoopServiceImpl extends HadoopComponentImpl
        implements HadoopService, ServiceStateChangeHandler {

    private Service service;
    private ServiceDeployerThread deployerThread;
    private static final int SHUTDOWN_DELAY = 5000;
    public static final String NO_FILESYSTEM = "Filesystem is not running";
    private boolean expectNodeTermination;
    private volatile boolean terminationInitiated;
    private Reference completeName;
    public static final String SERVICE_HAS_HALTED = "Service has halted";
    public static final String ERROR_NO_START = "Failed to start the ";
    private ManagedConfiguration configuration;
    private List<PortEntry> portList;
    private static final int CONNECT_TIMEOUT = 2000;
    private boolean serviceStartupInProgress;


    protected HadoopServiceImpl() throws RemoteException {
    }


    /**
     * Called after instantiation for deployment purposes. Heart monitor is started and if there is a parent the
     * deployed component is added to the heartbeat. Subclasses can override to provide additional deployment behavior.
     * Attributees that require injection are handled during sfDeploy().
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        expectNodeTermination = true; //conf.getBoolean(FileSystemNode.ATTR_EXPECT_NODE_TERMINATION, true);
        completeName = sfCompleteName();
    }

    /**
     * On shutdown, terminates any non-null service.
     *
     * @param status termination status
     */
    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        sfLog().info("Initiating "+getName()+" termination"
         +" deployerThread=" + deployerThread + " service=" + service);
        terminationInitiated = true;
        try {
            terminateDeployerThread();
        } catch (Exception e) {
            sfLog().warn("when terminating deployer thread " + e, e);
        }
        terminateHadoopService();
        try {
            checkPortsAreClosed();
        } catch (SmartFrogException e) {
            sfLog().warn("Ports not closed "+e, e);
        }
    }

    /**
     * Get the configuration used to deploy this service
     * @return the configuration; will be null if nothing has been deployed yet
     */
    public ManagedConfiguration getConfiguration() {
        return configuration;
    }

    protected boolean isServiceStartupInProgress() {
        return serviceStartupInProgress;
    }

    /**
     * Override point: check that after termination, any chosen ports are closed.
     *
     * @throws SmartFrogException if one or more ports are open
     */
    protected void checkPortsAreClosed() throws SmartFrogException {
        if (portList == null) {
            return;
        }
        StringBuilder ports = new StringBuilder();
        for (PortEntry port: portList) {
            InetSocketAddress address=port.address;
            if (address.getPort() > 0) {
                try {
                    HadoopUtils.checkPort(address, CONNECT_TIMEOUT);
                    //this is bad, the port is open
                    ports.append("Open port: ").append(port).append("\n");
                } catch (IOException connectFailure) {
                    //this is good, the port is closed
                    sfLog().debug("Port closed: " + port, connectFailure);
                }
            }
        }
        if (ports.length() > 0) {
            throw new SmartFrogException(ports.toString());
        }
    }

    /**
     * Get a list of ports that should be closed on startup and after termination.
     * This list is built up on startup and cached.
     * @param conf the configuration to use
     * @return null or a list of ports
     * @throws SmartFrogResolutionException failure to resolve a value
     * @throws RemoteException network trouble
     */
    protected List<PortEntry> buildPortList(ManagedConfiguration conf)
            throws SmartFrogResolutionException, RemoteException {
        return null;
    }


    /**
     * Terminate the deployer thread
     */
    protected void terminateDeployerThread() {
        ServiceDeployerThread deployer = getDeployerThread();
        if (deployer != null) {
            sfLog().info("Terminating deployer thread");
            terminateDeployerThread(deployer);
        } else {
            sfLog().info("No deployer thread to terminate");
        }
    }

    /**
     * Terminate the specified thread. subclasses can use alternate shutdown policies
     *
     * @param deployer thread to shut down
     */
    protected void terminateDeployerThread(ServiceDeployerThread deployer) {
        sfLog().info("Requesting thread termination");
        try {
            deployer.requestTerminationWithInterrupt();
        } catch (IllegalMonitorStateException e) {
            sfLog().warn("Could not notify the thread: "+e,e);
            deployer.interrupt();
        }
        sfLog().info("waiting for thread to finish");
        if (deployer.waitForThreadTermination(SHUTDOWN_DELAY)) {
            sfLog().warn("Deployer thread did not terminate within the specified shutdown delay; "
                    + "service is " + getService());
        }
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws SmartFrogLivenessException on any liveness problem
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    @Override
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        pingHadoopService();
    }


    /**
     * Test for the service being live
     *
     * @return true if the service is live
     * @throws RemoteException for RMI problems
     */
    @Override
    public boolean isServiceLive() throws RemoteException {
        Service s = getService();
        return s != null && s.getServiceState() == Service.ServiceState.LIVE;
    }

    /**
     * Return the name of this service; please override for better messages
     *
     * @return a name of the service for error messages
     */
    protected String getName() {
        return "Service";
    }

    /**
     * Get the deployer thread; may be null
     *
     * @return
     */
    protected ServiceDeployerThread getDeployerThread() {
        return deployerThread;
    }

    /**
     * Override point; tell the base class whether or not {@link #service} must be non-null in the {@link
     * #sfPing(Object)} operation
     *
     * @return true for non-null, false to allow null values.
     */
    protected boolean requireNonNullServiceInPing() {
        return true;
    }

    /**
     * When the namenode is terminated, and we are not shutting down ourself, then we notify
     * {@inheritDoc}
     */
    @Override
    public void onStateChange(Service hadoopService, Service.ServiceState oldState,
                              Service.ServiceState newState) {
        if (newState == Service.ServiceState.TERMINATED && !terminationInitiated) {
            TerminationRecord tr;
            Throwable thrown = hadoopService.getFailureCause();
            if (expectNodeTermination) {
                tr = TerminationRecord.normal(SERVICE_HAS_HALTED, completeName, thrown);
            } else {
                tr = TerminationRecord.abnormal(SERVICE_HAS_HALTED, completeName , thrown);
            }
            ComponentHelper helper = new ComponentHelper(this);
            helper.targetForWorkflowTermination(
                    tr);
        }
    }

    /**
     * Ping the hadoop service.
     *
     * @throws SmartFrogLivenessException on a ping failure, or a null service and non-null services are forbidden
     * @throws RemoteException for remoting problems
     */
    protected void pingHadoopService() throws SmartFrogLivenessException, RemoteException {
        try {
            ServiceDeployerThread deployer = deployerThread;
            if (deployer != null) {
                deployer.ping(true);
            }
            Service hadoopService = getService();
            Service.ServiceStatus serviceStatus = pingService();
            if (hadoopService != null) {
                List<Throwable> throwables = serviceStatus.getThrowables();
                //look for failure exceptions first
                if (!throwables.isEmpty()) {
                    //trouble
                    throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(
                            new SFHadoopException(serviceStatus, this));
                }
                //now look at the service state alone
                //as SF has stricter service state rules
                Service.ServiceState state = serviceStatus.getState();
                switch(state) {
                  case STARTED:
                  case LIVE:
                    break;

                  case FAILED:
                    throw new SmartFrogLivenessException(
                            "Service has Failed: " + serviceStatus,
                            service.getFailureCause(), this);
                      
                  case TERMINATED:
                    throw new SmartFrogLivenessException(
                            "Service has Terminated: " + serviceStatus,
                            service.getFailureCause(), this);
                  case UNDEFINED:
                  case CREATED:
                  default:
                    throw new SmartFrogLivenessException("Service is not live: "+serviceStatus, this);
                }
            } else if (requireNonNullServiceInPing()) {
                throw new SmartFrogLivenessException("No running " + getName(), this);
            }
        } catch (RemoteException e) {
            throw e;
        } catch (IOException e) {
            throw new SmartFrogLivenessException(e, this);
        }
    }

    /**
     * Terminate the service in the {@link #service} field, and sets it to to null.
     * Does nothing if the service is already terminated/null value
     */
    protected final void terminateHadoopService() {
        terminateService(getService());
    }

    /**
     * Inner service termination. Errors are logged at warn level.
     * If the parameter is in the {@link #service} field, that field is set to null
     * @param hadoopService the service to terminate.
     */
    protected void terminateService(Service hadoopService) {
        if (hadoopService != null) {
            synchronized (this) {
                if(hadoopService == getService()) {
                    setService(null);
                }
            }
            sfLog().info("Terminating hadoopService service " + hadoopService.getServiceName());
            try {
                hadoopService.close();
            } catch (IOException e) {
                sfLog().warn("When closing the service : "+ e, e);
            }
        }
        postTerminationCleanup();
    }

    /**
     * Do any post-termination cleanup from within
     * {@link #terminateService(Service)}
     */
    protected void postTerminationCleanup() {
    }


    /**
     * Get the current service value; may be null
     *
     * @return the service
     */
    public synchronized final Service getService() {
        return service;
    }

    /**
     * Set the current service value; null is acceptable
     *
     * @param service the new value
     */
    protected synchronized final void setService(Service service) {
        if (service != null && this.service != null) {
            throw new IllegalStateException("Cannot set a non-null service " + service + " on top of a valid service "
                    + this.service);
        }
        this.service = service;
    }


    /**
     * Deploy a service. It must be in the CREATED state. The field {@link #service} is set to the service argument,
     * which must be null. There is special handling for wrapped Jetty exceptions
     *
     * @param hadoopService service to bind to and deploy.
     * @param conf          configuration -used for diagnostics
     * @throws SmartFrogException           on any problem
     * @throws SFHadoopException            for Jetty exceptions and other causes of trouble
     * @throws SmartFrogDeploymentException on some wrapped IOExceptions
     */
    protected void deployService(Service hadoopService, ManagedConfiguration conf) throws SmartFrogException {
        deployerThread = createDeployerThread(hadoopService, conf);
        deployerThread.start();
    }

    /**
     * {@inheritDoc}
     */
    public final Service.ServiceState getServiceState() throws RemoteException {
        Service hadoop = getService();
        if (hadoop == null) {
            return Service.ServiceState.UNDEFINED;
        } else {
            return hadoop.getServiceState();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Service.ServiceStatus pingService() throws IOException {
        Service hadoop = getService();
        if (hadoop == null) {
            return null;
        } else {
            return hadoop.ping();
        }
    }


    /**
     * {@inheritDoc}
     *
     * @return a description string of the service
     * @throws IOException network problems
     */
    public String getDescription() throws RemoteException {
        return getName()+" managing "+ service == null ?
                            "No Service"
                            :service.toString();
    }


    /**
     * Create the deployer thread
     *
     * @param hadoopService service to bind to and deploy.
     * @param conf          configuration -used for diagnostics
     * @return a new thread that is an instance of ServiceDeployerThread
     */
    protected ServiceDeployerThread createDeployerThread(Service hadoopService,
                                                         ManagedConfiguration conf) {
        return new ServiceDeployerThread(hadoopService, conf, false);
    }


    /**
     * reject any attempts to set the service property to a non-null value if it is already non-null; stops us trying to
     * deploy multiple services
     *
     * @param hadoopService the service
     * @throws SmartFrogDeploymentException if a service is already deployed 
     */
    private synchronized void bindToService(Service hadoopService) throws SmartFrogDeploymentException {
        if (service != null) {
            throw new SmartFrogDeploymentException("Cannot bind to a new service (" + hadoopService + ")"
                    + " when an existing service is in use: "
                    + service);
        }
        setService(hadoopService);
    }

    /**
     * Inner deploy operation called on a second thread; can be subclassed for fun.
     *
     * @param hadoopService service to deploy
     * @param conf          configuration
     * @throws SmartFrogException
     */
    private void innerDeploy(Service hadoopService, ManagedConfiguration conf) throws SmartFrogException {
        try {
            bindToService(hadoopService);
            //start the service
            serviceStartupInProgress = true;
            getService().start();
            serviceStartupInProgress = false;
            onServiceDeploymentComplete(hadoopService);
        } catch (SmartFrogException e) {
            onServiceDeploymentFailed(hadoopService, e);
            throw e;
        } catch (Throwable e) {
            onServiceDeploymentFailed(hadoopService, e);
            //convert illegal argument exceptions
            throw SFHadoopException.forward("Failed to deploy " + hadoopService,
                    e,
                    this,
                    conf);
        } finally {
            serviceStartupInProgress = false;
        }
    }

    /**
     * Handler for service deployment failure; called from the deployment thread
     *
     * @param hadoopService service that failed to deploy
     */
    protected void onServiceDeploymentFailed(Service hadoopService, Throwable thrown) {
        sfLog().warn("Unable to deploy " + hadoopService, thrown);
        terminateService(hadoopService);
    }

    /**
     * Override point: Callback once service deployment is finished. It is not called in a synchronized context.
     *
     * @param hadoopService service that was deployed
     * @throws IOException        IO/hadoop problems
     * @throws SmartFrogException smartfrog problems
     */
    protected void onServiceDeploymentComplete(Service hadoopService) throws IOException, SmartFrogException {
        Service s = getService();
        if(s != null && !sfIsTerminated) {
            sfLog().info(getName() + " deployment complete: service is: " + s);
        } else {
            String message = getName() + " deployment completed after component was terminated."
                    + "Hadoop service is " + hadoopService;
            sfLog().warn(message);
            //do an emergency shutdown
            terminateService(hadoopService);
            //then fail gracelessly
            throw new SmartFrogException(message);
        }
    }

    /**
     * For use in sfStart(); calls {@link #createTheService(ManagedConfiguration)}
     * to create the service, then {@link #deployService(Service, ManagedConfiguration)}.
     * This usually starts an asynchronous thread to deploy the service via
     * {@link HadoopServiceImpl#innerDeploy(Service, ManagedConfiguration)}
     * @throws SmartFrogException wrapping any other exception thrown
     * @throws RemoteException network problems
     */
    protected void createAndDeployService() throws SmartFrogException, RemoteException {
        ManagedConfiguration conf = createConfiguration();
        configuration = conf;
        portList = buildPortList(conf);
        //now check the known ports are closed. This will bail out early
        checkPortsAreClosed();
        validateConfiguration(conf);
        try {
            Service createdService = createTheService(conf);
            deployService(createdService, conf);
        } catch (Throwable thrown) {
            throw SFHadoopException.forward(ERROR_NO_START + getName(),
                    thrown,
                    this,
                    conf);
        }
    }

    /**
     * Override point: any last minute validation of the configuration
     * @param conf the configuration to validate
     * @throws RemoteException RMI issues
     * @throws SmartFrogException Smartfrog problems
     */
    protected void validateConfiguration(ManagedConfiguration conf) throws SmartFrogException, RemoteException {
    }

    /**
     * Create the specific service
     *
     * @param conf configuration to use
     * @return the service that has been created. The default: null
     * @throws IOException hadoop or RMI issues
     * @throws SmartFrogException Smartfrog problems
     */
    protected Service createTheService(ManagedConfiguration conf) throws IOException, SmartFrogException {
        return null;
    }
    /**
     * This is the thread that starts the deployment
     */
    public class ServiceDeployerThread extends WorkflowThread {

        private Service hadoopService;
        private ManagedConfiguration conf;

        private boolean useWorkflowTermination;

        public ServiceDeployerThread(Service hadoopService, ManagedConfiguration conf,
                                      boolean useWorkflowTermination) {
            super(HadoopServiceImpl.this, useWorkflowTermination);
            this.useWorkflowTermination = useWorkflowTermination;
            this.hadoopService = hadoopService;
            this.conf = conf;
        }


        /**
         * only terminate if there was a failure to deploy.
         */
        @Override
        protected void processRunResults() {
            if (useWorkflowTermination) {
                super.processRunResults();
            } else {
                //now analyse the result, create a term record and maybe terminate the owner
                terminateIFFAbnormal();
            }
        }

        /**
         * Terminate if we are abnormal
         */
        protected void terminateIFFAbnormal() {
            boolean isNormal = getThrown() == null;
            if (!isNormal) {
                TerminationRecord tr = createTerminationRecord();
                aboutToTerminate(tr);
                ComponentHelper helper = new ComponentHelper(HadoopServiceImpl.this);
                helper.targetForTermination(tr, false, false, false);
            }
        }

        /**
         * The executor method calls {@link HadoopServiceImpl#innerDeploy(Service, ManagedConfiguration)} for the actual
         * deployment
         *
         * @throws Throwable any failure desired
         */
        @Override
        public void execute() throws Throwable {
            innerDeploy(hadoopService, conf);
        }

    }
}

