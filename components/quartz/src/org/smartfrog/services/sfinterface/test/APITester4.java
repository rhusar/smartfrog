package org.smartfrog.services.sfinterface.test;

import org.smartfrog.services.sfinterface.SFParseException;
import org.smartfrog.services.sfinterface.SFSubmitException;
import org.smartfrog.services.sfinterface.SmartFrogAdapterImpl;
import org.smartfrog.services.sfinterface.SmartfrogAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ganesaku
 * Date: Jun 6, 2005
 * Time: 11:01:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class APITester4 {


    public static void main(String[] args) {
        try {
            Map m = new HashMap();
	    Vector v1 = new Vector();
	    Vector v2 = new Vector();
	    Vector v3 = new Vector();
	    Vector v = new Vector();
	    v2.addElement("ritu");
	    v2.addElement("test");
	    v3.addElement("sfCodeBase");
	    v3.addElement("http://15.76.97.201:8080/sfExamples.jar");
	    v1.addElement("sfConfig:pair1:messages");
	    v1.addElement(v2);
	    v.addElement(v1);
	    v.addElement(v3);
            String codeBase = "http://lx9622.india.hp.com:8080/AvalancheGUI/Downloader.jsp?ftpComponent.jar";
           // m.put("sfCodeBase", codeBase);
            m.put("sfConfig:newComponentDescription", "D:\\cvs\\forge\\2005\\oct18\\core\\smartfrog\\src\\org\\smartfrog\\examples\\helloworld\\example4.sf");
            m.put("sfConfig:newComponentName",  "ServiceSubProcess1");
            m.put("sfConfig:newComponentCodebase", "http://15.76.97.201:8080/sfExamples.jar" );
	    m.put("sfConfig:newComponentExtraAtributes",v);
            String logFilepath = "c:\\temp";
            //  SmartFrogAdapterImpl.setLogFilePath(logFilepath);
            SmartfrogAdapter sfAdap = new SmartFrogAdapterImpl("D:\\cvs\\forge\\2005\\oct18\\core\\smartfrog\\dist");
        //    Map cd1 = sfAdap.submit("D:\\cvs\\forge\\2005\\oct18\\core\\smartfrog\\src\\org\\smartfrog\\services\\sfinterface\\test\\example_asyndepl1.sf", m, new String[]{"ebnt171", "localhost"});
          // Map cd1 = SmartFrogAdapterImpl.getAllAttribute("org/smartfrog/services/sfinterface/template.sf");
	     Map cd1 = sfAdap.submit("org/smartfrog/services/sfinterface/template.sf", m, new String[]{"ebnt171", "localhost"});
           // Map cd1 = sfAdap.submit("com/hp/grit/avalanche/client/sf/ftp/FTPDownload.sf", m, new String[]{"ebnt171", "localhost"});

            System.out.println("Done- \n" + cd1.toString());
       } catch (SFSubmitException e) {
            e.printStackTrace();
        } catch (SFParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

