package org.smartfrog.services.xunit.listeners.xml;

import org.smartfrog.services.xunit.listeners.TestIndex;

/**
 * interface for the test index document
 */
public interface XmlTestIndex extends TestIndex {

    /**
     * name of the output directory
     * {@value}
     */
    String ATTR_OUTPUT_DIRECTORY="outputDirectory";

    /**
     * filename to use
     * {@value}
     */
    String ATTR_FILENAME="filename";

    /**
     * How often should the index be created
     * {@value}
     */
    String ATTR_PUBLISH_FREQUENCY="publishFrequency";
}
