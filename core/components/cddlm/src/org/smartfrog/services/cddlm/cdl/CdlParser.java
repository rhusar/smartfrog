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
package org.smartfrog.services.cddlm.cdl;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * JDom based utility to parse CDL files. created Jul 1, 2004 1:49:31 PM
 */

public class CdlParser {

    /**
     * logic for resource loading
     */
    private ResourceLoader resourceLoader;

    /**
     * builder class
      */
    private SAXBuilder builder;

    /**
     * create a parser;
     *
     * @param loader   resource loader algorithm
     * @param validate validation logic.
     */
    public CdlParser(ResourceLoader loader, boolean validate) {
        resourceLoader = loader;
        builder = new SAXBuilder(validate);
    }


    /**
     * parse the file, and throw an exception if we couldnt
     *
     * @param filename
     * @return
     * @throws IOException
     * @throws JDOMException
     */
    Document parseFile(String filename) throws IOException, JDOMException {
        File f = new File(filename);
        return builder.build(f);
    }

    /**
     * parse a stream that we are provided
     * @param instream
     * @return
     * @throws IOException
     * @throws JDOMException
     */
    Document parseStream(InputStream instream) throws IOException,
            JDOMException {
        return builder.build(instream);
    }

    /**
     * load and parse a resoure through our current resource loader
     * @param resource
     * @return
     * @throws IOException
     * @throws JDOMException
     */
    Document parseResource(String resource) throws IOException, JDOMException {
        InputStream in = resourceLoader.loadResource(resource);
        return parseStream(in);
    }

}
