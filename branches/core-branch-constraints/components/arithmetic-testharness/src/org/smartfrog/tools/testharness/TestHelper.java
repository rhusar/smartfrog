package org.smartfrog.tools.testharness;
import java.io.PrintStream;

/** Helper functions called by test writers that have predefined
 * messages understood  by the test harness.
 */
public class TestHelper {

  /** A message printed when a daemon is ready */
  public static final String DAEMONS_STARTED="SmartFrog ready...";

  /** A message printed when the application is ready for clean-up. */
  public static final String SFSTART_FINISHED="DONE with SFStart...";

  /** A message printed when application clean-up is finished. */ 
  public static final String SFSTOP_FINISHED="DONE with SFStop...";

  /** A single appearance of this message forces the application is
   * ready for clean-up in all nodes. */
  public static final String FORCE_SFSTART_FINISHED="DONE with __FORCE__ SFStart...";

 /** A single appearance of this message forces the application is
   * done with clean-up in all nodes. */
  public static final String FORCE_SFSTOP_FINISHED="DONE with __FORCE__ SFStop...";

  // The format of an adhoc tag is
  // concat(BEGIN_CUSTOM_TAG,number,END_CUSTOM_TAG)

  /** A message printed to start a custom tag.*/
  public static final String BEGIN_CUSTOM_TAG="gV7LyCp4ZTRcDQPPm5Gl__";

  /** A message printed to end a custom tag.*/
  public static final String END_CUSTOM_TAG="__";

  /** A default value that represents search patterns for ALL semantics.*/
  public static final String[] 
    DEFAULT_SEARCH_NORMAL_PATTERNS= {DAEMONS_STARTED,
                                     SFSTART_FINISHED,
                                     SFSTOP_FINISHED};
  /** A default value that represents corresponding tags for ALL semantics.*/
  public static final String[] 
    DEFAULT_RESULT_NORMAL_TAGS = {"waitForDaemons","startApps","stopApps"};

  /** A default value that represents search patterns for ANY semantics.*/
  public static final String[]  
    DEFAULT_SEARCH_FORCE_PATTERNS= {FORCE_SFSTART_FINISHED,
                                    FORCE_SFSTOP_FINISHED};
  
  /** A default value that represents corresponding tags for ANY semantics.*/
  public static final String[] 
    DEFAULT_RESULT_FORCE_TAGS = {"startApps","stopApps"};

  /** A default value that represents custom search patterns, i.e.,
   * (concat(pattern,number,terminator)).*/  
  public static final String[]  
    DEFAULT_SEARCH_CUSTOM_PATTERNS= {BEGIN_CUSTOM_TAG};

  /** A default value  represents custom corresponding tags for i.e.,
   * (concat(pattern,number)).*/  
  public static final String[] 
    DEFAULT_RESULT_CUSTOM_TAGS = {"custom_"};



  /** Prints on the given output stream a custom tag understood by the
   * test harness.
   *  
   * @param os An output stream used to write the message.
   * @param tagNumber A number identifying the custom tag.
   * @param message An extra message to be added to the output.
   */
  public static void printCustomTag(PrintStream os,int tagNumber,
                                    String message) {
    if (message == null) 
      os.println(BEGIN_CUSTOM_TAG+tagNumber+END_CUSTOM_TAG);
    else 
       os.println(message+BEGIN_CUSTOM_TAG+tagNumber+END_CUSTOM_TAG);
  }


  /** Prints on the given output stream a custom tag understood by the
   * test harness.
   *  
   * @param os An output stream used to write the message.
   * @param tagNumber A number identifying the custom tag
   */
  public static void printCustomTag(PrintStream os,int tagNumber) {

    printCustomTag(os,tagNumber,null);
  }

  /** Prints on the given output stream a tag when a daemon is ready 
   *  
   * @param os An output stream used to write the message.
   */
  public static void printDaemonReady(PrintStream os) {

    os.println(DAEMONS_STARTED);
  }


  /** Prints on the given output stream a tag when applications have
   * been properly deployed and atarted. 
   *  
   * @param os An output stream used to write the message.
   * @param force A flag that forces status change regardless of other
   * nodes state.
   */
  public static void printSFStartDone(PrintStream os, boolean force) {
    
    printSFStartDone(os,force,null);

  }

  /** Prints on the given output stream a tag when applications have
   * been properly deployed and started. Extra message added to the
   * output. 
   *  
   * @param os An output stream used to write the message.
   * @param force A flag that forces status change regardless of other
   * nodes state.
   * @param message Extra message to be added.
   */
  public static void printSFStartDone(PrintStream os, boolean force,
                                      String message) {
    if (message ==null) {
      if (force)
        os.println(FORCE_SFSTART_FINISHED);
      else
        os.println(SFSTART_FINISHED);
    } else {
       if (force)
        os.println(message+FORCE_SFSTART_FINISHED);
      else
        os.println(message+SFSTART_FINISHED);
    }
  }


   /** Prints on the given output stream a tag when applications have
   * been properly stopped. 
   *  
   * @param os An output stream used to write the message.
   * @param force A flag that forces status change regardless of other
   * nodes state.
   */
  public static void printSFStopDone(PrintStream os, boolean force) {
    
    if (force)
      os.println(FORCE_SFSTOP_FINISHED);
    else
      os.println(SFSTOP_FINISHED);
  }
 
}
