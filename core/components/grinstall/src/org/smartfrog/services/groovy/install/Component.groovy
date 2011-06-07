package org.smartfrog.services.groovy.install

import org.smartfrog.services.groovy.install.download.ISource
import org.smartfrog.services.groovy.install.task.ITask
import java.rmi.RemoteException
import org.smartfrog.sfcore.common.SmartFrogCoreKeys
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.common.SmartFrogResolutionException
import org.smartfrog.sfcore.compound.CompoundImpl

/**
 * Base class for all Components
 */
class Component extends CompoundImpl implements IComponent {

    ComponentState state

    public Component() throws RemoteException {
        super()
    }

    @Override
    public synchronized void sfDeploy() throws RemoteException, SmartFrogException {
        super.sfDeploy()
        sfLog().debug("Deployed " + this.getClass().getSimpleName())
    }

    @Override
    public synchronized void sfStart() throws RemoteException, SmartFrogException {
        super.sfStart()
        sfLog().debug("Starting " + this.getClass().getSimpleName())
        init()
        sfChildren().each { child ->
            if (child instanceof ISource) {
                child.retrieve()
            }
        }
        while (state != ComponentState.READY) {
            execute()
            changeState()
        }
    }


    private void init() {
        sfLog().debug("Initialising ${sfCompleteName()}")
        state = ComponentState.REMOVED
    }

    private void execute() {
        switch (state) {
            case ComponentState.REMOVED:
                run("install")
                break
            case ComponentState.INSTALLED:
                run("preConfigure")
                break
            case ComponentState.PRECONFIGURED:
                run("start")
                break
            case ComponentState.STARTED:
                run("postConfigure")
                break
            default:
                return
        }
    }

    private void run(String task) {
        def taskObject = sfResolve(task)
        if (taskObject instanceof ITask) {
            taskObject.run()
        } else {
            throw new IllegalArgumentException("Value of attribute ${task} is not of class ITask")
        }
    }

    /**
     * Return the expected next state of the software component during deployment.
     */
    private ComponentState nextDeployState() {
        switch (state) {
            case ComponentState.REMOVED:
                return ComponentState.INSTALLED
            case ComponentState.INSTALLED:
                return ComponentState.PRECONFIGURED
            case ComponentState.PRECONFIGURED:
                return ComponentState.STARTED
            case ComponentState.STARTED:
                return ComponentState.POSTCONFIGURED
            case ComponentState.POSTCONFIGURED:
                return ComponentState.READY
            default:
                throw new IllegalStateException("No next state for current state $state defined!")
        }
    }

    private void changeState() {
        state = nextDeployState()
        sfReplaceAttribute("state", state)
    }

    /**
     * Overrides sfResolve to allow for resolving attributes without having to know the path to them
     * @param reference i.e.: port or webServer:port
     * @return reference to local or remote attribute
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    @Override
    public Object sfResolve(String reference) throws SmartFrogResolutionException, RemoteException {
        if (reference.contains(":")) {
            sfLog().debug("Resolving reference $reference with parser")
            def parts = reference.split(":")
            def root = sfResolveWithParser(SmartFrogCoreKeys.SF_ROOT)
            def ref = root.sfResolveWithParser("ATTRIB ${parts[0]}")
            for (int i = 1; i < parts.size(); i++) {
                ref = ref.sfResolve(parts[i])
            }
            return ref
        } else {
            return super.sfResolve(reference)
        }
    }
}