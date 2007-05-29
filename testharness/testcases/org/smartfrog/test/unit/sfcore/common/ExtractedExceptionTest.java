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
package org.smartfrog.test.unit.sfcore.common;

import junit.framework.TestCase;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogExtractedException;

import java.rmi.RemoteException;

/** created 15-Mar-2007 13:55:53 */

public class ExtractedExceptionTest extends TestCase {


    /** Constructs a test case with the given name. */
    public ExtractedExceptionTest(String name) {
        super(name);
    }


    private SmartFrogException sfe;
    private SmartFrogExtractedException sfee;
    private RemoteException remote;
    private NullPointerException npe;
    private SmartFrogException chaina;
    private SmartFrogException chainb;
    private Exception chainc, chaind;
    SmartFrogException sfe2,sfee2,remote2,npe2,chaina2,chainb2,chainc2,chaind2;


    /** Sets up the fixture, for example, open a network connection.
     *  This method is called before a test is executed.
     * */
    protected void setUp() throws Exception {
        sfe = new SmartFrogException("sfe");
        sfee = new SmartFrogExtractedException("sfee");
        remote = new RemoteException("remote");
        npe = new NullPointerException("npe");
        chaina = new SmartFrogException("chain1", sfe);
        chainb = new SmartFrogException("chain2", npe);
        chainc = new SmartFrogException("chain3",
                new RemoteException("remote3",
                        new NullPointerException("npe3")));
        chaind = new RemoteException("chain4",
                new SmartFrogException("remote4",
                        new NullPointerException("npe4")));
        sfe2 = c(sfe);
        sfee2 = c(sfee);
        remote2 = c(remote);
        npe2 = c(npe);
        chaina2 = c(chaina);
        chainb2 = c(chainb);
    }

    protected SmartFrogException c(Throwable t) {
        return SmartFrogExtractedException.convert(t);
    }

    protected void assertClassname(String name, SmartFrogExtractedException t) {
        assertEquals(name,t.shortClassName());
    }

    protected void assertMessageContains(String text,Throwable t) {
        assertEquals(text,t.getMessage());
    }

    protected void assertMessageEqual(Throwable expected, Throwable t) {
        assertEquals(expected.getMessage(), t.getMessage());
    }

    protected void assertLocalMessageEqual(Throwable expected, Throwable t) {
        assertEquals(expected.getLocalizedMessage(), t.getLocalizedMessage());
    }

    protected void assertExtracted(Throwable t) {
        assertTrue(t instanceof SmartFrogExtractedException);
    }

    public void testSFEisUnconverted() throws Exception {
        assertSame(sfe,sfe2);
    }

    public void testSFEEisUnconverted() throws Exception {
        assertSame(sfee, sfee2);
    }

    public void testAssertMatchesWorks() throws Exception {
        assertMatches(npe,npe);
        assertMatches(chainb, chainb);
    }

    private void assertConverted(Exception t1, SmartFrogException t2) {
        assertExtracted(t2);
        assertMatches(t1, t2);
    }

    private void assertMatches(Throwable t1, Throwable t2) {
        assertMessageEqual(t1, t2);
        assertLocalMessageEqual(t1, t2);
        StackTraceElement[] stack1 = t1.getStackTrace();
        StackTraceElement[] stack2 = t2.getStackTrace();
        assertEquals(stack1.length,stack2.length);
        for(int i=0;i<stack1.length;i++) {
            assertEquals(stack1[i],stack2[i]);
        }
        Throwable cause = t1.getCause();
        if(cause!=null) {
            assertNotNull(t2.getCause());
            assertMatches(cause,t2.getCause());
        }
    }

    public void testRemoteisConverted() throws Exception {
        assertMatches(remote, remote2);
    }

    public void testNpeConverted() throws Exception {
        assertExtracted(npe2);
        assertMatches(npe, npe2);
    }

    public void testChainANotConverted() throws Exception {
        assertSame(chaina,chaina2);
    }

    public void testChainBConverted() throws Exception {
        assertConverted(chainb, chainb2);
    }

    public void testChainCConverted() throws Exception {
        chainc2 = c(chainc);
        assertConverted(chainc, chainc2);
    }

    public void testChainDConverted() throws Exception {
        chaind2 = c(chaind);
        assertConverted(chaind, chaind2);
    }

}
