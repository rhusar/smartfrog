package org.smartfrog.services.deployapi.test.unit;

import junit.framework.TestCase;
import nu.xom.Element;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.services.cddlm.cdl.base.LifecycleStateEnum;
import org.smartfrog.services.deployapi.system.Utils;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRuntimeException;

/**
 */
public class LifecycleStateTest extends TestCase {


    public void testXmlRoundTrip() throws Exception {
        for(LifecycleStateEnum state: LifecycleStateEnum.values()) {
            Element e= Utils.toCmpState(state);
            SoapElement base = makeParent(e);
            LifecycleStateEnum back= Utils.parseCmpState(base);
        }
    }

    public void testNoParent() throws Exception {
        SoapElement base = makeParent(null);
        try {
            Utils.parseCmpState(base);
            fail("should have bailed out");
        } catch (BaseException e) {
            //expected
        }
    }

    public void testUnknownState() throws Exception {
        SoapElement e = Utils.toCmpState(LifecycleStateEnum.undefined);
        Element child = (Element) e.getChild(0);
        child.setLocalName("madeupname");
        SoapElement base = makeParent(e);
        try {
            Utils.parseCmpState(base);
            fail("should have bailed out");
        } catch (CdlRuntimeException ex) {
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

