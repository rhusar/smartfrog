
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
package org.smartfrog.tools.eclipse.ui.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;


/**
 * Set up default values
 */
public class SmartFrogPreferenceInit
    extends AbstractPreferenceInitializer
{
    public void initializeDefaultPreferences()
    {
        SmartFrogPlugin.getDefault().getPluginPreferences().setDefault(
            SmartFrogPreferencePage.SMARTFROG_LOCATION_PREFERENCE,
            SmartFrogPreferencePage.getDefaultSmartFrogLocaton());
        SmartFrogPlugin.getDefault().getPluginPreferences().setDefault(
                SmartFrogPreferencePage.RMIC_LOCATION_PREFERENCE,
                SmartFrogPreferencePage.getDefaultRmicLocaton());
    }
}
