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
    def utils = new ComponentUtils()
    Prim parent

    public GroovyTask() throws RemoteException {
    }

    /**
     * Implement {@link ITask#run()}
     * @throws RemoteException
     * @throws SmartFrogException
     */
    @Override
    public void run() throws RemoteException, SmartFrogException {
        // ScriptHelper needs component to bind it within task scripts
        parent = sfParent()
        GroovyComponentHelper helper = new GroovyComponentHelper(parent)

        if (!sfResolve(ATTR_FINISHED, false, false)) {
            String file = sfResolve(ATTR_FILE, "", false)
            if (file.isEmpty()) {
                // no task file specified
                sfLog().debug("No file specified")
                return
            }
            String scriptDirName = helper.resolvePath(IComponent.ATTR_SCRIPT_DIR, true);
            File scriptDir = new File(scriptDirName)
            previousTasks = sfResolve(ATTR_PRECONDITIONS, new Vector(), false)
            register()
            waitForPreconditions()
            execute(scriptDir, file)
            sfReplaceAttribute(ATTR_FINISHED, true)
            notifyObservers()
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
     * @param scriptDir directory
     * @param file file to look for
     * @return true iff the script was run
     */
    private boolean execute(File scriptDir, String file) {
        sfLog().info("Executing file $file in directory $scriptDir")
        File scriptFile = new File(scriptDir, file)
        if (!scriptFile.exists()) {
            sfLog().info("No script file \"$scriptFile\"")
            return false;
        }
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
        Script script
        try {
            ClassLoader loader = this.getClass().getClassLoader()
            GroovyShell shell = new GroovyShell(loader, binding, conf)
            String text = scriptFile.getText()
            script = shell.parse(text)
            if (script == null) {
                throw new SmartFrogLifecycleException("Null script from parsing $scriptFile")
            }
            if (sfLog().debugEnabled && !(script instanceof DelegatingScript)) {
                def hierarchy = utils.extractClassHierarchy(script)
                String message = "Unable to convert the instance $script" +
                        " into a ${DelegatingScript.class} -- class hierarchy is :\n$hierarchy"
                sfLog().debug(message)
            }

        } catch (Exception e) {
            sfLog().error("When Parsing $scriptFile: $e", e)
            throw new SmartFrogExtractedException(SmartFrogExtractedException.convert(e))
        }
        try {
            script.initialise()
            script.run()
            return true;
        } catch (Exception e) {
            sfLog().error("When executing $scriptFile: $e", e)
            throw new SmartFrogExtractedException(SmartFrogExtractedException.convert(e))
        }

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
}