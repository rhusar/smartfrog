package org.smartfrog.tools.ant;

import org.apache.tools.ant.Task;

/**
 */
public class TaskHelper {

    private Task owner;

    private ProjectHelper projectHelper;

    public TaskHelper(Task owner) {
        this.owner = owner;
        projectHelper=new ProjectHelper(owner.getProject());
    }

    /**
     * set a child task up as bound to this.
     * It is the inverse of Task.bindToOwner() in Ant1.7+,
     * implemented here, adding a call to init() for good measure.
     * @param task a child task
     */
    public void bindTask(Task task) {
        assert task != owner;
        task.setProject(owner.getProject());
        task.setOwningTarget(owner.getOwningTarget());
        task.setTaskName(owner.getTaskName());
        task.setDescription(owner.getDescription());
        task.setLocation(owner.getLocation());
        task.setTaskType(owner.getTaskType());
        task.init();
    }


    /**
     * using our memory location and a per-instance counter,
     * make up a new unique number.
     *
     * @return a new property name that is currently unique
     */
    public String createUniquePropertyName() {
        return projectHelper.createUniquePropertyName();
    }


}
