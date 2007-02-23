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

import java.io.*;

/**
 * created 11-Dec-2006 14:28:41
 */

public class ResourceTransmission extends FileTransmission {

    private String resource;

    public ResourceTransmission(MessageContext context, String resource) throws IOException {
        super(context);
        this.resource=resource;

        InputStream ins= context.getLoader().loadResource(resource);
        File outputFile = null;
        OutputStream outToFile = null;
        try {
            outputFile = File.createTempFile("alpine", ".xml");
            outToFile=new FileOutputStream(outputFile);
            int b;
            while((b=ins.read())>=0) {
                outToFile.write(b);
            }
            outToFile.flush();
            setFile(outputFile);
        } finally {
            outToFile.close();
            ins.close();
        }
    }


    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    public String toString() {
        return "Transmitting resource "+resource+ " as "+getFile().getPath();
    }
}
