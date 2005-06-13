package org.smartfrog.test.unit.sfcore.languages.cdl;

import junit.framework.TestCase;
import nu.xom.ParsingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.xml.utils.ResourceLoader;
import org.smartfrog.sfcore.languages.cdl.CdlCatalog;
import org.smartfrog.sfcore.languages.cdl.CdlParser;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * this is the testbase for the tests that load files
 */
public abstract class XmlTestBase extends TestCase {
    public final static String RESOURCES = "test/cdl/";
    public final static String VALID_RESOURCES = RESOURCES + "valid/";
    public final static String INVALID_RESOURCES = RESOURCES + "invalid/";
    public final static String RESOLUTION_RESOURCES = VALID_RESOURCES +
            "resolution/";
    public final static String INVALID_RESOLUTION_RESOURCES = INVALID_RESOURCES +
            "resolution/";
    public static final String WRONG_NAMESPACE_TEXT = "Cannot find the declaration of element 'cdl:cdl'";

    protected Log log = LogFactory.getLog(this.getClass());

    /**
     * {@value}
     */
    public static final String CDL_DOC_MINIMAL = VALID_RESOURCES +
            "minimal.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_FULL_EXAMPLE_1 = VALID_RESOURCES +
            "full-example-1.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_FULL_EXAMPLE_2 = VALID_RESOURCES +
            "full-example-2.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_FULL_EXAMPLE_3 = VALID_RESOURCES +
            "full-example-3.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_WEBSERVER = VALID_RESOURCES +
            "webserver.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_WEBSERVER_NO_NAMESPACE = VALID_RESOURCES +
            "webserver-no-namespace.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_WEBSERVER_DEFAULT_NAMESPACE = VALID_RESOURCES +
            "webserver-default-namespace.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_DOCUMENTED = VALID_RESOURCES +
            "documented.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_REFERENCES_1 = VALID_RESOURCES +
            "references-1.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_REFERENCES_2 = VALID_RESOURCES +
            "references-2.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_REFERENCES_3 = VALID_RESOURCES +
            "references-3.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_EXPRESSION_1 = VALID_RESOURCES +
            "expression-1.cdl";

    /**
     * {@value}
     */
    public static final String CDL_DOC_EXPRESSION_DUPLICATE = INVALID_RESOURCES +
            "expression-duplicate-variables.cdl";

    /**
     * {@value}
     */
    public static final String CDL_DOC_TYPE_1 = VALID_RESOURCES + "type-1.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_LAZY_1 = VALID_RESOURCES + "lazy-1.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_LAZY_2 = VALID_RESOURCES + "lazy-2.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_PARAMETERIZATION_1 = VALID_RESOURCES +
            "parameterization-1.cdl";
    /**
     * {@value}
     */
    public static final String CDL_DOC_EXTRA_ELEMENTS = VALID_RESOURCES +
            "extra-elements.cdl";

    /**
     * extends gets tested {@value}
     */
    public static final String CDL_DOC_EXTENDS_1 = RESOLUTION_RESOURCES +
            "extends-1.cdl";

    /**
     * This is our list of valid documents. These must all parse
     */
    protected final static String[] VALID_CDL = {
        CDL_DOC_MINIMAL,
        CDL_DOC_EXTRA_ELEMENTS,
        CDL_DOC_WEBSERVER,
        CDL_DOC_WEBSERVER_NO_NAMESPACE,
        CDL_DOC_WEBSERVER_DEFAULT_NAMESPACE,
        CDL_DOC_DOCUMENTED,
        CDL_DOC_REFERENCES_1,
        CDL_DOC_REFERENCES_2,
        CDL_DOC_REFERENCES_3,
        CDL_DOC_EXPRESSION_1,
        CDL_DOC_TYPE_1,
        CDL_DOC_LAZY_1,
        CDL_DOC_LAZY_2,
        CDL_DOC_PARAMETERIZATION_1,
        CDL_DOC_FULL_EXAMPLE_1,
        CDL_DOC_FULL_EXAMPLE_2,
        CDL_DOC_FULL_EXAMPLE_3,
    };
    public static final String CDL_DOC_WRONG_ELT_ORDER = INVALID_RESOURCES +
            "wrong_elt_order.cdl";
    public static final String CDL_DOC_WRONG_ROOT_ELT_TYPE = INVALID_RESOURCES +
            "wrong_root_elt_type.cdl";

    public static final String CDL_DOC_WRONG_NAMESPACE = INVALID_RESOURCES +
            "wrong_doc_namespace.cdl";


    public static final String CDL_DOC_BAD_NAMESPACE = INVALID_RESOLUTION_RESOURCES
            + "extends-bad-namespace.cdl";
    public static final String CDL_DOC_BAD_REFERENCE = INVALID_RESOLUTION_RESOURCES
            + "extends-bad-reference.cdl";
    public static final String CDL_DOC_DUPLICATE_NAME = INVALID_RESOLUTION_RESOURCES
            + "extends-duplicate-name.cdl";

    public static final String CDL_DOC_INDIRECT_LOOP = INVALID_RESOLUTION_RESOURCES
            + "extends-indirect-loop.cdl";

    public static final String CDL_DOC_DIRECT_LOOP = INVALID_RESOLUTION_RESOURCES
            + "extends-direct-loop.cdl";


    protected CdlParser parser;

    //made non-validating as too many errors were rising
    public static final boolean PARSER_MUST_VALIDATE = true;
    //public static final boolean PARSER_MUST_VALIDATE = false;

    /**
     * Constructs a test case with the given name.
     */
    protected XmlTestBase(String name) {
        super(name);
    }

    /**
     * create a new catalog, using the local classloader for resolution
     *
     * @return
     */
    protected CdlCatalog createCatalog() {
        ResourceLoader loader;
        loader = new ResourceLoader(this.getClass());
        return new CdlCatalog(loader);
    }

    private void loading(String filename) {
        log(filename);
    }

    protected void log(String message) {
        log.info(message);
    }

    protected CdlDocument load(String filename) throws IOException,
            ParsingException, CdlException {
        CdlDocument doc;
        loading(filename);
        doc = parser.parseResource(filename);
        return doc;
    }

    /**
     * configure the parser
     *
     * @throws SAXException
     */
    protected void initParser() throws SAXException {
        ResourceLoader loader = new ResourceLoader(this.getClass());
        parser = new CdlParser(loader, PARSER_MUST_VALIDATE);
    }

    /**
     * Sets up the fixture by initialising the parser
     */
    protected void setUp() throws Exception {
        super.setUp();
        initParser();
    }

    /**
     * assert that a resource loads as invalid CDL
     *
     * @param resource resource to load
     * @param text     text to look for in the exception (ParsingException or
     *                 CdlParsingException only)
     * @throws Exception for any other type of exception thrown during
     *                   load/parse
     */
    protected void assertInvalidCDL(String resource, String text)
            throws Exception {
        ParseContext context = new ParseContext();
        assertInvalidCDL(context, resource, text);
    }

    /**
     * assert that a resource loads as invalid CDL
     *
     * @param resource resource to load
     * @param text     text to look for in the exception (ParsingException or
     *                 CdlParsingException only)
     * @throws Exception for any other type of exception thrown during
     *                   load/parse
     */
    protected void assertInvalidCDL(ParseContext context,
            String resource,
            String text) throws Exception {
        try {
            if (text == null) {
                text = "";
            }
            CdlDocument doc = load(resource);
            doc.validate();
            doc.parse(context);
            fail("expected a validity failure with " + text);
        } catch (ParsingException e) {
            if (e.getMessage().indexOf(text) < 0) {
                log("expected [" + text + "] but got " + e.toString());
                throw e;
            }
        } catch (CdlException e) {
            if (e.getMessage().indexOf(text) < 0) {
                log("expected [" + text + "] but got " + e.toString());
                throw e;
            }
        }
    }

    /**
     * load a file; assert that it is valid. This
     *
     * @param resource
     * @throws IOException
     * @throws ParsingException
     * @throws CdlXmlParsingException
     */
    protected void assertValidCDL(String resource) throws IOException,
            ParsingException, CdlException {
        ParseContext context = new ParseContext();
        assertValidCDL(context, resource);
    }

    /**
     * load a file; assert that it is valid by initiating parsing
     *
     * @param resource
     * @throws IOException
     * @throws ParsingException
     * @throws CdlXmlParsingException
     */
    protected void assertValidCDL(ParseContext context, String resource)
            throws IOException,
            ParsingException, CdlException {
        CdlDocument cdlDocument = loadValidCDL(resource);
        cdlDocument.parse(context);
    }


    /**
     * Load a valid CDL document
     *
     * @param resource
     * @return
     */
    protected CdlDocument loadValidCDL(String resource) throws IOException,
            ParsingException, CdlException {
        CdlDocument doc = load(resource);
        doc.validate();
        return doc;

    }
}
