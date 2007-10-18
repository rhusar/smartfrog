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

package org.smartfrog.services.utils.concat;

import org.smartfrog.sfcore.reference.Reference;

/**
 * Defines the attribuites and references for Service Resource Manager that
 * mediates between FF and Utility Resource Manager.
 */
public interface SFConcat {
    /** Attribute name - concat. */
    final String ATR_CONCAT = "concat";
    /** Attribute name - reference. */
    final String ATR_REFERENCE = "reference";
    /** Attribute name - creat reference. */
    final String ATR_CREATE_REFERENCE = "createReference";
    /** Attribute name - debug. */
    final String ATR_DEBUG = "debug";
    /** Attribute name - string. */
    final String ATR_STRING = "string";

    /** References for attribute - concat. */
    final Reference REF_CONCAT = new Reference(ATR_CONCAT);
    /** References for attribute - debug. */
    final Reference REF_DEBUG = new Reference(ATR_DEBUG);
    /** References for attribute - string. */
    final Reference REF_STRING = new Reference(ATR_STRING);
    /** References for attribute - reference. */
    final Reference REF_REFERENCE = new Reference(
                ATR_REFERENCE);
    /** References for attribute - create reference. */
    final Reference REF_CREATE_REFERENCE = new Reference(
                ATR_CREATE_REFERENCE);
}
