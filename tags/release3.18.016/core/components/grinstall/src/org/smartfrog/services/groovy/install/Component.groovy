package org.smartfrog.services.groovy.install

import java.rmi.RemoteException
import org.smartfrog.services.groovy.install.download.ISource
import org.smartfrog.services.groovy.install.task.GroovyComponentHelper
import org.smartfrog.services.groovy.install.task.ITask
import org.smartfrog.sfcore.common.SmartFrogCoreKeys
import org.smartfrog.sfcore.common.SmartFrogDeploymentException
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.common.SmartFrogResolutionException
import org.smartfrog.sfcore.compound.CompoundImpl
import org.smartfrog.sfcore.prim.Prim
import org.smartfrog.sfcore.prim.TerminationRecord
import org.smartfrog.sfcore.utils.SmartFrogThread
import org.smartfrog.sfcore.utils.WorkflowThread

/**
 * Base class for all Components
 */
class Component extends CompoundImpl implements IComponent {


    private ComponentState componentState
    public File destDir
    private Executor executor;
    int terminationTimeout = 20000


    public Component() throws RemoteException {
    }

    @Override
    String getDestDir() {
        return destDir.toString()
    }

    public synchronized ComponentState getComponentState() {
        return componentState
    }

    protected synchronized void setComponentState(ComponentState state) {
        this.componentState = state
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

        // determine the script dir and bail out if it is missing
        File scriptDir = helper.resolveFile(ATTR_SCRIPT_DIR, true)
        if (!scriptDir.exists()) {
            throw new SmartFrogDeploymentException("No $ATTR_SCRIPT_DIR found \"$scriptDir\"")
        }
        //now look for the directory
        destDir = helper.resolveFile(Component.ATTR_DEST_DIR, true)
        if (!destDir.exists()) {
            //create it if missing and allowed
            boolean createDir = sfResolve(ATTR_CREATE_DEST_DIR, true, true)
            if (createDir) {
                destDir.mkdirs()
            } else {
                //it's missing and creation is not allowed
                throw new SmartFrogDeploymentException("No $ATTR_DEST_DIR directory \"$destDir \"")
            }
        } else {
            //check it's a directory
            if (!destDir.isDirectory()) {
                throw new SmartFrogDeploymentException("The $ATTR_DEST_DIR value \"$destDir \" is not a directory")
            }
        }

        init()
        executor = new Executor(this)
        executor.start()
    }

    /**
     * This thread runs through all the children and pushes them into their next state
     */
    private class Executor extends WorkflowThread {

        Executor(Prim prim) {
            super(prim, true)
        }

        @Override
        void execute() {
            //retrieve all the source data
            sfLog().info("Retrieving sources")
            sfChildren().each { child ->
                if (terminationRequested) {
                    return
                };

                if (child instanceof ISource) {
                    child.retrieve()
                }
            }
            while (getComponentState() != ComponentState.READY) {
                if (terminationRequested) return;
                executeNextChild()
                changeState()
            }
        }

    }

    /**
     * Initialise the component by entering the removed state
     */
    private void init() {
        if (sfLog().debugEnabled) sfLog().debug("Initialising ${sfCompleteName()}")
        enterState(ComponentState.REMOVED)
    }

    private boolean executeNextChild() {
        ComponentState currentState = getComponentState();
        boolean result = false;
        switch (currentState) {
            case ComponentState.REMOVED:
                result = run(ATTR_INSTALL)
                break
            case ComponentState.INSTALLED:
                result = run(ATTR_PRE_CONFIGURE)
                break
            case ComponentState.PRECONFIGURED:
                result = run(ATTR_START)
                break
            case ComponentState.STARTED:
                result = run(ATTR_POST_CONFIGURE)
                break
            default:
                result = false
        }
        return result;
    }

    /**
     * Resolve a task by the name of the attribute, and then run it
     * @param task
     * @throws SmartFrogResolutionException if the task is missing or of the wrong type
     */
    private boolean run(String task) {
        def taskObject = sfResolve(task)
        if (taskObject instanceof ITask) {
            return taskObject.run()
        } else {
            throw new SmartFrogResolutionException("Value of attribute ${task} is not of class ITask")
        }
    }

    /**
     * Return the expected next state of the software component during deployment.
     */
    private ComponentState nextDeployState() {
        switch (componentState) {
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
                throw new IllegalStateException("No next state for current state $componentState defined!")
        }
    }

    /**
     * Change to the next state and update the state attribute
     */
    private void changeState() {
        enterState(nextDeployState())
    }

    private void enterState(ComponentState state) {
        setComponentState(state)
        if (sfLog().debugEnabled) sfLog().debug("new state: " + getComponentState())
        sfReplaceAttribute(ATTR_STATE, getComponentState())
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
            if (sfLog().debugEnabled) sfLog().debug("Resolving reference $reference with parser")
            def parts = reference.split(":")
            def root = sfResolveWithParser(SmartFrogCoreKeys.SF_ROOT)
            def ref = root.sfResolveWithParser("ATTRIB ${parts[0]}")
            [1..parts.size()].each { i->
                ref = ref.sfResolve(parts[i])
            }
            return ref
        } else {
            return super.sfResolve(reference)
        }
    }

    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status)
        SmartFrogThread.requestAndWaitForThreadTermination(executor, terminationTimeout)
        //now try to execute the terminator. Note that it is pre-constructed, and just needs executing
        ITask terminator = (ITask) sfResolve(ATTR_TERMINATOR, (Prim) null, false);
        terminator?.run()
    }


}