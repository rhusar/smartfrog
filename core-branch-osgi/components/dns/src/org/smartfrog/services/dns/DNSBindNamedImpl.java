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

package org.smartfrog.services.dns;

import java.rmi.RemoteException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.common.Context;







/**
 * A named daemon wrapper for Bind 9. It can control the life-cycle 
 * of named if necessary, i.e., start, stop, clean-up... 
 *
 * 
 * 
 */
public class DNSBindNamedImpl extends DNSNamedImpl implements DNSNamed {

    /** The directory with (live) config  files.*/
    File configDir = null;

    /** The directory with "named" binaries.*/
    String binDir = null;
 
    /** The main configuration file name.*/
    String configFile = null;

    /** A static reference for the directory with (live) config  files.*/
    static final Reference REF_CONFIGDIR =
        new Reference(ReferencePart.here("configDir"));

    /** A static reference for the directory with named binaries.*/
    static final Reference REF_BINDIR =
        new Reference(ReferencePart.here("binDir"));
    
    /** A static reference for the main configuration file name.*/
    static final Reference REF_CONFIGFILE =
        new Reference(ReferencePart.here("configFile"));
     
    /** A binary name to invoke rndc. */
    public static final String RNDC = "rndc";

    /** A binary name to invoke "named". */
    public static final String NAMED = "named";


    /**
     * Creates a new <code>DNSBindNamedImpl</code> instance.
     *
     * @exception RemoteException if an error occurs
     */
    public DNSBindNamedImpl() 
        throws RemoteException {

    }


    
    /**
     * sfDeploy lifecycle method as required by SmartFrog
     * @exception SmartFrogException if an error occurs
     * @exception RemoteException if an error occurs
     */
    public synchronized void sfDeploy()
        throws SmartFrogException, RemoteException {
        
        super.sfDeploy();
        
        configDir =  new File(sfResolve(REF_CONFIGDIR, "", true));
        binDir =  sfResolve(REF_BINDIR, "", true);
        configFile =  sfResolve(REF_CONFIGFILE, "", true);
    }

    /**
     * Starts the named daemon unless it is already started.
     *
     * @exception DNSException Error while starting the named
     * daemon.
     */
    public synchronized void start() 
        throws DNSException {

        try {
            File conf = new File(configDir, configFile);
            if (!(conf.exists() && conf.canRead())) {
                throw new DNSException("Cannot open config file " + conf);
            }
            if (!execCommand(binDir, NAMED, "-u named -c " 
                             + conf.getCanonicalPath(),
                             configDir)) {
                throw new DNSException("Cannot start daemon");
            }        
            assertStatus(true);
        } catch (IOException e) {
            throw new DNSException("Cannot start daemon", e);
        }
    }



    /**
     * Stops the daemon, cleans up all the configuration
     * changes using dynamic updates, and patches the "fresh"
     * config files so that it can be
     * re-started in a known state.
     *
     * @exception DNSException Error while stopping/cleaning
     * the named daemon.
     */
    public synchronized void cleanUp() throws DNSException {
        
        // just in case...
        try {
            if (status()) {
                stop();
            }
        } catch (DNSException e) 
{
            // It is normal that it was not running
        }
        deleteConfigFiles();
        updateConfigFiles();                
        // change ownership to "named"
        try {
            if (!execCommand("/bin", "chown",
                             "-R named " + configDir.getCanonicalPath(), 
                             configDir)) {
                throw new DNSException("cannot chown files");
            }
        } catch (IOException e) {
            throw new DNSException("cannot chown files", e);
        }
    }


    /**
     * Deletes the previous configuration files if any.
     *
     * @exception DNSException if an error occurs
     */
    void deleteConfigFiles()
        throws DNSException {

        if (!configDir.exists()) {
            return;
        }
        
        File[] allFiles = configDir.listFiles();
        if (allFiles == null) {
            // not a directory...
            configDir.delete();
        }
        for (int i = 0; i < allFiles.length; i++) {
            // assumed no non-empty sub-directories to be deleted...
            allFiles[i].delete();
        }       
    }

    /**
     * Updates the config file and creates all the needed zone files.
     *
     * @exception DNSException if an error occurs
     */
    void updateConfigFiles() 
        throws DNSException {

        FileOutputStream fout = null;
        try {
            if (!configDir.exists()) {           
                configDir.mkdir();
            }
            File conf = new File(configDir, configFile);
            fout = new FileOutputStream(conf);
            PrintWriter out = new PrintWriter(fout);
            data.printNamedConfig(out);
            out.flush();
            out.close();
            dumpSOARecords(data, configDir, true);
        } catch (IOException e) {
            throw new DNSException("cannot update config file" , e);
        }
    }


    /**
     * Dumps all the zone records in a given hierarchy.
     *
     * @param comp A hierarchy of components.
     * @param dir A directory for the zone records files.
     * @param overwrite Whether to override existing files.
     * @exception DNSModifierException if an error occurs while
     * writing the files.
     */
    public static void dumpSOARecords(DNSComponent comp, File dir, 
                                      boolean overwrite)
        throws DNSModifierException {
        
        try {
            CDVisitor vis = DNSZoneImpl.getWriteSOARecordVisitor(dir,
                                                                 overwrite);
            comp.visit(vis, true);
        } catch (Exception e) {
            throw new  DNSModifierException("Can't dump records", e);
        }
    }
       

    /**
     * Stops the named daemon  assuming it is currently running.
     *
     * @exception DNSException Error while stopping the named
     * daemon.
     */
    public synchronized void stop() 
        throws DNSException{

        if (!execCommand(binDir, RNDC, "stop", configDir)) {
            throw new DNSException("Cannot stop daemon");
        }
        assertStatus(false);
    }

    /**
     * Flushes all the caches. This allows forward views or zones
     * to ensure they will get the most up to date information.
     *
     * @exception DNSException Error while flushing caches in the named
     * daemon.
     */
    public void flush()
        throws DNSException {
        
        if (!execCommand(binDir, RNDC, "flush", configDir)) {
            throw new DNSException("Cannot flush the daemon");
        }
        assertStatus(true);
    }
    
    /**
     * Executes a "native" command, i.e., outside java. This implementation
     * does not spawn a separate thread, does not read the output/error stream
     * of the process, and it just hangs if something failed or the process
     * blocks while filling up its output buffers. So for anything other than
     * a "trivial" command use RunShellImpl instead ...
     *
     * @param cmdPath A directory path for the command binary.
     * @param command The executable file name
     * @param args A string with all the arguments.
     * @param workDir A directory used as "working directory" for the process.
     * @return True if the command returns no error code, false otherwise.
     */
    boolean execCommand(String cmdPath, String command, String args,
                        File workDir) 
        throws DNSException {

        try {
            if (!workDir.exists()) {
                throw new IllegalArgumentException("Cannot find working dir "
                                                   + workDir);
            }
            File cmdFile = new File(cmdPath, command);
            if (!cmdFile.exists()) {
                throw new IllegalArgumentException("Cannot find exec "
                                                   + cmdFile);
            }
        
            String cmdLine = cmdFile.getCanonicalPath() + " " + args;
            // use current shell properties for invocation
            Process proc = Runtime.getRuntime().exec(cmdLine, null,
                                                     workDir);
            int exitVal = proc.waitFor();
            return (exitVal == 0 ? true : false);
        } catch (Exception e) {
            throw new DNSException("cannot exec command", e);
        }
    }

}
