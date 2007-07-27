
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

import org.smartfrog.tools.eclipse.SmartFrogPlugin;

/**
 * Store all help page IDs
 */
public interface IHelpContextIds
{
	 public static final String PREFIX = SmartFrogPlugin.getPluginId(  ) + "."; //$NON-NLS-1$
    String NEW_DESCRIPTION_MAIN_PAGE_HELP_ID =PREFIX+
        "NEW_DESCRIPTION_MAIN_PAGE_HELP_ID"; //$NON-NLS-1$
    String COMPOUND_WIZARD_PAGE_HELP_ID = PREFIX+"COMPOUND_WIZARD_PAGE_HELP_ID";//$NON-NLS-1$
	String PRIM_WIZARD_PAGE_HELP_ID = PREFIX+"PRIM_WIZARD_PAGE_HELP_ID";//$NON-NLS-1$
	String SMARTFRONG_PLUGIN_PREFERENCE_PAGE_HELP_ID = PREFIX+"SMARTFRONG_PLUGIN_PREFERENCE_PAGE_HELP_ID";//$NON-NLS-1$
	String DESCRIPTION_EDITOR_HELP_ID = PREFIX+"DESCRIPTION_EDITOR_HELP_ID";//$NON-NLS-1$
	String SMARTFROG_PROJECT_WIZARD_PAGE_HELP_ID =PREFIX+ "SMARTFROG_PROJECT_WIZARD_PAGE_HELP_ID";//$NON-NLS-1$
	String SMARTFROG_PROJECT_JAVA_WIZARD_PAGE_HELP_ID = PREFIX+"SMARTFROG_PROJECT_JAVA_WIZARD_PAGE_HELP_ID";//$NON-NLS-1$
}
