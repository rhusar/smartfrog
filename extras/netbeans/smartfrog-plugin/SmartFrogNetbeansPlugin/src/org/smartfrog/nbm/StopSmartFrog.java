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

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.ErrorManager;
import org.openide.windows.*;
import java.util.*;
import java.io.*;
import java.lang.Runtime;

public final class StopSmartFrog extends CallableSystemAction {
    
    public static void doShutdown() {
        if (Launch.isStarted()) {
            // get classpath
            String cp = SmartFrogSvcUtil.getSFClassPath();
            
            // set options
            String iniFile = SmartFrogSvcUtil.getIniFile();
            String sfDefault = SmartFrogSvcUtil.getSFDefault();
            
            // call jvm
            
            Process proc = null;
            try {
                String procString = "java ";
                procString += " -cp \"" + cp + "\"";
                procString += iniFile;
                procString += sfDefault;
                procString += " org.smartfrog.SFSystem -a rootProcess:TERMINATE:::LocalHost: -e";
                proc = Runtime.getRuntime().exec(procString);
                Launch.setNotStarted();
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
                ex.printStackTrace();
            }
            if (proc == null) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,new RuntimeException("Error stopping Smart Frog"));
            }
        }
        
    }
    
    public void performAction() {
        doShutdown();
    }
    
    public String getName() {
        return NbBundle.getMessage(StopSmartFrog.class, "CTL_StopSmartFrog");
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
