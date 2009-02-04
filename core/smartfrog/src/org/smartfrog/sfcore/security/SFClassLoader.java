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

package org.smartfrog.sfcore.security;

import org.smartfrog.sfcore.common.SmartFrogCoreProperty;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.server.RMIClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;


/**
 * Provides static methods to obtain a class loader used to download SmartFrog
 * components and their descriptions. If the system level property
 * org.smartfrog.codebase has been set to a URL (or semi-colon separated
 * URLs), it will return an RMI class loader with these addresses. Otherwise
 * it will return the context class loader of the current thread.
 */
public class SFClassLoader {
    /**
     * Name of the system property that specifies the URL (or semi-colon
     * separated URLS) from which we download componenents and their
     * descriptions.
     */
    public static final String SF_CODEBASE_PROPERTY = SmartFrogCoreProperty.codebase;

    /** Space separated urls from which we download the components */
    private static String targetClassBase = null;

    /** A debugging utility to print messages. */
    private static SFDebug debug;

    /**
     * Initializes the debugging.
     */
    static {
        debug = SFDebug.getInstance("SFClassLoader");
    }

    /**
     * Don't let anyone create one of these.
     */
    private SFClassLoader() {
    }

    /**
     * Loads (or reloads) from a system property from where we are downloading
     * components, represented by a space separated urls.
     */
    public synchronized static void loadTargetClassBase() {
        targetClassBase = System.getProperty(SF_CODEBASE_PROPERTY);
    }

    /**
     * Gets the codebase URL(s) path from a system property, formatted for the
     * RMIClassLoader syntax (space sparated URLs).
     *
     * @return a string containing the space separated URLs.
     */
    public synchronized static String getTargetClassBase() {
        if (targetClassBase == null) {
            loadTargetClassBase();
        }

        return targetClassBase;
    }
    /**
     * Gets a class loader for a particular codebase using an RMICLassLoader.If
     * a class loader with the same codebase URL path already exists,i.e., the
     * codebase did not change from the last invocation , it will be returned;
     * otherwise, a new class loader will be created. In any case, the
     * delegation parent of the URL class loader is the current thread context
     * class loader, so this one will always be queried. If the codebase is
     * null we return the current thread context class loader.
     *
     * @param codebase A codebase that defines the class loader.
     *
     * @return A class loader for that codebase.
     */
    static ClassLoader getClassLoader(String codebase) {
        if (codebase != null) {
            try {
                return RMIClassLoader.getClassLoader(codebase);
            } catch (Exception e) {
                //Just log and continue
                if (debug != null) {
                    debug.println("getClassLoader: Cannot get codebase " +
                        codebase + " getting exception " + e.getMessage());
                }
            }
        }

        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Returns a stream that points to a resource specified in a string,
     * typically a configuration file. This string could be either directly
     * loadable from our current class loaders or converted to a file-relative
     * URL. If security is activated, we check that it comes from a trusted
     * source and it has not been modified. We use the default codebase.
     *
     * @param resource name of the resource wanted.
     *
     * @return A stream to the resource or null if either is not available
     *         through our class loaders or does not fullfill the security
     *         requirements.
     */
    public static InputStream getResourceAsStream(String resource) {
        return getResourceAsStream(resource, null, true);
    }

    /**
     * Returns a stream that points to a resource specified in a string,
     * typically a configuration file. This string could be either directly
     * loadable from our current class loaders or converted to a file-relative
     * URL. If security is activated, we check that it comes from a trusted
     * source and it has not been modified.
     *
     * @param resource name of the resource wanted.
     * @param codebase suggested codebase for the classloader
     * @param useDefaultCodebase whether to try to find the class in the
     *        default codebase before using codebase.
     *
     * @return A stream to the resource or null if either is not available
     *         through our class loaders or does not fullfill the security
     *         requirements.
     */
    public static InputStream getResourceAsStream(String resource,
        String codebase, boolean useDefaultCodebase) {
        URL resourceURL;

        try {
            // Try first to directly generate a URL from resource
            resourceURL = new URL(resource);
            debug.println(resourceURL.toString());

            return getURLAsStream(resourceURL);
        } catch (Throwable e) {
            // Didn't work, the input is a malformed url or it is inside a
            // jar file and this is not explicit in the url.
            if (debug != null) {
                debug.println("getResourceAsStream:1 Cannot get url " +
                    e.getMessage());
            }
        }

        try {
            // Let's use a relative file path...
            resourceURL = stringToURL(resource);

            return getURLAsStream(resourceURL);
        } catch (Throwable e) {
            // Still in trouble, cannot obtain the resource from the file
            // system directly.
            if (debug != null) {
                debug.println("getResourceAsStream:2 Cannot get url " +
                    e.getMessage());
            }
        }

        // The preceeding / does not work when looking inside jar files.
        String resourceInJar = (resource.startsWith("/")
            ? resource.substring(1) : resource);

        // Try the class loaders
        Object result = classLoaderHelper(resourceInJar, codebase,
                useDefaultCodebase, false);

        return ((result instanceof InputStream) ? (InputStream) result : null);
    }

    /**
     * Takes a string and converts to a URL. If the string is not in URL format
     * an attempt is made to create a file based URL relative to where this
     * process started. If it is pointing to a jar file we use a jar-type URL.
     *
     * @param s string to convert
     *
     * @return URL form of the input string
     *
     * @throws Exception if failed to convert string to URL
     */
    static URL stringToURL(String s) throws Exception {
        try {
            return (((s.endsWith(".jar")) && (!(s.startsWith("jar:"))))
            ? new URL("jar:" + s + "!/") : new URL(s));
        } catch (Exception ex) {
            // ignore, try another one
        }

        String fUrl = (new File(s)).getAbsolutePath();
        fUrl = "file:" + (fUrl.startsWith("/") ? fUrl : ("/" + fUrl));

        return (((s.endsWith(".jar")) && (!(s.startsWith("jar:"))))
        ? new URL("jar:" + fUrl + "!/") : new URL(fUrl));
    }

    /**
     * Returns a stream pointing to a given resource specified by a URL after
     * performing security checks and making sure the resource exists.
     *
     * @param resourceURL URL of the input resource.
     *
     * @return Stream that points to that resource
     *
     * @throws ClassNotFoundException The resource does not exist or it does not meet the
     *            security requirements.
     * @throws IOException on IO problems
     */
    protected static InputStream getURLAsStream(URL resourceURL)
            throws ClassNotFoundException, IOException {
        URLConnection con = resourceURL.openConnection();
        InputStream in = getLocalInputStream(con);

        if (in != null) {
            return in;
        } else {
            // We want the caller to keep trying...
            throw new ClassNotFoundException("SFClassLoader::getURLAsStream cannot find " +
                resourceURL);
        }
    }

    /**
     * Checks that the resource pointed by a URLConnection comes from a trusted
     * source, this is, it has been granted the SFCommunityPermission. If this
     * is not the case it throws a security exception. Then, it uses that URL
     * to obtain an input stream to a locally cached object.
     *
     * @param con URLConnection to the resource to be checked.
     *
     * @return Stream that point to the resource. If the resource is in a
     *         signed jar we return a ByteArrayInputStream to a local copy for
     *         security reasons.
     *
     * @throws IOException in case of any error
     */
    protected static InputStream getLocalInputStream(URLConnection con) throws IOException {
        InputStream in = con.getInputStream();
        Certificate[] certs = null;

        if (con instanceof JarURLConnection) {
            // Loaded from a jar file, let's add the certicates.
            JarURLConnection conJar = (JarURLConnection) con;

            // Need to read the full entry so that I can get the certificates.
            int numBytes = in.available();
            byte[] resourceBytes = new byte[numBytes];
            int readBytes = 0;

            while (readBytes != numBytes) {
                // Sometimes the read returns early...
                readBytes += in.read(resourceBytes, readBytes,
                    numBytes - readBytes);
            }

            certs = conJar.getCertificates();

            // Need to return an InputStream to a local copy to avoid that
            // the entry changes after being checked.
            in = new ByteArrayInputStream(resourceBytes);
        }

        CodeSource cs = new CodeSource(con.getURL(), certs);
        Policy pc = Policy.getPolicy();
        PermissionCollection perms = ((pc == null) ? null : pc.getPermissions(cs));

        ProtectionDomain pd = new ProtectionDomain(cs, perms);
        try {
            quickReject(pd);
        } catch (SecurityException e) {
            // Special case where we relax the loading of resources, normally descriptions from the local filesystem
            Boolean secureResourcesOn = !SFSecurity.isSecureResourcesOff();
            if (secureResourcesOn) {                
              throw e;
            } else {
                if (debug != null) {
                  debug.println("WARNING!!:quickReject:  " + "access check failed for " + pd +" but secureResoucesOff is "+ !secureResourcesOn);
                }
            }
        }

        // No security exception, continues...
        return in;
    }

    /**
     * Checks that the given protection domain include a permission that
     * ensures that is coming from a trusted origin (SFCommunityPermission).
     * If this is not the case, and security is active, it throws a security
     * exception.
     *
     * @param pd a protection domain that needs to be checked.
     */
    public static void quickReject(ProtectionDomain pd) {
        if ((pd != null) && (pd.implies(new SFCommunityPermission()))) {
            // Resource came from a trusted sourced, no problem.
            return;
        }

        if (SFSecurity.isSecurityOn()) {
            // Didn't pass, we should not load this resource.
            throw new SecurityException("SFClassLoader:quickReject: " +
                "access check failed for " + pd);
        } else {
            if (debug != null) {
                debug.println("WARNING!!:quickReject:  " +
                    "access check failed for " + pd);
            }
        }
    }

    /**
     * Checks that a class is coming from a trusted origin. If this is not the
     * case, and security is active, it throws a security exception. This
     * allows to check the origin of resources even if they do not perform any
     * security sensitive operation.
     *
     * @param cl Class whose origin we want to check.
     */
    public static void quickRejectClass(Class cl) {
        quickReject(cl.getProtectionDomain());
    }

    /**
     * Checks that an object comes from a  class of a trusted origin. If this
     * is not the case, and security is active, it throws a security
     * exception. This allows to check the origin of resources even if they do
     * not perform any security sensitive operation.
     *
     * @param obj Object whose origin we want to check.
     */
    public static void quickRejectObject(Object obj) {
        quickRejectClass(obj.getClass());
    }

    /**
     * A helper class to implement getResourceAsStream
     *
     * @param resourceInJar resource to be located
     * @param codebase used to locate the resource
     *
     * @return input stream to the resource
     *
     * @throws ClassNotFoundException if unable to locate the resource
     * @throws IOException on IO problems
     */
    static InputStream getResourceHelper(String resourceInJar, String codebase)
            throws ClassNotFoundException, IOException {
        ClassLoader cl = getClassLoader(codebase);
        URL resourceURL = cl.getResource(resourceInJar);
        if (debug != null) {
            debug.println("ClassLoader for " + resourceInJar + " in jar " + codebase);
            debug.println("cl " + cl.getClass().getName());
            debug.println("cl.getResource(resourceInJar): " +
                    (resourceURL != null ? resourceURL : "not found"));
        }

        if (resourceURL == null) {
            throw new ClassNotFoundException("Unable to locate the resource " + resourceInJar + " in " + codebase);
        }
        return getURLAsStream(resourceURL);
    }

    /**
     * A helper class to implement forName.
     *
     * @param className fully qualified name of the desired class
     * @param codebase suggested codebase for the classloader
     *
     * @return class object representing the desired class
     *
     * @throws ClassNotFoundException if the class cannot be located
     */
    static Class forNameHelper(String className, String codebase)
        throws ClassNotFoundException {
        Class cl = Class.forName(className, true, getClassLoader(codebase));

		/* The classes referenced by cl, and loaded by the same class loader
           when cl is linked, are not checked here. This implies that if
           they are in a different jar file they might not have the same
           priviledges. For this reason, we have to use sealed packages
           and make jar files as self-contained as possible. */
        quickRejectClass(cl);

        return cl;
    }

    /**
     * Switch between loading operations.
     *
     * @param name Resource name to be loaded
     * @param codebase A suggested codebase to load this resource.
     * @param isForName whether we are loading a class or other resource
     *
     * @return A class or an input stream to the resource
     * @throws ClassNotFoundException if unable to locate the resource
     * @throws IOException on IO problems
     */
    static Object opHelper(String name, String codebase, boolean isForName)
            throws ClassNotFoundException, IOException {
        if (isForName) {
            return forNameHelper(name, codebase);
        } else {
            return getResourceHelper(name, codebase);
        }
    }

    /**
     * A helper class that encapsulates resource loading behaviour using a
     * class loader.
     *
     * @param name Resource name to be loaded
     * @param codebase A suggested codebase to load this resource.
     * @param useDefaultCodebase Whether to look in the default codebase or
     *        not.
     * @param isForName whether we are loading a class or other resource
     *
     * @return A class or an input stream to the resource
     */
    static Object classLoaderHelper(String name, String codebase,
        boolean useDefaultCodebase, boolean isForName) {
        if (debug != null) debug.println(" * classLoaderHelper: name "+name+", codebase "+codebase+", usedefaultcodebase "+useDefaultCodebase+", isforname "+isForName+", getTargetClassBase() "+getTargetClassBase());
        String msg = (isForName ? "forName" : "getResourceAsStream");
        Object result;
        // "default" equivalent to "not set".
        if ("default".equals(codebase)) {
            codebase = null;
        }

        //First, try the thread context class loader
        result = opHelperWithReporting(name, null, isForName, msg);
         if (debug != null) {if (result!=null) debug.println("   - Using thread context class loader");};

        // Second try the default codebase (if enabled)
        if (result==null && (useDefaultCodebase) && (getTargetClassBase() != null)) {
            result = opHelperWithReporting(name, getTargetClassBase(), isForName, msg);
             if (debug != null) debug.println("   - Using defaultCodeBase: "+getTargetClassBase());
        }

        //Last, try the class loader for the suggested codebase
        if (result==null && codebase != null) {
            result = opHelperWithReporting(name, codebase, isForName, msg);
            if (debug != null) {
                if (result!=null) {
                    debug.println("   - Using suggested: "+codebase);
                }
            }
        }
        if (debug != null) {
            if (result==null) {
                debug.println("   - Not luck in loading resource. Not found.");
            }
        }
        return result;
    }

    /**
     * a wrapper around {@link #opHelper} that adds reporting
     * @param name
     * @param codebase
     * @param isForName
     * @param msg
     * @throws LinkageError upstream
     * @return Object a wrapper object  for opHelper that adds reporting
     */
    private static Object opHelperWithReporting(String name, String codebase,
                                                boolean isForName, String msg) {
        Object result=null;
        try {
            result = opHelper(name, codebase, isForName);
        } catch (SecurityException se) {
            if (debug != null) {
                debug.println("SecurityException loading "+name+" in " + codebase +
                        " getting exception " + se.getMessage());
            }
        } catch (LinkageError le) {
            //we found the class, but could not handle it
            // We try next class loader throw e;
            if (debug != null) {
                debug.println(msg + " found "+name+" in " + codebase +
                    " getting exception " + le.getMessage());
            }
        } catch (Error t) {
            //anything else of type error here is bad
            //These are things that should not be caught and ignored.
            logOpHelperException(msg, name, codebase, t);
            throw t;
        } catch (ClassNotFoundException cnfe) {
            //ClassNotFound Exception
            // Not valid, continuing ...
            logOpHelperException(msg, name, codebase, cnfe);

        } catch (IOException ioe) {
            // Not valid, continuing ...
            logOpHelperException(msg, name, codebase, ioe);
        }
        return result;
    }

    private static void logOpHelperException(String msg, String name, String codebase, Throwable t) {
        if (debug != null) {
            debug.println(msg + " cannot find "+name+" in codebase \"" + codebase +
                "\" getting exception " + t); // printing toString() rather than getMessages() yields more information
        }
	}

    /**
     * This method is equivalent to Class.forName but it uses the SmartFrog
     * class loader (optionally using a remote web server), and it checks
     * whether the class comes from a trusted origin. Note that the classes
     * referenced by this one, and loaded when this one is linked, are not
     * checked here.
     *
     * @param className fully qualified name of the desired class
     * @param codebase suggested codebase for the classloader
     * @param useDefaultCodebase whether to try to find the class in the
     *        default codebase before using codebase.
     *
     * @return class object representing the desired class
     *
     * @throws ClassNotFoundException if the class cannot be located
     */
    public static Class forName(String className, String codebase,
        boolean useDefaultCodebase) throws ClassNotFoundException {
        // Try the class loaders
        Object result = classLoaderHelper(className, codebase,
                useDefaultCodebase, true);

        if (result instanceof Class) {
            return (Class) result;
        }

		try {
			// try to load the class without references
			// this will produce NoClassDefFoundError's if
			// dependencies are missing
			getClassLoader(codebase).loadClass(className);

			// don't know a case where this would be reached..
			throw new ClassNotFoundException("forName: loading of " + className
                    + " failed, but loading it without references succeeded. Check references.");
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException("forName: Cannot find " + className + " : " + e, e);
		} catch (NoClassDefFoundError err) {
			throw new ClassNotFoundException("forName: Error while loading "
                    + className + ": " + err, err);
		}
    }

    /**
     * This method is equivalent to Class.forName but it uses the SmartFrog
     * class loader (optionally using a remote web server), and it checks
     * whether the class comes from a trusted origin. Note that the classes
     * referenced by this one, and loaded when this one is linked, are not
     * checked here. We use the default codebase.
     *
     * @param className fully qualified name of the desired class
     *
     * @return class object representing the desired class
     *
     * @throws ClassNotFoundException if the class cannot be located
     */
    public static Class forName(String className) throws ClassNotFoundException {
        return forName(className, null, true);
    }
}
