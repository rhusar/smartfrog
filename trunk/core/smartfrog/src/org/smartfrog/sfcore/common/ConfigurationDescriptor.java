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

import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;

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
    //Action type
    private int actionType = Action.UNDEFINED;

    public String name = null;
    public String url = null;
   // public String deployReference = null;

    // Location
    public String host = null;
    public String subProcess = null;

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

    private int resultType = Result.UNDEFINED;
    private String resultMessage = null;
    public Throwable resultException = null;

    public Hashtable options = new Hashtable();

    //Special Options for SF1 Language
    public static class SF1Options {
            static String SFCONFIGREF= "sfConfigRef";
    }

    public String toString() {
        //return toString(", \n");
        return toString(", ");
    }

    public String toString(String separator){
        StringBuffer str = new StringBuffer();
        if (name!=null) {
            str.append(" n:"); str.append(name.toString());
        }
        str.append(separator);
        str.append(" t:"); str.append(Action.type[actionType].toString());

        if (url!=null) {
            str.append(separator);
            str.append(" u:"); str.append(url.toString());
        }
        if (getDeployReference()!=null) {
            str.append(separator);
            str.append(" d:"); str.append(getDeployReference().toString());
        }
        if (host!=null) {
            str.append(separator);
            str.append(" h:"); str.append(host.toString());
        }
        if (subProcess!=null) {
            str.append(separator);
            str.append(" s:"); str.append(subProcess.toString());
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

    public String statusString (){
       String separator= ", ";
       return statusString(separator);
    }
    public String statusString(String separator){
          StringBuffer message = new StringBuffer();
          String result = null;

          if (name!=null) {
              message.append(""); message.append(name.toString());
          }

          if (url!=null) {
              message.append(separator);
              message.append(" ["); message.append(url.toString()+"]");
          }
          if (getDeployReference()!=null) {
              message.append(separator);
              message.append(" deployReference: "); message.append(getDeployReference().toString());
          }
          if (host!=null) {
              message.append(separator);
              message.append(" host:"); message.append(host.toString());
          }
          if (subProcess!=null) {
              message.append(separator);
              message.append(" subProcess:"); message.append(subProcess.toString());
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
              if (resultMessage!=null) {
                  messageError.append(separator);
                  messageError.append(" Error:"); messageError.append(resultMessage.toString());
              }
              if (resultException!=null) {
                  messageError.append(separator);
                  messageError.append(" Exception:"); messageError.append(resultException.getMessage());
              }
              result= messageError.toString();
          }
          return result;
    }


    String token = ":";

    public ConfigurationDescriptor (String deploymentURL) throws SmartFrogInitException {
        try {
            if (deploymentURL==null)
                throw new SmartFrogInitException("Deployment URL: null");
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
                    name = deploymentURL.substring(1,
                        deploymentURL.indexOf("\"", 1));
                    tempURL = deploymentURL.substring(deploymentURL.
                        indexOf("\"", 1)+2);
                } else {
                    name = deploymentURL.substring(0,
                        deploymentURL.indexOf(token));
                    tempURL = deploymentURL.substring(deploymentURL.indexOf(
                        token)+1);
                }
                if (this.name.equals(" ")) {
                    this.name = null;
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
                    this.setActionType((String)deploymenturl[0]);
                }
            } catch (Exception ex) {
                throw new SmartFrogInitException(
                    "Error parsing ACTION_TYPE in: "+
                    deploymentURL, ex);
            }
            try {
              if (deploymenturl[3]!=null) {
                  //Logger.log("Url: "+(String)deploymenturl[1]);
                  this.url = (String)deploymenturl[1];
                  if (this.url.equals(" ")) {
                      this.url = null;
                  }
              }
            } catch (Exception ex) {
                throw new SmartFrogInitException("Error parsing URL in: "+
                                                 deploymentURL, ex);
            }
            try {
                //Logger.log("DeployRef: "+(String)deploymenturl[2]);
                this.setDeployReference((String)deploymenturl[2]);
            } catch (Exception ex) {
                throw new SmartFrogInitException(
                    "Error parsing DEPLOY_REFERENCE in: "+
                    deploymentURL, ex);
            }
            try {
                //Logger.log("host: "+(String)deploymenturl[3]);
                if (deploymenturl[3]!=null) {
                    this.host = ((String)deploymenturl[3]);
                    if (this.host.equals("")) {
                        this.host = null;
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
                    this.subProcess = (String)deploymenturl[4];
                    if (this.subProcess.equals("")) {
                        this.subProcess = null;
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


    public ConfigurationDescriptor (String name, String url){
        if (url == null) return;
        this.url = url;
        this.name=name;
    }

    public ConfigurationDescriptor (String name, String url,int actionType,String host, String subProcess)
            throws SmartFrogInitException{

        this.setActionType(actionType);
        this.url = url;
        this.name=name;
        this.host=host;
        this.subProcess=subProcess;
    }

    public ConfigurationDescriptor (String name, String url,int actionType,
                                    String deployReference ,String host, String subProcess)
            throws SmartFrogInitException{

        this.setActionType(actionType);
        this.url = url;
        this.name=name;
        // Deploy Reference is a particular case for SF1 and therefore added to
        // options
        this.setDeployReference(deployReference);
        this.host=host;
        this.subProcess=subProcess;
    }

    private Reference getDeployReference(){
        String key = SF1Options.SFCONFIGREF;
        if (options.containsKey(key)){
            return ((Reference)options.get(key));
        }
        return null;
    }

    private void setDeployReference(String reference){
        if (reference.equals(" ")){
            return;
        }
        this.options.put(SF1Options.SFCONFIGREF, new Reference (reference));
    }
    public int getActionType(){
        return actionType;
    }

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

    public void setSuccessfulResult(){
      this.resultType=Result.SUCCESSFUL;
    }

    public void setActionType(int type) throws SmartFrogInitException {
        if ((type<0)||(type>Action.type.length)) {
            throw new SmartFrogInitException("Action type unknown");
        }
        this.actionType = type;
    }

    public void setActionType(String type) throws SmartFrogInitException {
        if (type.equals(Action.type[Action.DEPLOY])) {
            this.actionType=Action.DEPLOY;
        } else if (type.equals(Action.type[Action.DETACH])) {
            this.actionType=Action.DETACH;
        } else if (type.equals(Action.type[Action.DETaTERM])) {
            this.actionType=Action.DETaTERM;
        } else if (type.equals(Action.type[Action.TERMINATE])) {
            this.actionType=Action.TERMINATE;
        } else {
            throw new SmartFrogInitException("Action type unknown: "+ type);
        }
    }


    public int getResultType() {
        return resultType;
    }

    public String getResultMessage() {
        if (this.resultMessage!=null)
            return resultMessage;
        else if (this.resultException!=null)
            return resultException.getMessage();
        return "no message";
    }


}
