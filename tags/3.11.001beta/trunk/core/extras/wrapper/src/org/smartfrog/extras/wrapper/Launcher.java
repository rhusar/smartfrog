/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */

/**
 *  This file is derived (with modifications, including repackaging), from Apache Ant code
 */

/*
 * Copyright  2003-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.smartfrog.extras.wrapper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Taken from the ant launcher and reworked
 * <p/>
 * This class extracts SFHOME from the command line or the environment and
 * executes any -lib operations
 *
 *
 * Uniquely in the .sf code, it uses the Java1.4 logging API.
 * This eliminates a dependency at this stage in the application's launch
 */
public class Launcher {
    /**
     * The SF Home property {@value}
     */
    public static final String SFHOME_PROPERTY = "sf.home";

    /**
     * The SF Home env variable {@value}
     */
    public static final String SFHOME_ENV_VARIABLE = "SFHOME";

    /**
     * The startup class that is to be run: {@value}
     */
    public static final String MAIN_CLASS = "org.smartfrog.extras.wrapper.launcher.WrappedSFSystem";

    /**
     * the name of the 2ary JAR to load, the one that contains WrappedSFSystem;
     * {@value}
     */
    public static final String SECONDARY_JAR = "sf-wrapper-launched.jar";

    public static final String JAVA_CLASSPATH = "java.class.path";

    /**
     * name of the subdir to look for lib files in {@lib}
     */
    public static final String LIB_SUBDIR = "/lib";

    public static final String LOG_NAME = "org.smartfrog.extras.wrapper";
    /**
     * a log
     */
    private static Logger log = Logger.getLogger(LOG_NAME);

    /**
     * Entry point for starting command line; only here for debug purposes.
     *
     * @param args commandline arguments
     */
    public static void main(String[] args) {
        try {
            Launcher launcher = new Launcher();
            LauncherInfo info = launcher.prelaunch(args);
            WrappedEntryPoint wrappedEntryPoint = info.load();
            wrappedEntryPoint.start();
        } catch (LaunchException e) {
            log.log(Level.SEVERE, "no launch", e);
        } catch (Throwable t) {
            log.log(Level.SEVERE, "no launch", t);
        }
    }

    private static Level level = Level.INFO;

    public static void log(String message) {
        log.log(level, message);
    }

    public static void log(String message, Throwable t) {
        log.log(Level.SEVERE, message, t);
    }

    /**
     * Add a CLASSPATH or -lib to lib path urls.
     *
     * @param path        the classpath or lib path to add to the libPathULRLs
     * @param getJars     if true and a path is a directory, add the jars in the
     *                    directory to the path urls
     * @param libPathURLs the list of paths to add to
     */
    private void addPath(String path, boolean getJars, List libPathURLs)
            throws MalformedURLException {
        StringTokenizer myTokenizer
                = new StringTokenizer(path,
                        System.getProperty("path.separator"));
        while (myTokenizer.hasMoreElements()) {
            String elementName = myTokenizer.nextToken();
            File element = new File(elementName);
            if (elementName.indexOf("%") != -1 && !element.exists()) {
                //strip out junk from DOS land
                continue;
            }
            if (getJars && element.isDirectory()) {
                // add any jars in the directory
                URL[] dirURLs = Locator.getLocationURLs(element);
                for (int j = 0; j < dirURLs.length; ++j) {
                    libPathURLs.add(dirURLs[j]);
                }
            }

            libPathURLs.add(element.toURL());
        }
    }

    /**
     * Run the launcher to launch Ant. Adds some extra arguments to the command
     * line <ul> <li> -lib dir : set a directory of JAR files <li> -libclasses
     * dir : set a directory of classes to add to the classpath <li> -cp
     * classpath : any classpath <li> --noclasspath: no classpath <li> -debug
     * extra logging of the launcher </ul>
     *
     * @param args the command line arguments
     * @throws MalformedURLException if the URLs required for the classloader
     *                               cannot be created.
     */
    public LauncherInfo prelaunch(String[] args) throws LaunchException,
            MalformedURLException {

        List libPaths = new ArrayList();
        String cpString = null;
        List argList = new ArrayList();
        String[] newArgs;
        boolean noClassPath = false;
        List libPathURLs = new ArrayList();

        //crack the arguments, stripping out our specials
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-lib")) {
                if (i == args.length - 1) {
                    throw new LaunchException("The -lib argument must "
                            + "be followed by a library location");
                }
                libPaths.add(args[++i]);
            } else if ("-libclasses".equals(args[i])) {
                if (i == args.length - 1) {
                    throw new LaunchException("The -libclasses argument must "
                            + "be followed by a directory");
                }
                File dir = new File(args[++i]);
                if (!dir.exists()) {
                    throw new LaunchException("Missing libclasses :" + dir);
                }
                libPathURLs.add(dir.toURL());
            } else if ("-cp".equals(args[i])) {
                if (i == args.length - 1) {
                    throw new LaunchException("The -cp argument must "
                            + "be followed by a classpath expression");
                }
                if (cpString != null) {
                    throw new LaunchException("The -cp argument must "
                            + "not be repeated");
                }
                cpString = args[++i];
            } else if ("--noclasspath".equals(args[i]) ||
                    "-noclasspath".equals(args[i])) {
                noClassPath = true;
            } else if ("-debug".equals(args[i])) {
                //debugging otions
                level = Level.FINEST;
            } else {
                argList.add(args[i]);
            }
        }

        String sfHomeProperty = System.getProperty(SFHOME_PROPERTY);
        if (sfHomeProperty == null) {
            sfHomeProperty = System.getenv(SFHOME_ENV_VARIABLE);
        }
        File home = null;

        File sourceJar = Locator.getClassSource(getClass());

        //get the dir we came from too.
        File jarDir;
        if (sourceJar.isDirectory()) {
            jarDir = sourceJar;
        } else {
            jarDir = sourceJar.getParentFile();
        }

        if (sfHomeProperty != null) {
            home = new File(sfHomeProperty);
        }
/*
        if (home == null || !home.exists()) {
            home = jarDir.getParentFile();
            System.setProperty(SFHOME_PROPERTY, home.getAbsolutePath());
        }
*/

        if (!home.exists()) {
            throw new LaunchException("SFHOME environment variable or sf.home property is not set correctly; "
                    + "SmartFrog could not be located");
        }


        //add in the 2ary file if it exists, bail out if not
        File secondary = new File(jarDir, SECONDARY_JAR);
        if (!secondary.exists()) {
            //throw new LaunchException("No secondary JAR found at "+secondary);
            log.warning("No secondary JAR found at " + secondary);
        }
        libPaths.add(secondary);

        //build a new set of params from the list of all args excluding the special
        //operations that only we handle
        newArgs = (String[]) argList.toArray(new String[0]);


        if (cpString != null && !noClassPath) {
            addPath(cpString, false, libPathURLs);
        }

        for (Iterator i = libPaths.iterator(); i.hasNext();) {
            String libPath = i.next().toString();
            addPath(libPath, true, libPathURLs);
        }

        URL[] libJars = (URL[]) libPathURLs.toArray(new URL[0]);

        //fetch all JARs in the SFHOME dir

        File libdir = new File(home + LIB_SUBDIR);
        if (!libdir.exists()) {
            throw new LaunchException("No library directory " + libdir);
        }
        URL[] systemJars = Locator.getLocationURLs(libdir);

        int numJars = libJars.length + systemJars.length;
        if (numJars == 0) {
            throw new LaunchException(
                    "No JARs found in " + home + " or on the command line");
        }
        URL[] jars = new URL[numJars];
        System.arraycopy(libJars, 0, jars, 0, libJars.length);
        System.arraycopy(systemJars, 0, jars, libJars.length,
                systemJars.length);


        // now update the class.path property
        StringBuffer baseClassPath
                = new StringBuffer(System.getProperty(JAVA_CLASSPATH));
        if (baseClassPath.charAt(baseClassPath.length() - 1)
                == File.pathSeparatorChar) {
            baseClassPath.setLength(baseClassPath.length() - 1);
        }

        for (int i = 0; i < jars.length; ++i) {
            baseClassPath.append(File.pathSeparatorChar);
            baseClassPath.append(Locator.fromURI(jars[i].toString()));
        }

        LauncherInfo subprocess = new LauncherInfo();

        String newClasspath = baseClassPath.toString();
        subprocess.setProperty(JAVA_CLASSPATH, newClasspath);
        subprocess.setProperty(SFHOME_PROPERTY, home.getAbsolutePath());

        URLClassLoader loader = new URLClassLoader(jars);
        Thread.currentThread().setContextClassLoader(loader);
        subprocess.jars = jars;
        log("jars = " + makeString(jars));
        subprocess.classloader = loader;
        log("args = " + makeString(newArgs));
        subprocess.processedArgs = newArgs;
        return subprocess;
    }

    public static String makeString(Object[] array) {
        StringBuffer buffer = new StringBuffer();
        buffer.append('[');
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                buffer.append(array[i].toString());
            }
        }
        buffer.append(']');
        return buffer.toString();
    }


    /**
     * this is the launcher info that we return
     */
    public static class LauncherInfo {

        public String[] processedArgs;
        /**
         * array of jars
         */
        public URL[] jars;

        /**
         * classloader set up to load the libraries
         */
        public ClassLoader classloader;

        public Properties properties = new Properties();

        public void setProperty(String name, String value) {
            properties.setProperty(name, value);
        }

        /**
         * construct a new classloader and create an isntance of our process
         *
         * @return
         * @throws IllegalAccessException
         * @throws InstantiationException
         * @throws ClassNotFoundException
         */
        WrappedEntryPoint load() throws IllegalAccessException,
                InstantiationException, ClassNotFoundException,
                LaunchException {
            log("loading \n" + this);
            URLClassLoader loader = new URLClassLoader(jars);
            //Thread.currentThread().setContextClassLoader(loader);
            Class mainClass = null;
            try {
                mainClass = loader.loadClass(MAIN_CLASS);
            } catch (ClassNotFoundException e) {
                log("Class not found " + MAIN_CLASS, e);
                throw e;
            } catch (NoClassDefFoundError e) {
                log("No class Def found" + e.getMessage(), e);
                throw e;
            }
            //set the system properties
            Enumeration it = properties.keys();
            while (it.hasMoreElements()) {
                String key = (String) it.nextElement();
                String value = (String) properties.get(key);
                log("Property " + key + " = " + value);
                System.setProperty(key, value);
            }

            WrappedEntryPoint entryPoint = (WrappedEntryPoint) mainClass.newInstance();
            entryPoint.setArgs(processedArgs);
            return entryPoint;
        }

        /**
         * for debugging
         *
         * @return args and jars
         */
        public String toString() {
            return "Args :" +
                    makeString(processedArgs)
                    + "\nJARS:" + makeString(jars);
        }

        /**
         * declare that we are a root process
         */
        public void addRootProcessProperty() {
            setProperty("org.smartfrog.sfcore.processcompound.sfProcessName;",
                    "rootProcess");
        }

        /**
         * force security on
         */
        public void forceSecurity() {
            setProperty("org.smartfrog.sfcore.security.required", "true");
        }
    }

}

