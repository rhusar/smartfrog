
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


package org.smartfrog.tools.eclipse;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * The main plugin class to be used in the desktop.
 */
public class SmartFrogPlugin
    extends AbstractUIPlugin
{
    public final static String PLUGIN_ID =
        "org.smartfrog.tools"; //$NON-NLS-1$

    //The shared instance.
    private static SmartFrogPlugin plugin;

    //Resource bundle.
    private ResourceBundle mResourceBundle;
    public static final String SMARTFROG_PARTITIONING =
        "SMARTFROG_PARTITIONING"; //$NON-NLS-1$

    /**
     * The constructor.
     */
    public SmartFrogPlugin()
    {
        super();
        plugin = this;

        try {
            mResourceBundle = ResourceBundle.getBundle(
                    "org.smartfrog.tools.eclipse.SmartFrogPluginResources"); //$NON-NLS-1$
        } catch (MissingResourceException x) {
            mResourceBundle = null;
        }
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context)
        throws Exception
    {
        super.start(context);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context)
        throws Exception
    {
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     */
    public static SmartFrogPlugin getDefault()
    {
        return plugin;
    }

    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     */
    public static String getResourceString(String key)
    {
        ResourceBundle bundle = SmartFrogPlugin.getDefault()
                                               .getResourceBundle();

        try {
            return ( bundle != null ) ? bundle.getString(key) : key;
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle()
    {
        return mResourceBundle;
    }

    /**
     * Returns the active workbench window.
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow()
    {
        return getDefault().getWorkbench().getActiveWorkbenchWindow();
    }

    /**
     * Returns the active workbench shell.
     *
     */
    public static Shell getActiveWorkbenchShell()
    {
        Shell shell = null;
        IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();

        if (null != activeWorkbenchWindow) {
            shell = activeWorkbenchWindow.getShell();
        }

        return shell;
    }

    /**
     * Returns the active workbench shell.
     *
     */
    public static Shell getWorkbenchShell()
    {
        Shell shell = null;
        IWorkbenchWindow activeWorkbenchWindow = getWorkbenchWindow();

        if (null != activeWorkbenchWindow) {
            shell = activeWorkbenchWindow.getShell();
        }

        return shell;
    }

    /**
     * @return the Workbench window, if can get any
     */
    public static IWorkbenchWindow getWorkbenchWindow()
    {
        IWorkbenchWindow window = getActiveWorkbenchWindow();

        if (null == window) {
            IWorkbenchWindow[] wws = getDefault().getWorkbench()
                                         .getWorkbenchWindows();

            if (0 < wws.length) {
                window = wws[ 0 ];
            }
        }

        return window;
    }

    /**
     * Returns the workspace instance.
     */
    public static IWorkspace getWorkspace()
    {
        return ResourcesPlugin.getWorkspace();
    }

    /**
     * @return plugin ID
     */
    public static String getPluginId()
    {
        return PLUGIN_ID;
    }
}
