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

import org.smartfrog.authoringtool.emf.Attribute;
import org.smartfrog.authoringtool.emf.Component;
import org.smartfrog.authoringtool.emf.Composite;
import org.smartfrog.authoringtool.emf.SmartfrogPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl#getValue <em>Value</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl#isStateData <em>State Data</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl#isStateListen <em>State Listen</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl#isStateNotify <em>State Notify</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl#isIsLazyValue <em>Is Lazy Value</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl#getComponent_Attribute <em>Component Attribute</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl#getComposite_Arrtibute <em>Composite Arrtibute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AttributeImpl extends ModelObjectImpl implements Attribute
{
  /**
   * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValue()
   * @generated
   * @ordered
   */
  protected static final String VALUE_EDEFAULT = "0";

  /**
   * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValue()
   * @generated
   * @ordered
   */
  protected String value = VALUE_EDEFAULT;

  /**
   * The default value of the '{@link #isStateData() <em>State Data</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isStateData()
   * @generated
   * @ordered
   */
  protected static final boolean STATE_DATA_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isStateData() <em>State Data</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isStateData()
   * @generated
   * @ordered
   */
  protected boolean stateData = STATE_DATA_EDEFAULT;

  /**
   * The default value of the '{@link #isStateListen() <em>State Listen</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isStateListen()
   * @generated
   * @ordered
   */
  protected static final boolean STATE_LISTEN_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isStateListen() <em>State Listen</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isStateListen()
   * @generated
   * @ordered
   */
  protected boolean stateListen = STATE_LISTEN_EDEFAULT;

  /**
   * The default value of the '{@link #isStateNotify() <em>State Notify</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isStateNotify()
   * @generated
   * @ordered
   */
  protected static final boolean STATE_NOTIFY_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isStateNotify() <em>State Notify</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isStateNotify()
   * @generated
   * @ordered
   */
  protected boolean stateNotify = STATE_NOTIFY_EDEFAULT;

  /**
   * The default value of the '{@link #isIsLazyValue() <em>Is Lazy Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isIsLazyValue()
   * @generated
   * @ordered
   */
  protected static final boolean IS_LAZY_VALUE_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isIsLazyValue() <em>Is Lazy Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isIsLazyValue()
   * @generated
   * @ordered
   */
  protected boolean isLazyValue = IS_LAZY_VALUE_EDEFAULT;

  /**
   * The cached value of the '{@link #getComponent_Attribute() <em>Component Attribute</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComponent_Attribute()
   * @generated
   * @ordered
   */
  protected Component component_Attribute;

  /**
   * The cached value of the '{@link #getComposite_Arrtibute() <em>Composite Arrtibute</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComposite_Arrtibute()
   * @generated
   * @ordered
   */
  protected Composite composite_Arrtibute;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AttributeImpl()
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
    return SmartfrogPackage.Literals.ATTRIBUTE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setValue(String newValue)
  {
    String oldValue = value;
    value = newValue;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.ATTRIBUTE__VALUE, oldValue, value));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isStateData()
  {
    return stateData;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setStateData(boolean newStateData)
  {
    boolean oldStateData = stateData;
    stateData = newStateData;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.ATTRIBUTE__STATE_DATA, oldStateData, stateData));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isStateListen()
  {
    return stateListen;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setStateListen(boolean newStateListen)
  {
    boolean oldStateListen = stateListen;
    stateListen = newStateListen;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.ATTRIBUTE__STATE_LISTEN, oldStateListen, stateListen));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isStateNotify()
  {
    return stateNotify;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setStateNotify(boolean newStateNotify)
  {
    boolean oldStateNotify = stateNotify;
    stateNotify = newStateNotify;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.ATTRIBUTE__STATE_NOTIFY, oldStateNotify, stateNotify));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isIsLazyValue()
  {
    return isLazyValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setIsLazyValue(boolean newIsLazyValue)
  {
    boolean oldIsLazyValue = isLazyValue;
    isLazyValue = newIsLazyValue;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.ATTRIBUTE__IS_LAZY_VALUE, oldIsLazyValue, isLazyValue));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Component getComponent_Attribute()
  {
    if (component_Attribute != null && component_Attribute.eIsProxy())
    {
      InternalEObject oldComponent_Attribute = (InternalEObject)component_Attribute;
      component_Attribute = (Component)eResolveProxy(oldComponent_Attribute);
      if (component_Attribute != oldComponent_Attribute)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.ATTRIBUTE__COMPONENT_ATTRIBUTE, oldComponent_Attribute, component_Attribute));
      }
    }
    return component_Attribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Component basicGetComponent_Attribute()
  {
    return component_Attribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setComponent_Attribute(Component newComponent_Attribute)
  {
    Component oldComponent_Attribute = component_Attribute;
    component_Attribute = newComponent_Attribute;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.ATTRIBUTE__COMPONENT_ATTRIBUTE, oldComponent_Attribute, component_Attribute));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite getComposite_Arrtibute()
  {
    if (composite_Arrtibute != null && composite_Arrtibute.eIsProxy())
    {
      InternalEObject oldComposite_Arrtibute = (InternalEObject)composite_Arrtibute;
      composite_Arrtibute = (Composite)eResolveProxy(oldComposite_Arrtibute);
      if (composite_Arrtibute != oldComposite_Arrtibute)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.ATTRIBUTE__COMPOSITE_ARRTIBUTE, oldComposite_Arrtibute, composite_Arrtibute));
      }
    }
    return composite_Arrtibute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite basicGetComposite_Arrtibute()
  {
    return composite_Arrtibute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setComposite_Arrtibute(Composite newComposite_Arrtibute)
  {
    Composite oldComposite_Arrtibute = composite_Arrtibute;
    composite_Arrtibute = newComposite_Arrtibute;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.ATTRIBUTE__COMPOSITE_ARRTIBUTE, oldComposite_Arrtibute, composite_Arrtibute));
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
      case SmartfrogPackage.ATTRIBUTE__VALUE:
        return getValue();
      case SmartfrogPackage.ATTRIBUTE__STATE_DATA:
        return isStateData() ? Boolean.TRUE : Boolean.FALSE;
      case SmartfrogPackage.ATTRIBUTE__STATE_LISTEN:
        return isStateListen() ? Boolean.TRUE : Boolean.FALSE;
      case SmartfrogPackage.ATTRIBUTE__STATE_NOTIFY:
        return isStateNotify() ? Boolean.TRUE : Boolean.FALSE;
      case SmartfrogPackage.ATTRIBUTE__IS_LAZY_VALUE:
        return isIsLazyValue() ? Boolean.TRUE : Boolean.FALSE;
      case SmartfrogPackage.ATTRIBUTE__COMPONENT_ATTRIBUTE:
        if (resolve) return getComponent_Attribute();
        return basicGetComponent_Attribute();
      case SmartfrogPackage.ATTRIBUTE__COMPOSITE_ARRTIBUTE:
        if (resolve) return getComposite_Arrtibute();
        return basicGetComposite_Arrtibute();
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
      case SmartfrogPackage.ATTRIBUTE__VALUE:
        setValue((String)newValue);
        return;
      case SmartfrogPackage.ATTRIBUTE__STATE_DATA:
        setStateData(((Boolean)newValue).booleanValue());
        return;
      case SmartfrogPackage.ATTRIBUTE__STATE_LISTEN:
        setStateListen(((Boolean)newValue).booleanValue());
        return;
      case SmartfrogPackage.ATTRIBUTE__STATE_NOTIFY:
        setStateNotify(((Boolean)newValue).booleanValue());
        return;
      case SmartfrogPackage.ATTRIBUTE__IS_LAZY_VALUE:
        setIsLazyValue(((Boolean)newValue).booleanValue());
        return;
      case SmartfrogPackage.ATTRIBUTE__COMPONENT_ATTRIBUTE:
        setComponent_Attribute((Component)newValue);
        return;
      case SmartfrogPackage.ATTRIBUTE__COMPOSITE_ARRTIBUTE:
        setComposite_Arrtibute((Composite)newValue);
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
      case SmartfrogPackage.ATTRIBUTE__VALUE:
        setValue(VALUE_EDEFAULT);
        return;
      case SmartfrogPackage.ATTRIBUTE__STATE_DATA:
        setStateData(STATE_DATA_EDEFAULT);
        return;
      case SmartfrogPackage.ATTRIBUTE__STATE_LISTEN:
        setStateListen(STATE_LISTEN_EDEFAULT);
        return;
      case SmartfrogPackage.ATTRIBUTE__STATE_NOTIFY:
        setStateNotify(STATE_NOTIFY_EDEFAULT);
        return;
      case SmartfrogPackage.ATTRIBUTE__IS_LAZY_VALUE:
        setIsLazyValue(IS_LAZY_VALUE_EDEFAULT);
        return;
      case SmartfrogPackage.ATTRIBUTE__COMPONENT_ATTRIBUTE:
        setComponent_Attribute((Component)null);
        return;
      case SmartfrogPackage.ATTRIBUTE__COMPOSITE_ARRTIBUTE:
        setComposite_Arrtibute((Composite)null);
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
      case SmartfrogPackage.ATTRIBUTE__VALUE:
        return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
      case SmartfrogPackage.ATTRIBUTE__STATE_DATA:
        return stateData != STATE_DATA_EDEFAULT;
      case SmartfrogPackage.ATTRIBUTE__STATE_LISTEN:
        return stateListen != STATE_LISTEN_EDEFAULT;
      case SmartfrogPackage.ATTRIBUTE__STATE_NOTIFY:
        return stateNotify != STATE_NOTIFY_EDEFAULT;
      case SmartfrogPackage.ATTRIBUTE__IS_LAZY_VALUE:
        return isLazyValue != IS_LAZY_VALUE_EDEFAULT;
      case SmartfrogPackage.ATTRIBUTE__COMPONENT_ATTRIBUTE:
        return component_Attribute != null;
      case SmartfrogPackage.ATTRIBUTE__COMPOSITE_ARRTIBUTE:
        return composite_Arrtibute != null;
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
    result.append(" (Value: ");
    result.append(value);
    result.append(", StateData: ");
    result.append(stateData);
    result.append(", StateListen: ");
    result.append(stateListen);
    result.append(", StateNotify: ");
    result.append(stateNotify);
    result.append(", IsLazyValue: ");
    result.append(isLazyValue);
    result.append(')');
    return result.toString();
  }

} //AttributeImpl
