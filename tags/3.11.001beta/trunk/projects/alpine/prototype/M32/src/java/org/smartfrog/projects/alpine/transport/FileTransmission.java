/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.projects.alpine.transport;

import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.transport.http.HttpTransmitter;

import java.io.File;

/**
 * Transmit an XML file to a destination, using the soap version and URL of the current
 * message context.
 * The file is not deleted afterwards.
 * created 11-Dec-2006 14:18:41
 */

public class FileTransmission extends Transmission {

    private File file;


    public FileTransmission(MessageContext context) {
        super(context);
    }

    /**
     * Transmit a file
     * @param context message context
     * @param file file to send
     */
    public FileTransmission(MessageContext context, File file) {
        super(context);
        this.file = file;
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * triggers the actuall transmission
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException in case of trouble.
     */
    public Object call() throws Exception {
        HttpTransmitter transmitter = new HttpTransmitter(this);
        transmitter.transmitPayload(file);
        return null;
    }
}
