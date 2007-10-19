package org.smartfrog.services.xunit.listeners.xml;

import org.smartfrog.services.xunit.listeners.TestIndex;

/**

 */
public interface XmlTestIndex extends TestIndex {

    String ATTR_OUTPUT_DIRECTORY="outputDirectory";

    String ATTR_FILENAME="filename";

    String ATTR_PUBLISH_FREQUENCY="publishFrequency";
}
