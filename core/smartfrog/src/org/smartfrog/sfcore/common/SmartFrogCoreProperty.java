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

package org.smartfrog.sfcore.common;

/**
 * All the properties used in SmartFrog core system should be defined here.
 */
public class SmartFrogCoreProperty {


    /**
     * Class Constructor. Nobody should call this.
     */
    private SmartFrogCoreProperty() {
    }

    //System

    /**
     * Base for all smartfrog properties. All properties looked up by classes
     * in SmartFrog use this as a base, add the package name and then the
     * property id to look up
     * @see SFSystem
     */
    public static final String propBase = "org.smartfrog.";

    /** Property name for class name for standard output stream.
     * @see SFSystem
     * */
    public static final String propOutStreamClass = propBase +
        "outStreamClass";

    /** Property name for class name for standard error stream.
     *  @see SFSystem
     *  */
    public static final String propErrStreamClass = propBase +
        "errStreamClass";

    /** Property name for ini file to read at start-up.
     *  @see SFSystem
     * */
    public static final String iniFile = propBase + "iniFile";


    /** Property name for logging stackTrace during exceptions.
     *  @see SFSystem
     *  */
    public static final String propLogStackTrace = propBase +
        "logger.logStackTrace";


    // Reference

    /**
     * Base for Reference property names
     * @see SFProcess
     */
    public static String propBaseReference = propBase+"Reference.";

    /**
     * Initial capacity for references. Looks up Reference.initCap (offset by
     * propBase). Defaults to 5 if not there
     * @see Reference
     */
    public static final String initCapReference = propBaseReference +"initCap";

    /**
     * Capacity increment for references. Looks up Reference.inc (offset by
     * propBase). Defaults to 2 if not there
     * @see Reference
     */
    public static final String incReference =  propBaseReference+"inc";


    //SFProcess

    /**
     * Base for process compound property names
     * @see SFProcess
     */
    public static String propBaseSFProcess = propBase +"sfcore.processcompound.";

    /**
     * Base for process compound default property names to load default
     * descriptions
     * @see SFProcess
     */
    public static String defaultDescPropBase = propBaseSFProcess + "sfDefault.";


    //ProcessCompund

    /**
     * Property that defines the process name
     * descriptions
     * @see ProcessCompoundImpl
     */
    public static String sfProcessName = propBase +"sfcore.processcompound.sfProcessName";

    //Compound

    /**
     * Property base for children vector properties. Uses SFSystem.propBase as
     * basis
     *
     * @see CompoundImpl
     */
    public static final String propBaseCompound = propBase+
        "sfcore.compound.";

    /**
     * Initial capacity for child vector (offset by
     * SmartFrogCoreProperty.propBaseCompound). Defaults to 5 if not there
     *
     * @see CompoundImpl
     */
    public static final String compoundChildCap = propBaseCompound + "childCap";

    /**
     * Capacity increment for child vector (offset
     * by SmartFrogCoreProperty.propBaseCompound).
     *
     * @see CompoundImpl
     */
    public static final String  compoundChildInc = propBaseCompound + "childInc";


   //Ordered Hash table
   /**
    * Property base for OrderedHashtable properties.
    *
    * @see OrderedHashtable
    */
   public static final String propBaseOrderedHashTable = propBase +
      "sfcore.common.OrderedHashtable.";

  /**
   * Initial capacity for OrderedHashtable.
   *
   * @see OrderedHashtable
   */
  public static final String initCapOrderedHashTable =
      propBaseOrderedHashTable+"initCap";

  /**
   * Load percentage for OrderedHashtable growth.
   *
   * @see OrderedHashtable
   */
  public static final String loadFacOrderedHashTable =
      propBaseOrderedHashTable+"loadFac";

  /**
   * Increment size for keys in OrderedHashtable.
   *
   * @see OrderedHashtable
   */
  public static final String keysIncOrderedHashTable =
      propBaseOrderedHashTable+"keysInc";


    //SFParser

    /**
     * Base property name for all parser related properties. SFSystem.propBase
     * followed by Parser.
     * @see parser.SFParser
     */
    public static final String propBaseSFParser = propBase+"sfcore.parser.";


    /**
     * Language name system property key. This property is used to
     * set the parser to construct if no language is specified.
     *
     * @see parser.SFParser
     */
    public static final String parserLanguage = propBaseSFParser+ "sfcore.language";

    /**
     * The package prefix for all language implementations. This is the
     * propBaseSFParser + LanguagesPackagePrefix. This sets the parser to construct
     * when getParser is called. Default is set by defaultParser property.
     *
     * @see parser.SFParser
     * @see defaultParser
     */
    public static final String languagesPackagePrefix= propBaseSFParser +"LanguagesPackagePrefix";

    /** This property defines the package prefix for languages defined in the
     * core.
     *
     * @see parser.SFParser
     * @see defaultParser
     */
    public static final String parserCorePackages = propBase+ "sfcore.languages";


    //SF Parser
    /**
     *  Base property name for all SF parser related properties.
     *
     * @see languages.sf.SFParser
     */
    public final static String propBaseSFParserSF = SmartFrogCoreProperty.parserCorePackages+".sf.";

    /**
     *  Property used to set the SF parser to construct when getParser
    *  is called
    *
     * @see languages.sf.SFParser
     */
    public final static String sfParserSFFactoryClass =
                   propBase+propBaseSFParserSF + "factoryClass";


   /**
    *  Property used to set the include handler to construct when
    *  getIncludeHandler is called in SF parser.
    *
    * @see languages.sf.SFParser
    */
   public final static String sfParserSFIncludeHandlerClass =
                              propBase+propBaseSFParserSF + "includeHandlerClass";
   /**
    *  Property used to define RMI port for SmartFrog Daemon
    *
    * @see languages.sf.SFParser
    */
   public final static String sfDaemonPort =
                              propBase+"ProcessCompound.sfRootLocatorPort";






}
