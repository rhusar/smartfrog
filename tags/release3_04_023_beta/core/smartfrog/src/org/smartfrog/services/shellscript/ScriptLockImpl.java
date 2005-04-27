package org.smartfrog.services.shellscript;

public class ScriptLockImpl  implements ScriptLock {

  private ScriptExecution sexec= null;

  public ScriptLockImpl(ScriptExecution sexec) {
    this.sexec=sexec;
  }

  /**
   *
   * @return ScriptExecution
   * @todo Implement this org.smartfrog.services.shellscript.ScriptLock method
   */
  public ScriptExecution getScriptExecution() {
    return sexec;
  }
}
