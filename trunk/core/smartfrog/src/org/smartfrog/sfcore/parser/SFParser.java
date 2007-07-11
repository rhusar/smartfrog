/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

import java.io.*;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogException;


import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFClassLoader;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;

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
    private String theLanguage = language;

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
    private StreamLanguageParser parser;

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
     *        with suffixed by language extension.  if null the uses sf language by default
     *
     * @throws SmartFrogException error crearing instance of parser
     */
    public SFParser(String languageOrUrl) throws SmartFrogException {
        if (languageOrUrl!=null) {
         theLanguage = getLanguageFromUrl(languageOrUrl);
        } else {
            theLanguage = getLanguageFromUrl("sf");
        }
             
        parser = getParser();
    }

    /* Constructs a parser for the given language */
    private StreamLanguageParser getParser() throws SmartFrogException {
        /**
         * Reference to parser class.
         */
        Class parserClass;

        try {
            parserClass = SFClassLoader.forName(languagesPrefix + "." +
                    theLanguage + ".SFParser");

            return (StreamLanguageParser) parserClass.newInstance();
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


   /*
   * ************************** Resource parsing **********************************
   */


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
     * @exception SmartFrogParseException error parsing stream
     */

     public Phases sfParseResource(String url) throws SmartFrogParseException {
         return sfParseResource(url,null);
     }



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

     public Phases sfParseResource(String url, String codebase) throws SmartFrogParseException {
         InputStream is=null;
         try {
             is = SFClassLoader.getResourceAsStream(url,codebase, true);
             if (is==null) {
                 throw new SmartFrogParseException(
                 MessageUtil.formatMessage(MSG_URL_TO_PARSE_NOT_FOUND,url));
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

   /*
   * ************************** Core parsing **********************************
   */

   /**
    * Parses component(s) from a reader. Returns a root component which
    * contains the parsed components. Includes should be handled by some
    * default include handler.
    * This must be used if non utf-8 encoded streams are to be used.
    *
    * @param reader to parse and compile from
    *
    * @return root component containing parsed component(s)
    *
    * @exception SmartFrogParseException error parsing stream
    */
   public Phases sfParse(Reader reader) throws SmartFrogParseException {
         return parser.sfParse(reader ,null);
   }

   /**
    * Parses component(s) from a reader. Returns a root component which
    * contains the parsed components. Includes should be handled by some
    * default include handler.
    * This must be used if non utf-8 encoded streams are to be used.
    *
    * @param reader to parse and compile from
    * @param codebase an optional codebase where the include may be found. If null, use the default code base
    *
    * @return root component containing parsed component(s)
    *
    * @exception SmartFrogParseException error parsing stream
    */
   public Phases sfParse(Reader reader, String codebase) throws SmartFrogParseException {
         return parser.sfParse(reader, codebase);
   }


   /**
    * Parses component(s) from an input stream. Returns a root component which
    * contains the parsed components. Includes should be handled by some
    * default include handler.
    *
    * @param is utf-8 encoded input stream to parse and compile from
    *
    * @return root component containing parsed component(s)
    *
    * @exception SmartFrogParseException error parsing stream
    */
   public Phases sfParse(InputStream is) throws SmartFrogParseException {
      try {
         return sfParse(new InputStreamReader(is, "utf-8"),null);
      } catch (UnsupportedEncodingException e) {
         throw new SmartFrogParseException("error in encoding of stream", e);
      }
   }

   /**
    * Parses component(s) from an input stream. Returns a root component which
    * contains the parsed components. Includes should be handled by some
    * default include handler.
    *
    * @param is utf-8 encoded input stream to parse and compile from
    * @param codebase an optional codebase where the include may be found. If null, use the default code base
    *
    * @return root component containing parsed component(s)
    *
    * @exception SmartFrogParseException error parsing stream
    */
   public Phases sfParse(InputStream is, String codebase) throws SmartFrogParseException {
      try {
         return sfParse(new InputStreamReader(is, "utf-8"), codebase);
      } catch (UnsupportedEncodingException e) {
         throw new SmartFrogParseException("error in encoding of stream", e);
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
          return sfParse(str, null);
    }

    /**
     * Parses component(s) from a string. Returns the root component. This is a
     * utility access method which currently does not support localization.
     *
     * @param str string to parse
     * @param codebase suggested codebase for the classloader
     *
     * @return root component containing parsed component(s)
     *
     * @exception SmartFrogParseException error parsing string
     */
    public Phases sfParse(String str, String codebase) throws SmartFrogParseException {
          return sfParse(new StringReader(str),codebase);
    }


   /*
   * ************************** Reference parsing **********************************
   */


   /**
    * Parses a reference from a reader. Used by components and
    * developers to quickly build references from a string (eg. sfResolve in
    * Prim). It applies the ReferencePhases conversion method to Referemnce.
    * This must be used if non utf-8 encoded streams are to be used.
    *
    * @param reader reader to parse for a reference
    *
    * @return parsed reference
    *
    * @exception SmartFrogCompilationException failed to parse reference
    */
   public Reference sfParseReference(Reader reader) throws SmartFrogCompilationException {
      ReferencePhases rp = parser.sfParseReference(reader);
      return rp.sfAsReference();
   }

    /**
     * Parses a reference from an input stream. Used by components and
     * developers to quickly build references from a string (eg. sfResolve in
     * Prim). It applies the ReferencePhases conversion method to Referemnce.
     *
     * @param is utf-8 encoded input stream to parse for a reference
     *
     * @return parsed reference
     *
     * @exception SmartFrogCompilationException failed to parse reference
     */
    public Reference sfParseReference(InputStream is) throws SmartFrogCompilationException {
       try {
          return sfParseReference(new InputStreamReader(is, "utf-8"));
       } catch (UnsupportedEncodingException e) {
          throw new SmartFrogParseException("error in encoding of stream", e);
       }
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
    public Reference sfParseReference(String txt) throws SmartFrogCompilationException {
          return sfParseReference(new StringReader(txt));
    }


   /*
   * ************************** Any value parsing **********************************
   */

    /**
     * Parses any value from a reader. (the meaning of "any" is language dependant)
     * This must be used if non utf-8 encoded streams are to be used.
     *
     * @param reader reader to parse for a value
     *
     * @return parsed value
     *
     * @exception SmartFrogParseException failed to parse any value
     */
    public Object sfParseAnyValue(Reader reader) throws SmartFrogCompilationException {
      Object o = parser.sfParseAnyValue(reader);
      if (o instanceof ReferencePhases) {
          o = ((ReferencePhases)o).sfAsReference();
       } else if (o instanceof SFComponentDescription) {
          o = ((SFComponentDescription)o).sfAsComponentDescription();
       }
      return o;
    }

    /**
     * Parses any value from an input stream. (the meaning of "any" is language dependant)
     *
     * @param is utf-8 encoded input stream to parse for a value
     *
     * @return parsed value
     *
     * @exception SmartFrogParseException failed to parse any value
     */
    public Object sfParseAnyValue(InputStream is) throws SmartFrogCompilationException {
      try {
         return sfParseAnyValue(new InputStreamReader(is, "utf-8"));
      } catch (UnsupportedEncodingException e) {
         throw new SmartFrogParseException("error in encoding of stream", e);
      }
    }


    /**
     * Parses any value from a string. (the meaning of "any" is language dependant)
     *
     * @param txt string to parse for a value
     *
     * @return parsed value
     *
     * @exception SmartFrogParseException failed to parse any value
     */
    public Object sfParseAnyValue(String txt) throws SmartFrogCompilationException {
        return sfParseAnyValue(new StringReader(txt));
    }


   /*
   * ************************** Primitive value parsing **********************************
   */


    /**
     * Parses a primitive value from a reader. (the meaning of primitive is language dependant).
     * This must be used if non utf-8 encoded streams are to be used.
     *
     * @param reader reader to parse for a value
     *
     * @return parsed value
     *
     * @exception SmartFrogParseException failed to parse primtiive value
     */
    public Object sfParsePrimitiveValue(Reader reader) throws SmartFrogCompilationException {
       Object o = parser.sfParsePrimitiveValue(reader);
       if (o instanceof ReferencePhases) {
               o = ((ReferencePhases)o).sfAsReference();
           }
       return o;
    }

   /**
     * Parses a primitive value from an input stream. (the meaning of primitive is language dependant)
     *
     * @param is utf-8 encoded input stream to parse for a value
     *
     * @return parsed value
     *
     * @exception SmartFrogParseException failed to parse primtiive value
     */
    public Object sfParsePrimitiveValue(InputStream is) throws SmartFrogCompilationException {
       try {
          return sfParsePrimitiveValue(new InputStreamReader(is, "utf-8"));
       } catch (UnsupportedEncodingException e) {
          throw new SmartFrogParseException("error in encoding of stream", e);
       }
    }

    /**
     * Parses a primitive value from a string. (the meaning of primitive is language dependant)
     *
     * @param txt string to parse for a value
     *
     * @return parsed value
     *
     * @exception SmartFrogParseException failed to parse primtiive value
     */
    public Object sfParsePrimitiveValue(String txt) throws SmartFrogCompilationException {
       return sfParsePrimitiveValue(new StringReader(txt));
    }


   /*
   * ************************** TAGS parsing **********************************
   */


    /**
     * Parses tags from a Reader.
     * This must be used if non utf-8 encoded streams are to be used.
     *
     * @param reader reader to parse for a value
     *
     * @return parsed value
     *
     * @exception org.smartfrog.sfcore.common.SmartFrogParseException failed to parse tags
     */
    public Object sfParseTags(Reader reader) throws SmartFrogCompilationException {
       return parser.sfParseTags(reader);
    }

   /**
     * Parses tags from an input stream.
     *
     * @param is utf-8 encoded input stream to parse for a value
     *
     * @return parsed value
     *
     * @exception org.smartfrog.sfcore.common.SmartFrogParseException failed to parse tags
     */
    public Object sfParseTags(InputStream is) throws SmartFrogCompilationException {
       Object o = null;
       try {
          return sfParseTags(new InputStreamReader(is, "utf-8"));
       } catch (UnsupportedEncodingException e) {
          throw new SmartFrogParseException("error in encoding of stream", e);
       }
    }

    /**
     * Parses tags from a string.
     *
     * @param txt string to parse for a value
     *
     * @return parsed value
     *
     * @exception SmartFrogParseException failed to parse any value
     */
    public Object sfParseTags(String txt) throws SmartFrogCompilationException {
          return sfParseTags(new StringReader(txt));
    }

   /**
    * Parses a component description from a reader.
    * This must be used if non utf-8 encoded streams are to be used.
    * All the langauge phases will have been applied, and the conversion to ComponentDescription
    * carried out.
    *
    * @param reader reader to parse for a value
    *
    * @return parsed component description
    *
    * @exception SmartFrogParseException failed to parse primtiive value
    */
   public ComponentDescription sfParseComponentDescription(Reader reader) throws SmartFrogCompilationException {
      Phases p = parser.sfParse(reader);
      try {
           p = p.sfResolvePhases();
       } catch (SmartFrogException e) {
           throw new SmartFrogCompilationException("Error resolving phases of component description", e);
       }
       return p.sfAsComponentDescription();
   }

    /**
     * Parses a component description from an input stream.
     * All the langauge phases will have been applied, and the conversion to ComponentDescription
     * carried out.
     *
     * @param is utf-8 encoded input stream to parse for a value
     *
     * @return parsed component description
     *
     * @exception SmartFrogParseException failed to parse primtiive value
     */
    public ComponentDescription sfParseComponentDescription(InputStream is) throws SmartFrogCompilationException {
       try {
          return sfParseComponentDescription(new InputStreamReader(is, "utf-8"));
       } catch (UnsupportedEncodingException e) {
          throw new SmartFrogParseException("error in encoding of string for reader", e);
       }
    }

    /**
     * Parses a component description from a string.
     * All the langauge phases will have been applied, and the conversion to ComponentDescription
     * carried out.
     *
     * @param txt input to parse for a value
     *
     * @return parsed component description
     *
     * @exception SmartFrogParseException failed to parse primtiive value
     */
     public ComponentDescription sfParseComponentDescription(String txt) throws SmartFrogCompilationException {
          return sfParseComponentDescription(new StringReader(txt));
    }


}
