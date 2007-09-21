/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

import org.smartfrog.SFSystem;

import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.rmi.RemoteException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.ConnectException;

import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 *  Creating a Configuration Descriptor with options
 */
public class ConfigurationDescriptor implements MessageKeys {
    private static final String SF_PARSE_TIME = "sfParseTime";
    private static final String SF_DEPLOY_TIME = "sfDeployTime";
    private static final String ATTR_PARSE_REPORT = "parseReport";
    private static final String ATTR_DIAGNOSTICS_REPORT = "diagnosticsReport";

    /**
     * an enumeration of our options
     */
    public static class Action {
        /**
         * private Constructor
         */
        private Action() {
        }

        public static final String ACT_DEPLOY = "DEPLOY";
        public static final int DEPLOY = 0;
        public static final String ACT_TERMINATE = "TERMINATE";
        public static final int TERMINATE = 1;
        public static final String ACT_UNDEFINED = "UNDEFINED";
        public static final int UNDEFINED = 2;
        public static final String ACT_DETACH= "DETACH";
        public static final int DETACH = 3;
        public static final String ACT_DETaTERM = "DETaTERM";
        public static final int DETaTERM = 4;
        public static final String ACT_PING = "PING";
        public static final int PING = 5;
        public static final String ACT_PARSE = "PARSE";
        public static final int PARSE = 6;
        public static final String ACT_DIAGNOSTICS = "DIAGNOSTICS";
        public static final int DIAGNOSTICS = 7;
        public static final String ACT_UPDATE = "UPDATE";
        public static final int UPDATE = 8;
        public static final String ACT_LOAD = "LOAD";
        public static final int LOAD = 9;
        public static final String ACT_DUMP = "DUMP";
        public static final int DUMP = 10;

        static public String[] type= {
                      ACT_DEPLOY,
                      ACT_TERMINATE,
                      ACT_UNDEFINED,
                      ACT_DETACH,
                      ACT_DETaTERM,
                      ACT_PING,
                      ACT_PARSE,
                      ACT_DIAGNOSTICS,
                      ACT_UPDATE,
                      ACT_LOAD,
                      ACT_DUMP
        };
    }


    private String originalSFACT = null;

    private String lineSeparator="\n    ";
    /**
     Action type; one of the Action enumerations. Initially set to
     #Action.UNDEFINED
     */
    private int actionType = Action.UNDEFINED;

    /**
     * the action to perform
     */
    private ConfigurationAction action;

    /**
     * application/component name
     */
    private String name = null;
    /**
     * resource to use during action. Usually a sf description
     */
    private String url = null;

    /**
     * host/hosts where to apply action. Can be null and then no rootProcess is used.
     */
    private String[] hostsList = null;
    /**
     * subProcess where to apply action. Can be null.
     */
    private String subProcess = null;


    /**
     *  SmartFrog context than can be used by ACTIONS
     */
    private Context context=null;




    /**
     * class acting as an enumeration for results
     */
    public static class Result {
        private Result() {
        }

        public static final int SUCCESSFUL=0;
        public static final int FAILED=1;
        public static final int UNDEFINED=2;
        public static final int UNKNOWN=3;
        static String[] type= {"SUCCESSFUL",
                               "FAILED",
                               "UNDEFINED",
                               "UNKNOWN"};
     }

    /**
     * Indicates if "execute" wass call. You need to use resetWasExecuted().
     */
    private boolean wasExecuted = false;

     /**
      * Result type for action
      */
     private int resultType = Result.UNDEFINED;

    /**
      * Result Object return by EXEC action
      */
     private Object resultObject = null;

     /**
      *  A result can be terminated if during a set of deployments one of them
      * fails, this is related to -t (terminate) option and it can only be applied
      * to DEPLOY actions.
      */
     public boolean isResultTerminated = false;

    /**
     * Indicates if the termination of a result object was succesful.
    */
     public boolean isResultTerminatedSuccessfully=true;

     /** possible result termination error message */
     private String resultTerminationMessage=null;

     /**
      * Result message for action
      */
     private String resultMessage = null;


    /**
      * Result exception for action
      */
     private Throwable resultException = null;




     /**
      * Extra parameters for action
      */
     private Hashtable options = new Hashtable();

     /**
      *   Special Options for SF1 Language
      */
     public static class SF1Options {
         static final String SFCONFIGREF = "sfConfigRef";
     }

    /**
     * To String -delegates to {@link #toString(String)}
     * @return  String
     */
    public String toString() {
        return toString(", ");
    }

    /**
     * To String
     * @param separatorString the separator to be used
     * @return String
     */
    public String toString(String separatorString){
        StringBuffer str = new StringBuffer();
        if (getName()!=null) {
            str.append(" name:"); str.append(getName());
        }
        str.append(separatorString);
        str.append(" type:"); str.append(Action.type[actionType]);

        if (getUrl()!=null) {
            str.append(separatorString);
            str.append(" url:"); str.append(getUrl());
        }
        if (getDeployReference()!=null && getDeployReference().size()>0) {
            str.append(separatorString);
            str.append(" depRef:");
            str.append(getDeployReference().toString());
        }
        if (getHostsString()!=null) {
            str.append(separatorString);
            str.append(" host:");
            str.append(getHostsString());
        }
        if (getSubProcess()!=null) {
            str.append(separatorString);
            str.append(" subProc:");
            str.append(getSubProcess());
        }

        str.append(separatorString);
        str.append(" resultType:");
        str.append(Result.type[resultType]);

        if (resultMessage!=null) {
            str.append(separatorString);
            str.append(" resultMessage:");
            str.append(resultMessage);
            }
        if (resultException!=null) {
          str.append(separatorString);
          str.append(" resultExceptionMessage:");
          str.append(resultException.getMessage());
          if (Logger.logStackTrace) {
             str.append(parseExceptionStackTrace(resultException,separatorString));
          }
        }
        return str.toString();
    }

    /**
     *  Gets status message using ', ' as separator
     * @return status message
     */
    public String statusString (){
       String s= ", ";
       return statusString(s);
    }
    /**
     * Gets status message
     * @param separatorString  the separator to be used
     * @return String status message
     */
    public String statusString(String separatorString){
          StringBuffer message = new StringBuffer();
          StringBuffer messageError=null;
          String result = "";
          if ((resultObject!=null)&&(resultObject instanceof Prim)){
            try {
              message.append("'");
              message.append(getResultObjectName());
              message.append("'");
            } catch(Exception ex){
               try {
                 if (getName()!=null) {
                     //This will happen when a component is terminated.
                     message.append(getName());
                     message.append("'");
                 }
               } catch (Exception ex1){
                   message.append(SmartFrogCoreKeys.SF_ROOT_PROCESS);
                   message.append("'");
               }
            }
          } else if (getName()!=null) {
              message.append("'");
              message.append(getName());
              message.append("'");
          }

          if ((getUrl() != null) && !isEmpty(getUrl())) {
              message.append(separatorString);
              message.append(" [");
              message.append(getUrl());
              message.append("]");
          }
          if (getDeployReference()!=null) {
              message.append(separatorString);
              message.append(" deployReference: ");
              message.append(getDeployReference().toString());
          }
          if (getHostsString()!=null) {
              message.append(separatorString);
              message.append(" host:");
              message.append(getHostsString());
          }
          if (getSubProcess()!=null) {
              message.append(separatorString);
              message.append(" subProcess:");
              message.append(getSubProcess());
          }

          if (Logger.logStackTrace) {
            if ( (resultObject != null) && (resultObject instanceof Prim)) {
              try {
                Object time = ( (Prim)resultObject).sfResolveHere(SF_PARSE_TIME);
                message.append(separatorString);
                message.append(" parse time: ");
                message.append(time);
              }
              catch (Exception ex1) {
                //Logger.logQuietly(ex1);
                if (SFSystem.sfLog().isIgnoreEnabled()){
                  SFSystem.sfLog().ignore(ex1);
               }
              }
            }

            if ( (resultObject != null) && (resultObject instanceof Prim)) {
              try {
                Object time = ( (Prim)resultObject).sfResolveHere(SF_DEPLOY_TIME);
                message.append(separatorString);
                message.append(" deploy time: ");
                message.append(time);
              }
              catch (Exception ex1) {
                //Logger.logQuietly(ex1);
                if (SFSystem.sfLog().isIgnoreEnabled()){SFSystem.sfLog().ignore(ex1);}
              }
            }
          }

          if (resultType==Result.SUCCESSFUL){
              switch (getActionType()) {
                case (ConfigurationDescriptor.Action.DEPLOY):
                    {
                    result= MessageUtil.formatMessage(MSG_DEPLOY_SUCCESS, message.toString());
                    }
                    break;
                case ConfigurationDescriptor.Action.DETACH:
                    {
                    result= MessageUtil.formatMessage(MSG_DETACH_SUCCESS, message.toString());
                    }
                    break;

                case ConfigurationDescriptor.Action.DETaTERM:
                    {
                    result= MessageUtil.formatMessage(MSG_DETACH_TERMINATE_SUCCESS, message.toString());
                    }
                    break;
                case ConfigurationDescriptor.Action.TERMINATE:
                   {
                   result= MessageUtil.formatMessage(MSG_TERMINATE_SUCCESS, message.toString());
                   }
                    break;
                case ConfigurationDescriptor.Action.PING: {
                    result = MessageUtil.formatMessage(MSG_PING_SUCCESS,
                                                       name,
                                                       getHostsString(),
                                                       getResultMessage());
                    }
                    break;
                case ConfigurationDescriptor.Action.DUMP: {
                    result = MessageUtil.formatMessage(MSG_DUMP_SUCCESS,
                                                       name,
                                                       getHostsString(),
                                                       getResultMessage());
                    }
                    break;
                 case ConfigurationDescriptor.Action.PARSE: {
                      result = "Parsed :"+getUrl() + lineSeparator
                              + getContextAttribute(ATTR_PARSE_REPORT).toString();
                      }
                     break;

                 case ConfigurationDescriptor.Action.DIAGNOSTICS: {
                    result = "Diagnostics report for "+getName()+ lineSeparator
                            + getContextAttribute(ATTR_DIAGNOSTICS_REPORT).toString();
                    }
                     break;

                  case ConfigurationDescriptor.Action.UPDATE: {
                      result= MessageUtil.formatMessage(MSG_UPDATE_SUCCESS, message.toString());
                     }
                      break;


                default:
                    // Unknown action.
                    messageError = new StringBuffer();
                    messageError.append(Result.type[resultType]);
                    messageError.append(" when trying ");
                    messageError.append(Action.type[actionType]);
                    messageError.append(" of ");
                    messageError.append(message);
                    result= messageError.toString();
                    break;
            }
          } else {
              messageError = new StringBuffer();
              messageError.append(Result.type[resultType]);
              messageError.append(" when trying ");
              messageError.append(Action.type[actionType]);
              messageError.append(" of ");
              messageError.append(message);
              result= messageError.toString();
          }
          if ((Logger.logStackTrace)||
              (((resultMessage != null) || (resultException != null)) && ((resultType != Result.SUCCESSFUL)|| isResultTerminated))) {
                  messageError = new StringBuffer();
                  messageError.append(lineSeparator);
                  messageError.append("Result:");
                  lineSeparator=lineSeparator+"  ";
                  if (resultMessage!=null && !isEmpty(resultMessage)) {
                     messageError.append(lineSeparator);
                     messageError.append("* Message: '");
                     messageError.append(resultMessage.replaceAll("\n",lineSeparator+"    "));
                     messageError.append("'");
                  }
                  if (resultException!=null) {
                      messageError.append(lineSeparator);
                      messageError.append("* Exception: '");
                      messageError.append(parseException(resultException, lineSeparator + "  "));
                      messageError.append("'");
                      if (Logger.logStackTrace) {
                         messageError.append(lineSeparator);
                         messageError.append("* StackTrace: '");
                         messageError.append(parseExceptionStackTrace(resultException, lineSeparator + "    "));
                         messageError.append("'");
                       }
                  }
                  if (isResultTerminated) {
                     messageError.append(lineSeparator);
                     messageError.append("* Result termination  ");
                     if (isResultTerminatedSuccessfully) messageError.append(" succeeded");
                     else messageError.append(" failed");
                     if (resultTerminationMessage!=null) messageError.append("; Message: "+resultTerminationMessage);  
                  }
                  if (originalSFACT!=null && Logger.logStackTrace) {
                      messageError.append(lineSeparator);
                      messageError.append("* Command line SFACT: '");
                      messageError.append(originalSFACT);
                      messageError.append("'");
                  }
                  if (originalSFACT!=null && Logger.logStackTrace) {
                      messageError.append(lineSeparator);
                      messageError.append("* To String: '");
                      messageError.append(toString(separatorString));
                      messageError.append("'");
                  }
                  result = result + messageError.toString();
           }

          return result;
    }

    /**
     * Generates a user friendly message for certain exceptions.
     * @param thr Exception
     * @param separatorString the line separator to be used in the message
     * @return String Exception message
     */
    private String parseException (Throwable thr, String separatorString){
        StringBuffer messageError = new StringBuffer();
        if (thr instanceof SmartFrogException){
            //messageError.append(((SmartFrogException)thr).toString("\n   "));
        } else if (thr instanceof UnknownHostException){
          //Logger.log(MessageUtil.formatMessage(MSG_UNKNOWN_HOST, opts.host), uhex);
          messageError.append( MessageUtil.formatMessage(MSG_UNKNOWN_HOST, getHostsString()));
        } else if (thr instanceof ConnectException){
          //Logger.log(MessageUtil.formatMessage(MSG_CONNECT_ERR, opts.host), cex);
          messageError.append(MessageUtil.formatMessage(MSG_CONNECT_ERR, getHostsString()));
        } else if (thr instanceof RemoteException) {
            //Logger.log(MessageUtil.formatMessage(MSG_REMOTE_CONNECT_ERR,opts.host), rmiEx);
            messageError.append(MessageUtil.formatMessage(MSG_REMOTE_CONNECT_ERR,getHostsString()));
        } else if (thr instanceof Exception) {
            //Logger.log(MessageUtil.formatMessage(MSG_UNHANDLED_EXCEPTION), ex);
            messageError.append(MessageUtil.formatMessage(MSG_UNHANDLED_EXCEPTION)
            );
        }

        if (thr instanceof SmartFrogException) {
            //messageError.append(lineSeparator);
            messageError.append(((SmartFrogException)thr).toString("\n"));
        } else {
            if ( thr !=null) {
                //messageError.append(lineSeparator);
                messageError.append(thr.toString());
            }
        }
        return messageError.toString().replaceAll("\n",separatorString);
    }

    /**
     * Generates a String for the StackTrace using lineSeparator
     * @param thr Exception
     * @param lineSeparator the line separartor to be used in the message
     * @return String Exception message
     */
    public static String parseExceptionStackTrace(Throwable thr, String lineSeparator) {

        if (thr == null) {
            return "";
        }

        final StringWriter sw = new StringWriter(1024);
        PrintWriter out=null;
        LineNumberReader in=null;
        StringBuffer messageError = new StringBuffer(lineSeparator);
        try {
            out = new PrintWriter(sw, false);
            thr.printStackTrace(out);
            out.close();
            in = new LineNumberReader(new StringReader(sw.toString()));
            String result;
            while ((result = in.readLine()) != null) {
                messageError.append(result);
                messageError.append(lineSeparator);
            }
        } catch (IOException ex) {
            // this should REALLY never happen
            throw new RuntimeException(ex.toString(),ex);
        } finally {
            if (in != null)
                try {in.close();} catch (IOException ignored) {}
            if (out!= null) {
                out.close();
            }
        }
        if (thr instanceof SmartFrogException) {
            //messageError.append(lineSeparator);
            messageError.append(((SmartFrogException) thr).toStringAll(lineSeparator));
        }
        return messageError.toString();
    }

    /**
     * this is a constant that defines what the inter-element token is when cracking
     * the string
     */
    private static final String separator = ":";

    /**
     * Internal buffer to parse urls.
     */
    private String tempURL = null;
    /**
     * Creates a Configuration Descriptor using a deployment URL
     * @param deploymentURL Format: 'name:ACTION:url:sfConfig:HOST:PROCESS'
     * <pre>
     *      - name: name where to apply ACTION
     *            ex. foo
     *            ex. "HOST localhost:foo"
     *            ex. 'HOST localhost:foo'
     *      - ACTION: possible actions: DEPLOY, TERMINATE, DETACH, DETaTERM, PING, PARSE, DIAGNOSTICS, UPDATE, LOAD
     *      - url: description used by ACTION
     *            ex. /home/sf/foo.sf
     *            ex. "c:\sf\foo.sf"
     *            ex. "c:\My documents\foo.sf"
     *            ex. 'c:\My documents\foo.sf'
     *      - target: component description name to use with action. It can be empty
     *            ex: foo
     *            ex: "first:foo"
     *            ex: 'first:foo'
     *            note: sfConfig cannot be use with DEPLOY!
     *      - HOST: host name or IP where to apply ACTION. When empty it assumes localhost.
     *            ex: localhost
     *            ex: 127.0.0.1
     *            ex(multiple host):["127.0.0.1","localhost"]
     *      - PROCESS: process namewhere to apply ACTION. When empty it assumes rootProcess
     *     ex1: Deploy a description in local daemon
     *        counterEx:DEPLOY:org/smartfrog/examples/counter/example.sf::localhost:
     *     ex2. Terminate local sfDaemon
     *        rootProcess:TERMINATE:::localhost:
     *     ex3: Deploy "counterToSucceed" from counter/example2.sf
     *        counterEx3:DEPLOY:org/smartfrog/examples/counter/example2.sf:"testLevel1:counterToSucceed":localhost:
     *</pre>
     * @see Action
     * @throws SmartFrogInitException  failure in some part of the process
     */
    public ConfigurationDescriptor (String deploymentURL) throws SmartFrogInitException {
        try {
            originalSFACT = deploymentURL;
            if (SFSystem.sfLog().isDebugEnabled()){SFSystem.sfLog().debug("Parsing SFACT: ["+originalSFACT+"]"); }

            if (deploymentURL==null) {
                throw new SmartFrogInitException("Deployment URL: null");
            }

            String item = null;
            deploymentURL = deploymentURL.trim();
            tempURL = deploymentURL;

            if (deploymentURL.length() < 1) {
                throw new SmartFrogInitException("Deployment URL: wrong format");
            }

            //GET SUBPROCESS_NAME (6th Element)
            try {
                setSubProcess(getAndCutLastFieldTempURL(separator));
            } catch (Exception ex) {
                throw new SmartFrogInitException( "Error parsing SUBPROCESS_NAME in: "+ deploymentURL+"("+ex.getMessage()+")", ex);
            }

            //GET HOST_NAME (5th Element)
            try {
                setHost(getAndCutLastFieldTempURL(separator));
            } catch (Exception ex) {
                throw new SmartFrogInitException("Error parsing HOST in: "+ deploymentURL+"("+ex.getMessage()+")", ex);
            }

            //GET DEPLOY_REFERENCE (4th Element)
            //If it contains : has to be between double quotes(")
            //ex. ...:"componentOne:componentTwo":...
            try {
                setDeployReference(getAndCutLastFieldTempURL(separator));
            } catch (Exception ex) {
                throw new SmartFrogInitException("Error parsing DEPLOY_REFERENCE in: "+ deploymentURL +"("+ex.getMessage()+")", ex);
            }

            //GET URL (3rd Element)
             //(Everything that is left at the end)
             try {
                 setUrl(getAndCutLastFieldTempURL(separator));
             } catch (Exception ex) {
                 throw new SmartFrogInitException( "Error parsing DEPLOY_REFERENCE in: "+ deploymentURL+"("+ex.getMessage()+")", ex);
            }

            //GET ACTION(2nd Element)
            try {
                setActionType(getAndCutLastFieldTempURL(separator));
            } catch (Exception ex) {
                throw new SmartFrogInitException("Error parsing ACTION_TYPE in: "+ deploymentURL +"("+ex.getMessage()+")", ex);
            }

            // GET NAME (1st Element)
            //Check if url starts with " and extract name:
            // Valid examples:
            // "HOST guijarro.hpl.hp.com":rootProcess:sfDefault:display":TER:::localhost:subprocess;
            // "HOST "127.0.0.1":rootProcess:sfDefault:display":TER:::localhost:subprocess;
            // 'HOST "127.0.0.1":rootProcess:sfDefault:display':TER:::localhost:subprocess;
            // display:TER:::localhost:subprocess;
            // :TER:::localhost:subprocess; ->Unnamed component
            try {
                String field ="";
                if (tempURL.trim().endsWith("\"")||tempURL.trim().endsWith("'")) {
                   field = tempURL.substring(1,tempURL.length()-1);
                   // shell like input will take away the " but if using -f
                   // then the " will be there
                   if (field.trim().endsWith("\"")||field.trim().endsWith("'")) {
                      field = field.substring(1,field.length()-1);
                   }

                } else {
                    field = tempURL;
                }
                if (SFSystem.sfLog().isTraceEnabled()) {SFSystem.sfLog().trace("  Extracted ["+field+"] from ["+tempURL+"]"); }
                setName(field);
            } catch (Exception ex) {
                SFSystem.sfLog().error(ex);
                throw new SmartFrogInitException("Error parsing NAME in: "+ deploymentURL+"("+ex.getMessage()+")", ex);
            }
            if (SFSystem.sfLog().isDebugEnabled()){SFSystem.sfLog().debug("Parsing SFACT results: ["+this+"]");}
        } catch (Throwable thr){
           resultException = thr;
           throw (SmartFrogInitException)SmartFrogInitException.forward(thr);
        }
    }


    /** String name for attribute '{@value}'. */
    final static String ATR_NAME = "name";
    /** String name for attribute '{@value}'. */
    final static String ATR_ACTION = "action";
    /** String name for attribute '{@value}'. */
    final static String ATR_DESC_URL = "descriptionURL";
    /** String name for attribute '{@value}'. */
    final static String ATR_TARGET = "targetReference";
    /** String name for attribute '{@value}'. */
    final static String ATR_HOST = "host";
    /** String name for attribute '{@value}'. */
    final static String ATR_PROCESS = "process";

    /**
     * Creates a Configuration Descriptor using a ComponentDescription
     * @param ComponentDescription Format:
     * <pre>
     *      CfgDesc extends {
     *         name;
     *         action;
     *         descriptionURL;
     *         targetReference;
     *         host;
     *         process;
     *      }
     *
     * Where:
     *      - name: name where to apply ACTION
     *            ex. "foo"
     *            ex. "HOST localhost:foo"
     *      - action: possible actions: DEPLOY, TERMINATE, DETACH, DETaTERM, PING, PARSE, DIAGNOSTICS, UPDATE, LOAD  {@see type}
     *      - descriptionURL: description used by ACTION
     *            ex. "/home/sf/foo.sf"
     *            ex. "c:\sf\foo.sf"
     *            ex. "c:\My documents\foo.sf"
     *      - target: component description name to use with action. It can be empty
     *            ex: "foo"
     *            ex: "first:foo"
     *            note: "sfConfig" cannot be use with DEPLOY!
     *      - host: host name or IP where to apply ACTION. When empty it assumes localhost.
     *            ex: "localhost"
     *            ex: "127.0.0.1"
     *            ex(multiple host):["127.0.0.1","localhost"]
     *      - process: process namewhere to apply ACTION. When empty it assumes rootProcess
     *
     *</pre>
     * @see Action
     * @throws SmartFrogInitException  failure in some part of the process
     */
    public ConfigurationDescriptor (ComponentDescription cd) throws SmartFrogInitException {
        if (cd==null) {
                throw new SmartFrogInitException("Deployment URL: null");
        }
        try {

            try {
                setName(cd.sfResolve(ATR_NAME, "", false));
            } catch (Exception ex) {
                SFSystem.sfLog().error(ex);
                throw new SmartFrogInitException("Error parsing NAME in: "+ cd +"("+ex.getMessage()+")", ex);
            }

            try {
                setActionType(cd.sfResolve(ATR_ACTION, "", true));
            } catch (Exception ex) {
                SFSystem.sfLog().error(ex);
                throw new SmartFrogInitException("Error parsing ACTION_TYPE in: "+ cd +"("+ex.getMessage()+")", ex);
            }                       

            try {
                setUrl(cd.sfResolve(ATR_DESC_URL, "", false));
            } catch (Exception ex) {
                 SFSystem.sfLog().error(ex);
                 throw new SmartFrogInitException( "Error parsing DESCRIPION_URL in: "+ cd +"("+ex.getMessage()+")", ex);
            }

            try {
                setDeployReference(cd.sfResolve(ATR_TARGET, "", false));
            } catch (Exception ex) {
                throw new SmartFrogInitException("Error parsing TARGET_REFERENCE in: "+ cd +"("+ex.getMessage()+")", ex);
            }

            try {
                setHost (cd.sfResolve(ATR_HOST, "", false));
            } catch (Exception ex) {
                throw new SmartFrogInitException("Error parsing HOST in: "+ cd +"("+ex.getMessage()+")", ex);
            }

            try {
                setSubProcess (cd.sfResolve(ATR_PROCESS, "", false));
            } catch (Exception ex) {
                throw new SmartFrogInitException( "Error parsing PROCESS_NAME in: "+ cd +"("+ex.getMessage()+")", ex);
            }

            if (SFSystem.sfLog().isDebugEnabled()){SFSystem.sfLog().debug("ConfigurationDescriptor created: ["+this+"], from "+cd);}

        } catch (Throwable thr){
           resultException = thr;
           throw (SmartFrogInitException)SmartFrogInitException.forward(thr);
        }
    }

    /**
     * Returns and cuts the last field from TempURL. Token marks the beginning
     * of the field. It previously removes " or '
     * @param token token marks the beginning of the field
     * @return String last field from TempURL marked by token
     * @throws java.lang.Exception  failure in some part of the process
     */
    private String getAndCutLastFieldTempURL( String token) throws Exception{
        String field = null;
        if (tempURL.trim().endsWith("\"")||tempURL.trim().endsWith("'")) {
            String tag ="\"";
            if (tempURL.trim().endsWith("'")) tag="'";
            String newURL = tempURL.substring(0, tempURL.length()-1).trim();
            int indexFirstQuote = newURL.lastIndexOf(tag);
            field = tempURL.substring(indexFirstQuote+1,tempURL.length()-1).trim();
            // shell like input will take away the " but if using -f
            // then the " will be there
            if (field.trim().endsWith("\"")||field.trim().endsWith("'")) {
               field = field.substring(1,field.length()-1).trim();
            }

            if (SFSystem.sfLog().isTraceEnabled()) {SFSystem.sfLog().trace("  Extracted ["+field+"] from ["+tempURL+"]"); }
            if (indexFirstQuote==-1) {
                indexFirstQuote = 1;
            }
            tempURL=tempURL.substring(0,indexFirstQuote-1).trim();
        } else {
            field = tempURL.substring(tempURL.lastIndexOf(token)+1, tempURL.length()).trim();
            if (SFSystem.sfLog().isTraceEnabled()) {SFSystem.sfLog().trace("  Extracted ["+field+"] from ["+tempURL+"]"); }
            tempURL = (tempURL.substring(0, tempURL.lastIndexOf(token)).trim());
        }
        return field;
    }
    /**
     *
     * @param name application/component name
     * @param url resource to use during action. Usually a sf description
     */
    public ConfigurationDescriptor (String name, String url){
        if (url == null) return;
        setUrl(url);
        setName(name);
    }

    /**
     *
     * @param name application/component name
     * @param url resource to use during action. Usually a sf description
     * @param actionType @see Action inner class for valid types
     * @param host host were to apply action. Can be null and then no rootProcess is used.
     * @param subProcess subProcess were to apply action. Can be null.
     * @throws SmartFrogInitException
     */
    public ConfigurationDescriptor (String name, String url,int actionType,String host, String subProcess)
            throws SmartFrogInitException{
        setActionType(actionType);
        setUrl(url);
        setName(name);
        setHost(host);
        setSubProcess(subProcess);
    }

    /**
     * empty constructor for people who know what they are doing
     */
    public ConfigurationDescriptor() {
    }

    /**
     * Creates Configuration Descriptor
     * @param name application/component name
     * @param url resource to use during action. Usually a sf description
     * @param actionType @see Action inner class for valid types
     * @param deployReference reference used for final resolve of a configuration
     * @param host host were to apply action. Can be null and then no rootProcess is used.
     * @param subProcess subProcess were to apply action. Can be null.
     * @throws SmartFrogInitException when a parameter is wrongly defined
     * @throws SmartFrogResolutionException if something cannot be resolved
     */
    public ConfigurationDescriptor (String name, String url,int actionType,
                                    String deployReference ,String host, String subProcess)
            throws SmartFrogInitException, SmartFrogResolutionException {

        setActionType(actionType);
        setUrl(url);
        setName(name);
        // Deploy Reference is a particular case for SF1 and therefore added to
        // options
        setDeployReference(deployReference);
        setHost(host);
        setSubProcess(subProcess);
    }

    /**
     * Test for a non-null string being empty
     * @param string string to trim and check the size
     * @return true if there is nothing but white space in the string
     */
    private static boolean isEmpty(String string) {
        return string.trim().length()==0;
    }

    /**
     * Gets defined use for final resolve of a configuration
     * @return Reference deployReference
     * @see Reference
     */
    public Reference getDeployReference(){
        String key = SF1Options.SFCONFIGREF;
        if (getOptions().containsKey(key)){
            return ((Reference)getOptions().get(key));
        }
        return null;
    }

    /**
     * Sets reference use for final resolve of a configuration
     * By default is will use 'sfConfig'.
     * @see Reference
     * @param reference
     * @throws SmartFrogResolutionException failure in resolving
     */
    public void setDeployReference(String reference) throws SmartFrogResolutionException{

        if (isEmpty(reference)){
            return;
        }
        getOptions().put(SF1Options.SFCONFIGREF,
                                  Reference.fromString(reference));
    }

    /**
     * Gets action type
     * @return int action type
     */
    public int getActionType(){
        return actionType;
    }

    /**
     * To set all attributes for any result
     * @param type Type of result
     * @see Result inner class
     * @param message result message
     * @param thr result exception if it existed
     */
    public void setResult(int type, String message, Throwable thr) {
        if ((type<0)||(type>Result.type.length)) {
            try {
                throw new SmartFrogInitException("Result type unknown");
            } catch (Exception ex) {
              SFSystem.sfLog().trace(ex);
            }
        } else resultType = type;
        if (message!=null) resultMessage = message;
        if (thr!=null) resultException = thr;
    }

    /**
     * Sets result as SUCCESSFULL
     */
    public void setSuccessfulResult(){
      resultType=Result.SUCCESSFUL;
    }

    /**
     * set the action type. this also sets the {@link #action} attribute
     * to an instance of the action which is needed to do the actual execution.
     *
     * @param type action type
     * @throws SmartFrogInitException  if the type is not valid
     */
    public void setActionType(int type) throws SmartFrogInitException {
        actionType = type;
        switch(actionType) {
            case Action.DEPLOY:
                action=new ActionDeploy();
                break;
            case Action.DETACH:
                action = new ActionDetach();
                break;
            case Action.TERMINATE:
                action = new ActionTerminate();
                break;
            case Action.DETaTERM:
                action = new ActionDetachAndTerminate();
                break;
            case Action.PING:
                action = new ActionPing();
                break;
            case Action.PARSE:
                action = new ActionParse();
                break;
            case Action.DIAGNOSTICS:
                action = new ActionDiagnostics();
                break;
            case Action.UPDATE:
                action = new ActionUpdate();
                break;
            case Action.LOAD:
                action = new ActionLoad();
                break;
            case Action.DUMP:
                action = new ActionDump();
                break;
            default:
                throw new SmartFrogInitException("Action type unknown");
        }
    }


    /**
     * Sets action type.
     * @param type action type
     * @throws SmartFrogInitException if the type is not valid
     * @see Action inner class for valid types
     */

    public void setActionType(String type) throws SmartFrogInitException {
        for (int i=0;i<Action.type.length;i++) {
            if(Action.type[i].equals(type)) {
                setActionType(i);
                return;
            }
        }
        //only get here on failure
        throw new SmartFrogInitException("Action type unknown: " + type);
    }

    /**
     * Gets result type
     * @return int result type
     *
     */
    public int getResultType() {
        return resultType;
    }

    /**
     * Parses resultMessage and resultException and generates one
     * message.
     * @return String result message
     *
     */
    public String getResultMessage() {
        if (resultMessage !=null) {
            return resultMessage;
        } else if (resultException !=null) {
            return resultException.getMessage();
        }
        return "no message";
    }

    /**
     * Performs the nominated action
     * @param targetProcess optional target process; set to null to
     * hand off process lookup to the ConfigurationAction subclass.
     * It keeps a reference to the result object in resultObject.
     * @return the object created from the operation
     * @throws SmartFrogException if smartfrog is unhappy
     * @throws RemoteException if the network is unhappy
     */
    public synchronized Object execute(ProcessCompound targetProcess) throws SmartFrogException,
            RemoteException {
        try {
            if (action==null) {
                throw new SmartFrogInitException("No valid action");
            }
            if (targetProcess==null) {
                resultObject = action.execute(this);
            } else {
                resultObject = action.execute(targetProcess, this);
            }
        } catch (SmartFrogException sex){
             setResult(ConfigurationDescriptor.Result.FAILED,null,sex);
             throw sex;
         } catch (RemoteException rex){
             setResult(ConfigurationDescriptor.Result.FAILED,null,rex);
             throw rex;
        }
        wasExecuted = true; //even if it failed. Should be reset by using resetExecute();
        return resultObject;
    }

    public synchronized void resetExecute (){
        wasExecuted = false;
    }

    public boolean wasExecuted (){
        return wasExecuted;
    }

    /** Terminates result object ONLY a result exists and it was deployed by this configuration descriptor using
     * a DEPLOY action. The success of the termination intent will be stored in {@link #isResultTerminatedSuccessfully}
     * @return  true if it tried to terminate result, false it it did not try
     */
    public boolean terminateDeployedResult (){
       if ((resultObject!=null)&& (resultObject instanceof Prim) &&
               (getActionType() == Action.DEPLOY) && !isResultTerminated){
            try {
             this.isResultTerminated=true;
             ((Prim)resultObject).sfTerminate(new TerminationRecord(TerminationRecord.
                    NORMAL,
                    "Multiple deployment failed", null));
            } catch (RemoteException ex) {
              this.resultTerminationMessage = ex.toString();
            }
            return true;
       }

       return false;
    }

    /**
     * get the name of this component
     * @return String component name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets result termination message. wasExecuted() should be true for a valid result. Null otherwise.
     * @return String termination message
     */
    public String getResultTerminationMessage() {
        return resultTerminationMessage;
    }

    /**
     * Gets result Termination exception if any. wasExecuted() should be true for a valid result. Null otherwise.
     * @return Throwable exception
     */
    public Throwable getResultException() {
        return resultException;
    }

    /**
     * Get action result. wasExecuted() should be true for a valid result. Null otherwise.
     * @return action result
     */
    public Object getResultObject() {
        return resultObject;
    }

    /**
     * get the name of resultObject only if it is a Prim. Otherways returns null
     * @return String  name of resultObject
     */
    public String getResultObjectName() {
        if ((resultObject != null) && (resultObject instanceof Prim)) {
            try {
                return (((Prim) resultObject).sfCompleteName().toString());
            } catch (RemoteException ex) {
                //Note that if the object was terminated this call will throw an exception.
                return null;
            }
        }
        return null;
    }


    /**
     * set the name of this component
     * @param name component name
     */
    public void setName(String name) {
        if (isEmpty(name)) {
            return;
        }
        this.name = name;
    }

    /**
     * Get resource to use during action.
     * @return a url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set resource to use during action.
     * @param url resource url
     */
    public void setUrl(String url) {
        // Added to avoid problems with "" in shell scripts (Unix vs Windows)
        url=url.trim();
        if (url.startsWith("\"")&&(url.endsWith("\""))){
           url=url.substring(1,url.length()-1);
        }
        this.url = url;
    }

    /**
     * host for action. May be null
     * @return  String hostname
     */
    public String[] getHosts() {
        return hostsList;
    }

    /**
     * host (first on the list) for action. May be null
     * @return  String hostname
     */
    public String getHost() {
        if ((hostsList == null)||(hostsList.length==0)) return null;
        return hostsList[0];
    }

    /**
     * host/s string representation. May be null when no hosts in list
     * @return  String hostname/s
     */
    public String getHostsString() {
        if (hostsList == null) return null;
        if (hostsList.length==1) return hostsList[0];
        else return Arrays.toString(hostsList);
    }

    /**
     * Host where to apply action. Can be null and then no rootProcess is used or can be a list
     * with the format [...,...,...]
     * This creates a list of one host only.
     * @param hostsString hostname/hostnames
     */
    public void setHost(String hostsString) throws SmartFrogInitException {
        if (hostsString==null || isEmpty(hostsString)) return;
        this.hostsList = getHostList(hostsString);
    }

    /**
     * Hosts where to apply action. Can be null and then no rootProcess is used.
     * @param hosts hostname
     */
    public void setHosts(String[] hosts) {
        if (hosts==null || hosts.length == 0) return;

        this.hostsList = hosts;
    }


    public String[] getHostList (String hostUrlString) throws SmartFrogInitException{
        String[] hostList = null;
        if (hostUrlString.startsWith("[")){
            if (!hostUrlString.endsWith("]")) throw new SmartFrogInitException( "Error parsing HOST_URLString in: "+ hostUrlString +", missing ']'");
            String newURLList = hostUrlString.substring(1, hostUrlString.length()-1).trim();
            hostList= newURLList.split(",");
        } else {
            //Remove [] and break the list in individual strings for separate hosts.
            // Assumes only one
            hostList = new String[1];
            hostList[0]=hostUrlString;
        }
        return hostList;
    }

    /**
     * subProcess where to apply action. Can be null.
     * @return string or null
     */
    public String getSubProcess() {
        return subProcess;
    }

    /**
     * Set the subProcess where to apply action.
     * Can be null or white space, in which case it is ignored
     * @param subProcess subProcess name
     */
    public void setSubProcess(String subProcess) {
        if (subProcess==null || isEmpty(subProcess)) return;
        this.subProcess = subProcess;
    }

    /**
     * get option hashtable. This is not a copy; it is a direct
     * accessor to the table.
     * @return  Hashtable option hashtable
     */
    public Hashtable getOptions() {
        return options;
    }

    /**
     * option table
     * @param options option hashtable
     */
    public void setOptions(Hashtable options) {
        this.options = options;
    }

    /**
     * Adds the name to option hashtable
     * @param name Object name
     * @param value value
     */
    public void setOption(Object name, Object value) {
       options.put(name, value);
    }

    /**
     * Gets the value from option hashtable
     * @param option Option name
     * @return Object value
     */
    public Object getOption(Object option){
       return options.get(option);
    }

    /**
     * Get Context
     * @return Context
     */
    public Context getContext (){
       return context;
    }

    /**
     * Set Context
     * @param context Context
     */
    public void setContext(Context context){
       this.context=context;
    }

    /**
     * Set Context attributes
     * @param name attribute name
     * @param value attribute value
     * @return Context
     */
    public Context setContextAttribute(Object name, Object value) {
        if (context==null) {
            context=new ContextImpl();
        }
        context.put(name,value);
        return context;
    }

    /**
     * Get Context Attribute
     * @param attributeName attribute name
     * @return Object attribute value
     */
    public Object getContextAttribute(Object attributeName){
       if (context==null)  return null;
       return context.get(attributeName);
    }
}
