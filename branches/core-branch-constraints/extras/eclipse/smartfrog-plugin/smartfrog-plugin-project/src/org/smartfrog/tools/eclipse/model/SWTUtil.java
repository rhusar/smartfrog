
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

import org.smartfrog.tools.eclipse.SmartFrogPlugin;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Utility methods for SWT
 */
public class SWTUtil
{
	/**
	 * Invoke an runable process in the Eclipse GUI thread
	 * @param runAble
	 */
    public static void runInSWTThread(Runnable runAble)
    {
        IWorkbenchWindow window = SmartFrogPlugin.getWorkbenchWindow();

        try {
            if (null != window) {
                Display display = window.getShell().getDisplay();

                display.syncExec(runAble);
            }
        } catch (Exception e) {
            if (null != window) {
                MessageDialog.openError(window.getShell(),
                    Messages.getString("SWTUtil.error.SwtError"),   //$NON-NLS-1$
                    e.toString());
            } else {
                ExceptionHandler.handle(e, Messages.getString("SWTUtil.error.SwtError"),  //$NON-NLS-1$
                		Messages.getString("SWTUtil.error.SwtError"));  //$NON-NLS-1$
            }
        }
    }

 
}
