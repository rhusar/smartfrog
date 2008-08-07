/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.sfcore.languages.sf.constraints;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.smartfrog.SFParse;
import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;
import org.smartfrog.sfcore.reference.AttribReferencePart;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.security.SFClassLoader;

import com.parctechnologies.eclipse.Atom;
import com.parctechnologies.eclipse.CompoundTerm;
import com.parctechnologies.eclipse.CompoundTermImpl;
import com.parctechnologies.eclipse.EXDRInputStream;
import com.parctechnologies.eclipse.EXDROutputStream;
import com.parctechnologies.eclipse.EclipseEngine;
import com.parctechnologies.eclipse.EclipseEngineOptions;
import com.parctechnologies.eclipse.EmbeddedEclipse;
import com.parctechnologies.eclipse.FromEclipseQueue;
import com.parctechnologies.eclipse.QueueListener;
import com.parctechnologies.eclipse.ToEclipseQueue;

/**
 * Implementation of solver for Eclipse
 */
public class EclipseSolver extends CoreSolver
        implements Runnable, QueueListener {

  /**
   * Default Eclipse options
   */
  private EclipseEngineOptions eclipseEngineOptions;

  /**
   * Eclipse engine
   */
  private EclipseEngine eclipse;

  /**
   * Record object to send to eclipse side
   */
  private Object get_val;

  /**
   * Thread for main eclipse goal
   */
  private Thread ecrThread;

  /**
   * sfConfig browser for purpose of setting user variables
   */
  private CDBrowser cdbm;

  /**
   * Are there user variables?
   */
  private boolean isuservars;

  /**
   * Component description pertaining to Constraint type being solved
   */
  private ComponentDescription solve_comp;

  /**
   * Queue for coms TO eclipse
   */
  private ToEclipseQueue java_to_eclipse;

  /**
   * Queue for coms FROM eclipse
   */
  private FromEclipseQueue eclipse_to_java;
  /**
   * Lock for object, so that solve method waits until constraint goal done
   * before exiting
   */
  private ReentrantLock solverLock = new ReentrantLock();
  /**
   * Condition for lock, so that solve method waits until constraint goal done
   * before exiting
   */
  private Condition solverFinished = solverLock.newCondition();
  /**
   * Indicates whether the eclipse secondary thread (for main eclipse goal) has
   * sought and lock yet
   */
  private boolean ecr_sought_lock = false;
  /**
   * Latest constraint goal has finished?
   */
  private boolean cons_finished = false;

  /**
   * Stores any exception thrown from Eclipse thread
   */
  private Exception general_error = null;

  /**
   * Relative path to constraint theory files
   */
  private static final String pathswitch = "/../constraints/";

  /**
   * Name of core theory file
   */
  private static final String coreFileSuffix = "core.ecl";
  /**
   * Name of additional theory file
   */
  private static final String theoryFileSuffix = "base.ecl";

  /**
   * Property indicating where to find additional user-specified theory files
   */
  private final String theoryFilePath
          = "opt.smartfrog.sfcore.languages.sf.constraints.theoryFilePath";


  private ConstraintResolutionState cresState;

  /**
   * Eclipse main goal to run in secondary thread
   */
  public void run() {
    try {
      eclipse.rpc("sfsolve");
    } catch (Exception e) {
      general_error = e;
      yieldLockFromECRThread();
    }
  }

  private static String sfHOME = SFSystem.getEnv("SFHOME");
  private static String corefile = sfHOME + pathswitch + coreFileSuffix;
  private static String thfile = sfHOME + pathswitch + theoryFileSuffix;

  static {
    if (sfHOME != null) {
      corefile = sfHOME + pathswitch + coreFileSuffix;
      thfile = sfHOME + pathswitch + theoryFileSuffix;
    }
  }

  public boolean getConstraintsPossible() {
    return true;
  }


  /**
   * Prepare the solver
   */
  protected void prepareSolver() throws SmartFrogResolutionException {

    if (sfHOME == null) {
      throw new SmartFrogResolutionException(
              "Environment variable SFHOME must be set. Context: constraint processing");
    }

    // create the theory
    try {
      prepareTheory(corefile, thfile);
    } catch (Exception e) {
      throw new SmartFrogResolutionException(
              "Unable to parse base theory for constraint resolution. ", e);
    }

    //Add the path root
    String thpath = System.getProperty(theoryFilePath);

    if (thpath != null) {
      try {
        runGoal("add_path(\"" + thpath + "\")");
      } catch (Exception e) {
        throw new SmartFrogResolutionException(
                "Unable to add root theory file path. ", e);
      }
    }
  }

  public void resetDoneBacktracking() {
    if (cresState != null) cresState.resetDoneBacktracking();
  }

  public Context hasBacktrackedTo() {
    if (cresState != null) {
      return cresState.hasBacktrackedTo();
    } else {
      return null;
    }
  }

  public void setShouldUndo(boolean undo) {
    if (cresState != null) cresState.setConstraintsShouldUndo(undo);
  }

  /**
   * Add undo action to current lhr
   *
   * @param ctxt
   * @param key
   * @param value
   */
  public void addUndo(Context ctxt, Object key, Object value) {
    if (cresState != null) cresState.addUndo(ctxt, key, value);
  }

  /**
   * Add undo action to current lhr
   *
   * @param fv
   * @param type
   */
  public void addUndo(FreeVar fv, int type) {
    if (cresState != null) cresState.addUndo(fv, type);
  }


  /**
   * Prepare the engine with two theories
   */
  public void prepareTheory(String coreFile, String coreFile2)
          throws Exception {
    // Get Eclipse Installation Directory...
    String eclipseDirectory = System.getenv("ECLIPSEDIRECTORY");

    if (eclipseDirectory == null) {
      throw new Exception(); //handle properly...
    }

    //Set up eclipse...
    eclipseEngineOptions = new EclipseEngineOptions(new File(eclipseDirectory));

    // Connect the Eclipse's standard streams to the JVMs
    eclipseEngineOptions.setUseQueues(false);

    // Initialise Eclipse
    eclipse = EmbeddedEclipse.getInstance(eclipseEngineOptions);

    //Consult core theory file
    eclipse.compile(new File(coreFile));

    //Consult core theory file
    eclipse.compile(new File(coreFile2));

    // Set up the java representation of two queue streams
    java_to_eclipse = eclipse.getToEclipseQueue("java_to_eclipse");
    eclipse_to_java = eclipse.getFromEclipseQueue("eclipse_to_java");

    eclipse_to_java.setListener(this);
  }

  /**
   * Run eclipse goal...
   */
  public void runGoal(String goal) {/*Do nothing for now*/}

  ;

  /**
   * Stop solving altogether
   */
  public void stopSolving() throws Exception {
    get_val = "sfstop";
    try {
      java_to_eclipse.setListener(this);
      //((EmbeddedEclipse) m_eclipse).destroy();   Do not destroy. Remove this. Need to do a "clean" of existing...
    } catch (Exception ignored) {

    }
    ecrThread = null;
    cresState = null;
  }

  /**
   * Solve a Constraint goal
   */
  public void solve(ComponentDescription comp, Vector attrs, Vector values,
                    Vector goal, Vector autos, boolean isuservars)
          throws Exception {

    //New Constraint Evaluation History, if appropriate...
    if (cresState == null) {
      cresState = new ConstraintResolutionState();
    }

    //Set comp as current solving comp descr
    solve_comp = comp;

    //Set is user vars?
    this.isuservars = isuservars;

    //Construct goal
    String _attrs = mapValueJE(attrs);
    String _values = mapValueJE(values);
    String _goal = mapValueJE(goal);
    String _autos = mapValueJE(autos);
    String verbose = (SFParse.isVerboseOptSet() ? "true" : "false");

    get_val = "sfsolve(" + _attrs + ", " + _values + ", " +
            cresState.addConstraintEval(comp.sfContext()) + ", " + _goal + ", "
            + _autos + ", " + verbose + ")";

    //System.out.println(m_get_val);

    //Allocate new thread for goal and start it
    cons_finished = ecr_sought_lock = false;

    if (ecrThread == null) {
      ecrThread = new Thread(this);
      ecrThread.start();
    }

    //Let rip...
    try {
      java_to_eclipse.setListener(this);
    } catch (Exception e) {
    }

    //Lock me to make sure I don't complete before constraint eval does...
    solverLock.lock();
    if (!cons_finished) {
      solverFinished.await();
    }
    solverLock.unlock();

    if (general_error != null) {
      throw general_error;
    }
  }

  /**
   * Maps Java object to String for transfer to Eclipse
   *
   * @param v object to be transformed
   * @return eclipse string
   */
  private String mapValueJE(Object v) throws SmartFrogResolutionException {
    return mapValueJE(v, false);
  }

  /**
   * Maps Java object to String for transfer to Eclipse
   *
   * @param v      object to be transformed
   * @param quoted indicates whether Strings should be double quoted
   * @return eclipse string
   */
  private String mapValueJE(Object v, boolean quoted)
          throws SmartFrogResolutionException {
    if (v instanceof FreeVar) {
      FreeVar fv = (FreeVar) v;
      if (fv.getConsEvalKey() != null) {
        if (fv.getConsEvalIdx() == -1) {
          fv.setConsEvalIdx(
                  cresState.getConsEvalIdx() + 1);  //+1 as yet to add entry

          String range_s = "null";
          Object range = fv.getRange();
          if (range != null) {
            if (range instanceof String) {
              range_s = (String) range;
            } else if (range instanceof Vector) {
              range_s = mapValueJE(range);
            } else {
              throw new SmartFrogResolutionException(
                      "Invalid range specifier for FreeVar: " + v);
            }
          }

          Object defVal = fv.getDefVal();
          String defVal_s = (defVal != null ? defVal_s = ", " + defVal
                  .toString() : "");
          return "sfvar(" + range_s + defVal_s + ")";

        } else {
          return "sfref(" + fv.getConsEvalIdx() + ", "
                  + fv.getConsEvalKey() + ")";
        }
      } else {
        return v.toString();
      }
    } else if (v instanceof Number) {
      return v.toString();
    } else if (v instanceof Vector) {
      String val = "[";
      boolean first = true;
      Iterator it = ((Vector) v).iterator();
      while (it.hasNext()) {
        if (first) {
          first = false;
        } else {
          val += ", ";
        }

        val += mapValueJE(it.next(), quoted);
      }
      val += "]";
      return val;
    } else if (v instanceof String) {
      if (quoted) {
        return "\"" + v + "\"";
      } else {
        return (String) v;
      }
    } else if (v instanceof SFNull) {
      return "sfnull";
    } else if (v instanceof SFReference) {
      ReferencePart rp = ((SFReference) v).firstElement();
      if (rp instanceof AttribReferencePart) {
        String ret_s = ((AttribReferencePart) rp).getValue().toString();
        if (quoted) {
          ret_s = "\"" + ret_s + "\"";
        }
        return ret_s;
      } else {
        return null;
      }
    } else if (v instanceof ComponentDescription) {
      return "sfcd";
    } else if (v instanceof Boolean) {
      return v.toString();
    } else {
      return null;
    }
  }


  /**
   * Maps Eclipse object to Java object after transfer from Eclipse
   *
   * @param v object to be transformed
   * @return transformed object
   */
  private Object mapValueEJ(Object v) {
    return mapValueEJ(v, false);
  }

  /**
   * Maps Eclipse object to Java object after transfer from Eclipse
   *
   * @param v          object to be transformed
   * @param ref_create indicates whether references should be created for atoms
   *                   -- used for subtyping directives
   * @return transformed object
   */
  private Object mapValueEJ(Object v, boolean ref_create) {
    if (v == null) {
      return new FreeVar();
    } else if (v.equals(Collections.EMPTY_LIST)) {
      return new Vector();
    } else if (v instanceof Number) {
      return v;
    } else if (v instanceof Collection) {
      Vector result = new Vector();
      Iterator it = ((Collection) v).iterator();
      while (it.hasNext()) {
        result.add(mapValueEJ(it.next(), ref_create));
      }
      return result;
    } else if (v instanceof String) {
      return v;
    } else if (v instanceof Atom) {
      Atom va = (Atom) v;
      if (va.functor().equals("sfnull")) {
        return SFNull.get();
      } else {
        String va_s = va.functor();
        try {
          if (ref_create) return Reference.fromString(va_s);
          return va_s;
        } catch (SmartFrogResolutionException sfre) {
          throw new SmartFrogEclipseRuntimeException(
                  "mapValueEJ: Trouble in creating reference from attribute string for subtyping. ");
        }
      }
    } else {
      throw new SmartFrogEclipseRuntimeException(
              "mapValueEJ: unknown data *from* solver " + v);
    }
  }

  /**
   * Unchecked Eclipse Exception, for throwing from Listeners dataAvailable,
   * dataRequest
   *
   * @author anfarr
   */
  static public class SmartFrogEclipseRuntimeException
          extends RuntimeException {
    SmartFrogEclipseRuntimeException(Throwable cause) {
      super(null, cause);
    }

    SmartFrogEclipseRuntimeException(String msg, Throwable cause) {
      super(msg, cause);
    }

    SmartFrogEclipseRuntimeException(String msg) {
      super(msg, null);
    }
  }

  private EclipseStatus est;
  private Vector rangeAttrs = new Vector();
  private EclipseCDAttr ecda;

  public class EclipseStatus {
    private boolean done;
    private int undo;
    private boolean back;
    private String ref;
    private String val;

    public void undo() {
      get_val = new Atom("back");
      try {
        java_to_eclipse.setListener(EclipseSolver.this);
      } catch (Exception e) {
        throw new SmartFrogEclipseRuntimeException(
                "Unanable to set JtoE listener", e);
      }
    }

    public void done() {

      cdbm.kill();
      Iterator atiter = rangeAttrs.iterator();
      get_val = new Atom("done");

      try {
        java_to_eclipse.setListener(EclipseSolver.this);
      } catch (Exception e) {
        throw new SmartFrogEclipseRuntimeException(
                "Unable to set JtoE listener", e);
      }

    }

    public boolean isDone() {
      return done;
    }

    public void setBack(boolean back) {
      this.back = back;
    }

    public boolean isBack() {
      return back;
    }

    public int getUndo() {
      return undo;
    }

    public String getUndoLabel() {
      if (ref != null) {
        return ref + " currently set to " + val;
      } else {
        return "";
      }
    }
  }


  public class EclipseCDAttr {
    private String name;
    private Object val;
    private Object range;
    private boolean set;
    private ComponentDescription cd;
    private Class vclass;

    public boolean isSet() {
      return set;
    }

    public boolean process_sel(String entry) {
      ecda = this;
      Collection c = (Collection) range;
      Iterator iter = c.iterator();
      while (iter.hasNext()) {
        String el = iter.next().toString();
        if (el.equals(entry)) {
          get_val = new CompoundTermImpl("set", new Atom(name),
                  new Atom(entry));
          try {
            java_to_eclipse.setListener(EclipseSolver.this);
          } catch (Exception e) {
            throw new SmartFrogEclipseRuntimeException(
                    "Unanable to reset JtoE listener", e);
          }

          return true;
        }
      }
      return false;
    }

    public String getRangeAsString() {
      return range.toString();
    }

    public String getAttrName() {
      return name;
    }

    public String toString() {
      if (set) {
        return name + " has value: " + val;
      } else {
        return name + " ranges over: " + range;
      }
    }
  }

  void populateBrowser() {
    Object root = cdbm.attr(null, "sfConfig", false);
    populateBrowser(orig, root, false);
  }

  void populateBrowser(ComponentDescription cd, Object root, boolean showme) {

    Iterator attriter = cd.sfAttributes();
    Context cxt = cd.sfContext();
    //String cxts = create_ref_str(cd);
    while (attriter.hasNext()) {
      Object attr = attriter.next();

      Object val = null;

      try {
        val = cxt.sfResolveAttribute(attr);
      } catch (Exception e) {
        throw new SmartFrogEclipseRuntimeException(
                "Can not resolve attribute when populating browser", e);
      }

      if (val instanceof SFApplyReference) {
        SFApplyReference val_ar = (SFApplyReference) val;
        val = val_ar.getComponentDescription();
      }

      if (val instanceof ComponentDescription) {
        boolean showkids = (!showme && solve_comp == val);
        Object chroot = cdbm.attr(root, attr.toString(), showme);
        populateBrowser((ComponentDescription) val, chroot, showkids);
      } else {
        EclipseCDAttr ecda = new EclipseCDAttr();
        ecda.cd = cd;
        ecda.name = attr.toString();
        ecda.val = val;
        cdbm.attr(root, ecda, showme);
        try {
          if (cd == solve_comp && cxt.sfContainsTag(attr, "sfConstraintUserVar")
                  && val instanceof FreeVar) {
            rangeAttrs.add(ecda);
            ecda.set = false;
          } else {
            ecda.set = true;
          }
        } catch (Exception e) {
          throw new SmartFrogEclipseRuntimeException(
                  "Can not check attribute for tag", e);
        }

      }

    }

  }

  void sfuser() {

    String classname = System.getProperty(
            "org.smartfrog.sfcore.languages.sf.constraints.CDBrowser");
    if (classname == null || !isuservars) {
      get_val = new Atom("done");
      try {
        java_to_eclipse.setListener(EclipseSolver.this);
      } catch (Exception e) {
        throw new SmartFrogEclipseRuntimeException(
                "Unable to set JtoE listener",e);
      }
      return;
    }

    try {
      cdbm = (CDBrowser) SFClassLoader.forName(classname).newInstance();

    } catch (Exception e) {
      throw new SmartFrogEclipseRuntimeException(
              "Can not instantiate CD Browser",e);

    }

    populateBrowser();


    if (rangeAttrs.size() != 0) {

      List ranges = new LinkedList();
      Iterator raiter = rangeAttrs.iterator();
      while (raiter.hasNext()) {
        EclipseCDAttr ecda = (EclipseCDAttr) raiter.next();
        ranges.add(ecda.getAttrName());
      }

      est = new EclipseStatus();
      cdbm.setES(est);
      get_val = new CompoundTermImpl("range", ranges);

    } else {
      get_val = new Atom("done");
    }

    try {
      java_to_eclipse.setListener(EclipseSolver.this);
    } catch (Exception e) {
      throw new SmartFrogEclipseRuntimeException("Unable to set JtoE listener",e);
    }

  }


  void range(CompoundTerm ct) {
    Collection c = (Collection) ct.arg(1);
    Iterator citer = c.iterator();
    Iterator riter = rangeAttrs.iterator();
    boolean all_done = true;

    while (citer.hasNext()) {
      EclipseCDAttr ecda = (EclipseCDAttr) riter.next();
      Collection range = (Collection) citer.next();
      if (range.size() > 1) {
        ecda.range = mapValueEJ(range);
        ecda.set = false;
        all_done = false;
      } else {
        Iterator range_iter = range.iterator();
        ecda.val = mapValueEJ(range_iter.next());
        ecda.set = true;
      }
    }

    est.done = all_done;
    cdbm.redraw();
  }

  void set(CompoundTerm ct) {
    est.undo = ((Integer) ct.arg(1)).intValue();
    Object ref = ct.arg(2);
    est.ref = (ref != null ? ((CompoundTerm) ref).functor() : null);
    Object val = ct.arg(3);
    est.val = (val != null ? ((CompoundTerm) val).functor() : null);
    est.back = ((CompoundTerm) ct.arg(4)).functor().equals("back");
    get_val = new Atom("range");
    try {
      java_to_eclipse.setListener(EclipseSolver.this);
    } catch (Exception e) {
      throw new SmartFrogEclipseRuntimeException("Unable to set JtoE listener",e);
    }
  }


  /**
   * Called when Eclipse flushes source
   */
  public void dataAvailable(Object source) {

    FromEclipseQueue m_iqueue = null;
    EXDRInputStream m_iqueue_formatted = null;

    if (m_iqueue == null) {
      m_iqueue = (FromEclipseQueue) source;
      m_iqueue_formatted = new EXDRInputStream(m_iqueue);
    }

    CompoundTerm ct = null;
    try {
      ct = (CompoundTerm) m_iqueue_formatted.readTerm();
    } catch (IOException ioe) {
      throw new SmartFrogEclipseRuntimeException(
              "dataAvailable: Unable to *read* from input stream. ", ioe);
    }

    String func = ct.functor();

    if (func.equals("sffailed")) {
      throw new SmartFrogEclipseRuntimeException(
              "Unable to solve constraints. General failure.");
    } else if (func.equals("sfuser")) {
      sfuser();
    } else if (func.equals("range")) {
      range(ct);
    } else if (func.equals("set")) {
      set(ct);
    } else if (func.equals("norange")) {
      throw new SmartFrogEclipseRuntimeException(
              "Unable to collect range information for sfConsUser tagged attributes. Probably because it has not been set in constraint annotations");
    }
    //If done on constraint goal, yield lock so solve() may complete
    else if (func.equals("sfdonegoal")) {
      yieldLockFromECRThread();
    }
    //Setting an attributes value
    else if (func.equals("sfset")) {

      int idx = ((Integer) ct.arg(1)).intValue();
      Object val = mapValueEJ(ct.arg(2));
      String qual = ((Atom) ct.arg(3)).functor();
      Collection ctar = (Collection) ct.arg(4);
      int cidx = ((Integer) ct.arg(5)).intValue();


      boolean sfcd = false;

      if (val instanceof String) {
        String val_s = (String) val;
        if (val_s.indexOf("sfcd") == 0) {
          sfcd = true;
          val = val_s.substring(4);
        }
      }

      FreeVar fv = null;
      if (qual.equals("first")) {
        val = fv = new FreeVar();
      }

      Iterator tar_iter = ctar.iterator();
      cresState.backtrackConstraintAss(idx, cidx);

      if (sfcd) val = cresState.adjustSetValue(val);

      boolean first = true;
      while (tar_iter.hasNext()) {
        CompoundTerm prim = (CompoundTerm) tar_iter.next();
        cidx = ((Integer) prim.arg(1)).intValue();
        String key = ((Atom) prim.arg(2)).functor();

        if (first) {
          if (fv != null) {
            fv.setConsEvalIdx(cidx);
            fv.setConsEvalKey(key);
          }
          first = false;
        }
        try {
          boolean success = cresState
                  .addConstraintAss(solve_comp, idx, key, val, cidx);
          get_val = "" + success;
        } catch (SmartFrogResolutionException smfre) {
          throw new SmartFrogEclipseRuntimeException(smfre);
        }
        try {
          java_to_eclipse.setListener(this);
        } catch (Exception e) {
        }
      }

    } else if (func.equals("sfsubtype")) {
      String attr = ((Atom) ct.arg(1)).functor();
      Vector types = (Vector) mapValueEJ(ct.arg(2),
              true);  //by this stage we know its a Vector
      cresState.setTyping(attr, types);
    }
  }

  /**
   * Yields lock so that solve() may finish
   */
  void yieldLockFromECRThread() {
    //Need to own lock in order to signal to opposition...
    if (!ecr_sought_lock) solverLock.lock();

    cons_finished = true;
    solverFinished.signalAll();
    solverLock.unlock();
  }

  /**
   * Called when Eclipse demands data
   */
  public void dataRequest(Object source) {
    ToEclipseQueue oqueue = null;
    EXDROutputStream oqueue_formatted = null;

    if (!ecr_sought_lock) {
      ecr_sought_lock = true;
      solverLock.lock();
    }

    if (oqueue == null) {
      oqueue = (ToEclipseQueue) source;
      oqueue_formatted = new EXDROutputStream(oqueue);
    }

    try {
      if (get_val != null) {
        oqueue_formatted.write(get_val);
        java_to_eclipse.setListener(null);
        get_val = null;
      } else {
        throw new SmartFrogEclipseRuntimeException(
                "dataRequest: No data available to write. ");
      }
    } catch (IOException ioe) {
      throw new SmartFrogEclipseRuntimeException(
              "dataRequest: Unable to *write* on output stream. ", ioe);
    }
  }
}

