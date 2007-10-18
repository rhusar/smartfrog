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
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class Launch extends CallableSystemAction {
    
    private static boolean started = false;
    
    public static boolean isStarted() {
        return started;
    }
    
    public static void setNotStarted() {
        started = false;
    }
    
    public void performAction() {
        if (started) {
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
            String procString[] = new String[7];
            procString[0]="java";
            procString[1]="-cp";
            procString[2]=cp;
            procString[3]=iniFile.trim();
            procString[4]=sfDefault.trim();
            procString[5]="-Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess";
            procString[6]="org.smartfrog.SFSystem";
            proc = Runtime.getRuntime().exec(procString);
            started = true;
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
            ex.printStackTrace();
            started = false;
        }
        if (proc != null) {
        ExecSupport es = new ExecSupport();
        try {
            es.displayProcessOutputs(proc,"Smart Frog Daemon");
        } catch (InterruptedException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
        }
        } else {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,new RuntimeException("Error starting Smart Frog"));
            started = false;
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(Launch.class, "CTL_Launch");
    }
    
    protected String iconResource() {
        return "org/smartfrog/nbm/frog16x16.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
