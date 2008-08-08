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
package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.processcompound.ProcessCompound;

import java.rmi.RemoteException;
import java.io.Serializable;
import java.io.InputStream;
import java.io.IOException;
import org.smartfrog.sfcore.parser.SFParser;
import java.util.Enumeration;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.security.SFClassLoader;
import java.util.Vector;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.SFLoader;

/**
 * Parse a component
 */
public class ActionParse extends ConfigurationAction implements Serializable {
    public static final String PARSE_MESSAGE = "Parse done.";

    /**
     * this has to be implemented by subclasses; execute a configuration command
     * against a specified target
     *
     * @param targetP   target where to execute the configuration command
     * @param configuration   configuration command to be executed
     * @return Object Reference to parsed component
     * @throws SmartFrogException  failure in some part of the process
     * @throws RemoteException    In case of network/rmi error
     */
    public Object execute(ProcessCompound targetP, ConfigurationDescriptor configuration)
            throws SmartFrogException,
            RemoteException {
        try {
//            if (targetP == null) {
//                targetP = SFProcess.sfSelectTargetProcess(configuration.getHost(),
//                        configuration.getSubProcess());
//            }

            ComponentDescription report =parse(configuration.getUrl());
            configuration.setContextAttribute("parseReport",report);
            configuration.setSuccessfulResult();
            configuration.setResult(ConfigurationDescriptor.Result.SUCCESSFUL,
                    PARSE_MESSAGE,
                    null);
            return report;
        } catch (SmartFrogException sex) {
            configuration.setResult(ConfigurationDescriptor.Result.FAILED,
                    null,
                    sex);
            throw sex;
        }

    }

    /**
     *  Parse a description
     *
     * @param description  description to be parsed
     * @param language     language to be used for parsing
     * @return   ComponentDescription Reference to parsed report
     * @throws SmartFrogException   failure in some part of the process
     */
    private  ComponentDescription parse(String description, String language) throws SmartFrogException {
        long start,finish;
        start=System.currentTimeMillis();
        ComponentDescription report= parseFile(description,language);
        finish = System.currentTimeMillis();
        report.sfAddAttribute("time",new Long(finish-start));
        return report;
    }

    /**
     * Parse a description from String url
     *
     * @param fileUrl url of the description to be parsed
     * @return   ComponentDescription Reference to parsed report
     * @throws SmartFrogException failure in some part of the process
     */
    private ComponentDescription parse(String fileUrl) throws SmartFrogException {
        long start,finish;
        start=System.currentTimeMillis();
        ComponentDescription report = parseFile(fileUrl);
        finish = System.currentTimeMillis();
        report.sfAddAttribute("time",new Long(finish-start));
        return report;
    }

    /**
     * Parse a description File
     * @param description description to be parsed
     * @param language  language to be used fro language
     * @return   ComponentDescription Reference to parsed report
     * @throws SmartFrogException  failure in some part of the process
     */
    private static ComponentDescription parseFile(String description, String language) throws SmartFrogException {
            ComponentDescription report = new ComponentDescriptionImpl(null,  new ContextImpl(), false);
            report.sfAddAttribute("description", description);
            report.sfAddAttribute("language", language);
            try {
                Vector phaseList;
                Phases top;
                try {
                    top = (new SFParser(language)).sfParse(description);
                    report.sfAddAttribute("raw",top.toString());
                    report.sfAddAttribute("raw result","OK");
                } catch (Exception ex) {
                    report.sfAddAttribute("raw result", "FAILED. "+ex.getMessage());
                    throw ex;
                }

                phaseList = top.sfGetPhases();
                String phase;

                for (Enumeration e = phaseList.elements(); e.hasMoreElements(); ) {
                    phase = (String) e.nextElement();
                    try {
                        top = top.sfResolvePhase(phase);
                        report.sfAddAttribute(phase,top.toString());
                        report.sfAddAttribute(phase+" result","OK");
                    } catch (Exception ex) {
                      //report.add("   "+ phase +" phase: "+ex.getMessage());
                      report.sfAddAttribute(""+ phase +" result","FAILED. "+ex.getMessage());
                      throw ex;
                    }
                }

            } catch (Exception e) {
                report.sfAddAttribute("error",e.toString());
            }
            return report;
    }

    /**
     * Parse a description file given String url
     * @param fileUrl url of the description to be parsed
     * @return  ComponentDescription Reference to parsed report
     * @throws SmartFrogException  failure in some part of the process
     */
    private static ComponentDescription parseFile(String fileUrl) throws SmartFrogException {
        ComponentDescription report = new ComponentDescriptionImpl(null, new ContextImpl(), false);
        report.sfAddAttribute("file", fileUrl);
        try {
            String language = getLanguageFromUrl(fileUrl);
            report.sfAddAttribute("language", language);
            //report.add("language: "+language);

            Vector phaseList;
            Phases top;

            InputStream is = null;
            try {
                is = SFLoader.getInputStreamSFException(fileUrl);
                if (is==null) {
                    String msg = MessageUtil.formatMessage(MessageKeys.MSG_URL_TO_PARSE_NOT_FOUND, fileUrl);
                    throw new SmartFrogParseException(msg);
                }
                top = (new SFParser(language)).sfParse(is);
                report.sfAddAttribute("raw", top.toString());
                report.sfAddAttribute("raw result", "OK");
            } catch (Exception ex) {
                report.sfAddAttribute("raw result", "FAILED. "+ex.getMessage());
                throw ex;
            } finally {
                if (is!=null) { try { is.close();} catch (IOException swallowed) { } }
            }
            phaseList = top.sfGetPhases();
            String phase;

            for (Enumeration e = phaseList.elements(); e.hasMoreElements(); ) {
                phase = (String)e.nextElement();
                try {
                    top = top.sfResolvePhase(phase);
                    report.sfAddAttribute(phase, top.toString());
                    report.sfAddAttribute(phase+" result", "OK");
                } catch (Exception ex) {
                    //report.add("   "+ phase +" phase: "+ex.getMessage());
                    report.sfAddAttribute(""+phase+" result","FAILED. "+ex.getMessage());
                    throw ex;
                }
            }

        } catch (Exception e) {
            report.sfAddAttribute("error", e.toString());
        }
        return report;

    }

    /**
     * Gets language from the URL
     *
     * @param url URL passed to application
     * @return Language string
     * @throws Exception In case any error while getting the language string
     */
    private static String getLanguageFromUrl(String url) throws Exception {
        int i = url.lastIndexOf('.');
        if (i <= 0) {
            // i.e. it cannot contain no "." or start with the only "."
            throw new SmartFrogException(
                "unable to source locate language in URL '" + url+"'");
        } else {
            return url.substring(i + 1);
        }
    }

}
