package org.smartfrog.services.deployapi.test.unit;

import junit.framework.TestCase;
import org.smartfrog.services.deployapi.system.LifecycleStateEnum;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import nu.xom.Element;
import nu.xom.Node;

/**
 */
public class LifecycleStateTest extends TestCase {


    public void testXmlRoundTrip() throws Exception {
        for(LifecycleStateEnum state: LifecycleStateEnum.values()) {
            Element e=state.toCmpState();
            SoapElement base = makeParent(e);
            LifecycleStateEnum back=LifecycleStateEnum.extract(base);
        }
    }

    public void testNoParent() throws Exception {
        SoapElement base = makeParent(null);
        try {
            LifecycleStateEnum.extract(base);
            fail("should have bailed out");
        } catch (BaseException e) {
            //expected
        }
    }

    public void testUnknownState() throws Exception {
        SoapElement e = LifecycleStateEnum.undefined.toCmpState();
        Element child = (Element) e.getChild(0);
        child.setLocalName("madeupname");
        SoapElement base = makeParent(e);
        try {
            LifecycleStateEnum.extract(base);
            fail("should have bailed out");
        } catch (BaseException ex) {
            //expected
        }
    }

    private SoapElement makeParent(Element e) {
        SoapElement base =new SoapElement("parent",null);
        if(e!=null) {
            base.appendChild(e);
        }
        return base;
    }
}

