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
package org.smartfrog.sfcore.languages.cdl.dom;

import org.ggf.cddlm.generated.api.CddlmConstants;

/**
 * created 25-May-2005 17:06:13
 */


public interface Names {
    /**
     * element name
     * {@value}
     */
    String ELEMENT_IMPORT = "import";
    String ELEMENT_TYPES = "types";
    String ELEMENT_CONFIGURATION = "configuration";
    String ELEMENT_SYSTEM = "system";
    String ELEMENT_CDL = "cdl";
    String ELEMENT_DOCUMENTATION = "documentation";
    String ELEMENT_EXPRESSION = "expression";
    String ELEMENT_VARIABLE = "variable";
    String ELEMENT_REF = "ref";

    String ATTR_NAMESPACE="namespace";
    String ATTR_LOCATION = "location";
    String ATTR_REFROOT = "refroot";
    String ATTR_REF = "ref";
    String ATTR_LAZY = "lazy";
    String ATTR_NAME = "name";
    String ATTR_VALUE_OF = "value-of";
    String ATTR_EXTENDS = "extends";
    String ATTR_TYPE = "type";
    String ATTR_TARGET_NAMESPACE = "targetNamespace";
    /**
     * our namespace {@value}
     */
    String CDL_NAMESPACE= CddlmConstants.XML_CDL_NAMESPACE;
}
