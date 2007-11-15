package org.smartfrog.services.sfinterface.test;

import org.smartfrog.services.sfinterface.SFParseException;
import org.smartfrog.services.sfinterface.SFSubmitException;
import org.smartfrog.services.sfinterface.SmartFrogAdapterImpl;
import org.smartfrog.services.sfinterface.SmartfrogAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class AttrTester {


    public static void main(String[] args) {
        try {
       
	   Map cd1 = SmartFrogAdapterImpl.getAllAttribute("C:\\StartComponent.sf");
            System.out.println("Done- \n" + cd1.toString());
	   System.out.println("==============");
	   Map cd2 = SmartFrogAdapterImpl.getAllAttribute("C:\\StartComponent.sf", "user");
            System.out.println("Done- \n" + cd2.toString());
       } catch (SFSubmitException e) {
            e.printStackTrace();
        } catch (SFParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

