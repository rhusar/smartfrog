package org.smartfrog.tools.testharness;
import org.smartfrog.services.utils.generic.OutputStreamIntf;
import org.smartfrog.services.display.PrintMsgInt;


/** An interface for a filter that scans the output of a process for
 * certain patterns, and notifies an scheduler of that event with a
 * predetermined tag.
 *
 */
public interface NotifyOutputFilter extends OutputStreamIntf {

  /** A tag that represents a reference to the scheduler that receives
   * notifications.*/
  public static final String SCHEDULER="scheduler";

  /** A tag that represents a file name to dump the output.*/
  public static final String FILE_NAME="fileNameOutput";

  /** A tag that represents search patterns for ALL semantics.*/
  public static final String SEARCH_NORMAL_PATTERNS="searchNormalPatterns";

  /** A string that represents corresponding tags for ALL semantics.*/
  public static final String RESULT_NORMAL_TAGS="resultNormalTags";

  /** A tag that represents search patterns for ANY semantics.*/
  public static final String SEARCH_FORCE_PATTERNS="searchForcePatterns";

  /** A string that represents corresponding tags for ANY semantics.*/
  public static final String RESULT_FORCE_TAGS="resultForceTags";

  /** A tag that represents custom search patterns, i.e.,
   * (concat(pattern,number,terminator)).*/
  public static final String SEARCH_CUSTOM_PATTERNS="searchCustomPatterns";

  /** A string that represents custom corresponding tags for i.e.,
   * (concat(pattern,number)).*/
  public static final String RESULT_CUSTOM_TAGS="resultCustomTags";

  /** A tag that represents a unique id for the scheduler. THIS IS A MUST.*/
  public static final String PROCESS_ID="processId";

  /** Closes all the output streams/resources associated with this filter.*/
  public void shutdown();

  /** Gets external component implementing PrintMsgInt*/

  //public PrintMsgInt getExternalPrintMsg();

 /** Sets external component implementing PrintMsgInt*/
  //public void setExternalPrintMsg(PrintMsgInt _printMsgImp);

}
