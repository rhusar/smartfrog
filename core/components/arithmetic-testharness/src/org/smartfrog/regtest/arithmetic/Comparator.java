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
  public void searchCache(Integer currentTarget){
    int index = candidateCache.indexOf(currentTarget); // returns the first index matching
    if (index!=-1) {
      // we have a match. Remove all candidates previous to the one matching the target
      for (int i = 0 ; i <= index ; i++) {
        candidateCache.removeElementAt(0);
		//candidateCache.removeElementAt(i);// india team changes
      }
      // and remove the current target.
      targetCache.removeElementAt(0);
      System.out.println( " Match : candidate = target = "+currentTarget.intValue());
      if (++actualCount == equalsCount) {
        TestHelper.printSFStartDone(System.out,true);
      }
    }
  }
  /**
   * Check if target and candidate are equal, and stop the test if they have been
   * for 'equalsCount' times.
   */
  protected int evaluate(String from, int value) {
  try {
  System.out.println(this.sfCompleteName() +" evaluate "+value+ " from "+ from);
  } catch (Exception e) {}

    if (from.equals("target")) {
      targetCache.addElement(new Integer(value));
    } else {
      candidateCache.addElement(new Integer(value));
    }

    System.out.println( " Target size   : "+ targetCache.size());
   // System.out.println( " Candidate : "+ candidateCache);
    // compare only if the first target has arrived. Always compare afterwards.
    if(targetCache.size()!=0) {
      Integer currentTarget = (Integer) targetCache.firstElement();
      searchCache(currentTarget);
      return value - currentTarget.intValue(); // irrelevant for comparison anyway
    }
	else
	{
	//System.out.println("Evaluate() ============>:returining -1 ");	
      return -1;
	}
  }
  public void sfDeploy() throws SmartFrogException, RemoteException {
    super.sfDeploy();
    equalsCount = ((Integer) sfResolve(refCount)).intValue();
  }
}
