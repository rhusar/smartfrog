package org.smartfrog.services.os.java;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * This is the base class, with a helper method to set the attributes
 */
public abstract class AbstractClasspathImpl extends PrimImpl implements Classpath {

    protected AbstractClasspathImpl() throws RemoteException {
    }


    /**
     * something that is called in the {@link #sfStart()} method to start the termination
     * process. It can be overridden, or called from an overridden sfStart method.
     */
    protected void maybeStartTerminator() {
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null, "classpath terminating", null, null);
    }

    /**
     * set the classpath up
     * @param files a List of type File.
     * @throws SmartFrogRuntimeException error in setting classpath
     * @throws RemoteException in case of rmi/network error
     */
    protected void setClasspathAttributes(List files) throws SmartFrogException,
            RemoteException {
        try {
            int size = files.size();
            Vector paths=new Vector(size);
            Vector uris= new Vector(size);
            StringBuffer pathbuffer=new StringBuffer();
            StringBuffer uribuffer = new StringBuffer();
            for(int i=0;i<size;i++) {
                File file=(File) files.get(i);
                String filepath=file.getAbsolutePath();

                paths.add(filepath);
                pathbuffer.append(filepath);
                pathbuffer.append(File.pathSeparatorChar);

                String uri=file.toURI().toURL().toString();
                uris.add(uri);
                uribuffer.append(uri);
                uribuffer.append(' ');
            }
            sfReplaceAttribute(ATTR_CLASSPATH_FILENAME_STRING,pathbuffer.toString());
            sfReplaceAttribute(ATTR_CLASSPATH_URI_STRING,
                    uribuffer.toString());
            sfReplaceAttribute(ATTR_CLASSPATH_STRING_LIST,
                    paths);
            sfReplaceAttribute(ATTR_CLASSPATH_URI_LIST,
                    uris);
        } catch (MalformedURLException e) {
            throw SmartFrogException.forward(e);
        }
    }

    /**
     * set the classpath up
     * @param files an array of files
     * @throws SmartFrogRuntimeException error in setting classpath
     * @throws RemoteException in case of rmi/network error
     */
    protected void setClasspathAttributes(File[] files) throws SmartFrogException,
            RemoteException {
        int size = files.length;
        List filelist=new ArrayList(size);
        for(int i=0;i<size;i++) {
            filelist.add(files[i]);
        }
        setClasspathAttributes(filelist);
    }

}
