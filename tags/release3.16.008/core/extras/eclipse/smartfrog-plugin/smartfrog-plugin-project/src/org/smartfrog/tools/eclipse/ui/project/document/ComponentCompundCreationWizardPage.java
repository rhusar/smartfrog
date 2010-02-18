
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


package org.smartfrog.tools.eclipse.ui.project.document;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.help.WorkbenchHelp;
import org.smartfrog.tools.eclipse.model.IHelpContextIds;


/**
 * Compound component wizard page
 */
public class ComponentCompundCreationWizardPage
    extends BaseComponentCreationWizardPage
{

    
    private static final String ORG_SMARTFROG_SFCORE_PRIM_PRIMIMPL =
        "org.smartfrog.sfcore.compound.CompoundImpl"; //$NON-NLS-1$
    private IWorkbench mWorkbench;
    private static final String[] INTERFACE_LISTS = {
            "org.smartfrog.sfcore.compound.Compound" //$NON-NLS-1$
        };

    /**
     * Constructor
     * @param  workbench         The current workbench
     * @param  selection         The current resource selection
     * @param  selectedProject   The project that is highlighted or contain the highlighted folder
     *
     */
    public ComponentCompundCreationWizardPage(IWorkbench workbench,
        IStructuredSelection selection, IProject selectedProject)
    {
        super(); 

        this.setTitle(Messages.getString("ComponentCompundCreationWizardPage.title.CompondWizard")); //$NON-NLS-1$
        this.setDescription(Messages.getString("ComponentCompundCreationWizardPage.description.compoundWizard")); //$NON-NLS-1$
        this.mWorkbench = workbench;
    }

    /*
     * Returns the default description file extension
     */
    public String getFileExtension()
    {
        return DEFAULT_DESCRIPTION_EXT;
    }

    /** (non-Javadoc)
     * Method declared on IDialogPage.
     */
    public void createControl(Composite parent)
    {
        super.createControl(parent);

        Composite composite = (Composite)getControl();
        setSuperClass(ORG_SMARTFROG_SFCORE_PRIM_PRIMIMPL, false);

        setSuperInterfaces(Arrays.asList(INTERFACE_LISTS), false);
        WorkbenchHelp.setHelp(getControl(),
            IHelpContextIds.COMPOUND_WIZARD_PAGE_HELP_ID);
    }

  
}
