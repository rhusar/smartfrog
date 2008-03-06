package org.smartfrog.tools.eclipse.ui.runner;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.smartfrog.tools.eclipse.SmartFrogPlugin;
import org.smartfrog.tools.eclipse.model.EditorUtil;
import org.smartfrog.tools.eclipse.ui.project.document.DescriptionCreationWizardPage;

public class SfPingAction extends IRunnerAction {

	public static SfPingExt PingTool = null;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		mWindow = window;
		mActivePage = window.getActivePage();
	}

	public void run(IAction action) {
		bringUpConsole();

		SfPingWizard wizard = new SfPingWizard(
				Messages.getString("SfPingAction.title.SFProcess"), Messages.getString("SfPingAction.widnowtitle.SFProcess"), Messages.getString("SfPingAction.description.SFProcess"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				getSelectFile(), mSelectedIFile);
		ResizableWizardDialog dialog = new ResizableWizardDialog(mWindow
				.getShell(), wizard);
		dialog.open();
		PingTool = wizard.getPingTool();

	}

	public static SfPingExt getPingTool() {
		return PingTool;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
