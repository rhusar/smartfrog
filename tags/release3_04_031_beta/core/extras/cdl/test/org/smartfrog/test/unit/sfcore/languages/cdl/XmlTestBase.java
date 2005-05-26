package org.smartfrog.test.unit.sfcore.languages.cdl;

import junit.framework.TestCase;
import org.smartfrog.sfcore.languages.cdl.CdlCatalog;
import org.smartfrog.services.xml.utils.ResourceLoader;

/**
 */
public abstract class XmlTestBase extends TestCase {
    private final static String RESOURCES = "files/cdl/";
    private final static String INVALID_RESOURCES = RESOURCES + "invalid/";
    private final static String VALID_RESOURCES = RESOURCES + "valid/";
    public static final String WRONG_NAMESPACE_TEXT = "Cannot find the declaration of element 'cdl:cdl'";
    public static final String CDL_DOC_MINIMAL = VALID_RESOURCES + "minimal.cdl";
    /**
     * This is our list of valid documents. These must all parse
     */
    protected final static String[] VALID_CDL = {
        CDL_DOC_MINIMAL,
        VALID_RESOURCES + "extra-elements.cdl",
        VALID_RESOURCES + "webserver.cdl",
        VALID_RESOURCES + "webserver-no-namespace.cdl",
        VALID_RESOURCES + "webserver-default-namespace.cdl",
        VALID_RESOURCES + "documented.cdl",
        VALID_RESOURCES + "references-1.cdl",
        VALID_RESOURCES + "references-2.cdl",
        VALID_RESOURCES + "references-3.cdl",
        VALID_RESOURCES + "expression-1.cdl",
        VALID_RESOURCES + "type-1.cdl",
        VALID_RESOURCES + "lazy-1.cdl",
        VALID_RESOURCES + "lazy-2.cdl",
        VALID_RESOURCES + "parameterization-1.cdl",
        VALID_RESOURCES + "full-example-1.cdl",
        VALID_RESOURCES + "full-example-2.cdl",
        VALID_RESOURCES + "full-example-3.cdl",
    };
    public static final String CDL_DOC_WRONG_ELT_ORDER = INVALID_RESOURCES +
            "wrong_elt_order.cdl";
    public static final String CDL_DOC_WRONG_ROOT_ELT_TYPE = INVALID_RESOURCES +
            "wrong_root_elt_type.cdl";
    public static final String CDL_DOC_DUPLICATE_NAMES = INVALID_RESOURCES +
            "duplicate-names.cdl";
    public static final String CDL_DOC_WRONG_NAMESPACE = INVALID_RESOURCES +
            "wrong_doc_namespace.cdl";

    /**
     * Constructs a test case with the given name.
     */
    protected XmlTestBase(String name) {
        super(name);
    }

    /**
     * create a new catalog, using the local classloader for resolution
     * @return
     */
    protected CdlCatalog createCatalog() {
        ResourceLoader loader;
        loader = new ResourceLoader(this.getClass());
        return new CdlCatalog(loader);
    }
}
