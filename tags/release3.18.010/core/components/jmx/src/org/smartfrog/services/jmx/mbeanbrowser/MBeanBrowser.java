/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jmx.mbeanbrowser;

import java.io.*;
import java.net.MalformedURLException;
import java.awt.*;
import javax.swing.UIManager;

import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.componentdescription.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.services.jmx.communication.ConnectionFactory;
import org.smartfrog.services.jmx.communication.ServerAddress;
import java.util.Vector;

//import com.sun.management.jmx.*;
import mx4j.*;
import java.rmi.RemoteException;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class MBeanBrowser extends PrimImpl implements Prim, Serializable {
    MainFrame frame = null;
    boolean packFrame = false;

    String URL = null;
    Boolean sfDetach = null;
    Boolean connect = null;
    int period = 0;
    int retries = 0;

    final static String usage = "\n\n" +
            "Usage: java -D... com.hp.SmartJMX.mbeanbrowser.MBeanBrowser [[-u url_protocol] -h host -p port -s service]\n" +
            "  ,or: java -D... com.hp.SmartJMX.mbeanbrowser.MBeanBrowser [rmi://host:port/service]\n";


    /**
     *  Constructs the application *
     *
     *@exception  Exception  Description of the Exception
     */
    public MBeanBrowser() throws Exception {
        // Parse system properties to check if LEVEL_TRACE and/or LEVEL_DEBUG are set
        // and enable the TRACE level accordingly
        //try {
            //Trace.parseTraceProperties();
     /*   } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    /**
     *  Initialises the Main frame
     *
     *@param  args           Description of the Parameter
     *@exception  Exception  Description of the Exception
     */

    public void init(String args[]) throws Exception {
        if ((args != null) && (args.length > 0)) {
            ServerAddress url = checkArguments(args);
            System.out.println(url.getHost()+":"+url.getPort()+"/"+url.getResource());
            frame = new MainFrame(url);
            System.out.println("GOT MAINFRAME FROM URL");
        } else {
            frame = new MainFrame();
        }
        //Validate frames that have preset sizes
        //Pack frames that have useful preferred size info, e.g. from their layout
	    System.out.println("I M HERE         5");
        if (packFrame) {
            frame.pack();
        } else {
            frame.validate();
        }
	    System.out.println("I M HERE         6");
        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize((int) screenSize.getWidth() * 4 / 5, (int) screenSize.getHeight() * 4 / 5);
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
    }


    /**
     *  Main method *
     *
     *@param  args           The command line arguments
     *@exception  Exception  Description of the Exception
     */
    public static void main(String[] args) throws Exception {
//    try {
//      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//    }
//    catch(Exception e) {
//      e.printStackTrace();
//    }
        try {
            MBeanBrowser mbeanbrowser = new MBeanBrowser();
            mbeanbrowser.getConfigFrom(getMBeanBrowserDescription());
            if (args.length == 0) {
                args = new String[]{mbeanbrowser.URL};
            }
            // If there is no arguments, we use the default url
	    System.out.println("I M HERE         1");
            mbeanbrowser.init(args);
	    System.out.println("I M HERE         2");
            if (mbeanbrowser.frame!=null){
                mbeanbrowser.frame.systemExit=true; // Will do a system.exit when window closed.
            }

	    System.out.println("I M HERE         3");
            mbeanbrowser.setConfig();
	    System.out.println("I M HERE         4");
       } catch (java.net.MalformedURLException me) {
            System.err.println( "Error: "+ me.getMessage()+ ". URL example: rmi://localhost:3800/RMIConnectorServer"+usage);
       } catch (java.lang.IllegalArgumentException ae) {
            System.err.println( "Error: "+ ae.getMessage()+""+usage);
       }
    }


    /**
     *  Description of the Method
     *
     *@param  args           Description of the Parameter
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    private ServerAddress checkArguments(String[] args) throws Exception {
        if (args == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        switch (args.length) {
            case 1:
                if ((args[0].charAt(0))!='-') {
                  return ConnectionFactory.parseURL(args[0]);
                }
            default:
                return parseArguments(args);
        }
    }

    /**
     *  Description of the Method
     *
     *@param  args                          Description of the Parameter
     *@return                               Description of the Return Value
     *@exception  IllegalArgumentException  Description of the Exception
     *@exception  MalformedURLException     Description of the Exception
     */
    private ServerAddress parseArguments(String args[]) throws IllegalArgumentException, MalformedURLException {
        char optionFlagIndicator = '-';
        boolean host = false;
        boolean port = false;
        boolean service = false;

        String protocol = args[0];
        ServerAddress parsed=null;
        if (protocol.charAt(0)!='-') {
            parsed = ConnectionFactory.createServerAddress(protocol, null, -1, null);
        } else {
            parsed = ConnectionFactory.createServerAddress("rmi", null, -1, null);
        }
        int i = 0;
        while (i < args.length) {
            if (args[i].charAt(0) == optionFlagIndicator) {
                switch (args[i].charAt(1)) {
                    case '?':
                        System.out.println("sfMBeanBrowser help:"+usage);
                        System.exit(0);
                        break;
                    case 'u':
                        try {
                            parsed = ConnectionFactory.createServerAddress(args[++i], parsed.getHost(), parsed.getPort(), parsed.getResource());
                        } catch (Exception e) {
                            throw new IllegalArgumentException("getting url protocol, " + e.getMessage());
                        }
                        break;
                    case 'h':
                        if (host) {
                            throw new IllegalArgumentException("at most one -h allowed");
                        }
                        host = true;
                        try {
                            parsed.setHost(args[++i]);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("getting host, " + e.getMessage());
                        }
                        break;
                    case 'p':
                        if (port) {
                            throw new IllegalArgumentException("at most one -p allowed");
                        }
                        port = true;
                        try {
                            parsed.setPort(Integer.parseInt(args[++i]));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("port must be a number");
                        } catch (Exception e) {
                            throw new IllegalArgumentException("getting port, "+ e.getMessage());
                        }
                        break;
                    case 's':
                        if (service) {
                            throw new IllegalArgumentException("at most one -p allowed");
                        }
                        service = true;
                        try {
                            parsed.setResource(args[++i]);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("getting service, "+e.getMessage());
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("unknown option " + args[i].charAt(1));
                }
            } else {
                throw new IllegalArgumentException("illegal option format for option " + args[i]);
            }
            i++;
        }
        if (host & port & service) {
            return parsed;
        } else {
            throw new IllegalArgumentException("arguments missing");
        }
    }


    /**
     *  Gets the mBeanBrowserDescription attribute of the MBeanBrowser class
     *
     *@return                The mBeanBrowserDescription value
     *@exception  Exception  Description of the Exception
     */
    public static ComponentDescription getMBeanBrowserDescription() throws Exception {
        InputStream is = SFClassLoader.getResourceAsStream("org/smartfrog/services/jmx/mbeanbrowser/MBeanBrowser.sf");
        Phases descr = (new SFParser("sf").sfParse(is));
        //Vector phases = new Vector();
   //     phases.add("type");
     //   phases.add("link");
       // descr.sfResolvePhases(phases);
       ComponentDescription cd = descr.sfAsComponentDescription();
       //System.out.println("COMP====" + cd.toString());
     //  ComponentDescription comp = (ComponentDescription) cd.sfResolveHere(new Reference(ReferencePart.here("MBeanBrowser")));
       Reference ref = Reference.fromString("MBeanBrowser"); 	
       Object comp = cd.sfResolve(ref);
      System.out.println("DESCR====" + comp.toString());       
       return (ComponentDescription) comp;
    }


    /**
     *  Gets the configFrom attribute of the MBeanBrowser object
     *
     *@param  cd  Description of the Parameter
     */
    public void getConfigFrom(ComponentDescription cd) {
        Context context = cd.sfContext();
        URL = (String) context.get("url");
        sfDetach = (Boolean) context.get("sfDetach");
        connect = (Boolean) context.get("connectWhenStarted");
        period = ((Integer) context.get("heartBeatPeriod")).intValue();
        retries = ((Integer) context.get("heartBeatRetries")).intValue();
    }


    /**
     *  Gets the configFrom attribute of the MBeanBrowser object
     *
     *@param  prim           Description of the Parameter
     *@exception  Exception  Description of the Exception
     */
    public void getConfigFrom(Prim prim) throws Exception {
        URL = (String) sfResolveHere("url");
        sfDetach = (Boolean) sfResolveHere("sfDetach");
        connect = (Boolean) sfResolveHere("connectWhenStarted");
        period = ((Integer) sfResolveHere("heartBeatPeriod")).intValue();
        retries = ((Integer) sfResolveHere("heartBeatRetries")).intValue();
    }


    /**
     *  Sets the config attribute of the MBeanBrowser object
     */
    public void setConfig() {
        try {
            if (sfDetach.booleanValue()) {
                this.sfDetach();
            }
        } catch (Exception e) {}
        frame.setHeartBeatPeriod(period);
        frame.setHeartBeatRetries(retries);
        if (connect.booleanValue()) {
            frame.connect();
        }
    }


    /**
     *  SmartFrog Lifecycle, just in case of usig it as a SF component *
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        try {
          super.sfDeploy();
          getConfigFrom(this);
          init(new String[] {URL});
          setConfig();
        } catch (Exception ex) {
          throw SmartFrogException.forward(ex);
        }
    }


    /**
     *  Description of the Method
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
    }


    /**
     *  Description of the Method
     *
     *@param  tr  Description of the Parameter
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        System.out.println("In MBeanBrowser, sfTerminatedWith called " + tr.toString());
        try {
            frame.disconnect();
            // To unregister as Listener
            frame.dispose();
            //System.exit(0);
        } catch (Exception e) {}
        super.sfTerminateWith(tr);
    }

}
