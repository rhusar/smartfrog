
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


package org.smartfrog.tools.eclipse.core;

import java.io.InputStream;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * The main plugin class to be used in the desktop.
 *
 */
public class CoreUtilities
{
    //Resource bundle.
    private static ResourceBundle sResourceBundle;

    private static ResourceBundle getBundle()
    {
        if (sResourceBundle == null) {
            try {
                sResourceBundle = ResourceBundle.getBundle(
                        "org.smartfrog.tools.eclipse.core.CoreResources"); //$NON-NLS-1$
            } catch (MissingResourceException x) {
                sResourceBundle = null;
            }
        }

        return sResourceBundle;
    }

    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     */
    public static String getResourceString(String key)
    {
        ResourceBundle bundle = getBundle();

        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public static InputStream getResourceStream(String pathToResource)
    {
        try {
            return Class.forName(
                    "org.smartfrog.tools.eclipse.core.CoreUtilities") //$NON-NLS-1$
                        .getResourceAsStream(pathToResource);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
