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
package org.smartfrog.sfcore.languages.cdl;

import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.generate.SmartFrogSourceGenerator;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.StreamParser;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * This is a parser for CDL XML streams. The base SFParser class contains some
 * assumptions about package names That force the name of this class. DO NOT
 * RENAME. created 18-Apr-2005 13:50:53
 *
 * @see org.smartfrog.sfcore.parser.SFParser
 * @see org.smartfrog.sfcore.languages.sf.SFParser for the reference example
 */

public class SFParser implements StreamParser {

    /**
     * Parses component(s) from an input stream. Returns a root component which
     * contains the parsed components. Includes should be handled by some
     * default include handler.
     *
     * @param is input stream to parse and compile from
     * @return root component containing parsed component(s)
     * @throws SmartFrogParseException error parsing stream
     */
    public Phases sfParse(InputStream is) throws SmartFrogParseException {
        try {
            //first, parse the CDL into smartfrog

            //TODO: use the smartfrog classloader
            ResourceLoader loader = new ResourceLoader(this.getClass());
            CdlParser parser = new CdlParser(loader, true);
            ParseContext parseContext = new ParseContext();
            CdlDocument cdlDocument = parser.parseStream(is);
            //here it is loaded; create the dom
            cdlDocument.parse(parseContext);

            //now we are ready to create a smartfrog file.
            File generated=SmartFrogSourceGenerator.translate(cdlDocument);
            //it is created, now parse that
            org.smartfrog.sfcore.parser.SFParser sfparser;
            sfparser=new org.smartfrog.sfcore.parser.SFParser("sf");

            InputStream newIn=null;
            Phases phases=null;
            try {
                newIn = new FileInputStream(generated);
                phases = sfparser.sfParse(newIn);
            } finally {
                if(newIn!=null) {
                    newIn.close();
                }
            }
            return phases;

        } catch (Throwable thrown) {
            throw (SmartFrogParseException) SmartFrogParseException.forward(
                    thrown);
        }


    }



    /**
     * Parses a reference from an input stream. Used by components and
     * developers to quickly build references from a string (eg. sfResolve in
     * Prim)
     *
     * @param is input stream to parse for a reference
     * @return parsed reference
     * @throws SmartFrogParseException failed to parse reference
     */
    public Reference sfParseReference(InputStream is)
            throws SmartFrogParseException {
        //TODO
        return null;
    }
}
