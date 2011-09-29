
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

/*
 */
package org.smartfrog.tools.eclipse.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.smartfrog.tools.eclipse.SmartFrogPlugin;

/**
 * Utility methods to access SmartFrog project structure
 */
public class SmartFrogProjectUtil
{
 
//    private static String getSrcPathName(IFile selectedIFile)
//    {
//        String srcPath = null;
//        IProject project = selectedIFile.getProject();
//    	IProjectNature nature =null;
//    	try {
//    		nature= project.getNature("org.eclipse.jdt.core.javanature"); //$NON-NLS-1$
//		} catch (CoreException e1) {
//		    return null;
//		    
//		}
//    	
//    	if (nature instanceof IJavaProject)
//    	{
//    		try {
//    			IPath outputLocation = ((IJavaProject)nature).getProject().getParent().getLocation();
//    			IPath outputLocation2 = ((IJavaProject)nature).getOutputLocation();
//    			String classpath = outputLocation.append(outputLocation2).toOSString();
//			} catch (JavaModelException e) {
//			    return null;
//			}
//    	} else
//    	{
//    	    return null;
//    	}
//		return srcPath;
//
//    }
    
    /**
     * Return selected SmartFrog file's output absolute path
     * @param selectedIFile	Selected SmartFrog project
     * @return
     */
    public static String getbinPathName(IFile selectedIFile)
    {
        String binPathStr = null;
     
        IProject project = selectedIFile.getProject();
    	IProjectNature nature =null;
    	try {
    		nature= project.getNature("org.eclipse.jdt.core.javanature"); //$NON-NLS-1$
		} catch (CoreException e1) {
		    MessageDialog.openError(SmartFrogPlugin.getWorkbenchShell(),
	                    Messages.getString("SmartFrogProjectUtil.title.selectSFFile"),  //$NON-NLS-1$
	                    Messages.getString("SmartFrogProjectUtil.description.selectSFFile") + e1.getMessage()); //$NON-NLS-1$
		}
    	
    	if (nature instanceof IJavaProject)
    	{
    		try {
    			IPath outputLocation = ((IJavaProject)nature).getProject().getLocation();
    			IPath outputLocation2 = ((IJavaProject)nature).getOutputLocation().removeFirstSegments(1);
    			binPathStr = outputLocation.append(outputLocation2).toOSString();

			} catch (JavaModelException e) {
				ExceptionHandler.handleInSWTThread(e,  Messages.getString("SmartFrogProjectUtil.title.selectSFFile"),  //$NON-NLS-1$
	                    Messages.getString("SmartFrogProjectUtil.description.selectSFFile")); //$NON-NLS-1$ //$NON-NLS-2$

			}
    	} else
    	{
    		ExceptionHandler.handleInSWTThread(new Exception(Messages.getString("SmartFrogProjectUtil.title.noSFProject")),Messages.getString("SmartFrogProjectUtil.7"),Messages.getString("SmartFrogProjectUtil.8")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    	}
    	
        return binPathStr;
    }
    

}
