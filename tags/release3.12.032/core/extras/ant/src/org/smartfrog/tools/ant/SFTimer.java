package org.smartfrog.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.util.DateUtils;

import java.util.Iterator;
import java.util.Vector;


public class SFTimer extends Task implements TaskContainer {

    /** Collection holding the nested tasks */
    private Vector nestedTasks = new Vector();

    /** Task name */
    private String name = "Timer";

    /** Time */
    long timeElapsed = 0;

    public void execute() throws BuildException {


		timeElapsed = System.currentTimeMillis();

		Iterator i = nestedTasks.iterator();

        while (i.hasNext()) {
			((Task) i.next()).execute();
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