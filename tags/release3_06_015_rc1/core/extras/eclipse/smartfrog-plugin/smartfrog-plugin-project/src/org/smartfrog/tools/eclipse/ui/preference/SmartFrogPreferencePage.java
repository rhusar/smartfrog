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

package org.smartfrog.tools.eclipse.ui.preference;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import org.eclipse.jface.preference.PreferencePage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.help.WorkbenchHelp;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;
import org.smartfrog.tools.eclipse.model.IHelpContextIds;
import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;
import org.smartfrog.tools.eclipse.model.Util;

import java.io.*;

/**
 * SmartFrog preference page
 */
public class SmartFrogPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {
    private static final String JAVA_HOME = "JAVA_HOME"; //$NON-NLS-1$

    private static final String SFHOME_ENV_NAME = "SFHOME"; //$NON-NLS-1$

    private static final int SIZING_TEXT_FIELD_WIDTH = 250;

    public static String SMARTFROG_LOCATION_PREFERENCE = "PreferenceID.SMARTFROG_LOCATION_PREFERENCE"; //$NON-NLS-1$

    public static String RMIC_LOCATION_PREFERENCE = "PreferenceID.RMIC_LOCATION_PREFERENCE"; //$NON-NLS-1$

    private Label mSmartFrogLocationLabel = null;

    private Text mSmartFrogLocationName = null;

    private Button mSmartFrogLocationBrowseButton = null;

    private Label mRmicLocationLabel;

    private Text mRmicLocationName;

    private Button mRmicLocationBrowseButton;

    private ModifyListener mLocationModifyListener = new ModifyListener() {
        public void modifyText(ModifyEvent e) {
            isValidValues();
        }
    };

    private SelectionListener mLocationButtonPressedListener = new SelectionListener() {
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
            DirectoryDialog dialog = new DirectoryDialog(getShell());
            dialog.setText(Messages
                    .getString("SmartFrogPreferencePage.Text.InstallLocation")); //$NON-NLS-1$
            dialog.setFilterPath(mSmartFrogLocationName.getText());

            String filename = dialog.open();

            if (filename != null) {
                if (e.widget == mSmartFrogLocationBrowseButton) {
                    mSmartFrogLocationName.setText(filename);
                }

                if (e.widget == mRmicLocationBrowseButton) {
                    mRmicLocationName.setText(filename);
                }
            }
        }
    };

    /**
     * Initialize the defaults values for browser location and port number.
     */
    public void init(IWorkbench workbench) {
        SmartFrogPlugin.getDefault().getPreferenceStore().setDefault(
                SMARTFROG_LOCATION_PREFERENCE, getDefaultSmartFrogLocaton());
        SmartFrogPlugin.getDefault().getPreferenceStore().setDefault(
                RMIC_LOCATION_PREFERENCE, getDefaultRmicLocaton());
    }

    /**
     * Creates the contents of the preference page, initializes actions to
     * listeners and loads default/previously-chosen values.
     * 
     * @param composite
     *            where the top control would sit.
     * @return control where all the contents are added.
     */
    protected Control createContents(Composite parent) {
        WorkbenchHelp.setHelp(getControl(),
                IHelpContextIds.SMARTFRONG_PLUGIN_PREFERENCE_PAGE_HELP_ID);

        Composite top = new Composite(parent, SWT.NULL);

        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.verticalSpacing = 10;
        top.setLayout(layout);
        top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        mSmartFrogLocationLabel = new Label(top, SWT.NONE);
        mSmartFrogLocationName = new Text(top, SWT.BORDER);
        mSmartFrogLocationBrowseButton = new Button(top, SWT.NONE);
        addEntry(top, mSmartFrogLocationLabel, mSmartFrogLocationName,
                mSmartFrogLocationBrowseButton, Messages
                        .getString("SmartFrogPreferencePage.Text.Home")); //$NON-NLS-1$

        mRmicLocationLabel = new Label(top, SWT.NONE);
        mRmicLocationName = new Text(top, SWT.BORDER);
        mRmicLocationBrowseButton = new Button(top, SWT.NONE);
        addEntry(top, mRmicLocationLabel, mRmicLocationName,
                mRmicLocationBrowseButton, Messages
                        .getString("SmartFrogPreferencePage.text.rmicLocation")); //$NON-NLS-1$

        doLoad();
        isValidValues();

        return top;
    }

    /**
     * @param top
     * @param label
     * @param text
     * @param button
     * @param labelText
     */
    private void addEntry(Composite top, Label label, Text text, Button button,
            String labelText) {
        label.setText(labelText);
        text.setToolTipText(labelText);

        GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
        data2.widthHint = SIZING_TEXT_FIELD_WIDTH;
        text.setLayoutData(data2);
        text.addModifyListener(mLocationModifyListener);

        button.setText(Messages
                .getString("SmartFrogPreferencePage.Text.Browse")); //$NON-NLS-1$
        button.setToolTipText(Messages
                .getString("SmartFrogPreferencePage.ToolTip.Browse")); //$NON-NLS-1$
        button.addSelectionListener(mLocationButtonPressedListener);
    }

    /**
     * doLoad() is a convenience method which retrieves the last stored
     * preference from the preference store.
     */
    protected void doLoad() {
        loadPreferences(mSmartFrogLocationName, SMARTFROG_LOCATION_PREFERENCE);
        loadPreferences(mRmicLocationName, RMIC_LOCATION_PREFERENCE);
    }


    private void loadPreferences(Text text, String preferenceID) {
        String location;
        location = SmartFrogPlugin.getDefault().getPreferenceStore().getString(
                preferenceID);

        if ((location.length() == 0)) {
            doLoadDefault();
        } else {
            text.setText(location);
        }
    }

    /**
     * doLoadDefault() is a convenience method which retrieves the default
     * preference from the preference store.
     */
    protected void doLoadDefault() {
        mSmartFrogLocationName.setText(SmartFrogPlugin.getDefault()
                .getPreferenceStore().getDefaultString(
                        SMARTFROG_LOCATION_PREFERENCE));
        mRmicLocationName.setText(SmartFrogPlugin.getDefault()
                .getPreferenceStore()
                .getDefaultString(RMIC_LOCATION_PREFERENCE));
    }

    /**
     * doStore() is a convenience method which stores the current preferences to
     * the preference store.
     */
    protected void doStore() {
        SmartFrogPlugin.getDefault().getPreferenceStore().setValue(
                SMARTFROG_LOCATION_PREFERENCE,
                mSmartFrogLocationName.getText().trim());
        SmartFrogPlugin.getDefault().getPreferenceStore().setValue(
                RMIC_LOCATION_PREFERENCE, mRmicLocationName.getText().trim());
    }

    /**
     * performDefaults() is a convenience method which retrieves the default
     * preference from the preference store.
     */
    protected void performDefaults() {
        doLoadDefault();
        setValid(true);
        updateApplyButton();
        super.performDefaults();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        if (isValidValues()) {
            doStore();
        }

        return super.performOk();
    }

    /**
     * Validates user input and sets proper error messages. 
     *  
     */
    private boolean isValidValues() {
        String errorMessage = null;
        boolean error = false;
        Path locationPath = new Path(mSmartFrogLocationName.getText().trim());
        error = validatePath(locationPath);

        if (!error) {
            locationPath = new Path(mRmicLocationName.getText().trim());
            error = validatePath(locationPath);
        }

        setValid(!error);
        updateApplyButton();

        return !error;
    }

    /**
     * Validate specified path
     */
    private boolean validatePath(Path path) {
        boolean error = false;
        String errorMessage = null;
        int segCount = path.segmentCount();

        if (segCount > 1) {
            IStatus s = ResourcesPlugin.getWorkspace().validatePath(
                    path.makeUNC(true).toString(), IResource.FILE);
            int severity = s.getSeverity();

            if (severity != IStatus.OK) {
                errorMessage = Messages
                        .getString("SmartFrogPreferencePage.Error.WrongDirName"); //$NON-NLS-1$
                error = true;
            }
        } else if (segCount == 1) {
            IStatus s = ResourcesPlugin.getWorkspace().validateName(
                    path.lastSegment(), IResource.FILE);
            int severity = s.getSeverity();

            if (severity != IStatus.OK) {
                errorMessage = Messages
                        .getString("SmartFrogPreferencePage.Error.WrongDirName"); //$NON-NLS-1$
                error = true;
            }
        }

        if (!error) {
            if (!path.toFile().exists()) {
                errorMessage = Messages.getString("SmartFrogPreferencePage.error.PathNotExist"); //$NON-NLS-1$
                error = true;
            }
        }

        setErrorMessage(errorMessage);
        setMessage(null);

        return error;
    }

    /**
     * Get the default install location, by SFHOME if it has
     * 
     * @return
     */
    static String getDefaultSmartFrogLocaton() {
        String location = Util.getEnv(SFHOME_ENV_NAME);

        if (null == location) {
            location = "/"; //$NON-NLS-1$
        }

        return location;
    }

    static String getDefaultRmicLocaton() {
        String location = Util.getEnv(JAVA_HOME);
        location = location + ISmartFrogConstants.FILE_SEPARATOR + "bin"; //$NON-NLS-1$

        if (null == location) {
            location = "/"; //$NON-NLS-1$
        }

        return location;
    }

    /**
     * Return the SmartFrog install location
     */
    public static String getSmartFrogLocation() {
        return SmartFrogPlugin.getDefault().getPreferenceStore().getString(
                SMARTFROG_LOCATION_PREFERENCE);
    }

    /**
     * Return the SmartFrog install location
     */
    public static String getRmiLocation() {
        return SmartFrogPlugin.getDefault().getPreferenceStore().getString(
                RMIC_LOCATION_PREFERENCE);
    }
   
}