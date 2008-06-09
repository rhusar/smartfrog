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
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl#getAttri_Name <em>Attri Name</em>}</li>
 *   <li>{@link org.smartfrog.authoringtool.emf.impl.AttributeImpl#getValue <em>Value</em>}</li>
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
   * The default value of the '{@link #getAttri_Name() <em>Attri Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttri_Name()
   * @generated
   * @ordered
   */
  protected static final String ATTRI_NAME_EDEFAULT = "0";

  /**
   * The cached value of the '{@link #getAttri_Name() <em>Attri Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttri_Name()
   * @generated
   * @ordered
   */
  protected String attri_Name = ATTRI_NAME_EDEFAULT;

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
  public String getAttri_Name()
  {
    return attri_Name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAttri_Name(String newAttri_Name)
  {
    String oldAttri_Name = attri_Name;
    attri_Name = newAttri_Name;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SmartfrogPackage.ATTRIBUTE__ATTRI_NAME, oldAttri_Name, attri_Name));
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
      case SmartfrogPackage.ATTRIBUTE__ATTRI_NAME:
        return getAttri_Name();
      case SmartfrogPackage.ATTRIBUTE__VALUE:
        return getValue();
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
      case SmartfrogPackage.ATTRIBUTE__ATTRI_NAME:
        setAttri_Name((String)newValue);
        return;
      case SmartfrogPackage.ATTRIBUTE__VALUE:
        setValue((String)newValue);
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
      case SmartfrogPackage.ATTRIBUTE__ATTRI_NAME:
        setAttri_Name(ATTRI_NAME_EDEFAULT);
        return;
      case SmartfrogPackage.ATTRIBUTE__VALUE:
        setValue(VALUE_EDEFAULT);
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
      case SmartfrogPackage.ATTRIBUTE__ATTRI_NAME:
        return ATTRI_NAME_EDEFAULT == null ? attri_Name != null : !ATTRI_NAME_EDEFAULT.equals(attri_Name);
      case SmartfrogPackage.ATTRIBUTE__VALUE:
        return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
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
    result.append(" (Attri_Name: ");
    result.append(attri_Name);
    result.append(", Value: ");
    result.append(value);
    result.append(')');
    return result.toString();
  }

} //AttributeImpl
