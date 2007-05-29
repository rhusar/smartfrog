/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl.generate;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.services.xml.utils.XsdUtils;

import java.util.Hashtable;

/**
 * Contains a mapping of sfi:types to java types
 * created 02-Feb-2006 12:44:47
 */

public class TypeMapper {

    private Hashtable<String,TypeGenerator> map;

    /**
     *  supported type mapping
     * {@value}
     */
    public static final String STRING = "string";

    /** supported type mapping {@value} */
    public static final String TRIMMED = "trimmed";

    /**
     *  supported type mapping
     * {@value}
     */
    public static final String BOOLEAN = "boolean";

    /**
     *  supported type mapping
     * {@value}
     */
    public static final String INTEGER = "integer";

    /**
     *  supported type mapping
     * {@value}
     */
    public static final String LONG = "long";

    /**
     *  supported type mapping
     * {@value}
     */
    public static final String FLOAT = "float";

    /**
     *  supported type mapping
     * {@value}
     */
    public static final String DOUBLE = "double";

    private void initialize() throws SmartFrogException {
        map = new Hashtable<String, TypeGenerator>(8);
        map.put(STRING,new ConstructorGenerator(String.class,false));
        map.put(TRIMMED, new ConstructorGenerator(String.class, true));
        map.put(BOOLEAN, new BoolGenerator());
        map.put(INTEGER, new ConstructorGenerator(Integer.class, true));
        map.put(LONG, new ConstructorGenerator(Long.class, true));
        map.put(FLOAT, new ConstructorGenerator(Float.class, true));
        map.put(DOUBLE, new ConstructorGenerator(Double.class, true));
    }

    public TypeMapper() throws SmartFrogException {
        initialize();
    }

    /**
     * map from a node to a type.
     * return null for no mapping.
     * @param node
     * @return null for no mapping.
     * @throws SmartFrogException
     */
    public Object map(PropertyList node) throws SmartFrogException {
        TypeGenerator generator = getGenerator(node);
        if(generator==null) {
            return null;
        }
        Object result;
        result = generator.generateType(node);
        return result;
    }

    /**
     * Get the generator for this node
     * @param node
     * @return null for no type specified
     * @throws SmartFrogException
     */
    private TypeGenerator getGenerator(PropertyList node) throws
            SmartFrogException {
        String type = extractMappingType(node);
        TypeGenerator generator = null;
        if (type != null) {
            type = type.trim();
            if (type.length() != 0) {
                generator = map.get(type);
                if (generator == null) {
                    generator = new ConstructorGenerator(type, false);
                }
            }
        }
        return generator;
    }

    /**
     * get the mapping from the attributes
     * @param node
     * @return
     */
    public String extractMappingType(PropertyList node) {
        final String value = node.getAttributeValue(Constants.SMARTFROG_TYPES_TYPE_ATTR,
                Constants.XMLNS_SMARTFROG_TYPES);
        return value;
    }

    /**
     * test for a node having the nillable=true value,
     * which states that an empty node means "this element is not here".
     * For curiousity, 'nillable' is a concept from Soap section 5, and 
     * causes no end of interop problems. 
     * @param node
     * @return true iff the sfi:nillable attribute is true
     */
    public boolean isOptional(PropertyList node) {
        String value = node.getAttributeValue(Constants.SMARTFROG_TYPES_OPTIONAL_ATTR,
                        Constants.XMLNS_SMARTFROG_TYPES);
        return XsdUtils.isXsdBooleanTrue(value);
    }

    /**
     * Test for a node being optional
     * That is: there is no text, there is an sfi:type attribute, and there
     * is an sfi:nillable attribute. 
     * @param node
     * @return
     */
    public boolean isEmptyOptionalNode(PropertyList node) {
        if(isOptional(node) && extractMappingType(node)!=null) {
            return node.getChildCount()==0;
        }
        return false;
    }
}
