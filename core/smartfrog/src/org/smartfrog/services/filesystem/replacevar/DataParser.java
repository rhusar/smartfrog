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

package org.smartfrog.services.filesystem.replacevar;

import java.util.Vector;

/**
 * Title:        Data object for the ParserVar
 * @version 1.0
 */

public class DataParser {

    Vector dataReplace = null;
    Vector dataAppend = null;
    String fileName = "";
    String newFileName = "";
    boolean shouldDetach = false;
    boolean shouldTerminate = false;
    // indicates if the data is complete and valid. It is complete when fileName and data are filled.
    boolean valid = true;

    public DataParser(String fileName, Vector dataAppend, Vector dataReplace) {
        this.dataAppend = dataAppend;
        this.dataReplace = dataReplace;
        this.fileName = fileName;
    }

    public void setShouldTerminate(boolean shouldTerminate) {
        this.shouldTerminate = shouldTerminate;
    }

    public void setShouldDetach(boolean shouldDetach) {
        this.shouldDetach = shouldDetach;
    }

    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public boolean shouldDetach() {
        return shouldDetach;
    }

    public Vector getDataReplace() {
        return dataReplace;
    }

    public Vector getDataAppend() {
        return dataAppend;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        if (fileName != null) {
            return fileName;
        } else {
            return "";
        }
    }

    public void setNewFileName(String fileName) {
        newFileName = fileName;
    }

    public String getNewFileName() {
        if (newFileName != null) {
            return newFileName;
        } else {
            return "";
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}