package org.smartfrog.tools.testharness.templategen;
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
import java.util.Properties;

/** Instantiates a meta-template with the customization that our
 * experimental setting requires, e.g., the names and number of
 * physical machines, the name of the main jar files, the generic
 * options used for testing (security on/off, dynamic loading
 * on/off). 
*/
public class RemoteTemplateGen { 

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

  String installDir = null;
  String sharedDir = null;
  
  char optionFlagIndicator = '-';

  public final String usage =   "\n" +
    "Usage: java org.smartfrog.tools.testharness.templateGen.TemplateGen " +
    "-t <templateFile> -h <hostsFile> -o <outputfile> -d <installDir> -s <sharedDir>";

  
  /**
   * Class Constructor.
   *
   * @param args input command line string. 
   * @exception Exception Can't instantiate template. 
   */
  public RemoteTemplateGen(String[] args) throws Exception {

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
   *   <hostname> <user> <password> <osType>
   *       ....
   */
  void readDaemons() throws Exception {

    allDaemons = new Vector();
    Reader r = new BufferedReader(new FileReader(hostsFileName));
      try {
          StreamTokenizer st = new StreamTokenizer(r);
          //    st.resetSyntax();
          st.eolIsSignificant(false);
          // Allow single line coments in the input
          st.slashSlashComments(true);
          st.wordChars('A', 'Z');
          st.wordChars('a', 'z');
          st.wordChars('/','/');
          //    st.wordChars('0', '9');
          st.wordChars('-','-');
          st.wordChars('.','.');
          st.wordChars(':',':');
          st.wordChars('*','*');
          st.wordChars('\\','\\');
          st.whitespaceChars(' ',' ');
          String tempHostName=null;
          String tempUser=null;
          String tempPassword=null;
          String tempOS = null;

          while ((tempHostName =getWord(st))!=null) {
              tempUser = getWord(st);
              tempPassword = getWord(st);
              tempOS = getWord(st);
                allDaemons.add(new RemoteDaemon(tempHostName,tempUser,tempPassword, tempOS));
          }
      } finally {
          r.close();
      }
  }


  /** Gets the next tokenized word from a stream.
   * 
   *
   * @param st An input StreamTokenizer.
   * @return A word just read from the input.
   */
  private String getWord(StreamTokenizer st) throws Exception {
    
    int token = st.nextToken();
    if (token == StreamTokenizer.TT_WORD)
      return st.sval;
    else
      return null;
  }

  /**
   * Instantiates the velocity template.
   * Closes the output stream in the process -aleays
   * @param out Output stream to dump the template.
   * @exception Exception Error while instantiating the template.
   */
  private void instantiateTemplate(PrintStream out) throws Exception {

      BufferedWriter writer =
              new BufferedWriter(new OutputStreamWriter(out));
      try {
		Properties p = new Properties();
		p.setProperty("file.resource.loader.path", "/");
          Velocity.init(p);
          VelocityContext context = new VelocityContext();
          context.put("allDaemons", allDaemons);
          context.put("installDir", installDir);
          context.put("sharedDir", sharedDir);
          template = Velocity.getTemplate(templateFileName);
          if (template != null) {
              template.merge(context, writer);
          }
      } finally {
          writer.flush();
          writer.close();
      }
  }


    /** Scans command line options.
   *
   *
   * @param args The input command line.
   */
  private void readOptions(String[] args) {

        String errorString = null;
        int i;
        for (i = 0; i < args.length & errorString == null;) {
            try {
                if (args[i].charAt(0) == optionFlagIndicator) {
                    switch (args[i].charAt(1)) {
                        case'?':
                            errorString = "SFSystem help";
                            break;
                        case't':
                            templateFileName = args[++i];
                            break;
                        case'h':
                            hostsFileName = args[++i];
                            break;
                        case'd':
                            installDir = args[++i];
                            break;
                        case's':
                            sharedDir = args[++i];
                            break;
                        case'o':
                            outputFileName = args[++i];
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
      RemoteTemplateGen result = new RemoteTemplateGen(args);
      //result.dump(System.out);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
