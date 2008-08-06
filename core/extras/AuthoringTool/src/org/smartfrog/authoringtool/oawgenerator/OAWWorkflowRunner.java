package org.smartfrog.authoringtool.oawgenerator;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.gems.designer.oawgenerator.OAWGeneratorConfiguration;
import org.gems.designer.oawgenerator.OAWGeneratorRegistry;
import org.gems.designer.oawgenerator.runwizard.OAWGenerationWizard;
import org.openarchitectureware.workflow.WorkflowRunner;
import org.openarchitectureware.workflow.monitor.NullProgressMonitor;
import org.osgi.framework.Bundle;
import org.smartfrog.authoringtool.SmartfrogPlugin;

/**
 * This class lets the user choose a oAW generator workflow to run.
 * The workflows to choose from are defined in the [projectRoot]/dsml.oawconfig 
 * file.
 * 
 * Generated by the GEMS oAW plugin. oAW documentation:
 * http://www.eclipse.org/gmt/oaw/doc/
 */
public class OAWWorkflowRunner {
	IFile originalInputModelFile = null;
	String modelURI = null;
	URL workflowURL = null;

	public void runWorkflow() {
		// the workbench
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		IEditorPart editor = page.getActiveEditor();

		// get the input model file
		originalInputModelFile = ((FileEditorInput) editor.getEditorInput())
				.getFile();

		// get gemx file for input
		modelURI = originalInputModelFile.getLocation().toFile().toURI()
				.toString() ;
		//+ ".gemx"  Vel

		try {
			// get the properties
			OAWGeneratorRegistry registry = OAWGeneratorRegistry.getInstance();

			// create and open generator chooser wizard
			OAWGenerationWizard wizard = new OAWGenerationWizard();
			WizardDialog wDialog = new WizardDialog(workbenchWindow.getShell(),
					wizard);
			int ret = wDialog.open();
			if (ret == Window.OK) {
				OAWGeneratorConfiguration config = wizard.getGeneratorConfig();
				String workflowURLString = config.getWorkflowFile();
			//	String outputPath = config.getOutputFolder();
				
				
				
				// Fix the output path here : Vel
				IProject ip= originalInputModelFile.getProject(); 
				System.out.println("IProject :" + ip.getName() );
				IPath path =ip.getFolder("src").getRawLocation();
				String outputPath = path.toOSString();

				if (workflowURLString == null) {
					MessageDialog.openError(workbenchWindow.getShell(),	"Error", "Workflow URL was not set!");
					throw new Exception("Workflow URL was not set!");
				}

				// get the bundle to resolve workflow location
				Bundle bundle = Platform.getBundle("org.smartfrog.authoringtool");
				workflowURL = bundle.getResource(workflowURLString);

				if (outputPath != null) {
					Map<String, ?> slotContents = new HashMap();
					Map<String, String> properties = new HashMap<String, String>();
					// set the properties of the ouptput and the URI to the
					// model that can be used in the workflow.oaw file
					properties.put("model", modelURI);
					properties.put("output", outputPath);

					// run workflow
					new WorkflowRunner()
							.run(workflowURL.getFile(),
									new NullProgressMonitor(), properties,
									slotContents);

					MessageDialog.openInformation(workbenchWindow.getShell(),
							"Success",config.getSuccessMessage() + "\n\nGenerated to outlet:\n"
									+ outputPath);
				}
			}
		} catch (IOException e) {
			System.err.println("Problem finding required file.");
			MessageDialog.openError(workbenchWindow.getShell(), "Exception",
					"Error finding required file!: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			MessageDialog.openError(workbenchWindow.getShell(), "Exception", e
					.getMessage());
			e.printStackTrace();
		}
	}

}