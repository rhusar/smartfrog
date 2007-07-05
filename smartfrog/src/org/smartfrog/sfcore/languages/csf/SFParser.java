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

package org.smartfrog.sfcore.languages.csf;

import java.io.InputStream;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.languages.csf.csfcomponentdescription.CSFComponentDescription;
import org.smartfrog.sfcore.languages.sf.IncludeHandler;
import org.smartfrog.sfcore.parser.Phases;

import org.smartfrog.sfcore.parser.StreamLanguageParser;
import org.smartfrog.sfcore.parser.ReferencePhases;

import org.smartfrog.sfcore.security.SFClassLoader;

import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.lang.reflect.Constructor;




/**
 * Implements the default Parser interface for SmartFrog parsers. This
 * implementation uses DefaultParser (generated by sfparser.jj) to parse
 * SmartFrog syntax. The getParser utility method will use the
 * org.smartfrog.sfcore.parser.parserClass attribute to construct an instance
 * of a Parser interface with as default this class. This allows different
 * implementations of the parser. Note that a different implementation of the
 * parser will also mean that the default include handler will need to be
 * subclasses and changed since it uses the DefaultParser to parse include
 * files
 */
public class SFParser implements StreamLanguageParser {


    /**
     * Default SF parser factory class
     */
    public static final String defaultSFFactoryClass =
            "org.smartfrog.sfcore.languages.csf.DefaultFactory";

    /**
     * Default SF parser Include Handler class
     */
    public static final String defaultSFIncludeHandlerClass =
            "org.smartfrog.sfcore.languages.sf.DefaultIncludeHandler";

    /**
     * Factory class name system property key. This is the propBase +
     * componentFactoryClass. This sets the parser to construct when getParser
     * is called. Default is an instance of this class.
     */
    public static String factoryClassName = SFSystem.getProperty(SmartFrogCoreProperty.sfParserSFFactoryClass, defaultSFFactoryClass);

    /**
     * include handler class name system property key. This is the propBase +
     * includeHandlerClass. This sets the include handler to construct when
     * getIncludeHandler is called. Default is an instance of
     * DefaultIncludeHandler class.
     */
    public static String includeHandlerClassName = SFSystem.getProperty(SmartFrogCoreProperty.sfParserSFIncludeHandlerClass, defaultSFIncludeHandlerClass);

    /**
     * Stores the class for the factory.
     */
    protected static Class factoryClass;

    /**
     * Stores the class for the include handler.
     */
    protected static Class includeHandlerClass;

    /**
     * Constructs a factory using the factoryClass (prepended by propBase)
     * system property.
     */
    protected static Factory factory = null;


    //
    // Parser
    //

    /**
     * Constructor for getParser to be able to construct an instance of this
     * parser.
     */
    public SFParser() {
    }


    /**
     * Constructs an includehandler using the includeHandlerClass (prepended by
     * propBase) system property.
     *
     * @param codebase an optional codebase where the include may be found. If null, use the default code base
     * @return new include handler
     * @throws Exception failed to construct handler
     */
    public static IncludeHandler getIncludeHandler(String codebase) throws Exception {
        try {
            if (includeHandlerClass == null) {
                includeHandlerClass = Class.forName(includeHandlerClassName);
            }
            Class[] includeHandlerConstArgsTypes = {String.class};

            Constructor includeHandlerConst = includeHandlerClass.getConstructor(includeHandlerConstArgsTypes);

            Object[] deplConstArgs = {codebase};

            return (IncludeHandler) includeHandlerConst.newInstance(deplConstArgs);

        } catch (NoSuchMethodException nsmetexcp) {
            throw new SmartFrogException(MessageUtil.formatMessage(MessageKeys.MSG_METHOD_NOT_FOUND, includeHandlerClassName, "getConstructor()"), nsmetexcp);
        } catch (ClassNotFoundException cnfexcp) {
            throw new SmartFrogException(MessageUtil.formatMessage(MessageKeys.MSG_CLASS_NOT_FOUND, includeHandlerClassName), cnfexcp);
        } catch (InstantiationException instexcp) {
            throw new SmartFrogException(MessageUtil.formatMessage(MessageKeys.MSG_INSTANTIATION_ERROR, includeHandlerClassName), instexcp);
        } catch (IllegalAccessException illaexcp) {
            throw new SmartFrogException(MessageUtil.formatMessage(MessageKeys.MSG_ILLEGAL_ACCESS, includeHandlerClassName, "newInstance()"), illaexcp);
//      } catch (InvocationTargetException intarexcp) {
//          throw new SmartFrogException(MessageUtil.formatMessage(
//              MessageKeys.MSG_INVOCATION_TARGET, includeHandlerClassName), intarexcp);
        } catch (Throwable ex) {
            throw SmartFrogException.forward(ex);
        }
    }


    /**
     * Gets the factory attribute of the SFParser class.
     *
     * @return The factory value
     * @throws ParseException error creating factory class
     */
    public static Factory getFactory() throws ParseException {
        try {
            if (factory == null) {
                if (factoryClass == null) {
                    factoryClass = Class.forName(factoryClassName);
                    factory = (Factory) factoryClass.newInstance();
                }
            }
        } catch (Exception ex) {
            throw new ParseException("Error creating factory class ");
        }

        return factory;
    }


    /**
     * Generate a componentDescription node for the tree given the name of the
     * type of the tree node.
     *
     * @param componentType type of the node component
     * @return tree node
     * @throws ParseException non existant component type
     */
    public static CSFComponentDescription componentFactory(String componentType)
            throws ParseException {
        return getFactory().node(componentType);
    }


    /**
     * Parse an input stream. Creates a DefaultParser and a root component
     * description and uses the Attributes rule in the DefaultParser to fill in
     * the root. Inlcudes are handled by the given include handler.
     *
     * @param is      input stream to parse
     * @param handler include handler
     * @return root description with parsed attributes
     * @throws SmartFrogParseException failure while parsing
     */
    public Phases sfParse(InputStream is, IncludeHandler handler) throws SmartFrogParseException {
        try {
            CSFComponentDescription root = componentFactory("root");
            (new DefaultParser(is, handler)).Attributes(root);
            return root;
        } catch (ParseException pe) {
            throw (SmartFrogParseException) SmartFrogParseException.forward(pe);
        }
    }


    /**
     * Parse an input stream. Forwards to the expanded parse method with the
     * result of getIncludeHandler.
     *
     * @param is input stream to parse
     * @return root description with parsed attributes
     * @throws SmartFrogParseException failure while parsing
     */
    public Phases sfParse(InputStream is) throws SmartFrogParseException {
        return sfParse(is, (String) null);
    }

    /**
     * Parse an input stream. Forwards to the expanded parse method with the
     * result of getIncludeHandler.
     *
     * @param is       input stream to parse
     * @param codebase an optional codebase where the include may be found. If null, use the default code base
     * @return root description with parsed attributes
     * @throws SmartFrogParseException failure while parsing
     */
    public Phases sfParse(InputStream is, String codebase) throws SmartFrogParseException {
        try {
            return sfParse(is, getIncludeHandler(codebase));
        } catch (Throwable thr) {
            throw (SmartFrogParseException) SmartFrogParseException.forward(thr);
        }
    }


    /**
     * Parses a reference from given string. This is NOT a cheap method since a
     * new DefaultParser will be constructed to create the reference.
     *
     * @param is input stream to parse
     * @return parsed reference
     * @throws SmartFrogParseException failure while parsing reference
     */
    public ReferencePhases sfParseReference(InputStream is) throws SmartFrogParseException {
        try {
            return (new DefaultParser(is, null)).Reference();
        } catch (ParseException pe) {
            //throw new SmartFrogParseException (pe.getMessage(),pe);
            throw new SmartFrogParseException("Error parsing reference from InputStream", pe);
        }
    }

    /**
     * Parses any value (ie as allowed in an attribute definition) from given string. This is NOT a cheap method since a
     * new DefaultParser will be constructed to create the reference.
     *
     * @param is input stream to parse
     * @return parsed value
     * @throws SmartFrogParseException failure while parsing value
     */
    public Object sfParseAnyValue(InputStream is) throws SmartFrogParseException {
        try {
            return (new DefaultParser(is, null)).AnyValue();
        } catch (ParseException pe) {
            //throw new SmartFrogParseException (pe.getMessage(),pe);
            throw new SmartFrogParseException("Error parsing any value from InputStream", pe);
        }
    }

    /**
     * Parses any primitive value (ie no links, component descriptions) from given string. This is NOT a cheap method since a
     * new DefaultParser will be constructed to create the reference.
     *
     * @param is input stream to parse
     * @return parsed value
     * @throws SmartFrogParseException failure while parsing primitive value
     */
    public Object sfParsePrimitiveValue(InputStream is) throws SmartFrogParseException {
        try {
            return (new DefaultParser(is, null)).PrimitiveValue();
        } catch (ParseException pe) {
            //throw new SmartFrogParseException (pe.getMessage(),pe);
            throw new SmartFrogParseException("Error parsing primitive value from InputStream", pe);
        }
    }

   /**
    *  Parses tags from given string. This is NOT a cheap method since a
    *  new DefaultParser will be constructed to create the reference.
    *
    *@param  is                      input stream to parse
    *@return                         parsed value
    *@exception  SmartFrogParseException  failure while parsing value
    */
   public Object sfParseTags(InputStream is) throws SmartFrogParseException {
       try {
           return (new org.smartfrog.sfcore.languages.csf.DefaultParser(is, null)).TagsSet();
       } catch (ParseException pe){
           throw new SmartFrogParseException ("Error parsing Tags from InputStream", pe);
       }
   }
}
