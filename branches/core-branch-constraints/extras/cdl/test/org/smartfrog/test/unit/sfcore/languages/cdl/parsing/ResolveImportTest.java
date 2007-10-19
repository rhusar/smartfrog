package org.smartfrog.test.unit.sfcore.languages.cdl.parsing;

import org.smartfrog.sfcore.languages.cdl.importing.ClasspathResolver;
import org.smartfrog.sfcore.languages.cdl.importing.ImportResolver;
import org.smartfrog.test.unit.sfcore.languages.cdl.XmlTestBase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * Test our importation
 */
public class ResolveImportTest extends XmlTestBase {
    private ImportResolver resolver = new ClasspathResolver();
    public static final String RESOURCE = "log4j.properties";

    public ResolveImportTest(String name) {
        super(name);
    }

    public void testResolveNoPath() throws Exception {
        URL url = resolve(RESOURCE);
    }

    private URL resolve(String path) throws IOException {
        return resolver.createReferenceURL(path);
    }

    private void expectResolveFailure(String path) throws IOException {
        try {
            URL url = resolve(path);
            fail("Expected to fail, got " + url.toString());
        } catch (FileNotFoundException e) {
            //swallow
        }
    }


    public void testResolveForwardSlash() throws Exception {
        expectResolveFailure("/" + RESOURCE);
    }

    public void testBadPath() throws Exception {
        expectResolveFailure("no-such-file.class");
    }

    public void testBackslash() throws Exception {
        expectResolveFailure("\\" + RESOURCE);
    }

}
