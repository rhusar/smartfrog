/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.smartfrog.authoringtool.emf.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.smartfrog.authoringtool.emf.Component;
import org.smartfrog.authoringtool.emf.Connectors;
import org.smartfrog.authoringtool.emf.OutputDependencyConnection;
import org.smartfrog.authoringtool.emf.SmartfrogPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Output Dependency Connection</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.OutputDependencyConnectionImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.OutputDependencyConnectionImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.OutputDependencyConnectionImpl#getRelevant <em>Relevant</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.OutputDependencyConnectionImpl#getEnabled <em>Enabled</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.OutputDependencyConnectionImpl#getOutput_Dependency_Name <em>Output Dependency Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OutputDependencyConnectionImpl extends EObjectImpl implements OutputDependencyConnection
{
  /**
   * The cached value of the '{@link #getSource() <em>Source</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSource()
   * @generated
   * @ordered
   */
  protected Connectors source;

  /**
   * The cached value of the '{@link #getTarget() <em>Target</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTarget()
   * @generated
   * @ordered
   */
  protected Component target;

  /**
   * The default value of the '{@link #getRelevant() <em>Relevant</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelevant()
   * @generated
   * @ordered
   */
  protected static final String RELEVANT_EDEFAULT = "0";

  /**
   * The cached value of the '{@link #getRelevant() <em>Relevant</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelevant()
   * @generated
   * @ordered
   */
  protected String relevant = RELEVANT_EDEFAULT;

  /**
   * The default value of the '{@link #getEnabled() <em>Enabled</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnabled()
   * @generated
   * @ordered
   */
  protected static final String ENABLED_EDEFAULT = "0";

  /**
   * The cached value of the '{@link #getEnabled() <em>Enabled</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnabled()
   * @generated
   * @ordered
   */
  protected String enabled = ENABLED_EDEFAULT;

  /**
   * The default value of the '{@link #getOutput_Dependency_Name() <em>Output Dependency Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOutput_Dependency_Name()
   * @generated
   * @ordered
   */
  protected static final String OUTPUT_DEPENDENCY_NAME_EDEFAULT = "0";

  /**
   * The cached value of the '{@link #getOutput_Dependency_Name() <em>Output Dependency Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOutput_Dependency_Name()
   * @generated
   * @ordered
   */
  protected String output_Dependency_Name = OUTPUT_DEPENDENCY_NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OutputDependencyConnectionImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EClass eStaticClass()
  {
    return SmartfrogPackage.Literals.OUTPUT_DEPENDENCY_CONNECTION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Connectors getSource()
  {
    if (source != null && source.eIsProxy())
    {
      InternalEObject oldSource = (InternalEObject)source;
      source = (Connectors)eResolveProxy(oldSource);
      if (source != oldSource)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__SOURCE, oldSource, source));
      }
    }
    return source;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Connectors basicGetSource()
  {
    return source;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSource(Connectors newSource)
  {
    Connectors oldSource = source;
    source = newSource;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__SOURCE, oldSource, source));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Component getTarget()
  {
    if (target != null && target.eIsProxy())
    {
      InternalEObject oldTarget = (InternalEObject)target;
      target = (Component)eResolveProxy(oldTarget);
      if (target != oldTarget)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__TARGET, oldTarget, target));
      }
    }
    return target;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Component basicGetTarget()
  {
    return target;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTarget(Component newTarget)
  {
    Component oldTarget = target;
    target = newTarget;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__TARGET, oldTarget, target));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getRelevant()
  {
    return relevant;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRelevant(String newRelevant)
  {
    String oldRelevant = relevant;
    relevant = newRelevant;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__RELEVANT, oldRelevant, relevant));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getEnabled()
  {
    return enabled;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEnabled(String newEnabled)
  {
    String oldEnabled = enabled;
    enabled = newEnabled;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__ENABLED, oldEnabled, enabled));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getOutput_Dependency_Name()
  {
    return output_Dependency_Name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOutput_Dependency_Name(String newOutput_Dependency_Name)
  {
    String oldOutput_Dependency_Name = output_Dependency_Name;
    output_Dependency_Name = newOutput_Dependency_Name;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__OUTPUT_DEPENDENCY_NAME, oldOutput_Dependency_Name, output_Dependency_Name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__SOURCE:
        if (resolve) return getSource();
        return basicGetSource();
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__TARGET:
        if (resolve) return getTarget();
        return basicGetTarget();
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__RELEVANT:
        return getRelevant();
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__ENABLED:
        return getEnabled();
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__OUTPUT_DEPENDENCY_NAME:
        return getOutput_Dependency_Name();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__SOURCE:
        setSource((Connectors)newValue);
        return;
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__TARGET:
        setTarget((Component)newValue);
        return;
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__RELEVANT:
        setRelevant((String)newValue);
        return;
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__ENABLED:
        setEnabled((String)newValue);
        return;
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__OUTPUT_DEPENDENCY_NAME:
        setOutput_Dependency_Name((String)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__SOURCE:
        setSource((Connectors)null);
        return;
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__TARGET:
        setTarget((Component)null);
        return;
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__RELEVANT:
        setRelevant(RELEVANT_EDEFAULT);
        return;
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__ENABLED:
        setEnabled(ENABLED_EDEFAULT);
        return;
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__OUTPUT_DEPENDENCY_NAME:
        setOutput_Dependency_Name(OUTPUT_DEPENDENCY_NAME_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__SOURCE:
        return source != null;
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__TARGET:
        return target != null;
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__RELEVANT:
        return RELEVANT_EDEFAULT == null ? relevant != null : !RELEVANT_EDEFAULT.equals(relevant);
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__ENABLED:
        return ENABLED_EDEFAULT == null ? enabled != null : !ENABLED_EDEFAULT.equals(enabled);
      case SmartfrogPackage.OUTPUT_DEPENDENCY_CONNECTION__OUTPUT_DEPENDENCY_NAME:
        return OUTPUT_DEPENDENCY_NAME_EDEFAULT == null ? output_Dependency_Name != null : !OUTPUT_DEPENDENCY_NAME_EDEFAULT.equals(output_Dependency_Name);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (Relevant: ");
    result.append(relevant);
    result.append(", Enabled: ");
    result.append(enabled);
    result.append(", Output_Dependency_Name: ");
    result.append(output_Dependency_Name);
    result.append(')');
    return result.toString();
  }

} //OutputDependencyConnectionImpl
