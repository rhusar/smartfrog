package org.smartfrog.sfcore.languages.csf;

import org.smartfrog.sfcore.languages.sf.IncludeHandler;
import org.smartfrog.sfcore.security.SFClassLoader;

import java.util.Vector;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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
     * Constructor.
     */
    public DefaultIncludeHandler() {
        baseCodebase = null;
    }

    /**
     * Constructor.
     *
     * @param baseCodebase the codebase for this include handler to which will be appended the codebase passed in the
     * parseIncldue method.
     */
    public DefaultIncludeHandler(String baseCodebase) {
        this.baseCodebase = baseCodebase;
    }

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
    public Vector parseInclude(String include, String codebase) throws Exception {
        return (new org.smartfrog.sfcore.languages.csf.DefaultParser(openInclude(include, codebase), new DefaultIncludeHandler(actualCodebase(codebase)))).AttributeList();
    }

    /**
     * Locate the include and returns an input stream on it. This uses
     * SFSystem.stringToURL to check whether include is a URL or a file. On
     * failure it tries to use standard getResourceAsStream to get the inlude
     * of the classpath. Subclasses can override to provide additional means
     * of locating includes.
     *
     * @param include include to locate
     * @param codebase an optional codebase where hte include may be found. If null, use the default code base
     *
     * @return reader (UTF-8) on located include
     *
     * @exception Exception failed to locate or open include
     */
    protected Reader openInclude(String include, String codebase) throws Exception {
        InputStream is = null;

        is = SFClassLoader.getResourceAsStream(include, actualCodebase(codebase), true);

        if (is == null) {
            throw new Exception("Include file: " + include + " not found");
        }

        return new InputStreamReader(is, "utf-8");
    }

    /**
     * build a concatenated codebase from the base codebase and the codebase passed as a parameter
     *
     *  @param codebase the codeebase to concatenate to the base. May be null.
     */
    protected String actualCodebase(String codebase) {
        String actualCodebase = null;
        if ((baseCodebase != null) && (codebase != null)) actualCodebase = baseCodebase + " " + codebase;
        else if (baseCodebase != null) actualCodebase = baseCodebase;
        else if (codebase != null) actualCodebase = codebase;
        return actualCodebase;
    }
}
