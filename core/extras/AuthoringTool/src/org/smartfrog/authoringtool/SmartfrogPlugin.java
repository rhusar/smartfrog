package org.smartfrog.authoringtool;

import org.eclipse.ui.plugin.*;
import org.osgi.framework.BundleContext;
import java.util.*;


public class SmartfrogPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static SmartfrogPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
  

	public SmartfrogPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.smartfrog.authoringtool.SmartfrogPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}


	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	public static SmartfrogPlugin getDefault() {
		return plugin;
	}


	public static String getResourceString(String key) {
		ResourceBundle bundle = SmartfrogPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}
