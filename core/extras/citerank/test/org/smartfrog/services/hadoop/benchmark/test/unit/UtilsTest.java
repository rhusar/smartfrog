package org.smartfrog.services.hadoop.benchmark.test.unit;

import junit.framework.TestCase;
import org.smartfrog.services.hadoop.benchmark.citerank.Utils;

/**
 *
 */
public class UtilsTest extends TestCase {


    public void testEmptyURL() {
        assertEquals("", Utils.createCitationURL("","something"));
    }

    public void testSimple() {
        assertEquals("/something.html", Utils.createCitationURL("/","something"));
    }
    

}
