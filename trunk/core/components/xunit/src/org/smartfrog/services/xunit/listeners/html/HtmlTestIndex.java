package org.smartfrog.services.xunit.listeners.html;

import org.smartfrog.services.xunit.listeners.xml.XmlTestIndex;

/**

 */
public interface HtmlTestIndex extends XmlTestIndex {


    String ATTR_TITLE = HtmlTestListenerFactory.ATTR_TITLE;

    String ATTR_CSS_RESOURCE = HtmlTestListenerFactory.ATTR_CSS_RESOURCE;

    String ATTR_CSS_URL = HtmlTestListenerFactory.ATTR_CSS_URL;

    String ATTR_CSS_DATA = HtmlTestListenerFactory.ATTR_CSS_DATA;
}
