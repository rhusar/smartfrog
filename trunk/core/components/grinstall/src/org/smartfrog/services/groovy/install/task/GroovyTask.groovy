package org.smartfrog.services.groovy.install.task

import java.rmi.RemoteException
import org.codehaus.groovy.control.CompilerConfiguration
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.common.SmartFrogExtractedException
import org.smartfrog.sfcore.prim.PrimImpl
import org.smartfrog.services.groovy.install.Component
import org.smartfrog.services.groovy.install.utils.ComponentUtils
import org.smartfrog.services.groovy.install.IComponent
import org.smartfrog.sfcore.common.SmartFrogLifecycleException
import org.smartfrog.sfcore.prim.Prim

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
     * Implement {@link ITask#run() }
     * @throws RemoteException
     * @throws SmartFrogException
     */

    @Override
    public void run() throws RemoteException, SmartFrogException {
        // ScriptHelper needs component to bind it within task scripts
        parent = sfParent()
        GroovyComponentHelper helper = new GroovyComponentHelper(parent)

        if (!sfResolve(ATTR_FINISHED, false, false)) {
            def file = sfResolve(ATTR_FILE, "", false)
            if (!file || file.isEmpty()) {
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

    private boolean  execute(File scriptDir, String file) {
        sfLog().info("Executing file $file in directory $scriptDir")
        File scriptFile = new File(scriptDir, file)
        if (!scriptFile.exists()) {
            sfLog().info("No script file \"$scriptFile\"")
            return false;
        }
        Component parentComponent = (Component) sfParent()
        CompilerConfiguration conf = new CompilerConfiguration()
        String delegationScriptName = DelegatingScript.class.name
        DelegatingScript ds = new DelegatingScript()
        conf.setScriptBaseClass(delegationScriptName)
        Binding binding = new Binding()
        binding.setVariable(DelegatingScript.PARENT, parentComponent);
        try {
            ClassLoader loader = ds.getClass().getClassLoader()
            GroovyShell shell = new GroovyShell(loader, binding, conf)
            String text = scriptFile.getText()
            Script script = shell.parse(text)
            if (script == null) {
                throw new SmartFrogLifecycleException("Null script from parsing $scriptFile")
            }
            if (sfLog().debugEnabled && !(script instanceof DelegatingScript)) {
                def hierarchy = utils.extractClassHierarchy(script)
                String message = "Unable to convert the instance $script" +
                        " into a ${DelegatingScript.class} -- class hierarchy is :\n$hierarchy"
                sfLog().debug(message)
            }
/*            DelegatingScript dgs = (DelegatingScript) script;
            dgs.setComponent(parentComponent)*/
            script.initialise()
            script.run()
            return true;
        } catch (Exception e) {
            sfLog().error("When trying to parse $scriptFile: $e", e)
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