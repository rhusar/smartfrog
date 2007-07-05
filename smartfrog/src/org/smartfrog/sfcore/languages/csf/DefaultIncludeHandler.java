package org.smartfrog.sfcore.languages.csf;

import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;
import org.smartfrog.sfcore.languages.sf.IncludeHandler;

import java.util.Vector;

/**
 * This is the default include handler for the SmartFrog parser. This simply
 * creates a new parser and returns an AttributeList. The include name is
 * located using SFSystem.stringToURl which will try to use the name as a URL,
 * or make a file URL to locate it. Otherwise the classloader's
 * getResourceAsStream is used to locate the include. Subclasses can override
 * this to do more sophisticated include storage. The format of an
 * AttributeList as returned by DefaultParser is a vector of Object[] with
 * element 0 the name of the attribute and element 1 the value.
 *
 */
public class DefaultIncludeHandler implements IncludeHandler {

    String baseCodebase;

    /**
     * Parses given include. This implementation constructs a new DefaultParser
     * on the result of openInclude and uses the AttributeList methods to
     * construct the vector of attributes
     *
     * @param include include file to parse
     * @param codebase an optional codebase where the include may be found. If null, use the default codebase
     *
     * @return vector of attribute name X value pairs
     *
     * @exception Exception error while locating or parsing include
     */
    public Vector parseInclude(String include, SFReference codebase) throws Exception {
        return (new org.smartfrog.sfcore.languages.csf.DefaultParser(
                org.smartfrog.sfcore.parser.SFParser.openInclude(include, codebase),
                new DefaultIncludeHandler())).AttributeList();
    }

}
