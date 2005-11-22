package org.smartfrog.tools.testharness;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.io.InputStreamReader;
import org.smartfrog.services.display.PrintMsgInt;
import org.smartfrog.services.display.PrintErrMsgInt;
import java.util.Vector;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import java.net.*;

/** Implements a filter that scans the output of a process for
 * certain patterns, and notifies an scheduler of that event with a
 * predetermined tag.
 */

public class NotifyOutputFilterImpl implements NotifyOutputFilter, Runnable , PrintMsgInt, PrintErrMsgInt {

  boolean verbose = true;
  boolean formatMsg = true;


  /** An array of tags that represents search patterns for ALL semantics.*/
  String[] searchNormalPatterns;

  /** An array of tags that represents search patterns for ANY semantics.*/
  String[] searchForcePatterns;

  /** An array of tags that represents custom search patterns, i.e.,
   * (concat(pattern,number,terminator)).*/
  String[] searchCustomPatterns;

  /** An array of strings that represents corresponding tags for ALL semantics.*/
  String[] resultNormalTags;

  /** An array of strings that represents corresponding tags for ANY semantics.*/
  String[] resultForceTags;

  /** An array of strings that represents custom corresponding tags for i.e.,
   * (concat(pattern,number,terminator)).*/
  String[] resultCustomTags;

  /** A reference to an scheduler that will be notified of our findings. */
  Scheduler scheduler;

  /** The name of the file to where we copy the input to this filter. */
  String fileOutputName;

  /** A stream to redirect the output of the process.*/
  PrintStream dumpOut;

  /** A unique identifier of this filter for the scheduler. */
  String myId;
  static int i = 0;
  /** A pipe that connects the daemon's output with pIn. */
  PipedOutputStream pOut;

  /** A pipe that connects the "pumping" thread with pOut. */
  PipedInputStream pIn;

  /** A wrapped pIn that performs character conversion. */
  BufferedReader bIn;

  /** A thread that performs the "pumping" and notifies the scheduler. */
  Thread thread;

  /** External component where to send a copy of everything that Filter receives */
  PrintMsgInt printMsgImp = null;

  /** Indicates if this Filter is connected to other component*/
  boolean havePrinter = false;


  ReportGenerator repGen = null;
  /**
   * Class Constructor.
   */
  public NotifyOutputFilterImpl(Scheduler scheduler,
                                String fileOutputName,
                                String myId,
                                String[] searchNormalPatterns,
                                String[] resultNormalTags,
                                String[] searchForcePatterns,
                                String[] resultForceTags,
                                String[] searchCustomPatterns,
                                String[] resultCustomTags)
    throws IOException {
    this.scheduler = scheduler;
    this.myId = myId;
    this.fileOutputName = fileOutputName;
	System.out.println("fileOutputName in NotifyOutputFilterImpl =============> " +fileOutputName);


 /*  if (fileOutputName==null)
       dumpOut = System.out;
    else {
      try {
        FileOutputStream temp  = new FileOutputStream(fileOutputName,true);
        dumpOut = new PrintStream(temp);

		dumpOut.println("dumpout is created =============>");

      } catch (IOException e) {
        System.out.println(e.getMessage());
        dumpOut = System.out;
      }
    }

	System.out.println("dumpOut in NotifyOutputFilterImpl =============> " +dumpOut.toString());

*/
    this.searchNormalPatterns = searchNormalPatterns;
    this.searchForcePatterns = searchForcePatterns;
    this.searchCustomPatterns = searchCustomPatterns;

    this.resultNormalTags = resultNormalTags;
    this.resultForceTags = resultForceTags;
    this.resultCustomTags = resultCustomTags;

    if ((searchNormalPatterns.length != resultNormalTags.length) ||
        (searchForcePatterns.length != resultForceTags.length) ||
        (searchCustomPatterns.length != resultCustomTags.length)) {
      throw new IllegalArgumentException("number of patterns and tags do not match");
    }
    pOut = new PipedOutputStream();
    pIn = new PipedInputStream(pOut);
    bIn =  new BufferedReader(new InputStreamReader(pIn));

    repGen= new ReportGenerator();

    thread = new Thread(this);

    thread.start();


//    if (debug) System.out.println("Create NotifyOutputFilterImpl:"+this.toString());
  }


  /** Gets the output stream that the monitored application should use.
   *
   *
   * @return The output stream that the monitored application should use.
   */
  public OutputStream getOutputStream() {
    //if (debug) System.out.println("NotifyOutputFilter:getOutputStream (It should manage more than one pOut!!");//DEBUG
    //Renew pOut // not included before!
    this.shutdown();
    this.thread.interrupt();
    this.thread=null;
    try {
    pOut = new PipedOutputStream();
    pIn = new PipedInputStream(pOut);
    bIn =  new BufferedReader(new InputStreamReader(pIn));
    } catch (Exception ex) {
       ex.printStackTrace();
    }

    // not included before!
     thread = new Thread(this);

     thread.start();


    return pOut;
  }


  /** Notifies the scheduler of a new event.
   *
   * @param tag An identifier that the scheduler finds unique.
   * @param force Ignore what other clients say and do allow checks on
   * "tag" to proceed.
   * @exception RemoteException An error contacting the scheduler.
   */
  void submitTag(String tag, boolean force, String line) throws RemoteException {
     // System.out.println(":::::TAG TO SUBMIT "+ tag);
//    if (debug) System.out.println("NotifyOutputFilter: submitTag!!:"+tag+", force:"+force);//DEBUG
    log (" TAG found. Message:" + line);
    if (scheduler != null){
	scheduler.signalGoAhead(tag, myId,force);
//       if (debug) System.out.println("NotifyOutputFilterID "+scheduler.toString());
//       if (debug) System.out.println("NotifyOutputFilterSIGNAL: submitted Tag.signalGoAhead!!:"+tag+", force:"+force);//DEBUG
    }
  }


  /** Looks for patterns in a line and returns the matching tag. Note
   * that only the *first* matching pattern is used, and (possibly)
   * others are just ignored.
   *
   *
   * @param line A string that may contain a wanted pattern.
   * @param searchPatterns A set of patterns that we are looking for.
   * @param resultTags A set of mathing tags for those patterns (in
   * the same order as the search patterns)
   * @return A matching tag or null if no pattern was found.
   */
  String scanTag(String line, String[] searchPatterns,
                 String[] resultTags) {
 // System.out.println("LINE======" + line);
    for (int i=0;i<searchPatterns.length;i++) {
      if ((line.indexOf(searchPatterns[i])) != -1) {
/*	if (!resultTags[i].equals("waitForDaemons")) {
        	String tag = resultTags[i].concat("_");
        	 tag = tag.concat(Integer.toString(count));
		return tag;
	}
	else*/
	return resultTags[i];
      }
    }

    return null;
  }

  /** Looks for custom patterns in a line and returns the matching
   * tag.  Custom patterns have the generic form:
   * concat(Pattern,Number,END_CUSTOM_TAG) and matching custom tags are
   * of the form concat(Tag,Number) where "Number" is the same in both
   * cases. Note that only the *first* matching pattern is used, and
   *  (possibly) others are just ignored.
   *
   *
   * @param line A string that may contain a wanted pattern.
   * @param searchPatterns A set of patterns that we are looking for.
   * @param resultTags A set of mathing tags for those patterns (in
   * the same order as the search patterns)
   * @return A matching tag or null if no pattern was found.
   */
  String scanCustomTag(String line, String[] searchPatterns,
                       String[] resultTags) {

    int index;
    for (int i=0;i<searchPatterns.length;i++) {
      if ((index = line.indexOf(searchPatterns[i])) != -1) {
        String temp =
          line.substring(index+searchPatterns[i].length());
        if ((index = temp.indexOf(TestHelper.END_CUSTOM_TAG)) != -1) {
          String numberString = temp.substring(0,index);
          Integer num = new Integer(numberString);
          return resultTags[i]+num.toString();
        }
      }

    }
    return null;
  }




  /** Main thread for "pumping" and  filtering the process output. It
   * also notifies  the scheduler results of this filtering.
   *
   */
  public void run() {

    String line;
   String resultTag = null;
    try {
			 if (this.fileOutputName==null)
        dumpOut = System.out;
		else

		{
			try {
			FileOutputStream temp  = new FileOutputStream(this.fileOutputName,true);
			dumpOut = new PrintStream(temp, true);
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
				dumpOut = System.out;
	        }
       }

//      if (debug) System.out.println("NotifyOutputFilter: Starting!!");//DEBUG
       //System.out.println("NotifyOutputFilter===========>: Starting!!");
      while ((line = bIn.readLine()) != null) {
        //if (debug) System.out.println("NotifyOutputFilter: Line!!"+line);//DEBUG
//        if (debug) {log("OutputStream:"+line);}
//        else {log(line);}
//		System.out.println("NotifyOutputFilter: Line!!"+line);
		log(line);



	//	dumpOut.flush();

	//	dumpOut.println(line);
/*		if (this.count > 0 && this.host !=null) {
		String attr = "app_".concat(Integer.toString(this.count));
		Compound  cp = SFProcess.getRootLocator().getRootProcessCompound(InetAddress.getByName(this.host), this.port);
		if (cp.sfContainsAttribute(attr)) {
			System.out.println("Attribute in CP========="  + cp.sfResolve(attr).toString());
			resultTag = exploreTag(line);
		  }
		}
		else */
	 	resultTag = exploreTag(line);

		//System.out.println("RESULT TAG===============" + resultTag);
      }
      //if (debug) System.out.println("NotifyOutputFilter: Finishing!!");//DEBUG

    } catch (Exception e) {
      dumpOut.println(e.getMessage());
    } finally {
      if (dumpOut != System.out)
        dumpOut.close();
    }
  }

private String exploreTag(String line) throws RemoteException {

    if (havePrinter) {
       try {
          printMsgImp.printMsg("FILTERED-> "+ line + "");
       } catch (Exception ex) {
          //System.out.println(ex);
          ex.printStackTrace();
       }
    }
    String resultTag= null;
  //try {
  //  int k = 0;
  //  String var[]=null;	
  //  int i=0;
    if ((resultTag = scanTag(line,searchNormalPatterns,resultNormalTags)) != null)

      submitTag(resultTag,false, line);
    else if ((resultTag = scanTag(line,searchForcePatterns,resultForceTags)) != null)
    {
	System.out.println(":::::::::::::::::::: ResultTag"+ resultTag);
//	System.out.println(":::::::::::::::::::: COUNT"+ this.count);
        /*var[i] = resultTag;
        i++;
	if (this.count > 0 && this.host !=null) {
		String attr = "app_".concat(Integer.toString(this.count));
		Compound  cp = SFProcess.getRootLocator().getRootProcessCompound(InetAddress.getByName(this.host), this.port);
		if (cp.sfContainsAttribute(attr) && i == this.count - 1) {
		    for ( int j = 1; j < i ; j++) {
			if (var[0].equals(var[j]))
				k++;	 
		    }
                     if ( k == this.count - 1)
                      submitTag(resultTag,true, line);
		  }
	}*/
/*	if (resultTag.equals("startApps")) {
		startcounter++;
	//	System.out.println("START COUNTER============" + startcounter);
        }
	if (resultTag.equals("stopApps")) {
		stopcounter++;
		//System.out.println("STOP COUNTER============" + stopcounter);
        }
	if (startcounter == this.count && resultTag.equals("startApps")) {
	//	System.out.println("CALLING SUBMITTAG");
         	submitTag(resultTag,true, line);
	} 
	if (stopcounter == this.count && resultTag.equals("stopApps"))*/
         	submitTag(resultTag,true, line);

         Vector tagVect=new Vector();
         //tagVect.add(PROCESS_ID);
         tagVect.add(line);
         ReportGenerator.report.add(tagVect);
        System.out.println("*************************** size of report"+ ReportGenerator.report.size());
    }
    else if ((resultTag = scanCustomTag(line,searchCustomPatterns,resultCustomTags)) != null)
      submitTag(resultTag,false, line);
//    } catch (Exception ex) {
  //        ex.printStackTrace();
  // }
    return resultTag;
}

  /** Closes all the output streams/resources associated with this filter.*/
  public void shutdown() {
        ReportGenerator repGen = new ReportGenerator();
        repGen.generateReport();
    try {
      // This should kill the thread and this closes dumpOut...

      pIn.close();
      pOut.close();
   } catch (IOException e) {
    }

  }

  public synchronized void printMsg(String msg) {
    try {
        String resultTag = exploreTag (msg);

    } catch (Exception e) {
      dumpOut.println(e.getMessage());
    }
  }

  public synchronized void printErrMsg(String msg) {
    try {
        String resultTag = exploreTag(msg);
    } catch (Exception e) {
      dumpOut.println(e.getMessage());
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

  public String toString (){

    return new String ("NotifyFilterImpl.toString: customTags:"+arrayToString(this.searchCustomPatterns)
                              +"->"+ arrayToString(this.resultCustomTags)
                  + ", forceTags:"+arrayToString(this.searchForcePatterns)
                               +"->"+arrayToString(this.resultForceTags)
                    +", normalTags:"+arrayToString(this.searchNormalPatterns)
                               +"->"+arrayToString(this.resultNormalTags));
  }






  /** Prints the current line to our redirected output.
   *
   * @param line A line to be printed.
   */

  private void log (String message){

	//System.out.println("In LOG =====================>");
     message = "[NOTIFY_OUTPUT_FILTER."+myId+"] "+message;
	 if (formatMsg) message= formatMsg(message);
     if (verbose) dumpOut.println(message);
   }
   /** Prints the current line to our redirected output.
    *
    * @param line A line to be printed.
    */

   private void logErr (String message){
     message = "[NOTIFY_OUTPUT_FILTER."+myId+"] "+message;
     if (formatMsg) message= formatMsg(message);
     if (verbose) dumpOut.println("[ERR] "+message);
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

   public PrintMsgInt getExternalPrintMsg() {
        return printMsgImp;
   }

   public void setExternalPrintMsg(PrintMsgInt _printMsgImp) {
        if (_printMsgImp!=null) {
            this.havePrinter=true;
            printMsgImp = _printMsgImp;
        }
   }
}







