package org.smartfrog.test.unit.sfcore;

import junit.framework.TestCase;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.reference.Reference;

public class RelativeReferenceTest extends TestCase {
    public void testAlreadyRelative() throws SmartFrogResolutionException {
        Reference alreadyRelative = new Reference("HERE foo");
        Reference base = Reference.fromString("HOST foo.net");

        Reference madeRelative = alreadyRelative.makeRelative(base);

        assertEquals("Making an already relative reference relative should not change it",
                alreadyRelative, madeRelative);
        // The implementation has the right to return the same object if no change
    }

    public void testDifferentPrefixDoesNotChange() throws SmartFrogResolutionException {
        Reference base = Reference.fromString("HOST bar.net:rootProcess");
        Reference absoluteButOtherBase = Reference.fromString("HOST foo.net:rootProcess:quux");

        Reference madeRelative = absoluteButOtherBase.makeRelative(base);

        assertEquals("Making an absolute reference relative to an absolute reference that does not prefix it should not change it",
                absoluteButOtherBase, madeRelative);
    }

    public void testSamePrefixMakesRelative() throws SmartFrogResolutionException {
        Reference base = Reference.fromString("HOST foo.net:fooProcess:bar");
        Reference toBeChanged = Reference.fromString("HOST foo.net:fooProcess:bar:quux:foobar");

        Reference madeRelative = toBeChanged.makeRelative(base);

        assertEquals(Reference.fromString("HERE quux:foobar"), madeRelative);
    }

    public void testShorterThanBaseDoesNotChange() throws SmartFrogResolutionException {
        Reference shorter = Reference.fromString("HOST foo.net:fooProcess:bar");
        Reference longer = Reference.fromString("HOST foo.net:fooProcess:bar:quux:foobar");

        Reference madeRelative = shorter.makeRelative(longer);

        assertEquals(shorter, madeRelative);
    }

    public void testBaseNotAbsoluteStillChanges() throws SmartFrogResolutionException {
        Reference notAbsoluteBase = Reference.fromString("HERE foo");
        Reference relative = Reference.fromString("HERE foo:quux:bar");

        assertEquals(Reference.fromString("HERE quux:bar"), relative.makeRelative(notAbsoluteBase));
    }

}
