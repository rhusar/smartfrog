
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

import org.smartfrog.tools.eclipse.ui.preference.SmartFrogPreferencePage;

/**
 * Global constants to use in this plugin
 */
public interface ISmartFrogConstants
{
    static final String FILE_SEPARATOR = System.getProperty("file.separator"); //$NON-NLS-1$
    static final String CLASS_SEPARATOR = System.getProperty("path.separator"); //$NON-NLS-1$
    static final String WHITE_SPACE = " "; //$NON-NLS-1$
    static final String SEMICOLON = ";"; //$NON-NLS-1$
    static final String COLON = ":"; //$NON-NLS-1$
    static final String DOUBLE_QUOTE = "\""; //$NON-NLS-1$

  //  static final String CORE_VERSION= SmartFrogPreferencePage.getVersion();

    //GLOBAL ERROR number
    static final int INTERNAL_ERROR = 1;
    static final int ERROR = 1;
    static final int SUCCESS = 0;

    //Action ID, need to be same as the one defined in the plugin.xml
    static final String NEW_SMARTFROG_PROJECT_ID =
        "org.smartfrog.tools.ui.project.smartfrogproject"; //$NON-NLS-1$
    static final String NEW_DESCRIPTION_ID =
        "org.smartfrog.tools.eclipse.ui.project.document.DescriptionCreationWizard"; //$NON-NLS-1$
    static final String NEW_PRIM_COMPONENT_ID =
        "org.smartfrog.tools.eclipse.ui.project.document.ComponentCreationWizard"; //$NON-NLS-1$
    static final String NEW_COMPOUND_COMPONENT_ID = "org.smartfrog.tools.eclipse.ui.project.document.ComponentCompundCreationWizard";//$NON-NLS-1$



   /*    public static final String[] SMARTFROG_DEFAULT_LIBS = {
    		FILE_SEPARATOR + "lib" + FILE_SEPARATOR + "smartfrog.jar", //$NON-NLS-1$
    		FILE_SEPARATOR + "lib" + FILE_SEPARATOR + "sfServices.jar",
            FILE_SEPARATOR + "lib" + FILE_SEPARATOR + "sfExamples.jar" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        };
    //Libs for SmartFrog Framework
    public static final String[] SMARTFROG_LIBS = {
    		FILE_SEPARATOR + "lib" + FILE_SEPARATOR + "smartfrog"+SmartFrogPreferencePage.getVersion()+"jar", //$NON-NLS-1$
    		FILE_SEPARATOR + "lib" + FILE_SEPARATOR + "sfServices"+SmartFrogPreferencePage.getVersion() +"jar",
            FILE_SEPARATOR + "lib" + FILE_SEPARATOR + "sfExamples"+SmartFrogPreferencePage.getVersion()+"jar" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        };  */
    public static String SMARTFROG_GUI_TOOLS_LIB = FILE_SEPARATOR + "lib" + FILE_SEPARATOR + "SFGuiTools.jar"; //$NON-NLS-1$

	static final String SMARTFROG_CONSOLE_ID = "org.smartfrog.tools.eclipse.ui.console.ConsoleView"; //$NON-NLS-1$




}
