/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */
package org.smartfrog.regtest.arithmetic;

import java.rmi.*;
import org.smartfrog.sfcore.common.*;

//import org.smartfrog.services.scripting.*;
import org.smartfrog.sfcore.reference.*;
import java.util.Vector;
import org.smartfrog.tools.testharness.TestHelper;

public class Comparator extends NetElemImpl implements Remote {
  Vector candidateCache = new Vector(100);
  Vector targetCache = new Vector();
  /** Reference used to look up the equals count */
  protected static final Reference refCount =
      new Reference(ReferencePart.here("count"));

  // the number of times we want the component to match target and candidate
  int equalsCount = 1;

  int actualCount = 0;
  public Comparator() throws java.rmi.RemoteException {
  }

  public void searchCache(Integer currentTarget) {
    int index = candidateCache.indexOf(currentTarget); // returns the first index matching
    if (index != -1) {
      // we have a match. Remove all candidates previous to the one matching the target
      for (int i = 0; i <= index; i++) {
        candidateCache.removeElementAt(0);
        //candidateCache.removeElementAt(i);// india team changes
      }
      // and remove the current target.
      targetCache.removeElementAt(0);
      System.out.println("\n++++++" +
                         "\n  Match : candidate = target = " + currentTarget.intValue()
                         + ", actual count: " + (actualCount + 1) + "/" + equalsCount
                         + ",  " + sfCompleteNameSafe()
                         + "\n++++++");
      if (++actualCount == equalsCount) {
        TestHelper.printSFStartDone(System.out, true);
      }
    }
  }

  /**
   * Check if target and candidate are equal, and stop the test if they have been
   * for 'equalsCount' times.
   */
  protected int evaluate(String from, int value) {

    if (from.equals("target")) {
      targetCache.addElement(new Integer(value));
    } else {
      candidateCache.addElement(new Integer(value));
    }

    System.out.println("COMPARATOR: " + " evaluate " + value
                       + " from '" + from +"'"
                       + ", Target size: " + targetCache.size()
                       + ", " + this.sfCompleteNameSafe());
    // System.out.println( " Candidate : "+ candidateCache);
    // compare only if the first target has arrived. Always compare afterwards.
    if (targetCache.size() != 0) {
      Integer currentTarget = (Integer) targetCache.firstElement();
      searchCache(currentTarget);
      return value - currentTarget.intValue(); // irrelevant for comparison anyway
    } else {
      //System.out.println("Evaluate() ============>:returining -1 ");
      return -1;
    }
  }

  public void sfDeploy() throws SmartFrogException, RemoteException {
    super.sfDeploy();
    equalsCount = ( (Integer) sfResolve(refCount)).intValue();
    System.out.println(" COMPARATOR: " + " Evaluating " + equalsCount + " matches. " + sfCompleteNameSafe() );
  }
}
