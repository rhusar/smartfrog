
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


package org.smartfrog.tools.eclipse.ui.perspective;

import org.eclipse.debug.ui.IDebugUIConstants;

import org.eclipse.jdt.ui.JavaUI;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;


/**
 * Create SmartFrog perspective
 */
public class SmartFrogPerspective
    implements IPerspectiveFactory
{
    private static final String ID_BOTTOM_CORNER_FOLDER =
        "ID_BOTTOM_CORNER_FOLDER"; //$NON-NLS-1$
    private static final String ID_LEFT_CORNER_FOLDER = "ID_LEFT_CORNER_FOLDER"; //$NON-NLS-1$
    public static final float ONE_FOURTH_PERCENT = (float)0.25;
    public static final float THREE_FOURTH_PERCENT = (float)0.75;

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
     */
    public void createInitialLayout(IPageLayout layout)
    {
        // create editor area
        String editorArea = layout.getEditorArea();

        // create view layouts
        IFolderLayout folder = layout.createFolder(ID_LEFT_CORNER_FOLDER,
                IPageLayout.LEFT, ONE_FOURTH_PERCENT, editorArea);

//        folder.addView(IPageLayout.ID_RES_NAV);
        folder.addView(JavaUI.ID_PACKAGES);
        folder.addPlaceholder(JavaUI.ID_TYPE_HIERARCHY);

        IFolderLayout outputfolder = layout.createFolder(
                ID_BOTTOM_CORNER_FOLDER, IPageLayout.BOTTOM,
                THREE_FOURTH_PERCENT, editorArea);

        outputfolder.addView(IPageLayout.ID_TASK_LIST);
        outputfolder.addView(ISmartFrogConstants.SMARTFROG_CONSOLE_ID);
//        layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT,
//            THREE_FOURTH_PERCENT, editorArea);

        // new views - add view shortcuts
        layout.addShowViewShortcut(JavaUI.ID_PACKAGES);

        layout.addShowViewShortcut(JavaUI.ID_TYPE_HIERARCHY);

        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);

        layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);

        layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);

        // add launch action set
        layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);

        // Add entries to the File menu
        layout.addNewWizardShortcut(ISmartFrogConstants.NEW_DESCRIPTION_ID);
        layout.addNewWizardShortcut(ISmartFrogConstants.NEW_PRIM_COMPONENT_ID);
        layout.addNewWizardShortcut(ISmartFrogConstants.NEW_COMPOUND_COMPONENT_ID);
        layout.addNewWizardShortcut(
            ISmartFrogConstants.NEW_SMARTFROG_PROJECT_ID);
    }
}
