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

/**
 * created 05-Apr-2005 14:28:15
 */

public class FileSystem {

    /**
     * Error text when a looked up reference resolves to something
     * that is not yet deployed.
     * {@value}
     */
    public static final String ERROR_UNDEPLOYED_CD = "This attribute resolves" +
                        "to a not-yet-deployed component: ";

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

            }
        }
    }

    /**
     * Close a stream; do nothing if null. Ignore all thrown IOExceptions
     *
     * @param channel
     */
    public static void close(Reader channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {

            }
        }
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
            String path = fileAsPrim.sfResolve(FileUsingComponent.ATTR_ABSOLUTE_PATH,
                    (String)null,
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
            throw new SmartFrogResolutionException(ERROR_UNDEPLOYED_CD+cd);
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
        String absolutePath = component.sfResolve(FileUsingComponent.ATTR_ABSOLUTE_PATH, "", true);
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
    public static File resolveAbsolutePath(FileUsingComponent component)
            throws SmartFrogResolutionException, RemoteException {
        return resolveAbsolutePath((Prim) component);
    }
}
