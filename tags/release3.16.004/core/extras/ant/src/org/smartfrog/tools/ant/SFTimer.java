package org.smartfrog.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.util.DateUtils;

import java.util.Vector;


public class SFTimer extends Task implements TaskContainer {

    /** Collection holding the nested tasks */
    private Vector<Task> nestedTasks = new Vector<Task>();

    /** Task name */
    private String name = "Timer";

    /** Time */
    long timeElapsed = 0;

    @Override
    public void execute() throws BuildException {


		timeElapsed = System.currentTimeMillis();
        for(Task task:nestedTasks) {
            task.execute();
        }


		timeElapsed = System.currentTimeMillis()- timeElapsed;

        long milliseconds = ((timeElapsed % (60 *1000)) % 1000);

        log( name +" took " + DateUtils.formatElapsedTime(timeElapsed) +" "+ milliseconds +" ms" );
	}

    public void addTask(Task task) {
		nestedTasks.add(task);
	}

    public void setName(String name) {
		this.name = name;
	}
}