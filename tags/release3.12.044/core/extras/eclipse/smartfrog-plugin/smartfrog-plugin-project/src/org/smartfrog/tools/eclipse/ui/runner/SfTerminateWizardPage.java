
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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * Wizard page to collect the SF process information
 * No validation for the value yet
 */

public class SfTerminateWizardPage
    extends WizardPage
{
    private static final String DEFAULT_PROCESSNAME = "ProcessName"; //$NON-NLS-1$
    private static final String DEFAULT_HOST = "127.0.0.1"; //$NON-NLS-1$
    private Label mLocationLabel;

    private Text mLocationName;

    private Label mProcessNameLabel;

    private Combo mProcessName;

    private boolean mForStopper;

    /**
     * @param title Wizard title
     * @param desc Wizard page description
     * @param image Wizard page image
     * @param forStopper for the Stopper wizard or Runner wizard
     *                         Stopper wizard only need process name
     */
    public SfTerminateWizardPage(String title, String desc,
        ImageDescriptor image, boolean forStopper)
    {
        super(title, title, image);
        setDescription(desc);
        mForStopper = forStopper;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent)
    {
        Composite topPage = new Composite(parent, SWT.NULL);

        topPage.setLayout(new FillLayout());

        Composite setttingsGrp = createSettingsGroup(topPage);
        setControl(topPage);
    }

    /**
     * Create settings group
     */
    private Composite createSettingsGroup(Composite parent)
    {
        Composite group = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = 10;
        group.setLayout(layout);

        if (!mForStopper) {
            mLocationLabel = new Label(group, SWT.NONE);
            mLocationLabel.setText(Messages.getString(
                    "SfProcessRunnerWizardPage.text.HostName")); //$NON-NLS-1$
            mLocationName = new Text(group, SWT.BORDER);
            mLocationName.setToolTipText(Messages.getString(
                    "SfProcessRunnerWizardPage.toolTip.HostName")); //$NON-NLS-1$
            mLocationName.setText(DEFAULT_HOST);

            GridData data1 = new GridData(GridData.FILL_HORIZONTAL);
            data1.widthHint = 250;
            mLocationName.setLayoutData(data1);
        }

        mProcessNameLabel = new Label(group, SWT.NONE);
        mProcessNameLabel.setText(Messages.getString(
                "SfProcessRunnerWizardPage.text.processName")); //$NON-NLS-1$
        mProcessName = new Combo(group, SWT.BORDER);
        mProcessName.setToolTipText(Messages.getString(
                "SfProcessRunnerWizardPage.toolTip.ProcessName")); //$NON-NLS-1$
        mProcessName.setText(DEFAULT_PROCESSNAME);
        // add the list of deployed components
        Object obj[][]=MngProcess.getInstance().getListProcesses();
        for (int i=0; i < obj.length; i++ )
        {
        mProcessName.add(obj[i][0].toString());
        }
        GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
        data2.widthHint = 250;
        mProcessName.setLayoutData(data2);

        return group;
    }

    /**
     * Return host name
     */
    public String getHostName()
    {
        if (mForStopper) {
            return null;
        } else {
            return mLocationName.getText().trim();
        }
    }

    /**
     *
     * @return entered process name
     */
    public String getProcessName()
    {
        return mProcessName.getText().trim();
    }
}
