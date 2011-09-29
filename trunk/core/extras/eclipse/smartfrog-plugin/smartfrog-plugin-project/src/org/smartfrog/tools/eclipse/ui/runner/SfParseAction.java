package org.smartfrog.tools.eclipse.ui.runner;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.smartfrog.tools.eclipse.SmartFrogPlugin;
import org.smartfrog.tools.eclipse.model.EditorUtil;
import org.smartfrog.tools.eclipse.ui.project.document.DescriptionCreationWizardPage;

public class SfParseAction extends IRunnerAction {

	public void init(IWorkbenchWindow window) {
		mWindow = window;
		mActivePage = window.getActivePage();
	}

	public void run(IAction action) {

		if ((null == getSelectFile())
				|| (!getSelectFile()
						.endsWith(
								DescriptionCreationWizardPage.DEFAULT_DESCRIPTION_EXT_WITH_DOT))) {
			MessageDialog
					.openError(
							mWindow.getShell(),
							Messages
									.getString("SmartFrogGUIRunnerAction.Title.SelectDescriptionFile"), //$NON-NLS-1$
							Messages
									.getString("SmartFrogGUIRunnerAction.Message.SelectDescriptionFile")); //$NON-NLS-1$

			return;
		}
		bringUpConsole();
		SfParseExt sfParser = new SfParseExt(SmartFrogPlugin
				.getWorkbenchWindow().getShell(), mSelectedIFile);
		sfParser.start();

	}

	public void dispose() {

	}

}
