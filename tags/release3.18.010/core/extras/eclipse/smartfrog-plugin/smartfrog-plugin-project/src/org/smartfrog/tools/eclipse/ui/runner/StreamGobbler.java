
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
import org.smartfrog.tools.eclipse.ui.console.SmartFrogConsoleDocument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Catch the process output and direct to
 */
public class StreamGobbler
    extends Thread
{
    private InputStream is;
    private String type;
    private SmartFrogConsoleDocument fDcument = SmartFrogConsoleDocument.getInstance(  );

    /**
     * @param is         input stream
     * @param type        output type/prefix
     */
    public StreamGobbler(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;

            while (( line = br.readLine() ) != null) {
                fDcument.append( line + "\n", SmartFrogConsoleDocument.MSG_DEFAULT ); //$NON-NLS-1$
            }
        } catch (IOException ioe) {
            ExceptionHandler.handle(ioe,
                ( Messages.getString("StreamGobbler.Title.CantCatchOutput") ), //$NON-NLS-1$
                ( Messages.getString("StreamGobbler.Message.CantCatchOutput") )); //$NON-NLS-1$
        }
    }
}
