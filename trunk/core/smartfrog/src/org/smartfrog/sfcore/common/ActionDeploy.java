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
package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author steve loughran
 *         created 18-Mar-2004 10:24:24
 */

public class ActionDeploy extends ConfigurationAction {

    /**
     * Deploy a single application URL.
     *
     * @param appName name of the application
     * @param target  the target process compound to request deployment
     * @throws SmartFrogException       something went wrong with the deploy -this may contain a nested exception
     * @throws java.rmi.RemoteException if anything went wrong over the net
     */
    public static Prim deployFromURL(String url, String appName,
                                     ProcessCompound target)
            throws SmartFrogException, RemoteException {

        /* @Todo there is almost no difference between this method and
        * #deployFromURLsGiven; the latter could
        * have its core replaced by this with some work.*/

        Prim deployedApp = null;
        Context nameContext = null;
        nameContext = new ContextImpl();
        nameContext.put("sfProcessComponentName", appName);

        deployedApp = deployFromURL(url, appName, target, nameContext);
        return deployedApp;

    }

    private static Prim deployFromURL(String url, String appName,
                                      ProcessCompound target,
                                      Context nameContext) throws SmartFrogException,
            RemoteException {

        Prim deployedApp = null;

        InputStream is = null;
        try {
            //assumes that the URL refers to stuff on the classpath
            is = SFClassLoader.getResourceAsStream(url);

            if (is == null) {
                throw new SmartFrogDeploymentException(MessageUtil.
                        formatMessage(MessageKeys.MSG_URL_NOT_FOUND,
                                url, appName));
            }
            deployedApp = deployFrom(is, target, nameContext,
                    SFParser.getLanguageFromUrl(url));
        } catch (SmartFrogException sfex) {
            sfex.put("URL:", url);
            sfex.put("Component Name:", appName);
            throw sfex;
        } catch (RemoteException ex) {
            //rethrow
            throw ex;
        } catch (Exception ex) {
            //anything that was not dealt with gets wrapped
            throw new SmartFrogException(ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                    //TODO
                }
            }
        }
        return deployedApp;
    }

    /**
     * Parses and deploys "sfConfig" from a stream to the target process
     * compound rethrows an exception if it fails, after trying to clean up.
     *
     * @param is       input stream to parse
     * @param target   the target process compound to request deployment
     * @param c        a context of additional attributes that should be set before
     *                 deployment
     * @param language the language whose parser to use
     * @return Reference to deployed component
     * @throws SmartFrogException failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     */
    public static Prim deployFrom(InputStream is, ProcessCompound
            target, Context c, String language) throws SmartFrogException,
            RemoteException {
        assert target!=null;
        Prim comp = null;
        Phases top;
        //To calculate how long it takes to deploy a description
        long deployTime = 0;
        long parseTime = 0;
        if (Logger.logStackTrace) {
            deployTime = System.currentTimeMillis();
        }
        try {
            top = new SFParser(language).sfParse(is);
        } catch (SmartFrogException sfex) {
            throw sfex;
        } catch (Throwable thr) {
            throw new SmartFrogException(MessageUtil.
                    formatMessage(MessageKeys.MSG_ERR_PARSE), thr);
        }
        try {
            top = top.sfResolvePhases();
        } catch (SmartFrogException sfex) {
            throw sfex;
        } catch (Throwable thr) {
            throw new SmartFrogException(MessageUtil.
                    formatMessage(MessageKeys.MSG_ERR_RESOLVE_PHASE), thr);
        }
        try {
            ComponentDescription cd = top.sfAsComponentDescription();
            if (Logger.logStackTrace) {
                parseTime = System.currentTimeMillis() - deployTime;
                deployTime = System.currentTimeMillis();
            }
            comp = target.sfDeployComponentDescription(null, null, cd, c);
            try {
                comp.sfDeploy();
            } catch (Throwable thr) {
                if (thr instanceof SmartFrogLifecycleException)
                    throw (SmartFrogLifecycleException) SmartFrogLifecycleException.forward(thr);
                throw SmartFrogLifecycleException.sfDeploy("", thr, null);
            }
            try {
                comp.sfStart();
            } catch (Throwable thr) {
                if (thr instanceof SmartFrogLifecycleException)
                    throw (SmartFrogLifecycleException) SmartFrogLifecycleException.forward(thr);
                throw SmartFrogLifecycleException.sfStart("", thr, null);
            }
        } catch (Throwable thr) {
            if (comp != null) {
                Reference compName = null;
                try {
                    compName = comp.sfCompleteName();
                } catch (Exception ex) {
                }
                try {
                    comp.sfTerminate(TerminationRecord.
                            abnormal("Deployment Failure: " +
                            thr, compName));
                } catch (Exception ex) {
                }
            }
            throw SmartFrogException.forward(thr);
        }

        if (Logger.logStackTrace) {
            deployTime = System.currentTimeMillis() - deployTime;
            try {
                comp.sfAddAttribute("sfParseTime", new Long(parseTime));
                comp.sfAddAttribute("sfDeployTime", new Long(deployTime));
            } catch (Exception ex) {
                //ignored, this is only information
            }
        }
        return comp;
    }

    /**
     * deploy
     *
     * @param targetP
     * @param configuration
     */
    public Object execute(ProcessCompound targetP,
                        ConfigurationDescriptor configuration) throws SmartFrogException,
            RemoteException {
        Prim prim = deployFromURL(configuration.url, configuration.name, targetP);
        configuration.setSuccessfulResult();
        return prim;
    }

}
