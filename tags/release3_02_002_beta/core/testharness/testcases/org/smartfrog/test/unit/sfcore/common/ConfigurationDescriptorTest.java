
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


package org.smartfrog.test.unit.sfcore.common;

import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.SFSystem;
import junit.framework.Assert;

import org.smartfrog.test.SmartfrogTestBase;

import java.util.Enumeration;

import java.rmi.RemoteException;
import java.net.UnknownHostException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.security.SFGeneralSecurityException;
import org.smartfrog.sfcore.prim.Prim;

import junit.framework.TestCase;

public class ConfigurationDescriptorTest extends TestCase implements ConfigurationDescriptorTestURLs {

    public ConfigurationDescriptorTest (String s) {
        super(s);
    }


//    public void testConstructionParse1() {
//        ConfigurationDescriptor cd =null;
//        try {
//            cd = new ConfigurationDescriptor(url1);
//            fail();
//        } catch (SmartFrogInitException e) {
//            assertEquals("Error parsing ACTION_TYPE in: display:TER:::localhost:subprocess", e.getMessage().toString());
//        }
//    }
//
//    public void testConstructionParse1b() {
//        ConfigurationDescriptor cd =null;
//        try {
//            cd = new ConfigurationDescriptor(url1b);
//            fail();
//        } catch (SmartFrogInitException e) {
//            assertEquals("Error parsing ACTION_TYPE in: \"HOST guijarro-j-3.hpl.hp.com:rootProcess:sfDefault:display\":TER:::localhost:subprocess", e.getMessage().toString());
//        }
//    }
//
//    public void testConstructionParse7() {
//        ConfigurationDescriptor cd =null;
//        String validUrl=validUrl = "d:\\cvs\\SmartFrog\\core\\extras\\ant\\test\\files\\valid.sf";
//        try {
//            cd = new ConfigurationDescriptor(url7);
//
//            if (!(cd.getUrl().equals(validUrl))){
//                fail("Failed to get valid url: "+validUrl +" in " + cd.toString() +" when parsing "+ url7);
//            }
//        } catch (SmartFrogInitException e) {
//            fail("Fail when trying to read: "+validUrl+", "+ e.toString());
//        }
//    }

    /**
         * Deploy a component, expecting a smartfrog exception. You can
         * also specify the classname of a contained fault -which, if specified,
         * must be contained, and some text to be searched for in this exception.
         * @param sfact  SFACT: SmartFrog Action Descriptors
         * @param testDescription Description for the test
         * @param exceptionName name of the exception thrown
         * @param searchString string which must be found in the exception message
         * @param containedExceptionName optional classname of a contained
         * exception; does not have to be the full name; a fraction will suffice.
         * @param containedExceptionText optional text in the contained fault.
         * Ignored if the containedExceptionClass parametere is null.
         * @throws RemoteException in the event of remote trouble.
         */
        protected void deployExpectingException(String sfact,
                                                String testDescription,
                                                String exceptionName,
                                                String searchString,
                                                String containedExceptionName,
                                                String containedExceptionText) throws SmartFrogException,
                RemoteException, UnknownHostException, SFGeneralSecurityException {
            testDescription = "- Test description: "+testDescription+" -  \n";
            System.out.println("\n"+testDescription+"\n");

            ConfigurationDescriptor cfgDesc=null;
            try {
                cfgDesc = new ConfigurationDescriptor(sfact);
                fail(testDescription+ "We expected an exception here:"+exceptionName
                         +" but not exception was thrown");

             } catch (Throwable thr) {
                 if (cfgDesc!=null) {
                     if (searchString!=null) {
                         assertContains(cfgDesc.statusString(), searchString,testDescription);
                     }
                     if (containedExceptionName!=null) {
                         Throwable cause = thr.getCause();
                         assertNotNull(testDescription+"expected throwable of type "
                                       +containedExceptionName,
                                       cause);
                         assertContains(cause.toString(),
                                        containedExceptionName,
                                        cfgDesc.statusString());

                         if (containedExceptionText!=null) {
                             assertContains(cause.toString(),
                                            containedExceptionText,
                                            cfgDesc.statusString());
                         }
                     }
                 }
             }
        }

        /**
         * assert that a throwable's classname is of a given type/substring
         * @param thrown
         * @param name
         */
        public void assertThrowableNamed(Throwable thrown,String name, String cfgDescMsg) {
            assertContains(thrown.getClass().getName(),name, cfgDescMsg);
            //assertContains(thrown.toString(),name, cfgDescMsg);
        }

        /**
         * assert that a string contains a substring
         * @param source
         * @param substring
         * @param cfgDescMsg
         */
        public void assertContains(String source, String substring, String cfgDescMsg) {
            assertNotNull("No string to look for ["+substring+"]",source);
            assertTrue("Did not find ["+substring+"] in ["+source+"]"+"\n, Result:"+cfgDescMsg,
                    source.indexOf(substring)>=0);
        }

        /**
         * Deploys an application and returns the refence to deployed application.
         * @param sfact  SFACT: SmartFrog Action Descriptors
         * @param testDescription Description for the test
         * @return Reference to deployed application
         * @throws RemoteException in the event of remote trouble.
         */
        protected void deployExpectingSuccess(String sfact, String testDescription)
                                                        throws Exception,Throwable {
            testDescription = "- Test description: "+testDescription+" -  \n";
            System.out.println("\n"+testDescription+"\n");

            try {
                ConfigurationDescriptor cfgDesc = new ConfigurationDescriptor(sfact);
            } catch (Throwable throwable) {
               fail(testDescription + throwable.getMessage());
               throw throwable;
            }
        }


        public void testurlTest01 ()throws Throwable{
           String sfact = urlTest01;
           String description = "testurlTest01("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }
        public void testurlTest02 ()throws Throwable{
           String sfact = urlTest02;
           String description = "testurlTest02("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }
        public void testurlTest03 ()throws Throwable{
           String sfact = urlTest03;
           String description = "testurlTest03("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }
        public void testurlTest04 ()throws Throwable{
           String sfact = urlTest04;
           String description = "testurlTest04("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }
        public void testurlTest05 ()throws Throwable{
           String sfact = urlTest05;
           String description = "testurlTest05("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }
        public void testurlTest06 ()throws Throwable{
           String sfact = urlTest06;
           String description = "testurlTest06("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }
        public void testurlTest07 ()throws Throwable{
           String sfact = urlTest07;
           String description = "testurlTest07("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }
        public void testurlTest07b ()throws Throwable{
           String sfact = urlTest07b;
           String description = "testurlTest07b("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }

        public void testurlTest08 ()throws Throwable{
           String sfact = urlTest08;
           String description = "testurlTest08("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }
        public void testurlTest09 ()throws Throwable{
           String sfact = urlTest09;
           String description = "testurlTest09("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }
        public void testurlTest10 ()throws Throwable{
           String sfact = urlTest10;
           String description = "testurlTest10("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }
        public void testurlTest11 ()throws Throwable{
           String sfact = urlTest11;
           String description = "testurlTest11("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }
        public void testurlTest12b ()throws Throwable{
           String sfact = urlTest12b;
           String description = "testurlTest15b("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }

//        public void testurlTest13 ()throws Throwable{
//           String sfact = urlTest13;
//           String description = "testurlTest13("+sfact+")";
//           this.deployExpectingSuccess(sfact,description);
//        }
//        public void testurlTest14 ()throws Throwable{
//           String sfact = urlTest14;
//           String description = "testurlTest14("+sfact+")";
//           this.deployExpectingSuccess(sfact,description);
//        }
//
//        public void testurlTest15 ()throws Throwable{
//           String sfact = urlTest15;
//           String description = "testurlTest15("+sfact+")";
//           this.deployExpectingSuccess(sfact,description);
//        }


        // This one has to be the last successful test. It terminates the
        // daemon
        public void testurlTest100 ()throws Throwable{
           String sfact = urlTest100;
           String description = "testurlTest12("+sfact+")";
           this.deployExpectingSuccess(sfact,description);
        }

//        public void testurlTest16 ()throws Throwable{
//           String sfact = urlTest16;
//           String description = "("+sfact+")";
//           this.deployExpectingSuccess(sfact,description);
//        }

}
