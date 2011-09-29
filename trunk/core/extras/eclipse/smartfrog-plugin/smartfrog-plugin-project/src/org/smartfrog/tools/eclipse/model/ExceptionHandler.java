
/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.tools.eclipse.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.swt.widgets.Shell;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;

import java.io.StringWriter;

import java.lang.reflect.InvocationTargetException;


/**
 * The default exception handler shows an error dialog when one of its
 * handle methods is called.
 * If the passed exception is a CoreException/Exception an error dialog
 * pops up showing the exception's status information.
 * For a InvocationTargetException a normal message dialog pops up
 * showing the exception's message.
 * Additionally the exception is written to the platform log.
 */
public class ExceptionHandler
{
    private static ExceptionHandler fgInstance = new ExceptionHandler();

    /**
     * Logs the given exception using the platform's logging mechanism.
     */
    public static void log(Throwable t, String message)
    {
        SmartFrogPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,
                SmartFrogPlugin.PLUGIN_ID, ISmartFrogConstants.INTERNAL_ERROR,
                message, t));
    }
    
    /**
     * Logs the given exception using the platform's logging mechanism.
     */
    public static void log(Throwable e)
    {
        log(e, Messages.getString("ExceptionHandler.Error.InternalError")); //$NON-NLS-1$
    }

    /**
     * Display the given Exception in a popup window. The workbench shell is used as a parent
     * for the dialog window.
     *
     * @param e the CoreException to be handled
     * @param title the dialog window's window title
     * @param message message to be displayed by the dialog window
     */
    public static void handle(Exception e, String title, String message)
    {
        handle(e, SmartFrogPlugin.getWorkbenchShell(), title, message);
    }

    /**
     * Display the given Exception in a popup window, while the caller is not in GUI.
     * @param e the CoreException to be handled
     * @param title the dialog window's window title
     * @param message message to be displayed by the dialog window
     */
    public static void handleInSWTThread(final Exception e, final String title,
        final String message)
    {
        SWTUtil.runInSWTThread(new Runnable() {
                public void run()
                {
                    handle(e, SmartFrogPlugin.getWorkbenchShell(), title,
                        message);
                }
            });
    }

    /**
     * Handles the given CoreException. The workbench shell is used as a parent
     * for the dialog window.
     *
     * @param e the CoreException to be handled
     * @param title the dialog window's window title
     * @param message message to be displayed by the dialog window
     */
    public static void handle(CoreException e, String title, String message)
    {
        handle(e, SmartFrogPlugin.getWorkbenchShell(), title, message);
    }

    /**
     * Handles the given Exception.
     *
     * @param e the CoreException to be handled
     * @param parent the dialog window's parent shell
     * @param title the dialog window's window title
     * @param message message to be displayed by the dialog window
     */
    public static void handle(Exception e, Shell parent, String title,
        String message)
    {
        fgInstance.perform(e, parent, title, message);
    }

    /**
     * Handles the given CoreException.
     *
     * @param e the CoreException to be handled
     * @param parent the dialog window's parent shell
     * @param title the dialog window's window title
     * @param message message to be displayed by the dialog window
     */
    public static void handle(CoreException e, Shell parent, String title,
        String message)
    {
        fgInstance.perform(e, parent, title, message);
    }

    /**
     * Handles the given InvocationTargetException. The workbench shell is used
     * as a parent for the dialog window.
     *
     * @param e the InvocationTargetException to be handled
     * @param title the dialog window's window title
     * @param message message to be displayed by the dialog window
     */
    public static void handle(InvocationTargetException e, String title,
        String message)
    {
        handle(e, SmartFrogPlugin.getWorkbenchShell(), title, message);
    }

    /**
     * Handles the given InvocationTargetException.
     *
     * @param e the InvocationTargetException to be handled
     * @param parent the dialog window's parent shell
     * @param title the dialog window's window title
     * @param message message to be displayed by the dialog window
     */
    public static void handle(InvocationTargetException e, Shell parent,
        String title, String message)
    {
        fgInstance.perform(e, parent, title, message);
    }

    protected void perform(Exception e, Shell shell, String title,
        String message)
    {
        log(e);
        displayMessageDialog(e, e.getMessage(), shell, title, message);
    }

    protected void perform(CoreException e, Shell shell, String title,
        String message)
    {
        log(e);

        IStatus status = e.getStatus();

        if (status != null) {
            ErrorDialog.openError(shell, title, message, status);
        } else {
            displayMessageDialog(e, e.getMessage(), shell, title, message);
        }
    }

    protected void perform(InvocationTargetException e, Shell shell,
        String title, String message)
    {
        Throwable target = e.getTargetException();

        if (target instanceof CoreException) {
            perform((CoreException)target, shell, title, message);
        } else {
            log(e);

            if (( e.getMessage() != null ) && ( e.getMessage().length() > 0 )) {
                displayMessageDialog(e, e.getMessage(), shell, title, message);
            } else {
                displayMessageDialog(e, target.getMessage(), shell, title,
                    message);
            }
        }
    }

    private void displayMessageDialog(Throwable t, String exceptionMessage,
        Shell shell, String title, String message)
    {
        StringWriter msg = new StringWriter();

        if (message != null) {
            msg.write(message);
            msg.write("\n\n"); //$NON-NLS-1$
        }

        if (( exceptionMessage == null ) || ( exceptionMessage.length() == 0 )) {
            msg.write(Messages.getString(
                    "ExceptionHandler.Error.CheckLogFile")); //$NON-NLS-1$
        } else {
            msg.write(exceptionMessage);
        }

        MessageDialog.openError(shell, title, msg.toString());
    }
}
