package org.smartfrog.regtest.arithmetic;

import java.rmi.*;
import java.util.*;
import java.lang.*;

import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.compound.*;


public class Generator extends NetElemImpl implements Remote {

  int seed;
  int delay;
  int diff;
  int min;

  Thread generator;

  class TheGenerator extends Thread {
	public void run() {
	  Random r = new Random(seed);
	  while (true) {
	      int v = Math.abs((r.nextInt()%diff))+min;
	      System.out.println(name + " generating " + v);
	      addValue(v);
	      try  {
		  sleep(delay*1000);
		} catch (Exception e) {}
	  }
	}
  }


  public Generator() throws java.rmi.RemoteException {
	super();
  }

  public void sfStart() throws SmartFrogException, RemoteException {
	super.sfStart();
	min = ((Integer) sfResolve("min")).intValue();
	diff = ((Integer) sfResolve("max")).intValue() - min + 1;
	seed = ((Integer) sfResolve("seed")).intValue();
	delay = ((Integer) sfResolve("interval")).intValue();
	generator = new TheGenerator();
	generator.start();
  } 
 
  public void sfTerminateWith(TerminationRecord tr) {
	try {
	  if (generator != null) generator.stop();
	} catch (Exception e) {}
	super.sfTerminateWith(tr);
  }  
}
