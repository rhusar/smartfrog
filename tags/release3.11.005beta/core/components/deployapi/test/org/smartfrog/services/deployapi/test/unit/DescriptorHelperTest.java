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

package org.smartfrog.services.deployapi.test.unit;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import org.smartfrog.services.deployapi.binding.DescriptorHelper;
import org.smartfrog.services.deployapi.system.Constants;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.faults.BaseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * test descriptor helper class
 */
public class DescriptorHelperTest extends UnitTestBase {
    public static final String REFERENCE = "http://somewhere/path";
    public static final String LANGUAGE = Constants.XML_CDL_NAMESPACE;

    public DescriptorHelperTest(String name) {
        super(name);
    }

    DescriptorHelper helper;

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        helper=new DescriptorHelper(null);
    }

    public void testXomReference() throws Exception {
        Element element;
        element = helper.createReferenceXomDescriptor(REFERENCE,
                LANGUAGE);
        Nodes nodes = element.query("//api:descriptor/api:reference",
                Constants.XOM_CONTEXT);
        assertEquals(1,nodes.size());
        Element e=(Element) nodes.get(0);
        assertEquals(REFERENCE, e.getValue());
        Attribute a = element.getAttribute(DescriptorHelper.LANGUAGE,
                Constants.CDL_API_TYPES_NAMESPACE);
        assertEquals(LANGUAGE,a.getValue());
    }

    public void testRetrieveHttp() throws Exception {
        assertRetrieveFails("http://localhost:8080/file/");
    }

    private void assertRetrieveFails(String url) throws IOException {
        try {
            File f=helper.retrieveRemoteReference(url, "");
            f.delete();
            fail("expected failure:"+url);
        } catch (BaseException e) {
            //success
        }
    }

    public void testMissingFile() throws Exception {
        assertRetrieveFails("file://a-nonexistent-file.xml");
    }

    public void testLocalFile() throws Exception {
        File f = createTempFile();
        try {
            String url = f.toURI().toString();
            assertTrue(url.endsWith(".xml"));
            File f2=helper.retrieveRemoteReference(url, "xml");
            assertEquals(f.getAbsolutePath(),f2.getAbsolutePath());
        } finally {
            f.delete();
        }
    }

    private File createTempFile() throws IOException {
        File f=helper.createTempFile("xml");
        return f;
    }

    protected Element loadInlineDescriptorFromResource(
            String resource, String language) throws IOException,
            ParsingException {
        InputStream in=loadResource(resource);
        Element descriptor = helper.loadInlineDescriptor(in, language);
        Element initRequest = helper.createInitRequest();
        initRequest.appendChild(descriptor);
        return initRequest;

    }


    public void testRoundTripReference() throws Exception {
        File f = createTempFile();
        try {
            String url = f.toURI().toString();
            Element initRequest = helper.createInitRequest();
            Element d = helper.createReferenceXomDescriptor(f.toURI().toString(),
                    LANGUAGE);
            initRequest.appendChild(d);
            helper.validateRequest(initRequest);
            File f2 = helper.extractBodyToFile(initRequest, "xml");
            assertEquals(f.getAbsolutePath(), f2.getAbsolutePath());
        } finally {
            f.delete();
        }
    }


    public void testLoadFromResource() throws Exception {
        Element request =loadInlineDescriptorFromResource(DOC_CREATE,"test");
        File f=helper.extractBodyToFile(request, "xml");
        
    }
    
    public void testExtractResourceToXml() throws Exception {
        Element request = loadInlineDescriptorFromResource(DOC_CREATE, "test");
        Element descriptor=helper.extractDescriptorAsXML(request);
        Nodes nodes = descriptor.query("api:body/test:tests/test:test",
                Constants.XOM_CONTEXT);
        assertTrue(nodes.size()>0);
    }
    

    public void testLoadSmartFrogFileinline() throws Exception {
        final String body = "wibble";
        Element request = createSFrequest(body);
        helper.validateRequest(request);
        File file = helper.extractBodyToFile(request, "xml");
        String contents = Utils.loadFile(file,Constants.CHARSET_UTF8);
        assertTrue("search string not found in "+contents, 
                contents.indexOf(body)>=0);
    }

    private Element createSFrequest(String body) {
        Element descriptor=helper.createSmartFrogInlineDescriptor(body);
        Element request=helper.createInitRequest(descriptor);
        return request;
    }

    public void testSaveToSmartfrogFile() throws Exception {
        final String body = "wibble";
        Element request = createSFrequest(body);
        helper.validateRequest(request);
        Element descriptor = helper.extractDescriptorAsXML(request);
        Nodes n=descriptor.query("api:body/sf:smartfrog",Constants.XOM_CONTEXT);
        Element sfnode=(Element)n.get(0);
        String value = sfnode.getValue();
        assertEquals(body,value);
        File file=helper.saveInlineSmartFrog(request);
        String contents = Utils.loadFile(file, Constants.CHARSET_UTF8);
        assertTrue("search string not found in " + contents,
                contents.indexOf(body) == 0); 
    }
}
