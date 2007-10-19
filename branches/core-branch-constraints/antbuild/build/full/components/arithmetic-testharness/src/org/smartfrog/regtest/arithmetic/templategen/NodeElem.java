package org.smartfrog.regtest.arithmetic.templategen;
import java.util.Vector;

/** Defines constants for all the parsed network elements.
 *
 */
public interface NodeElem {

  // All basic elements allowed.
  public static final String PLUS="Plus"; 
  public static final String TIMES="Times"; 
  public static final String NEGATE="Negate"; 
  public static final String CONSTANT="Constant"; 
  public static final String GENERATOR="Generator";
  public static final String EVALUATOR="Evaluator";  
  
  public static final String[] ALL_OPS={PLUS,TIMES,NEGATE,CONSTANT,
                                        GENERATOR,EVALUATOR};
  public static final String[] ALL_OPS_OUTPUT={"+","*","-","","G",""};
  public static final String[] COMMON_OPS={PLUS,TIMES,NEGATE,CONSTANT};
  public static final String[] GENERATOR_OPS={GENERATOR};

  /** A fixed token that identifies the end of the operation.*/
  public static final String TERMINATOR_TOKEN="T";

  /** Max number of virtual hosts.*/
  public static final int MAX_HOSTS=64;

  /** Max value for a constant.*/
  public static final int MAX_CONSTANT=64000;

  /** Gets the parent of this node.
   *
   * @return the parent of this node.
   */
  public NodeElem getParent();
  
  /** Sets the parent of this node.
   *
   * @param parent The parent of this node.
   */
  public void  setParent(NodeElem parent);

  /** Gets a unique name for this node.
   *
   * @return A unique name for this node.
   */
  public String getUniqueName();

  /** Gets extra information about the node , e.g., the value of a
   * constant. 
   *
   *
   * @return Extra info about the node
   */
  public String getInfo();

  /** Gets the operation associated with this node.
   *
   * @return The operation associated with this node.
   */
  public String getOperation();


  /** Gets a logical host for placing this component.
   *
   *
   * @return A logical host for placing this component.
   */
  public String getHost();


  /** Gets whether this node is the left child of its parent.
   *
   *
   * @return Whether  this node is the left child of its parent.
   */
  public boolean isLeftChild();


  /** Traverses the tree fixing the "parent" pointers. Used for the
   * root node.
   */
  public void computeParents();

  /** Traverses the tree fixing the "parent" pointers.
   * @param isLeftChild whether we are a left child of our parent.
   */
  public void computeParents(boolean isLeftChild);

  /** Returns a vector with all the nodes in my current sub-tree
   * (including myself) that have an operation contained in a target
   * array of patterns.
   *
   * @param all A vector used to add the matched nodes.
   * @param pattern A collection of patterns that ANY will work.
   * @return A vector with all the nodes in my subtree matching ANY of
   * the patterns.
   */
  public Vector extract(Vector all, String[] pattern);


  /** Generates random subtrees from this node, assuming a given
   * branching probability.
   *
   * @param p The branching probability.
   * @param damping A damping factor to limit tree growth.
   */
  public void genRandomTree(double p, double damping); 

  /** Gets the number of nodes in this subtree (including this node).
   *
   * @return The number of nodes in this subtree (including this node).
   */
  public int getNumberNodes();
  
}
