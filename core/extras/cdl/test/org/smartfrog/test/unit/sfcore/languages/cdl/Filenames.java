package org.smartfrog.test.unit.sfcore.languages.cdl;

/**
 */
public interface Filenames {
    String RESOURCES = "test/cdl/";
    String VALID_RESOURCES = RESOURCES + "valid/";
    String INVALID_RESOURCES = RESOURCES + "invalid/";
    String RESOLUTION_RESOURCES = VALID_RESOURCES +
            "resolution/";
    String INVALID_RESOLUTION_RESOURCES = INVALID_RESOURCES +
            "resolution/";
    String WRONG_NAMESPACE_TEXT = "Cannot find the declaration of element 'cdl:cdl'";
    /**
     * {@value}
     */
    String CDL_DOC_MINIMAL = VALID_RESOURCES +
            "minimal.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_FULL_EXAMPLE_1 = VALID_RESOURCES +
            "full-example-1.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_FULL_EXAMPLE_2 = VALID_RESOURCES +
            "full-example-2.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_FULL_EXAMPLE_3 = VALID_RESOURCES +
            "full-example-3.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_WEBSERVER = VALID_RESOURCES +
            "webserver.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_WEBSERVER_NO_NAMESPACE = VALID_RESOURCES +
            "webserver-no-namespace.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_WEBSERVER_DEFAULT_NAMESPACE = VALID_RESOURCES +
            "webserver-default-namespace.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_DOCUMENTED = VALID_RESOURCES +
            "documented.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_REFERENCES_1 = VALID_RESOURCES +
            "references-1.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_REFERENCES_2 = VALID_RESOURCES +
            "references-2.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_REFERENCES_3 = VALID_RESOURCES +
            "references-3.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_EXPRESSION_1 = VALID_RESOURCES +
            "expression-1.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_EXPRESSION_DUPLICATE = INVALID_RESOURCES +
            "expression-duplicate-variables.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_TYPE_1 = VALID_RESOURCES + "type-1.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_LAZY_1 = VALID_RESOURCES + "lazy-1.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_LAZY_2 = VALID_RESOURCES + "lazy-2.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_PARAMETERIZATION_1 = VALID_RESOURCES +
            "parameterization-1.cdl";
    /**
     * {@value}
     */
    String CDL_DOC_EXTRA_ELEMENTS = VALID_RESOURCES +
            "extra-elements.cdl";
    /**
     * extends gets tested {@value}
     */
    String CDL_DOC_EXTENDS_1 = RESOLUTION_RESOURCES +
            "extends-1.cdl";

    String CDL_DOC_EXTENDS_2 = RESOLUTION_RESOURCES +
            "extends-2.cdl";
    String CDL_DOC_EXTENDS_NAMESPACES_1 = RESOLUTION_RESOURCES +
            "extends-namespaces-1.cdl";
    String CDL_DOC_EXTENDS_CHILD_EXTENSION = RESOLUTION_RESOURCES +
            "extends-child-extension.cdl";
    String CDL_DOC_WRONG_ELT_ORDER = INVALID_RESOURCES +
            "wrong_elt_order.cdl";
    String CDL_DOC_WRONG_ROOT_ELT_TYPE = INVALID_RESOURCES +
            "wrong_root_elt_type.cdl";
    String CDL_DOC_WRONG_NAMESPACE = INVALID_RESOURCES +
            "wrong_doc_namespace.cdl";
    String EXTENDS_DOC_BAD_NAMESPACE = INVALID_RESOLUTION_RESOURCES
            + "extends-bad-namespace.cdl";
    String EXTENDS_BAD_REFERENCE = INVALID_RESOLUTION_RESOURCES
            + "extends-bad-reference.cdl";
    String EXTENDS_DUPLICATE_NAME = INVALID_RESOLUTION_RESOURCES
            + "extends-duplicate-name.cdl";
    String EXTENDS_INDIRECT_LOOP = INVALID_RESOLUTION_RESOURCES
            + "extends-indirect-loop.cdl";
    String EXTENDS_DIRECT_LOOP = INVALID_RESOLUTION_RESOURCES
            + "extends-direct-loop.cdl";
    String EXTENDS_UNKNOWN_LOCALNAME = INVALID_RESOLUTION_RESOURCES
            + "extends-unknown-localname.cdl";
    String EXTENDS_UNKNOWN_NAMESPACE = INVALID_RESOLUTION_RESOURCES
            + "extends-unknown-namespace.cdl";
    String EXTENDS_RECURSIVE_EXTENDS = INVALID_RESOLUTION_RESOURCES
            + "extends-recursive-extends.cdl";
    String EXTENDS_DEFAULT_NAMESPACE_NOT_IN_EXTENDS = INVALID_RESOLUTION_RESOURCES
            + "extends-default-namespace-not-in-extends.cdl";
    String EXTENDS_SYSTEM_EXTENDS = INVALID_RESOLUTION_RESOURCES
            + "extends-default-namespace-not-in-extends.cdl";

}
