/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.junit.test.targets;

import junit.framework.TestCase;

/**
 * Date: 05-Jul-2004
 * Time: 22:09:54
 */
public class ThrowingTest extends TestCase {


    /**
     * No-arg constructor to enable serialization. This method is not intended to be used by mere mortals without
     * calling setName().
     */
    public ThrowingTest() {
    }

    public ThrowingTest(String s) {
        super(s);
    }

    public void testThrowing() {
        throw new RuntimeException("This was meant to happen");
    }

    public void testDoubleThrow() throws Exception{
        try {
            throw new Exception("nested");
        } catch (Exception e) {
            throw new RuntimeException("caught exception",e);
        }
    }

    public void testTripleThrow() throws Exception {
        try {
            try {
                throw new Exception("nested");
            } catch (Exception e) {
                throw new RuntimeException("caught exception", e);
            }
        } catch (RuntimeException e) {
            throw new Exception("nested exception", e);
        }
    }

}
