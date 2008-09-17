
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

import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;
import org.smartfrog.tools.eclipse.ui.preference.SmartFrogPreferencePage;


/**
 * Launch SmartFrog Management console
 */
class SfConsoleRunnerExt
    extends ISfRunnerExt
{
    public void run()
    {
        String cmdStart[] = createCmd();

        executeCmd(cmdStart);
    }

    private String[] createCmd()
    {
        String batchDir = "bin"; //$NON-NLS-1$
        String dir =   SmartFrogPreferencePage.getSmartFrogLocation() +
            ISmartFrogConstants.FILE_SEPARATOR + batchDir +
            ISmartFrogConstants.FILE_SEPARATOR ;

        String cmdGeneral[] = getCommandGeneralArray();


        String cmds[] = new String[cmdGeneral.length+1];
        for (int i=0;  i < cmdGeneral.length; i++)
        {
        	cmds[i] = cmdGeneral[i];
        }
        cmds[cmdGeneral.length]=  dir + CMD_SFMANAGEMENT_CONSOLE ;
        	
        return cmds;
    }
}
