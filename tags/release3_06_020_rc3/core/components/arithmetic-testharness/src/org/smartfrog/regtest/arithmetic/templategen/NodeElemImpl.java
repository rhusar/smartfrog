package org.smartfrog.regtest.arithmetic.templategen;
import java.lang.IllegalArgumentException;
import java.util.Vector;
import java.util.Random;
import java.security.SecureRandom;


/** Generic element that represent a parsed basic operation.
 *
 */
public class NodeElemImpl implements NodeElem {
  /** Operation index for this node. */
  int opIndex;
  /** Left descendant in the parse tree. */
  NodeElem leftChild=null;
  /** Right descendant in the parse tree. */
  NodeElem rightChild=null;
  /** Whether we are a left child of our parent. */
  boolean isLeftChild=true;
  /** Ancestor of this node in the parse tree.*/
  NodeElem parent;
  /** Extra info about the node. */
  String info;
  /** A unique name for this element.*/
  String uniqueName;
  /** A logical host for placing this component.*/
  String host;
  /** A random number shared by all the nodes. */
  static Random rnd = new SecureRandom();

  /**
   * Class Constructor.
   *
   * @param op Basic operation of this element.
   * @param leftChild Element on my left subtree.
   * @param rightChild Element on my right subtree.
   * @param info Extra info about this node.
   */
  public NodeElemImpl(String op, NodeElem leftChild, NodeElem rightChild,
                      String info) {

    this.opIndex = getIndexOp(op);
    this.leftChild = leftChild;
    this.rightChild = rightChild;
    this.info =info;
    this.uniqueName = GlobalName.getName(op);
    this.host =  GlobalName.getName("host",NodeElem.MAX_HOSTS);
  }

  
  /** Returns an index inside an array of basic operations. 
   *
   * @param oper Operation that we want to index.
   * @return An index inside an array of basic operations. 
   */
  int getIndexOp(String oper) {
    for (int i=0;i<NodeElem.ALL_OPS.length;i++) {
      if (ALL_OPS[i].equals(oper))
        return i;
    }
    throw new IllegalArgumentException("Bad Argument: "+oper);
  }

  /** Returns a unique name for this node.
   *
   * @return A unique name for this node.
   */
  public String getUniqueName() {
    
    return uniqueName;
  }

  public String toString() {
    /*
    System.out.println("Calling toString in " + uniqueName);
    if (parent!= null) {
      System.out.println(" parent is " + parent.getUniqueName());
      if (isLeftChild)
        System.out.println(" I am left child");
      else
        System.out.println(" I am right child");
    } else 
      System.out.println("parent is null");
    */                 
    // Leave node.
    if (leftChild == null) {
      if (info != null)
        return ALL_OPS_OUTPUT[opIndex]+info;
      else
        return ALL_OPS_OUTPUT[opIndex];
    }
    // Unary operator.
    if (rightChild == null) {
      return "("+ ALL_OPS_OUTPUT[opIndex]+ leftChild + ")";
    }
     // Binary operator.
    return "("+ leftChild + ALL_OPS_OUTPUT[opIndex]+ rightChild + ")";
  }
   
  /** Returns the parent of this node.
   *
   * @return the parent of this node.
   */
  public NodeElem getParent() {
    return parent;
  }
  
  /** Sets the parent of this node.
   *
   * @param parent The parent of this node.
   */
  public void  setParent(NodeElem parent) {
    this.parent = parent;
  }

  /** Traverses the tree fixing the "parent" pointers.
   */
  public void computeParents() {
    computeParents(true);
  }

  /** Gets a logical host for placing this component.
   *
   *
   * @return A logical host for placing this component.
   */
  public String getHost() {
    return host;
  }
    
  /** Gets whether this node is the left child of its parent.
   *
   *
   * @return Whether  this node is the left child of its parent.
   */
   public boolean isLeftChild() {
    return isLeftChild;
  }

  /** Traverses the tree fixing the "parent" pointers.
   * @param isLeftChild whether we are a left child of our parent.
   */
  public void computeParents(boolean isLeftChild) {
    
    this.isLeftChild = isLeftChild; 
    if (leftChild != null) {
      leftChild.setParent(this);
      leftChild.computeParents(true);
    }

    if (rightChild != null) {
      rightChild.setParent(this);
      rightChild.computeParents(false);
    }
  }

  
  /** Gets the operation associated with this node.
   *
   * @return The operation associated with this node.
   */
  public String getOperation() {
    return NodeElem.ALL_OPS[opIndex];
  }


  /** Gets extra information about the node , e.g., the value of a
   * constant. 
   *
   *
   * @return Extra info about the node
   */
  public String getInfo() {
    return info;
  }

  /** Generates random subtrees from this node, assuming a given
   * branching probability.
   *
   * @param p The branching probability.
   * @param damping A damping factor to limit tree growth.
   */
  public void genRandomTree(double p, double damping) {

    switch( opIndex) {
      //Binary operators
    case 0://PLUS:
    case 1://TIMES:
      leftChild = genRandomSubTree(p,damping);
      rightChild = genRandomSubTree(p,damping);
      break;
      // Unary operators
    case 5://EVALUATOR:
    case 2://NEGATE:
      leftChild = genRandomSubTree(p,damping);
      break;
      // Leaves...
    case  3://CONSTANT:
      info = (new Integer(getRandomNumberInt())).toString();
      break;
    default:
      //GENERATOR do nothing...
    }
  }

  /** Generates a random subtree that becomes one of our children. 
   *
   *
   * @param p The branching probability.
   * @param damping A damping factor to limit tree growth.
   * @return A randomly generated subtree.
   */
  NodeElem  genRandomSubTree(double p, double damping) {
    String tag=null;     
    double rnd = getRandomNumberDouble();
    NodeElem temp;
    // Start with binary op.
    if (rnd < p/2.0) 
      tag = PLUS; // probability  p/2
    else if (rnd < p) 
      tag = TIMES; // probability  p/2
    // Now unary op
    else if (rnd < (p+(1.0-p)/3.0))
      tag = NEGATE; // probability  (1-p)/3
    // Finally leave nodes.
    else if (rnd < (p+ 2.0*(1.0-p)/3.0))
      tag = CONSTANT;// probability  (1-p)/3
    else
      tag = GENERATOR;// probability  (1-p)/3
     
    System.out.println("Generating node with tag " +tag+ (new Double(rnd)).toString());
    temp = new NodeElemImpl(tag,null,null,null);
    // The probablity of spawning a new child is reduced by damping..
    temp.genRandomTree(p*damping,damping);
    return temp;
  }


  /** Returns a vector with all the nodes in my current sub-tree
   * (including myself) that have an operation contained in a target
   * array of patterns.
   *
   * @param all A vector used to add the matched nodes.
   * @param pattern A collection of patterns that ANY will work.
   * @return A vector with all the nodes in my subtree matching ANY of
   * the patterns.
   */
  public Vector extract(Vector all, String[] pattern) {
    
    Vector result = all;
    for (int i=0;i< pattern.length;i++) {
      if (pattern[i].equals(getOperation())) {
        result.add(this);
        break;
      }
    }

    if (leftChild != null) 
      result = leftChild.extract(result,pattern);
        
     if (rightChild != null) 
      result = rightChild.extract(result,pattern);

     return result;
  }

  /** Gets a double random number from 0.0 to 1.0;
   *
   *
   * @return A double random number from 0.0 to 1.0;
   */
  synchronized static double getRandomNumberDouble() {
    return rnd.nextDouble();
  }

  /** Gets an integer random number from 1 to MAX_CONSTANT;
   *
   *
   * @return  An integer random number from 1 to MAX_CONSTANT;
   */
  synchronized static int getRandomNumberInt() {
    return (1+rnd.nextInt(MAX_CONSTANT));
  }


  /** Gets the number of nodes in this subtree (including this node).
   *
   * @return The number of nodes in this subtree (including this node).
   */
  public int getNumberNodes() {
    int value = 1;
    if (leftChild != null)
      value += leftChild.getNumberNodes();
    if (rightChild != null)  
      value += rightChild.getNumberNodes();
    return value;
  }
}
