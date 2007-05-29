/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.nbm;
import java.beans.PropertyVetoException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.JarFileSystem;

public class SmartFrogSvcUtil {
    
    private static String sfHome=null;
    private static String sfUserHome=null;
    private static String sfCp=null;
    private static int sfQuietTime = 1;
    
    private static ArrayList<URL> furls = null;
    private static ArrayList<URL> jarUrls = null;
    
    static {
        rebuildInfo();
    }
    
    /** Creates a new instance of SmartFrogSvcUtil */
    public SmartFrogSvcUtil() {
    }
    
    public static String getSFHome() {
        return sfHome;
    }
    
    public static String getSFUserHome() {
        return sfUserHome;
    }
    
    public static boolean getSFRestrictToIncludes() {
        return SmartfrogsvcAdvancedOption.getRestrictInclude();
    }
    
    public static String getSFClassPath() {
        return sfCp;
    }
    
    public static int getSFQuietTime() {
        return sfQuietTime;
    }
    
    protected static void rebuildInfo() {
        sfHome = SmartfrogsvcAdvancedOption.getSFHome();
        sfUserHome = SmartfrogsvcAdvancedOption.getSFUserHome();
        sfQuietTime = SmartfrogsvcAdvancedOption.getQuietTime();
        sfCp = buildSFClassPath();
    }
    
    private static String buildSFClassPath() {
        furls = new ArrayList<URL>();
        jarUrls = new ArrayList<URL>();
        
        StringBuffer cp = new StringBuffer();
        File sfHomeLibDir = new File(sfHome,"lib");
        if (sfHomeLibDir != null && sfHomeLibDir.exists()) {
            File[] lFiles = sfHomeLibDir.listFiles();
            for ( int i=0; i<lFiles.length; i++) {
                File f1 = lFiles[i];
                if (lFiles[i].getAbsolutePath().endsWith(".jar")) {
                    cp.append(lFiles[i].getAbsolutePath());
                    try {
                        furls.add(FileUtil.toFileObject(lFiles[i]).getURL());
                        JarFileSystem jfs = new JarFileSystem();
                        try {
                            jfs.setJarFile(lFiles[i]);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (PropertyVetoException ex) {
                            ex.printStackTrace();
                        }
                        
                        URL u = jfs.getRoot().getURL();
                        jarUrls.add(u);
                        
                    } catch (FileStateInvalidException ex) {
                        ex.printStackTrace();
                    }
                    if (i+1 < lFiles.length) {
                        cp.append(File.pathSeparator);
                    }
                }
            }
        }
        
        String[] homeFiles = sfUserHome.split(File.pathSeparator);
        for (int x=0; x<homeFiles.length; x++) {
            File sfUserHomeDir = new File(homeFiles[x]);
            if (sfUserHomeDir.exists()) {
                try {
                    furls.add(FileUtil.toFileObject(sfUserHomeDir).getURL());
                } catch (FileStateInvalidException ex) {
                    ex.printStackTrace();
                }
                File[] lFiles = sfUserHomeDir.listFiles();
                
                for ( int i=0; i<lFiles.length; i++) {
                    File f1 = lFiles[i];
                    if (lFiles[i].getAbsolutePath().endsWith(".jar")) {
                        try {
                            furls.add(FileUtil.toFileObject(lFiles[i]).getURL());
                            
                            JarFileSystem jfs = new JarFileSystem();
                            try {
                                jfs.setJarFile(lFiles[i]);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (PropertyVetoException ex) {
                                ex.printStackTrace();
                            }
                            
                            URL u = jfs.getRoot().getURL();
                            jarUrls.add(u); 
                        } catch (FileStateInvalidException ex) {
                            ex.printStackTrace();
                        }
                        
                        
                        if (cp.toString().charAt(cp.length()-1) != File.pathSeparatorChar) {
                            cp.append(File.pathSeparator);
                        }
                        cp.append(lFiles[i].getAbsolutePath());
                        if (i+1 < lFiles.length) {
                            cp.append(File.pathSeparator);
                        }
                    }
                }
            }
        }
        
        
        //int i = jarUrls.size();
        //URL[] lUrl = new URL[i];
        //Iterator<URL> iter = jarUrls.iterator();
        //int x=0;
        //while (iter.hasNext()) {
        //    lUrl[x++] = iter.next();
        //}
        //ClassPath newCp = ClassPathSupport.createClassPath(lUrl);
        //ClassPath[] arrayCP = new ClassPath[1];
        //arrayCP[0] = newCp;
        //GlobalPathRegistry.getDefault().register(ClassPath.BOOT,arrayCP);
        //GlobalPathRegistry.getDefault().register(ClassPath.COMPILE,arrayCP);
        //GlobalPathRegistry.getDefault().register(ClassPath.EXECUTE,arrayCP);
        //GlobalPathRegistry.getDefault().register(ClassPath.SOURCE,arrayCP);
        
        
        
        return cp.toString();
    }
    
    public static String getIniFile() {
        return " -Dorg.smartfrog.iniFile=" + sfHome + File.separator + "bin" + File.separator + "default.ini";
    }
    
    public static String getSFDefault() {
        return " -Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault=" + sfHome + File.separator + "bin" + File.separator + "default.sf";
    }
    
    
    public static String getUrlCodebase() {
        Iterator<URL> iter = furls.iterator();
        String res = new String();
        while (iter.hasNext()) {
            res += iter.next().toExternalForm() + " ";
        }
        res = res.substring(0,res.length()-1);
        return res;
    }
    
}
