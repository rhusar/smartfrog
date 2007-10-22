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
package org.smartfrog.services.axis;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * created 29-Apr-2004 16:43:17
 */

public class DelegatingClassLoader extends ClassLoader {

    private String codebase;


    /**
     * Sets the default assertion status for this class loader to
     * <tt>false</tt> and discards any package defaults or class assertion
     * status settings associated with the class loader.  This method is
     * provided so that class loaders can be made to ignore any command line or
     * persistent assertion status settings and "start with a clean slate."
     * </p>
     *
     * @since 1.4
     */
    public synchronized void clearAssertionStatus() {
        super.clearAssertionStatus();
    }

    /**
     * Sets the default assertion status for this class loader.  This setting
     * determines whether classes loaded by this class loader and initialized
     * in the future will have assertions enabled or disabled by default.
     * This setting may be overridden on a per-package or per-class basis by
     * invoking {@link #setPackageAssertionStatus(String, boolean)} or {@link
     * #setClassAssertionStatus(String, boolean)}.  </p>
     *
     * @param enabled <tt>true</tt> if classes loaded by this class loader will
     *                henceforth have assertions enabled by default, <tt>false</tt>
     *                if they will have assertions disabled by default.
     * @since 1.4
     */
    public synchronized void setDefaultAssertionStatus(boolean enabled) {
        super.setDefaultAssertionStatus(enabled);
    }

    /**
     * Creates a new class loader using the specified parent class loader for
     * delegation.
     * <p/>
     * <p> If there is a security manager, its {@link
     * SecurityManager#checkCreateClassLoader()
     * <tt>checkCreateClassLoader</tt>} method is invoked.  This may result in
     * a security exception.  </p>
     *
     * @param parent The parent class loader
     * @throws SecurityException If a security manager exists and its
     *                           <tt>checkCreateClassLoader</tt> method doesn't allow creation
     *                           of a new class loader.
     * @since 1.2
     */
    protected DelegatingClassLoader(ClassLoader parent, String codebase) {
        super(parent);
        this.codebase = codebase;
    }

    /**
     * Returns all of the <tt>Packages</tt> defined by this class loader and
     * its ancestors.  </p>
     *
     * @return The array of <tt>Package</tt> objects defined by this
     *         <tt>ClassLoader</tt>
     * @since 1.2
     */
    protected Package[] getPackages() {
        return super.getPackages();
    }

    /**
     * Sets the desired assertion status for the named top-level class in this
     * class loader and any nested classes contained therein.  This setting
     * takes precedence over the class loader's default assertion status, and
     * over any applicable per-package default.  This method has no effect if
     * the named class has already been initialized.  (Once a class is
     * initialized, its assertion status cannot change.)
     * <p/>
     * <p> If the named class is not a top-level class, this invocation will
     * have no effect on the actual assertion status of any class, and its
     * return value is undefined.  </p>
     *
     * @param className The fully qualified class name of the top-level class whose
     *                  assertion status is to be set.
     * @param enabled   <tt>true</tt> if the named class is to have assertions
     *                  enabled when (and if) it is initialized, <tt>false</tt> if the
     *                  class is to have assertions disabled.
     * @since 1.4
     */
    public synchronized void setClassAssertionStatus(String className,
                                                     boolean enabled) {
        super.setClassAssertionStatus(className, enabled);
    }

    /**
     * Sets the package default assertion status for the named package.  The
     * package default assertion status determines the assertion status for
     * classes initialized in the future that belong to the named package or
     * any of its "subpackages".
     * <p/>
     * <p> A subpackage of a package named p is any package whose name begins
     * with "<tt>p.</tt>".  For example, <tt>javax.swing.text</tt> is a
     * subpackage of <tt>javax.swing</tt>, and both <tt>java.util</tt> and
     * <tt>java.lang.reflect</tt> are subpackages of <tt>java</tt>.
     * <p/>
     * <p> In the event that multiple package defaults apply to a given class,
     * the package default pertaining to the most specific package takes
     * precedence over the others.  For example, if <tt>javax.lang</tt> and
     * <tt>javax.lang.reflect</tt> both have package defaults associated with
     * them, the latter package default applies to classes in
     * <tt>javax.lang.reflect</tt>.
     * <p/>
     * <p> Package defaults take precedence over the class loader's default
     * assertion status, and may be overridden on a per-class basis by invoking
     * {@link #setClassAssertionStatus(String, boolean)}.  </p>
     *
     * @param packageName The name of the package whose package default assertion status
     *                    is to be set. A <tt>null</tt> value indicates the unnamed
     *                    package that is "current" (<a *
     *                    href="http://java.sun.com/docs/books/jls/">Java Language
     *                    Specification</a>, section 7.4.2).
     * @param enabled     <tt>true</tt> if classes loaded by this classloader and
     *                    belonging to the named package or any of its subpackages will
     *                    have assertions enabled by default, <tt>false</tt> if they will
     *                    have assertions disabled by default.
     * @since 1.4
     */
    public synchronized void setPackageAssertionStatus(String packageName,
                                                       boolean enabled) {
        super.setPackageAssertionStatus(packageName, enabled);
    }

    /**
     * Returns an input stream for reading the specified resource.
     * <p/>
     * <p> The search order is described in the documentation for {@link
     * #getResource(String)}.  </p>
     *
     * @param name The resource name
     * @return An input stream for reading the resource, or <tt>null</tt>
     *         if the resource could not be found
     * @since 1.1
     */
    public InputStream getResourceAsStream(String name) {
        return super.getResourceAsStream(name);
    }

    /**
     * Finds the specified class.  This method should be overridden by class
     * loader implementations that follow the delegation model for loading
     * classes, and will be invoked by the {@link #loadClass
     * <tt>loadClass</tt>} method after checking the parent class loader for
     * the requested class.  The default implementation throws a
     * <tt>ClassNotFoundException</tt>.  </p>
     *
     * @param name The name of the class
     * @return The resulting <tt>Class</tt> object
     * @throws ClassNotFoundException If the class could not be found
     * @since 1.2
     */
    protected Class findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    /**
     * Loads the class with the specified name.  This method searches for
     * classes in the same manner as the {@link #loadClass(String, boolean)}
     * method.  It is invoked by the Java virtual machine to resolve class
     * references.  Invoking this method is equivalent to invoking {@link
     * #loadClass(String, boolean) <tt>loadClass(name, false)</tt>}.  </p>
     *
     * @param name The name of the class
     * @return The resulting <tt>Class</tt> object
     * @throws ClassNotFoundException If the class was not found
     */
    public Class loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    /**
     * Loads the class with the specified name.  The default implementation
     * of this method searches for classes in the following order:
     * <p/>
     * <p><ol>
     * <p/>
     * <li><p> Invoke {@link #findLoadedClass(String)} to check if the class
     * has already been loaded.  </p></li>
     * <p/>
     * <li><p> Invoke the {@link #loadClass(String) <tt>loadClass</tt>} method
     * on the parent class loader.  If the parent is <tt>null</tt> the class
     * loader built-in to the virtual machine is used, instead.  </p></li>
     * <p/>
     * <li><p> Invoke the {@link #findClass(String)} method to find the
     * class.  </p></li>
     * <p/>
     * </ol>
     * <p/>
     * <p> If the class was found using the above steps, and the
     * <tt>resolve</tt> flag is true, this method will then invoke the {@link
     * #resolveClass(Class)} method on the resulting <tt>Class</tt> object.
     * <p/>
     * <p> Subclasses of <tt>ClassLoader</tt> are encouraged to override {@link
     * #findClass(String)}, rather than this method.  </p>
     *
     * @param name    The name of the class
     * @param resolve If <tt>true</tt> then resolve the class
     * @return The resulting <tt>Class</tt> object
     * @throws ClassNotFoundException If the class could not be found
     */
    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    /**
     * Returns a <tt>Package</tt> that has been defined by this class loader
     * or any of its ancestors.  </p>
     *
     * @param name The package name
     * @return The <tt>Package</tt> corresponding to the given name, or
     *         <tt>null</tt> if not found
     * @since 1.2
     */
    protected Package getPackage(String name) {
        return super.getPackage(name);
    }

    /**
     * Returns the absolute path name of a native library.  The VM invokes this
     * method to locate the native libraries that belong to classes loaded with
     * this class loader. If this method returns <tt>null</tt>, the VM
     * searches the library along the path specified as the
     * "<tt>java.library.path</tt>" property.  </p>
     *
     * @param libname The library name
     * @return The absolute path of the native library
     * @see System#loadLibrary(String)
     * @see System#mapLibraryName(String)
     * @since 1.2
     */
    protected String findLibrary(String libname) {
        return super.findLibrary(libname);
    }

    /**
     * Finds the resource with the given name. Class loader implementations
     * should override this method to specify where to find resources.  </p>
     *
     * @param name The resource name
     * @return A <tt>URL</tt> object for reading the resource, or
     *         <tt>null</tt> if the resource could not be found
     * @since 1.2
     */
    protected URL findResource(String name) {
        return super.findResource(name);
    }

    /**
     * Finds the resource with the given name.  A resource is some data
     * (images, audio, text, etc) that can be accessed by class code in a way
     * that is independent of the location of the code.
     * <p/>
     * <p> The name of a resource is a '<tt>/</tt>'-separated path name that
     * identifies the resource.
     * <p/>
     * <p> This method will first search the parent class loader for the
     * resource; if the parent is <tt>null</tt> the path of the class loader
     * built-in to the virtual machine is searched.  That failing, this method
     * will invoke {@link #findResource(String)} to find the resource.  </p>
     *
     * @param name The resource name
     * @return A <tt>URL</tt> object for reading the resource, or
     *         <tt>null</tt> if the resource could not be found or the invoker
     *         doesn't have adequate  privileges to get the resource.
     * @since 1.1
     */
    public URL getResource(String name) {
        return super.getResource(name);
    }

    /**
     * Returns an enumeration of {@link java.net.URL <tt>URL</tt>} objects
     * representing all the resources with the given name. Class loader
     * implementations should override this method to specify where to load
     * resources from.  </p>
     *
     * @param name The resource name
     * @return An enumeration of {@link java.net.URL <tt>URL</tt>} objects for
     *         the resources
     * @throws java.io.IOException If I/O errors occur
     * @since 1.2
     */
    protected Enumeration findResources(String name) throws IOException {
        return super.findResources(name);
    }

    /**
     * Defines a package by name in this <tt>ClassLoader</tt>.  This allows
     * class loaders to define the packages for their classes. Packages must
     * be created before the class is defined, and package names must be
     * unique within a class loader and cannot be redefined or changed once
     * created.  </p>
     *
     * @param name        The package name
     * @param specTitle   The specification title
     * @param specVersion The specification version
     * @param specVendor  The specification vendor
     * @param implTitle   The implementation title
     * @param implVersion The implementation version
     * @param implVendor  The implementation vendor
     * @param sealBase    If not <tt>null</tt>, then this package is sealed with
     *                    respect to the given code source {@link java.net.URL
     *                    <tt>URL</tt>}  object.  Otherwise, the package is not sealed.
     * @return The newly defined <tt>Package</tt> object
     * @throws IllegalArgumentException If package name duplicates an existing package either in this
     *                                  class loader or one of its ancestors
     * @since 1.2
     */
    protected Package definePackage(String name, String specTitle,
                                    String specVersion, String specVendor,
                                    String implTitle, String implVersion,
                                    String implVendor, URL sealBase) throws IllegalArgumentException {
        return super.definePackage(name, specTitle, specVersion, specVendor,
                implTitle, implVersion, implVendor, sealBase);
    }
}
