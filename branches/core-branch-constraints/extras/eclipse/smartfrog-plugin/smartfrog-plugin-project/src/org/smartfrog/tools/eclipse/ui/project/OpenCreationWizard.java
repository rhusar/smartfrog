
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


package org.smartfrog.tools.eclipse.ui.project;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.IWorkbenchWizard;

import org.smartfrog.tools.eclipse.model.ExceptionHandler;


public abstract class OpenCreationWizard
    extends Action
    implements IWorkbenchWindowActionDelegate
{
    private IWorkbenchWindow mWindow = null;
    private IStructuredSelection mSelection = null;

    /**
     * Creates the specific wizard.
     * (to be implemented by a subclass)
     */
    abstract protected Wizard createWizard()
        throws CoreException;

    /**
     * The user has invoked this action.
     * @see IActionDelegate#run(IAction)
     */
    public void run(IAction action)
    {
        Shell mShell = mWindow.getShell();

        try {
            Wizard wizard = createWizard();

            if (wizard instanceof IWorkbenchWizard) {
                ( (IWorkbenchWizard)wizard ).init(mWindow.getWorkbench(),
                    mSelection);
            }

            WizardDialog dialog = new WizardDialog(mShell, wizard);
            dialog.create();
            dialog.open();
        } catch (Exception e) {
            ExceptionHandler.handle(e, mShell,
                ( Messages.getString(
                        "OpenCreationWizard.Title.CreateWizardError") ), //$NON-NLS-1$
                ( Messages.getString(
                        "OpenCreationWizard.Error.CreateWizardError") )); //$NON-NLS-1$
        }
    }

    public void dispose()
    {
    }

    /*
     * save the window
     * @see IWorkbenchWindowActionDelegate#init(IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window)
    {
        this.mWindow = window;
    }

    /*
     * save the selection if of kind IStructuredSelection or
     * create an empty structured selection.
     * @see IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
        if (selection instanceof IStructuredSelection) {
            this.mSelection = (IStructuredSelection)selection;
        } else {
            this.mSelection = new StructuredSelection();
        }
    }
}
