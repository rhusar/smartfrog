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
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Taken from the ant launcher and reworked
 */
public class Launcher {
    /**
     * The Ant Home property
     */
    public static final String SFHOME_PROPERTY = "sf.home";

    /**
     * The Ant Home property
     */
    public static final String SFHOME_ENV_VARIABLE = "SFHOME";

    /**
     * The startup class that is to be run
     */
    public static final String MAIN_CLASS = "org.smartfrog.extras.wrapper.launcher.WrappedSFSystem";

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
        } catch (LaunchException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
     * Run the launcher to launch Ant
     *
     * @param args the command line arguments
     * @throws MalformedURLException if the URLs required for the classloader
     *                               cannot be created.
     */
    public LauncherInfo prelaunch(String[] args) throws LaunchException,
            MalformedURLException {
        String antHomeProperty = System.getProperty(SFHOME_PROPERTY);
        File antHome = null;

        File sourceJar = Locator.getClassSource(getClass());
        File jarDir = sourceJar.getParentFile();

        if (antHomeProperty != null) {
            antHome = new File(antHomeProperty);
        }

        if (antHome == null || !antHome.exists()) {
            antHome = jarDir.getParentFile();
            System.setProperty(SFHOME_PROPERTY, antHome.getAbsolutePath());
        }

        if (!antHome.exists()) {
            throw new LaunchException("SF home is set incorrectly or "
                    + "ant could not be located");
        }

        List libPaths = new ArrayList();
        String cpString = null;
        List argList = new ArrayList();
        String[] newArgs;
        boolean noUserLib = false;
        boolean noClassPath = false;

        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-lib")) {
                if (i == args.length - 1) {
                    throw new LaunchException("The -lib argument must "
                            + "be followed by a library location");
                }
                libPaths.add(args[++i]);
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
            } else {
                argList.add(args[i]);
            }
        }

        //decide whether to copy the existing arg set, or
        //build a new one from the list of all args excluding the special
        //operations that only we handle
        if (libPaths.size() == 0 && cpString == null) {
            newArgs = args;
        } else {
            newArgs = (String[]) argList.toArray(new String[0]);
        }

        List libPathURLs = new ArrayList();

        if (cpString != null && !noClassPath) {
            addPath(cpString, false, libPathURLs);
        }

        for (Iterator i = libPaths.iterator(); i.hasNext();) {
            String libPath = (String) i.next();
            addPath(libPath, true, libPathURLs);
        }

        URL[] libJars = (URL[]) libPathURLs.toArray(new URL[0]);


        // determine ant library directory for system jars: use property
        // or default using location of ant-launcher.jar
        File antLibDir = null;
        String antLibDirProperty = System.getProperty(SFHOME_PROPERTY);
        if (antLibDirProperty != null) {
            antLibDir = new File(antLibDirProperty);
        }
        if ((antLibDir == null) || !antLibDir.exists()) {
            antLibDir = jarDir;
            System.setProperty(SFHOME_PROPERTY,
                    antLibDir.getAbsolutePath());
        }
        URL[] systemJars = Locator.getLocationURLs(antLibDir);


        int numJars = libJars.length + systemJars.length;
        URL[] jars = new URL[numJars];
        System.arraycopy(libJars, 0, jars, 0, libJars.length);
        System.arraycopy(systemJars, 0, jars, libJars.length,
                systemJars.length);


        // now update the class.path property
        StringBuffer baseClassPath
                = new StringBuffer(System.getProperty("java.class.path"));
        if (baseClassPath.charAt(baseClassPath.length() - 1)
                == File.pathSeparatorChar) {
            baseClassPath.setLength(baseClassPath.length() - 1);
        }

        for (int i = 0; i < jars.length; ++i) {
            baseClassPath.append(File.pathSeparatorChar);
            baseClassPath.append(Locator.fromURI(jars[i].toString()));
        }

        System.setProperty("java.class.path", baseClassPath.toString());

        URLClassLoader loader = new URLClassLoader(jars);
        Thread.currentThread().setContextClassLoader(loader);
        LauncherInfo info = new LauncherInfo();
        info.jars = jars;
        info.classloader = loader;
        info.processedArgs = newArgs;
        return info;
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

        /**
         * construct a new classloader and create an isntance of our process
         *
         * @return
         * @throws IllegalAccessException
         * @throws InstantiationException
         * @throws ClassNotFoundException
         */
        WrappedEntryPoint load() throws IllegalAccessException,
                InstantiationException, ClassNotFoundException {
            URLClassLoader loader = new URLClassLoader(jars);
            //Thread.currentThread().setContextClassLoader(loader);
            Class mainClass = loader.loadClass(MAIN_CLASS);
            return (WrappedEntryPoint) mainClass.newInstance();
        }
    }

}

