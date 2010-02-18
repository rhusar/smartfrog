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
import org.smartfrog.authoringtool.emf.InputDependencyConnection;
import org.smartfrog.authoringtool.emf.SmartfrogPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Input Dependency Connection</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.InputDependencyConnectionImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.InputDependencyConnectionImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.InputDependencyConnectionImpl#getRelevant <em>Relevant</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.InputDependencyConnectionImpl#getEnabled <em>Enabled</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.InputDependencyConnectionImpl#getInput_Dependency_Name <em>Input Dependency Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InputDependencyConnectionImpl extends EObjectImpl implements InputDependencyConnection
{
  /**
   * The cached value of the '{@link #getSource() <em>Source</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSource()
   * @generated
   * @ordered
   */
  protected Component source;

  /**
   * The cached value of the '{@link #getTarget() <em>Target</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTarget()
   * @generated
   * @ordered
   */
  protected Connectors target;

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
   * The default value of the '{@link #getInput_Dependency_Name() <em>Input Dependency Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInput_Dependency_Name()
   * @generated
   * @ordered
   */
  protected static final String INPUT_DEPENDENCY_NAME_EDEFAULT = "0";

  /**
   * The cached value of the '{@link #getInput_Dependency_Name() <em>Input Dependency Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInput_Dependency_Name()
   * @generated
   * @ordered
   */
  protected String input_Dependency_Name = INPUT_DEPENDENCY_NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected InputDependencyConnectionImpl()
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
    return SmartfrogPackage.Literals.INPUT_DEPENDENCY_CONNECTION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Component getSource()
  {
    if (source != null && source.eIsProxy())
    {
      InternalEObject oldSource = (InternalEObject)source;
      source = (Component)eResolveProxy(oldSource);
      if (source != oldSource)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__SOURCE, oldSource, source));
      }
    }
    return source;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Component basicGetSource()
  {
    return source;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSource(Component newSource)
  {
    Component oldSource = source;
    source = newSource;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__SOURCE, oldSource, source));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Connectors getTarget()
  {
    if (target != null && target.eIsProxy())
    {
      InternalEObject oldTarget = (InternalEObject)target;
      target = (Connectors)eResolveProxy(oldTarget);
      if (target != oldTarget)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__TARGET, oldTarget, target));
      }
    }
    return target;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Connectors basicGetTarget()
  {
    return target;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTarget(Connectors newTarget)
  {
    Connectors oldTarget = target;
    target = newTarget;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__TARGET, oldTarget, target));
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
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__RELEVANT, oldRelevant, relevant));
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
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__ENABLED, oldEnabled, enabled));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getInput_Dependency_Name()
  {
    return input_Dependency_Name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInput_Dependency_Name(String newInput_Dependency_Name)
  {
    String oldInput_Dependency_Name = input_Dependency_Name;
    input_Dependency_Name = newInput_Dependency_Name;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__INPUT_DEPENDENCY_NAME, oldInput_Dependency_Name, input_Dependency_Name));
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
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__SOURCE:
        if (resolve) return getSource();
        return basicGetSource();
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__TARGET:
        if (resolve) return getTarget();
        return basicGetTarget();
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__RELEVANT:
        return getRelevant();
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__ENABLED:
        return getEnabled();
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__INPUT_DEPENDENCY_NAME:
        return getInput_Dependency_Name();
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
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__SOURCE:
        setSource((Component)newValue);
        return;
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__TARGET:
        setTarget((Connectors)newValue);
        return;
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__RELEVANT:
        setRelevant((String)newValue);
        return;
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__ENABLED:
        setEnabled((String)newValue);
        return;
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__INPUT_DEPENDENCY_NAME:
        setInput_Dependency_Name((String)newValue);
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
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__SOURCE:
        setSource((Component)null);
        return;
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__TARGET:
        setTarget((Connectors)null);
        return;
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__RELEVANT:
        setRelevant(RELEVANT_EDEFAULT);
        return;
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__ENABLED:
        setEnabled(ENABLED_EDEFAULT);
        return;
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__INPUT_DEPENDENCY_NAME:
        setInput_Dependency_Name(INPUT_DEPENDENCY_NAME_EDEFAULT);
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
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__SOURCE:
        return source != null;
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__TARGET:
        return target != null;
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__RELEVANT:
        return RELEVANT_EDEFAULT == null ? relevant != null : !RELEVANT_EDEFAULT.equals(relevant);
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__ENABLED:
        return ENABLED_EDEFAULT == null ? enabled != null : !ENABLED_EDEFAULT.equals(enabled);
      case SmartfrogPackage.INPUT_DEPENDENCY_CONNECTION__INPUT_DEPENDENCY_NAME:
        return INPUT_DEPENDENCY_NAME_EDEFAULT == null ? input_Dependency_Name != null : !INPUT_DEPENDENCY_NAME_EDEFAULT.equals(input_Dependency_Name);
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
    result.append(", Input_Dependency_Name: ");
    result.append(input_Dependency_Name);
    result.append(')');
    return result.toString();
  }

} //InputDependencyConnectionImpl
