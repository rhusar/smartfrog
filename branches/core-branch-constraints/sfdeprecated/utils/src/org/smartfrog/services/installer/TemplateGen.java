package org.smartfrog.services.installer;
import java.io.StringReader;
import java.util.Vector;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

/** Instantiates a meta-template with the customization that our
 * experimental setting requires, e.g., the names and number of
 * physical machines, the name of the main jar files, the generic
 * options used for testing (security on/off, dynamic loading
 * on/off). 
*/
public class TemplateGen { 

  /** An input file with the "raw" template. */
  String templateFileName;

  /** An out file for the processed template. */
  String outputFileName;

  /** An input file with the description of the hosts involved. */
  String hostsFileName;

  /** A template that will instantiate the example.*/ 
  Template template;

  /** A collection of all the daemons.*/
  Vector allDaemons;

  /** A special daemon for dynamic loading jar files. */
  Daemon principalDaemon = null;

  /** A flag to generate templates using security.*/
  boolean securityOn=false;

  /** A flag to generate templates downloading jars dynamically from
   * web servers */
  boolean dynamicLoadingOn=false;

  /** Number of Daemons deployed. */
  int numberDaemons=0;
  
  char optionFlagIndicator = '-';

  public final String usage =   "\n" +
    "Usage: java org.smartfrog.tools.testharness.templateGen.TemplateGen " +
    "-t <templateFile> -h <hostsFile> -o <outputfile> [-s for security"+
    " ON] [-d for dynamic loading] ";

  
  /**
   * Class Constructor.
   *
   * @param args input command line string. 
   * @exception Exception Can't instantiate template. 
   */
  public TemplateGen(String[] args) throws Exception {

    readOptions(args);
    
    readDaemons();

    if (outputFileName == null) 
      instantiateTemplate(System.out);
    else 
      instantiateTemplate(new 
        PrintStream(new FileOutputStream(outputFileName)));
  }

  /** Read the description of the daemons from a file. The format of
   * this file is :
   *   <logicalName> <hostname> <username> <passwordFile> <ftpLocalFiles> <releasename> <emailto> <emailfrom> <emailserver> <OSType>
   *       ....
   *
   */
  void readDaemons() throws Exception {

    allDaemons = new Vector();
    Reader r = new BufferedReader(new FileReader(hostsFileName));
    StreamTokenizer st = new StreamTokenizer(r);
    //    st.resetSyntax();
    st.eolIsSignificant(false);
    // Allow single line coments in the input
  //  st.slashSlashComments(true);
    st.wordChars('A', 'Z');
    st.wordChars('a', 'z');    
    st.wordChars('/','/');
    st.wordChars('0', '9');
    st.wordChars('-','-');
    st.wordChars('.','.');
    st.wordChars(':',':');
    st.wordChars('_','_');
    st.wordChars('@','@');
    st.wordChars('\\','\\');
    st.whitespaceChars(' ',' ');
    String tempLogicalName=null;
    String tempHostName=null;
    String tempUserName=null;
    String tempPasswordFile=null;
    String tempLocalFile1=null;
    String tempLocalFile2=null;
    String tempLocalFile3=null;
    String tempReleaseName=null;
    String tempEmailTo=null;
    String tempEmailFrom=null;
    String tempEmailServer=null;
    String tempOS = null;
    String tempJavaHome = null;
      // First is the principal host.
    tempLogicalName = getWord(st);
    tempOS = getWord(st);
    tempHostName = getWord(st);

    //if (tempOS.equals(Daemon.LINUX)) {
    	tempUserName = getWord(st);
    	tempPasswordFile = getWord(st);
    //}
    tempLocalFile1 = getWord(st);
    if (tempOS.equals(Daemon.WINDOWS)) {
    	tempLocalFile2 = getWord(st);
    	tempLocalFile3 = getWord(st);
    }
    tempReleaseName = getWord(st);
    tempEmailTo = getWord(st);
    tempEmailFrom = getWord(st);
    tempEmailServer = getWord(st);
    if (tempOS.equals(Daemon.WINDOWS))
	   tempJavaHome = getWord(st); 
    // if dynamicLoadingOn we mark as special the first daemon.
    principalDaemon = new Daemon(tempLogicalName,tempHostName,
                                 tempUserName,tempPasswordFile, tempLocalFile1,
				 tempLocalFile2, tempLocalFile3, tempReleaseName, 
				 tempEmailTo, tempEmailFrom, tempEmailServer,
				 tempOS, tempJavaHome);
    allDaemons.add(principalDaemon);
        
    while ((tempLogicalName =getWord(st))!=null) {
      tempOS = getWord(st);
      tempHostName = getWord(st);
     // if (tempOS.equals(Daemon.LINUX)) {
    	tempUserName = getWord(st);
    	tempPasswordFile = getWord(st);
      //}
      tempLocalFile1 = getWord(st);
      if (tempOS.equals(Daemon.WINDOWS)) {
    	tempLocalFile2 = getWord(st);
    	tempLocalFile3 = getWord(st);
      }
      tempReleaseName = getWord(st);
      tempEmailTo = getWord(st);
      tempEmailFrom = getWord(st);
      tempEmailServer = getWord(st);
      if (tempOS.equals(Daemon.WINDOWS))
	   tempJavaHome = getWord(st); 
      allDaemons.add(new Daemon(tempLogicalName,tempHostName,
                                tempUserName, tempPasswordFile,tempLocalFile1, 
				tempLocalFile2, tempLocalFile3, tempReleaseName,
				tempEmailTo, tempEmailFrom, tempEmailServer, 
				tempOS, tempJavaHome));
     
    }
  }


  /** Gets the next tokenized word from a stream.
   * 
   *
   * @param st An input StreamTokenizer.
   * @return A word just read from the input.
   */
  String getWord(StreamTokenizer st) throws Exception {
    
    int token = st.nextToken();
    if (token == StreamTokenizer.TT_WORD)
      return st.sval;
    else
      return null;
  }

  /**Instantiates the velocity template.
   *
   * @param out Output stream to dump the template.
   * @exception Exception Error while instantiating the template.
   */
  void instantiateTemplate(PrintStream out) throws Exception {
    
    Velocity.init();
    VelocityContext context = new VelocityContext();
    context.put("allDaemons", allDaemons);
    context.put("principalDaemon",principalDaemon);
    context.put("dynamicLoadingOn",new Boolean(dynamicLoadingOn));
    context.put("securityOn",new Boolean(securityOn));
  
    template = Velocity.getTemplate(templateFileName);
    BufferedWriter writer = 
      new BufferedWriter(new OutputStreamWriter(out));
    if (template != null)
      template.merge(context, writer);
    writer.flush();
    writer.close();    
  }


  /** Scans command line options.
   *
   *
   * @param args The input command line.
   */
  void readOptions(String[] args) {

    String errorString=null;
    int i;
    for (i=0;i<args.length & errorString == null;) {
      try {
        if (args[i].charAt(0) == optionFlagIndicator) {
          switch (args[i].charAt(1)) {
          case '?':
            errorString = "SFSystem help";
            break;
          case 't':
            templateFileName = args[++i];
            break;
          case 'h':
            hostsFileName = args[++i];
            break;
          case 'o':
            outputFileName = args[++i];
            break;
          case 'd':
            dynamicLoadingOn=true;
            break;
          case 's':
            securityOn=true;
            break;
          default: 
            errorString = "unknown option " + args[i].charAt(1);
          }
        } else {
          errorString = "illegal option format for option " + args[i];
        }
        i++;
      } catch (Exception e) {
        errorString = "illegal format for options ";
      }
    }
    if (errorString != null) {
      errorString += usage;
      throw new IllegalArgumentException(errorString);
    }
  }
  
  static public void main(String[] args) {
    try {
      TemplateGen result = new TemplateGen(args);
      //result.dump(System.out);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
