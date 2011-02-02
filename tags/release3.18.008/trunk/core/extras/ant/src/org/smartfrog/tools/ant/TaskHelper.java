package org.smartfrog.tools.ant;

import org.apache.tools.ant.Task;

/**
 * something to help with tasks
 */
public class TaskHelper {

    private Task owner;

    private ProjectHelper projectHelper;

    public TaskHelper(Task owner) {
        this.owner = owner;
    }

    /**
     * set a child task up as bound to this. It is the inverse of Task.bindToOwner() in Ant1.7+, 
     * adding a call to init() for good measure.
     *
     * @param task a child task
     */
    public void bindTask(Task task) {
        assert task != owner;
        task.bindToOwner(owner);
        //this is not in Task.bindToOwner
        task.init();
    }

    private ProjectHelper getProjectHelper() {
        if (projectHelper == null) {
            projectHelper = new ProjectHelper(owner.getProject());
        }
        return projectHelper;
    }

    /**
     * using our memory location and a per-instance counter, make up a new unique number.
     *
     * @return a new property name that is currently unique
     */
    public String createUniquePropertyName() {
        return getProjectHelper().createUniquePropertyName();
    }


}
