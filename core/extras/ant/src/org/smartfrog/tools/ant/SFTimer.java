/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/

package org.smartfrog.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.util.DateUtils;

import java.util.Vector;

/**
 * Timer task to print out how long a series of nested tasks takes. 
 */
public class SFTimer extends Task implements TaskContainer {

    /** Collection holding the nested tasks */
    private Vector<Task> nestedTasks = new Vector<Task>();

    /** Task name */
    private String name = "Timer";

    /** Time */
    long timeElapsed = 0;

    /**
     * {@inheritDoc}
     * @throws BuildException on any failure of a child
     */
    @Override
    @SuppressWarnings({"RefusedBequest"})
    public void execute() throws BuildException {


		timeElapsed = System.currentTimeMillis();
        try {
            for(Task task:nestedTasks) {
                task.execute();
            }
        } finally {
            timeElapsed = System.currentTimeMillis()- timeElapsed;
            long milliseconds = ((timeElapsed % (60 *1000)) % 1000);
            log( name +" took " + DateUtils.formatElapsedTime(timeElapsed) +" "+ milliseconds +" ms" );
        }

	}

    public void addTask(Task task) {
		nestedTasks.add(task);
	}

    public void setName(String name) {
		this.name = name;
	}
}