package org.smartfrog.regtest.arithmetic.templategen;
import java.io.StringReader;
import java.util.Vector;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.lang.Math;

/** Instantiates example templates for an operation described with a
 * simple string, e.g.:
 *   (3+G)*2+GT  where "G" is a shared generator and "T" always terminate
 * the operation string to simplify parsing.
 *
 */
public class ExampleGen {

  /** An input file with the "raw" template. */
  String templateFileName;

  /** An out file for the processed template. */
  String outputFileName;

  /** An input string describing the operation. */
  String operation;

  /**  An object that encapsulates the parsed operator. */
  Calculator calc;

  /** Root node of the parsed operations tree.*/
  NodeElem root;

  /** A collection of all the generator nodes.*/
  Vector generators;

  /** A collection of all the nodes that are not generators or evaluators.*/
  Vector commonNodes;

  /** A template that will instantiate the example.*/ 
  Template template;

  /** Estimated number of nodes for the random tree.*/
  //int numNodes=100;
  /** Factor used to reduce the probability of branching when we
   * create a sub-tree.*/
  double dampingFactor=0.5;

  char optionFlagIndicator = '-';

  public final String usage =   "\n" +
    "Usage: java org.smartfrog.regtest.arithmetic.templategen.ExampleGen " +
    "-t <templateFile> -x <operation> -o <outputfile> -p <double:branchprob>";

  /**
   * Class Constructor.
   *
   * @param args input command line string. 
   * @exception Exception Can't instantiate template. 
   */
  public ExampleGen(String[] args) throws Exception {
    readOptions(args);

    if (operation != null) {
      calc = new Calculator(new StringReader(operation));
      root = calc.parseOp();
    } else {
      root = generateRandomTree(dampingFactor);
      // just Checking...
      String oper = root.toString();
      System.out.println("***before parsing "+oper);
      System.out.println("*** number of nodes " + (new
        Integer(root.getNumberNodes())).toString()); 
      calc = new Calculator(new StringReader(oper+NodeElem.TERMINATOR_TOKEN));
      root = calc.parseOp();
      System.out.println("***after parsing "+root);      
      System.out.println("*** number of nodes " + 
                         (new Integer(root.getNumberNodes())).toString());
    }

    root.computeParents();    
    generators = root.extract(new Vector(), NodeElem.GENERATOR_OPS);    
    commonNodes = root.extract(new Vector(),NodeElem.COMMON_OPS);

    System.out.println(root);
    if (outputFileName == null) 
      instantiateTemplate(System.out);
    else 
      instantiateTemplate(new 
        PrintStream(new FileOutputStream(outputFileName)));
  }

  /**Instantiates the velocity template.
   *
   * @param out Output stream to dump the template.
   * @exception Exception Error while instantiating the template.
   */
  void instantiateTemplate(PrintStream out) throws Exception {
    
    Velocity.init();
    VelocityContext context = new VelocityContext();
    context.put("evaluator", root);
    context.put("generators",generators);
    context.put("commonNodes",commonNodes);
    template = Velocity.getTemplate(templateFileName);
    BufferedWriter writer = 
      new BufferedWriter(new OutputStreamWriter(out));
    if (template != null)
      template.merge(context, writer);
    writer.flush();
    writer.close();    
  }
    
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
          case 'x':
            operation = args[++i];
            break;
          case 'o':
            outputFileName = args[++i];
            break;
          case 'd':
            dampingFactor = Double.parseDouble(args[++i]);
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

  /** Generates a random expression tree with (on average) a given
   * number of nodes.
   *
   * @param numNodes Average number of nodes of the tree
   * @return Root element of the tree.
   */
  NodeElem generateRandomTree(double damping) {
    
    NodeElem top = new NodeElemImpl( NodeElem.EVALUATOR,
                                     null,null,null);
    // double branchProb = getBranchProbability(numNodes);
    top.genRandomTree(1.0,damping);
    return top;
  }

  
  static public void main(String[] args) {
    try {
      ExampleGen result = new ExampleGen(args);
      //result.dump(System.out);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
