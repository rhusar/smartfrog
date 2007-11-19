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

package org.smartfrog.examples.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.Writer;
import java.rmi.RemoteException;
import java.util.Date;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.PrettyPrinting;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimHook;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;

/**
 * Persist a component to a file
 */
public class SFPersist extends PrimImpl implements Prim {

    /**
     * the firectory for persistng the contexts
     */
    private String directory;

    /* the hook objects */
    private PrimHook sfPersister = new SfPersister();
    private PrimHook sfDePersister = new SfDePersister();

    /**
     * the log component to which to generate the trace logs
     */
    private LogSF log = null;

    /* number to add to filename to make unique */
    private int nextInt = 0;

    /**
     * Constructor.
     *
     * @throws RemoteException in case of network/rmi error
     */
    public SFPersist() throws RemoteException {
    }

    /**
     * Deploys the component.
     *
     * @throws SmartFrogException in case of error while deploying
     * @throws RemoteException    in case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();

        log = sfGetCoreLog();

        directory = sfResolve("directory", directory, true);
    }

    /**
     * Starts the component.
     *
     * @throws SmartFrogException in case of error in starting
     * @throws RemoteException    in case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();

        restartComponents();

        // applied by default
        sfDeployWithHooks.addHook(sfPersister);
        sfTerminateWithHooks.addHook(sfDePersister);
    }

    /**
     * Terminate the component.
     *
     * @param r TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord r) {
        try {
            sfDeployWithHooks.removeHook(sfPersister);
            sfTerminateWithHooks.removeHook(sfDePersister);
            // remove file
        } catch (Exception e) {

        }
        super.sfTerminateWith(r);
    }


    private void restartComponents() throws SmartFrogException {
        File restartDirectory = new File(directory);
        File[] files = restartDirectory.listFiles(new sfFileFilter());

        for (int i = 0; i < files.length; i++) {
            deployFile(files[i]);
        }
    }

    private void deployFile(File f) throws SmartFrogException {
        try {
            if (log.isInfoEnabled()) {
                log.info("Restart description from file " + f);
            }
            Phases top = (new SFParser("sf")).sfParse(new FileInputStream(f));
            ComponentDescription topd = top.sfAsComponentDescription(); // should b fully resolved,,,
            System.out.println("topd " + topd);
            SFProcess.getProcessCompound().sfCreateNewApp(null, topd, null);
        } catch (Exception e) {
            SmartFrogException.forward("Error redeploying persisted component from file " +
                    f, e);
        }
    }

    private class sfFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.endsWith(".sf");
        }
    }

    /**
     * Utility inner class- persister functinoality
     */
    private class SfPersister implements PrimHook {
        /**
         * sfHookAction for deploying
         *
         * @param prim              prim component
         * @param terminationRecord TerminationRecord object
         * @throws SmartFrogException in case of any error
         */
        public void sfHookAction(Prim prim,
                TerminationRecord terminationRecord)
                throws SmartFrogException {
            boolean sfPersist = false;
            // write the context to the file relative to the directory...
            try {
                System.out.println("running hook");
                if (prim.sfParent() instanceof ProcessCompound) {
                    sfPersist = prim.sfResolve("sfPersist", sfPersist, false);
                    if (sfPersist) {
                        String time = new Long(new Date().getTime()).toString();
                        String filename = "sfPersist." +
                                time +
                                "." +
                                nextInt++ +
                                ".sf";
                        prim.sfReplaceAttribute("sfPersisted", filename);
                        prim.sfReplaceAttribute("sfPersistedDirectory",
                                directory);
                        Context context = prim.sfContext();

                        if (log.isInfoEnabled()) {
                            log.info("persisting description to file " +
                                    filename);
                        }
                        Writer out = new FileWriter(new File(directory,
                                filename));
                        ((PrettyPrinting) context).writeOn(out, 1);
                        out.close();
                    }
                }
            } catch (Exception e) {
                // ignore
                if (log.isErrorEnabled()) {
                    log.error("Error persisting component to file " +
                            sfPersist,
                            e);
                }
                throw SmartFrogException.forward("Error persisting component to file " +
                        sfPersist, e);
            }
        }
    }

    /**
     * Utility inner class- removing persister functinoality
     */
    private class SfDePersister implements PrimHook {
        /**
         * sfHookAction for terminating
         *
         * @param prim              prim component
         * @param terminationRecord TerminationRecord object
         * @throws SmartFrogException in case of any error
         */
        public void sfHookAction(Prim prim,
                TerminationRecord terminationRecord)
                throws SmartFrogException {
            File file = null;
            // write the context to the file relative to the directory...
            try {
                System.out.println("running termination hook");
                String filename = prim.sfResolve("sfPersisted", "", false);
                if (!filename.equals("")) {
                    file = new File(directory, filename);
                    boolean deleted = file.delete();
                    if (log.isDebugEnabled()) {
                        log.debug("Persisted file " +
                                file +
                                " removal: result: " +
                                deleted);
                    }
                }
            } catch (Exception e) {
                // ignore
                if (log.isErrorEnabled()) {
                    log.error("Error persisting component to file " + file, e);
                }
                throw SmartFrogException.forward("Error persisting component to file " +
                        file, e);
            }
        }
    }

}
