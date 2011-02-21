package org.smartfrog.test.unit.sfcore.parser;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.sfcore.annotations.Description;
import org.smartfrog.sfcore.common.LocalSmartFrogDescriptor;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Test wire formatting by creating CDs by hand and checking that they parse
 *
 *
 */

public class TestCDWireFormat extends TestCase {

    static final Log LOG = LogFactory.getLog(TestCDWireFormat.class);

    public LocalSmartFrogDescriptor parse(final String source) throws IOException,
            SmartFrogCompilationException {
        LocalSmartFrogDescriptor descriptor = new LocalSmartFrogDescriptor();
        descriptor.parseText(source);
        if(descriptor.hasErrors()) {
            throw descriptor.createExceptionFromErrors("Parse failure ", source);
        }
        return descriptor;
    }

    public String writeToString(final ComponentDescription cd) throws IOException {
        StringWriter sw = new StringWriter();
        innerWrite(cd, sw);
        return "sfConfig " + sw.toString();
    }

    /**
     * Inner write, does not include the component name
     * @param cd cd to write
     * @param writer writer to write to
     * @throws IOException
     */
    private void innerWrite(final ComponentDescription cd, final Writer writer) throws IOException {
        if (cd instanceof ComponentDescriptionImpl) {
            ComponentDescriptionImpl cdi = (ComponentDescriptionImpl) cd;
            cdi.writeOn(writer);
        } else {
            writer.write(cd.toString());
        }
    }


    public void write(final ComponentDescription cd, final OutputStream writer) throws IOException {
        String s = writeToString(cd);
        PrintStream ps = new PrintStream(writer);
        ps.print(s);
        ps.flush();
    }




    private void roundTrip(final ComponentDescription cd) throws IOException, SmartFrogCompilationException {
        String s = writeToString(cd);
        parse(s);
    }


    private ComponentDescriptionImpl newCD() {
        return new ComponentDescriptionImpl(null, null, false);
    }

    @Description("Verify that integers are handled")
    public void testIntegerRoundTrip() throws Throwable {
        roundTrip("1", 1);
    }

    @Description("Verify that bools are handled")
    public void testboolRoundTrip() throws Throwable {
         roundTrip("true", true);
    }

    @Description("Verify that bools are handled")
    public void testFalseRoundTrip() throws Throwable {
        roundTrip("false", false);
    }

    private ComponentDescription roundTrip(final String key, final Object value)
            throws SmartFrogRuntimeException, IOException, SmartFrogCompilationException {
        ComponentDescription cd = newCD();
        cd.sfReplaceAttribute(key, value);
        roundTrip(cd);
        return cd;
    }

    @Description("Verify that a string with colons in is handled")
    public void testColonsRoundTrip() throws Throwable {
        ComponentDescription cd = newCD();
        cd.sfReplaceAttribute("sup_child_20110217_18:36:46:356", 1);
        cd.sfReplaceAttribute(":", 2);
        roundTrip(cd);
    }

    @Description("Verify that a string with spaces in is handled")
    public void testSpacesRoundTrip() throws Throwable {
        roundTrip("sup_child_20110217_18 36 46 356", 1);
    }

    public void testNullRoundTrip() throws Throwable {
        roundTrip("null", SFNull.get());
    }

    public void testExtendsRoundTrip() throws Throwable {
        roundTrip("extends", SFNull.get());
    }


}
