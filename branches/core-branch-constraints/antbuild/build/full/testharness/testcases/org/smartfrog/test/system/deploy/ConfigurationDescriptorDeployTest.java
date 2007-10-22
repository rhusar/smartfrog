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

package org.smartfrog.test.system.deploy;


import org.smartfrog.test.SmartFrogTestBase;

import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.SFSystem;


import java.util.Enumeration;
import java.util.Vector;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.OptionSet;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.unit.sfcore.common.ConfigurationDescriptorTestURLs;


public class ConfigurationDescriptorDeployTest extends SmartFrogTestBase implements ConfigurationDescriptorTestURLs {


    public ConfigurationDescriptorDeployTest (String s) {
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
    /*
        protected void deployExpectingException2(String sfact,
                                                String testDescription,
                                                String exceptionName,
                                                String searchString,
                                                String containedExceptionName,
                                                String containedExceptionText) throws SmartFrogException,
                RemoteException, UnknownHostException, SFGeneralSecurityException {
            testDescription = "- Test description: \n   "+testDescription+" -  \n";
            System.out.println("\n"+testDescription);
            //Start Smartfrog
            SFSystem.runSmartFrog();
            ConfigurationDescriptor cfgDesc = new ConfigurationDescriptor(sfact);

            Object deployedApp = null;
            try {
                //Deploy and don't throw exception. Exception will be contained
                // in a ConfigurationDescriptor.
                deployedApp = SFSystem.runConfigurationDescriptor(cfgDesc,false);
                if ((deployedApp instanceof ConfigurationDescriptor) &&
                    (((ConfigurationDescriptor)deployedApp).resultException!=null)){
                    Throwable thr = ((ConfigurationDescriptor)deployedApp).resultException;
                    assertContains(cfgDesc.statusString(), searchString);
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

                } else {
                    fail(testDescription+ "We expected an exception here:"+exceptionName
                         +" but got this result "+deployedApp.toString());
                }
             } catch (Exception fault) {
                fail(fault.toString());
             }
        }
*/
    /**
     * Deploys an application and returns the refence to deployed application.
     * @param fileURL file with a list of SFACT: SmartFrog Action Descriptors
     * @param testDescription Description for the test
     * @return Reference to last deployed application
     * @throws RemoteException in the event of remote trouble.
     */
    protected Prim deployExpectingSuccessFile(String fileURL, String testDescription)
            throws Throwable {
        testDescription = "- Test descriptions file: \n   " + testDescription + " -  \n";
        Vector cfgDescS = OptionSet.readCfgDescriptorsFile (fileURL);
        try {
            Object deployedApp=null;
            getLog().info("\n Testing: " + testDescription + "\n    ");
            for (Enumeration items = cfgDescS.elements(); items.hasMoreElements();) {
              ConfigurationDescriptor cfgDesc =(ConfigurationDescriptor)items.nextElement();
                getLog().info("\n    To deploy: " + cfgDesc.toString("\n    "));
              deployedApp = SFSystem.runConfigurationDescriptor( cfgDesc,true);
                getLog().info("\n      Result: "+ cfgDesc.toString("\n    "));
//                if (deployedApp instanceof Prim) {
//                    log.info("\n" + testDescription + "\n    " + cfgDesc.toString("\n    "+ ((Prim) deployedApp).sfCompleteName()));
//                } else if (deployedApp instanceof ConfigurationDescriptor) {
//                    Throwable exception = ((ConfigurationDescriptor) deployedApp).resultException;
//                    if (exception != null) ;
//                    {
//                        log.info("\n Exception for: " +  testDescription + "\n    " + exception.toString());
//                        throw exception;
//                    }
//                 }
             } //for
             if (deployedApp instanceof Prim) {
                    return ((Prim) deployedApp);
             }
        } catch (Throwable throwable) {
            logThrowable(testDescription + "\n    " + fileURL,throwable);
            throw throwable;
        }
        fail(testDescription + "something odd came back");
        //fail throws a fault; this is here to keep the compiler happy.
        return null;
    }


    public void testurlTest01() throws Throwable {
        String sfact = fileURLTest01;
        String description = "testFileURLTest01 (" + sfact + ")";
        application = deployExpectingSuccessFile(sfact, description);
    }
//
//    public void testurlTest02() throws Throwable {
//        String sfact = urlTest02;
//        String description = "testurlTest02(" + sfact + ")";
//        application = deployExpectingSuccess2(sfact, description);
//    }
//
//    public void testurlTest03() throws Throwable {
//        String sfact = urlTest03;
//        String description = "testurlTest03(" + sfact + ")";
//        application = deployExpectingSuccess2(sfact, description);
//    }
//
//    public void testurlTest04() throws Throwable {
//        String sfact = urlTest04;
//        String description = "testurlTest04(" + sfact + ")";
//        application = deployExpectingSuccess2(sfact, description);
//    }
//
//    public void testurlTest05() throws Throwable {
//        String sfact = urlTest05;
//        String description = "testurlTest05(" + sfact + ")";
//        application = deployExpectingSuccess2(sfact, description);
//    }
//
//    public void testurlTest06() throws Throwable {
//        String sfact = urlTest06;
//        String description = "testurlTest06(" + sfact + ")";
//        application = deployExpectingSuccess2(sfact, description);
//    }
//
//    public void testurlTest07() throws Throwable {
//        String sfact = urlTest07;
//        String description = "testurlTest07(" + sfact + ")";
//        application = deployExpectingSuccess2(sfact, description);
//    }
//
//
//    public void testurlTest08() throws Throwable {
//        String sfact = urlTest08;
//        String description = "testurlTest08(" + sfact + ")";
//        application = deployExpectingSuccess2(sfact, description);
//    }
//
//    public void testurlTest09() throws Throwable {
//        String sfact = urlTest09;
//        String description = "testurlTest09(" + sfact + ")";
//        application = deployExpectingSuccess2(sfact, description);
//    }
//
//    public void testurlTest09b() throws Throwable {
//        String sfact = urlTest07b;
//        String description = "testurlTest07b(" + sfact + ")";
//        application = deployExpectingSuccess2(sfact, description);
//    }
//
//    public void testurlTest10() throws Throwable {
//        String sfact = urlTest10;
//        String description = "testurlTest10(" + sfact + ")";
//        application = deployExpectingSuccess2(sfact, description);
//    }
//
//    public void testurlTest11() throws Throwable {
//        String sfact = urlTest11;
//        String description = "testurlTest11(" + sfact + ")";
//        application = deployExpectingSuccess2(sfact, description);
//    }

// 12 and 12b cannot be run when 7 is.
//        public void testurlTest12 ()throws Throwable{
//           String sfact = urlTest12;
//           String description = "testurlTest12("+sfact+")";
//           deployExpectingSuccess2(sfact,description);
//        }
//
//        public void testurlTest12b ()throws Throwable{
//           String sfact = urlTest12b;
//           String description = "testurlTest12b("+sfact+")";
//           deployExpectingSuccess2(sfact,description);
//        }

//        public void testurlTest13 ()throws Throwable{
//           String sfact = urlTest13;
//           String description = "testurlTest13("+sfact+")";
//           deployExpectingSuccess2(sfact,description);
//        }
//        public void testurlTest14 ()throws Throwable{
//           String sfact = urlTest14;
//           String description = "testurlTest14("+sfact+")";
//           deployExpectingSuccess2(sfact,description);
//        }
//
//        public void testurlTest15 ()throws Throwable{
//           String sfact = urlTest15;
//           String description = "testurlTest15("+sfact+")";
//           deployExpectingSuccess2(sfact,description);
//        }
//

        // This one has to be the last successful test. It terminates the
        // daemon
//        public void testurlTest100 ()throws Throwable{
//           String sfact = urlTest100;
//           String description = "testurlTest12("+sfact+")";
//           deployExpectingSuccess2(sfact,description);
//        }

//        public void testurlTest16 ()throws Throwable{
//           String sfact = urlTest16;
//           String description = "("+sfact+")";
//           deployExpectingSuccess2(sfact,description);
//        }

}
