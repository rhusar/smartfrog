package org.smartfrog.tools.eclipse.ui.runner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;

class SfPingWizard extends Wizard {

	private ImageDescriptor mImage = null;
	private String mDesc = null;
	private String mTitle = null;
	private IFile mFile = null;
	private IProject mProject = null;
	private SfProcessRunnerWizardPage runnerPage;
	private String mSelectedFile;
	private SfPingExt mSfPingTool;

	public SfPingWizard(String windowTitle, String pageTitle, String desc,
			String selectedFile, IFile selectedIFile) {
		setWindowTitle(windowTitle);
		mTitle = pageTitle;
		mDesc = desc;
		mSelectedFile = selectedFile;
		mFile = selectedIFile;
	}

	public void addPages() {
		runnerPage = new SfProcessRunnerWizardPage(
				Messages.getString("SfPingWizard.title.SFProcess"), //$NON-NLS-1$
				Messages
						.getString("SfPingWizard.description.SFProcess"), null, //$NON-NLS-1$
				false); //$NON-NLS-1$
		addPage(runnerPage);
	}

	public boolean performFinish() {
		String hostName = runnerPage.getHostName();
		String processName = runnerPage.getProcessName();
		mSfPingTool = new SfPingExt(SmartFrogPlugin.getWorkbenchWindow().getShell(), hostName, processName);
		mSfPingTool.start();
		return true;
	}

	public SfPingExt getPingTool() {
		return mSfPingTool;
	}
}
