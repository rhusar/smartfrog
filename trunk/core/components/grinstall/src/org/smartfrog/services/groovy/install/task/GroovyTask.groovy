package org.smartfrog.services.groovy.install.task

import java.rmi.RemoteException
import org.codehaus.groovy.control.CompilerConfiguration
import org.smartfrog.services.groovy.install.Component
import org.smartfrog.services.groovy.install.IComponent
import org.smartfrog.services.groovy.install.utils.ComponentUtils
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.common.SmartFrogExtractedException
import org.smartfrog.sfcore.common.SmartFrogLifecycleException
import org.smartfrog.sfcore.prim.Prim
import org.smartfrog.sfcore.prim.PrimImpl
import org.smartfrog.sfcore.common.SmartFrogLivenessException

/**
 * User: koenigbe
 * Date: 02/02/11
 * Time: 11:07
 */
class GroovyTask extends PrimImpl implements ITask {

    private final String simplename = getClass().getSimpleName()

    private Vector previousTasks

    private List<ITask> observers = new ArrayList<ITask>()

    private final Object lock = new Object()
    ComponentUtils utils = new ComponentUtils()
    Prim parent
    String file
    File scriptDir
    String scriptDirName
    Script script
    File scriptFile
    boolean pingable

    public GroovyTask() throws RemoteException {
    }

    @Override
    synchronized void sfStart() {
        super.sfStart()
        parent = sfParent()
        GroovyComponentHelper helper = new GroovyComponentHelper(parent)
        file = sfResolve(ATTR_FILE, "", false)
        scriptDirName = helper.resolvePath(IComponent.ATTR_SCRIPT_DIR, true)
        scriptDir = new File(scriptDirName)
        previousTasks = sfResolve(ATTR_PRECONDITIONS, new Vector(), false)
        scriptFile = new File(scriptDir, file)

        pingable = sfResolve(ATTR_PINGABLE, false, false);
        if (pingable) {
            script = loadScript(scriptDir, file)

        }
    }

    /**
     * Implement {@link ITask#run()}. A pingable task returns false
     * @return true if the operation executed, and returned true itself
     * @throws RemoteException
     * @throws SmartFrogException
     */
    @Override
    public boolean run() throws RemoteException, SmartFrogException {
        if(pingable) {
            return false;
        }
        // ScriptHelper needs component to bind it within task scripts

        if (!sfResolve(ATTR_FINISHED, false, false)) {
            if (file.isEmpty()) {
                // no task file specified
                sfLog().debug("No file specified")
                return false
            }

            register()
            waitForPreconditions()
            sfLog().info("Executing file $scriptFile")
            boolean result = execute()
            sfReplaceAttribute(ATTR_FINISHED, true)
            notifyObservers()
            return result;
        } else {
            return false;
        }
    }

    private void register() {
        previousTasks.each { task ->
            if (task instanceof ITask || task instanceof GroovyTask) {
                task.addObserver(this)
            } else {
                throw new SmartFrogException("""Reference $task does not refer to an object of class ITask.
                                                task is ${task.class.name}""")
            }
        }
    }

    private void waitForPreconditions() {
        def wait = false
        while (true) {
            wait = false
            previousTasks.each { task ->
                wait = !task.sfResolve(ATTR_FINISHED, false, false)
            }
            if (wait) lock()
            else return
        }
    }

    private void lock() {
        synchronized (lock) {
            try {
                sfLog().debug("Going to sleep")
                lock.wait();
            } catch (InterruptedException ex) {
                sfLog().debug("Thread interrupted");
            }
        }
    }

    /**
     * Notify everything that is waiting on the lock that they arefree to continue
     */
    private void unlock() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * Execute a script
     * @return true iff the script was run
     */
    private boolean execute() {
        script = loadScript(scriptDir, file)
        try {
            script?.run()
            return true;
        } catch (Exception e) {
            sfLog().error("When executing $scriptFile: $e", e)
            throw new SmartFrogExtractedException(SmartFrogExtractedException.convert(e))
        }

    }

    /**
     * Load the script file from the filesystem
     * @param scriptDir the directory the script exists in
     * @param file filename to look for
     * @return the script or null
     */
    private Script loadScript(File scriptDir, String file) {

        File scriptFile = new File(scriptDir, file)
        if (!scriptFile.exists()) {
            sfLog().info("No script file \"$scriptFile\"")
            script = null;
        } else {
            Component parentComponent = (Component) sfParent()
            CompilerConfiguration conf = new CompilerConfiguration()
            //set the base class for the script. This will be loaded with a new classloader, so the
            //resulting script cannot be cast back to an instance, or invoked with new types.
            conf.setScriptBaseClass(DelegatingScript.class.name)

            //instead params are passed down via the binding
            Binding binding = new Binding()
            binding.setVariable(DelegatingScript.PARENT, parentComponent)
            binding.setVariable(DelegatingScript.DESTDIR, new File(parentComponent.getDestDir()))
            binding.setVariable(DelegatingScript.SCRIPTDIR, scriptDir)

            try {
                GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding, conf)
                String text = scriptFile.getText()
                script = shell.parse(text)
                if (script == null) {
                    throw new SmartFrogLifecycleException("Null script from parsing $scriptFile")
                }
                script.initialise()
            } catch (Exception e) {
                sfLog().error("When Parsing $scriptFile: $e", e)
                throw new SmartFrogExtractedException(SmartFrogExtractedException.convert(e))
            }
        }
        return script;
    }

    @Override
    public void addObserver(ITask observer) throws RemoteException, SmartFrogException {
        observers.add(observer)
    }

    private void notifyObservers() {
        observers.each { observer ->
            observer.update()
        }
    }

    @Override
    public void update() throws RemoteException, SmartFrogException {
        unlock()
    }

    /**
     *  Execute the ping operation, thrown an exception if there is a problem
     */
    void runPing() {
        try {
            if(pingable) {
                script?.run();
            }
        } catch (SmartFrogExtractedException e) {
            throw new SmartFrogLivenessException(e);
        }
    }

    @Override
    void sfPing(Object source) {
        super.sfPing(source)
        runPing();
    }


}