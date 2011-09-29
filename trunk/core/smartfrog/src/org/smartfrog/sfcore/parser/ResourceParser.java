package org.smartfrog.sfcore.parser;

import org.smartfrog.sfcore.common.SmartFrogParseException;


/**
 * The interface for parsing of resources directly, without first having to wrap them into readers.
 * The resources are assumed to be UTF-8 encoded.
 *
 */
public interface ResourceParser {

    /**
     * Parses component(s) from an resource url. Returns a root component which
     * contains the parsed components. Includes should be handled by some
     * default include handler.
     *
     * @param url to resource to parse and compile from, and whose contents
     * are utf-8 encoded
     *
     * @return Phases root component containing parsed component(s)
     *
     * @exception org.smartfrog.sfcore.common.SmartFrogParseException error parsing stream
     */

     public Phases sfParseResource(String url) throws SmartFrogParseException;



    /**
     * Parses component(s) from an resource url. Returns a root component which
     * contains the parsed components. Includes should be handled by some
     * default include handler.
     *
     * @param url to resource to parse and compile from, and whose contents
     * are utf-8 encoded
     * @param codebase suggested codebase for the classloader
     *
     * @return Phases root component containing parsed component(s)
     *
     * @exception SmartFrogParseException error parsing stream
     */

     public Phases sfParseResource(String url, String codebase) throws SmartFrogParseException;

}
