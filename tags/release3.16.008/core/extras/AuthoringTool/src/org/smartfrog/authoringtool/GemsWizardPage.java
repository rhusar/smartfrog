
package org.smartfrog.authoringtool;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

import org.gems.designer.InitialContentProvider;
import org.gems.designer.ModelInstance;
import org.gems.designer.ModelInstanceRepository;
import org.gems.designer.ModelRepository;
import org.gems.designer.PluginUtilities;


public class GemsWizardPage 
	extends WizardNewFileCreationPage 
	implements SelectionListener
{


private IWorkbench	workbench;
private static int count = 1;


public GemsWizardPage(IWorkbench aWorkbench, IStructuredSelection selection) {
	super("Smartfrog DSML", selection);  
	this.setTitle("Smartfrog DSML");
	this.setDescription("Create a Smartfrog DSML instance");
	this.setImageDescriptor(ImageDescriptor.createFromFile(getClass(),"icons/logicbanner.gif"));  
	this.workbench = aWorkbench;
}

public void createControl(Composite parent) {
	super.createControl(parent);
	this.setFileName("Smartfrog" + count + ".sfml"); 
	
	Composite composite = (Composite)getControl();

	new Label(composite,SWT.NONE);

	setPageComplete(validatePage());
}

protected InputStream getInitialContents() {
	ByteArrayInputStream bais = null;
	try{

			String intial = "<?xml version=\"1.0\" encoding=\"ASCII\"?>\n<smartfrog:Root xmlns:smartfrog=\"http://www.smartfrog.org/sfml\"></smartfrog:Root>";
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            java.io.OutputStreamWriter out = new java.io.OutputStreamWriter(baos);
            out.write(intial);
            out.flush();
  			out.close();
            baos.close();
            bais = new ByteArrayInputStream(baos.toByteArray());
            bais.close();
	}
	catch(Exception e) {
		e.printStackTrace();
	}
	return bais;
}

public boolean finish() {
	IFile newFile = createNewFile();
	if (newFile == null) 
		return false;  

    Hashtable useroptions = new Hashtable();
	InitialContentProvider[] initcontents = PluginUtilities.getInitialContentProviders(SmartfrogProvider.MODEL_ID);
	for(InitialContentProvider init : initcontents)
		init.provideContent(SmartfrogProvider.getInstance(),newFile, useroptions);
	

	try {
		IWorkbenchWindow dwindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = dwindow.getActivePage();
		if (page != null)
			IDE.openEditor(page, newFile, true);
	} 
	catch (org.eclipse.ui.PartInitException e) {
		e.printStackTrace();
		return false;
	}
	count++;
	return true;
}

/**
 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
 */
public void widgetSelected(SelectionEvent e) {
	
}

public void widgetDefaultSelected(SelectionEvent e) {
}

}