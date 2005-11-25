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
package org.smartfrog.test.unit.sfcore.languages.cdl.standard;

import org.ggf.cddlm.cdl.test.CDLProcessor;
import org.ggf.cddlm.cdl.test.CDLException;
import org.w3c.dom.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.sfcore.languages.cdl.CdlCatalog;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.test.unit.sfcore.languages.cdl.DocumentTestHelper;

import java.net.URI;
import java.io.IOException;

import nu.xom.ParsingException;

/**
 * created 25-Nov-2005 15:09:51
 */

public class CdlSmartFrogProcessor implements CDLProcessor {

    protected Log log = LogFactory.getLog(this.getClass());
   DocumentTestHelper helper=new DocumentTestHelper();


    /**
     * Give a document which will be referred to by the CDL document to be resolved.
     * This must be called before resolve() so that the CDL document can refer to
     * a document with the URI.
     *
     * @param id
     * @param doc
     */
    public void put(URI id, Document doc) {
        //TODO
    }

    /**
     * Resolve a CDL document. If resolution is successful, it returns a resolved data
     * (which is a data under &lt;cdl:system&gt; after resolution) as a Document. If resolution
     * is not successful, it throws CDLException.
     *
     * @param doc a CDL document to be resolved.
     * @return resolved data.
     * @throws CDLException
     *
     */
    public Document resolve(Document doc) throws CDLException {
        try {
            ParseContext context = new ParseContext();
            CdlDocument document=helper.load(doc);
            //TODO
            return null;
        } catch (CdlException e) {
            throw new CDLException(e);
        } catch (ParsingException e) {
            throw new CDLException(e);
        }
    }




    /**
     * This is called when a test sequence is finished. After that, this processor
     * should not be used. Thus, an implementation may throw away any stateful resources
     * caused from invocations of put() and resolve().
     */
    public void close() {
        //TODO
    }




}
