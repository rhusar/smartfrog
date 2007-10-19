
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

import org.eclipse.core.runtime.IStatus;

import org.eclipse.jdt.core.JavaConventions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import org.smartfrog.tools.eclipse.core.CoreUtilities;


/**
 * A Wizard Page that allows the user to specify a Java package name.
 */
public class JavaPackagePage
    extends WizardPage
{
    /**
     * SIZING_TEXT_FIELD_WIDTH is the width of a Package Name field.
     */
    private static final int SIZING_TEXT_FIELD_WIDTH = 250;

    /**
     * Package Name controls
     */
    private Text fPackageNameField;
    private Label fPackageLabel;

    /**
     * The listener is used to validate the Java package name
     * entered by the user. Installed on the package name field.
     */
    private Listener locationModifyListener = new Listener() {
            public void handleEvent(Event e)
            {
                setPageComplete(validatePage());
            }
        };

    /**
     * Constructor for JavaPackagePage.
     * @param pageName
     */
    public JavaPackagePage(String pageName)
    {
        super(pageName);
    }

    /**
     * Method validatePage.
     * @return true if a valid package name has been specified.
     */
    private boolean validatePage()
    {
        boolean returnValue = true;

        setErrorMessage(null);
        setMessage(null);

        String packageName = fPackageNameField.getText();

        if (packageName.length() > 0) {
            IStatus s = JavaConventions.validatePackageName(packageName);
            int severity = s.getSeverity();

            if (severity != IStatus.OK) {
                setErrorMessage(s.getMessage());

                if (severity == IStatus.ERROR) {
                    returnValue = false;
                }
            }
        }

        return returnValue;
    }

    /**
     * Constructor for JavaPackagePage.
     * @param pageName
     * @param title
     * @param titleImage
     */
    public JavaPackagePage(String pageName, String title,
        ImageDescriptor titleImage)
    {
        super(pageName, title, titleImage);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent)
    {
        // Layout
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.verticalSpacing = 10;

        // Composite
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        setControl(composite);
        createPageLayout(composite);
    }

    private void createPageLayout(Composite composite)
    {
        createJavaPackageLayout(composite);
    }

    private void createJavaPackageLayout(Composite composite)
    {
        // Let the user to specify the package for the skeleton
        Composite pkgGroup = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        pkgGroup.setLayout(layout);
        pkgGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        fPackageLabel = new Label(pkgGroup, SWT.NONE);

        String packageLabelText = CoreUtilities.getResourceString(
                "Wizard.JavaPackagePagePackageFieldLabel"); //$NON-NLS-1$
        fPackageLabel.setText(packageLabelText);

        fPackageNameField = new Text(pkgGroup, SWT.BORDER);

        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        fPackageNameField.setLayoutData(data);

        // Install the Listener
        fPackageNameField.addListener(SWT.Modify, locationModifyListener);

        fPackageNameField.setEnabled(true);
    }

    /**
     * Utility method that creates a group instance
     * and sets the default layout data.
     *
     * @param parent        the parent for the new composite
     * @param numColumns        the number of columns
     * @return                        the new group
     */
    private Group createGroup(Composite parent, int numColumns)
    {
        // Layout
        GridLayout layout = new GridLayout();
        layout.numColumns = numColumns;

        // Group
        Group group = new Group(parent, SWT.SHADOW_NONE);
        group.setLayout(layout);

        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 3;
        group.setLayoutData(data);

        return group;
    } // createGroup

    /**
     * Utility method that creates a button instance
     * and sets the default layout data.
     *
     * @param parent        the parent for the new label
     * @param text                the text for the new button
     * @return                        the new label
     */
    private Button createButton(Composite parent, String text, int style)
    {
        Button button = new Button(parent, style);
        button.setText(text);

        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        button.setLayoutData(data);

        return button;
    } // createButton

    /**
     * Method getPackageName.
     * @return String - The name of the package the user has entered into
     * the field.
     */
    public String getPackageName()
    {
        return fPackageNameField.getText();
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

        if (visible) {
            fPackageNameField.setFocus();
        }
    }
}
