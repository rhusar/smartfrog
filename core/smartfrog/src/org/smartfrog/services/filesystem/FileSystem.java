/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.PlatformHelper;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.io.Reader;
import java.io.File;
import java.rmi.RemoteException;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileOutputStream;

/**
 * Filesystem operations
 */

public class FileSystem {

    /**
     * Error text when a looked up reference resolves to something that is not
     * yet deployed. {@value}
     */
    public static final String ERROR_UNDEPLOYED_CD = "This attribute resolves" +
            "to a not-yet-deployed component: ";
    public static final String ERROR_INACCESSIBLE_FILE =
            "Error! File is not accessible : ";
    public static final String ERROR_FILE_IS_A_DIRECTORY =
            "Error! File is a directory : ";

    // helper class only
    private FileSystem() {
    }

    /**
     * Close a stream; do nothing if null. Ignore all thrown IOExceptions
     *
     * @param stream
     */
    public static void close(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                //ignored
            }
        }
    }

    /**
     * Close a stream; do nothing if null. Ignore all thrown IOExceptions
     *
     * @param stream
     */
    public static void close(OutputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                //ignored
            }
        }
    }

    /**
     * Close a stream; do nothing if null. Ignore all thrown IOExceptions
     *
     * @param channel
     */
    public static void close(Writer channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                //ignored
            }
        }
    }

    /**
     * Close a reader; do nothing if null. Ignore all thrown IOExceptions
     *
     * @param channel
     */
    public static void close(Reader channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                //ignored
            }
        }
    }

    /**
     * Close a channel; do nothing if null. Ignore all thrown IOExceptions
     *
     * @param channel
     */
    public static void close(FileChannel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                //ignored
            }
        }
    }

    /**
     * Create a temporary file. There is a very small, very very small, race condition here
     * as we delete the temp file and recreate it as a dir. This may also be a security risk in
     * the right hands.
     * @param prefix prefix
     * @param suffix suffix -include the . for a .ext style suffix
     * @param dir parent dir; use null for java.io.tmpdir
     * @return
     * @throws IOException
     */
    public static File tempDir(String prefix,String suffix,File dir) throws IOException {
        File file=File.createTempFile(prefix,suffix,dir);
        file.delete();
        file.mkdir();
        return file;
    }

    /**
     * recursive directory deletion. If handed a file, will delete that.
     * @param dir directory
     */
    public static void recursiveDelete(File dir) {
        if(dir==null || dir.exists()) {
            //no-op
            return;
        }
        if(dir.isDirectory()) {
            File[] files = dir.listFiles();
            for(int i=0;i<files.length;i++) {
                recursiveDelete(files[i]);
            }
        }
        dir.delete();
    }


    /**
     * This static call is a helper for any component that wants to get either
     * an absolute path or a FileIntf binding to an attribute. The attribute is
     * looked up on a component. If it is bound to anything that implements
     * FileIntf, then that component is asked for an absolute path. if it is
     * bound to a string, then the string is turned into an absolute path,
     * relative to any directory named, after the string is converted into
     * platform appropriate forward/back slashes.
     *
     * @param component component to look up the path from
     * @param attribute the name of the attribute to look up
     * @param defval    a default value. This should already be in the local
     *                  format for the target platform, and absolute. Can be
     *                  null. No used when mandatory is true
     * @param baseDir   optional base directory for a relative file when
     *                  constructing from a string
     * @param mandatory flag that triggers the throwing of a SmartFrogResolutionException
     *                  when things go wrong
     * @param platform  a platform to use for converting filetypes. Set to null
     *                  to use the default helper for this platform.
     * @return the absolute path
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    public static String lookupAbsolutePath(Prim component,
                                            Reference attribute,
                                            String defval,
                                            File baseDir,
                                            boolean mandatory,
                                            PlatformHelper platform)
            throws SmartFrogResolutionException, RemoteException {
        Object pathAttr = component.sfResolve(attribute, mandatory);
        if (pathAttr == null) {
            //mandatory must be false, because we did not get a value.
            return defval;
        }
        if (pathAttr instanceof FileIntf) {
            //file interface: get the info direct from the component
            //FileIntf fileComponent = (FileIntf) pathAttr;
            //String path = fileComponent.getAbsolutePath();
            Prim fileAsPrim = (Prim) pathAttr;
            String path =
                    fileAsPrim.sfResolve(FileUsingComponent.ATTR_ABSOLUTE_PATH,
                            (String) null,
                            true);
            return path;
        }
        if (pathAttr instanceof String) {
            //string: convert that into an absolute path
            //without any directory info. so its relative to "here"
            //wherever "here" is for the process
            String filename = (String) pathAttr;
            if (platform == null) {
                platform = PlatformHelper.getLocalPlatform();
            }
            filename = platform.convertFilename(filename);
            File newfile;
            //create a file from the string
            if (baseDir != null) {
                newfile = new File(baseDir, filename);
            } else {
                newfile = new File(filename);
            }
            String path = newfile.getAbsolutePath();
            return path;
        }
        //something else.

        //at this point the type is not supported. So
        //we have to advise the caller that they have an illegal type.

        Reference owner;
        owner = ComponentHelper.completeNameSafe(component);
        if (pathAttr instanceof ComponentDescription) {
            ComponentDescription cd = (ComponentDescription) pathAttr;
            throw new SmartFrogResolutionException(ERROR_UNDEPLOYED_CD + cd);
        }

        throw new SmartFrogResolutionException(attribute, owner,
                MessageUtil.formatMessage(SmartFrogResolutionException.MSG_ILLEGAL_CLASS_TYPE)
                        +
                        " : " +
                        pathAttr.getClass().toString()
                        + " - " + pathAttr);
    }

    /**
     * This static call is a helper for any component that wants to get either
     * an absolute path or a FileIntf binding to an attribute. The attribute is
     * looked up on a component. If it is bound to anything that implements
     * FileIntf, then that component is asked for an absolute path. if it is
     * bound to a string, then the string is turned into an absolute path,
     * relative to any directory named, after the string is converted into
     * platform appropriate forward/back slashes.
     *
     * @param component component to look up the path from
     * @param attribute the name of the attribute to look up
     * @param defval    a default value. This should already be in the local
     *                  format for the target platform, and absolute. Can be
     *                  null. Not used when mandatory is true
     * @param baseDir   optional base directory for a relative file when
     *                  constructing from a string
     * @param mandatory flag that triggers the throwing of a SmartFrogResolutionException
     *                  when things go wrong
     * @param platform  a platform to use for converting filetypes. Set to null
     *                  to use the default helper for this platform.
     * @return the resolved absolute path
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    public static String lookupAbsolutePath(Prim component,
                                            String attribute,
                                            String defval,
                                            File baseDir,
                                            boolean mandatory,
                                            PlatformHelper platform)
            throws SmartFrogResolutionException, RemoteException {
        return lookupAbsolutePath(component,
                new Reference(attribute),
                defval,
                baseDir,
                mandatory,
                platform);
    }

    /**
     * Look up the absolutePath attribute of any component, then turn it into a
     * file.
     *
     * @param component component to resolve against
     * @return file representing the path.
     * @throws SmartFrogResolutionException If the attribute is not defined.
     * @throws RemoteException
     */
    public static File resolveAbsolutePath(Prim component)
            throws SmartFrogResolutionException,
            RemoteException {
        String absolutePath =
                component.sfResolve(FileUsingComponent.ATTR_ABSOLUTE_PATH,
                        "",
                        true);
        File file = new File(absolutePath);
        return file;
    }

    /**
     * Look up the absolutePath attribute of any FileUsingComponent, then turn
     * it into a file. Note that the RPC method is not used; only sf attributes.
     * Thus the coupling is much looser.
     *
     * @param component component to resolve against
     * @return file representing the path.
     * @throws SmartFrogResolutionException If the attribute is not defined.
     * @throws RemoteException
     */
    private static File resolveAbsolutePath(FileUsingComponent component)
            throws SmartFrogResolutionException, RemoteException {
        return resolveAbsolutePath((Prim) component);
    }

    // Contributed by Sanjay Dahiya

    public static final int BUF_SIZE = 50000;
    private static byte[] BUF = new byte[BUF_SIZE];

    /**
     * Copies a <code>File</code> to a <code>File</code>
     *
     * @param src  File
     * @param dest File
     * @throws IOException for IO Trouble
     * @throws SmartFrogResolutionException if the source or dest doesnt have a filename
     */
    public static void fCopy(Prim src, Prim dest)
            throws IOException,
            SmartFrogResolutionException {

        File sourceFile= resolveAbsolutePath(src);
        File destFile = resolveAbsolutePath(dest);
        fCopy(sourceFile, destFile);
    }
    /**
     * Copies a <code>File</code> to a <code>File</code>
     *
     * @param src  File
     * @param dest File
     * @throws IOException
     */
    public static void fCopy(File src, File dest) throws IOException {
        if(src.equals(dest)) {
            return;
        }
        fCopy(new FileInputStream(src), dest);
    }

    /**
     * Copies an <code>FileInputStream</code> to a <code>File</code>
     *
     * @param src  FileInputStream
     * @param dest File
     * @throws IOException
     */
    public static void fCopy(FileInputStream src, File dest)
            throws IOException {
        FileChannel srcChannel = null;
        FileChannel dstChannel = null;
        try {
            if (null == src) {
                throw new IOException("No source stream");
            }
            if (null == dest) {
                throw new IOException("No dest file");
            }

            srcChannel = src.getChannel();
            dstChannel = new FileOutputStream(dest).getChannel();
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
        } finally {
            // Close the streams
            close(srcChannel);
            close(dstChannel);
            close(src);
        }

    }

    /**
     * Copies an <code>InputStream</code> to a file All streams are closed
     * afterwards.
     *
     * @param in         stream to copy from
     * @param outputFile file to copy to
     * @return the number of bytes copied
     * @throws IOException if an I/O error occurs (may result in partially done
     *                     work)
     */
    public static long fCopy(InputStream in, File outputFile) throws
            IOException {
        FileOutputStream out = null;
        out = new FileOutputStream(outputFile);
        return fCopy(in, out);
    }

    /**
     * Copies an <code>InputStream</code> to an <code>OutputStream</ code> using
     * a local internal buffer for performance. Compared to {@link
     * #globalBufferCopy(InputStream, OutputStream)} this method allows for
     * better concurrency, but each time it is called generates a buffer which
     * will be garbage.
     * <p/>
     * All streams are closed afterwards.
     *
     * @param in  stream to copy from
     * @param out stream to copy to
     * @return the number of bytes copied
     * @throws IOException if an I/O error occurs (may result in partially done
     *                     work)
     * @see #globalBufferCopy(InputStream, OutputStream)
     */
    public static long fCopy(InputStream in, OutputStream out) throws
            IOException {
        // we need a buffer of our own, so no one else interferes
        byte[] buf = new byte[BUF_SIZE];
        return copy(in, out, buf);
    }

    /**
     * Copies an <code>InputStream</code> to an <code>OutputStream</ code> using
     * a global internal buffer for performance. Compared to {@link
     * #fCopy(InputStream, OutputStream)} this method generated no garbage, but
     * decreases concurrency.
     *
     * All streams are closed afterwards.
     * @param in  stream to copy from
     * @param out stream to copy to
     * @return the number of bytes copied
     * @throws IOException if an I/O error occurs (may result in partially done
     *                     work)
     * @see #fCopy(InputStream, OutputStream)
     */
    public static long globalBufferCopy(InputStream in, OutputStream out) throws
            IOException {
        synchronized (BUF) {
            return copy(in, out, BUF);
        }
    }

    /**
     * Copies an <code>InputStream</code> to an <code>OutputStream</ code> using
     * the specified buffer.
     *
     * All streams are closed afterwards.
     * @param in         stream to copy from
     * @param out        stream to copy to
     * @param copyBuffer buffer used for copying
     * @return the number of bytes copied
     * @throws IOException if an I/O error occurs (may result in partially done
     *                     work)
     * @see #globalBufferCopy(InputStream, OutputStream)
     * @see #fCopy(InputStream, OutputStream)
     */
    public static long copy(InputStream in, OutputStream out, byte[] copyBuffer)
            throws
            IOException {
        long bytesCopied = 0;
        int read = -1;
        try {

            while ((read = in.read(copyBuffer, 0, copyBuffer.length)) != -1) {
                out.write(copyBuffer, 0, read);
                bytesCopied += read;
            }
            return bytesCopied;
        } finally {
            close(out);
            close(in);
        }
    }

    /**
     * Read a file fully into a string buffer.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static StringBuffer readFile(File file) throws IOException {
        StringBuffer buf = new StringBuffer();

        if (null == file) {
            return null;
        }

        if (!file.exists() || !file.canRead()) {
            throw new IOException(ERROR_INACCESSIBLE_FILE + file);
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String str = null;
            while (null != (str = reader.readLine())) {
                buf.append(str);
                buf.append("\n");
            }
            return buf;
        } finally {
            close(reader);
        }
    }

    /**
     * Reads last (numLines) lines from end of a file.
     *
     * @param file
     * @param numLines
     * @return
     * @throws IOException
     */
    public static StringBuffer tail(File file, int numLines)
            throws IOException {
        StringBuffer buf = new StringBuffer();
        String[] strings = new String[numLines];

        if (null == file) {
            return null;
        }
        //  dont even open the file if no lines need to be read.
        //  following algo doesnt work for zero lines.
        if (0 == numLines) {
            return buf;
        }

        if (!file.exists() || !file.canRead()) {
            throw new IOException(ERROR_INACCESSIBLE_FILE + file);
        }
        if (file.isDirectory()) {
            throw new IOException(ERROR_FILE_IS_A_DIRECTORY + file);
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String str = null;
            int totalLines = 0;
            for (int lineCtr = 0;
                 null != (str = reader.readLine());
                 totalLines++) {

                // first check if end of array has reached.
                // makes lines to rotate in array,
                if (lineCtr == strings.length) {
                    lineCtr = 0;
                }
                // this wont work if strings.length == 0, but that ok since we already checked
                // for that.
                strings[lineCtr++] = str + "\n";
            }

            // this is where we need to start reading the (circular) array from
            int firstLineIndex = (totalLines < strings.length) ? 0 :
                    totalLines % strings.length;
            // this is where we stop, useful if file is smaller than number of lines.
            int endMarker =
                    (totalLines < strings.length) ? totalLines : strings.length;

            for (int i = 0, ctr = firstLineIndex; i < endMarker; i++) {
                buf.append(strings[ctr++]);
                if (ctr == strings.length) {
                    ctr = 0;
                }
            }
            return buf;
        } finally {
            close(reader);
        }
    }

    /**
     * Get the tail of a file
     *
     * @param filepath
     * @param numLines
     * @return
     * @throws IOException
     */
    public static StringBuffer tail(String filepath, int numLines) throws
            IOException {
        return tail(new File(filepath), numLines);
    }
//  End Contributed by Sanjay Dahiya

    /**
     * Create a temp file in the directory names, with the given prefix and suffix.
     * @param prefix prefix -required.
     * @param suffix suffix, e,g. ".ext";
     * @param dir parent dir; can be null
     * @return the directory.
     * @throws org.smartfrog.sfcore.common.SmartFrogException a wrapper for any IOException.
     */
    public static File createTempFile(String prefix, String suffix, String dir) throws SmartFrogException {
        File file;
        try {
            if (dir == null) {
                file = File.createTempFile(prefix, suffix);
            } else {
                File directory = new File(dir);
                directory.mkdirs();
                file = File.createTempFile(prefix, suffix, directory);
            }
        } catch (IOException e) {
            throw SmartFrogException.forward(e);
        }
        return file;
    }

    /**
     * Create a temp directory in the directory named, with the given prefix and suffix.
     * This is done by creating a temp file, deleting it and creating a dir of the same name.
     * There is a fractional moment of race condition there, where bad things could happen.
     * @param dir    parent dir; can be null
     * @param prefix prefix -required.
     * @param suffix suffix, e,g. ".ext";
     * @return the directory.
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *          a wrapper for any IOException.
     */
    public static File createTempDir(String dir, String prefix, String suffix)
            throws SmartFrogException {
        File file=createTempFile(prefix, suffix, dir);
        file.delete();
        file.mkdir();
        return file;
    }

}
