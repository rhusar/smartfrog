
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


package org.smartfrog.tools.eclipse.core.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.help.WorkbenchHelp;

import org.smartfrog.tools.eclipse.core.CoreUtilities;
import org.smartfrog.tools.eclipse.model.IHelpContextIds;


public class NewProjectCreationPage
    extends WizardNewProjectCreationPage
{
    /**
     * @param pageName
     */
    public NewProjectCreationPage(String pageName)
    {
        super(pageName);
    }

    protected boolean validatePage()
    {
        boolean ret = true;
        ret = super.validatePage();

        if (!isValidPrjName(this.getProjectName())) {
            setErrorMessage(CoreUtilities.getResourceString(
                    "NewProjectCreationPage.error.noSpecialCharsAllowed")); //$NON-NLS-1$

            ret = false;
        }

        return ret;
    }
    
    public void createControl(Composite parent) {
        super.createControl(parent);
    	WorkbenchHelp.setHelp(getControl(),
                IHelpContextIds.SMARTFROG_PROJECT_WIZARD_PAGE_HELP_ID);
    }

    /**
     * Valid project name has to be: digit,letter,"-" or "_"
     */
    public boolean isValidPrjName(String str)
    {
        boolean ret = true;

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetterOrDigit(str.charAt(i)) &&
                    ( str.charAt(i) != '-' ) && ( str.charAt(i) != '_' )) {
                ret = false;

                break;
            }
        }

        return ret;
    }
}
