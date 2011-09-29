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

package org.smartfrog.tools.eclipse.model.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;
import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;
import org.smartfrog.tools.eclipse.model.SmartFrogProjectUtil;
import org.smartfrog.tools.eclipse.model.Util;
import org.smartfrog.tools.eclipse.ui.preference.SmartFrogPreferencePage;
import org.smartfrog.tools.eclipse.ui.project.document.BaseComponentCreationWizardPage;
import org.smartfrog.tools.eclipse.ui.runner.StreamGobbler;

import java.io.File;

/**
 * Use rmic compiler to build the RMI classes.
 */
public class BasicSmartFrogBuilder {
    private static final String DESTINATION_OPTION = "-d"; //$NON-NLS-1$

    private static final String REMOTE_INTERFACE = "Remote"; //$NON-NLS-1$

    protected String mClassPath = SmartFrogPreferencePage
            .getSmartFrogLocation()
            +  SmartFrogPlugin.getSmartFrogLib()[ 0 ]
            + Util.getClassSeparator()
            + SmartFrogPreferencePage.getSmartFrogLocation()
            +  SmartFrogPlugin.getSmartFrogLib()[ 1 ]
            + Util.getClassSeparator()
            + SmartFrogPreferencePage.getSmartFrogLocation()
            + ISmartFrogConstants.SMARTFROG_GUI_TOOLS_LIB;

    /**
     * check whether the selected resource is RMI class or not, if yes, build it
     * 
     * @param resource  the selected resource
     * @return @throws
     *         CoreException
     */
    protected boolean checkRmiClass(IResource resource) throws CoreException {
        boolean rmiClass = false;

        if ((null != resource) && (resource instanceof IFile)
                && (resource.exists())) {
            IPath sourcePath = new Path("src"); //TODO hardcoded path, need to find the API //$NON-NLS-1$
            IPath resourcePath = resource.getProjectRelativePath();

            if (sourcePath.isPrefixOf(resourcePath)
                    && (resourcePath.toOSString()
                            .endsWith(BaseComponentCreationWizardPage.DEFAULT_DESCRIPTION_EXT))) {
                IJavaElement element = JavaCore.create(resource);
                ICompilationUnit cu = (ICompilationUnit) element;
                IType[] allTypes = cu.getAllTypes();

                if (allTypes.length < 1) {
                    return false;
                }

                IType type = allTypes[0]; //This should be a resource type
                if (type.isClass() == false)
            	{
            	return false;
            	}
                ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
                IType[] allInterfaces = hierarchy.getAllInterfaces();

                for (int i = 0; i < allInterfaces.length; i++) {
                    String elementName = allInterfaces[i].getElementName();

                    if (elementName.equals(REMOTE_INTERFACE)) {
                        //TODO have not check the package name yet
                        rmiClass = true;

                        break;
                    }
                }

                if (rmiClass) {
                    IPackageDeclaration[] packageDeclarations = cu
                            .getPackageDeclarations();

                    String packageName = ""; //$NON-NLS-1$

                    if (packageDeclarations.length > 0) {
                        //should have one package name only
                        packageName = packageDeclarations[0].getElementName();
                    }

                    buildRMI((IFile) resource, packageName.trim());
                }
            }
        }

        return rmiClass;
    }

    /**
     * use rmi compiler to compile the specified Java File
     * 
     * @param javaSource the selected JavaFile (BaseComponentCreationWizardPage.DEFAULT_DESCRIPTION_EXT)
     * @param packageName	The package name of the selected JavaFile
     * @return
     * @throws CoreException
     */
    private boolean buildRMI(IFile javaSource, String packageName) throws CoreException {
        boolean ret = true;

        String fileStr = javaSource.getName();
        if (packageName.length() > 0)
        {
        	fileStr = packageName + "." + fileStr; //$NON-NLS-1$
        }

        fileStr = fileStr.substring(0, fileStr.length() - BaseComponentCreationWizardPage.DEFAULT_DESCRIPTION_EXT.length());

        String workDir = javaSource.getProject().getLocation().toOSString();
        String binDir = SmartFrogProjectUtil.getbinPathName(javaSource);

        String rmiLocation = SmartFrogPreferencePage.getRmiLocation();
        try {
                        
            String cmds[] = new String[6];
            cmds[0]=  SmartFrogPreferencePage.getRmiLocation()
            + ISmartFrogConstants.FILE_SEPARATOR + "rmic" ;
            cmds[1] = "-classpath";
            cmds[2] = mClassPath + Util.getClassSeparator() + binDir; 
            cmds[3] = DESTINATION_OPTION;
            cmds[4] = binDir ;
            cmds[5] = fileStr;
            Process mProcess = Runtime.getRuntime().exec(cmds, null,
                    new File(workDir + "/src")); //$NON-NLS-1$
            ( new StreamGobbler(mProcess.getInputStream(), "builder:ERROR") ).start(); //$NON-NLS-1$
            ( new StreamGobbler(mProcess.getErrorStream(), "builder:OUTPUT") ).start(); //$NON-NLS-1$

            int status = mProcess.waitFor();
        } catch (Exception e) {
            throw(new CoreException(
                    new Status(IStatus.ERROR,
                        SmartFrogPlugin.getPluginId(), 0, 
                        Messages.getString("BasicSmartFrogBuilder.error.RmicCompilerError"), e))); //$NON-NLS-1$
            
        }

        return ret;
    }
}