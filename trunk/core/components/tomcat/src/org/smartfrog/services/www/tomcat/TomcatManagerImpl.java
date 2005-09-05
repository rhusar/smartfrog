package org.smartfrog.services.www.tomcat;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

/**
 * This component simpy starts the installed Tomcat.
 */
public class TomcatManagerImpl extends CompoundImpl implements Compound, TomcatManager {

    Reference nameRef = new Reference(ReferencePart.here("name"));	
    String name;	
    //E.g. jakarta-404
    Reference tomcatHomeRef = new Reference(ReferencePart.here("tomcatHome"));	
    String tomcatHome;	
    //E.g. /
    Reference installLocationRef = new Reference(ReferencePart.here("installLocation"));	
    String installLocation;	

    boolean terminated = false;

    //Standard Remote Constructor
    public TomcatManagerImpl() throws RemoteException {
    }

    /**
     * Retrieve the parameters from the sf file
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
	try {
            name = sfResolve(nameRef).toString();
	    tomcatHome = sfResolve(tomcatHomeRef).toString();
	    installLocation = sfResolve(installLocationRef).toString();
	} catch(SmartFrogResolutionException re) {
            name = sfCompleteName().toString();
	}
    }
  
    /**
     * This calls the setTomcatState passing it true to start the instance of
     * Tomcat.
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        setTomcatState(true);
    }
    
    /**
     * This shutsdown the running Tomcat and Terminates the component.
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        setTomcatState(false);
        terminated = true;
        super.sfTerminateWith(tr);
    }    

    /**
     *
     */
    public void setTomcatState(boolean desiredState) {
         try {
             String shellLocation = "/bin/bash";
	     Process p            = Runtime.getRuntime().exec(shellLocation);
	     BufferedReader pOut  = new BufferedReader(new InputStreamReader(p.getInputStream()));
	     BufferedReader pErr  = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	     DataOutputStream dos = new DataOutputStream(p.getOutputStream());
	     
	     dos.writeBytes(". ~/.bashrc"+(char)10);
	     dos.flush();
	     //Whether the desired state is to be running or not
	     if (desiredState) {
	         dos.writeBytes(""+installLocation+tomcatHome+"/bin/startup.sh"+(char)10);
	     } else {
	         dos.writeBytes(""+installLocation+tomcatHome+"/bin/shutdown.sh"+(char)10);
	     }
	     dos.flush();
	     dos.close();

	     try {
                 String out = pOut.readLine();
		 while(out != null) {
                     System.out.println("pOut = "+out);
		     out = pOut.readLine();
		 }
	     } catch(IOException ioe) {
                 System.out.println("IOException ioe = "+ioe);
	     }
	     try {
                 String err = pOut.readLine();
		 while(err != null) {
                     System.out.println("pErr = "+err);
		     err = pOut.readLine();
		 }
	     } catch(IOException ioe) {
                 System.out.println("IOException ioe = "+ioe);
	     }
	 } catch(IOException ioe) {
             System.out.println("IOException ioe = "+ioe);
	 }
    }
	
}
