/**
 * (C) Copyright 2005 Hewlett-Packard Development Company, LP This library is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version. This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA For
 * more information: www.smartfrog.org
 */

package org.smartfrog.test.unit.java;

import org.smartfrog.services.os.java.SerializedArtifact;
import org.smartfrog.sfcore.common.SmartFrogException;

import junit.framework.TestCase;

/**
 * test case for policy tests; contains common paths and methods
 */
public abstract class AbstractPolicyTestBase extends TestCase {

    protected SerializedArtifact logging;
    public static final String COMMONS_LOGGING = "commons-logging";
    public static final String VERSION = "1.04";

    public static final String COMMONS_LOGGING_JAR= COMMONS_LOGGING+"-"+VERSION+".jar";

    public static final String MAVEN1_PATH = COMMONS_LOGGING + "/jars/"+COMMONS_LOGGING_JAR;

    public static final String MAVEN2_PATH = COMMONS_LOGGING+"/"
        + COMMONS_LOGGING+"/"+VERSION+"/"+ COMMONS_LOGGING_JAR;

    /**
     * create a commons logging
     */
    public void createArtifact() {
        logging= new SerializedArtifact();
        logging.artifact = COMMONS_LOGGING;
        logging.project = logging.artifact ;
        logging.version = VERSION;
        logging.extension = "jar";
    }

    protected void setUp() throws Exception {
        super.setUp();
        createArtifact();
    }
    
    protected static void assertErrorMessageContains(Throwable t,String text) {
        String m=t.getMessage();
        assertNotNull("Looking for "+text+" but the message is null in "+t,m);
        assertTrue("Did not find "+text+" in "+m,
                m.contains(text));
    }

    /**
     * Create a path for the logging artifact
     * @return String logging path
     * @throws SmartFrogException
     */
    protected abstract String createLoggingPath() throws Exception;



}
