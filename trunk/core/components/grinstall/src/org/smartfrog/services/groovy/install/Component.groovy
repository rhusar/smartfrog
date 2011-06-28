package org.smartfrog.services.groovy.install

import java.rmi.RemoteException
import org.smartfrog.services.groovy.install.download.ISource
import org.smartfrog.services.groovy.install.task.ITask
import org.smartfrog.sfcore.common.SmartFrogCoreKeys
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.common.SmartFrogResolutionException
import org.smartfrog.sfcore.compound.CompoundImpl
import org.smartfrog.services.groovy.install.task.GroovyComponentHelper
import org.smartfrog.sfcore.common.SmartFrogDeploymentException

/**
 * Base class for all Components
 */
class Component extends CompoundImpl implements IComponent {



    protected ComponentState state
    public File destDir


    public Component() throws RemoteException {
    }

    @Override
    String getDestDir() {
        return destDir.toString()
    }

    /**
     * Startup: start the superclass (which will deploy all children), then for every child that is a source,
     * get it to retrieve its artifacts.
     * Then, while not in the ready state, execute and then change state. This needs to be done in a thread
     * @throws RemoteException network problems
     * @throws SmartFrogException startup problems
     */
    @Override
    public synchronized void sfStart() throws RemoteException, SmartFrogException {
        super.sfStart()
        GroovyComponentHelper helper = new GroovyComponentHelper(this)

        File scriptDir = helper.resolveFile(ATTR_SCRIPT_DIR, true)
        if (!scriptDir.exists()) {
            throw new SmartFrogDeploymentException("No $ATTR_SCRIPT_DIR found \"$scriptDir\"")
        }
        destDir = helper.resolveFile(Component.ATTR_DEST_DIR, true)
        if (!destDir.exists()) {
            boolean createDir = sfResolve(ATTR_CREATE_DEST_DIR, true, true)
            if (createDir) {
                destDir.mkdirs()
            } else {
                throw new SmartFrogDeploymentException("No $ATTR_DEST_DIR directory \"$destDir \"")
            }
        }

        init()


        //retrieve all the source data
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

    /**
     * Initialise the component by entering the removed state
     */
    private void init() {
        if (sfLog().isDebugEnabled()) sfLog().debug("Initialising ${sfCompleteName()}")
        state = ComponentState.REMOVED
        publishState()
    }

    private void execute() {
        switch (state) {
            case ComponentState.REMOVED:
                run(ATTR_INSTALL)
                break
            case ComponentState.INSTALLED:
                run(ATTR_PRE_CONFIGURE)
                break
            case ComponentState.PRECONFIGURED:
                run(ATTR_START)
                break
            case ComponentState.STARTED:
                run(ATTR_POST_CONFIGURE)
                break
            default:
                return
        }
    }

    /**
     * Resolve a task by the name of the attribute, and then run it
     * @param task
     * @throws SmartFrogResolutionException if the task is missing or of the wrong type
     */
    private void run(String task) {
        def taskObject = sfResolve(task)
        if (taskObject instanceof ITask) {
            taskObject.run()
        } else {
            throw new SmartFrogResolutionException("Value of attribute ${task} is not of class ITask")
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

    /**
     * Change to the next state and update the state attribute
     */
    private void changeState() {
        state = nextDeployState()
        publishState()
    }

    private def publishState() {
        sfReplaceAttribute(ATTR_STATE, state)
    }

    /**
     * Overrides sfResolve to allow for resolving attributes without having to know the path to them
     * @param reference i.e.: port or webServer:port
     * @return reference to local or remote attribute
     * @throws SmartFrogResolutionException resolution failure
     * @throws RemoteException network problems
     */
    @Override
    public Object sfResolve(String reference) throws SmartFrogResolutionException, RemoteException {
        if (reference.contains(":")) {
            if (sfLog().isDebugEnabled()) sfLog().debug("Resolving reference $reference with parser")
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