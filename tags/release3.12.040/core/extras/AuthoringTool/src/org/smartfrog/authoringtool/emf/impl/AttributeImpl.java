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
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl#getComposite_arrtibutes <em>Composite arrtibutes</em>}</li>
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
  protected static final boolean IS_LAZY_VALUE_EDEFAULT = false; // TODO The default value literal "0" is not valid.

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
   * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributes()
   * @generated
   * @ordered
   */
  protected Component attributes;

  /**
   * The cached value of the '{@link #getComposite_arrtibutes() <em>Composite arrtibutes</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComposite_arrtibutes()
   * @generated
   * @ordered
   */
  protected Composite composite_arrtibutes;

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
  public Component getAttributes()
  {
    if (attributes != null && attributes.eIsProxy())
    {
      InternalEObject oldAttributes = (InternalEObject)attributes;
      attributes = (Component)eResolveProxy(oldAttributes);
      if (attributes != oldAttributes)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.ATTRIBUTE__ATTRIBUTES, oldAttributes, attributes));
      }
    }
    return attributes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Component basicGetAttributes()
  {
    return attributes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAttributes(Component newAttributes)
  {
    Component oldAttributes = attributes;
    attributes = newAttributes;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.ATTRIBUTE__ATTRIBUTES, oldAttributes, attributes));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite getComposite_arrtibutes()
  {
    if (composite_arrtibutes != null && composite_arrtibutes.eIsProxy())
    {
      InternalEObject oldComposite_arrtibutes = (InternalEObject)composite_arrtibutes;
      composite_arrtibutes = (Composite)eResolveProxy(oldComposite_arrtibutes);
      if (composite_arrtibutes != oldComposite_arrtibutes)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SmartfrogPackage.ATTRIBUTE__COMPOSITE_ARRTIBUTES, oldComposite_arrtibutes, composite_arrtibutes));
      }
    }
    return composite_arrtibutes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Composite basicGetComposite_arrtibutes()
  {
    return composite_arrtibutes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setComposite_arrtibutes(Composite newComposite_arrtibutes)
  {
    Composite oldComposite_arrtibutes = composite_arrtibutes;
    composite_arrtibutes = newComposite_arrtibutes;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.ATTRIBUTE__COMPOSITE_ARRTIBUTES, oldComposite_arrtibutes, composite_arrtibutes));
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
      case SmartfrogPackage.ATTRIBUTE__ATTRIBUTES:
        if (resolve) return getAttributes();
        return basicGetAttributes();
      case SmartfrogPackage.ATTRIBUTE__COMPOSITE_ARRTIBUTES:
        if (resolve) return getComposite_arrtibutes();
        return basicGetComposite_arrtibutes();
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
      case SmartfrogPackage.ATTRIBUTE__ATTRIBUTES:
        setAttributes((Component)newValue);
        return;
      case SmartfrogPackage.ATTRIBUTE__COMPOSITE_ARRTIBUTES:
        setComposite_arrtibutes((Composite)newValue);
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
      case SmartfrogPackage.ATTRIBUTE__ATTRIBUTES:
        setAttributes((Component)null);
        return;
      case SmartfrogPackage.ATTRIBUTE__COMPOSITE_ARRTIBUTES:
        setComposite_arrtibutes((Composite)null);
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
      case SmartfrogPackage.ATTRIBUTE__ATTRIBUTES:
        return attributes != null;
      case SmartfrogPackage.ATTRIBUTE__COMPOSITE_ARRTIBUTES:
        return composite_arrtibutes != null;
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
