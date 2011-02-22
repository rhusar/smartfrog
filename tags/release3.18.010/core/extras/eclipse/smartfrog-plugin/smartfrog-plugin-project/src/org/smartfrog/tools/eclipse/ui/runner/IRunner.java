
/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.tools.eclipse.ui.runner;

import org.smartfrog.tools.eclipse.model.ExceptionHandler;
import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;
import org.smartfrog.tools.eclipse.ui.console.SmartFrogConsoleDocument;
import org.smartfrog.tools.eclipse.ui.preference.SmartFrogPreferencePage;

import java.io.File;

/**
 * Provide basic methods for all runner classes and run as a thread
 */
public abstract class IRunner
    extends Thread
{
    protected Process mProcess = null;
    private SmartFrogConsoleDocument fDcument = SmartFrogConsoleDocument.getInstance(  );
    /**
     * Return whether the process is running.
     */
    public abstract boolean isRunning();
    
    /**
     * Execute the command in the thread
     * @param cmd	Command string
     * @return
     */
    protected int executeCmd(String cmd)
    {
 
    	
        try {
            fDcument.append(cmd , SmartFrogConsoleDocument.MSG_DEFAULT);
            mProcess = Runtime.getRuntime().exec( cmd, null, new File(SmartFrogPreferencePage.getSmartFrogLocation()));
            ( new StreamGobbler(mProcess.getInputStream(), "ERROR") ).start(); //$NON-NLS-1$
            ( new StreamGobbler(mProcess.getErrorStream(), "OUTPUT") ).start(); //$NON-NLS-1$

            int status = mProcess.waitFor();

            if (0 != status) {
                // if this the daemon, it may just got killed before a now one launched, ignore this now
//                ExceptionHandler.log(new Exception(""), //$NON-NLS-1$
//                    ( Messages.getString(
//                            "SmartFrogGUIRunnerExt.Message.LaunchFailed") )); //$NON-NLS-1$

                return ISmartFrogConstants.ERROR;
            }

            return ISmartFrogConstants.SUCCESS;
        } catch (Exception e) {
            ExceptionHandler.handleInSWTThread(e,
                ( Messages.getString("SmartFrogGUIRunnerExt.Title.CantLaunch") ), //$NON-NLS-1$
                ( Messages.getString(
                        "SmartFrogGUIRunnerExt.Message.CantLaunch") )); //$NON-NLS-1$
        }

        return ISmartFrogConstants.ERROR;
    }

    /**
     * Execute the command in the thread
     * @param cmd	Command string
     * @return
     */
    protected int executeCmd(String[] cmd)
    {
 
    	
        try {
        	for (int i=0; i <cmd.length; i++)
        	{
        		fDcument.append(cmd[i] + " " , SmartFrogConsoleDocument.MSG_DEFAULT);
        	}
            mProcess = Runtime.getRuntime().exec( cmd, null, new File(SmartFrogPreferencePage.getSmartFrogLocation()));
            ( new StreamGobbler(mProcess.getInputStream(), "ERROR") ).start(); //$NON-NLS-1$
            ( new StreamGobbler(mProcess.getErrorStream(), "OUTPUT") ).start(); //$NON-NLS-1$

            int status = mProcess.waitFor();

            if (0 != status) {
                // if this the daemon, it may just got killed before a now one launched, ignore this now
//                ExceptionHandler.log(new Exception(""), //$NON-NLS-1$
//                    ( Messages.getString(
//                            "SmartFrogGUIRunnerExt.Message.LaunchFailed") )); //$NON-NLS-1$

                return ISmartFrogConstants.ERROR;
            }

            return ISmartFrogConstants.SUCCESS;
        } catch (Exception e) {
            ExceptionHandler.handleInSWTThread(e,
                ( Messages.getString("SmartFrogGUIRunnerExt.Title.CantLaunch") ), //$NON-NLS-1$
                ( Messages.getString(
                        "SmartFrogGUIRunnerExt.Message.CantLaunch") )); //$NON-NLS-1$
        }

        return ISmartFrogConstants.ERROR;
    }

    
    /**
     * 
     * @return the running process
     */
    protected Process getProcess()
    {
        return mProcess;
    }
}
