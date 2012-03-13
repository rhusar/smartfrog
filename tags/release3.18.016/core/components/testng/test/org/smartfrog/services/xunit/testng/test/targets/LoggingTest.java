/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xunit.testng.test.targets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 */
public class LoggingTest {

    private Log log;


    @BeforeClass
    public void setUp() {
        LogFactory lf= LogFactory.getFactory();
        log = LogFactory.getLog(getClass());
    }

    @Test()
    public void firstTest() {
        System.out.println("text1");
        log.info("Info");
        log.warn("Warning");
        log.error("Error");
        System.out.println("text2");
    }

    @Test()
    public void aSlowTest() {
        System.out.println("Slow test");
    }
}
