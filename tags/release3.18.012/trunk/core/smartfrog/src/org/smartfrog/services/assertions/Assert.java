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
package org.smartfrog.services.assertions;

import java.rmi.Remote;


/**
 * created 28-Apr-2004 11:40:33
 */
public interface Assert extends Remote {

    /**
     * {@value}
     */
    String ATTR_IS_TRUE = "isTrue";
    /**
     * {@value}
     */
    String ATTR_IS_FALSE = "isFalse";
    /**
     * {@value}
     */
    String ATTR_REFERENCE = "reference";
    /**
     * {@value}
     */
    String ATTR_REFERENCE_REQUIRED = "referenceRequired";
    /**
     * {@value}
     */
    String ATTR_HAS_ATTRIBUTE = "hasAttribute";
    /**
     * {@value}
     */
    String ATTR_EVALUATES_TRUE = "evaluatesTrue";
    /**
     * {@value}
     */
    String ATTR_EVALUATES_FALSE = "evaluatesFalse";
    /**
     * check when we start up {@value}
     */
    String ATTR_CHECK_ON_STARTUP = "checkOnStartup";
    /**
     * check on a ping {@value}
     */
    String ATTR_CHECK_ON_LIVENESS = "checkOnLiveness";

    /**
     * {@value}
     */
    String ATTR_FILE_EXISTS = "fileExists";
    /**
     * {@value}
     */
    String ATTR_DIR_EXISTS = "dirExists";
    /**
     * {@value}
     */
    String ATTR_ATTRIBUTE_EQUALS = "attributeEquals";

    /**
     * first equality string {@value}
     */
    String ATTR_EQUALS_STRING1 = "equalsString1";

    /**
     * second equality string {@value}
     */
    String ATTR_EQUALS_STRING2 = "equalsString2";

    /**
     * {@value}
     */
    String ATTR_EQUALITY_IGNORES_CASE = "equalityIgnoresCase";


    /**
     * {@value}
     */
    String ATTR_MESSAGE = "message";

    //
    /**
     * value of a vector element to test {@value}
     */
    String ATTR_VECTOR_VALUE = "attributeVectorValue";
    //the index to look for
    /**
     * the index to look for {@value}
     */
    String ATTR_VECTOR_INDEX = "attributeVectorIndex";

    /**
     * the min length of the vector {@value}
     */
    String ATTR_VECTOR_MIN_LENGTH = "attributeVectorMinLength";
    /**
     * the max length of the vector {@value}
     */
    String ATTR_VECTOR_MAX_LENGTH = "attributeVectorMaxLength";

}
