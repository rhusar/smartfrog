/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.services.ant;

import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildEvent;

import java.io.PrintStream;

/**
 * This just wraps any other logger, and adds halting facilities.
 * when halted, the next log operation or event will cause a BuildInterruptedException to be raised.
 * <p/>
 * Created 06-Nov-2007 14:43:08
 */

public class InterruptibleLogger implements BuildLogger {


    private volatile boolean halt;
    private volatile boolean halted;
    private BuildLogger logger;

    /**
     * Create a logger
     * @param logger the real logger
     */
    public InterruptibleLogger(BuildLogger logger) {
        this.logger = logger;
    }


    /**
     * Get the inner logger
     * @return the inner logger
     */
    public BuildLogger getLogger() {
        return logger;
    }

    /**
     * Halt this build
     */
    public synchronized void halt() {
        halted = false;
        halt = true;
    }

    /**
     * Return true if the build is being halted.
     * @return the current state of the halt variable
     */
    public boolean isHalt() {
        return halt;
    }

    /**
     * check for being halted.
     * @throws BuildInterruptedException if we have been halted, and this is the first
     * check since the request was made.
     */
    private synchronized void checkForHalt() throws BuildInterruptedException {
        if (halt && !halted) {
            halted = true;
            throw new BuildInterruptedException();
        }
    }

    /**
     * Sets the highest level of message this logger should respond to.
     *
     * Only messages with a message level lower than or equal to the given level should be written to the log. <P>
     * Constants for the message levels are in the {@link Project Project} class. The order of the levels, from least to
     * most verbose, is <code>MSG_ERR</code>, <code>MSG_WARN</code>, <code>MSG_INFO</code>, <code>MSG_VERBOSE</code>,
     * <code>MSG_DEBUG</code>.
     *
     * @param level the logging level for the logger.
     */
    public void setMessageOutputLevel(int level) {
        checkForHalt();
        logger.setMessageOutputLevel(level);
    }

    /**
     * Sets the output stream to which this logger is to send its output.
     *
     * @param output The output stream for the logger. Must not be <code>null</code>.
     */
    public void setOutputPrintStream(PrintStream out) {
        checkForHalt();
        logger.setOutputPrintStream(out);
    }

    /**
     * Sets this logger to produce emacs (and other editor) friendly output.
     *
     * @param emacsMode <code>true</code> if output is to be unadorned so that emacs and other editors can parse files
     *                  names, etc.
     */
    public void setEmacsMode(boolean emacsMode) {
        checkForHalt();
        logger.setEmacsMode(emacsMode);
    }

    /**
     * Sets the output stream to which this logger is to send error messages.
     *
     * @param err The error stream for the logger. Must not be <code>null</code>.
     */
    public void setErrorPrintStream(PrintStream err) {
        checkForHalt();
        logger.setErrorPrintStream(err);
    }

    /**
     * Signals that a build has started. This event is fired before any targets have started.
     *
     * @param event An event with any relevant extra information. Must not be <code>null</code>.
     */
    public void buildStarted(BuildEvent event) {
        checkForHalt();
        logger.buildStarted(event);
    }

    /**
     * Signals that the last target has finished. This event will still be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information. Must not be <code>null</code>.
     * @see BuildEvent#getException()
     */
    public void buildFinished(BuildEvent event) {
        checkForHalt();
        logger.buildFinished(event);
    }

    /**
     * Signals that a target is starting.
     *
     * @param event An event with any relevant extra information. Must not be <code>null</code>.
     * @see BuildEvent#getTarget()
     */
    public void targetStarted(BuildEvent event) {
        checkForHalt();
        logger.targetStarted(event);

    }

    /**
     * Signals that a target has finished. This event will still be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information. Must not be <code>null</code>.
     * @see BuildEvent#getException()
     */
    public void targetFinished(BuildEvent event) {
        checkForHalt();
        logger.targetFinished(event);
    }

    /**
     * Signals that a task is starting.
     *
     * @param event An event with any relevant extra information. Must not be <code>null</code>.
     * @see BuildEvent#getTask()
     */
    public void taskStarted(BuildEvent event) {
        checkForHalt();
        logger.taskStarted(event);
    }

    /**
     * Signals that a task has finished. This event will still be fired if an error occurred during the build.
     *
     * @param event An event with any relevant extra information. Must not be <code>null</code>.
     * @see BuildEvent#getException()
     */
    public void taskFinished(BuildEvent event) {
        checkForHalt();
        logger.taskFinished(event);
    }

    /**
     * Signals a message logging event.
     *
     * @param event An event with any relevant extra information. Must not be <code>null</code>.
     * @see BuildEvent#getMessage()
     * @see BuildEvent#getException()
     * @see BuildEvent#getPriority()
     */
    public void messageLogged(BuildEvent event) {
        checkForHalt();
        logger.messageLogged(event);
    }
}
