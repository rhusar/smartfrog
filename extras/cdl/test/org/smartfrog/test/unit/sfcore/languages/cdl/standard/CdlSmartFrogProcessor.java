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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ggf.cddlm.cdl.test.CDLException;
import org.ggf.cddlm.cdl.test.CDLProcessor;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.importing.FilestoreImportResolver;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.test.unit.sfcore.languages.cdl.DocumentTestHelper;
import org.smartfrog.services.xml.java5.XomToDom3;
import org.smartfrog.services.xml.utils.DomToXom;
import org.smartfrog.services.filesystem.FileSystem;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.net.URI;
import java.io.PrintStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;

import nu.xom.converters.DOMConverter;
import nu.xom.Serializer;
import nu.xom.Element;

import javax.xml.namespace.QName;

/**
 * created 25-Nov-2005 15:09:51
 */

public class CdlSmartFrogProcessor implements CDLProcessor {

    private Log log = LogFactory.getLog(this.getClass());
    private DocumentTestHelper helper;
    private FilestoreImportResolver filestore;


    public CdlSmartFrogProcessor() throws SAXException {
        helper = new DocumentTestHelper(true);
        File dir = null;
        try {
            dir = File.createTempFile("files", ".tmp");
            dir.delete();
            filestore = new FilestoreImportResolver(dir);
        } catch (IOException e) {
            throw new CDLException("when creating the filestore in directory "
                    +dir,e);
        }
    }

    /**
     * Give a document which will be referred to by the CDL document to be resolved.
     * This must be called before resolve() so that the CDL document can refer to
     * a document with the URI.
     *
     * @param id
     * @param doc
     */
    public void put(URI id, Document doc) {
        try {
            File file=filestore.createEntry(id, ".cdl");
            saveToFile(doc, file);
        } catch (IOException e) {
            throw new CDLException(e);
        }
    }

    /**
     * Resolve a CDL document. If resolution is successful, it returns a resolved data
     * (which is a data under &lt;cdl:system&gt; after resolution) as a Document. If resolution
     * is not successful, it throws CDLException.
     *
     * @param doc a CDL document to be resolved.
     * @return resolved data.
     * @throws CDLException
     */
    public Document resolve(Document doc) throws CDLException {
        try {
            ParseContext context = new ParseContext();
            context.setImportResolver(filestore);
            CdlDocument cdlDocument = helper.load(doc);
            cdlDocument.parse(context);
            Element system= cdlDocument.getSystem();
            system.detach();
            nu.xom.Document newRoot = new nu.xom.Document(system);
            return XomToDom3.fromXom(newRoot);
        } catch (Exception e) {
            throw new CDLException(e);
        }
    }


    /**
     * This is called when a test sequence is finished. After that, this processor
     * should not be used. Thus, an implementation may throw away any stateful resources
     * caused from invocations of put() and resolve().
     */
    public void close() {
        try {
            filestore.close();
        } catch (IOException e) {
            throw new CDLException("When closing "+filestore,e);
        }
    }


    /**
     * Dump an XML doc to an output stream
     *
     * @param out     output stream
     * @param doc     doc to dump (may be null)
     * @param message text message
     */
    public void dump(PrintStream out, Document doc, String message) throws IOException {
        out.println(message);
        if (doc == null) {
            out.println("(null)");
            return;
        }
        saveToStream(doc, out);
    }

    /**
     * Save a doc to a stream
     * @param doc
     * @param out
     * @throws IOException
     */

    private void saveToStream(Document doc, PrintStream out) throws IOException {
        nu.xom.Document document = DOMConverter.convert(doc);
        Serializer ser=new Serializer(out);
        ser.write(document);
    }


    private void saveToFile(Document doc, File file) throws IOException {
        PrintStream print=null;
        try {
            print=new PrintStream(file,"UTF-8");
            saveToStream(doc, print);
        } finally {
            FileSystem.close(print);
        }
    }

    /**
     * Verify that a received fault is of the expected type. How this is done
     * is up to the implementation; a default implementation would be to
     * always return true.
     * This method is called when a test declared that a fault would be thrown.
     *
     * @param test      the name of the test, e,g cddlm-cdl-2005-01-0001
     * @param faultCode the fault code that was expected in this fault. Is null if the
     *                  test document did not specify what the fault code would be
     * @param thrown    the exception that was received
     * @return true if the throwable was the type expected.
     */
    public boolean isExpectedFault(String test, QName faultCode, Throwable thrown) {
        return true;
    }
}
