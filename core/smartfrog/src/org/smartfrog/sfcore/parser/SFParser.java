/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.sfcore.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;


/**
 * Implements the Parser interface for SmartFrog parsers. This implementation
 * uses the SFParser class from the various languages to implement the
 * methods. The language to use is either given as part of the method or the
 * default language is used. This is determined by the value of the system
 * property org.smartfrog.sfcore.parser.Language", and this in turn defaults
 * to "sf". The language package is determined by the package prefix
 * constructed from the value of the system property
 * "org.smartfrog.sfcore.parser.LanguagePrefix" and defaults to
 * "org.smartfrog.Languages". The lnaguage to be used is added as part of that
 * package to generate the actual package name.
 *
 */
public class SFParser implements Parser, MessageKeys {


    /**
     * Language name system property key. This is the propBase + Language. This
     * sets the parser to construct if no language is specified. Default is "sf"
     */
    public static String language = SFSystem.getProperty( SmartFrogCoreProperty.parserLanguage, "sf");

    /** Define the language to use to be the default language. */
    private static String theLanguage = language;

    /**
     * The package prefix for all language implementations. This is the
     * propBase + LanguagesPackagePrefix. This sets the parser to construct
     * when getParser is called. Default is "org.smartfrog.sfcore.languages"
     * defined in "SmartFrogCoreProperty.parserPackages".
     */
    public String languagesPrefix = SFSystem.getProperty(
                            SmartFrogCoreProperty.languagesPackagePrefix,
                            SmartFrogCoreProperty.parserCorePackages);

    //
    // Parser
    //

    /* cache the real parser for the language */
    private StreamParser parser;

    /**
     * Constructor for an instance of the parser for the default language.
     *
     * @throws SmartFrogException error creating insatnce of parser
     */
    public SFParser() throws SmartFrogException {
        parser = getParser();
    }

    /**
     * Constructor for an instance of the parser for the given language.
     *
     * @param languageOrUrl the name of the language to use or the url
     *        with suffixed by language extension.
     *
     * @throws SmartFrogException error crearing instance of parser
     */
    public SFParser(String languageOrUrl) throws SmartFrogException {
        theLanguage = getLanguageFromUrl(language);
        parser = getParser();
    }

    /* Constructs a parser for the given language */
    private StreamParser getParser() throws SmartFrogException {
        /**
         * Referebce to parser class.
         */
        Class parserClass;

        try {
            parserClass = SFClassLoader.forName(languagesPrefix + "." +
                    theLanguage + ".SFParser");

            return (StreamParser) parserClass.newInstance();
        } catch (ClassNotFoundException cnfexcp) {
            throw new SmartFrogRuntimeException(MessageUtil.formatMessage(
                    MSG_CLASS_NOT_FOUND,
                    languagesPrefix + "." + theLanguage + ".SFParser"),
                cnfexcp);
        } catch (InstantiationException instexcp) {
            throw new SmartFrogRuntimeException(MessageUtil.formatMessage(
                    MSG_INSTANTIATION_ERROR,
                    languagesPrefix + "." + theLanguage + ".SFParser"),
                instexcp);
        } catch (IllegalAccessException illaexcp) {
            throw new SmartFrogRuntimeException(MessageUtil.formatMessage(
                    MSG_ILLEGAL_ACCESS,
                    languagesPrefix + "." + theLanguage + ".SFParser",
                    "newInstance()"), illaexcp);
        }
    }

    /**
     * Parses component(s) from an input stream. Returns a root component which
     * contains the parsed components. Includes should be handled by some
     * default include handler.
     *
     * @param is input stream to parse and compile from
     *
     * @return root component containing parsed component(s)
     *
     * @exception SmartFrogParseException error parsing stream
     */
    public Phases sfParse(InputStream is) throws SmartFrogParseException {
        return parser.sfParse(is);
    }


    /**
     * Parses component(s) from an resource url. Returns a root component which
     * contains the parsed components. Includes should be handled by some
     * default include handler.
     *
     * @param url to resource to parse and compile from
     *
     * @return Phases root component containing parsed component(s)
     *
     * @exception SmartFrogParseException error parsing stream
     */

     public Phases sfParseResource(String url) throws SmartFrogParseException {
         InputStream is=null;
         try {
             is = SFClassLoader.getResourceAsStream(url);
             if (is==null) {
                 throw new SmartFrogParseException(
                 MessageUtil.formatMessage(MSG_URL_TO_PARSE_NOT_FOUND,url));
//                     MessageUtil.formatMessage(MSG_INPUTSTREAM_NULL)+
//                     ". " +
//                     MessageUtil.formatMessage(MSG_LOADING_URL,url));
             }
             return sfParse(is);
         } catch (SmartFrogParseException spex){
             throw spex;
         } catch (Throwable thr) {
             throw new SmartFrogParseException(MessageUtil.
                     formatMessage(MSG_ERR_PARSE), thr);
         } finally {
             if (is!=null) {
                 try {
                     is.close();
                 } catch (IOException ignored) {
                 //TODO
                 }
             }
         }
     }


    /**
     * Parses component(s) from a string. Returns the root component. This is a
     * utility access method which currently does not support localization.
     *
     * @param str string to parse
     *
     * @return root component containing parsed component(s)
     *
     * @exception SmartFrogParseException error parsing string
     */
    public Phases sfParse(String str) throws SmartFrogParseException {
        return sfParse(new ByteArrayInputStream(str.getBytes()));
    }

    /**
     * Parses a reference from an input stream. Used by components and
     * developers to quickly build references from a string (eg. sfResolve in
     * Prim)
     *
     * @param is input stream to parse for a reference
     *
     * @return parsed reference
     *
     * @exception SmartFrogParseException failed to parse reference
     */
    public Reference sfParseReference(InputStream is) throws SmartFrogParseException {
        return parser.sfParseReference(is);
    }

    /**
     * Parses a reference from a string. Used by components and developers to
     * quickly build references from a string (eg. sfResolve in Prim)
     *
     * @param txt textual representation of the reference
     *
     * @return parsed reference
     *
     * @exception SmartFrogParseException failed to parse reference
     */
    public Reference sfParseReference(String txt) throws SmartFrogParseException {
        return parser.sfParseReference(new ByteArrayInputStream(txt.getBytes()));
    }


    /**
      * Gets language from the URL
      *
      * @param url URL passed to application
      *
      * @return Language string
      *
      * @throws SmartFrogParseException In case any error while getting the
      *         language string
      */
     public static String getLanguageFromUrl(String url)
         throws SmartFrogParseException {
         if (url == null) {
             throw new SmartFrogParseException (MessageUtil.formatMessage(
                     MSG_NULL_URL));
         }

         int i = url.lastIndexOf('.');

         return url.substring(i + 1).trim();

     }

}
