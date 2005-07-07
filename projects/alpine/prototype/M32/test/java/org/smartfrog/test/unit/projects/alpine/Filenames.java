package org.smartfrog.test.unit.projects.alpine;

/**
 
 */
public interface Filenames {

    public static final String RESOURCES = "files/alpine/soap/";
    public static final String VALID_RESOURCES = RESOURCES + "valid/";
    public static final String INVALID_RESOURCES = RESOURCES + "invalid/";
    public static final String VALID_SOAP_11 = VALID_RESOURCES + "soap11/";
    public static final String INVALID_SOAP_11 = INVALID_RESOURCES + "soap11/";
    public static final String VALID_WSI = VALID_RESOURCES + "wsi/";
    public static final String INVALID_WSI = INVALID_RESOURCES + "wsi/";

    /**
     * Test file
     * {@value}
     */
    public static final String SOAP_MUST_UNDERSTAND_HEADER = VALID_SOAP_11 
            + "must-understand-header.xml";

    /**
     * Test file {@value}
     */
    public static final String SOAP_MUST_UNDERSTAND_ZERO = VALID_SOAP_11
            + "must-understand-zero.xml";

    /**
     * Test file {@value}
     */
    public static final String SOAP_RESPONSE_FAULT_MUST_UNDERSTAND = VALID_SOAP_11
            + "response-fault-mustunderstand.xml";
    /**
     * Test file {@value}
     */
    public static final String SOAP_RPCENC = VALID_SOAP_11
            + "rpcenc.xml";
    /**
     * Test file {@value}
     */
    public static final String SOAP_SIMPLE = VALID_SOAP_11
            + "simple.xml";
    /**
     * Test file {@value}
     */
    public static final String SOAP_FAULT_INTERNAL = VALID_SOAP_11
            + "soap-fault-internal.xml";
    /**
     * Test file {@value}
     */
    public static final String SOAP_HTML_MESSAGE = VALID_SOAP_11
            + "soap-html-message.xml";
    /**
     * Test file {@value}
     */
    public static final String SOAP_HTML_RESPONSE = VALID_SOAP_11
            + "soap-html-response.xml";
    /**
     * Test file {@value}
     */
    public static final String WSI_FAULT_ENVELOPE = VALID_WSI
            + "fault-envelope.xml";
    /**
     * Test file {@value}
     */
    public static final String WSI_FAULT_LOCAL_ELEMENTS = VALID_WSI
            + "fault-local-elements.xml";
    /**
     * Test file {@value}
     */
    public static final String WSI_FAULT_QNAME = VALID_WSI
            + "fault-qname.xml";
    /**
     * Test file {@value}
     */
    public static final String WSI_FAULT_GOOD = VALID_WSI
            + "wsi-fault-good.xml";
    /**
     * Test file {@value}
     */
    public static final String WSI_VALID_1 = VALID_WSI
            + "wsi-valid1.xml";
}
