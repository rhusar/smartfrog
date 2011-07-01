package org.smartfrog.services.groovy.install.task

import groovy.text.GStringTemplateEngine
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemManager
import org.apache.commons.vfs2.FileSystemOptions
import org.apache.commons.vfs2.FileType
import org.apache.commons.vfs2.Selectors
import org.apache.commons.vfs2.VFS
import org.apache.commons.vfs2.provider.http.HttpFileSystemConfigBuilder
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder
import org.smartfrog.services.groovy.install.Component
import org.smartfrog.services.groovy.install.IComponent
import org.smartfrog.sfcore.common.SmartFrogCoreKeys
import org.smartfrog.sfcore.common.SmartFrogDeploymentException
import org.smartfrog.sfcore.common.SmartFrogExtractedException
import org.smartfrog.sfcore.logging.LogFactory
import org.smartfrog.sfcore.logging.LogSF

/**
 * Provides access to worker functions which may be used in Source components and in task files.
 *
 */
class GroovyComponentHelper {

    public static final String DEFAULT_LOG_NAME = "GroovyComponentHelper"
    private LogSF sfLog = LogFactory.sfGetProcessLog()

    private FileSystemManager manager
    private FileSystemOptions options
    private FileObject destDir
    private File destFile
    private FileObject scriptDir
    private IComponent component
    int execTimeout = 60000

    public GroovyComponentHelper(IComponent comp) {
        component = comp
        try {
            sfLog = LogFactory.getLog(component.sfResolve(SmartFrogCoreKeys.SF_APP_LOG_NAME, DEFAULT_LOG_NAME, true))
        } catch (Exception e) {
            sfLog.error(e.toString(), e)
            throw new SmartFrogExtractedException(SmartFrogExtractedException.convert(e))
        }
        String rootDir = resolvePath(Component.ATTR_DEST_DIR, true);
        String scriptDir = resolvePath(Component.ATTR_SCRIPT_DIR, true);
        execTimeout = component.sfResolve(Component.ATTR_EXEC_TIMEOUT, execTimeout, false)

        bind(scriptDir, rootDir);
    }

    /**
     * for use in testing: create a component helper bound to a specific directory
     * @param destDir the destination dir
     */
    public GroovyComponentHelper(String scriptDir, String destDir) {
        bind(scriptDir, destDir);
    }

    private def bind(String scriptDir, String destDir) {
        manager = VFS.getManager()
        this.destDir = manager.resolveFile(destDir)
        this.destFile = new File(destDir)
        this.scriptDir = manager.resolveFile(scriptDir)
        options = new FileSystemOptions();
        propagateProxySettings()
    }



    public File resolveFile(String name, boolean mandatory) {
        return org.smartfrog.services.filesystem.FileSystem.
                lookupAbsoluteFile(component,
                        name,
                        null,
                        null,
                        mandatory,
                        null);
    }

    public String resolvePath(String name, boolean mandatory) {
        return org.smartfrog.services.filesystem.FileSystem.
                lookupAbsolutePath(component,
                        name,
                        null,
                        null,
                        mandatory,
                        null);
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

    public Process command(String command) {
        // all commands are executed in root directory by default
        return this.command(command, destDir.name.path)
    }

    public Process command(String cmd, String directory) {
        if (sfLog.debugEnabled) sfLog.debug("Executing command $cmd in directory $directory")
        File execDir = directory ? new File(directory) : null;
        return command(cmd, execDir)
    }

    public Process command(String cmd, File directory) {
        if (sfLog.debugEnabled) sfLog.debug("Executing command $cmd in directory $directory")
        sfLog.info("$directory> $cmd")
        try {
            return cmd.execute((String[]) null, directory)
        } catch (Exception e) {
            //sfLog.error("Executing ${command}: ${e}", e)
            // We have to catch exceptions and throw our own.
            // Otherwise we get an Unmarshallexception (Script1)
            throw new SmartFrogExtractedException("Executing ${cmd}: ${e}",
                    SmartFrogExtractedException.convert(e))
        }
    }

    /**
     * A blocking exec
     * @param cmd the command to execute
     * @param directory directory -can be null
     * @param timeout the timeout
     * @return
     */
    public int exec(String cmd, File directory, long timeout) {
        Process ps = command(cmd, directory)
        ps.waitForOrKill(timeout)
        return ps.exitValue()
    }

    /**
     * A blocking exec
     * @param cmd the command to execute
     * @param directory directory -can be null
     * @param timeout the timeout
     * @return
     */
    public int exec(String cmd) {
        return exec(cmd, this.destFile, execTimeout)
    }

    /**
     * A blocking exec
     * @param cmd the command to execute
     * @param directory directory -can be null
     * @param timeout the timeout
     * @return
     */
    public int exec(String cmd, String dir) {
        return exec(cmd, new File(dir), execTimeout)
    }

    /**
     * Install an RPM
     * @param file RPM file to install
     * @return the RPM process
     */
    public Process rpm(String file) {
        FileObject src = resolve(file);
        verifySourceIsValid("rpm", src)
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
     * @param source file to unpack
     * @param targetDir the dest dir
     * @return the unpack process
     */
    public int unpack(String source, String targetDir) {
        sfLog.debug("Unpacking $source")
        FileObject src = resolve(source);
        verifySourceIsValid("unpack", src)
        if (source.endsWith("gz")) {
            return exec("tar x -f $source", targetDir)
        }
        else if (source.endsWith("bz2")) {
            return exec("tar x -f $source", targetDir)
        }
        else {
            throw new SmartFrogDeploymentException("Unknown archive type: $source")
        }
    }

    /**
     * Unpack a file into the root dir
     * @param file file to unpack
     * @return the unpack process
     */
    public int unpack(String file) {
        return unpack(file, destDir.getName().getPath())
    }

    /**
     * Resolve a file
     * @param location url or path
     * @return the resolved location, which is root-relative on a simple path
     */
    private FileObject resolve(String location) {
        sfLog.debug("Resolving URL: $location")
        if (location.contains("http://") || location.contains("sftp://")) {
            assert !location.contains("***")
            return manager.resolveFile(location, options)
        } else {
            // resolve relative paths against root
            return manager.resolveFile(destDir, location)
        }
    }

    /**
     * Resolve a file
     * @param location url or path
     * @return the resolved location, which is root-relative on a simple path
     */
    private FileObject resolveSrc(String location) {
        sfLog.debug("Resolving URL: $location")
        if (location.contains("http://") || location.contains("sftp://")) {
            assert !location.contains("***")
            return manager.resolveFile(location, options)
        } else {
            // resolve relative paths against root
            return manager.resolveFile(scriptDir, location)
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
            sfLog.debug("A file or directory with the name $directory already exists.")
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
        if (!src.exists()) sfLog.debug("Cannot delete $fileOrDirectory: it does not exist.")
        def delete = src.delete(Selectors.SELECT_ALL)
        src.close()
        return delete != 0
    }

    /**
     * Copy from the source to the destination, both are resolved first
     * @param source source URL or path
     * @param destination dest URL or path
     */
    public String copy(String source, String destination) {
        sfLog.debug("Copying $source to $destination")
        FileObject src = resolveSrc(source);
        verifySourceIsValid("copy", src)
        FileObject dest = resolve(destination);
        try {
            FileType sourceType = src.type
            if (sourceType == FileType.FILE) {
                boolean destExists = dest.exists()
                if (destExists) {
                    if (dest.type == FileType.FILE) {
                        if (sfLog.debugEnabled) sfLog.debug("Destination file $dest will be overwritten")
                        dest.copyFrom(src, org.apache.commons.vfs2.Selectors.SELECT_SELF)
                    } else if (dest.type == FileType.FOLDER) {
                        if (sfLog.debugEnabled) sfLog.debug("Copying file $source into directory $destination")
                        dest = resolve("$destination/${src.name.baseName}")
                        dest.copyFrom(src, org.apache.commons.vfs2.Selectors.SELECT_SELF)
                    }
                } else {
                    if (sfLog.debugEnabled) sfLog.debug("Copying file $source to $destination")
                    dest.copyFrom(src, org.apache.commons.vfs2.Selectors.SELECT_SELF)
                }
            } else if (sourceType == FileType.FOLDER) {
                if (dest.exists()) {
                    if (dest.type == FileType.FILE) {
                        throw new SmartFrogDeploymentException("Cannot copy directory $source into file $destination")
                    } else {
                        if (sfLog.debugEnabled) sfLog.debug("Copying directory contents of $source into directory $destination")
                        dest.copyFrom(src, org.apache.commons.vfs2.Selectors.EXCLUDE_SELF)
                    }
                } else {
                    if (sfLog.debugEnabled) sfLog.debug("Directory $destination does not exist and will be created")
                    dest.copyFrom(src, org.apache.commons.vfs2.Selectors.EXCLUDE_SELF)
                }
            }
        } finally {
            src.close()
            dest.close()
        }
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
        verifySourceIsValid("move", src)
        FileObject dest = resolve(destination);
        if (src.getType() == FileType.FILE) {
            if (dest.exists()) {
                if (dest.getType() == FileType.FILE) {
                    sfLog.warn("Destination file $dest already exists and will be overwritten!")
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

    protected def verifySourceIsValid(String operation, FileObject src) {
        if (!src.exists()) {
            throw new SmartFrogDeploymentException(operation + ": source \"$src\" - does not exist");
        }
        if (!src.isReadable()) {
            throw new SmartFrogDeploymentException(operation + ": Source \"$src\" cannot be read")
        }
    }

    /**
     * Replaces Groovy code within a config file using GStringTemplateEngine.
     * @param file - file
     * @param binding - variables refered to in the file
     */
    public void parse(String file, String destFile, Map binding) {
        def engine = new GStringTemplateEngine()
        def input = new File(file)
        def output = new File(destFile)
        if (!input.exists()) {
            sfLog.error("File $file does not exist!")
            return
        }
        try {
            def template = engine.createTemplate(input.text).make(binding)
            sfLog.debug("Created template")
            def replacedText = template.toString()
            sfLog.debug("Evaluated template as: $replacedText")
            output.write(replacedText)
        } catch (e) {
            def text = "Failed to create template from $file into $destFile: $e"
            sfLog.error(text, e)
            throw new SmartFrogExtractedException(text, SmartFrogExtractedException.convert(e))
        }
    }

    /**
     * Replaces Groovy code within a config file using GStringTemplateEngine.
     * Makes component available inside the file as "comp".
     * @see this.parse ( file , binding )
     */
    public void parse(String file) {
        Map<String, Object> binding = createParseBinding()
        parse(file, file, binding)
    }

    /**
     * Copy a file with parsing to a destination directory
     * @param source source file
     * @param destFile destination file
     */
    public void copyAndParseTo(String source, String dest) {
        FileObject src = resolveSrc(source);
        File destFile = new File(dest)
        if (!destFile.parentFile.exists()) {
            destFile.parentFile.mkdirs()
        }
        parse(src.name.pathDecoded, destFile.getAbsolutePath(), createParseBinding())
    }


    protected Map<String, Object> createParseBinding() {
        Map binding = [
                comp: component,
                destDir: component.getDestDir()
        ]
        return binding
    }

    public boolean touch(String filename) {
        File file = new File(filename)
        return file.createNewFile();
    }

    public boolean touch(File dir, String filename) {
        File file = new File(dir, filename)
        return file.createNewFile();
    }

    public boolean touch(String dir, String filename) {
        File file = new File(dir, filename)
        return file.createNewFile();
    }


    public void fail(String text) {
        throw new SmartFrogDeploymentException(text);
    }

    public void failIf(def value, String text) {
        if (value) fail(text);
    }
}