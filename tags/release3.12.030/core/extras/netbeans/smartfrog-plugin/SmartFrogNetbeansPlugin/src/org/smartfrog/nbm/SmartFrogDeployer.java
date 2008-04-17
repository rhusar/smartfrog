/* (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import java.util.TreeSet;

public class SmartFrogDeployer {
    
    /** Creates a new instance of SmartFrogDeployer */
    public SmartFrogDeployer() {
    }
    
    private static TreeSet<String> deployedNames = new TreeSet<String>();
    
    public static TreeSet<String> getDeployedNames() {
        return deployedNames;
    }
    
    public static void removeDeployedName(String name) {
        deployedNames.remove(name);
    }
    
    public static void terminateComponent() {
        
        TreeSet<String> currentNames = SmartFrogDeployer.getDeployedNames();
        SelectCurrentName scn = new SelectCurrentName(null,true);
        scn.setModel(currentNames.iterator());
        int centerX = scn.getWidth()/2;
        int centerY = scn.getWidth()/2;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int)screenSize.getWidth();
        int screenHeight = (int)screenSize.getHeight();
        int screenCenterX = screenWidth / 2;
        int screenCenterY = screenHeight / 2;
        int topLeftX = screenCenterX - centerX;
        int topLeftY = screenCenterY - centerY;
        scn.setLocation(topLeftX,topLeftY);
        scn.setVisible(true);
        
        if (scn.isCanceled()) {
            return;
        }
        //String name = JOptionPane.showInputDialog("Name ?") ;
        String[] names = scn.getSelectedNames();
        
        // get classpath
        String cp = SmartFrogSvcUtil.getSFClassPath();
        
        // figure out the name for this sf file
        
        // set options
        String iniFile = SmartFrogSvcUtil.getIniFile();
        String sfDefault = SmartFrogSvcUtil.getSFDefault();
        
        // call jvm
        
        for (int i=0; i<names.length; i++) {
            Process proc = null;
            try {
                String[] procString = new String[9];
                procString[0]="java";
                procString[1]="-cp";
                procString[2]=cp;
                procString[3]=iniFile.trim();
                procString[4]=sfDefault.trim();
                procString[5]="org.smartfrog.SFSystem";
                procString[6]="-a";
                procString[7]="\""+names[i] + "\":TERMINATE:''::localhost:";
                procString[8]="-e";
                
                proc = Runtime.getRuntime().exec(procString);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
                ex.printStackTrace();
            }
            if (proc != null) {
                ExecSupport es = new ExecSupport();
                try {
                    es.displayProcessOutputs(proc,"Smart Frog Terminate");
                    SmartFrogDeployer.removeDeployedName(names[i]);
                } catch (InterruptedException ex) {
                    ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
                }
            } else {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,new RuntimeException("Error starting Smart Terminate"));
            }
        }
    }
    
    public static void deployComponent(SmartFrogFileTypeDataObject c, boolean usePackage) {
        FileObject fo = c.getPrimaryFile();
        Project p = FileOwnerQuery.getOwner(fo);
        Sources ss = ProjectUtils.getSources(p);
        SourceGroup[] sg = ss.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        
        String comPath = "";
        if (usePackage) {
            String fileObjectPath = fo.getPath();
            for (int i=0; i<sg.length; i++) {
                int idx = fileObjectPath.indexOf(sg[i].getRootFolder().getPath());
                if (idx >= 0) {
                    System.out.println("found path");
                    comPath = fileObjectPath.substring(sg[i].getRootFolder().getPath().length());
                }
            }
        } else {
            comPath = FileUtil.getFileDisplayName(fo);
        }
        
        String displayName = comPath;
        
        int idx = displayName.lastIndexOf(File.separator);
        String sfName = displayName.substring(idx+1);
        
        String name = JOptionPane.showInputDialog("Name?") ;
        boolean foundName = false;
        if (deployedNames.size()>0) {
            foundName = deployedNames.contains(name);
        }
        if (foundName) {
            JOptionPane.showMessageDialog(null,"Error: name already has been deployed","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // get classpath
        String cp = SmartFrogSvcUtil.getSFClassPath();
        
        // set options
        String iniFile = SmartFrogSvcUtil.getIniFile();
        String sfDefault = SmartFrogSvcUtil.getSFDefault();
        
        // call jvm
        
        Process proc = null;
        try {
            String[] procString = new String[9];
            procString[0]="java";
            procString[1]="-cp";
            procString[2]=cp;
            procString[3]=iniFile.trim();
            procString[4]=sfDefault.trim();
            procString[5]="org.smartfrog.SFSystem";
            procString[6]="-a";
            procString[7]="\""+name + "\":DEPLOY:'" + comPath + "'::localhost:";
            procString[8]="-e";
            proc = Runtime.getRuntime().exec(procString);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
            ex.printStackTrace();
        }
        if (proc != null) {
            ExecSupport es = new ExecSupport();
            try {
                es.displayProcessOutputs(proc,"Smart Frog Deploy");
                deployedNames.add(name);
            } catch (InterruptedException ex) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
            }
        } else {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,new RuntimeException("Error starting Smart Frog"));
        }
    }
    
}
