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
package org.smartfrog.test.system.initfile;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.prim.Prim;
import java.io.InputStream;
import org.smartfrog.SFSystem;
import java.io.File;
import java.io.*;
import java.util.ArrayList;


public class InitFileTest  extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/initfile/";
	

    public InitFileTest(String name) {
        super(name);
    }

    
    // org.smartfrog.sfcore.processcompound.sfRootLocatorPort
    public void testImproperPortValue() throws Exception
	{
	   

	    System.out.println("Test case : testImproperPortValue");
	    String ls_str;
	    Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("java -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.sfcore.processcompound.sfRootLocatorPort=qwe -cp C:/eclipse/workspace/SF/smartfrog/dist/lib/smartfrog.jar org.smartfrog.SFSystem");
            InputStream stderr = proc.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
	        StringBuffer fullResult=new StringBuffer(); 
            String line = null;
            System.out.println("____ERROR____");
            while ( (line = br.readLine()) != null)
            {
            	System.out.println(line);
            	fullResult.append(line);
            }
            assertContains(fullResult.toString(), "SmartFrogDeploymentException");
	        System.out.println("____ERROR____");
            proc.destroy();
        
	}
    // org.smartfrog.sfcore.processcompound.sfProcessTimeout   
    public void testImproperProcessTimeout() throws Exception
	{
		    StringBuffer fullResult1=new StringBuffer(); 
			String ls1_str;
	    	System.out.println("Test case :testImproperProcessTimeout");
	    	Runtime runtime1 = Runtime.getRuntime();
            Process proc1 = runtime1.exec("java -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.sfcore.processcompound.sfProcessTimeout=wer -cp C:/eclipse/workspace/SF/smartfrog/dist/lib/smartfrog.jar org.smartfrog.SFSystem");
            InputStream stderr1 = proc1.getErrorStream();
            InputStreamReader isr1 = new InputStreamReader(stderr1);
            BufferedReader br1 = new BufferedReader(isr1);
            String line1 = null;
            
            while ( (line1 = br1.readLine()) != null)
        	{
        		System.out.println(line1);
        		fullResult1.append(line1);
        	}
            if(fullResult1 == null)
			{
            	System.out.println("looking for std out put ");
            	System.out.println("____ERROR 1____");
            	InputStream stdoutput = proc1.getInputStream();
            	InputStreamReader isr1op = new InputStreamReader(stdoutput);
            	BufferedReader br1op = new BufferedReader(isr1op);
            	while ( (line1 = br1op.readLine()) != null)
            	{
            		System.out.println(line1);
            		fullResult1.append(line1);
            	}
            	assertContains(fullResult1.toString(), "SmartFrog [rootProcess] dead");
            }
            else
            {

            	assertTrue("Trying to start Instance", !(fullResult1.toString().indexOf("another instance is running")>=0));
            }
            System.out.println("____ERROR 1____");
            proc1.destroy();
        	}
	// org.smartfrog.sfcore.processcompound.sfLivenessDelay
	public void testImpropersfLivenessDelay() throws Exception
	{
		    StringBuffer fullResult2=new StringBuffer(); 
			String ls2_str;
	    	System.out.println("Test case :testImpropersfLivenessDelay");
	    	Runtime runtime2 = Runtime.getRuntime();
            Process proc2 = runtime2.exec("java -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.sfcore.processcompound.sfLivenessDelay=wer -cp C:/eclipse/workspace/SF/smartfrog/dist/lib/smartfrog.jar org.smartfrog.SFSystem");
            
            //
            InputStream stderr2 = proc2.getErrorStream();
            InputStreamReader isr2 = new InputStreamReader(stderr2);
            BufferedReader br2 = new BufferedReader(isr2);
            String line2 = null;
            System.out.println("____ERROR 2____");
            while ( (line2 = br2.readLine()) != null)
        	{
        		System.out.println(line2);
        		fullResult2.append(line2);
        	}
            if(!((line2 = br2.readLine()) != null))
			{
            	InputStream stdoutput = proc2.getInputStream();
            	InputStreamReader isr2op = new InputStreamReader(stdoutput);
            	BufferedReader br2op = new BufferedReader(isr2op);
            	System.out.println("____ERROR 2____");
            	while ( (line2 = br2op.readLine()) != null)
            	{
            		System.out.println(line2);
            		fullResult2.append(line2);
            	}
            	assertContains(fullResult2.toString(), "SmartFrog [rootProcess] dead");
            }
            else
            {
            	assertContains(fullResult2.toString(), "SmartFrog [rootProcess] dead");
            }
            
           
            	
            
            System.out.println("____ERROR 2____");
            proc2.destroy();
        	}
	// org.smartfrog.sfcore.processcompound.sfLivenessFactor
	public void testImproperSFLivenessFactor() throws Exception
	{
		    StringBuffer fullResult3=new StringBuffer(); 
			String ls3_str;
	    	System.out.println("Test case :testImproperSFLivenessFactor");
	    	Runtime runtime3 = Runtime.getRuntime();
            Process proc3 = runtime3.exec("java -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.sfcore.processcompound.sfLivenessFactor=wer -cp C:/eclipse/workspace/SF/smartfrog/dist/lib/smartfrog.jar org.smartfrog.SFSystem");
            InputStream stderr3 = proc3.getErrorStream();
            InputStreamReader isr3 = new InputStreamReader(stderr3);
            BufferedReader br3 = new BufferedReader(isr3);
            String line3 = null;
            
            while ( (line3 = br3.readLine()) != null)
        	{
        		System.out.println(line3);
        		fullResult3.append(line3);
        	}
            if(!((line3 = br3.readLine()) != null))
			{
            	System.out.println("____ERROR 3____");
            	InputStream stdoutput = proc3.getInputStream();
            	InputStreamReader isr3op = new InputStreamReader(stdoutput);
            	BufferedReader br3op = new BufferedReader(isr3op);
            	while ( (line3 = br3op.readLine()) != null)
            	{
            		System.out.println(line3);
            		fullResult3.append(line3);
            	}
            	assertContains(fullResult3.toString(), "SmartFrog [rootProcess] dead");
            }
            else
            {
            	assertContains(fullResult3.toString(), "SmartFrog [rootProcess] dead");
            }
           System.out.println("____ERROR 3____");
            proc3.destroy();
        	}
	// org.smartfrog.sfcore.processcompound.sfProcessAllow
	public void testImproperSFProcessAllow() throws Exception
	{
		    StringBuffer fullResult4=new StringBuffer(); 
			String ls4_str;
	    	System.out.println("Test case :testImproperSFProcessAllow");
	    	Runtime runtime4 = Runtime.getRuntime();
            Process proc4 = runtime4.exec("java -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.sfcore.processcompound.sfProcessAllow=wer -cp C:/eclipse/workspace/SF/smartfrog/dist/lib/smartfrog.jar org.smartfrog.SFSystem");
            InputStream stderr4 = proc4.getErrorStream();
            InputStreamReader isr4 = new InputStreamReader(stderr4);
            BufferedReader br4 = new BufferedReader(isr4);
            String line4 = null;
            
            while ( (line4 = br4.readLine()) != null)
        	{
        		System.out.println(line4);
        		fullResult4.append(line4);
        	}
            if(fullResult4 == null)
			{
            	System.out.println("looking for std out put ");
            	System.out.println("____ERROR 4____");
            	InputStream stdoutput = proc4.getInputStream();
            	InputStreamReader isr4op = new InputStreamReader(stdoutput);
            	BufferedReader br4op = new BufferedReader(isr4op);
            	while ( (line4 = br4op.readLine()) != null)
            	{
            		System.out.println(line4);
            		fullResult4.append(line4);
            	}
            	assertContains(fullResult4.toString(), "SmartFrog [rootProcess] dead");
            }
            else
            {

            	assertTrue("Trying to start Instance", !(fullResult4.toString().indexOf("another instance is running")>=0));
            }
            System.out.println("____ERROR 4____");
            proc4.destroy();
        	}
	// org.smartfrog.sfcore.logging.LogImpl.logLevel
	public void testImproperLogLevel() throws Exception
	{
		    StringBuffer fullResult5=new StringBuffer(); 
			String ls5_str;
	    	System.out.println("Test case :testImproperLogLevel");
	    	Runtime runtime5 = Runtime.getRuntime();
            Process proc5 = runtime5.exec("java -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.sfcore.logging.LogImpl.logLevel=wer -cp C:/eclipse/workspace/SF/smartfrog/dist/lib/smartfrog.jar org.smartfrog.SFSystem");
            InputStream stderr5 = proc5.getErrorStream();
            InputStreamReader isr5 = new InputStreamReader(stderr5);
            BufferedReader br5 = new BufferedReader(isr5);
            String line5 = null;
            
            while ( (line5 = br5.readLine()) != null)
        	{
        		System.out.println(line5);
        		fullResult5.append(line5);
        	}
            if(fullResult5 == null)
			{
            	System.out.println("looking for std out put ");
            	System.out.println("____ERROR 5____");
            	InputStream stdoutput = proc5.getInputStream();
            	InputStreamReader isr5op = new InputStreamReader(stdoutput);
            	BufferedReader br5op = new BufferedReader(isr5op);
            	while ( (line5 = br5op.readLine()) != null)
            	{
            		System.out.println(line5);
            		fullResult5.append(line5);
            	}
            	assertContains(fullResult5.toString(), "SmartFrog [rootProcess] dead");
            }
            else
            {

            	assertTrue("Trying to start Instance", !(fullResult5.toString().indexOf("another instance is running")>=0));
            }
            System.out.println("____ERROR 5____");
            proc5.destroy();
        	}
	// org.smartfrog.logger.logStackTrace
	public void testImproperLogStackTrace() throws Exception
	{
		    StringBuffer fullResult6=new StringBuffer(); 
			String ls6_str;
	    	System.out.println("Test case :testImproperlogStackTrace");
	    	Runtime runtime6 = Runtime.getRuntime();
            Process proc6 = runtime6.exec("java -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.logger.logStackTrace=wer -cp C:/eclipse/workspace/SF/smartfrog/dist/lib/smartfrog.jar org.smartfrog.SFSystem");
            InputStream stderr6 = proc6.getErrorStream();
            InputStreamReader isr6 = new InputStreamReader(stderr6);
            BufferedReader br6 = new BufferedReader(isr6);
            String line6 = null;
            
            while ( (line6 = br6.readLine()) != null)
        	{
        		System.out.println(line6);
        		fullResult6.append(line6);
        	}
            if(fullResult6 == null)
			{
            	System.out.println("looking for std out put ");
            	System.out.println("____ERROR 6____");
            	InputStream stdoutput = proc6.getInputStream();
            	InputStreamReader isr6op = new InputStreamReader(stdoutput);
            	BufferedReader br6op = new BufferedReader(isr6op);
            	while ( (line6 = br6op.readLine()) != null)
            	{
            		System.out.println(line6);
            		fullResult6.append(line6);
            	}
            	assertContains(fullResult6.toString(), "SmartFrog [rootProcess] dead");
            }
            else
            {

            	assertTrue("Trying to start Instance", !(fullResult6.toString().indexOf("another instance is running")>=0));
            }
            System.out.println("____ERROR 6____");
            proc6.destroy();
        	}
	// org.smartfrog.sfcore.logging.LogImpl.localLoggerClass
	public void testImproperLocalLoggerClass() throws Exception
	{
		    StringBuffer fullResult7=new StringBuffer(); 
			String ls7_str;
	    	System.out.println("Test case :testImproperLocalLoggerClass");
	    	Runtime runtime7 = Runtime.getRuntime();
            Process proc7 = runtime7.exec("java -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.sfcore.logging.LogImpl.localLoggerClass=123 -cp C:/eclipse/workspace/SF/smartfrog/dist/lib/smartfrog.jar org.smartfrog.SFSystem");
            InputStream stderr7 = proc7.getErrorStream();
            InputStreamReader isr7 = new InputStreamReader(stderr7);
            BufferedReader br7 = new BufferedReader(isr7);
            String line7 = null;
            
            while ( (line7 = br7.readLine()) != null)
        	{
        		System.out.println(line7);
        		fullResult7.append(line7);
        	}
            if(fullResult7 == null)
			{
            	System.out.println("looking for std out put ");
            	System.out.println("____ERROR 7____");
            	InputStream stdoutput = proc7.getInputStream();
            	InputStreamReader isr7op = new InputStreamReader(stdoutput);
            	BufferedReader br7op = new BufferedReader(isr7op);
            	while ( (line7 = br7op.readLine()) != null)
            	{
            		System.out.println(line7);
            		fullResult7.append(line7);
            	}
            	assertContains(fullResult7.toString(), "SmartFrog [rootProcess] dead");
            }
            else
            {

            	assertTrue("Trying to start Instance", !(fullResult7.toString().indexOf("another instance is running")>=0));
            }
            System.out.println("____ERROR 7____");
            proc7.destroy();
        	}
	
}
