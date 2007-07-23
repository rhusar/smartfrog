package org.smartfrog.tools.testharness;

import java.rmi.RemoteException;
import java.util.Vector;
import java.io.OutputStream;
import java.rmi.Remote;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.services.display.PrintMsgInt;
import org.smartfrog.services.display.PrintErrMsgInt;
import org.smartfrog.sfcore.common.*;


public class NotifyOutputFilterPrimImpl extends PrimImpl implements NotifyOutputFilter, Remote , PrintMsgInt, PrintErrMsgInt{

  boolean verbose = true;

  boolean formatMsg = false;

  /** An object that implements the NotifyOutputFilter interface. */
  NotifyOutputFilter  notifyFilter;


  /**
   * Class Constructor.
   *
   * @exception RemoteException
   */
  public NotifyOutputFilterPrimImpl() throws RemoteException {

  }

  /** Reads a SF attribute composed of an array of strings.
   *
   *
   * @param sfTag Attribute that name the array of strings.
   * @param defaultValue A default value in case we cannot find it.
   * @return An array of strings mapping to that attribute or the
   * suggested default value.
   */
  public String[] readArrayStrings(String sfTag,String[] defaultValue) {

    try {
      //log("NotifyOutputFilterPrimImpl.readArrayStrings:"+sfTag);
      Vector temp = (Vector) sfResolve(sfTag);
      String [] tags = new String [temp.size()];
      for (int i=0; i<temp.size();i++){
        //log("    - NotifyOutputFilterPrimImpl.readArrayStrings (element): "+temp.elementAt(i).toString());
        tags[i]=temp.elementAt(i).toString();
      }
      return tags;
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /** Deploy the compound. Deployment is defined as iterating over the
   * context and deploying any parsed eager components.
   *
   * @throws Exception failure deploying compound or sub-component
   */
  public void sfDeploy() throws SmartFrogException, RemoteException {
    super.sfDeploy();
    try {
         Object formatMsgObj = sfResolve("formatMsg");
         if (formatMsgObj instanceof Boolean) {
            formatMsg = ((Boolean)formatMsgObj).booleanValue();
         } else if (formatMsgObj instanceof String) {
            // String format (Deprecapeted)
            String formatMsgStr = (String) formatMsgObj;
            if (formatMsgStr.equals("true")) {
               formatMsg = true;
               //log("NotifyOutputFilter: Formating messages.");
            } else {
               //log("NotifyOutputFilter: NOT Formating messages.");
               formatMsg = false;
            }
         }
      } catch (SmartFrogResolutionException e) {
      }



    /* An array of tags that represents search patterns for ALL semantics.*/
    String[] searchNormalPatterns =
      readArrayStrings(SEARCH_NORMAL_PATTERNS,
                       TestHelper.DEFAULT_SEARCH_NORMAL_PATTERNS);
    /* An array of tags that represents search patterns for ANY semantics.*/
    String[] searchForcePatterns=
      readArrayStrings(SEARCH_FORCE_PATTERNS,
                       TestHelper.DEFAULT_SEARCH_FORCE_PATTERNS);
    /* An array of tags that represents custom search patterns, i.e.,
     * (concat(pattern,number,terminator)).*/
    String[] searchCustomPatterns =
      readArrayStrings(SEARCH_CUSTOM_PATTERNS,
                       TestHelper.DEFAULT_SEARCH_CUSTOM_PATTERNS);
    /* An array of strings that represents corresponding tags for ALL
     * semantics.*/
    String[] resultNormalTags =
      readArrayStrings(RESULT_NORMAL_TAGS,
                       TestHelper.DEFAULT_RESULT_NORMAL_TAGS);
    /* An array of strings that represents corresponding tags for ANY
     * semantics.*/
    String[] resultForceTags =
      readArrayStrings(RESULT_FORCE_TAGS,
                       TestHelper.DEFAULT_RESULT_FORCE_TAGS);
    /* An array of strings that represents custom corresponding tags for i.e.,
     * (concat(pattern,number,terminator)).*/
    String[] resultCustomTags =
      readArrayStrings(RESULT_CUSTOM_TAGS,
                       TestHelper.DEFAULT_RESULT_CUSTOM_TAGS);
    /* A reference to an scheduler that will be notified of our findings. */
    Scheduler scheduler = null;
    try {
      //log("NotifyOutFilterPrimImpl.scheduler searching.");
      //scheduler = (Scheduler) sfResolve(SCHEDULER);
	  scheduler = (Scheduler) sfResolve(SCHEDULER, scheduler, true);
	  //log("NotifyOutFilterPrimImpl.scheduler found!");
    } catch (Exception e) {
      logErr(" NotifyOutFilterPrimImpl.scheduler NOT found!");
      // Do not try to notify
      scheduler = null;
    }

    /* The name of the file to where we copy the input to this filter. */
    String fileOutputName=null;
    try {
      fileOutputName = (String) sfResolve(FILE_NAME,fileOutputName , false);
	

    } catch (Exception e) {
      // Use System.out
      fileOutputName = null;
    }



    /* A  unique id for the scheduler. THIS IS A MUST.*/
    String processId = null;
	 processId = sfResolve(PROCESS_ID,processId, true);
         System.out.println("Process Id: =====" + processId);
	log("searchNormalPatterns:"+arrayToString(searchNormalPatterns));
        log("resultNormalTags:"+arrayToString(resultNormalTags));
    try {
        notifyFilter = new
                        NotifyOutputFilterImpl(scheduler,fileOutputName,
                             processId,searchNormalPatterns,
                             resultNormalTags,searchForcePatterns,
                             resultForceTags,searchCustomPatterns,
                             resultCustomTags);
    }catch (Exception ex) {
        SmartFrogException.forward(ex);
    }

    try {
      Object obj = sfResolve("externalPrinter");
      log("reference externalPrinter found!*************************************************");
      if (obj instanceof PrintMsgInt) {
        PrintMsgInt printMsgImp = (PrintMsgInt)obj;
         log("reference externalPrinter found!*****************ACCEPTED*****************");
        ((NotifyOutputFilterImpl)this.notifyFilter).setExternalPrintMsg(printMsgImp);
      }
    } catch (SmartFrogResolutionException e) {
    }

  }

  String arrayToString (String[] temp){
    if (temp == null) return "null";
    String txt=new String();
    for (int i=0; i<temp.length;i++){
      txt=txt+", "+temp[i];
    }
    return txt;
  }

  /** Performs the notify filter's specific termination behaviour.
   *
   *
   * @param status termination status
   */
  public void sfTerminateWith(TerminationRecord status) {

    shutdown();
    super.sfTerminateWith(status);
  }


  /** Gets the output stream that the monitored application should use.
   *
   *
   * @return The output stream that the monitored application should use.
   */
  public OutputStream getOutputStream() {

    if (notifyFilter != null)
      return notifyFilter.getOutputStream();
    else
      return null;
  }


  /** Closes all the output streams/resources associated with this
   * filter.*/
  public void shutdown() {
    if (notifyFilter != null)
      notifyFilter.shutdown();
  }


    /**
    *  Description of the Method
    *
    *@param  msg  Description of Parameter
    */
   public synchronized void printMsg(String msg) {
      if (formatMsg) {
         msg = formatMsg(msg);
      }
      try {
         ((PrintMsgInt)this.notifyFilter).printMsg(msg);
      } catch (Exception ex){
      }
   }


    /**
    *  Description of the Method
    *
    *@param  msg  Description of Parameter
    */
   public synchronized void printErrMsg(String msg) {
      if (formatMsg) {
         msg = formatMsg(msg);
      }
      try {
         ((PrintErrMsgInt)this.notifyFilter).printErrMsg(msg);
      } catch (Exception ex){
      }
   }


   private void log (String message){
      message = "[NOTIFY_OUTPUT_FILTER_PRIM."+PROCESS_ID+"] "+message;
      if (formatMsg) message= formatMsg(message);
      if (verbose) System.out.println(message);
	  
	  }

    private void logErr (String message){
      message = "[NOTIFY_OUTPUT_FILTER_PRIM."+PROCESS_ID+"] "+message;
      if (formatMsg) message= formatMsg(message);
      if (verbose) System.err.println(message);
    }

    /**
    *  Description of the Method
    *
    *@param  msg  Description of Parameter
    *@return      Description of the Returned Value
    */
   private String formatMsg(String msg) {
      msg = "[" + (new java.text.SimpleDateFormat("HH:mm:ss.SSS dd/MM/yy").format(new java.util.Date(System.currentTimeMillis()))) + "] " + msg;
      return msg;
   }
   /*public String toString (){
      return ("NotifyOutputFilterPrimImpl "+this.PROCESS_ID+", "+this.notifyFilter.toString());
   }*/
}

