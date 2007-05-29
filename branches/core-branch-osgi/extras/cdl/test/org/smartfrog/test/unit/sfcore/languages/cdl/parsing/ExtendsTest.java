/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.test.unit.sfcore.languages.cdl.parsing;

import nu.xom.Attribute;
import nu.xom.ParsingException;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.dom.SystemElement;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRecursiveExtendsException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRuntimeException;
import org.smartfrog.sfcore.languages.cdl.resolving.ExtendsContext;
import org.smartfrog.sfcore.languages.cdl.resolving.ExtendsResolver;
import org.smartfrog.test.unit.sfcore.languages.cdl.XmlTestBase;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.EmptyStackException;

/**
 * Test extension created 10-Jun-2005 16:53:50
 */

public class ExtendsTest extends XmlTestBase {

    ExtendsContext extendsContext = new ExtendsContext();
    public static final QName PROPERTYLIST = new QName(
            Constants.XMLNS_DEPLOY_API_TYPES,
            "propertylist");
    public static final QName LOCALONLY = new QName("propertylist");

    private QName a1 = new QName("a1");
    private QName a2 = new QName("a2");
    private QName a3 = new QName("a3");

    public ExtendsTest(String name) {
        super(name);
    }


    public void testExtendsContextWorks() throws Exception {
        extendsContext.enter(PROPERTYLIST);
        extendsContext.enter(LOCALONLY);
        extendsContext.exit(LOCALONLY);
        extendsContext.exit(PROPERTYLIST);
        assertEquals(0, extendsContext.depth());
    }

    public void testExtendsContextRecursion() throws Exception {
        extendsContext.enter(PROPERTYLIST);
        extendsContext.enter(LOCALONLY);
        try {
            extendsContext.enter(PROPERTYLIST);
            fail("expected error");
        } catch (CdlRecursiveExtendsException e) {
            //success!
        }
    }

    public void testExtendsContextRecursion2() throws Exception {
        extendsContext.enter(PROPERTYLIST);
        extendsContext.enter(LOCALONLY);
        try {
            extendsContext.enter(LOCALONLY);
            fail("expected error");
        } catch (CdlRecursiveExtendsException e) {
            //success!
        }
    }

    public void testExtendsContextNull() throws Exception {
        try {
            extendsContext.enter(null);
            fail("expected error");
        } catch (CdlResolutionException e) {
            //success!
        }
    }

    public void testExtendsContextWrongExit() throws Exception {
        extendsContext.enter(PROPERTYLIST);
        try {
            extendsContext.exit(LOCALONLY);
            fail("expected error");
        } catch (CdlRuntimeException e) {
            //success!
        }
    }

    public void testExtendsContextEmptyPop() throws Exception {
        try {
            extendsContext.exit(LOCALONLY);
            fail("expected error");
        } catch (EmptyStackException e) {
            //success!
        }
    }

    public void testExtendsContextNullDoesntNPE() throws Exception {
        extendsContext.enter(PROPERTYLIST);
        try {
            extendsContext.exit(null);
            fail("expected error");
        } catch (CdlRuntimeException e) {
            //success!
        }
    }

    public void testDuplicatePrototypes() throws Exception {
        assertInvalidCDL(EXTENDS_DUPLICATE_NAME,
                ParseContext.ERROR_DUPLICATE_PROTOTYPE);
    }

    /**
     * test is obsolete
     * @throws IOException
     * @throws CdlException
     * @throws ParsingException
     */
    public void NotestExtendsAttributeExtracted() throws IOException, CdlException,
            ParsingException {
        ParseContext context = new ParseContext();
        CdlDocument cdlDocument = parseValidCDL(context, CDL_DOC_EXTENDS_1);
        PropertyList propertyList = cdlDocument.getParseContext()
                .prototypeResolve(a2);
        assertNotNull(propertyList);
        assertNotNull("@extends not found", propertyList.getExtendsName());
    }

    public void testAttributeCopyOnExtends() throws IOException, CdlException,
            ParsingException {
        ParseContext context = new ParseContext();
        CdlDocument cdlDocument = parseValidCDL(context, CDL_DOC_EXTENDS_1);
        PropertyList propertyList = context.prototypeResolve(a2);
        assertNotNull(propertyList);
        Attribute attribute = propertyList.getAttribute("attr");
        assertNotNull("Attribute copy failed", attribute);
        assertEquals("a1", attribute.getValue());
    }

    public void testAttributeOverrideCopyOnExtends() throws IOException, CdlException,
            ParsingException {
        ParseContext context = new ParseContext();
        CdlDocument cdlDocument = parseValidCDL(context, CDL_DOC_EXTENDS_1);

        PropertyList propertyList = cdlDocument.getSystem().getChildTemplateMatching(a3);
        assertNotNull(propertyList);
        Attribute attribute = propertyList.getAttribute("attr");
        assertNotNull("Attribute copy failed", attribute);
        assertEquals("a3", attribute.getValue());
    }

    public void testDirectLoop() throws Exception {
        assertInvalidCDL(EXTENDS_DIRECT_LOOP,
                ExtendsContext.ERROR_RECURSING);
    }

    public void testIndirectLoop() throws Exception {
        assertInvalidCDL(EXTENDS_INDIRECT_LOOP,
                ExtendsContext.ERROR_RECURSING);
    }

    public void testBadReference() throws Exception {
        assertInvalidCDL(EXTENDS_BAD_REFERENCE,
                ExtendsResolver.ERROR_UNKNOWN_TEMPLATE);
    }

    public void testUnknownNamespace() throws Exception {
        //This is a Xom error @ parse time.
        assertInvalidCDL(EXTENDS_UNKNOWN_NAMESPACE,
                "UndeclaredPrefix");
    }

    public void testExtends2() throws IOException, CdlException,
            ParsingException {
        ParseContext context = new ParseContext();
        CdlDocument cdlDocument = parseValidCDL(context, CDL_DOC_EXTENDS_2);
    }


    public void testExtendsAttributeInheritance() throws IOException, CdlException,
            ParsingException {
        ParseContext context = new ParseContext();
        CdlDocument cdlDocument = parseValidCDL(context,
                CDL_DOC_ATTRIBUTE_INHERITANCE);
        //xpath tests to verify the stuff
        SystemElement system = cdlDocument.getSystem();
        PropertyList child = system.getChildTemplateMatching(
                new QName("child"));
        assertHasAttribute(child, "attr_a2");
        assertAttributeValueEquals(child, "attr_a3", "a3");
        assertAttributeValueEquals(child, "attr_a1", "a1");
        assertHasAttribute(child, "attr_system");

    }

    public void testExtendsNonElementChildren() throws IOException, CdlException,
            ParsingException {
        ParseContext context = new ParseContext();
        CdlDocument cdlDocument = parseValidCDL(context,
                CDL_DOC_EXTENDS_NON_ELEMENT_CHILDREN);
    }

    public void testExtendsIndirectRecursive() throws Exception {
        assertInvalidCDL(EXTENDS_INDIRECT_RECURSIVE,
                ExtendsContext.ERROR_RECURSING);
    }

    public void testExtendsRecursiveOverride() throws Exception {
        assertInvalidCDL(EXTENDS_RECURSIVE_OVERRIDE,
                ExtendsContext.ERROR_RECURSING);
    }

    public void testExtendsDocumentation() throws Exception {
        assertInvalidCDL(EXTENDS_DOCUMENTATION,
                ExtendsResolver.ERROR_UNKNOWN_TEMPLATE);
    }

    public void testExtendsDocumentation2() throws Exception {
        assertInvalidCDL(EXTENDS_DOCUMENTATION,
                ExtendsResolver.ERROR_UNKNOWN_TEMPLATE);
    }

    public void testElementsElementPropagation() throws Exception {
        ParseContext context = new ParseContext();
        CdlDocument cdlDocument = parseValidCDL(context,
                CDL_DOC_EXTENDS_ELEMENT_PROPAGATION);
        //xpath tests to verify the stuff
        SystemElement system = cdlDocument.getSystem();
        PropertyList component = lookupChildPropertyList(system,
                "Component",
                "");
        assertElementValueEquals(component, "text");
    }

    public void testElementsNestedElements() throws Exception {
        ParseContext context = new ParseContext();
        CdlDocument cdlDocument = parseValidCDL(context,
                CDL_DOC_EXTENDS_NESTED_ELEMENTS);
        //xpath tests to verify the stuff
        SystemElement system = cdlDocument.getSystem();
        PropertyList component = lookupChildPropertyList(system,
                "Component",
                "");
        PropertyList nested = lookupChildPropertyList(component, "nested");

        assertElementValueEquals(nested, "text");
    }


    public void testExtendsChildExtension() throws Exception {
        ParseContext context = new ParseContext();
        CdlDocument cdlDocument = parseValidCDL(context,
                CDL_DOC_EXTENDS_CHILD_EXTENSION);
        //xpath tests to verify the stuff
        SystemElement system = cdlDocument.getSystem();
        PropertyList component = lookupChildPropertyList(system,
                "Component",
                "");
        PropertyList child = lookupChildPropertyList(component, "child2");
        assertElementValueEquals(child, "text");
    }

    public void testExtendsWithinSystem() throws Exception {
        ParseContext context = new ParseContext();
        assertInvalidCDL(CDL_DOC_EXTENDS_WITHIN_SYSTEM,
                ExtendsResolver.ERROR_UNKNOWN_TEMPLATE);
        //xpath tests to verify the stuff
/*
        CdlDocument cdlDocument = parseValidCDL(context,
                CDL_DOC_EXTENDS_WITHIN_SYSTEM);
        SystemElement system = cdlDocument.getSystem();
        PropertyList component = lookupChildPropertyList(system,
                "Component",
                "");
        PropertyList child = lookupChildPropertyList(component, "child2");
        assertAttributeValueEquals(child, "size", "20");
        assertElementValueEquals(child, "text");
*/
    }


    /**
     * lookup a child property list entry; throw an assertion if it is null
     *
     * @param parent    parent component
     * @param name      local name
     * @param namespace namespace (or "")
     * @return property list
     */
    public PropertyList lookupChildPropertyList(PropertyList parent,
                                                String name,
                                                String namespace) {
        PropertyList child = (PropertyList) parent.getFirstChildElement(name,
                namespace);
        assertNotNull("Failed to resolve child on " + parent, child);
        return child;
    }

    public PropertyList lookupChildPropertyList(PropertyList parent,
                                                String name) {
        return lookupChildPropertyList(parent, name, "");
    }


    public void testElementExtension1() throws Exception {
        ParseContext context = new ParseContext();
        CdlDocument cdlDocument = parseValidCDL(context,
                CDL_DOC_EXTENDS_1);
        //xpath tests to verify the stuff
        SystemElement system = cdlDocument.getSystem();
        PropertyList a3 = system.getChildTemplateMatching("", "a3");
    }

    /**
     * Assert that nested text takes priority
     *
     * @throws Exception
     */
    public void testNestedTextIsOverridden() throws Exception {
        ParseContext context = new ParseContext();
        CdlDocument cdlDocument = parseValidCDL(context,
                CDL_DOC_EXTENDS_NON_ELEMENT_CHILDREN);
        //xpath tests to verify the stuff
        SystemElement system = cdlDocument.getSystem();
        PropertyList component = lookupChildPropertyList(system,
                "Extension",
                "");
        assertElementTextContains(component, "Extension :");
    }


}
