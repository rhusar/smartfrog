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
package org.cddlm.components;


/**
 * created 28-Apr-2004 11:40:33
 */
public interface Assert {
    //~ Static fields/initializers ---------------------------------------------

    /*
       isTrue extends OptionalBoolean;
       isFalse extends OptionalBoolean;
       reference extends OptionalCD;
       evaluatesTrue extends OptionalString;
       evaluatesFalse extends OptionalString;
     */
    public static final String IS_TRUE = "isTrue";
    public static final String IS_FALSE = "isFalse";
    public static final String REFERENCE = "reference";
    public static final String EVALUATES_TRUE = "evaluatesTrue";
    public static final String EVALUATES_FALSE = "evaluatesFalse";
}
