package org.smartfrog.services.groovy.install.task

import groovy.text.GStringTemplateEngine
import org.smartfrog.services.groovy.install.Component
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemManager
import org.apache.commons.vfs2.FileSystemOptions
import org.apache.commons.vfs2.FileType
import org.apache.commons.vfs2.Selectors
import org.apache.commons.vfs2.VFS
import org.smartfrog.sfcore.common.SmartFrogCoreKeys
import org.smartfrog.sfcore.common.SmartFrogExtractedException
import org.smartfrog.sfcore.logging.LogFactory
import org.smartfrog.sfcore.logging.LogSF
import org.smartfrog.sfcore.common.SmartFrogDeploymentException
import org.apache.commons.vfs2.provider.http.HttpFileSystemConfigBuilder
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder

/**
 * Provides access to worker functions which may be used in Source components and in task files.
 *
 */
class GroovyComponentHelper {

    private LogSF sfLog = LogFactory.sfGetProcessLog()

    private FileSystemManager manager
    private FileSystemOptions options
    private FileObject root

    private Component component

    public GroovyComponentHelper(Component comp) {
        def rootDir
        if (comp) {
            component = comp
            try {
                sfLog = LogFactory.getLog(component.sfResolve(SmartFrogCoreKeys.SF_APP_LOG_NAME, "Helper", true))
            } catch (Exception e) {
                sfLog.error(e.toString(),e)
                throw new SmartFrogExtractedException(SmartFrogExtractedException.convert(e))
            }
            rootDir = component.sfResolve("directory")
        } else {
            //no component, set the root dir to the current dir
            rootDir = System.getProperty("java.io.tmpdir")
        }
        manager = VFS.getManager()
        root = manager.resolveFile(rootDir.toString())

        options = new FileSystemOptions();
        propagateProxySettings();

    }


    public void propagateProxySettings() {
        String proxyHost = System.getProperty("http.proxyHost")
        String proxyPort = System.getProperty("http.proxyPort")
        if (proxyHost) {
            int port = Integer.parseInt(proxyPort);
            HttpFileSystemConfigBuilder.getInstance().setProxyHost(options, proxyHost)
            HttpFileSystemConfigBuilder.getInstance().setProxyPort(options, port)

            SftpFileSystemConfigBuilder.getInstance().setProxyHost(options, proxyHost)
            SftpFileSystemConfigBuilder.getInstance().setProxyPort(options, port)
            SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false)
            SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(options, "no")
        }
    }

    public Process command(String c) {
        // all commands are executed in root directory by default
        return command(c, root.getName().getPath())
    }

    public Process command(String c, String directory) {
        sfLog.debug("Executing command $c in directory $directory")
        try {
            if (directory) {
                return c.execute((String[]) null, new File(directory))
            } else {
                return c.execute()
            }
        } catch (Exception e) {
            sfLog.error(e.toString(), e)
            // We have to catch exceptions and throw our own.
            // Otherwise we get an Unmarshallexception (Script1)
            throw new SmartFrogExtractedException(SmartFrogExtractedException.convert(e))
        }
    }

    /**
     * Install an RPM
     * @param file RPM file to install
     * @return the RPM process
     */
    public Process rpm(String file) {
        return command("rpm -i $file")
    }

    /**
     * Extract a compressed tar file in a directory.
     * Although you can threat .tar files as directories and copy its contents using VFS
     * it is faster to just copy the .tar file in one piece and then extract it on the host machine.
     * Also, this avoids out of heap space errors!
     *
     * http://apache-commons.680414.n4.nabble.com/commons-vfs-copy-progress-aborting-copy-operation-td740573.html
     *
     * @param file file to unpack
     * @param directory the dest fir
     * @return the unpack process
     */
    public Process unpack(String file, String directory) {
        sfLog.debug("Unpacking $file")
        if (file.endsWith("gz")) {
            return command("tar zxf $file", directory)
        }
        else if (file.endsWith("bz2")) {
            return command("tar jxf $file", directory)
        }
        else {
            throw new SmartFrogDeploymentException("Unknown archive type: $file")
        }
    }

    /**
     * Unpack a file into the root dir
     * @param file file to unpack
     * @return the unpack process
     */
    public Process unpack(String file) {
        return unpack(file, root.getName().getPath())
    }

    /**
     * Resolve a file
     * @param location url or path
     * @return the resolved location, which is root-relative on a simple path
     */
    private FileObject resolve(location) {
        sfLog.debug("Resolving URL: $location")
        if (location.contains("http://") || location.contains("sftp://")) {
            assert !location.contains("***")
            return manager.resolveFile(location, options)
        } else {
            // resolve relative paths against root
            return manager.resolveFile(root, location)
        }
    }

    /**
     * Resolve then create a directory
     * @param directory the directory to create (root-relative)
     * @return true iff the directory was created. False means it already exists
     */
    public boolean mkdir(directory) {
        sfLog.debug("Creating $directory")
        FileObject dir = resolve(directory)
        if (dir.exists()) {
            sfLog.error("A file or directory with same name already exists.")
            return false;
        }
        dir.createFolder()
        dir.close()
        return true
    }

    /**
     * Resolve then delete a file or directory
     * @param directory the target path (root-relative)
     * @return true iff the path was deleted
     */
    public boolean delete(fileOrDirectory) {
        sfLog.debug("Deleting $fileOrDirectory")
        FileObject src = resolve(fileOrDirectory)
        if (!src.exists()) sfLog.warn("Cannot delete. The file or directory does not exist.")
        def delete = src.delete(Selectors.SELECT_ALL)
        src.close()
        return delete != 0
    }

    /**
     * Copy from the source to the destination, both are resolved first
     * @param source source URL or path
     * @param destination dest URL or path
     */
    public void copy(String source, String destination) {
        sfLog.debug("Copying $source to $destination")
        FileObject src = resolve(source);
        if (!src.exists()) {
            throw new SmartFrogDeploymentException("Source URL $source does not exist!")
        }
        if (!src.isReadable()) {
            throw new SmartFrogDeploymentException("Source URL $source cannot be read")
        }
        FileObject dest = resolve(destination);
        if (src.getType() == FileType.FILE) {
            if (dest.exists()) {
                if (dest.getType() == FileType.FILE) {
                    sfLog.warn("Destination file does already exist and will be overwritten!")
                    dest.copyFrom(src, Selectors.SELECT_SELF)
                } else if (dest.getType() == FileType.FOLDER) {
                    sfLog.info("Copying file $source into directory $destination")
                    dest = resolve("$destination/${src.getName().getBaseName()}")
                    dest.copyFrom(src, Selectors.SELECT_SELF)
                }
            } else {
                sfLog.info("Copying file $source to $destination")
                dest.copyFrom(src, Selectors.SELECT_SELF)
            }
        } else if (src.getType() == FileType.FOLDER) {
            if (dest.exists()) {
                if (dest.getType() == FileType.FILE) {
                    throw new SmartFrogDeploymentException("Cannot copy directory $source into file $destination")
                } else {
                    sfLog.info("Copying directory contents of $source into directory $destination")
                    dest.copyFrom(src, Selectors.EXCLUDE_SELF)
                }
            } else {
                sfLog.info("Directory $destination does not exist and will be created")
                dest.copyFrom(src, Selectors.EXCLUDE_SELF)
            }
        }
        src.close()
        dest.close()
    }

    /**
     * Copy from the source to the destination, both are resolved first
     * @param source source URL or path
     * @param destination dest URL or path
     */
    public void copyDir(String source, String destination) {
        
    }

    /**
     * Move from the source to the destination, both are resolved first
     * @param source source URL or path
     * @param destination dest URL or path
     */
    public void move(String source, String destination) {
        sfLog.debug("Moving $source to $destination")
        FileObject src = resolve(source);
        if (!src.exists()) {
            sfLog.error("Source URL does not exist!")
            return
        }
        if (!src.isReadable()) {
            sfLog.error("Source URL cannot be read!")
            return
        }
        FileObject dest = resolve(destination);
        if (src.getType() == FileType.FILE) {
            if (dest.exists()) {
                if (dest.getType() == FileType.FILE) {
                    sfLog.warn("Destination file does already exist and will be overwritten!")
                    src.moveTo(dest)
                } else if (dest.getType() == FileType.FOLDER) {
                    sfLog.info("Moving file $source into directory $destination")
                    dest = resolve("$destination/${src.getName().getBaseName()}")
                    src.moveTo(dest)
                }
            } else {
                sfLog.info("Moving file $source to $destination")
                src.moveTo(dest)
            }
        } else if (src.getType() == FileType.FOLDER) {
            if (dest.exists()) {
                if (dest.getType() == FileType.FILE) {
                    throw new SmartFrogDeploymentException("Cannot move directory $source into file $destination")
                } else {
                    sfLog.info("Moving directory contents of $source into directory $destination")
                    src.getChildren().each { srcChild ->
                        FileObject destChild = resolve("$destination/${srcChild.getName().getBaseName()}")
                        srcChild.moveTo(destChild)
                        destChild.close()
                    }
                }
            } else {
                sfLog.info("Directory $destination does not exist and will be created")
                src.moveTo(dest)
            }
        }
        src.close()
        dest.close()
    }

    /**
     * Replaces Groovy code within a config file using GStringTemplateEngine.
     * @param file - file
     * @param binding - variables refered to in the file
     */
    public void parse(String file, binding) {
        def engine = new GStringTemplateEngine()
        def input = new File(file);
        if (!input.exists()) {
            sfLog.error("File $file does not exist!")
            return
        }
        try {
            def template = engine.createTemplate(input.text).make(binding)
            sfLog.debug("Created template")
            def replacedText = template.toString()
            sfLog.debug("Evaluated template as: $replacedText")
            input.write(replacedText)
        } catch (e) {
            sfLog.error(e.toString(), e)
            throw new SmartFrogExtractedException(SmartFrogExtractedException.convert(e))
        }
    }

    /**
     * Replaces Groovy code within a config file using GStringTemplateEngine.
     * Makes component available inside the file as "comp".
     * @see this.parse ( file , binding )
     */
    public void parse(file) {
        def binding = [
                comp: component
        ]
        parse(file, binding)
    }
}