/*
 * (C) Copyright 2007 Hewlett-Packard Development Company, LP
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * For more information: www.smartfrog.org
 */

package org.smartfrog.nbm;

import junit.framework.TestCase;

/**
 *
 * @author slo
 */
public class SmartFrogSvcUtilTest extends TestCase {
    
    public SmartFrogSvcUtilTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SmartFrogSvcUtil.rebuildInfo();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getSFHome method, of class SmartFrogSvcUtil.
     */
    public void testGetSFHome() {
        String result = SmartFrogSvcUtil.getSFHome();
    }

    /**
     * Test of getSFUserHome method, of class SmartFrogSvcUtil.
     */
    public void testGetSFUserHome() {
        String result = SmartFrogSvcUtil.getSFUserHome();
    }

    /**
     * Test of getSFRestrictToIncludes method, of class SmartFrogSvcUtil.
     */
    public void testGetSFRestrictToIncludes() {
        boolean result = SmartFrogSvcUtil.getSFRestrictToIncludes();
    }

    /**
     * Test of getSFClassPath method, of class SmartFrogSvcUtil.
     */
    public void testGetSFClassPath() {
        String result = SmartFrogSvcUtil.getSFClassPath();
    }

    /**
     * Test of getSFQuietTime method, of class SmartFrogSvcUtil.
     */
    public void testGetSFQuietTime() {
        int result = SmartFrogSvcUtil.getSFQuietTime();
    }


    /**
     * Test of getIniFile method, of class SmartFrogSvcUtil.
     */
    public void testGetIniFile() {
        String result = SmartFrogSvcUtil.getIniFile();
        assertNotNull(result);
    }

    /**
     * Test of getSFDefault method, of class SmartFrogSvcUtil.
     */
    public void testGetSFDefault() {
        String result = SmartFrogSvcUtil.getSFDefault();
        assertNotNull(result);
    }

    /**
     * Test of getUrlCodebase method, of class SmartFrogSvcUtil.
     */
    public void testGetUrlCodebase() {
        String result = SmartFrogSvcUtil.getUrlCodebase();
        assertNotNull(result);
    }

}
