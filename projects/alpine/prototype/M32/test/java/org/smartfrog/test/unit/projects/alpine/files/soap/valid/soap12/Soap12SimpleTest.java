package org.smartfrog.test.unit.projects.alpine.files.soap.valid.soap12;

import org.smartfrog.test.unit.projects.alpine.ValidTestBase;
import org.smartfrog.projects.alpine.om.soap11.Body;
import org.smartfrog.projects.alpine.om.soap11.Envelope;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.XPathContextFactory;
import org.smartfrog.projects.alpine.xmlutils.NodesIterator;
import nu.xom.XPathContext;
import nu.xom.Nodes;
import nu.xom.Node;

/**

 */
public class Soap12SimpleTest extends ValidTestBase {
    private Body body;
    private Envelope envelope;
    private XPathContext xpathContext;
    public static final String NODE_VALUE = "DEF";

    public Soap12SimpleTest(String name) {
        super(name);
    }

    /**
     * Implement this
     *
     * @return the resource to test
     */
    protected String getTestResource() {
        return SOAP12_SIMPLE;
    }

    /**
     * Sets up the fixture by initialising the parser and then loading in the resource specified by {@link
     * #getTestResource()}
     */
    protected void setUp() throws Exception {
        super.setUp();
        MessageDocument doc = getDocument();
        envelope = getEnvelope(doc);
        body = envelope.getBody();
        assertNotNull("there is no body to this message body",body);
        xpathContext = XPathContextFactory.create();
        xpathContext.addNamespace("m", URI_EXAMPLE_ORG_1);
    }

    /**
     * first xpath test
     * @throws Exception
     */
    public void testXpath() throws Exception {
        MessageDocument doc = getDocument();
        Body body;
        Envelope envelope = getEnvelope(doc);
        body = envelope.getBody();
        Nodes nodes = body.query("m:GetLastTradePriceDetailed/m:symbol", xpathContext);
        assertEquals(1, nodes.size());
        Node n1 = nodes.get(0);
        String value = n1.getValue();
        assertEquals(NODE_VALUE, value);
        //test our nodes iterator
        int count=0;
        NodesIterator it = new NodesIterator(nodes);
        for (Node n : it) {
            count++;
        }
        assertEquals("iterated once",1,count);
    }

    public void testXpath2() throws Exception {
        for(Node n: body.xpath("m:GetLastTradePriceDetailed/m:symbol", xpathContext))
        {
            String value = n.getValue();
            assertEquals(NODE_VALUE, value);
        }
    }

    /**
     * get the envelope
     * @param doc
     */
    public Envelope getEnvelope(MessageDocument doc) {
        return doc.getEnvelope();
    }


}
