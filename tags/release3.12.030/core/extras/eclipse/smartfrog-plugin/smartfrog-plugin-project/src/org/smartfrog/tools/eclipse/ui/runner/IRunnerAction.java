
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


package org.smartfrog.tools.eclipse.ui.runner;

import org.eclipse.core.resources.IFile;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;
import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;
import org.smartfrog.tools.eclipse.ui.console.ConsoleView;


/**
 * Basic action class for all SmartFrog actions
 */
abstract class IRunnerAction
    extends Action
    implements IWorkbenchWindowActionDelegate
{
    protected IWorkbenchWindow mWindow = null;
    protected IWorkbenchPage mActivePage = null;
    protected IStructuredSelection mSelection = null;
    protected IFile mSelectedIFile = null;

    /**
     * Bring the SmartFrog console to up front
     */
    protected void bringUpConsole()
    {
        ConsoleView.activeConsole(SmartFrogPlugin.getWorkbenchWindow(),
            ISmartFrogConstants.SMARTFROG_CONSOLE_ID);
    }

    /**
     * Get the highlight files
     *
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
        if (selection instanceof IStructuredSelection) {
            Object firstElement = ( (IStructuredSelection)selection )
                .getFirstElement();

            if (firstElement instanceof IFile) {
                IFile file = (IFile)firstElement;
                mSelectedIFile = file;
            }
        } else if (selection instanceof ITextSelection) {
            //

            if (( null != mActivePage ) &&
                    ( mActivePage.getActivePart() instanceof ITextEditor )) {
                IFileEditorInput iFileEdInput = null;

                ITextEditor iTextEd = (ITextEditor)mActivePage.getActivePart();

                if (( iTextEd != null ) &&
                        ( iTextEd.getEditorInput() instanceof IFileEditorInput )) {
                    iFileEdInput = (IFileEditorInput)iTextEd.getEditorInput();
                    mSelectedIFile = iFileEdInput.getFile();
                }
            }
        }
    }

    /**
     * 
     * @return Selected file
     */
    protected String getSelectFile()
    {
        String selectedFile = null;

        if (null != mSelectedIFile) {
            selectedFile = mSelectedIFile.getLocation().toOSString();
        }

        return selectedFile;
    }
}
