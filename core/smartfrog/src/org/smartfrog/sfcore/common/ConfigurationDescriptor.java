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

import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.reference.Reference;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.rmi.RemoteException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.ConnectException;



import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.processcompound.ProcessCompound;

public class ConfigurationDescriptor implements MessageKeys{
    public static class Action {
        final static public int DEPLOY=0;
        final static public int TERMINATE=1;
        final static public int UNDEFINED=2;
        final static public int DETACH=3;
        final static public int DETaTERM=4;
        static public String[] type= {"DEPLOY",
                      "TERMINATE",
                      "UNDEFINED",
                      "DETACH",
                      "DETaTERM"};
    }
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
     * host where to apply action. Can be null and then no rootProcess is used.
     */
    private String host = null;
    /**
     * subProcess where to apply action. Can be null.
     */
    private String subProcess = null;

    /**
     * class acting as an enumeration for results
     */
    public static class Result {
        final static public int SUCCESSFUL=0;
        final static public int FAILED=1;
        final static public int UNDEFINED=2;
        final static public int UNKNOWN=3;
        static String[] type= {"SUCCESSFUL",
                               "FAILED",
                               "UNDEFINED",
                               "UNKNOWN"};
     }

     /**
      * Result type for action
      */
     private int resultType = Result.UNDEFINED;
     /**
      * Result message for action
      */
     private String resultMessage = null;
     /**
      * Result exception for action
      */
     public Throwable resultException = null;
     /**
      * Extra parameters for action
      */
     private Hashtable options = new Hashtable();

     /**
      *   Special Options for SF1 Language
      */
     public static class SF1Options {
         static String SFCONFIGREF = "sfConfigRef";
     }

    /**
     * To String
     * @return
     */
    public String toString() {
        //return toString(", \n");
        return toString(", ");
    }

    /**
     * To String
     * @param separator
     * @return
     */
    public String toString(String separator){
        StringBuffer str = new StringBuffer();
        if (getName()!=null) {
            str.append(" n:"); str.append(getName().toString());
        }
        str.append(separator);
        str.append(" t:"); str.append(Action.type[actionType].toString());

        if (getUrl()!=null) {
            str.append(separator);
            str.append(" u:"); str.append(getUrl().toString());
        }
        if ((getDeployReference()!=null)&&(getDeployReference().size()>0)) {
            str.append(separator);
            str.append(" d:"); str.append(getDeployReference().toString());
        }
        if (getHost()!=null) {
            str.append(separator);
            str.append(" h:"); str.append(getHost().toString());
        }
        if (getSubProcess()!=null) {
            str.append(separator);
            str.append(" s:"); str.append(getSubProcess().toString());
        }

        str.append(separator);
        str.append(" rt:"); str.append(Result.type[resultType].toString());

        if (resultMessage!=null) {
            str.append(separator);
            str.append(" rm:");  str.append(resultMessage.toString());
            }
        if (resultException!=null) {
            str.append(separator);
            str.append(" rex:"); str.append(resultException.getMessage());}
        return str.toString();
    }

    /**
     *  Gets status message using ', ' as separator
     * @return status message
     */
    public String statusString (){
       String separator= ", ";
       return statusString(separator);
    }
    /**
     * Gets status message
     * @param separator
     * @return message
     */
    public String statusString(String separator){
          StringBuffer message = new StringBuffer();
          String result = null;

          if (getName()!=null) {
              message.append(""); message.append(getName().toString());
          }

          if (getUrl()!=null) {
              message.append(separator);
              message.append(" ["); message.append(getUrl().toString()+"]");
          }
          if (getDeployReference()!=null) {
              message.append(separator);
              message.append(" deployReference: "); message.append(getDeployReference().toString());
          }
          if (getHost()!=null) {
              message.append(separator);
              message.append(" host:"); message.append(getHost().toString());
          }
          if (getSubProcess()!=null) {
              message.append(separator);
              message.append(" subProcess:"); message.append(getSubProcess().toString());
          }

          if (this.resultType==Result.SUCCESSFUL){
              switch (this.getActionType()) {
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
                default:
                    // Unknown action.
            }
            return result;
          } else {
              StringBuffer messageError = new StringBuffer();
              messageError.append(""); messageError.append(Result.type[resultType].toString());
              messageError.append(" when trying "); messageError.append(Action.type[actionType].toString());
              messageError.append(" of ");
              messageError.append(message);
              if ((resultMessage!=null)||(resultException!=null)) {
                  messageError.append(separator);
                  messageError.append("\n   Error:");
                  if (resultMessage!=null) {
                     resultMessage.toString();
                  }
                  if (resultException!=null) {
                      messageError.append("\n   "+parseException(resultException));
                  }
              }
              result= messageError.toString();
          }
          return result;
    }

    /**
     * Generates a user friendly message for certain exceptions.
     * @param thr Exception
     * @return message
     */
    private String parseException (Throwable thr){
        StringBuffer messageError = new StringBuffer();
        if (thr instanceof SmartFrogException){
            //messageError.append(((SmartFrogException)thr).toString("\n   "));
        } else if (thr instanceof UnknownHostException){
          //Logger.log(MessageUtil.formatMessage(MSG_UNKNOWN_HOST, opts.host), uhex);
          messageError.append( MessageUtil.formatMessage(MSG_UNKNOWN_HOST, host));
        } else if (thr instanceof ConnectException){
          //Logger.log(MessageUtil.formatMessage(MSG_CONNECT_ERR, opts.host), cex);
          messageError.append(MessageUtil.formatMessage(MSG_CONNECT_ERR, host));
        } else if (thr instanceof RemoteException) {
            //Logger.log(MessageUtil.formatMessage(MSG_REMOTE_CONNECT_ERR,opts.host), rmiEx);
            messageError.append(MessageUtil.formatMessage(MSG_REMOTE_CONNECT_ERR,host));
        } else if (thr instanceof Exception) {
            //Logger.log(MessageUtil.formatMessage(MSG_UNHANDLED_EXCEPTION), ex);
            messageError.append(MessageUtil.formatMessage(MSG_UNHANDLED_EXCEPTION));
        }

        if (thr instanceof SmartFrogException) {
            messageError.append("  ");
            messageError.append(((SmartFrogException)thr).toString("\n     "));
        } else {
            messageError.append("\n     ");
            messageError.append(thr.toString());
        }
        return messageError.toString();
    }


    /**
     * this is a constant that defines what the inter-element token is when cracking
     * the string
     */
    private static final String separator = ":";

    /**
     * Creates a Configuration Descriptor using a deployment URL
     * @param deploymentURL Format: 'name:ACTION:url:sfConfig:HOST:PROCESS'
     *      - name: name where to apply ACTION
     *      - ACTION: possible actions: DEPLOY, TERMINATE, DETACH, DETaTERM
     *      - url: description used by ACTION
     *      - target: for now only 'sfConfig' or 'empty' are considered.
     *      - HOST: host name or IP where to apply ACTION. When empty it assumes localhost.
     *      - PROCESS: process namewhere to apply ACTION. When empty it assumes rootProcess\n" +
     *     ex. Deploy a description
     *        counterEx:DEPLOY:org/.../example.sf:sfConfig:localhost:process
     *     ex. Terminate local sfDaemon
     *        rootProcess:TERMINATE:::localhost:
     *
     * @throws SmartFrogInitException
     *
     * @todo fix this text for JavaDocs
     */
    public ConfigurationDescriptor (String deploymentURL) throws SmartFrogInitException {
        try {
            if (deploymentURL==null) {
                throw new SmartFrogInitException("Deployment URL: null");
            }
            String tempURL = null;
            String item = null;
            deploymentURL = deploymentURL.trim();
            if (deploymentURL.length() < 1) throw
                    new SmartFrogInitException("Deployment URL: wrong format");
            //Check if url starts with " and extract name:
            //"HOST guijarro-j-3.hpl.hp.com:rootProcess:sfDefault:display":TER:::localhost:subprocess;
            //display:TER:::localhost:subprocess;
            try {
                if (deploymentURL.startsWith("\"")) {
                    setName(deploymentURL.substring(1,
                        deploymentURL.indexOf("\"", 1)));
                    tempURL = deploymentURL.substring(deploymentURL.
                        indexOf("\"", 1)+2);
                } else {
                    setName(deploymentURL.substring(0,
                        deploymentURL.indexOf(separator)));
                    tempURL = deploymentURL.substring(deploymentURL.indexOf(
                            separator)+1);
                }
                if (this.getName().equals(" ")) {
                    this.setName(null);
                }
            } catch (Exception ex) {
                throw new SmartFrogInitException("Error parsing NAME in: "+
                                                 deploymentURL, ex);
            }

            String[] deploymenturl = tempURL.split(":",-1);

            if (deploymenturl.length<1) throw
                    new SmartFrogInitException("Deployment URL: wrong format");
            try {
                if (deploymenturl[3]!=null) {
                    //Logger.log("Type: "+(String)deploymenturl[0]);
                    this.setActionType(deploymenturl[0]);
                }
            } catch (Exception ex) {
                throw new SmartFrogInitException(
                    "Error parsing ACTION_TYPE in: "+
                    deploymentURL, ex);
            }
            try {
              if (deploymenturl[3]!=null) {
                  //Logger.log("Url: "+(String)deploymenturl[1]);
                  this.setUrl(deploymenturl[1]);
                  if (this.getUrl().equals(" ")) {
                      this.setUrl(null);
                  }
              }
            } catch (Exception ex) {
                throw new SmartFrogInitException("Error parsing URL in: "+
                                                 deploymentURL, ex);
            }
            try {
                //Logger.log("DeployRef: "+(String)deploymenturl[2]);
                this.setDeployReference(deploymenturl[2]);
            } catch (Exception ex) {
                throw new SmartFrogInitException(
                    "Error parsing DEPLOY_REFERENCE in: "+
                    deploymentURL, ex);
            }
            try {
                //Logger.log("host: "+(String)deploymenturl[3]);
                if (deploymenturl[3]!=null) {
                    this.setHost((deploymenturl[3]));
                    if (this.getHost().equals("")) {
                        this.setHost(null);
                    }
                }
            } catch (Exception ex) {
                throw new SmartFrogInitException("Error parsing HOST in: "+
                                                 deploymentURL, ex);
            }
            try {
              if (!deploymentURL.endsWith(":")) {
                    //Logger.log("subproc: "+(String)deploymenturl[4]);
                if (deploymenturl[4]!=null) {
                    this.setSubProcess(deploymenturl[4]);
                    if (this.getSubProcess().equals("")) {
                        this.setSubProcess(null);
                    }
                }
              }
            } catch (Exception ex) {
                throw new SmartFrogInitException(
                    "Error parsing SUBPROCESS in: "+
                    deploymentURL, ex);
            }
        } catch (Throwable thr){
           this.resultException = thr;
           throw (SmartFrogInitException)SmartFrogInitException.forward(thr);
        }
    }

    /**
     *
     * @param name application/component name
     * @param url resource to use during action. Usually a sf description
     */
    public ConfigurationDescriptor (String name, String url){
        if (url == null) return;
        this.setUrl(url);
        this.setName(name);
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

        this.setActionType(actionType);
        this.setUrl(url);
        this.setName(name);
        this.setHost(host);
        this.setSubProcess(subProcess);
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
     */
    public ConfigurationDescriptor (String name, String url,int actionType,
                                    String deployReference ,String host, String subProcess)
            throws SmartFrogInitException{

        this.setActionType(actionType);
        this.setUrl(url);
        this.setName(name);
        // Deploy Reference is a particular case for SF1 and therefore added to
        // options
        this.setDeployReference(deployReference);
        this.setHost(host);
        this.setSubProcess(subProcess);
    }

    /**
     * Gets defined use for final resolve of a configuration
     * @return deployReference
     * @see Reference
     */
    private Reference getDeployReference(){
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
     */
    private void setDeployReference(String reference){

        if (reference.trim().equals("")){
            return;
        }
        this.getOptions().put(SF1Options.SFCONFIGREF, new Reference (reference));
    }

    /**
     * Gets action type
     * @return
     */
    public int getActionType(){
        return actionType;
    }

    /**
     * To set all attributes for any result
     * @param type Type of result @see Result inner class
     * @param message result message
     * @param thr result exception if it existed
     */
    public void setResult(int type, String message, Throwable thr) {
        if ((type<0)||(type>Result.type.length)) {
            try {
                throw new SmartFrogInitException("Result type unknown");
            } catch (Exception ex) {
                Logger.log(ex);
            }
        } else this.resultType = type;
        if (message!=null) this.resultMessage = message;
        if (thr!=null) this.resultException = thr;
    }

    /**
     * Sets result as SUCCESSFULL
     */
    public void setSuccessfulResult(){
      this.resultType=Result.SUCCESSFUL;
    }

    /**
     * set the action type. this also sets the #action attribute
     * which is needed to do the actual execution.
     *
     * @param type
     * @throws SmartFrogInitException
     */
    public void setActionType(int type) throws SmartFrogInitException {
        this.actionType = type;
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
            default:
                throw new SmartFrogInitException("Action type unknown");
        }
    }


    /**
     * Sets action type.
     * @throws SmartFrogInitException it the type is not valid
     * @see Action inner class for valid types
     */

    public void setActionType(String type) throws SmartFrogInitException {
        if (type.equals(Action.type[Action.DEPLOY])) {
            setActionType(Action.DEPLOY);
        } else if (type.equals(Action.type[Action.DETACH])) {
            setActionType(Action.DETACH);
        } else if (type.equals(Action.type[Action.DETaTERM])) {
            setActionType(Action.DETaTERM);
        } else if (type.equals(Action.type[Action.TERMINATE])) {
            setActionType(Action.TERMINATE);
        } else {
            throw new SmartFrogInitException("Action type unknown: "+ type);
        }
    }


    public int getResultType() {
        return resultType;
    }

    /**
     * Parses resultMessage and resultException and generates one
     * message.
     * @return message
     *
     */
    public String getResultMessage() {
        if (this.resultMessage!=null)
            return resultMessage;
        else if (this.resultException!=null)
            return resultException.getMessage();
        return "no message";
    }

    /**
     * Performs the nominated action
     * @param targetProcess optional target process; set to null to
     * hand off process lookup to the ConfigurationAction subclass.
     * @return the object created from the operation
     * @throws SmartFrogException if smartfrog is unhappy
     * @throws RemoteException if the network is unhappy
     */
    public Object execute(ProcessCompound targetProcess) throws SmartFrogException,
            RemoteException {
        if(action ==null) {
            throw new SmartFrogInitException("No valid action");
        }
        if(targetProcess==null) {
            return action.execute(this);
        } else {
            return action.execute(targetProcess,this);
        }
    }

    /**
     * get the name of this component
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * set the name of this component
     * @param name
     */
    public void setName(String name) {
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
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * host for action. May be null
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     * host where to apply action. Can be null and then no rootProcess is used.
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * subProcess where to apply action. Can be null.
     * @return string or null
     */
    public String getSubProcess() {
        return subProcess;
    }

    /**
     * set subProcess where to apply action. Can be null.
     * @param subProcess
     */
    public void setSubProcess(String subProcess) {
        this.subProcess = subProcess;
    }

    /**
     * get option hashtable. This is not a copy; it is a direct
     * accessor to the table.
     * @return
     */
    public Hashtable getOptions() {
        return options;
    }

    /**
     * option table
     * @param options
     */
    public void setOptions(Hashtable options) {
        this.options = options;
    }


}
