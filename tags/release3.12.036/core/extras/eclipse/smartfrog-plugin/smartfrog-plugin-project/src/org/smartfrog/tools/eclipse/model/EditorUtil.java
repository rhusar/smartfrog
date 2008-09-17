
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

import org.eclipse.core.resources.IFile;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class EditorUtil
{
    /**
     * Save the file if it is open in the editor area.
     * @param file
     * @param confirm - true to ask the user before saving unsaved changes (recommended), and false to save unsaved changes without asking
     */
    public static void saveFileIfItIsOpenInEditor(IFile file, boolean confirm)
    {
        IWorkbench workbench = SmartFrogPlugin.getDefault().getWorkbench();
        IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();

        for (int i = 0; i < windows.length; i++) {
            IWorkbenchPage[] pages = windows[ i ].getPages();

            for (int pageIndex = 0; pageIndex < pages.length; pageIndex++) {
                IEditorReference[] editors = pages[ pageIndex ]
                    .getEditorReferences();

                for (int editorIndex = 0; editorIndex < editors.length;
                        editorIndex++) {
                    IEditorReference ep = editors[ editorIndex ];

                    IEditorInput input = (IEditorInput)ep.getEditor(true)
                                                         .getEditorInput();

                    if (input instanceof IFileEditorInput) {
                        IFile compFile = ( (IFileEditorInput)input ).getFile();

                        if (( null != file ) && ( null != compFile )) {
                            if (file.getLocation().toString().equals(
                                        compFile.getLocation().toString())) {
                                IEditorPart editorPart = ep.getEditor(false);
                                pages[ i ].saveEditor(editorPart, confirm);

                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Is the file dirty?
     * @param file
     */
    public static boolean isFileDirty(IFile file)
    {
        IEditorPart[] dirtyEditors = getDirtyEditors();
        String fileName = file.getFullPath().toOSString();

        if (dirtyEditors.length > 0) {
            for (int i = 0; i < dirtyEditors.length; i++) {
                if (dirtyEditors[ i ].getEditorInput() instanceof
                        IFileEditorInput) {
                    IFile dirtyFile = ( (IFileEditorInput)dirtyEditors[ i ]
                            .getEditorInput() ).getFile();

                    if (fileName.equals(dirtyFile.getFullPath().toOSString())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Gets the dirty file list from the supplied list of files
     * @param fileList
     * @return the dirty file list
     */
    public static List getDirtyFiles(List fileList)
    {
        // get the dirty files
        if (null != fileList) {
            List dirtyFilesList = new ArrayList();

            for (Iterator iter = fileList.iterator(); iter.hasNext();) {
                Object element = (Object)iter.next();

                if (element instanceof IFile) {
                    if (isFileDirty((IFile)element)) {
                        dirtyFilesList.add(element);
                    }
                }
            }

            return dirtyFilesList;
        }

        return null;
    }

    /**
     * Saves the dirty files from the supplied list of files
     * @param fileList
     * @param confirm
     */
    public static void saveDirtyFiles(List fileList, boolean confirm)
    {
        if (null != fileList) {
            for (Iterator iter = fileList.iterator(); iter.hasNext();) {
                Object element = (Object)iter.next();

                if (element instanceof IFile) {
                    saveFileIfItIsOpenInEditor((IFile)element, confirm);
                }
            }
        }
    }

    /**
     * Get all the dirty editors
     * @return
     */
    public static IEditorPart[] getDirtyEditors()
    {
        Set inputs = new HashSet();
        List dirtyEditorList = new ArrayList(0);
        IWorkbench workbench = SmartFrogPlugin.getDefault().getWorkbench();
        IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();

        for (int i = 0; i < windows.length; i++) {
            IWorkbenchPage[] pages = windows[ i ].getPages();

            for (int j = 0; j < pages.length; j++) {
                IEditorPart[] editors = pages[ j ].getDirtyEditors();

                for (int k = 0; k < editors.length; k++) {
                    IEditorPart editorPart = editors[ k ];
                    IEditorInput editorInput = editorPart.getEditorInput();

                    if (!inputs.contains(editorInput)) {
                        inputs.add(editorInput);
                        dirtyEditorList.add(editorPart);
                    }
                }
            }
        }

        return (IEditorPart[])dirtyEditorList.toArray(
                new IEditorPart[ dirtyEditorList.size() ]);
    }
}
