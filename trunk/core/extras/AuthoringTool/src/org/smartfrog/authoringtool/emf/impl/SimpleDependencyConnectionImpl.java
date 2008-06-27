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
import org.smartfrog.authoringtool.emf.SimpleDependencyConnection;
import org.smartfrog.authoringtool.emf.SmartfrogPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Simple Dependency Connection</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.SimpleDependencyConnectionImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.SimpleDependencyConnectionImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.SimpleDependencyConnectionImpl#getRelevant <em>Relevant</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.SimpleDependencyConnectionImpl#getEnabled <em>Enabled</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.SimpleDependencyConnectionImpl#getDependency_Name <em>Dependency Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SimpleDependencyConnectionImpl extends EObjectImpl implements SimpleDependencyConnection
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
   * The default value of the '{@link #getDependency_Name() <em>Dependency Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDependency_Name()
   * @generated
   * @ordered
   */
  protected static final String DEPENDENCY_NAME_EDEFAULT = "0";

  /**
   * The cached value of the '{@link #getDependency_Name() <em>Dependency Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDependency_Name()
   * @generated
   * @ordered
   */
  protected String dependency_Name = DEPENDENCY_NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SimpleDependencyConnectionImpl()
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
    return SmartfrogPackage.Literals.SIMPLE_DEPENDENCY_CONNECTION;
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
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__SOURCE, oldSource, source));
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
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__SOURCE, oldSource, source));
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
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__TARGET, oldTarget, target));
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
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__TARGET, oldTarget, target));
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
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__RELEVANT, oldRelevant, relevant));
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
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__ENABLED, oldEnabled, enabled));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getDependency_Name()
  {
    return dependency_Name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDependency_Name(String newDependency_Name)
  {
    String oldDependency_Name = dependency_Name;
    dependency_Name = newDependency_Name;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__DEPENDENCY_NAME, oldDependency_Name, dependency_Name));
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
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__SOURCE:
        if (resolve) return getSource();
        return basicGetSource();
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__TARGET:
        if (resolve) return getTarget();
        return basicGetTarget();
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__RELEVANT:
        return getRelevant();
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__ENABLED:
        return getEnabled();
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__DEPENDENCY_NAME:
        return getDependency_Name();
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
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__SOURCE:
        setSource((Component)newValue);
        return;
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__TARGET:
        setTarget((Component)newValue);
        return;
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__RELEVANT:
        setRelevant((String)newValue);
        return;
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__ENABLED:
        setEnabled((String)newValue);
        return;
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__DEPENDENCY_NAME:
        setDependency_Name((String)newValue);
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
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__SOURCE:
        setSource((Component)null);
        return;
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__TARGET:
        setTarget((Component)null);
        return;
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__RELEVANT:
        setRelevant(RELEVANT_EDEFAULT);
        return;
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__ENABLED:
        setEnabled(ENABLED_EDEFAULT);
        return;
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__DEPENDENCY_NAME:
        setDependency_Name(DEPENDENCY_NAME_EDEFAULT);
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
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__SOURCE:
        return source != null;
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__TARGET:
        return target != null;
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__RELEVANT:
        return RELEVANT_EDEFAULT == null ? relevant != null : !RELEVANT_EDEFAULT.equals(relevant);
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__ENABLED:
        return ENABLED_EDEFAULT == null ? enabled != null : !ENABLED_EDEFAULT.equals(enabled);
      case SmartfrogPackage.SIMPLE_DEPENDENCY_CONNECTION__DEPENDENCY_NAME:
        return DEPENDENCY_NAME_EDEFAULT == null ? dependency_Name != null : !DEPENDENCY_NAME_EDEFAULT.equals(dependency_Name);
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
    result.append(", Dependency_Name: ");
    result.append(dependency_Name);
    result.append(')');
    return result.toString();
  }

} //SimpleDependencyConnectionImpl
