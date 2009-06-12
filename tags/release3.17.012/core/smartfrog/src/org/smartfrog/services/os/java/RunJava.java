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
package org.smartfrog.services.os.java;

import org.smartfrog.services.os.runshell.RunShell;

/**
 * created 21-May-2004 17:23:10
 */


public interface RunJava extends RunShell {

    String ATTR_JARFILE="jar";
    String ATTR_ENVIRONMENT = "environment";
    String ATTR_CLASSNAME = "classname";
    String ATTR_CLASSPATH="classpath";
    String ATTR_SYSPROPERTIES="sysProperties";
    String ATTR_MAXMEMORY = "maxMemory";
    String ATTR_ASSERTIONS ="assertions";
    String ATTR_SYSTEMASSERTIONS ="systemAssertions";
    String ATTR_JVM_ARGS="jvmargs";
    String ATTR_ARGUMENTS="arguments";
    String ATTR_ENDORSED_DIRS="endorsedDirs";
}
