package org.smartfrog.services.groovy.install.task

import java.rmi.RemoteException
import org.codehaus.groovy.control.CompilerConfiguration
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.common.SmartFrogExtractedException
import org.smartfrog.sfcore.prim.PrimImpl
import org.smartfrog.services.groovy.install.Component

/**
 * User: koenigbe
 * Date: 02/02/11
 * Time: 11:07
 */
class GroovyTask extends PrimImpl implements ITask {

    private static final String simplename = getClass().getSimpleName()

    private Vector previousTasks

    private List<ITask> observers = new ArrayList<ITask>()

    private final Object lock = new Object()

    public GroovyTask() throws RemoteException {
        super()
    }


    @Override
    public void run() throws RemoteException, SmartFrogException {
        if (!sfResolve(ATTR_FINISHED, false, false)) {
            def file = sfResolve(ATTR_FILE, "", false)
            if (!file) return // no task file specified
            def directory = sfParent().sfResolve(ATTR_DIRECTORY)
            previousTasks = sfResolve(ATTR_PRECONDITIONS, new Vector(), false)
            register()
            waitForPreconditions()
            execute(directory, file)
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
                if (!task.sfResolve(ATTR_FINISHED, false, false)) {
                    wait = true
                }
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

    private void unlock() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    private void execute(directory, file) {
        sfLog().debug("Executing file $file in directory $directory")
        def f = new File("$directory/$file")
        def conf = new CompilerConfiguration()
        conf.setScriptBaseClass("org.smartfrog.services.groovy.install.task.DelegatingScript")
        DelegatingScript s
        try {
            s = new GroovyShell(conf).parse(f.getText())
        } catch (Exception e) {
            sfLog().error(e.toString(), e)
            throw new SmartFrogExtractedException(SmartFrogExtractedException.convert(e))
        }
        s?.setComponent((Component)sfParent())
        s?.run()
    }

    @Override
    public void addObserver(ITask observer) throws RemoteException, SmartFrogException {
        observers.add(observer)
    }

    private void notifyObservers() {
        observers.each { it.update() }
    }

    @Override
    public void update() throws RemoteException, SmartFrogException {
        unlock()
    }
}