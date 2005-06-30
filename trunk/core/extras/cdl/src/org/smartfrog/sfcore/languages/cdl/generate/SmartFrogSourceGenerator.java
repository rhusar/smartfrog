/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl.generate;

import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.logging.Log;

import java.io.File;
import java.io.IOException;

/**
 * This class generates a smartfrog source file from a CDL document
 */
public class SmartFrogSourceGenerator {

    /**
     * a log
     */
    private Log log = ClassLogger.getLog(this);


    private CdlDocument document;


    /**
     * prepare to generate source for a particular file
     *
     * @param document
     */
    public SmartFrogSourceGenerator(CdlDocument document) {
        this.document = document;

    }

    /**
     * Generate a new file
     *
     * @throws IOException
     */
    public void generate(String filename) throws IOException, CdlException {
        File destFile;
        destFile = new File(filename);
        generate(destFile);
    }

    /**
     * Generate a new file
     *
     * @throws IOException
     */
    public void generate(File destFile) throws IOException, CdlException {
        String destname = destFile.getAbsolutePath();
        if (log.isDebugEnabled()) {
            log.debug("Writing to " + destname);
        }
        //ask the doc to print itself

        GenerateContext context = new GenerateContext();
        context.begin(destFile);
        document.toSmartFrog(context);
        context.end();
    }



    /**
     * Save a file to smartfrog
     * @param cdlDocument
     * @return
     * @throws IOException
     * @throws CdlException
     */
    public File saveToSmartFrog(CdlDocument cdlDocument) throws IOException,
            CdlException {
        File tempFile = File.createTempFile("cdl", ".sf");
        log.info("Tempfile=" + tempFile);
        generate(tempFile);
        return tempFile;
    }

    /**
     * save a file
     * @param cdlDocument
     * @return
     * @throws IOException
     * @throws CdlException
     */
    public static File translate(CdlDocument cdlDocument) throws IOException,
            CdlException {
        SmartFrogSourceGenerator generator = new SmartFrogSourceGenerator(cdlDocument);
        return generator.saveToSmartFrog(cdlDocument);
    }

}
