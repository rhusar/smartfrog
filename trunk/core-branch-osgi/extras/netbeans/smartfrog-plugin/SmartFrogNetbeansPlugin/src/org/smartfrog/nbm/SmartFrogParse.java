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

import java.io.IOException;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class SmartFrogParse extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        DataObject c = (DataObject) activatedNodes[0].getCookie(DataObject.class);
        FileObject f = c.getPrimaryFile();
        // get classpath
        String cp = SmartFrogSvcUtil.getSFClassPath();
        
        // set options
        String iniFile = SmartFrogSvcUtil.getIniFile();
        String sfDefault = SmartFrogSvcUtil.getSFDefault();
        String userDir = FileUtil.getFileDisplayName(f.getParent());
        
        // call jvm
        
        Process proc = null;
        try {
            String[] procString = new String[8];
            procString[0]="java";
            procString[1]="-cp";
            procString[2]=cp;
            procString[3]="-Duser.dir=\"" + userDir + "\"";
            procString[4]="org.smartfrog.SFParse";
            procString[5]="-d";
            procString[6]="-v";
            procString[7]=FileUtil.getFileDisplayName(f);
            
            proc = Runtime.getRuntime().exec(procString);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
            ex.printStackTrace();
        }
        if (proc != null) {
        ExecSupport es = new ExecSupport();
        try {
            es.displayProcessOutputs(proc,"Smart Frog Parse");
        } catch (InterruptedException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
        }
        } else {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,new RuntimeException("Error starting Smart Frog"));
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(SmartFrogParse.class, "CTL_SmartFrogParse");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            DataObject.class
        };
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}

