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

    public static final String IS_TRUE = "isTrue";
    public static final String IS_FALSE = "isFalse";
    public static final String REFERENCE = "reference";
    public static final String HAS_ATTRIBUTE ="hasAttribute";
    public static final String EVALUATES_TRUE = "evaluatesTrue";
    public static final String EVALUATES_FALSE = "evaluatesFalse";
    public static final String CHECK_ON_STARTUP = "checkOnStartup";
    public static final String FILE_EXISTS = "fileExists";
    public static final String DIR_EXISTS = "dirExists";
    public String ATTRIBUTE_EQUALS = "attributeEquals";

    /**
     * first equality string
     */
    public String EQUALS_STRING1 = "equalsString1";

    /**
     * second equality string
     */
    public String EQUALS_STRING2 = "equalsString2";

    public String EQUALITY_IGNORES_CASE = "equalityIgnoresCase";

    /**
     * check when we start up
     */
    public static final String CHECK_ON_LIVENESS = "checkOnLiveness";

    public static final String MESSAGE="message";
}
